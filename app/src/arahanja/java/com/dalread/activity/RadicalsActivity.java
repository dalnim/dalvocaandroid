package com.dalread.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dalread.R;
import com.dalread.adapter.RadicalGroupAdapter;
import com.dalread.adapter.StrokeGroupAdapter;
import com.dalread.asyntask.CustomAsyncTask;
import com.dalread.asyntask.OnAsyncTaskListener;
import com.dalread.component.Toolbar;
import com.dalread.listener.OnClickListener;
import com.dalread.model.DIC_HANJA;
import com.dalread.model.HanjaGroupType;
import com.dalread.model.HanjaGroupTypeModel;
import com.dalread.model.HanjaItem;
import com.dalread.model.VOCABOOK_HANJA;
import com.dalread.network.events.BaseEvent;
import com.dalread.network.events.SuccessEvent;
import com.dalread.util.BaseBindUtils;
import com.dalread.util.BaseVoca;
import com.dalread.util.Constant;
import com.dalread.util.Loading;
import com.dalread.util.Utils;
import com.dalread.util.Voca;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import butterknife.OnClick;
import io.realm.RealmResults;

public class RadicalsActivity extends BaseGroupedHanjaActivity implements OnAsyncTaskListener {
    private RadicalGroupAdapter groupAdapterVertical;
    private StrokeGroupAdapter groupAdapterLandscape;

    private Map<Long, List<DIC_HANJA>> groupMap; //Store Radical's Strokes and its Hanja list (부수의 획수별 한자리스트를 가짐)
    private List<String> groupListLandscape; //획순을 가지고 있음, 상단의 가로 리사이클뷰
    private Long selectedStroke;
    private DIC_HANJA dicHanjaRadical;
    private HanjaGroupType hanjaGroupType;
    private HanjaGroupTypeModel hanjaGroupTypeModel;

    public static Intent createIntent(Context context, HanjaGroupTypeModel hanjaGroupTypeModel) {
        Intent intent = new Intent(context, RadicalsActivity.class);
        intent.putExtra(Constant.BUNDLE.KEY_HANJA_GROUP_TYPE_MODEL, hanjaGroupTypeModel);
        return intent;
    }

    @Override
    protected RecyclerView getRvSearch() {
        return binding.rvSearch;
    }

    @Override
    protected RecyclerView.Adapter<RecyclerView.ViewHolder> getGroupAdapterVertical() {
        return groupAdapterVertical;
    }

    @Override
    protected Toolbar getToolbar() {
        return binding.header;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    protected void initData() {
        super.initData();

        hanjaGroupTypeModel = getIntent().getParcelableExtra(Constant.BUNDLE.KEY_HANJA_GROUP_TYPE_MODEL);
        hanjaGroupType = hanjaGroupTypeModel.getHanjaGroupType();

        groupAdapterVertical = new RadicalGroupAdapter(this, onGroupClickListener);
        groupMap = new LinkedHashMap<>();

        groupListLandscape = new ArrayList<String>();
        groupAdapterLandscape = new StrokeGroupAdapter(this, onGroupClickListenerLandscape);
        binding.rvGroupLandscape.setAdapter(groupAdapterLandscape);
        binding.rvGroupLandscape.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        binding.rvGroupLandscape.addItemDecoration(BaseBindUtils.getSeparatorDecoration(context));

        binding.rvGroupLandscape.setVisibility(View.VISIBLE);
    }

    private final OnClickListener onGroupClickListenerLandscape = (view, object) -> {
        selectedStroke = Long.parseLong((String) object);
        bindGroupDataVertical();
        binding.rvGroupVertical.getLayoutManager().scrollToPosition(0);
    };

    @Override
    protected void initLayout() {
        super.initLayout();

        if (getToolbar() != null) {
            getToolbar().setTitle(R.string.radicals);
        }
    }

    @Override
    protected void getData() {
        groupMap.clear();
        groupListLandscape.clear();
        Loading.showDelay(context, 1000L);

        BaseVoca.executeRealmTransactionAsync(realm -> {
            RealmResults<VOCABOOK_HANJA> vocabookHanjaRealmResults = realm.where(VOCABOOK_HANJA.class)
                    .equalTo(Constant.REALMDB.KEY_VOCABOOKS_ID, Constant.HANJA.WORKBOOK_ID.radical) // Default book for Radicals
                    .equalTo(Constant.REALMDB.KEY_VOCA_TYPE, Constant.API_VALUE.VALUE_VOCA_TYPE_WORD) // Type word
                    .sort(Constant.REALMDB.KEY_DISP_ORDER)
                    .findAll();
            if (!vocabookHanjaRealmResults.isEmpty()) {
                hanjaMap.clear();
                for (VOCABOOK_HANJA vocabookHanja : vocabookHanjaRealmResults) {
                    DIC_HANJA dicHanja = realm.where(DIC_HANJA.class)
                            .equalTo(Constant.REALMDB.KEY_ID, vocabookHanja.getVOCA_ID())
                            .findFirst();
                    if (dicHanja != null) {
                        DIC_HANJA copiedDicHanja = realm.copyFromRealm(dicHanja);
                        long strokes = copiedDicHanja.getSTROKES();
                        if (groupMap.containsKey(strokes)) {
                            groupMap.get(strokes).add(copiedDicHanja);
                        } else {
                            groupListLandscape.add(String.valueOf(strokes));
                            List<DIC_HANJA> groupHanjas = new ArrayList<>();
                            groupHanjas.add(copiedDicHanja);
                            groupMap.put(strokes, groupHanjas);
                        }
                    }
                }
            }
//            bindGroupData2();
        }, () -> {
            bindGroupDataLandscape();
            bindGroupDataVertical();
            Loading.hide();
        }, error -> {
            error.printStackTrace();
            Loading.hide();
        });
//        }, this::bindGroupData, error -> {
//            error.printStackTrace();
//
//            Loading.hide();
//        });
    }

    @Override
    protected void showSimplifiedChineseBeyondLevelHanja() {
        super.showSimplifiedChineseBeyondLevelHanja();
//        bindGroupDataVertical();
        getHanjaList(dicHanjaRadical);
//        callAsyncTask(TYPE_REFRESH_VOCA, dicHanjaRadical);
    }
    private void bindGroupDataVertical() {
        if ((selectedStroke == null) && (!Utils.isEmpty(groupListLandscape))) {
            selectedStroke = Long.valueOf(groupListLandscape.get(0));
        }

        int selectedPositionVertical = getSelectedPostionVertical();
        groupAdapterVertical.setData(groupMap, selectedStroke, selectedPositionVertical);
        groupAdapterVertical.notifyDataSetChanged();

        if (groupMap.containsKey(selectedStroke)) {
            List<DIC_HANJA> dicHanjaList = groupMap.get(selectedStroke);
            if (!Utils.isEmpty(dicHanjaList)) {
                dicHanjaRadical = dicHanjaList.get(selectedPositionVertical);
                getHanjaList(dicHanjaRadical);
            }
        }
    }

    private int getSelectedPostionVertical() {
        int selectedPosition = 0;
        if (hanjaGroupTypeModel.getHanja() != null) {
            boolean isFound = false;
            DIC_HANJA dicHanja = hanjaGroupTypeModel.getHanja();
            List<DIC_HANJA> dicHanjaList = groupMap.get(selectedStroke);
            for (DIC_HANJA dicHanjaInMap : dicHanjaList) {
                if (Voca.isSameVoca(dicHanja, dicHanjaInMap)) {
                    isFound = true;
                    break;
                }
                selectedPosition++;
            }
            if (!isFound)
                selectedPosition = 0;
        }
        return selectedPosition;
    }

    private void bindGroupDataLandscape() {
//        bindGroupData();

        groupAdapterLandscape.setData(groupListLandscape, getSelectedPostionLandscape());
        groupAdapterLandscape.notifyDataSetChanged();
//        Loading.hide();
    }

    private int getSelectedPostionLandscape() {
        int selectedPosition = 0;
        if (hanjaGroupTypeModel.getHanja() != null) {
            DIC_HANJA dicHanja = hanjaGroupTypeModel.getHanja();
            for (String str : groupListLandscape) {
                if (str.equals(dicHanja.getSTROKES().toString())) {
                    break;
                }
                selectedPosition++;
            }
        }
        selectedStroke = Long.valueOf(selectedPosition);
        return selectedPosition;
    }
    private final OnClickListener onGroupClickListener = (view, object) -> {
        if (object instanceof DIC_HANJA) {
            dicHanjaRadical = (DIC_HANJA) object;
            if (hanjaMap.containsKey(object)) {
                previousShowDifficultStatus = PREVIOUS_SHOW_DIFFICULT_STATUS_ALL;
                if (hanjaGroupTypeModel.getHanja() != null) {
                    hanjaWordGridAdapter.setHanjaItemToHighlight(hanjaGroupTypeModel.getHanja());
                }
                bindFoundHanjaListInRvInfoRecyclerView(hanjaMap.get(dicHanjaRadical), false,false);
                scrollToTopPositionInSearchRecyclerView();
                updateUI(dicHanjaRadical);
            } else {
                getHanjaList(dicHanjaRadical);
            }
        }
    };

    protected void updateUI(DIC_HANJA dicHanja) {
        if (dicHanja == null)
            return;

        super.updateUI(dicHanja);

//        groupBinding.llRadical.setVisibility(View.VISIBLE);
        binding.tvVoca.setVisibility(View.VISIBLE);

        binding.icBookmark.setImageResource(dicHanja.getHI_BOOKMARK() != null && dicHanja.getHI_BOOKMARK() == 1
                ? R.drawable.ic_favorite_new_on
                : R.drawable.ic_favorite_new_off);

//        VocaKnow.updateIconVocaKnow(context,
//                groupBinding.tvKnow,
//                dicHanja.getHI_VOCA_KNOW() == null ? Constant.VOCA_KNOW.VOCA_KNOW_NOTRATED : dicHanja.getHI_VOCA_KNOW().intValue());

        binding.tvVoca.setText(dicHanja.getHI_VOCA_WITH_VOCAORI());
        binding.tvMeaningPronounce.setText(Html.fromHtml(Voca.getColoredPronounceWithMeaning(dicHanja, context)));
//        groupBinding.tvDetailInfo.setText(dicHanja.getMEANING_KO_DETAILED()); // Don't need to show detail info here. Just click Voca to show it in Word View.

    }

    private void getHanjaList(DIC_HANJA hanja) {
        Loading.showDelay(context, 1000L);

        BaseVoca.executeRealmTransactionAsync(realm -> {
//            String[] s1 = {Constant.REALMDB.KEY_STROKES, Constant.REALMDB.KEY_PRONOUNCE1_FIRST, Constant.REALMDB.KEY_MEANING1};
//            Sort[] s2 = {Sort.ASCENDING, Sort.ASCENDING, Sort.ASCENDING};
            List<DIC_HANJA> hanjas = realm.copyFromRealm(
                    realm.where(DIC_HANJA.class)
                            .equalTo(Constant.REALMDB.KEY_RADICAL, hanja.getVOCA())
//                            .sort(s1, s2)
                            .findAll()
            );

            List<DIC_HANJA> hanjasAfter = Voca.prepareHanjaListToDisplay(realm, hanjas, hanjaGroupTypeModel.getHanja());
            Voca.showToastIfNoHanja(this, hanjas.size(), hanjasAfter.size());
//            hanjas = Voca.prepareHanjaListToDisplay(realm, hanjas, hanjaGroupTypeModel.getHanja());
            hanjaMap.put(hanja, new ArrayList<>(hanjasAfter));
        }, () -> {
            previousShowDifficultStatus = PREVIOUS_SHOW_DIFFICULT_STATUS_ALL;
            if (hanjaGroupTypeModel.getHanja() != null) {
                hanjaWordGridAdapter.setHanjaItemToHighlight(hanjaGroupTypeModel.getHanja());
            }
            bindFoundHanjaListInRvInfoRecyclerView(hanjaMap.get(hanja), false,false);
//            scrollToTopPositionInSearchRecyclerView(); //TODO : Need this?
            updateUI(hanja);

            Loading.hide();
        }, error -> {
            error.printStackTrace();

            Loading.hide();
        });
    }
    protected void initOnClickListener() {
        super.initOnClickListener();
        binding.tvVoca.setOnClickListener(this);
        binding.tvMeaningPronounce.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.tvVoca:
            case R.id.tv_meaning_pronounce:
                openHanjaInfoView(dicHanjaRadical);
//                openHanjaWordInfoActivity(dicHanjaRadical);
                break;
//            case R.id.iv_show_word_list:
//                bindFoundHanjaListInRvInfoRecyclerView(hanjaMap.get(dicHanjaRadical), true,true);
//                updateUITextShowWordList(false);
//                break;
        }
    }
//    @OnClick({
//            R.id.tvVoca, R.id.tv_meaning_pronounce
//    })
//    void onClick(View view) {
//        super.onClick(view);
//        switch (view.getId()) {
//            case R.id.tvVoca:
//            case R.id.tv_meaning_pronounce:
//                openHanjaInfoView(dicHanjaRadical);
////                openHanjaWordInfoActivity(dicHanjaRadical);
//                break;
////            case R.id.iv_show_word_list:
////                bindFoundHanjaListInRvInfoRecyclerView(hanjaMap.get(dicHanjaRadical), true,true);
////                updateUITextShowWordList(false);
////                break;
//        }
//    }

    @Subscribe
    public void onEvent(SuccessEvent successEvent) {
        if (successEvent.getScreen() == BaseEvent.Screen.MAIN) {
            BaseEvent.EventType type = successEvent.getEventType();
            switch (type) {
                case VOCA_KNOW_CHANGED:
                case BOOKMARK_CHANGED:
                    callAsyncTask(TYPE_REFRESH_VOCA, (HanjaItem) successEvent.getModel());
                    break;
                case MULTIPLE_VOCA_KNOW_CHANGED:
                    getHanjaList(dicHanjaRadical); //Reload all data
                    break;
            }
        }
    }

    private void callAsyncTask(int type, HanjaItem hanjaItem) {
        new CustomAsyncTask(this, this, hanjaItem, type, true).execute();
    }

    @Override
    public void onInitAsyncTask() {
//        Loading.show(this);
    }

    @Override
    public void onPrevExecuteAsyncTask() {

    }

    @Override
    public Object onPostExecuteAsyncTask(int searchType, Object data) {
        switch (searchType) {
            case TYPE_REFRESH_VOCA:
                return updateHanjaList(hanjaMap.get(dicHanjaRadical), (HanjaItem) data); //refresh only dicHanja in the list.
        }
        return null;
    }

    @Override
    public void onFinishAsyncTask(int searchType, Object resultData, Object data) {
        switch (searchType) {
            case TYPE_REFRESH_VOCA:
                bindFoundHanjaListInRvInfoRecyclerView((List<HanjaItem>)resultData, true,false);
                updateUI(dicHanjaRadical);
                break;
        }

        Loading.hide();
    }

    protected Object getHanjaMapKey() {
        return dicHanjaRadical;
    }
}
