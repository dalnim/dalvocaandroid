package com.dalread.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.dalread.DalFlavor;
import com.dalread.R;
import com.dalread.adapter.PracticeConversationInChatAdapter;
import com.dalread.adapter.VocaPopupAdapter;
import com.dalread.component.AraFlowLayout;
import com.dalread.component.Toolbar;
import com.dalread.component.kprogresshud.Helper;
import com.dalread.composition.NormalTts;
import com.dalread.database.SharedPreferencesDB;
import com.dalread.databinding.ActivityPracticeConversationBinding;
import com.dalread.databinding.ConversationOptionBottomSheetDialogBinding;
import com.dalread.databinding.VocaBottomSheetDialogBinding;
import com.dalread.dialog.ConversationOptionBottomSheetDialog;
import com.dalread.dialog.PracticeConversationMenuDialog;
import com.dalread.dialog.VocaBottomSheetDialog;
import com.dalread.helper.ChooseWordHelper;
import com.dalread.helper.ConversationHelper;
import com.dalread.helper.ExecutorHelper;
import com.dalread.helper.PromptUtil;
import com.dalread.interfaces.IVocaBasicItem;
import com.dalread.interfaces.IVocaFullPlayTTSItem;
import com.dalread.listener.OnClickListener;
import com.dalread.listener.OnDoubleClickListener;
import com.dalread.model.ConversationModel;
import com.dalread.model.VocaStudyChatExam;
import com.dalread.model.VocaTypeId;
import com.dalread.model.WordListType;
import com.dalread.network.events.SuccessEvent;
import com.dalread.util.AppFlavorUtil;
import com.dalread.util.BaseCollectionUtil;
import com.dalread.util.BasePermissionUtils;
import com.dalread.util.BaseVoca;
import com.dalread.util.BaseVocaList;
import com.dalread.util.ChatGptWebUtil;
import com.dalread.util.Constant;
import com.dalread.util.CopyTextUtil;
import com.dalread.util.DLog;
import com.dalread.util.LanguageUtil;
import com.dalread.util.Loading;
import com.dalread.util.PlaylistUtil;
import com.dalread.util.PointGptUtil;
import com.dalread.util.RecyclerViewUtils;
import com.dalread.util.SpeechRecognizerUtil;
import com.dalread.util.StringUtils;
import com.dalread.util.ToastUtil;
import com.dalread.util.TranslateUtil;
import com.dalread.util.Utils;
import com.dalread.util.Voca;
import com.dalread.util.VocaKnow;

import org.greenrobot.eventbus.Subscribe;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PracticeConversationActivity extends BaseMainFolderActivity {
    private PracticeConversationInChatAdapter adapter;
    private ActivityPracticeConversationBinding binding;
    private VocaPopupAdapter vocaPopupAdapter;
    private List<IVocaFullPlayTTSItem> vocaList; //DB에서 가져옴. 이걸 modelList로 변환함.
    private List<ConversationModel> modelList; //PracticeConversationInChatAdapter에서 사용됨.
    private List<VocaStudyChatExam> quizList; //4버튼 퀴즈용.
    private List<VocaStudyChatExam> wrongQuizList;
//    private int quizPos;
    private VocaStudyChatExam currentQuiz;
    private IVocaFullPlayTTSItem currentVoca;
    private NormalTts normalTts;
    private ConversationModel conversationModelUser;
    private ConversationModel conversationModelResponse;
    private ConversationModel selectedMessage;
    private ChooseWordHelper chooseWordHelper;
    private ExecutorHelper executorHelper;
    private ConversationHelper conversationHelper;
    private SpeechRecognizerUtil speechRecognizerUtil;
    private ConversationHelper.PracticeType practiceType = ConversationHelper.PracticeType.CHOOSE_WORD;
    private int indexInVocaList = -1;
    private int bookId = -1;
    private List<VocaTypeId> vocaTypeIdList;
    private boolean isFirstOpen = true;
    private boolean isPlaySttOption = true;
    private boolean isSttPlayedAutomatically = false;
    private boolean isSpeakingGuide = false;
    private boolean isUsingSpeechRecognizer = false;
    private boolean userLineOfDialogCompleted = false;
    private boolean isTtsFromAppLineOfDialog = false;
    private boolean isDisplayTranslation = true;
    private ConversationHelper.Person user;
    private boolean isPrepareUserPart;
    private WordListType wordListType;
    private String sttResult = "";

    public static Intent createIntentWithBookId(Context context, WordListType wordListType, int bookId) {
        Intent intent = new Intent(context, PracticeConversationActivity.class);
        intent.putExtra(Constant.BUNDLE.KEY_WORDLIST_TYPE, wordListType);
        intent.putExtra(Constant.BUNDLE.KEY_VOCA_BOOK_ID, bookId);
        return intent;
    }
    public static Intent createIntentByVocaTypeId(Context context, List<VocaTypeId> list) {
        Intent intent = new Intent(context, PracticeConversationActivity.class);
        intent.putExtra(Constant.BUNDLE.KEY_VOCA_TYPE_ID, (Serializable) list);
        intent.putExtra(Constant.BUNDLE.KEY_WORDLIST_TYPE, WordListType.VOCA_TYPE_ID_LIST);
        return intent;
    }
    public static Intent createIntentByQuizList(Context context, List<VocaStudyChatExam> list) {
        Intent intent = new Intent(context, PracticeConversationActivity.class);
        intent.putExtra(Constant.BUNDLE.KEY_DATA, (Serializable) list);
        intent.putExtra(Constant.BUNDLE.KEY_WORDLIST_TYPE, WordListType.QUIZ_LIST);
        return intent;
    }
    @Override
    protected View getContentView() {
        binding = ActivityPracticeConversationBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        return view;
    }

    @Override
    protected Toolbar getToolbar() {
        return binding.header;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        user = ConversationHelper.Person.B;
        checkPointToContinue();
    }

    private void checkPointToContinue() {
        if (AppFlavorUtil.isAraPlayerApp() || AppFlavorUtil.isAraHanjaApp()) {
            getVocaList();
        } else {
            if (PointGptUtil.needToShowFullAd(this)) {
                PointGptUtil.askToWatchRewardedAd(PracticeConversationActivity.this, new PointGptUtil.OnRewardPointListener() {
                    @Override
                    public void onSuccess() {
                        getVocaList();
                    }

                    @Override
                    public void onContinue() {
                        getVocaList();
                    }

                    @Override
                    public void onFail() {
                        ToastUtil.getInstance(PracticeConversationActivity.this).show(R.string.toast_fail_to_get_point_close_and_open_app_again);
                        finish();
                    }

                    @Override
                    public void onCancel() {
                        finish();
                    }
                });
            } else {
                PointGptUtil.consumePoint(PracticeConversationActivity.this, PointGptUtil.pointForConversation);
                getVocaList();
            }
        }
    }
    private void getSharedPreferences() {
        String practiceTypeString = sharedPreferences.getConversationPracticeType();
        if (Utils.isEmpty(practiceTypeString) || (wordListType == WordListType.QUIZ_LIST))
            practiceTypeString = ConversationHelper.PracticeType.CHOOSE_WORD.name();

        practiceType = ConversationHelper.PracticeType.valueOf(practiceTypeString);

        isPlaySttOption = sharedPreferences.isPlayTtsOnConversation();
        isDisplayTranslation = sharedPreferences.isShowMeaning();
//        updateUiByPracticeType();
        if (wordListType == WordListType.QUIZ_LIST) {
            isDisplayTranslation = true;
        }
        adapter.setIsDisplayTranslation(isDisplayTranslation);
    }
    private void updateUiByPracticeType() {
        if (practiceType == ConversationHelper.PracticeType.SPEAKING) {
            binding.araFlowLayout.setVisibility(View.GONE);
            binding.llLayoutQuizAnswerButtons.llRoot.setVisibility(View.GONE);
            binding.llDialogRecordInChat.llRoot.setVisibility(View.VISIBLE);
        } else if (practiceType == ConversationHelper.PracticeType.CHOOSE_WORD) {
            if (wordListType == WordListType.QUIZ_LIST) {
                binding.araFlowLayout.setVisibility(View.GONE);
                binding.llLayoutQuizAnswerButtons.llRoot.setVisibility(View.INVISIBLE);
                binding.llDialogRecordInChat.llRoot.setVisibility(View.GONE);
            } else {
                binding.llDialogRecordInChat.llRoot.setVisibility(View.GONE);
                binding.llLayoutQuizAnswerButtons.llRoot.setVisibility(View.GONE);
                binding.araFlowLayout.setVisibility(View.VISIBLE);
            }
        }
    }

    private void updateVisible_llDialogRecordInChat(boolean toVisbile) {
        runOnUiThread(() -> {
            if (practiceType == ConversationHelper.PracticeType.SPEAKING) {
                int visibility = toVisbile ? View.VISIBLE : View.INVISIBLE;
                binding.llDialogRecordInChat.tvHeader.setVisibility(View.GONE);
                binding.llDialogRecordInChat.ivMic.setVisibility(visibility);
                binding.llDialogRecordInChat.btnSend.setVisibility(visibility);
//                binding.llDialogRecordInChat.llRoot.setVisibility(toVisbile ? View.VISIBLE : View.INVISIBLE);
            } else {
                binding.llDialogRecordInChat.llRoot.setVisibility(View.GONE);
            }
        });
    }

    private void showFirstGuideForUser() {
        adapter.addModel(conversationHelper.showFirstGuide(practiceType));
    }

    private void startPraciceDialog() {
//        updateVisible_llDialogRecordInChat(false);
        if (user == ConversationHelper.Person.B) {
            //유저가 B면 A를 먼저 앱에서 시작한다.
            prepareAppPart();
        } else {
            //유저가 A이면 유저가 먼저 시작한다.
            prepareUserPart();
        }

    }
    private void prepareAppPart() {
        if (wordListType == WordListType.QUIZ_LIST) {
            VocaStudyChatExam nextQuiz = nextLineOfDialogForQuiz(false);
            if (nextQuiz == null)
                return;
            startAppPartForQuiz(nextQuiz);
        } else {
            IVocaFullPlayTTSItem nextVoca = nextLineOfDialog(false);
            if (nextVoca == null)
                return;

            startAppPart(nextVoca);
        }
    }

    private void prepareUserPart() {
        IVocaFullPlayTTSItem nextVoca = nextLineOfDialog(true);
        if (nextVoca == null)
            return;
        startUserPart(nextVoca);
    }
//    private void prepareAppPart() {
//        String sentence = nextLineOfDialog(false);
//        if (Utils.isEmpty(sentence))
//            return;
//
//        startAppPart(sentence);
//    }
//
//    private void prepareUserPart() {
//        String sentence = nextLineOfDialog(true);
//        if (Utils.isEmpty(sentence))
//            return;
//        startUserPart(sentence);
//    }

    private void startAppPart(IVocaFullPlayTTSItem nextVoca) {
        updateVisible_llDialogRecordInChat(false);
        conversationModelResponse = conversationHelper.addAppMessage(nextVoca.getVIVoca(), nextLineOfTranslation());
        adapter.addModel(conversationModelResponse);
        if (isPlaySttOption) {
            isTtsFromAppLineOfDialog = true;
            playTts(conversationModelResponse, true, true);
        } else {
            prepareUserPart();
        }
        isPrepareUserPart = false;
    }
    private void startAppPartForQuiz(VocaStudyChatExam quiz) {
        updateVisible_llDialogRecordInChat(false);
        conversationModelResponse = conversationHelper.addAppMessage(quiz);
        adapter.addModel(conversationModelResponse);
        if (isPlaySttOption) {
            isTtsFromAppLineOfDialog = true;
            playTts(conversationModelResponse, true, true);
        } else {
            prepareUserPart();
        }
        isPrepareUserPart = false;
    }

    //먼저 빈 채팅버블을 하나 만들고 단어를 고르던지, 말을 하면 글자를 빈 채팅버블에 추가한다. 문장이 제대로 완성되거나 다음으로 가기 하면 앱차례로 넘어간다.
    private void startUserPart(IVocaFullPlayTTSItem nextVoca) {
        updateHint(nextVoca);
        if (wordListType == WordListType.QUIZ_LIST) {
            conversationModelUser = conversationHelper.addUserMessage("", "");
        } else {
            conversationModelUser = conversationHelper.addUserMessage("", nextLineOfTranslation());
        }
        adapter.addModel(conversationModelUser);
//        binding.flowLayout.post(() -> scrollToLastPosition(true));

        //단어 고르기 모드이면, 단어를 랜덤하게 만들고 유저가 선택하기를 기다린다.
        if (practiceType == ConversationHelper.PracticeType.CHOOSE_WORD) {
            if (wordListType == WordListType.QUIZ_LIST) {
                populateQuizListGridLayout();
            } else {
                populateGridLayout(nextVoca.getVIVoca());
            }
        } else if (practiceType == ConversationHelper.PracticeType.SPEAKING) {
            //말하기 모드이면 자동으로 시작한다.
            startSpeechRecognizer();
        }
        isPrepareUserPart = true;
    }

    private boolean isIncreaseIndexInVocaList(boolean isUserPart) {
        return (!((wordListType == WordListType.SERVER_VOCA_BOOK_EXPRESSION_LIST
                || wordListType == WordListType.BOOKMARK
                || wordListType == WordListType.QUIZ_LIST
                || wordListType == WordListType.VOCA_TYPE_ID_LIST)
                && (isUserPart)));
    }
    private IVocaFullPlayTTSItem nextLineOfDialog(boolean isUserPart) {
        if (isIncreaseIndexInVocaList(isUserPart)) {
            indexInVocaList++;
        }
        if (Utils.isIndexInsideRange(vocaList, indexInVocaList)) {
            currentVoca = vocaList.get(indexInVocaList);
            return currentVoca;
        } else {
            //다음 라인이 없으면 이번 라운드는 끝난거다.
            new Handler(Looper.getMainLooper()).postDelayed(() -> endOfRound(), 800);
        }
        return null;
    }

    private VocaStudyChatExam nextLineOfDialogForQuiz(boolean isUserPart) {
        if (isIncreaseIndexInVocaList(isUserPart)) {
            indexInVocaList++;
        }
        if (Utils.isIndexInsideRange(quizList, indexInVocaList)) {
            currentQuiz = quizList.get(indexInVocaList);
            return currentQuiz;
        } else {
            //다음 라인이 없으면 이번 라운드는 끝난거다.
            new Handler(Looper.getMainLooper()).postDelayed(() -> endOfRound(), 800);
        }
        return null;
    }

    private String nextLineOfTranslation() {
        if (currentVoca == null) {
            return "";
        }
        return currentVoca.getVIMeaning(LanguageUtil.getMotherTongueLanguage(this));
    }

    private void endOfRound() {
        indexInVocaList = -1;
        if (user == ConversationHelper.Person.A) {
            user = ConversationHelper.Person.B;
            ConversationModel model = conversationHelper.addGuideMessage(getString(R.string.conversation_guide_word_matching_mode));
            adapter.addModel(model);
        } else {
            user = ConversationHelper.Person.A;
            ConversationModel model = conversationHelper.addGuideMessage(getString(R.string.conversation_guide_switch_ab_person));
            adapter.addModel(model);
        }
        startPraciceDialog();
    }

    @Override
    protected void initLayout() {
        super.initLayout();
        initRecyclerView();
        initChooseWordLayout();
        initToolbar();

    }

    @Override
    protected void initListener() {
        super.initListener();
        binding.llDialogRecordInChat.ivMic.setOnClickListener( v -> {
            if (isUsingSpeechRecognizer) {
                stopSpeechRecognizer();
            } else {
                startSpeechRecognizer();
            }
        });
        binding.llDialogRecordInChat.btnSend.setOnClickListener( v -> {
            stopSpeechRecognizer();
            updateVisible_llDialogRecordInChat(false);
            useDefaultUserSentenceWhenClickNextAndSttResultEmpty();
            userLineOfDialogCompleted();
        });
        binding.llLayoutQuizAnswerButtons.btnAnswer1.setOnClickListener(v -> {
            onQuizAnswerButtonClick(1);
        });
        binding.llLayoutQuizAnswerButtons.btnAnswer2.setOnClickListener(v -> {
            onQuizAnswerButtonClick(2);
        });
        binding.llLayoutQuizAnswerButtons.btnAnswer3.setOnClickListener(v -> {
            onQuizAnswerButtonClick(3);
        });
        binding.llLayoutQuizAnswerButtons.btnAnswer4.setOnClickListener(v -> {
            onQuizAnswerButtonClick(4);
        });
    }
    private void useDefaultUserSentenceWhenClickNextAndSttResultEmpty() {
        if (Utils.isEmpty(sttResult)) {
            conversationModelUser.setMESSAGE_CONTENT(binding.tvHint.getText().toString());
            binding.rvConversation.post(() -> {
                adapter.notifyItemChanged(adapter.getItemCount() - 1);
            });
        }
        sttResult = "";
    }

    private void userLineOfDialogCompleted() {
        userLineOfDialogCompleted = true;
        if (wordListType == WordListType.QUIZ_LIST) {
            moveNextLineOfDialogAfterUserLineOfDialog();
        } else {
            if (isPlaySttOption) {
                playTts(conversationModelUser, true, true);
            } else {
                moveNextLineOfDialogAfterUserLineOfDialog();
            }
        }
    }

    private void startSpeechRecognizer() {
        if (BasePermissionUtils.checkRecordAudio(this, true)) {
            updateVisible_llDialogRecordInChat(true);
            binding.llDialogRecordInChat.tvHeader.setText(R.string.conversation_guide_speaking_stop_by_click_button);
            conversationModelUser.setMESSAGE_CONTENT("");
            binding.rvConversation.post(() -> {
                adapter.notifyItemChanged(adapter.getItemCount() - 1);
            });
            runOnUiThread(() -> {
                speechRecognizerUtil.startTranscription();
                binding.llDialogRecordInChat.ivMic.setImageResource(R.drawable.ic_mic_stop);
            });

            isUsingSpeechRecognizer = true;
        }
    }

    private void stopSpeechRecognizer() {
        binding.llDialogRecordInChat.tvHeader.setText(R.string.conversation_guide_speaking_mode);
        speechRecognizerUtil.stopTranscription();
        binding.llDialogRecordInChat.ivMic.setImageResource(R.drawable.ic_mic_off);
        isUsingSpeechRecognizer = false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        BasePermissionUtils.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
        switch (requestCode) {
            case BasePermissionUtils.REQUEST_CODE_RECORD_AUDIO:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startSpeechRecognizer();
                }
                break;
        }
    }

    private void initChooseWordLayout() {
        binding.araFlowLayout.setChildSpacing(Helper.dpToPixel(5, this));
        binding.araFlowLayout.setRowSpacing(Helper.dpToPixel(10, this));
        binding.araFlowLayout.setMaxRows(2);
        binding.araFlowLayout.setMinRows(1);
    }

    @Override
    protected void initData() {
        super.initData();
        normalTts = new NormalTts(this, onTtsCompleteListener);
        conversationHelper = new ConversationHelper(this, binding.rvConversation, adapter, subDatabase);
        executorHelper = new ExecutorHelper();
        speechRecognizerUtil = new SpeechRecognizerUtil(this, speechRecognizerUtilListener);
        bookId = getIntent().getIntExtra(Constant.BUNDLE.KEY_VOCA_BOOK_ID, -1);
        vocaTypeIdList = (List<VocaTypeId>) getIntent().getSerializableExtra(Constant.BUNDLE.KEY_VOCA_TYPE_ID);
        wordListType = (WordListType) getIntent().getSerializableExtra(Constant.BUNDLE.KEY_WORDLIST_TYPE);
        if ((wordListType == WordListType.BOOK) || (wordListType == WordListType.SERVER_VOCA_BOOK_EXPRESSION_LIST)) {
            sharedPreferences.setLastReadBookId(bookId);
        }
    }

    private void initRecyclerView() {
        modelList = new ArrayList<>();
        adapter = new PracticeConversationInChatAdapter(this, modelList, isDisplayTranslation, wordListType, binding.rvConversation, vocaKnowActivity.onDoubleClickListenerOnBaseVocaKnow, onDoubleClickListener);
        binding.rvConversation.setAdapter(adapter);
        binding.rvConversation.setLayoutManager(new LinearLayoutManager(this));
        RecyclerViewUtils.disableChangeAnimations(binding.rvConversation);
    }

    SpeechRecognizerUtil.SpeechRecognizerUtilListener speechRecognizerUtilListener = new SpeechRecognizerUtil.SpeechRecognizerUtilListener() {
        @Override
        public void onTranscriptionResults(String text) {
            sttResult = "";
            if (!Utils.isEmpty(text)) {
                sttResult = text;
                //STT로 부터 나온 결과는 소문자에 특수문자등이 없어서, 동일하면 원래 연습하는 글을 보여준다. 지금은 영어로만 검사한다. 중국어등 다른거 할때는 별도 로직 필요
                boolean isSttRight = StringUtils.areEnglishStringsEqualIgnoreCaseExceptPunctation(sttResult, binding.tvHint.getText().toString());
                if (isSttRight) {
                    updateVisible_llDialogRecordInChat(false);
                    conversationModelUser.setMESSAGE_CONTENT(binding.tvHint.getText().toString());
                } else {
                    conversationModelUser.setMESSAGE_CONTENT(sttResult);
                }
                binding.rvConversation.post(() -> {
                    adapter.notifyItemChanged(adapter.getItemCount() - 1);
                });
                stopSpeechRecognizer();
                if (isSttRight) {
                    userLineOfDialogCompleted();
                }
            }
        }

        @Override
        public void onError(String message) {
            stopSpeechRecognizer();
            binding.llDialogRecordInChat.tvHeader.setText(R.string.conversation_guide_speaking_failed_stt);
        }
    };

    protected OnDoubleClickListener onDoubleClickListener = new OnDoubleClickListener() {
        @Override
        public void onClick(View view, Object object) {
            selectedMessage = (ConversationModel) object;
            if (selectedMessage == null)
                return;
            switch (view.getId()) {
//                case R.id.ibVocaList:
//                    conversationHelper.getOneLineDifficultWordAndMeaning(selectedMessage.getMESSAGE_CONTENT(), selectedMessage);
//                    Intent intent = VocaListActivity.createIntentByVocaTypeId(PracticeConversationActivity.this, BaseVocaList.convertToVocaTypeIdList(selectedMessage.getAllWordList()));
//                    convVocaListActivityLauncher.launch(intent);
//                    break;
                case R.id.ibSpeaker:
                    playTts(selectedMessage,true, false);
                    break;
                case R.id.ibCopy:
                    CopyTextUtil.openCopyConversationModel(PracticeConversationActivity.this, selectedMessage);
                    break;
                case R.id.ivTranslate:
                    TranslateUtil.openWebTranslate(context, selectedMessage.getMESSAGE_CONTENT(), () -> {});
                    break;
                case R.id.ivGptIcon:
                    openGptCustomTabFromSentence(selectedMessage);
////                    ChatGptWebUtil.openGptCustomTabForSentence(PracticeConversationActivity.this, selectedMessage.getMESSAGE_CONTENT(), bookId);
//                    CopyTextUtil.copyToClipboard(context, selectedMessage.getMESSAGE_CONTENT());
//                    ChatGptWebUtil.openUrlInCustomTabWithDialogueAndBookId(context, selectedMessage.getMESSAGE_CONTENT(), bookId);
////                        ChatGptWebUtil.openUrlInCustomTab(context, selectedMessage.getMESSAGE_CONTENT(), false);
                    break;
            }
        }

        @Override
        public void onDoubleClick(View view, Object object) {
            stopTtsPlaying();
            selectedMessage = (ConversationModel) object;
            if (selectedMessage == null)
                return;
            switch (view.getId()) {
                case R.id.ibSpeaker:
                    playTts(selectedMessage, false, false);
                    break;
            }
        }
    };

    private void openGptCustomTabFromSentence(ConversationModel model) {
        CopyTextUtil.openCopyConvPractice(this, (countToCopy) -> {
            StringBuilder result = new StringBuilder();
            if (countToCopy > 0) {
                result.append(model.getMessageContentAndTranslation());
                if (countToCopy > 1) {
                    int prevIndex = getFirstNonGuideIndex(modelList, model.getAdapterPosition() - 1);

                    if (BaseCollectionUtil.isIndexInsideList(modelList, prevIndex)) {
                        result.insert(0, modelList.get(prevIndex).getMessageContentAndTranslation() + "\n");
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
            CopyTextUtil.copyToClipboard(context, result.toString());
        }
        ChatGptWebUtil.openUrlInCustomTabWithDialogueAndBookId(context, result.toString(), bookId, customTabActivityHelper.getSession());
    }
    private int getFirstNonGuideIndex(List<ConversationModel> modelList, int index) {
        if (index < 0 || index >= modelList.size() || !modelList.get(index).isGuide()) {
            return index;
        }
        return getFirstNonGuideIndex(modelList, index - 1);
    }


    private void playTts(ConversationModel model, boolean isPlayOnce, boolean isSttPlayedAutomatically) {
        this.isSttPlayedAutomatically = isSttPlayedAutomatically;
        if (normalTts.isReady()) {
            if (normalTts.isPlaying()) {
                resetAllPlayingIcons();
                normalTts.stop();
                //앱 파트의 대사를 강제로 멈춰 버리면, 아직 유저파트가 준비가 안되었을때를 대비한 코드.
                if (!isSttPlayedAutomatically && model.isResponse()) {
                    if (isPrepareUserPart == false) {
                        prepareUserPart();
                    }
                }
            } else {
                updateTtsSpeakerIcon(model, true);
                normalTts.speak(model.getMESSAGE_CONTENT(), isPlayOnce);
            }
        }
    }
    private void playTtsForHint(String text) {
        if (normalTts.isReady()) {
            if (normalTts.isPlaying()) {
                normalTts.stop();
            }
            binding.ivSpeaker.setImageResource(R.drawable.ic_volume_up_48dp);
            normalTts.speak(text, true);

        }
    }

    private void stopTtsPlaying() {
        if (normalTts.isPlaying()) {
            resetAllPlayingIcons();
            normalTts.stop();
        }
    }

    private void resetAllPlayingIcons() {
        new Handler(Looper.getMainLooper()).post(() -> adapter.resetAllPlayingIcons());
    }
    private void updateTtsSpeakerIcon(ConversationModel model, boolean isPlaying) {
        if (model != null) {
            model.setPlayingTts(isPlaying);
            adapter.notifyDataSetChanged();
        }
    }

    NormalTts.OnTtsCompleteListener onTtsCompleteListener = new NormalTts.OnTtsCompleteListener() {
        @Override
        public void onTtsInit() {
            DLog.d("onTtsCompleteListener", "onTtsInit");
        }

        @Override
        public void onTtsStart() {
        }

        @Override
        public void onTtsComplete() {
            resetAllPlayingIcons();
            if (isSttPlayedAutomatically) {
                if (userLineOfDialogCompleted) {
                    moveNextLineOfDialogAfterUserLineOfDialog();
                    userLineOfDialogCompleted = false;
                } else {
                    //너무 빨리 유저 파트를 시작하면, 앱이 하는 말을 STT가 인식해버릴수있다.
                    new Handler(Looper.getMainLooper()).postDelayed(() -> prepareUserPart(), 500);
                }
            }
            binding.ivSpeaker.setImageResource(R.drawable.ic_volume_up_outline_48dp);
            isSpeakingGuide = false;
        }

        @Override
        public void onTtsError() {
        }
    };

    private final ActivityResultLauncher<Intent> convVocaListActivityLauncher =
        registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK) {
                Intent data = result.getData();
                if (data != null) {
                    if (data.getBooleanExtra(Constant.INTENT.KEY_DATA, false)) {
                        //해당 쉘의 단어를 다시 한다.
                        if (selectedMessage != null) {
                            conversationHelper.updateOneLineDifficultWordAndMeaning(selectedMessage);
                        }
                    }
                }
            }
        });

    private void getVocaList() {
        Loading.show(this);
        Runnable task = () -> {
            try {
                if (wordListType == WordListType.USER_VOCA_BOOK_LOCAL) {
                    vocaList = subDatabase.getVocaListByBookIdInUserVocaBookLocal(bookId);
                } else if (wordListType == WordListType.BOOKMARK) {
                    vocaList = subDatabase.getVocaBookmarkList();
                } else if (wordListType.equals(WordListType.VOCA_TYPE_ID_LIST)) {
                    getVocaListByVocaTypeIdList();
                } else if (wordListType.equals(WordListType.QUIZ_LIST)) {
                    vocaList = (List<IVocaFullPlayTTSItem>) getIntent().getSerializableExtra(Constant.PLAYER.INTENT.KEY_DATA);
                    quizList = (List<VocaStudyChatExam>) getIntent().getSerializableExtra(Constant.PLAYER.INTENT.KEY_DATA);
                } else {
                    vocaList = subDatabase.getVocaListByBookIdInVocaBook(bookId);
                }

                Voca.resetVocaList(vocaList);
//                updateHint(getString(R.string.conversation_guide_study_this));
                showOptionBottomSheetOnMainThread();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                Loading.hide();
            }
        };
        executorHelper.executeTask(task);

    }

    private void getVocaListByVocaTypeIdList() {
        if (vocaList == null)
            vocaList = new ArrayList<>();
        vocaList.clear();
        List<IVocaFullPlayTTSItem> vocaListLocal = subDatabase.getPlayTTSVocaListByVocaIdList(vocaTypeIdList);
        Map<String, IVocaFullPlayTTSItem> vocaListMap = BaseVocaList.convertVocaTypeIdMap(vocaListLocal);
        for(VocaTypeId item : vocaTypeIdList) {
            String vocaTypeId = item.getVIVocaTypeId();
            if (vocaListMap.containsKey(vocaTypeId)) {
                vocaList.add(vocaListMap.get(vocaTypeId));
            }
        }
    }

    private void showOptionBottomSheetOnMainThread() {
        runOnUiThread(() -> {
            showOptionBottomSheet();
        });
    }

    protected void initToolbar() {
        updateToolbar(getString(R.string.view_title_practice_conversation));
    }

    private void updateToolbar(String title) {
        binding.header.setTitle(title);
    }

    private void showConversationPopup() {
        if (!Utils.isEmpty(vocaList)) {
            VocaBottomSheetDialogBinding dialogBinding = VocaBottomSheetDialogBinding.inflate(getLayoutInflater());
            VocaBottomSheetDialog bottomSheetDialog = new VocaBottomSheetDialog(context, dialogBinding, vocaList, vocaKnowActivity.onDoubleClickListenerOnBaseVocaKnow, false);
            bottomSheetDialog.show();
            vocaPopupAdapter = bottomSheetDialog.getAdapter();
        }
    }

    private void showOptionBottomSheet() {
        if (!Utils.isEmpty(vocaList)) {
            ConversationOptionBottomSheetDialogBinding dialogBinding = ConversationOptionBottomSheetDialogBinding.inflate(getLayoutInflater());
            ConversationOptionBottomSheetDialog bottomSheetDialog = new ConversationOptionBottomSheetDialog(context, dialogBinding, isFirstOpen, wordListType);
            bottomSheetDialog.setOnDismissListener((dialogInterface) -> {
                getSharedPreferences();
                if (isFirstOpen) {
                    doAtFirstOpen();
                }
            });
            //이건 맨처음 bottomSheetDialog가 떳을때 하드웨어 백버튼을 누르면 액티비티를 빠져나가는거다. 나중에 쓸지 모르니 지우지 말것.
//            bottomSheetDialog.setOnKeyListener((dialog, keyCode, event) -> {
//                if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
//                    if (isStartConversationAutomatically && (bottomSheetDialog.isShowing())) {
//                        finish();
//                        return true; // consume the event
//                    }
//                }
//                return false;
//            });

            bottomSheetDialog.show();
        }
    }

    private void doAtFirstOpen() {
        updateUiByPracticeType();
        showFirstGuideForUser();
        startPraciceDialog();
        isFirstOpen = false;
    }

    //flowLayout에 단어를 추가하고 나면 리사이클뷰가 마지막까지 안보여주는 버그를 수정한다.
    private void updateRecyclerViewPadding() {
        binding.araFlowLayout.post(() -> {
            int paddingBottom = 0; //binding.flowLayout.getVisibility() == View.VISIBLE ? binding.flowLayout.getHeight() : 0;
            binding.rvConversation.setPadding(0, 0, 0, paddingBottom);
            scrollToLastPosition(true);
        });
    }

    private void scrollToLastPosition(boolean isSmoothScroll) {
        int position = adapter.getItemCount() - 1;
        if (position >= 0) {
            if (isSmoothScroll) {
                binding.rvConversation.smoothScrollToPosition(adapter.getItemCount() - 1);
            } else {
                binding.rvConversation.scrollToPosition(adapter.getItemCount() - 1);
            }
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
        showConversationPopup();
    }

    @Override
    public void onHeaderTextRightClick() {
        openMenuDialog();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopTtsPlaying();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executorHelper != null) {
            executorHelper.shutdown();
        }
        if (normalTts != null) {
            normalTts.shutdown();
        }
        if (speechRecognizerUtil != null) {
            speechRecognizerUtil.destroy();
        }
    }

    private void updateHint(IVocaFullPlayTTSItem nextVoca) {
        runOnUiThread(() -> {
            if (AppFlavorUtil.isAraConvChineseApp()) {
                binding.tvHint.setText(BaseVoca.getVocaWithPronounce(PracticeConversationActivity.this, nextVoca)); //getVocaWithPronounce
            } else {
                binding.tvHint.setText(nextVoca.getVIVoca());
            }
        });
    }


    private void openMenuDialog() {
        final PracticeConversationMenuDialog dialog = new PracticeConversationMenuDialog(this, new OnClickListener() {
            @Override
            public void onClick(View view, Object object) {
                switch (view.getId()) {
                    case R.id.llOption:
                        showOptionBottomSheetOnMainThread();
                        break;
                    case R.id.llOpenSetting:
                        DalFlavor.openSettings(PracticeConversationActivity.this);
                        break;
                    case R.id.llShowPlayAllWords:
                        PlaylistUtil.openPlaylistByBookId(PracticeConversationActivity.this, vocaList, wordListType, bookId, playTTS, subDatabase.getDatabasePath());
                        break;
                }
            }
        });

        dialog.show();
    }

    private void populateQuizListGridLayout() {
        VocaStudyChatExam quiz = quizList.get(indexInVocaList);
        binding.llLayoutQuizAnswerButtons.btnAnswer1.setText(quiz.getAnswer1());
        binding.llLayoutQuizAnswerButtons.btnAnswer2.setText(quiz.getAnswer2());
        binding.llLayoutQuizAnswerButtons.btnAnswer3.setText(quiz.getAnswer3());
        binding.llLayoutQuizAnswerButtons.btnAnswer4.setText(quiz.getAnswer4());
        binding.llLayoutQuizAnswerButtons.llRoot.setVisibility(View.VISIBLE);
    }

    private void populateGridLayout(String sentence) {
        chooseWordHelper = new ChooseWordHelper(this, sentence);
        binding.araFlowLayout.removeAllViews();
        for (String word : chooseWordHelper.getRandomizedWords()) {
            TextView textView = buildLabel(word);
            textView.setOnClickListener(v -> onWordButtonClick(v, sentence, word));
            // 가끔 추가된게 안보일때가 있어서 main thread에서 추가한다.
            binding.araFlowLayout.post(() -> binding.araFlowLayout.addView(textView));
        }
        binding.araFlowLayout.post(() -> updateRecyclerViewPadding());
    }

    private TextView buildLabel(String text) {
        TextView textView = new TextView(this);
        textView.setMaxLines(2);
        textView.setMaxWidth((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 200, getResources().getDisplayMetrics()));
        textView.setText(text);
        textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        textView.setTextColor(ContextCompat.getColor(context, R.color.dayNightLiteWhiteColor));
        textView.setPadding((int)dpToPx(16), (int)dpToPx(8), (int)dpToPx(16), (int)dpToPx(8));
        textView.setBackgroundResource(R.drawable.bg_border_chat_word_choose);
        return textView;
    }

    private float dpToPx(float dp){
        return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }


    private void onQuizAnswerButtonClick(int selectedAnswer) {
        VocaStudyChatExam quiz = quizList.get(indexInVocaList);
        onQuizAnswerButtonClick(selectedAnswer, quiz);
    }
    private void onQuizAnswerButtonClick(int selectedAnswer, VocaStudyChatExam quiz) {
        if (quiz.getCorrectAnswerNumber() == selectedAnswer) {
            conversationModelUser.setMESSAGE_CONTENT(quiz.getQuestion());
            conversationModelUser.setMESSAGE_TRANSLATION(quiz.getCorrectAnswer());
            binding.llLayoutQuizAnswerButtons.llRoot.setVisibility(View.INVISIBLE);
            userLineOfDialogCompleted();
        }
    }

    private void onWordButtonClick(View view, String originalSentence, String word) {
        if (chooseWordHelper.isCorrectWord(word)) {
            ((AraFlowLayout) view.getParent()).removeView(view);

            String nextWord = chooseWordHelper.getNextWord();
            if (nextWord != null) {
                ((TextView) view).setText(nextWord);
                chooseWordHelper.getRandomizedWords().add(nextWord);
                view.setVisibility(View.VISIBLE);
            } else {
                view.setVisibility(View.INVISIBLE);
            }

            String prevSentence = conversationModelUser.getMESSAGE_CONTENT();
            conversationModelUser.setMESSAGE_CONTENT(chooseWordHelper.makeSentenceFromWord(prevSentence, word));
            binding.rvConversation.post(() -> {
                adapter.notifyItemChanged(adapter.getItemCount() - 1);
                scrollToLastPosition(true);
            });
            if (chooseWordHelper.isCompleted()) {
                conversationModelUser.setMESSAGE_CONTENT(originalSentence);
                userLineOfDialogCompleted();
            }
        }
    }

    private void moveNextLineOfDialogAfterUserLineOfDialog() {
        userLineOfDialogCompleted = false;
        new Handler(Looper.getMainLooper()).postDelayed(() -> prepareAppPart(), 800);
    }


//    @Override
//    public void onBackPressed() {
//        ConfirmationDialog confirmationDialog = ConfirmationDialog.createWithYesNo(this, R.string.msg_confirm_exit, onConfirmListener);
//        confirmationDialog.show();
//    }
//    private ConfirmationDialog.OnDialogClickListener onConfirmListener = new ConfirmationDialog.OnDialogClickListener() {
//
//        @Override
//        public void onPositive(DialogInterface dialog) {
//            dialog.dismiss();
//            finish();
//        }
//
//        @Override
//        public void onNegative(DialogInterface dialog) {
//            dialog.dismiss();
//        }
//    };

    @Subscribe
    public void onEvent(SuccessEvent successEvent) {
        switch (successEvent.getScreen()) {
            case COMMON_VOCA_DATA:
                IVocaFullPlayTTSItem item = (IVocaFullPlayTTSItem) successEvent.getModel();
                switch (successEvent.getEventType()) {
                    case COMMON_VOCA_KNOW_CHANGED:
                    case COMMON_VOCA_KNOWPRONOUNCE_CHANGED:
                    case COMMON_VOCA_BOOKMKARK_CHANGED:
                        refreshSearchViewAdapterDataChanged(item);
                        break;
                }
                break;
        }
    }

    private void refreshSearchViewAdapterDataChanged(IVocaBasicItem voca) {
        if (Utils.isEmpty(vocaList))
            return;


        for (IVocaBasicItem item : modelList) {
            if (Voca.isSameVoca(item, voca)) {
                int newBookmark = voca.getVIBookmark();
                int newVocaKnow = voca.getVIVocaKnow();
                int newVocaKnowPronounce = VocaKnow.getVocaKnowPronounceByVocaKnow(newVocaKnow);
                item.setVIBookmark(newBookmark);
                item.setVIVocaKnow(newVocaKnow);
                item.setVIVocaKnowPronounce(newVocaKnowPronounce);
                break;
            }
        }
        for (IVocaBasicItem item : vocaList) {
            if (Voca.isSameVoca(item, voca)) {
                int newBookmark = voca.getVIBookmark();
                int newVocaKnow = voca.getVIVocaKnow();
                int newVocaKnowPronounce = VocaKnow.getVocaKnowPronounceByVocaKnow(newVocaKnow);
                item.setVIBookmark(newBookmark);
                item.setVIVocaKnow(newVocaKnow);
                item.setVIVocaKnowPronounce(newVocaKnowPronounce);
                break;
            }
        }
        //modelList를 변경하였으므로 adapter를 갱신해준다.
        adapter.notifyDataSetChanged();

        if (vocaPopupAdapter != null) {
            vocaPopupAdapter.setVocaList(vocaList);
            vocaPopupAdapter.notifyDataSetChanged();
        }
    }
}
