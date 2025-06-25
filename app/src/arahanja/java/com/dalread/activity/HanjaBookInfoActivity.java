package com.dalread.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dalread.R;
import com.dalread.adapter.VocaHanjaSentenceInfoAdapter;
import com.dalread.component.GridSeparatorItemDecoration;
import com.dalread.component.Toolbar;
import com.dalread.databinding.ActivityHanjaBookInfoBinding;
import com.dalread.model.VOCABOOKS_HANJA_CLASSICS;
import com.dalread.network.events.SuccessEvent;
import com.dalread.util.BaseBindUtils;
import com.dalread.util.Constant;
import com.dalread.util.DLog;
import com.dalread.util.ToastUtil;
import com.dalread.util.Utils;
import com.dalread.util.Voca;

import org.greenrobot.eventbus.Subscribe;

import butterknife.BindDimen;

@SuppressLint("NonConstantResourceId")
public class HanjaBookInfoActivity extends BaseHanjaInfoActivity implements View.OnClickListener  {
//    @BindDimen(R.dimen.study_hanja_small_box_width)
//    int smallBoxWidth;
//    @BindDimen(R.dimen.study_hanja_small_box_margin)
//    int smallBoxMargin;

    private VocaHanjaSentenceInfoAdapter adapter;
    private int heightRoot = 0;
    private VOCABOOKS_HANJA_CLASSICS hanjaBook;

    private ActivityHanjaBookInfoBinding binding;

    public static Intent createIntent(Context context, VOCABOOKS_HANJA_CLASSICS vocabooksHanjaClassics) {
        Intent intent = new Intent(context, HanjaBookInfoActivity.class);
        intent.putExtra(Constant.BUNDLE.KEY_VOCA_BOOK, vocabooksHanjaClassics);
        return intent;
    }

    protected View getContentView() {
        binding = ActivityHanjaBookInfoBinding.inflate(getLayoutInflater());
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
        hanjaBook = (VOCABOOKS_HANJA_CLASSICS) getIntent().getSerializableExtra(Constant.BUNDLE.KEY_VOCA_BOOK);
        _initLayout();
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
        binding.rvInfo.setLayoutManager(layoutManager);
        binding.rvInfo.addItemDecoration(new GridSeparatorItemDecoration(getDrawable(R.drawable.grid_item_divider), noOfColumns));
        binding.rvInfo.setAdapter(adapter);

        if (getToolbar() != null) {
            getToolbar().setTitle(getBookInfoTitle());
        }

        binding.llRoot.post(() -> heightRoot = binding.llRoot.getHeight());
        binding.vAdjustHeightReading.ivAdjustTableHeight.setOnTouchListener(separatorBarListener);
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
            if (Utils.isFirstTimeAdjustTableHeight(HanjaBookInfoActivity.this)) {
                ToastUtil.getInstance(HanjaBookInfoActivity.this).show(R.string.toast_first_time_adjust_table_height);
            } else {
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

    @Subscribe
    public void onEvent(SuccessEvent successEvent) {
        switch (successEvent.getScreen()) {
            case EDIT_HANJA_SENTENCE:
                switch (successEvent.getEventType()) {
                    case DATA_CHANGED:
                        reloadData();
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

    private void reloadData() {
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
        adapter.setData(getBookInfoAllToFindHanja());
        adapter.notifyDataSetChanged();
        updateUI();
    }

    private void updateUI() {
        binding.tvDetailInfo.setText(getBookInfo());
    }

    private void setOnClickListeners() {
        binding.vAdjustHeightReading.ivWordList.setOnClickListener(this);
        binding.vAdjustHeightReading.ivWebSearch.setOnClickListener(this);
        binding.vAdjustHeightReading.ivAdjustTableHeight.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ivWordList:
//                if (onItemDoubleClickListener != null) {
//                    onItemDoubleClickListener.onClick(view, getBookInfoAllToFindHanja());
//                }
                break;
            case R.id.ivWebSearch:
                Utils.openWebSearchForHanja(context, getBookName() + " " + hanjaBook.getHANJA_KO());
                break;
        }
    }

    private String getBookInfoAllToFindHanja() {
        return getBookInfoTitle() + getBookInfo();
    }

    private String getBookInfo() {
        return hanjaBook.getNameDetailed(context);
    }
    private String getBookInfoTitle() {
        if (Utils.isEmpty(hanjaBook.getHANJA_KO())) {
            return getBookName();
        } else {
            return  getBookName() + "(" + hanjaBook.getHANJA_KO() + ")";
        }
    }

    private String getBookName() {
        return hanjaBook.getName(context);
    }

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


    @Override
    protected void openWebDictionaryScreen() {
        Utils.openWebDictionaryForHanja(this, hanjaBook.getNAME_KO());
//        Utils.openWeb(this, Constant.URL_WEB_DIC_HANJA_KOREAN + hanjaSentence.getVOCA());
    }

    @Override
    protected void openWebSearchScreen() {
        Utils.openWebSearchForHanja(this, hanjaBook.getNAME_KO());
//        Utils.openWeb(this, Constant.URL_WEB_SEARCH_GOOGLE_HANJA_KOREAN + hanjaSentence.getVOCA() + "+뜻");
    }
}
