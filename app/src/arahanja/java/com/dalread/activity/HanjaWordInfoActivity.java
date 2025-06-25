package com.dalread.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dalread.R;
import com.dalread.adapter.VocaHanjaWordInfoAdapter;
import com.dalread.asyntask.CustomAsyncTask;
import com.dalread.asyntask.OnAsyncTaskListener;
import com.dalread.component.Toolbar;
import com.dalread.databinding.ActivityHanjaWordListBinding;
import com.dalread.dialog.YesNoDialog;
import com.dalread.listener.OnDoubleClickListener;
import com.dalread.listener.OnYesNoClickListener;
import com.dalread.model.DIC_HANJA;
import com.dalread.model.HanjaGroupType;
import com.dalread.model.HanjaGroupTypeModel;
import com.dalread.model.HanjaQuizItem;
import com.dalread.network.events.SuccessEvent;
import com.dalread.util.AbstractPointUtil;
import com.dalread.util.BaseBindUtils;
import com.dalread.util.Constant;
import com.dalread.util.HanjaVoca;
import com.dalread.util.Loading;
import com.dalread.util.PointUtil;
import com.dalread.util.Utils;
import com.dalread.util.Voca;

import org.greenrobot.eventbus.Subscribe;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import me.zhanghai.android.fastscroll.FastScrollerBuilder;

@SuppressLint("NonConstantResourceId")
public class HanjaWordInfoActivity extends BaseHanjaInfoActivity implements OnAsyncTaskListener {
    private DIC_HANJA mDicHanja;
    private VocaHanjaWordInfoAdapter adapter;
    protected final int TYPE_REFRESH_VOCA_AND_OPEN_EDIT_VIEW = 0;
    private boolean isOpenEditView_;
    private AbstractPointUtil pointUtil;
    private ActivityHanjaWordListBinding binding;

    public static Intent createIntent(Context context, DIC_HANJA hanja) {
        Intent intent = new Intent(context, HanjaWordInfoActivity.class);
        intent.putExtra(Constant.BUNDLE.KEY_VOCA_TYPE, hanja.getHI_VOCA_TYPE());
        intent.putExtra(Constant.BUNDLE.KEY_VOCA_ID, hanja.getID());
        return intent;
    }

    protected View getContentView() {
        binding = ActivityHanjaWordListBinding.inflate(getLayoutInflater());
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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        pointUtil = new PointUtil(this); //이건 super.onCreate보다 먼저해야 pointUtil을 사용하는곳에서 null에러가 안난다.
        super.onCreate(savedInstanceState);
        checkPointToContinue();
    }

    private void checkPointToContinue() {
        // 아라한자에서는 포인트 체크 우회 - 포인트 소모 없이 바로 진행
        // if (pointUtil.needToShowFullAd()) {
        //     final YesNoDialog dialog = new YesNoDialog(HanjaWordInfoActivity.this, R.string.warning, R.string.dialog_title_need_point_to_open_hanja_detail_view, null, new OnYesNoClickListener() {
        //         @Override
        //         public void onYesClick(View view, Object object) {
        //             watchRewardedAd();
        //         }

        //         @Override
        //         public void onNoClick(View view, Object object) {
        //             onBackPressed();
        //         }
        //     });
        //     dialog.show();
        // } else {
        //     pointUtil.consumePoint(1);
        // }
    }

    private void watchRewardedAd() {
        pointUtil.showRewardedAd(null);
    }
    @Override
    protected void onResume() {
        super.onResume();
//        if (adapter != null) {
            adapter.notifyDataSetChanged();
//        }
    }
//    @Override
//    protected RecyclerView.Adapter<RecyclerView.ViewHolder> getAdapter() {
//        return adapter;
//    }

    @Override
    protected void initDialog() {
        super.initDialog();
        hanjaCommonMenuDialog.showPracticeMenu(true);
        hanjaCommonMenuDialog.showRefreshVocaFromServerMenu(true);
    }

    @Override
    protected void getData() {
//        _initLayout();
        mDicHanja = Voca.searchHanjaWordByID((long) getIntent().getLongExtra(Constant.BUNDLE.KEY_VOCA_ID, 0));
//        hanja = (DIC_HANJA) getIntent().getSerializableExtra(Constant.BUNDLE.KEY_DIC_HANJA);
        Voca.searchAlternativeMeaningPronounceFirst(mDicHanja);
        mDicHanja.setHQIParentVoca(mDicHanja.getVOCA());
        mDicHanja.setHQIIndexHanja(0);
        bindData();

        addMobileAdsView();
        loadBanner();
    }

    @Override
    protected void initLayout() {
        super.initLayout(); //Previous code didn't call Super.
        //TODO : I think These codes are same as in the super, but if I remove these, this view shows empty view.
        new FastScrollerBuilder(binding.rvInfo).build();

        adapter = new VocaHanjaWordInfoAdapter(this, pointUtil, onRubyWordClickListener, vocaKnowActivity.onDoubleClickListenerOnBaseVocaKnow, onDoubleClickListenerForCommon, onHanjaGroupTypeDoubleClickListener );
        binding.rvInfo.setLayoutManager(new LinearLayoutManager(context));
        binding.rvInfo.addItemDecoration(BaseBindUtils.getSeparatorDecoration(context));
        binding.rvInfo.setAdapter(adapter);

        if (getToolbar() != null) {
            getToolbar().setTitle(R.string.word_info);
        }
    }

    @Subscribe
    public void onEvent(SuccessEvent successEvent) {
        switch (successEvent.getScreen()) {
            case EDIT_HANJA_WORD:
            case EDIT_HANJA_SENTENCE:
            case MAIN:
                switch (successEvent.getEventType()) {
                    case VOCA_KNOW_CHANGED:
                    case BOOKMARK_CHANGED:
                    case DATA_CHANGED:
                        reloadData();
                }
                break;
            case HANJA_VOCA:
                DIC_HANJA dicHanja = (DIC_HANJA) successEvent.getModel();
                switch (successEvent.getEventType()) {
                    case UPDATE_LOCAL_HANJA_WORD_MODEL_WITH_SERVER_MODEL_AND_OPEN_EDIT_SCREEN:
                        if (isOpenEditView_) {
                            openEditHanjaMeaningScreen(dicHanja);
                            isOpenEditView_ = false;
                        }
                        reloadData();
                        break;
                    case OPEN_EDIT_HANJA_WORD_SCREEN:
                        if ((isOpenEditView_) && (Voca.isSameVoca(dicHanja, mDicHanja))) {
                            openEditHanjaMeaningScreen(mDicHanja);
                        }
                        break;
                }
                break;
        }
    }

    protected void refreshVocaFromServer() {
        isOpenEditView_ = false;
        HanjaVoca.refreshHanjaWordAndOpenEditView(context, mDicHanja, isOpenEditView_);
    }

    private void reloadData() {
        mDicHanja = Voca.searchHanjaWordByID(mDicHanja.getID());
        if (mDicHanja == null)
            return;

        bindData();
    }

    protected void openHanjaQuizWritingScreen() {
        Intent intent = new Intent(context, HanjaQuizWritingActivity.class);
        intent.putExtra(Constant.BUNDLE.KEY_QUIZ_HANJA_LIST, (Serializable) getQuizHanjaList());
        openNewScreen(intent);
    }
    protected void openNormalTextViewForHuriganaScreen() {
        if (mDicHanja != null) {
            openNewScreen(
                    NormalTextViewForHuriganaActivity.createIntent(this, mDicHanja)
            );
        }
    }
    private List<HanjaQuizItem> getQuizHanjaList() {
        List<HanjaQuizItem> hanjaQuizItems = new ArrayList<>();
        hanjaQuizItems.add(mDicHanja);
        return hanjaQuizItems;
    }

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
        adapter.setData(mDicHanja);
        adapter.notifyDataSetChanged();
    }

    private OnDoubleClickListener onHanjaGroupTypeDoubleClickListener = new OnDoubleClickListener() {
        @Override
        public void onClick(View view, Object object) {
            HanjaGroupTypeModel hanjaGroupTypeModel = (HanjaGroupTypeModel) object;
            if ((hanjaGroupTypeModel.getHanjaGroupType() == HanjaGroupType.VOCA_LEVEL)
                ||  (hanjaGroupTypeModel.getHanjaGroupType() == HanjaGroupType.STROKES)) {
                openStrokesScreen(hanjaGroupTypeModel);
            } else if ((hanjaGroupTypeModel.getHanjaGroupType() == HanjaGroupType.LEVEL_KOREA_TYPE_1)
                || (hanjaGroupTypeModel.getHanjaGroupType() == HanjaGroupType.LEVEL_KOREA_TYPE_2)
                || (hanjaGroupTypeModel.getHanjaGroupType() == HanjaGroupType.LEVEL_KOREA_TYPE_3)) {
                openLevelKoreaTypeScreen(hanjaGroupTypeModel);
            } else if (hanjaGroupTypeModel.getHanjaGroupType() == HanjaGroupType.RADICAL) {
                if (hanjaGroupTypeModel.getIsFindAllHanjasThatContainThisHanja()) {
                    openShowHanjaInInGroupScreen(hanjaGroupTypeModel);
                } else {
                    openRadicalsScreen(hanjaGroupTypeModel);
                }
            } else if (hanjaGroupTypeModel.getHanjaGroupType() == HanjaGroupType.CJK) {
                openShowHanjaInInGroupScreen(hanjaGroupTypeModel);
            } else if ((hanjaGroupTypeModel.getHanjaGroupType() == HanjaGroupType.DECOMPOSITION)
                || (hanjaGroupTypeModel.getHanjaGroupType() == HanjaGroupType.CONFUSED)){
                openShowHanjaInInGroupScreen(hanjaGroupTypeModel);
            } else if ((hanjaGroupTypeModel.getHanjaGroupType() == HanjaGroupType.COMMON_MIDDLE_SCHOOL)
                || (hanjaGroupTypeModel.getHanjaGroupType() == HanjaGroupType.COMMON_HIGH_SCHOOL)
                || (hanjaGroupTypeModel.getHanjaGroupType() == HanjaGroupType.COMMON_USE_JAPAN)
                || (hanjaGroupTypeModel.getHanjaGroupType() == HanjaGroupType.COMMON_HSK)) {
                openStrokesScreen(hanjaGroupTypeModel);
//                openStrokesScreen(hanjaGroupTypeModel.getHanjaGroupType());
//            } else if (hanjaGroupTypeModel.getHanjaGroupType() == HanjaGroupType.COMMON_USE_JAPAN) {
//                openStrokesScreen(hanjaGroupTypeModel);
            }

        }

        @Override
        public void onDoubleClick(View view, Object object) {

        }
    };

    private void openStrokesScreen(HanjaGroupTypeModel hanjaGroupTypeModel) {
        openNewScreen(
                StrokesActivity.createIntent(HanjaWordInfoActivity.this, hanjaGroupTypeModel)
        );
    }

    private void openRadicalsScreen(HanjaGroupTypeModel hanjaGroupTypeModel) {
        openNewScreen(
                RadicalsActivity.createIntent(this, hanjaGroupTypeModel)
        );
    }

    private void openShowHanjaInInGroupScreen(HanjaGroupTypeModel hanjaGroupTypeModel) {
        openNewScreen(
                ShowHanjaInGroupActivity.createIntent(this, hanjaGroupTypeModel)
        );
    }



    private void openLevelKoreaTypeScreen(HanjaGroupTypeModel hanjaGroupTypeModel) {
        int bookId = Constant.HANJA.WORKBOOK_ID.bookIdLevelKoreaType1;
        if (hanjaGroupTypeModel.getHanjaGroupType() == HanjaGroupType.LEVEL_KOREA_TYPE_2) {
            bookId = Constant.HANJA.WORKBOOK_ID.bookIdLevelKoreaType2;
        } else if (hanjaGroupTypeModel.getHanjaGroupType() == HanjaGroupType.LEVEL_KOREA_TYPE_3) {
            bookId = Constant.HANJA.WORKBOOK_ID.bookIdLevelKoreaType3;
        }

        openNewScreen(
                LevelKoreaTypeActivity.createIntent(this, hanjaGroupTypeModel, bookId, "")
        );
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

    protected void openEditHanjaMeaningScreen() {
        if (Utils.isConnected(context)) {
            isOpenEditView_ = true;
            HanjaVoca.refreshHanjaWordAndOpenEditView(this, mDicHanja, isOpenEditView_);
//            callAsyncTask(TYPE_REFRESH_VOCA_AND_OPEN_EDIT_VIEW, mDicHanja);
        } else {
            openEditHanjaMeaningScreen(mDicHanja);
        }
    }

    private void openEditHanjaMeaningScreen(DIC_HANJA dicHanja) {
        openNewScreen(
                EditHanjaWordMeaningActivity.createIntent(this, dicHanja)
        );
    }

    private void callAsyncTask(int type, DIC_HANJA dicHanja) {
        new CustomAsyncTask(this, this, dicHanja, type, true).execute();
    }
    private void saveVoca(DIC_HANJA dicHanja) {
        Voca.updateHanjaWord(dicHanja);
    }

    @Override
    protected void openWebDictionaryScreen() {
        Utils.openWebDictionaryForHanja(this, mDicHanja.getVOCA());
//        Utils.openWeb(this, Constant.URL_WEB_DIC_HANJA_KOREAN + hanja.getVOCA());
    }

    @Override
    protected void openWebSearchScreen() {
        Utils.openWebSearchForHanja(this, mDicHanja.getVOCA());
//        Utils.openWeb(this, Constant.URL_WEB_SEARCH_GOOGLE_HANJA_KOREAN + hanja.getVOCA() + "+뜻");
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
        DIC_HANJA dicHanja = (DIC_HANJA) data;
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
}
