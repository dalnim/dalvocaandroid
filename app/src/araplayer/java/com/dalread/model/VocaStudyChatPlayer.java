package com.dalread.model;

import com.dalread.database.sqlite.model.DicModel;

public class VocaStudyChatPlayer extends VocaStudyChat {

    private DicModel dicModel;

    public DicModel getDicModel() {
        return dicModel;
    }

    public void setDicModel(DicModel dicModel) {
        this.dicModel = dicModel;
    }
}
