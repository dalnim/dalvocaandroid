package com.dalread.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.dalread.R;
import com.dalread.asyntask.BackupVoicesTask;
import com.dalread.base.BaseWordInfoFragment;
import com.dalread.base.EnumLanguage;
import com.dalread.base.PlayVocaActivity;
import com.dalread.component.Toolbar;
import com.dalread.interfaces.IVocaFullItem;
import com.dalread.model.VocaDetailInfo;
import com.dalread.network.DalApiListener;
import com.dalread.network.events.BaseEvent;
import com.dalread.network.events.SuccessEvent;
import com.dalread.util.Constant;
import com.dalread.util.DLog;
import com.dalread.util.Loading;
import com.dalread.util.Utils;
import com.dalread.util.Voca;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;

public class WordInfoActivity extends PlayVocaActivity {

    @BindView(R.id.nav_bottom) BottomNavigationView bottomNavigationView;
    private PopupMenu popupMenu;
    private int currentBottomNavigationId;
    private boolean dataChanged;
    private VocaDetailInfo vocaDetailInfo;
    private BackupVoicesTask backupVoicesTask;
    private IVocaFullItem iVocaFullItem;
//    private PlayerFileModel playerFileModel;
//    private String idsDataChangeFromPhraseInfor;

    public static Intent createIntent(Context context, IVocaFullItem iVocaFullItem) {
        Intent intent = new Intent(context, WordInfoActivity.class);
        intent.putExtra(Constant.BUNDLE.KEY_VOCA, iVocaFullItem);
        return intent;
    }

    @Override
    protected int getContentViewId() {
        return R.layout.activity_word_info;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
        initLayout();
        initEventBus();
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
        popupMenu.show();
    }

    @Override
    public void onHeaderTextRightClick() {

    }

    @Override
    public void onResume() {
        super.onResume();

        if (dataChanged) {
            initData();
        }
    }

    @Override
    public void onBackPressed() {
        if (dataChanged) {
//            application.getEventBus().post(new SuccessEvent(BaseEvent.Screen.DICTATION_MODE, BaseEvent.EventType.DATA_CHANGED, new String[]{idsDataChangeFromPhraseInfor, String.valueOf(voca.getVocaId())}));
//            application.getEventBus().post(new SuccessEvent(BaseEvent.Screen.DAL_PLAYER, BaseEvent.EventType.DATA_CHANGED, new String[]{idsDataChangeFromPhraseInfor, String.valueOf(voca.getVocaId())}));
//            application.getEventBus().post(new SuccessEvent(BaseEvent.Screen.MAIN, BaseEvent.EventType.DATA_CHANGED, idsDataChangeFromPhraseInfor));
        }

        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        unRegisterEventBus();
        super.onDestroy();
    }

    @Subscribe
    public void onEvent(SuccessEvent successEvent) {
//        if (successEvent.getScreen() == BaseEvent.Screen.MAIN) {
//            BaseEvent.EventType type = successEvent.getEventType();
//            if (type == BaseEvent.EventType.DATA_CHANGED) {
//                dataChanged = true;
//                ids = (String) successEvent.getModel();
//            }
//        }
        if (successEvent.getScreen() == BaseEvent.Screen.EDIT_VOCA) {
            BaseEvent.EventType type = successEvent.getEventType();
            if (type == BaseEvent.EventType.DATA_CHANGED) {
                dataChanged = true;
//                EditVoca editVoca = (EditVoca)successEvent.getModel();
//                activity.getSubDatabase().updatePhraseInformation(editVoca);
//                String subtitleIdsThatContainThisVoca = getSubDatabase().getIdOfSubtitleFromPhraseInformation(editVoca.getiVocaFullItem().getVIVocaId());
//
//                this.idsDataChangeFromPhraseInfor = subtitleIdsThatContainThisVoca;
//                this.vocaIdDataChangeFromPhraseInfor = editVoca.getiVocaFullItem().getVIVocaId().toString();
//                this.oldVocaIdDataChangeFromPhraseInfor = editVoca.getOldVocaId().toString();
//                this.isDataChangeFromPhraseInfor = true;
//                break;
//

//                ids = (String) successEvent.getModel();
//                String[] data = (String[]) successEvent.getModel();
//                idsDataChangeFromPhraseInfor = data[0];
            }
        }
    }

    public void initData() {
//        Intent intent = getIntent();
//        if (intent != null) {
            iVocaFullItem = (IVocaFullItem)getIntent().getSerializableExtra(Constant.BUNDLE.KEY_VOCA);
            final int vocaId = iVocaFullItem.getVIVocaId();
            final int vocaType = iVocaFullItem.getVIVocaType();

//            final int vocaId = intent.getIntExtra(Constant.BUNDLE.KEY_VOCA_ID, 0);
//            final int vocaType = intent.getIntExtra(Constant.BUNDLE.KEY_VOCA_TYPE, 0);
//            playerFileModel = intent.getParcelableExtra(Constant.PLAYER.INTENT.KEY_VIDEO_FILE);
            DLog.d(getLogTag(), "vocaId=" + vocaId + " - vocaType=" + vocaType);
            if (vocaId > 0 && vocaType > 0) {
                final int uid = sharedPreferences.getUidDefault();
                if (uid > 0) {
                    if (Utils.isConnected(this)) {
                        Loading.show(this);
                        application.getDalAiImpl().getVocaDetailInfo(
                                String.valueOf(uid),
                                sharedPreferences.getLangStudyCode(),
                                sharedPreferences.getMotherTongueLangCode(),
                                vocaId,
                                vocaType,
                                new DalApiListener<VocaDetailInfo>() {

                                    @Override
                                    public void onSuccess(VocaDetailInfo response) {
                                        Loading.hide();

                                        vocaDetailInfo = response;
                                        int studyLang = EnumLanguage.findByFormatApi(sharedPreferences.getStudyLanguage()).getIdApi();
                                        vocaDetailInfo.setPath(Voca.getOutputRecordingFileName(
                                                studyLang,
                                                vocaType,
                                                vocaId,
                                                uid
                                        ));
                                        Fragment fragment = getSupportFragmentManager().findFragmentById(getFragmentContainerId());
                                        if (fragment instanceof BaseWordInfoFragment) {
                                            fragment.getArguments().putSerializable(Constant.BUNDLE.KEY_VOCA, vocaDetailInfo);
                                            ((BaseWordInfoFragment) fragment).requestGetData();
                                        }
                                        if (!dataChanged) {
                                            bottomNavigationView.setSelectedItemId(R.id.nav_info);
                                        }
                                    }

                                    @Override
                                    public void onFailure(String error) {
                                        Loading.hide();
                                    }
                                }
                        );
                    }
                }
            }
//        }
    }

    private void initLayout() {
        popupMenu = new PopupMenu(this, toolbar.getIconRight());
        popupMenu.getMenuInflater().inflate(R.menu.menu_word_info, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();
                switch (id) {
                    case R.id.study:
                        openStudyScreen();
                        return true;
                    case R.id.edit_phrase:
                        openEditMeaningScreen();
                        return true;
//                    case R.id.backup:
//                        backupVoice();
//                        return true;
                    case R.id.web_dictionary:
                        openWebDictionaryContent();
                        return true;
                    case R.id.cancel:
                        popupMenu.dismiss();
                        return true;
                }
                return false;
            }
        });
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id != currentBottomNavigationId) {
                    switch (id) {
                        case R.id.nav_info:
//                            vocaDetailInfo.setPIPlaying(false);
                            Bundle bundle = new Bundle();
                            bundle.putSerializable(Constant.BUNDLE.KEY_VOCA, vocaDetailInfo);
                            Fragment fragment = new WordInfoFragment();
                            fragment.setArguments(bundle);
                            Utils.loadFragment(WordInfoActivity.this, fragment, getFragmentContainerId(), false);
                            toolbar.setTitle(R.string.word_info);
                            currentBottomNavigationId = id;
                            return true;
                        case R.id.nav_native:
//                            vocaDetailInfo.setPIPlaying(false);
                            bundle = new Bundle();
                            bundle.putSerializable(Constant.BUNDLE.KEY_VOCA, vocaDetailInfo);
                            fragment = new NativeSpeakersVoiceFragment();
                            fragment.setArguments(bundle);
                            Utils.loadFragment(WordInfoActivity.this, fragment, getFragmentContainerId(), false);
                            toolbar.setTitle(R.string.native_voice);
                            currentBottomNavigationId = id;
                            return true;
//                        case R.id.nav_backup:
//                            voca.setPIPlaying(false);
//                            bundle = new Bundle();
//                            bundle.putSerializable(Constant.BUNDLE.KEY_VOCA, voca);
//                            fragment = new BackupRecordingListFragment();
//                            fragment.setArguments(bundle);
//                            Utils.loadFragment(WordInfoActivity.this, fragment, getFragmentContainerId(), false);
//                            toolbar.setTitle(R.string.backup_voice_recordings);
//                            currentBottomNavigationId = id;
//                            return true;
                    }
                }
                return false;
            }
        });
    }

    private void openStudyScreen() {
        Intent intent = new Intent(this, StudyActivity.class);
        intent.putExtra(Constant.BUNDLE.KEY_VOCA, iVocaFullItem.getVIVoca());
        intent.putExtra(Constant.BUNDLE.KEY_SELF_STUDY, true);
        openNewScreen(intent);
    }

    private void openEditMeaningScreen() {
        openNewScreen(
                EditMeaningActivity.createIntent(WordInfoActivity.this, iVocaFullItem)
        );


//        if (!Utils.isConnected(this)) {
//            ToastUtil.getInstance(this).show(R.string.msg_log_in_required_to_edit_meaning);
//            return;
//        }
//
//        try {
//            Intent intent = new Intent(this, EditMeaningActivity.class);
//            intent.putExtra(Constant.BUNDLE.KEY_VOCA, voca);
////            intent.putExtra(Constant.PLAYER.INTENT.KEY_VIDEO_FILE, playerFileModel);
//            openNewScreen(intent);
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
    }

//    private void backupVoice() {
//        if (iVocaFullItem == null)
//            return;
//        if (backupVoicesTask == null) {
//            backupVoicesTask = new BackupVoicesTask(this, getUserID(), EnumLanguage.findByFormatApi(sharedPreferences.getStudyLanguage()).getIdApi());
//        }
//        backupVoicesTask.backup(vocaDetailInfo);
//    }

    public void setDataChanged(boolean dataChanged) {
        this.dataChanged = dataChanged;
    }

    public Toolbar getToolbar() {
        return toolbar;
    }

    private void openWebDictionaryContent() {
        Intent intent = new Intent(this, WebDictionaryContentActivity.class);
        intent.putExtra(Constant.PLAYER.INTENT.KEY_WORD, iVocaFullItem.getVIVoca());
        startActivity(intent);
    }
}
