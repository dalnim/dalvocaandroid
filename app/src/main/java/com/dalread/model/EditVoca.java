package com.dalread.model;

import com.dalread.interfaces.IVocaFullItem;

public class EditVoca {
    private IVocaFullItem iVocaFullItem;
    private Integer oldVocaId;

    public EditVoca(IVocaFullItem iVocaFullItem, Integer oldVocaId) {
        this.iVocaFullItem = iVocaFullItem;
        this.oldVocaId = oldVocaId;
    }

    public IVocaFullItem getiVocaFullItem() {
        return iVocaFullItem;
    }

    public void setiVocaFullItem(IVocaFullItem iVocaFullItem) {
        this.iVocaFullItem = iVocaFullItem;
    }

    public Integer getOldVocaId() {
        return oldVocaId;
    }

    public void setOldVocaId(Integer oldVocaId) {
        this.oldVocaId = oldVocaId;
    }
}
