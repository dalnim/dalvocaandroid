package com.dalread.network;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface AraConvApi {
    // API Key는 환경변수에서 읽어옴 (.env 파일)
    String API_KEY = System.getenv("OPENAI_API_KEY") != null ? 
        System.getenv("OPENAI_API_KEY") : "YOUR_OPENAI_API_KEY_HERE";

    @Headers({
            "Content-Type: application/json",
            "Authorization: Bearer " + API_KEY
    })
    @POST("chat/completions")
    Call<ChatCompletionResponse> sendMessage(@Body ChatGPTRequest request);

    @Headers({
            "Content-Type: application/json",
            "Authorization: Bearer " + API_KEY
    })
    @POST("completions")
    Call<CompletionsResponse> getCompletions(@Body CompletionsRequest request);

    @Headers({
            "Content-Type: application/json",
            "Authorization: Bearer " + API_KEY
    })
    @POST("edits")
    Call<EditResponse> editPrompt(@Body EditRequest request);
}
