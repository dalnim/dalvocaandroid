package com.dalread.util;

import java.util.HashMap;
import java.util.Map;

public class AraPlayerInAppProductsClass extends InAppProductsClass {
    @Override
    public Map<String, AraInAppProduct> getPointProducts() {
        Map<String, AraInAppProduct> productMap = new HashMap<>(COMMON_PRODUCTS);
        productMap.put(inapp_buy_100_points, new AraInAppProduct(true, 100));
        productMap.put(inapp_buy_300_points, new AraInAppProduct(true, 300));
        productMap.put(inapp_buy_1000_points, new AraInAppProduct(true, 1000));
        return productMap;
    }
}
