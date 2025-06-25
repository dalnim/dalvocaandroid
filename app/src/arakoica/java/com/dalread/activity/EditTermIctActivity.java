package com.dalread.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;

import com.dalread.R;
import com.dalread.base.VocaActivity;
import com.dalread.database.sqlite.SubDatabase;
import com.dalread.database.sqlite.model.DIC_ICT_TERM;
import com.dalread.databinding.ActivityEditIctTermBinding;
import com.dalread.dialog.AlertDialog;
import com.dalread.network.events.BaseEvent;
import com.dalread.network.events.SuccessEvent;
import com.dalread.util.BaseStorageUtil;
import com.dalread.util.Constant;
import com.dalread.util.Loading;
import com.dalread.util.StringUtils;
import com.dalread.util.ToastUtil;
import com.dalread.util.Utils;

import butterknife.OnClick;

public class EditTermIctActivity extends VocaActivity {
    private DIC_ICT_TERM item;
    private DIC_ICT_TERM itemOri;
    private SubDatabase subDatabase;
    private static boolean isEditTerm = true;

    private ActivityEditIctTermBinding binding;
    protected View getContentView() {
        binding = ActivityEditIctTermBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        return view;
    }
    public static Intent createIntentAddTerm(Context context) {
        Intent intent = new Intent(context, EditTermIctActivity.class);
        isEditTerm = false;
        return intent;
    }
    public static Intent createIntent(Context context, DIC_ICT_TERM dicHanjaSentence) {
        Intent intent = new Intent(context, EditTermIctActivity.class);
        intent.putExtra(Constant.BUNDLE.KEY_DIC_HANJA, dicHanjaSentence);
        isEditTerm = true;
        return intent;
    }

    @Override
    protected int getContentViewId() {
        return 0;//R.layout.activity_edit_hanja_sentence_meaning;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setOnClickListeners();
        initData();
        initLayout();
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
        saveChanges();
    }

    @Override
    public void onHeaderTextRightClick() {
        updateChanges();
        if (item.isEdited(itemOri)) {
            saveChanges();
        } else {
            ToastUtil.getInstance(this).show(R.string.toast_no_date_is_changed);
        }
    }



    public void initData() {
        subDatabase = SubDatabase.getInstance(this, BaseStorageUtil.getAraConvDBPathWithFileName(this));
        item = (DIC_ICT_TERM) getIntent().getSerializableExtra(Constant.BUNDLE.KEY_DIC_HANJA);
        if (item == null) {
            item = new DIC_ICT_TERM();
            item.setID(subDatabase.getMaxId());
        }
        itemOri = DIC_ICT_TERM.copy(item);
    }

    private void initLayout() {
        alertDialog = new AlertDialog(this);
//        binding.tvVocaId.setText("Voca type : " + item.getVIVocaType() + ", ID : " + item.getID());
        binding.tvVocaId.setText("ID : " + item.getID());
        binding.header.setTitle(isEditTerm ? R.string.title_edit_term : R.string.title_add_term);

        binding.etEngAbbr.setText(item.getTERM_ENG_ABBR());
        binding.etEngFull.setText(item.getTERM_ENG_FULL());
        binding.etKoTitle.setText(item.getTERM_KO_TITLE());
        binding.etHanjaTitle.setText(item.getTERM_HANJA_TITLE());
        binding.etKoShort.setText(item.getTERM_KO_SHORT());
        binding.etKoFull.setText(item.getTERM_KO_FULL());
        binding.etGroupNation.setText(item.getGROUP_NATION());
        binding.etUrl.setText(item.getURL());

        binding.scIctTermGroupIct.setChecked(item.isGROUP_ICT());
        binding.scIctTermGroupHw.setChecked(item.isGROUP_HW());
        binding.scIctTermGroupGis.setChecked(item.isGROUP_GIS());
        binding.scIctTermGroupEtc.setChecked(item.isGROUP_ETC());

    }

    private void updateChanges() {
        item.setTERM_ENG_ABBR(StringUtils.normalizeString(binding.etEngAbbr.getText().toString().trim()));
        item.setTERM_ENG_FULL(StringUtils.normalizeString(binding.etEngFull.getText().toString().trim()));
        item.setTERM_KO_TITLE(StringUtils.normalizeString(binding.etKoTitle.getText().toString().trim()));
        item.setTERM_HANJA_TITLE(StringUtils.normalizeString(binding.etHanjaTitle.getText().toString().trim()));
        item.setTERM_KO_SHORT(StringUtils.normalizeString(binding.etKoShort.getText().toString().trim()));
        item.setTERM_KO_FULL(StringUtils.normalizeString(binding.etKoFull.getText().toString().trim()));
        item.setGROUP_NATION(StringUtils.normalizeString(binding.etGroupNation.getText().toString().trim()));
        item.setURL(StringUtils.normalizeString(binding.etUrl.getText().toString().trim()));
    }

    private void saveChangesInLocal(Integer id) {
        if (isEditTerm) {
            subDatabase.updateVoca(item);
        } else {
            item.setID(id);
            subDatabase.insertVocaByServerId(item);
        }
    }

    private void saveChanges() {
        int uid = getUserID();
//        if (uid > 0) {
//            if (Utils.isConnected(this)) {
                Loading.show(this);
                if (isEditTerm) {
                    updateIctTerm(uid);
                } else {
                    addIctTerm(uid);
                }
//            } else {
//                alertDialog.showNoInternet();
//            }
//        } else {
//            alertDialog.showLogInRequired();
//        }
    }

    private void updateIctTerm(int uid) {
        changeIctTermSuccess(item.getID());
//        ((AraKoicaApplication) application).getAraKoicaApiImpl().updateIctTerm(item, uid, new DalApiListener<Boolean>() {
//            @Override
//            public void onSuccess(Boolean response) {
//                if ((response != null) && (response == true)) {
//                    changeIctTermSuccess(item.getVIId());
//                } else {
//                    ToastUtil.getInstance(EditTermIctActivity.this).show(getString(R.string.failed_to_save));
//                }
//                Loading.hide();
//            }
//
//            @Override
//            public void onFailure(String error) {
//                Loading.hide();
//            }
//        });
    }

    private void addIctTerm(int uid) {
        //추가는 기존꺼 대비 최고 의 ID를 줘야함.
        changeIctTermSuccess(item.getID());
//        ((AraKoicaApplication) application).getAraKoicaApiImpl().addIctTerm(item, uid, new DalApiListener<Integer>() {
//            @Override
//            public void onSuccess(Integer response) {
//                if (response > 0) {
//                    changeIctTermSuccess(response);
//                } else {
//                    ToastUtil.getInstance(EditTermIctActivity.this).show(getString(R.string.failed_to_save));
//                }
//                Loading.hide();
//            }
//
//            @Override
//            public void onFailure(String error) {
//                Loading.hide();
//            }
//        });
    }

    private void changeIctTermSuccess(Integer id) {
        saveChangesInLocal(id);
        if (isEditTerm) {
            application.getEventBus().post(new SuccessEvent(BaseEvent.Screen.EDIT_ICT_TERM, BaseEvent.EventType.DATA_CHANGED, item));
        } else {
            application.getEventBus().post(new SuccessEvent(BaseEvent.Screen.EDIT_ICT_TERM, BaseEvent.EventType.DATA_ADDED, item));
        }
        onBackPressed();
        ToastUtil.getInstance(EditTermIctActivity.this).show(getString(R.string.saved));
    }
    @OnClick({
            R.id.ivWebSearch, R.id.ivWebDictionary, R.id.ivWebDictionaryEn2, R.id.ivWebDictionaryKorean, R.id.ivWebDictionaryEn
    })
    void onClick(View view) {
        updateChanges();
        switch (view.getId()) {
            case R.id.ivWebSearch:
                Utils.openWebSearch(this, item.getTERM_ENG_ABBR() + " " + item.getTERM_ENG_FULL() + " " + item.getTERM_KO_TITLE());
                break;
            case R.id.ivWebDictionary:
                Utils.openWebDictionaryForHanja(this, item.getHanjaToSearchInWebDictionary());
                break;
            case R.id.ivWebDictionaryKorean:
                Utils.openWebDictionaryForKorean(this, item.getTERM_KO_TITLE());
                break;
            case R.id.ivWebDictionaryEn:
                Utils.openWebDictionaryForHanja_en(this, item.getTERM_ENG_ABBR());
                break;
            case R.id.ivWebDictionaryEn2:
                Utils.openWebDictionaryForHanja_en(this, item.getTERM_ENG_FULL());
                break;
        }
    }

    private void setOnClickListeners() {
        binding.scIctTermGroupIct.setOnCheckedChangeListener(onCheckedChangeListener);
        binding.scIctTermGroupHw.setOnCheckedChangeListener(onCheckedChangeListener);
        binding.scIctTermGroupGis.setOnCheckedChangeListener(onCheckedChangeListener);
        binding.scIctTermGroupEtc.setOnCheckedChangeListener(onCheckedChangeListener);
    }

    private CompoundButton.OnCheckedChangeListener onCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            int id = buttonView.getId();
            int intChecked = isChecked ? 1 : 0;
            switch (id) {
                case R.id.scIctTermGroupIct:
                    item.setGROUP_ICT(intChecked);
                    break;
                case R.id.scIctTermGroupHw:
                    item.setGROUP_HW(intChecked);
                    break;
                case R.id.scIctTermGroupGis:
                    item.setGROUP_GIS(intChecked);
                    break;
                case R.id.scIctTermGroupEtc:
                    item.setGROUP_ETC(intChecked);
                    break;

            }
        }
    };
}