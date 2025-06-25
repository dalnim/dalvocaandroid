package com.dalread.util;

import android.view.View;
import android.widget.TextView;

public class ViewUtil {

    public static boolean isViewGone(View view) {
        return view.getVisibility() == View.GONE;
    }


    public static void setVisibility(View view, boolean isShow) {
        view.setVisibility(isShow ? View.VISIBLE : View.GONE);
    }

    public static void setVisibility(View view, TextView tv) {
        view.setVisibility(Utils.isEmpty(tv.getText().toString()) ? View.GONE : View.VISIBLE);
    }

    public static boolean hitTest(View view, int x, int y) {
        final int tx = (int) (view.getTranslationX() + 0.5f);
        final int ty = (int) (view.getTranslationY() + 0.5f);
        final int left = view.getLeft() + tx;
        final int right = view.getRight() + tx;
        final int top = view.getTop() + ty;
        final int bottom = view.getBottom() + ty;

        return (x >= left) && (x <= right) && (y >= top) && (y <= bottom);
    }

    //Dalnim add
    //------
    public static void setViewListVisibility(int viewVisibility, View... views){
        for (View view : views){
            view.setVisibility(viewVisibility);
        }
    }

    public static void setViewListVisibilityVisible(View... views){
        setViewListVisibility(View.VISIBLE, views);
    }

    public static void setViewListVisibilityInvisible(View... views){
        setViewListVisibility(View.INVISIBLE, views);
    }

    public static void setViewListVisibilityGone(View... views){
        setViewListVisibility(View.GONE, views);
    }

//    //Dalnim add : need to get StudyLang to decide correctly.
//    public static boolean showKnowPronounceIcon(int studyLangCode, int vocaKnow, int vocaKnowPronounce) {
//        boolean blnResult = false;
//
//        if ((studyLangCode == EnumLanguage.CHINESE_SIMPLIFIED.getIdApi())
//                || (studyLangCode == EnumLanguage.CHINESE_TRADITIONAL.getIdApi())
//                || (studyLangCode == EnumLanguage.JAPANESE.getIdApi())) {
//            if (vocaKnowPronounce < Constant.VOCA_KNOW.VOCA_KNOW_KNOWN)
//                blnResult = true;
//        } else if (studyLangCode == EnumLanguage.KOREAN.getIdApi()) {
//        } else if (studyLangCode == EnumLanguage.KOREAN.getIdApi()) {
//        } else {
//            if (vocaKnow < Constant.VOCA_KNOW.VOCA_KNOW_KNOWN) {
//                blnResult = false;
//            } else if (vocaKnowPronounce < Constant.VOCA_KNOW.VOCA_KNOW_KNOWN) {
//                blnResult = true;
//            }
//        }
//
//        return blnResult;
//    }
//    //------
}
