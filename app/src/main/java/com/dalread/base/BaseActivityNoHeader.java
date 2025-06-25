package com.dalread.base;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.dalread.BaseApplication;
import com.dalread.BuildConfig;
import com.dalread.R;
import com.dalread.activity.ChangePasswordActivity;
import com.dalread.activity.ConfirmTTSActivity;
import com.dalread.activity.ConfirmTTSSettingActivity;
import com.dalread.activity.SortActivity;
import com.dalread.activity.SpeakActivity;
import com.dalread.activity.WordActivity;
import com.dalread.activity.WordChangeActivity;
import com.dalread.database.SharedPreferencesDB;
import com.dalread.dialog.ConfirmExitDialog;
import com.dalread.model.WordModel;
import com.dalread.network.events.BaseEvent;
import com.dalread.network.events.ErrorEvent;
import com.dalread.network.events.SuccessEvent;
import com.dalread.util.Constant;
import com.dalread.util.DLog;
import com.dalread.util.LanguageUtil;
import com.dalread.util.Loading;
import com.dalread.util.LocaleHelper;
import com.dalread.util.ToastUtil;
import com.dalread.util.Utils;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.Locale;

import butterknife.BindView;
import io.realm.Realm;

//이건 나중에 BaseActivity를 상속받으면 안되나?
public abstract class BaseActivityNoHeader extends AppCompatActivity implements IActivity {

    protected Context mContext;
    protected Resources res;
    protected SharedPreferencesDB mSharedPref;
    protected final String TAG = this.getClass().getName();
    protected Realm realm;
    protected boolean isBack = false;
    protected EventBus eventBus = EventBus.getDefault();
    protected BaseApplication mApplication;
    @Nullable
    @BindView(R.id.llAdmob)
    protected AdView llAdmob;
//    protected BillingProcessor billingProcessor;
    protected boolean readyToPurchase = false;
    protected String mLanguage;
    // TTS
    protected final int TTS_CHECK_CODE = 1505;
    protected TextToSpeech ttsWord;
    protected TextToSpeech ttsMeaning;
    protected boolean isTTSSupport = true;
    protected ITTSListener ttsListener;
    protected ITTSListener ttsListener2;
    protected int sizeTTS = 0;
    protected boolean isTTSSettingHasChanged = false;

    private Handler handlerLoading;

    protected int currentAudio = Constant.AUDIO_SOUND.AUDIO_NORMAL;
    private AudioManager audioManager;

    public static final int PERMISSION_REQUEST_CODE = 16;

    protected abstract void onPermissionSuccess(int request_code);

    protected abstract void onPermissionFail(int request_code);

    protected abstract void onChangeLanguage();

    @Override
    protected void attachBaseContext(Context newBase) {
//        super.attachBaseContext(newBase);
        super.attachBaseContext(LocaleHelper.onAttach(newBase)); //TODO : Do we need this?
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        DLog.e(TAG, "onCreate()");
        mApplication = (BaseApplication) getApplication();
        mContext = this.getApplicationContext();
        res = mContext.getResources();
        if (!eventBus.isRegistered(this)) {
            eventBus.register(this);
        }
        mSharedPref = SharedPreferencesDB.getInstance(mContext);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        realm = Realm.getDefaultInstance();
        super.onCreate(savedInstanceState);
        if (!BuildConfig.FLAVOR.equals(EnumFlavor.DALVOCA.getName())) {
            settingLanguage();
        }
        overridePendingTransition(R.anim.slide_right_to_left_in, R.anim.slide_right_to_left_out);
        this.onIOnCreate(savedInstanceState);
        if (llAdmob != null) {
            llAdmob.loadAd(new AdRequest.Builder().build());
        }
    }

    @Subscribe
    public void onEvent(ErrorEvent event) {
        DLog.d(TAG, "ErrorEvent=" + event.getScreen() + " - type=" + event.getEventType());
        this.onErrorEvent(event);
    }

    @Subscribe
    public void onEvent(final SuccessEvent event) {
        DLog.d(TAG, "SuccessEvent=" + event.getScreen() + " - type=" + event.getEventType());
        this.onSuccessEvent(event);
    }

    public void initAudioManager() {
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        switch (audioManager.getRingerMode()) {
            case AudioManager.RINGER_MODE_SILENT:
                currentAudio = Constant.AUDIO_SOUND.AUDIO_MUTE;
                break;
            default:
                final int curVol = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                currentAudio = curVol > 0 ? Constant.AUDIO_SOUND.AUDIO_NORMAL : Constant.AUDIO_SOUND.AUDIO_ZERO;
                DLog.d(TAG, "curVol=" + curVol + " - currentAudio=" + currentAudio);
                break;
        }
        initSoundBroadcast();
    }

    public void initSoundBroadcast() {
        IntentFilter filter = new IntentFilter(AudioManager.RINGER_MODE_CHANGED_ACTION);
        registerReceiver(receiver, filter);
    }

    public void stopAudioManager() {
        if (audioManager != null) {
            audioManager = null;
            unregisterReceiver(receiver);
        }
    }

    public BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int num = intent.getIntExtra(AudioManager.EXTRA_RINGER_MODE, -1);
            DLog.d(TAG, "num=" + num);
            switch (num) {
                case AudioManager.RINGER_MODE_SILENT:
                    currentAudio = Constant.AUDIO_SOUND.AUDIO_MUTE;
                    break;
                default:
                    final int curVol = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                    currentAudio = curVol > 0 ? Constant.AUDIO_SOUND.AUDIO_NORMAL : Constant.AUDIO_SOUND.AUDIO_ZERO;
                    DLog.d(TAG, "curVol=" + curVol + " - currentAudio=" + currentAudio);
                    break;
            }
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_UP:
                changeUpVolume();
                return true;
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                changeDownVolume();
                return true;
            default:
                return super.onKeyDown(keyCode, event);
        }
    }

    private void changeUpVolume() {
        if (audioManager == null)
            return;
        audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
        switch (audioManager.getRingerMode()) {
            case AudioManager.RINGER_MODE_SILENT:
                currentAudio = Constant.AUDIO_SOUND.AUDIO_MUTE;
                break;
            default:
                final int curVol = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                currentAudio = curVol > 0 ? Constant.AUDIO_SOUND.AUDIO_NORMAL : Constant.AUDIO_SOUND.AUDIO_ZERO;
                DLog.d(TAG, "curVol=" + curVol + " - currentAudio=" + currentAudio);
                break;
        }
    }

    private void changeDownVolume() {
        if (audioManager == null)
            return;
        audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI);
        switch (audioManager.getRingerMode()) {
            case AudioManager.RINGER_MODE_SILENT:
                currentAudio = Constant.AUDIO_SOUND.AUDIO_MUTE;
                break;
            default:
                final int curVol = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                currentAudio = curVol > 0 ? Constant.AUDIO_SOUND.AUDIO_NORMAL : Constant.AUDIO_SOUND.AUDIO_ZERO;
                DLog.d(TAG, "curVol=" + curVol + " - currentAudio=" + currentAudio);
                break;
        }
    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    @Override
    protected void onRestart() {
        // TODO Auto-generated method stub
        DLog.e(TAG, "onRestart()");
        super.onRestart();
        this.onIRestart();
    }

    @Override
    protected void onStart() {
        // TODO Auto-generated method stub
        DLog.e(TAG, "onStart()");
        super.onStart();
        this.onIStart();
    }

    @Override
    protected void onResume() {
        DLog.e(TAG, "onResume() - isTTSSettingHasChanged=" + isTTSSettingHasChanged);
        mApplication.setCurrentActivity(this);

        super.onResume();

        if (!BuildConfig.FLAVOR.equals(EnumFlavor.DALVOCA.getName())
                && !BuildConfig.FLAVOR.equals(EnumFlavor.ARAPLAYER.getName())) {
            openConfirm(getCurrentScreen());
        }
        if (isTTSSettingHasChanged && !isTTSSupport) {
            isTTSSettingHasChanged = false;
            isTTSSupport = true;
            shutdownTTS();
            initTTS();
        }
        //이건 쓸일이 있는지 모르겠다.
//        if (LanguageUtil.isChangeLanguages(mSharedPref, mLanguage)) {
//            settingLanguage();
//            onChangeLanguage();
//        }
        if (llAdmob != null) {
            llAdmob.resume();
        }
        updateLayoutAds();
        this.onIResume();
    }

    @Override
    protected void onPause() {
        DLog.e(TAG, "onPause()");
        if (llAdmob != null) {
            llAdmob.pause();
        }
        super.onPause();
        this.onIPause();
    }

    @Override
    protected void onStop() {
        DLog.e(TAG, "onStop()");
        stopTTS();
        super.onStop();
        this.onIStop();
    }

    @Override
    protected void onDestroy() {
        DLog.e(TAG, "onDestroy()");
        shutdownTTS();
        clearCurrentActivity();
        if (llAdmob != null) {
            llAdmob.destroy();
        }
//        if (billingProcessor != null)
//            billingProcessor.release();
        eventBus.unregister(this);
        hideLoading();
        stopAudioManager();
        super.onDestroy();
        this.onIDestroy();
    }

    @Override
    protected void onUserLeaveHint() {
        clearCurrentActivity();
        super.onUserLeaveHint();
        this.onIUserLeaveHint();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (billingProcessor != null && !billingProcessor.handleActivityResult(requestCode, resultCode, data))
//            super.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
//        this.onIActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        DLog.e(TAG, "onBackPressed()");
        if (isBack) {
            showConfirmExit();
            return;
        }
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_left_to_right_in, R.anim.slide_left_to_right_out);
        this.onIBackPressed();
    }

    protected void showConfirmExit() {
        new ConfirmExitDialog(EnumType.D_CONFIRM_EXIT, this, new BaseDialogListener() {
            @Override
            public void onBaseDialogListenerShow(EnumType type, BaseDialog dialog, View v, int position, Object data) {

            }

            @Override
            public void onBaseDialogListenerOk(EnumType type, BaseDialog dialog,View v, int position, Object data) {
                finish();
            }

            @Override
            public void onBaseDialogListenerCancel(EnumType type, BaseDialog dialog,View v, int position, Object data) {

            }

            @Override
            public void onBaseDialogListenerClick(EnumType type, BaseDialog dialog,View v, int position, Object data) {

            }

            @Override
            public void onBaseDialogListenerClickMulti(EnumType type, BaseDialog dialog,View v, int position, Object[] data) {

            }
        }).show();
    }

    protected void showLoading() {
        Loading.show(this);
    }

    protected void hideLoading() {
        hideLoading(true);
    }

    protected void hideLoading(final boolean isDelay) {
        if (handlerLoading == null) {
            handlerLoading = new Handler(Looper.getMainLooper());
        }
        handlerLoading.postDelayed(new Runnable() {
            @Override
            public void run() {
                Loading.hide();
            }
        }, isDelay ? 300 : 0);
    }

    public EventBus getEventBus() {
        return eventBus;
    }

    public void setEventBus(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    protected int getScreenHeight() {
        return findViewById(android.R.id.content).getHeight();
    }

    public boolean isLogin() {
        return mSharedPref != null && mSharedPref.isUID();
    }

    public boolean isNetwork() {
        if (!Utils.isConnected(this)) {
            ToastUtil.getInstance(this).showErrorNetwork();
            return false;
        }
        return true;
    }

//    public void initBilling(BillingProcessor.IBillingHandler handler) {
//        if (!BillingProcessor.isIabServiceAvailable(this)) {
//            ToastUtil.getInstance(this).show(R.string.error_msg_billing_unavailable);
//        }
//        bp = new BillingProcessor(this, getString(R.string.billing_base_64).trim(), Constant.BILLING.MERCHANT_ID, handler);
//    }
//
//    public void consumePurchaseAds() {
//        if (bp != null) {
//            if (bp.consumePurchase(getString(R.string.billing_admob_project_id))) {
//                updateLayoutAds();
//                onChangeLanguage();
//                ToastUtil.getInstance(this).show(R.string.msg_ads_restore_success);
//            }
//        }
//    }

    public void removeAds() {
        if (!readyToPurchase) {
            ToastUtil.getInstance(this).show(R.string.error_msg_billing_not_init);
            return;
        }
//        if (billingProcessor != null) {
//            billingProcessor.purchase(this, getString(R.string.billing_admob_project_id));
//        }
    }

    public void updateLayoutAds() {
        if (llAdmob != null) {
            llAdmob.setVisibility(isRemoveAds() ? View.GONE : View.VISIBLE);
        }
        onChangeLanguage();
    }

    public boolean isRemoveAds() {
//        if (billingProcessor != null) {
//            return billingProcessor.isPurchased(getString(R.string.billing_admob_project_id));
//        }
        return false;
    }

    public void updateLayoutAds(boolean isShowKeyboard) {
        if (llAdmob != null) {
            if (!isRemoveAds()) {
                llAdmob.setVisibility(isShowKeyboard ? View.GONE : View.VISIBLE);
            }
        }
    }

    public void openConfirmTTSSetting(BaseEvent.Screen screen) {
        startActivity(ConfirmTTSSettingActivity.createIntent(this, screen));
    }

    public void openTTSSetting() {
        isTTSSettingHasChanged = true;
        Intent intent = new Intent();
        intent.setAction("com.android.settings.TTS_SETTINGS");
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public void checkTTSIntent() {
        Intent checkIntent = new Intent();
        checkIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(checkIntent, TTS_CHECK_CODE);
    }

    public void startInstallTSS() {
        Intent installIntent = new Intent();
        installIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
        startActivity(installIntent);
    }
    //이건 왜하냐? 앱을 열때 클립보드에 내용이 있으면 그기에서 바로 모르는 단어 찾을려고 한거 같은데. 이걸 쓰지 않는다. ConfirmActivity도 없다.
    public void openConfirm(final BaseEvent.Screen screen) {
        DLog.d(TAG, "openConfirm");
        if (!mApplication.isOpenConfirm()) {
            return;
        }
        if (screen == null) return;
        DLog.d(TAG, "screen=" + screen.toString());
        if (screen == BaseEvent.Screen.CONFIRM) {
            finish();
            return;
        }
        mApplication.setOpenConfirm(false);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String textClip = Utils.getTextClipboard(BaseActivityNoHeader.this, true);
                if (!Utils.isEmpty(textClip)) {
                    final String mCopiedText = mSharedPref.getCopiedText();
                    DLog.d(TAG, "textClip=" + textClip + " - mCopiedText=" + mCopiedText);
                    if (!mCopiedText.equals(textClip)) {
                        if (isContainRightCharToTheLanguage(textClip)) {
                            mSharedPref.setCopiedText(textClip);
//                            startActivity(ConfirmActivity.createIntent(BaseActivityNoHeader.this, screen));
                        }
                    }
                }
            }
        });
    }

    public boolean isContainRightCharToTheLanguage(final String text) {
        DLog.d(TAG, "isContainRightCharToTheLanguage");
        boolean isRight;
        switch (Constant.API.STUDY_LANG) {
            case Constant.SETTING_LANGUAGES_MOTHERS.LANG_JA:
                isRight = LanguageUtil.checkJapanWord(text);
                break;
            case Constant.SETTING_LANGUAGES_MOTHERS.LANG_CH_S:
                isRight = LanguageUtil.countChineseMatchRate(text) > Constant.CHECK_LANGUAGES.MAX_RATE_CHINESE;
                break;
            default:
                isRight = LanguageUtil.countEnglishMatchRate(text) > Constant.CHECK_LANGUAGES.MAX_RATE_ENGLISH;
                break;
        }
        return isRight;
    }

    public void clearCurrentActivity() {
        final Activity currActivity = mApplication.getCurrentActivity();
        if (currActivity != null && this.equals(currActivity)) {
            mApplication.setCurrentActivity(null);
        }
    }

    /**
     * Setting default language of device for app
     */
    public void settingLanguage() {
        DLog.d(TAG, "settingLanguage");
        if (mSharedPref == null) {
            mSharedPref = SharedPreferencesDB.getInstance(this);
        }
        if (Utils.isEmpty(mSharedPref.getLanguage())) {
            mLanguage = Locale.getDefault().getLanguage();
            DLog.d(TAG, "language = null was set to default=" + mLanguage);
        } else {
            mLanguage = mSharedPref.getLanguage();
            DLog.d(TAG, "language get from share Pref=" + mLanguage);
        }
        // set language for app
        Locale locale = new Locale(mLanguage);
        Configuration config = new Configuration();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            setSystemLocale(config, locale);
        } else {
            setSystemLocaleLegacy(config, locale);
        }
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
    }

    public void settingLanguage(int index) {
        DLog.d(TAG, "settingLanguage - index=" + index + " - mSharedPref.getSettingIndexMotherTongue()=" + mSharedPref.getSettingIndexMotherTongue());
        mLanguage = LanguageUtil.convertIndexToLanguage(index);
        mSharedPref.setLanguage(mLanguage);
        mSharedPref.setSettingMotherTongue(LanguageUtil.getMotherTongueLangDisplay(index));
        DLog.d(TAG, "settingMotherTongue=" + mSharedPref.getSettingMotherTongue());
        settingLanguage();
        onChangeLanguage();
    }

    @SuppressWarnings("deprecation")
    public Locale getSystemLocaleLegacy(Configuration config) {
        return config.locale;
    }

    @TargetApi(Build.VERSION_CODES.N)
    public Locale getSystemLocale(Configuration config) {
        return config.getLocales().get(0);
    }

    @SuppressWarnings("deprecation")
    public void setSystemLocaleLegacy(Configuration config, Locale locale) {
        config.locale = locale;
    }

    @TargetApi(Build.VERSION_CODES.N)
    public void setSystemLocale(Configuration config, Locale locale) {
        config.setLocale(locale);
    }

    public boolean checkTTSSupported(BaseEvent.Screen screen) {
        if (currentAudio == Constant.AUDIO_SOUND.AUDIO_MUTE) {
            ToastUtil.getInstance(this).show(R.string.error_msg_check_mute);
            return false;
        }
        if (currentAudio == Constant.AUDIO_SOUND.AUDIO_ZERO) {
            ToastUtil.getInstance(this).show(R.string.error_msg_zero_volume);
            return false;
        }
        if (!isTTSSupport) {
//            if (!isCheckTTSSetting) {
//                isCheckTTSSetting = true;
            openConfirmTTSSetting(screen);
//            }
            return false;
        }
        return true;
    }

    public void restoreSizeTTS() {
        sizeTTS = 0;
    }

    public void stopTTS() {
        DLog.d(TAG, "stopTTS");
        restoreSizeTTS();
        if (ttsWord != null) {
            ttsWord.stop();
        }
        if (ttsMeaning != null) {
            ttsMeaning.stop();
        }
    }

    public void shutdownTTS() {
        DLog.d(TAG, "shutdownTTS");
        restoreSizeTTS();
        if (ttsWord != null) {
            ttsWord.stop();
            ttsWord.shutdown();
        }
        ttsWord = null;
        if (ttsMeaning != null) {
            ttsMeaning.stop();
            ttsMeaning.shutdown();
        }
        ttsMeaning = null;
    }

    public void initTTS() {
        ttsWord = new TextToSpeech(this, ttsInitListener);
        loadTTSWordSpeed();
    }

    protected TextToSpeech.OnInitListener ttsInitListener = new TextToSpeech.OnInitListener() {
        @Override
        public void onInit(int status) {
            if (status == TextToSpeech.SUCCESS) {
                int resultVoiceLang = ttsWord.setLanguage(new Locale(Constant.API.VOICE_LANG));
                DLog.d(TAG, "resultVoiceLang=" + resultVoiceLang);
                if (resultVoiceLang == TextToSpeech.LANG_MISSING_DATA || resultVoiceLang == TextToSpeech.LANG_NOT_SUPPORTED) {
                    isTTSSupport = false;
//                    ToastUtil.getInstance(this).show((BaseActivityNoHeader.this, R.string.error_msg_tts_not_supported);
                    return;
                }
                initTTSMeaning();
                ttsWord.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                    @Override
                    public void onStart(String utteranceId) {
                        DLog.d(TAG, "start ttsListener1 - data=" + utteranceId);
                        if (ttsListener != null) {
                            ttsListener.onTTSStart(utteranceId);
                        }
                    }

                    @Override
                    public void onDone(String utteranceId) {
                        DLog.d(TAG, "onDone ttsListener1 - data=" + utteranceId);
                        DLog.d(TAG, "isSpeaking=" + ttsWord.isSpeaking());
                        sizeTTS--;
                        if (ttsListener != null) {
                            ttsListener.onTTSDone(utteranceId);
                            if (sizeTTS <= 0) {
                                ttsListener.onComplete(sizeTTS);
                            }
                        }
                    }

                    @Override
                    public void onError(String utteranceId) {
                        DLog.d(TAG, "onError ttsListener1 - data=" + utteranceId);
                        DLog.d(TAG, "isSpeaking=" + ttsWord.isSpeaking());
                        sizeTTS--;
                        if (ttsListener != null) {
                            ttsListener.onTTSError(utteranceId);
                        }
                    }
                });
            } else {
//                ToastUtil.getInstance(this).show((BaseActivityNoHeader.this, R.string.error_msg_tts_initializing);
                isTTSSupport = false;
            }
        }
    };

    public void initTTSMeaning() {
        ttsMeaning = new TextToSpeech(this, ttsInitListenerMeaning);
        loadTTSMeaningSpeed();
    }

    public void loadTTSWordSpeed() {
        final float speed = (float) mSharedPref.getSettingTTSSpeed() / 50;
        if (ttsWord != null) {
            ttsWord.setSpeechRate(speed);
        }
    }


    public void loadTTSMeaningSpeed() {
        final float speed = (float) mSharedPref.getSettingTTSSpeed() / 50;
        if (ttsMeaning != null) {
            ttsMeaning.setSpeechRate(speed);
        }
    }

    public void updateTTSWordSpeed(int value) {
        mSharedPref.setSettingTTSSpeed(value);
        final float speed = (float) value / 50;
        DLog.d(TAG, "updateTTSWordSpeed - value=" + value + " - speed=" + speed);
        if (ttsWord != null) {
            ttsWord.setSpeechRate(speed);
        }
    }

    public void updateTTSMeaningSpeed(int value) {
        mSharedPref.setSettingTTSSpeed(value);
        final float speed = (float) value / 50;
        DLog.d(TAG, "updateTTSMeaningSpeed - value=" + value + " - speed=" + speed);
        if (ttsMeaning != null) {
            ttsMeaning.setSpeechRate(speed);
        }
    }

    protected TextToSpeech.OnInitListener ttsInitListenerMeaning = new TextToSpeech.OnInitListener() {
        @Override
        public void onInit(int status) {
            if (status == TextToSpeech.SUCCESS) {
                int resultVoiceMother = ttsMeaning.setLanguage(new Locale(LanguageUtil.getMotherTongueVoiceLang(mSharedPref.getSettingMotherTongue())));
                DLog.d(TAG, "resultVoiceMother=" + resultVoiceMother);
                if (resultVoiceMother == TextToSpeech.LANG_MISSING_DATA || resultVoiceMother == TextToSpeech.LANG_NOT_SUPPORTED) {
                    isTTSSupport = false;
//                    ToastUtil.getInstance(this).show((BaseActivityNoHeader.this, R.string.error_msg_tts_not_supported);
                    return;
                }
                ttsMeaning.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                    @Override
                    public void onStart(String utteranceId) {
                        DLog.d(TAG, "start ttsListener2 - data=" + utteranceId);
                        if (ttsListener2 != null) {
                            ttsListener2.onTTSStart(utteranceId);
                        }
                    }

                    @Override
                    public void onDone(String utteranceId) {
                        DLog.d(TAG, "onDone ttsListener2 - data=" + utteranceId);
                        sizeTTS--;
                        if (ttsListener2 != null) {
                            ttsListener2.onTTSDone(utteranceId);
                            if (sizeTTS <= 0) {
                                ttsListener2.onComplete(sizeTTS);
                            }
                        }
                    }

                    @Override
                    public void onError(String utteranceId) {
                        DLog.d(TAG, "onError ttsListener2 - data=" + utteranceId);
                        sizeTTS--;
                        if (ttsListener2 != null) {
                            ttsListener2.onTTSError(utteranceId);
                        }
                    }
                });
            } else {
//                ToastUtil.getInstance(this).show((BaseActivityNoHeader.this, R.string.error_msg_tts_initializing);
                isTTSSupport = false;
            }
        }
    };

    public void setTTSListener(ITTSListener ttsListener) {
        this.ttsListener = ttsListener;
    }

    public void setTtsListener2(ITTSListener ttsListener2) {
        this.ttsListener2 = ttsListener2;
    }

    public void callSpeakWordModel(WordModel wordModel) {
        if (wordModel != null) {
            callSpeckWord(wordModel.getWord());
            callSpeckMeaning(wordModel.getMeaning());
        }
    }

    public void callSpeckWord(String value) {
        if (!Utils.isEmpty(value)) {
            if (ttsWord != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    final String utteranceId = this.hashCode() + "";
                    ttsWord.speak(value, TextToSpeech.QUEUE_ADD, null, utteranceId);
                    ttsWord.playSilentUtterance(Constant.TTS_DELAY, TextToSpeech.QUEUE_ADD, null);
                } else {
                    ttsWord.speak(value, TextToSpeech.QUEUE_ADD, null);
                    ttsWord.playSilence(Constant.TTS_DELAY, TextToSpeech.QUEUE_ADD, null);
                }
                sizeTTS++;
            }
        }
    }

    public void callSpeckMeaning(String value) {
        if (!Utils.isEmpty(value)) {
            if (ttsMeaning != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    final String utteranceId = this.hashCode() + "";
                    ttsMeaning.speak(value, TextToSpeech.QUEUE_ADD, null, utteranceId);
                    ttsMeaning.playSilentUtterance(Constant.TTS_DELAY, TextToSpeech.QUEUE_ADD, null);
                } else {
                    ttsMeaning.speak(value, TextToSpeech.QUEUE_ADD, null);
                    ttsMeaning.playSilence(Constant.TTS_DELAY, TextToSpeech.QUEUE_ADD, null);
                }
                sizeTTS++;
            }
        }
    }
    //이건 어디서 사용했지? 현재 아무도 안쓴다.
//    public void toggleFullscreen(boolean fullscreen) {
//        WindowManager.LayoutParams attrs = getWindow().getAttributes();
//        if (fullscreen) {
//            attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
//        } else {
//            attrs.flags &= ~WindowManager.LayoutParams.FLAG_FULLSCREEN;
//        }
//        getWindow().setAttributes(attrs);
//    }

    public BaseEvent.Screen getCurrentScreen() {
        if (this instanceof ChangePasswordActivity) {
            return BaseEvent.Screen.CHANGE_PASSWORD;
        } else if (this instanceof ConfirmTTSActivity) {
            return BaseEvent.Screen.CONFIRM_TTS;
        } else if (this instanceof ConfirmTTSSettingActivity) {
            return BaseEvent.Screen.CONFIRM_TTS_SETTING;
        } else if (this instanceof SortActivity) {
            return BaseEvent.Screen.SORT;
        } else if (this instanceof SpeakActivity) {
            return BaseEvent.Screen.SPEAK;
        } else if (this instanceof WordChangeActivity) {
            return BaseEvent.Screen.WORD_MEANING;
        } else if (this instanceof WordActivity) {
            return BaseEvent.Screen.WORD;
        }
        return null;
    }

//    @TargetApi(Build.VERSION_CODES.M)
//    public void startFloatyForAboveAndroidL() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            if (!Settings.canDrawOverlays(this)) {
//                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
//                        Uri.parse("package:" + getPackageName()));
//                startActivityForResult(intent, PERMISSION_REQUEST_CODE);
//            }
//        }
//    }
}
