package com.dalread.model;

import com.dalread.util.Constant;

public class ScrollableMenuModel {
    private Constant.MenuButtonInPlayer type;
    private int drawableIcon;
    private String menuName;
    private boolean isSelected = false;

    public ScrollableMenuModel(Constant.MenuButtonInPlayer type, int drawableIcon, String menuName) {
        this.type = type;
        this.drawableIcon = drawableIcon;
        this.menuName = menuName;
    }

    public Constant.MenuButtonInPlayer getType() {
        return type;
    }

    public void setType(Constant.MenuButtonInPlayer type) {
        this.type = type;
    }

    public int getDrawableIcon() {
        return drawableIcon;
    }

    public void setDrawableIcon(int drawableIcon) {
        this.drawableIcon = drawableIcon;
    }

    public String getMenuName() {
        return menuName;
    }

    public void setMenuName(String menuName) {
        this.menuName = menuName;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
