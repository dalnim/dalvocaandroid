package com.dalread.base;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;

import com.dalread.DalFlavor;
import com.dalread.R;
import com.dalread.activity.CopySelectedTextActivity;
import com.dalread.activity.QuizPlayerActivity;
import com.dalread.adapter.PlayListByTtsAdapter;
import com.dalread.asyntask.BackupVoicesTask;
import com.dalread.component.CenterLayoutManager;
import com.dalread.composition.AbstractBaseVocaKnowActivity;
import com.dalread.composition.PlayTTS;
import com.dalread.composition.VocaKnowActivity;
import com.dalread.database.sqlite.SubDatabase;
import com.dalread.databinding.ActivityPlayAllBinding;
import com.dalread.dialog.AddFolderNameDialog;
import com.dalread.dialog.AlertDialog;
import com.dalread.dialog.ChoosePlaylistCheckItemDialog;
import com.dalread.dialog.ChoosePlaylistRangeDialog;
import com.dalread.dialog.SelectRange2Dialog;
import com.dalread.dialog.SingleChoiceDialog;
import com.dalread.dialog.TypeVocaBookNameDialog;
import com.dalread.interfaces.IVocaCoreItem;
import com.dalread.interfaces.IVocaFullPlayTTSItem;
import com.dalread.listener.OnClickDialogListener;
import com.dalread.listener.OnDoubleClickListener;
import com.dalread.listener.OnShufflePlayListListener;
import com.dalread.model.VocaBook;
import com.dalread.model.VocaInBook;
import com.dalread.model.WordListType;
import com.dalread.network.DalApiListener;
import com.dalread.network.events.BaseEvent;
import com.dalread.network.events.SuccessEvent;
import com.dalread.network.models.AllBookListResponse;
import com.dalread.util.BaseBindUtils;
import com.dalread.util.BaseVoca;
import com.dalread.util.BaseVocaKnow;
import com.dalread.util.Constant;
import com.dalread.util.DialogUtil;
import com.dalread.util.Loading;
import com.dalread.util.RepeatUtil;
import com.dalread.util.ToastUtil;
import com.dalread.util.Utils;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import butterknife.OnClick;
//BasePlayListByTtsActivity와 BaseVocaFilterActivity는 공통되는게 많다. BasePlayListByTtsActivity를 Abastrct로 하지 말고, 둘다의 공통 무모 클래스를 하나 만들것.
public abstract class BasePlayListByTtsActivity extends BaseActivity {// implements OnAsyncTaskListenerWithType {
    private boolean isRandomPlayInPlayListByTts;
    protected int bookId;
    protected static SubDatabase subDatabase;
    protected static String subDatabasePath;
    protected WordListType wordListType;
    protected boolean isConversationBook;

    protected abstract ArrayList<IVocaFullPlayTTSItem> getFullItems(List<IVocaCoreItem> vocaCoreItemList);

    private enum SELECT_RANGE_TYPE {
        ALL, AMKI_FIRST, AMKI_SECOND, ONLY_UNKNOWN, UNKNOWN_AND_LESS, BOOKMARK, RANGE
    }
    protected PlayTTS playTTS;
    private PopupMenu popupMenu;
    private CenterLayoutManager layoutManager;
    protected PlayListByTtsAdapter adapter;
    private ArrayList<IVocaFullPlayTTSItem> orignalFullItems;
    private ArrayList<IVocaFullPlayTTSItem> displayItems;
    private ArrayList<IVocaFullPlayTTSItem> playlistItems;
    private ArrayList<Boolean> fullStates;
    private boolean checkedChanged;
    private int checkedCount;
    private SelectRange2Dialog selectRangeDialog;
//    private ChoosePlaylistDialog choosePlaylistDialog;
    private ChoosePlaylistCheckItemDialog choosePlaylistCheckItemDialog;
    private ChoosePlaylistRangeDialog choosePlaylistRangeDialog;
    private AddFolderNameDialog addFolderNameDialog;
    private TextToSpeech combineTTS;
    private ArrayList<VocaBook> userBooks;
    private String[] userBookNames;
    private SingleChoiceDialog userBooksDialog;
    private TypeVocaBookNameDialog bookNameDialog;
    private AlertDialog alertDialog;
    private SingleChoiceDialog singleChoiceDialog;
    private boolean dataChanged;
    private BackupVoicesTask backupVoicesTask;
    protected AbstractBaseVocaKnowActivity vocaKnowActivity;
    private ActivityPlayAllBinding binding;

    @Override
    protected View getContentView() {
        binding = ActivityPlayAllBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        return view;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        playTTS = new PlayTTS(this);
        initBundle();
        initLocalDatabase();
        initVocaKnowActivity();
        initRecyclerView(); //Dalnim : switched line order with initToolbar(). (fullItems was null if initToolbar() run first)
        initDialog();
        initToolbar();
        updateBottomPlayStatusIcon();
        initPlayVocaHelper();
        askIgnoreBatteryOptimization();
    }
    private void initBundle() {
        bookId = getIntent().getIntExtra(Constant.BUNDLE.KEY_VOCA_BOOK_ID, -1);
        wordListType = (WordListType) getIntent().getSerializableExtra(Constant.BUNDLE.KEY_WORDLIST_TYPE);
        subDatabasePath = getIntent().getStringExtra(Constant.BUNDLE.KEY_SUB_DATABASE_PATH);
    }
    protected void upateVisibilityCheckItem(boolean toVisible) {
        if (isConversationBook) {
            binding.layoutPlayAll.tvSelectRange.setVisibility(View.GONE);
            binding.layoutPlayAll.tvCheckItem.setVisibility(View.GONE);
        } else {
            if (toVisible) {
                binding.layoutPlayAll.tvSelectRange.setVisibility(View.VISIBLE);
                binding.layoutPlayAll.tvCheckItem.setVisibility(View.VISIBLE);
            } else {
                binding.layoutPlayAll.tvSelectRange.setVisibility(View.VISIBLE);
                binding.layoutPlayAll.tvCheckItem.setVisibility(View.INVISIBLE);
            }
        }
    }
    private void askIgnoreBatteryOptimization() {
        if (!sharedPreferences.isAskedIgnoreBatteryOptimization()) {
            sharedPreferences.setAskedToIgnoreBatteryOptimization(true);
            Utils.askIgnoreBatteryOptimization(this, null);
        }
    }

    protected void initLocalDatabase() {
        if (subDatabase != null) {
            subDatabase.close();
        }
        subDatabase = null;
//        String destPathWithFileName = BaseStorageUtil.getAraConvDBPathWithFileName(context);
        subDatabase = SubDatabase.getInstance(this, subDatabasePath);
    }
    protected void initVocaKnowActivity() {
        vocaKnowActivity = new VocaKnowActivity(this, subDatabase);
    }
//    public void setVocaKnowActivity(BaseVocaKnowActivity vocaKnowActivity) {
//        this.vocaKnowActivity = vocaKnowActivity;
//    }

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
        stopList();
        popupMenu.show();
    }

    @Override
    public void onHeaderTextRightClick() {

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    // ear phone
    @Subscribe
    public void onEvent(SuccessEvent event) {
        switch (event.getScreen()) {
            case COMMON_VOCA_DATA:
                IVocaFullPlayTTSItem item= (IVocaFullPlayTTSItem) event.getModel();
                switch (event.getEventType()) {
                    case COMMON_VOCA_KNOW_CHANGED:
                    case COMMON_VOCA_KNOWPRONOUNCE_CHANGED:
                    case COMMON_VOCA_BOOKMKARK_CHANGED:
                        refreshSearchViewAdapterVocaKnow(item);
                        break;
                }
                break;
            case MEDIA_BUTTON:
                switch (event.getEventType()) {
                    case MEDIA_BUTTON_PAUSE:
                    case MEDIA_BUTTON_PLAY_BRROADCAST:
                        playList();
                        break;
                    case MEDIA_BUTTON_PLAY:
                    case MEDIA_BUTTON_PAUSE_BRROADCAST:
                        pauseList();
                        break;
                    case MEDIA_BUTTON_HEADSETHOOK:
                    case MEDIA_MEDIA_PLAY_OR_PAUSE:
                        if (playTTS.isPlayStateStop()) {
                            playList();
                        } else if (playTTS.isPlayStatePause()) {
                            playList();// TODO : Dalnim need to use resumePlaylist(????);
                        } else if (playTTS.isPlayStatePlaySingle()) {
                            pausePlaylist();
                        } else if (playTTS.isPlayStatePlayMulti()) {
                            pausePlaylist();
                        }
                        break;
                }
        }
    }

    protected void initToolbar() {
        binding.header.setSearchListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                clearItems();
                if (s.isEmpty()) {
                    displayItems.addAll(orignalFullItems);
                } else {
                    for (IVocaFullPlayTTSItem item : orignalFullItems) {
                        if (item.getVIVoca().contains(s)) {
                            displayItems.add(item);
                            playlistItems.add(item);
                            fullStates.add(true);
                        } else {
                            item.setVIChecked(false);
                            fullStates.add(false);
                        }
                    }
                }
                binding.rvVoca.post(new Runnable() {

                    @Override
                    public void run() {
                        bindData();
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
        binding.header.showSearchView();

        initPopupMenu();
    }

    private void initPopupMenu() {
        popupMenu = new PopupMenu(this, binding.header.getIconRight());
        popupMenu.getMenuInflater().inflate(R.menu.menu_voca_playlist, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(onMenuItemClickListener);
        hideUnusedPopupMenu();
        if (Utils.isDebugOrAdminUser(context)) {
            // Add Combine MP3 function for debug mode only
            popupMenu.getMenu().add(R.string.combine);
            combineTTS = new TextToSpeech(this, new TextToSpeech.OnInitListener() {

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

    private void hideUnusedPopupMenu() {
        popupMenu.getMenu().findItem(R.id.make_favorite).setVisible(false);
        popupMenu.getMenu().findItem(R.id.add_to_favorite).setVisible(false);
        popupMenu.getMenu().findItem(R.id.add_workbook).setVisible(false);
        popupMenu.getMenu().findItem(R.id.copy).setVisible(false);
        popupMenu.getMenu().findItem(R.id.backup).setVisible(false);
        popupMenu.getMenu().findItem(R.id.quiz).setVisible(false);


//        popupMenu.getMenu().findItem(R.id.settings).setVisible(true);
    }

    private void initRecyclerView() {
        List<IVocaCoreItem> vocaCoreItemList = (List<IVocaCoreItem>) getIntent().getSerializableExtra(Constant.BUNDLE.KEY_VOCA_PLAYLIST);
        displayItems = getFullItems(vocaCoreItemList);
        if (displayItems == null) {
            displayItems = new ArrayList<>();
        }
        orignalFullItems = new ArrayList<>(displayItems);
        playlistItems = new ArrayList<>();
        fullStates = new ArrayList<>();
        for (int i = 0; i < orignalFullItems.size(); i++) {
            IVocaFullPlayTTSItem item = orignalFullItems.get(i);
            item.setVIChecked(true);
            item.setVIIndex(i + 1);
            playlistItems.add(item);
            fullStates.add(true);
        }
        checkedCount = displayItems.size();
        adapter = new PlayListByTtsAdapter(this, sharedPreferences.getDisplayPronunciation(), vocaKnowActivity.onDoubleClickListenerOnBaseVocaKnow, onDoubleClickListener);
        adapter.setData(displayItems);
        adapter.setConversationBook(isConversationBook);
        binding.rvVoca.setAdapter(adapter);
        binding.rvVoca.setLayoutManager(layoutManager = new CenterLayoutManager(this));
    }

//    @Override
    protected void initDialog() {
//        super.initDialog();
        selectRangeDialog = new SelectRange2Dialog(this, new SelectRange2Dialog.OnRangeSelectListener() {
            @Override
            public void onSelect(int from, int to) {
                updateStateRangeText();
                updateStateRangeData(from, to);
            }

            @Override
            public void onDismiss(View v) {

            }
        });
        choosePlaylistCheckItemDialog = new ChoosePlaylistCheckItemDialog(this, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                switch (which) {
                    case R.id.tvCheckAll:
                        updateStateAllText();
                        updateSelectUnselectAllItems(true);
                        break;
                    case R.id.tvCheckNone:
                        updateStateNoneText();
                        updateSelectUnselectAllItems(false);
                        break;

                    case R.id.tvCheckLastChosen:
                        if (checkedCount == 0 || checkedCount == displayItems.size()) {
                            updateStateLastText();
                            updateStateLastData();
                        }
                        break;
                }
            }
        });
        choosePlaylistRangeDialog = new ChoosePlaylistRangeDialog(this, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case R.id.tvSelectAll:
                        selectVocaListByType(SELECT_RANGE_TYPE.ALL);
                        bindData();
                        break;
                    case R.id.tvSelectFirstWords:
                        selectVocaListByType(SELECT_RANGE_TYPE.AMKI_FIRST);
                        bindData();
                        break;
                    case R.id.tvSelectSecondWords:
                        selectVocaListByType(SELECT_RANGE_TYPE.AMKI_SECOND);
                        bindData();
                        break;
                    case R.id.tvSelectOnlyUnknownWords:
                        selectVocaListByType(SELECT_RANGE_TYPE.ONLY_UNKNOWN);
                        bindData();
                        break;
                    case R.id.tvSelectUnknownAndLess:
                        selectVocaListByType(SELECT_RANGE_TYPE.UNKNOWN_AND_LESS);
                        bindData();
                        break;
                    case R.id.tvSelectBookmarked:
                        selectVocaListByType(SELECT_RANGE_TYPE.BOOKMARK);
                        bindData();
                        break;
                    case R.id.tvSelectByRandom:
                        selectVocaListByRandom();
                        bindData();
                        break;
                    case R.id.tvSelectByRange:
                        selectRangeDialog.show(1, orignalFullItems.size());
                        break;
                }
            }
        });
        addFolderNameDialog = new AddFolderNameDialog(this, new BaseDialogListener() {

            @Override
            public void onBaseDialogListenerShow(EnumType type, BaseDialog dialog, View v, int position, Object data) {
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
        userBooksDialog = new SingleChoiceDialog(this);
        bookNameDialog = new TypeVocaBookNameDialog(this, new BaseDialogListener() {

            @Override
            public void onBaseDialogListenerShow(EnumType type, BaseDialog dialog, View v, int position, Object data) {
            }

            @Override
            public void onBaseDialogListenerOk(EnumType type, BaseDialog dialog, View v, int position, Object data) {
                String name = (String) data;
                if (((String) data).isEmpty()) {
                    ToastUtil.getInstance(BasePlayListByTtsActivity.this).show(R.string.msg_type_wordbook_name);
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
        alertDialog = new AlertDialog(this);
        singleChoiceDialog = new SingleChoiceDialog(this);
    }

    private void initPlayVocaHelper() {
        if (!playTTS.playTTSHelper.hasMotherTongueListener()) {
            playTTS.playTTSHelper.setMotherTongueListener(new UtteranceProgressListener() {

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
        if (!playTTS.playTTSHelper.hasStudyListener()) {
            playTTS.playTTSHelper.setStudyListener(new UtteranceProgressListener() {

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
            binding.rvVoca.post(() -> {
                int pos = adapter.notifyPlaylistItemChanged(item);
                if (playTTS.isPlayStatePlayMulti() && playing) {
                    layoutManager.smoothScrollToPosition(binding.rvVoca, null, pos);
                    String title = getString(R.string.play_index, pos + 1, playlistItems.size());
                    binding.header.setTitle(title);
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
                        backupVoicesTask = new BackupVoicesTask(BasePlayListByTtsActivity.this, getUserID(), EnumLanguage.findByFormatApi(sharedPreferences.getStudyLanguage()).getIdApi());
                    }
                    backupVoicesTask.backup(playlistItems);
                    break;
                case R.id.settings:
                    DalFlavor.openSettings(BasePlayListByTtsActivity.this);
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

    @OnClick({R.id.ic_play, R.id.ic_pause, R.id.ic_stop, R.id.tvSelectRange, R.id.tvCheckItem})
    void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.ic_play:
//                playList();
                playVoca();
                break;
            case R.id.ic_pause:
                pauseList();
                break;
            case R.id.ic_stop:
                stopList();
                adapter.setListenComprehension2(false);
                bindData();
                break;
            case R.id.tvSelectRange:
                if (playTTS.isPlayStateStop()) {
                    choosePlaylistRangeDialog.show();
                }
                break;
            case R.id.tvCheckItem:
                if (playTTS.isPlayStateStop()) {
                    choosePlaylistCheckItemDialog.show();
                }
                break;
        }
    }

    private void playVoca() {
        if (isConversationBook) {
            displayItems.forEach(e -> e.setVIRepeatCount(1));
            playList();
        } else {
            chooseRepeatCountBeforePlaying();
        }
    }
    private void chooseRepeatCountBeforePlaying() {
        final String[] readCountValues = BaseVoca.getRepeatCountValues(this);
        DialogUtil.showRepeatCountDialog(this, R.string.desc_listen_comprehension_2_voca_list, new OnClickDialogListener() {
            @Override
            public void onClick(View view, Object object) {
                adapter.setListenComprehension2(true);

                int which = (int) object;
                int repeatCount = RepeatUtil.getRepeatCount(BasePlayListByTtsActivity.this, readCountValues[which]);
                displayItems.forEach(e -> e.setVIRepeatCount(repeatCount));
                playList();
            }

            @Override
            public void onDismiss(View view, Object object) {

            }
        });
    }

    private void playList() {
        if (checkedCount > 0) {
            initPlayVocaHelper();// TODO : I call this just in case the listner is null. Need to update not the listner to null
            upateVisibilityCheckItem(false);
//            binding.layoutPlayAll.tvSelectRange.setVisibility(View.INVISIBLE);
//            binding.layoutPlayAll.tvCheckItem.setVisibility(View.INVISIBLE);
            if (playTTS.isPlayStateStop()) {
                if (checkedCount > 0) {
                    updatePlaylist();
                    if (isRandomPlayInPlayListByTts)
                        Collections.shuffle(playlistItems);
                    displayPlaylistItems();
                    playTTS.playTTSHelper.setConversationBook(isConversationBook ? true : false);
                    playPlaylist();
                }
            } else if (playTTS.isPlayStatePause()) {
                resumePlaylist(-1);
            }
        }
    }
    private void pauseList() {
        if (checkedCount > 0) {
            if (playTTS.isPlayStatePlayMulti()) {
                pausePlaylist();
            }
        }
    }
    protected void stopList() {
        if (checkedCount > 0) {
            upateVisibilityCheckItem(true);
//            binding.layoutPlayAll.tvSelectRange.setVisibility(View.VISIBLE);
//            binding.layoutPlayAll.tvCheckItem.setVisibility(View.VISIBLE);
            if (playTTS.isPlayStatePlaySingle()) {
                stopPlaylist();
            } else if (playTTS.isPlayStatePause() || playTTS.isPlayStatePlayMulti()) {
                stopPlaylist();
                //Don't need to go first item when stops playing.
//                displayFullItems();
            }
        }
    }

    private void updatePlaylist() {
        if (checkedChanged) {
            checkedChanged = false;
            playlistItems.clear();
            for (IVocaFullPlayTTSItem item : displayItems) {
                if (item.isVIChecked()) {
                    playlistItems.add(item);
                }
            }
        }
    }

    private void updateBottomPlayStatusIcon() {
        if (playTTS.isPlayStateStop()) {
            binding.layoutPlayAll.icPlay.setColorFilter(BaseBindUtils.getEnableColor());
            binding.layoutPlayAll.icPause.setColorFilter(BaseBindUtils.getDisableColor());
            binding.layoutPlayAll.icStop.setColorFilter(BaseBindUtils.getDisableColor());
        } else if (playTTS.isPlayStatePause()) {
            binding.layoutPlayAll.icPlay.setColorFilter(BaseBindUtils.getEnableColor());
            binding.layoutPlayAll.icPause.setColorFilter(BaseBindUtils.getDisableColor());
            binding.layoutPlayAll.icStop.setColorFilter(BaseBindUtils.getEnableColor());
        } else if (playTTS.isPlayStatePlaySingle()) {
            binding.layoutPlayAll.icPlay.setColorFilter(BaseBindUtils.getDisableColor());
            binding.layoutPlayAll.icPause.setColorFilter(BaseBindUtils.getDisableColor());
            binding.layoutPlayAll.icStop.setColorFilter(BaseBindUtils.getEnableColor());
        } else if (playTTS.isPlayStatePlayMulti()) {
            binding.layoutPlayAll.icPlay.setColorFilter(BaseBindUtils.getDisableColor());
            binding.layoutPlayAll.icPause.setColorFilter(BaseBindUtils.getEnableColor());
            binding.layoutPlayAll.icStop.setColorFilter(BaseBindUtils.getEnableColor());
        }
    }

    private void displayPlaylistItems() {
        adapter.setData(playlistItems);
        adapter.notifyDataSetChanged();
    }

    private void displayFullItems() {
        bindData();
        binding.rvVoca.post(new Runnable() {
            @Override
            public void run() {
                layoutManager.smoothScrollToPosition(binding.rvVoca, null, 0);
            }
        });
    }

    private void playSingle(IVocaFullPlayTTSItem item) {
        playTTS.playSingle(item);
        updateBottomPlayStatusIcon();
    }

    private void playPlaylist() {
        playTTS.playPlaylist(playlistItems);
        updateBottomPlayStatusIcon();
    }

    private void pausePlaylist() {
        playTTS.pausePlaylist();
        updateBottomPlayStatusIcon();
    }

    private void resumePlaylist(int pos) {
        playTTS.resumePlaylist(pos);
        updateBottomPlayStatusIcon();
    }

    private void stopPlaylist() {
        playTTS.stopPlaylist();
        binding.header.setTitle(R.string.play_all_words);
        updateBottomPlayStatusIcon();
    }

    protected OnDoubleClickListener onDoubleClickListener = new OnDoubleClickListener() {
        @Override
        public void onClick(View view, Object data) {
            if (view.getId() == R.id.scRandomPlayInPlayListByTts) {
                isRandomPlayInPlayListByTts = (boolean) data;
                checkedChanged = true;
                if (isRandomPlayInPlayListByTts) {
                    playTTS.playTTSHelper.setOnShufflePlayListListener(onShufflePlayListListener);
                } else {
                    playTTS.playTTSHelper.setOnShufflePlayListListener(null);
                }
            } else {
                IVocaFullPlayTTSItem voca = (IVocaFullPlayTTSItem) data;
                switch (view.getId()) {
                    case R.id.ivSpeaker:
                        initPlayVocaHelper();// TODO : I call this just in case the listner is null. Need to update not the listner to null

                        if (playTTS.isPlayStateStop()) {
                            playSingle(voca);
                        } else if (playTTS.isPlayStatePause()) {
                            resumePlaylist(voca.isVIPlaying() ? -1 : playlistItems.indexOf(voca));
                        } else if (playTTS.isPlayStatePlaySingle()) {
                            boolean isPlaying = voca.isVIPlaying();
                            stopPlaylist();
                            if (!isPlaying) {
                                playSingle(voca);
                            }
                        }
                        break;
                    case R.id.llMain:
                        if (playTTS.isPlayStateStop()) {
                            voca.setVIChecked(!voca.isVIChecked());
                            adapter.notifyPlaylistItemChanged(voca);

                            checkedChanged = true;
                            boolean checked = voca.isVIChecked();
                            if (checked) {
                                checkedCount++;
                            } else {
                                checkedCount--;
                            }

                            if (checkedCount == displayItems.size()) {
                                updateStateAllText();
                            } else if (checkedCount == 0) {
                                updateStateNoneText();
                            } else {
                                updateStateLastText();
                                if (fullStates.contains(displayItems.indexOf(voca))) {
                                    fullStates.set(displayItems.indexOf(voca), checked);
                                }
                            }
                        }
                        break;
                }
            }
        }

        @Override
        public void onDoubleClick(View view, Object data) {

        }
    };


//    private OnPlaylistItemClickListener listener = new OnPlaylistItemClickListener() {
//
//        @Override
//        public void onItemClick(Object item) {
//            IVocaFullPlayTTSItem voca = (IVocaFullPlayTTSItem) item;
//            if (playTTS.isPlayStateStop()) {
//                voca.setVIChecked(!voca.isVIChecked());
//                adapter.notifyPlaylistItemChanged(voca);
//
//                checkedChanged = true;
//                boolean checked = voca.isVIChecked();
//                if (checked) {
//                    checkedCount++;
//                } else {
//                    checkedCount--;
//                }
//
//                if (checkedCount == displayItems.size()) {
//                    updateStateAllText();
//                } else if (checkedCount == 0) {
//                    updateStateNoneText();
//                } else {
//                    updateStateLastText();
//                    if (fullStates.contains(displayItems.indexOf(voca))) {
//                        fullStates.set(displayItems.indexOf(voca), checked);
//                    }
//                }
//            }
//        }
//
//        @Override
//        public void onPlayClick(Object item) {
//            initPlayVocaHelper();// TODO : I call this just in case the listner is null. Need to update not the listner to null
//            IVocaFullPlayTTSItem voca = (IVocaFullPlayTTSItem) item;
//            if (playTTS.isPlayStateStop()) {
//                playSingle(voca);
//            } else if (playTTS.isPlayStatePause()) {
//                resumePlaylist(voca.isVIPlaying() ? -1 : playlistItems.indexOf(voca));
//            } else if (playTTS.isPlayStatePlaySingle()) {
//                boolean isPlaying = voca.isVIPlaying();
//                stopPlaylist();
//                if (!isPlaying) {
//                    playSingle(voca);
//                }
//            }
//        }
//    };

    private void updateStateAllText() {
//        binding.layoutPlayAll.tvStatus.setText(R.string.select_all);
        binding.layoutPlayAll.icPlay.setColorFilter(BaseBindUtils.getEnableColor());
    }

    private void updateSelectUnselectAllItems(boolean selectAll) {
        playlistItems.clear();
        for (IVocaFullPlayTTSItem item : displayItems) {
            if (selectAll) {
                item.setVIChecked(true);
                playlistItems.add(item);
            } else {
                item.setVIChecked(false);
            }
        }

        if (selectAll) {
            checkedCount = displayItems.size();
        } else {
            checkedCount = 0;
        }
        checkedChanged = false;
        adapter.notifyDataSetChanged();
    }

//    private void updateStateAllData() {
//        playlistItems.clear();
//        for (IVocaFullPlayTTSItem item : displayItems) {
//            item.setVIChecked(true);
//            playlistItems.add(item);
//        }
//        checkedCount = displayItems.size();
//        checkedChanged = false;
//        adapter.notifyDataSetChanged();
//    }

    private void updateStateUnknownText() {
//        binding.layoutPlayAll.tvStatus.setText(R.string.select_unknown_words);
        binding.layoutPlayAll.icPlay.setColorFilter(checkedCount > 0 ? BaseBindUtils.getEnableColor() : BaseBindUtils.getDisableColor());
    }

    private void updateStateUnknownData() {
        ArrayList<IVocaFullPlayTTSItem> displayItemsTemp = new ArrayList<>(displayItems);
        clearItems();
        for (IVocaFullPlayTTSItem item : displayItemsTemp) {
            if (BaseVocaKnow.isNotKnown(item)) {
                item.setVIChecked(true);
                displayItems.add(item);
                playlistItems.add(item);
                fullStates.add(true);
            } else {
                item.setVIChecked(false);
                fullStates.add(false);
            }
        }
        checkedCount = playlistItems.size();
        checkedChanged = false;
        bindData();
    }

    private void selectVocaListByType(SELECT_RANGE_TYPE selectRangeType) {
        clearItemsWithCheckedCount();
        for (IVocaFullPlayTTSItem item : orignalFullItems) {
            if ((selectRangeType == SELECT_RANGE_TYPE.ALL)
                || ((selectRangeType == SELECT_RANGE_TYPE.AMKI_FIRST) && (BaseVocaKnow.isAmkiGrade1(item)))
                    || ((selectRangeType == SELECT_RANGE_TYPE.AMKI_SECOND) && (BaseVocaKnow.isAmkiGrade2(item)))
                    || ((selectRangeType == SELECT_RANGE_TYPE.ONLY_UNKNOWN) && (BaseVocaKnow.isUnknown(item)))
                    || ((selectRangeType == SELECT_RANGE_TYPE.UNKNOWN_AND_LESS) && (BaseVocaKnow.isUnknownAndLess(item)))
                || ((selectRangeType == SELECT_RANGE_TYPE.BOOKMARK) && (BaseVoca.isBookmark(item)))
            ) {
                item.setVIChecked(true);
                displayItems.add(item);
                playlistItems.add(item);
                fullStates.add(true);
                checkedCount++;
            } else {
                item.setVIChecked(false);
                fullStates.add(false);
            }
        }
        checkedChanged = false;
        bindData();
    }

    private void selectVocaListByRandom() {
        String[] items = new String[]{"5", "10", "15", "20", "25", "30"};
        int checkedItem = 1;
        singleChoiceDialog.showWrapContentHeight(
                R.string.choose,
                items,
                checkedItem,
                R.string.ok,
                R.string.cancel,
                new OnClickDialogListener() {
                    @Override
                    public void onClick(View view, Object object) {
                        final int which = (int) object;
                        String selectedItem = items[which];
                        clearItemsWithCheckedCount();
                        List<Integer> randomList = Utils.getRandomIndexList(orignalFullItems, Integer.valueOf(selectedItem) - 1);
                        for (Integer index : randomList) {
                            if (index < orignalFullItems.size()) {
                                IVocaFullPlayTTSItem item = orignalFullItems.get(index);
                                item.setVIChecked(true);
                                displayItems.add(item);
                                playlistItems.add(item);
                                fullStates.add(true);
                                checkedCount++;
                            }
                        }
                        checkedChanged = false;
                        bindData();
                    }

                    @Override
                    public void onDismiss(View view, Object object) {

                    }
                }
        );
    }

    private void clearItemsWithCheckedCount() {
        clearItems();
        checkedCount = 0;
    }

    private void clearItems() {
        displayItems.clear();
        playlistItems.clear();
        fullStates.clear();
    }

    private void updateStateNoneText() {
//        binding.layoutPlayAll.tvStatus.setText(R.string.unselect_all);
        binding.layoutPlayAll.icPlay.setColorFilter(BaseBindUtils.getDisableColor());
    }

//    private void updateStateNoneData() {
//        playlistItems.clear();
//        for (IVocaFullPlayTTSItem item : displayItems) {
//            item.setVIChecked(false);
//        }
//        checkedCount = 0;
//        checkedChanged = false;
//        adapter.notifyDataSetChanged();
//    }

    private void updateStateRangeText() {
//        binding.layoutPlayAll.tvStatus.setText(R.string.select_range);
        binding.layoutPlayAll.icPlay.setColorFilter(BaseBindUtils.getEnableColor());
    }

    private void updateStateRangeData(int from, int to) {
        if (from < 1) {
            from = 1;
        }
        if (to < 1) {
            to = 1;
        }
        int size = displayItems.size();
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
        ArrayList<IVocaFullPlayTTSItem> displayItemsTemp = new ArrayList<>(displayItems);
        clearItemsWithCheckedCount();
        from--;
        to--;
        for (int i = 0; i < size; i++) {
            IVocaFullPlayTTSItem item = displayItemsTemp.get(i);
            if (from <= i && i <= to) {
                item.setVIChecked(true);
                displayItems.add(item);
                playlistItems.add(item);
                fullStates.add(true);
                checkedCount++;
            } else {
                item.setVIChecked(false);
                fullStates.add(false);
            }
        }
        checkedChanged = false;
        bindData();
    }

    private void updateStateLastText() {
//        binding.layoutPlayAll.tvStatus.setText(R.string.last_selected);
        binding.layoutPlayAll.icPlay.setColorFilter(BaseBindUtils.getEnableColor());
    }

    private void updateStateLastData() {
        playlistItems.clear();
        checkedCount = 0;
        int count = displayItems.size();
        for (int i = 0; i < count; i++) {
            IVocaFullPlayTTSItem item = displayItems.get(i);
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
            Intent intent = new Intent(this, CopySelectedTextActivity.class);
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
        ToastUtil.getInstance(this).show("Please apply merge voices patch!");
    }

    private void getUserBooks(final String newBookName) {
        int uid = getUserID();
        if (uid > 0) {
            if (Utils.isConnected(this)) {
                Loading.show(this);
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
            ToastUtil.getInstance(this).show(R.string.msg_no_user_book);
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
            if (Utils.isConnected(this)) {
                StringBuilder vocaIds = new StringBuilder();
                StringBuilder typeIds = new StringBuilder();
                StringBuilder useServerVocabookDatas = new StringBuilder();
                StringBuilder idInServerVocabooks = new StringBuilder();
                if (checkedChanged) {
                    checkedChanged = false;
                    playlistItems.clear();
                    for (IVocaFullPlayTTSItem item : displayItems) {
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
                    alertDialog.show(R.string.msg_no_word_selected, R.string.ok, null);
                } else {
                    Loading.show(this);
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
                                        ToastUtil.getInstance(BasePlayListByTtsActivity.this).show(R.string.add_success);
                                    } else {
                                        ToastUtil.getInstance(BasePlayListByTtsActivity.this).show(R.string.add_fail);
                                    }
                                }

                                @Override
                                public void onFailure(String error) {
                                    Loading.hide();
                                    ToastUtil.getInstance(BasePlayListByTtsActivity.this).show(R.string.add_fail);
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
            if (Utils.isConnected(this)) {
                Loading.show(this);
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
                                    ToastUtil.getInstance(BasePlayListByTtsActivity.this).show(R.string.create_success);
                                    getUserBooks(name);
                                } else {
                                    Loading.hide();
                                    ToastUtil.getInstance(BasePlayListByTtsActivity.this).show(R.string.msg_book_name_existed);
                                }
                            }

                            @Override
                            public void onFailure(String error) {
                                Loading.hide();
                                ToastUtil.getInstance(BasePlayListByTtsActivity.this).show(R.string.create_fail);
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
        Intent i = new Intent(this, QuizPlayerActivity.class);
        i.putExtra(Constant.BUNDLE.KEY_VOCA_PLAYLIST, playlistItems);
        openNewScreen(i);
    }

    private void openSelectWordbookScreen() {
//        Intent intent = new Intent(context, WordbookByCategoryActivity.class);
//        intent.putExtra(Constant.BUNDLE.KEY_SELECT_MULTI, true);
//        openNewScreenForResult(intent, Constant.REQUEST_CODE.SELECT_BOOK);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constant.REQUEST_CODE.SELECT_BOOK
                && resultCode == Activity.RESULT_OK
                && data != null) {
            List<VocaBook> selectedBooks = (List<VocaBook>) data.getSerializableExtra(Constant.BUNDLE.KEY_VOCA_BOOKS);
            if (selectedBooks != null) {
                Loading.show(this);
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
                            int index = displayItems.size();
                            for (VocaInBook voca : response) {
                                voca.setPath(BaseVoca.getOutputRecordingFileName(
                                        studyLang,
                                        voca.getVocaType(),
                                        voca.getVocaId(),
                                        getUserID()
                                ));
                                voca.setIndex(++index);
                                voca.setVIChecked(true);
                                vocas.add(voca);
                                orignalFullItems.add(voca);
                                displayItems.add(voca);
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

    private void refreshSearchViewAdapterVocaKnow(IVocaFullPlayTTSItem voca) {
        if (Utils.isEmpty(orignalFullItems))
            return;

        for (IVocaFullPlayTTSItem iVocaFullItem : orignalFullItems) {
            if (BaseVoca.isSameVoca(iVocaFullItem, voca)) {
                int newBookmark = voca.getVIBookmark();
                int newVocaKnow = voca.getVIVocaKnow();
                int newVocaKnowPronounce = BaseVocaKnow.getVocaKnowPronounceByVocaKnow(newVocaKnow);
                iVocaFullItem.setVIBookmark(newBookmark);
                iVocaFullItem.setVIVocaKnow(newVocaKnow);
                iVocaFullItem.setVIVocaKnowPronounce(newVocaKnowPronounce);
                adapter.notifyPlaylistItemChanged(iVocaFullItem);
                break;
            }
        }

        bindData();
    }

    private void bindData() {
        adapter.setData(displayItems);
        adapter.notifyDataSetChanged();
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
        playTTS.stopPlayVoca();
        unRegisterEventBus();
        super.onDestroy();
    }

    private OnShufflePlayListListener onShufflePlayListListener = new OnShufflePlayListListener() {
        @Override
        public void onShuffle() {
            stopList();
            bindData();
            playList();
        }
    };

    protected ArrayList<IVocaFullPlayTTSItem> getFullItemsByInputOrder(List<IVocaFullPlayTTSItem> iVocaFullPlayTTSItemList, List<IVocaCoreItem> vocaCoreItemList) {
        Map<String, IVocaFullPlayTTSItem> mapVoca = iVocaFullPlayTTSItemList.stream()
                .collect(Collectors.toMap(e -> e.getVIVocaTypeId(), e -> e, (p1, p2) -> p1));
        List<IVocaFullPlayTTSItem> resultList = new ArrayList<>();
        for(IVocaCoreItem item : vocaCoreItemList) {
            if (mapVoca.containsKey(item.getVIVocaTypeId())) {
                resultList.add(mapVoca.get(item.getVIVocaTypeId()));
            }
        }
        return (ArrayList<IVocaFullPlayTTSItem>) resultList;
    }
}
