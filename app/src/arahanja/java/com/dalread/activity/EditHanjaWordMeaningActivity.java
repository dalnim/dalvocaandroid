package com.dalread.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;

import com.dalread.AraHanjaApplication;
import com.dalread.R;
import com.dalread.base.BaseActivity;
import com.dalread.base.VocaActivity;
import com.dalread.component.Toolbar;
import com.dalread.databinding.ActivityEditHanjaWordMeaningBinding;
import com.dalread.dialog.AlertDialog;
import com.dalread.model.DIC_HANJA;
import com.dalread.network.DalApiListener;
import com.dalread.network.events.BaseEvent;
import com.dalread.network.events.SuccessEvent;
import com.dalread.util.Constant;
import com.dalread.util.Loading;
import com.dalread.util.ToastUtil;
import com.dalread.util.Utils;
import com.dalread.util.Voca;

import org.apache.commons.lang3.StringUtils;

import butterknife.BindDimen;
import butterknife.OnClick;

public class EditHanjaWordMeaningActivity extends BaseActivity implements View.OnTouchListener, View.OnClickListener {
//    @BindDimen(R.dimen.item_pronounce_symbol_size) int itemSymbolSize;
//    @BindDimen(R.dimen.rv_pronounce_symbol_margin) int rvSymbolMargin;

    private AlertDialog alertDialog;

    private DIC_HANJA mDicHanja;
    private ActivityEditHanjaWordMeaningBinding binding;

    protected View getContentView() {
        binding = ActivityEditHanjaWordMeaningBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        return view;
    }

    @Override
    protected Toolbar getToolbar() {
        return null;
    }


    public static Intent createIntent(Context context, DIC_HANJA dicHanja) {
        Intent intent = new Intent(context, EditHanjaWordMeaningActivity.class);
        intent.putExtra(Constant.BUNDLE.KEY_DIC_HANJA, dicHanja);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initTextListener();
        initOnClickListener();
        initData();
        initLayout(mDicHanja);

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
        saveChanges();
    }

    @Override
    public void onHeaderIconRightClick() {
        saveChanges();
    }

    @Override
    public void onHeaderTextRightClick() {
//        if (hasNoChange()) {
//            onBackPressed();
//        } else {
            updateChanges();
            saveChanges();
//        }
    }

    public void initData() {
        mDicHanja = (DIC_HANJA) getIntent().getSerializableExtra(Constant.BUNDLE.KEY_DIC_HANJA);
    }
    private void initLayout(DIC_HANJA dicHanja) {
        alertDialog = new AlertDialog(this);
        binding.tvVocaId.setText("Voca type : " + mDicHanja.getVIVocaType() + ", ID : " + mDicHanja.getID() + ", Unicode : " + mDicHanja.getUNICODE_HEX());
        binding.tvVoca.setText(mDicHanja.getVOCA());
        binding.etMeaning1.setText(mDicHanja.getMEANING1());
        binding.etPronounce1First.setText(mDicHanja.getPRONOUNCE1_FIRST());
        binding.etPronounce1.setText(mDicHanja.getPRONOUNCE1());
        binding.etMeaning2.setText(mDicHanja.getMEANING2());
        binding.etPronounce2First.setText(mDicHanja.getPRONOUNCE2_FIRST());
        binding.etPronounce2.setText(mDicHanja.getPRONOUNCE2());
        binding.etMeaning3.setText(mDicHanja.getMEANING3());
        binding.etPronounce3First.setText(mDicHanja.getPRONOUNCE3_FIRST());
        binding.etPronounce3.setText(mDicHanja.getPRONOUNCE3());
        binding.etStrokes.setText(mDicHanja.getSTROKES().toString());
        binding.etRadical.setText(mDicHanja.getRADICAL());
        binding.etLeftComponent.setText(mDicHanja.getLEFTCOMPONENT());
        binding.etRightComponent.setText(mDicHanja.getRIGHTCOMPONENT());
        binding.etPronouncePinyin.setText(mDicHanja.getPINYIN());
        binding.etVocaOri.setText(mDicHanja.getVOCAORI());
        binding.etHanjaKorea.setText(mDicHanja.getHANJA_KOREA());
        binding.etHanjaSimplified.setText(mDicHanja.getHANJA_SIMPLIFIED());
        binding.etHanjaJapan.setText(mDicHanja.getHANJA_JAPAN());
        binding.etHanjaTaiwan.setText(mDicHanja.getHANJA_TAIWAN());
        binding.etHanjaShortForm.setText(mDicHanja.getHANJA_SHORT_FORM());
        binding.etHanjaVariant1.setText(mDicHanja.getHANJA_VARIANT_1());
        binding.etHanjaVariant2.setText(mDicHanja.getHANJA_VARIANT_2());
        binding.etHanjaSokja.setText(mDicHanja.getHANJA_SOKJA());
        binding.etPronounceKunYomi.setText(mDicHanja.getKUNYOMI());
        binding.etPronounceOnYomi.setText(mDicHanja.getONYOMI());
        binding.etDetailInfo.setText(mDicHanja.getMEANING_KO_DETAILED());
        binding.etMeaningEng.setText(mDicHanja.getMEANING_ENG());
        binding.etEngDetailInfo.setText(mDicHanja.getMEANING_ENG_DETAILED());

        updateMeaning();
    }

//    private boolean hasNoChange() {
    //Dalnim : need to update logic to check the changes.
//        return et_meaning1.getText().toString().equals(mDicHanja.getVOCA());
//    }

    private void updateMeaning() {
        StringBuilder sbMeaningKO = new StringBuilder();
        String pronounce1 = binding.etPronounce1.getText().toString().equals("") ? "" : "/" + binding.etPronounce1.getText().toString();
        getMeaningSub(sbMeaningKO, binding.etMeaning1.getText().toString() + " " + binding.etPronounce1First.getText().toString() + pronounce1);
        getMeaningSub(sbMeaningKO, binding.etMeaning2.getText().toString() + " " + binding.etPronounce2First.getText().toString());
        getMeaningSub(sbMeaningKO, binding.etMeaning3.getText().toString() + " " + binding.etPronounce3First.getText().toString());
        String meaningKO = StringUtils.removeEnd(sbMeaningKO.toString(), ",");
        binding.tvMeaning.setText(meaningKO);

    }

    private void getMeaningSub(StringBuilder sbMeaningKO, String strMeaningSub) {
        if (!Utils.isEmpty(strMeaningSub)) {
            sbMeaningKO.append(strMeaningSub + ",");
        }
    }

    private void updateChanges() {
        updateMeaning();
        mDicHanja.setMEANING1(com.dalread.util.StringUtils.normalizeString(binding.etMeaning1.getText().toString().trim()));
        mDicHanja.setPRONOUNCE1(binding.etPronounce1.getText().toString().trim());
        mDicHanja.setPRONOUNCE1_FIRST(binding.etPronounce1First.getText().toString().trim());
        mDicHanja.setMEANING1_PRONOUNCE1(com.dalread.util.StringUtils.normalizeString(com.dalread.util.StringUtils.removeSpaces(binding.etMeaning1.getText().toString().trim() + binding.etPronounce1.getText().toString().trim())));
        mDicHanja.setMEANING1_PRONOUNCE1_FIRST(com.dalread.util.StringUtils.normalizeString(com.dalread.util.StringUtils.removeSpaces(binding.etMeaning1.getText().toString().trim() + binding.etPronounce1First.getText().toString().trim())));

        mDicHanja.setMEANING2(com.dalread.util.StringUtils.normalizeString(binding.etMeaning2.getText().toString().trim()));
        mDicHanja.setPRONOUNCE2(binding.etPronounce2.getText().toString().trim());
        mDicHanja.setPRONOUNCE2_FIRST(com.dalread.util.StringUtils.removeSpaces(binding.etPronounce2First.getText().toString().trim()));

        mDicHanja.setMEANING3(com.dalread.util.StringUtils.normalizeString(binding.etMeaning3.getText().toString().trim()));
        mDicHanja.setPRONOUNCE3(binding.etPronounce3.getText().toString().trim());
        mDicHanja.setPRONOUNCE3_FIRST(com.dalread.util.StringUtils.removeSpaces(binding.etPronounce3First.getText().toString().trim()));

        mDicHanja.setLEFTCOMPONENT(com.dalread.util.StringUtils.normalizeString(com.dalread.util.StringUtils.removeSpaces(binding.etLeftComponent.getText().toString().trim())));
        mDicHanja.setRIGHTCOMPONENT(com.dalread.util.StringUtils.normalizeString(com.dalread.util.StringUtils.removeSpaces(binding.etRightComponent.getText().toString().trim())));

        mDicHanja.setSTROKES(Long.valueOf(binding.etStrokes.getText().toString().trim()));
        mDicHanja.setRADICAL(com.dalread.util.StringUtils.normalizeString(binding.etRadical.getText().toString().trim()));
        mDicHanja.setMEANING_KO(com.dalread.util.StringUtils.normalizeString(binding.tvMeaning.getText().toString().trim()));
        mDicHanja.setMEANING(com.dalread.util.StringUtils.normalizeString(binding.tvMeaning.getText().toString().trim()));
        mDicHanja.setPINYIN(com.dalread.util.StringUtils.normalizeString(binding.etPronouncePinyin.getText().toString().trim()));
        if (Utils.isEmpty(binding.etVocaOri.getText().toString().trim())) {
            mDicHanja.setVOCAORI(mDicHanja.getVOCA().trim());
        } else {
            mDicHanja.setVOCAORI(binding.etVocaOri.getText().toString().trim());
        }
        mDicHanja.setHANJA_KOREA(com.dalread.util.StringUtils.normalizeString(binding.etHanjaKorea.getText().toString().trim()));
        mDicHanja.setHANJA_SIMPLIFIED(com.dalread.util.StringUtils.normalizeString(binding.etHanjaSimplified.getText().toString().trim()));
        mDicHanja.setHANJA_JAPAN(com.dalread.util.StringUtils.normalizeString(binding.etHanjaJapan.getText().toString().trim()));
        mDicHanja.setHANJA_TAIWAN(com.dalread.util.StringUtils.normalizeString(binding.etHanjaTaiwan.getText().toString().trim()));
        mDicHanja.setHANJA_SHORT_FORM(com.dalread.util.StringUtils.normalizeString(binding.etHanjaShortForm.getText().toString().trim()));
        mDicHanja.setHANJA_VARIANT_1(com.dalread.util.StringUtils.normalizeString(binding.etHanjaVariant1.getText().toString().trim()));
        mDicHanja.setHANJA_VARIANT_2(com.dalread.util.StringUtils.normalizeString(binding.etHanjaVariant2.getText().toString().trim()));
        mDicHanja.setHANJA_SOKJA(com.dalread.util.StringUtils.normalizeString(binding.etHanjaSokja.getText().toString().trim()));

        mDicHanja.setKUNYOMI(binding.etPronounceKunYomi.getText().toString().trim());
        mDicHanja.setONYOMI(binding.etPronounceOnYomi.getText().toString().trim());
        mDicHanja.setMEANING_ENG(com.dalread.util.StringUtils.normalizeString(binding.etMeaningEng.getText().toString().trim()));
        mDicHanja.setMEANING_ENG_DETAILED(com.dalread.util.StringUtils.normalizeString(binding.etEngDetailInfo.getText().toString().trim()));
        mDicHanja.setMEANING_KO_DETAILED(com.dalread.util.StringUtils.normalizeString(binding.etDetailInfo.getText().toString().trim()));
        mDicHanja.setMEANING_DETAILED(com.dalread.util.StringUtils.normalizeString(binding.etDetailInfo.getText().toString().trim()));
    }
    private void saveChanges() {
        int uid = getUserID();
        if (uid > 0) {
            if (Utils.isConnected(this)) {
                Loading.show(this);
                ((AraHanjaApplication) application).getAraHanjaApiImpl().updateHanjaWordMeaningWithID(mDicHanja, new DalApiListener<Boolean>() {
                    @Override
                    public void onSuccess(Boolean response) {
                        if ((response != null) && (response == true)) {
                            saveVoca();
                            ToastUtil.getInstance(EditHanjaWordMeaningActivity.this).show(getString(R.string.saved));
                            application.getEventBus().post(new SuccessEvent(BaseEvent.Screen.EDIT_HANJA_WORD, BaseEvent.EventType.DATA_CHANGED, mDicHanja));
                        } else {
                            ToastUtil.getInstance(EditHanjaWordMeaningActivity.this).show(getString(R.string.failed_to_save));
                        }
                        Loading.hide();
                        onBackPressed();
                    }

                    @Override
                    public void onFailure(String error) {
                        Loading.hide();
                    }
                });
            } else {
                alertDialog.showNoInternet();
            }
        } else {
            alertDialog.showLogInRequired();
        }
    }
    private void saveVoca() {
        Voca.updateHanjaWord(mDicHanja);
    }

    private void initTextListener() {
        TextWatcher tw = new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                // TODO Auto-generated method stub
                updateMeaning();
            }
        };

        binding.etMeaning1.addTextChangedListener(tw);
        binding.etPronounce1First.addTextChangedListener(tw);
        binding.etPronounce1.addTextChangedListener(tw);
        binding.etMeaning2.addTextChangedListener(tw);
        binding.etPronounce2First.addTextChangedListener(tw);
        binding.etPronounce2.addTextChangedListener(tw);
        binding.etMeaning3.addTextChangedListener(tw);
        binding.etPronounce3First.addTextChangedListener(tw);
        binding.etPronounce3.addTextChangedListener(tw);

    }
    private void initOnClickListener() {
        binding.tvVoca.setOnClickListener(this);
        binding.ivWebSearch.setOnClickListener(this);
        binding.ivWebDictionary.setOnClickListener(this);
        binding.ivWebDictionaryChS.setOnClickListener(this);
        binding.ivWebDictionaryJp.setOnClickListener(this);
        binding.ivWebDictionaryEn.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvVoca:
                Utils.copyToClipboard(this, mDicHanja.getVOCA(), R.string.copied);
                break;
            case R.id.ivWebSearch:
                Utils.openWebSearchForHanja(this, mDicHanja.getVOCA());
                break;
            case R.id.iv_web_dictionary:
                Utils.openWebDictionaryForHanja(this, mDicHanja.getVOCA());
                break;
            case R.id.iv_web_dictionary_ch_s:
                Utils.openWebDictionaryForHanja_ch_s(this, mDicHanja.getVOCA());
                break;
            case R.id.iv_web_dictionary_jp:
                Utils.openWebDictionaryForHanja_jp(this, mDicHanja.getVOCA());
                break;
            case R.id.iv_web_dictionary_en:
                Utils.openWebDictionaryForHanja_en(this, mDicHanja.getMEANING_KO());
                break;
        }
    }
//    @OnClick({
//            R.id.tvVoca, R.id.ivWebSearch, R.id.iv_web_dictionary, R.id.iv_web_dictionary_ch_s, R.id.iv_web_dictionary_en, R.id.iv_web_dictionary_jp
//    })
//    void onClick(View view) {
//        switch (view.getId()) {
//            case R.id.tvVoca:
//                Utils.copyToClipboard(this, mDicHanja.getVOCA(), R.string.copied);
//                break;
//            case R.id.ivWebSearch:
//                Utils.openWebSearchForHanja(this, mDicHanja.getVOCA());
//                break;
//            case R.id.iv_web_dictionary:
//                Utils.openWebDictionaryForHanja(this, mDicHanja.getVOCA());
//                break;
//            case R.id.iv_web_dictionary_ch_s:
//                Utils.openWebDictionaryForHanja_ch_s(this, mDicHanja.getVOCA());
//                break;
//            case R.id.iv_web_dictionary_jp:
//                Utils.openWebDictionaryForHanja_jp(this, mDicHanja.getVOCA());
//                break;
//            case R.id.iv_web_dictionary_en:
//                Utils.openWebDictionaryForHanja_en(this, mDicHanja.getMEANING_KO());
//                break;
//        }
//    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (v.getId() == R.id.et_detail_info) {
            v.getParent().requestDisallowInterceptTouchEvent(true);
            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_UP:
                    v.getParent().requestDisallowInterceptTouchEvent(false);
                    break;
            }
        }
        return false;
    }





}