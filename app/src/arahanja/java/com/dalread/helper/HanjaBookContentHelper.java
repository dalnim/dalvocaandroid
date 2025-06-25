package com.dalread.helper;

import com.dalread.database.SharedPreferencesDB;
import com.dalread.model.VOCABOOKS_HANJA_CLASSICS;
import com.dalread.util.PurchasedBooksUtil;

public class HanjaBookContentHelper {
    VOCABOOKS_HANJA_CLASSICS vocabooksHanjaClassics;
    SharedPreferencesDB sharedPreferencesDB;

    public HanjaBookContentHelper(VOCABOOKS_HANJA_CLASSICS vocabooksHanjaClassics, SharedPreferencesDB sharedPreferencesDB) {
        this.vocabooksHanjaClassics = vocabooksHanjaClassics;
        this.sharedPreferencesDB = sharedPreferencesDB;
    }

    public boolean isPurchasedBook() {
        if (vocabooksHanjaClassics.getID().equals(PurchasedBooksUtil.bookIdOfThousandCharacter)) {
            return PurchasedBooksUtil.isPurchasedThousandCharacter(sharedPreferencesDB);
        }
        return false;
    }
}
