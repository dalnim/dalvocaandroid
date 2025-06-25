package com.dalread.helper;

import android.content.Context;
import android.view.View;

import com.dalread.R;
import com.dalread.base.EnumLanguage;
import com.dalread.database.SharedPreferencesDB;
import com.dalread.database.sqlite.SubDatabase;
import com.dalread.dialog.SingleChoiceDialog;
import com.dalread.interfaces.IVocaFullPlayTTSItem;
import com.dalread.listener.OnClickDialogListener;
import com.dalread.model.GptTextShortCut;
import com.dalread.network.GptUserOption;
import com.dalread.util.BaseVocaList;
import com.dalread.util.StringUtils;
import com.dalread.util.Utils;

import java.util.List;

public class PromptUtil {
    public static String getPromptMessageVocaKnow(GptUserOption gptOptions, EnumLanguage motherTongue, List<IVocaFullPlayTTSItem> vocaListToStudy, boolean isShowInstuduction) {
        String prompt = "";//""뜻을 한국어로 설명해주세요."; //프롬프트에 뭔가를 추가를 하면 API에서 정상적으로 뭔가를 가져오지 못한다.
//        if (isShowInstuduction) {
//            prompt = "Please provide a detailed explanation in Korean for the following sentence including its grammatical explanation, and give me other English expressions for it, each on a separate line. ";
//        }
        String text = "";
        if (BaseVocaList.getCountOfUnknownInList(vocaListToStudy) > 0) {
            text = BaseVocaList.getUnknownVocasAsString(vocaListToStudy);// , motherTongue);
        } else {
            text = BaseVocaList.getAllVocasAsString(vocaListToStudy);//, motherTongue);
//            prompt += "" + vocas + "";
        }
        prompt += "\"" + text + "\"";
        return prompt;
    }

    public interface AskWhatToDoWithCopiedTextListener {
        public void selectedMenu(String menu);
    }
    public static void askWhatToDoWithCopiedText(Context context, SubDatabase subDatabase, String copiedText, AskWhatToDoWithCopiedTextListener listener) {
        List<GptTextShortCut> list = subDatabase.getGptShortCutListForPopupMenu1();
        int checkedItem = SharedPreferencesDB.getInstance(context).getChoiceAskWhatToDoWithCopiedText();
        if (!Utils.isIndexInsideRange(list, checkedItem)) {
            checkedItem = 1;
        }
        String[] displayOptions = list.stream().map( e -> e.getMEANINGByMenuLang(context)).toArray(String[]::new);
        SingleChoiceDialog singleChoiceDialog = new SingleChoiceDialog(context);
        singleChoiceDialog.showWrapContentHeight(
                R.string.dialog_title_ask_what_to_do_with_copied_text,
                displayOptions,
                checkedItem,
                R.string.ok,
                R.string.cancel,
                new OnClickDialogListener() {
                    @Override
                    public void onClick(View view, Object object) {
                        final int which = (int)object;
                        SharedPreferencesDB.getInstance(context).setChoiceAskWhatToDoWithCopiedText(which);
                        SharedPreferencesDB.getInstance(context).setConversationToStudyInGptWeb(copiedText);
                        String prefix = StringUtils.replaceLanguageMarker(context.getString(R.string.dialog_prefix_ask_what_to_do_with_copied_text), context);
                        if (which == 0) {
                            listener.selectedMenu(copiedText);
                        } else {
                            listener.selectedMenu(prefix + "\n\n" + copiedText + "\n\n-----\n" + displayOptions[which]);
                        }
                    }

                    @Override
                    public void onDismiss(View view, Object object) {

                    }
                });
    }
}
