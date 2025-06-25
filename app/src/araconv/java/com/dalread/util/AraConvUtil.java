package com.dalread.util;

import android.content.Context;
import android.view.View;

import com.dalread.BaseApplication;
import com.dalread.R;
import com.dalread.base.EnumLanguage;
import com.dalread.database.SharedPreferencesDB;
import com.dalread.database.sqlite.SubDatabase;
import com.dalread.dialog.SingleChoiceDialog;
import com.dalread.interfaces.IVocaFullPlayTTSItem;
import com.dalread.listener.OnClickDialogListener;
import com.dalread.network.GptUserOption;
import com.dalread.network.events.BaseEvent;
import com.dalread.network.events.SuccessEvent;

import java.util.List;
import java.util.StringJoiner;

public class AraConvUtil {
//    public static int vocaCountToCheck = 20;
    public static String getWholeConversationWithAB(Context context, List<IVocaFullPlayTTSItem> vocaList) {
        StringJoiner joiner = new StringJoiner("\n");
        for (IVocaFullPlayTTSItem item : vocaList) {
//            String prefix = item.getVIIndex() + "\t" + item.getPersonAB() + "\t : ";
            joiner.add(item.getPersonAB() + "\t : " + item.getVIVoca() + "\t" + item.getVIMeaning(LanguageUtil.getMotherTongueLanguage(context)));
        }
        return joiner.toString();
    }

    public static String getInstructionForRolePlayingWithGpt(Context context, SubDatabase subDatabase, String conversation, boolean isGptStartFirst) {
        StringJoiner joiner = new StringJoiner("\n");
        joiner.add(conversation);
        joiner.add("\n\n-----");
        String instruction = subDatabase.getGptShortCutToPracticeConversation(EnumLanguage.ENGLISH, isGptStartFirst);
        instruction += "(" + subDatabase.getGptShortCutToPracticeConversation(EnumLanguage.getMotherTongueLanguage(context), isGptStartFirst) + ")";
        joiner.add(instruction);
        return joiner.toString();
    }


    public static String getInstructionToRephraseConversationToGpt(Context context, SubDatabase subDatabase, String conversation) {
        StringJoiner joiner = new StringJoiner("\n");
        joiner.add(conversation);
        joiner.add("\n\n-----");
        String instruction = subDatabase.getGptShortCutListForMakeRolePlayFormat(EnumLanguage.getMotherTongueLanguage(context));
        instruction = StringUtils.replaceLanguageMarker(instruction, context);
        joiner.add(instruction);
        return joiner.toString();
    }

    public static void openLanguageLevelDialog(Context context, GptUserOption gptOptions) {
        String[] displayOptions = context.getResources().getStringArray(R.array.array_sign_up_word_level);
        SingleChoiceDialog singleChoiceDialog = new SingleChoiceDialog(context);
        singleChoiceDialog.showWrapContentHeight(
            R.string.menu_langauge_level,
            displayOptions,
            gptOptions.getStudentLangLevelIndex(),
            R.string.ok,
            R.string.cancel,
            new OnClickDialogListener() {
                @Override
                public void onClick(View view, Object object) {
                    final int which = (int) object;
                    GptUserOption selectedOption = GptUserOption.getGptOptionsByIndex(which);

                    SharedPreferencesDB.getInstance(context).setSettingMyLanguageLevel(selectedOption.getStudentLangLevel());
                    BaseApplication.getInstance().getEventBus().post(new SuccessEvent(BaseEvent.Screen.SETTING, BaseEvent.EventType.LANGUAGE_LEVEL_CHANGED, null));
                }

                @Override
                public void onDismiss(View view, Object object) {

                }
            });
    }
}
