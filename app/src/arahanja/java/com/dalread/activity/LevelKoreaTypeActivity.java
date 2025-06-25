package com.dalread.activity;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.dalread.adapter.StrokeGroupAdapter;
import com.dalread.asyntask.CustomAsyncTask;
import com.dalread.asyntask.OnAsyncTaskListener;
import com.dalread.component.Toolbar;
import com.dalread.listener.OnClickListener;
import com.dalread.model.DIC_HANJA;
import com.dalread.model.HanjaGroupType;
import com.dalread.model.HanjaGroupTypeModel;
import com.dalread.model.HanjaItem;
import com.dalread.model.VOCABOOKS_HANJA;
import com.dalread.network.events.BaseEvent;
import com.dalread.network.events.SuccessEvent;
import com.dalread.util.BaseVoca;
import com.dalread.util.Constant;
import com.dalread.util.Loading;
import com.dalread.util.UserUtil;
import com.dalread.util.Voca;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import io.realm.RealmResults;

public class LevelKoreaTypeActivity extends BaseGroupedHanjaActivity implements OnAsyncTaskListener {
    private StrokeGroupAdapter groupAdapter;
    private List<String> groupListVertical;
    private String groupData;
    private HanjaGroupType hanjaGroupType;
    private HanjaGroupTypeModel hanjaGroupTypeModel;
    private String columnName;
    private String viewTitle;
    private Long bookId;

    @Override
    protected RecyclerView getRvSearch() {
        return binding.rvSearch;
    }

    public static Intent createIntent(Context context, HanjaGroupTypeModel hanjaGroupTypeModel, long bookId, String workbookName) {
        Intent intent = new Intent(context, LevelKoreaTypeActivity.class);
        intent.putExtra(Constant.BUNDLE.KEY_HANJA_GROUP_TYPE_MODEL, hanjaGroupTypeModel);
        intent.putExtra(Constant.BUNDLE.KEY_VOCA_BOOK_ID, bookId);
        intent.putExtra(Constant.BUNDLE.KEY_VOCA_BOOK_NAME, workbookName);
        return intent;
    }

    @Override
    protected RecyclerView.Adapter<RecyclerView.ViewHolder> getGroupAdapterVertical() {
        return groupAdapter;
    }

    @Override
    protected void initData() {
        super.initData();

        hanjaGroupTypeModel = getIntent().getParcelableExtra(Constant.BUNDLE.KEY_HANJA_GROUP_TYPE_MODEL);
        hanjaGroupType = hanjaGroupTypeModel.getHanjaGroupType();
        setVariableValueForGroupViewType(hanjaGroupType);

        groupAdapter = new StrokeGroupAdapter(this, onGroupClickListener);
        groupListVertical = new ArrayList<>();
    }

    private void setVariableValueForGroupViewType(HanjaGroupType hanjaGroupType) {
        bookId = getIntent().getLongExtra(Constant.BUNDLE.KEY_VOCA_BOOK_ID, 0);
        viewTitle = getIntent().getStringExtra(Constant.BUNDLE.KEY_VOCA_BOOK_NAME).equals("") ? getBookTitle(bookId) : getIntent().getStringExtra(Constant.BUNDLE.KEY_VOCA_BOOK_NAME);

        if (hanjaGroupType == HanjaGroupType.LEVEL_KOREA_TYPE_1) {
            columnName = Constant.REALMDB.KEY_LEVEL_KOREA_TYPE_1;
        } else if (hanjaGroupType == HanjaGroupType.LEVEL_KOREA_TYPE_2) {
            columnName = Constant.REALMDB.KEY_LEVEL_KOREA_TYPE_2;
        } else if (hanjaGroupType == HanjaGroupType.LEVEL_KOREA_TYPE_3) {
            columnName = Constant.REALMDB.KEY_LEVEL_KOREA_TYPE_3;
        }
    }

    private String getBookTitle(Long bookId) {
        return Voca.getVocabooksHanjaNameByBookId(bookId, this);
    }

    @Override
    protected void initLayout() {
        super.initLayout();

        if (getToolbar() != null) {
            getToolbar().setTitle(viewTitle);
        }
//        groupAdapter.setListener(onGroupClickListener);
        binding.llRadical.setVisibility(View.GONE);
    }

    @Override
    protected void getData() {
        groupListVertical.clear();

        Loading.showDelay(context, 1000L);
        //Get sorted list by LevelKoreaType
        List<VOCABOOKS_HANJA> vocabooksHanjaList = Voca.getSubWorkbookListFromBookID(bookId, UserUtil.getVocabooksUsedByUserType(this));

        BaseVoca.executeRealmTransactionAsync(realm -> {
            RealmResults<DIC_HANJA> dicHanjaRealmResults = realm.where(DIC_HANJA.class)
                    .notEqualTo(columnName, "")
                    .distinct(columnName)
                    .findAll();

            for (VOCABOOKS_HANJA vocabooksHanja : vocabooksHanjaList) {
                String nameKoInVocabook = vocabooksHanja.getNAME_KO();
                for (DIC_HANJA hanja : dicHanjaRealmResults) {
                    String nameKo = hanja.getLEVEL_KOREA_TYPE_1OrEmptyString();
                    if (hanjaGroupType == hanjaGroupType.LEVEL_KOREA_TYPE_2) {
                        nameKo = hanja.getLEVEL_KOREA_TYPE_2OrEmptyString();
                    } else if (hanjaGroupType == hanjaGroupType.LEVEL_KOREA_TYPE_3) {
                        nameKo = hanja.getLEVEL_KOREA_TYPE_3OrEmptyString();
                    }

                    if (nameKoInVocabook.equals(nameKo)) {
                        groupListVertical.add(nameKo);
                        break;
                    }
                }
            }
        }, this::bindGroupData, error -> {
            error.printStackTrace();

            Loading.hide();
        });


    }

    private void bindGroupData() {
        groupAdapter.setData(groupListVertical, getSelectedPostion());
        groupAdapter.notifyDataSetChanged();
    }

    private int getSelectedPostion() {
        int selectedPosition = 0;
        if (hanjaGroupTypeModel.getHanja() != null) {
            DIC_HANJA dicHanja = hanjaGroupTypeModel.getHanja();
            for (String str : groupListVertical) {
                if (hanjaGroupTypeModel.getHanjaGroupType() == HanjaGroupType.LEVEL_KOREA_TYPE_1) {
                    if (str.equals(dicHanja.getLEVEL_KOREA_TYPE_1OrEmptyString())) {
                        break;
                    }
                } else if (hanjaGroupTypeModel.getHanjaGroupType() == HanjaGroupType.LEVEL_KOREA_TYPE_2) {
                    if (str.equals(dicHanja.getLEVEL_KOREA_TYPE_2OrEmptyString())) {
                        break;
                    }
                } else if (hanjaGroupTypeModel.getHanjaGroupType() == HanjaGroupType.LEVEL_KOREA_TYPE_3) {
                    if (str.equals(dicHanja.getLEVEL_KOREA_TYPE_3OrEmptyString())) {
                        break;
                    }
                }
                selectedPosition++;
            }
        }
        return selectedPosition;
    }

    private final OnClickListener onGroupClickListener = (view, object) -> {
        groupData = (String) object;
        if (hanjaMap.containsKey(groupData)) {
            previousShowDifficultStatus = PREVIOUS_SHOW_DIFFICULT_STATUS_ALL;
            if (hanjaGroupTypeModel.getHanja() != null) {
                hanjaWordGridAdapter.setHanjaItemToHighlight(hanjaGroupTypeModel.getHanja());
            }
            bindFoundHanjaListInRvInfoRecyclerView(hanjaMap.get(groupData), false,false);
            updateUI(groupData);
            scrollToTopPositionInSearchRecyclerView();
        } else {
            getHanjaList(groupData);
            updateUI(groupData);
        }
    };


    protected void updateUI(Object hanjaMapKey) {
        super.updateUI(hanjaMapKey);

        binding.tvMeaningPronounce.setText(groupData);
    }

    private void getHanjaList(String columnValue) {
        Loading.showDelay(context, 1000L);
        BaseVoca.executeRealmTransactionAsync(realm -> {
            List<DIC_HANJA> hanjas = realm.copyFromRealm(
                    realm.where(DIC_HANJA.class)
                            .equalTo(columnName, columnValue)
                            .findAll()
            );


            List<DIC_HANJA> hanjasAfter = Voca.prepareHanjaListToDisplay(realm, hanjas, hanjaGroupTypeModel.getHanja());
            Voca.showToastIfNoHanja(this, hanjas.size(), hanjasAfter.size());
            hanjaMap.put(columnValue, new ArrayList<>(hanjasAfter));
        }, () -> {
            previousShowDifficultStatus = PREVIOUS_SHOW_DIFFICULT_STATUS_ALL;
            if (hanjaGroupTypeModel.getHanja() != null) {
                hanjaWordGridAdapter.setHanjaItemToHighlight(hanjaGroupTypeModel.getHanja());
            }
            bindFoundHanjaListInRvInfoRecyclerView(hanjaMap.get(columnValue), false,false);
            updateUI(groupData);
            scrollToTopPositionInSearchRecyclerView();
            Loading.hide();
        }, error -> {
            error.printStackTrace();

            Loading.hide();
        });
    }

    @Override
    protected Toolbar getToolbar() {
        return binding.header;
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
                    getHanjaList(groupData);
                    updateUI(groupData);
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
                bindFoundHanjaListInRvInfoRecyclerView((List<HanjaItem>)resultData, true,false);
                updateUI(groupData);
                break;
        }

        Loading.hide();
    }

    protected Object getHanjaMapKey() {
        return groupData;
    }
}
