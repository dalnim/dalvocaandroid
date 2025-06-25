package com.dalread.activity;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.dalread.R;
import com.dalread.base.BaseProfileActivity;
import com.dalread.base.EnumUserType;
import com.dalread.network.DalApiListener;
import com.dalread.network.events.BaseEvent;
import com.dalread.network.events.SuccessEvent;
import com.dalread.util.Constant;
import com.dalread.util.DLog;
import com.dalread.util.ToastUtil;
import com.dalread.util.Utils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;

import butterknife.BindColor;
import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnEditorAction;

public class OtherProfileActivity extends BaseProfileActivity {

    @BindView(R.id.tv_online)
    TextView tvOnline;
    @BindView(R.id.ic_following)
    ImageView icFollowing;
    @BindView(R.id.ic_allow_chat)
    ImageView icAllowChat;
    @BindView(R.id.ic_block)
    ImageView icBlock;
    @BindView(R.id.ic_favorite)
    ImageView icFavorite;
    @BindView(R.id.iv_introduction)
    ImageView ivIntroduction;
    @BindView(R.id.et_nickname)
    EditText etNickname;
    @BindView(R.id.tv_desc_by_me)
    TextView tvDescByMe;
    @BindView(R.id.v_hide_info)
    View vHideInfo;
    @BindView(R.id.v_student_lesson_option_container)
    View vStudentLessonOptionContainer;

    @BindColor(R.color.color_play)
    int clPlay;
    @BindColor(R.color.color_black_speaker_icon)
    int clStop;

    private int opponentId;
    private MediaPlayer mediaPlayer;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_other_profile;
    }

    @Override
    protected int getOpponentId() {
        return opponentId;
    }

    @Override
    protected OnSuccessListener<FileDownloadTask.TaskSnapshot> getOnDownloadIntroductionSuccess() {
        return taskSnapshot -> DLog.i(getLogTag(), "Download introduction onSuccess");
    }

    @Override
    protected OnFailureListener getOnDownloadIntroductionFailure() {
        return e -> DLog.i(getLogTag(), "Download introduction onFailure");
    }

    @Override
    protected void initData() {
        super.initData();

        opponentId = getIntent().getIntExtra(Constant.BUNDLE.KEY_OPPONENT_ID, 0);
    }

    @Override
    protected void initLayout() {
        super.initLayout();

        if (sharedPreferences.getUserType() == EnumUserType.SYSTEM_MANAGER.getType()) {
            vStudentLessonOptionContainer.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void bindData() {
        super.bindData();

        if (hideInfo) {
            tvName.setText("");
            vHideInfo.setVisibility(View.GONE);
        }
        if (user.isUserOnline()) {
            tvOnline.setText(R.string.online);
            tvOnline.setCompoundDrawablesWithIntrinsicBounds(R.drawable.circle_blue, 0, 0, 0);
        } else {
            tvOnline.setText(R.string.offline);
            tvOnline.setCompoundDrawablesWithIntrinsicBounds(R.drawable.circle_grey, 0, 0, 0);
        }
        etNickname.setText(user.getOponentNameByMe());
        String desc = user.getOponentDescByMe();
        if (TextUtils.isEmpty(desc)) {
            tvDescByMe.setText(R.string.hint_add_desc);
        } else {
            tvDescByMe.setText(desc);
        }
        bindIconFollowing();
        bindIconAllowChat();
        bindIconBlock();
        bindIconFavorite();
        bindIconIntroduction();
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

    private void bindIconFollowing() {
        icFollowing.setImageResource(user.isFollowingUser() ? R.drawable.ic_follow_off : R.drawable.ic_follow_on);
    }

    private void bindIconAllowChat() {
        icAllowChat.setImageResource(user.isAllowChat() ? R.drawable.ic_chat_on : R.drawable.ic_chat_off);
    }

    private void bindIconBlock() {
        icBlock.setImageResource(user.isBlockUser() ? R.drawable.ic_block_on : R.drawable.ic_block_off);
    }

    private void bindIconFavorite() {
        icFavorite.setImageResource(user.isFavoriteUser() ? R.drawable.ic_favorite_on : R.drawable.ic_favorite_off);
    }

    private void bindIconIntroduction() {
        if (user.getHasIntroductionFile() == 1) {
            ivIntroduction.setVisibility(View.VISIBLE);
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        }
    }

    @OnClick({R.id.ic_following, R.id.ic_block, R.id.ic_favorite, R.id.iv_introduction, R.id.v_desc_by_me, R.id.v_1st_grade, R.id.v_student_lesson_option})
    void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.ic_following:
                manageFollowingETC();
                break;
            case R.id.ic_block:
                manageBlockETC();
                break;
            case R.id.ic_favorite:
                manageFavoriteETC();
                break;
            case R.id.iv_introduction:
                handlePlayer();
                break;
            case R.id.v_desc_by_me:
                openDescByMeScreen();
                break;
            case R.id.v_1st_grade:
                openOtherUserTarget();
                break;
            case R.id.v_student_lesson_option:
                openStudentLessonOption();
                break;
            default:
                break;
        }
    }

    private void manageFollowingETC() {
        int uid = getUserID();
        if (uid > 0) {
            if (Utils.isConnected(context)) {
                String followingEtcType = Constant.API_VALUE.FOLLOWING_ETC_TYPE_FOLLOWING;
                boolean isManageRecord = !user.isFollowingUser();
                final int manageRecord = isManageRecord ? 1 : 0;
                application.getDalAiImpl().manageFollowingETC(
                        user.getUid(),
                        followingEtcType,
                        manageRecord,
                        new DalApiListener<Boolean>() {

                            @Override
                            public void onSuccess(Boolean response) {
                                if (response) {
                                    user.setIsFollowingUser(manageRecord);
                                    bindIconFollowing();
                                    postDataChangedEvent();
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

    private void manageBlockETC() {
        int uid = getUserID();
        if (uid > 0) {
            if (Utils.isConnected(context)) {
                String followingEtcType = Constant.API_VALUE.FOLLOWING_ETC_TYPE_BLOCK;
                boolean isManageRecord = !user.isBlockUser();
                final int manageRecord = isManageRecord ? 1 : 0;
                application.getDalAiImpl().manageFollowingETC(
                        user.getUid(),
                        followingEtcType,
                        manageRecord,
                        new DalApiListener<Boolean>() {

                            @Override
                            public void onSuccess(Boolean response) {
                                if (response) {
                                    user.setIsBlockUser(manageRecord);
                                    bindIconBlock();
                                    postDataChangedEvent();
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

    private void manageFavoriteETC() {
        int uid = getUserID();
        if (uid > 0) {
            if (Utils.isConnected(context)) {
                String followingEtcType = Constant.API_VALUE.FOLLOWING_ETC_TYPE_FAVORITE;
                boolean isManageRecord = !user.isFavoriteUser();
                final int manageRecord = isManageRecord ? 1 : 0;
                application.getDalAiImpl().manageFollowingETC(
                        user.getUid(),
                        followingEtcType,
                        manageRecord,
                        new DalApiListener<Boolean>() {

                            @Override
                            public void onSuccess(Boolean response) {
                                if (response) {
                                    user.setIsFavoriteUser(manageRecord);
                                    bindIconFavorite();
                                    postDataChangedEvent();
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

    private void handlePlayer() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.reset();
            ivIntroduction.setColorFilter(clStop);
        } else {
            try {
                Uri uri = Uri.fromFile(introductionFile);
                mediaPlayer.setDataSource(context, uri);
                mediaPlayer.setLooping(true);
                mediaPlayer.prepare();
                mediaPlayer.start();
                ivIntroduction.setColorFilter(clPlay);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void openDescByMeScreen() {
        Intent intent = new Intent(context, DescByMeActivity.class);
        intent.putExtra(Constant.BUNDLE.KEY_PROFILE, user);
        openNewScreen(intent);
    }

    @OnEditorAction(R.id.et_nickname)
    boolean onDonePressed(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            updateOpponentNameByMe();
        }
        return false;
    }

    private void updateOpponentNameByMe() {
        int uid = getUserID();
        if (uid > 0) {
            if (Utils.isConnected(context)) {
                final String name = etNickname.getText().toString();
                application.getDalAiImpl().updateOpponentNameByMe(
                        user.getUid(),
                        name,
                        user.getOponentDescByMe(),
                        new DalApiListener<Boolean>() {

                            @Override
                            public void onSuccess(Boolean response) {
                                if (response) {
                                    user.setOponentNameByMe(name);
                                    postDataChangedEvent();
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

    private void postDataChangedEvent() {
        application.getEventBus().post(new SuccessEvent(BaseEvent.Screen.MAIN, BaseEvent.EventType.DATA_CHANGED, null));
    }

    @Override
    protected void openUserListScreen(int userListType) {
        Intent intent = new Intent(context, ActivityUserList.class);
        intent.putExtra(Constant.BUNDLE.KEY_USER_LIST_TYPE, userListType);
        intent.putExtra(Constant.BUNDLE.KEY_OTHER_USER_ID, user.getUid());
        openNewScreen(intent);
    }

    private void openOtherUserTarget() {
        ToastUtil.getInstance(this).show(R.string.msg_under_development);
//        Intent intent = new Intent(context, TargetsOtherUserActivity.class);
//        intent.putExtra(Constant.BUNDLE.KEY_OTHER_USER_ID, user.getUid());
//        intent.putExtra(Constant.BUNDLE.KEY_OTHER_USER_NAME, user.getName());
//        openNewScreen(intent);
    }

    private void openStudentLessonOption() {
        ToastUtil.getInstance(this).show(R.string.msg_under_development);
//        Intent intent = new Intent(context, StudentLessonOptionActivity.class);
//        intent.putExtra(Constant.BUNDLE.KEY_OTHER_USER_ID, user.getUid());
//        intent.putExtra(Constant.BUNDLE.KEY_OTHER_USER_NAME, user.getName());
//        openNewScreen(intent);
    }

    @Override
    protected void onDestroy() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }

        super.onDestroy();
    }
}
