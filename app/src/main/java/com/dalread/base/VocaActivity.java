package com.dalread.base;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.dalread.BaseApplication;
import com.dalread.BuildConfig;
import com.dalread.R;
import com.dalread.component.Toolbar;
import com.dalread.database.SharedPreferencesDB;
import com.dalread.dialog.AlertDialog;
import com.dalread.dialog.ConfirmationDialog;
import com.dalread.dialog.UpdateAppVersionDialog;
import com.dalread.interfaces.IVocaBasicItem;
import com.dalread.listener.OnHeaderListener;
import com.dalread.listener.OnOpenNewScreen;
import com.dalread.util.BaseVoca;
import com.dalread.util.BaseVocaKnow;
import com.dalread.util.Constant;
import com.dalread.util.DLog;
import com.dalread.util.LocaleHelper;
import com.dalread.util.Utils;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;

//Same as BaseActivity
/*
 * @deprecated Replaced by {@link #BaseActivity}
 */
@Deprecated
public abstract class VocaActivity extends AppCompatActivity implements OnHeaderListener {

    protected abstract @LayoutRes
    int getContentViewId();

    protected BaseApplication application;
    protected SharedPreferencesDB sharedPreferences;
    public EventBus eventBus;
    protected ConfirmationDialog adminDialog;
    @Nullable
    @BindView(R.id.header)
    protected Toolbar toolbar;
    public AlertDialog alertDialog;
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DLog.i(getLogTag(), "onCreate");

        preInit();
        if (getContentViewId() != 0) {
            setContentView(getContentViewId());
        } else {
            setContentView(getContentView());
        }
        bindButterKnife();
        if (getToolbar() != null) {
            getToolbar().setListener(this);
        }
        viewSlideAnimation();
    }

    protected void bindButterKnife() {
        ButterKnife.bind(this);
    }

    protected View getContentView() {
        return null;
    }
    protected void viewSlideAnimation() {
        boolean viewSlideRightToLeftInAnimation = getIntent().getBooleanExtra(Constant.BUNDLE.KEY_SLIDE_RIGHT_TO_LEFT_IN_ANIMATION, true);
        if (viewSlideRightToLeftInAnimation) {
            overridePendingTransition(R.anim.slide_right_to_left_in, R.anim.slide_right_to_left_out);
        } else {
            overridePendingTransition(R.anim.slide_left_to_right_in, R.anim.slide_left_to_right_out);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        DLog.i(getLogTag(), "onStart");
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        DLog.i(getLogTag(), "onRestart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        application.setCurrentActivity(this);
        DLog.i(getLogTag(), "onResume");
    }

    @Override
    protected void onPause() {
        DLog.i(getLogTag(), "onPause");

        super.onPause();
    }

    @Override
    protected void onStop() {
        DLog.i(getLogTag(), "onStop");

        super.onStop();
    }

    @Override
    protected void onDestroy() {
        DLog.i(getLogTag(), "onDestroy");

        super.onDestroy();
    }

    @Override
    public void finish() {
        super.finish();

        overridePendingTransition(R.anim.slide_left_to_right_in, R.anim.slide_left_to_right_out);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Fragment fragment = getSupportFragmentManager().findFragmentById(getFragmentContainerId());
        if (fragment != null) {
            fragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    protected @IdRes
    int getFragmentContainerId() {
        return R.id.fragment_container;
    }

//    protected void confirmExitApp() {
//        new ConfirmExitDialog(EnumType.D_CONFIRM_EXIT, this, new BaseDialogListener() {
//
//            @Override
//            public void onBaseDialogListenerShow(EnumType type, BaseDialog dialog, View v, int position, Object data) {
//            }
//
//            @Override
//            public void onBaseDialogListenerOk(EnumType type, BaseDialog dialog, View v, int position, Object data) {
//                signOutFirebaseAccount();
//                finish();
//            }
//
//            @Override
//            public void onBaseDialogListenerCancel(EnumType type, BaseDialog dialog, View v, int position, Object data) {
//            }
//
//            @Override
//            public void onBaseDialogListenerClick(EnumType type, BaseDialog dialog, View v, int position, Object data) {
//            }
//
//            @Override
//            public void onBaseDialogListenerClickMulti(EnumType type, BaseDialog dialog, View v, int position, Object[] data) {
//            }
//        }).show();
//    }

    private void preInit() {
        application = BaseApplication.getInstance();
        sharedPreferences = application.getSharedPref();
    }

    protected int getUserID() {
        return sharedPreferences.getRealUid();
    }

    protected boolean isLoggedIn() {
        return sharedPreferences.isUID();
    }

    protected String getUserName() {
        return sharedPreferences.getUserName(this);
    }

    protected String getPoint() {
        return String.valueOf(sharedPreferences.getPointVoca());
    }

//    public void logIn() {
//        if (NetworkUtil.isNetworkConnetedIfNotShowWarningAsPopup(this)) {
//            startActivity(SignInUpActivity.createIntent(this));
////        startActivity(LoginActivity.createIntent(this));
//        }
//    }
//
//    protected void showLogoutConfirm(BaseEvent.Screen screen) {
//        ConfirmationDialog dialog = new ConfirmationDialog(this,
//                R.string.logout_title,
//                R.string.logout_message,
//                R.string.logout_btn_yes,
//                R.string.logout_btn_no,
//                new ConfirmationDialog.OnDialogClickListener() {
//                    @Override
//                    public void onPositive(DialogInterface dialog) {
//                        dialog.dismiss();
//                        logOut(screen);
//                    }
//
//                    @Override
//                    public void onNegative(DialogInterface dialog) {
//                        dialog.dismiss();
//                    }
//                });
//        dialog.show();
//    }
//
//    protected void logOut(BaseEvent.Screen screen) {
//        if (this instanceof OnOpenNewScreen) {
//            ((OnOpenNewScreen) this).onOpen();
//        }
//        if (Utils.isConnected(this)) {
//            Loading.show(this);
//            updateLastAccessDate(Constant.ACCESS_OR_EXIT_APP.EXIT);
//            application.getDalAiImpl().logout(
//                    screen,
//                    sharedPreferences.getToken(),
//                    sharedPreferences.getUid(),
//                    sharedPreferences.getEmail()
//            );
//        } else {
//            application.getDalAiImpl().logoutOnClient();
//        }
//    }
//
//    protected void logOutChangeServer(final DalApiListener<Boolean> listener) {
//        Loading.show(this);
//        if (Utils.isConnected(this)) {
//            application.getDalAiImpl().logout(
//                    sharedPreferences.getToken(),
//                    sharedPreferences.getUid(),
//                    sharedPreferences.getEmail(),
//                    new DalApiListener<Boolean>() {
//                        @Override
//                        public void onSuccess(Boolean response) {
//                            if (listener != null) {
//                                listener.onSuccess(response);
//                            }
//                        }
//
//                        @Override
//                        public void onFailure(String error) {
//                            if (listener != null) {
//                                listener.onFailure(error);
//                            }
//                        }
//                    }
//            );
//        } else {
//            application.getDalAiImpl().logoutOnClient();
//            if (listener != null) {
//                listener.onSuccess(true);
//            }
//        }
//    }

    protected String getLogTag() {
        return "*******" + getClass().getSimpleName() + "*******";
    }

    public void openNewScreenWithRightToLeftInAnimation(Class aClass) {
        if (this instanceof OnOpenNewScreen) {
            ((OnOpenNewScreen) this).onOpen();
        }
        Intent i = new Intent(this, aClass);
        i.putExtra(Constant.BUNDLE.KEY_SLIDE_RIGHT_TO_LEFT_IN_ANIMATION, false);
        startActivity(i);
    }

    public void openNewScreen(Class aClass) {
        if (this instanceof OnOpenNewScreen) {
            ((OnOpenNewScreen) this).onOpen();
        }
        Intent i = new Intent(this, aClass);
        startActivity(i);
    }

    public void openNewScreen(Intent i) {
        if (this instanceof OnOpenNewScreen) {
            ((OnOpenNewScreen) this).onOpen();
        }
        startActivity(i);
    }

    public void openNewScreenForResult(Intent i, int requestCode) {
        if (this instanceof OnOpenNewScreen) {
            ((OnOpenNewScreen) this).onOpen();
        }
        startActivityForResult(i, requestCode);
    }

//    protected void signInFirebaseAccount() {
//        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
//        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
//        if (currentUser == null) {
//            firebaseAuth.signInWithEmailAndPassword(Constant.FIREBASE_EMAIL, Constant.FIREBASE_PASSWORD)
//                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//
//                        @Override
//                        public void onComplete(@NonNull Task<AuthResult> task) {
//                            DLog.i("signInFirebaseAccount", task.isSuccessful() ? "Successful" : "Failure");
//                        }
//                    });
//        }
//    }
//
//    protected void signOutFirebaseAccount() {
//        FirebaseAuth.getInstance().signOut();
//    }
//
//    protected void updateLastAccessDate(int accessOrExitApp) {
//        int uid = getUserID();
//        if (uid > 0 && Utils.isConnected(this)) {
//            application.getDalAiImpl().updateLastAccessDate(
//                    String.valueOf(uid),
//                    sharedPreferences.getLangStudyCode(),
//                    sharedPreferences.getEmail(),
//                    accessOrExitApp
//            );
//        }
//    }

    protected void getCountOf1StAmkiGrade() {
        if (Utils.isConnected(this)) {
            application.getDalAiImpl().getCountOf1StAmkiGrade(null);
        }
    }

    protected void confirmUpdateApp() {
        final UpdateAppVersionDialog updateDialog = new UpdateAppVersionDialog(this,
                R.string.msg_confirm_update_app_title,
                R.string.msg_confirm_update_app_msg,
                R.string.msg_confirm_update_app_yes_btn,
                R.string.msg_confirm_update_app_no_btn,
                new ConfirmationDialog.OnDialogClickListener() {
                    @Override
                    public void onPositive(DialogInterface dialog) {
                        Utils.openGooglePlay(VocaActivity.this, BuildConfig.APPLICATION_ID);
                    }

                    @Override
                    public void onNegative(DialogInterface dialog) {

                    }
                });
        updateDialog.show();
    }

    protected void initEventBus() {
        eventBus = EventBus.getDefault();
        if (!eventBus.isRegistered(this)) {
            eventBus.register(this);
        }
    }

    protected void unRegisterEventBus() {
        if (eventBus != null && eventBus.isRegistered(this)) {
            eventBus.unregister(this);
        }
    }

//    protected void openServerSelector(SingleChoiceDialog singleChoiceDialog, int currentBaseUrlPos) {
//        singleChoiceDialog.showWrapContentHeight(
//                R.string.choose,
//                Constant.BASE_URL_LABELS,
//                currentBaseUrlPos,
//                R.string.ok,
//                R.string.cancel,
//                new OnClickDialogListener() {
//                    @Override
//                    public void onClick(View view, Object object) {
//                        handleChooseServer(currentBaseUrlPos, (Integer) object);
//                    }
//
//                    @Override
//                    public void onDismiss(View view, Object object) {
//
//                    }
//                }
//        );
//    }
//
//    /**
//     * Check open mobile info dialog when
//     * 1. Build DEBUG
//     * 2. using LTE Mobile
//     * 3. using local work or local home
//     */
//    protected void checkOpenMobileInfoDialog(SingleChoiceDialog singleChoiceDialog, int currentBaseUrlPos) {
//        // Only run when build DEBUG
//        if (!Utils.isDebug()) return;
//        // Only check when user use LTE Mobile
//        if (!Utils.hasMobileConnected(this)) return;
//        // Only check when user choice local work or local home
//        final String baseUrl = sharedPreferences.getBaseUrl();
//        if (baseUrl.equalsIgnoreCase(Constant.BASE_API_URL_LOCAL_WORK_LAN) ||
//                baseUrl.equalsIgnoreCase(Constant.BASE_API_URL_LOCAL_HOME)) {
//            final InfoDialog infoDialog = new InfoDialog(this,
//                    R.string.info_mobile_lte_mode_title,
//                    R.string.info_mobile_lte_mode_msg,
//                    R.string.info_mobile_lte_mode_ok_btn,
//                    v -> openServerSelector(singleChoiceDialog, currentBaseUrlPos));
//            infoDialog.show();
//        }
//    }
//
//    protected void handleChooseServer(int currentBaseUrlPos, int position) {
//        if (isCustomServer(position)) {
//            showCustomServerDialog(currentBaseUrlPos, position);
//        } else {
//            handleChangeServer(currentBaseUrlPos, position);
//        }
//    }
//
//    protected void showCustomServerDialog(int currentBaseUrlPos, int position) {
//        final CustomServerDialog dialog = new CustomServerDialog(this,
//                url -> handleChangeServer(currentBaseUrlPos, position, url),
//                Constant.BASE_URL_LABELS[position]);
//        dialog.show();
//    }
//
//    protected void handleChangeServer(int currentBaseUrlPos, int position) {
//        handleChangeServer(currentBaseUrlPos, position, null);
//    }
//
//    protected void handleChangeServer(int currentBaseUrlPos, int position, String url) {
//        if (position == currentBaseUrlPos & !isCustomServer(position)) {
//            application.getDalAiImpl().updateFirebaseToken();
//            return;
//        }
//        if (isLoggedIn()) {
//            logOutChangeServer(null);
//            clearDataWhenChangeServer(position, url, true);
//        } else {
//            clearDataWhenChangeServer(position, url, false);
//        }
//    }
//
//    protected String getBaseUrlValues(int position, String url) {
//        return isCustomServer(position) ? url : Constant.BASE_URL_VALUES[position];
//    }
//
//    protected boolean isCustomServer(int position) {
//        return position == Constant.BASE_URL_LABELS.length - 1;
//    }
//
//    protected void setBaseUrl(int position, String url) {
//        sharedPreferences.setBaseUrlIndex(position);
//        if (isCustomServer(position)) {
//            sharedPreferences.setBaseCustomUrl(url);
//        }
//        sharedPreferences.setBaseUrl(url);
//    }
//
//    protected void clearDataWhenChangeServer(int position, String url, boolean isLogout) {
//        final String baseUrl = getBaseUrlValues(position, url);
//        application.buildBaseRetrofit(baseUrl);
//        setBaseUrl(position, baseUrl);
//        if (BuildConfig.FLAVOR.equals(EnumFlavor.DALVOCA.name())) {
//            GetLessonListForWidgetService.getLessonList(getApplicationContext());
//        }
//        application.getDalAiImpl().updateFirebaseToken();
//        if (isLogout) {
//            BaseVoca.deleteVocaVersion();
//        }
//        Loading.hide();
//        sharedPreferences.setShowLoginWhenChangeServer(isLogout);
//        recreateThis();
//    }

    protected void recreateThis() {
        if (alertDialog != null && alertDialog.isShowing()) {
            alertDialog.dismiss();
        }
        BaseVoca.deleteAllRealmData();
        if (BuildConfig.FLAVOR.equals(EnumFlavor.DALVOCA.getName())) {
            recreate();
        } else {
            startActivity(getIntent());
            finishAffinity();
            overridePendingTransition(0, 0);
        }
    }

    public AlertDialog getAlertDialog() {
        return alertDialog;
    }

    @Nullable
    public Toolbar getToolbar() {
        return toolbar;
    }

//    @Override
//    public boolean dispatchKeyEvent(KeyEvent event) {
//        if (event.getAction() == KeyEvent.ACTION_UP) {
//            final int keyCode = event.getKeyCode();
//            DLog.d(getLogTag(), "dispatchKeyEvent keyCode=" + keyCode);
//            if (keyCode == KeyEvent.KEYCODE_HEADSETHOOK) {
//                eventBus.post(new SuccessEvent(BaseEvent.Screen.MEDIA_BUTTON, BaseEvent.EventType.MEDIA_BUTTON_HEADSETHOOK, true));
//                ToastUtil.getInstance(this).show("dispatchKeyEvent KEYCODE_HEADSETHOOK keyCode=" + keyCode);
//            } else if (keyCode == KeyEvent.KEYCODE_MEDIA_PAUSE) {
//                eventBus.post(new SuccessEvent(BaseEvent.Screen.MEDIA_BUTTON, BaseEvent.EventType.MEDIA_BUTTON_PLAY, true));
//                ToastUtil.getInstance(this).show("dispatchKeyEvent KEYCODE_MEDIA_PAUSE keyCode=" + keyCode);
//            } else if (keyCode == KeyEvent.KEYCODE_MEDIA_PLAY) {
//                eventBus.post(new SuccessEvent(BaseEvent.Screen.MEDIA_BUTTON, BaseEvent.EventType.MEDIA_BUTTON_PAUSE, true));
//                ToastUtil.getInstance(this).show("dispatchKeyEvent KEYCODE_MEDIA_PLAY keyCode=" + keyCode);
//            } else if (keyCode == KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE) {
//                //TODO : Dalnim - When I first open a PlayerFragment, dispatchKeyEvent is called. but if I open a word list view and back to the PlayerFragment then notthing is called.
//                //TODO : This is called when I'm only in the PlayerFragment
//                eventBus.post(new SuccessEvent(BaseEvent.Screen.MEDIA_BUTTON, BaseEvent.EventType.MEDIA_MEDIA_PLAY_OR_PAUSE, true));
//                ToastUtil.getInstance(this).show("dispatchKeyEvent KEYCODE_MEDIA_PLAY_PAUSE keyCode=" + keyCode);
//            } else if (keyCode == KeyEvent.KEYCODE_MEDIA_STOP) {
//                eventBus.post(new SuccessEvent(BaseEvent.Screen.MEDIA_BUTTON, BaseEvent.EventType.MEDIA_MEDIA_STOP, true));
//                ToastUtil.getInstance(this).show("dispatchKeyEvent KEYCODE_MEDIA_STOP keyCode=" + keyCode);
//            } else if (keyCode == KeyEvent.KEYCODE_BACK) {
//                ToastUtil.getInstance(this).show("dispatchKeyEvent KEYCODE_BACK keyCode=" + keyCode);
//                return false;
//            } else {
//                eventBus.post(new SuccessEvent(BaseEvent.Screen.DAL_PLAYER, BaseEvent.EventType.KEYBOARD_EVENT, keyCode));
//                ToastUtil.getInstance(this).show("dispatchKeyEvent else keyCode=" + keyCode);
//            }
//            return true;
//        }
//        return super.dispatchKeyEvent(event);
//    }

    public boolean updateModelWithNewVocaKnow(IVocaBasicItem iVocaBasicItem, int newVocaKnow, int newVocaKnowPronounce) {

        int vocaKnowToSet = newVocaKnow == -1 ? iVocaBasicItem.getVIVocaKnow() : newVocaKnow;
        int vocaKnowPronounceToSet = newVocaKnowPronounce == -1 ? iVocaBasicItem.getVIVocaKnowPronounce() : newVocaKnowPronounce;
        if (newVocaKnowPronounce == -1)
            vocaKnowPronounceToSet = BaseVocaKnow.setVocaKnowPronounceToKnownWhenVocaKnowIsKnown(newVocaKnow, vocaKnowPronounceToSet);

        boolean isVocaKnowValueChanged = BaseVocaKnow.isKnowValueChanged(iVocaBasicItem.getVIVocaKnow(), newVocaKnow, iVocaBasicItem.getVIVocaKnowPronounce(), vocaKnowPronounceToSet);
        if (isVocaKnowValueChanged) {
            iVocaBasicItem.setVIVocaKnow(vocaKnowToSet);
            iVocaBasicItem.setVIVocaKnowPronounce(vocaKnowPronounceToSet);
            return true;
        }

        return false;
    }


    public void updateIconVocaKnowPronounce(Context context, IVocaBasicItem iVocaBasicItem, ImageView ivVocaKnowPronounce) {
//        ivVocaKnowPronounce.setVisibility(VocaKnow.isShowVocaKnowPronounceIcon(iVocaBasicItem) ? View.VISIBLE : View.INVISIBLE);
//        boolean showKnowPronounceIcon = ViewUtil.showKnowPronounceIcon(SharedPreferencesDB.getInstance(context).getLangStudyCode(), iVocaBasicItem.getVIVocaKnow(), iVocaBasicItem.getVIVocaKnowPronounce());
//        ivVocaKnowPronounce.setVisibility(showKnowPronounceIcon ? View.VISIBLE : View.INVISIBLE);
        BaseVocaKnow.updateIconVocaKnowPronounce(context, ivVocaKnowPronounce, iVocaBasicItem);
    }

    public void updateIconVocaKnow(Context context, IVocaBasicItem iVocaBasicItem, TextView tvKnowIcon) {
        BaseVocaKnow.updateIconVocaKnow(context, tvKnowIcon, iVocaBasicItem.getVIVocaKnow());
    }

    public void updateIconVocaBookmark(IVocaBasicItem iVocaBasicItem, ImageView ivBookmark, boolean isDispalyIconAlways) {
//        ivBookmark.setVisibility(iVocaBasicItem.isVIBookmark() ? View.VISIBLE : View.INVISIBLE);
//        VocaKnow.updateBookmarkIcon(ivBookmark, iVocaBasicItem.isVIBookmark());
        BaseVocaKnow.updateIconVocaBookmark(ivBookmark, iVocaBasicItem.isVIBookmark(), isDispalyIconAlways);
    }

}
