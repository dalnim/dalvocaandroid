package com.dalread.helper;

import android.app.Activity;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.ProductDetails;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.QueryProductDetailsParams;
import com.android.billingclient.api.QueryProductDetailsResult;
import com.dalread.AraHanjaApplication;
import com.dalread.BaseApplication;
import com.dalread.network.events.BaseEvent;
import com.dalread.network.events.SuccessEvent;
import com.dalread.util.AraHanjaInAppProductsClass;
import com.dalread.util.DLog;
import com.dalread.util.InAppProductsClass;
import com.dalread.util.PointUtil;
import com.dalread.util.ToastUtil;
import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class AraHanjaBillingClientHelper extends BillingClientHelper {
    private AraHanjaBillingClientHelper(Activity activity) {
        super(activity);
    }

    public static synchronized BillingClientHelper getInstance(Activity activity) {
        if (billingClientHelper == null) {
            billingClientHelper = new AraHanjaBillingClientHelper(activity);
        }
        return billingClientHelper;
    }

    @Override
    protected void querySpecificPurchases(ImmutableList.Builder<QueryProductDetailsParams.Product> productListBuilder) {
        productListBuilder.add(
                QueryProductDetailsParams.Product.newBuilder()
                        .setProductId(AraHanjaInAppProductsClass.book_thousand_character_classic)
                        .setProductType(BillingClient.ProductType.INAPP)
                        .build()
        );
    }

    @Override
    protected ImmutableList<QueryProductDetailsParams.Product> getProducts() {
        ImmutableList<QueryProductDetailsParams.Product> productList = super.getProducts();

        ImmutableList<QueryProductDetailsParams.Product> additionalProducts = ImmutableList.of(
//                QueryProductDetailsParams.Product.newBuilder()
//                        .setProductId(AraHanjaInAppProductsClass.test_book_thousand_character_classic_1)
//                        .setProductType(BillingClient.ProductType.INAPP)
//                        .build(),
                QueryProductDetailsParams.Product.newBuilder()
                        .setProductId(InAppProductsClass.inapp_buy_1000_points)
                        .setProductType(BillingClient.ProductType.INAPP)
                        .build(),
                QueryProductDetailsParams.Product.newBuilder()
                        .setProductId(InAppProductsClass.inapp_buy_3000_points)
                        .setProductType(BillingClient.ProductType.INAPP)
                        .build(),
                QueryProductDetailsParams.Product.newBuilder()
                        .setProductId(InAppProductsClass.inapp_buy_10000_points)
                        .setProductType(BillingClient.ProductType.INAPP)
                        .build()
        );

        return ImmutableList.<QueryProductDetailsParams.Product>builder()
                .addAll(productList)
                .addAll(additionalProducts)
                .build();
    }
    //천자문등 개별 책의 InApp을 구매할때 사용
    protected void queryAvailableSpecificProducts() {
        QueryProductDetailsParams queryProductDetailsParams = getQueryClassicProductDetailsParams();

        billingClient.queryProductDetailsAsync(queryProductDetailsParams, (billingResult, queryProductDetailsResult) -> {
            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && queryProductDetailsResult != null && !queryProductDetailsResult.getProductDetailsList().isEmpty()) {
                ImmutableList.Builder<BillingFlowParams.ProductDetailsParams> builder = ImmutableList.builder();
                for (ProductDetails productDetails : queryProductDetailsResult.getProductDetailsList()) {
                    DLog.i(TAG, "productDetails: " + productDetails);
                    BillingFlowParams.ProductDetailsParams productDetailsParams =
                            BillingFlowParams.ProductDetailsParams.newBuilder()
                                    .setProductDetails(productDetails)
                                    .build();

                    builder.add(productDetailsParams);
                }
                specificProductDetailsParamsList = builder.build();
            } else {
                ToastUtil.getInstance(activity).show(billingResult.getDebugMessage());
            }
        });
    }

    private ImmutableList<QueryProductDetailsParams.Product> getClassicProducts() {
        ImmutableList<QueryProductDetailsParams.Product> productList = super.getProducts();

        ImmutableList<QueryProductDetailsParams.Product> additionalProducts = ImmutableList.of(
                QueryProductDetailsParams.Product.newBuilder()
                        .setProductId(AraHanjaInAppProductsClass.book_thousand_character_classic)
                        .setProductType(BillingClient.ProductType.INAPP)
                        .build()
        );

        return ImmutableList.<QueryProductDetailsParams.Product>builder()
                .addAll(productList)
                .addAll(additionalProducts)
                .build();
    }
    public QueryProductDetailsParams getQueryClassicProductDetailsParams() {
        return QueryProductDetailsParams.newBuilder().setProductList(getClassicProducts()).build();
    }

    @Override
    protected void restorePurchases(String product) {
        handleBookThousandCharacterInApp(new ArrayList<>(Arrays.asList(product)), true);
//        if (product.equals(InAppProductsClass.inapp_remove_banner)) {
//            ToastUtil.getInstance(activity).show("banner_ad_removal를 복원해야 합니다");
//        }
    }
    @Override
    protected InAppProductsClass getInAppProductsClass() {
        return new AraHanjaInAppProductsClass();
    }

    @Override
    protected int onUpdatePoints(Purchase purchase) {
        int pointsToAdd = 0;
        Map<String, InAppProductsClass.AraInAppProduct> appProducts = new AraHanjaInAppProductsClass().getPointProducts();
        for (Map.Entry<String, InAppProductsClass.AraInAppProduct> entry : appProducts.entrySet()) {
            String key = entry.getKey();
            InAppProductsClass.AraInAppProduct product = entry.getValue();

            if (product.isConsumable() && purchase.getProducts().contains(key)) {
                pointsToAdd = product.getPoints();
                break;
            }
        }
        PointUtil pointUtil = new PointUtil(activity);
        pointUtil.addPoint(pointsToAdd);
        return pointsToAdd;
    }

    @Override
    protected boolean handleAppSpecificUnConsumableInApp(List<String> purchasedProducts) {
        return handleBookThousandCharacterInApp(purchasedProducts, false);
    }

    private boolean handleBookThousandCharacterInApp(List<String> purchasedProducts, boolean isRestored) {
        if (purchasedProducts.contains(AraHanjaInAppProductsClass.book_thousand_character_classic)) {
            sharedPref.setPurchasedClassicBookThousandCharacter(true);
            if (!isRestored) {
                AraHanjaApplication application = (AraHanjaApplication) BaseApplication.getInstance();
                application.getEventBus().post(new SuccessEvent(BaseEvent.Screen.ALL, BaseEvent.EventType.PURCHASED_IN_APP_CLASSIC, AraHanjaInAppProductsClass.book_thousand_character_classic));
            }
            return true;
        }
        return false;
    }
}
