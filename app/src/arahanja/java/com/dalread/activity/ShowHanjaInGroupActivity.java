package com.dalread.activity;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.dalread.R;
import com.dalread.asyntask.CustomAsyncTask;
import com.dalread.asyntask.OnAsyncTaskListener;
import com.dalread.component.Toolbar;
import com.dalread.model.DIC_HANJA;
import com.dalread.model.HanjaGroupTypeModel;
import com.dalread.model.HanjaItem;
import com.dalread.network.events.BaseEvent;
import com.dalread.network.events.SuccessEvent;
import com.dalread.util.BaseVoca;
import com.dalread.util.Constant;
import com.dalread.util.Loading;
import com.dalread.util.Voca;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

public class ShowHanjaInGroupActivity extends BaseGroupedHanjaActivity implements OnAsyncTaskListener {
    private HanjaGroupTypeModel hanjaGroupTypeModel;
    private HanjaItem hanjaItem;

    public static Intent createIntent(Context context, HanjaGroupTypeModel hanjaGroupTypeModel) {
        Intent intent = new Intent(context, ShowHanjaInGroupActivity.class);
        intent.putExtra(Constant.BUNDLE.KEY_HANJA_GROUP_TYPE_MODEL, hanjaGroupTypeModel);
        return intent;
    }

    @Override
    protected RecyclerView getRvSearch() {
        return binding.rvSearch;
    }

    @Override
    protected RecyclerView.Adapter<RecyclerView.ViewHolder> getGroupAdapterVertical() {
        return null;
    }

    @Override
    protected void initData() {
        super.initData();

        hanjaGroupTypeModel = getIntent().getParcelableExtra(Constant.BUNDLE.KEY_HANJA_GROUP_TYPE_MODEL);
        hanjaItem = hanjaGroupTypeModel.getHanja();
    }

    @Override
    protected void initLayout() {
        super.initLayout();

        if (getToolbar() != null) {
            getToolbar().setTitle(hanjaItem.getHI_VOCA());
        }
        binding.llRadical.setVisibility(View.GONE);
        binding.rvGroupVertical.setVisibility(View.GONE);
        binding.rvGroupLandscape.setVisibility(View.GONE);

    }

    @Override
    protected void getData() {
        getDataMain();
    }

    @Override
    protected void showSimplifiedChineseBeyondLevelHanja() {
        super.showSimplifiedChineseBeyondLevelHanja();
        getDataMain();
    }

    private void getDataMain() {
        Loading.showDelay(context, 1000L);
        String voca = hanjaItem.getHI_VOCA();

        BaseVoca.executeRealmTransactionAsync(realm -> {
            List<DIC_HANJA> hanjas = realm.copyFromRealm(
                    realm.where(DIC_HANJA.class)
                            .equalTo(Constant.REALMDB.KEY_VOCA, voca)
                            .or()
                            .equalTo(Constant.REALMDB.KEY_VOCAORI, voca)
                            .or()
                            .equalTo(Constant.REALMDB.KEY_RADICAL, voca)
                            .or()
                            .equalTo(Constant.REALMDB.KEY_HANJA_KOREA, voca)
                            .or()
                            .equalTo(Constant.REALMDB.KEY_HANJA_JAPAN, voca)
                            .or()
                            .equalTo(Constant.REALMDB.KEY_HANJA_TAIWAN, voca)
                            .or()
                            .equalTo(Constant.REALMDB.KEY_HANJA_SIMPLIFIED, voca)
                            .or()
                            .equalTo(Constant.REALMDB.KEY_HANJA_SHORT_FORM, voca)
                            .or()
                            .equalTo(Constant.REALMDB.KEY_HANJA_SOKJA, voca)
                            .or()
                            .equalTo(Constant.REALMDB.KEY_HANJA_VARIANT_1, voca)
                            .or()
                            .equalTo(Constant.REALMDB.KEY_HANJA_VARIANT_2, voca)
                            .or()
                            .contains(Constant.REALMDB.KEY_LEFTCOMPONENT, voca)
                            .or()
                            .contains(Constant.REALMDB.KEY_RIGHTCOMPONENT, voca)
                            .findAll()
            );


            List<DIC_HANJA> hanjasAfter = Voca.prepareHanjaListToDisplay(realm, hanjas, hanjaGroupTypeModel.getHanja());
            Voca.showToastIfNoHanja(this, hanjas.size(), hanjasAfter.size());

            List<HanjaItem> hanjaItemList = new ArrayList<>();
            for (DIC_HANJA dicHanja : hanjasAfter) {
                hanjaItemList.add(dicHanja);
            }
            hanjaMap.put(hanjaItem, hanjaItemList);
        }, this::bindData, error -> {
            error.printStackTrace();

            Loading.hide();
        });
    }

    private void bindData() {
        hanjaWordGridAdapter.setHanjaItemToHighlight(hanjaItem);
        bindFoundHanjaListInRvInfoRecyclerView(hanjaMap.get(hanjaItem), false, false);
        updateUI(hanjaItem);
        Loading.hide();
    }

    protected void updateUI(Object hanjaMapKey) {
        super.updateUI(hanjaMapKey);

        binding.tvMeaningPronounce.setText(getString(R.string.hanja_list_that_contain_this_hanja, hanjaItem.getHI_VOCA()));
    }

    @Override
    protected Toolbar getToolbar() {
        return binding.header;
    }

    @Subscribe
    public void onEvent(SuccessEvent successEvent) {
        if (successEvent.getScreen() == BaseEvent.Screen.MAIN) {
            BaseEvent.EventType type = successEvent.getEventType();
            if (type == BaseEvent.EventType.VOCA_KNOW_CHANGED) {
                DIC_HANJA dicHanja= (DIC_HANJA) successEvent.getModel();
                callAsyncTask(TYPE_REFRESH_VOCA, dicHanja);
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
                return updateHanjaList(hanjaMap.get(hanjaItem), (HanjaItem) data);

        }
        return null;
    }

    @Override
    public void onFinishAsyncTask(int searchType, Object resultData, Object data) {
        switch (searchType) {
            case TYPE_REFRESH_VOCA:
                bindFoundHanjaListInRvInfoRecyclerView((List<HanjaItem>)resultData, true,false);
                updateUI(hanjaItem);
                break;
        }

        Loading.hide();
    }

    protected Object getHanjaMapKey() {
        return hanjaItem;
    }
}
