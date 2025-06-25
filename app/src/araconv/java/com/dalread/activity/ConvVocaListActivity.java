package com.dalread.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.UtteranceProgressListener;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.dalread.DalFlavor;
import com.dalread.R;
import com.dalread.adapter.ConvVocaListAdapter;
import com.dalread.adapter.VocaListAdapter;
import com.dalread.asyntask.CustomAsyncTask;
import com.dalread.base.BaseVocaFilterActivity;
import com.dalread.base.EnumLanguage;
import com.dalread.composition.VoiceRecording;
import com.dalread.database.SharedPreferencesDB;
import com.dalread.database.sqlite.model.DIC_SENTENCE_MODEL;
import com.dalread.databinding.ActivityConvVocaListBinding;
import com.dalread.dialog.ConvCommonMenuDialog;
import com.dalread.dialog.ConvDictionaryOptionDialog;
import com.dalread.dialog.ConvVocaMenuDialog;
import com.dalread.dialog.VocaFilterDialog;
import com.dalread.helper.ExecutorHelper;
import com.dalread.helper.PromptUtil;
import com.dalread.helper.VoiceFileDownloadHelper;
import com.dalread.interfaces.IVocaBasicItem;
import com.dalread.interfaces.IVocaFullItem;
import com.dalread.interfaces.IVocaFullPlayTTSItem;
import com.dalread.listener.OnAsyncTaskListenerWithType;
import com.dalread.listener.OnClickListener;
import com.dalread.listener.OnDoubleClickListener;
import com.dalread.model.EditVoca;
import com.dalread.model.VocaKnowGroupSelect;
import com.dalread.model.VocaTypeId;
import com.dalread.model.WordListType;
import com.dalread.network.events.SuccessEvent;
import com.dalread.util.AppFlavorUtil;
import com.dalread.util.AraConvUtil;
import com.dalread.util.BasePermissionUtils;
import com.dalread.util.BaseVoca;
import com.dalread.util.BaseVocaList;
import com.dalread.util.ChatGptWebUtil;
import com.dalread.util.Constant;
import com.dalread.util.ConversationBookUtil;
import com.dalread.util.CopyTextUtil;
import com.dalread.util.DLog;
import com.dalread.util.DateUtils;
import com.dalread.util.DialogUtil;
import com.dalread.util.LanguageUtil;
import com.dalread.util.Loading;
import com.dalread.util.OpenViewUtil;
import com.dalread.util.PlaylistUtil;
import com.dalread.util.ToastUtil;
import com.dalread.util.TranslateUtil;
import com.dalread.util.UserUtil;
import com.dalread.util.Utils;
import com.dalread.util.Voca;
import com.dalread.util.VocaKnow;
import com.dalread.util.VocaListUtil;

import org.greenrobot.eventbus.Subscribe;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import butterknife.BindString;
import me.zhanghai.android.fastscroll.FastScrollerBuilder;

//VocaListActivity와 중복되는게 많음.
@SuppressLint("NonConstantResourceId")
public class ConvVocaListActivity extends BaseConvActivity implements OnAsyncTaskListenerWithType, View.OnClickListener {
    @BindString(R.string.tpl_wb_known_word_count)
    String tplKnownWordCount;
    private List<IVocaFullPlayTTSItem> allWordList; //단어 리스트를 보여줄때 사용.
    private List<IVocaFullPlayTTSItem> vocaList;
    private boolean isShowFabButton;
    private ConvVocaListAdapter convVocaListAdapter;
    private VocaListAdapter vocaListAdapter;
    private boolean show4Buttons;
    private ExecutorHelper executorHelper;
    private String vocaTypeListWithComma;
    private String vocaIDListWithComma;
    private String keyword = "";
    private final int TYPE_INIT_DATA = 0;
    private final int TYPE_GENERATE_DATA_FOR_PLAY_LIST = TYPE_INIT_DATA + 1;
    private final int TYPE_DISPLAY_VOICE_RECORDED_IN_UI = TYPE_GENERATE_DATA_FOR_PLAY_LIST + 1;
    private ActivityConvVocaListBinding binding;
    private int bookId = -1;
    private VoiceRecording voiceRecording;
    private WordListType wordListType;
    protected VocaFilterDialog vocaFilterDialog;
    protected ConvDictionaryOptionDialog convDictionaryOptionDialog;
    private static boolean isShowPlayAll;
    private boolean isVocaChanged = false;
    private int vocabookTypeCode = Constant.API_VALUE.VOCABOOK_TYPE_CODE_NONE;
    private List<VocaTypeId> vocaTypeIdList;

    public static Intent createIntentByBookId(Context context, int bookId, boolean isShowPlayAllParam) {
        isShowPlayAll = isShowPlayAllParam;
        Intent intent = new Intent(context, ConvVocaListActivity.class);
        intent.putExtra(Constant.BUNDLE.KEY_VOCA_TYPE, Constant.API_VALUE.VALUE_VOCA_TYPE_SENTENCE);
        if ((new ConversationBookUtil()).isConversationBook(WordListType.BOOK, bookId))
            intent.putExtra(Constant.BUNDLE.KEY_WORDLIST_TYPE, WordListType.BOOK);
        else
            intent.putExtra(Constant.BUNDLE.KEY_WORDLIST_TYPE, WordListType.SERVER_VOCA_BOOK_EXPRESSION_LIST);
        intent.putExtra(Constant.BUNDLE.KEY_VOCA_BOOK_ID, bookId);
        if (AppFlavorUtil.isAraHangulApp()) {
            intent.putExtra(Constant.BUNDLE.KEY_VOCABOOK_TYPE_CODE_SERVER, Constant.API_VALUE.VOCABOOK_TYPE_CODE_NONE);
        } else {
            intent.putExtra(Constant.BUNDLE.KEY_VOCABOOK_TYPE_CODE_SERVER, Constant.API_VALUE.VOCABOOK_TYPE_CODE_SERVER);
        }
        return intent;
    }

    public static Intent createIntentByUserVocaBookLocal(Context context, int userVocaBookId, String bookName) {
        Intent intent = new Intent(context, ConvVocaListActivity.class);
        intent.putExtra(Constant.BUNDLE.KEY_WORDLIST_TYPE, WordListType.USER_VOCA_BOOK_LOCAL);
        intent.putExtra(Constant.BUNDLE.KEY_VOCA_BOOK_ID, userVocaBookId);
        intent.putExtra(Constant.BUNDLE.KEY_VOCA_BOOK_NAME, bookName);
        intent.putExtra(Constant.BUNDLE.KEY_VOCABOOK_TYPE_CODE_SERVER, Constant.API_VALUE.VOCABOOK_TYPE_CODE_USER);
        return intent;
    }

    public static Intent createIntentByBookmark(Context context) {
        Intent intent = new Intent(context, ConvVocaListActivity.class);
        intent.putExtra(Constant.BUNDLE.KEY_WORDLIST_TYPE, WordListType.BOOKMARK);
        return intent;
    }

    public static Intent createIntentForDictionary(Context context) {
        Intent intent = new Intent(context, ConvVocaListActivity.class);
        intent.putExtra(Constant.BUNDLE.KEY_WORDLIST_TYPE, WordListType.DICTIONARY);
        return intent;
    }
    @NonNull
    public static Intent createIntentForSearchHistory(Context context) {
        Intent intent = new Intent(context, ConvVocaListActivity.class);
        intent.putExtra(Constant.BUNDLE.KEY_WORDLIST_TYPE, WordListType.SEARCH_HISTORY);
        return intent;
    }
    public static Intent createIntentByVocaTypeId(Context context, List<VocaTypeId> vocaTypeIdList) {
        Intent intent = new Intent(context, ConvVocaListActivity.class);
        intent.putExtra(Constant.BUNDLE.KEY_VOCA_TYPE_ID, (Serializable) vocaTypeIdList);
        intent.putExtra(Constant.BUNDLE.KEY_WORDLIST_TYPE, WordListType.VOCA_TYPE_ID_LIST);
        intent.putExtra(Constant.BUNDLE.KEY_VOCABOOK_TYPE_CODE_SERVER, Constant.API_VALUE.VOCABOOK_TYPE_CODE_NONE);
        return intent;
    }
    protected View getContentView() {
        binding = ActivityConvVocaListBinding.inflate(getLayoutInflater());
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


    private void openConvVocaMenuDialog(IVocaFullPlayTTSItem voca) {
        final ConvVocaMenuDialog dialog = new ConvVocaMenuDialog(this, new OnClickListener() {
            @Override
            public void onClick(View view, Object object) {
                switch (view.getId()) {
                    case R.id.llWordList:
                        openWordListButton();
                        break;
                    case R.id.llEdit:
                        if (vocabookTypeCode == Constant.API_VALUE.VOCABOOK_TYPE_CODE_NONE) {
                            OpenViewUtil.openEditMeaningScreen(ConvVocaListActivity.this, voca);
                        } else {
                            OpenViewUtil.openEditMeaningVocaBookScreen(ConvVocaListActivity.this, voca, vocabookTypeCode);
                        }
                        break;
                    case R.id.llTranslate:
                        TranslateUtil.openWebTranslate(ConvVocaListActivity.this, voca.getVIVoca(), () -> {});
                        updateSearchHistory(voca);
                        break;
                    case R.id.llWebDictionary:
                        OpenViewUtil.openExternalWebDictionary(context, voca);
                        break;
                    case R.id.llCopy:
                        CopyTextUtil.openCopyVoca(ConvVocaListActivity.this, voca);
                        break;
                }
            }
        }, wordListType, true);

        dialog.show();
    }

    private void openMenuDialog() {
        final ConvCommonMenuDialog dialog = new ConvCommonMenuDialog(this, new OnClickListener() {
            @Override
            public void onClick(View view, Object object) {
                switch (view.getId()) {
                    case R.id.llShow4Buttons:
                        refresh4Buttons();
                        break;
                    case R.id.llOpenSetting:
                        DalFlavor.openSettings(ConvVocaListActivity.this);
                        break;
                    case R.id.llShowPlayAllWords:
                        openWordListPlayerPlayAllWordsDialog();
                        break;
                    case R.id.llWordList:
                        openWordListButton();
                        break;
                    case R.id.llShowSearchDictonaryOption:
                        openConvDictionaryOptionDialog();
                        break;
                    case R.id.llClearSearchHistory:
                        subDatabase.clearSearchHistory();
                        bindData(Collections.emptyList());
                        break;
                    case R.id.llCopy:
                        CopyTextUtil.openCopyConversationDialog(context, vocaList, true);
                        break;
                    case R.id.llMakeRolePlaying:
                        openConversationRawDataActivity();
                        break;
                    case R.id.llChatGpt:
                        startActivity(ChatGptActivity.createIntentWithBookId(ConvVocaListActivity.this, bookId));
                        break;
//                    case R.id.tvAllToKnown:
//                        getVocaTypeAndID();
//                        changeMultipleVocaKnow(vocaTypeListWithComma, vocaIDListWithComma, Constant.VOCA_KNOW.VOCA_KNOW_KNOWN);
//                        break;
//                    case R.id.tvAllToUnknown:
//                        getVocaTypeAndID();
//                        changeMultipleVocaKnow(vocaTypeListWithComma, vocaIDListWithComma, Constant.VOCA_KNOW.VOCA_KNOW_UNKNOWN);
//                        break;
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

                switch (successEvent.getEventType()) {
                    case DATA_CHANGED:
                        isVocaChanged = true;
                        if (successEvent.getModel() instanceof EditVoca) {
                            EditVoca editVoca = (EditVoca) successEvent.getModel();
                            IVocaFullItem voca = editVoca.getiVocaFullItem();
                            if (subDatabase.updateVocaInVocabook(voca, vocabookTypeCode))
                                refreshSearchViewAdapterDataChanged(voca);
                        } else if (successEvent.getModel() instanceof IVocaFullItem) {
                            IVocaFullItem voca = (IVocaFullItem) successEvent.getModel();
                            if (subDatabase.updateVocaInVocabook(voca, vocabookTypeCode))
                                refreshSearchViewAdapterDataChanged(voca);
                        } else {
                            DLog.d("", "successEvent.getModel() checking is needed.");
                        }
                        break;
                }
                break;
        }
    }

    private void refreshSearchViewAdapterDataChanged(IVocaBasicItem voca) {
        if (Utils.isEmpty(vocaList))
            return;

        for (IVocaBasicItem item : vocaList) {
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
        bindData(vocaList);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle();
        initMediaPlayer();
        initPlayVocaHelper();
        hideMenusOnReleaseMode();
        callAsyncTask(TYPE_INIT_DATA, null);
        setOnClickListeners();
    }
    private void hideMenusOnReleaseMode() {
        if (UserUtil.isDebugOrAdminUser(this)) {
            binding.fabChatApi.setVisibility(View.VISIBLE);
        } else {
            binding.fabChatApi.setVisibility(View.GONE);
        }
    }
    private void setTitle() {
        if (toolbar != null) {
            String title = "";
            if (wordListType.equals(WordListType.BOOK) || wordListType.equals(WordListType.SERVER_VOCA_BOOK_EXPRESSION_LIST)) {
                if (bookId > 0) {
                    title = subDatabase.getBookTitle(bookId, EnumLanguage.getMotherTongueLanguage(this));
                }
            } else if (wordListType.equals(WordListType.USER_VOCA_BOOK_LOCAL)) {
                title = getIntent().getStringExtra(Constant.BUNDLE.KEY_VOCA_BOOK_NAME);
            } else if (wordListType.equals(WordListType.BOOKMARK)) {
                title = getString(R.string.bookmark_list);
            } else if (wordListType.equals(WordListType.DICTIONARY)) {
                title = getString(R.string.dictionary);
            } else if (wordListType.equals(WordListType.SEARCH_HISTORY)) {
                title = getString(R.string.view_title_search_history);
            }
            toolbar.setTitle(title);
        }
    }
    private void initMediaPlayer() {
        voiceRecording = new VoiceRecording(this);
    }

    private void initAdapter() {
        convVocaListAdapter = new ConvVocaListAdapter(this, vocaKnowActivity.onDoubleClickListenerOnBaseVocaKnow, onDoubleClickListener);
        convVocaListAdapter.setShowPlayAll(isShowPlayAll);
        convVocaListAdapter.setWordListType(wordListType);
        convVocaListAdapter.setBookId(bookId);

        vocaListAdapter = new VocaListAdapter(this, vocaKnowActivity.onDoubleClickListenerOnBaseVocaKnow, onDoubleClickListener);
        vocaListAdapter.setShowPlayAll(isShowPlayAll);
        vocaListAdapter.setWordListType(wordListType);

        if (UserUtil.isDebugOrAdminUser(this)) {
            ToastUtil.getInstance(this).show("bookId : " + bookId);
        }
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

    private OnDoubleClickListener onDoubleClickListener = new OnDoubleClickListener() {
        @Override
        public void onClick(View view, Object data) {
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
                        ToastUtil.getInstance(ConvVocaListActivity.this).show(R.string.toast_listen_my_voice_recording);
                        updateAfterRecording(voca);
                        playTTS.stopPlaylist();
                        if (playTTS.canPlay()) {
                            playTTS.playMyVoiceOnce(voca);
                        }

                    }
                    break;
                case R.id.icPlayAll:
                    openWordListPlayerPlayAllWordsDialog();
//                    handlePlayAll(null);
                    break;
                case R.id.ivShowRecordedVoice:
                    callAsyncTask(TYPE_DISPLAY_VOICE_RECORDED_IN_UI, null);
                    break;
                case R.id.ivEditView:
                    if (vocabookTypeCode == Constant.API_VALUE.VOCABOOK_TYPE_CODE_NONE) {
                        OpenViewUtil.openEditMeaningScreen(ConvVocaListActivity.this, voca);
                    } else {
                        OpenViewUtil.openEditMeaningVocaBookScreen(ConvVocaListActivity.this, voca, vocabookTypeCode);
                    }
                    break;
                case R.id.ivTranslate:
                    TranslateUtil.openWebTranslate(ConvVocaListActivity.this, voca.getVIVoca(), () -> {});
//                    OpenViewUtil.openExternalWebDictionary(ConvVocaListActivity.this, voca);
                    updateSearchHistory(voca);
                    break;
                case R.id.right_view:
                    resetSearchHistory(voca);
                    break;
                case R.id.ivCopy:
                    CopyTextUtil.copyToClipboardShowWhatCopied(ConvVocaListActivity.this, voca.getVIVoca());
                    break;
                case R.id.ivGptIcon:
                    openGptCustomTabFromSentence(voca);
                    break;
            }
        }

        @Override
        public void onDoubleClick(View view, Object data) {

        }
    };

    private String getVocaMeaning(IVocaFullPlayTTSItem voca) {
        if (AppFlavorUtil.isAraHangulApp())
            return voca.getVIVocaMeaning(this) + "\n" + voca.getVIMeaningDetailed(LanguageUtil.getMotherTongueLanguage(context));
        return voca.getVIVocaMeaning(this);
    }
    private void openGptCustomTabFromSentence(IVocaFullPlayTTSItem voca) {
        CopyTextUtil.openCopyConvPractice(this, (countToCopy) -> {
            StringBuilder result = new StringBuilder();
            if (countToCopy > 0) {
                result.append(getVocaMeaning(voca));
                if (countToCopy > 1) {
                    int indexPreviousVoca = vocaList.indexOf(voca);
                    if (indexPreviousVoca > 0) {
                        IVocaFullPlayTTSItem previousVoca = vocaList.get(indexPreviousVoca);
                        result.insert(0, getVocaMeaning(previousVoca) + "\n");
                    }
                }
                PromptUtil.askWhatToDoWithCopiedText(this, subDatabase, result.toString(), menu -> {
                    openGptCustomTabFromSentenceMain(menu);
                });
            } else {
                SharedPreferencesDB.getInstance(context).setConversationToStudyInGptWeb("");
                openGptCustomTabFromSentenceMain(result.toString());
            }

        });
    }

    private void openGptCustomTabFromSentenceMain(String result) {
        if (!Utils.isEmpty(result)) {
            CopyTextUtil.copyToClipboard(context, result);
        }
        ChatGptWebUtil.openUrlInCustomTabWithDialogueAndBookId(context, result, bookId, customTabActivityHelper.getSession());
    }

    private void resetSearchHistory(IVocaFullPlayTTSItem voca) {
        vocaList.remove(voca);
        subDatabase.resetSearchHistory(voca.getVIId());
    }
    private void updateSearchHistory(IVocaFullPlayTTSItem voca) {
        voca.setVISEARCH_HISTORY(DateUtils.getCurrentDateTimeFullFormat());
        subDatabase.updateWordSeachyHistory(voca);
    }
    private void setOnClickListeners() {
        binding.layoutItemContentSearch.btnResetSearch.setOnClickListener(this);
        binding.layoutItemContentSearch.etSearchKeyword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {

                return true;
//                boolean result = false;
//                if (i == EditorInfo.IME_ACTION_SEARCH) {
//                    String strKeyword = binding.layoutItemContentSearch.etSearchKeyword.getText().toString().trim();
//                    binding.layoutItemContentSearch.btnResetSearch.setVisibility(Utils.isEmpty(strKeyword) ? View.GONE : View.VISIBLE);
//                    keyword = Utils.isEmpty(strKeyword) ? "" : strKeyword;
//                    adapter.setKeyword(keyword);
//                    callAsyncTask(TYPE_INIT_DATA, null);
//                    Utils.hideSoftKeyboard(context, binding.layoutItemContentSearch.etSearchKeyword);
//                    result = true;
//                }
//                return result;
            }
        });
        binding.fabChatGPT.setOnClickListener(view -> {
            openGptCustomTab();
        });
        binding.fabPracticeConversation.setOnClickListener(view -> {
            if (wordListType == WordListType.BOOK) {
                startActivity(PracticeConversationActivity.createIntentWithBookId(ConvVocaListActivity.this, wordListType, bookId));
            } else {
                startActivity(BaseVocaFilterActivity.createIntentWithBookId(ConvVocaListActivity.this, wordListType, BaseVocaFilterActivity.NextView.PRACTICE_CONVERSATION, bookId, subDatabase.getDatabasePath()));
            }
        });
        binding.fabChatApi.setOnClickListener(view -> {
            startActivity(ChatGptActivity.createIntentWithBookId(ConvVocaListActivity.this, bookId));
        });
    }

    private void openWordListButton(IVocaFullPlayTTSItem item) {
        ExecutorHelper executorHelper = new ExecutorHelper();
        Runnable task = () -> {
            runOnUiThread(() -> Loading.showDelay(ConvVocaListActivity.this));
            List<IVocaFullPlayTTSItem> allWordListInVoca = VocaListUtil.getAllWordListOfFromDB(item.getVIVoca(), subDatabase);

            if (Utils.isEmpty(allWordListInVoca)) {
                ToastUtil.getInstance(ConvVocaListActivity.this).show(R.string.toast_no_data_to_show);
            } else {
                runOnUiThread(() -> {
                    Intent intent = VocaListActivity.createIntentByVocaTypeId(ConvVocaListActivity.this, BaseVocaList.convertToVocaTypeIdList(allWordListInVoca));
                    startActivity(intent);
                    Loading.hide();
                });
            }
        };
        executorHelper.executeTask(task);
    }

    private void openWordListButton() {
        ExecutorHelper executorHelper = new ExecutorHelper();
        Runnable task = () -> {
            runOnUiThread(() -> Loading.showDelay(ConvVocaListActivity.this));
            if (Utils.isEmpty(allWordList)) {
                allWordList = VocaListUtil.getAllWordListOfFromDB(vocaList, subDatabase);
            }
            if (Utils.isEmpty(allWordList)) {
                ToastUtil.getInstance(ConvVocaListActivity.this).show(R.string.toast_no_data_to_show);
            } else {
                runOnUiThread(() -> {
                    Intent intent = VocaListActivity.createIntentByVocaTypeId(ConvVocaListActivity.this, BaseVocaList.convertToVocaTypeIdList(allWordList));
                    startActivity(intent);
                    Loading.hide();
                });
            }
        };
        executorHelper.executeTask(task);
    }

    private void openGptCustomTab() {
        DialogUtil.openGptFromAraConvConversationView(ConvVocaListActivity.this,(menuIndex) -> {
            String dialogue = AraConvUtil.getWholeConversationWithAB(context, vocaList);
            SharedPreferencesDB.getInstance(context).setConversationToStudyInGptWeb(dialogue);
            String strToCopy = "";
            switch (menuIndex) {
                case 0:
                    strToCopy = AraConvUtil.getInstructionForRolePlayingWithGpt(ConvVocaListActivity.this, subDatabase, dialogue, true);
                    break;
                default:
                    strToCopy = AraConvUtil.getInstructionToRephraseConversationToGpt(ConvVocaListActivity.this, subDatabase, dialogue);
                    break;
            }
            CopyTextUtil.copyToClipboard(ConvVocaListActivity.this, strToCopy);
            ChatGptWebUtil.openUrlInCustomTabWithDialogueAndBookId(ConvVocaListActivity.this, strToCopy, bookId, customTabActivityHelper.getSession());
//                ChatGptWebUtil.openUrlInCustomTabWithBookId(ConvVocaListActivity.this, bookId, customTabActivityHelper.getSession());

        });
    }

    private void openConversationRawDataActivity() {
        Intent popupIntent = new Intent(context, ConversationRawDataActivity.class);
        context.startActivity(popupIntent);
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
            Voca.resetIsRecordingVoca(vocaList);
            convVocaListAdapter.notifyDataSetChanged();
        }
    }

    private void stopPlayingAdResetSpeakerIcon() {
        if (playTTS.isPlaying()) {
            playTTS.stopPlayVoca();
            Voca.resetIsPlayingTTS(vocaList);
            convVocaListAdapter.notifyDataSetChanged();
        }
    }

    private void updateSpeakerUIStatus(IVocaFullPlayTTSItem voca, boolean playing) {
        if (convVocaListAdapter.getItem(voca) != null) {
            Voca.setIsPlayingTTS(vocaList, voca, playing);
            convVocaListAdapter.notifyDataSetChanged();
        }
    }

    private void updateMicUIStatus(IVocaFullPlayTTSItem voca, boolean recording) {
        if (convVocaListAdapter.getItem(voca) != null) {
            Voca.setIsRecordingTTS(vocaList, voca, recording);
            if (recording)
                convVocaListAdapter.setIsRecordingVoca(voca);
            else
                convVocaListAdapter.setIsRecordingVoca(null);
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
        binding.layoutItemContentBookTitle.vItem.setVisibility(View.GONE);
        isShowFabButton = getIntent().getBooleanExtra(Constant.BUNDLE.KEY_SHOW_FAB_BUTTON, true);
        bookId = getIntent().getIntExtra(Constant.BUNDLE.KEY_VOCA_BOOK_ID, -1);
        show4Buttons = getIntent().getBooleanExtra(Constant.BUNDLE.KEY_SHOW_4_BUTTONS, false);
        wordListType = (WordListType) getIntent().getSerializableExtra(Constant.BUNDLE.KEY_WORDLIST_TYPE);
        vocaTypeIdList = (List<VocaTypeId>) getIntent().getSerializableExtra(Constant.BUNDLE.KEY_VOCA_TYPE_ID);
        vocabookTypeCode = getIntent().getIntExtra(Constant.BUNDLE.KEY_VOCABOOK_TYPE_CODE_SERVER, Constant.API_VALUE.VOCABOOK_TYPE_CODE_NONE);
        initAdapter();
    }

    @Override
    protected void initLayout() {
        super.initLayout();
        new FastScrollerBuilder(binding.rvInfo).build();

        binding.rvInfo.setLayoutManager(new LinearLayoutManager(context));
        binding.rvInfo.setAdapter(convVocaListAdapter);
    }


    @SuppressWarnings("unchecked")
    @Override
    protected void getData() {

    }

    private void bindData(List<IVocaFullPlayTTSItem> iVocaFullItemList) {
        convVocaListAdapter.setData(iVocaFullItemList);
        convVocaListAdapter.setShow4Buttons(show4Buttons);
        convVocaListAdapter.notifyDataSetChanged();
    }

    private void bindVoca(IVocaFullPlayTTSItem voca) {
        convVocaListAdapter.notifyItemChanged(voca);
    }


    private void refresh4Buttons() {
        show4Buttons = !show4Buttons;
        convVocaListAdapter.setShow4Buttons(show4Buttons);
        convVocaListAdapter.notifyDataSetChanged();
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
//        if ((type == TYPE_INIT_DATA) && (wordListType.equals(WordListType.CONVERSATION_RAW_DATA))) {
//            CopyTextUtil.getTextFromClipboardWithDelay(this, (text) -> {
//                vocaList = parserUserVocabookLocal(text);
//                if (!Utils.isEmpty(vocaList)) {
//
//                }
//                new CustomAsyncTask(ConvVocaListActivity.this, ConvVocaListActivity.this, data, type, true).execute();
//            });
//        } else{
//            new CustomAsyncTask(this, this, data, type, true).execute();
//        }
        new CustomAsyncTask(this, this, data, type, true).execute();
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
        switch (searchType) {
            case TYPE_INIT_DATA:
                if (wordListType.equals(WordListType.BOOK)) {
                    if (bookId > 0) {
                        vocaList = subDatabase.getVocaListByBookIdInVocaBook(bookId);
                    }
                } else if (wordListType.equals(WordListType.SERVER_VOCA_BOOK_EXPRESSION_LIST)) {
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
                } else if (wordListType.equals(WordListType.USER_VOCA_BOOK_LOCAL)) {
                    if (bookId > 0) {
                        vocaList = subDatabase.getVocaListByBookIdInUserVocaBookLocal(bookId);
                    }
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
        }
        return null;
    }

    @Override
    public void onFinishAsyncTask(int searchType, Object resultData, Object data) {
        switch (searchType) {
            case TYPE_INIT_DATA:
            case TYPE_DISPLAY_VOICE_RECORDED_IN_UI:
                vocaList = (List<IVocaFullPlayTTSItem>)resultData;
                bindData(vocaList);
                break;
            case TYPE_GENERATE_DATA_FOR_PLAY_LIST:
                PlaylistUtil.openPlaylistByBookId(ConvVocaListActivity.this, (List<IVocaFullPlayTTSItem>) resultData,  wordListType, bookId, playTTS, subDatabase.getDatabasePath());
                break;
        }
        Loading.hide();
        if (searchType == TYPE_INIT_DATA) {
            if (AppFlavorUtil.isAraHangulApp()) {
                binding.fabChatGPT.setVisibility(View.GONE);
            } else {
                binding.fabChatGPT.setVisibility(isShowFabButton ? View.VISIBLE : View.GONE);
            }
            if ((wordListType == WordListType.BOOKMARK) || (wordListType == WordListType.SERVER_VOCA_BOOK_EXPRESSION_LIST)) {
                binding.fabPracticeConversation.setVisibility(View.VISIBLE);
                binding.fabChatGPT.setVisibility(View.GONE);
            } else {
                if (AppFlavorUtil.isAraHangulApp()) {
                    binding.fabPracticeConversation.setVisibility(View.GONE);
                } else {
                    binding.fabPracticeConversation.setVisibility(isShowFabButton ? View.VISIBLE : View.GONE);
                }
            }
            if (UserUtil.isDebugOrAdminUser(this)) {
                binding.fabChatGPT.setVisibility(isShowFabButton ? View.VISIBLE : View.GONE);
                binding.fabPracticeConversation.setVisibility(isShowFabButton ? View.VISIBLE : View.GONE);
                binding.fabChatApi.setVisibility(isShowFabButton ? View.VISIBLE : View.GONE);
            } else {
                binding.fabChatApi.setVisibility(View.GONE);
            }

        }
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
        if ((new ConversationBookUtil()).isConversationBook(wordListType, bookId)) {
            PlaylistUtil.openPlaylistByBookId(ConvVocaListActivity.this, vocaList, wordListType, bookId, playTTS, subDatabase.getDatabasePath());
        } else if (bookId > 0) {
            startActivity(BaseVocaFilterActivity.createIntentWithBookId(ConvVocaListActivity.this, wordListType, BaseVocaFilterActivity.NextView.PLAY_ALL, bookId, subDatabase.getDatabasePath()));
        } else {
            startActivity(BaseVocaFilterActivity.createIntentWithVocaTypeIdList(ConvVocaListActivity.this, wordListType, BaseVocaFilterActivity.NextView.PLAY_ALL, BaseVocaList.convertToVocaTypeIdList(vocaList), subDatabase.getDatabasePath()));
        }
    }
    protected OnClickListener onVocaFilterDialogListener = (view, object) -> {
        DLog.d(getLogTag(), "onWordListPlayerPlayAllWordsDialogListener");
        if (view.getId() == R.id.btn_ok) {
//            ExecutorHelper executorHelper = new ExecutorHelper();
//            Runnable task = () -> {
                Map<String, IVocaFullPlayTTSItem> filteredData = BaseVocaList.getVocaTypeIdMapByFilter((VocaKnowGroupSelect) object, vocaList);
                List<IVocaFullPlayTTSItem> filteredDisplayItems = new ArrayList<>();
                for (IVocaFullPlayTTSItem item : vocaList) {
                    String vocaTypeId = item.getVIVocaTypeId();
                    if (filteredData.containsKey(vocaTypeId)) {
                        filteredDisplayItems.add(item);
                    } else {
                        item.setVIChecked(false);
                    }
                }
                if (filteredDisplayItems.size() > 0) {
                    PlaylistUtil.openPlaylistByBookId(ConvVocaListActivity.this, filteredDisplayItems, wordListType, bookId, playTTS, subDatabase.getDatabasePath());
                } else {
                    ToastUtil.getInstance(this).show("전체 듣기를 항목이 하나도 없습니다");
                }

//                bindData(vocaList);
//            };
//            executorHelper.executeTask(task);
        }
    };

//    protected OnClickListener onVocaFilterDialogListener = (view, object) -> {
//        DLog.d(getLogTag(), "onWordListPlayerPlayAllWordsDialogListener");
//        switch (view.getId()) {
//            case R.id.tvOpenSelectedItemsOnly:
//                callAsyncTask(TYPE_GENERATE_DATA_FOR_PLAY_LIST, (VocaKnowGroupSelect) object);
//                break;
//            case R.id.tvOpenBookmarkedItemsOnly:
//                PlaylistUtil.openPlaylistByBookId(this, vocaList.stream().filter(e -> Voca.isBookmark(e)).collect(Collectors.toList()), wordListType, bookId, playTTS);
////                openPlaylistActivity(vocaList.stream().filter(e -> Voca.isBookmark(e)).collect(Collectors.toList()));
//                break;
//            case R.id.tvOpenAllItems:
//                PlaylistUtil.openPlaylistByBookId(this, vocaList, wordListType, bookId, playTTS);
////                openPlaylistActivity(vocaList);
//                break;
//        }
//    };

//    private void openPlaylistActivity(List<? extends IVocaFullPlayTTSItem> data) {
//        Intent intent = new Intent(this, PlayListByTtsActivity.class);
//        List<VocaTypeId> vocaTypeIdList = BaseVocaList.convertToVocaTypeIdList(data);
//        intent.putExtra(Constant.BUNDLE.KEY_VOCA_PLAYLIST, (Serializable) vocaTypeIdList);
//        intent.putExtra(Constant.BUNDLE.KEY_VOCA_BOOK_ID, bookId);
////        intent.putExtra(Constant.BUNDLE.KEY_VOCA_KNOW_ACTIVITY, (Serializable) vocaKnowActivity);
//        playTTS.stopPlayVoca();
//        playTTS.setIncludeMeaning();
//        openNewScreen(intent);
//    }

    @Override
    public void onDestroy() {
        voiceRecording.destroy();
        super.onDestroy();
        if (executorHelper != null) {
            executorHelper.shutdown();
        }
    }

    @Override
    public void onStop() {
        DLog.d(getLogTag(), "onStop");
        super.onStop();
        if (!sharedPreferences.getKeepPlayingOnBackgroundMode()) {
            playTTS.stopPlayVoca();
            Voca.resetVocaList(vocaList);
            bindData(vocaList);
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
            case R.id.btnResetSearch:
                binding.layoutItemContentSearch.btnResetSearch.setVisibility(View.GONE);
                binding.layoutItemContentSearch.etSearchKeyword.setText("");
                keyword = "";
                convVocaListAdapter.setKeyword(keyword);
                vocaList = Collections.EMPTY_LIST;
                bindData(vocaList);
                break;
            case R.id.icPlayAll:
                openWordListPlayerPlayAllWordsDialog();
                break;
        }
//        if ((v.getId() == R.id.btnResetSearch)) {
//            binding.layoutItemContentSearch.btnResetSearch.setVisibility(View.GONE);
//            binding.layoutItemContentSearch.etSearchKeyword.setText("");
//            keyword = "";
//            adapter.setKeyword(keyword);
//            vocaList = Collections.EMPTY_LIST;
//            bindData(vocaList);
//        }
    }
}
