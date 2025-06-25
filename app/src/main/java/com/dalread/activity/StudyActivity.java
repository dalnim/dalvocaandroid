package com.dalread.activity;

import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.UtteranceProgressListener;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.dalread.R;
import com.dalread.base.EnumLanguage;
import com.dalread.base.PlayVocaActivity;
import com.dalread.dialog.AlertDialog;
import com.dalread.model.VocaStudy;
import com.dalread.util.BaseVoca;
import com.dalread.util.Constant;
import com.dalread.util.LanguageUtil;
import com.dalread.util.Utils;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.Serializable;

import butterknife.BindView;
import butterknife.OnClick;

public class StudyActivity extends PlayVocaActivity {

    @BindView(R.id.nav_bottom)
    BottomNavigationView bottomNavigationView;
    @BindView(R.id.tv_word)
    TextView tvWord;
    @BindView(R.id.tv_meaning)
    TextView tvMeaning;
    @BindView(R.id.ic_play)
    ImageView icPlay;

    private int currentBottomNavigationId;
    private VocaStudy voca;
    private String vocaDisplay;
    private boolean selfStudy;
    private AlertDialog alertDialog;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_study;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initBottomNavigation();
        initDialog();
        initData();
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

        initPlayVocaHelper();
    }

    @OnClick({R.id.ic_play, R.id.tv_word})
    void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.ic_play:
                playVoca();
                break;
            case R.id.tv_word:
                voca.setDisplayed(!voca.isDisplayed());
                displayStarOrVoca();
                break;
        }
    }

    private void initBottomNavigation() {
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id != currentBottomNavigationId) {
                    switch (id) {
                        case R.id.nav_writing:
                            boolean handWriting = (EnumLanguage.CHINESE_SIMPLIFIED.getFormatApi().equals(sharedPreferences.getStudyLanguage())
                                    || EnumLanguage.HANJA.getFormatApi().equals(sharedPreferences.getStudyLanguage()))
                                    && !BaseVoca.containTwoChineseCharacters(vocaDisplay);
                            if (handWriting) {
                                openHandWritingScreen();
                            } else {
                                openChoosingScreen();
                            }
                            currentBottomNavigationId = id;
                            toolbar.setTitle(R.string.study_writing);
                            return true;
                        case R.id.nav_speaking:
                            Bundle bundle = new Bundle();
                            bundle.putSerializable(Constant.BUNDLE.KEY_VOCA, voca);
                            Fragment fragment = new StudySpeakingFragment();
                            fragment.setArguments(bundle);
                            Utils.loadFragment(StudyActivity.this, fragment, getFragmentContainerId(), false);
                            currentBottomNavigationId = id;
                            toolbar.setTitle(R.string.study_speaking);
                            hideFinishButton();
                            return true;
                    }
                }
                return false;
            }
        });
    }

    private void initDialog() {
        alertDialog = new AlertDialog(this);
    }

    private void initData() {
        Intent intent = getIntent();
        if (intent != null) {
            Serializable serializable = intent.getSerializableExtra(Constant.BUNDLE.KEY_VOCA);
            if (serializable != null) {
                voca = (VocaStudy) serializable;
                vocaDisplay = BaseVoca.getVocaDisplay(voca);
                displayStar();
                String text = voca.getVIMeaning(LanguageUtil.getMotherTongueLanguage(this));
                tvMeaning.setText(text);
                BaseVoca.updateIconSpeaker(icPlay, voca);

                if (getSupportFragmentManager().getFragments().isEmpty()) {
                    bottomNavigationView.setSelectedItemId(R.id.nav_writing);
                }
            }
            selfStudy = intent.getBooleanExtra(Constant.BUNDLE.KEY_SELF_STUDY, false);
        }
    }

    public void displayStar() {
        voca.setDisplayed(false);
        displayStarOrVoca();
    }

    private void displayStarOrVoca() {
        String text;
        if (voca.isDisplayed()) {
            text = vocaDisplay;
            if (sharedPreferences.getDisplayPronunciation() && !TextUtils.isEmpty(voca.getPronounce())) {
                text += " [" + voca.getPronounce() + "]";
            }
        } else {
            text = BaseVoca.getStarForText(vocaDisplay);
        }
        tvWord.setText(text);
    }

    private void initPlayVocaHelper() {
        if (!playVocaHelper.hasMotherTongueListener()) {
            playVocaHelper.setMotherTongueListener(new UtteranceProgressListener() {

                @Override
                public void onStart(String utteranceId) {
                    updateItemStatus(true);
                }

                @Override
                public void onDone(String utteranceId) {
                }

                @Override
                public void onError(String utteranceId) {
                }
            });
        }
        if (!playVocaHelper.hasStudyListener()) {
            playVocaHelper.setStudyListener(new UtteranceProgressListener() {

                @Override
                public void onStart(String utteranceId) {
                }

                @Override
                public void onDone(String utteranceId) {
                    updateItemStatus(false);
                }

                @Override
                public void onError(String utteranceId) {
                    updateItemStatus(false);
                }
            });
        }
    }

    private void updateItemStatus(boolean playing) {
        voca.setVIPlaying(playing);
        icPlay.post(new Runnable() {

            @Override
            public void run() {
                BaseVoca.updateIconSpeaker(icPlay, voca);
            }
        });
    }

    private void playVoca() {
        boolean isPlaying = voca.isVIPlaying();
        playVocaHelper.stop();
//        if (!isPlaying) {
//            preparePlayVoca(voca);
//        }
    }

    public void finishWriting() {
        bottomNavigationView.setSelectedItemId(R.id.nav_speaking);
    }

    private void finishSpeaking() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(getFragmentContainerId());
        if (fragment instanceof StudySpeakingFragment) {
            ((StudySpeakingFragment) fragment).finishSpeaking(selfStudy);
        }
    }

    public void showFinishButton() {
        toolbar.setTextRight(R.string.tb_finish);
        toolbar.getIconRight().setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (Utils.isConnected(StudyActivity.this)) {
                    hideFinishButton();
                    finishSpeaking();
                } else {
                    alertDialog.showNoInternet();
                }
            }
        });
        toolbar.showTvRight();
    }

    public void hideFinishButton() {
        toolbar.hideTvRight();
    }

    public void showHandWritingButton() {
        toolbar.setTextRight(R.string.tb_hand_writing);
        toolbar.getTvRight().setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                openHandWritingScreen();
            }
        });
        toolbar.showTvRight();
    }

    public void showChoosingButton() {
        toolbar.setTextRight(R.string.tb_choosing);
        toolbar.getIconRight().setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                openChoosingScreen();
            }
        });
        toolbar.showTvRight();
    }

    private void openHandWritingScreen() {
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constant.BUNDLE.KEY_VOCA, voca);
        Fragment fragment = new StudyHandWritingFragment();
        fragment.setArguments(bundle);
        Utils.loadFragment(StudyActivity.this, fragment, getFragmentContainerId(), false);
        showChoosingButton();
    }

    private void openChoosingScreen() {
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constant.BUNDLE.KEY_VOCA, voca);
        Fragment fragment = new StudyChoosingFragment();
        fragment.setArguments(bundle);
        Utils.loadFragment(StudyActivity.this, fragment, getFragmentContainerId(), false);
        showHandWritingButton();
    }
}
