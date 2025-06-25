package com.dalread.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dalread.BuildConfig;
import com.dalread.R;
import com.dalread.adapter.FavoritesAdapter;
import com.dalread.base.BaseDalVocaPlayVocaActivity;
import com.dalread.base.BaseDialog;
import com.dalread.base.BaseDialogListener;
import com.dalread.base.EnumType;
import com.dalread.component.SeparatorDecoration;
import com.dalread.dialog.AlertDialog;
import com.dalread.dialog.TypeVocaBookNameDialog;
import com.dalread.listener.OnWordbookClickListener;
import com.dalread.model.VocaBook;
import com.dalread.network.DalApiListener;
import com.dalread.network.events.BaseEvent;
import com.dalread.network.events.SuccessEvent;
import com.dalread.network.models.AllBookListResponse;
import com.dalread.util.Constant;
import com.dalread.util.Loading;
import com.dalread.util.ToastUtil;
import com.dalread.util.Utils;
import com.nikhilpanju.recyclerviewenhanced.RecyclerTouchListener;

import org.greenrobot.eventbus.Subscribe;

import java.io.Serializable;
import java.util.List;

import butterknife.BindColor;
import butterknife.BindDimen;
import butterknife.BindView;

public class FavoritesActivity extends BaseDalVocaPlayVocaActivity {

    @BindView(R.id.rv_book) RecyclerView rvBook;
    @BindColor(R.color.color_divider) int clDivider;
    @BindDimen(R.dimen.divider_height) float dividerHeight;

    private AlertDialog alertDialog;
    protected FavoritesAdapter adapter;
    protected List<VocaBook> userBooks;
    private RecyclerTouchListener recyclerTouchListener;
    private TypeVocaBookNameDialog bookNameDialog;
    private boolean dataChanged;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_favorites;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initRecyclerView();
        initDialog();
        initEventBus();
        getData();
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

    }

    @Override
    public void onHeaderIconRightClick() {

    }

    @Override
    public void onHeaderTextRightClick() {

    }

    @Override
    public void onResume() {
        super.onResume();

        if (dataChanged) {
            dataChanged = false;
            getData();
        }
        rvBook.addOnItemTouchListener(recyclerTouchListener);
    }

    @Override
    public void onPause() {
        rvBook.removeOnItemTouchListener(recyclerTouchListener);
        super.onPause();
    }

    @Override
    public void onDestroy() {
        unRegisterEventBus();
        super.onDestroy();
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

    private void initRecyclerView() {
        adapter = new FavoritesAdapter();
        adapter.setListener(getOnWordbookClickListener());
        rvBook.setAdapter(adapter);
        rvBook.setLayoutManager(new LinearLayoutManager(this));
        rvBook.addItemDecoration(new SeparatorDecoration(this, clDivider, dividerHeight));
        recyclerTouchListener = new RecyclerTouchListener(this, rvBook)
                .setSwipeOptionViews(R.id.tv_delete)
                .setSwipeable(R.id.v_foreground, R.id.v_background, new RecyclerTouchListener.OnSwipeOptionsClickListener() {

                    @Override
                    public void onSwipeOptionClicked(int viewId, int position) {
                        VocaBook userBook = userBooks.remove(position - 1);
                        adapter.notifyItemRemoved(position);
                        deleteUserVocaBook(userBook.getId());
                    }
                });
    }

    private void initDialog() {
        bookNameDialog = new TypeVocaBookNameDialog(this, onNewBookListener);
        alertDialog = new AlertDialog(this);
    }

    protected void getData() {
        int uid = sharedPreferences.getRealUid();
        if (uid > 0) {
            if (Utils.isConnected(this)) {
                Loading.show(this);
                application.getDalAiImpl().getAllVocaBookList(
                        String.valueOf(uid),
                        sharedPreferences.getLangStudyCode(),
                        sharedPreferences.getMotherTongueLangCode(),
                        new DalApiListener<AllBookListResponse>() {

                            @Override
                            public void onSuccess(AllBookListResponse response) {
                                userBooks = response.getUserBooks();
                                Loading.hide();
                                bindData();
                            }

                            @Override
                            public void onFailure(String error) {
                                Loading.hide();
                            }
                        }
                );
            } else {
                alertDialog.showNoInternet();
                bindEmptyData();
            }
        } else {
            alertDialog.showLogInRequired();
            bindEmptyData();
        }
    }

    protected void bindData() {
        adapter.setData(userBooks);
        adapter.notifyDataSetChanged();

        setUnswipeableRows();
    }

    private void bindEmptyData() {
        userBooks = null;
        bindData();
    }

    private void setUnswipeableRows() {
        recyclerTouchListener.setUnSwipeableRows(0);
    }

    protected OnWordbookClickListener getOnWordbookClickListener() {
        return new OnWordbookClickListener() {

            @Override
            public void onAddClick() {
                bookNameDialog.show();
            }

            @Override
            public void onDetailsClick(VocaBook book) {
                Intent intent = new Intent(FavoritesActivity.this, VocaBookActivity.class);
                intent.putExtra(Constant.BUNDLE.KEY_VOCA_BOOK, book);
                intent.putExtra(Constant.BUNDLE.KEY_VOCA_USER_BOOK_LIST, (Serializable) userBooks);
                startActivity(intent);
            }
        };
    }

    private BaseDialogListener onNewBookListener = new BaseDialogListener() {

        @Override
        public void onBaseDialogListenerShow(EnumType type, BaseDialog currentDialog, View v, int position, Object data) {
        }

        @Override
        public void onBaseDialogListenerOk(EnumType type, BaseDialog dialog, View v, int position, Object data) {
            String name = (String) data;
            if (((String) data).isEmpty()) {
                ToastUtil.getInstance(FavoritesActivity.this).show(R.string.msg_type_wordbook_name);
            } else {
                createUserVocaBook(name);
            }
        }

        @Override
        public void onBaseDialogListenerCancel(EnumType type, BaseDialog dialog, View v, int position, Object data) {
        }

        @Override
        public void onBaseDialogListenerClick(EnumType type, BaseDialog dialog, View v, int position, Object data) {
        }

        @Override
        public void onBaseDialogListenerClickMulti(EnumType type, BaseDialog dialog, View v, int position, Object[] data) {
        }
    };

    private void createUserVocaBook(String name) {
        int uid = sharedPreferences.getRealUid();
        if (uid > 0) {
            if (Utils.isConnected(this)) {
                Loading.show(this);
                application.getDalAiImpl().addVocaBookInUserVocaBookList(
                        String.valueOf(uid),
                        sharedPreferences.getLangStudyCode(),
                        sharedPreferences.getMotherTongueLangCode(),
                        name,
                        new DalApiListener<Boolean>() {

                            @Override
                            public void onSuccess(Boolean response) {
                                Loading.hide();
                                if (response) {
                                    bookNameDialog.dismiss();
                                    getData();
                                } else {
                                    ToastUtil.getInstance(FavoritesActivity.this).show(R.string.msg_book_name_existed);
                                }
                            }

                            @Override
                            public void onFailure(String error) {
                                Loading.hide();
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

    private void deleteUserVocaBook(int bookId) {
        int uid = sharedPreferences.getRealUid();
        if (uid > 0 && Utils.isConnected(this)) {
            application.getDalAiImpl().deleteVocaBookInUserVocaBookList(
                    String.valueOf(uid),
                    sharedPreferences.getLangStudyCode(),
                    sharedPreferences.getMotherTongueLangCode(),
                    bookId,
                    BuildConfig.DEBUG ? new DalApiListener<Boolean>() {

                        @Override
                        public void onSuccess(Boolean response) {
                            ToastUtil.getInstance(FavoritesActivity.this).show("Dev: Delete book " + (response ? "successfully" : "failed"));
                        }

                        @Override
                        public void onFailure(String error) {
                        }
                    } : null
            );
        }
    }
}
