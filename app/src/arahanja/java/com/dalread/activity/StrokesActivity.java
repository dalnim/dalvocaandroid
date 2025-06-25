package com.dalread.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dalread.R;
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
import com.dalread.util.DLog;
import com.dalread.util.LanguageUtilKorean;
import com.dalread.util.Loading;
import com.dalread.util.Utils;
import com.dalread.util.Voca;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.realm.RealmResults;

public class StrokesActivity extends BaseGroupedHanjaActivity implements OnAsyncTaskListener {
    private StrokeGroupAdapter groupAdapterVertical;
    private StrokeGroupAdapter groupAdapterLandscape;
    private List<String> groupListVertical;
    private List<String> groupListAllForStringColumnType; //groupListVertical is filterd(exclude if it has no Hanja data) data from groupListVerticalAll (getDataStringColumnType)
    private List<String> groupListLandscape;
    private String groupData;
    private HanjaGroupType hanjaGroupType;
    private HanjaGroupTypeModel hanjaGroupTypeModel;
    private String columnName;
    private String viewTitle;
    final private int minValueForColumn = 0;
    final private int maxValueForColumn = 999;
    private boolean isFirstAd = true;

    public static Intent createIntent(Context context, HanjaGroupTypeModel hanjaGroupTypeModel) {
        Intent intent = new Intent(context, StrokesActivity.class);
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
        groupAdapterVertical = new StrokeGroupAdapter(this, onGroupClickListener);

        groupListVertical = new ArrayList<>();
        groupListAllForStringColumnType = new ArrayList<>();
        groupListLandscape = new ArrayList<>();
        setVariableValueForGroupViewType(hanjaGroupType);
    }

    private void setVariableValueForGroupViewType(HanjaGroupType hanjaGroupType) {
        if (hanjaGroupType == HanjaGroupType.STROKES) {
            columnName = Constant.REALMDB.KEY_STROKES;
            viewTitle = getString(R.string.strokes);

        } else if (hanjaGroupType == HanjaGroupType.VOCA_LEVEL) {
            columnName = Constant.REALMDB.KEY_VOCA_LEVEL;
            viewTitle = getString(R.string.group_hanja_by_level);
        } else if (hanjaGroupType == HanjaGroupType.COMMON_USE_JAPAN) {
            columnName = Constant.REALMDB.KEY_COMMON_USE_JAPAN;
            viewTitle = getString(R.string.group_hanja_by_common_japan);
        } else if (hanjaGroupType == HanjaGroupType.COMMON_HSK) {
            columnName = Constant.REALMDB.KEY_LEVEL_HSK;
            viewTitle = getString(R.string.group_hanja_by_hsk);
        } else { //if (hanjaGroupType == HanjaGroupType.HANGUL) {
            columnName = Constant.REALMDB.KEY_PRONOUNCE;
            if (hanjaGroupType == HanjaGroupType.HANGUL) {
                viewTitle = getString(R.string.group_hanja_by_hangul_alphabet);
            } else if (hanjaGroupType == HanjaGroupType.COMMON_MIDDLE_SCHOOL) {
                binding.rvGroupVertical.setVisibility(View.GONE);
                viewTitle = getString(R.string.title_common_hanja_middle_school);
            } else if (hanjaGroupType == HanjaGroupType.COMMON_HIGH_SCHOOL) {
                binding.rvGroupVertical.setVisibility(View.GONE);
                viewTitle = getString(R.string.title_common_hanja_high_school);
            }

            groupAdapterLandscape = new StrokeGroupAdapter(this, onGroupClickListenerLandscape);
            binding.rvGroupLandscape.setAdapter(groupAdapterLandscape);
            binding.rvGroupLandscape.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
            binding.rvGroupLandscape.addItemDecoration(BaseBindUtils.getSeparatorVerticalDecoration(context));
//            groupBinding.rvGroupLandscape.addItemDecoration(new SeparatorDecoration(context, clDivider, dividerHeight));
            binding.rvGroupLandscape.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void initLayout() {
        super.initLayout();

        if (getToolbar() != null) {
            getToolbar().setTitle(viewTitle);
        }
        binding.llRadical.setVisibility(View.GONE);
    }

    @Override
    protected void getData() {
        groupListVertical.clear();

        Loading.showDelay(context, 1000L);

        if ((hanjaGroupType == HanjaGroupType.STROKES)
            || (hanjaGroupType == HanjaGroupType.VOCA_LEVEL)) {
            getDataIntColumnType();
        } else {// if (hanjaGroupType == HanjaGroupType.HANGUL) {
            groupListAllForStringColumnType.clear();
            if (hanjaGroupType == HanjaGroupType.HANGUL) {
                groupListLandscape = Arrays.asList("ㄱ","ㄴ","ㄷ","ㄹ","ㅁ","ㅂ","ㅅ","ㅇ","ㅈ","ㅊ","ㅋ","ㅌ","ㅍ","ㅎ","ㄲ","ㅆ");
                getDataStringColumnType();
            } else if (hanjaGroupType == HanjaGroupType.COMMON_MIDDLE_SCHOOL) {
                groupListLandscape = Arrays.asList("ㄱ","ㄴ","ㄷ","ㄹ","ㅁ","ㅂ","ㅅ","ㅇ","ㅈ","ㅊ","ㅋ","ㅌ","ㅍ","ㅎ","ㅆ");
                getHanjaListFromBookID(Constant.HANJA.WORKBOOK_ID.commonMiddleSchool);
            } else if (hanjaGroupType == HanjaGroupType.COMMON_HIGH_SCHOOL) {
                groupListLandscape = Arrays.asList("ㄱ","ㄴ","ㄷ","ㄹ","ㅁ","ㅂ","ㅅ","ㅇ","ㅈ","ㅊ","ㅋ","ㅌ","ㅍ","ㅎ","ㅆ");
                getHanjaListFromBookID(Constant.HANJA.WORKBOOK_ID.commonHighSchool);
            } else if (hanjaGroupType == HanjaGroupType.COMMON_USE_JAPAN) {
                getDataStringColumnType();
            } else if (hanjaGroupType == HanjaGroupType.COMMON_HSK) {
                getDataStringColumnType();
            }
        }
    }

    private void getDataIntColumnType() {
        BaseVoca.executeRealmTransactionAsync(realm -> {
            RealmResults<DIC_HANJA> dicHanjaRealmResults = realm.where(DIC_HANJA.class)
                    .greaterThan(columnName, minValueForColumn)
                    .lessThan(columnName, maxValueForColumn)
                    .sort(columnName)
                    .distinct(columnName)
                    .findAll();
            for (DIC_HANJA hanja : dicHanjaRealmResults) {
                if (hanjaGroupType == HanjaGroupType.STROKES) {
                    groupListVertical.add(hanja.getSTROKES().toString());
                } else if (hanjaGroupType == HanjaGroupType.VOCA_LEVEL) {
                    groupListVertical.add(hanja.getVOCA_LEVEL().toString());
                }
            }
        }, this::bindGroupDataVertical, error -> {
            error.printStackTrace();

            Loading.hide();
        });
    }

    private void getDataStringColumnType() {
        BaseVoca.executeRealmTransactionAsync(realm -> {
            RealmResults<DIC_HANJA> dicHanjaRealmResults = realm.where(DIC_HANJA.class)
                    .sort(columnName)
                    .distinct(columnName)
                    .findAll();
            for (DIC_HANJA hanja : dicHanjaRealmResults) {
                if (hanjaGroupType == HanjaGroupType.HANGUL) {
                    String pronounce = hanja.getPRONOUNCE();
                    if (pronounce.length() == 1) {
                        groupListAllForStringColumnType.add(hanja.getPRONOUNCE());
                        groupListVertical.add(hanja.getPRONOUNCE());
                    }
                } else if ((hanjaGroupType == HanjaGroupType.COMMON_HSK) && (!Utils.isEmpty(hanja.getLEVEL_HSK()))) {
                    groupListVertical.add(hanja.getLEVEL_HSK());
                } else if ((hanjaGroupType == HanjaGroupType.COMMON_USE_JAPAN) && (!Utils.isEmpty(hanja.getCOMMON_USE_JAPAN()))) {
                    groupListVertical.add(hanja.getCOMMON_USE_JAPAN());
                }
            }
        }, this::bindGroupDataLandscape, error -> {
            error.printStackTrace();

            Loading.hide();
        });
    }

    private void getHanjaListFromBookID(int bookId) {
        List<VOCABOOK_HANJA> vocabookHanjaList = Voca.getVocabookHanjaListByID(bookId);
        List<HanjaItem> hanjaItemList = Voca.getSortedHanjaItemsByDispOrder(vocabookHanjaList);
        Map<String, String> mapToCheckDuplicate = new HashMap<>();
        for (HanjaItem hanjaItem : hanjaItemList) {
            DLog.e("getHanjaListFromBookID", hanjaItem.getHI_ID() + " " + hanjaItem.getHI_VOCA() + " " + hanjaItem.getHI_PRONOUNCE1_FIRST());
            String pronounce = hanjaItem.getHI_PRONOUNCE1_FIRST();
            if ((pronounce.length() == 1) && (!mapToCheckDuplicate.containsKey(pronounce))) {
                groupListAllForStringColumnType.add(pronounce);
                groupListVertical.add(pronounce);
                mapToCheckDuplicate.put(pronounce, "");
            }
        }
        bindGroupDataLandscape();
        Loading.hide();
    }

    private void bindGroupDataVertical() {
        int selectedPosition = getSelectedVerticalPostion();
        groupAdapterVertical.setData(groupListVertical, selectedPosition);
        groupAdapterVertical.notifyDataSetChanged();
        binding.rvGroupVertical.getLayoutManager().scrollToPosition(selectedPosition);
    }

    private int getSelectedVerticalPostion() {
        int selectedPosition = 0;
        if (hanjaGroupTypeModel.getHanja() != null) {
            DIC_HANJA dicHanja = hanjaGroupTypeModel.getHanja();
            for (String str : groupListVertical) {
                if (hanjaGroupTypeModel.getHanjaGroupType() == HanjaGroupType.VOCA_LEVEL) {
                    if (str.equals(dicHanja.getVOCA_LEVEL().toString())) {
                        break;
                    }
                } else if (hanjaGroupTypeModel.getHanjaGroupType() == HanjaGroupType.STROKES) {
                    if (str.equals(dicHanja.getSTROKES().toString())) {
                        break;
                    }
                } else if (hanjaGroupTypeModel.getHanjaGroupType() == HanjaGroupType.COMMON_USE_JAPAN) {
                    if (str.equals(dicHanja.getCOMMON_USE_JAPAN())) {
                        break;
                    }
                } else if (hanjaGroupTypeModel.getHanjaGroupType() == HanjaGroupType.COMMON_HSK) {
                    if (str.equals(dicHanja.getLEVEL_HSK())) {
                        break;
                    }
                }
                selectedPosition++;
            }
        }
        if ((selectedPosition == groupListVertical.size()) && (!Utils.isEmpty(groupListVertical))) {
            selectedPosition--;
        }
        return selectedPosition;
    }

    private int getSelectedLandscapePostion() {
        int selectedPosition = 0;
        if (hasLandScapeGroup()) {
            if (hanjaGroupTypeModel.getHanja() != null) {
                DIC_HANJA dicHanja = hanjaGroupTypeModel.getHanja();
                String pronounce = dicHanja.getPRONOUNCE1_FIRST();
                String strChoSungFromOneCharacter = LanguageUtilKorean.getChosungFromOneCharacter(pronounce);
                for (String str : groupListLandscape) {
                    if (str.equals(strChoSungFromOneCharacter)) {
                        break;
                    }
                    selectedPosition++;
                }
            }
            if ((selectedPosition == groupListLandscape.size()) && (!Utils.isEmpty(groupListLandscape))) {
                selectedPosition--;
            }
        }
        return selectedPosition;
    }

    private boolean hasLandScapeGroup() {
        if (((hanjaGroupTypeModel.getHanjaGroupType() == HanjaGroupType.VOCA_LEVEL)
                || (hanjaGroupTypeModel.getHanjaGroupType() == HanjaGroupType.STROKES)
                || (hanjaGroupTypeModel.getHanjaGroupType() == HanjaGroupType.COMMON_USE_JAPAN)
                || (hanjaGroupTypeModel.getHanjaGroupType() == HanjaGroupType.COMMON_HSK))) {
            return false;
        }
        return true;
    }
    private void bindGroupDataLandscape() {
        bindGroupDataVertical();
        if (hasLandScapeGroup()) {
            int selectedPosition = getSelectedLandscapePostion();
            groupAdapterLandscape.setData(groupListLandscape, selectedPosition);
            groupAdapterLandscape.notifyDataSetChanged();
            binding.rvGroupLandscape.getLayoutManager().scrollToPosition(selectedPosition);
        }
    }

    private final OnClickListener onGroupClickListener = (view, object) -> {
        groupData = (String) object;
        if (hanjaMap.containsKey(groupData)) {
            previousShowDifficultStatus = PREVIOUS_SHOW_DIFFICULT_STATUS_ALL;
            bindFoundHanjaListInRvInfoRecyclerView(hanjaMap.get(groupData), false,false);
            updateUI(groupData);
            scrollToTopPositionInSearchRecyclerView();
        } else {
            refreshAllData();
            updateUI(groupData);
        }
    };


    private final OnClickListener onGroupClickListenerLandscape = (view, object) -> {
        groupData = (String) object;
        if (groupListLandscape.contains(groupData)) {
            groupListVertical = LanguageUtilKorean.speedHangleCheck(groupData, groupListAllForStringColumnType);
            bindGroupDataVertical();
            if (!Utils.isEmptyCollection(groupListVertical))
                binding.rvGroupVertical.getLayoutManager().scrollToPosition(0);
        }
    };

    protected void updateUI(Object hanjaMapKey) {
        super.updateUI(hanjaMapKey);

        if (((hanjaGroupTypeModel.getHanjaGroupType() == HanjaGroupType.COMMON_MIDDLE_SCHOOL)
                || (hanjaGroupTypeModel.getHanjaGroupType() == HanjaGroupType.COMMON_HIGH_SCHOOL))) {
            binding.tvMeaningPronounce.setText(viewTitle);
        } else {
            binding.tvMeaningPronounce.setText(viewTitle + " " + groupData);
        }
    }

    private void getHanjaListIntColumnType(String columnValue) {
        Loading.showDelay(context, 1000L);

        BaseVoca.executeRealmTransactionAsync(realm -> {
            List<DIC_HANJA> hanjas = realm.copyFromRealm(
                    realm.where(DIC_HANJA.class)
                            .equalTo(columnName, Integer.parseInt(columnValue))
                            .findAll()
            );

            List<DIC_HANJA> hanjasAfter = Voca.prepareHanjaListToDisplay(realm, hanjas, hanjaGroupTypeModel.getHanja());
            Voca.showToastIfNoHanja(this, hanjas.size(), hanjasAfter.size());
//            hanjas = Voca.prepareHanjaListToDisplay(realm, hanjas, hanjaGroupTypeModel.getHanja());

            hanjaMap.put(columnValue, new ArrayList<>(hanjasAfter));
        }, () -> {
            updateHanjaList(columnValue);
//            previousShowDifficultStatus = PREVIOUS_SHOW_DIFFICULT_STATUS_ALL;
//            if (hanjaGroupTypeModel.getHanja() != null) {
//                hanjaWordGridAdapter.setHanjaItemToHighlight(hanjaGroupTypeModel.getHanja());
//            }
//            bindFoundHanjaListInRvInfoRecyclerView(hanjaMap.get(columnValue), false, false);
//            updateUI(groupData);
//            scrollToTopPositionInSearchRecyclerView();
//            Loading.hide();
        }, error -> {
            error.printStackTrace();

            Loading.hide();
        });
    }

    private void getHanjaListStringColumnType(String columnValue) {
        Loading.showDelay(context, 1000L);

        BaseVoca.executeRealmTransactionAsync(realm -> {
            List<DIC_HANJA> hanjas = realm.copyFromRealm(
                    realm.where(DIC_HANJA.class)
                            .equalTo(columnName, columnValue)
                            .findAll()
            );

            List<DIC_HANJA> hanjasAfter = Voca.prepareHanjaListToDisplay(realm, hanjas, hanjaGroupTypeModel.getHanja());
            Voca.showToastIfNoHanja(this, hanjas.size(), hanjasAfter.size());

//            hanjas = Voca.prepareHanjaListToDisplay(realm, hanjas, hanjaGroupTypeModel.getHanja());

            hanjaMap.put(columnValue, new ArrayList<>(hanjasAfter));
        }, () -> {
            updateHanjaList(columnValue);
        }, error -> {
            error.printStackTrace();

            Loading.hide();
        });
    }

    private void getHanjaListCommonMiddleSchool(String columnValue, String commonUseValue) {
        Loading.showDelay(context, 1000L);

        BaseVoca.executeRealmTransactionAsync(realm -> {
            List<DIC_HANJA> hanjas = realm.copyFromRealm(
                    realm.where(DIC_HANJA.class)
                            .equalTo(Constant.REALMDB.KEY_COMMON_USE_KOREA, commonUseValue)
                            .findAll()
            );

            Map<String, String> mapHanjas = new HashMap<>();
            for(String str : groupListVertical) {
                if (!mapHanjas.containsKey(str)) {
                    mapHanjas.put(str, "");
                }
            }
            List<DIC_HANJA> hanjasTemp = new ArrayList<>();
            for(DIC_HANJA dicHanja : hanjas) {
                if (mapHanjas.containsKey(dicHanja.getPRONOUNCE1_FIRST())) {
                    hanjasTemp.add(dicHanja);
                }
            }
            List<DIC_HANJA> hanjasAfter = Voca.prepareHanjaListToDisplay(realm, hanjasTemp, hanjaGroupTypeModel.getHanja());
            Voca.showToastIfNoHanja(this, hanjasTemp.size(), hanjasAfter.size());

            Collections.sort(hanjasAfter, Comparator.nullsLast(Comparator.comparing(HanjaItem::getHI_PRONOUNCE1_FIRST)
                    .thenComparing(HanjaItem::getHI_MEANING1)
                    .thenComparing(HanjaItem::getHI_VOCA_LEVEL)));
            hanjaMap.put(columnValue, new ArrayList<>(hanjasAfter));
        }, () -> {
            updateHanjaList(columnValue);
        }, error -> {
            error.printStackTrace();

            Loading.hide();
        });
    }



    private void updateHanjaList(String columnValue) {
        previousShowDifficultStatus = PREVIOUS_SHOW_DIFFICULT_STATUS_ALL;
        if (hanjaGroupTypeModel.getHanja() != null) {
            hanjaWordGridAdapter.setHanjaItemToHighlight(hanjaGroupTypeModel.getHanja());
        }
        bindFoundHanjaListInRvInfoRecyclerView(hanjaMap.get(columnValue), false,false);
        binding.rvInfo.post(() -> {
            RecyclerView.LayoutManager layoutManager = binding.rvInfo.getLayoutManager();
            if (layoutManager instanceof GridLayoutManager) {
                if (((GridLayoutManager) layoutManager).findLastVisibleItemPosition() == hanjaWordGridAdapter.getItemCount() - 1) {
                    binding.fssvPreview.fullScroll(View.FOCUS_DOWN);
                }
            }
        });

        updateUI(groupData);
        scrollToTopPositionInSearchRecyclerView();
        Loading.hide();
    }

    @Subscribe
    public void onEvent(SuccessEvent successEvent) {
        if (successEvent.getScreen() == BaseEvent.Screen.MAIN) {
            BaseEvent.EventType type = successEvent.getEventType();
            switch (type) {
                case VOCA_KNOW_CHANGED:
                case BOOKMARK_CHANGED:
                    DIC_HANJA dicHanja= (DIC_HANJA) successEvent.getModel();
                    callAsyncTask(TYPE_REFRESH_VOCA, dicHanja);
                    break;
                case MULTIPLE_VOCA_KNOW_CHANGED:
                    refreshAllData();
                    break;
                case ON_ADS_LOADED:
                    if (isFirstAd
                            && (hanjaWordGridAdapter.getHanjaItemToHighlight() != null)
                            && (isHighlightHanjaExistInTheLast(hanjaMap.get(groupData)))) {
                        RecyclerView.LayoutManager layoutManager = binding.rvInfo.getLayoutManager();
                        if (layoutManager instanceof GridLayoutManager) {
                            int lastVisibleItemPositionBeforeAdsLoaded = ((GridLayoutManager) layoutManager).findLastVisibleItemPosition();
                            if (lastVisibleItemPositionBeforeAdsLoaded > 0) {
                                binding.rvInfo.post(() -> {
                                    binding.rvInfo.scrollToPosition(lastVisibleItemPositionBeforeAdsLoaded);
                                    if (lastVisibleItemPositionBeforeAdsLoaded == hanjaWordGridAdapter.getItemCount() - 1) {
                                        binding.fssvPreview.post(() -> binding.fssvPreview.fullScroll(View.FOCUS_DOWN));
                                    }
                                });
                            }
                        }
                        isFirstAd = false;
                    }
                    break;
            }
        }
    }

    private void callAsyncTask(int type, DIC_HANJA dicHanja) {
        new CustomAsyncTask(this, this, dicHanja, type, true).execute();
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
                return updateHanjaList(hanjaMap.get(groupData), (HanjaItem) data);

        }
        return null;
    }

    @Override
    public void onFinishAsyncTask(int searchType, Object resultData, Object data) {

        switch (searchType) {
            case TYPE_REFRESH_VOCA:
                bindFoundHanjaListInRvInfoRecyclerView((List<HanjaItem>) resultData, true,false);
                updateUI(groupData);
                break;
        }

        Loading.hide();
    }

    protected Object getHanjaMapKey() {
        return groupData;
    }

    @Override
    protected void showSimplifiedChineseBeyondLevelHanja() {
        super.showSimplifiedChineseBeyondLevelHanja();
        refreshAllData();
    }

    private void refreshAllData() {
        if ((hanjaGroupType == HanjaGroupType.STROKES)
                || (hanjaGroupType == HanjaGroupType.VOCA_LEVEL)) {
            getHanjaListIntColumnType(groupData);
        } else if ((hanjaGroupType == HanjaGroupType.HANGUL)
                || (hanjaGroupType == HanjaGroupType.COMMON_USE_JAPAN)
                || (hanjaGroupType == HanjaGroupType.COMMON_HSK)) {
            getHanjaListStringColumnType(groupData);
        } else if (hanjaGroupType == HanjaGroupType.COMMON_MIDDLE_SCHOOL) {
            getHanjaListCommonMiddleSchool(groupData, Constant.HANJA.COMMON_USE.commonMiddleSchool);
        } else if (hanjaGroupType == HanjaGroupType.COMMON_HIGH_SCHOOL) {
            getHanjaListCommonMiddleSchool(groupData, Constant.HANJA.COMMON_USE.commonHighSchool);
        }
    }
}
