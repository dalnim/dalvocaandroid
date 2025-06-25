package com.dalread.helper;

import android.app.Activity;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.QueryProductDetailsParams;
import com.dalread.util.AraPlayerInAppProductsClass;
import com.dalread.util.InAppProductsClass;
import com.dalread.util.PointUtil;
import com.dalread.util.ToastUtil;
import com.google.common.collect.ImmutableList;

import java.util.Map;

public class AraPlayerBillingClientHelper extends BillingClientHelper {
    public AraPlayerBillingClientHelper(Activity activity) {
        super(activity);
    }

    public static synchronized BillingClientHelper getInstance(Activity activity) {
        if (billingClientHelper == null) {
            billingClientHelper = new AraPlayerBillingClientHelper(activity);
        }
        return billingClientHelper;
    }

    @Override
    protected ImmutableList<QueryProductDetailsParams.Product> getProducts() {
        ImmutableList<QueryProductDetailsParams.Product> productList = super.getProducts();

        ImmutableList<QueryProductDetailsParams.Product> additionalProducts = ImmutableList.of(
                QueryProductDetailsParams.Product.newBuilder()
                        .setProductId(InAppProductsClass.inapp_buy_100_points)
                        .setProductType(BillingClient.ProductType.INAPP)
                        .build(),
                QueryProductDetailsParams.Product.newBuilder()
                        .setProductId(InAppProductsClass.inapp_buy_300_points)
                        .setProductType(BillingClient.ProductType.INAPP)
                        .build(),
                QueryProductDetailsParams.Product.newBuilder()
                        .setProductId(InAppProductsClass.inapp_buy_1000_points)
                        .setProductType(BillingClient.ProductType.INAPP)
                        .build()
        );

        return ImmutableList.<QueryProductDetailsParams.Product>builder()
                .addAll(productList)
                .addAll(additionalProducts)
                .build();
    }

    @Override
    protected void restorePurchases(String product) {
        if (product.equals(InAppProductsClass.inapp_remove_banner)) {
            ToastUtil.getInstance(activity).show("banner_ad_removal를 복원해야 합니다");
        }
    }
    @Override
    protected InAppProductsClass getInAppProductsClass() {
        return new AraPlayerInAppProductsClass();
    }

    @Override
    protected int onUpdatePoints(Purchase purchase) {
        int pointsToAdd = 0;
        Map<String, InAppProductsClass.AraInAppProduct> appProducts = new AraPlayerInAppProductsClass().getPointProducts();
        for (Map.Entry<String, InAppProductsClass.AraInAppProduct> entry : appProducts.entrySet()) {
            String key = entry.getKey();
            InAppProductsClass.AraInAppProduct product = entry.getValue();

            if (product.isConsumable() && purchase.getProducts().contains(key)) {
                pointsToAdd = product.getPoints();
                break;
            }
        }

        (new PointUtil(activity)).addPoint(pointsToAdd);
        return pointsToAdd;
    }
}
