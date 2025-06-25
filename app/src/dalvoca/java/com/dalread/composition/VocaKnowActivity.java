package com.dalread.composition;

import android.app.Activity;

import com.dalread.interfaces.IVocaBasicItem;

public class VocaKnowActivity extends AbstractBaseVocaKnowActivity {
    public VocaKnowActivity(Activity activity) {
        super(activity);
    }



    @Override
    protected void updateVocaKnowInDB(IVocaBasicItem voca, int newVocaKnow) {
//        Voca.updateVocaKnow((HanjaItem)voca, newVocaKnow, BaseVocaKnow.getVocaKnowPronounceByVocaKnow(newVocaKnow));
    }
    @Override
    protected void updateVocaKnowPronounceInDB(IVocaBasicItem voca, int newVocaKnowPronounce) {
//        Voca.updateVocaKnow((HanjaItem)voca, voca.getVIVocaKnow(), newVocaKnowPronounce);
    }

    @Override
    protected void udpateBookmarkInLocalDB(IVocaBasicItem item) {
//        int vocaType = item.getVIVocaType();
//        long voacId = item.getVIVocaId().longValue();
//        if (VocaKnow.isVocaTypeWord(vocaType)) {
//            DIC_HANJA dicHanja = Voca.searchHanjaWordByID(voacId);
//            dicHanja.swapBOOKMARK();
//            Voca.updateHanjaWord(dicHanja);
//        } else if (VocaKnow.isVocaTypeSentence(vocaType)) {
//            DIC_HANJA_SENTENCE dicHanjaSentence = Voca.searchHanjaSentenceByID(voacId);
//            dicHanjaSentence.swapBOOKMARK();
//            Voca.updateHanjaSentence(dicHanjaSentence);
//        } else if (VocaKnow.isVocaTypeBook(vocaType)) {
//            DIC_HANJA_BOOK dicHanjaBook = Voca.searchHanjaBookByID(voacId);
//            dicHanjaBook.swapBOOKMARK();
//            Voca.updateHanjaBook(dicHanjaBook);
//        }
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
