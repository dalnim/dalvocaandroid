package com.dalread.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.RecyclerView;

import com.dalread.R;
import com.dalread.adapter.HanjaConvertAdapter;
import com.dalread.asyntask.CustomAsyncTask;
import com.dalread.asyntask.OnAsyncTaskListener;
import com.dalread.component.Toolbar;
import com.dalread.databinding.ItemConvertToHanjaBinding;
import com.dalread.model.DIC_HANJA;
import com.dalread.model.DIC_HANJA_SENTENCE;
import com.dalread.model.HanjaItem;
import com.dalread.model.RubyTextModel;
import com.dalread.network.events.SuccessEvent;
import com.dalread.util.BaseBindUtils;
import com.dalread.util.BaseVocaKnow;
import com.dalread.util.Constant;
import com.dalread.util.DLog;
import com.dalread.util.Loading;
import com.dalread.util.StringUtils;
import com.dalread.util.ToastUtil;
import com.dalread.util.Utils;
import com.dalread.util.Voca;
import com.google.android.flexbox.FlexboxLayoutManager;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

public class ConvertToHanjaActivity extends BaseHanjaInfoActivity implements OnAsyncTaskListener {
    private int heightRoot = 0;

    private List<HanjaItem> hanjaItemList;
    private HanjaConvertAdapter hanjaConvertAdapter;

    private final int TYPE_INIT_DATA = 0;
    private final int TYPE_CONVERT_TO_HANJA = TYPE_INIT_DATA + 1;
    private final int TYPE_CONVERT_KOREAN_TO_HANJA = TYPE_CONVERT_TO_HANJA + 1;

    private ItemConvertToHanjaBinding binding;
    private List<String> listRubyTextAndUnknownWord;

    public static Intent createIntent(Context context, String text) {
        Intent intent = new Intent(context, ConvertToHanjaActivity.class);
        intent.putExtra(Constant.BUNDLE.KEY_HANJA_DATA, text);
        return intent;
    }

    @Override
    protected RecyclerView getRvSearch() {
        return binding.rvSearch;
    }

    @Override
    protected void getData() {

    }

    protected View getContentView() {
        binding = ItemConvertToHanjaBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        return view;
    }

    @Override
    protected Toolbar getToolbar() {
        return binding.header;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        initEventBus();
//        setOnClickListeners();
//        initLayout();
        initRecycleView();
        _initData();
        addMobileAdsView();
        loadBanner();
        updateEyeIcon();
    }

    protected void initListener() {
        super.initListener();
        binding.ivParseContentToMakeRubyText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                convertToHanja();
            }
        });
        binding.vAdjustHeightReading.ivWordList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openHanjaWordListInfoView(hanjaItemList);
            }
        });
        binding.vAdjustHeightReading.ivEye.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchShowFurigana();
                updateEyeIcon();
            }
        });
        binding.vAdjustHeightReading.ivWebSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String str = binding.etContent.getText().toString();
                if (!Utils.isEmpty(str)) {
                    Utils.openWebDictionaryForHanja(ConvertToHanjaActivity.this, str);
                }
            }
        });
    }

    private void switchShowFurigana() {
        int isShowFurigana = Constant.SHOW_FURIGANA_DIFFICULT_WORDS_ONLY;
        if (getIsShowFurigana() == Constant.SHOW_FURIGANA_DIFFICULT_WORDS_ONLY) {
            isShowFurigana = Constant.SHOW_FURIGANA_ALL;
        }
        sharedPreferences.setShowHuriganaForHanja(isShowFurigana);
        updateLayout(listRubyTextAndUnknownWord.get(0));
    }
    private void updateEyeIcon() {
        if (getIsShowFurigana() == Constant.SHOW_FURIGANA_DIFFICULT_WORDS_ONLY) {
            binding.vAdjustHeightReading.ivEye.setImageResource(R.drawable.ic_new_eye_close2);
        } else {
            binding.vAdjustHeightReading.ivEye.setImageResource(R.drawable.ic_new_eye_open2);
        }
    }

    private int getIsShowFurigana() {
        return sharedPreferences.getShowHuriganaForHanja();
    }

    @Override
    protected void onDestroy() {
        unRegisterEventBus();

        super.onDestroy();
    }

    protected void initLayout() {
        super.initLayout();
        DLog.d(getLogTag(), "initLayout");
        binding.llRoot.post(() -> heightRoot = binding.llRoot.getHeight());
        binding.vAdjustHeightReading.ivAdjustTableHeight.setOnTouchListener(separatorBarListener);

        binding.tvBookHanjaFurigana.setOnTextSelectedListener(onRubyTextSelectedListener);
    }

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
        hanjaCommonMenuDialog.showOpenHanjaExcludeOptionDialogMenu(false);
        hanjaCommonMenuDialog.show();
    }

    private void initRecycleView() {
        int smallBoxWidth = BaseBindUtils.getStudyHanjaSmallBoxWidth(this);
        setLayoutManager(smallBoxWidth, binding.rvHanjaCandidate);
        hanjaConvertAdapter = new HanjaConvertAdapter(this, vocaKnowActivity.onDoubleClickListenerOnBaseVocaKnow, onDoubleClickListenerForCommon);
        binding.rvHanjaCandidate.setAdapter(hanjaConvertAdapter);
    }

    private void setLayoutManager(int itemWidth, RecyclerView recyclerView) {
        FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
    }

    private void bindData() {
        // reset adapter to reset item width.
        binding.rvHanjaCandidate.setAdapter(null);
        binding.rvHanjaCandidate.setAdapter(hanjaConvertAdapter);
        hanjaConvertAdapter.setData(hanjaItemList);
        hanjaConvertAdapter.notifyDataSetChanged();
    }

    public void _initData() {
        binding.etContent.setText(getTextToConvertFromBundle());
        convertToHanja();
    }

    private void convertToHanja() {
        String str = getTextToConvert();
        if (!Utils.isEmpty(str)) {
            if ((StringUtils.containChineseCharacters(str))) {
                callAsyncTask(TYPE_CONVERT_TO_HANJA);
            } else {
                callAsyncTask(TYPE_CONVERT_KOREAN_TO_HANJA);
            }
        }
    }

    private String getTextToConvert() {
        return binding.etContent.getText().toString().trim();
    }

    private String getTextToConvertFromBundle() {
        String str = getTextFromTextSelectionAndOpenApp();
        if (Utils.isEmpty(str)) {
            str = getIntent().getStringExtra(Constant.BUNDLE.KEY_HANJA_DATA);
        }
        if (Utils.isEmpty(str)) {
            return "";
        } else {
            int maxLengthStringToConvert = 5000;
            if (str.length() > maxLengthStringToConvert) {
                ToastUtil.getInstance(this).show("최대 " + maxLengthStringToConvert + "글자까지만 가능합니다");
            }
            return StringUtils.sliceStingIfTooLongToConvertHanja(str, maxLengthStringToConvert);
        }
    }

    private String getTextFromTextSelectionAndOpenApp() {
        //Custom Text Selection With ACTION_PROCESS_TEXT
        //https://betterprogramming.pub/custom-text-selection-with-action-process-text-9c1cd9b24027 or //https://dev.to/bigaru/providing-custom-text-selection-actions-in-android-1akc
        return (String) getIntent().getCharSequenceExtra(Intent.EXTRA_PROCESS_TEXT);
    }

    @Subscribe
    public void onEvent(SuccessEvent successEvent) {
        switch (successEvent.getScreen()) {
            case MAIN:
                switch (successEvent.getEventType()) {
                    case BOOKMARK_CHANGED:
                    case VOCA_KNOW_CHANGED:
                    case MULTIPLE_VOCA_KNOW_CHANGED:
                        convertToHanja();
                        break;
                }
                break;
        }
    }
    private void callAsyncTask(int type) {
        new CustomAsyncTask(this, this, null, type, true).execute();
    }

    private void updateLayout(String rubyText) {
        runOnUiThread(() -> {
            binding.tvBookHanjaFurigana.setFuriganViewForBookVoca(rubyText, getIsShowFurigana()); //Constant.SHOW_FURIGANA_ALL);
//            binding.tvBookHanjaFurigana.setOnTextSelectedListener(onRubyTextSelectedListener);
        });
    }

    private List<HanjaItem> convertKoreanToHanja(String text) {
        List<HanjaItem> hanjaItemCandidateList = new ArrayList<>();
        if (text.trim().equals(""))
            return hanjaItemCandidateList;

//        int itemWidth = smallBoxWidth;
        if (text.length() == 1) {
            List<DIC_HANJA> dicHanjaList = Voca.searchHanjaWordByText(text);
            hanjaItemCandidateList.addAll(dicHanjaList);
        } else {
            List<DIC_HANJA_SENTENCE> dicHanjaSentenceList = searchHanjaSentenceByPronounceRecursive(text);
            hanjaItemCandidateList.addAll(dicHanjaSentenceList);

//            itemWidth = getItemWith(text, dicHanjaSentenceList);
//            int itemWidth = getItemWith(text);
            setLayoutManager(getItemWith(text), binding.rvHanjaCandidate);
        }
//        setLayoutManager(itemWidth, binding.rvHanjaCandidate);



        return hanjaItemCandidateList;
    }

//    private int getItemWith(String text, List<DIC_HANJA_SENTENCE> dicHanjaSentenceList) {
//        int textLength = dicHanjaSentenceList.size() > 0 ? dicHanjaSentenceList.get(0).getVOCA().length() : text.length();
//        int itemWidth = textLength < 4 ? (int) (smallBoxWidth * textLength * 0.6) : (int) (smallBoxWidth * textLength * 0.5);
//        return itemWidth;
//    }

    private int getItemWith(String text) {
        int textLength = text.length();
        int smallBoxWidth = BaseBindUtils.getStudyHanjaSmallBoxWidth(this);
        int itemWidth = textLength < 4 ? (int) (smallBoxWidth * textLength * 0.6) : (int) (smallBoxWidth * textLength * 0.5);
        return itemWidth;
    }

    private List<DIC_HANJA_SENTENCE> searchHanjaSentenceByPronounceRecursive(String text) {
        List<DIC_HANJA_SENTENCE> dicHanjaSentenceList = Voca.searchHanjaSentenceByPronounceContains(text);
//        if ((dicHanjaSentenceList == null) || (dicHanjaSentenceList.isEmpty())) {
        if (Utils.isEmpty(dicHanjaSentenceList)) {
            if (text.length() > 2) {
                text = text.substring(0, text.length() - 1);
                dicHanjaSentenceList = searchHanjaSentenceByPronounceRecursive(text);
            }
        }
        return dicHanjaSentenceList;
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
            if (Utils.isFirstTimeAdjustTableHeight(ConvertToHanjaActivity.this)) {
                ToastUtil.getInstance(ConvertToHanjaActivity.this).show(R.string.toast_first_time_adjust_table_height);
            } else {
                final int height = heightRoot / 8;
//            View vAbove = view.getId() == R.id.v_adjust_height_reading ? vReadingTop : rvContent;
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        pressStartTime = System.currentTimeMillis();
                        pressedX = event.getX();
                        pressedY = event.getY();
                        stayedWithinClickDistance = true;
                        DLog.d(getLogTag(), "pressedY=" + pressedY + " - height=" + binding.vReadingTop.getHeight());
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (stayedWithinClickDistance && distance(pressedX, pressedY, event.getX(), event.getY()) > MAX_CLICK_DISTANCE) {
                            stayedWithinClickDistance = false;
                        }
//                    if (vAbove != null && vAbove.getVisibility() != View.GONE) {
                        final float currentY = event.getY() - pressedY;
                        DLog.d(getLogTag(), "pressedY=" + pressedY + "currentY=" + currentY);
                        int currentHeight = (int) (binding.vReadingTop.getHeight() + currentY);
                        if (currentHeight <= height) {
                            currentHeight = height;
                        } else if (currentHeight >= (heightRoot - height)) {
                            currentHeight = heightRoot - height;
                        }
                        binding.vReadingTop.setLayoutParams(new LinearLayout.LayoutParams(binding.vReadingTop.getWidth(), currentHeight));
                        DLog.d(getLogTag(), "height move=" + binding.vReadingTop.getHeight() + " - currentHeight=" + currentHeight);
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
            }
            return true;
        }
    };

    @Override
    public void onInitAsyncTask() {
        Loading.show(this);
    }

    @Override
    public void onPrevExecuteAsyncTask() {

    }

    @Override
    public Object onPostExecuteAsyncTask(int searchType, Object data) {
        switch (searchType) {
            case TYPE_INIT_DATA:
            case TYPE_CONVERT_TO_HANJA:
//                String textToConvert = StringUtils.getOnlyChinese(getTextToConvert());
                String textToConvert = getTextToConvert();
                hanjaItemList = Voca.getHanjaWordItemListFromContent(textToConvert, true);
                for(HanjaItem hanjaItem : hanjaItemList) {
                    if (BaseVocaKnow.isVocaTypeSentence(hanjaItem)) {
                        setLayoutManager(getItemWith(hanjaItem.getHI_VOCA()), binding.rvHanjaCandidate);
                        break;
                    }
                }
                return Voca.getRubyTextAndUnknownWord(textToConvert, "");
            case TYPE_CONVERT_KOREAN_TO_HANJA:
                hanjaItemList = convertKoreanToHanja(getTextToConvert());
                return Voca.getRubyTextAndUnknownWord("", "");
        }
        return Voca.getRubyTextAndUnknownWord(getTextToConvert(), "");
    }

    @Override
    public void onFinishAsyncTask(int searchType, Object resultData, Object data) {
        updateLayout((List<String>) resultData);
        bindData();
        Utils.hideSoftKeyboard(this, binding.etContent);
        Loading.hide();
    }

    private void updateLayout(List<String> resultData) {
        listRubyTextAndUnknownWord = resultData;
        updateLayout(listRubyTextAndUnknownWord.get(0));
    }

//    public FuriganaView.OnTextSelectedListener onRubyTextSelectedListener = new FuriganaView.OnTextSelectedListener() {
//        @Override
//        public void onTextSelected(String text, RubyTextModel rubyTextModel) {
//            openHanjaWordInfoView(rubyTextModel);
//        }
//
//        @Override
//        public void onDoubleClick(String text, RubyTextModel rubyTextModel) {
//            DIC_HANJA hanja = Voca.searchHanjaWordByID((long) rubyTextModel.getVocaId());
//
//            int vocaKnowToBe = Constant.VOCA_KNOW.VOCA_KNOW_KNOWN;
//            if (VocaKnow.isKnown(rubyTextModel.getVocaKnow())) {
//                vocaKnowToBe = Constant.VOCA_KNOW.VOCA_KNOW_UNKNOWN;
//            }
//            vocaKnowActivity.checkLocalAndChangeVocaKnow(hanja, vocaKnowToBe);
//        }
//    };

    private void openHanjaWordInfoView(RubyTextModel rubyTextModel) {
        openHanjaInfoView(Voca.searchHanjaWordByID((long) rubyTextModel.getVocaId()));
    }

    @Override
    protected void showHanjaSearchResultView() {
        super.showHanjaSearchResultView();
        binding.llMain.setVisibility(View.GONE);
        binding.adViewContainer.setVisibility(View.GONE);
    }
    @Override
    protected void hideHanjaSearchResultView() {
        super.hideHanjaSearchResultView();
        binding.llMain.setVisibility(View.VISIBLE);
        binding.adViewContainer.setVisibility(View.VISIBLE);
    }
}