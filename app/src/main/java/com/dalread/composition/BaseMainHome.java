package com.dalread.composition;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.dalread.BaseApplication;
import com.dalread.BuildConfig;
import com.dalread.R;
import com.dalread.activity.AppDownloadListActivity;
import com.dalread.activity.SignInUpActivity;
import com.dalread.base.BaseDialog;
import com.dalread.base.BaseDialogListener;
import com.dalread.base.EnumFlavor;
import com.dalread.base.EnumType;
import com.dalread.database.SharedPreferencesDB;
import com.dalread.dialog.ConfirmExitDialog;
import com.dalread.dialog.ConfirmationDialog;
import com.dalread.dialog.ConfirmationTextboxDialog;
import com.dalread.dialog.CustomServerDialog;
import com.dalread.dialog.InfoDialog;
import com.dalread.dialog.SingleChoiceDialog;
import com.dalread.dialog.UpdateAppVersionDialog;
import com.dalread.helper.BillingClientHelper;
import com.dalread.listener.OnClickDialogListener;
import com.dalread.listener.OnOpenNewScreen;
import com.dalread.network.DalApiListener;
import com.dalread.network.GetMainDataHelper;
import com.dalread.network.events.BaseEvent;
import com.dalread.network.events.SuccessEvent;
import com.dalread.util.AppFlavorUtil;
import com.dalread.util.BaseVoca;
import com.dalread.util.Constant;
import com.dalread.util.DLog;
import com.dalread.util.Loading;
import com.dalread.util.NetworkUtil;
import com.dalread.util.UserUtil;
import com.dalread.util.Utils;
import com.dalread.widget.GetLessonListForWidgetService;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class BaseMainHome {
    private AppCompatActivity activity;
    private BaseApplication application;
    private SharedPreferencesDB sharedPreferences;
    private GetMainDataHelper dataHelper;
    private BillingClientHelper billingClientHelper; //이건 왜 있을까? 왜 addObserver를 할까?
    public BaseMainHome(AppCompatActivity activity, BillingClientHelper billingClientHelper) {
        this.activity = activity;
        this.billingClientHelper = billingClientHelper;
        preInit();
        initDataHelper();
        activity.getLifecycle().addObserver(billingClientHelper);
    }

////    @Override
//    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        preInit();
////        sharedPreferences = SharedPreferencesDB.getInstance(this);
//    }

    private void preInit() {
        application = BaseApplication.getInstance();
        sharedPreferences = application.getSharedPref();
    }

    private void initDataHelper() {
        if (dataHelper == null) {
            dataHelper = new GetMainDataHelper(activity, application.getDalAiImpl(), sharedPreferences, null);
        }
        //TODO : In AraConv Don't need to call this
        if (!(AppFlavorUtil.isAraConvApp() || AppFlavorUtil.isAraHanjaApp()))
            dataHelper.getDataToDalPlayer(sharedPreferences.getRealUid());
    }
//    @Override
    public void logIn() {
        if (NetworkUtil.isNetworkConnetedIfNotShowWarningAsPopup(activity)) {
            activity.startActivity(SignInUpActivity.createIntent(activity));
        }
    }

//    @Override
    public void confirmExitApp() {
        new ConfirmExitDialog(EnumType.D_CONFIRM_EXIT, activity, new BaseDialogListener() {

            @Override
            public void onBaseDialogListenerShow(EnumType type, BaseDialog dialog, View v, int position, Object data) {
            }

            @Override
            public void onBaseDialogListenerOk(EnumType type, BaseDialog dialog, View v, int position, Object data) {
                signOutFirebaseAccount();
                activity.finish();
            }

            @Override
            public void onBaseDialogListenerCancel(EnumType type, BaseDialog dialog, View v, int position, Object data) {
            }

            @Override
            public void onBaseDialogListenerClick(EnumType type, BaseDialog dialog, View v, int position, Object data) {
            }

            @Override
            public void onBaseDialogListenerClickMulti(EnumType type, BaseDialog dialog, View v, int position, Object[] data) {
            }
        }).show();
    }

//    @Override
//    public void updateLastAccessDate(int accessOrExitApp) {
//        int uid = UserUtil.getUserID(activity);
//        if (uid > 0 && Utils.isConnected(activity)) {
//            application.getDalAiImpl().updateLastAccessDate(
//                    String.valueOf(uid),
//                    sharedPreferences.getLangStudyCode(),
//                    sharedPreferences.getEmail(),
//                    accessOrExitApp
//            );
//        }
//    }

//    @Override
    public void showLogoutConfirm(BaseEvent.Screen screen) {
        ConfirmationDialog dialog = new ConfirmationDialog(activity,
                R.string.logout_title,
                R.string.logout_message,
                R.string.logout_btn_yes,
                R.string.logout_btn_no,
                new ConfirmationDialog.OnDialogClickListener() {
                    @Override
                    public void onPositive(DialogInterface dialog) {
                        dialog.dismiss();
                        logOut(screen);
                    }

                    @Override
                    public void onNegative(DialogInterface dialog) {
                        dialog.dismiss();
                    }
                });
        dialog.show();
    }

    public void showDeleteAccountConfirm(BaseEvent.Screen screen) {
        ConfirmationTextboxDialog dialog = new ConfirmationTextboxDialog(activity,
                R.string.logout_title,
                R.string.delete_account_message,
                R.string.logout_btn_yes,
                R.string.logout_btn_no,
                new ConfirmationTextboxDialog.OnTextboxDialogClickListener() {
                    @Override
                    public void onPositive(DialogInterface dialog, String password) {
                        dialog.dismiss();
                        deleteAccount(screen, password);
                    }

                    @Override
                    public void onNegative(DialogInterface dialog) {
                        dialog.dismiss();
                    }
                });
        dialog.show();
    }

//    @Override
    public void openServerSelector(SingleChoiceDialog singleChoiceDialog, int currentBaseUrlPos) {
        singleChoiceDialog.showWrapContentHeight(
                R.string.choose,
                Constant.BASE_URL_LABELS,
                currentBaseUrlPos,
                R.string.ok,
                R.string.cancel,
                new OnClickDialogListener() {
                    @Override
                    public void onClick(View view, Object object) {
                        handleChooseServer(currentBaseUrlPos, (Integer) object);
                    }

                    @Override
                    public void onDismiss(View view, Object object) {

                    }
                }
        );
    }

//    @Override
    /**
     * Check open mobile info dialog when
     * 1. Build DEBUG
     * 2. using LTE Mobile
     * 3. using local work or local home
     */
    public void checkOpenMobileInfoDialog(SingleChoiceDialog singleChoiceDialog, int currentBaseUrlPos) {
        // Only run when build DEBUG
        if (!Utils.isDebug()) return;
        // Only check when user use LTE Mobile
        if (!Utils.hasMobileConnected(activity)) return;
        // Only check when user choice local work or local home
        final String baseUrl = sharedPreferences.getBaseUrl();
        if (baseUrl.equalsIgnoreCase(Constant.BASE_API_URL_LOCAL_WORK_LAN) ||
                baseUrl.equalsIgnoreCase(Constant.BASE_API_URL_LOCAL_HOME)) {
            final InfoDialog infoDialog = new InfoDialog(activity,
                    R.string.info_mobile_lte_mode_title,
                    R.string.info_mobile_lte_mode_msg,
                    R.string.info_mobile_lte_mode_ok_btn,
                    v -> openServerSelector(singleChoiceDialog, currentBaseUrlPos));
            infoDialog.show();
        }
    }

//    @Override
    public void confirmUpdateApp() {
        final UpdateAppVersionDialog updateDialog = new UpdateAppVersionDialog(activity,
                R.string.msg_confirm_update_app_title,
                R.string.msg_confirm_update_app_msg,
                R.string.msg_confirm_update_app_yes_btn,
                R.string.msg_confirm_update_app_no_btn,
                new ConfirmationDialog.OnDialogClickListener() {
                    @Override
                    public void onPositive(DialogInterface dialog) {
                        Utils.openGooglePlay(activity, BuildConfig.APPLICATION_ID);
                    }

                    @Override
                    public void onNegative(DialogInterface dialog) {

                    }
                });
        updateDialog.show();
    }


    private void logOut(BaseEvent.Screen screen) {
        if (this instanceof OnOpenNewScreen) {
            ((OnOpenNewScreen) this).onOpen();
        }
        if (Utils.isConnected(activity)) {
            Loading.show(activity);
            NetworkUtil.updateLastAccessDate(application, Constant.ACCESS_OR_EXIT_APP.EXIT);
            application.getDalAiImpl().logout(
                    screen,
                    sharedPreferences.getToken(),
                    sharedPreferences.getUid(),
                    sharedPreferences.getEmail()
            );
        } else {
            application.getDalAiImpl().logoutOnClient();
        }
    }

    private void deleteAccount(BaseEvent.Screen screen, String password) {
        if (this instanceof OnOpenNewScreen) {
            ((OnOpenNewScreen) this).onOpen();
        }
        Loading.show(activity);
        application.getDalAiImpl().deleteAccount(
                screen,
                sharedPreferences.getToken(),
                sharedPreferences.getEmail(),
                password
        );
    }

    //
    private void logOutChangeServer(final DalApiListener<Boolean> listener) {
        Loading.show(activity);
        if (Utils.isConnected(activity)) {
            application.getDalAiImpl().logout(
                    sharedPreferences.getToken(),
                    sharedPreferences.getUid(),
                    sharedPreferences.getEmail(),
                    new DalApiListener<Boolean>() {
                        @Override
                        public void onSuccess(Boolean response) {
                            if (listener != null) {
                                listener.onSuccess(response);
                            }
                        }

                        @Override
                        public void onFailure(String error) {
                            if (listener != null) {
                                listener.onFailure(error);
                            }
                        }
                    }
            );
        } else {
            application.getDalAiImpl().logoutOnClient();
            if (listener != null) {
                listener.onSuccess(true);
            }
        }
    }
    //
//    @Override
    public void signInFirebaseAccount() {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser == null) {
            firebaseAuth.signInWithEmailAndPassword(Constant.FIREBASE_EMAIL, Constant.FIREBASE_PASSWORD)
                    .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {

                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            DLog.i("signInFirebaseAccount", task.isSuccessful() ? "Successful" : "Failure");
                        }
                    });
        }
    }
//
    private void signOutFirebaseAccount() {
        FirebaseAuth.getInstance().signOut();
    }

    private void handleChooseServer(int currentBaseUrlPos, int position) {
        if (isCustomServer(position)) {
            showCustomServerDialog(currentBaseUrlPos, position);
        } else {
            handleChangeServer(currentBaseUrlPos, position);
        }
    }

    private void showCustomServerDialog(int currentBaseUrlPos, int position) {
        final CustomServerDialog dialog = new CustomServerDialog(activity,
                url -> handleChangeServer(currentBaseUrlPos, position, url),
                Constant.BASE_URL_LABELS[position]);
        dialog.show();
    }

    private void handleChangeServer(int currentBaseUrlPos, int position) {
        handleChangeServer(currentBaseUrlPos, position, null);
    }

    private void handleChangeServer(int currentBaseUrlPos, int position, String url) {
        if (position == currentBaseUrlPos & !isCustomServer(position)) {
            application.getDalAiImpl().updateFirebaseToken();
            return;
        }
        if (UserUtil.isLoggedIn(activity)) {
            logOutChangeServer(null);
            clearDataWhenChangeServer(position, url, true);
        } else {
            clearDataWhenChangeServer(position, url, false);
        }
    }

    private String getBaseUrlValues(int position, String url) {
        return isCustomServer(position) ? url : Constant.BASE_URL_VALUES[position];
    }

    private boolean isCustomServer(int position) {
        return position == Constant.BASE_URL_LABELS.length - 1;
    }

    private void setBaseUrl(int position, String url) {
        sharedPreferences.setBaseUrlIndex(position);
        if (isCustomServer(position)) {
            sharedPreferences.setBaseCustomUrl(url);
        }
        sharedPreferences.setBaseUrl(url);
    }

    private void clearDataWhenChangeServer(int position, String url, boolean isLogout) {
        final String baseUrl = getBaseUrlValues(position, url);
        application.buildBaseRetrofit(baseUrl);
        setBaseUrl(position, baseUrl);
        if (BuildConfig.FLAVOR.equals(EnumFlavor.DALVOCA.name())) {
            GetLessonListForWidgetService.getLessonList(activity);
        }
        application.getDalAiImpl().updateFirebaseToken();
        if (isLogout) {
            BaseVoca.deleteVocaVersion();
        }
        Loading.hide();
        sharedPreferences.setShowLoginWhenChangeServer(isLogout);
        recreateThis();
    }

    //TODO : Duplicate code
    private void recreateThis() {
//        if (alertDialog != null && alertDialog.isShowing()) {
//            alertDialog.dismiss();
//        }
        BaseVoca.deleteAllRealmData();
        if (BuildConfig.FLAVOR.equals(EnumFlavor.DALVOCA.getName())) {
            activity.recreate();
        } else {
            activity.startActivity(activity.getIntent());
            activity.finishAffinity();
            activity.overridePendingTransition(0, 0);
        }
    }

    public void removeBannerAds() {
        //이건 지우지 말것.isPurchasedRemoveBannerAds을 사용하는게 맞는지 확인이 필요함.
//        if (sharedPreferences.isPurchasedRemoveBannerAds()) {
//            application.getSharedPref().setRemoveBannerAds(true);
//            application.getEventBus().post(new SuccessEvent(BaseEvent.Screen.MAIN, BaseEvent.EventType.REMOVE_BANNER_ADS, true));
//            return;
//        }
//        activity.startActivity(new Intent(activity, InAppPointListParentActivity.class));
    }

//    public void inAppPurchase() {
//        if (billingClientHelper.getProductDetailsParams() == null) {
//            SnackbarUtil.getInstance(activity).show(R.string.toast_error_fetching_in_app_items);
//        } else {
////            Intent intent = new Intent(activity, InAppPointListParentActivity.class);
////            intent.putExtra("billingClientHelper", (Serializable)billingClientHelper);
////            activity.startActivity(intent);
//
//            activity.startActivity(new Intent(activity, InAppPointListParentActivity.class));
//        }
//    }

    public void restoreBannerAds() {
        sharedPreferences.setRemoveBannerAds(false);
        application.getEventBus().post(new SuccessEvent(BaseEvent.Screen.MAIN, BaseEvent.EventType.REMOVE_BANNER_ADS, false));
    }

    public void shareApp() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        String appUrl = "http://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID;
        sendIntent.putExtra(Intent.EXTRA_TEXT, appUrl);
        sendIntent.setType("text/plain");

        Intent shareIntent = Intent.createChooser(sendIntent, null);
        activity.startActivity(shareIntent);
    }

    public void rateApp() {
        activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID)));
//        Utils.openGooglePlay(activity, BuildConfig.APPLICATION_ID);
    }

    public void openAppDownload(Context context) {
        context.startActivity(new Intent(context, AppDownloadListActivity.class));
    }
}
