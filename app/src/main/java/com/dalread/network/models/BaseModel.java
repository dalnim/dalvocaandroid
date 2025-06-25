package com.dalread.network.models;

/**
 * Created by JetVHS on 2/26/2017.
 */
public class BaseModel {
    private int code;

    public BaseModel() {
    }

    public BaseModel(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return "BaseModel{" +
                "code=" + code +
                '}';
    }
}
