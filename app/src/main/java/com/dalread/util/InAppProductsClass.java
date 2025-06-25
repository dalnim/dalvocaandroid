package com.dalread.util;

import java.util.HashMap;
import java.util.Map;

public abstract class InAppProductsClass {
    public static final String test_inapp_remove_banner = "test_remove_ad";
    public static final String test_inapp_points = "test_points_1";

    public static final String inapp_remove_banner = "remove_banner";
//    public static final String inapp_buy_20_points = "points_20";
//    public static final String inapp_buy_50_points = "points_50";
    public static final String inapp_buy_100_points = "points_100";
//    public static final String inapp_buy_200_points = "points_200";
    public static final String inapp_buy_300_points = "points_300";
    public static final String inapp_buy_1000_points = "points_1000";
    public static final String inapp_buy_3000_points = "points_3000";
    public static final String inapp_buy_10000_points = "points_10000";

    protected final Map<String, AraInAppProduct> COMMON_PRODUCTS = new HashMap<String, AraInAppProduct>() {{
        put(test_inapp_remove_banner, new AraInAppProduct(false, 0));
        put(test_inapp_points, new AraInAppProduct(true, 2));

        put(inapp_remove_banner, new AraInAppProduct(false, 0));
    }};

    protected abstract Map<String, AraInAppProduct> getPointProducts();

    public boolean isConsumable(String productId) {
        Map<String, AraInAppProduct> productMap = getPointProducts();
        if (productMap.containsKey(productId)) {
            return productMap.get(productId).isConsumable();
        }
        return false; // 기본적으로 non-consumable로 가정합니다.
    }

    // 제품 클래스
    public static class AraInAppProduct {
        private boolean consumable;  // 소비 가능 여부
        private int points;  // 포인트

        public AraInAppProduct(boolean consumable, int points) {
            this.consumable = consumable;
            this.points = points;
        }

        public boolean isConsumable() {
            return consumable;
        }

        public int getPoints() {
            return points;
        }
    }
}
