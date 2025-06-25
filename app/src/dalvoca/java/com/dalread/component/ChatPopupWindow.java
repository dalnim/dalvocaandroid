package com.dalread.component;

import android.app.Activity;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dalread.R;
import com.dalread.adapter.ChatUsersAdapter;
import com.dalread.component.SeparatorDecoration;
import com.dalread.database.SharedPreferencesDB;
import com.dalread.model.User;
import com.dalread.util.Constant;

import java.util.ArrayList;

import butterknife.BindColor;
import butterknife.BindDimen;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ChatPopupWindow extends PopupWindow {

    @BindView(R.id.v_call)
    LinearLayout vCall;
    @BindView(R.id.tv_call)
    TextView tvCall;
    @BindView(R.id.tv_menu_1)
    TextView tvMenu1;
    @BindView(R.id.tv_menu_2)
    TextView tvMenu2;
    @BindView(R.id.tv_menu_3)
    TextView tvMenu3;
    @BindView(R.id.v_menu_message)
    View vMenuMessage;
    @BindView(R.id.v_menu_invite)
    View vMenuInvite;
    @BindView(R.id.v_menu_change_role)
    View vMenuChangeRole;
    @BindView(R.id.tv_show_hide_student_meaning)
    TextView tvShowHideStudentMeaning;
    @BindView(R.id.tv_users)
    TextView tvUsers;
    @BindView(R.id.rv_users)
    RecyclerView rvUsers;

    @BindString(R.string.show_student_meaning)
    String strShowStudentMeaning;
    @BindString(R.string.hide_student_meaning)
    String strHideStudentMeaning;
    @BindString(R.string.tpl_chat_users)
    String tplChatUsers;
    @BindString(R.string.tpl_study_users)
    String tplStudyUsers;

    @BindColor(R.color.color_divider)
    int clDivider;

    @BindDimen(R.dimen.divider_height)
    float dividerHeight;

    private Activity activity;
    private View anchor;
    private ChatUsersAdapter chatUsersAdapter;
    private ArrayList<User> chatUsers;
    private ArrayList<User> studyUsers;
    private boolean isChatMode;
    private OnItemClickListener listener;

    public ChatPopupWindow(Activity activity, View anchor, SharedPreferencesDB sharedPreferences) {
        super(
                activity.getLayoutInflater().inflate(R.layout.layout_chat_mode_menu, null),
                activity.getResources().getDisplayMetrics().widthPixels * 3 / 4, ViewGroup.LayoutParams.WRAP_CONTENT
        );

        this.activity = activity;
        this.anchor = anchor;

        ButterKnife.bind(this, getContentView());
        setFocusable(true);
        setOutsideTouchable(true);
        setBackgroundDrawable(new BitmapDrawable());

        initRecyclerView(sharedPreferences);
    }

    private void initRecyclerView(SharedPreferencesDB sharedPreferences) {
        chatUsersAdapter = new ChatUsersAdapter(activity, sharedPreferences);
        rvUsers.setAdapter(chatUsersAdapter);
        rvUsers.setLayoutManager(new LinearLayoutManager(activity));
        rvUsers.addItemDecoration(new SeparatorDecoration(activity, clDivider, dividerHeight));
    }

    public void show(boolean isChatMode) {
        show(isChatMode, Constant.JITSI.CALl_TYPE.HIDE);
    }

    public void show(boolean isChatMode, int callType) {

        vCall.setVisibility(View.VISIBLE);
        switch (callType) {
            case Constant.JITSI.CALl_TYPE.NONE:
                tvCall.setText(R.string.make_a_call);
                break;
            case Constant.JITSI.CALl_TYPE.CALLED:
                tvCall.setText(R.string.open_a_call_view);
                break;
            default:
                vCall.setVisibility(View.GONE);
                break;
        }

        this.isChatMode = isChatMode;
        if (isChatMode) {
            tvMenu1.setText(R.string.study_mode);
            tvMenu2.setText(R.string.about_study_mode);
            tvMenu3.setText(R.string.edit_room_name);
            vMenuMessage.setVisibility(View.GONE);
            vMenuInvite.setVisibility(View.GONE);
            vMenuChangeRole.setVisibility(View.GONE);
            tvUsers.setText(String.format(tplChatUsers, chatUsers.size()));
            chatUsersAdapter.setUsers(chatUsers, true);
        } else { // study mode
            tvMenu1.setText(R.string.chat_mode);
            tvMenu2.setText(R.string.about_study_mode);
            tvMenu3.setText(R.string.finish_study_mode);
            vMenuMessage.setVisibility(View.GONE);
            vMenuInvite.setVisibility(View.GONE);
            vMenuChangeRole.setVisibility(View.GONE);
            tvUsers.setText(String.format(tplStudyUsers, studyUsers.size()));
            chatUsersAdapter.setUsers(studyUsers, false);
        }
        showAsDropDown(anchor);
    }

    @OnClick({R.id.tv_menu_1, R.id.tv_menu_2, R.id.tv_menu_3, R.id.tv_menu_voice_call,
            R.id.tv_menu_use_speaker_phone, R.id.tv_menu_mute, R.id.tv_current_guide,
            R.id.tv_show_hide_student_meaning, R.id.tv_menu_student_option, R.id.v_menu_message,
            R.id.tv_menu_chat_message, R.id.v_menu_invite, R.id.v_menu_change_role,
            R.id.v_call, R.id.tv_refresh_user_list})
    void onClick(View view) {
        if (listener == null) return;
        int id = view.getId();
        switch (id) {
            case R.id.tv_menu_1:
                if (isChatMode) {
                    listener.onStudyModeClick();
                } else {
                    listener.onChatModeClick();
                }
                break;
            case R.id.tv_menu_2:
                listener.onAboutLessonClick();
                break;
            case R.id.tv_menu_3:
                if (isChatMode) {
                } else {
                    listener.onFinishStudyClick();
                }
                break;
            case R.id.tv_menu_voice_call:
                listener.onVoiceCallClick();
                break;
            case R.id.tv_menu_use_speaker_phone:
                listener.onUseSpeakerPhoneClick();
                break;
            case R.id.tv_menu_mute:
                listener.onMuteClick();
                break;
            case R.id.tv_show_hide_student_meaning:
                boolean isShow = tvShowHideStudentMeaning.getText().toString().equals(strShowStudentMeaning);
                listener.onShowHideStudentMeaningClick(isShow);
                setButtonShowHideStudentMeaningText(!isShow);
                break;
            case R.id.tv_current_guide:
                listener.onCurrentGuideClick();
                break;
            case R.id.tv_menu_student_option:
                listener.onStudentOptionClick();
                break;
            case R.id.v_menu_message:
                listener.onMessageClick();
                break;
            case R.id.tv_menu_chat_message:
                listener.onChatMessageClick();
                break;
            case R.id.v_menu_invite:
                listener.onInviteClick();
                break;
            case R.id.v_menu_change_role:
                listener.onChangeRoleClick();
                break;
            case R.id.v_call:
                listener.onCallClick();
                break;
            case R.id.tv_refresh_user_list:
                listener.onRefreshUserListClick();
                break;
            default:
                break;
        }
    }

    public void setButtonShowHideStudentMeaningText(boolean isShow) {
        tvShowHideStudentMeaning.setText(isShow ? strShowStudentMeaning : strHideStudentMeaning);
    }

    public void setChatUsers(ArrayList<User> chatUsers) {
        this.chatUsers = chatUsers;
    }

    public void setStudyUsers(ArrayList<User> studyUsers) {
        this.studyUsers = studyUsers;
        if (isShowing() && !isChatMode) {
            chatUsersAdapter.setUsers(studyUsers, false);
        }
    }

    public void setListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public interface OnItemClickListener {

        void onStudyModeClick();

        void onChatModeClick();

        void onVoiceCallClick();

        void onUseSpeakerPhoneClick();

        void onMuteClick();

        void onCurrentGuideClick();

        void onStudentOptionClick();

        void onMessageClick();

        void onChatMessageClick();

        void onInviteClick();

        void onChangeRoleClick();

        void onFinishStudyClick();

        void onAboutLessonClick();

        void onCallClick();

        void onRefreshUserListClick();

        void onShowHideStudentMeaningClick(boolean isShow);
    }
}
