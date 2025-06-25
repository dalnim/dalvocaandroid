package com.dalread.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.PopupMenu;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.dalread.R;
import com.dalread.asyntask.BackupVoicesTask;
import com.dalread.base.BaseDalVocaPlayVocaActivity;
import com.dalread.base.BaseWordInfoFragment;
import com.dalread.base.EnumLanguage;
import com.dalread.component.Toolbar;
import com.dalread.model.VocaDetailInfo;
import com.dalread.network.DalApiListener;
import com.dalread.network.events.BaseEvent;
import com.dalread.network.events.SuccessEvent;
import com.dalread.util.Constant;
import com.dalread.util.Loading;
import com.dalread.util.Utils;
import com.dalread.util.Voca;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;

@SuppressLint("NonConstantResourceId")
public class WordInfoActivity extends BaseDalVocaPlayVocaActivity {

    @BindView(R.id.nav_bottom)
    BottomNavigationView bottomNavigationView;

    private PopupMenu popupMenu;
    private int currentBottomNavigationId;
    private boolean dataChanged;
    private VocaDetailInfo voca;
    private BackupVoicesTask backupVoicesTask;

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
            application.getEventBus().post(new SuccessEvent(BaseEvent.Screen.MAIN, BaseEvent.EventType.DATA_CHANGED, 0));
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
        if (successEvent.getScreen() == BaseEvent.Screen.MAIN) {
            BaseEvent.EventType type = successEvent.getEventType();
            if (type == BaseEvent.EventType.DATA_CHANGED) {
                dataChanged = true;
            }
        }
    }

    public void initData() {
        Intent intent = getIntent();
        if (intent != null) {
            final int vocaId = intent.getIntExtra(Constant.BUNDLE.KEY_VOCA_ID, 0);
            final int vocaType = intent.getIntExtra(Constant.BUNDLE.KEY_VOCA_TYPE, 0);
            if (vocaId > 0 && vocaType > 0) {
                final int uid = getUserID();
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

                                        voca = response;
                                        int studyLang = EnumLanguage.findByFormatApi(sharedPreferences.getStudyLanguage()).getIdApi();
                                        voca.setPath(Voca.getOutputRecordingFileName(
                                                studyLang,
                                                vocaType,
                                                vocaId,
                                                uid
                                        ));
                                        Fragment fragment = getSupportFragmentManager().findFragmentById(getFragmentContainerId());
                                        if (fragment instanceof BaseWordInfoFragment) {
                                            Bundle bundle = fragment.getArguments();
                                            if (bundle != null) {
                                                bundle.putSerializable(Constant.BUNDLE.KEY_VOCA, voca);
                                            }
                                            ((BaseWordInfoFragment) fragment).requestGetData();
                                        }
                                        if (dataChanged) {
                                            dataChanged = false;
                                        } else {
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
        }
    }

    private void initLayout() {
        if (toolbar != null) {
            popupMenu = new PopupMenu(this, toolbar.getIconRight());
            popupMenu.getMenuInflater().inflate(R.menu.menu_word_info, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(item -> {
                int id = item.getItemId();
                switch (id) {
                    case R.id.study:
                        openStudyScreen();
                        return true;
                    case R.id.edit_meaning:
                        openEditMeaningScreen();
                        return true;
                    case R.id.backup:
                        backupVoice();
                        return true;
                }
                return false;
            });
        }

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id != currentBottomNavigationId) {
                switch (id) {
                    case R.id.nav_info:
                        voca.setVIPlaying(false);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable(Constant.BUNDLE.KEY_VOCA, voca);
                        Fragment fragment = new WordInfoFragment();
                        fragment.setArguments(bundle);
                        Utils.loadFragment(WordInfoActivity.this, fragment, getFragmentContainerId(), false);
                        toolbar.setTitle(R.string.word_info);
                        currentBottomNavigationId = id;
                        return true;
                    case R.id.nav_native:
                        voca.setVIPlaying(false);
                        bundle = new Bundle();
                        bundle.putSerializable(Constant.BUNDLE.KEY_VOCA, voca);
                        fragment = new NativeSpeakersVoiceFragment();
                        fragment.setArguments(bundle);
                        Utils.loadFragment(WordInfoActivity.this, fragment, getFragmentContainerId(), false);
                        toolbar.setTitle(R.string.native_voice);
                        currentBottomNavigationId = id;
                        return true;
//                    case R.id.nav_backup:
//                        voca.setPIPlaying(false);
//                        bundle = new Bundle();
//                        bundle.putSerializable(Constant.BUNDLE.KEY_VOCA, voca);
//                        fragment = new BackupRecordingListFragment();
//                        fragment.setArguments(bundle);
//                        Utils.loadFragment(WordInfoActivity.this, fragment, getFragmentContainerId(), false);
//                        toolbar.setTitle(R.string.backup_voice_recordings);
//                        currentBottomNavigationId = id;
//                        return true;
                }
            }
            return false;
        });
    }

    private void openStudyScreen() {
        Intent intent = new Intent(this, StudyActivity.class);
        intent.putExtra(Constant.BUNDLE.KEY_VOCA, Voca.createVocaStudy(voca));
        intent.putExtra(Constant.BUNDLE.KEY_SELF_STUDY, true);
        openNewScreen(intent);
    }

    private void openEditMeaningScreen() {
        Intent intent = new Intent(this, EditMeaningActivity.class);
        intent.putExtra(Constant.BUNDLE.KEY_VOCA, voca);
        openNewScreen(intent);
    }

    private void backupVoice() {
        if (voca == null)
            return;
        if (backupVoicesTask == null) {
            backupVoicesTask = new BackupVoicesTask(this, getUserID(), EnumLanguage.findByFormatApi(sharedPreferences.getStudyLanguage()).getIdApi());
        }
        backupVoicesTask.backup(voca);
    }

    public void setDataChanged(boolean dataChanged) {
        this.dataChanged = dataChanged;
    }

    public Toolbar getToolbar() {
        return toolbar;
    }
}
