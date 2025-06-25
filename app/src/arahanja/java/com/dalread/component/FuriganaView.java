package com.dalread.component;

import android.content.Context;
import android.util.AttributeSet;

import com.dalread.util.Constant;

import java.util.Vector;

/**
 * Created by Akira on 2016/06/24.
 */
public class FuriganaView extends BaseFuriganaView {
    public FuriganaView(Context context) {
        super(context);
        initialize(context, null);
    }

    public FuriganaView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context, attrs);
    }

    public FuriganaView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context, attrs);
    }

    public void setFuriganViewForBookVoca(String rubyText, int showFurigana) {
        setFuriganViewCommon(false, showFurigana, rubyText);
    }
    public void setFuriganViewForDifficultWords(String rubyText) {
        setUseHighLightColorForUnknownWord(false);
        setFuriganViewCommon(true, Constant.SHOW_FURIGANA_OFF, rubyText);
    }
    public void setFuriganViewForMeaning(String rubyText) {
        setFuriganViewCommon(true, Constant.SHOW_FURIGANA_OFF, rubyText);
    }
    private void setFuriganViewCommon(boolean isTutor, int showFurigana, String rubyText) {
        setTutor(isTutor); //setIsShowFurigana(Constant.SHOW_FURIGANA_OFF);와 겹침. 나중에 하나를 없애야 함.
        resetText();
        setIsKnownPronounceMeaning(true);
        setIsShowFurigana(showFurigana);
        setJText(rubyText);
    }


    protected void setTextSizeOfmFuriganaTextPaint(float size) {
        mFuriganaTextPaint.setTextSize(size * 0.8f);
    }

    protected int getFuriganaView_line_spacing() {
        if (isShowFurigana == Constant.SHOW_FURIGANA_OFF) {
            return Constant.RUBY.DEFAULT_LINE_SPACE_FOR_HIDE_RUBY;
        } else {
            return Constant.RUBY.DEFAULT_LINE_SPACE_FOR_HANJA;
        }
    }

    protected void addNewLineBreak() {
        Vector<PairText> pt1 = new Vector<>();
        pt1.add(new BaseFuriganaView.PairText(new BaseFuriganaView.JText(Constant.BREAK_CHARACTER, TYPE_NORMAL, 0)));
        BaseFuriganaView.Line l1 = new BaseFuriganaView.Line();
        l1.setPairTexts(pt1);
        mLines.add(l1);
    }

}