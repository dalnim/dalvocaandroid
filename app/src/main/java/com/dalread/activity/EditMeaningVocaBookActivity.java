package com.dalread.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.View;

import androidx.recyclerview.widget.GridLayoutManager;

import com.dalread.R;
import com.dalread.adapter.SymbolAdapter;
import com.dalread.asyntask.CustomAsyncTask;
import com.dalread.asyntask.OnAsyncTaskListener;
import com.dalread.base.BaseActivity;
import com.dalread.base.EnumLanguage;
import com.dalread.component.Toolbar;
import com.dalread.database.WebDictionaryQuery;
import com.dalread.databinding.ActivityEditMeaningBinding;
import com.dalread.dialog.AlertDialog;
import com.dalread.dialog.WebDictionaryDialog;
import com.dalread.interfaces.IVocaBasicItem;
import com.dalread.interfaces.IVocaFullItem;
import com.dalread.listener.OnClickDialogListener;
import com.dalread.listener.OnSymbolListener;
import com.dalread.model.VocaDetailInfo;
import com.dalread.model.WebDictionaryModel;
import com.dalread.network.DalApiListener;
import com.dalread.network.events.BaseEvent;
import com.dalread.network.events.SuccessEvent;
import com.dalread.util.BaseBindUtils;
import com.dalread.util.BaseVoca;
import com.dalread.util.BaseVocaKnow;
import com.dalread.util.Constant;
import com.dalread.util.LanguageUtil;
import com.dalread.util.Loading;
import com.dalread.util.ToastUtil;
import com.dalread.util.UserUtil;
import com.dalread.util.Utils;

import org.apache.commons.lang3.StringUtils;

import butterknife.OnClick;
import butterknife.OnFocusChange;

//EditMeaningActivity와 중복되는 코드가 많다. 추상클래스 만들어서 리팩토링 할것
public class EditMeaningVocaBookActivity extends BaseActivity implements OnAsyncTaskListener {
    private final int TYPE_REFRESH_VOCA_AND_OPEN_EDIT_VIEW = 0;

    private IVocaFullItem voca;
    private AlertDialog alertDialog;
    EnumLanguage enumStudyLanguage;
    EnumLanguage enumMotherTongue;
    private int vocabookTypeCode = Constant.API_VALUE.VOCABOOK_TYPE_CODE_SERVER;
    private ActivityEditMeaningBinding binding;

    @Override
    protected View getContentView() {
        binding = ActivityEditMeaningBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        return view;
    }

    @Override
    protected Toolbar getToolbar() {
        return binding.header;
    }

    public static Intent createIntent(Context context, IVocaFullItem iVocaFullItem, int vocabookTypeCode) {
        Intent intent = new Intent(context, EditMeaningVocaBookActivity.class);
        intent.putExtra(Constant.BUNDLE.KEY_VOCA, iVocaFullItem);
        intent.putExtra(Constant.BUNDLE.KEY_VOCABOOK_TYPE_CODE_SERVER, vocabookTypeCode);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        initListener();
        initData();
        initLayout();
        initPronounceSymbolList();
        getServerVocaDataAndUpdateItFirst();
//        hideSaveButtonAtFirst();
    }

    private void hideSaveButtonAtFirst() {
        binding.header.getTvRight().setVisibility(View.GONE);
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

    }
    private boolean isServerVocabook() {
        return vocabookTypeCode == Constant.API_VALUE.VOCABOOK_TYPE_CODE_SERVER;
    }
    @Override
    public void onHeaderTextRightClick() {
        if (hasChange(voca)) {
            if (hasMeaningOrDetailedInfo()) {
                if (isServerVocabook())
                    saveChangesInServerVocabook();
                else
                    saveChangesInUserVocabook();
            } else {
                AlertDialog alertDialog = new AlertDialog(context);
                alertDialog.show(R.string.toast_need_meaning_to_save_voca, R.string.ok, null);
            }

        } else {
            onBackPressed();
        }
    }

    private boolean hasMeaningOrDetailedInfo() {
        return hasMeaning() || hasMeaningDetailed();
    }

    private boolean hasMeaning() {
        return !Utils.isEmpty(binding.etMeaning.getText().toString().trim());
    }

    private boolean hasMeaningDetailed() {
        return !Utils.isEmpty(binding.etDetailInfo.getText().toString().trim());
    }

    public void initData() {
        voca = (IVocaFullItem)getIntent().getSerializableExtra(Constant.BUNDLE.KEY_VOCA);
        vocabookTypeCode = getIntent().getIntExtra(Constant.BUNDLE.KEY_VOCABOOK_TYPE_CODE_SERVER, Constant.API_VALUE.VOCABOOK_TYPE_CODE_SERVER);
        enumStudyLanguage = EnumLanguage.getStudyLanguage(this);
        enumMotherTongue = EnumLanguage.getMotherTongueLanguage(this);
    }

    private void initListener() {
        binding.etMeaning.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (Utils.isEmpty(charSequence.toString().trim())) {
                    binding.header.getTvRight().setVisibility(View.GONE);
                } else {
                    binding.header.getTvRight().setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void initPronounceSymbolList() {
        if (LanguageUtil.isStudyLangEnglish(context)) {
            initRvSymbol(Constant.ENGLISH_PRONUNCIATION.split(" "));
        } else if (LanguageUtil.isStudyLangChinese(context)) {
            initRvSymbol(Constant.CHINESE_PRONUNCIATION.split(" "));
        } else {
            binding.rvSymbol.setVisibility(View.GONE);
        }
    }

    protected void getServerVocaDataAndUpdateItFirst() {
        callAsyncTask(TYPE_REFRESH_VOCA_AND_OPEN_EDIT_VIEW, voca);
    }
    private void callAsyncTask(int type, IVocaBasicItem iVocaBasicItem) {
        new CustomAsyncTask(this, this, iVocaBasicItem, type, true).execute();
    }

    private void initLayout() {
        updateVocaDataInEditView(voca);
        hideMaxMeaningLengthViewForSentence();
        setMeaningMaxLengthForWord();
        showTextViewTitleMeaningEnglish();
        showTextViewTitleMeaningDetailedEnglish();
        if (UserUtil.isDebugOrAdminUser(this)) {
            binding.tvVocaTypeId.setVisibility(View.VISIBLE);
            if (vocabookTypeCode == Constant.API_VALUE.VOCABOOK_TYPE_CODE_USER) {
                binding.tvVocaTypeId.setText("ID in USER_VOCABOOK : " + voca.getVIIdInVocaBook());
            } else if (vocabookTypeCode == Constant.API_VALUE.VOCABOOK_TYPE_CODE_SERVER) {
                binding.tvVocaTypeId.setText("ID in SERVER_VOCABOOK : " + voca.getVIIdInVocaBook());
            }
        } else {
            binding.tvVocaTypeId.setVisibility(View.GONE);
        }
        alertDialog = new AlertDialog(this);
//        disablePronounceView();
    }



    private void setMeaningMaxLengthForWord() {
        int maxLength = 20;
        if (BaseVocaKnow.isVocaTypeSentence(voca))
            maxLength = 999999;
        binding.etMeaning.setFilters(new InputFilter[] {new InputFilter.LengthFilter(maxLength)});
    }
    private void hideMaxMeaningLengthViewForSentence() {
        binding.tvMaxMeaningLength.setVisibility(BaseVocaKnow.isVocaTypeSentence(voca) ? View.GONE : View.VISIBLE);
    }
    private void showTextViewTitleMeaningEnglish() {
        if (LanguageUtil.isStudyLangEnglish(this)) {
            binding.tvMeaningEnglish.setVisibility(View.GONE);
        } else {
            if (LanguageUtil.isMotherTongueLangEnglish(this) || (Utils.isEmpty(voca.getVIMeaningEng()))) {
                binding.tvMeaningEnglish.setVisibility(View.GONE);
            } else {
                binding.tvMeaningEnglish.setVisibility(View.VISIBLE);
                binding.tvMeaningEnglish.setText(getString(R.string.textview_title_meaning_english, voca.getVIMeaningEng()));
            }
        }
    }
    private void showTextViewTitleMeaningDetailedEnglish() {
        if (LanguageUtil.isStudyLangEnglish(this)) {
            binding.tvMeaningEnglishDetailed.setVisibility(View.GONE);
        } else {
            if (LanguageUtil.isMotherTongueLangEnglish(this) || (Utils.isEmpty(voca.getVIMeaningEngDetailed()))) {
                binding.tvMeaningEnglishDetailed.setVisibility(View.GONE);
            } else {
                binding.tvMeaningEnglishDetailed.setVisibility(View.VISIBLE);
                binding.tvMeaningEnglishDetailed.setText(getString(R.string.textview_title_meaning_english_detailed, voca.getVIMeaningEngDetailed()));
            }
        }
    }
    private void initRvSymbol(String[] symbols) {
        int noOfColumns = BaseVoca.calculateNoOfColumns(this, BaseBindUtils.getPronounceSymbolSize(this), BaseBindUtils.getPronounceSymbolMargin(this) * 3);
        binding.rvSymbol.setLayoutManager(new GridLayoutManager(this, noOfColumns));
        binding.rvSymbol.setAdapter(new SymbolAdapter(symbols, new OnSymbolListener() {

            @Override
            public void onClick(String symbol) {
                binding.etPronunciation.append(symbol);
            }
        }));
    }

    @OnFocusChange({R.id.etMeaning, R.id.etDetailInfo})
    void onFocusChanged(View view, boolean hasFocus) {
        if (hasFocus) {
            closeSymbols();
        }
    }

    @OnClick({R.id.btnSymbol, R.id.etWord, R.id.etPronunciation,
            R.id.tvMeaningEnglish, R.id.tvMeaningEnglishDetailed,
            R.id.ivWebDictionary, R.id.tvCloseSymbol})
    void onClick(View view) {
        switch (view.getId()) {
            case R.id.etWord:
                //TODO : this is not called when I click the etWord edit text.
                copyToClipboard(binding.etWord.getText().toString());
                break;
            case R.id.etPronunciation:
                copyToClipboard(binding.etPronunciation.getText().toString());
                break;
            case R.id.btnSymbol:
                showSymbols();
                break;
            case R.id.tvMeaningEnglish:
                copyToClipboard(voca.getVIMeaningEng());
                break;
            case R.id.tvMeaningEnglishDetailed:
                copyToClipboard(voca.getVIMeaningEngDetailed());
                break;
            case R.id.ivWebDictionary:
                openWebDictionary();
                break;
            case R.id.tvCloseSymbol:
                closeSymbols();
                break;
            default:
                break;
        }
    }

    private void copyToClipboard(String text) {
        if (!Utils.isEmpty(text)) {
            Utils.copyToClipboard(this, text, R.string.copied);
        }
    }

    private void openWebDictionary() {

        final WebDictionaryModel webDictionary = WebDictionaryQuery.getFirst(BaseVoca.getRealm(),
                enumStudyLanguage.getIdApi(), enumMotherTongue.getIdApi());
        String url;
        if (webDictionary == null) {
            if (enumMotherTongue.getIdApi() == EnumLanguage.KOREAN.getIdApi()) {
                url = Constant.PLAYER.WEB_DICTIONARY.URL_DEFAULT + voca.getVIVoca();
            } else {
                openWebDictionary();
                return;
            }
        } else {
            url = webDictionary.getUrl().replace(Constant.PLAYER.WEB_DICTIONARY.WORD_REPLACE, voca.getVIVoca());
        }
        final WebDictionaryDialog dialog = new WebDictionaryDialog(this, url, new OnClickDialogListener() {
            @Override
            public void onClick(View view, Object object) {

            }

            @Override
            public void onDismiss(View view, Object object) {

            }
        });
        dialog.show();
    }

    private void showSymbols() {
        boolean hasFocus = binding.etPronunciation.hasFocus();
        binding.etPronunciation.requestFocus();
        if (!hasFocus) {
            String pronunciation = binding.etPronunciation.getText().toString();
            binding.etPronunciation.setSelection(pronunciation.length());
        }
        Utils.hideSoftKeyboard(this, binding.etPronunciation);
        binding.vSymbol.setVisibility(View.VISIBLE);
    }

    private void closeSymbols() {
        binding.vSymbol.setVisibility(View.GONE);
    }

    private boolean hasChange(IVocaBasicItem iVocaBasicItem) {
        //서버의 Response가 Voca값이 없으면 일단 변경이 없다고 하고, 로컬껄 쓰자.
        if (Utils.isEmpty(iVocaBasicItem.getVIVoca()))
            return false;

        return (!binding.etWord.getText().toString().equals(iVocaBasicItem.getVIVoca())
                || !binding.etPronunciation.getText().toString().equals(iVocaBasicItem.getVIPronounce())
                || !binding.etMeaning.getText().toString().equals(iVocaBasicItem.getVIMeaning(LanguageUtil.getMotherTongueLanguage(this)))
                || !binding.etDetailInfo.getText().toString().equals(iVocaBasicItem.getVIMeaningDetailed(LanguageUtil.getMotherTongueLanguage(this))));
    }

//    private boolean hasNoChange() {
//        return etWord.getText().toString().equals(Voca.getVocaDisplay(dicModel))
//                && etPronunciation.getText().toString().equals(dicModel.getPronounce())
//                && etMeaning.getText().toString().equals(dicModel.getMeaning())
//                && etDetailInfo.getText().toString().equals(dicModel.getMeaningDetailed());
//    }

    //이건 VOCABOOK에 저장하기 위함.
    private void saveChages() {
//        application.getDalAiImpl().updateVocaBookMeaningWithID(BaseEvent.Screen.WORD_MEANING, mSharedPref.getToken(), mSharedPref.getUid(), EnumLanguage.findByFormatApi(Constant.API.STUDY_LANG).getIdApi(), mSharedPref.getSettingMotherTongueCode(), wordModel.getVocaId(), wordModel.getVocaType(), wordModel.getVoca(), wordModel.getMeaning(), wordModel.getMeaningDetailed(), wordModel.getPronounce());
    }

    private void saveChangesInUserVocabook() {
        saveEditiedVoca(true);
    }
    private void saveChangesInServerVocabook() {
        int uid = getUserID();
        if (uid > 0) {
            if (Utils.isConnected(this)) {
                Loading.show(this);
                int studyLang = enumStudyLanguage.getIdApi();
                int langDisplay = enumMotherTongue.getIdApi();
                String vocaDisplay = voca.getVIVoca();
                String meaning = binding.etMeaning.getText().toString().trim();
                String meaningDetailed = binding.etDetailInfo.getText().toString().trim();
                int idInVocabook = voca.getVIIdInVocaBook();

                application.getDalAiImpl().updateVocaInSerVocabookDetail(uid, studyLang, langDisplay, vocaDisplay, meaning, meaningDetailed, idInVocabook, vocabookTypeCode,new DalApiListener<Boolean>() {
                    @Override
                    public void onSuccess(Boolean response) {

                        saveEditiedVoca(true);
                        Loading.hide();
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

    private void saveEditiedVoca(boolean isGoBack) {
        voca.setVIPronounce(binding.etPronunciation.getText().toString().trim());
        voca.setVIMeaning(LanguageUtil.getMotherTongueLanguage(this), binding.etMeaning.getText().toString().trim());
        voca.setVIMeaningDetailed(LanguageUtil.getMotherTongueLanguage(this), binding.etDetailInfo.getText().toString().trim());

        application.getEventBus().post(new SuccessEvent(BaseEvent.Screen.EDIT_VOCA, BaseEvent.EventType.DATA_CHANGED, voca));
        if (isGoBack)
            onBackPressed();
    }

    @Override
    public void onInitAsyncTask() {

    }

    @Override
    public void onPrevExecuteAsyncTask() {
        Loading.show(this);
    }

    @Override
    public Object onPostExecuteAsyncTask(int searchType, Object data) {
        switch (searchType) {
            case TYPE_REFRESH_VOCA_AND_OPEN_EDIT_VIEW:
                IVocaFullItem iVocaFullItem = (IVocaFullItem) data;
                if (iVocaFullItem.getVIVocaId() > 0) {
                    refreshVocaAndOpenEditViewByVocaId(iVocaFullItem);
                }
                break;

        }
        return null;
    }

    @Override
    public void onFinishAsyncTask(int searchType, Object resultData, Object data) {
        switch (searchType) {
            case TYPE_REFRESH_VOCA_AND_OPEN_EDIT_VIEW:

                break;
        }

        Loading.hide();
    }

    public void refreshVocaAndOpenEditViewByVocaId(IVocaFullItem iVocaFullItem) {
        final int uid = sharedPreferences.getRealUid();

        Loading.show(this);
        application.getDalAiImpl().getServerVocaBookInfo(
                String.valueOf(uid),
                sharedPreferences.getLangStudyCode(),
                sharedPreferences.getMotherTongueLangCode(),
                iVocaFullItem.getVIIdInVocaBook(),
                vocabookTypeCode,
                new DalApiListener<VocaDetailInfo>() {
                    @Override
                    public void onSuccess(VocaDetailInfo response) {
                        Loading.hide();
                        if ((response != null) && (hasChange(response))) {
                            ToastUtil.getInstance(EditMeaningVocaBookActivity.this).show(R.string.updated_with_latest_data_from_server);
                            updateVocaDataInEditView(response);
                            saveEditiedVoca(false);
                        }
                    }

                    @Override
                    public void onFailure(String error) {
                        Loading.hide();
                    }
                }
        );
    }

    private void updateVocaDataInEditView(IVocaBasicItem item) {
        binding.etWord.setText(item.getVIVoca());
        binding.etMeaning.setText(item.getVIMeaning(enumMotherTongue));
        binding.etDetailInfo.setText(item.getVIMeaningDetailed(enumMotherTongue));

        updatePronounceDataInEditView(item.getVIPronounce());
    }

    private void updatePronounceDataInEditView(String pronunciation) {
        enablePronounceEditView(pronunciation);
        binding.etPronunciation.setText(pronunciation);
        binding.etMeaning.postDelayed(new Runnable() {
            @Override
            public void run() {
                Utils.showSoftKeyboard(EditMeaningVocaBookActivity.this, binding.etMeaning);
            }
        }, Constant.ON_RESUME_DELAY);
    }

    private void enablePronounceEditView(String pronunciation) {
        if (UserUtil.isDebugOrAdminUser(this)) {
            binding.etPronunciation.setEnabled(true);
        } else {
            if (LanguageUtil.isStudyLangKorean(this)) {
                binding.llPronounce.setVisibility(View.GONE);
            } else {
                binding.etPronunciation.setEnabled(StringUtils.isEmpty(pronunciation) ? true : false);
            }
        }
    }
}