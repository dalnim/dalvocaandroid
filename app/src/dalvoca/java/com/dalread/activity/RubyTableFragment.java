package com.dalread.activity;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.dalread.R;
import com.dalread.adapter.RubyTableAdapter;
import com.dalread.base.BaseRubyFragment;
import com.dalread.component.CenterLayoutManager;
import com.dalread.component.SeparatorDecoration;
import com.dalread.dialog.RubyWordDefinitionDialog;
import com.dalread.listener.OnRubyTableListener;
import com.dalread.listener.OnRubyWordDefinitionListener;
import com.dalread.model.RubyListModel;
import com.dalread.model.RubyTextModel;
import com.dalread.model.VocaDetailInfo;
import com.dalread.network.DalApiListener;
import com.dalread.util.Loading;

import butterknife.BindColor;
import butterknife.BindDimen;
import butterknife.BindView;

public class RubyTableFragment extends BaseRubyFragment implements OnRubyTableListener, OnRubyWordDefinitionListener {

    @BindView(R.id.rv_ruby) RecyclerView rvRuby;
    @BindColor(R.color.color_divider) int clDivider;
    @BindDimen(R.dimen.divider_height) float dividerHeight;

    private RubyListModel rubyListModel;
    private RubyTableAdapter adapter;
    private RubyWordDefinitionDialog rubyDialog;

    @Override
    protected int getContentViewId() {
        return R.layout.fragment_ruby_table;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getRubyList();
    }

    private void getRubyList() {
        Loading.show(activity);
        application.getDalAiImpl().makeRubyTextList(0, 0, new DalApiListener<RubyListModel>() {
            @Override
            public void onSuccess(RubyListModel response) {
                rubyListModel = response;
                init();
            }

            @Override
            public void onFailure(String error) {
                Loading.hide();
            }
        });
    }

    private void init() {
        adapter = new RubyTableAdapter(rubyListModel, this);
        rvRuby.setAdapter(adapter);
        rvRuby.setLayoutManager(new CenterLayoutManager(activity));
        rvRuby.addItemDecoration(new SeparatorDecoration(activity, clDivider, dividerHeight));
        Loading.hide();
    }

    @Override
    public void onTextSelected(String text, RubyTextModel rubyTextModel) {
        getVocaDetailInfo(rubyTextModel);
    }

    private void getVocaDetailInfo(final RubyTextModel rubyTextModel) {
        Loading.show(activity);
        application.getDalAiImpl().getVocaDetailInfo(
                String.valueOf(sharedPreferences.getRealUid()),
                sharedPreferences.getLangStudyCode(),
                sharedPreferences.getMotherTongueLangCode(),
                rubyTextModel.getVocaId(),
                rubyTextModel.getVocaType(),
                new DalApiListener<VocaDetailInfo>() {

                    @Override
                    public void onSuccess(VocaDetailInfo response) {
                        Loading.hide();
                        openRubyWordDefinition(response, rubyTextModel);
                    }

                    @Override
                    public void onFailure(String error) {
                        Loading.hide();
                    }
                }
        );
    }

    private void openRubyWordDefinition(VocaDetailInfo response, RubyTextModel rubyTextModel) {
        if (rubyDialog == null) {
            rubyDialog = new RubyWordDefinitionDialog(activity, response, rubyTextModel,this);
        } else {
            if (rubyDialog.isShowing()) {
                rubyDialog.dismiss();
            }
            rubyDialog.updateData(response, rubyTextModel);
        }
        rubyDialog.show();
    }

    @Override
    public void refreshData(RubyTextModel rubyTextModel) {

    }
}
