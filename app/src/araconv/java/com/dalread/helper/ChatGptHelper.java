package com.dalread.helper;

import android.content.Context;
import android.os.Handler;

import androidx.recyclerview.widget.RecyclerView;

import com.dalread.adapter.ChatGptAdapter;
import com.dalread.base.EnumLanguage;
import com.dalread.database.sqlite.SubDatabase;
import com.dalread.interfaces.IVocaFullPlayTTSItem;
import com.dalread.model.GPT_CHAT_MESSAGE;
import com.dalread.network.ChatCompletionRequest;
import com.dalread.network.CompletionsRequest;
import com.dalread.network.GptRequestBuilder;
import com.dalread.network.GptStudyMode;
import com.dalread.network.GptUserOption;
import com.dalread.util.BaseVocaKnow;
import com.dalread.util.BaseVocaList;
import com.dalread.util.VocaListUtil;

import java.util.List;
//이건 챗GPT API를 사용할때만 쓴다. ConversationHelper를 쓴다.
public class ChatGptHelper {
    private RecyclerView recyclerView;
    private ChatGptAdapter adapter;
    private Context context;
    private SubDatabase subDatabase;

    public ChatGptHelper(Context context, RecyclerView recyclerView, ChatGptAdapter adapter, SubDatabase subDatabase) {
        this.recyclerView = recyclerView;
        this.adapter = adapter;
        this.context = context;
        this.subDatabase = subDatabase;
    }

    public ChatCompletionRequest initChatGpt(GptUserOption gptOptions, GptStudyMode gptStudyMode) {
        return GptRequestBuilder.initChatGpt(gptOptions, ChatCompletionRequest.RequestResponseType.GUIDE, gptStudyMode);
    }

    //첨에는 gptMessage의 message가 ...이지만, 이 메소드가 끝나면 response와 동일해진다.
    public void addGptResponse(String response, GPT_CHAT_MESSAGE gptMessage, GptUserOption gptOptions) {
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

    private void getOneLineDifficultWordAndMeaning(String response, GPT_CHAT_MESSAGE gptMessage) {
        List<IVocaFullPlayTTSItem> allWordList = VocaListUtil.getAllWordListOfFromDB(response, subDatabase); //subDatabase.getAllWordListWordListWithComma(wordListWithComma);
        List<IVocaFullPlayTTSItem> wordList = BaseVocaList.getDifficultVocaListFromList(allWordList);
        gptMessage.setAllWordList(allWordList);
        gptMessage.setDifficultWordMeaning(BaseVocaKnow.getOneLineDifficultWordAndMeaning(context, wordList));
    }

    public void updateOneLineDifficultWordAndMeaning(GPT_CHAT_MESSAGE gptMessage) {
        getOneLineDifficultWordAndMeaning(gptMessage.getMESSAGE_CONTENT(), gptMessage);
        adapter.notifyItemChanged(gptMessage.getAdapterPosition());
    }

    //addGptResponse가 안되었을때 사용함.
    public void addAppResponse(String response, GPT_CHAT_MESSAGE gptMessage, GptUserOption gptOptions) {
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

    public void addGptPrompt(String prompt) {
        adapter.addMessage(createGptChatMessageForPrompt(prompt));
    }

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

//    private GptMessage convertGptChatMessageToGptMessage(GPT_CHAT_MESSAGE gptChatMessage) {
//        GptMessage.MessageType messageType;
//        switch (gptChatMessage.getTYPE()) {
//            case "PROMPT":
//                messageType = GptMessage.MessageType.PROMPT;
//                break;
//            case "RESPONSE":
//                messageType = GptMessage.MessageType.RESPONSE;
//                break;
//            default:
//                messageType = GptMessage.MessageType.APP;
//                break;
//        }
//        GptMessage gptMessage = new GptMessage(gptChatMessage.getMESSAGE_CONTENT(), messageType);
//
//        return gptMessage;
//    }
//
//    public List<GptMessage> convertGptChatMessagesToGptMessages(List<GPT_CHAT_MESSAGE> gptChatMessages) {
//        List<GptMessage> gptMessages = new ArrayList<>();
//        for (GPT_CHAT_MESSAGE gptChatMessage : gptChatMessages) {
//            gptMessages.add(convertGptChatMessageToGptMessage(gptChatMessage));
//        }
//        return gptMessages;
//    }
//    public GPT_CHAT_MESSAGE convertGptMessageToGptChatMessage(GptMessage message) {
//        GPT_CHAT_MESSAGE gptChatMessage = new GPT_CHAT_MESSAGE();
//        gptChatMessage.setMESSAGE_TYPE("TEXT");
//        gptChatMessage.setMESSAGE_CONTENT(message.getMessage());
//        gptChatMessage.setTYPE(message.getMessageType().name());
//
//        return gptChatMessage;
//    }

    public GPT_CHAT_MESSAGE createGptChatMessageForPrompt(String message) {
        return createGptChatMessageForApp(message, ChatCompletionRequest.RequestResponseType.REQUEST);
    }
    public GPT_CHAT_MESSAGE createGptChatMessageForResponse(String message) {
        return createGptChatMessageForApp(message, ChatCompletionRequest.RequestResponseType.RESPONSE);
    }
    public GPT_CHAT_MESSAGE createGptChatMessageForApp(String message) {
        return createGptChatMessageForApp(message, ChatCompletionRequest.RequestResponseType.GUIDE);
    }
    private GPT_CHAT_MESSAGE createGptChatMessageForApp(String message, ChatCompletionRequest.RequestResponseType messageType) {
        GPT_CHAT_MESSAGE gptChatMessage = new GPT_CHAT_MESSAGE();
        gptChatMessage.setMESSAGE_CONTENT(message);
        gptChatMessage.setREQUEST_RESONPSE_TYPE(messageType.name());
        return gptChatMessage;
    }
}
