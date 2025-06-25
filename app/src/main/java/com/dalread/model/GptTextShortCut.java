package com.dalread.model;

import com.dalread.database.sqlite.model.AbstractMeanings;
import com.dalread.util.Constant;

public class GptTextShortCut extends AbstractMeanings {
    private int id;
    private int systemDefault;
    private int displayOrder;
    private String title = "";
    private int edited;
    private int usedPrompt; //프롬프트로 쓸지 여부
    private int usePopup1Menu;
    private int usePopup2Menu;
    private int usePopup3Menu;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isSystemDefault() {
        return systemDefault == Constant.INT_BOOLEAN.TRUE;
    }

    public void setSystemDefault(int systemDefault) {
        this.systemDefault = systemDefault;
    }

    public int getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(int displayOrder) {
        this.displayOrder = displayOrder;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getEdited() {
        return edited;
    }

    public void setEdited(int edited) {
        this.edited = edited;
    }

    public int getUsedPrompt() {
        return usedPrompt;
    }

    public void setUsedPrompt(int usedPrompt) {
        this.usedPrompt = usedPrompt;
    }

    public boolean isUsePopupMenu() {
        return isUsePopup1Menu() || isUsePopup2Menu() || isUsePopup3Menu();
    }
    public boolean isUsePopup1Menu() {
        return usePopup1Menu == Constant.INT_BOOLEAN.TRUE;
    }

    public int getUsePopup1Menu() {
        return usePopup1Menu;
    }

    public int getUsePopup2Menu() {
        return usePopup2Menu;
    }

    public int getUsePopup3Menu() {
        return usePopup3Menu;
    }

    public void setUsePopup1Menu(int usePopup1Menu) {
        this.usePopup1Menu = usePopup1Menu;
    }
    public void setUsePopup1Menu(boolean value) {
        this.usePopup1Menu = value == true ? Constant.INT_BOOLEAN.TRUE : Constant.INT_BOOLEAN.FASLE;
    }
    public boolean isUsePopup2Menu() {
        return usePopup2Menu == Constant.INT_BOOLEAN.TRUE;
    }

    public void setUsePopup2Menu(int usePopup2Menu) {
        this.usePopup2Menu = usePopup2Menu;
    }
    public void setUsePopup2Menu(boolean value) {
        this.usePopup2Menu = value == true ? Constant.INT_BOOLEAN.TRUE : Constant.INT_BOOLEAN.FASLE;
    }
    public boolean isUsePopup3Menu() {
        return usePopup3Menu == Constant.INT_BOOLEAN.TRUE;
    }

    public void setUsePopup3Menu(int usePopup3Menu) {
        this.usePopup3Menu = usePopup3Menu;
    }
    public void setUsePopup3Menu(boolean value) {
        this.usePopup3Menu = value == true ? Constant.INT_BOOLEAN.TRUE : Constant.INT_BOOLEAN.FASLE;
    }
}
