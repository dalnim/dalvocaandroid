package com.dalread.helper;

import android.content.Context;
import android.os.Handler;

import androidx.recyclerview.widget.RecyclerView;

import com.dalread.R;
import com.dalread.adapter.PracticeConversationInChatAdapter;
import com.dalread.base.EnumLanguage;
import com.dalread.database.sqlite.SubDatabase;
import com.dalread.interfaces.IVocaFullPlayTTSItem;
import com.dalread.model.ConversationModel;
import com.dalread.model.VocaStudyChatExam;
import com.dalread.network.ChatCompletionRequest;
import com.dalread.network.CompletionsRequest;
import com.dalread.network.GptRequestBuilder;
import com.dalread.network.GptStudyMode;
import com.dalread.network.GptUserOption;
import com.dalread.util.BaseVocaKnow;
import com.dalread.util.BaseVocaList;
import com.dalread.util.VocaListUtil;

import java.util.List;

public class ConversationHelper {
    private RecyclerView recyclerView;
    private PracticeConversationInChatAdapter adapter;
    private Context context;
    private SubDatabase subDatabase;
    public enum PracticeType {
        CHOOSE_WORD,
        SPEAKING
    }
    public enum Person {
        A, B
    }
    public ConversationHelper(Context context, RecyclerView recyclerView, PracticeConversationInChatAdapter adapter, SubDatabase subDatabase) {
        this.recyclerView = recyclerView;
        this.adapter = adapter;
        this.context = context;
        this.subDatabase = subDatabase;
    }

    public ChatCompletionRequest initChatGpt(GptUserOption gptOptions, GptStudyMode gptStudyMode) {
        return GptRequestBuilder.initChatGpt(gptOptions, ChatCompletionRequest.RequestResponseType.GUIDE, gptStudyMode);
    }

    //첨에는 gptMessage의 message가 ...이지만, 이 메소드가 끝나면 response와 동일해진다.
    public void addGptResponse(String response, ConversationModel gptMessage, GptUserOption gptOptions) {
        getOneLineDifficultWordAndMeaning(response, gptMessage);
        String[] words = response.split("\\s+");
//        if (gptOptions == GptOptions.ADVANCED) {
        gptMessage.setMESSAGE_CONTENT(response);
        adapter.notifyItemChanged(adapter.getItemCount() - 1);
//        } else {
//            gptMessage.setMessage("");
//            adapter.notifyItemChanged(adapter.getItemCount() - 1);
//            Handler handler = new Handler();
//            for (int i = 0; i < words.length; i++) {
//                final int index = i;
//                handler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        String word = words[index];
//                        gptMessage.setMessage(gptMessage.getMessage() + " " + word);
//                        adapter.notifyItemChanged(adapter.getItemCount() - 1);
//                        recyclerView.post(new Runnable() {
//                            @Override
//                            public void run() {
//                                recyclerView.smoothScrollToPosition(adapter.getItemCount() - 1);
//                            }
//                        });
//                    }
//                }, i * gptOptions.getGptTypingSpeedInConsole());
//            }
//        }
    }

    public void getOneLineDifficultWordAndMeaning(String response, ConversationModel gptMessage) {
        List<IVocaFullPlayTTSItem> allWordList = VocaListUtil.getAllWordListOfFromDB(response, subDatabase); //subDatabase.getAllWordListWordListWithComma(wordListWithComma);
        List<IVocaFullPlayTTSItem> wordList = BaseVocaList.getDifficultVocaListFromList(allWordList);
        gptMessage.setAllWordList(allWordList);
        gptMessage.setDifficultWordMeaning(BaseVocaKnow.getOneLineDifficultWordAndMeaning(context, wordList));
    }

    public void updateOneLineDifficultWordAndMeaning(ConversationModel gptMessage) {
        getOneLineDifficultWordAndMeaning(gptMessage.getMESSAGE_CONTENT(), gptMessage);
        if (adapter != null)
            adapter.notifyItemChanged(gptMessage.getAdapterPosition());
    }

    //addGptResponse가 안되었을때 사용함.
    public void addAppResponse(String response, ConversationModel gptMessage, GptUserOption gptOptions) {
        String[] words = response.split("\\s+");
        if (gptOptions == GptUserOption.ADVANCED) {
            gptMessage.setMESSAGE_CONTENT(response);
            adapter.notifyItemChanged(adapter.getItemCount() - 1);
        } else {
            gptMessage.setMESSAGE_CONTENT("");
            adapter.notifyItemChanged(adapter.getItemCount() - 1);
            Handler handler = new Handler();
            for (int i = 0; i < words.length; i++) {
                final int index = i;
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        String word = words[index];
                        gptMessage.setMESSAGE_CONTENT(gptMessage.getMESSAGE_CONTENT() + " " + word);
                        adapter.notifyItemChanged(adapter.getItemCount() - 1);
                        recyclerView.post(new Runnable() {
                            @Override
                            public void run() {
                                recyclerView.smoothScrollToPosition(adapter.getItemCount() - 1);
                            }
                        });
                    }
                }, i * gptOptions.getGptTypingSpeedInConsole());
            }
        }
    }

//    public void addGptPrompt(String prompt) {
//        adapter.addMessage(createGptChatMessageForPrompt(prompt));
//    }

    public ChatCompletionRequest createConversationGptRequest(String prompt, GptUserOption gptOptions, boolean isNewConverstaion) {
        return GptRequestBuilder.createConversationGptRequest(prompt, ChatCompletionRequest.RequestResponseType.GUIDE, gptOptions, isNewConverstaion);
    }

    public ChatCompletionRequest createVocaKnowChatGptRequest(String prompt, GptUserOption gptOptions, List<IVocaFullPlayTTSItem> vocaListToStudy, EnumLanguage motherTongue, boolean isShowInstuduction) {
        if (BaseVocaList.getCountOfUnknownInList(vocaListToStudy) > 0) {
            return GptRequestBuilder.sendVocaKnowPromptToGpt(prompt, ChatCompletionRequest.RequestResponseType.GUIDE, gptOptions, false, isShowInstuduction);
        } else {
            return GptRequestBuilder.sendVocaKnowPromptToGpt(prompt, ChatCompletionRequest.RequestResponseType.GUIDE, gptOptions, true, isShowInstuduction);
        }
    }

    public CompletionsRequest createVocaKnowCompletionsRequest(String prompt, GptUserOption gptOptions, List<IVocaFullPlayTTSItem> vocaListToStudy, EnumLanguage motherTongue, boolean isShowInstuduction) {
        if (BaseVocaList.getCountOfUnknownInList(vocaListToStudy) > 0) {
            return GptRequestBuilder.sendVocaKnowPromptToCompletions("뜻을 설명해줘. 한국어로 해줘" + prompt, gptOptions);
        } else {
            return GptRequestBuilder.sendVocaKnowPromptToCompletions("같은 뜻의 다른 영어표현도 알려줘. 한국어로 해줘 " + prompt, gptOptions);
        }
    }

    public ChatCompletionRequest createStudyDialogGptRequest(String prompt, GptUserOption gptOptions) {
        return GptRequestBuilder.createStudyDialogGptRequest(prompt, ChatCompletionRequest.RequestResponseType.GUIDE, gptOptions);
    }

    public ConversationModel showFirstGuide(PracticeType practiceType) {
        String message = "";
        switch (practiceType) {
            case CHOOSE_WORD:
                message = context.getString(R.string.conversation_guide_word_matching_mode);
                break;
            case SPEAKING:
                message =  context.getString(R.string.conversation_guide_speaking_mode);
                break;
        }
        return addGuideMessage(message);
    }
    public ConversationModel addUserMessage(String message, String translation) {
        return addMessage(message, translation, ChatCompletionRequest.RequestResponseType.REQUEST);
    }
    public ConversationModel addAppMessage(String message, String translation) {
        return addMessage(message, translation, ChatCompletionRequest.RequestResponseType.RESPONSE);
    }
    public ConversationModel addAppMessage(VocaStudyChatExam quiz) {
        ConversationModel model = new ConversationModel();
        model.setMESSAGE_CONTENT(quiz.getQuestion());
        model.setREQUEST_RESONPSE_TYPE(ChatCompletionRequest.RequestResponseType.RESPONSE.name());
        model.setMESSAGE_TRANSLATION("");
        model.setVIVocaType(quiz.getVIVocaType());
        model.setVIVocaId(quiz.getVIVocaId());
        model.setVIVoca(quiz.getVIVoca());
        model.setVIPronounce(quiz.getVIPronounce());
        model.setVIVocaKnow(quiz.getVIVocaKnow());
        model.setVIVocaKnowPronounce(quiz.getVIVocaKnowPronounce());
        model.setVIBookmark(quiz.getVIBookmark());
        return model;
    }
    public ConversationModel addGuideMessage(String message) {
        return addMessage(message, "", ChatCompletionRequest.RequestResponseType.GUIDE);
    }
    private ConversationModel addMessage(String message, String translation, ChatCompletionRequest.RequestResponseType messageType) {
        ConversationModel model = new ConversationModel();
        model.setMESSAGE_CONTENT(message);
        model.setREQUEST_RESONPSE_TYPE(messageType.name());
        model.setMESSAGE_TRANSLATION(translation);
        return model;
    }
}
