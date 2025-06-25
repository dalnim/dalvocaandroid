package com.dalread.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dalread.R;
import com.dalread.adapter.HanjaConvertAdapter;
import com.dalread.adapter.HanjaWordGridAdapter;
import com.dalread.asyntask.CustomAsyncTask;
import com.dalread.asyntask.OnAsyncTaskListener;
import com.dalread.component.SeparatorDecoration;
import com.dalread.component.Toolbar;
import com.dalread.databinding.ItemAppendHanjaToHangulBinding;
import com.dalread.dialog.ConvertToHanjaMenuDialog;
import com.dalread.dialog.HanjaConvertDialog;
import com.dalread.model.DIC_HANJA;
import com.dalread.model.DIC_HANJA_SENTENCE;
import com.dalread.model.HanjaItem;
import com.dalread.model.RubyTextModel;
import com.dalread.network.events.BaseEvent;
import com.dalread.network.events.SuccessEvent;
import com.dalread.util.BaseBindUtils;
import com.dalread.util.BaseVoca;
import com.dalread.util.Constant;
import com.dalread.util.DLog;
import com.dalread.util.HanjaVoca;
import com.dalread.util.Loading;
import com.dalread.util.StringUtils;
import com.dalread.util.ToastUtil;
import com.dalread.util.Utils;
import com.dalread.util.Voca;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import butterknife.BindColor;
import butterknife.BindDimen;
import butterknife.OnClick;
//Will use this to append Hanja later.
public class AppendHanjaToHangulActivity extends BaseHanjaInfoActivity implements OnAsyncTaskListener, View.OnClickListener {
    private int heightRoot = 0;

    private List<HanjaItem> hanjaItemList;
    private HanjaWordGridAdapter adapter;
    private HanjaConvertAdapter hanjaConvertAdapter;
    private String textToConvertToHanja;
    private int cursorPositionToInsertHanja;
    private final int TYPE_INIT_DATA = 0;
//    private final int TYPE_EXTRACT_HANJA = TYPE_INIT_DATA + 1;
    private final int TYPE_CONVERT_KOREAN_TO_HANJA = TYPE_INIT_DATA + 1;

    private final int PREVIOUS_SHOW_DIFFICULT_STATUS_NULL = 0;
    private final int PREVIOUS_SHOW_DIFFICULT_STATUS_ALL = PREVIOUS_SHOW_DIFFICULT_STATUS_NULL + 1;
    private final int PREVIOUS_SHOW_DIFFICULT_STATUS_NOT_ALL = PREVIOUS_SHOW_DIFFICULT_STATUS_ALL + 1;
    private int previousShowDifficultStatus = PREVIOUS_SHOW_DIFFICULT_STATUS_NULL;
    private ItemAppendHanjaToHangulBinding binding;

    public static Intent createIntent(Context context, String text) {
        Intent intent = new Intent(context, AppendHanjaToHangulActivity.class);
        intent.putExtra(Constant.BUNDLE.KEY_HANJA_DATA, text);
        return intent;
    }

    @Override
    protected RecyclerView getRvSearch() {
        return binding.rvSearch;
    }

//    @Override
//    protected RecyclerView.Adapter<RecyclerView.ViewHolder> getAdapter() {
//        return null;
//    }

    @Override
    protected void getData() {

    }

//    @Override
//    protected List<HanjaQuizItem> getQuizHanjaList() {
//        return null;
//    }

//    @Override
//    protected void updateVocaKnowInDB(IVocaBasicItem voca, int newVocaKnow) {
//        super.updateVocaKnowInDB(voca, newVocaKnow);
//    }
//
//    @Override
//    protected void updateVocaKnowPronounceInDB(IVocaBasicItem voca, int newVocaKnowPronounce) {
//        super.updateVocaKnowPronounceInDB(voca, newVocaKnowPronounce);
//    }


    protected View getContentView() {
        binding = ItemAppendHanjaToHangulBinding.inflate(getLayoutInflater());
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
        initEventBus();
//        initTextListener();
        initOnClickListener();
        _initLayout();
        initRecycleView();
        _initData();
    }

    @Override
    protected void onDestroy() {
        unRegisterEventBus();

        super.onDestroy();
    }

    private void _initLayout() {
        DLog.d(getLogTag(), "initLayout");
        if (getToolbar() != null) {
            getToolbar().setTitle(R.string.hanja_home_menu_append_hanja_to_hangul);
        }

        binding.llRoot.post(() -> heightRoot = binding.llRoot.getHeight());
        hideHanjaCandidateList();
        binding.vAdjustHeightReading.setOnTouchListener(separatorBarListener);
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
        openHanjaBookMenuDialog();
    }

    private void initRecycleView() {
        binding.rvInfo.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.HORIZONTAL));
        binding.rvInfo.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        int smallBoxWidth = BaseBindUtils.getStudyHanjaSmallBoxWidth(this);
        setLayoutManager(smallBoxWidth, binding.rvInfo);
//        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, Voca.calculateNoOfColumns(this, smallBoxWidth, 10));
//        rv_info.setLayoutManager(layoutManager);
        binding.rvInfo.addItemDecoration(new SeparatorDecoration(this, BaseBindUtils.getDividerColor(), BaseBindUtils.getDividerHeight(this)));
        adapter = new HanjaWordGridAdapter(this, vocaKnowActivity.onDoubleClickListenerOnBaseVocaKnow, onDoubleClickListenerForCommon);
//        adapter.setDoubleClick(doubleClick);
        binding.rvInfo.setAdapter(adapter);

        binding.rvHanjaCandidate.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.HORIZONTAL));
        binding.rvHanjaCandidate.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        setLayoutManager(smallBoxWidth, binding.rvHanjaCandidate);
        binding.rvHanjaCandidate.addItemDecoration(new SeparatorDecoration(this, BaseBindUtils.getDividerColor(), BaseBindUtils.getDividerHeight(this)));
        hanjaConvertAdapter = new HanjaConvertAdapter(this, vocaKnowActivity.onDoubleClickListenerOnBaseVocaKnow, onDoubleClickListenerForCommon);
        binding.rvHanjaCandidate.setAdapter(hanjaConvertAdapter);
    }

    private void setLayoutManager(int itemWidth, RecyclerView recyclerView) {
        RecyclerView.LayoutManager layoutManager1 = new GridLayoutManager(this, Voca.calculateNoOfColumns(this, itemWidth, 10));
        recyclerView.setLayoutManager(layoutManager1);
    }

    private void bindData(boolean changeShowDifficultStatus) {
        adapter.setData(filterDifficultWords(changeShowDifficultStatus));
        adapter.notifyDataSetChanged();
    }

    private void bindDataForHanjaConvert(List<HanjaItem> hanjaItemList) {
        hanjaConvertAdapter.setData(hanjaItemList);
        hanjaConvertAdapter.notifyDataSetChanged();
    }
    private List<HanjaItem> filterDifficultWords(boolean changeShowDifficultStatus) {
        List<HanjaItem> hanjaItemListFiltered = new ArrayList<>();
        List<HanjaItem> hanjasDifficultWords = hanjaItemList.stream()
                .filter(e -> e.getHI_VOCA_KNOW() < Constant.VOCA_KNOW.VOCA_KNOW_KNOWN)
                .collect(Collectors.toList());

        if (previousShowDifficultStatus == PREVIOUS_SHOW_DIFFICULT_STATUS_NULL) {
            hanjaItemListFiltered.addAll(hanjasDifficultWords);
            previousShowDifficultStatus = PREVIOUS_SHOW_DIFFICULT_STATUS_NOT_ALL;
        } else {
            if (changeShowDifficultStatus) {
                int addedCount = 0;
                if (previousShowDifficultStatus == PREVIOUS_SHOW_DIFFICULT_STATUS_ALL) {
                    if ((hanjasDifficultWords.size() > 0) && (hanjasDifficultWords.size() < hanjaItemList.size())) {
                        hanjaItemListFiltered.addAll(hanjasDifficultWords);
                        addedCount = hanjasDifficultWords.size();
                    }
                } else {
                    hanjaItemListFiltered.addAll(hanjaItemList);
                    addedCount = hanjaItemList.size();
                }

                if (hanjaItemList.size() == addedCount) {
                    previousShowDifficultStatus = PREVIOUS_SHOW_DIFFICULT_STATUS_ALL;
                } else {
                    previousShowDifficultStatus = PREVIOUS_SHOW_DIFFICULT_STATUS_NOT_ALL;
                }
            } else {
                if (previousShowDifficultStatus == PREVIOUS_SHOW_DIFFICULT_STATUS_ALL) {
                    hanjaItemListFiltered.addAll(hanjaItemList);
                } else {
                    if ((hanjasDifficultWords.size() > 0) && (hanjasDifficultWords.size() < hanjaItemList.size())) {
                        hanjaItemListFiltered.addAll(hanjasDifficultWords);
                    }
                }
            }
        }
        return hanjaItemListFiltered;
    }

    public void _initData() {

        //Custom Text Selection With ACTION_PROCESS_TEXT
        //https://betterprogramming.pub/custom-text-selection-with-action-process-text-9c1cd9b24027 or //https://dev.to/bigaru/providing-custom-text-selection-actions-in-android-1akc
        String str = getIntent().getStringExtra(Constant.BUNDLE.KEY_HANJA_DATA);
        if (!Utils.isEmpty(str)) {
            binding.etContent.setText(str);
            textToConvertToHanja = str;
            cursorPositionToInsertHanja = str.length();
            callAsyncTask(textToConvertToHanja, TYPE_CONVERT_KOREAN_TO_HANJA);
        }
    }



    @Subscribe
    public void onEvent(SuccessEvent successEvent) {
        if (successEvent.getScreen() == BaseEvent.Screen.MAIN) {
            BaseEvent.EventType type = successEvent.getEventType();
            if (type == BaseEvent.EventType.VOCA_KNOW_CHANGED) {
                callAsyncTask(textToConvertToHanja, TYPE_CONVERT_KOREAN_TO_HANJA);
            }
        }
    }
    private void callAsyncTask(int type) {
        new CustomAsyncTask(this, this, null, type, true).execute();
    }

    private void callAsyncTask(String data, int type) {
        new CustomAsyncTask(this, this, data, type, true).execute();
    }

    private void updateLayout(String rubyText) {
        runOnUiThread(() -> {
            binding.tvBookHanjaFurigana.setFuriganViewForBookVoca(rubyText, Constant.SHOW_FURIGANA_ALL);
            binding.tvBookHanjaFurigana.setOnTextSelectedListener(onRubyTextSelectedListener);
        });
    }

    private void initOnClickListener() {
        binding.ivConvertToHanja.setOnClickListener(this);
        binding.ivWordList.setOnClickListener(this);
        binding.ivShowWordList.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.iv_convert_to_hanja:
                textToConvertToHanja = getBeforeCursorWordToConvertToHanja();
                callAsyncTask(textToConvertToHanja, TYPE_CONVERT_KOREAN_TO_HANJA);
                break;
            case R.id.ivWordList:
                openHanjaSentenceInfoView(hanjaItemList);
                break;
            case R.id.iv_show_word_list:
                bindData(true);
                break;
            default:
                break;
        }
    }
//    @OnClick({R.id.iv_convert_to_hanja, R.id.ivWordList, R.id.iv_show_word_list})
//    void onClick(View view) {
//        int id = view.getId();
//        switch (id) {
//            case R.id.iv_convert_to_hanja:
//                textToConvertToHanja = getBeforeCursorWordToConvertToHanja();
//                callAsyncTask(textToConvertToHanja, TYPE_CONVERT_KOREAN_TO_HANJA);
//                break;
//            case R.id.ivWordList:
//                openHanjaSentenceInfoView(hanjaItemList);
//                break;
//            case R.id.iv_show_word_list:
//                bindData(true);
//                break;
//            default:
//                break;
//        }
//    }

    private String getBeforeCursorWordToConvertToHanja() {
        cursorPositionToInsertHanja = binding.etContent.getSelectionStart();
        String allText = binding.etContent.getText().toString();
        String charBeforeCursor = allText.substring(cursorPositionToInsertHanja - 1, cursorPositionToInsertHanja);
        if (!StringUtils.isOnlyKorean(charBeforeCursor))
            return "";

        if ((allText.length() > 0) && (cursorPositionToInsertHanja < allText.length())) {
            allText = allText.substring(0, cursorPositionToInsertHanja);
        }
        allText = StringUtils.removeSpecial(allText);
        String arrayString[] = allText.trim().split("\\s+");
        String result = arrayString.length == 0 ? "" : arrayString[arrayString.length - 1];

        return result;
    }

    private void openHanjaBookMenuDialog() {
        final ConvertToHanjaMenuDialog dialog = new ConvertToHanjaMenuDialog(this, (view, object) -> {
            switch (view.getId()) {
                case R.id.llAddSentence:
                    openEditViewIfVocaIsNotInServerDB();
                    break;
                case R.id.ll_home:
                    backToHome();
                    break;
            }
        });
        dialog.show();
    }



    private void checkNewVocaIsExistInServerDB() {

    }

    private List<HanjaItem> convertKoreanToHanja(String text) {
        List<HanjaItem> hanjaItemCandidateList = new ArrayList<>();
        if (text.trim().equals(""))
            return hanjaItemCandidateList;

        int cursorPositionToInsertHanjaOri = cursorPositionToInsertHanja;
        int itemWidth = BaseBindUtils.getStudyHanjaSmallBoxWidth(this);
        if (text.length() == 1) {
            List<DIC_HANJA> dicHanjaList = Voca.searchHanjaWordByText(text);
            hanjaItemCandidateList.addAll(dicHanjaList);
        } else {
            List<DIC_HANJA_SENTENCE> dicHanjaSentenceList = searchHanjaSentenceByTextRecursive(text);
            hanjaItemCandidateList.addAll(dicHanjaSentenceList);

            itemWidth = getItemWith(text, dicHanjaSentenceList);
        }

        if (hanjaItemCandidateList.size() == 0) {
            setCursorInText(cursorPositionToInsertHanjaOri, binding.etContent);
//            et_content.setSelection(cursorPositionToInsertHanjaOri);
        } else {
            setLayoutManager(itemWidth, binding.rvHanjaCandidate);
        }

        return hanjaItemCandidateList;
    }

    private int getItemWith(String text, List<DIC_HANJA_SENTENCE> dicHanjaSentenceList) {
        int textLength = dicHanjaSentenceList.size() > 0 ? dicHanjaSentenceList.get(0).getVOCA().length() : text.length();
        int smallBoxWidth = BaseBindUtils.getStudyHanjaSmallBoxWidth(this);
        int itemWidth = textLength < 4 ? (int) (smallBoxWidth * textLength * 0.6) : (int) (smallBoxWidth * textLength * 0.5);
        return itemWidth;
    }

    private List<DIC_HANJA_SENTENCE> searchHanjaSentenceByTextRecursive(String text) {
        List<DIC_HANJA_SENTENCE> dicHanjaSentenceList = Voca.searchHanjaSentenceByPronounceContains(text);
//        if ((dicHanjaSentenceList == null) || (dicHanjaSentenceList.isEmpty())) {
        if (Utils.isEmpty(dicHanjaSentenceList)) {
            if (text.length() > 2) {
                text = text.substring(0, text.length() - 1);
                cursorPositionToInsertHanja--;
                setCursorInText(cursorPositionToInsertHanja, binding.etContent);
//                et_content.setSelection(cursorPositionToInsertHanja);
                dicHanjaSentenceList = searchHanjaSentenceByTextRecursive(text);
            }
        }
        return dicHanjaSentenceList;

    }

    private void openHanjaSentenceInfoView(List<HanjaItem> hanjaItemList) {
        if ((hanjaItemList == null) || (hanjaItemList.size() == 0))
            return;

        openNewScreen(
                HanjaWordListInfoActivity.createIntentVocaTypeId(this, HanjaVoca.getVocaTypeIdFromHanjaItemList(hanjaItemList), true)
        );
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
                String contents = binding.etContent.getText().toString();
                hanjaItemList = Voca.getHanjaWordItemListFromContent(contents, true);
                return Voca.getRubyTextAndUnknownWord(contents, "");
            case TYPE_CONVERT_KOREAN_TO_HANJA:
                String text = data.toString();
                List<HanjaItem> hanjaItemList = convertKoreanToHanja(text);
                return hanjaItemList;
        }
        return null;
    }

    @Override
    public void onFinishAsyncTask(int searchType, Object resultData, Object data) {
        switch (searchType) {
            case TYPE_INIT_DATA:
                updateLayout((List<String>) resultData);
                bindData(false);
                hideHanjaCandidateList();
                break;
            case TYPE_CONVERT_KOREAN_TO_HANJA:
                List<HanjaItem> hanjaItemList = (List<HanjaItem>) resultData;
                bindDataForHanjaConvert(hanjaItemList);
                showHanjaCandidateList();
                break;
        }
        Utils.hideSoftKeyboard(this, binding.etContent);
        Loading.hide();
    }

    private void updateLayout(List<String> resultData) {
        List<String> listRubyTextAndUnknownWord  = resultData;
        String rubyText = listRubyTextAndUnknownWord.get(0);
        String unknownWordText = listRubyTextAndUnknownWord.get(1); //Dalnim : I don't use this anymore
        updateLayout(rubyText);
    }

//    public FuriganaView.OnTextSelectedListener onRubyTextSelectedListener = new FuriganaView.OnTextSelectedListener() {
//
//        @Override
//        public void onTextSelected(String text, RubyTextModel rubyTextModel) {
//            openHanjaWordInfoView(rubyTextModel);
//        }
//
//        @Override
//        public void onDoubleClick(String text, RubyTextModel rubyTextModel) {
//            DIC_HANJA hanja = new DIC_HANJA();
//            hanja.setID((long) rubyTextModel.getVocaId());
//
//            int vocaKnowToBe = Constant.VOCA_KNOW.VOCA_KNOW_KNOWN;
//            if (rubyTextModel.getVocaKnow() >= Constant.VOCA_KNOW.VOCA_KNOW_KNOWN) {
//                vocaKnowToBe = Constant.VOCA_KNOW.VOCA_KNOW_UNKNOWN;
//            }
//            vocaKnowActivity.checkLocalAndChangeVocaKnow(hanja, vocaKnowToBe);
//        }
//    };

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
            openHanjaInfoView(dicHanja);
//            openNewScreen(
//                    HanjaWordInfoActivity.createIntent(this, dicHanja)
//            );
        }
    }

//    protected OnDoubleClickListener onDoubleClickListener = new OnDoubleClickListener() {
//        @Override
//        public void onClick(View view, Object object) {
//            HanjaItem hanjaItem = (HanjaItem) object;
//            switch (view.getId()) {
//                case R.id.v_item:
//                    openHanjaConvertDialog(hanjaItem);
//
//                    break;
//            }
//        }
//
//        @Override
//        public void onDoubleClick(View view, Object object) {
//            vocaKnowActivity.switchVocaKnow(object);
//        }
//    };

    private void openHanjaConvertDialog(HanjaItem hanjaItem) {
        final HanjaConvertDialog dialog = new HanjaConvertDialog(this, sharedPreferences, (view, object) -> {
            switch (view.getId()) {
                case R.id.ll_choose_hanja:
                    appendHanja(hanjaItem.getHI_VOCA());
                    hideHanjaCandidateList();
                    break;
                case R.id.ll_open_info_view:
                    openHanjaInfoView(hanjaItem);
                    break;

            }
        });
        dialog.show();
    }

    private void appendHanja(String hanja) {
        String allText = binding.etContent.getText().toString();
        if (allText.length() >= cursorPositionToInsertHanja) {
            String textBefore = allText.substring(0, cursorPositionToInsertHanja);
            String textHanja = "(" + hanja + ")";
            String textAfter = allText.substring(cursorPositionToInsertHanja, allText.length());
            binding.etContent.setText(textBefore + textHanja + textAfter);
            cursorPositionToInsertHanja = textBefore.length() + textHanja.length();
        }
        setCursorInText(cursorPositionToInsertHanja, binding.etContent);
//        et_content.setSelection(cursorPositionToInsertHanja);
    }

    private void setCursorInText(int curPosition, EditText editText) {
        if ((curPosition >= 0) && (curPosition < editText.getText().toString().length())) {
            runOnUiThread(() -> editText.setSelection(curPosition));
        }
    }
    private void showHanjaCandidateList() {
        binding.rvInfo.setVisibility(View.GONE);
        binding.rvHanjaCandidate.setVisibility(View.VISIBLE);
    }

    private void hideHanjaCandidateList() {
        binding.rvInfo.setVisibility(View.VISIBLE);
        binding.rvHanjaCandidate.setVisibility(View.GONE);
    }

    @Override
    protected void showHanjaSearchResultView() {
        super.showHanjaSearchResultView();
        binding.llMain.setVisibility(View.GONE);
    }
    @Override
    protected void hideHanjaSearchResultView() {
        super.hideHanjaSearchResultView();
        binding.llMain.setVisibility(View.VISIBLE);
    }

//    private void initTextListener() {
//        et_content.addTextChangedListener(new TextWatcher() {
//
//            @Override
//            public void afterTextChanged(Editable s) {
//
//            }
//
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count,
//                                          int after) {
//                // TODO Auto-generated method stub
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before,
//                                      int count) {
//                // TODO Auto-generated method stub
//                String textToConvertHanja = s.toString();
//                textToConvertToHanja = textToConvertHanja;
//                callAsyncTask(textToConvertToHanja, TYPE_CONVERT_KOREAN_TO_HANJA);
//            }
//        });
//    }
}