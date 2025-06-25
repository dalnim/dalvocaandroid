package com.dalread.composition;

import android.app.Activity;

import com.dalread.database.SubDatabase;
import com.dalread.interfaces.IVocaBasicItem;

public class VocaKnowActivity extends AbstractBaseVocaKnowActivity {
    protected SubDatabase subDatabase;
    public VocaKnowActivity(Activity activity, SubDatabase subDatabase) {
        super(activity);
        registerVocaDialog.setVisibilityBookmarkMenu(false);
        this.subDatabase = subDatabase;
    }

    @Override
    protected void updateVocaKnowInDB(IVocaBasicItem voca, int newVocaKnow) {
        voca.setVIVocaKnow(newVocaKnow);
    }
    @Override
    protected void updateVocaKnowPronounceInDB(IVocaBasicItem voca, int newVocaKnowPronounce) {
        voca.setVIVocaKnowPronounce(newVocaKnowPronounce);
    }

    @Override
    protected void udpateBookmarkInLocalDB(IVocaBasicItem item) {
        subDatabase.swapBookmark(item);
    }

    @Override
    protected void udpateHasVoiceFileInLocalDB(IVocaBasicItem item) {
    }

}
