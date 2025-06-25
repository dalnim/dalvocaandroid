package com.dalread.composition;

import android.content.Context;

import com.dalread.database.sqlite.SubDatabase;
import com.dalread.interfaces.IVocaBasicItem;
import com.dalread.model.DIC_HANJA;
import com.dalread.model.DIC_HANJA_BOOK;
import com.dalread.model.DIC_HANJA_SENTENCE;
import com.dalread.util.BaseVocaKnow;
import com.dalread.util.Constant;
import com.dalread.util.Voca;
import com.dalread.util.VocaKnow;

public class VocaKnowActivity extends AbstractBaseVocaKnowActivity {
    //아라한자는 subDatabase를 안쓴다. 컴파일 에러 방지용.
    protected SubDatabase subDatabase;
    public VocaKnowActivity(Context activity) {
        super(activity);
        registerVocaDialog.setVisibilityBookmarkMenu(false);
    }
    public VocaKnowActivity(Context activity, SubDatabase subDatabase) {
        super(activity);
        registerVocaDialog.setVisibilityBookmarkMenu(false);
        this.subDatabase = subDatabase;
    }


    //한자는 HanjaItem이 IVocaBasicItem을 상속받지 않아서 따로 처리한다. -> 이제는 IVocaCoreItem을 상속받아서 따로 처리 안해도 된다.
    @Override
    protected void updateVocaKnowInDB(IVocaBasicItem voca, int newVocaKnow) {
////        voca.setVIVocaKnow(newVocaKnow);
//
//        Voca.updateVocaKnow((HanjaItem)voca, newVocaKnow, BaseVocaKnow.getVocaKnowPronounceByVocaKnow(newVocaKnow));
        Voca.updateVocaKnow(voca, newVocaKnow, BaseVocaKnow.getVocaKnowPronounceByVocaKnow(newVocaKnow));
    }
    @Override
    protected void updateVocaKnowPronounceInDB(IVocaBasicItem voca, int newVocaKnowPronounce) {
////        voca.setVIVocaKnowPronounce(newVocaKnowPronounce);
//
//        Voca.updateVocaKnow((HanjaItem)voca, voca.getVIVocaKnow(), newVocaKnowPronounce);
        Voca.updateVocaKnow(voca, voca.getVIVocaKnow(), newVocaKnowPronounce);
    }

    @Override
    protected void udpateBookmarkInLocalDB(IVocaBasicItem item) {
        int vocaType = item.getVIVocaType();
        long voacId = item.getVIVocaId().longValue();
        //실제 DB에서는 BOOKMARK가 안바뀐 상태이므로, 여기서 바꿔져야한다.
        if (VocaKnow.isVocaTypeWord(vocaType)) {
            DIC_HANJA dicHanja = Voca.searchHanjaWordByID(voacId);
            dicHanja.swapBOOKMARK();
            Voca.updateHanjaWord(dicHanja);
        } else if (VocaKnow.isVocaTypeSentence(vocaType)) {
            DIC_HANJA_SENTENCE dicHanjaSentence = Voca.searchHanjaSentenceByID(voacId);
            dicHanjaSentence.swapBOOKMARK();
            Voca.updateHanjaSentence(dicHanjaSentence);
        } else if (VocaKnow.isVocaTypeBook(vocaType)) {
            DIC_HANJA_BOOK dicHanjaBook = Voca.searchHanjaBookByID(voacId);
            dicHanjaBook.swapBOOKMARK();
            Voca.updateHanjaBook(dicHanjaBook);
        }
    }

    protected void onVocaKnowChanged(int vocaID, int vocaKnow) {
        long vocaKnowPronounce = BaseVocaKnow.getVocaKnowPronounceByVocaKnow(vocaKnow);
        Voca.executeRealmTransaction(realm -> {
            DIC_HANJA hanja1 = realm.where(DIC_HANJA.class)
                    .equalTo(Constant.REALMDB.KEY_ID, vocaID)
                    .findFirst();
            if (hanja1 != null) {
                hanja1.setVOCA_KNOW((long) vocaKnow);
                hanja1.setVOCA_KNOWPRONOUNCE(vocaKnowPronounce);
            }
        });
    }

}
