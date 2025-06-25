package com.dalread.model;

public class DisplayObject {

    private String name;
    private int order;

    public DisplayObject(String name, int order) {
        setName(name);
        setOrder(order);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }
}
