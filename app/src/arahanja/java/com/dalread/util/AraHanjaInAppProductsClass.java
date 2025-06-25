package com.dalread.util;

import java.util.HashMap;
import java.util.Map;

public class AraHanjaInAppProductsClass extends InAppProductsClass {
    public static final String book_thousand_character_classic = "book_thousand_character_classic";

//    public Map<String, String> getClassicBooks() {
//        Map<String, String> productMap = new HashMap<>();
//        productMap.put(test_book_thousand_character_classic_1, "");
//        return productMap;
//    }

    @Override
    public Map<String, AraInAppProduct> getPointProducts() {
        Map<String, AraInAppProduct> productMap = new HashMap<>(COMMON_PRODUCTS);
        productMap.put(inapp_buy_1000_points, new AraInAppProduct(true, 1000));
        productMap.put(inapp_buy_3000_points, new AraInAppProduct(true, 3000));
        productMap.put(inapp_buy_10000_points, new AraInAppProduct(true, 10000));
        return productMap;
    }
}
