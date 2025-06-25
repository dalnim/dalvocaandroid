package com.dalread.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dalread.R;
import com.dalread.adapter.SelfPracticeSpeakingAdapter;
import com.dalread.base.BaseVocaActivity;
import com.dalread.component.CenterLayoutManager;
import com.dalread.component.SeparatorDecoration;
import com.dalread.dialog.AlertDialog;
import com.dalread.helper.SelfPracticeSpeakingPlayVocaHelper;
import com.dalread.listener.OnPracticeClickListener;
import com.dalread.model.VocaBook;
import com.dalread.model.VocaPractice;
import com.dalread.util.Constant;
import com.dalread.util.PermissionUtils;
import com.dalread.util.Voca;

import java.io.File;
import java.util.ArrayList;

import butterknife.BindColor;
import butterknife.BindDimen;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.OnClick;

public class SelfPracticeSpeakingActivity extends BaseVocaActivity {

    @BindView(R.id.tv_right)
    TextView tvRight;
    @BindView(R.id.v_book_info)
    View vBookInfo;
    @BindView(R.id.tv_book_name)
    TextView tvBookName;
    @BindView(R.id.tv_a)
    TextView tvA;
    @BindView(R.id.tv_b)
    TextView tvB;
    @BindView(R.id.v_start_count_down)
    View vStartCountDown;
    @BindView(R.id.tv_start_count_down)
    TextView tvStartCountDown;
    @BindView(R.id.v_app_turn)
    View vAppTurn;
    @BindView(R.id.v_your_turn)
    View vYourTurn;
    @BindView(R.id.v_recording)
    View vRecording;
    @BindView(R.id.tv_record_time_left)
    TextView tvRecordTimeLeft;
    @BindView(R.id.tv_round_left)
    TextView tvRoundLeft;
    @BindView(R.id.rv_voca)
    RecyclerView rvVoca;

    @BindString(R.string.tpl_a)
    String tplA;
    @BindString(R.string.tpl_b)
    String tplB;
    @BindString(R.string.app_name)
    String strDalvoca;
    @BindString(R.string.you)
    String strYou;

    @BindColor(R.color.color_divider)
    int clDivider;

    @BindDimen(R.dimen.divider_height)
    float dividerHeight;

    private Context context;
    private VocaBook vocaBook;
    private ArrayList<VocaPractice> vocas;
    private SelfPracticeSpeakingAdapter adapter;
    private CenterLayoutManager layoutManager;
    private SelfPracticeSpeakingPlayVocaHelper playVocaHelper;
    private String selfRole;
    private File voiceFolder;
    private File practiceFolder;
    private int roundLeft;
    private CountDownTimer practiceCountDownTimer;
    private CountDownTimer recordCountDownTimer;
    private AlertDialog alertDialog;
    private boolean isActivityStopped;
    private boolean isPracticing;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_self_practice_speaking;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initData();
        initLayout();
        initPlayer();
        initTimer();
        if (havePermissionToRecord()) {
            startCountDownAfterDelay();
        }
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
        if (isPracticing()) {
            stopPractice();
        } else {
            startCountDown();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case PermissionUtils.REQUEST_CODE_RECORD_AUDIO:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (PermissionUtils.checkWriteExternalStorage(this, true)) {
                        startCountDownAfterDelay();
                    }
                }
                break;
            case PermissionUtils.REQUEST_CODE_WRITE_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startCountDownAfterDelay();
                }
                break;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        isActivityStopped = false;
    }

    @Override
    protected void onStop() {
        isActivityStopped = true;

        super.onStop();
    }

    @Override
    protected void onDestroy() {
        destroyPlayer();
        destroyTimer();

        super.onDestroy();
    }

    private void initData() {
        context = this;
        Intent intent = getIntent();
        vocaBook = (VocaBook) intent.getSerializableExtra(Constant.BUNDLE.KEY_VOCA_BOOK);
        vocas = (ArrayList<VocaPractice>) intent.getSerializableExtra(Constant.BUNDLE.KEY_VOCA_PRACTICE_LIST);
        selfRole = Constant.SELF_PRACTICE_B;
        voiceFolder = Voca.getVoiceFolderOnLocal(context);
        practiceFolder = Voca.getVoicePracticeFolderOnLocal(context);
        roundLeft = Constant.SELF_PRACTICE_ROUND_MAX;
    }

    private void initLayout() {
        // book info
        String text = vocaBook.getName();
        tvBookName.setText(text);
        text = String.format(tplA, strDalvoca);
        tvA.setText(text);
        text = String.format(tplB, strYou);
        tvB.setText(text);
        showBookInfo();
        // recycler view
        adapter = new SelfPracticeSpeakingAdapter(vocas, sharedPreferences.getDisplayPronunciation());
        adapter.setListener(onPracticeClickListener);
        rvVoca.setAdapter(adapter);
        rvVoca.setLayoutManager(layoutManager = new CenterLayoutManager(context));
        rvVoca.addItemDecoration(BaseBindUtils.getSeparatorDecoration(context));
        // others
        alertDialog = new AlertDialog(context);
    }

    private void initPlayer() {
        playVocaHelper = new SelfPracticeSpeakingPlayVocaHelper(context, sharedPreferences, application.getDalAiImpl(), onPlayStatusChangeListener);
        playVocaHelper.setVocas(vocas);
        playVocaHelper.setSelfRole(selfRole);
        playVocaHelper.setVoiceFolder(voiceFolder);
        playVocaHelper.setPracticeFolder(practiceFolder);
        playVocaHelper.initData();
    }

    private void initTimer() {
        practiceCountDownTimer = new CountDownTimer(Constant.START_SELF_PRACTICE_COUNT_DOWN_TIME, Constant.START_SELF_PRACTICE_COUNT_DOWN_INTERVAL) {

            @Override
            public void onTick(long millisUntilFinished) {
                String text = String.valueOf(millisUntilFinished / Constant.START_SELF_PRACTICE_COUNT_DOWN_INTERVAL);
                tvStartCountDown.setText(text);
            }

            @Override
            public void onFinish() {
                if (isActivityStopped) {
                    showBookInfo();
                } else {
                    tvStartCountDown.setText("0");
                    if (!vocas.isEmpty()) {
                        startRound();
                    }
                }
            }
        };
        recordCountDownTimer = new CountDownTimer(Constant.RECORD_SELF_PRACTICE_COUNT_DOWN_TIME, Constant.RECORD_SELF_PRACTICE_COUNT_DOWN_INTERVAL) {

            @Override
            public void onTick(long millisUntilFinished) {
                String text = String.valueOf(millisUntilFinished / Constant.RECORD_SELF_PRACTICE_COUNT_DOWN_INTERVAL);
                tvRecordTimeLeft.setText(text);
            }

            @Override
            public void onFinish() {
                playVocaHelper.stopRecord();
            }
        };
    }

    private boolean havePermissionToRecord() {
        return PermissionUtils.checkRecordAudio(this, true)
                && PermissionUtils.checkWriteExternalStorage(this, true);
    }

    private void showBookInfo() {
        vBookInfo.setVisibility(View.VISIBLE);
        vStartCountDown.setVisibility(View.GONE);
        vAppTurn.setVisibility(View.GONE);
        vYourTurn.setVisibility(View.GONE);
        vRecording.setVisibility(View.GONE);
        tvRoundLeft.setVisibility(View.GONE);
    }

    private void showStartCountDown() {
        vBookInfo.setVisibility(View.GONE);
        vStartCountDown.setVisibility(View.VISIBLE);
        vAppTurn.setVisibility(View.GONE);
        vYourTurn.setVisibility(View.GONE);
        vRecording.setVisibility(View.GONE);
        tvRoundLeft.setVisibility(View.GONE);
    }

    private void showAppTurn() {
        vBookInfo.setVisibility(View.GONE);
        vStartCountDown.setVisibility(View.GONE);
        vAppTurn.setVisibility(View.VISIBLE);
        vYourTurn.setVisibility(View.GONE);
        vRecording.setVisibility(View.GONE);
        tvRoundLeft.setVisibility(View.GONE);
    }

    private void showYourTurn() {
        vBookInfo.setVisibility(View.GONE);
        vStartCountDown.setVisibility(View.GONE);
        vAppTurn.setVisibility(View.GONE);
        vYourTurn.setVisibility(View.VISIBLE);
        vRecording.setVisibility(View.GONE);
        tvRoundLeft.setVisibility(View.GONE);
    }

    private void showRecording() {
        vBookInfo.setVisibility(View.GONE);
        vStartCountDown.setVisibility(View.GONE);
        vAppTurn.setVisibility(View.GONE);
        vYourTurn.setVisibility(View.GONE);
        vRecording.setVisibility(View.VISIBLE);
        tvRoundLeft.setVisibility(View.GONE);
    }

    private void showRoundLeft() {
        vBookInfo.setVisibility(View.GONE);
        vStartCountDown.setVisibility(View.GONE);
        vAppTurn.setVisibility(View.GONE);
        vYourTurn.setVisibility(View.GONE);
        vRecording.setVisibility(View.GONE);
        tvRoundLeft.setVisibility(View.VISIBLE);
        String text = getString(R.string.more_time_to_go, roundLeft);
        tvRoundLeft.setText(text);
    }

    private void startCountDownAfterDelay() {
        rvVoca.postDelayed(new Runnable() {

            @Override
            public void run() {
                startCountDown();
            }
        }, Constant.AUTO_START_SELF_PRACTICE_DELAY);
    }

    private void startCountDown() {
        if (!isActivityStopped) {
            isPracticing = true;
            updateRightButton();
            showStartCountDown();
            practiceCountDownTimer.start();
        }
    }

    private void startRound() {
        if (roundLeft < Constant.SELF_PRACTICE_ROUND_MAX - 1) {
            playVocaHelper.startRound(true);
        } else { // play 1st native speaker voice before recording for the 1st 2 rounds
            playVocaHelper.startFirstRound(true);
        }
        if (roundLeft <= 2) {
            adapter.hideAllWords();
        }
    }

    private SelfPracticeSpeakingPlayVocaHelper.OnPlayStatusChangeListener onPlayStatusChangeListener = new SelfPracticeSpeakingPlayVocaHelper.OnPlayStatusChangeListener() {

        @Override
        public void onAppTurn(final VocaPractice voca) {
            showAppTurn();
            rvVoca.post(new Runnable() {

                @Override
                public void run() {
                    int pos = adapter.notifyItemChanged(voca);
                    layoutManager.smoothScrollToPosition(rvVoca, null, pos);
                }
            });
        }

        @Override
        public void onYourTurn(final VocaPractice voca) {
            showYourTurn();
            rvVoca.post(new Runnable() {

                @Override
                public void run() {
                    int pos = adapter.notifyItemChanged(voca);
                    layoutManager.smoothScrollToPosition(rvVoca, null, pos);
                }
            });
        }

        @Override
        public void onRecord(final VocaPractice voca) {
            showRecording();
            rvVoca.post(new Runnable() {

                @Override
                public void run() {
                    adapter.notifyItemChanged(voca);
                }
            });
            recordCountDownTimer.start();
        }

        @Override
        public void onFinishTurn(final VocaPractice voca) {
            rvVoca.post(new Runnable() {

                @Override
                public void run() {
                    adapter.notifyItemChanged(voca);
                }
            });
        }

        @Override
        public void onFinishRound() {
            if (--roundLeft > 0) {
                finishRound();
            } else {
                finishPractice();
            }
        }

        @Override
        public void onPlay(final VocaPractice voca) {
            rvVoca.post(new Runnable() {

                @Override
                public void run() {
                    adapter.notifyItemChanged(voca);
                }
            });
        }

        @Override
        public void onStop(final VocaPractice voca) {
            rvVoca.post(new Runnable() {

                @Override
                public void run() {
                    adapter.notifyItemChanged(voca);
                }
            });
        }
    };

    private void finishRound() {
        showRoundLeft();
        swapRole();
        rvVoca.postDelayed(new Runnable() {

            @Override
            public void run() {
                startRound();
            }
        }, Constant.START_SELF_PRACTICE_NEXT_ROUND_DELAY);
    }

    private void swapRole() {
        if (selfRole.equals(Constant.SELF_PRACTICE_A)) {
            selfRole = Constant.SELF_PRACTICE_B;
        } else {
            selfRole = Constant.SELF_PRACTICE_A;
        }
        playVocaHelper.setSelfRole(selfRole);
    }

    private void finishPractice() {
        isPracticing = false;
        updateRightButton();
        showBookInfo();
    }

    private void stopPractice() {
        practiceCountDownTimer.cancel();
        recordCountDownTimer.cancel();
        playVocaHelper.stopPractice();
        finishPractice();
    }

    private boolean isPracticing() {
        return isPracticing;
    }

    private void updateRightButton() {
        if (isPracticing()) {
            tvRight.setText(R.string.stop);
        } else {
            tvRight.setText(R.string.start_over);
        }
    }

    private void goToPlayAllWordsScreen() {
        Intent intent = new Intent(context, PlaylistSelfPracticeActivity.class);
        intent.putExtra(Constant.BUNDLE.KEY_VOCA_PRACTICE_LIST, vocas);
        openNewScreen(intent);
    }

    private void destroyPlayer() {
        playVocaHelper.destroy();
        playVocaHelper = null;
    }

    private void destroyTimer() {
        practiceCountDownTimer.cancel();
        practiceCountDownTimer = null;
        recordCountDownTimer.cancel();
        recordCountDownTimer = null;
    }

    @OnClick({R.id.ic_play_all, R.id.v_recording})
    void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.ic_play_all:
                if (Voca.hasAllRecordedFiles(practiceFolder, vocas)) {
                    goToPlayAllWordsScreen();
                } else {
                    alertDialog.show(
                            R.string.msg_need_practice,
                            R.string.ok,
                            null
                    );
                }
                break;
            case R.id.v_recording:
                recordCountDownTimer.cancel();
                recordCountDownTimer.onFinish();
                break;
            default:
                break;
        }
    }

    private OnPracticeClickListener onPracticeClickListener = new OnPracticeClickListener() {

        @Override
        public void onPlayClick(VocaPractice voca) {
            if (isPracticing())
                return;

            boolean isPlaying = voca.isVIPlaying();
            playVocaHelper.stopVoca();
            if (!isPlaying) {
                playVocaHelper.checkNativeSpeakerVoiceFileAndPlay(voca);
            }
        }

        @Override
        public void onGradeClick(VocaPractice voca) {
        }

        @Override
        public void onInfoClick(VocaPractice voca) {
        }
    };
}
