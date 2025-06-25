package com.dalread.util;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.AttrRes;
import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.core.content.ContextCompat;

import com.dalread.R;

public class AraThemeUtil {
  //colorPrimary등을 가져올때 사용. getThemeColor(R.attr.colorPrimary)같은 형식.
  @ColorInt
  public static int getThemeColor(Context context, @AttrRes int themeAttrId) {
    TypedArray a = context.obtainStyledAttributes(new int[]{themeAttrId});
    try {
      return a.getColor(0, Color.TRANSPARENT);
    } finally {
      a.recycle();
    }
  }

  public static int getMaterialAlertDialogResId() {
    return AppFlavorUtil.isAraMultiPlayerApp() ?  R.style.MultiMaterialAlertDialogMaterial3 : R.style.AraMaterialAlertDialogMaterial3;
  }
  // 테마 리소스 ID를 결정하는 메서드
  public static int getThemeResId(Context context) {
      return AppFlavorUtil.isAraMultiPlayerApp() ? R.style.AraMultiPlayerTheme : R.style.BaseTheme;
  }

  public static int getSingleChoiceDialogThemeResId() {
    return AppFlavorUtil.isAraMultiPlayerApp() ? R.style.MultiSingleChoiceDialog : R.style.VocaAlertDialog;
  }

  public static int getYesNoDialogThemeResId() {
    return AppFlavorUtil.isAraMultiPlayerApp() ? R.style.MultiPlayerTransparentDialog : R.style.TransparentDialog;
  }

  // YesNoDialog의 Title과 Body의 글자색을 설정하는 메서드
  public static void setYesNoDialogBodyTextColor(Context context, TextView textView) {
    textView.setTextColor(ContextCompat.getColor(context, AppFlavorUtil.isAraMultiPlayerApp() ? R.color.textPrimaryWhiteColor : R.color.textPrimaryColor));
  }

  public static void setYesNodDialogBodyBackgroundColor(Context context, View view) {
    view.setBackgroundColor(ContextCompat.getColor(context, AppFlavorUtil.isAraMultiPlayerApp() ? R.color.multiPlayerBackgroundLightBlackColor : R.color.backgroundWhiteColor));
  }

  public static void setDialogBtnTextColor(Context context, TextView textView) {
    textView.setTextColor(ContextCompat.getColor(context, AppFlavorUtil.isAraMultiPlayerApp() ? R.color.textPrimaryWhiteColor : R.color.textPrimaryColor));
  }

  public static void setDialogRedBtnTextColor(Context context, TextView textView) {
    textView.setTextColor(ContextCompat.getColor(context, AppFlavorUtil.isAraMultiPlayerApp() ? R.color.textRedColor : R.color.textRedColor));
  }

  public static void setDialogStyle(View view) {
    if (view instanceof TextView) {
      TextView textView = (TextView) view;
      int styleResId = getButtonBaseResId();
      textView.setTextAppearance(styleResId);
    }
  }
  //코드로 이렇게 변경하는건 잘안되는거 같음. style을 통채로는 안되어서 background만 따로 해야한다. 이건 그냥둠.
  private static int getButtonBaseResId() {
    return AppFlavorUtil.isAraMultiPlayerApp() ? R.style.Multi_Button_Base : R.style.Button_Base;
  }

  public static void setTextColor(Context context, TextView textView, @ColorRes int colorResId) {
    textView.setTextColor(ContextCompat.getColor(context, colorResId));
  }

  public static void setBackgroundColor(Context context, View view, @ColorRes int colorResId) {
    setBackgroundColor(view, ContextCompat.getColor(context, colorResId));
  }

  public static void setHintTextColor(Context context, TextView textView, @ColorRes int colorResId) {
    textView.setHintTextColor(ContextCompat.getColor(context, colorResId));
  }

  public static void setBackgroundColor(View view, @ColorInt int color) {
    view.setBackgroundColor(color);
  }
}
