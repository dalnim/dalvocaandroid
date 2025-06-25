package com.dalread;

import android.app.Activity;
import android.app.Application;
import android.content.ComponentCallbacks2;
import android.webkit.WebView;

import com.dalread.base.EnumFlavor;
import com.dalread.database.SharedPreferencesDB;
import com.dalread.helper.PlayTTSHelper;
import com.dalread.helper.PlayVocaHelper;
import com.dalread.helper.PlayVocaNativeSpeakerHelper;
import com.dalread.model.BookMarkModel;
import com.dalread.network.DalApi;
import com.dalread.network.DalApiImpl;
import com.dalread.network.DalLoggingInterceptor;
import com.dalread.network.FirebaseApi;
import com.dalread.network.FirebaseApiImpl;
import com.dalread.util.BaseStorageUtil;
import com.dalread.util.Constant;
import com.dalread.util.DLog;
import com.dalread.util.DarkThemeUtil;
import com.dalread.util.NetworkUtil;
import com.dalread.util.Utils;
import com.facebook.stetho.Stetho;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.uphyca.stetho_realm.RealmInspectorModulesProvider;

import org.greenrobot.eventbus.EventBus;

import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import io.realm.DynamicRealm;
import io.realm.FieldAttribute;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmMigration;
import io.realm.RealmSchema;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
/**
 * Created by JetVHS on 2/21/2017.
 */
//Application.ActivityLifecycleCallbacks은 왜 어디에 쓸려고 쓰게 되었는지 파악 필요.
public abstract class BaseApplication extends Application implements Application.ActivityLifecycleCallbacks {

    private final String TAG = this.getClass().getName();
    protected Retrofit retrofit;
    private DalApi dalApi;
    private DalApiImpl dalAiImpl;
    private Retrofit firebaseRetrofit;
    private FirebaseApi firebaseApi;
    private FirebaseApiImpl firebaseApiImpl;
    private EventBus eventBus = EventBus.getDefault();
    protected SharedPreferencesDB mSharedPref;
//    private WordListModel wordListModel; //이건 안쓰는거 같음.
    private Activity currentActivity;
    private boolean isOpenConfirm = true;
    private boolean isBackground = false;
    public static BaseApplication instance;
    private PlayVocaHelper playVocaHelper; //Will delete later(Will use PlayTTSHelper)
    private PlayTTSHelper playTTSHelper;
    private PlayVocaNativeSpeakerHelper playVocaNativeSpeakerHelper;
//    public static int darkLightTheme;

    public abstract RealmConfiguration onInitRealmConfiguration();
    public abstract void initData();
    public abstract void buildRetrofit(String baseUrl);

    public static BaseApplication getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        initMobileAds();
        if (BuildConfig.DEBUG) {
            WebView.setWebContentsDebuggingEnabled(true);
        }
        mSharedPref = SharedPreferencesDB.getInstance(this);
        this.buildBaseRetrofit(mSharedPref.getBaseUrl());
        buildFirebaseRetrofit();
        createAllBaseFolder();
        Realm.init(this);
        RealmConfiguration realmConfig;
        if (BuildConfig.FLAVOR.equals(EnumFlavor.ENGLISH_FREE.getName())) {
            realmConfig = new RealmConfiguration.Builder()
                    .name(Realm.DEFAULT_REALM_NAME)
                    .schemaVersion(BuildConfig.DB_VERSION)
                    .migration(veltraMigration)
                    .build();
        } else {
            realmConfig = onInitRealmConfiguration();
        }
        if (realmConfig != null) {
            Realm.setDefaultConfiguration(realmConfig);
        }
//        getDefaultInstance()를 하면 /data/data/com.dalnimsoft.arakoica/files/araonesoft.realm이 빈걸로 초기화 되어버린다. 왜그런지는 모르겠음.
//        Realm r2 = Realm.getDefaultInstance();
        initData();
        initDataDefault();
        DarkThemeUtil.setDarkModeOnCreate(this);
//        setDarkModeOnCreate();
        increaseAppUseCount();
        playVocaHelper = new PlayVocaHelper(this);
        playTTSHelper = new PlayTTSHelper(this);
        playVocaNativeSpeakerHelper = new PlayVocaNativeSpeakerHelper(this);
        registerActivityLifecycleCallbacks(this);

        Stetho.initialize(
                Stetho.newInitializerBuilder(this)
                        .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                        .enableWebKitInspector(RealmInspectorModulesProvider.builder(this).build())
                        .build());

        RealmInspectorModulesProvider.builder(this)
                .withFolder(getCacheDir())
                .withMetaTables()
                .withDescendingOrder()
                .withLimit(1000)
                .databaseNamePattern(Pattern.compile(".+\\.realm"))
                .build();

        NetworkUtil.updateLastAccessDate(instance, Constant.ACCESS_OR_EXIT_APP.ACCESS);
    }

    private void increaseAppUseCount() {
        long appUseCount = mSharedPref.getAppUseCount();
        mSharedPref.setAppUseCount(appUseCount + 1);
    }
    protected void initMobileAds() {
        new Thread(
                () -> {
                    // Initialize the Google Mobile Ads SDK on a background thread.
                    MobileAds.initialize(this, initializationStatus -> {});
                })
                .start();
//
//        MobileAds.initialize(this, new OnInitializationCompleteListener() {
//            @Override
//            public void onInitializationComplete(InitializationStatus initializationStatus) {
//            }
//        });
    }
    public void buildBaseRetrofit(String baseUrl) {
        DLog.d("SON", "buildRetrofit baseUrl=" + baseUrl);
        OkHttpClient.Builder builder = new OkHttpClient().newBuilder();
        if (Constant.DEBUG) {
//            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
//            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
//            builder.addInterceptor(loggingInterceptor);
            builder.addInterceptor(new DalLoggingInterceptor());
        }
        OkHttpClient client = builder.connectTimeout(60, TimeUnit.SECONDS).
                readTimeout(600, TimeUnit.SECONDS).writeTimeout(600, TimeUnit.SECONDS).build();
        retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
        dalApi = retrofit.create(DalApi.class);
        dalAiImpl = new DalApiImpl(this);
        buildRetrofit(baseUrl);
    }

    public void buildFirebaseRetrofit() {
        OkHttpClient.Builder builder = new OkHttpClient().newBuilder();
        if (Constant.DEBUG) {
//            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
//            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
//            builder.addInterceptor(loggingInterceptor);
            builder.addInterceptor(new DalLoggingInterceptor());
        }
        OkHttpClient client = builder.connectTimeout(60, TimeUnit.SECONDS).
                readTimeout(600, TimeUnit.SECONDS).writeTimeout(600, TimeUnit.SECONDS).build();
        firebaseRetrofit = new Retrofit.Builder()
                .baseUrl(Constant.BASE_API_URL_FIREBASE)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
        firebaseApi = firebaseRetrofit.create(FirebaseApi.class);
        firebaseApiImpl = new FirebaseApiImpl(this);
    }

//    @Override
//    protected void attachBaseContext(Context newBase) {
//        super.attachBaseContext(newBase);
////        MultiDex.install(this); //TODO : I think we don't need MultiDex for AraApps. minSdkVersion 이 21보다 작을때만 필요함. 그 이상은 기본으로 설정됨.
//    }

    private void initDataDefault() {
        if (mSharedPref.getFirstLaunchApp()) {
//            mSharedPref.setFirstLaunchApp(false);
            if (!BuildConfig.FLAVOR.equals(EnumFlavor.DALVOCA.getName()) && !BuildConfig.FLAVOR.equals(EnumFlavor.ARAHANJA.getName()) && !BuildConfig.FLAVOR.equals(EnumFlavor.ARAKOICA.getName())) {
                //여기를 타면 araonesoft.realm이 초기화 되어 버림. 그리고 initBookmarkDefault는 현재 안쓰는것임.
//                initBookmarkDefault();
            }
            initVoiceRecordingFileListInRealm(); //이건 아라한글에서만 사용되는데, 이런건 위에서 initData();로 가지고 아라한글에서만 실행하면 될거 같은데...
        }
    }

    protected void createAllBaseFolder() {
        DLog.d("", "createDalPlayerFolder");
        BaseStorageUtil.createAllBaseFolder(this);
    }
//    //이건 이제 더이상 안쓴다. 아주 옛날앱에서 쓰던거다.
//    private void initBookmarkDefault() {
//        Realm realm = Realm.getDefaultInstance();
//        final BookMarkModel bookmarkModel = realm.where(BookMarkModel.class).findFirst();
//        if (bookmarkModel != null) return;
//        realm.executeTransaction(new Realm.Transaction() {
//            @Override
//            public void execute(Realm realm) {
//                createBookmark(realm, Constant.BOOKMARK_DEFAULT.BOOKMARK_URL, Constant.BOOKMARK_DEFAULT.BOOKMARK_TITLE);
//                createBookmark(realm, Constant.BOOKMARK_DEFAULT.BOOKMARK_URL_1, Constant.BOOKMARK_DEFAULT.BOOKMARK_TITLE_1);
//            }
//        });
//        realm.close();
//    }

    protected void initVoiceRecordingFileListInRealm() {

    }
    private BookMarkModel createBookmark(Realm realm, final String url, final String title) {
        BookMarkModel bookmarkModel = realm.createObject(BookMarkModel.class, Utils.getNextKeyBookmarkModel(realm));
        bookmarkModel.setUrl(url);
        bookmarkModel.setName(title);
        return bookmarkModel;
    }

    RealmMigration veltraMigration = new RealmMigration() {
        @Override
        public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {
            DLog.d(TAG, "oldVersion=" + oldVersion + " - newVersion=" + newVersion);
            RealmSchema schema = realm.getSchema();
            if (oldVersion == 1) {
                schema.create("ReadingListModel")
                        .addField("id", long.class, FieldAttribute.PRIMARY_KEY)
                        .addField("uId", String.class)
                        .addField("title", String.class)
                        .addField("content", String.class)
                        .addField("md5", String.class)
                        .addField("allWordsCount", String.class)
                        .addField("knownWordsCount", String.class)
                        .addField("unknownWordsCount", String.class)
                        .addField("bookmarkedWordsCount", String.class)
                        .addField("modDate", String.class)
                        .addField("langStudy", String.class);
                oldVersion++;
            }
        }
    };

    public Retrofit getRetrofit() {
        return retrofit;
    }

    public void setRetrofit(Retrofit retrofit) {
        this.retrofit = retrofit;
    }

    public DalApi getDalApi() {
        return dalApi;
    }

    public void setDalApi(DalApi dalApi) {
        this.dalApi = dalApi;
    }

    public DalApiImpl getDalAiImpl() {
        return dalAiImpl;
    }

    public void setDalAiImpl(DalApiImpl dalAiImpl) {
        this.dalAiImpl = dalAiImpl;
    }

    public EventBus getEventBus() {
        return eventBus;
    }

    public void setEventBus(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    public SharedPreferencesDB getSharedPref() {
        return mSharedPref;
    }

    public void setSharedPref(SharedPreferencesDB mSharedPref) {
        this.mSharedPref = mSharedPref;
    }

//    public WordListModel getWordListModel() {
//        return wordListModel;
//    }
//
//    public void setWordListModel(WordListModel wordListModel) {
//        this.wordListModel = wordListModel;
//    }
//
//    public List<WordModel> getWordList() {
//        return wordListModel == null || wordListModel.getWordModelsUse() == null ? null : wordListModel.getWordModelsUse();
//    }

    public Activity getCurrentActivity() {
        return currentActivity;
    }

    public void setCurrentActivity(Activity currentActivity) {
        this.currentActivity = currentActivity;
    }

    public boolean isOpenConfirm() {
        return isOpenConfirm;
    }

    public void setOpenConfirm(boolean openConfirm) {
        isOpenConfirm = openConfirm;
    }

//    @Override
//    public void onActivityCreated(Activity activity, Bundle bundle) {
//
//    }
//
//    @Override
//    public void onActivityStarted(Activity activity) {
//
//    }
//
//    @Override
//    public void onActivityResumed(Activity activity) {
//        if (isBackground) {
//            isBackground = false;
//        }
//        setCurrentActivity(activity);
//    }
//
//    @Override
//    public void onActivityPaused(Activity activity) {
//
//    }
//
//    @Override
//    public void onActivityStopped(Activity activity) {
//
//    }
//
//    @Override
//    public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {
//
//    }
//
//    @Override
//    public void onActivityDestroyed(Activity activity) {
//        NetworkUtil.updateLastAccessDate(instance, Constant.ACCESS_OR_EXIT_APP.EXIT);
//    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        if (level == ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN) {
            DLog.d(TAG, "app went to background");
            isOpenConfirm = true;
            isBackground = true;
        }
    }

    public boolean isBackground() {
        return isBackground;
    }

    public void setBackground(boolean background) {
        isBackground = background;
    }


    public PlayVocaHelper getPlayVocaHelper() {
        return playVocaHelper;
    }

    public PlayTTSHelper getPlayTTSHelper() {
        return playTTSHelper;
    }

    public PlayVocaNativeSpeakerHelper getPlayVocaNativeSpeakerHelper() {
        return playVocaNativeSpeakerHelper;
    }

    public FirebaseApi getFirebaseApi() {
        return firebaseApi;
    }

    public FirebaseApiImpl getFirebaseApiImpl() {
        return firebaseApiImpl;
    }

//    protected void setDarkModeOnCreate() {
//        darkLightTheme = getDarkLightThemeFromStorage();
//        AppCompatDelegate.setDefaultNightMode(darkLightTheme);
//    }
//
//    public int getDarkLightThemeFromStorage() {
//        int selectedTheme = mSharedPref.getSelectedTheme();
//        if (selectedTheme == EnumTheme.SYSTEM_DEFAULT.getId()) {
//            return MODE_NIGHT_FOLLOW_SYSTEM;
//        } else if (selectedTheme == EnumTheme.DARK.getId()) {
//            return MODE_NIGHT_YES;
//        } else return MODE_NIGHT_NO;
//    }
//
//    public boolean isDarkMode(Context context) {
//        return (context.getResources().getConfiguration().uiMode &
//                Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES;
//    }
}
