package com.dalread.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.dalread.R;
import com.dalread.adapter.SelfPracticeSpeakingAdapter;
import com.dalread.base.BaseVocaActivity;
import com.dalread.component.CenterLayoutManager;
import com.dalread.component.SeparatorDecoration;
import com.dalread.helper.SelfPracticeSpeakingPlayVocaHelper;
import com.dalread.listener.OnPracticeClickListener;
import com.dalread.model.VocaPractice;
import com.dalread.util.Constant;
import com.dalread.util.Voca;

import java.io.File;
import java.util.ArrayList;

import butterknife.BindColor;
import butterknife.BindDimen;
import butterknife.BindView;
import butterknife.OnClick;

public class PlaylistSelfPracticeActivity extends BaseVocaActivity {

    @BindView(R.id.rv_voca)
    RecyclerView rvVoca;
    @BindView(R.id.tvSelectByRange)
    TextView tvStatus;
    @BindView(R.id.ic_play)
    ImageView icPlay;
    @BindView(R.id.ic_pause)
    ImageView icPause;
    @BindView(R.id.ic_stop)
    ImageView icStop;

    @BindColor(R.color.color_divider)
    int clDivider;
    @BindColor(R.color.colorBlack)
    int clEnable;
    @BindColor(R.color.color_stop)
    int clDisable;

    @BindDimen(R.dimen.divider_height)
    float dividerHeight;

    private static final int STATE_STOP = 0;
    private static final int STATE_PAUSE = STATE_STOP + 1;
    private static final int STATE_PLAY_SINGLE = STATE_PAUSE + 1;
    private static final int STATE_PLAY_MULTI = STATE_PLAY_SINGLE + 1;

    private Context context;
    private ArrayList<VocaPractice> vocas;
    private String selfRole;
    private File voiceFolder;
    private File practiceFolder;
    private SelfPracticeSpeakingAdapter adapter;
    private CenterLayoutManager layoutManager;
    private SelfPracticeSpeakingPlayVocaHelper playVocaHelper;
    private int playingState;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_play_all;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initData();
        initLayout();
        initPlayer();
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
    protected void onDestroy() {
        destroyPlayer();

        super.onDestroy();
    }

    private void initData() {
        context = this;
        vocas = (ArrayList<VocaPractice>) getIntent().getSerializableExtra(Constant.BUNDLE.KEY_VOCA_PRACTICE_LIST);
        selfRole = Constant.SELF_PRACTICE_B;
        voiceFolder = Voca.getVoiceFolderOnLocal(context);
        practiceFolder = Voca.getVoicePracticeFolderOnLocal(context);
    }

    private void initLayout() {
        // recycler view
        adapter = new SelfPracticeSpeakingAdapter(vocas, sharedPreferences.getDisplayPronunciation());
        adapter.setListener(onPracticeClickListener);
        rvVoca.setAdapter(adapter);
        rvVoca.setLayoutManager(layoutManager = new CenterLayoutManager(context));
        rvVoca.addItemDecoration(BaseBindUtils.getSeparatorDecoration(context));
        // others
        tvStatus.setVisibility(View.INVISIBLE);
    }

    private void initPlayer() {
        playVocaHelper = new SelfPracticeSpeakingPlayVocaHelper(context, sharedPreferences, application.getDalAiImpl(), onPlayStatusChangeListener);
        playVocaHelper.setVocas(vocas);
        playVocaHelper.setSelfRole(selfRole);
        playVocaHelper.setVoiceFolder(voiceFolder);
        playVocaHelper.setPracticeFolder(practiceFolder);
        playVocaHelper.initData();
    }

    private void playSingle(VocaPractice voca) {
        playVocaHelper.checkNativeSpeakerVoiceFileAndPlay(voca);
        playingState = STATE_PLAY_SINGLE;
        updateIcon();
    }

    private void playPlaylist() {
        playVocaHelper.startRound(false);
        playingState = STATE_PLAY_MULTI;
        updateIcon();
    }

    private void pausePlaylist() {
        playVocaHelper.pausePractice();
        playingState = STATE_PAUSE;
        updateIcon();
    }

    private void resumePlaylist() {
        playVocaHelper.resumePractice();
        playingState = STATE_PLAY_MULTI;
        updateIcon();
    }

    private void resumePlaylist(VocaPractice voca) {
        playVocaHelper.resumePractice(voca);
        playingState = STATE_PLAY_MULTI;
        updateIcon();
    }

    private void stopPlaylist() {
        playVocaHelper.stopPractice();
        playingState = STATE_STOP;
        updateIcon();
    }

    private void updateIcon() {
        switch (playingState) {
            case STATE_STOP:
                icPlay.setColorFilter(clEnable);
                icPause.setColorFilter(clDisable);
                icStop.setColorFilter(clDisable);
                break;
            case STATE_PAUSE:
                icPlay.setColorFilter(clEnable);
                icPause.setColorFilter(clDisable);
                icStop.setColorFilter(clEnable);
                break;
            case STATE_PLAY_SINGLE:
                icPlay.setColorFilter(clDisable);
                icPause.setColorFilter(clDisable);
                icStop.setColorFilter(clEnable);
                break;
            case STATE_PLAY_MULTI:
                icPlay.setColorFilter(clDisable);
                icPause.setColorFilter(clEnable);
                icStop.setColorFilter(clEnable);
                break;
        }
    }

    private void swapRole() {
        if (selfRole.equals(Constant.SELF_PRACTICE_A)) {
            selfRole = Constant.SELF_PRACTICE_B;
        } else {
            selfRole = Constant.SELF_PRACTICE_A;
        }
        playVocaHelper.setSelfRole(selfRole);
    }

    private void destroyPlayer() {
        playVocaHelper.destroy();
        playVocaHelper = null;
    }

    @OnClick({R.id.ic_left, R.id.ic_play, R.id.ic_pause, R.id.ic_stop})
    void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.ic_play:
                if (playingState == STATE_STOP) {
                    playPlaylist();
                } else if (playingState == STATE_PAUSE) {
                    resumePlaylist();
                }
                break;
            case R.id.ic_pause:
                if (playingState == STATE_PLAY_MULTI) {
                    pausePlaylist();
                }
                break;
            case R.id.ic_stop:
                if (playingState != STATE_STOP) {
                    stopPlaylist();
                }
            default:
                break;
        }
    }

    private OnPracticeClickListener onPracticeClickListener = new OnPracticeClickListener() {

        @Override
        public void onPlayClick(VocaPractice voca) {
            if (playingState == STATE_STOP) {
                playSingle(voca);
            } else if (playingState == STATE_PAUSE) {
                resumePlaylist(voca);
            } else if (playingState == STATE_PLAY_SINGLE) {
                boolean isPlaying = voca.isVIPlaying();
                stopPlaylist();
                if (!isPlaying) {
                    playSingle(voca);
                }
            }
        }

        @Override
        public void onGradeClick(VocaPractice voca) {
        }

        @Override
        public void onInfoClick(VocaPractice voca) {
        }
    };

    private SelfPracticeSpeakingPlayVocaHelper.OnPlayStatusChangeListener onPlayStatusChangeListener = new SelfPracticeSpeakingPlayVocaHelper.OnPlayStatusChangeListener() {

        @Override
        public void onAppTurn(final VocaPractice voca) {
            voca.setVIChecked(true); // this is to update grey background
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
            voca.setVIChecked(true); // this is to update grey background
            rvVoca.post(new Runnable() {

                @Override
                public void run() {
                    int pos = adapter.notifyItemChanged(voca);
                    layoutManager.smoothScrollToPosition(rvVoca, null, pos);
                }
            });
        }

        @Override
        public void onRecord(VocaPractice voca) {
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
            swapRole();
            rvVoca.postDelayed(new Runnable() {

                @Override
                public void run() {
                    playVocaHelper.startRound(false);
                }
            }, Constant.START_SELF_PRACTICE_NEXT_ROUND_DELAY);
        }

        @Override
        public void onPlay(final VocaPractice voca) {
            voca.setVIChecked(true); // this is to update grey background
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
}
