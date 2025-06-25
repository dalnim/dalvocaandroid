package com.dalread.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.dalread.R;
import com.dalread.base.VocaActivity;
import com.dalread.database.SubDatabase;
import com.dalread.database.sqlite.model.DIC_ICT_TERM;
import com.dalread.databinding.ActivityEditIctTermBinding;
import com.dalread.dialog.AlertDialog;
import com.dalread.network.events.BaseEvent;
import com.dalread.network.events.SuccessEvent;
import com.dalread.util.BaseStorageUtil;
import com.dalread.util.Constant;
import com.dalread.util.StringUtils;
import com.dalread.util.Utils;

import butterknife.OnClick;

public class EditTermIctActivity extends VocaActivity {
    private DIC_ICT_TERM item;
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
        saveChanges();
    }



    public void initData() {
        item = (DIC_ICT_TERM) getIntent().getSerializableExtra(Constant.BUNDLE.KEY_DIC_HANJA);
        if (item == null) {
            item = new DIC_ICT_TERM();
        }
    }

    private void initLayout() {
        alertDialog = new AlertDialog(this);
        binding.tvVocaId.setText("Voca type : " + item.getVIVocaType() + ", ID : " + item.getID());
        binding.header.setTitle(isEditTerm ? R.string.title_edit_term : R.string.title_add_term);

        binding.etEngAbbr.setText(item.getTERM_ENG_ABBR());
        binding.etEngFull.setText(item.getTERM_ENG_FULL());
        binding.etKoTitle.setText(item.getTERM_KO_TITLE());
        binding.etHanjaTitle.setText(item.getTERM_HANJA_TITLE());
        binding.etKoShort.setText(item.getTERM_KO_SHORT());
        binding.etKoFull.setText(item.getTERM_KO_FULL());
    }

    private void updateChanges() {
        item.setTERM_ENG_ABBR(StringUtils.normalizeString(binding.etEngAbbr.getText().toString().trim()));
        item.setTERM_ENG_FULL(StringUtils.normalizeString(binding.etEngFull.getText().toString().trim()));
        item.setTERM_KO_TITLE(StringUtils.normalizeString(binding.etKoTitle.getText().toString().trim()));
        item.setTERM_HANJA_TITLE(StringUtils.normalizeString(binding.etHanjaTitle.getText().toString().trim()));
        item.setTERM_KO_SHORT(StringUtils.normalizeString(binding.etKoShort.getText().toString().trim()));
        item.setTERM_KO_FULL(StringUtils.normalizeString(binding.etKoFull.getText().toString().trim()));
    }
    private void saveChanges() {
        saveVoca();
        if (isEditTerm) {
            application.getEventBus().post(new SuccessEvent(BaseEvent.Screen.EDIT_ICT_TERM, BaseEvent.EventType.DATA_CHANGED, item));
        } else {
            application.getEventBus().post(new SuccessEvent(BaseEvent.Screen.EDIT_ICT_TERM, BaseEvent.EventType.DATA_ADDED, item));
        }
//        application.getEventBus().post(new SuccessEvent(BaseEvent.Screen.EDIT_HANJA_SENTENCE, BaseEvent.EventType.DATA_CHANGED, item));
        onBackPressed();
    }

    private void saveVoca() {
        String destPathWithFileName = BaseStorageUtil.getAraConvDBPathWithFileName(this);
        SubDatabase subDatabase = SubDatabase.getInstance(this, destPathWithFileName);
        if (isEditTerm) {
            subDatabase.updateVoca(item);
        } else {
//            int id = subDatabase.insertVoca(item);
//            item.setID(id);
        }

    }

    @OnClick({
            R.id.ivWebSearch, R.id.ivWebDictionary, R.id.ivWebDictionaryEn2, R.id.ivWebDictionaryKorean, R.id.ivWebDictionaryEn
    })
    void onClick(View view) {
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
}