package com.dalread.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dalread.R;
import com.dalread.adapter.VocaHanjaSentenceInfoAdapter;
import com.dalread.asyntask.OnAsyncTaskListener;
import com.dalread.component.GridSeparatorItemDecoration;
import com.dalread.component.Toolbar;
import com.dalread.databinding.ActivityHanjaSetenceBinding;
import com.dalread.model.DIC_HANJA_SENTENCE;
import com.dalread.model.HanjaItem;
import com.dalread.network.events.SuccessEvent;
import com.dalread.util.BaseBindUtils;
import com.dalread.util.Constant;
import com.dalread.util.DLog;
import com.dalread.util.HanjaVoca;
import com.dalread.util.Loading;
import com.dalread.util.Utils;
import com.dalread.util.Voca;
import com.dalread.util.VocaKnow;

import org.greenrobot.eventbus.Subscribe;

import java.util.List;

import butterknife.BindDimen;

@SuppressLint("NonConstantResourceId")
public class HanjaSentenceInfoActivity extends BaseHanjaInfoActivity implements OnAsyncTaskListener, View.OnClickListener  {
//    @BindDimen(R.dimen.study_hanja_small_box_width)
//    int smallBoxWidth;
//    @BindDimen(R.dimen.study_hanja_small_box_margin)
//    int smallBoxMargin;

    protected final int TYPE_REFRESH_VOCA_AND_OPEN_EDIT_VIEW = 0;
    private DIC_HANJA_SENTENCE hanjaSentence;
    private VocaHanjaSentenceInfoAdapter adapter;
    private int heightRoot = 0;

    private ActivityHanjaSetenceBinding binding;
    private boolean isOpenEditView_;

    public static Intent createIntent(Context context, DIC_HANJA_SENTENCE hanja) {
        Intent intent = new Intent(context, HanjaSentenceInfoActivity.class);
        intent.putExtra(Constant.BUNDLE.KEY_VOCA_TYPE, hanja.getHI_VOCA_TYPE());
        intent.putExtra(Constant.BUNDLE.KEY_VOCA_ID, hanja.getID());
        return intent;
    }

    protected View getContentView() {
        binding = ActivityHanjaSetenceBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        return view;
    }

    @Override
    protected Toolbar getToolbar() {
        return binding.header;
    }

    @Override
    protected RecyclerView getRvSearch() {
        return binding.rvSearch;
    }


//    @Override
//    protected RecyclerView.Adapter<RecyclerView.ViewHolder> getAdapter() {
//        return adapter;
//    }

    @Override
    protected void getData() {
        adapter = new VocaHanjaSentenceInfoAdapter(this, vocaKnowActivity.onDoubleClickListenerOnBaseVocaKnow, onDoubleClickListenerForCommon);
        _initLayout();
        hanjaSentence = Voca.searchHanjaSentenceByID((long) getIntent().getLongExtra(Constant.BUNDLE.KEY_VOCA_ID, 0));
        addMobileAdsView();
        loadBanner();
        bindData();
        setOnClickListeners();
    }

    @Override
    protected void initDialog() {
        super.initDialog();
        hanjaCommonMenuDialog.showRefreshVocaFromServerMenu(true);
    }

    private void _initLayout() {
        int numberToReduceColumnCount = 1;
        int smallBoxWidth = BaseBindUtils.getStudyHanjaSmallBoxWidth(this);
        int noOfColumns = Voca.calculateNoOfColumns(this, (int) (smallBoxWidth * sharedPreferences.getAraHanjaFontSizeRatio(this)), 10) - numberToReduceColumnCount;

        GridLayoutManager layoutManager = new GridLayoutManager(context, noOfColumns);
//        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
//            @Override
//            public int getSpanSize(int position) {
//                return noOfColumns;
//            }
//        });
        binding.rvInfo.setLayoutManager(layoutManager);
        binding.rvInfo.addItemDecoration(new GridSeparatorItemDecoration(getDrawable(R.drawable.grid_item_divider), noOfColumns));
        binding.rvInfo.setAdapter(adapter);

        if (getToolbar() != null) {
            getToolbar().setTitle(R.string.info);
        }

        binding.llRoot.post(() -> heightRoot = binding.llRoot.getHeight());
        binding.layoutItemHanjaSentenceInfoIconsHeader.vForeground.setOnTouchListener(separatorBarListener);
    }

    private View.OnTouchListener separatorBarListener = new View.OnTouchListener() {
        /**
         * Max allowed distance to move during a "click", in DP.
         */
        private static final int MAX_CLICK_DISTANCE = 15;
        private float pressedX;
        private float pressedY;
        /**
         * Max allowed duration for a "click", in milliseconds.
         */
        private static final int MAX_CLICK_DURATION = 200;
        private long pressStartTime;
        private boolean stayedWithinClickDistance;

        private float distance(float x1, float y1, float x2, float y2) {
            float dx = x1 - x2;
            float dy = y1 - y2;
            float distanceInPx = (float) Math.sqrt(dx * dx + dy * dy);
            return pxToDp(distanceInPx);
        }

        private float pxToDp(float px) {
            return px / getResources().getDisplayMetrics().density;
        }

        @Override
        public boolean onTouch(View view, MotionEvent event) {
            final int height = heightRoot / 8;
//            View vAbove = view.getId() == R.id.v_adjust_height_reading ? vReadingTop : rvContent;
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    pressStartTime = System.currentTimeMillis();
                    pressedX = event.getX();
                    pressedY = event.getY();
                    stayedWithinClickDistance = true;
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (stayedWithinClickDistance && distance(pressedX, pressedY, event.getX(), event.getY()) > MAX_CLICK_DISTANCE) {
                        stayedWithinClickDistance = false;
                    }
//                    if (vAbove != null && vAbove.getVisibility() != View.GONE) {
                    final float currentY = event.getY() - pressedY;
                    DLog.d(getLogTag(), "pressedY=" + pressedY + "currentY=" + currentY);
                    int currentHeight = (int) (binding.layoutItemHanjaSentenceInfoGeneral.getRoot().getHeight() + currentY);
                    if (currentHeight <= height) {
                        currentHeight = height;
                    } else if (currentHeight >= (heightRoot - height)) {
                        currentHeight = heightRoot - height;
                    }
                    binding.layoutItemHanjaSentenceInfoGeneral.getRoot().setLayoutParams(new LinearLayout.LayoutParams(binding.layoutItemHanjaSentenceInfoGeneral.getRoot().getWidth(), currentHeight));
                    DLog.d(getLogTag(), "height move=" + binding.layoutItemHanjaSentenceInfoGeneral.getRoot().getHeight() + " - currentHeight=" + currentHeight);
//                    }
                    break;
                case MotionEvent.ACTION_UP:
//                    long pressDuration = System.currentTimeMillis() - pressStartTime;
//                    if (pressDuration < MAX_CLICK_DURATION && stayedWithinClickDistance) {
////                        onClickSeparatorBar(view);
//                    }
                    break;
                default:
                    return false;
            }

            return true;
        }
    };

    @Subscribe
    public void onEvent(SuccessEvent successEvent) {
        switch (successEvent.getScreen()) {
            case EDIT_HANJA_SENTENCE:
                switch (successEvent.getEventType()) {
                    case DATA_CHANGED:
                        reloadData();
                }
                break;
            case HANJA_VOCA:
                DIC_HANJA_SENTENCE dicHanjaSentence = (DIC_HANJA_SENTENCE) successEvent.getModel();
                switch (successEvent.getEventType()) {
                    case UPDATE_LOCAL_HANJA_SENTENCE_MODEL_WITH_SERVER_MODEL_AND_OPEN_EDIT_SCREEN:
                        if (isOpenEditView_) {
                            openEditHanjaMeaningScreen(dicHanjaSentence);
                            isOpenEditView_ = false;
                        }
                        reloadData();
                        break;
                    case OPEN_EDIT_HANJA_SENTENCE_SCREEN:
                        if ((isOpenEditView_) && (Voca.isSameVoca((HanjaItem)dicHanjaSentence, hanjaSentence))) {
                            openEditHanjaMeaningScreen(hanjaSentence);
                        }
                        break;
                }
                break;
            case MAIN:
                switch (successEvent.getEventType()) {
                    case BOOKMARK_CHANGED:
                    case VOCA_KNOW_CHANGED:
                    case MULTIPLE_VOCA_KNOW_CHANGED:
                        reloadData();
                }
                break;
        }
    }

    protected void refreshVocaFromServer() {
        isOpenEditView_ = false;
        HanjaVoca.refreshHanjaSentenceAndOpenEditView(context, hanjaSentence, isOpenEditView_);
    }

    private void reloadData() {
        hanjaSentence = Voca.searchHanjaSentenceByID(hanjaSentence.getID());
        if (hanjaSentence == null)
            return;

        bindData();
    }

//    @Override
//    protected List<HanjaQuizItem> getQuizHanjaList() {
//        return new ArrayList<>();
//    }

//    @Override
//    protected void updateVocaKnowInDB(IVocaBasicItem voca, int newVocaKnow) {
//        super.updateVocaKnowInDB(voca, newVocaKnow);
//        adapter.notifyItemChanged(0);
//    }
//
//    @Override
//    protected void updateVocaKnowPronounceInDB(IVocaBasicItem voca, int newVocaKnowPronounce) {
//        super.updateVocaKnowPronounceInDB(voca, newVocaKnowPronounce);
//        adapter.notifyItemChanged(0);
//    }

    private void bindData() {
        adapter.setData(hanjaSentence);
        adapter.notifyDataSetChanged();
        updateUI();
    }

    private void updateUI() {

        VocaKnow.updateIconVocaKnow(context, binding.layoutItemHanjaSentenceInfoGeneral.ivKnow, hanjaSentence.getVOCA_KNOW().intValue());
        updateBookmarkIcon(hanjaSentence);
        VocaKnow.updateVocaColor(context, binding.layoutItemHanjaSentenceInfoGeneral.tvVoca, hanjaSentence.getVOCA_KNOW().intValue() );
//        if (hanjaSentence.getVOCA_KNOW() == Constant.VOCA_KNOW.VOCA_KNOW_KNOWN) {
//            if (hanjaSentence.getVOCA_KNOWPRONOUNCE() == Constant.VOCA_KNOW.VOCA_KNOW_KNOWN) {
//                binding.layoutItemHanjaSentenceInfoGeneral.tvVoca.setTextColor(ContextCompat.getColor(context, R.color.color_ruby_text_normal));
//            } else {
//                binding.layoutItemHanjaSentenceInfoGeneral.tvVoca.setTextColor(ContextCompat.getColor(context, R.color.color_ruby_text_unknown_pronounce));
//            }
//        } else {
//            binding.layoutItemHanjaSentenceInfoGeneral.tvVoca.setTextColor(ContextCompat.getColor(context, R.color.color_ruby_text_unknown_meaning));
//        }
        String voca = hanjaSentence.getVOCA();
        binding.layoutItemHanjaSentenceInfoGeneral.tvVoca.setText(voca);

        String pronounce = hanjaSentence.getPRONOUNCE();
        if (!Utils.isEmpty(pronounce))
            binding.layoutItemHanjaSentenceInfoGeneral.tvPronounce.setText(String.format(" [%s]", pronounce));

        showJapaneseHanja(hanjaSentence);
        showChineseHanja(hanjaSentence);
        showMeaning(hanjaSentence);
        showMeaningEng(hanjaSentence);
    }

    private void updateBookmarkIcon(HanjaItem hanja) {
        binding.layoutItemHanjaSentenceInfoGeneral.ivBookmark.setImageResource(hanja.getHI_BOOKMARK() != null && hanja.getHI_BOOKMARK() == 1
                ? R.drawable.ic_favorite_new_on
                : R.drawable.ic_favorite_new_off);
    }

    private void showJapaneseHanja(DIC_HANJA_SENTENCE hanjaSentence) {
        showJapaneseChineseHanjaCommon(hanjaSentence.getVOCA_JP(), hanjaSentence.getPRONOUNCE_JP(), binding.layoutItemHanjaSentenceInfoGeneral.llJapaneseHanja, binding.layoutItemHanjaSentenceInfoGeneral.tvJapaneseHanja);
    }

    private void showChineseHanja(DIC_HANJA_SENTENCE hanjaSentence) {
        showJapaneseChineseHanjaCommon(hanjaSentence.getVOCA_CH_S(), hanjaSentence.getPRONOUNCE_CH_S(), binding.layoutItemHanjaSentenceInfoGeneral.llChineseHanja, binding.layoutItemHanjaSentenceInfoGeneral.tvChineseHanja);
    }

    private void showJapaneseChineseHanjaCommon(String hanja, String pronounce, LinearLayout linearLayout, TextView textView) {
        String result = "";
        if (!TextUtils.isEmpty(hanja) && !TextUtils.isEmpty(pronounce)) {
            result = hanja + " [" + pronounce + "]";
        } else if (!TextUtils.isEmpty(hanja)) {
            result = hanja;
        } else if (!TextUtils.isEmpty(pronounce)) {
            result = pronounce;
        }
        if (result.trim().equals("")) {
            linearLayout.setVisibility(View.GONE);
        } else {
            textView.setText(result);
            linearLayout.setVisibility(View.VISIBLE);
        }
    }



    private void showMeaning(DIC_HANJA_SENTENCE hanjaSentence) {
        String meaning = hanjaSentence.getMEANING_KO();
        String meaningDetailed = hanjaSentence.getMEANING_KO_DETAILED();
        String result = "";
        if (!TextUtils.isEmpty(meaning) && !TextUtils.isEmpty(meaningDetailed)) {
            result = meaning + "\n\n" + meaningDetailed;
        } else if (!TextUtils.isEmpty(meaning)) {
            result = meaning;
        } else if (!TextUtils.isEmpty(meaning)) {
            result = hanjaSentence.getMEANING_ENG();
        }
        if (result.trim().equals("")) {
            binding.layoutItemHanjaSentenceInfoGeneral.tvMeaningFurigana.setVisibility(View.GONE);
        } else {
            List<String> listRubyTextAndUnknownWord = Voca.getRubyTextAndUnknownWord(result, "");
            String rubyText = listRubyTextAndUnknownWord.get(0);
            binding.layoutItemHanjaSentenceInfoGeneral.tvMeaningFurigana.setOnTextSelectedListener(onRubyWordClickListener);

            binding.layoutItemHanjaSentenceInfoGeneral.tvMeaningFurigana.setFuriganViewForMeaning(rubyText);
//            binding.layoutItemHanjaSentenceInfoGeneral.tvMeaningFurigana.setTutor(true);
//            binding.layoutItemHanjaSentenceInfoGeneral.tvMeaningFurigana.resetText();
//            binding.layoutItemHanjaSentenceInfoGeneral.tvMeaningFurigana.setIsKnownPronounceMeaning(true);
//            binding.layoutItemHanjaSentenceInfoGeneral.tvMeaningFurigana.setIsShowFurigana(Constant.SHOW_FURIGANA_OFF);
//            binding.layoutItemHanjaSentenceInfoGeneral.tvMeaningFurigana.setJText(rubyText);
            binding.layoutItemHanjaSentenceInfoGeneral.tvMeaningFurigana.setVisibility(View.VISIBLE);
        }
    }

    private void showMeaningEng(DIC_HANJA_SENTENCE hanjaSentence) {
        String meaningEng = hanjaSentence.getMEANING_ENG();
        String meaningEngDetailed = hanjaSentence.getMEANING_ENG_DETAILED();
        String result = "";
        if (!TextUtils.isEmpty(meaningEng) && !TextUtils.isEmpty(meaningEngDetailed)) {
            result = meaningEng + "\n\n" + meaningEngDetailed;
        } else if (!TextUtils.isEmpty(meaningEng)) {
            result = meaningEng;
        } else if (!TextUtils.isEmpty(meaningEng)) {
            result = hanjaSentence.getMEANING_ENG();
        }
        if (!isMeaningOrDetailedHaveValue(hanjaSentence)) {
            result = "\n\n" + result;
        }
        if (result.trim().equals("")) {
            binding.layoutItemHanjaSentenceInfoGeneral.tvMeaningEng.setVisibility(View.GONE);
        } else {
            binding.layoutItemHanjaSentenceInfoGeneral.tvMeaningEng.setText(result);
            binding.layoutItemHanjaSentenceInfoGeneral.tvMeaningEng.setVisibility(View.VISIBLE);
        }
    }

    private void setOnClickListeners() {
        binding.layoutItemHanjaSentenceInfoGeneral.ivBookmark.setOnClickListener(this);
        binding.layoutItemHanjaSentenceInfoGeneral.ivKnow.setOnClickListener(this);
        binding.layoutItemHanjaSentenceInfoGeneral.tvVoca.setOnClickListener(this);
        binding.layoutItemHanjaSentenceInfoIconsHeader.ivWordList.setOnClickListener(this);
        binding.layoutItemHanjaSentenceInfoIconsHeader.ivEditView.setOnClickListener(this);
        binding.layoutItemHanjaSentenceInfoIconsHeader.ivCopy.setOnClickListener(this);
        binding.layoutItemHanjaSentenceInfoIconsHeader.ivWebSearch.setOnClickListener(this);
        binding.layoutItemHanjaSentenceInfoIconsHeader.ivWebDictionary.setOnClickListener(this);
        binding.layoutItemHanjaSentenceInfoIconsHeader.ivWebDictionaryChS.setOnClickListener(this);
        binding.layoutItemHanjaSentenceInfoIconsHeader.ivWebDictionaryJp.setOnClickListener(this);
        binding.layoutItemHanjaSentenceInfoIconsHeader.ivWebDictionaryEn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ivBookmark:
            case R.id.ivKnow:
                if (vocaKnowActivity.onDoubleClickListenerOnBaseVocaKnow != null) {
                    vocaKnowActivity.onDoubleClickListenerOnBaseVocaKnow.onClick(view, hanjaSentence);
                }
                break;
            case R.id.ivWordList:
            case R.id.ivEditView:
            case R.id.ivCopy:
                if (onDoubleClickListenerForCommon != null) {
                    onDoubleClickListenerForCommon.onClick(view, hanjaSentence);
                }
                break;
            case R.id.tvVoca:
                Utils.copyToClipboard(context, hanjaSentence.getVOCA(), R.string.copied);
//                if (onItemDoubleClickListener != null) {
//                    onItemDoubleClickListener.onClick(view, hanjaSentence.getVOCA());
//                }
                break;
            case R.id.ivWebSearch:
                if ((hanjaSentence != null) || (hanjaSentence.getVOCA() != null))
                    Utils.openWebSearchForHanja(context, hanjaSentence.getVOCA());
                break;
            case R.id.ivWebDictionary:
                if ((hanjaSentence != null) || (hanjaSentence.getVOCA() != null))
                    Utils.openWebDictionaryForHanja(context, hanjaSentence.getVOCA());
                break;
            case R.id.ivWebDictionaryChS:
                if ((hanjaSentence != null) || (hanjaSentence.getVOCA() != null))
                    Utils.openWebDictionaryForHanja_ch_s(context, hanjaSentence.getVOCA());
                break;
            case R.id.ivWebDictionaryJp:
                if ((hanjaSentence != null) || (hanjaSentence.getVOCA() != null))
                    Utils.openWebDictionaryForHanja_jp(context, hanjaSentence.getVOCA());
                break;
            case R.id.ivWebDictionaryEn:
                if ((hanjaSentence != null) || (Utils.isEmpty(hanjaSentence.getPRONOUNCE())))
                    Utils.openWebDictionaryForHanja_en(context, hanjaSentence.getPRONOUNCE());
                break;
        }
    }

    private boolean isMeaningOrDetailedHaveValue(DIC_HANJA_SENTENCE hanjaSentence) {
        String meaning = hanjaSentence.getMEANING_KO();
        String meaningDetailed = hanjaSentence.getMEANING_KO_DETAILED();
        String result = "";
        if (!TextUtils.isEmpty(meaning) && !TextUtils.isEmpty(meaningDetailed)) {
            result = meaning + "\n\n" + meaningDetailed;
        } else if (!TextUtils.isEmpty(meaning)) {
            result = meaning;
        }
        return result.trim().equals("");
    }

//    private boolean isMeaningOrDetailedHaveValue() {
//        return binding.layoutItemHanjaSentenceInfoGeneral.tvMeaning.getText().toString().trim().equals("");
//    }
    protected void showHanjaSearchResultView() {
        super.showHanjaSearchResultView();
        binding.llMain.setVisibility(View.GONE);
        binding.adViewContainer.setVisibility(View.GONE);
    }

    protected void hideHanjaSearchResultView() {
        super.hideHanjaSearchResultView();
        binding.llMain.setVisibility(View.VISIBLE);
        binding.adViewContainer.setVisibility(View.VISIBLE);
    }

//    private void callAsyncTask(int type, DIC_HANJA_SENTENCE dicHanjaSentence) {
//        new CustomAsyncTask(this, this, dicHanjaSentence, type, true).execute();
//    }

    protected void openEditHanjaMeaningScreen() {
        if (Utils.isConnected(this)) {
            if (Utils.isConnected(context)) {
                isOpenEditView_ = true;
                HanjaVoca.refreshHanjaSentenceAndOpenEditView(this, hanjaSentence, isOpenEditView_);
            } else {
                openEditHanjaMeaningScreen(hanjaSentence);
            }
        } else {
            openEditHanjaMeaningScreen(hanjaSentence);
        }
    }

    private void openEditHanjaMeaningScreen(DIC_HANJA_SENTENCE dicHanjaSentence) {
        openNewScreen(
                EditHanjaSentenceMeaningActivity.createIntent(this, dicHanjaSentence)
        );
    }

    @Override
    protected void openWebDictionaryScreen() {
        Utils.openWebDictionaryForHanja(this, hanjaSentence.getVOCA());
//        Utils.openWeb(this, Constant.URL_WEB_DIC_HANJA_KOREAN + hanjaSentence.getVOCA());
    }

    @Override
    protected void openWebSearchScreen() {
        Utils.openWebSearchForHanja(this, hanjaSentence.getVOCA());
//        Utils.openWeb(this, Constant.URL_WEB_SEARCH_GOOGLE_HANJA_KOREAN + hanjaSentence.getVOCA() + "+뜻");
    }

    @Override
    public void onInitAsyncTask() {
//        Loading.show(this);
    }

    @Override
    public void onPrevExecuteAsyncTask() {
        Loading.show(this);
    }

    @Override
    public Object onPostExecuteAsyncTask(int searchType, Object data) {
        DIC_HANJA_SENTENCE dicHanjaSentence = (DIC_HANJA_SENTENCE) data;
        switch (searchType) {
            case TYPE_REFRESH_VOCA_AND_OPEN_EDIT_VIEW:
                break;

        }
        return null;
    }

    @Override
    public void onFinishAsyncTask(int searchType, Object resultData, Object data) {
        switch (searchType) {
            case TYPE_REFRESH_VOCA_AND_OPEN_EDIT_VIEW:
                break;
        }

        Loading.hide();
    }

    protected void openNormalTextViewForHuriganaScreen() {
        if (hanjaSentence != null) {
            openNewScreen(
                    NormalTextViewForHuriganaActivity.createIntent(this, hanjaSentence)
            );
        }
    }
}
