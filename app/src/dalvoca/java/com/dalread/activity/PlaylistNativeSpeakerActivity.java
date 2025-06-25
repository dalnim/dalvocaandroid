package com.dalread.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.RecyclerView;

import com.dalread.R;
import com.dalread.adapter.PlayListByTtsAdapter;
import com.dalread.base.BasePlayVocaNativeSpeakerActivity;
import com.dalread.component.CenterLayoutManager;
import com.dalread.component.SeparatorDecoration;
import com.dalread.dialog.ChoosePlaylistDialog;
import com.dalread.interfaces.IVocaFullPlayTTSItem;
import com.dalread.listener.OnPlayVocaListener;
import com.dalread.listener.OnPlaylistItemClickListener;
import com.dalread.util.Constant;
import com.dalread.util.LanguageUtil;
import com.dalread.util.ToastUtil;
import com.dalread.util.Voca;

import java.util.ArrayList;

import butterknife.BindColor;
import butterknife.BindDimen;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.OnClick;

public class PlaylistNativeSpeakerActivity extends BasePlayVocaNativeSpeakerActivity {

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

    @BindString(R.string.app_name)
    String appName;
    @BindString(R.string.play_index)
    String tplIndex;

    private static final int STATE_STOP = 0;
    private static final int STATE_PAUSE = STATE_STOP + 1;
    private static final int STATE_PLAY_SINGLE = STATE_PAUSE + 1;
    private static final int STATE_PLAY_MULTI = STATE_PLAY_SINGLE + 1;

    private PopupMenu popupMenu;
    private CenterLayoutManager layoutManager;
    private PlayListByTtsAdapter adapter;
    private ArrayList<IVocaFullPlayTTSItem> backupFullItems;
    private ArrayList<IVocaFullPlayTTSItem> fullItems;
    private ArrayList<IVocaFullPlayTTSItem> playlistItems;
    private ArrayList<Boolean> fullStates;
    private boolean checkedChanged;
    private int checkedCount;
    private ChoosePlaylistDialog choosePlaylistDialog;
    private int playingState;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_play_all;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initRecyclerView();
        initToolbar();
        initDialog();
        updateIcon();
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

        initPlayVocaHelper();
    }

    private void initToolbar() {
        toolbar.setSearchListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if (fullItems == null)
                    return true;

                fullItems.clear();
                if (s.isEmpty()) {
                    fullItems.addAll(backupFullItems);
                } else {
                    for (IVocaFullPlayTTSItem item : backupFullItems) {
                        if (item.getVIVoca().contains(s)) {
                            fullItems.add(item);
                        }
                    }
                }
                rvVoca.post(new Runnable() {

                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();
                    }
                });
                checkedChanged = true;
                return true;
            }
        }, new SearchView.OnCloseListener() {

            @Override
            public boolean onClose() {
                return true;
            }
        });
        toolbar.showSearchView();

        popupMenu = new PopupMenu(this, toolbar.getIconRight());
        popupMenu.getMenuInflater().inflate(R.menu.menu_voca_playlist, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(onMenuItemClickListener);
    }

    private void initRecyclerView() {
        fullItems = (ArrayList<IVocaFullPlayTTSItem>) getIntent().getSerializableExtra(Constant.BUNDLE.KEY_VOCA_PLAYLIST);
        if (fullItems == null) {
            fullItems = new ArrayList<>();
        }
        backupFullItems = new ArrayList<>(fullItems);
        playlistItems = new ArrayList<>();
        fullStates = new ArrayList<>();
        for (IVocaFullPlayTTSItem item : fullItems) {
            item.setVIChecked(true);
            playlistItems.add(item);
            fullStates.add(true);
        }
        checkedCount = fullItems.size();
        adapter = new PlayListByTtsAdapter(context, sharedPreferences.getDisplayPronunciation());
        adapter.setData(fullItems);
//        adapter.setListener(listener);
        rvVoca.setAdapter(adapter);
        rvVoca.setLayoutManager(layoutManager = new CenterLayoutManager(this));
        rvVoca.addItemDecoration(new SeparatorDecoration(this, clDivider, dividerHeight));
    }

    private void initDialog() {
        choosePlaylistDialog = new ChoosePlaylistDialog(this, new DialogInterface.OnClickListener() {

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
    }

    private void initPlayVocaHelper() {
        if (!playVocaHelper.hasOnPlayVocaListener()) {
            playVocaHelper.setOnPlayVocaListener(new OnPlayVocaListener() {

                @Override
                public void onPlay(final IVocaFullPlayTTSItem voca) {
                    voca.setVIPlaying(true);
                    rvVoca.post(new Runnable() {

                        @Override
                        public void run() {
                            int pos = adapter.notifyPlaylistItemChanged(voca);
                            if (playingState == STATE_PLAY_MULTI) {
                                layoutManager.smoothScrollToPosition(rvVoca, null, pos);
                                String title = String.format(tplIndex, pos + 1, playlistItems.size());
                                toolbar.setTitle(title);
                            }
                        }
                    });
                }

                @Override
                public void onStop(final IVocaFullPlayTTSItem voca) {
                    voca.setVIPlaying(false);
                    rvVoca.post(new Runnable() {

                        @Override
                        public void run() {
                            adapter.notifyPlaylistItemChanged(voca);
                        }
                    });
                }
            });
        }
    }

    private PopupMenu.OnMenuItemClickListener onMenuItemClickListener = new PopupMenu.OnMenuItemClickListener() {

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            int id = item.getItemId();
            switch (id) {
                case R.id.copy:
                    ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    if (clipboard != null) {
                        clipboard.setPrimaryClip(ClipData.newPlainText(appName, getPlaylistInfo()));
                        ToastUtil.getInstance(PlaylistNativeSpeakerActivity.this).show(R.string.copied);
                    }
                    break;
            }
            return true;
        }
    };

    @OnClick({R.id.ic_play, R.id.ic_pause, R.id.ic_stop, R.id.tvSelectByRange})
    void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.ic_play:
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
                if (playingState == STATE_PLAY_SINGLE) {
                    stopPlaylist();
                } else if (playingState == STATE_PAUSE || playingState == STATE_PLAY_MULTI) {
                    stopPlaylist();
                    displayFullItems();
                }
                break;
            case R.id.tvSelectByRange:
                if (playingState == STATE_STOP) {
                    choosePlaylistDialog.show();
                }
                break;
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
        startPlayVoca(item);
        playingState = STATE_PLAY_SINGLE;
        updateIcon();
    }

    private void playPlaylist() {
        startPlayVoca(playlistItems);
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
        playingState = STATE_STOP;
        toolbar.setTitle(R.string.play_all_words);
        updateIcon();
    }

    private OnPlaylistItemClickListener listener = new OnPlaylistItemClickListener() {

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

    private String getPlaylistInfo() {
        updatePlaylist();
        String result = "";
        int count = playlistItems.size();
        if (count > 0) {
            IVocaFullPlayTTSItem item = playlistItems.get(0);
            result += item.getVIMeaning(LanguageUtil.getMotherTongueLanguage(this)) + "\n" + Voca.getVocaDisplay(item);
            for (int i = 1; i < count; i++) {
                item = playlistItems.get(i);
                result += "\n\n" + item.getVIMeaning(LanguageUtil.getMotherTongueLanguage(this)) + "\n" + Voca.getVocaDisplay(item);
            }
        }
        return result;
    }
}
