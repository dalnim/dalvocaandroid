package com.dalread.model;

import android.content.Context;

import com.dalread.interfaces.IVocaCoreItem;

import java.io.Serializable;

public interface HanjaItem extends Serializable, IVocaCoreItem {
    Long getHI_INDEX();
    void setHI_INDEX(Long INDEX);

    Long getHI_ID();

    int getHI_VOCA_TYPE();

    String getHI_VOCA();

    String getHI_VOCA_WITH_VOCAORI();

    String getHI_VOCAORI();

    String getHI_MEANING1();

    String getHI_MEANING(Context context);

    String getHI_MEANING_DETAILED(Context context);

    String getHI_PRONOUNCE1_FIRST();

    Long getHI_VOCA_KNOW();

    Long getHI_VOCA_KNOWPRONOUNCE();

    Long getHI_BOOKMARK();

    void swapHI_BOOKMARK();

    void setHI_BOOKMARK(Long BOOKMARK);

    boolean isHI_BOOKMARK();

    void setHI_VOCA_KNOW(Long VOCA_KNOW);

    void setHI_VOCA_KNOWPRONOUNCE(Long VOCA_KNOWPRONOUNCE);

    String getHI_ALL_TEXT();

    String getHI_ALL_TEXT_FOR_NORMAL_TEXT();

    String getHI_MEANING_AND_DETAILED();

    Long getHI_VOCA_LEVEL();
}
