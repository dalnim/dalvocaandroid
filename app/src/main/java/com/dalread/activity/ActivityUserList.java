package com.dalread.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.dalread.R;
import com.dalread.adapter.UserListAdapter;
import com.dalread.base.BaseVocaActivity;
import com.dalread.dialog.AlertDialog;
import com.dalread.model.User;
import com.dalread.network.DalApiListener;
import com.dalread.network.events.BaseEvent;
import com.dalread.network.events.SuccessEvent;
import com.dalread.network.models.UserListResponse;
import com.dalread.util.BaseBindUtils;
import com.dalread.util.Constant;
import com.dalread.util.DLog;
import com.dalread.util.Loading;
import com.dalread.util.Utils;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;

import butterknife.BindView;

public class ActivityUserList extends BaseVocaActivity {

    @BindView(R.id.vRefresh)
    SwipeRefreshLayout vRefresh;
    @BindView(R.id.rvUser)
    RecyclerView rvUser;

//    @BindColor(R.color.color_divider)
//    int clDivider;
//
//    @BindDimen(R.dimen.divider_height)
//    float dividerHeight;

    private Context context;
    private int userListType;
    private String searchString;
    private PopupMenu popupMenu;
    private UserListAdapter adapter;
    private LinearLayoutManager layoutManager;
    private AlertDialog alertDialog;
    private int loadingPos;
    private boolean isLoading;
    private boolean canLoadMore;
    private boolean dataChanged;
    private int otherUid; // id of other user

    @Override
    protected int getContentViewId() {
        return R.layout.activity_user_list;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initData();
        initEventBus();
        initToolbar();
        initRecyclerView();
        initSwipeRefreshLayout();
        initDialog();

        getData(false, false);
    }

    @Override
    public void onHeaderLeftClick() {
        onBackPressed();
    }
    @Override
    public void onHeaderLeft2Click() {
    }
    @Override
    public void onHeaderRightClick() {
        if (popupMenu != null) {
            popupMenu.show();
        }
    }

    @Override
    public void onHeaderIconRightClick() {

    }

    @Override
    public void onHeaderTextRightClick() {

    }

    private void initData() {
        context = this;
        userListType = getIntent().getIntExtra(Constant.BUNDLE.KEY_USER_LIST_TYPE, Constant.USER_LIST.ALL);
        searchString = "";
        otherUid = getIntent().getIntExtra(Constant.BUNDLE.KEY_OTHER_USER_ID, 0);
    }

    private void initToolbar() {
        toolbar.setSearchListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                searchString = s;
                if (userListType == Constant.USER_LIST.ALL) {
                    getData(true, false);
                } else {
                    if (adapter != null)
                        adapter.filterData(searchString);
                }
                return true;
            }
        }, new SearchView.OnCloseListener() {

            @Override
            public boolean onClose() {
                searchString = "";
                if (userListType == Constant.USER_LIST.ALL) {
                    getData(true, false);
                } else {
                    adapter.filterData(searchString);
                }
                return true;
            }
        });
        toolbar.showSearchView();
        if (userListType == Constant.USER_LIST.ALL) {
            toolbar.setTitle(R.string.all_user_list);
        } else if (userListType == Constant.USER_LIST.FOLLOWING) {
            toolbar.setTitle(R.string.following);
            if (otherUid == 0) {
                toolbar.setIconRight(R.drawable.ic_more_vert_white_24dp);
                popupMenu = new PopupMenu(context, toolbar.getIconRight());
                popupMenu.getMenuInflater().inflate(R.menu.menu_add, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        openAllUserListScreen();
                        return true;
                    }
                });
            }
        } else if (userListType == Constant.USER_LIST.FOLLOWER) {
            toolbar.setTitle(R.string.follower);
        } else if (userListType == Constant.USER_LIST.FAVORITE) {
            toolbar.setTitle(R.string.favorite_users);
        } else if (userListType == Constant.USER_LIST.BLOCK) {
            toolbar.setTitle(R.string.block_users);
        }
    }

    private void initRecyclerView() {
        adapter = new UserListAdapter(sharedPreferences, userListType, otherUid > 0);
        adapter.setListener(new UserListAdapter.OnUserClickListener() {

            @Override
            public void onProfile(User user) {
                openOtherUserProfileScreen(user);
            }

            @Override
            public void onAction(User user) {
                manageFollowingETC(user);
            }
        });
        rvUser.setAdapter(adapter);
        rvUser.setLayoutManager(layoutManager = new LinearLayoutManager(context));
        rvUser.addItemDecoration(BaseBindUtils.getSeparatorDecoration(context));
        rvUser.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (!isLoading && canLoadMore) {
                    int count = layoutManager.getItemCount();
                    int last = layoutManager.findLastVisibleItemPosition();
                    if (count <= last + 2) { // load more when scrolled to the second-last item
                        DLog.i(getLogTag(), "count = " + count);
                        DLog.i(getLogTag(), "last + 2 = " + (last + 2));
                        canLoadMore = false;
                        getData(false, true);
                    }
                }
            }
        });
    }

    private void initSwipeRefreshLayout() {
        vRefresh.setColorSchemeResources(R.color.colorBlue);
        vRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                getData(false, false);
                vRefresh.setRefreshing(false);
            }
        });
    }

    private void initDialog() {
        alertDialog = new AlertDialog(context);
    }

    private void getData(final boolean isSearching, final boolean isLoadMore) {
        int uid = getUserID();
        if (uid > 0) {
            if (Utils.isConnected(context)) {
                isLoading = true;
                rvUser.post(new Runnable() {

                    @Override
                    public void run() {
                        if (isLoadMore) {
                            adapter.setLoadMore(true);
                        } else {
                            loadingPos = 0;
                            if (!isSearching) {
                                Loading.show(context);
                            }
                        }
                        application.getDalAiImpl().getUserList(
                                isSearching ? Constant.USER_LIST.FILTER_NAME : userListType,
                                otherUid,
                                loadingPos,
                                Constant.LOADING_MAX_ITEM + 1,
                                searchString,
                                new DalApiListener<UserListResponse>() {

                                    @Override
                                    public void onSuccess(UserListResponse response) {
                                        isLoading = false;
                                        ArrayList<User> userList = response.getUsers();
                                        if (userList.size() == Constant.LOADING_MAX_ITEM + 1) {
                                            userList.remove(Constant.LOADING_MAX_ITEM);
                                            canLoadMore = true;
                                        }
                                        if (isLoadMore) {
                                            adapter.setLoadMore(false);
                                            adapter.addData(userList);
                                        } else {
                                            Loading.hide();
                                            adapter.setData(userList);
                                        }
                                        loadingPos += userList.size();
                                        DLog.i(getLogTag(), "loadingPos = " + loadingPos);
                                    }

                                    @Override
                                    public void onFailure(String error) {
                                        isLoading = false;
                                        if (isLoadMore) {
                                            adapter.setLoadMore(false);
                                        } else {
                                            Loading.hide();
                                        }
                                    }
                                }
                        );
                    }
                });
            } else {
                alertDialog.showNoInternet();
            }
        } else {
            alertDialog.showLogInRequired();
        }
    }

    private void openAllUserListScreen() {
        Intent intent = new Intent(context, ActivityUserList.class);
        intent.putExtra(Constant.BUNDLE.KEY_USER_LIST_TYPE, Constant.USER_LIST.ALL);
        openNewScreen(intent);
    }

    private void openOtherUserProfileScreen(User user) {
        Intent intent = new Intent(context, OtherProfileActivity.class);
        intent.putExtra(Constant.BUNDLE.KEY_OPPONENT_ID, user.getUid());
        openNewScreen(intent);
    }

    private void manageFollowingETC(final User user) {
        int uid = getUserID();
        if (uid > 0) {
            if (Utils.isConnected(context)) {
                String followingEtcType;
                boolean isManageRecord;
                if (userListType == Constant.USER_LIST.ALL) {
                    followingEtcType = Constant.API_VALUE.FOLLOWING_ETC_TYPE_FOLLOWING;
                    isManageRecord = !user.isFollowingUser();
                } else if (userListType == Constant.USER_LIST.FOLLOWING) {
                    followingEtcType = Constant.API_VALUE.FOLLOWING_ETC_TYPE_FOLLOWING;
                    isManageRecord = !user.isFollowingUser();
                } else if (userListType == Constant.USER_LIST.FOLLOWER) {
                    followingEtcType = Constant.API_VALUE.FOLLOWING_ETC_TYPE_FOLLOWING;
                    isManageRecord = !user.isFollowingUser();
                } else if (userListType == Constant.USER_LIST.FAVORITE) {
                    followingEtcType = Constant.API_VALUE.FOLLOWING_ETC_TYPE_FAVORITE;
                    isManageRecord = !user.isFavoriteUser();
                } else if (userListType == Constant.USER_LIST.BLOCK) {
                    followingEtcType = Constant.API_VALUE.FOLLOWING_ETC_TYPE_BLOCK;
                    isManageRecord = !user.isBlockUser();
                } else {
                    return;
                }
                final int manageRecord = isManageRecord ? 1 : 0;
                application.getDalAiImpl().manageFollowingETC(
                        user.getUid(),
                        followingEtcType,
                        manageRecord,
                        new DalApiListener<Boolean>() {

                            @Override
                            public void onSuccess(Boolean response) {
                                if (response) {
                                    if (userListType == Constant.USER_LIST.ALL) {
                                        user.setIsFollowingUser(manageRecord);
                                        application.getEventBus().post(new SuccessEvent(BaseEvent.Screen.MAIN, BaseEvent.EventType.DATA_CHANGED, null));
                                    } else if (userListType == Constant.USER_LIST.FOLLOWING) {
                                        user.setIsFollowingUser(manageRecord);
                                    } else if (userListType == Constant.USER_LIST.FOLLOWER) {
                                        user.setIsFollowingUser(manageRecord);
                                    } else if (userListType == Constant.USER_LIST.FAVORITE) {
                                        user.setIsFavoriteUser(manageRecord);
                                    } else if (userListType == Constant.USER_LIST.BLOCK) {
                                        user.setIsBlockUser(manageRecord);
                                    }
                                    adapter.notifyItemChanged(user);
                                }
                            }

                            @Override
                            public void onFailure(String error) {
                            }
                        }
                );
            } else {
                alertDialog.showNoInternet();
            }
        } else {
            alertDialog.showLogInRequired();
        }
    }

    @Subscribe
    public void onEvent(SuccessEvent successEvent) {
        if (successEvent.getScreen() == BaseEvent.Screen.MAIN) {
            BaseEvent.EventType type = successEvent.getEventType();
            if (type == BaseEvent.EventType.DATA_CHANGED) {
                dataChanged = true;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (dataChanged) {
            dataChanged = false;
            getData(false, false);
        }
    }

    @Override
    protected void onDestroy() {
        destroyToolbar();
        unRegisterEventBus();
        super.onDestroy();
    }

    private void destroyToolbar() {
        if (toolbar != null) {
            toolbar.clearSearchListener();
            toolbar.hideSearchView();
        }
    }
}
