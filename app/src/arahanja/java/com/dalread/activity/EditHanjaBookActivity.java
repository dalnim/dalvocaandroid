package com.dalread.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import com.dalread.AraHanjaApplication;
import com.dalread.R;
import com.dalread.base.BaseActivity;
import com.dalread.component.Toolbar;
import com.dalread.databinding.ActivityEditHanjaBookBinding;
import com.dalread.dialog.AlertDialog;
import com.dalread.model.DIC_HANJA_BOOK;
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

public class EditHanjaBookActivity extends BaseActivity implements View.OnTouchListener, View.OnClickListener {
    private AlertDialog alertDialog;

    private DIC_HANJA_BOOK mHanjaItem;
    private ActivityEditHanjaBookBinding binding;

    protected View getContentView() {
        binding = ActivityEditHanjaBookBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        return view;
    }

    @Override
    protected Toolbar getToolbar() {
        return null;
    }


    public static Intent createIntent(Context context, DIC_HANJA_BOOK hanjaItem) {
        Intent intent = new Intent(context, EditHanjaBookActivity.class);
        intent.putExtra(Constant.BUNDLE.KEY_DIC_HANJA, hanjaItem);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
        initLayout();
        initOnClickListener();
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
        saveChanges();
    }

    @Override
    public void onHeaderIconRightClick() {

    }

    @Override
    public void onHeaderTextRightClick() {
        updateChanges();
        saveChanges();
    }

    public void initData() {
        mHanjaItem = (DIC_HANJA_BOOK) getIntent().getSerializableExtra(Constant.BUNDLE.KEY_DIC_HANJA);
    }
    private void initLayout() {
        alertDialog = new AlertDialog(this);
        binding.tvVocaId.setText("Voca type : " + mHanjaItem.getHI_VOCA_TYPE() + ", ID : " + mHanjaItem.getHI_ID());
        binding.etVoca.setText(mHanjaItem.getVOCA());
        binding.etPronounce.setText(mHanjaItem.getPRONOUNCE());
        binding.etMeaning.setText(mHanjaItem.getMEANING_KO());
        binding.etMeaningDetail.setText(mHanjaItem.getMEANING_KO_DETAILED());

    }

    private void updateChanges() {
        mHanjaItem.setVOCA(StringUtils.normalizeString(binding.etVoca.getText().toString().trim()));
        mHanjaItem.setPRONOUNCE(StringUtils.normalizeString(binding.etPronounce.getText().toString().trim()));
        mHanjaItem.setMEANING_KO(StringUtils.normalizeString(binding.etMeaning.getText().toString().trim()));
        mHanjaItem.setMEANING_KO_DETAILED(StringUtils.normalizeString(binding.etMeaningDetail.getText().toString().trim()));
    }
    private void saveChanges() {
        int uid = getUserID();
//        if (uid > 0) {
            if (Utils.isConnected(this)) {
                Loading.show(this);
                ((AraHanjaApplication) application).getAraHanjaApiImpl().updateHanjaBookWithID(mHanjaItem, new DalApiListener<Boolean>() {
                    @Override
                    public void onSuccess(Boolean response) {
                        if ((response != null) && (response == true)) {
                            saveBook();
                            ToastUtil.getInstance(EditHanjaBookActivity.this).show(getString(R.string.saved));
                            application.getEventBus().post(new SuccessEvent(BaseEvent.Screen.EDIT_HANJA_BOOK, BaseEvent.EventType.DATA_CHANGED, mHanjaItem));
                        } else {
                            ToastUtil.getInstance(EditHanjaBookActivity.this).show(getString(R.string.failed_to_save));
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
//        } else {
//            alertDialog.showLogInRequired();
//        }
    }
    private void saveBook() {
        Voca.updateHanjaBook(mHanjaItem);
    }


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

    private void initOnClickListener() {
        binding.tvVoca.setOnClickListener(this);
        binding.ivWebSearch.setOnClickListener(this);
        binding.ivWebDictionary.setOnClickListener(this);
        binding.ivWebDictionaryChS.setOnClickListener(this);
        binding.ivWebDictionaryEn.setOnClickListener(this);
        binding.ivWebDictionaryJp.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.iv_web_dictionary_en) {
            Utils.openWebDictionaryForHanja_en(this, mHanjaItem.getMEANING_KO());
        } else {
            String vocaOnlyChinese = StringUtils.getOnlyChinese(mHanjaItem.getVOCA());
            switch (v.getId()) {
                case R.id.tvVoca:
                    Utils.copyToClipboard(this, vocaOnlyChinese, R.string.copied);
                    break;
                case R.id.ivWebSearch:
                    Utils.openWebSearchForHanja(this, vocaOnlyChinese);
                    break;
                case R.id.iv_web_dictionary:
                    Utils.openWebDictionaryForHanja(this, vocaOnlyChinese);
                    break;
                case R.id.iv_web_dictionary_ch_s:
                    Utils.openWebDictionaryForHanja_ch_s(this, vocaOnlyChinese);
                    break;
                case R.id.iv_web_dictionary_jp:
                    Utils.openWebDictionaryForHanja_jp(this, vocaOnlyChinese);
                    break;
            }
        }
    }
//    @OnClick({
//            R.id.tvVoca, R.id.ivWebSearch, R.id.iv_web_dictionary, R.id.iv_web_dictionary_ch_s, R.id.iv_web_dictionary_en, R.id.iv_web_dictionary_jp
//    })
//    void onClick(View view) {
//        if (view.getId() == R.id.iv_web_dictionary_en) {
//            Utils.openWebDictionaryForHanja_en(this, mHanjaItem.getMEANING_KO());
//        } else {
//            String vocaOnlyChinese = StringUtils.getOnlyChinese(mHanjaItem.getVOCA());
//            switch (view.getId()) {
//                case R.id.tvVoca:
//                    Utils.copyToClipboard(this, vocaOnlyChinese, R.string.copied);
//                    break;
//                case R.id.ivWebSearch:
//                    Utils.openWebSearchForHanja(this, vocaOnlyChinese);
//                    break;
//                case R.id.iv_web_dictionary:
//                    Utils.openWebDictionaryForHanja(this, vocaOnlyChinese);
//                    break;
//                case R.id.iv_web_dictionary_ch_s:
//                    Utils.openWebDictionaryForHanja_ch_s(this, vocaOnlyChinese);
//                    break;
//                case R.id.iv_web_dictionary_jp:
//                    Utils.openWebDictionaryForHanja_jp(this, vocaOnlyChinese);
//                    break;
//            }
//        }
//
//    }
}