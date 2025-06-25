package com.dalread.activity;

import android.annotation.SuppressLint;
import android.view.View;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dalread.R;
import com.dalread.adapter.HanjaWordGridAdapter;
import com.dalread.component.GridSeparatorItemDecoration;
import com.dalread.databinding.ActivityGroupedListBinding;
import com.dalread.model.HanjaItem;
import com.dalread.util.BaseBindUtils;
import com.dalread.util.Constant;
import com.dalread.util.HanjaVoca;
import com.dalread.util.NumberUtil;
import com.dalread.util.ToastUtil;
import com.dalread.util.Utils;
import com.dalread.util.ViewUtil;
import com.dalread.util.Voca;
import com.dalread.util.VocaQuiz;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import butterknife.BindDimen;
import butterknife.OnClick;
import me.zhanghai.android.fastscroll.FastScrollerBuilder;

@SuppressLint("NonConstantResourceId")
public abstract class BaseGroupedHanjaActivity extends BaseHanjaInfoActivity implements View.OnClickListener {
    private int spanCountForHanjaGrid;
    protected abstract RecyclerView.Adapter<RecyclerView.ViewHolder> getGroupAdapterVertical();
    protected HanjaWordGridAdapter hanjaWordGridAdapter;
    protected Map<Object, List<HanjaItem>> hanjaMap;
    protected Object hanjaMapKey;
    protected final int TYPE_REFRESH_VOCA = 0;
    protected final int PREVIOUS_SHOW_DIFFICULT_STATUS_NULL = 0;
    protected final int PREVIOUS_SHOW_DIFFICULT_STATUS_ALL = PREVIOUS_SHOW_DIFFICULT_STATUS_NULL + 1;
    protected final int PREVIOUS_SHOW_DIFFICULT_STATUS_NOT_ALL = PREVIOUS_SHOW_DIFFICULT_STATUS_ALL + 1;
    protected int previousShowDifficultStatus = PREVIOUS_SHOW_DIFFICULT_STATUS_NULL;

    protected ActivityGroupedListBinding binding;

    protected View getContentView() {
        binding = ActivityGroupedListBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        return view;
    }
    @Override
    protected void initData() {
        super.initData();

//        hanjaAdapter = new SearchHanjaAdapter(context);
        hanjaMap = new LinkedHashMap<>();
        initOnClickListener();
        //TODO : check this.
        addMobileAdsView();
        loadBanner();
    }

    @Override
    protected void initLayout() {
        super.initLayout();
        new FastScrollerBuilder(binding.rvInfo).build();

//        hanjaAdapter.setOnDoubleClickListener(onItemDoubleClickListener);

        int numberToReduceColumnCount = 1;
        int smallBoxWidth = BaseBindUtils.getStudyHanjaSmallBoxWidth(this);
        spanCountForHanjaGrid = Voca.calculateNoOfColumns(this, (int) (smallBoxWidth * sharedPreferences.getAraHanjaFontSizeRatio(this)), 10) - numberToReduceColumnCount;

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, spanCountForHanjaGrid);
        binding.rvInfo.setLayoutManager(layoutManager);
        binding.rvInfo.addItemDecoration(new GridSeparatorItemDecoration(getDrawable(R.drawable.grid_item_divider), spanCountForHanjaGrid));
        hanjaWordGridAdapter = new HanjaWordGridAdapter(context, vocaKnowActivity.onDoubleClickListenerOnBaseVocaKnow, onDoubleClickListenerForCommon);
        binding.rvInfo.setAdapter(hanjaWordGridAdapter);

        binding.rvGroupVertical.setAdapter(getGroupAdapterVertical());
        binding.rvGroupVertical.setLayoutManager(new LinearLayoutManager(context));
        binding.rvGroupVertical.addItemDecoration(BaseBindUtils.getSeparatorDecoration(context));

//        super.initLayout();
    }

//    @Override
//    protected List<HanjaQuizItem> getQuizHanjaList() {
//        return null;
//    }

//    @Override
//    protected void updateVocaKnowInDB(IVocaBasicItem voca, int newVocaKnow) {
//        super.updateVocaKnowInDB(voca, newVocaKnow);
//        hanjaWordGridAdapter.notifyDataSetChanged();
//    }
//
//    @Override
//    protected void updateVocaKnowPronounceInDB(IVocaBasicItem voca, int newVocaKnowPronounce) {
//        super.updateVocaKnowPronounceInDB(voca, newVocaKnowPronounce);
//        hanjaWordGridAdapter.notifyDataSetChanged();
//    }

    protected void initOnClickListener() {
        binding.ivEye.setOnClickListener(this);
        binding.ivWordList.setOnClickListener(this);
        binding.ivPracticeQuiz.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.ivEye:
                bindFoundHanjaListInRvInfoRecyclerView(hanjaMap.get(getHanjaMapKey()), true,true);
                updateEyeIcon(true);
                break;
            case R.id.ivWordList:
                openHanjaWordListInfoView(hanjaMap.get(getHanjaMapKey()));
                break;
            case R.id.ivPracticeQuiz:
                openPracticeQuizScreen(hanjaMap.get(getHanjaMapKey()));
                break;
            default:
                break;
        }
    }
//    @OnClick({ R.id.ivEye, R.id.ivWordList, R.id.ivPracticeQuiz})
//    void onClick(View view) {
//        int id = view.getId();
//        switch (id) {
//            case R.id.ivEye:
//                bindFoundHanjaListInRvInfoRecyclerView(hanjaMap.get(getHanjaMapKey()), true,true);
//                updateEyeIcon(true);
//                break;
//            case R.id.ivWordList:
//                openHanjaWordListInfoView(hanjaMap.get(getHanjaMapKey()));
//                break;
//            case R.id.ivPracticeQuiz:
//                openPracticeQuizScreen(hanjaMap.get(getHanjaMapKey()));
//                break;
//            default:
//                break;
//        }
//    }
    protected void openPracticeQuizScreen(List<HanjaItem> hanjaItemList) {
        startActivity(PracticeConversationActivity.createIntentByQuizList(this, VocaQuiz.generateVocaStudyChatExamFromHanjaItemList(this, hanjaItemList)));
    }
    protected void openHanjaWordListInfoView(List<HanjaItem> hanjaItemList) {
        if (previousShowDifficultStatus != PREVIOUS_SHOW_DIFFICULT_STATUS_ALL) {
            hanjaItemList = getDifficultHanjaItemList(hanjaItemList);
        }
        openNewScreen(
                HanjaWordListInfoActivity.createIntentVocaTypeId(this, HanjaVoca.getVocaTypeIdFromHanjaItemList(hanjaItemList), true)
        );
    }

    protected Object getHanjaMapKey() {
        return null;
    }
    protected void bindFoundHanjaListInRvInfoRecyclerView(List<HanjaItem> hanjaItemList, boolean dofilter, boolean isClickedShowWordList) {
        if (dofilter) {
            hanjaWordGridAdapter.setData(filterDifficultWords(hanjaItemList, isClickedShowWordList));
        } else {
            hanjaWordGridAdapter.setData(hanjaItemList);
        }
        hanjaWordGridAdapter.notifyDataSetChanged();

        scrollToPositionToHighlight(hanjaItemList);
    }

    private void scrollToPositionToHighlight(List<HanjaItem> hanjaItemList) {
        HanjaItem hanjaItemToHighlight = hanjaWordGridAdapter.getHanjaItemToHighlight();
        if (hanjaItemToHighlight == null) {
            if (blnScrollToFirstInHanjaListView) {
                binding.rvInfo.getLayoutManager().scrollToPosition(0);
            } else {
                blnScrollToFirstInHanjaListView = true;
            }
        } else {
            binding.rvInfo.getLayoutManager().scrollToPosition(getIndexToScrollToPositionToHighlight(hanjaItemList));

        }
    }

    private int getIndexToScrollToPositionToHighlight(List<HanjaItem> hanjaItemList) {
        int indexToScroll = 0;
        HanjaItem hanjaItemToHighlight = hanjaWordGridAdapter.getHanjaItemToHighlight();
        if (hanjaItemToHighlight != null) {
            boolean foundHanja = false;
            for(HanjaItem hanjaItem : hanjaItemList) {
                if (Voca.isSameVoca(hanjaItem, hanjaItemToHighlight)) {
                    foundHanja = true;
                    break;
                }
                indexToScroll++;
            }
            if (!foundHanja)
                indexToScroll = 0;
        }
        return indexToScroll;
    }

    protected boolean isHighlightHanjaExistInTheLast(List<HanjaItem> hanjaItemList) {
        if (Utils.isEmpty(hanjaItemList))
            return false;

        int indexToScroll = getIndexToScrollToPositionToHighlight(hanjaItemList);
        int rowsCountInTheHanjaGrid = (hanjaItemList.size() - 1) / spanCountForHanjaGrid;
        int indexToScrollInRows = indexToScroll / spanCountForHanjaGrid;
        if (rowsCountInTheHanjaGrid == indexToScrollInRows)
            return true;
        return false;
    }



    protected List<HanjaItem> filterDifficultWords(List<HanjaItem> hanjaItemList, boolean isClickedShowWordList) {
        List<HanjaItem> hanjaItemListFiltered = new ArrayList<>();
        List<HanjaItem> hanjaItemListDifficultWords = getDifficultHanjaItemList(hanjaItemList);

        if (isClickedShowWordList) {
            if (previousShowDifficultStatus == PREVIOUS_SHOW_DIFFICULT_STATUS_NOT_ALL) {
                hanjaItemListFiltered.addAll(hanjaItemList);
            } else {
                if (hanjaItemListDifficultWords.size() == 0) {
                    hanjaItemListFiltered.addAll(hanjaItemList);
                } else {
                    hanjaItemListFiltered.addAll(hanjaItemListDifficultWords);
                }
            }


        } else {
            if (previousShowDifficultStatus == PREVIOUS_SHOW_DIFFICULT_STATUS_NULL) {
                hanjaItemListFiltered.addAll(hanjaItemList);
            } else {
                if (previousShowDifficultStatus == PREVIOUS_SHOW_DIFFICULT_STATUS_NOT_ALL) {
                    if (hanjaItemListDifficultWords.size() == 0) {
                        hanjaItemListFiltered.addAll(hanjaItemList);
                    } else {
                        hanjaItemListFiltered.addAll(hanjaItemListDifficultWords);
                    }
                } else {
                    hanjaItemListFiltered.addAll(hanjaItemList);
                }
            }
        }

        if (hanjaItemList.size() == hanjaItemListFiltered.size()) {
            previousShowDifficultStatus = PREVIOUS_SHOW_DIFFICULT_STATUS_ALL;
        } else {
            previousShowDifficultStatus = PREVIOUS_SHOW_DIFFICULT_STATUS_NOT_ALL;
        }
        return hanjaItemListFiltered;
    }

    @NotNull
    private List<HanjaItem> getDifficultHanjaItemList(List<HanjaItem> hanjaItemList) {
        return hanjaItemList.stream()
                .filter(e -> e.getHI_VOCA_KNOW() < Constant.VOCA_KNOW.VOCA_KNOW_KNOWN)
                .collect(Collectors.toList());
    }

    protected void updateEyeIcon(boolean showToast) {
        if (previousShowDifficultStatus == PREVIOUS_SHOW_DIFFICULT_STATUS_ALL) {
            binding.ivEye.setImageResource(R.drawable.ic_new_eye_close);
            if (showToast)
                ToastUtil.getInstance(this).show("전체 단어를 다 보여줍니다.");
        } else {
            binding.ivEye.setImageResource(R.drawable.ic_new_eye_open);
            if (showToast)
                ToastUtil.getInstance(this).show("모르는 단어만 보여줍니다.");
        }
    }
    protected void updateUI(Object hanjaMapKey) {
        binding.llRadical.setVisibility(View.VISIBLE);
        binding.tvVoca.setVisibility(View.GONE);
        binding.tvDetailInfo.setText("");

        if (hanjaMap.containsKey(hanjaMapKey)) {
            List<HanjaItem> hanjaItemList = hanjaMap.get(hanjaMapKey);

            int knowAll = hanjaItemList.size();
            int knowCount = (int) hanjaItemList.stream().filter(e -> e.getHI_VOCA_KNOW() == Constant.VOCA_KNOW.VOCA_KNOW_KNOWN).count();
            binding.tvUnknownWordInfo.setText(getString(R.string.msg_you_know_how_many_words, NumberUtil.formatNumber(knowCount), NumberUtil.formatNumber(knowAll)));

            binding.ivEye.setVisibility(knowCount == 0 ? View.INVISIBLE : View.VISIBLE);
            updateEyeIcon(false);
        }
    }

    protected void showHanjaSearchResultView() {
        super.showHanjaSearchResultView();
        ViewUtil.setViewListVisibilityGone(binding.llMain, binding.rvGroupLandscape);//, groupBinding.adViewContainer);
    }

    protected void hideHanjaSearchResultView() {
        super.hideHanjaSearchResultView();
        ViewUtil.setViewListVisibilityVisible(binding.llMain, binding.rvGroupLandscape, binding.adViewContainer);
    }

    protected List<HanjaItem> updateHanjaList(List<HanjaItem> hanjaItemList, HanjaItem hanjaItem) {
        if (hanjaItem == null)
            return hanjaItemList;

        for(int i = 0; i < hanjaItemList.size(); i++) {
            HanjaItem hanjaItemInList = hanjaItemList.get(i);
            if (hanjaItemInList.getHI_ID().equals(hanjaItem.getHI_ID())) {
                hanjaItemInList.setHI_VOCA_KNOW(hanjaItem.getHI_VOCA_KNOW());
                hanjaItemInList.setHI_VOCA_KNOWPRONOUNCE(hanjaItem.getHI_VOCA_KNOWPRONOUNCE());
                hanjaItemInList.setHI_BOOKMARK(hanjaItem.getHI_BOOKMARK());
                break;
            }
        }
        return hanjaItemList;
    }

}
