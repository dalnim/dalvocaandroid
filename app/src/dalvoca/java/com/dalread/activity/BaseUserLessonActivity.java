package com.dalread.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dalread.R;
import com.dalread.adapter.UserLessonAdapter;
import com.dalread.base.BaseVocaActivity;
import com.dalread.component.SeparatorDecoration;
import com.dalread.dialog.AlertDialog;
import com.dalread.listener.OnClickListener;
import com.dalread.model.User;
import com.dalread.network.DalApiListener;
import com.dalread.util.Constant;
import com.dalread.util.Loading;
import com.dalread.util.Utils;

import java.util.List;

import butterknife.BindColor;
import butterknife.BindDimen;
import butterknife.BindView;

public abstract class BaseUserLessonActivity extends BaseVocaActivity {

    @BindView(R.id.rv_content) RecyclerView rvContent;
    @BindColor(R.color.color_divider) int clDivider;
    @BindDimen(R.dimen.divider_height) float dividerHeight;

    private Context context;
    protected int studyLang;
    private AlertDialog alertDialog;
    private UserLessonAdapter adapter;

    @Override
    protected int getContentViewId() {
        return R.layout.fragment_toolbar_and_recycler_view;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initData();
        initLayout();
        baseGetData();
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

    private void initData() {
        context = this;
        studyLang = getIntent().getIntExtra(Constant.BUNDLE.KEY_STUDY_LANG, 0);
    }

    private void initLayout() {
        toolbar.setTitle(getToolbarTitle());
        toolbar.setSearchListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                adapter.filterData(s);
                adapter.notifyDataSetChanged();
                return true;
            }
        }, () -> {
            adapter.filterData("");
            adapter.notifyDataSetChanged();
            return true;
        });
        toolbar.showSearchView();
        alertDialog = new AlertDialog(context);
        adapter = new UserLessonAdapter(context, sharedPreferences);
        adapter.setListener(onUserClickListener);
        rvContent.setAdapter(adapter);
        rvContent.setLayoutManager(new LinearLayoutManager(context));
        rvContent.addItemDecoration(BaseBindUtils.getSeparatorDecoration(context));
    }

    private void baseGetData() {
        if (Utils.isConnected(context)) {
            Loading.show(context);
            int sortType = Constant.API_VALUE.USER_SORT_TYPE_NAME;
            getData(studyLang, sortType, new DalApiListener<List<User>>() {

                @Override
                public void onSuccess(List<User> response) {
                    bindData(response);
                    Loading.hide();
                }

                @Override
                public void onFailure(String error) {
                    Loading.hide();
                }
            });
        } else {
            alertDialog.showNoInternet();
        }
    }

    private void bindData(List<User> users) {
        adapter.setHeader(getHeaderText(users == null ? 0 : users.size()));
        adapter.setData(users);
        adapter.notifyDataSetChanged();
    }

    private OnClickListener onUserClickListener = new OnClickListener() {

        @Override
        public void onClick(View view, Object object) {
            if (object instanceof User) {
                User user = (User) object;
                int uid = user.getUid();
                int myUid = sharedPreferences.getRealUid();
                if (view.getId() == R.id.iv_avatar) {
                    // open profile view
                    if (uid == myUid) {
                        openNewScreen(MyProfileActivity.class);
                    } else {
                        Intent intent = new Intent(context, OtherProfileActivity.class);
                        intent.putExtra(Constant.BUNDLE.KEY_OPPONENT_ID, uid);
                        openNewScreen(intent);
                    }
                } else if (view.getId() == R.id.iv_action) {
                    Intent data = new Intent();
                    data.putExtra(Constant.BUNDLE.KEY_OTHER_USER_ID, user.getUid());
                    data.putExtra(Constant.BUNDLE.KEY_OTHER_USER_NAME, user.getName());
                    setResult(Activity.RESULT_OK, data);
                    finish();
                }
            }
        }
    };

    protected abstract int getToolbarTitle();

    protected abstract void getData(int studyLang, int sortType, DalApiListener<List<User>> dalApiListener);

    protected abstract String getHeaderText(int size);
}
