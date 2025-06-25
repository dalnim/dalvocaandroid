package com.dalread.network.events;

/**
 * Created by JetVHS on 10/30/2016.
 */
public class SuccessEvent extends BaseEvent {
    Object model;
    public SuccessEvent(Screen screen, EventType eventType, Object model) {
        super(screen, eventType);
        this.model = model;
    }

    public Object getModel() {
        return model;
    }
}