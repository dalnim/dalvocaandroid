package com.dalread.network.events;

/**
 * Created by JetVHS on 10/30/2016.
 */
public class ErrorEvent extends BaseEvent {
    String msg;
    int code;
    Object data;

    public ErrorEvent(Screen screen, EventType eventType, Object data) {
        super(screen, eventType);
        this.data = data;
    }

    public ErrorEvent(Screen screen, EventType eventType) {
        super(screen, eventType);
    }

    public ErrorEvent(Screen screen, EventType eventType, String msg) {
        super(screen, eventType);
        this.msg = msg;
    }

    public ErrorEvent(Screen screen, EventType eventType, String msg, int code) {
        super(screen, eventType);
        this.msg = msg;
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public int getCode() {
        return code;
    }

    public Object getData() {
        return data;
    }
}