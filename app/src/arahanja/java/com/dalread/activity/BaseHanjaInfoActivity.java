package com.dalread.activity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dalread.AraHanjaApplication;
import com.dalread.R;
import com.dalread.adapter.SearchHanjaAdapter;
import com.dalread.base.BaseActivity;
import com.dalread.component.BaseFuriganaView;
import com.dalread.component.FuriganaView;
import com.dalread.composition.VocaKnowActivity;
import com.dalread.dialog.DialogAddSentence;
import com.dalread.dialog.HanjaCommonMenuDialog;
import com.dalread.dialog.HanjaExcludeOptionDialog;
import com.dalread.interfaces.IVocaBasicItem;
import com.dalread.listener.OnDoubleClickListener;
import com.dalread.model.DIC_HANJA;
import com.dalread.model.DIC_HANJA_BOOK;
import com.dalread.model.DIC_HANJA_SENTENCE;
import com.dalread.model.HanjaItem;
import com.dalread.model.RubyTextModel;
import com.dalread.network.DalApiListener;
import com.dalread.network.events.BaseEvent;
import com.dalread.network.events.SuccessEvent;
import com.dalread.util.BaseBindUtils;
import com.dalread.util.BaseMobileAd;
import com.dalread.util.BaseVoca;
import com.dalread.util.Constant;
import com.dalread.util.DLog;
import com.dalread.util.DarkThemeUtil;
import com.dalread.util.HanjaVoca;
import com.dalread.util.LanguageUtilKorean;
import com.dalread.util.Loading;
import com.dalread.util.MobileAd;
import com.dalread.util.StringUtils;
import com.dalread.util.ToastUtil;
import com.dalread.util.UserUtil;
import com.dalread.util.Utils;
import com.dalread.util.Voca;
import com.dalread.util.VocaKnow;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import butterknife.BindColor;
import butterknife.BindDimen;
import me.zhanghai.android.fastscroll.FastScrollerBuilder;

@SuppressLint("NonConstantResourceId")
public abstract class BaseHanjaInfoActivity extends BaseActivity {
    protected VocaKnowActivity vocaKnowActivity;
    protected OnDoubleClickListener onDoubleClickListenerForCommon;
    protected FuriganaView.OnTextSelectedListener onRubyTextSelectedListener;
    protected abstract void getData();
    protected abstract RecyclerView getRvSearch();

    HanjaCommonMenuDialog hanjaCommonMenuDialog;
    HanjaExcludeOptionDialog hanjaExcludeOptionDialog;
    protected SearchHanjaAdapter searchHanjaAdapter;


    protected List<HanjaItem> hanjaListInSearchView;
    protected FrameLayout adContainerView;
    protected AdView mAdView;
    protected boolean blnScrollToFirstInHanjaListView;
    protected boolean canShowAds = true;
    private String keyword = "";

    @Override
    public void onHeaderLeftClick() {
        onBackPressed();
    }
    @Override
    public void onHeaderLeft2Click() {
    }
    @Override
    public void onHeaderRightClick() {
    }

    @Override
    public void onHeaderIconRightClick() {
    }

    @Override
    public void onHeaderTextRightClick() {
        hanjaCommonMenuDialog.show();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        vocaKnowActivity = new VocaKnowActivity(this);
        long lastTimeClickOnAds = sharedPreferences.getLastAdsClickedTime();
        if (lastTimeClickOnAds > 0) {
            if (System.currentTimeMillis() - lastTimeClickOnAds < Constant.TIME_DISTANCE_TO_SHOW_ADS_AFTER_CLICKED) {
                canShowAds = false;
            }
        }
        initListener(); //Do this before initLayout
        initData();
        initLayout();
        initDialog();


        initSearch();
        getData();
        initListener();
    }

    @Override
    protected void onDestroy() {
        unRegisterEventBus();

        super.onDestroy();
    }

    protected void initData() {
        blnScrollToFirstInHanjaListView = true;
        hanjaListInSearchView = new ArrayList<>();
    }

    protected void initLayout() {
        new FastScrollerBuilder(getRvSearch()).build();
//        getRvSearch().setBackgroundTintList(context.getColorStateList(R.color.colorBlack));
//        getRvSearch().setAdapter(getAdapter()); //initSearch()의 getRvSearch().setAdapter(searchHanjaAdapter);와 차이점은???
//        getRvSearch().setLayoutManager(new LinearLayoutManager(context));
//        getRvSearch().addItemDecoration(new SeparatorDecoration(context, clDivider, dividerHeight));
    }

    protected void initDialog() {
        hanjaCommonMenuDialog = new HanjaCommonMenuDialog(context, onDialogItemClickListener);
        hanjaExcludeOptionDialog = new HanjaExcludeOptionDialog(context, onHanjaExcludeOptionDialogItemClickListener);
    }

    protected void initSearch() {
        searchHanjaAdapter = new SearchHanjaAdapter(this);
        searchHanjaAdapter.setOnDoubleClickListener(vocaKnowActivity.onDoubleClickListenerOnBaseVocaKnow);
        getRvSearch().setAdapter(searchHanjaAdapter);
        getRvSearch().setLayoutManager(new LinearLayoutManager(this));
        getRvSearch().addItemDecoration(BaseBindUtils.getSeparatorDecoration(this));

        getToolbar().setSearchListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                keyword = StringUtils.normalizeString(s.trim());
                if (keyword.isEmpty()) {
//                    hanjaListInSearchView = new ArrayList<>();
                    hideHanjaSearchResultView();
                } else {
                    showHanjaSearchResultView();
                    hanjaListInSearchView = new ArrayList<>();
                    if (LanguageUtilKorean.isChoSung(keyword.substring(keyword.length()-1))) {
//                        if (keyword.length() == 1)
//                            hanjaListInSearchView = new ArrayList<>();
                        return true;
                    }
//                    hanjaListInSearchView = new ArrayList<>();
                    List<HanjaItem> hanjaListInSearchViewTemp = new ArrayList<>();
                    BaseVoca.executeRealmTransaction(realm -> {
                        Set<HanjaItem> listResult = Voca.searchHanjaByAllMethodsSorted(realm, keyword);
                        for (HanjaItem hanja : listResult) {
                            if (hanja instanceof DIC_HANJA) {
                                DIC_HANJA dicHanja = (DIC_HANJA) hanja;
                                Voca.searchAlternativeMeaningPronounceFirst(realm, dicHanja);
                            }
                            hanjaListInSearchViewTemp.add(hanja);
                        }
                    });
                    if (!Utils.isEmpty(hanjaListInSearchViewTemp)) {
                        hanjaListInSearchView = hanjaListInSearchViewTemp;
                    }
                    searchHanjaAdapter.setHanjaList(hanjaListInSearchView);
                    if (hanjaListInSearchView.isEmpty()) {
                        //TODO : Need to call this method in other place when there is no data in the Local DB for the searched word.
//                        if (Utils.isConnected(BaseHanjaInfoActivity.this)) {
//                            if (keyword.length() == 1) {
//                                HanjaVoca.getHanjaWordByVocaFromServer(context, keyword);
//                            } else {
//                                HanjaVoca.getHanjaSentenceByVocaFromServer(context, keyword);
//                            }
//                        } else {
                            searchHanjaAdapter.notifyDataSetChanged();
//                        }
                    } else {
                        searchHanjaAdapter.setKeyword(keyword);
                        searchHanjaAdapter.setOnClickListener((view, object) -> {
                            switch (view.getId()) {
                                case R.id.v_item:
                                    openHanjaInfoView(object);
                                    break;
                                case R.id.ivKnow:
                                case R.id.ivBookmark:
                                    if (vocaKnowActivity.onDoubleClickListenerOnBaseVocaKnow != null) {
                                        vocaKnowActivity.onDoubleClickListenerOnBaseVocaKnow.onClick(view, object);
                                    }
                                    break;
//                                case R.id.ivBookmark:
//                                    udpateBookmarkInDB((IVocaBasicItem) object);
//                                    break;
                            }
                        });
                        searchHanjaAdapter.notifyDataSetChanged();
                    }
                }
                return true;
            }
        }, () -> {
            hideHanjaSearchResultView();
            return true;
        });
        getToolbar().showSearchView();
    }

    protected void initListener() {
        onDoubleClickListenerForCommon = new OnDoubleClickListener() {
            @Override
            public void onClick(View view, Object object) {
                if (object instanceof IVocaBasicItem) {
                    switch (view.getId()) {
                        case R.id.tvVoca:
                        case R.id.ivCopy:
                            BaseVoca.openCopyDialog(context, (IVocaBasicItem) object);
                            break;
                        case R.id.ivWordList:
                            onClickWordListIcon(object);
                            break;
                        case R.id.ivEditView:
                            onClickEditIcon();
                            break;
                        default:
                            onClickDefaultOnItemDoubleClickListener(object);
                            break;
                    }

                } else if (object instanceof String) {
                    String text = (String) object;
                    switch (view.getId()) {
                        case R.id.tvVoca:
                            Utils.copyToClipboard(context, text, R.string.copied);
                            break;
                        case R.id.ivWordList:
                            onClickWordListText(text);
                            break;
                        default:
                            onClickDefaultTextOnItemDoubleClickListener(text);
                    }
                }
            }

            @Override
            public void onDoubleClick(View view, Object object) {

            }
        };

        onRubyTextSelectedListener = new BaseFuriganaView.OnTextSelectedListener() {
            @Override
            public void onTextSelected(String text, RubyTextModel rubyTextModel) {
                openHanjaWordInfoView(rubyTextModel);
            }

            @Override
            public void onDoubleClick(String text, RubyTextModel rubyTextModel) {
                DIC_HANJA hanja = Voca.searchHanjaWordByID((long) rubyTextModel.getVocaId());

                int vocaKnowToBe = Constant.VOCA_KNOW.VOCA_KNOW_KNOWN;
                if (VocaKnow.isKnown(rubyTextModel.getVocaKnow())) {
                    vocaKnowToBe = Constant.VOCA_KNOW.VOCA_KNOW_UNKNOWN;
                }
                vocaKnowActivity.checkLocalAndChangeVocaKnow(hanja, vocaKnowToBe);
            }
        };
    }


    protected void showHanjaSearchResultView() {
        getRvSearch().setVisibility(View.VISIBLE);
    }

    protected void hideHanjaSearchResultView() {
        getRvSearch().setVisibility(View.GONE);
    }

//    protected boolean isShowingHanjaSearchResultView() {
//        return getRvSearch().getVisibility() == View.VISIBLE;
//    }

    @Subscribe
    public void onEvent(SuccessEvent successEvent) {
        switch (successEvent.getScreen()) {
            case COMMON_VOCA_DATA:
                IVocaBasicItem iVocaBasicItem= (IVocaBasicItem) successEvent.getModel();
                switch (successEvent.getEventType()) {
                    case COMMON_VOCA_KNOW_CHANGED:
                        refreshSearchViewAdapterVocaKnow(iVocaBasicItem,iVocaBasicItem.getVIVocaKnow());
                        break;
                    case COMMON_VOCA_KNOWPRONOUNCE_CHANGED:
                        refreshAdapterVocaKnowPronounce(iVocaBasicItem,iVocaBasicItem.getVIVocaKnowPronounce());
                        break;
                }
                break;
        }
    }

    private void refreshSearchViewAdapterVocaKnow(IVocaBasicItem voca, int vocaKnow) {
        if ((hanjaListInSearchView == null) || (hanjaListInSearchView.size() == 0))
            return;

        for (HanjaItem hanjaItem : hanjaListInSearchView) {
            if (hanjaItem.getHI_ID().intValue() == voca.getVIVocaId()) {
                hanjaItem.setHI_VOCA_KNOW((long) vocaKnow);
                long vocaKnowPronounce = VocaKnow.getVocaKnowPronounceByVocaKnow(vocaKnow);
                hanjaItem.setHI_VOCA_KNOWPRONOUNCE(vocaKnowPronounce);
                break;
            }
        }
        searchHanjaAdapter.setHanjaList(hanjaListInSearchView);
        searchHanjaAdapter.notifyDataSetChanged();
    }

    private void refreshAdapterVocaKnowPronounce(IVocaBasicItem voca, int vocaKnowPronounce) {
        if ((hanjaListInSearchView == null) || (hanjaListInSearchView.size() == 0))
            return;

        for (HanjaItem hanjaItem : hanjaListInSearchView) {
            if (hanjaItem.getHI_ID().intValue() == voca.getVIVocaId()) {
                hanjaItem.setHI_VOCA_KNOWPRONOUNCE((long) vocaKnowPronounce);
                break;
            }
        }
        searchHanjaAdapter.setHanjaList(hanjaListInSearchView);
        searchHanjaAdapter.notifyDataSetChanged();
    }

    private void openHanjaExcludeOptionDialog() {
        hanjaExcludeOptionDialog.show();
    }
    protected void openNormalTextViewForHuriganaScreen() {

    }
    protected void openHanjaQuizWritingScreen() {
//        Intent intent = new Intent(context, HanjaQuizWritingActivity.class);
//        intent.putExtra(Constant.BUNDLE.KEY_QUIZ_HANJA_LIST, (Serializable) getQuizHanjaList());
//        openNewScreen(intent);
    }

    protected void openWebDictionaryScreen() {
        ToastUtil.getInstance(context).show(R.string.msg_under_development);
    }

    protected void openWebSearchScreen() {
        ToastUtil.getInstance(context).show(R.string.msg_under_development);
    }

    //TODO : These case is for AraHanja Only
//    protected OnDoubleClickListener onItemDoubleClickListener = new OnDoubleClickListener() {
//        @Override
//        public void onClick(View view, Object object) {
//            if (object instanceof HanjaItem) {
//                switch (view.getId()) {
//                    case R.id.iv_word_list:
//                    case R.id.ivWordList:
//                        openHanjaWordListInfoView((HanjaItem) object);
//                        break;
//                    case R.id.ivEditView:
//                        openEditHanjaMeaningScreen();
//                        break;
//                    default:
//                        openHanjaInfoView(object);
//                        break;
//                }
//
//            } else if (object instanceof String) {
//                String text = (String) object;
//                switch (view.getId()) {
//                    case R.id.tvVoca:
//                        Utils.copyToClipboard(BaseHanjaInfoActivity.this, text, R.string.copied);
//                        break;
//                    case R.id.ivWordList:
//                        openHanjaWordListInfoView(text);
//                        break;
//                    default:
//                        final DIC_HANJA[] hanja = new DIC_HANJA[1];
//                        BaseVoca.executeRealmTransaction(realm -> {
//                            DIC_HANJA hanja1 = realm.where(DIC_HANJA.class).equalTo(Constant.REALMDB.KEY_VOCA, text).findFirst();
//                            if (hanja1 != null) {
//                                hanja[0] = realm.copyFromRealm(hanja1);
//                            }
//                        });
//                        if (hanja[0] != null) {
//                            openHanjaInfoView(hanja[0]);
//                        }
//                }
//            }
//        }
//
//        @Override
//        public void onDoubleClick(View view, Object object) {
//            switchVocaKnow(object);
//        }
//    };

//    @Override
    protected void onClickWordListIcon(Object object) {
        openHanjaWordListInfoView((HanjaItem) object);
    }
//    @Override
    protected void onClickEditIcon() {
        openEditHanjaMeaningScreen();
    }
//    @Override
    protected void onClickDefaultOnItemDoubleClickListener(Object object) {
        openHanjaInfoView(object);
    }
//    @Override
    protected void onClickWordListText(String text) {
        openHanjaWordListInfoView(text);
    }
//    @Override
    protected void onClickDefaultTextOnItemDoubleClickListener(String text) {
        final DIC_HANJA[] hanja = new DIC_HANJA[1];
        BaseVoca.executeRealmTransaction(realm -> {
            DIC_HANJA hanja1 = realm.where(DIC_HANJA.class).equalTo(Constant.REALMDB.KEY_VOCA, text).findFirst();
            if (hanja1 != null) {
                hanja[0] = realm.copyFromRealm(hanja1);
            }
        });
        if (hanja[0] != null) {
            openHanjaInfoView(hanja[0]);
        }
    }

//    @Override
//    protected boolean canSwitchVocaKnowObject(Object object) {
//        boolean result = false;
//        if (UserUtil.isLoggedIn(this, true)) {
//            if ((object instanceof DIC_HANJA) || (object instanceof DIC_HANJA_SENTENCE)) {
//                result = true;
//            }
//        }
//        return result;
//    }
//    @Override
//    protected void switchVocaKnow(Object object) {
//        super.switchVocaKnow(object);
//        if (canSwitchVocaKnowObject(object)) {
//            blnScrollToFirstInHanjaListView = false;
//        }
//    }

    protected final DialogInterface.OnClickListener onDialogItemClickListener = (dialog, which) -> {
        switch (which) {
            case R.id.llRefreshVocaFromServer:
                vocaKnowActivity.refreshVocaFromServer();
                break;
            case R.id.llWritingPractice:
                openHanjaQuizWritingScreen();
                break;
            case R.id.llOpenNormalTextViewForHurigana:
                openNormalTextViewForHuriganaScreen();
                break;
            case R.id.tvBackToHome:
                backToHome();
                break;
            case R.id.llOpenHanjaExcludeOptionDialog:
                openHanjaExcludeOptionDialog();
                break;
            default:
                break;
        }
    };

    protected final DialogInterface.OnClickListener onHanjaExcludeOptionDialogItemClickListener = (dialog, which) -> {
        switch (which) {
            case R.id.tv_ok:
                showSimplifiedChineseBeyondLevelHanja();
                break;
        }
    };

    protected void showSimplifiedChineseBeyondLevelHanja() {
        ToastUtil.getInstance(this).show(R.string.msg_indexing_list);
    }

    protected void openEditHanjaMeaningScreen() {

    }

    protected void openHanjaWordListInfoView(String content) {
        openNewScreen(
                HanjaWordListInfoActivity.createIntentByContent(this, content, true)
        );
    }

    protected void openHanjaWordListInfoView(HanjaItem hanjaItem) {
        openNewScreen(
                HanjaWordListInfoActivity.createIntentByContent(this, hanjaItem.getHI_ALL_TEXT(), true)
        );
    }

    protected void openHanjaWordListInfoView(List<HanjaItem> hanjaItemList) {
        openNewScreen(
                HanjaWordListInfoActivity.createIntentVocaTypeId(this, HanjaVoca.getVocaTypeIdFromHanjaItemList(hanjaItemList), true)
        );
    }

    protected void scrollToTopPositionInSearchRecyclerView() {
        //TODO : want to set hanjaItemToHighlight to center of view.
        getRvSearch().getLayoutManager().scrollToPosition(0);
    }

    protected void addMobileAdsView() {
        if (!canShowAds) return;
        adContainerView = findViewById(R.id.adViewContainer); // Todo : how to use binding instead of findViewById?
        // Admob Step 1 - Create an AdView and set the ad unit ID on it.
        mAdView = new AdView(this);
        if (DarkThemeUtil.isDarkMode(this)) {
            mAdView.setForeground(new ColorDrawable(ContextCompat.getColor(this, R.color.ads_layer_color_in_dark_mode)));
        }
        mAdView.setAdUnitId(MobileAd.getAdsBannerId(this));
        adContainerView.addView(mAdView);
    }

    protected void loadBanner() {
        if (!canShowAds) return;
        AdRequest adRequest = new AdRequest.Builder().build();
        AdSize adSize = BaseMobileAd.getAdSize(this);
        // Admob Step 4 - Set the adaptive ad size on the ad view.
        mAdView.setAdSize(adSize);

        // Admob Step 5 - Start loading the ad in the background.
        mAdView.loadAd(adRequest);

        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
                DLog.d(getLogTag(), "onAdLoaded");
                application.getEventBus().post(new SuccessEvent(BaseEvent.Screen.MAIN, BaseEvent.EventType.ON_ADS_LOADED, true));
            }

            @Override
            public void onAdFailedToLoad(LoadAdError adError) {
                // Code to be executed when an ad request fails.
                DLog.d(getLogTag(), "onAdFailedToLoad");
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
                sharedPreferences.setLastAdsClickedTime(System.currentTimeMillis());
                if (adContainerView != null) {
                    adContainerView.removeView(mAdView);
                }
                DLog.d(getLogTag(), "onAdOpened");
            }

            @Override
            public void onAdClicked() {
                // Code to be executed when the user clicks on an ad.
                DLog.d(getLogTag(), "onAdClicked");
            }

            @Override
            public void onAdClosed() {
                // Code to be executed when the user is about to return
                // to the app after tapping on an ad.
                DLog.d(getLogTag(), "onAdClosed");
            }
        });
    }

    protected void openHanjaInfoView(Object object) {
        if (object == null)
            return;

        if (object instanceof DIC_HANJA) {
            openNewScreen(
                    HanjaWordInfoActivity.createIntent(BaseHanjaInfoActivity.this, (DIC_HANJA) object)
            );
        } else if (object instanceof DIC_HANJA_SENTENCE) {
            openNewScreen(
                    HanjaSentenceInfoActivity.createIntent(BaseHanjaInfoActivity.this, (DIC_HANJA_SENTENCE) object)
            );
        } else if (object instanceof DIC_HANJA_BOOK) {
            ToastUtil.getInstance(this).show(R.string.msg_under_development);
        }
    }

    private DialogAddSentence.OnAddSentenceListener addSentenceListener = new DialogAddSentence.OnAddSentenceListener() {
        @Override
        public void onSelect(String sentence, String pronounce) {
            int uid = getUserID();
            if (uid > 0) {
                if (Utils.isConnected(BaseHanjaInfoActivity.this)) {
                    Loading.show(BaseHanjaInfoActivity.this);
                    ((AraHanjaApplication)application).getAraHanjaApiImpl().isExistHanjaByVocaAndType(sentence, Constant.API_VALUE.VALUE_VOCA_TYPE_SENTENCE, new DalApiListener<Boolean>() {
                        @Override
                        public void onSuccess(Boolean response) {
                            if ((response != null) && (response == true)) {
                                ToastUtil.getInstance(BaseHanjaInfoActivity.this).show(R.string.msg_already_exist_in_server);
                            } else {
                                DIC_HANJA_SENTENCE dicHanjaSentence = new DIC_HANJA_SENTENCE(sentence);
                                dicHanjaSentence.setPRONOUNCE(pronounce);
                                openNewScreen(
                                        EditHanjaSentenceMeaningActivity.createIntent(BaseHanjaInfoActivity.this, dicHanjaSentence)
                                );
                            }
                            Loading.hide();
                        }

                        @Override
                        public void onFailure(String error) {
                            ToastUtil.getInstance(BaseHanjaInfoActivity.this).show(R.string.msg_error_while_check_in_server);
                            Loading.hide();
                        }
                    });
                } else {
                    alertDialog.showNoInternet();
                }
            } else {
                alertDialog.showLogInRequired();
            }
        }


        @Override
        public void onDismiss(View v) {

        }
    };

    protected void openEditViewIfVocaIsNotInServerDB() {
        final DialogAddSentence dialog = new DialogAddSentence(this, keyword, addSentenceListener);

//        new DialogAddSentence(this,
//                R.string.info,
//                R.string.msg_write_new_hanja_sentence,
//                R.string.placeholder_write_new_hanja_sentence,
//                "",
//                addSentenceListener
//                );
        dialog.show();
    }

    protected FuriganaView.OnTextSelectedListener onRubyWordClickListener = new FuriganaView.OnTextSelectedListener() {
        @Override
        public void onTextSelected(String text, RubyTextModel rubyTextModel) {
            openHanjaWordInfoView(rubyTextModel);
        }

        @Override
        public void onDoubleClick(String text, RubyTextModel rubyTextModel) {
            if (UserUtil.isLoggedIn(BaseHanjaInfoActivity.this, true)) {
                DIC_HANJA hanja = new DIC_HANJA();
                hanja.setID((long) rubyTextModel.getVocaId());

                int vocaKnowToBe = Constant.VOCA_KNOW.VOCA_KNOW_KNOWN;
                if (rubyTextModel.getVocaKnow() >= Constant.VOCA_KNOW.VOCA_KNOW_KNOWN) {
                    vocaKnowToBe = Constant.VOCA_KNOW.VOCA_KNOW_UNKNOWN;
                }
                vocaKnowActivity.checkAndChangeVocaKnow(hanja, vocaKnowToBe);
            }
        }
    };

    private void openHanjaWordInfoView(RubyTextModel rubyTextModel) {
        List<DIC_HANJA> hanjaList = new ArrayList<>();
        BaseVoca.executeRealmTransaction(realm -> {
            DIC_HANJA dicHanja = Voca.searchHanjaWordByVocaID(realm, rubyTextModel.getVocaId());
            hanjaList.add(realm.copyFromRealm(dicHanja));
        });
        if (hanjaList.isEmpty()) {
            ToastUtil.getInstance(this).show("There is no dictionary for " + rubyTextModel.getVoca());
        } else {
            DIC_HANJA dicHanja = hanjaList.get(0);
            openNewScreen(
                    HanjaWordInfoActivity.createIntent(this, dicHanja)
            );
        }
    }

//    @Override
//    protected void onVocaKnowChanged(int vocaID, int vocaKnow) {
//        long vocaKnowPronounce = BaseVocaKnow.getVocaKnowPronounceByVocaKnow(vocaKnow);
//        Voca.executeRealmTransaction(realm -> {
//            DIC_HANJA hanja1 = realm.where(DIC_HANJA.class)
//                    .equalTo(Constant.REALMDB.KEY_ID, vocaID)
//                    .findFirst();
//            if (hanja1 != null) {
//                hanja1.setVOCA_KNOW((long) vocaKnow);
//                hanja1.setVOCA_KNOWPRONOUNCE(vocaKnowPronounce);
//            }
//        });
//    }
//    @Override
//    protected void updateVocaKnowInDB(IVocaBasicItem voca, int newVocaKnow) {
//        Voca.updateVocaKnow((HanjaItem)voca, newVocaKnow, BaseVocaKnow.getVocaKnowPronounceByVocaKnow(newVocaKnow));
//    }
//    @Override
//    protected void updateVocaKnowPronounceInDB(IVocaBasicItem voca, int newVocaKnowPronounce) {
//        Voca.updateVocaKnow((HanjaItem)voca, voca.getVIVocaKnow(), newVocaKnowPronounce);
//    }

    protected void backToHome() {
        backToHome(MainHomeActivity.class);
    }

    @Override
    public void onBackPressed() {
        if ((getToolbar() != null) && (getToolbar().isSearchStarted())) {
            getToolbar().closeSearchView();
        } else {
            super.onBackPressed();
        }

    }
}
