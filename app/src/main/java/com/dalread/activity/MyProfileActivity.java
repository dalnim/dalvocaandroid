package com.dalread.activity;

import android.content.pm.PackageManager;
import android.view.View;

import androidx.annotation.NonNull;

import com.dalread.R;
import com.dalread.base.BaseProfileActivity;
import com.dalread.dialog.RecordIntroductionDialog;
import com.dalread.util.BasePermissionUtils;
import com.dalread.util.Constant;
import com.dalread.util.DLog;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;

import butterknife.OnClick;

public class MyProfileActivity extends BaseProfileActivity {

    private RecordIntroductionDialog recordIntroductionDialog;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_my_profile;
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
        openEditProfileScreen();
    }

    @Override
    protected int getOpponentId() {
        return 0;
    }

    @Override
    protected OnSuccessListener<FileDownloadTask.TaskSnapshot> getOnDownloadIntroductionSuccess() {
        return new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {

            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                DLog.i(getLogTag(), "Download introduction onSuccess");
            }
        };
    }

    @Override
    protected OnFailureListener getOnDownloadIntroductionFailure() {
        return new OnFailureListener() {

            @Override
            public void onFailure(@NonNull Exception e) {
                DLog.i(getLogTag(), "Download introduction onFailure");
            }
        };
    }

    @Override
    protected void initLayout() {
        super.initLayout();

        recordIntroductionDialog = new RecordIntroductionDialog(context, introductionFile);
    }

    @Override
    protected void bindData() {
        super.bindData();

        recordIntroductionDialog.setHasIntroductionFile(user.getHasIntroductionFile() == 1);
    }

    @OnClick({R.id.v_favorite, R.id.v_block, R.id.iv_introduction})
    void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.v_favorite:
                openUserListScreen(Constant.USER_LIST.FAVORITE);
                break;
            case R.id.v_block:
                openUserListScreen(Constant.USER_LIST.BLOCK);
                break;
            case R.id.iv_introduction:
                openRecordIntroductionDialog();
                break;
            default:
                break;
        }
    }

    private void openRecordIntroductionDialog() {
        if (BasePermissionUtils.checkRecordAudio(this, true)
                && BasePermissionUtils.checkWriteExternalStorage(this, true)) {
            recordIntroductionDialog.show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            openRecordIntroductionDialog();
        }
    }

    @Override
    protected void onDestroy() {
        recordIntroductionDialog.onDestroy();

        super.onDestroy();
    }
}
