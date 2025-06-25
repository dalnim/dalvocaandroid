package com.dalread.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckedTextView;
import android.widget.EditText;

import androidx.annotation.Nullable;

import com.dalread.R;
import com.dalread.base.BaseRubyFragment;
import com.dalread.base.BaseVocaActivity;
import com.dalread.base.EnumLanguage;
import com.dalread.component.FuriganaView;
import com.dalread.dialog.RubyWordDefinitionDialog;
import com.dalread.listener.OnRubyWordDefinitionListener;
import com.dalread.model.RubyTextModel;
import com.dalread.model.VocaDetailInfo;
import com.dalread.network.DalApiListener;
import com.dalread.util.DLog;
import com.dalread.util.Loading;

import butterknife.BindView;
import butterknife.OnClick;

public class RubyTextFragment extends BaseRubyFragment implements FuriganaView.OnTextSelectedListener,
        OnRubyWordDefinitionListener {

    @BindView(R.id.tv_ruby) FuriganaView tvRuby;
    @BindView(R.id.et_ruby) EditText editText;
    @BindView(R.id.ckTutor) CheckedTextView ckTutor;
    private RubyWordDefinitionDialog rubyDialog;

    @Override
    protected int getContentViewId() {
        return R.layout.fragment_ruby_text;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        init();
    }

    @OnClick(R.id.btn_send)
    public void sendClick() {
        final String textInput = editText.getText().toString();
        if (TextUtils.isEmpty(textInput)) return;
        getRubyText(textInput);
    }

    @OnClick(R.id.btn_setting)
    public void settingClick() {
        ((BaseVocaActivity)getActivity()).openNewScreen(SettingsActivity.class);
    }

    private void init() {
        tvRuby.setOnTextSelectedListener(this);
        updateTextDefault();
        ckTutor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateLayoutTutor();
            }
        });
    }

    private void updateTextDefault() {
        editText.setText(EnumLanguage.findByFormatApi(sharedPreferences.getStudyLanguage()).getLetStudy());
    }

    private void getRubyText(String inputText) {
        Loading.show(activity);
        application.getDalAiImpl().makeRubyText(0, 0, inputText, new DalApiListener<RubyTextModel>() {
            @Override
            public void onSuccess(RubyTextModel response) {
                tvRuby.resetText();
                tvRuby.setJText(response.getContent());
                Loading.hide();
            }

            @Override
            public void onFailure(String error) {
                DLog.e(getLogTag(), "error makeRubyText API");
                Loading.hide();
            }
        });
    }

    @Override
    public void onTextSelected(String text, RubyTextModel rubyTextModel) {
        DLog.d(getLogTag(), "onTextSelected - text=" + text + " - ruby=" + rubyTextModel.toString());
        getVocaDetailInfo(rubyTextModel);
    }

    @Override
    public void onDoubleClick(String text, RubyTextModel rubyTextModel) {
        DLog.d(getLogTag(), "onDoubleClick - text=" + text + " - ruby=" + rubyTextModel.toString());
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
            rubyDialog = new RubyWordDefinitionDialog(activity, response, rubyTextModel, this);
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
        DLog.e(getLogTag(), rubyTextModel.toString());
        tvRuby.reDrawLayout(rubyTextModel.getContent());
    }

    private void updateLayoutTutor() {
        ckTutor.setSelected(!ckTutor.isSelected());
        tvRuby.setTutor(ckTutor.isSelected());
        tvRuby.reload();
    }
}
