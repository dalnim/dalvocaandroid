package com.dalread.util;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.dalread.R;
import com.dalread.base.EnumLanguage;
import com.dalread.database.SharedPreferencesDB;
import com.dalread.interfaces.IVocaBasicItem;
import com.dalread.interfaces.IVocaCoreItem;
import com.dalread.interfaces.IVocaFullPlayTTSItem;

import java.util.List;
import java.util.stream.Collectors;

public class BaseVocaKnow {
    public static int getVocaColor(Context context, int vocaKnow) {
        int colorId = getVocaColor(vocaKnow);
        return ContextCompat.getColor(context, colorId);
    }
    public static int getVocaColor(int vocaKnow) {
        return vocaKnow < Constant.VOCA_KNOW.VOCA_KNOW_KNOWN ? R.color.color_red : R.color.black;
    }

    public static boolean isNotRated(IVocaBasicItem item) {
        return isNotRated(item.getVIVocaKnow());
    }

    public static boolean isNotRated(int vocaKnow) {
        return vocaKnow == Constant.VOCA_KNOW.VOCA_KNOW_NOTRATED;
    }

    public static int getKnowValue() {
        return Constant.VOCA_KNOW.VOCA_KNOW_KNOWN;
    }
    public static int getAmkiGrade1Value() {
        return Constant.VOCA_KNOW.VOCA_KNOW_AMKI_GRADE_1;
    }
    public static int getAmkiGrade2Value() {
        return Constant.VOCA_KNOW.VOCA_KNOW_AMKI_GRADE_2;
    }
    public static int getUnknowValue() {
        return Constant.VOCA_KNOW.VOCA_KNOW_UNKNOWN;
    }

    public static boolean isKnown(int vocaKnow) {
        return vocaKnow >= Constant.VOCA_KNOW.VOCA_KNOW_KNOWN;
    }

    public static boolean isKnown(IVocaBasicItem item) {
        return isKnown(item.getVIVocaKnow());
    }

    public static boolean isUnknown(int vocaKnow) {
        return vocaKnow == Constant.VOCA_KNOW.VOCA_KNOW_UNKNOWN;
    }

    public static boolean isUnknown(IVocaBasicItem item) {
        return isUnknown(item.getVIVocaKnow());
    }

    public static boolean isUnknownAndLess(int vocaKnow) {
        return vocaKnow < Constant.VOCA_KNOW.VOCA_KNOW_KNOWN;
    }

    public static boolean isUnknownAndLess(IVocaBasicItem item) {
        return isUnknownAndLess(item.getVIVocaKnow());
    }
    public static boolean isUnknownAndLessPronounce(IVocaBasicItem item) {
        return isUnknownAndLess(item.getVIVocaKnowPronounce());
    }
    public static boolean isAmkiGrade1(int vocaKnow) {
        return vocaKnow == Constant.VOCA_KNOW.VOCA_KNOW_AMKI_GRADE_1;
    }

    public static boolean isAmkiGrade1(IVocaBasicItem item) {
        return isAmkiGrade1(item.getVIVocaKnow());
    }

    public static boolean isAmkiGrade2(int vocaKnow) {
        return vocaKnow == Constant.VOCA_KNOW.VOCA_KNOW_AMKI_GRADE_2;
    }

    public static boolean isAmkiGrade2(IVocaBasicItem item) {
        return isAmkiGrade2(item.getVIVocaKnow());
    }

    /*
     * @deprecated Replaced by {@link #isUnknownAndLess(int)}
     */
    @Deprecated
    public static boolean isNotKnown(int vocaKnow) {
        return vocaKnow < Constant.VOCA_KNOW.VOCA_KNOW_KNOWN;
    }
    /*
     * @deprecated Replaced by {@link #isUnknownAndLess()}
     */
    @Deprecated
    public static boolean isNotKnown(IVocaBasicItem item) {
        return isNotKnown(item.getVIVocaKnow());
    }

    public static boolean isKnowValueChanged(int vocaKnow, int newVocaKnow) {
        return !(vocaKnow == newVocaKnow);
    }

    public static boolean isKnowValueChanged(int vocaKnow, int newVocaKnow, int vocaKnowPronounce, int newVocaKnowPronounce) {
        return !(vocaKnow == newVocaKnow && vocaKnowPronounce == newVocaKnowPronounce);
    }

    public static boolean isVocaTypeWord(IVocaCoreItem item) {
        return isVocaTypeWord(item.getVIVocaType());
    }
    public static boolean isVocaTypeWord(Integer vocaType) {
        return vocaType == Constant.API_VALUE.VALUE_VOCA_TYPE_WORD;
    }

    public static boolean isVocaTypeSentence(IVocaCoreItem item) {
        return isVocaTypeSentence(item.getVIVocaType());
    }
    public static boolean isVocaTypeSentence(Integer vocaType) {
        return vocaType == Constant.API_VALUE.VALUE_VOCA_TYPE_SENTENCE;
    }

    public static boolean isVocaTypeSubtitle(IVocaCoreItem item) {
        return isVocaTypeSubtitleTuned(item) || isVocaTypeSubtitleUntuned(item);
    }

    public static boolean isVocaTypeSubtitleTuned(IVocaCoreItem item) {
        return (item.getVIVocaType() == Constant.API_VALUE.VALUE_VOCA_TYPE_SUBTITLE);
    }

    public static boolean isVocaTypeSubtitleUntuned(IVocaCoreItem item) {
        return (item.getVIVocaType() == Constant.API_VALUE.VALUE_VOCA_TYPE_SUBTITLE_UNTUNED);
    }

    public static boolean isVocaTypeBook(Integer vocaType) {
        return vocaType == Constant.API_VALUE.VALUE_VOCA_TYPE_BOOK;
    }

    public static int switchVocaKnow(int know) {
        if (know == Constant.VOCA_KNOW.VOCA_KNOW_KNOWN) {
            return Constant.VOCA_KNOW.VOCA_KNOW_UNKNOWN;
        }
        return Constant.VOCA_KNOW.VOCA_KNOW_KNOWN;
    }

    public static int setVocaKnowPronounceToKnownWhenVocaKnowIsKnown(int vocaKnow, int vocaKnowPronounce) {
        if (vocaKnow == Constant.VOCA_KNOW.VOCA_KNOW_KNOWN) {
            vocaKnowPronounce = vocaKnow;
        }
        return vocaKnowPronounce;
    }
    public static boolean isVocaKnowOrVocaKnowPronounceChanged(int vocaKnowOld, int vocaKnowNew, int vocaKnowPronounceOld, int vocaKnowPronounceKnew) {
        return (vocaKnowOld == vocaKnowNew) && (vocaKnowPronounceOld == vocaKnowPronounceKnew);
    }

    public static void updateImageViewStyleByVocaKnow(Context context, ImageView iv, TextView tv, int vocaKnow) {
        updateIconVocaKnow(context, tv, vocaKnow);
        tv.setVisibility(View.VISIBLE);
        iv.setVisibility(View.GONE);
    }


    public static IVocaBasicItem switchVocaKnow(IVocaBasicItem iVocaBasicItem) {
        if (iVocaBasicItem.getVIVocaKnow() < Constant.VOCA_KNOW.VOCA_KNOW_KNOWN) {
            iVocaBasicItem.setVIVocaKnow(Constant.VOCA_KNOW.VOCA_KNOW_KNOWN);
            iVocaBasicItem.setVIVocaKnowPronounce(Constant.VOCA_KNOW.VOCA_KNOW_KNOWN);
        } else {
            iVocaBasicItem.setVIVocaKnow(Constant.VOCA_KNOW.VOCA_KNOW_UNKNOWN);
            iVocaBasicItem.setVIVocaKnowPronounce(Constant.VOCA_KNOW.VOCA_KNOW_UNKNOWN);
        }
        return iVocaBasicItem;
    }

    public static boolean isUnknownPronounceWhenKnownWord(IVocaBasicItem iVocaBasicItem) {
        return ((iVocaBasicItem.getVIVocaKnow() == Constant.VOCA_KNOW.VOCA_KNOW_KNOWN) &&
                ((iVocaBasicItem.getVIVocaKnowPronounce() < Constant.VOCA_KNOW.VOCA_KNOW_KNOWN) && (iVocaBasicItem.getVIVocaKnowPronounce() > Constant.VOCA_KNOW.VOCA_KNOW_NOTRATED)));
    }

//    public static void updateBookmarkIcon(ImageView view, boolean isBookmark) {
//        view.setImageResource(isBookmark ? R.drawable.ic_favorite_new_on : R.drawable.ic_favorite_new_off);
//    }

    public static void updateIconVocaKnowPronounce(Context context, ImageView view, IVocaBasicItem item) {
        boolean showKnowPronounceIcon = showKnowPronounceIcon(SharedPreferencesDB.getInstance(context).getLangStudyCode(), item.getVIVocaKnow(), item.getVIVocaKnowPronounce());
        view.setVisibility(showKnowPronounceIcon ? View.VISIBLE : View.INVISIBLE);
        view.setImageResource(isUnknownPronounceWhenKnownWord(item) ? R.drawable.ic_unknown_pronounce : R.drawable.ic_known_pronounce);
    }

    public static void updateIconVocaBookmark(ImageView view, IVocaBasicItem iVocaBasicItem, boolean isDispalyIconAlways) {
        updateIconVocaBookmark(view, iVocaBasicItem.isVIBookmark(), isDispalyIconAlways);

    }

    public static void updateIconVocaBookmark(ImageView view, boolean isBookmark, boolean isDispalyIconAlways) {
        if (isDispalyIconAlways) {
            view.setVisibility(View.VISIBLE);
        } else {
            view.setVisibility(isBookmark ? View.VISIBLE : View.INVISIBLE);
        }
        view.setImageResource(isBookmark ? R.drawable.ic_favorite_new_on : R.drawable.ic_favorite_new_off);
    }

    public static void updateIconVocaKnow(Context context, TextView tv, int know) {
        if (isKnown(know)) {
            tv.setText(Constant.AMKI_GRADE.DISPLAY_KNOWN);
            tv.setBackgroundResource(R.drawable.bg_circle_voca_know_known);
        } else if (isAmkiGrade1(know)) {
            tv.setText(Constant.AMKI_GRADE.DISPLAY_A);
            tv.setBackgroundResource(R.drawable.bg_circle_voca_know_amki_grade_1);
        } else if (isAmkiGrade2(know)) {
            tv.setText(Constant.AMKI_GRADE.DISPLAY_B);
            tv.setBackgroundResource(R.drawable.bg_circle_voca_know_amki_grade_2);
        } else if (isNotRated(know)) {
            tv.setText(Constant.AMKI_GRADE.DISPLAY_NOT_RATED);
            tv.setBackgroundResource(R.drawable.bg_circle_voca_know_not_determined);
        } else {
            tv.setText(Constant.AMKI_GRADE.DISPLAY_UNKNOWN);
            tv.setBackgroundResource(R.drawable.bg_circle_voca_know_unknown);
        }
    }

    public static void updateVocaColor(Context context, TextView tv, int know) {
        if (isKnown(know)) {
            tv.setTextColor(ContextCompat.getColor(context, R.color.color_ruby_text_normal));
        } else {
            tv.setTextColor(ContextCompat.getColor(context, R.color.color_ruby_text_unknown_meaning));
        }
    }

    /*
     * @deprecated Replaced by {@link #updateIconVocaKnow()}
     */
    @Deprecated
    public static void updateIconVocaKnow(Context context, ImageView imageView, int vocaKnow) {
        if (isKnown(vocaKnow)) {
            imageView.setImageResource(R.drawable.ic_voca_know_circle2_known);
        } else if (isAmkiGrade1(vocaKnow)) {
            imageView.setImageResource(R.drawable.ic_voca_know_circle2_1st);
        } else if (isAmkiGrade2(vocaKnow)) {
            imageView.setImageResource(R.drawable.ic_voca_know_circle2_2nd);
        } else if (isNotRated(vocaKnow)) {
            imageView.setImageResource(R.drawable.ic_voca_know_circle2_notrated);
        } else {
            imageView.setImageResource(R.drawable.ic_voca_know_circle2_unknown);
        }
    }

    public static void updateIconVocaKnow(ImageView imageView, int vocaKnow) {
        if (isKnown(vocaKnow)) {
            imageView.setImageResource(R.drawable.ic_voca_know_circle2_known);
        } else if (isAmkiGrade1(vocaKnow)) {
            imageView.setImageResource(R.drawable.ic_voca_know_circle2_1st);
        } else if (isAmkiGrade2(vocaKnow)) {
            imageView.setImageResource(R.drawable.ic_voca_know_circle2_2nd);
        } else if (isNotRated(vocaKnow)) {
            imageView.setImageResource(R.drawable.ic_voca_know_circle2_notrated);
        } else {
            imageView.setImageResource(R.drawable.ic_voca_know_circle2_unknown);
        }
    }

    public static void updateIconVocaKnowGrayCircle(ImageView imageView, int vocaKnow) {
        if (isKnown(vocaKnow)) {
            imageView.setImageResource(R.drawable.ic_voca_know_gray_circle_known);
        } else if (isAmkiGrade1(vocaKnow)) {
            imageView.setImageResource(R.drawable.ic_voca_know_gray_circle_1st);
        } else if (isAmkiGrade2(vocaKnow)) {
            imageView.setImageResource(R.drawable.ic_voca_know_gray_circle_2nd);
        } else if (isNotRated(vocaKnow)) {
            imageView.setImageResource(R.drawable.ic_voca_know_gray_circle_not_rated);
        } else {
            imageView.setImageResource(R.drawable.ic_voca_know_gray_circle_unknown);
        }
    }

    public static void updateIconVocaKnowText(ImageView imageView, int vocaKnow) {
        if (isKnown(vocaKnow)) {
            imageView.setImageResource(R.drawable.ic_voca_know_text_known);
        } else if (isAmkiGrade1(vocaKnow)) {
            imageView.setImageResource(R.drawable.ic_voca_know_text_1st);
        } else if (isAmkiGrade2(vocaKnow)) {
            imageView.setImageResource(R.drawable.ic_voca_know_text_2nd);
        } else if (isNotRated(vocaKnow)) {
            imageView.setImageResource(R.drawable.ic_voca_know_text_not_rated);
        } else {
            imageView.setImageResource(R.drawable.ic_voca_know_text_unknown);
        }
    }

    /*
     * @deprecated Replaced by {@link #updateIconVocaKnow()}
     */
    @Deprecated
    public static void updateIconVocaKnow(Context context, ImageView imageView, IVocaBasicItem voca) {
        int vocaKnow = voca.getVIVocaKnow() == null
                            ? Constant.VOCA_KNOW.VOCA_KNOW_NOTRATED
                            : voca.getVIVocaKnow();
        updateIconVocaKnow(context, imageView, vocaKnow);
    }

    public static void updateIconVocaKnow(ImageView imageView, IVocaBasicItem voca) {
        updateIconVocaKnow(imageView, getVocaKnow(voca));
    }

    public static void updateIconVocaKnowGrayCircle(ImageView imageView, IVocaBasicItem voca) {
        updateIconVocaKnowGrayCircle(imageView, getVocaKnow(voca));
    }

    public static void updateIconVocaKnowText(ImageView imageView, IVocaBasicItem voca) {
        updateIconVocaKnowText(imageView, getVocaKnow(voca));
    }

    public static int getVocaKnow(IVocaBasicItem voca) {
        return voca.getVIVocaKnow() == null
                ? Constant.VOCA_KNOW.VOCA_KNOW_NOTRATED
                : voca.getVIVocaKnow();
    }

    public static boolean isShowVocaKnowPronounceIcon(IVocaBasicItem item) {
        return isUnknownPronounceWhenKnownWord(item);
    }

    public static boolean showKnowPronounceIcon(int studyLangCode, int vocaKnow, int vocaKnowPronounce) {
        boolean blnResult = false;

        if ((studyLangCode == EnumLanguage.CHINESE_SIMPLIFIED.getIdApi())
                || (studyLangCode == EnumLanguage.CHINESE_TRADITIONAL.getIdApi())
                || (studyLangCode == EnumLanguage.JAPANESE.getIdApi())) {
            if (vocaKnow >= Constant.VOCA_KNOW.VOCA_KNOW_KNOWN) {
                if ((vocaKnowPronounce > Constant.VOCA_KNOW.VOCA_KNOW_NOTRATED) && (vocaKnowPronounce < Constant.VOCA_KNOW.VOCA_KNOW_KNOWN))
                    blnResult = true;
            }
        } else if (studyLangCode == EnumLanguage.KOREAN.getIdApi()) {
        } else {
            if (vocaKnow < Constant.VOCA_KNOW.VOCA_KNOW_KNOWN) {
                blnResult = false;
            } else if ((vocaKnowPronounce > Constant.VOCA_KNOW.VOCA_KNOW_NOTRATED) && (vocaKnowPronounce < Constant.VOCA_KNOW.VOCA_KNOW_KNOWN)) {
                blnResult = true;
            }
        }

        return blnResult;
    }
    //------

    public static int getVocaKnowPronounceByVocaKnow(int vocaKnow) {
        return vocaKnow == Constant.VOCA_KNOW.VOCA_KNOW_KNOWN ? Constant.VOCA_KNOW.VOCA_KNOW_KNOWN : Constant.VOCA_KNOW.VOCA_KNOW_UNKNOWN;
    }

    public static String getOneLineDifficultWordAndMeaning(Context context, List<IVocaFullPlayTTSItem> wordList) {
        return wordList.stream().filter(e -> !Utils.isEmpty(e.getVIMeaning(LanguageUtil.getMotherTongueLanguage(context))))
                .map(e -> e.getVIVoca() + "(" + e.getVIMeaning(LanguageUtil.getMotherTongueLanguage(context)) + ")").collect(Collectors.joining(", "));
    }

//    public static VocaTypeIdListWithComma getVocaTypeAndIDListWithComma(List<IVocaBasicItem> vocaList) {
//        StringJoiner sjVocaType = new StringJoiner(",");
//        StringJoiner sjVocaID = new StringJoiner(",");
//        for(IVocaBasicItem voca : vocaList) {
//            sjVocaType.add(String.valueOf(voca.getVIVocaType()));
//            sjVocaID.add(String.valueOf(voca.getVIVocaId()));
//        }
//
//        return new VocaTypeIdListWithComma(sjVocaType.toString(), sjVocaID.toString());
//    }
//
//    public static String getVocaListInStringWithEnter(List<? extends IVocaBasicItem> list) {
//        return getVocaListInStringCore(list, "\t");
//    }
//    private static String getVocaListInStringCore(List<? extends IVocaBasicItem> list, String delimiter) {
//        return list.stream().map(e -> e.getVIVoca()).collect(Collectors.joining(delimiter));
//    }
//    public static String getVocaKnowChangedText(List<? extends IVocaBasicItem> listBefore, List<? extends IVocaBasicItem> listAfter) {
//        StringJoiner result = new StringJoiner("\n");
//        for (int i = 0; i < listBefore.size(); i++) {
//            IVocaBasicItem originalItem = listBefore.get(i);
//            IVocaBasicItem updatedItem = listAfter.get(i);
//            int originalItemId = originalItem.getVIId();
//            int updatedItemId = updatedItem.getVIId();
//            int originalItemVocaKnow = originalItem.getVIVocaKnow();
//            int updatedItemVocaKnow = updatedItem.getVIVocaKnow();
//            if (originalItem.getVIId().equals(updatedItem.getVIId()) && originalItem.getVIVocaKnow() != updatedItem.getVIVocaKnow()) {
//                int id = originalItem.getVIId();
//                int oldVocaKnow = originalItem.getVIVocaKnow();
//                int newVocaKnow = updatedItem.getVIVocaKnow();
//                System.out.println("Item with id " + id + " changed from " + oldVocaKnow + " to " + newVocaKnow);
//                result.add(originalItem.getVIVoca());
//            }
//        }
//        return result.toString().trim();
//    }
//
//    public static String getAllVocasAsString(List<? extends IVocaBasicItem> list) {
//        return list.stream()
//                .map(e -> e.getVIVoca())
//                .collect(Collectors.joining("\n"));
//    }
//
//    public static String getUnknownVocasAsString(List<? extends IVocaBasicItem> list) {
//        return list.stream()
//                .filter(e -> isUnknownAndLess(e.getVIVocaKnow()))
//                .map(e -> e.getVIVoca())
//                .collect(Collectors.joining("\n"));
//    }
//
//    public static String getKnownVocasAsString(List<? extends IVocaBasicItem> list) {
//        return list.stream()
//                .filter(e -> isKnown(e.getVIVocaKnow()))
//                .map(e -> e.getVIVoca())
//                .collect(Collectors.joining("\n"));
//    }
}
