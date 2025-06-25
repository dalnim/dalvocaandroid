package com.dalread.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.speech.tts.UtteranceProgressListener;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.dalread.R;
import com.dalread.adapter.NativeSpeakersVoiceAdapter;
import com.dalread.base.BaseWordInfoFragment;
import com.dalread.component.CenterLayoutManager;
import com.dalread.component.SeparatorDecoration;
import com.dalread.component.Toolbar;
import com.dalread.dialog.ChoosePlaylistDialog;
import com.dalread.helper.PlayVocaHelper;
import com.dalread.interfaces.IVocaFullPlayTTSItem;
import com.dalread.listener.OnPlaylistItemClickListener;
import com.dalread.model.StudentVoice;
import com.dalread.model.VocaDetailInfo;
import com.dalread.network.DalApiListener;
import com.dalread.util.Constant;
import com.dalread.util.Utils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindColor;
import butterknife.BindDimen;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.OnClick;

public class NativeSpeakersVoiceFragment extends BaseWordInfoFragment {

    @BindView(R.id.rv_voca)
    RecyclerView rvVoca;
    @BindView(R.id.tvSelectRange)
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

    @BindString(R.string.play_index)
    String tplIndex;

    private static final int STATE_STOP = 0;
    private static final int STATE_PAUSE = STATE_STOP + 1;
    private static final int STATE_PLAY_SINGLE = STATE_PAUSE + 1;
    private static final int STATE_PLAY_MULTI = STATE_PLAY_SINGLE + 1;

    private VocaDetailInfo voca;
    private NativeSpeakersVoiceAdapter adapter;
    private ArrayList<IVocaFullPlayTTSItem> fullItems;
    private ArrayList<IVocaFullPlayTTSItem> playlistItems;
    private ArrayList<Boolean> fullStates;
    private CenterLayoutManager layoutManager;
    private PlayVocaHelper playVocaHelper;
    private int playingState;
    private Toolbar toolbar;
    private boolean checkedChanged;
    private int checkedCount;
    private ChoosePlaylistDialog choosePlaylistDialog;

    @Override
    protected int getContentViewId() {
        return R.layout.fragment_native_speakers_voice;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initData();
        initLayout();
    }

    @Override
    public void onResume() {
        super.onResume();

        initPlayVocaHelper();
    }

    @Override
    public void requestGetData() {
    }

    @OnClick({R.id.ic_play, R.id.ic_pause, R.id.ic_stop, R.id.tvSelectRange})
    void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.ic_play:
                tvStatus.setVisibility(View.INVISIBLE);
                if (playingState == STATE_STOP) {
                    if (checkedCount > 0) {
                        updatePlaylist();
                        displayPlaylistItems();
                        playPlaylist();
                    }
                } else if (playingState == STATE_PAUSE) {
                    resumePlaylist(-1);
                }
                break;
            case R.id.ic_pause:
                if (playingState == STATE_PLAY_MULTI) {
                    pausePlaylist();
                }
                break;
            case R.id.ic_stop:
                tvStatus.setVisibility(View.VISIBLE);
                if (playingState == STATE_PLAY_SINGLE) {
                    stopPlaylist();
                } else if (playingState == STATE_PAUSE || playingState == STATE_PLAY_MULTI) {
                    stopPlaylist();
                    displayFullItems();
                }
                break;
            case R.id.tvSelectRange:
                if (playingState == STATE_STOP) {
                    choosePlaylistDialog.show();
                }
                break;
        }
    }

    private void initData() {
        fullItems = new ArrayList<>();
        playlistItems = new ArrayList<>();
        fullStates = new ArrayList<>();
        playVocaHelper = activity.getPlayVocaHelper();
        voca = (VocaDetailInfo) getArguments().getSerializable(Constant.BUNDLE.KEY_VOCA);
        if (voca != null) {
            int uid = sharedPreferences.getRealUid();
            if (uid > 0 && Utils.isConnected(activity)) {
                application.getDalAiImpl().getListOfStudentForThisVoca(
                        String.valueOf(uid),
                        sharedPreferences.getLangStudyCode(),
                        String.valueOf(voca.getVocaId()),
                        voca.getVocaType(),
                        new DalApiListener<List<StudentVoice>>() {

                            @Override
                            public void onSuccess(List<StudentVoice> response) {
                                for (StudentVoice item : response) {
                                    item.setVoca(voca);
                                    item.setVIChecked(true);
                                    fullItems.add(item);
                                    playlistItems.add(item);
                                    fullStates.add(true);
                                    checkedCount++;
                                }
                                adapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onFailure(String error) {
                            }
                        }
                );
            }
        }
    }

    private void initLayout() {
        toolbar = activity.getToolbar();
        choosePlaylistDialog = new ChoosePlaylistDialog(activity, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case R.id.tv_all:
                        if (checkedCount < fullItems.size()) {
                            updateStateAllText();
                            updateStateAllData();
                        }
                        break;
                    case R.id.tv_none:
                        if (checkedCount > 0) {
                            updateStateNoneText();
                            updateStateNoneData();
                        }
                        break;
                    case R.id.tv_last:
                        if (checkedCount == 0 || checkedCount == fullItems.size()) {
                            updateStateLastText();
                            updateStateLastData();
                        }
                        break;
                }
            }
        });
        adapter = new NativeSpeakersVoiceAdapter(sharedPreferences);
        adapter.setData(fullItems);
        adapter.setListener(onPlaylistItemClickListener);
        rvVoca.setAdapter(adapter);
        rvVoca.setLayoutManager(layoutManager = new CenterLayoutManager(activity));
        rvVoca.addItemDecoration(new SeparatorDecoration(activity, clDivider, dividerHeight));
    }

    private void initPlayVocaHelper() {
        if (!playVocaHelper.hasMotherTongueListener()) {
            playVocaHelper.setMotherTongueListener(new UtteranceProgressListener() {

                @Override
                public void onStart(String utteranceId) {
                    updateItemStatus(utteranceId, true);
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
                    updateItemStatus(utteranceId, false);
                }

                @Override
                public void onError(String utteranceId) {
                    updateItemStatus(utteranceId, false);
                }
            });
        }
    }

    private void updateItemStatus(String utteranceId, final boolean playing) {
        ArrayList<IVocaFullPlayTTSItem> items = playingState == STATE_PLAY_MULTI ? playlistItems : fullItems;
        int count = items.size();
        for (int i = 0; i < count; i++) {
            IVocaFullPlayTTSItem voca = items.get(i);
            if (utteranceId.equals(String.valueOf(voca.getVIId()))) {
                voca.setVIPlaying(playing);
                final int pos = i;
                rvVoca.post(new Runnable() {

                    @Override
                    public void run() {
                        adapter.notifyItemChanged(pos);
                        if (playingState == STATE_PLAY_MULTI && playing) {
                            layoutManager.smoothScrollToPosition(rvVoca, null, pos);
                            String title = String.format(tplIndex, pos + 1, playlistItems.size());
                            toolbar.setTitle(title);
                        }
                    }
                });
                break;
            }
        }
    }

    private void updatePlaylist() {
        if (checkedChanged) {
            checkedChanged = false;
            playlistItems.clear();
            for (IVocaFullPlayTTSItem item : fullItems) {
                if (item.isVIChecked()) {
                    playlistItems.add(item);
                }
            }
        }
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

    private void displayPlaylistItems() {
        adapter.setData(playlistItems);
        adapter.notifyDataSetChanged();
    }

    private void displayFullItems() {
        adapter.setData(fullItems);
        adapter.notifyDataSetChanged();
        rvVoca.post(new Runnable() {

            @Override
            public void run() {
                layoutManager.smoothScrollToPosition(rvVoca, null, 0);
            }
        });
    }

    private void playSingle(IVocaFullPlayTTSItem item) {
        activity.preparePlayStudentVoice(item);
        playingState = STATE_PLAY_SINGLE;
        updateIcon();
    }

    private void playPlaylist() {
        activity.preparePlayStudentVoice(playlistItems);
        playingState = STATE_PLAY_MULTI;
        updateIcon();
    }

    private void pausePlaylist() {
        playVocaHelper.pause();
        playingState = STATE_PAUSE;
        updateIcon();
    }

    private void resumePlaylist(int pos) {
        playVocaHelper.resume(pos);
        playingState = STATE_PLAY_MULTI;
        updateIcon();
    }

    private void stopPlaylist() {
        playVocaHelper.stop();
        activity.clearDownloadList();
        playingState = STATE_STOP;
        toolbar.setTitle(R.string.play_all_words);
        updateIcon();
    }

    private OnPlaylistItemClickListener onPlaylistItemClickListener = new OnPlaylistItemClickListener() {

        @Override
        public void onItemClick(Object item) {
            IVocaFullPlayTTSItem voca = (IVocaFullPlayTTSItem) item;
            if (playingState == STATE_STOP) {
                voca.setVIChecked(!voca.isVIChecked());
                adapter.notifyPlaylistItemChanged(voca);

                checkedChanged = true;
                boolean checked = voca.isVIChecked();
                if (checked) {
                    checkedCount++;
                } else {
                    checkedCount--;
                }

                if (checkedCount == fullItems.size()) {
                    updateStateAllText();
                } else if (checkedCount == 0) {
                    updateStateNoneText();
                } else {
                    updateStateLastText();
                    fullStates.set(fullItems.indexOf(voca), checked);
                }
            }
        }

        @Override
        public void onPlayClick(Object item) {
            IVocaFullPlayTTSItem voca = (IVocaFullPlayTTSItem) item;
            if (playingState == STATE_STOP) {
                playSingle(voca);
            } else if (playingState == STATE_PAUSE) {
                resumePlaylist(voca.isVIPlaying() ? -1 : playlistItems.indexOf(voca));
            } else if (playingState == STATE_PLAY_SINGLE) {
                boolean isPlaying = voca.isVIPlaying();
                stopPlaylist();
                if (!isPlaying) {
                    playSingle(voca);
                }
            }
        }
    };

    private void updateStateAllText() {
        tvStatus.setText(R.string.select_all);
        icPlay.setColorFilter(clEnable);
    }

    private void updateStateAllData() {
        playlistItems.clear();
        for (IVocaFullPlayTTSItem item : fullItems) {
            item.setVIChecked(true);
            playlistItems.add(item);
        }
        checkedCount = fullItems.size();
        checkedChanged = false;
        adapter.notifyDataSetChanged();
    }

    private void updateStateNoneText() {
        tvStatus.setText(R.string.unselect_all);
        icPlay.setColorFilter(clDisable);
    }

    private void updateStateNoneData() {
        playlistItems.clear();
        for (IVocaFullPlayTTSItem item : fullItems) {
            item.setVIChecked(false);
        }
        checkedCount = 0;
        checkedChanged = false;
        adapter.notifyDataSetChanged();
    }

    private void updateStateLastText() {
        tvStatus.setText(R.string.last_selected);
        icPlay.setColorFilter(clEnable);
    }

    private void updateStateLastData() {
        playlistItems.clear();
        checkedCount = 0;
        int count = fullItems.size();
        for (int i = 0; i < count; i++) {
            IVocaFullPlayTTSItem item = fullItems.get(i);
            boolean checked = fullStates.get(i);
            item.setVIChecked(checked);
            if (checked) {
                playlistItems.add(item);
                checkedCount++;
            }
        }
        checkedChanged = false;
        adapter.notifyDataSetChanged();
    }
}
