package com.dalread.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.UtteranceProgressListener;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.dalread.DalFlavor;
import com.dalread.R;
import com.dalread.adapter.VocaListAdapter;
import com.dalread.asyntask.CustomAsyncTask;
import com.dalread.base.EnumLanguage;
import com.dalread.component.SeparatorDecoration;
import com.dalread.composition.VoiceRecording;
import com.dalread.database.sqlite.model.DIC_SENTENCE_MODEL;
import com.dalread.databinding.ActivityVocaListBinding;
import com.dalread.dialog.ConvDictionaryOptionDialog;
import com.dalread.dialog.ConvVocaMenuDialog;
import com.dalread.dialog.WordListMenuDialog;
import com.dalread.dialog.WordListPlayerPlayAllWordsDialog;
import com.dalread.helper.VoiceFileDownloadHelper;
import com.dalread.interfaces.IVocaBasicItem;
import com.dalread.interfaces.IVocaFullItem;
import com.dalread.interfaces.IVocaFullPlayTTSItem;
import com.dalread.listener.OnAsyncTaskListenerWithType;
import com.dalread.listener.OnClickListener;
import com.dalread.listener.OnDoubleClickListener;
import com.dalread.model.EditVoca;
import com.dalread.model.VocaKnowGroupSelect;
import com.dalread.model.VocaKnowGroupSelected;
import com.dalread.model.VocaTypeId;
import com.dalread.model.WordListHeaderModel;
import com.dalread.model.WordListType;
import com.dalread.network.events.SuccessEvent;
import com.dalread.util.BaseBindUtils;
import com.dalread.util.BasePermissionUtils;
import com.dalread.util.BaseVoca;
import com.dalread.util.BaseVocaKnow;
import com.dalread.util.BaseVocaList;
import com.dalread.util.Constant;
import com.dalread.util.CopyTextUtil;
import com.dalread.util.DLog;
import com.dalread.util.DateUtils;
import com.dalread.util.LanguageUtil;
import com.dalread.util.Loading;
import com.dalread.util.OpenViewUtil;
import com.dalread.util.ToastUtil;
import com.dalread.util.TranslateUtil;
import com.dalread.util.Utils;
import com.dalread.util.Voca;
import com.dalread.util.VocaKnow;

import org.greenrobot.eventbus.Subscribe;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import butterknife.BindString;
import me.zhanghai.android.fastscroll.FastScrollerBuilder;

//ConvVocaListActivity와 중복되는게 많음.
@SuppressLint("NonConstantResourceId")
public class VocaListActivity extends BaseConvActivity implements OnAsyncTaskListenerWithType, View.OnClickListener {
    @BindString(R.string.tpl_wb_known_word_count)
    String tplKnownWordCount;
    
    protected VocaKnowGroupSelected<IVocaFullPlayTTSItem> vocaKnowGroupSelected = new VocaKnowGroupSelected(this, VocaKnowGroupSelected.Type.AMKI_GRADE);
    private boolean isShowFabButton;
    private VocaListAdapter vocaListAdapter;
    private boolean show4Buttons;
    private String vocaTypeListWithComma;
    private String vocaIDListWithComma;
    private String keyword = "";
    private final int TYPE_INIT_DATA = 0;
    private final int TYPE_GENERATE_DATA_FOR_PLAY_LIST = TYPE_INIT_DATA + 1;
    private final int TYPE_DISPLAY_VOICE_RECORDED_IN_UI = TYPE_GENERATE_DATA_FOR_PLAY_LIST + 1;
    private final int TYPE_CHANGE_BOTTOM_MENU = TYPE_DISPLAY_VOICE_RECORDED_IN_UI + 1;
    private ActivityVocaListBinding binding;
    private int bookId = -1;
    private VoiceRecording voiceRecording;
    private WordListType wordListType;
    protected WordListPlayerPlayAllWordsDialog wordListPlayerPlayAllWordsDialog;
    protected ConvDictionaryOptionDialog convDictionaryOptionDialog;
    private static boolean isShowPlayAll;
    private boolean isVocaChanged = false;
    private List<VocaTypeId> vocaTypeIdList;
    protected int currentBottomNavigationId;

    public static Intent createIntentByBookId(Context context, int bookId, boolean isShowPlayAllParam) {
        isShowPlayAll = isShowPlayAllParam;
        Intent intent = new Intent(context, VocaListActivity.class);
        intent.putExtra(Constant.BUNDLE.KEY_VOCA_TYPE, Constant.API_VALUE.VALUE_VOCA_TYPE_SENTENCE);
        intent.putExtra(Constant.BUNDLE.KEY_WORDLIST_TYPE, WordListType.BOOK);
        intent.putExtra(Constant.BUNDLE.KEY_VOCA_BOOK_ID, bookId);
//        intent.putExtra(Constant.BUNDLE.KEY_WORDLIST_TYPE, WordListType.VOCA_TYPE_ID_LIST);
        return intent;
    }

    public static Intent createIntentByBookmark(Context context) {
        Intent intent = new Intent(context, VocaListActivity.class);
        intent.putExtra(Constant.BUNDLE.KEY_WORDLIST_TYPE, WordListType.BOOKMARK);
        return intent;
    }

    public static Intent createIntentForDictionary(Context context) {
        Intent intent = new Intent(context, VocaListActivity.class);
        intent.putExtra(Constant.BUNDLE.KEY_WORDLIST_TYPE, WordListType.DICTIONARY);
        return intent;
    }
    @NonNull
    public static Intent createIntentForSearchHistory(Context context) {
        Intent intent = new Intent(context, VocaListActivity.class);
        intent.putExtra(Constant.BUNDLE.KEY_WORDLIST_TYPE, WordListType.SEARCH_HISTORY);
        return intent;
    }
    public static Intent createIntentByVocaTypeId(Context context, List<VocaTypeId> vocaTypeIdList) {
        Intent intent = new Intent(context, VocaListActivity.class);
        intent.putExtra(Constant.BUNDLE.KEY_VOCA_TYPE_ID, (Serializable) vocaTypeIdList);
        intent.putExtra(Constant.BUNDLE.KEY_WORDLIST_TYPE, WordListType.VOCA_TYPE_ID_LIST);
        return intent;
    }
    protected View getContentView() {
        binding = ActivityVocaListBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        return view;
    }

    @Override
    protected int getContentViewId() {
        return 0;//R.layout.activity_hanja_word_list;
    }


    @Override
    public void onHeaderTextRightClick() {
        openMenuDialog();
    }

    private void openMenuDialog() {
        final WordListMenuDialog dialog = new WordListMenuDialog(this, new OnClickListener() {
            @Override
            public void onClick(View view, Object object) {
                switch (view.getId()) {
                    case R.id.llShow4Buttons:
                        refresh4Buttons();
                        break;
                    case R.id.llOpenSetting:
                        DalFlavor.openSettings(VocaListActivity.this);
                        break;
                    case R.id.llShowPlayAllWords:
                        openWordListPlayerPlayAllWordsDialog();
                        break;
                    case R.id.llShowSearchDictonaryOption:
                        openConvDictionaryOptionDialog();
                        break;
                    case R.id.llCopy:
                        CopyTextUtil.openCopyConversationDialog(context, vocaKnowGroupSelected.getAllVocaList(), true);
                        break;
                    case R.id.llChatGpt:
                        startActivity(ChatGptActivity.createIntentWithBookId(VocaListActivity.this, bookId));
                        break;
                    case R.id.llBackToHome:
                        backToHome();
                        break;
                }
            }
        }, wordListType);

        dialog.setShow4Buttons(show4Buttons);
        dialog.show();
    }

    @Subscribe
    public void onEvent(SuccessEvent successEvent) {
        switch (successEvent.getScreen()) {
            case COMMON_VOCA_DATA:
                IVocaFullPlayTTSItem item= (IVocaFullPlayTTSItem) successEvent.getModel();
                switch (successEvent.getEventType()) {
                    case COMMON_VOCA_KNOW_CHANGED:
                    case COMMON_VOCA_KNOWPRONOUNCE_CHANGED:
                    case COMMON_VOCA_BOOKMKARK_CHANGED:
                        isVocaChanged = true;
                        refreshSearchViewAdapterDataChanged(item);
                        break;
                }
                break;
            case EDIT_VOCA:
                EditVoca editVoca = (EditVoca) successEvent.getModel();
                switch (successEvent.getEventType()) {
                    case DATA_CHANGED:
                        isVocaChanged = true;
                        IVocaBasicItem voca = editVoca.getiVocaFullItem();
                        refreshSearchViewAdapterDataChanged(voca);
                        subDatabase.updateVoca(voca);
                        break;
                }
                break;
        }
    }

    private void refreshSearchViewAdapterDataChanged(IVocaBasicItem voca) {
        if (Utils.isEmpty(vocaKnowGroupSelected.getAllVocaList()))
            return;

        for (IVocaBasicItem item : vocaKnowGroupSelected.getAllVocaList()) {
            if (Voca.isSameVoca(item, voca)) {
                int newBookmark = voca.getVIBookmark();
                int newVocaKnow = voca.getVIVocaKnow();
                int newVocaKnowPronounce = VocaKnow.getVocaKnowPronounceByVocaKnow(newVocaKnow);
                item.setVIBookmark(newBookmark);
                item.setVIVocaKnow(newVocaKnow);
                item.setVIVocaKnowPronounce(newVocaKnowPronounce);
                item.setVIPronounce(voca.getVIPronounce());
                item.setVIMeaning(LanguageUtil.getMotherTongueLanguage(this), voca.getVIMeaning(LanguageUtil.getMotherTongueLanguage(this)));
                item.setVIMeaningDetailed(LanguageUtil.getMotherTongueLanguage(this), voca.getVIMeaningDetailed(LanguageUtil.getMotherTongueLanguage(this)));
                break;
            }
        }
        bindData();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle();
        initMediaPlayer();
        initPlayVocaHelper();
        callAsyncTask(TYPE_INIT_DATA, null);
        initBottomNavigation();
        setOnClickListeners();
    }

    private void setTitle() {
        if (toolbar != null) {
            String title = "";
            if (wordListType.equals(WordListType.BOOK)) {
                if (bookId > 0) {
                    title = subDatabase.getBookTitle(bookId, EnumLanguage.getMotherTongueLanguage(this));

                }
            } else if (wordListType.equals(WordListType.BOOKMARK)) {
                title = getString(R.string.bookmark_list);
            } else if (wordListType.equals(WordListType.DICTIONARY)) {
                title = getString(R.string.dictionary);
            } else if (wordListType.equals(WordListType.SEARCH_HISTORY)) {
                title = getString(R.string.view_title_search_history);
            } else {
                title = getString(R.string.tb_word_list);
            }
            toolbar.setTitle(title);
        }
    }
    private void initMediaPlayer() {
        voiceRecording = new VoiceRecording(this);
    }
    private void initAdapter() {
        vocaListAdapter = new VocaListAdapter(this, vocaKnowActivity.onDoubleClickListenerOnBaseVocaKnow, onDoubleClickListener);
        vocaListAdapter.setShowPlayAll(isShowPlayAll);
        vocaListAdapter.setWordListType(wordListType);
        
        ToastUtil.getInstance(this).show("bookId : " + bookId);
    }

    private void initPlayVocaHelper() {
        if (!playTTS.playTTSHelper.hasMotherTongueListener()) {
            playTTS.playTTSHelper.setMotherTongueListener(new UtteranceProgressListener() {

                @Override
                public void onStart(String utteranceId) {
//                    updateItemStatus(utteranceId, true);
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
//                    updateItemStatus(utteranceId, false);
                }

                @Override
                public void onError(String utteranceId) {
//                    updateItemStatus(utteranceId, false);
                }
            });
        }
    }
    private void openConvVocaMenuDialog(IVocaFullPlayTTSItem voca) {
        final ConvVocaMenuDialog dialog = new ConvVocaMenuDialog(this, new OnClickListener() {
            @Override
            public void onClick(View view, Object object) {
                switch (view.getId()) {
                    case R.id.llEdit:
                        OpenViewUtil.openEditMeaningScreen(VocaListActivity.this, voca);
                        break;
                    case R.id.llTranslate:
                        TranslateUtil.openWebTranslate(VocaListActivity.this, voca.getVIVoca(), () -> {});
                        updateSearchHistory(voca);
                        break;
                    case R.id.llWebDictionary:
                        OpenViewUtil.openExternalWebDictionary(context, voca);
                        break;
                    case R.id.llCopy:
                        CopyTextUtil.openCopyVoca(VocaListActivity.this, voca);
                        break;
                }
            }
        }, wordListType, false);

        dialog.show();
    }
    private OnDoubleClickListener onDoubleClickListener = new OnDoubleClickListener() {
        @Override
        public void onClick(View view, Object data) {
            if (data instanceof WordListHeaderModel) {
                switch (view.getId()) {
                    case R.id.icPlayAll:
                        openPlaylistActivity((WordListHeaderModel)data);
                        break;
                }
            } else if (data instanceof IVocaFullPlayTTSItem) {
                IVocaFullPlayTTSItem voca = (IVocaFullPlayTTSItem) data;
                switch (view.getId()) {
                    case R.id.ivArrowRight:
                        openConvVocaMenuDialog(voca);
                        break;
                    case R.id.ivSpeaker:
                        stopRecordingAndResetMicIcon();
                        setRecordFile(voca);
//                    voiceRecording.onClickSpeaker();
//                    initPlayVocaHelper();// TODO : I call this just in case the listner is null. Need to update not the listner to null
                        playTTS.playTTSHelper.isPlayTTSAtFirst = false;
                        if (playTTS.canPlay()) {
                            updateSpeakerUIStatus(voca, true);
                            playTTS.playSingle(voca);
//                        Voca.resetIsPlayingTTS(vocaList);
                        } else if (playTTS.isPlayStatePlaySingle()) {
                            boolean isPlaying = voca.isVIPlaying();
                            playTTS.stopPlayVoca();
                            updateSpeakerUIStatus(voca, false);
                            if (!isPlaying) {
                                updateSpeakerUIStatus(voca, true);
                                playTTS.playSingle(voca);
                            }
                        }
                        break;
                    case R.id.ivMic:
                        stopPlayingAdResetSpeakerIcon();
                        boolean wasRecording = voca.isVIRecording();
                        setRecordFile(voca);
                        voiceRecording.onClickMic();
                        if (voiceRecording.recording) {
                            updateMicUIStatus(voca, true);
                        } else {
                            updateMicUIStatus(voca, false);
                        }

                        if (wasRecording) {
                            ToastUtil.getInstance(VocaListActivity.this).show(R.string.toast_listen_my_voice_recording);
                            updateAfterRecording(voca);
                            playTTS.stopPlaylist();
                            if (playTTS.canPlay()) {
                                playTTS.playMyVoiceOnce(voca);
                            }

                        }
                        break;

                    case R.id.ivShowRecordedVoice:
                        callAsyncTask(TYPE_DISPLAY_VOICE_RECORDED_IN_UI, null);
                        break;
                    case R.id.ivEditView:
                        OpenViewUtil.openEditMeaningScreen(VocaListActivity.this, voca);
                        break;
                    case R.id.ivTranslate:
                        TranslateUtil.openWebTranslate(VocaListActivity.this, voca.getVIVoca(), () -> {
                        });
//                    OpenViewUtil.openExternalWebDictionary(ConvVocaListActivity.this, voca);
                        updateSearchHistory(voca);
                        break;
                    case R.id.right_view:
                        resetSearchHistory(voca);
                        break;
                    case R.id.ivCopy:
                        CopyTextUtil.copyToClipboardShowWhatCopied(VocaListActivity.this, voca.getVIVoca());
                        break;
                }
            }
        }

        @Override
        public void onDoubleClick(View view, Object data) {

        }
    };
    private void resetSearchHistory(IVocaFullPlayTTSItem voca) {
        vocaKnowGroupSelected.getAllVocaList().remove(voca);
        subDatabase.resetSearchHistory(voca.getVIId());
    }
    private void updateSearchHistory(IVocaFullPlayTTSItem voca) {
        voca.setVISEARCH_HISTORY(DateUtils.getCurrentDateTimeFullFormat());
        subDatabase.updateWordSeachyHistory(voca);
    }
    private void setOnClickListeners() {
        //임시 주석 처리
//        binding.fab.setOnClickListener(view -> {
//            openGptCustomTab();
//        });
//        binding.fabChatApi.setOnClickListener(view -> {
//            startActivity(ChatGptActivity.createIntentWithBookId(VocaListActivity.this, bookId));
//        });
    }

    private void updateRealmDbAfterRecording(IVocaFullPlayTTSItem voca) {
        String voiceFileName = BaseVoca.getMyOutputRecordingFileName(this, voca);
        VoiceFileDownloadHelper downloadFileHelper = new VoiceFileDownloadHelper(getBaseContext(), true, null);
        downloadFileHelper.getServerVoiceFileInfo(voiceFileName, BaseVoca.onVoiceFileInfoDownloadListener);
    }

    public void setRecordFile(IVocaFullPlayTTSItem voca) {
        if (voca instanceof DIC_SENTENCE_MODEL) {
            ((DIC_SENTENCE_MODEL)voca).setPath(Voca.getMyOutputRecordingFileName(context, voca));
        }

        voiceRecording.recordFile = Voca.getVoiceFileInApp(this, voca.getVIPath());
    }

    private void stopRecordingAndResetMicIcon() {
        if (voiceRecording.recording) {
            voiceRecording.stopRecording();
            Voca.resetIsRecordingVoca(vocaKnowGroupSelected.getAllVocaList());
            vocaListAdapter.notifyDataSetChanged();
        }
    }

    private void stopPlayingAdResetSpeakerIcon() {
        if (playTTS.isPlaying()) {
            playTTS.stopPlayVoca();
            Voca.resetIsPlayingTTS(vocaKnowGroupSelected.getAllVocaList());
            vocaListAdapter.notifyDataSetChanged();
        }
    }

    private void updateSpeakerUIStatus(IVocaFullPlayTTSItem voca, boolean playing) {
        if (voca != null) {
            Voca.setIsPlayingTTS(vocaKnowGroupSelected.getAllVocaList(), voca, playing);
            vocaListAdapter.notifyDataSetChanged();
        }
    }

    private void updateMicUIStatus(IVocaFullPlayTTSItem voca, boolean recording) {
        if (voca != null) {
            Voca.setIsRecordingTTS(vocaKnowGroupSelected.getAllVocaList(), voca, recording);
            if (recording)
                vocaListAdapter.setIsRecordingVoca(voca);
            else
                vocaListAdapter.setIsRecordingVoca(null);
//            adapter.notifyDataSetChanged();
        }
    }

    private void updateAfterRecording(IVocaFullPlayTTSItem voca) {
        updateModelAfterRecording(voca);
        updateSqlDbAfterRecording(voca);
        updateRealmDbAfterRecording(voca);
        bindVoca(voca);
    }

    private void updateModelAfterRecording(IVocaFullPlayTTSItem voca) {
        voca.setVIVoiceFile(Constant.INT_BOOLEAN.TRUE);
    }
    private void updateSqlDbAfterRecording(IVocaFullPlayTTSItem voca) {
        subDatabase.setHasVoiceFile(voca, Constant.INT_BOOLEAN.TRUE);
    }

    protected void initData() {
        super.initData();
        show4Buttons = false;
        isShowFabButton = getIntent().getBooleanExtra(Constant.BUNDLE.KEY_SHOW_FAB_BUTTON, true);
        bookId = getIntent().getIntExtra(Constant.BUNDLE.KEY_VOCA_BOOK_ID, -1);
        show4Buttons = getIntent().getBooleanExtra(Constant.BUNDLE.KEY_SHOW_4_BUTTONS, false);
        wordListType = (WordListType) getIntent().getSerializableExtra(Constant.BUNDLE.KEY_WORDLIST_TYPE);
        vocaTypeIdList = (List<VocaTypeId>) getIntent().getSerializableExtra(Constant.BUNDLE.KEY_VOCA_TYPE_ID);
        initAdapter();
    }

    @Override
    protected void initLayout() {
        super.initLayout();
        new FastScrollerBuilder(binding.rvInfo).build();

        binding.rvInfo.setLayoutManager(new LinearLayoutManager(context));
        binding.rvInfo.addItemDecoration(BaseBindUtils.getSeparatorDecoration(context));
        binding.rvInfo.setAdapter(vocaListAdapter);
    }


    @SuppressWarnings("unchecked")
    @Override
    protected void getData() {

    }

//    @Override
//    protected List<HanjaQuizItem> getQuizHanjaList() {
//        return new ArrayList<>();
//    }



//    protected final DialogInterface.OnClickListener onDialogItemClickListener = (dialog, which) -> {
//        switch (which) {
////            case R.id.tvShow4Buttons:
////                refresh4Buttons();
////                break;
////            case R.id.tvAllToKnown:
////                getVocaTypeAndID();
////                changeMultipleVocaKnow(vocaTypeListWithComma, vocaIDListWithComma, Constant.VOCA_KNOW.VOCA_KNOW_KNOWN);
////                break;
////            case R.id.tvAllToUnknown:
////                getVocaTypeAndID();
////                changeMultipleVocaKnow(vocaTypeListWithComma, vocaIDListWithComma, Constant.VOCA_KNOW.VOCA_KNOW_UNKNOWN);
////                break;
//            case R.id.tvBackToHome:
//                backToHome();
//                break;
//            default:
//                break;
//        }
//    };

    private void bindData() {
        vocaListAdapter.setData(vocaKnowGroupSelected);
        vocaListAdapter.setShow4Buttons(show4Buttons);
        vocaListAdapter.notifyDataSetChanged();
    }
    private void bindVoca(IVocaFullPlayTTSItem voca) {
        vocaListAdapter.notifyItemChanged(voca);
    }


    private void refresh4Buttons() {
        show4Buttons = !show4Buttons;
        vocaListAdapter.setShow4Buttons(show4Buttons);
        vocaListAdapter.notifyDataSetChanged();
//        hanjaWordListDialog.update4ButtonName(show4Buttons);
    }

//    private void getVocaTypeAndID() {
//        StringJoiner sjVocaType = new StringJoiner(",");
//        StringJoiner sjVocaID = new StringJoiner(",");
//        for(HanjaItem hanjaItem : vocaList) {
//            sjVocaType.add(String.valueOf(hanjaItem.getHI_VOCA_TYPE()));
//            sjVocaID.add(String.valueOf(hanjaItem.getHI_ID()));
//        }
//
//        vocaTypeListWithComma = sjVocaType.toString();
//        vocaIDListWithComma = sjVocaID.toString();
//    }

    private void callAsyncTask(int type, Object data) {
        new CustomAsyncTask(this, this, data, type, true).execute();
    }

    protected void initBottomNavigation() {
        binding.nvBottom.getMenu().findItem(R.id.nav_frequency).setVisible(false);
        binding.nvBottom.setOnNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id != currentBottomNavigationId) {
                currentBottomNavigationId = id;
                callAsyncTask(TYPE_CHANGE_BOTTOM_MENU, null);
                return true;
            }
            return false;
        });
        currentBottomNavigationId = R.id.nav_grade;
    }

    @Override
    public void onInitAsyncTask(int searchType) {
        switch (searchType) {
            case TYPE_DISPLAY_VOICE_RECORDED_IN_UI:
                ToastUtil.getInstance(getBaseContext()).show(R.string.toast_set_mic_black_my_recorded_voice_item);
//                Loading.show(this, "내가 녹음한 항목의 마이크와 스피커는 검게 표시해줍니다.");
                break;
            default:
                Loading.showDelay(this);
                break;
        }
    }
    @Override
    public void onInitAsyncTask() {

    }

    @Override
    public void onPrevExecuteAsyncTask() {

    }

    @Override
    public Object onPostExecuteAsyncTask(int searchType, Object data) {
        List<IVocaFullPlayTTSItem> vocaList = new ArrayList<>();
        switch (searchType) {
            case TYPE_INIT_DATA:
                if (wordListType.equals(WordListType.BOOK)) {
                    if (bookId > 0) {
                        vocaList = subDatabase.getVocaListByBookId(bookId);
                    }
                } else if (wordListType.equals(WordListType.VOCA_TYPE_ID_LIST)) {
                    vocaList = subDatabase.getPlayTTSVocaListByVocaIdList(vocaTypeIdList);
                } else if (wordListType.equals(WordListType.BOOKMARK)) {
                    vocaList = subDatabase.getVocaBookmarkList();
                } else if (wordListType.equals(WordListType.DICTIONARY)) {
                    vocaList = subDatabase.getAllWordVocaListStartWith(keyword, isSearchInStudyLang());
                } else if (wordListType.equals(WordListType.SEARCH_HISTORY)) {
                    vocaList = subDatabase.getWordSearchHistory(keyword, isSearchInStudyLang());
                }

                Voca.resetVocaList(vocaList);
                return vocaList;
            case TYPE_GENERATE_DATA_FOR_PLAY_LIST:
                return BaseVocaList.getWordListByWordListGroup((VocaKnowGroupSelect) data, vocaList);
            case TYPE_DISPLAY_VOICE_RECORDED_IN_UI:
                int cntOfVoiceFiles = Voca.udpateVoiceFileInVocaList(getBaseContext(), vocaList);
                if (cntOfVoiceFiles == 0) {
                    ToastUtil.getInstance(getBaseContext()).show(R.string.toast_no_my_recorded_voice_file);
                }
                return vocaList;
            case TYPE_CHANGE_BOTTOM_MENU:
                return null;
        }
        return null;
    }

    @Override
    public void onFinishAsyncTask(int searchType, Object resultData, Object data) {
        switch (searchType) {
            case TYPE_INIT_DATA:
            case TYPE_DISPLAY_VOICE_RECORDED_IN_UI:
                vocaKnowGroupSelected = BaseVocaList.getWordListByWordListGroup(this, (List<IVocaFullPlayTTSItem>)resultData);
                bindData();
//                binding.LayoutItemWordListPlayerHeader.root.setVisibility(View.VISIBLE);
                break;
            case TYPE_GENERATE_DATA_FOR_PLAY_LIST:
                openPlaylistActivity((List<IVocaFullPlayTTSItem>) resultData);
                break;
            case TYPE_CHANGE_BOTTOM_MENU:
                if (currentBottomNavigationId == R.id.nav_grade) {
                    vocaKnowGroupSelected.setType(VocaKnowGroupSelected.Type.AMKI_GRADE);
                } else if (currentBottomNavigationId == R.id.nav_alphabet) {
                    vocaKnowGroupSelected.setType(VocaKnowGroupSelected.Type.ALPAHBET);
                }
                bindData();
                break;
        }
        Loading.hide();
        if (searchType == TYPE_INIT_DATA) {
            binding.nvBottom.setVisibility(View.VISIBLE);
            binding.fab.setVisibility(isShowFabButton ? View.VISIBLE : View.GONE);
            binding.fabChatApi.setVisibility(isShowFabButton ? View.VISIBLE : View.GONE);
            //임시로 일단 좀 숨기자
            binding.fab.setVisibility(View.GONE);
            binding.fabChatApi.setVisibility(View.GONE);
        }
    }

    @Override
    public void onHeaderLeftClick() {
        onBackPressed();
    }
    @Override
    public void onBackPressed() {
//        MobileAd.showInterstitialAd(this);
        setResult(RESULT_OK, new Intent().putExtra(Constant.INTENT.KEY_DATA, isVocaChanged));
        super.onBackPressed();
    }
    private boolean isSearchInStudyLang() {
        return sharedPreferences.getSearchInStudyLang();
    }
    private void openConvDictionaryOptionDialog() {
        if (convDictionaryOptionDialog == null) {
            convDictionaryOptionDialog = new ConvDictionaryOptionDialog(this);
        }
        convDictionaryOptionDialog.show();
    }

    private void openWordListPlayerPlayAllWordsDialog() {
        if (wordListPlayerPlayAllWordsDialog == null) {
            wordListPlayerPlayAllWordsDialog = new WordListPlayerPlayAllWordsDialog(this, onWordListPlayerPlayAllWordsDialogListener);
        }
        wordListPlayerPlayAllWordsDialog.show();
    }
    protected OnClickListener onWordListPlayerPlayAllWordsDialogListener = (view, object) -> {
        DLog.d(getLogTag(), "onWordListPlayerPlayAllWordsDialogListener");
        switch (view.getId()) {
            case R.id.tvOpenSelectedItemsOnly:
                callAsyncTask(TYPE_GENERATE_DATA_FOR_PLAY_LIST, (VocaKnowGroupSelect) object);
                break;
            case R.id.tvOpenBookmarkedItemsOnly:
                openPlaylistActivity(vocaKnowGroupSelected.getAllVocaList().stream().filter(e -> Voca.isBookmark(e)).collect(Collectors.toList()));
                break;
            case R.id.tvOpenAllItems:
                openPlaylistActivity(vocaKnowGroupSelected.getAllVocaList());
                break;
        }
    };

    private void openPlaylistActivity(WordListHeaderModel item) {
        if (BaseVocaKnow.isAmkiGrade1(item.getKnow())) {
            openPlaylistActivity(vocaKnowGroupSelected.getAmkiGrade1List());
        } else if (BaseVocaKnow.isAmkiGrade2(item.getKnow())) {
            openPlaylistActivity(vocaKnowGroupSelected.getAmkiGrade2List());
        } else if (BaseVocaKnow.isNotRated(item.getKnow())) {
            openPlaylistActivity(vocaKnowGroupSelected.getNotRatedList());
        } else if (BaseVocaKnow.isUnknown(item.getKnow())) {
            openPlaylistActivity(vocaKnowGroupSelected.getUnknownList());
        } else if (BaseVocaKnow.isKnown(item.getKnow())) {
            openPlaylistActivity(vocaKnowGroupSelected.getKnownList());
        }
    }

    private void openPlaylistActivity(List<? extends IVocaFullItem> data) {
        Intent intent = new Intent(this, PlayListByTtsActivity.class);
        List<VocaTypeId> vocaTypeIdList = BaseVocaList.convertToVocaTypeIdList(data);
        intent.putExtra(Constant.BUNDLE.KEY_VOCA_PLAYLIST, (Serializable) vocaTypeIdList);
        intent.putExtra(Constant.BUNDLE.KEY_WORDLIST_TYPE, wordListType);
        intent.putExtra(Constant.BUNDLE.KEY_VOCA_BOOK_ID, bookId);
//        intent.putExtra(Constant.BUNDLE.KEY_VOCA_KNOW_ACTIVITY, (Serializable) vocaKnowActivity);
        playTTS.stopPlayVoca();
        playTTS.setIncludeMeaning();
        openNewScreen(intent);
    }

    @Override
    public void onDestroy() {
        voiceRecording.destroy();
        super.onDestroy();
    }

    @Override
    public void onStop() {
        DLog.d(getLogTag(), "onStop");
        super.onStop();
        if (!sharedPreferences.getKeepPlayingOnBackgroundMode()) {
            playTTS.stopPlayVoca();
            Voca.resetVocaList(vocaKnowGroupSelected.getAllVocaList());
            bindData();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        BasePermissionUtils.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
//        if (requestCode == PermissionUtils.REQUEST_CODE_RECORD_AUDIO) {
//            for (int i = 0, len = permissions.length; i < len; i++) {
//                String permission = permissions[i];
//                if (permission.equalsIgnoreCase(Manifest.permission.RECORD_AUDIO)) {
//                    if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
//                        boolean showRationale = shouldShowRequestPermissionRationale(permission);
//                        if (!showRationale) {
//                            // Deny permission and the application will not ask again,
//                            // show popup to remind user to enable permission
//                            ConfirmationDialog dialog = new ConfirmationDialog(this, R.string.permission_needed_title,
//                                    R.string.record_audio_permission_needed_message, R.string.open_app_details_settings, R.string.cancel,
//                                    new ConfirmationDialog.OnDialogClickListener() {
//                                @Override
//                                public void onPositive(DialogInterface dialog) {
//                                    dialog.dismiss();
//                                    Intent intent = new Intent();
//                                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
//                                    Uri uri = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null);
//                                    intent.setData(uri);
//                                    ConvVocaListActivity.this.startActivity(intent);
//                                }
//
//                                @Override
//                                public void onNegative(DialogInterface dialog) {
//                                    dialog.dismiss();
//                                }
//                            });
//                            dialog.show();
//                        }
//                    }
//                }
//            }
//        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.icPlayAll:
                openPlaylistActivity(vocaKnowGroupSelected.getAllVocaList());
                break;
        }
    }
}
