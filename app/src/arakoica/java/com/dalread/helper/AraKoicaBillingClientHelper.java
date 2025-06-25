package com.dalread.helper;

import android.app.Activity;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.ProductDetails;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.QueryProductDetailsParams;
import com.dalread.util.DLog;
import com.dalread.util.InAppProductsClass;
import com.dalread.util.ToastUtil;
import com.google.common.collect.ImmutableList;

import java.util.List;

public class AraKoicaBillingClientHelper extends BillingClientHelper {
    private AraKoicaBillingClientHelper(Activity activity) {
        super(activity);
    }

    public static synchronized BillingClientHelper getInstance(Activity activity) {
        if (billingClientHelper == null) {
            billingClientHelper = new AraKoicaBillingClientHelper(activity);
        }
        return billingClientHelper;
    }

    @Override
    protected void querySpecificPurchases(ImmutableList.Builder<QueryProductDetailsParams.Product> productListBuilder) {

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

        billingClient.queryProductDetailsAsync(queryProductDetailsParams, (billingResult, list) -> {
            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && list != null && !list.isEmpty()) {
                ImmutableList.Builder<BillingFlowParams.ProductDetailsParams> builder = ImmutableList.builder();
                for (ProductDetails productDetails : list) {
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



        return ImmutableList.<QueryProductDetailsParams.Product>builder()
                .addAll(productList)
                .build();
    }
    public QueryProductDetailsParams getQueryClassicProductDetailsParams() {
        return QueryProductDetailsParams.newBuilder().setProductList(getClassicProducts()).build();
    }

    @Override
    protected void restorePurchases(String product) {
//        if (product.equals(InAppProductsClass.inapp_remove_banner)) {
//            ToastUtil.getInstance(activity).show("banner_ad_removal를 복원해야 합니다");
//        }
    }
    @Override
    protected InAppProductsClass getInAppProductsClass() {
        return null;
    }

    @Override
    protected int onUpdatePoints(Purchase purchase) {
        int pointsToAdd = 0;

        return pointsToAdd;
    }

    @Override
    protected boolean handleAppSpecificUnConsumableInApp(List<String> purchasedProducts) {
        return false;
    }
}
