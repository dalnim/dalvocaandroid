package com.dalread.network;

import android.util.Log;

import androidx.annotation.NonNull;

import com.dalread.AraConvApplication;

import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AraConvApiImpl {
    private final String TAG = "AraHanjaApiImpl";
    private static AraConvApi service;
    private Call<ChatCompletionResponse> currentApiCall;
    //    private static Retrofit chatGptApi;
    private static Retrofit retrofit;
    private AraConvApplication mApplication;

    public AraConvApiImpl(AraConvApplication mApplication) {
        this.mApplication = mApplication;
        OkHttpClient client = getOkHttpClient();

        this.service = getRetrofitInstance(client);
    }

    //API를 부를때 URL경로등 로그를 제대로 다 보여줄려고.
    @NonNull
    private OkHttpClient getOkHttpClient() {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        int timeOut = 10;
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .connectTimeout(timeOut, TimeUnit.SECONDS)
                .readTimeout(timeOut, TimeUnit.SECONDS)
                .writeTimeout(timeOut, TimeUnit.SECONDS)
                .build();
        return client;
    }

    private static AraConvApi getRetrofitInstance(OkHttpClient client) {
        final String baseUrl = "https://api.openai.com/v1/";
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .client(client)
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit.create(AraConvApi.class);
    }

    public void cancelSendMessageToChatCompletion() {
        if (currentApiCall != null && !currentApiCall.isCanceled()) {
            currentApiCall.cancel();
        }
    }

//    @Deprecated
//    public void initChatCompletion(ChatGPTRequest request, DalApiListener<String> listener) {
//        // Cancel any previous API call that might be in progress
//        currentApiCall = service.sendMessage(request);
//        currentApiCall.enqueue(new Callback<ChatCompletionResponse>() {
//            @Override
//            public void onResponse(Call<ChatCompletionResponse> call, Response<ChatCompletionResponse> response) {
//                if (response.isSuccessful()) {
//                    ChatCompletionResponse chatCompletionResponse = response.body();
//                    if (chatCompletionResponse != null) {
//                        List<ChatCompletionResponse.Choice> choices = chatCompletionResponse.getChoices();
//                        if (choices != null && !choices.isEmpty()) {
//                            StringBuilder sb = new StringBuilder();
//                            for (ChatCompletionResponse.Choice choice : choices) {
//                                sb.append(choice.getMessage().getContent());
//                            }
//                            listener.onSuccess(sb.toString());
//                        }
//                    } else {
//                        int i = 0;
//                        listener.onFailure("chatCompletionResponse is null");
//                    }
//                } else {
//                    listener.onFailure("response.isSuccessful() false");
//                }
//            }
//
//            @Override
//            public void onFailure(Call<ChatCompletionResponse> call, Throwable t) {
//                if (call.isCanceled()) {
//                    Log.d("DalApiClient", "API call was canceled");
//                } else {
//                    listener.onFailure("GPT server is busy");
//                }
//            }
//        });
//    }

    public void sendMessageToChatCompletion(ChatCompletionRequest request, DalApiListener<ChatCompletionResponse> listener) {
        // Cancel any previous API call that might be in progress
        cancelSendMessageToChatCompletion();
        ChatGPTRequest chatGPTRequest = request.getChatGPTRequest();
        currentApiCall = service.sendMessage(chatGPTRequest);
        currentApiCall.enqueue(new Callback<ChatCompletionResponse>() {
            @Override
            public void onResponse(Call<ChatCompletionResponse> call, Response<ChatCompletionResponse> response) {
                if (response.isSuccessful()) {
                    ChatCompletionResponse chatCompletionResponse = response.body();
                    if (chatCompletionResponse != null) {
                        List<ChatCompletionResponse.Choice> choices = chatCompletionResponse.getChoices();
                        if (choices != null && !choices.isEmpty()) {
                            StringBuilder sb = new StringBuilder();
                            for (ChatCompletionResponse.Choice choice : choices) {
                                sb.append(choice.getMessage().getContent());
                            }

                        }
                        listener.onSuccess(chatCompletionResponse);
                    } else {
                        int i = 0;
                        listener.onFailure("chatCompletionResponse is null");
                    }
                } else {
                    listener.onFailure("response.isSuccessful() false");
                }
            }

            @Override
            public void onFailure(Call<ChatCompletionResponse> call, Throwable t) {
                if (call.isCanceled()) {
                    Log.d("DalApiClient", "API call was canceled");
                } else {
                    listener.onFailure("GPT server is busy");
                }
            }
        });
    }

    public void getCompletion(CompletionsRequest request, DalApiListener<String> listener) {
        service.getCompletions(request).enqueue(new Callback<CompletionsResponse>() {
            @Override
            public void onResponse(Call<CompletionsResponse> call, Response<CompletionsResponse> response) {
                if (response.isSuccessful()) {
                    CompletionsResponse completionsResponse = response.body();
                    if (completionsResponse != null) {
                        List<CompletionsResponse.Choice> choices = completionsResponse.getChoices();
                        if (choices != null && !choices.isEmpty()) {
                            StringBuilder sb = new StringBuilder();
                            for (CompletionsResponse.Choice choice : choices) {
                                sb.append(choice.getText());
                            }
                            listener.onSuccess(sb.toString());
                        }
                    } else {
                        int i = 0;
                        listener.onFailure("sendMessageToCompletion chatGPTResponse is null");
                    }
                } else {
                    listener.onFailure("sendMessageToCompletion response.isSuccessful() false");
                }
            }

            @Override
            public void onFailure(Call<CompletionsResponse> call, Throwable t) {
                // Handle error: Network failure
                t.printStackTrace();
                listener.onFailure("sendMessageToCompletion GPT server is busy");
            }
        });
    }


    public void editPrompt(DalApiListener<String> listener) {
        EditRequest request = new EditRequest("text-davinci-edit-001", "What day of the wek is it?", "Fix the spelling mistakes");
        Call<EditResponse> call = service.editPrompt(request);
        call.enqueue(new Callback<EditResponse>() {
            @Override
            public void onResponse(Call<EditResponse> call, Response<EditResponse> response) {
                if (response.isSuccessful()) {
                    EditResponse editResponse = response.body();
                    if (listener != null) {
                        listener.onSuccess(editResponse.getChoices().get(0).getText());
                    }
                } else {
                    Log.e(TAG, "response is not Successful(): ");
                    if (listener != null) {
                        listener.onFailure(null);
                    }
                }
            }

            @Override
            public void onFailure(Call<EditResponse> call, Throwable t) {
                // Handle the failure here
                Log.e(TAG, "onFailure: " + t);
                if (listener != null) {
                    listener.onFailure(null);
                }
            }
        });

    }


}




