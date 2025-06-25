package com.dalread.model;

import android.content.Context;

import com.dalread.interfaces.IVocaBasicItem;
import com.dalread.util.LanguageUtil;

import java.io.Serializable;

public class VocaKnowMeaning extends VocaKnowAndKnowpronounce implements Serializable {

//    @SerializedName("VOCA")
    private String voca;
//    @SerializedName("MEANING")
    private String meaning;

    public VocaKnowMeaning(int vocaId, int vocaType, int vocaKnow, int vocaKnowPronounce, String voca, String meaning) {
        super(vocaId, vocaType, vocaKnow, vocaKnowPronounce);
        this.voca = voca;
        this.meaning = meaning;
    }

    public VocaKnowMeaning(Context context, IVocaBasicItem item) {
        super(item);
        this.voca = item.getVIVoca();
        this.meaning = item.getVIMeaning(LanguageUtil.getMotherTongueLanguage(context));
    }
    public String getVoca() {
        return voca;
    }

    public void setVoca(String voca) {
        this.voca = voca;
    }

    public String getMeaning() {
        return meaning;
    }

    public void setMeaning(String meaning) {
        this.meaning = meaning;
    }
}
