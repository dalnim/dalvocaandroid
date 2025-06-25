package com.dalread.network;

public class ChatCompletionRequest {
    public enum RequestResponseType {
        REQUEST,
        RESPONSE,
        GUIDE
    }
    private ChatGPTRequest chatGPTRequest;
    private RequestResponseType requestResponseType;

    public ChatCompletionRequest(ChatGPTRequest chatGPTRequest, RequestResponseType requestResponseType) {
        this.chatGPTRequest = chatGPTRequest;
        this.requestResponseType = requestResponseType;
    }

    public ChatGPTRequest getChatGPTRequest() {
        return chatGPTRequest;
    }

    public RequestResponseType getRequestResponseType() {
        return requestResponseType;
    }

    public void setRequestResponseType(RequestResponseType requestResponseType) {
        this.requestResponseType = requestResponseType;
    }

    public boolean isMessageTypeGuide() {
        return requestResponseType == RequestResponseType.GUIDE;
    }

}
