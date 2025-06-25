package com.dalread.base;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.dalread.R;
import com.dalread.activity.ActivityUserList;
import com.dalread.activity.BioActivity;
import com.dalread.activity.EditProfileActivity;
import com.dalread.dialog.AlertDialog;
import com.dalread.model.User;
import com.dalread.network.DalApiListener;
import com.dalread.network.events.BaseEvent;
import com.dalread.network.events.SuccessEvent;
import com.dalread.util.BaseVoca;
import com.dalread.util.Constant;
import com.dalread.util.DateUtils;
import com.dalread.util.Loading;
import com.dalread.util.Utils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.util.Date;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.OnClick;

public abstract class BaseProfileActivity extends BaseVocaActivity {

    @BindView(R.id.iv_avatar)
    ImageView ivAvatar;
    @BindView(R.id.tv_name)
    protected TextView tvName;
    @BindView(R.id.tv_location)
    TextView tvLocation;
    @BindView(R.id.tv_join_date)
    TextView tvJoinDate;
    @BindView(R.id.tv_score)
    TextView tvScore;
    @BindView(R.id.tv_following_count)
    TextView tvFollowingCount;
    @BindView(R.id.tv_follower_count)
    TextView tvFollowerCount;
    @BindView(R.id.tv_bio)
    TextView tvBio;

    @BindString(R.string.tpl_join_date)
    String tplJoinDate;

    protected Context context;
    protected File introductionFile;
    protected AlertDialog alertDialog;
    protected User user;
    protected boolean hideInfo;
    protected EventBus eventBus;
    protected boolean dataChanged;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initData();
        postInitData();
        initLayout();
        initEventBus();

        getData();
        getIntroduction();
    }

    protected void initData() {
        context = this;
    }

    private void postInitData() {
        introductionFile = BaseVoca.getIntroductionFileOnLocal(context, getIntroductionUid());
    }

    protected void initLayout() {
        alertDialog = new AlertDialog(context);
    }

    private void getData() {
        int uid = getUserID();
        if (uid > 0) {
            if (Utils.isConnected(context)) {
                Loading.show(context);
                final int opponentId = getOpponentId();
                application.getDalAiImpl().getProfileInfo(opponentId, new DalApiListener<User>() {

                    @Override
                    public void onSuccess(User response) {
                        user = response;
                        hideInfo = sharedPreferences.getUserType() != EnumUserType.SYSTEM_MANAGER.getType() && opponentId > 0 && !user.isShareMyRecording();
                        bindData();
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
        } else {
            alertDialog.showLogInRequired();
        }
    }

    protected void bindData() {
        ivAvatar.setImageResource(BaseVoca.getAvatarResource(user.getSex(), user.getAge()));
        tvName.setText(user.getName());
        String location = BaseVoca.getUserDisplayLocation(context, user);
        tvLocation.setText(location);
        String joinDate = DateUtils.getDateOnlyFormat().format(new Date(DateUtils.secondsToMillis(user.getJoinDateTS())));
        tvJoinDate.setText(joinDate);
        String score = String.valueOf(user.getScore());
        tvScore.setText(score);
        String followingCount = String.valueOf(user.getFollowingCount());
        tvFollowingCount.setText(followingCount);
        String followerCount = String.valueOf(user.getFollowerCount());
        tvFollowerCount.setText(followerCount);
        String bio = user.getBio();
        if (TextUtils.isEmpty(bio)) {
            tvBio.setText(R.string.msg_no_bio);
        } else {
            tvBio.setText(bio);
        }
    }


    private void getIntroduction() {
        if (introductionFile == null)
            return;
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(BaseVoca.getIntroductionFilePathOnFirebase(getIntroductionUid()));
        storageReference.getFile(introductionFile)
                .addOnSuccessListener(getOnDownloadIntroductionSuccess())
                .addOnFailureListener(getOnDownloadIntroductionFailure());
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
            getData();
        }
    }

    @Override
    protected void onDestroy() {
        unRegisterEventBus();
        super.onDestroy();
    }

    @OnClick({R.id.v_bio, R.id.v_following, R.id.v_follower})
    void onBaseClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.v_bio:
                if (!hideInfo) {
                    openBioScreen();
                }
                break;
            case R.id.v_following:
                if (!hideInfo) {
                    openUserListScreen(Constant.USER_LIST.FOLLOWING);
                }
                break;
            case R.id.v_follower:
                if (!hideInfo) {
                    openUserListScreen(Constant.USER_LIST.FOLLOWER);
                }
                break;
            default:
                break;
        }
    }

    protected void openEditProfileScreen() {
        Intent intent = new Intent(context, EditProfileActivity.class);
        intent.putExtra(Constant.BUNDLE.KEY_PROFILE, user);
        openNewScreen(intent);
    }

    protected void openBioScreen() {
        Intent intent = new Intent(context, BioActivity.class);
        intent.putExtra(Constant.BUNDLE.KEY_PROFILE, user);
        intent.putExtra(Constant.BUNDLE.KEY_READ_ONLY, true);
        openNewScreen(intent);
    }

    protected void openUserListScreen(int userListType) {
        Intent intent = new Intent(context, ActivityUserList.class);
        intent.putExtra(Constant.BUNDLE.KEY_USER_LIST_TYPE, userListType);
        openNewScreen(intent);
    }

    private int getIntroductionUid() {
        return getOpponentId() > 0 ? getOpponentId() : getUserID();
    }

    protected abstract int getOpponentId();

    protected abstract OnSuccessListener<FileDownloadTask.TaskSnapshot> getOnDownloadIntroductionSuccess();

    protected abstract OnFailureListener getOnDownloadIntroductionFailure();
}
