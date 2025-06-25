package com.dalread.model;

import java.util.ArrayList;
import java.util.List;

public class ParentGroup {

    private long id;
    private String text;
    private List<HanjaItem> childList;

    public ParentGroup(long id, String text, List<HanjaItem> childList) {
        setId(id);
        setText(text);
        setChildList(childList);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getText() {
        if (text == null) {
            text = "";
        }
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public List<HanjaItem> getChildList() {
        if (childList == null) {
            childList = new ArrayList<>();
        }
        return childList;
    }

    public void setChildList(List<HanjaItem> childList) {
        this.childList = childList;
    }
}
