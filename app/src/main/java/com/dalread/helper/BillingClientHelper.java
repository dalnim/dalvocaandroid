package com.dalread.helper;

import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;

import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ConsumeParams;
import com.android.billingclient.api.ConsumeResponseListener;
import com.android.billingclient.api.PendingPurchasesParams;
import com.android.billingclient.api.ProductDetails;
import com.android.billingclient.api.ProductDetailsResponseListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.QueryProductDetailsParams;
import com.android.billingclient.api.QueryPurchasesParams;
import com.android.billingclient.api.QueryProductDetailsResult;
import com.dalread.BaseApplication;
import com.dalread.R;
import com.dalread.database.SharedPreferencesDB;
import com.dalread.network.events.BaseEvent;
import com.dalread.network.events.SuccessEvent;
import com.dalread.util.DLog;
import com.dalread.util.InAppProductsClass;
import com.dalread.util.ToastUtil;
import com.dalread.util.UserUtil;
import com.dalread.util.Utils;
import com.google.common.collect.ImmutableList;

import java.io.Serializable;
import java.util.List;

public class BillingClientHelper implements Serializable, LifecycleObserver, PurchasesUpdatedListener, BillingClientStateListener {
    protected static BillingClientHelper billingClientHelper;
    protected final String TAG = "BillingClientHelper";
    protected Activity activity;
    protected BillingClient billingClient;
    protected SharedPreferencesDB sharedPref;
    private ImmutableList<BillingFlowParams.ProductDetailsParams> productDetailsParamsList;
    protected ImmutableList<BillingFlowParams.ProductDetailsParams> specificProductDetailsParamsList;

    public BillingClientHelper(Activity activity) {
        this.activity = activity;
        this.sharedPref = SharedPreferencesDB.getInstance(activity);
    }

    public static BillingClientHelper getInstance(Activity activity) {
        if (billingClientHelper == null) {
            return billingClientHelper = new BillingClientHelper(activity);
        }
        return billingClientHelper;
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    public void create() {
        DLog.v(TAG,"create");
        initBillingClient();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    public void destroy() {
        DLog.v(TAG,"destroy");
        if (billingClient != null && billingClient.isReady()) {
            billingClient.endConnection();
        }
    }

    private void initBillingClient() {
        billingClient = BillingClient.newBuilder(activity)
                .setListener(this)
                .enablePendingPurchases(PendingPurchasesParams.newBuilder().enableOneTimeProducts().build())
                .build();
        if (!billingClient.isReady()) {
            startBillingConnection();
        }
    }

    @Override
    public void onBillingServiceDisconnected() {
        DLog.v(TAG,"onBillingServiceDisconnected");
    }

    @Override
    public void onBillingSetupFinished(@NonNull BillingResult billingResult) {
        DLog.v(TAG,"onBillingSetupFinished - billingResult: " + billingResult);
        if (billingResult.getResponseCode() ==  BillingClient.BillingResponseCode.OK) {
            // The BillingClient is ready. You can query purchases here.
            DLog.v(TAG,"Setup Billing Done");
            restorePurchases();
            queryAvailableProducts();
            queryAvailableSpecificProducts();
//            queryPurchases();
        } else {
            handleBillingResultError(billingResult);
        }
    }

    //나중에는 배너 광고같은 경우는 이미 구입했다가 앱을 재설치시에는 복원시켜줘야 한다.
    private void restorePurchases() {
        billingClient.queryPurchasesAsync(QueryPurchasesParams.newBuilder()
            .setProductType(BillingClient.ProductType.INAPP)
            .build(), (billingResult, purchasesList) -> {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && purchasesList != null) {
                    for (Purchase purchase : purchasesList) {
                        // 여기서는 인앱 상품을 확인하고 필요한 처리를 수행합니다.
                        List<String> products = purchase.getProducts();
                        for (String product : products) {
                            if (product.equals(InAppProductsClass.inapp_remove_banner)) {
                                ToastUtil.getInstance(activity).show("banner_ad_removal를 복원해야 합니다");
                            } else {
                                restorePurchases(product);
                            }
                        }
                    }
                }
            });
    }

    protected void restorePurchases(String product) {

    }
    private void startBillingConnection() {
        DLog.v(TAG,"startBillingConnection");
        billingClient.startConnection(this);
    }
    //천자문등 개발 InApp을 구매할때 사용
    protected void queryAvailableSpecificProducts() {
    }

    private void queryAvailableProducts() {
        DLog.i(TAG, "queryAvailableProducts");
        QueryProductDetailsParams queryProductDetailsParams = getQueryProductDetailsParams();

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
                productDetailsParamsList = builder.build();
            } else {
                ToastUtil.getInstance(activity).show(billingResult.getDebugMessage());
            }
        });
    }

    private QueryProductDetailsParams getQueryProductDetailsParams() {
        return QueryProductDetailsParams.newBuilder().setProductList(getProducts()).build();
    }

    protected ImmutableList<QueryProductDetailsParams.Product> getProducts() {
        ImmutableList.Builder<QueryProductDetailsParams.Product> productListBuilder = ImmutableList.builder();
        //디버그 모드일때만 테스트 포인트를 추가한다.
        if (Utils.isDebug()) {
            productListBuilder.add(
                    QueryProductDetailsParams.Product.newBuilder()
                            .setProductId(InAppProductsClass.test_inapp_remove_banner)
                            .setProductType(BillingClient.ProductType.INAPP)
                            .build(),
                    QueryProductDetailsParams.Product.newBuilder()
                            .setProductId(InAppProductsClass.test_inapp_points)
                            .setProductType(BillingClient.ProductType.INAPP)
                            .build()
            );
        }
        //아직 배너 제거는 구현 안했음.
//        productListBuilder.add(
//                QueryProductDetailsParams.AraInAppProduct.newBuilder()
//                        .setProductId(InAppProductsClass.inapp_remove_banner)
//                        .setProductType(BillingClient.ProductType.INAPP)
//                        .build()
//        );

        return productListBuilder.build();
    }



    protected void querySpecificPurchases(ImmutableList.Builder<QueryProductDetailsParams.Product> productListBuilder) {
    }

    private void queryPurchases() {
        DLog.i(TAG, "queryPurchases");
//        ImmutableList<QueryProductDetailsParams.Product> productList = ImmutableList.of(QueryProductDetailsParams.Product.newBuilder()
//                .setProductId(InAppProductsClass.inapp_buy_1000_points)
//                .setProductId(InAppProductsClass.inapp_buy_3000_points)
//                .setProductId(InAppProductsClass.inapp_buy_10000_points)
//                .setProductId(AraHanjaInAppProductsClass.test_book_thousand_character_classic_1)
//                .setProductType(BillingClient.ProductType.INAPP)
//                .build());

        // Create productListBuilder for adding products
        ImmutableList.Builder<QueryProductDetailsParams.Product> productListBuilder = ImmutableList.builder();

        // Add common products
        productListBuilder
                .add(QueryProductDetailsParams.Product.newBuilder()
                        .setProductId(InAppProductsClass.inapp_buy_1000_points)
                        .setProductType(BillingClient.ProductType.INAPP)
                        .build())
                .add(QueryProductDetailsParams.Product.newBuilder()
                        .setProductId(InAppProductsClass.inapp_buy_3000_points)
                        .setProductType(BillingClient.ProductType.INAPP)
                        .build())
                .add(QueryProductDetailsParams.Product.newBuilder()
                        .setProductId(InAppProductsClass.inapp_buy_10000_points)
                        .setProductType(BillingClient.ProductType.INAPP)
                        .build());
        querySpecificPurchases(productListBuilder);
        ImmutableList<QueryProductDetailsParams.Product> productList = productListBuilder.build();

        QueryProductDetailsParams params = QueryProductDetailsParams.newBuilder()
                .setProductList(productList)
                .build();

        billingClient.queryProductDetailsAsync(
                params,
                new ProductDetailsResponseListener() {
                    public void onProductDetailsResponse(BillingResult billingResult, QueryProductDetailsResult queryProductDetailsResult) {
                        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                            for (ProductDetails purchase : queryProductDetailsResult.getProductDetailsList()) {
                                DLog.v(TAG,"onQueryPurchasesResponse - purchase: " + purchase);
                            }
                        }
                    }
                }
        );

//        billingClient.queryPurchasesAsync(BillingClient.ProductType.INAPP, (billingResult, list) -> {
//            DLog.v(TAG,"onQueryPurchasesResponse - billingResult: " + billingResult);
//            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
//                for (Purchase purchase : list) {
//                    DLog.v(TAG,"onQueryPurchasesResponse - purchase: " + purchase);
//                    String removeBannerAdsSku = inapp_remove_banner;
//                    if (BuildConfig.DEBUG) {
//                        removeBannerAdsSku = SKU_PURCHASE_DEBUG;
//                    }
//                    for(String s: purchase.getSkus()) {
//                        DLog.v(TAG,"onQueryPurchasesResponse - sku: " + s);
//                    }
//                    if (purchase.getSkus().contains(removeBannerAdsSku)) {
//                        application.getSharedPref().setPurchasedBannerAds(true);
//                    }
//                }
//            }
//        });
    }

    //인앱을 구입하고 나면 앱내에서 처리해야 할 일들을 적음(포인트 구매하고 나면, 앱 내애에서 포인트를 추가해줘야 한다)
    @Override
    public void onPurchasesUpdated(@NonNull BillingResult billingResult, @Nullable List<Purchase> list) {
        DLog.i(TAG, "onPurchasesUpdated - billingResult: " + billingResult);
        int responseCode = billingResult.getResponseCode();
        if (responseCode == BillingClient.BillingResponseCode.OK && list != null) {
            InAppProductsClass inAppProducts = getInAppProductsClass();
            for (Purchase purchase : list) {
                for (String productId : purchase.getProducts()) {
                    if (inAppProducts.isConsumable(productId)) {
                        handleConsumableInApp(purchase);
                    } else {
                        handleUnConsumableInApp(purchase);
                    }
                }
            }
        } else {
            handleBillingResultError(billingResult);
        }
    }

    //이건 아직 안만들었다. 배너 제거는 이걸로 테스트 해봐야 함.
    private void handleUnConsumableInApp(Purchase purchase) {
        AcknowledgePurchaseParams params =
                AcknowledgePurchaseParams.newBuilder().setPurchaseToken(purchase.getPurchaseToken()).build();
        billingClient.acknowledgePurchase(params, billingResult1 -> {
            DLog.i(TAG, "acknowledgePurchase - billingResult: " + billingResult1);
            if (billingResult1.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                List<String> purchasedProducts = purchase.getProducts();
                if (handleRemoveBannerInApp(purchasedProducts)) {
                } else if (handleAppSpecificUnConsumableInApp(purchasedProducts)) {
                } else {
                    ToastUtil.getInstance(activity).show(purchase.getProducts() + "처리 필요");
                }
//                if (purchasedProducts.contains(InAppProductsClass.inapp_remove_banner)) {
//                    sharedPref.setPurchasedBannerAds(true);
//                    sharedPref.setRemoveBannerAds(true);
//                    BaseApplication.getInstance().getEventBus().post(new SuccessEvent(BaseEvent.Screen.MAIN, BaseEvent.EventType.REMOVE_BANNER_ADS, true));
//                } else if (purchasedProducts.contains(AraHanjaInAppProductsClass.test_book_thousand_character_classic_1)) {
//                    sharedPref.setPurchasedClassicBookThousandCharacter(true);
//                } else {
//                    ToastUtil.getInstance(activity).show(purchase.getProducts() + "처리 필요");
//                }

            } else {
                handleBillingResultError(billingResult1);
            }
        });
    }

    private boolean handleRemoveBannerInApp(List<String> purchasedProducts) {
        if (purchasedProducts.contains(InAppProductsClass.inapp_remove_banner)) {
            sharedPref.setPurchasedBannerAds(true);
            sharedPref.setRemoveBannerAds(true);
            BaseApplication.getInstance().getEventBus().post(new SuccessEvent(BaseEvent.Screen.MAIN, BaseEvent.EventType.REMOVE_BANNER_ADS, true));
            return true;
        }
        return false;
    }

    protected boolean handleAppSpecificUnConsumableInApp(List<String> purchasedProducts) {
        return false;
    }

    private void handleConsumableInApp(Purchase purchase) {
        int points = onUpdatePoints(purchase);
        ConsumeParams consumeParams = ConsumeParams.newBuilder()
                .setPurchaseToken(purchase.getPurchaseToken())
                .build();
        billingClient.consumeAsync(consumeParams, new ConsumeResponseListener() {
            @Override
            public void onConsumeResponse(@NonNull BillingResult billingResult, @NonNull String s) {
                if(billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    //Snack바는 하단에 표시되어 배너광고 위에 보여서 좀 불안해서 일단 Toast로 띄운다.
//                                    SnackbarUtil.getInstance(application.getCurrentActivity()).show(application.getString(R.string.snackbar_points_added, points));
                    ToastUtil.getInstance(activity).show(activity.getString(R.string.snackbar_points_added, points));
                } else {
                    DLog.d(TAG, "소모 실패");
                }
            }
        });
    }

    protected InAppProductsClass getInAppProductsClass() {
        return null;
    }

    protected int onUpdatePoints(Purchase purchase) {
        return 0;
    }

    public void handleSelectedInApp(Activity activity, BillingFlowParams.ProductDetailsParams selectedInApp) {
        ImmutableList.Builder<BillingFlowParams.ProductDetailsParams> builder = ImmutableList.builder();
        builder.add(selectedInApp);
        ImmutableList<BillingFlowParams.ProductDetailsParams> productDetailsParamsList = builder.build();

        BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
                .setProductDetailsParamsList(productDetailsParamsList)
                .build();

        launchBillingFlow(activity, billingFlowParams);
    }

    private void launchBillingFlow(Activity activity, BillingFlowParams billingFlowParams) {
        BillingResult billingResult = billingClient.launchBillingFlow(activity, billingFlowParams);
        DLog.i(TAG, "launchBillingFlow - responseCode: " + billingResult);
        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
            DLog.i(TAG, "billing response ok");
        } else {
            handleBillingResultError(billingResult);
        }
    }

    public ImmutableList<BillingFlowParams.ProductDetailsParams> getProductDetailsParams() {
        if (productDetailsParamsList == null) {
            return ImmutableList.of();
        }
        return productDetailsParamsList;
    }

    public ImmutableList<BillingFlowParams.ProductDetailsParams> getSpecificProductDetailsParams() {
        if (specificProductDetailsParamsList == null) {
            return ImmutableList.of();
        }
        return specificProductDetailsParamsList;
    }

    private void handleBillingResultError(BillingResult billingResult) {
        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED) {
            DLog.i(TAG, "user cancelled the purchase flow");
            ToastUtil.getInstance(activity).show(R.string.toast_in_app_purchase_cancelled);
        } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED) {
            DLog.i(TAG, "ITEM_ALREADY_OWNED - response code: " + billingResult.getResponseCode());
            //시스템에서 이미 보유중인 아이템이라고 메시지를 띄워서 따로 띄울 필요는 없다.
        } else {
            DLog.i(TAG, "other error! - response code: " + billingResult.getResponseCode());
            if (UserUtil.isDebugOrAdminUser(activity)) {
                ToastUtil.getInstance(activity).show(billingResult.getDebugMessage());
            }
        }
    }
}
