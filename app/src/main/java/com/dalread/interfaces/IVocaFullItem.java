package com.dalread.interfaces;

public interface IVocaFullItem extends IVocaBasicItem {
    Integer getVIIndex();
    void setVIIndex(Integer index);

    String getVIPosAll();
    default int getVIIdInVocaBook() {
        return 0;
    }
    default void setVIIdInVocaBook(int value) {
    }
}
