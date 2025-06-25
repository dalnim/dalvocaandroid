package com.dalread.dialog;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dalread.DalRealApplication;
import com.dalread.R;
import com.dalread.base.BaseDialog;
import com.dalread.base.VocaActivity;
import com.dalread.listener.OnRubyWordDefinitionListener;
import com.dalread.model.RubyTextModel;
import com.dalread.model.VocaDetailInfo;
import com.dalread.network.DalApiListener;
import com.dalread.util.DLog;
import com.dalread.util.Loading;
import com.dalread.util.VocaKnow;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.ResponseBody;

@SuppressLint("NonConstantResourceId")
public class RubyWordDefinitionDialog extends BaseDialog implements DialogInterface.OnDismissListener {

    @BindView(R.id.llAmkiGrade)
    RelativeLayout llAmkiGrade;
    @BindView(R.id.llEdit)
    LinearLayout llEdit;
    @BindView(R.id.tvWord)
    TextView tvWord;
    @BindView(R.id.ivBookmark)
    ImageView ivBookmark;
    @BindView(R.id.tvKnownWord)
    CheckedTextView tvKnownWord;
    @BindView(R.id.tvKnownPronounce)
    CheckedTextView tvKnownPronounce;
    @BindView(R.id.etPronuncation)
    EditText etPronuncation;
    @BindView(R.id.etMeaning)
    EditText etMeaning;
    private final VocaActivity activity;
    private final DalRealApplication application;
    private VocaDetailInfo vocaDetailInfo;
    private RubyTextModel rubyTextModel;
    private final OnRubyWordDefinitionListener listener;
//    private boolean isShowKnownPronounce = false;

    public RubyWordDefinitionDialog(VocaActivity activity,
                                    VocaDetailInfo vocaDetailInfo,
                                    RubyTextModel rubyTextModel,
                                    OnRubyWordDefinitionListener listener) {
        super(activity);

        setContentView(R.layout.dialog_ruby_word_definition);
        ButterKnife.bind(this);
        this.activity = activity;
        this.application = (DalRealApplication) activity.getApplication();
        this.listener = listener;
        initLayout();
        updateData(vocaDetailInfo, rubyTextModel);
        setOnDismissListener(this);
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
    }

    private void reloadFuriganaView() {
        if (listener != null) {
            listener.refreshData(RubyWordDefinitionDialog.this.rubyTextModel);
        }
    }

    private void initLayout() {
        etMeaning.setOnEditorActionListener(editorActionListener);
        etPronuncation.setOnEditorActionListener(editorActionListener);
    }

    public void updateData(VocaDetailInfo vocaDetailInfo, RubyTextModel rubyTextModel) {
        this.vocaDetailInfo = vocaDetailInfo;
        this.rubyTextModel = rubyTextModel;
        initData();
    }

    private void initData() {
        if (vocaDetailInfo == null) {
            dismiss();
            return;
        }
        ivBookmark.setSelected(rubyTextModel.isBookmark());
        tvWord.setText(vocaDetailInfo.getVocaDisplay());
        updateKnownWordSelect();
        updateKnownPronounceSelect();
        etPronuncation.setText(vocaDetailInfo.getPronounce());
        etMeaning.setText(vocaDetailInfo.getMeaning());
//        checkShowHidePronunciationAndKnownPronunciation();
        updateAmkiGrade();
    }

    private void updateKnownWordSelect() {
        tvKnownWord.setSelected(rubyTextModel.isKnown());
    }

    private void updateKnownPronounceSelect() {
        tvKnownPronounce.setSelected(rubyTextModel.isKnownPronounce());
    }

    private void updateAmkiGrade() {
        llAmkiGrade.setVisibility(!tvKnownWord.isSelected()
                && tvKnownPronounce.isSelected()
//                && isShowKnownPronounce
                ? View.GONE : View.VISIBLE);
    }

//    private void checkShowHidePronunciationAndKnownPronunciation() {
//        isShowKnownPronounce = Voca.checkStudyLanguageJPCN(mSharedPref);
//        DLog.e(TAG, "isShowKnownPronounce=" + isShowKnownPronounce);
//        if (isShowKnownPronounce) {
//            etPronuncation.setVisibility(View.VISIBLE);
//            tvKnownPronounce.setVisibility(View.VISIBLE);
//        } else {
//            etPronuncation.setVisibility(View.GONE);
//            tvKnownPronounce.setVisibility(View.GONE);
//        }
//    }

    @OnClick({R.id.ivClose,
            R.id.ivVol, R.id.ivBookmark, R.id.llAmkiGrade,
            R.id.tvKnownWord, R.id.tvKnownPronounce,
            R.id.studyButton, R.id.moreButton})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivClose:
                dismiss();
                break;
            case R.id.ivVol:
                break;
            case R.id.ivBookmark:
                handleBookmark();
                break;
            case R.id.llAmkiGrade:
                handleChangeVocaKnow();
                break;
            case R.id.tvKnownWord:
                handleKnownWord();
                break;
            case R.id.tvKnownPronounce:
                handleKnownPronounce();
                break;
            case R.id.studyButton:
                break;
            case R.id.moreButton:
                break;
        }
    }

    private void handleBookmark() {
        Loading.show(mContext);
        if (ivBookmark.isSelected()) {
            deleteFromBookmark();
        } else {
            addToBookmark();
        }
    }

    private void addToBookmark() {
        application.getDalAiImpl().addToBookmark(vocaDetailInfo.getVoca(), String.valueOf(rubyTextModel.getVocaId()), rubyTextModel.getVocaType(), bookmarkListener);
    }

    private void deleteFromBookmark() {
        application.getDalAiImpl().deleteFromBookmark(vocaDetailInfo.getVoca(), String.valueOf(rubyTextModel.getVocaId()), rubyTextModel.getVocaType(), bookmarkListener);
    }

    private final DalApiListener<ResponseBody> bookmarkListener = new DalApiListener<ResponseBody>() {
        @Override
        public void onSuccess(ResponseBody response) {
            ivBookmark.setSelected(!ivBookmark.isSelected());
            rubyTextModel.updateBookmark();
            reloadFuriganaView();
            Loading.hide();
        }

        @Override
        public void onFailure(String error) {
            Loading.hide();
            DLog.e(TAG, "bookmarkListener error");
        }
    };

    private void handleKnownWord() {
        Loading.show(mContext);
        if (tvKnownWord.isSelected()) {
            wordUnKnown();
        } else {
            wordKnown();
        }
    }

    private void wordKnown() {
        application.getDalAiImpl().setWordKnown(vocaDetailInfo.getVoca(), String.valueOf(rubyTextModel.getVocaId()), wordKnownListener);
    }

    private void wordUnKnown() {
        application.getDalAiImpl().setWordUnknown(vocaDetailInfo.getVoca(), String.valueOf(rubyTextModel.getVocaId()), wordKnownListener);
    }

    private final DalApiListener<ResponseBody> wordKnownListener = new DalApiListener<ResponseBody>() {
        @Override
        public void onSuccess(ResponseBody response) {
            rubyTextModel.updateKnownWord();
            updateKnownWordSelect();
//            if (!isShowKnownPronounce) {
//                handleKnownPronounce(!tvKnownWord.isSelected());
//            }
            updateAmkiGrade();
            reloadFuriganaView();
            Loading.hide();
        }

        @Override
        public void onFailure(String error) {
            Loading.hide();
            DLog.e(TAG, "wordKnownListener error");
        }
    };

    private void handleChangeVocaKnow() {
        Loading.show(mContext);
        application.getDalAiImpl().changeMultipleVocaKnow(
                String.valueOf(vocaDetailInfo.getVocaKnow()),
                String.valueOf(VocaKnow.getVocaKnowPronounceByVocaKnow(vocaDetailInfo.getVocaKnow())),
                String.valueOf(vocaDetailInfo.getVocaId()),
                String.valueOf(vocaDetailInfo.getVocaType()),
                new DalApiListener<Boolean>() {
                    @Override
                    public void onSuccess(Boolean response) {
                        DLog.e(TAG, "changeAmkiGrade onSuccess=" + response);
                        Loading.hide();
                    }

                    @Override
                    public void onFailure(String error) {
                        DLog.e(TAG, "changeAmkiGrade onFailure");
                        Loading.hide();
                    }
                });
    }

    private void handleKnownPronounce() {
        handleKnownPronounce(tvKnownPronounce.isSelected());
    }

    private void handleKnownPronounce(boolean isSelected) {
        Loading.show(mContext);
        if (isSelected) {
            setPronounceUnknown();
        } else {
            setPronounceKnown();
        }
    }

    private void setPronounceKnown() {
        application.getDalAiImpl().setPronounceKnown(vocaDetailInfo.getVoca(), String.valueOf(rubyTextModel.getVocaId()), pronounceKnownListener);
    }

    private void setPronounceUnknown() {
        application.getDalAiImpl().setPronounceUnknown(vocaDetailInfo.getVoca(), String.valueOf(rubyTextModel.getVocaId()), pronounceKnownListener);
    }

    private final DalApiListener<ResponseBody> pronounceKnownListener = new DalApiListener<ResponseBody>() {
        @Override
        public void onSuccess(ResponseBody response) {
            rubyTextModel.updateKnownPronounce();
            updateKnownPronounceSelect();
            updateAmkiGrade();
            reloadFuriganaView();
            Loading.hide();
        }

        @Override
        public void onFailure(String error) {
            Loading.hide();
            DLog.e(TAG, "pronounceKnownListener error");
        }
    };

    private final EditText.OnEditorActionListener editorActionListener = new EditText.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                handleUpdateWordMeaning();
                return true;
            }
            return false;
        }
    };

    private void handleUpdateWordMeaning() {
        final String strPronounce = etPronuncation.getText().toString();
        final String strMeaning = etMeaning.getText().toString();
        if (vocaDetailInfo.getMeaning() != null
                && vocaDetailInfo.getPronounce() != null
                && vocaDetailInfo.getMeaning().equals(strMeaning)
                && vocaDetailInfo.getPronounce().equals(strPronounce))
            return;
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Loading.show(mContext);
                application.getDalAiImpl().updateWordMeaningWithID(
                        String.valueOf(vocaDetailInfo.getVocaId()),
                        vocaDetailInfo.getVocaType(),
                        vocaDetailInfo.getVoca(),
                        strMeaning,
                        vocaDetailInfo.getMeaningDetailed(),
                        strPronounce,
                        new DalApiListener<Boolean>() {
                            @Override
                            public void onSuccess(Boolean response) {
                                DLog.e(TAG, "updateWordMeaning onSuccess");
                                vocaDetailInfo.setPronounce(strPronounce);
                                rubyTextModel.updateWordPronounce(strPronounce);
                                vocaDetailInfo.setMeaning(strMeaning);
                                rubyTextModel.updateWordMeaning(strMeaning);
                                reloadFuriganaView();
                                Loading.hide();
                            }

                            @Override
                            public void onFailure(String error) {
                                DLog.e(TAG, "updateWordMeaning onFailure");
                                Loading.hide();
                            }
                        });
            }
        });
    }
}
