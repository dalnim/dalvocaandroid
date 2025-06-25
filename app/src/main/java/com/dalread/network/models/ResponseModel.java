package com.dalread.network.models;

/**
 * Created by JetVHS on 10/30/2016.
 */
public class ResponseModel<T> {
    private T content;
    private int code;

    public ResponseModel() {
    }

    public ResponseModel(T content, int code) {
        this.content = content;
        this.code = code;
    }

    public T getContent() {
        return content;
    }

    public void setContent(T content) {
        this.content = content;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return "ResponseModel{" +
                "content=" + content +
                ", code=" + code +
                '}';
    }
}
