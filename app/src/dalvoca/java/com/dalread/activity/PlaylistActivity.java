package com.dalread.activity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.RecyclerView;

import com.dalread.DalFlavor;
import com.dalread.R;
import com.dalread.adapter.PlayListByTtsAdapter;
import com.dalread.asyntask.BackupVoicesTask;
import com.dalread.base.BaseDalVocaPlayVocaActivity;
import com.dalread.base.BaseDialog;
import com.dalread.base.BaseDialogListener;
import com.dalread.base.EnumLanguage;
import com.dalread.base.EnumType;
import com.dalread.component.CenterLayoutManager;
import com.dalread.component.SeparatorDecoration;
import com.dalread.dialog.AddFolderNameDialog;
import com.dalread.dialog.AlertDialog;
import com.dalread.dialog.ChoosePlaylistDialog;
import com.dalread.dialog.SelectRangeDialog;
import com.dalread.dialog.SingleChoiceDialog;
import com.dalread.dialog.TypeVocaBookNameDialog;
import com.dalread.interfaces.IVocaFullPlayTTSItem;
import com.dalread.listener.OnClickDialogListener;
import com.dalread.listener.OnPlaylistItemClickListener;
import com.dalread.model.AmkiItem;
import com.dalread.model.VocaBook;
import com.dalread.model.VocaInBook;
import com.dalread.network.DalApiListener;
import com.dalread.network.events.BaseEvent;
import com.dalread.network.events.SuccessEvent;
import com.dalread.network.models.AllBookListResponse;
import com.dalread.util.Constant;
import com.dalread.util.Loading;
import com.dalread.util.ToastUtil;
import com.dalread.util.Utils;
import com.dalread.util.Voca;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindColor;
import butterknife.BindDimen;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.OnClick;

public class PlaylistActivity extends BaseDalVocaPlayVocaActivity {

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

    @BindString(R.string.msg_no_word_selected)
    String msgNoWord;
    @BindString(R.string.ok)
    String msgOk;
    @BindString(R.string.play_index)
    String tplIndex;

    private static final int STATE_STOP = 0;
    private static final int STATE_PAUSE = STATE_STOP + 1;
    private static final int STATE_PLAY_SINGLE = STATE_PAUSE + 1;
    private static final int STATE_PLAY_MULTI = STATE_PLAY_SINGLE + 1;

    private Context context;
    private PopupMenu popupMenu;
    private CenterLayoutManager layoutManager;
    private PlayListByTtsAdapter adapter;
    private ArrayList<IVocaFullPlayTTSItem> backupFullItems;
    private ArrayList<IVocaFullPlayTTSItem> fullItems;
    private ArrayList<IVocaFullPlayTTSItem> playlistItems;
    private ArrayList<Boolean> fullStates;
    private boolean checkedChanged;
    private int checkedCount;
    private SelectRangeDialog selectRangeDialog;
    private ChoosePlaylistDialog choosePlaylistDialog;
    private int playingState;
    private AddFolderNameDialog addFolderNameDialog;
    private TextToSpeech combineTTS;
    private ArrayList<VocaBook> userBooks;
    private String[] userBookNames;
    private SingleChoiceDialog userBooksDialog;
    private TypeVocaBookNameDialog bookNameDialog;
    private AlertDialog alertDialog;
    private boolean dataChanged;
    private BackupVoicesTask backupVoicesTask;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_play_all;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        initToolbar();
        initRecyclerView();
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

        popupMenu = new PopupMenu(context, toolbar.getIconRight());
        popupMenu.getMenuInflater().inflate(R.menu.menu_voca_playlist, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(onMenuItemClickListener);
        if (Utils.isDebugOrAdminUser(context)) {
            // Add Combine MP3 function for debug mode only
            popupMenu.getMenu().add(R.string.combine);
            combineTTS = new TextToSpeech(context, new TextToSpeech.OnInitListener() {

                @Override
                public void onInit(int status) {
                    if (status == TextToSpeech.SUCCESS) {
                        combineTTS.setLanguage(EnumLanguage.findByFormatApi(sharedPreferences.getMotherTongueLanguage()).getLocale());
                    } else {
                        combineTTS = null;
                    }
                }
            });
        }
    }

    private void initRecyclerView() {
        fullItems = (ArrayList<IVocaFullPlayTTSItem>) getIntent().getSerializableExtra(Constant.BUNDLE.KEY_VOCA_PLAYLIST);
        if (fullItems == null) {
            fullItems = new ArrayList<>();
        }
        backupFullItems = new ArrayList<>(fullItems);
        playlistItems = new ArrayList<>();
        fullStates = new ArrayList<>();
        for (int i = 0; i < fullItems.size(); i++) {
            IVocaFullPlayTTSItem item = fullItems.get(i);
            item.setVIChecked(true);
            item.setVIIndex(i + 1);
            playlistItems.add(item);
            fullStates.add(true);
        }
        checkedCount = fullItems.size();
        adapter = new PlayListByTtsAdapter(context, sharedPreferences.getDisplayPronunciation());
        adapter.setData(fullItems);
//        adapter.setListener(listener);
        rvVoca.setAdapter(adapter);
        rvVoca.setLayoutManager(layoutManager = new CenterLayoutManager(context));
        rvVoca.addItemDecoration(BaseBindUtils.getSeparatorDecoration(context));
    }

    private void initDialog() {
        selectRangeDialog = new SelectRangeDialog(context, new SelectRangeDialog.OnRangeSelectListener() {

            @Override
            public void onSelect(int from, int to) {
                updateStateRangeText();
                updateStateRangeData(from, to);
            }

            @Override
            public void onDismiss(View v) {

            }
        });
        choosePlaylistDialog = new ChoosePlaylistDialog(context, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case R.id.tv_all:
                        if (checkedCount < fullItems.size()) {
                            updateStateAllText();
                            updateStateAllData();
                        }
                        break;
                    case R.id.tv_unknown:
                        updateStateUnknownData();
                        updateStateUnknownText();
                        break;
                    case R.id.tv_none:
                        if (checkedCount > 0) {
                            updateStateNoneText();
                            updateStateNoneData();
                        }
                        break;
                    case R.id.tv_range:
                        selectRangeDialog.show(1, fullItems.size());
                        break;
                    case R.id.tv_last:
                        if (checkedCount == 0 || checkedCount == fullItems.size()) {
                            updateStateLastText();
                            updateStateLastData();
                        }
                        break;
                    default:
                        break;
                }
            }
        });
        addFolderNameDialog = new AddFolderNameDialog(context, new BaseDialogListener() {

            @Override
            public void onBaseDialogListenerShow(EnumType type, BaseDialog currentDialog, View v, int position, Object data) {
            }

            @Override
            public void onBaseDialogListenerOk(EnumType type, BaseDialog dialog, View v, int position, Object data) {
                String name = (String) data;
                if (!name.isEmpty()) {
                    updatePlaylist();
                    addFolder(name);
                }
            }

            @Override
            public void onBaseDialogListenerCancel(EnumType type, BaseDialog dialog, View v, int position, Object data) {
            }

            @Override
            public void onBaseDialogListenerClick(EnumType type, BaseDialog dialog, View v, int position, Object data) {
            }

            @Override
            public void onBaseDialogListenerClickMulti(EnumType type, BaseDialog dialog, View v, int position, Object[] data) {
            }
        });
        userBooksDialog = new SingleChoiceDialog(context);
        bookNameDialog = new TypeVocaBookNameDialog(context, new BaseDialogListener() {

            @Override
            public void onBaseDialogListenerShow(EnumType type, BaseDialog currentDialog, View v, int position, Object data) {
            }

            @Override
            public void onBaseDialogListenerOk(EnumType type, BaseDialog dialog, View v, int position, Object data) {
                String name = (String) data;
                if (((String) data).isEmpty()) {
                    ToastUtil.getInstance(context).show(R.string.msg_type_wordbook_name);
                } else {
                    createUserVocaBook(name);
                }
            }

            @Override
            public void onBaseDialogListenerCancel(EnumType type, BaseDialog dialog, View v, int position, Object data) {
            }

            @Override
            public void onBaseDialogListenerClick(EnumType type, BaseDialog dialog, View v, int position, Object data) {
            }

            @Override
            public void onBaseDialogListenerClickMulti(EnumType type, BaseDialog dialog, View v, int position, Object[] data) {
            }
        });
        alertDialog = new AlertDialog(context);
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

    private void updateItemStatus(String utteranceId, boolean playing) {
        IVocaFullPlayTTSItem item = adapter.getItemByVocaId(utteranceId);
        if (item != null) {
            item.setVIPlaying(playing);
            rvVoca.post(() -> {
                int pos = adapter.notifyPlaylistItemChanged(item);
                if (playingState == STATE_PLAY_MULTI && playing) {
                    layoutManager.smoothScrollToPosition(rvVoca, null, pos);
                    String title = String.format(tplIndex, pos + 1, playlistItems.size());
                    toolbar.setTitle(title);
                }
            });
        }
    }

    private PopupMenu.OnMenuItemClickListener onMenuItemClickListener = new PopupMenu.OnMenuItemClickListener() {

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            int id = item.getItemId();
            switch (id) {
                case R.id.make_favorite:
                    bookNameDialog.show();
                    break;
                case R.id.add_to_favorite:
                    if (userBookNames == null) {
                        getUserBooks(null);
                    } else {
                        showUserBooks();
                    }
                    break;
                case R.id.add_workbook:
                    openSelectWordbookScreen();
                    break;
                case R.id.copy:
                    openCopySelectedTextScreen();
                    break;
                case R.id.backup:
                    updatePlaylist();
                    if (backupVoicesTask == null) {
                        backupVoicesTask = new BackupVoicesTask(context, getUserID(), EnumLanguage.findByFormatApi(sharedPreferences.getStudyLanguage()).getIdApi());
                    }
                    backupVoicesTask.backup(playlistItems);
                    break;
                case R.id.settings:
                    DalFlavor.openSettings(PlaylistActivity.this);
                    break;
                case R.id.quiz:
                    updatePlaylist();
                    makeAQuiz();
                    break;
                default:
                    if (Utils.isDebugOrAdminUser(context)) {
                        showAddFolderNameDialog();
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
        preparePlayVoca(item);
        playingState = STATE_PLAY_SINGLE;
        updateIcon();
    }

    private void playPlaylist() {
        preparePlayVoca(playlistItems);
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
        clearDownloadList();
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

    private void updateStateUnknownText() {
//        tvStatus.setText(R.string.select_unknown_words);
        icPlay.setColorFilter(checkedCount > 0 ? clEnable : clDisable);
    }

    private void updateStateUnknownData() {
        playlistItems.clear();
        fullStates.clear();
        for (IVocaFullPlayTTSItem item : fullItems) {
            if (item instanceof AmkiItem) {
                AmkiItem amkiItem = (AmkiItem) item;
                if (amkiItem.getAmkiKnow() == Constant.VOCA_KNOW.VOCA_KNOW_UNKNOWN) {
                    item.setVIChecked(true);
                    playlistItems.add(item);
                    fullStates.add(true);
                } else {
                    item.setVIChecked(false);
                    fullStates.add(false);
                }
            }
        }
        checkedCount = playlistItems.size();
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

    private void updateStateRangeText() {
        tvStatus.setText(R.string.select_range);
        icPlay.setColorFilter(clEnable);
    }

    private void updateStateRangeData(int from, int to) {
        if (from < 1) {
            from = 1;
        }
        if (to < 1) {
            to = 1;
        }
        int size = fullItems.size();
        if (from > size) {
            from = size;
        }
        if (to > size) {
            to = size;
        }
        if (to < from) {
            int temp = from;
            from = to;
            to = temp;
        }
        playlistItems.clear();
        fullStates.clear();
        checkedCount = 0;
        from--;
        to--;
        for (int i = 0; i < size; i++) {
            IVocaFullPlayTTSItem item = fullItems.get(i);
            if (from <= i && i <= to) {
                item.setVIChecked(true);
                playlistItems.add(item);
                fullStates.add(true);
                checkedCount++;
            } else {
                item.setVIChecked(false);
                fullStates.add(false);
            }
        }
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

    private void openCopySelectedTextScreen() {
        updatePlaylist();
        if (!playlistItems.isEmpty()) {
            Intent intent = new Intent(context, CopySelectedTextActivity.class);
            intent.putExtra(Constant.BUNDLE.KEY_VOCA_PLAYLIST, playlistItems);
            openNewScreen(intent);
        }
    }

    private void showAddFolderNameDialog() {
//        String newFolderName = bookName + "_" + Utils.getCurrentDateTimeWithUnderline();
        String newFolderName = Utils.getCurrentDateTimeWithUnderline();
        addFolderNameDialog.show(newFolderName);
    }

    private void addFolder(String folderName) {
        ToastUtil.getInstance(context).show("Please apply merge voices patch!");
    }

    private void getUserBooks(final String newBookName) {
        int uid = getUserID();
        if (uid > 0) {
            if (Utils.isConnected(context)) {
                Loading.show(context);
                application.getDalAiImpl().getAllVocaBookList(
                        String.valueOf(uid),
                        sharedPreferences.getLangStudyCode(),
                        sharedPreferences.getMotherTongueLangCode(),
                        new DalApiListener<AllBookListResponse>() {

                            @Override
                            public void onSuccess(AllBookListResponse response) {
                                userBooks = response.getUserBooks();
                                int count = userBooks.size();
                                userBookNames = new String[count];
                                VocaBook newBook = null;
                                for (int i = 0; i < count; i++) {
                                    VocaBook userBook = userBooks.get(i);
                                    String userBookName = userBook.getName();
                                    userBookNames[i] = userBookName;
                                    if (newBookName != null && newBookName.equals(userBookName)) {
                                        newBook = userBook;
                                    }
                                }
                                if (newBook == null) {
                                    Loading.hide();
                                    showUserBooks();
                                } else {
                                    addVocasToUserBook(newBook);
                                }
                            }

                            @Override
                            public void onFailure(String error) {
                                Loading.hide();
                            }
                        }
                );
            } else {
                Loading.hide();
                alertDialog.showNoInternet();
            }
        } else {
            Loading.hide();
            alertDialog.showLogInRequired();
        }
    }

    private void showUserBooks() {
        if (userBookNames.length == 0) {
            ToastUtil.getInstance(context).show( R.string.msg_no_user_book);
        } else {
            userBooksDialog.show(
                    R.string.add_to_book,
                    userBookNames,
                    -1,
                    R.string.ok,
                    R.string.cancel,
                    new OnClickDialogListener() {
                        @Override
                        public void onClick(View view, Object object) {
                            final int which = (int) object;
                            addVocasToUserBook(userBooks.get(which));
                        }

                        @Override
                        public void onDismiss(View view, Object object) {

                        }
                    }
            );
        }
    }

    private void addVocasToUserBook(VocaBook userBook) {
        int uid = getUserID();
        if (uid > 0) {
            if (Utils.isConnected(context)) {
                StringBuilder vocaIds = new StringBuilder();
                StringBuilder typeIds = new StringBuilder();
                StringBuilder useServerVocabookDatas = new StringBuilder();
                StringBuilder idInServerVocabooks = new StringBuilder();
                if (checkedChanged) {
                    checkedChanged = false;
                    playlistItems.clear();
                    for (IVocaFullPlayTTSItem item : fullItems) {
                        if (item.isVIChecked()) {
                            playlistItems.add(item);
                            vocaIds.append(",").append(item.getVIVocaId());
                            typeIds.append(",").append(item.getVIVocaType());
                            useServerVocabookDatas.append(",").append((item instanceof VocaInBook) ? ((VocaInBook) item).getUseServerVocabookData() : 0);
                            idInServerVocabooks.append(",").append((item instanceof VocaInBook) ? ((VocaInBook) item).getIdInServerVocabook() : 0);
                        }
                    }
                } else {
                    for (IVocaFullPlayTTSItem item : playlistItems) {
                        if (item.isVIChecked()) {
                            vocaIds.append(",").append(item.getVIVocaId());
                            typeIds.append(",").append(item.getVIVocaType());
                            useServerVocabookDatas.append(",").append((item instanceof VocaInBook) ? ((VocaInBook) item).getUseServerVocabookData() : 0);
                            idInServerVocabooks.append(",").append((item instanceof VocaInBook) ? ((VocaInBook) item).getIdInServerVocabook() : 0);
                        }
                    }
                }
                if (vocaIds.length() == 0) {
                    Loading.hide();
                    alertDialog.show(msgNoWord, msgOk, null);
                } else {
                    Loading.show(context);
                    application.getDalAiImpl().addVocasInUserVocaBook(
                            String.valueOf(uid),
                            sharedPreferences.getLangStudyCode(),
                            String.valueOf(userBook.getId()),
                            vocaIds.substring(1),
                            typeIds.substring(1),
                            useServerVocabookDatas.substring(1),
                            idInServerVocabooks.substring(1),
                            new DalApiListener<Boolean>() {

                                @Override
                                public void onSuccess(Boolean response) {
                                    Loading.hide();
                                    if (response) {
                                        dataChanged = true;
                                        ToastUtil.getInstance(context).show( R.string.add_success);
                                    } else {
                                        ToastUtil.getInstance(context).show( R.string.add_fail);
                                    }
                                }

                                @Override
                                public void onFailure(String error) {
                                    Loading.hide();
                                    ToastUtil.getInstance(context).show( R.string.add_fail);
                                }
                            });
                }
            } else {
                Loading.hide();
                alertDialog.showNoInternet();
            }
        } else {
            Loading.hide();
            alertDialog.showLogInRequired();
        }
    }

    private void createUserVocaBook(final String name) {
        int uid = getUserID();
        if (uid > 0) {
            if (Utils.isConnected(context)) {
                Loading.show(context);
                application.getDalAiImpl().addVocaBookInUserVocaBookList(
                        String.valueOf(uid),
                        sharedPreferences.getLangStudyCode(),
                        sharedPreferences.getMotherTongueLangCode(),
                        name,
                        new DalApiListener<Boolean>() {

                            @Override
                            public void onSuccess(Boolean response) {
                                if (response) {
                                    dataChanged = true;
                                    bookNameDialog.dismiss();
                                    bookNameDialog.clearText();
                                    ToastUtil.getInstance(context).show( R.string.create_success);
                                    getUserBooks(name);
                                } else {
                                    Loading.hide();
                                    ToastUtil.getInstance(context).show( R.string.msg_book_name_existed);
                                }
                            }

                            @Override
                            public void onFailure(String error) {
                                Loading.hide();
                                ToastUtil.getInstance(context).show( R.string.create_fail);
                            }
                        }
                );
            } else {
                alertDialog.showNoInternet();
            }
        } else {
            alertDialog.showLogInRequired();
        }
    }

    private void makeAQuiz() {
        Intent i = new Intent(context, ChoiceQuizActivity.class);
        i.putExtra(Constant.BUNDLE.KEY_VOCA_PLAYLIST, playlistItems);
        openNewScreen(i);
    }

    private void openSelectWordbookScreen() {
        Intent intent = new Intent(context, WordbookByCategoryActivity.class);
        intent.putExtra(Constant.BUNDLE.KEY_SELECT_MULTI, true);
        openNewScreenForResult(intent, Constant.REQUEST_CODE.SELECT_BOOK);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constant.REQUEST_CODE.SELECT_BOOK
                && resultCode == Activity.RESULT_OK
                && data != null) {
            List<VocaBook> selectedBooks = (List<VocaBook>) data.getSerializableExtra(Constant.BUNDLE.KEY_VOCA_BOOKS);
            if (selectedBooks != null) {
                Loading.show(context);
                getVocasFromAllVocaBook(selectedBooks, new ArrayList<>());
            }
        }
    }

    private void getVocasFromAllVocaBook(List<VocaBook> selectedBooks, List<VocaInBook> vocas) {
        if (selectedBooks.isEmpty()) {
            Loading.hide();
            if (!vocas.isEmpty()) {
                checkedChanged = true;
                adapter.addData(new ArrayList<>(vocas));
            }
        } else {
            application.getDalAiImpl().getVocasFromAllVocaBook(
                    String.valueOf(getUserID()),
                    sharedPreferences.getLangStudyCode(),
                    sharedPreferences.getMotherTongueLangCode(),
                    String.valueOf(selectedBooks.remove(0).getId()),
                    Constant.API_VALUE.VALUE_SERVER_BOOK,
                    0,
                    Constant.LOADING_MAX_ITEM,
                    0,
                    new DalApiListener<List<VocaInBook>>() {

                        @Override
                        public void onSuccess(List<VocaInBook> response) {
                            int studyLang = EnumLanguage.findByFormatApi(sharedPreferences.getStudyLanguage()).getIdApi();
                            int index = fullItems.size();
                            for (VocaInBook voca : response) {
                                voca.setPath(Voca.getOutputRecordingFileName(
                                        studyLang,
                                        voca.getVocaType(),
                                        voca.getVocaId(),
                                        getUserID()
                                ));
                                voca.setIndex(++index);
                                voca.setVIChecked(true);
                                vocas.add(voca);
                                backupFullItems.add(voca);
                                fullItems.add(voca);
                                fullStates.add(true);
                            }
                            getVocasFromAllVocaBook(selectedBooks, vocas);
                        }

                        @Override
                        public void onFailure(String error) {
                            getVocasFromAllVocaBook(selectedBooks, vocas);
                        }
                    }
            );
        }
    }

    @Override
    public void onOpen() {
        icStop.performClick();
    }

    @Override
    public void onBackPressed() {
        if (dataChanged) {
            application.getEventBus().post(new SuccessEvent(BaseEvent.Screen.MAIN, BaseEvent.EventType.DATA_CHANGED, true));
        }
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        if (combineTTS != null) {
            combineTTS.stop();
            combineTTS.shutdown();
            combineTTS = null;
        }

        super.onDestroy();
    }
}
