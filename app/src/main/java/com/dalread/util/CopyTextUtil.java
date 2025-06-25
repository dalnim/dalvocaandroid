package com.dalread.util;

import static android.content.Context.CLIPBOARD_SERVICE;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Handler;
import android.view.View;

import com.dalread.R;
import com.dalread.database.SharedPreferencesDB;
import com.dalread.dialog.SingleChoiceDialog;
import com.dalread.interfaces.IVocaBasicItem;
import com.dalread.interfaces.IVocaFullPlayTTSItem;
import com.dalread.listener.OnClickDialogListener;
import com.dalread.model.ConversationModel;

import java.util.List;
import java.util.StringJoiner;

public class CopyTextUtil {
    public static final String TAG = "Utils";
    public static String getMessageInToastToShow(Context context, String strToCopy) {
        int maxStringLength = 60;
        return StringUtils.getSubstringWithMoreText(context.getString(R.string.copied) + "\n" + strToCopy, maxStringLength);
    }

    public static void copyToClipboard(Context mContext, final String text, final int stringResourceId) {
        copyToClipboard(mContext, text, mContext.getString(stringResourceId));
    }
    public static void copyToClipboard(Context context, final String text, final String toastText) {
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Copied Text", text);
        clipboard.setPrimaryClip(clip);
        ToastUtil.getInstance(context).show(toastText);
    }

    public static void copyToClipboardShowWhatCopied(Context context, final String text) {
        if (Utils.isEmpty(text))
            return;

        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Copied Text", text);
        clipboard.setPrimaryClip(clip);
        String toastText = getMessageInToastToShow(context, text);
        ToastUtil.getInstance(context).show(toastText);
    }

    public static void copyToClipboard(Context mContext, final String text) {
        DLog.d(TAG, "setTextClipboard - text=" + text);
        ClipboardManager clipboard = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Copied Text", text);
        clipboard.setPrimaryClip(clip);
    }

    public static void openCopyConversationDialog(Context context, List<IVocaFullPlayTTSItem> vocaList, boolean isAddIndex) {
        String[] displayOptions = context.getResources().getStringArray(R.array.array_choice_copy_dialogs);
        SingleChoiceDialog singleChoiceDialog = new SingleChoiceDialog(context);
        singleChoiceDialog.showWrapContentHeight(
                R.string.menu_to_copy,
                displayOptions,
                1,
                R.string.ok,
                R.string.cancel,
                new OnClickDialogListener() {
                    @Override
                    public void onClick(View view, Object object) {
                        copyToClipboard((int) object);
                    }

                    private void copyToClipboard(int object) {
                        final int which = object;
                        StringJoiner joiner = new StringJoiner("\n");
                        for (IVocaFullPlayTTSItem item : vocaList) {
                            String prefix = "";
                            if (isAddIndex) {
//                                prefix = item.getVIIndex() + "\t" + item.getPersonAB() + "\t";
                                prefix = item.getPersonAB() + "\t";
                            }
                            switch (which) {
                                case 0:
                                    joiner.add(prefix + item.getVIVoca());
                                    break;
                                case 1:
                                    joiner.add(prefix + item.getVIVocaMeaning(context));// item.getVIVoca() + "\t" + item.getVIMeaning(LanguageUtil.getMotherTongueLanguage(context)));
                                    break;
                                case 2:
                                    joiner.add(prefix + item.getVIMeaning(LanguageUtil.getMotherTongueLanguage(context)));
                                    break;
                            }
                        }
                        String strToCopy = joiner.toString();
//                        String toastText = CopyTextUtil.getMessageInToastToShow(context, strToCopy);
                        copyToClipboardShowWhatCopied(context, strToCopy);
                    }

                    @Override
                    public void onDismiss(View view, Object object) {

                    }
                });
    }

    public static void openCopyVoca(Context context, IVocaBasicItem voca) {
        String[] displayOptions = context.getResources().getStringArray(R.array.array_choice_copy_dialogs);
        SingleChoiceDialog singleChoiceDialog = new SingleChoiceDialog(context);
        singleChoiceDialog.showWrapContentHeight(
            R.string.menu_to_copy,
            displayOptions,
            1,
            R.string.ok,
            R.string.cancel,
            new OnClickDialogListener() {
                @Override
                public void onClick(View view, Object object) {
                    final int which = (int)object;
                    String strToCopy = "";

                        switch (which) {
                            case 0:
                                strToCopy = voca.getVIVoca();
                                break;
                            case 1:
                                strToCopy = voca.getVIVoca() + "\t" + voca.getVIMeaning(LanguageUtil.getMotherTongueLanguage(context));
                                break;
                            case 2:
                                strToCopy = voca.getVIMeaning(LanguageUtil.getMotherTongueLanguage(context));
                                break;
                        }

                    copyToClipboardShowWhatCopied(context, strToCopy);
                }

                @Override
                public void onDismiss(View view, Object object) {

                }
            });
    }
    public static String getTextFromClipboard(Context context) {
        String str = "";
        ClipboardManager cm = (ClipboardManager) context.getSystemService(CLIPBOARD_SERVICE);
        if ((cm != null) && (cm.hasPrimaryClip() == true)) {
            ClipData clip = cm.getPrimaryClip();
            ClipData.Item item = clip.getItemAt(0);
            if ((item != null) && (item.getText() != null)) {
                str = item.getText().toString();
            }
        }
        return str;
    }
    //getTextFromClipboard가 안먹는 경우는 아래를 사용하자.
    public static void getTextFromClipboardWithDelay(Context context, final OnTextFromClipboardListener listener) {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                String str = "";
                ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                if (cm != null && cm.hasPrimaryClip()) {
                    ClipData clip = cm.getPrimaryClip();
                    ClipData.Item item = clip.getItemAt(0);
                    if (item != null && item.getText() != null) {
                        str = item.getText().toString();
                    }
                }
                listener.onTextFromClipboard(str);
            }
        }, 100);
    }

    public interface OnTextFromClipboardListener {
        void onTextFromClipboard(String text);
    }

    public interface CopyConvPracticeListener {
        public void countOfSentencesToCopy(int countToCopy);
    }
    public static void openCopyConvPractice(Context context, CopyConvPracticeListener listener) {
        String[] displayOptions = context.getResources().getStringArray(R.array.array_choice_copy_sentence_conv_practice);
        int checkedItem = SharedPreferencesDB.getInstance(context).getChoiceCopySentenceConvPractice();
        SingleChoiceDialog singleChoiceDialog = new SingleChoiceDialog(context);
        singleChoiceDialog.showWrapContentHeight(
                R.string.dialog_title_copy_sentence_conv_practice,
                displayOptions,
                checkedItem,
                R.string.ok,
                R.string.cancel,
                new OnClickDialogListener() {
                    @Override
                    public void onClick(View view, Object object) {
                        final int which = (int)object;
                        SharedPreferencesDB.getInstance(context).setChoiceCopySentenceConvPractice(which);
                        if (listener != null) {
                            listener.countOfSentencesToCopy(which);
                        }
                    }

                    @Override
                    public void onDismiss(View view, Object object) {

                    }
                });
    }

    public static void openCopyConversationModel(Context context, ConversationModel item) {
        String[] displayOptions = context.getResources().getStringArray(R.array.array_choice_copy_dialogs);
        SingleChoiceDialog singleChoiceDialog = new SingleChoiceDialog(context);
        singleChoiceDialog.showWrapContentHeight(
                R.string.menu_to_copy,
                displayOptions,
                1,
                R.string.ok,
                R.string.cancel,
                new OnClickDialogListener() {
                    @Override
                    public void onClick(View view, Object object) {
                        final int which = (int)object;
                        String strToCopy = "";

                        switch (which) {
                            case 0:
                                strToCopy = item.getMESSAGE_CONTENT();
                                break;
                            case 1:
                                strToCopy = item.getMessageContentAndTranslation();
                                break;
                            case 2:
                                strToCopy = item.getMESSAGE_TRANSLATION();
                                break;
                        }

                        CopyTextUtil.copyToClipboardShowWhatCopied(context, strToCopy);
                    }

                    @Override
                    public void onDismiss(View view, Object object) {

                    }
                });
    }
}
