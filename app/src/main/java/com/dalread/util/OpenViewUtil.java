package com.dalread.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;

import com.dalread.R;
import com.dalread.activity.EditMeaningActivity;
import com.dalread.activity.EditMeaningVocaBookActivity;
import com.dalread.activity.WebDictionaryActivity;
import com.dalread.database.WebDictionaryQuery;
import com.dalread.dialog.YesNoDialog;
import com.dalread.interfaces.IVocaFullItem;
import com.dalread.interfaces.IVocaFullPlayTTSItem;
import com.dalread.listener.OnYesNoClickListener;
import com.dalread.model.WebDictionaryModel;

public class OpenViewUtil {
    public static void openEditMeaningScreen(Context context, IVocaFullItem voca) {
        if (UserUtil.isLoggedIn(context, true)) {
            try {
                Intent intent = new Intent(context, EditMeaningActivity.class);
                intent.putExtra(Constant.BUNDLE.KEY_VOCA, voca);
                context.startActivity(intent);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
    public static void openEditMeaningVocaBookScreen(Context context, IVocaFullItem voca, int vocabookTypeCode) {
        context.startActivity(EditMeaningVocaBookActivity.createIntent(context, voca, vocabookTypeCode));
    }
    public static void openExternalWebDictionary(Context context, IVocaFullPlayTTSItem voca) {
        final WebDictionaryModel webDictionary = WebDictionaryQuery.getFirst(BaseVoca.getRealm(), LanguageUtil.getStudyLanguageCode(context), LanguageUtil.getMotherTongueLanguageCode(context));
        if (webDictionary != null) {
            String url = webDictionary.getUrl().replace(Constant.PLAYER.WEB_DICTIONARY.WORD_REPLACE, voca.getVIVoca());
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
        } else {
            final YesNoDialog dialog = new YesNoDialog(context,
                    R.string.warning,
                    R.string.msg_warning_web_dictionary_is_empty, null,
                    new OnYesNoClickListener() {
                        @Override
                        public void onYesClick(View view, Object object) {
                            Intent intent = new Intent(context, WebDictionaryActivity.class);
                            context.startActivity(intent);
                        }

                        @Override
                        public void onNoClick(View view, Object object) {

                        }
                    });
            dialog.show();
        }
    }

    public static void openGooglePlayStore(Context context, String url) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            intent.setPackage("com.android.vending");
            context.startActivity(intent);
        } catch (android.content.ActivityNotFoundException ex) {
            // If Google Play Store app is not available, open the URL in a web browser
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            context.startActivity(intent);
        }
    }

}
