package com.dalread.util;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import androidx.browser.customtabs.CustomTabsIntent;
import androidx.browser.customtabs.CustomTabsSession;
import androidx.core.content.ContextCompat;

import com.dalread.R;
import com.dalread.base.EnumGpt;
import com.dalread.database.SharedPreferencesDB;
import com.dalread.model.WordListType;

import java.util.LinkedHashMap;

public class ChatGptWebUtil {
    public static String intentExtraName = "MenuItem";
    public static String intentExtraValueOpenVocaList = "AraConvOpenVocaList";
    public static String intentExtraValueOpenSentenceDiffWords = "intentExtraValueOpenSentenceDiffWords";
    public static String intentExtraValueOpenTextShortCUt = "intentExtraValueOpenTextShortCUt";
    public static String intentExtraValueOpenMakeConversationList = "intentExtraValueOpenMakeConversationList";

    private static int bookId;
    private static String conversation;
    private static CustomTabsIntent customTabsIntent = null;
    private static LinkedHashMap<String, String> menuMap;
    public interface ConversationBookUtilInterface {
        boolean isConversationBook(WordListType type, int bookId);
    }

    public static String getGptUrl(Context context) {
        int gptStorage = SharedPreferencesDB.getInstance(context).getSelectedGpt();
        return EnumGpt.getUrlFromId(gptStorage);
    }
    public static void openUrlInCustomTabWithBookId(Context context, int inBookId, CustomTabsSession session) {
        bookId = inBookId;
        openUrlInCustomTabCommon(context, session);
    }

    public static void openUrlInCustomTabWithDialogue(Context context, String inConversation, CustomTabsSession session) {
        conversation = inConversation;
        bookId = -1;
        openUrlInCustomTabCommon(context, session);
    }

    public static void openUrlInCustomTabWithDialogueAndBookId(Context context, String inConversation, int inBookId, CustomTabsSession session) {
        conversation = inConversation;
        bookId = inBookId;
        openUrlInCustomTabCommon(context, session);
    }

    public static void openUrlInCustomTabWithLastBookId(Context context, String inConversation, CustomTabsSession session) {
        conversation = inConversation;
        bookId = (int) SharedPreferencesDB.getInstance(context).getLastReadBookId();
        openUrlInCustomTabCommon(context, session);
    }

    public static void openUrlInCustomTab(Context context, CustomTabsSession session) {
        conversation = "";
        bookId = (int) SharedPreferencesDB.getInstance(context).getLastReadBookId();
        openUrlInCustomTabCommon(context, session);
    }

    private static void openUrlInCustomTabCommon(Context context, CustomTabsSession session) {
        SharedPreferencesDB sharedPreferencesDB = SharedPreferencesDB.getInstance(context);
        if (sharedPreferencesDB.isFirstChatGptWebUse()) {
            DialogUtil.showPositiveDialog(context, context.getString(R.string.info), context.getString(R.string.dialog_message_first_chat_gpt_web_user), context.getString(R.string.ok), () -> {
                launchCustomTab(context, session);
                sharedPreferencesDB.setFirstChatGptWebUse();
            });
        } else if (sharedPreferencesDB.isSecondChatGptWebUse()) {
            DialogUtil.showPositiveDialog(context, R.string.info, R.string.dialog_message_second_chat_gpt_web_user, R.string.ok, () -> {
                launchCustomTab(context, session);
                sharedPreferencesDB.setSecondChatGptWebUse();
            });
        } else {
            launchCustomTab(context, session);
        }
//        sharedPreferencesDB.setConversationToStudyInGptWeb(conversation);
    }
    //https://github.com/GoogleChrome/android-browser-helper 참조 할것
    public static void launchCustomTab(Context context, CustomTabsSession session) {
        if (customTabsIntent == null) {
            CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder(session)
                    .setCloseButtonIcon(BaseUtilImage.xmlToBitmapDrawable(context, R.drawable.baseline_arrow_back_24)) //Close button은 24 싸이즈만 된다.
                    .setToolbarColor(ContextCompat.getColor(context, R.color.primaryColor))
                    .setStartAnimations(context, R.anim.slide_right_to_left_in, R.anim.slide_right_to_left_out)
                    .setExitAnimations(context, R.anim.slide_left_to_right_in, R.anim.slide_left_to_right_out);
            if (AppFlavorUtil.isAraConvApp()) {
//                addMenuItems(context, builder); //일단 메뉴는 넣지 말아보자.
            }
            addActionButton(context, builder);

            customTabsIntent = builder.build();
        }
        customTabsIntent.launchUrl(context, Uri.parse(getGptUrl(context)));
    }

    private static void addMenuItems(Context context, CustomTabsIntent.Builder builder, ConversationBookUtilInterface conversationBookUtil) {
        GptWebMenuHelper menuHelper = new GptWebMenuHelper(context);

        menuMap = menuHelper.getMenuList();
        // 일단 메뉴는 동적으로 안한다.
//        for (String key : menuMap.keySet()) {
//            builder.addMenuItem(key, createMenuPendingIntent(context, key));
//        }
        builder.addMenuItem(context.getString(R.string.menu_web_chat_gpt_show_difficult_words), createMenuPendingIntent(context, intentExtraValueOpenSentenceDiffWords));
//        builder.addMenuItem(context.getString(R.string.menu_web_chat_gpt_make_role_playing), createMenuPendingIntent(context, intentExtraValueOpenMakeConversationList));
        if (conversationBookUtil.isConversationBook(WordListType.BOOK, bookId)) {
            builder.addMenuItem(context.getString(R.string.menu_web_chat_current_study), createMenuPendingIntent(context, intentExtraValueOpenVocaList));
        }
    }

    private static void addActionButton(Context context, CustomTabsIntent.Builder builder) {
        builder.setActionButton(
                BaseUtilImage.xmlToBitmapDrawable(context, R.drawable.exo_ic_speed), //xml파일을 액션버튼에 넣으면 액션버튼이 "공유"기능이 동작해서 xml을 bitmap으로 바꾼다.
                "Text Shortcut",
                createSharePendingIntent(context),
                true
        );
    }

    private static PendingIntent createSharePendingIntent(Context context) {
        Intent intent = new Intent(context, GptWebMenuReceiver.class);
        intent.putExtra(intentExtraName, intentExtraValueOpenTextShortCUt);

        return PendingIntent.getBroadcast(
                context,
                0,
                intent,
                PendingIntent.FLAG_IMMUTABLE
        );
    }

    private static PendingIntent createMenuPendingIntent(Context context, String menuItem) {
        Intent intent = new Intent(context, GptWebMenuReceiver.class);
        intent.putExtra(intentExtraName, menuItem);
        return PendingIntent.getBroadcast(context, menuItem.hashCode(), intent, PendingIntent.FLAG_IMMUTABLE);
    }
//    private static PendingIntent createMenuPendingIntent(Context context, String menuItem) {
//        Intent intent;
//        if (menuItem.equals("Menu Item 3")) {
//            intent = new Intent(context, GptWebMenuActivity.class);
//        } else {
//            intent = new Intent(context, GptWebMenuReceiver.class);
//        }
//        intent.putExtra(intentExtraName, menuItem);
//        PendingIntent pendingIntent;
//        if (menuItem.equals("Menu Item 3")) {
//            pendingIntent = PendingIntent.getActivity(context, menuItem.hashCode(), intent, PendingIntent.FLAG_IMMUTABLE);
//        } else {
//            pendingIntent = PendingIntent.getBroadcast(context, menuItem.hashCode(), intent, PendingIntent.FLAG_IMMUTABLE);
//        }
//        return pendingIntent;
//    }

    public static String getConversation() {
        return conversation;
    }
    public static int getBookId() {
        return bookId;
    }

//    private static CustomTabsIntent customTabsIntent = null;
//
//    public static void openUrlInCustomTab(Context context, CustomTabsSession session) {
//        if (customTabsIntent == null) {
//            CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder(session)
//                    .setCloseButtonIcon(BaseUtilImage.xmlToBitmapDrawable(context, R.drawable.baseline_arrow_back_24)) //Close button은 24 싸이즈만 된다.
//                    .setToolbarColor(ContextCompat.getColor(context, R.color.primaryColor))
//                    .setStartAnimations(context, R.anim.slide_right_to_left_in, R.anim.slide_right_to_left_out)
//                    .setExitAnimations(context, R.anim.slide_left_to_right_in, R.anim.slide_left_to_right_out);
//
//            customTabsIntent = builder.build();
//        }
//        customTabsIntent.launchUrl(context, Uri.parse(Constant.URL_CHAT_GPT));
//    }
}
