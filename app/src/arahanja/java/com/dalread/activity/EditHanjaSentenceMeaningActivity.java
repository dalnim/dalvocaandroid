package com.dalread.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import com.dalread.AraHanjaApplication;
import com.dalread.R;
import com.dalread.base.BaseActivity;
import com.dalread.base.VocaActivity;
import com.dalread.component.Toolbar;
import com.dalread.databinding.ActivityEditHanjaSentenceMeaningBinding;
import com.dalread.dialog.AlertDialog;
import com.dalread.model.DIC_HANJA_SENTENCE;
import com.dalread.network.DalApiListener;
import com.dalread.network.events.BaseEvent;
import com.dalread.network.events.SuccessEvent;
import com.dalread.util.Constant;
import com.dalread.util.Loading;
import com.dalread.util.StringUtils;
import com.dalread.util.ToastUtil;
import com.dalread.util.Utils;
import com.dalread.util.Voca;

import butterknife.OnClick;

public class EditHanjaSentenceMeaningActivity extends BaseActivity implements View.OnTouchListener, View.OnClickListener {

    private DIC_HANJA_SENTENCE mDicHanjaSentence;
    private ActivityEditHanjaSentenceMeaningBinding binding;
    private boolean isNewVoca = false;
    protected View getContentView() {
        binding = ActivityEditHanjaSentenceMeaningBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        return view;
    }

    @Override
    protected Toolbar getToolbar() {
        return binding.header;
    }

    public static Intent createIntent(Context context, DIC_HANJA_SENTENCE dicHanjaSentence) {
        Intent intent = new Intent(context, EditHanjaSentenceMeaningActivity.class);
        intent.putExtra(Constant.BUNDLE.KEY_DIC_HANJA, dicHanjaSentence);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initOnClickListener();
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
        updateChanges();
        if (isNewVoca) {
            addNewVocaInServerDB();
        } else {
            saveChanges();
        }
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
        if (isNewVoca) {
            addNewVocaInServerDB();
        } else {
            saveChanges();
        }
        //        }
    }



    public void initData() {
        mDicHanjaSentence = (DIC_HANJA_SENTENCE) getIntent().getSerializableExtra(Constant.BUNDLE.KEY_DIC_HANJA);
    }

    private void initLayout() {
        alertDialog = new AlertDialog(this);
        isNewVoca = mDicHanjaSentence.getID() < 0 ? true : false;
        binding.tvVocaId.setText("Voca type : " + mDicHanjaSentence.getVIVocaType() + ", ID : " + mDicHanjaSentence.getID());
        binding.header.setTitle(isNewVoca ? R.string.title_add_sentence : R.string.title_edit_phrase);
        binding.tvVoca.setText(mDicHanjaSentence.getVOCA());
        binding.etPronounce.setText(mDicHanjaSentence.getPRONOUNCE());
        binding.etVocaChs.setText(mDicHanjaSentence.getVOCA_CH_S());
        binding.etVocaJp.setText(mDicHanjaSentence.getVOCA_JP());
        binding.etPronouncePinyin.setText(mDicHanjaSentence.getPRONOUNCE_CH_S());
        binding.etPronounceJapanese.setText(mDicHanjaSentence.getPRONOUNCE_JP());
        binding.etMeaning.setText(mDicHanjaSentence.getMEANING_KO());
        binding.etDetailInfo.setText(mDicHanjaSentence.getMEANING_KO_DETAILED());
        binding.etMeaningEng.setText(mDicHanjaSentence.getMEANING_ENG());
        binding.etEngDetailInfo.setText(mDicHanjaSentence.getMEANING_ENG_DETAILED());
    }

//    private boolean hasNoChange() {
    //Dalnim : need to update logic to check the changes.
//        return et_meaning.getText().toString().equals(mDicHanjaSentence.getMEANING_KO());
//    }

    private void updateChanges() {
        mDicHanjaSentence.setPRONOUNCE(StringUtils.normalizeString(binding.etPronounce.getText().toString().trim()));
        mDicHanjaSentence.setVOCA_CH_S(StringUtils.normalizeString(binding.etVocaChs.getText().toString().trim()));
        mDicHanjaSentence.setVOCA_JP(StringUtils.normalizeString(binding.etVocaJp.getText().toString().trim()));
        mDicHanjaSentence.setPRONOUNCE_CH_S(StringUtils.normalizeString(binding.etPronouncePinyin.getText().toString().trim()));
        mDicHanjaSentence.setPRONOUNCE_JP(StringUtils.normalizeString(binding.etPronounceJapanese.getText().toString().trim()));
        mDicHanjaSentence.setMEANING_KO(StringUtils.normalizeString(binding.etMeaning.getText().toString().trim()));
        mDicHanjaSentence.setMEANING_KO_DETAILED(StringUtils.normalizeString(binding.etDetailInfo.getText().toString().trim()));
        mDicHanjaSentence.setMEANING_ENG(StringUtils.normalizeString(binding.etMeaningEng.getText().toString().trim()));
        mDicHanjaSentence.setMEANING_ENG_DETAILED(StringUtils.normalizeString(binding.etEngDetailInfo.getText().toString().trim()));
    }
    private void saveChanges() {
        int uid = getUserID();
        if (uid > 0) {
            if (Utils.isConnected(this)) {
                Loading.show(this);
                ((AraHanjaApplication) application).getAraHanjaApiImpl().updateHanjaSentenceMeaningWithID(mDicHanjaSentence,  new DalApiListener<Boolean>() {
                    @Override
                    public void onSuccess(Boolean response) {
                        if ((response != null) && (response == true)) {
                            saveVoca();
                            ToastUtil.getInstance(EditHanjaSentenceMeaningActivity.this).show(getString(R.string.saved));
                            application.getEventBus().post(new SuccessEvent(BaseEvent.Screen.EDIT_HANJA_SENTENCE, BaseEvent.EventType.DATA_CHANGED, mDicHanjaSentence));
                        } else {
                            ToastUtil.getInstance(EditHanjaSentenceMeaningActivity.this).show(getString(R.string.failed_to_save));
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

    private void addNewVocaInServerDB() {
        int uid = getUserID();
        if (uid > 0) {
            if (Utils.isConnected(this)) {
                Loading.show(this);
                ((AraHanjaApplication) application).getAraHanjaApiImpl().addHanjaSentenceWithoutVocaId(mDicHanjaSentence, new DalApiListener<Integer>() {
                            @Override
                            public void onSuccess(Integer response) {
                                if ((response != null) && (response > 0)) {
                                    mDicHanjaSentence.setID(Long.valueOf(response));
                                    insertNewVocaInLocalDB(mDicHanjaSentence);
                                    ToastUtil.getInstance(EditHanjaSentenceMeaningActivity.this).show(getString(R.string.added_new_voca));
                                } else {
                                    ToastUtil.getInstance(EditHanjaSentenceMeaningActivity.this).show(getString(R.string.failed_to_add_new_voca));
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
        Voca.updateHanjaSentence(mDicHanjaSentence);
    }

    private void insertNewVocaInLocalDB(DIC_HANJA_SENTENCE dicHanjaSentence) {
        Voca.addHanjaSentence(dicHanjaSentence);
    }
    private void initOnClickListener() {
        binding.tvVoca.setOnClickListener(this);
        binding.ivWebSearch.setOnClickListener(this);
        binding.ivWebDictionary.setOnClickListener(this);
        binding.ivWebDictionaryEn.setOnClickListener(this);
        binding.ivWebDictionaryChS.setOnClickListener(this);
        binding.ivWebDictionaryJp.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvVoca:
                Utils.copyToClipboard(this, mDicHanjaSentence.getVOCA(), R.string.copied);
                break;
            case R.id.ivWebSearch:
                Utils.openWebSearchForHanja(this, mDicHanjaSentence.getVOCA());
                break;
            case R.id.iv_web_dictionary:
                Utils.openWebDictionaryForHanja(this, mDicHanjaSentence.getVOCA());
                break;
            case R.id.iv_web_dictionary_ch_s:
                if ((mDicHanjaSentence != null) || (mDicHanjaSentence.getVOCA() != null))
                    Utils.openWebDictionaryForHanja_ch_s(this, mDicHanjaSentence.getVOCA());
                break;
            case R.id.iv_web_dictionary_jp:
                if ((mDicHanjaSentence != null) || (mDicHanjaSentence.getVOCA() != null))
                    Utils.openWebDictionaryForHanja_jp(this, mDicHanjaSentence.getVOCA());
                break;
            case R.id.iv_web_dictionary_en:
                if ((mDicHanjaSentence != null) || (!Utils.isEmpty(mDicHanjaSentence.getPRONOUNCE())))
                    Utils.openWebDictionaryForHanja_en(this, mDicHanjaSentence.getPRONOUNCE());
                break;
        }
    }
//    @OnClick({
//            R.id.tvVoca, R.id.ivWebSearch, R.id.iv_web_dictionary, R.id.iv_web_dictionary_ch_s, R.id.iv_web_dictionary_en, R.id.iv_web_dictionary_jp
//    })
//    void onClick(View view) {
//        switch (view.getId()) {
//            case R.id.tvVoca:
//                Utils.copyToClipboard(this, mDicHanjaSentence.getVOCA(), R.string.copied);
//                break;
//            case R.id.ivWebSearch:
//                Utils.openWebSearchForHanja(this, mDicHanjaSentence.getVOCA());
//                break;
//            case R.id.iv_web_dictionary:
//                Utils.openWebDictionaryForHanja(this, mDicHanjaSentence.getVOCA());
//                break;
//            case R.id.iv_web_dictionary_ch_s:
//                if ((mDicHanjaSentence != null) || (mDicHanjaSentence.getVOCA() != null))
//                    Utils.openWebDictionaryForHanja_ch_s(this, mDicHanjaSentence.getVOCA());
//                break;
//            case R.id.iv_web_dictionary_jp:
//                if ((mDicHanjaSentence != null) || (mDicHanjaSentence.getVOCA() != null))
//                    Utils.openWebDictionaryForHanja_jp(this, mDicHanjaSentence.getVOCA());
//                break;
//            case R.id.iv_web_dictionary_en:
//                if ((mDicHanjaSentence != null) || (!Utils.isEmpty(mDicHanjaSentence.getPRONOUNCE())))
//                    Utils.openWebDictionaryForHanja_en(this, mDicHanjaSentence.getPRONOUNCE());
//                break;
//        }
//    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (v.getId() == R.id.etDetailInfo
                || v.getId() == R.id.etMeaning
                || v.getId() == R.id.etEngDetailInfo
                || v.getId() == R.id.etMeaningEng) {
            //https://stackoverflow.com/questions/24428808/how-to-scroll-the-edittext-inside-the-scrollview
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