package com.dalread.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.dalread.activity.GptWebTextShortCutActivity;

public class GptWebMenuReceiver extends BroadcastReceiver {
    private GptWebMenuHelper gptMenuHelper;
    private Context context;
    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        gptMenuHelper = new GptWebMenuHelper(context);
        String key = intent.getStringExtra(ChatGptWebUtil.intentExtraName);
        if (gptMenuHelper.hasKey(key)) {
            aaa(key);
        } else {
            if (key.equals(ChatGptWebUtil.intentExtraValueOpenTextShortCUt)) {
                Intent popupIntent = new Intent(context, GptWebTextShortCutActivity.class);
                popupIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); //BroadcastReceiver에서 액티비티를 띄울려면 이게 꼭 있어야 한다.
                context.startActivity(popupIntent);
            }
//            if (key.equals(ChatGptWebUtil.intentExtraValueOpenSentenceDiffWords)) {
//                Intent popupIntent = new Intent(context, SentenceDiffWordsActivity.class);
//                popupIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); //BroadcastReceiver에서 액티비티를 띄울려면 이게 꼭 있어야 한다.
//                context.startActivity(popupIntent);
//            } else if (key.equals(ChatGptWebUtil.intentExtraValueOpenMakeConversationList)) {
//                Intent popupIntent = new Intent(context, ConversationRawDataActivity.class);
//                popupIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); //BroadcastReceiver에서 액티비티를 띄울려면 이게 꼭 있어야 한다.
//                context.startActivity(popupIntent);
//            } else if (key.equals(ChatGptWebUtil.intentExtraValueOpenVocaList)) {
//                Intent popupIntent = new Intent(context, ConvVocaListActivity.class);
//                popupIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); //BroadcastReceiver에서 액티비티를 띄울려면 이게 꼭 있어야 한다.
//                popupIntent.putExtra(Constant.BUNDLE.KEY_VOCA_TYPE, Constant.API_VALUE.VALUE_VOCA_TYPE_SENTENCE);
//                popupIntent.putExtra(Constant.BUNDLE.KEY_WORDLIST_TYPE, WordListType.BOOK);
//                popupIntent.putExtra(Constant.BUNDLE.KEY_VOCA_BOOK_ID, ChatGptWebUtil.getBookId());
//                popupIntent.putExtra(Constant.BUNDLE.KEY_SHOW_FAB_BUTTON, false);
//                context.startActivity(popupIntent);
//            }
        }
    }

    private void aaa(String key) {
        String value = gptMenuHelper.getValue(key);
        String clipboardString = CopyTextUtil.getTextFromClipboard(context);
        String result = value.contains("%") ? value.replace("%", clipboardString) : value;
        CopyTextUtil.copyToClipboardShowWhatCopied(context, result);
    }
}