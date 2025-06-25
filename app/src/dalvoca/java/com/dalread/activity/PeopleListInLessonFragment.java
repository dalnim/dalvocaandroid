package com.dalread.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dalread.R;
import com.dalread.adapter.PeopleInLessonAdapter;
import com.dalread.base.BaseLessonListFragment;
import com.dalread.component.SeparatorDecoration;
import com.dalread.component.Toolbar;
import com.dalread.dialog.AlertDialog;
import com.dalread.listener.OnClickListener;
import com.dalread.model.Lesson;
import com.dalread.model.User;
import com.dalread.network.DalApiListener;
import com.dalread.util.Constant;
import com.dalread.util.Loading;
import com.dalread.util.Utils;

import java.util.List;

import butterknife.BindColor;
import butterknife.BindDimen;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.OnClick;

public class PeopleListInLessonFragment extends BaseLessonListFragment {

    @BindView(R.id.header) Toolbar toolbar;
    @BindView(R.id.rv_content) RecyclerView rvContent;
    @BindColor(R.color.color_divider) int clDivider;
    @BindDimen(R.dimen.divider_height) float dividerHeight;

    @BindString(R.string.tpl_tutor_list) String tplTutorList;
    @BindString(R.string.tpl_student_list) String tplStudentList;

    private Context context;
    private int lessonType;
    private AlertDialog alertDialog;
    private PeopleInLessonAdapter adapter;

    @Override
    protected int getContentViewId() {
        return R.layout.fragment_toolbar_and_recycler_view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initData();
        initLayout();

        getData();
    }

    private void initData() {
        context = getContext();
        lessonType = getArguments().getInt(Constant.BUNDLE.KEY_LESSON_TYPE);
    }

    private void initLayout() {
        if (lessonType == Constant.API_VALUE.LIST_LESSON_FOR_STUDENT) {
            toolbar.setTitle(R.string.tutor_list);
        } else {
            toolbar.setTitle(R.string.student_list);
        }
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
        }, new SearchView.OnCloseListener() {

            @Override
            public boolean onClose() {
                adapter.filterData("");
                adapter.notifyDataSetChanged();
                return true;
            }
        });
        toolbar.showSearchView();
        alertDialog = new AlertDialog(context);
        adapter = new PeopleInLessonAdapter(context);
        adapter.setListener(onPeopleClickListener);
        rvContent.setAdapter(adapter);
        rvContent.setLayoutManager(new LinearLayoutManager(context));
        rvContent.addItemDecoration(BaseBindUtils.getSeparatorDecoration(context));
    }

    private void getData() {
        if (Utils.isConnected(context)) {
            Loading.show(context);
            int sortType = Constant.API_VALUE.USER_SORT_TYPE_NAME;
            DalApiListener<List<User>> dalApiListener = new DalApiListener<List<User>>() {

                @Override
                public void onSuccess(List<User> response) {
                    bindData(response);
                    Loading.hide();
                }

                @Override
                public void onFailure(String error) {
                    Loading.hide();
                }
            };
            if (lessonType == Constant.API_VALUE.LIST_LESSON_FOR_STUDENT) {
                application.getDalAiImpl().getAllTutorListForLesson(0, sortType, dalApiListener);
            } else {
                application.getDalAiImpl().getAllStudentListForLesson(0, sortType, dalApiListener);
            }
        } else {
            alertDialog.showNoInternet();
        }
    }

    private void bindData(List<User> users) {
        String header = String.format(
                lessonType == Constant.API_VALUE.LIST_LESSON_FOR_STUDENT ? tplTutorList : tplStudentList,
                users == null ? 0 : users.size()
        );
        adapter.setHeader(header);
        adapter.setData(users);
        adapter.notifyDataSetChanged();
    }

    private OnClickListener onPeopleClickListener = new OnClickListener() {

        @Override
        public void onClick(View view, Object object) {
            if (object instanceof User) {
                User user = (User) object;
                int uid = user.getUid();
                int myUid = sharedPreferences.getRealUid();
                if (view.getId() == R.id.ic_left) {
                    // open profile view
                    if (uid == myUid) {
                        activity.openNewScreen(MyProfileActivity.class);
                    } else {
                        Intent intent = new Intent(context, OtherProfileActivity.class);
                        intent.putExtra(Constant.BUNDLE.KEY_OPPONENT_ID, uid);
                        activity.openNewScreen(intent);
                    }
                } else {
                    // open study view
                    Lesson lesson = new Lesson();
                    lesson.setLessonType(lessonType);
                    if (lessonType == Constant.API_VALUE.LIST_LESSON_FOR_STUDENT) {
                        lesson.setStudentId(myUid);
                        lesson.setTutorId(uid);
                    } else {
                        lesson.setStudentId(uid);
                        lesson.setTutorId(myUid);
                    }
                    Intent i = new Intent(context, ChatDetailsActivity.class);
                    i.putExtra(Constant.BUNDLE.KEY_LESSON, lesson);
                    activity.openNewScreen(i);
                }
            }
        }
    };

    @OnClick({R.id.ic_left})
    void onIcLeftClick() {
        activity.onBackPressed();
    }
}
