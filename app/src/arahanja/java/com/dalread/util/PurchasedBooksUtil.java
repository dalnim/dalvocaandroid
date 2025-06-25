package com.dalread.util;

import android.content.Context;

import com.dalread.database.SharedPreferencesDB;
import com.dalread.model.VOCABOOKS_HANJA_CLASSICS;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PurchasedBooksUtil {
    public static Long bookIdOfThousandCharacter = 49L;

    private static Map<Long, String> getBookIds(Context context) {
        Map<Long, String> bookIds = new HashMap<>();
        SharedPreferencesDB sharedPreferencesDB = SharedPreferencesDB.getInstance(context);
        if (isPurchasedThousandCharacter(sharedPreferencesDB)) {
            bookIds.put(bookIdOfThousandCharacter, AraHanjaInAppProductsClass.book_thousand_character_classic);
        }
        return bookIds;
    }
    public static boolean isPurchasedThousandCharacter(SharedPreferencesDB sharedPreferencesDB) {
        return sharedPreferencesDB.isPurchasedClassicBookThousandCharacter();
    }
    public static void updateBooks(List<VOCABOOKS_HANJA_CLASSICS> books, Context context) {
        Map<Long, String> purchasedBookIds = getBookIds(context);
        for (VOCABOOKS_HANJA_CLASSICS book : books) {
            if (purchasedBookIds.containsKey(book.getID())) {
                book.setUSED((long) Constant.VOCABOOKS.USED.USE_FOR_FREE);
            } else if (book.getID() != bookIdOfThousandCharacter){
                //일단 천자문이 아닌 다른 책도 현재는 무료로 해둔다.
                book.setUSED((long) Constant.VOCABOOKS.USED.USE_FOR_FREE);
            }
        }
    }
}

