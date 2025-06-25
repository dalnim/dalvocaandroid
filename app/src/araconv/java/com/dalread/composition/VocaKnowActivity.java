package com.dalread.composition;

import android.app.Activity;

import com.dalread.database.sqlite.SubDatabase;
import com.dalread.interfaces.IVocaBasicItem;
import com.dalread.util.Constant;

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
        subDatabase.updateVocaKnow(voca);
    }
    @Override
    protected void updateVocaKnowPronounceInDB(IVocaBasicItem voca, int newVocaKnowPronounce) {
        voca.setVIVocaKnowPronounce(newVocaKnowPronounce);
        subDatabase.updateVocaKnow(voca);
    }

    @Override
    protected void udpateBookmarkInLocalDB(IVocaBasicItem item) {
        subDatabase.swapBookmark(item);
    }

    @Override
    protected void udpateHasVoiceFileInLocalDB(IVocaBasicItem item) {
        subDatabase.setHasVoiceFile(item, item.hasVIVoiceFile() ? Constant.INT_BOOLEAN.TRUE : Constant.INT_BOOLEAN.FASLE);
    }
}
