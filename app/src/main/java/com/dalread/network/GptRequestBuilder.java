package com.dalread.network;

import androidx.annotation.NonNull;

import com.dalread.database.GptSystemInstructions;
import com.dalread.util.Utils;

import java.util.ArrayList;
import java.util.List;

public class GptRequestBuilder {
    private final static String model = "gpt-3.5-turbo";
    private final static String modelDavinci = "text-davinci-003";
//    public static ChatGPTRequest sendPromptFirstAppUse(String prompt, GptOptions gptOptions) {
//        return getChatGPTRequest(prompt, gptOptions, GptSystemInstructions.FIRST_CHAT_ROOM_USE_1, GptSystemInstructions.FIRST_CHAT_ROOM_USE_2);
//    }

    public static ChatCompletionRequest initChatGpt(GptUserOption gptOptions, ChatCompletionRequest.RequestResponseType messageType, GptStudyMode gptStudyMode) {
        List<String> list = new ArrayList<>();
        list.add(GptSystemInstructions.FIRST_CHAT_ROOM_USE_COMMON_1);
        list.add(GptSystemInstructions.FIRST_CHAT_ROOM_USE_COMMON_2);
//        if (gptStudyMode == GptStudyMode.DIALOGUE) {
//            list.add(GptSystemInstructions.FIRST_CHAT_ROOM_USE_DIALOGUE_1);
//            list.add(GptSystemInstructions.FIRST_CHAT_ROOM_USE_DIALOGUE_2);
//        } else if (gptStudyMode == GptStudyMode.VOCA) {
//            list.add(GptSystemInstructions.FIRST_CHAT_ROOM_USE_VOCA_1);
//            list.add(GptSystemInstructions.FIRST_CHAT_ROOM_USE_VOCA_2);
//        } else {
//            list.add(GptSystemInstructions.FIRST_CHAT_ROOM_USE_FREE_TALKING_1);
//            list.add(GptSystemInstructions.FIRST_CHAT_ROOM_USE_FREE_TALKING_2);
//        }
        String[] systemInstructions = list.toArray(new String[0]);
        makeMessageForGpt("prompt", systemInstructions);


        return getChatCompletionRequest("", messageType, gptOptions, systemInstructions);
    }



    public static ChatCompletionRequest sendPromptFirstAppUse(String prompt, ChatCompletionRequest.RequestResponseType messageType, GptUserOption gptOptions, String vocaToStudy) {
        String[] systemInstructions = new String[] {GptSystemInstructions.FIRST_CHAT_ROOM_USE_COMMON_1, GptSystemInstructions.FIRST_CHAT_ROOM_USE_COMMON_2};
        return getChatCompletionRequest(prompt, messageType, gptOptions, systemInstructions);
    }

    public static ChatCompletionRequest sendPromptFirstChat(String prompt, ChatCompletionRequest.RequestResponseType messageType, GptUserOption gptOptions, String... systemInstructions) {
        return getChatCompletionRequest(prompt, messageType, gptOptions, systemInstructions);
    }
    public static ChatCompletionRequest sendVocaKnowPromptToGpt(String prompt, ChatCompletionRequest.RequestResponseType messageType, GptUserOption gptOptions, boolean isVocaKnown, boolean isShowInstuduction) {
        String[] systemInstructions = new String[0];
//        if (isShowInstuduction) {
            systemInstructions = new String[] {GptSystemInstructions.VOCA_KNOW_PROMPT_1, GptSystemInstructions.VOCA_KNOW_PROMPT_2};
//        }
        return getChatCompletionRequest(prompt, messageType, gptOptions, systemInstructions);
    }
    public static ChatCompletionRequest createConversationGptRequest(String prompt, ChatCompletionRequest.RequestResponseType messageType, GptUserOption gptOptions, boolean isNewConverstaion) {
        List<String> list = new ArrayList<>();
        list.add(GptSystemInstructions.FIRST_CHAT_ROOM_USE_DIALOGUE_1);
        list.add(GptSystemInstructions.FIRST_CHAT_ROOM_USE_DIALOGUE_2);
        String[] systemInstructions = list.toArray(new String[0]);
        return getChatCompletionRequest(prompt, messageType,gptOptions, systemInstructions);
    }
    public static ChatCompletionRequest createStudyDialogGptRequest(String prompt, ChatCompletionRequest.RequestResponseType messageType, GptUserOption gptOptions) {
        List<String> list = new ArrayList<>();
        list.add(GptSystemInstructions.FIRST_CHAT_ROOM_USE_DIALOGUE_1);
        list.add(GptSystemInstructions.FIRST_CHAT_ROOM_USE_DIALOGUE_2);
        String[] systemInstructions = list.toArray(new String[0]);
        return getChatCompletionRequest(prompt, messageType, gptOptions, systemInstructions);
    }


    public static CompletionsRequest sendVocaKnowPromptToCompletions1(String prompt, GptUserOption gptOptions, boolean isShowInstuduction) {
        String[] systemInstructions = new String[0];
//        if (isShowInstuduction) {
        systemInstructions = new String[] {GptSystemInstructions.VOCA_KNOW_PROMPT_1, GptSystemInstructions.VOCA_KNOW_PROMPT_1};
//        }

        // Combine systemInstructions and prompt
        StringBuilder combinedPrompt = new StringBuilder();
        for (String instruction : systemInstructions) {
            combinedPrompt.append(instruction).append("\n");
        }
        combinedPrompt.append(prompt);

        return getCompletionsRequest(combinedPrompt.toString(), gptOptions);
    }

    public static CompletionsRequest sendVocaKnowPromptToCompletions(String prompt, GptUserOption gptOptions) {
        return getCompletionsRequest(prompt, gptOptions);
    }

    public static ChatCompletionRequest sendNormalPromptToGpt(String prompt, ChatCompletionRequest.RequestResponseType messageType, GptUserOption gptOptions, String... systemInstructions) {
        return getChatCompletionRequest(prompt, messageType, gptOptions, systemInstructions);
    }

    @NonNull
    private static ChatCompletionRequest getChatCompletionRequest(String prompt, ChatCompletionRequest.RequestResponseType messageType, GptUserOption gptOptions, String[] systemInstructions) {
        List<ChatGPTRequest.Message> messages = makeMessageForGpt(prompt, systemInstructions);
        ChatGPTRequest request = new ChatGPTRequest(messages, model, gptOptions.getMaxResponseTokens(), gptOptions.getTemperature());
        request.setStream(true, ChatGPTRequest.Message.Role.user);
        return new ChatCompletionRequest(request, messageType);
    }

    public static List<ChatGPTRequest.Message> makeMessageForGpt(String prompt, String... systemInstructions) {
        String assistantByPreviousMessageFromGpt = "";
        List<ChatGPTRequest.Message> messages = new ArrayList<>();
        for (String systemInstruction : systemInstructions) {
            if (!Utils.isEmpty(systemInstruction)) {
                messages.add(new ChatGPTRequest.Message(ChatGPTRequest.Message.Role.system, systemInstruction));
            }
        }
        messages.add(new ChatGPTRequest.Message(ChatGPTRequest.Message.Role.user, prompt));
//        messages.add(new ChatGPTRequest.Message(ChatGPTRequest.Message.Role.assistant, assistantByPreviousMessageFromGpt));
        return messages;
    }

    @NonNull
    private static CompletionsRequest getCompletionsRequest(String prompt, GptUserOption gptOptions) {
//        List<ChatGPTRequest.Message> messages = makeMessageForGpt(prompt, systemInstructions);
        CompletionsRequest request = new CompletionsRequest(modelDavinci, prompt, gptOptions.getMaxResponseTokens(), 0.5);
        request.setStream(false);
        return request;
    }
}