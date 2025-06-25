package com.dalread.activity;

import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.dalread.base.BaseActivity;
import com.dalread.composition.PlayTTS;
import com.dalread.composition.VocaKnowActivity;
import com.dalread.database.sqlite.SubDatabase;
import com.dalread.helper.CustomTabActivityHelper;
import com.dalread.util.BaseStorageUtil;
import com.dalread.util.ChatGptWebUtil;
import com.dalread.util.DLog;

//이건 BaseConvActivity등을 상속받지 못하는 main folder에 있는 클래스에서 쓸려고 만든거다.
public abstract class BaseMainFolderActivity extends BaseActivity implements CustomTabActivityHelper.ConnectionCallback {
    protected PlayTTS playTTS;
    protected VocaKnowActivity vocaKnowActivity;
    protected SubDatabase subDatabase;
    protected CustomTabActivityHelper customTabActivityHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initSubDatabase();
        playTTS = new PlayTTS(this);
        vocaKnowActivity = new VocaKnowActivity(this, subDatabase);
        initData();
        initLayout();
        initDialog();
        initListener();
         // In VocaActivity.
        getData();
    }


    protected void initData() {

    }

    protected void initDialog() {

    }

    protected void initLayout() {

    }

    protected void initListener() {
        customTabActivityHelper = new CustomTabActivityHelper();
        customTabActivityHelper.setConnectionCallback(this);
    }

    private void initSubDatabase() {
        if (subDatabase != null) {
            subDatabase.close();
        }
        subDatabase = null;
        String destPathWithFileName = BaseStorageUtil.getAraConvDBPathWithFileName(context);
        subDatabase = SubDatabase.getInstance(this, destPathWithFileName);
    }


    protected void getData() {

    }

    @Override
    protected void onStart() {
        super.onStart();
        customTabActivityHelper.bindCustomTabsService(this);
        DLog.i(getLogTag(), "onStart");
        if (!sharedPreferences.getKeepPlayingOnBackgroundMode())
            playTTS.stopPlayVoca();
    }

    @Override
    protected void onPause() {
        DLog.i(getLogTag(), "onPause");
        if (isFinishing()) {
            playTTS.stopPlayVoca();
        }
        super.onPause();
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
    public void onCustomTabsConnected() {
        Uri uri  = Uri.parse(ChatGptWebUtil.getGptUrl(this));
        customTabActivityHelper.mayLaunchUrl(uri, null, null);
    }

    @Override
    public void onCustomTabsDisconnected() {

    }

    @Override
    protected void onStop() {
        super.onStop();
        customTabActivityHelper.unbindCustomTabsService(this);
    }
}
