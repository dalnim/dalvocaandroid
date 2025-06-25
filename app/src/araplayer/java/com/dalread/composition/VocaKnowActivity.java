package com.dalread.composition;

import android.app.Activity;

import com.dalread.database.sqlite.SubDatabase;
import com.dalread.interfaces.IVocaBasicItem;

public class VocaKnowActivity extends AbstractBaseVocaKnowActivity {
    private SubDatabase subDatabase;

    public VocaKnowActivity(Activity activity, SubDatabase subDatabase) {
        super(activity);
        this.subDatabase = subDatabase;
    }

    @Override
    protected void updateVocaKnowInDB(IVocaBasicItem voca, int newVocaKnow) {
        voca.setVIVocaKnow(newVocaKnow);
        subDatabase.updateVocaKnowInDB(voca);
    }
    @Override
    protected void updateVocaKnowPronounceInDB(IVocaBasicItem voca, int newVocaKnowPronounce) {
        voca.setVIVocaKnowPronounce(newVocaKnowPronounce);
        subDatabase.updateVocaKnowInDB(voca);
    }

    @Override
    protected void udpateBookmarkInLocalDB(IVocaBasicItem item) {
        subDatabase.updateBookmarkSubtitle(item);
    }

    protected void onVocaKnowChanged(int vocaID, int vocaKnow) {
//        long vocaKnowPronounce = BaseVocaKnow.getVocaKnowPronounceByVocaKnow(vocaKnow);
//        Voca.executeRealmTransaction(realm -> {
//            DIC_HANJA hanja1 = realm.where(DIC_HANJA.class)
//                    .equalTo(Constant.REALMDB.KEY_ID, vocaID)
//                    .findFirst();
//            if (hanja1 != null) {
//                hanja1.setVOCA_KNOW((long) vocaKnow);
//                hanja1.setVOCA_KNOWPRONOUNCE(vocaKnowPronounce);
//            }
//        });
    }

}
