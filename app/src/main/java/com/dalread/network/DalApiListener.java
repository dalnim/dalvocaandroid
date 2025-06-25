package com.dalread.network;

public interface DalApiListener<T> {
    void onSuccess(T response);

    void onFailure(String error);
}
