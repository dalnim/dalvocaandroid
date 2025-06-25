package com.dalread.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.dalread.AraConvApplication;
import com.dalread.R;
import com.dalread.adapter.ChatGptAdapter;
import com.dalread.base.EnumLanguage;
import com.dalread.composition.NormalTts;
import com.dalread.database.GptSystemInstructions;
import com.dalread.databinding.ActivityChatGptBinding;
import com.dalread.databinding.VocaBottomSheetDialogBinding;
import com.dalread.dialog.VocaBottomSheetDialog;
import com.dalread.dialog.VocaWritingPracticeDialog;
import com.dalread.dialog.YesNoDialog;
import com.dalread.helper.ChatGptHelper;
import com.dalread.helper.ExecutorHelper;
import com.dalread.helper.GptDialogueHelper;
import com.dalread.helper.PromptUtil;
import com.dalread.interfaces.IVocaFullPlayTTSItem;
import com.dalread.listener.OnClickListener;
import com.dalread.listener.OnDoubleClickListener;
import com.dalread.listener.OnYesNoClickListener;
import com.dalread.model.GPT_CHAT_MESSAGE;
import com.dalread.network.ChatCompletionRequest;
import com.dalread.network.ChatCompletionResponse;
import com.dalread.network.CompletionsRequest;
import com.dalread.network.DalApiListener;
import com.dalread.network.GptRequestBuilder;
import com.dalread.network.GptStudyMode;
import com.dalread.network.GptUserOption;
import com.dalread.network.events.SuccessEvent;
import com.dalread.util.AraConvUtil;
import com.dalread.util.BaseMobileAd;
import com.dalread.util.BaseVocaList;
import com.dalread.util.Constant;
import com.dalread.util.CopyTextUtil;
import com.dalread.util.DLog;
import com.dalread.util.LanguageUtil;
import com.dalread.util.Loading;
import com.dalread.util.PointGptUtil;
import com.dalread.util.RecyclerViewUtils;
import com.dalread.util.StringUtils;
import com.dalread.util.ToastUtil;
import com.dalread.util.Utils;
import com.dalread.util.Voca;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.rewarded.RewardItem;

import org.apache.commons.lang3.SerializationUtils;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ChatGptActivity extends BaseConvActivity {
    private List<IVocaFullPlayTTSItem> originalVocaListToStudy;
    private enum LearningStepVoca {
        CHECK_VOCA_KNOW,
        WRITE_PRACTICE,
        SPEAK_PRACTICE
    }
    private ChatGptAdapter adapter;
    private ActivityChatGptBinding binding;
    private List<GPT_CHAT_MESSAGE> gptChatMessages;
    private final int countToGetMessages = 10;
    private int pageToGetMessages = 1;
    private int lastMessageId = 0;
    private List<IVocaFullPlayTTSItem> vocaList;
    private GptDialogueHelper dialogueHelper;
    private NormalTts normalTts;
    private GPT_CHAT_MESSAGE selectedMessage;
    private boolean isPlayTtsOnce;
    private boolean suggestionsInKeyboardEnabled;
    private int bookId = -1;
    private int vocaId = -1; //현재는 VocaType이 문장만을 한다.
    private boolean isSendingPromptToGpt = false;
    private ExecutorHelper executorHelper;
    private ChatGptHelper chatGptHelper;
    private GptUserOption gptOptions = GptUserOption.BEGINNER;
    private boolean isFirstOpen = true;
    private List<IVocaFullPlayTTSItem> vocaListToStudy;
//    private IVocaFullPlayTTSItem vocaToStudy;
    private LearningStepVoca currentStep = LearningStepVoca.CHECK_VOCA_KNOW;
    private GptStudyMode gptStudyMode = GptStudyMode.FREE_TALKING;
    private String lastUserPrompt = "";
    private String lastAppPrompt = "";
    private boolean isShowInstuduction = true;
    public static Intent createIntent(Context context) {
        Intent intent = new Intent(context, ChatGptActivity.class);
        return intent;
    }
    public static Intent createIntentWithBookId(Context context,  int bookId) {
        Intent intent = new Intent(context, ChatGptActivity.class);
        intent.putExtra(Constant.BUNDLE.KEY_VOCA_BOOK_ID, bookId);
        return intent;
    }
    public static Intent createIntentWithVocaId(Context context, int vocaId) {
        Intent intent = new Intent(context, ChatGptActivity.class);
        intent.putExtra(Constant.BUNDLE.KEY_VOCA_ID, vocaId);
        return intent;
    }
    @Override
    protected View getContentView() {
        binding = ActivityChatGptBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        return view;
    }

    @Override
    protected int getContentViewId() {
        return 0;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getVocaList();
        //이줄이 없으면 키보드가 채팅창을 가린다. 그리고 여기는 smoothScrollToPosition을 안쓰는게 좀더 자연스럽니다. (빠릿하다)
        binding.rvChatGpt.addOnLayoutChangeListener((v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom) -> scrollToLastPosition(false));
    }
    @Override
    protected void initLayout() {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        binding.sendButton.setOnClickListener(view -> sendMessage(binding.promptEditText.getText().toString()));
        binding.promptEditText.setOnEditorActionListener((textView, actionId, keyEvent) -> {
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                sendMessage(textView.getText().toString());
                return true;
            }
            return false;
        });
//        binding.promptEditText.setOnFocusChangeListener((v, hasFocus) -> {
//            if (hasFocus) {
//                hideBottomActionExtraButtons();
//            }
//        });
        binding.promptEditText.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                hideBottomActionExtraButtons();
            }
            return false;
        });

        binding.guideButton.setOnClickListener(v -> {
            String prompt = "Hi, How may I help you?";
            GptDialogueHelper.Conversation conversation = new GptDialogueHelper.Conversation(GptDialogueHelper.Person.A, prompt);
            dialogueHelper.addMessage(conversation);
            StringBuilder extraText = new StringBuilder();
            extraText.append(GptSystemInstructions.START_NEW_CONVERSATION_1);
            extraText.append(GptSystemInstructions.START_NEW_CONVERSATION_2);
            extraText.append(GptSystemInstructions.START_NEW_CONVERSATION_3);
            extraText.append(GptSystemInstructions.START_NEW_CONVERSATION_4);
            extraText.append("Don't add 'A' or 'B' in front of text\n");
//            chatGptHelper.addGptPrompt(prompt);
            ChatCompletionRequest request = chatGptHelper.createConversationGptRequest(extraText + conversation.getTextWithPerson() + "\n" + conversation.nextPersonWithbrackets(), gptOptions, true);
            adapter.addMessage(chatGptHelper.createGptChatMessageForPrompt(""));
            sendMessageToChatGpt(request);
        });
        binding.ibMoreCommand.setOnClickListener(v -> {
            toggleExtraButtons();
        });
        binding.bottomActionExtraButtons.btnGptAnswerInStudyLang.setOnClickListener(answerInStudyLangListener);
        binding.bottomActionExtraButtons.btnGptAnswerInMotherTongue.setOnClickListener(answerInMotherTongueListener);
        binding.bottomActionExtraButtons.btnGptVocaKnowPopup.setOnClickListener( v -> {
            hideBottomActionExtraButtons();
            currentStep = LearningStepVoca.CHECK_VOCA_KNOW;
            showPopupToStudy();
        });
        binding.bottomActionExtraButtons.btnGpt1.setOnClickListener(v -> CopyTextUtil.openCopyConversationDialog(context, vocaListToStudy, false));
        binding.bottomActionExtraButtons.btnGpt2.setOnClickListener(v -> showPopupToStudy());
        binding.bottomActionExtraButtons.btnGpt3.setOnClickListener(v -> increaseLearningStep());
        initToolbar();
    }
    @Override
    protected void initData() {
        initRecyclerView();
        normalTts = new NormalTts(this, onTtsCompleteListener);

        chatGptHelper = new ChatGptHelper(this, binding.rvChatGpt, adapter, subDatabase);
        getGptOption();
//        initChatGpt();
        displayPreviousMessages();
        executorHelper = new ExecutorHelper();
        dialogueHelper = new GptDialogueHelper();
        bookId = getIntent().getIntExtra(Constant.BUNDLE.KEY_VOCA_BOOK_ID, -1);
        vocaId = getIntent().getIntExtra(Constant.BUNDLE.KEY_VOCA_ID, -1);
        if (bookId > 0) {
            gptStudyMode = GptStudyMode.DIALOGUE;
        } else if (vocaId > 0) {
            gptStudyMode = GptStudyMode.VOCA;
        } else {
            gptStudyMode = GptStudyMode.FREE_TALKING;
        }
        BaseMobileAd.loadRewardedAd(this);
    }

    private void displayPreviousMessages() {
        gptChatMessages = subDatabase.getGptChatMessages(pageToGetMessages, countToGetMessages);
        gptChatMessages.sort(Comparator.comparing(GPT_CHAT_MESSAGE::getID));
        adapter.addMessages(gptChatMessages);
        if (!gptChatMessages.isEmpty()) {
            lastMessageId = gptChatMessages.get(gptChatMessages.size() - 1).getID();
        }
        scrollToLastPosition();
    }

    private void initRecyclerView() {
        adapter = new ChatGptAdapter(this, onDoubleClickListener);
        binding.rvChatGpt.setAdapter(adapter);
        binding.rvChatGpt.setLayoutManager(new LinearLayoutManager(this));
        RecyclerViewUtils.disableChangeAnimations(binding.rvChatGpt);
    }
//
//    //ChatGptHelper에서 GPT응답을 단어단위로 업데이트 할때 깜빡거려서 방지하기 위해서 애니메이션 효과를 없앰.
//    private void preventBlinkingAdapter() {
//        RecyclerView.ItemAnimator itemAnimator = binding.rvChatGpt.getItemAnimator();
//        if (itemAnimator instanceof SimpleItemAnimator) {
//            // If the ItemAnimator is a SimpleItemAnimator, disable change animations
//            ((SimpleItemAnimator) itemAnimator).setSupportsChangeAnimations(false);
//        }
//    }

    protected OnDoubleClickListener onDoubleClickListener = new OnDoubleClickListener() {
        @Override
        public void onClick(View view, Object object) {
            clearPreviousGtpMessageSpeaking((GPT_CHAT_MESSAGE) object);
            selectedMessage = (GPT_CHAT_MESSAGE) object;
            if (selectedMessage == null)
                return;
            switch (view.getId()) {
                case R.id.ibVocaList:
                    List<IVocaFullPlayTTSItem> difficultWordList = selectedMessage.getAllWordList();
                    Intent intent = ConvVocaListActivity.createIntentByVocaTypeId(ChatGptActivity.this, BaseVocaList.convertToVocaTypeIdList(difficultWordList));
                    convVocaListActivityLauncher.launch(intent);
                    break;
                case R.id.ibSpeaker:
                    if (normalTts.isReady()) {
                        if (normalTts.isPlaying()) {
                            updateTtsSpeaker(false);
                            normalTts.stop();
                        } else {
                            updateTtsSpeaker(true);
                            normalTts.speak(selectedMessage.getMESSAGE_CONTENT(), true);
                        }
                    }
                    break;
                case R.id.ibCopy:
                    CopyTextUtil.copyToClipboardShowWhatCopied(ChatGptActivity.this, selectedMessage.getMESSAGE_CONTENT());
                    break;
            }
        }

        private void clearPreviousGtpMessageSpeaking(GPT_CHAT_MESSAGE object) {
            if (selectedMessage != null) {
                GPT_CHAT_MESSAGE gptMessageTemp = object;
                if (selectedMessage != gptMessageTemp) {
                    stopTtsPlaying();
                }
            }
        }

        @Override
        public void onDoubleClick(View view, Object object) {
            clearPreviousGtpMessageSpeaking((GPT_CHAT_MESSAGE) object);
            selectedMessage = (GPT_CHAT_MESSAGE) object;
            if (selectedMessage == null)
                return;
            switch (view.getId()) {
                case R.id.ibSpeaker:
                    if (normalTts.isReady()) {
                        if (normalTts.isPlaying()) {
                            updateTtsSpeaker(false);
                            normalTts.stop();
                        } else {
                            updateTtsSpeaker(true);
                            normalTts.speak(selectedMessage.getMESSAGE_CONTENT(), false);
                        }
                    }
                    break;
            }
        }
    };

    private void stopTtsPlaying() {
        if (normalTts.isPlaying()) {
            updateTtsSpeaker(false);
            normalTts.stop();
        }
    }

    private void updateTtsSpeaker(boolean isPlaying) {
        if ((selectedMessage != null) && (adapter != null)) {
            selectedMessage.setPlayingTts(isPlaying);
            adapter.notifyItemChanged(selectedMessage.getAdapterPosition());
        }
    }

    NormalTts.OnTtsCompleteListener onTtsCompleteListener = new NormalTts.OnTtsCompleteListener() {
        @Override
        public void onTtsInit() {
            DLog.d("onTtsCompleteListener", "onTtsInit");
            updateTtsSpeaker(false);
        }

        @Override
        public void onTtsStart() {
            updateTtsSpeaker(true);
            DLog.d("onTtsCompleteListener", "onTtsStart");
        }

        @Override
        public void onTtsComplete() {
            updateTtsSpeaker(false);
            DLog.d("onTtsCompleteListener", "onTtsComplete");
        }

        @Override
        public void onTtsError() {
            DLog.d("onTtsCompleteListener", "onTtsInit");
            updateTtsSpeaker(false);
        }
    };
//    private void updateSpeakerUIStatus(IVocaFullPlayTTSItem voca, boolean playing) {
//        if (adapter.getItem(voca) != null) {
//            Voca.setIsPlayingTTS(vocaList, voca, playing);
//            adapter.notifyDataSetChanged();
//        }
//    }
    private final ActivityResultLauncher<Intent> convVocaListActivityLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null) {
                        if (data.getBooleanExtra(Constant.INTENT.KEY_DATA, false)) {
                            //해당 쉘의 단어를 다시 한다.
                            if (selectedMessage != null) {
                                chatGptHelper.updateOneLineDifficultWordAndMeaning(selectedMessage);
                            }
                        }
                    }
                }
            });
    private void getGptOption() {
        if (Utils.isEmpty(sharedPreferences.getSettingMyLanguageLevel())) {
            AraConvUtil.openLanguageLevelDialog(this, gptOptions);
        } else {
            gptOptions = GptUserOption.getGptOptionsByLevel(sharedPreferences.getSettingMyLanguageLevel());
        }
        //일단은 고급 사용자도 키보드에 단어 힌트를 보이게 한다.
//        EditTextUtils.showOrHideSuggestionsInKeyboardForMultiLine(binding.promptEditText, gptOptions.isShowSuggestonInKeyboard());
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

                        break;
                }
                break;
        }
    }

//    private void showGptOptionsDialog() {
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setTitle("Choose your English proficiency level")
//            .setItems(R.array.gpt_options, new DialogInterface.OnClickListener() {
//                public void onClick(DialogInterface dialog, int which) {
//                    switch (which) {
//                        case 0:
//                            gptOptions = GptOptions.BEGINNER;
//                            break;
//                        case 1:
//                            gptOptions = GptOptions.INTERMEDIATE;
//                            break;
//                        case 2:
//                            gptOptions = GptOptions.ADVANCED;
//                            break;
//                    }
//                    sharedPreferences.setGptOption(gptOptions.getStudentLangLevel());
//                }
//            });
//        builder.create().show();
//    }

    private void getVocaList() {
        if ((bookId > 0) || (vocaId > 0)) {
            Loading.show(this);
            Runnable task = new Runnable() {
                @Override
                public void run() {
                    try {
                        if (bookId > 0) {
                            vocaList = subDatabase.getVocaListByBookIdInVocaBook(bookId);
                        } else if (vocaId > 0) {
                            vocaList = subDatabase.getVocaListByVocaId(vocaId);
                        }
                        if (Utils.isEmpty(vocaList)) {
                            updateTvGuideText("학습할 리스트가 없습니다. 챗GPT와 자유대화를 하세요.");
                        } else {
                            Voca.resetVocaList(vocaList);
                            updateTvGuideText("다음을 공부합니다. 궁금한게 있으면 아무때나 챗GPT에게 물어보세요.");
                            showPopupToStudy();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        Loading.hide();
                    }
                }
            };
            executorHelper.executeTask(task);
        }
    }

    private void showPopupToStudy() {
        if (gptStudyMode.isVoca()) {
            switch (currentStep) {
                case CHECK_VOCA_KNOW:
                    showPopupToCheckVocaKnow();
                    break;
                case WRITE_PRACTICE:
                    showWritingPracticeDialog();
                    break;
//            case SPEAK_PRACTICE:
//                showPopupToCheckVocaKnow();
//                break;
            }
        } else if (gptStudyMode.isDialogue()) {
            runOnUiThread(() -> {
                showDialoguePopup();
            });
        }
    }
    private void showPopupToCheckVocaKnow() {
        vocaListToStudy = BaseVocaList.getRandomUnknown(vocaList, 1);
        runOnUiThread(() -> {
            showVocaPopup();
        });
    }

    protected void initToolbar() {
        toolbar.setTitle(R.string.view_title_study_with_chat_gpt);
    }

    private void showPointGpt() {
        toolbar.setTitle("Point : " + PointGptUtil.getPoint(this));
    }
    private void sendMessage(String prompt) {
        if (isSendingPromptToGpt) {
            ((AraConvApplication) application).getAraHanjaApiImpl().cancelSendMessageToChatCompletion();
            binding.sendButton.setImageResource(R.drawable.ic_send);
            isSendingPromptToGpt = false;
            binding.progressBar.setVisibility(View.GONE);
            ToastUtil.getInstance(ChatGptActivity.this).show(R.string.toast_message_cancel_sending_message_to_gpt);
        } else {
            if (PointGptUtil.needToShowFullAd(this)) {
                askToWatchRewardedAd(prompt);
            } else {
                showPointGpt();
                prompt = StringUtils.trimIncludeWhitespace(prompt);
                if (!TextUtils.isEmpty(prompt)) {
                    chatGptHelper.addGptPrompt(prompt);
                    sendMessageToChatGPT(prompt);
                    scrollToLastPosition();
                }
            }
        }
    }

    private void askToWatchRewardedAd(String prompt) {
        final YesNoDialog dialog = new YesNoDialog(context,
                R.string.warning,
                R.string.msg_warning_need_point_for_gpt, null,
                new OnYesNoClickListener() {
                    @Override
                    public void onYesClick(View view, Object object) {
                        showRewardedAd(prompt);
                    }

                    @Override
                    public void onNoClick(View view, Object object) {

                    }
                });
        dialog.show();
    }

    private void showRewardedAd(String prompt) {
        BaseMobileAd.showRewardedAd(this, new OnUserEarnedRewardListener() {
                    @Override
                    public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                        PointGptUtil.addPointGptAfterFullAd(ChatGptActivity.this, rewardItem.getAmount());
                        sendMessage(prompt);
                    }
                });
//                BaseMobileAd.showRewardedAd(this, new RewardedAdLoadCallback() {
//                    @Override
//                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
//                        super.onAdFailedToLoad(loadAdError);
//                        BaseMobileAd.loadRewardedAd(ChatGptActivity.this);
//                    }
//
//                    @Override
//                    public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
//                        super.onAdLoaded(rewardedAd);
//                        RewardItem item = rewardedAd.getRewardItem();
//                        PointGptUtil.addPointGptAfterFullAd(ChatGptActivity.this, item.getAmount());
//                        sendMessage(prompt);
//                    }
//                });
    }

    private void updateLastPrompt(String prompt) {
        lastUserPrompt = prompt;
    }

    private void sendMessageToChatGPT(String prompt) {
        ChatCompletionRequest request = null;
        if (isFirstOpen) {
            request = GptRequestBuilder.sendPromptFirstAppUse(prompt, ChatCompletionRequest.RequestResponseType.REQUEST, gptOptions, getStudyText());
        } else {
            request = GptRequestBuilder.sendNormalPromptToGpt(prompt, ChatCompletionRequest.RequestResponseType.REQUEST, gptOptions, GptSystemInstructions.CORRECTION);
        }

        updateLastPrompt(prompt);
        isFirstOpen = false;
        sendMessageToChatGpt(request);
    }

    private void getCompletion(CompletionsRequest request) {
//        binding.progressBar.setVisibility(View.VISIBLE);
//        GptMessage gptMessage = new GptMessage("...", GptMessage.MessageType.RESPONSE);
//        adapter.addMessage(gptMessage);
//        scrollToLastPostion();
//        adapter.showProgressBarOnResponse(); //이건 제대로 안된다. notifyItemInserted가 끝나기 전에 이 코드가 불리니, gptResponseViewHolder이 null이 된다.
//        ((AraConvApplication) application).getAraHanjaApiImpl().getCompletion(request, new DalApiListener<String>() {
//            @Override
//            public void onSuccess(String response) {
//                handleResponse(true, response);
////                handleResponse(false, "");
//            }
//
//            @Override
//            public void onFailure(String error) {
//                handleResponse(false, error);
//            }
//
//            private void handleResponse(boolean isGptResponse, String response) {
//                binding.promptEditText.setText("");
//                String prompt = "";
//                if (isGptResponse) {
//                    prompt = "궁금한건 챗GPT에 물어보세요. 없으면 옆의 버튼을 누르세요. ";
//                    chatGptHelper.addGptResponse(response, gptMessage, gptOptions);
//                } else {
//                    String promptErr = "GPT가 응답하지 않습니다. 너무 많은 사용자가 사용중입니다. " + response;
//                    chatGptHelper.addAppResponse(promptErr, gptMessage, gptOptions);
//                    prompt = "옆의 버튼을 누르세요. ";
//                }
//
//                switch(currentStep) {
//                    case CHECK_VOCA_KNOW:
//                        updateTvGuideText(prompt + "쓰기 연습을 해봅시다.");
//                        break;
//                    case WRITE_PRACTICE:
//                        updateTvGuideText(prompt + "말하기 연습을 해봅시다.");
//                        break;
//                    case SPEAK_PRACTICE:
//                        updateTvGuideText(prompt + "다른 문장으로 넘어갑니다.");
//                        break;
//                }
//                increaseLearningStep();
//                scrollToLastPostion();
//                adapter.hideProgressBarOnResponse();
//                binding.progressBar.setVisibility(View.GONE);
//                if (isStepToCheckVocaKnow()) {
//                    showVocaPopup();
//                }
//            }
//        });

    }
    private void sendMessageToChatGpt(ChatCompletionRequest request) {
        binding.progressBar.setVisibility(View.VISIBLE);
        GPT_CHAT_MESSAGE message = chatGptHelper.createGptChatMessageForResponse("");
//        message.setTYPE(ChatGPTRequest.MessageType.PROMPT.name());
//        GptMessage gptMessage = new GptMessage("", GptMessage.MessageType.RESPONSE);
        adapter.addMessage(message);
        scrollToLastPosition();
        subDatabase.insertGptRequest(request);
//        adapter.showProgressBarOnResponse(); //이건 제대로 안된다. notifyItemInserted가 끝나기 전에 이 코드가 불리니, gptResponseViewHolder이 null이 된다.
        ((AraConvApplication) application).getAraHanjaApiImpl().sendMessageToChatCompletion(request, new DalApiListener<ChatCompletionResponse>() {
            @Override
            public void onSuccess(ChatCompletionResponse response) {
                StringBuilder sb = new StringBuilder();
                if (response != null) {
                    List<ChatCompletionResponse.Choice> choices = response.getChoices();
                    if (choices != null && !choices.isEmpty()) {
                        for (ChatCompletionResponse.Choice choice : choices) {
                            sb.append(choice.getMessage().getContent());
                        }
                    }
                    if (!request.isMessageTypeGuide()) {
                        subDatabase.insertGptResponse(response, sb.toString());
                    }
                }
                handleResponse(true, sb.toString());
                PointGptUtil.consumePointByToken(ChatGptActivity.this, response.getUsage().getTotalTokens());
//                handleResponse(false, "");
            }

            @Override
            public void onFailure(String error) {
                handleResponse(false, error);
            }

            private void handleResponse(boolean isSuccess, String response) {
                binding.promptEditText.setText("");
                String prompt = "";
                if (isSuccess) {
                    prompt = "궁금한건 챗GPT에 물어보세요. 없으면 옆의 버튼을 누르세요. ";
                    chatGptHelper.addGptResponse(response, message, gptOptions);
                } else {
                    String promptErr = "GPT가 응답하지 않습니다. 너무 많은 사용자가 사용중입니다. " + response;
                    chatGptHelper.addAppResponse(promptErr, message, gptOptions);
                    prompt = "옆의 버튼을 누르세요. ";
                }

                switch (currentStep) {
                    case CHECK_VOCA_KNOW:
                        updateTvGuideText(prompt + "쓰기 연습을 해봅시다.");
                        break;
                    case WRITE_PRACTICE:
                        updateTvGuideText(prompt + "말하기 연습을 해봅시다.");
                        break;
                    case SPEAK_PRACTICE:
                        updateTvGuideText(prompt + "다른 문장으로 넘어갑니다.");
                        break;
                }
                increaseLearningStep();
                scrollToLastPosition();
//                adapter.hideProgressBarOnResponse();
                binding.progressBar.setVisibility(View.GONE);
                if (isStepToCheckVocaKnow()) {
                    showVocaPopup();
                }
                binding.sendButton.setImageResource(R.drawable.ic_send);
                isSendingPromptToGpt = false;

            }
        });
        binding.sendButton.setImageResource(R.drawable.ic_left);
        isSendingPromptToGpt = true;

    }

    private void initChatGpt() {
        ChatCompletionRequest request = chatGptHelper.initChatGpt(gptOptions, gptStudyMode);
        subDatabase.insertGptRequest(request);
        ((AraConvApplication) application).getAraHanjaApiImpl().sendMessageToChatCompletion(request, new DalApiListener<ChatCompletionResponse>() {
            @Override
            public void onSuccess(ChatCompletionResponse response) {
                StringBuilder sb = new StringBuilder();
                if (response != null) {
                    List<ChatCompletionResponse.Choice> choices = response.getChoices();
                    if (choices != null && !choices.isEmpty()) {
                        for (ChatCompletionResponse.Choice choice : choices) {
                            sb.append(choice.getMessage().getContent());
                        }
                    }
                    if (!request.isMessageTypeGuide()) {
                        subDatabase.insertGptResponse(response, sb.toString());
                    }
                }
                handleResponse(true, sb.toString());
//                handleResponse(false, "");
            }

            @Override
            public void onFailure(String error) {
                handleResponse(false, error);
            }

            private void handleResponse(boolean isSuccess, String response) {
                if (gptStudyMode.isDialogue()) {
                    String prompt = BaseVocaList.getVocaListInStringWithEnter(vocaList);
                    String promptForGpt = prompt + "\n I'll give you dialogue don't answer me with full whole dialogue back. A is person A. You're A. Say A sentence once and wait for my response.";
                    ChatCompletionRequest request = chatGptHelper.createStudyDialogGptRequest(promptForGpt, gptOptions);
                    adapter.addMessage(chatGptHelper.createGptChatMessageForPrompt(prompt + "\n 를 롤플레잉 연습합니다. 챗GPT가 말하면 대답하세요."));
                    sendMessageToChatGpt(request);
                }
            }
        });
    }


    public void increaseLearningStep() {
        switch (currentStep) {
            case CHECK_VOCA_KNOW:
                currentStep = LearningStepVoca.WRITE_PRACTICE;
                break;
            case WRITE_PRACTICE:
                currentStep = LearningStepVoca.SPEAK_PRACTICE;
                break;
//            case SPEAK_PRACTICE:
//                currentStep = LearningStep.CHECK_VOCA_KNOW;
//                break;
            default:
                currentStep = currentStep;
        }
        toolbar.setTitle(currentStep.toString());
    }

    private boolean isStepToCheckVocaKnow() {
        return currentStep == LearningStepVoca.CHECK_VOCA_KNOW;
    }

    private void showDialoguePopup() {
        if (!vocaList.isEmpty()) {
            VocaBottomSheetDialogBinding dialogBinding = VocaBottomSheetDialogBinding.inflate(getLayoutInflater());
            VocaBottomSheetDialog bottomSheetDialog = new VocaBottomSheetDialog(context, dialogBinding, vocaList, vocaKnowActivity.onDoubleClickListenerOnBaseVocaKnow, false);
            bottomSheetDialog.show();
        }
    }
    private void showVocaPopup() {
        if (!isStepToCheckVocaKnow() || Utils.isEmpty(vocaListToStudy)) {
            return;
        }
        VocaBottomSheetDialogBinding dialogBinding = VocaBottomSheetDialogBinding.inflate(getLayoutInflater());
        originalVocaListToStudy = SerializationUtils.clone(new ArrayList<>(vocaListToStudy));
        VocaBottomSheetDialog bottomSheetDialog = new VocaBottomSheetDialog(context, dialogBinding, vocaList, vocaKnowActivity.onDoubleClickListenerOnBaseVocaKnow, false);
        bottomSheetDialog.setOnDismissListener(dismissListener);
        bottomSheetDialog.show();
    }
    DialogInterface.OnDismissListener dismissListener = (dialogInterface) -> {
        updateTvGuideText("챗GPT가 응답을 준비중입니다.");
        EnumLanguage motherTongue = LanguageUtil.getMotherTongueLanguage(this);
        String prompt = PromptUtil.getPromptMessageVocaKnow(gptOptions, motherTongue, vocaListToStudy, isShowInstuduction);
        updateLastPrompt(prompt);
        isShowInstuduction = !isShowInstuduction;
        ChatCompletionRequest request = chatGptHelper.createVocaKnowChatGptRequest(prompt, gptOptions, vocaListToStudy, motherTongue, isShowInstuduction);
        adapter.addMessage(chatGptHelper.createGptChatMessageForPrompt(prompt + "에 대해서 학습합니다."));
        sendMessageToChatGpt(request);


//        CompletionsRequest completionsRequest = chatGptHelper.createVocaKnowCompletionsRequest(prompt, gptOptions, vocaListToStudy, motherTongue, isShowInstuduction);
//        getCompletion(completionsRequest);
    };

    @NonNull
    private String getStudyText() {
        return BaseVocaList.getVocaListInStringWithEnter(vocaListToStudy);
    }

    private void bindData() {
        adapter.notifyDataSetChanged();
    }

    private void scrollToLastPosition() {
        scrollToLastPosition(true);
    }

    private void scrollToLastPosition(boolean isSmoothScroll) {
        int position = adapter.getItemCount() - 1;
        if (position >= 0) {
            if (isSmoothScroll) {
                binding.rvChatGpt.smoothScrollToPosition(adapter.getItemCount() - 1);
            } else {
                binding.rvChatGpt.scrollToPosition(adapter.getItemCount() - 1);
            }
        }
    }

    @Override
    public void onHeaderLeftClick() {
        onBackPressed();
    }

    @Override
    public void onHeaderIconRightClick() {
//        showDialoguePopup(); //나중에는 다른 곳으로 옮겨야 함.
        subDatabase.deleteAllGptChatMessages();
        ToastUtil.getInstance(this).show("테이블 초기화함");
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
    }

    View.OnClickListener answerInStudyLangListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            hideBottomActionExtraButtons();
            sendAnswerAgainPrompt(GptSystemInstructions.ANSWER_STUDY_LANG, "학습어");
        }
    };

    View.OnClickListener answerInMotherTongueListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            hideBottomActionExtraButtons();
            sendAnswerAgainPrompt(GptSystemInstructions.ANSWER_MOTHER_TONGUE, "모국어");
        }
    };

    private void sendAnswerAgainPrompt(String instruction, String language) {
        String prompt = lastUserPrompt + instruction;
        String message = "챗GPT에게 " + language + "로 다시 대답해달라고 요청했습니다.";
        updateTvGuideText(message);
        adapter.addMessage(chatGptHelper.createGptChatMessageForApp(""));
        ChatCompletionRequest request = GptRequestBuilder.sendNormalPromptToGpt(prompt, ChatCompletionRequest.RequestResponseType.GUIDE, gptOptions, instruction);
        sendMessageToChatGpt(request);
    }

    private void updateTvGuideText(String str) {
        runOnUiThread(() -> {
            binding.tvHint.setText(str);
        });
    }

    private void showWritingPracticeDialog() {
        VocaWritingPracticeDialog writingPracticeDialog = new VocaWritingPracticeDialog(this, vocaListToStudy.get(0), listener);
        writingPracticeDialog.show();
    }

    OnClickListener listener = new OnClickListener() {
        @Override
        public void onClick(View view, Object object) {
            switch (view.getId()) {
                case R.id.button_ok:
                    ToastUtil.getInstance(ChatGptActivity.this).show("OK");
                    break;
                case R.id.button_cancel:
                    ToastUtil.getInstance(ChatGptActivity.this).show("Cancel");
                    break;

            }
        }
    };

    private void toggleExtraButtons() {
        if (binding.bottomActionExtraButtons.getRoot().getVisibility() == View.GONE) {
            // Hide keyboard
            Utils.hideSoftKeyboard(this, binding.promptEditText);
            showBottomActionExtraButtons();
        } else {
            binding.promptEditText.requestFocus();
            Utils.showSoftKeyboard(this, binding.promptEditText);
            hideBottomActionExtraButtons();
        }
    }

    private void hideBottomActionExtraButtons() {
        binding.bottomActionExtraButtons.getRoot().setVisibility(View.GONE);
        binding.ibMoreCommand.setImageResource(R.drawable.ic_baseline_add_24);
    }

    private void showBottomActionExtraButtons() {
        binding.bottomActionExtraButtons.getRoot().setVisibility(View.generateViewId());
        binding.ibMoreCommand.setImageResource(R.drawable.ic_close_black_24dp);
    }

//    @Override
//    public void onBackPressed() {
////        ConfirmationDialog confirmationDialog = ConfirmationDialog.createWithYesNo(this, R.string.msg_confirm_exit_gpt, onConfirmListener);
////        confirmationDialog.show();
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
}
