package com.dalread.activity;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.dalread.R;
import com.dalread.base.VocaActivity;
import com.dalread.dialog.AlertDialog;
import com.dalread.model.HanjaQuizItem;
import com.dalread.model.VocaStudyChatExam;
import com.dalread.util.BaseVoca;
import com.dalread.util.Constant;
import com.dalread.util.DLog;
import com.dalread.util.ToastUtil;
import com.dalread.util.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.OnClick;

public class HanjaQuizWritingActivity extends VocaActivity {

    @BindView(R.id.tv_all_words_count)
    TextView tvAllWordsCount;
    @BindView(R.id.tv_last_word)
    TextView tvLastWord;
    @BindView(R.id.tv_character_count)
    TextView tvCharacterCount;
    @BindView(R.id.v_wrong)
    View vWrong;
    @BindView(R.id.tv_wrong_count)
    TextView tvWrongCount;
    @BindView(R.id.v_correct)
    View vCorrect;
    @BindView(R.id.tv_correct_count)
    TextView tvCorrectCount;
    @BindView(R.id.tv_practice_index)
    TextView tvPracticeIndex;
    @BindView(R.id.tv_parent_voca)
    TextView tvParentVoca;
    @BindView(R.id.ic_visible)
    ImageView icVisible;
    @BindView(R.id.pb_time)
    ProgressBar pbTime;
    @BindView(R.id.wv_hanzi_writer)
    WebView wvHanziWriter;
    @BindView(R.id.tv_meaning)
    TextView tvMeaning;

    @BindString(R.string.tpl_all_words_count)
    String tplAllWordsCount;
    @BindString(R.string.tpl_practice_index)
    String tplPracticeIndex;
    @BindString(R.string.tpl_score_and_answer_again)
    String tplScoreAndAnswerAgain;
    @BindString(R.string.tpl_no_support_character)
    String tplNoSupportCharacter;

    private static final String WEB_INTERFACE_NAME = "AndroidInterface";

    private Context context;
    private int quizCount;
    private List<HanjaQuizItem> hanjaList;
    private int hanjaPos;
    private List<HanjaQuizItem> wrongHanjaList;
    private int practiceIndex;
    private boolean visible;
    private boolean buttonClicked;
    private AlertDialog alertDialog;
    private ObjectAnimator objectAnimator;
    private boolean cancelAnimator;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_hanja_quiz_writing;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initData();
        initView();
    }

    @SuppressWarnings("unchecked")
    private void initData() {
        context = this;
        hanjaList = new ArrayList<>();
        if (getIntent().getExtras().containsKey(Constant.BUNDLE.KEY_QUIZ_LIST)) {
            List<VocaStudyChatExam> quizList = (List<VocaStudyChatExam>) getIntent().getSerializableExtra(Constant.BUNDLE.KEY_QUIZ_LIST);
            quizCount = quizList.size();
            for (VocaStudyChatExam quiz : quizList) {
                for (HanjaQuizItem hanja : quiz.getHanjaList()) {
                    hanja.setHQIParentVoca(quiz.getVoca());
                    hanjaList.add(hanja);
                }
            }
        } else if (getIntent().getExtras().containsKey(Constant.BUNDLE.KEY_QUIZ_HANJA_LIST)) {
            List<HanjaQuizItem> vocas = (List<HanjaQuizItem>) getIntent().getSerializableExtra(Constant.BUNDLE.KEY_QUIZ_HANJA_LIST);
            quizCount = vocas.size();
            hanjaList.addAll(vocas);
        }
        hanjaPos = -1;
        wrongHanjaList = new ArrayList<>();
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initView() {
        //
        wvHanziWriter.getSettings().setJavaScriptEnabled(true);
        wvHanziWriter.addJavascriptInterface(new HanziWriterWebInterface(), WEB_INTERFACE_NAME);
        wvHanziWriter.loadUrl("file:///android_asset/hanziwriter/index.html");
        //
        alertDialog = new AlertDialog(context);
        //
        objectAnimator = ObjectAnimator.ofInt(pbTime, "progress", 100, 0);
        objectAnimator.setDuration(Constant.ANSWER_HANJA_QUIZ_TIME);
        objectAnimator.setInterpolator(new LinearInterpolator());
        objectAnimator.addListener(animatorListener);
        //
        tvAllWordsCount.setText(String.format(tplAllWordsCount, quizCount));
    }

    private void startCharacter() {
        if (hanjaPos < hanjaList.size()) {
            wvHanziWriter.post(() -> {
                if (hanjaPos >= 0) {
                    HanjaQuizItem hanja = hanjaList.get(hanjaPos);
                    tvLastWord.setText(hanja.getHQIMeaningWithPronounceForHanja());
                } else {
                    tvLastWord.setText("");
                    tvWrongCount.setText("0");
                    tvCorrectCount.setText("0");
                }
                //
                String text = String.valueOf(hanjaList.size() - 1 - hanjaPos);
                tvCharacterCount.setText(text);
                //
                practiceIndex = Constant.ANSWER_HANJA_QUIZ_START_INDEX;
                //
                if (++hanjaPos < hanjaList.size()) {
                    visible = true;
                    updateCharacter();
                    animateCharacter();
                    setPracticeIndexText();
                    setVocaText();
                    setVisibleIcon();
                } else {
                    /*if (wrongHanjaList.isEmpty()) {*/
                    alertDialog.show(R.string.msg_finish_quiz, 0, null);
                /*} else {
                    alertDialog.show(
                            String.format(tplScoreAndAnswerAgain, Constant.TEST_MAX_SCORE / hanjaList.size() * (hanjaList.size() - wrongHanjaList.size())),
                            "",
                            (dialog, which) -> {
                                hanjaList.clear();
                                hanjaList.addAll(wrongHanjaList);
                                hanjaPos = -1;
                                wrongHanjaList.clear();
                                startCharacter();
                                dialog.dismiss();
                            }
                    );
                }*/
                }
            });
        }
    }

    private void repeatCharacter() {
        if (++practiceIndex <= Constant.ANSWER_HANJA_QUIZ_REPEAT_COUNT) {
            visible = false;
            refreshQuiz();
            setPracticeIndexText();
            setVocaText();
            setVisibleIcon();
        } else {
            startCharacter();
        }
    }

    private void updateCharacter() {
        wvHanziWriter.post(() -> {
            String character = hanjaList.get(hanjaPos).getHQIVoca();
            /*if (hanjaPos == 0) {
                character = "敎";
            }*/
            int boxWidth = wvHanziWriter.getWidth();
            DLog.i("updateCharacter", "boxWidth = " + boxWidth);
            int boxHeight = wvHanziWriter.getHeight();
            DLog.i("updateCharacter", "boxHeight = " + boxHeight);
            float density = context.getResources().getDisplayMetrics().density;
            DLog.i("updateCharacter", "density = " + density);
            wvHanziWriter.evaluateJavascript(
                    String.format("javascript: updateCharacter(\"%1$s\", true, %2$s, %3$s)", character, boxWidth / density, boxHeight / density),
                    null
            );
        });
    }

    private void animateCharacter() {
        wvHanziWriter.post(() -> wvHanziWriter.evaluateJavascript(
                "javascript: animateCharacter()",
                null
        ));
    }

    private void noSupportCharacter() {
        wvHanziWriter.post(() -> {
            String character = hanjaList.get(hanjaPos).getHQIVoca();
            ToastUtil.getInstance(context).show(String.format(tplNoSupportCharacter, character));
        });
        wvHanziWriter.postDelayed(this::onCorrect, Constant.TOAST_SHORT_TIME);
    }

    private void refreshQuiz() {
        wvHanziWriter.post(() -> {
            String showOutline = String.valueOf(visible);
            wvHanziWriter.evaluateJavascript(
                    String.format("javascript: refreshQuiz(%s)", showOutline),
                    null
            );
            if (buttonClicked) {
                buttonClicked = false;
            } else {
                startTimer();
            }
            if (practiceIndex < Constant.ANSWER_HANJA_QUIZ_REPEAT_COUNT) {
                highlightStroke(0);
            }
        });
    }

    private void highlightStroke(int num) {
        wvHanziWriter.post(() -> {
            String strokeNum = String.valueOf(num);
            wvHanziWriter.evaluateJavascript(
                    String.format("javascript: highlightStroke(%s)", strokeNum),
                    null
            );
        });
    }

    private void setPracticeIndexText() {
        tvPracticeIndex.setText(String.format(tplPracticeIndex, practiceIndex, Constant.ANSWER_HANJA_QUIZ_REPEAT_COUNT));
    }

    private void setVocaText() {
        if (Utils.isIndexInsideRange(hanjaList, hanjaPos)) {
            HanjaQuizItem voca = hanjaList.get(hanjaPos);
            String parentVoca = voca.getHQIParentVoca();
            int start = voca.getHQIIndexHanja();
            if (!visible) {
                parentVoca = parentVoca.substring(0, start) + "*" + (start < parentVoca.length() - 1 ? parentVoca.substring(start + 1) : "");
            }
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(parentVoca);
            spannableStringBuilder.setSpan(new ForegroundColorSpan(Color.BLACK), start, start + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            tvParentVoca.setText(spannableStringBuilder);
            String meaning = voca.getHQIMeaningWithPronounceForHanja();
            if (!visible) {
                meaning = meaning.replace(voca.getHQIVoca(), "*");
            }
            tvMeaning.setText(meaning);
        }
    }

    private void setVisibleIcon() {
        icVisible.setImageResource(visible ? R.drawable.ic_visibility_black_24dp : R.drawable.ic_visibility_off_black_24dp);
    }

    private void startTimer() {
        wvHanziWriter.post(() -> objectAnimator.start());
    }

    private void stopTimer() {
        wvHanziWriter.post(() -> objectAnimator.cancel());
    }

    private Animator.AnimatorListener animatorListener = new Animator.AnimatorListener() {

        @Override
        public void onAnimationStart(Animator animation) {
            cancelAnimator = false;
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            if (isActivityAvailable()) {
                if (cancelAnimator) {
                    cancelAnimator = false;
                    return;
                }
                onWrong();
            }
        }

        @Override
        public void onAnimationCancel(Animator animation) {
            cancelAnimator = true;
        }

        @Override
        public void onAnimationRepeat(Animator animation) {
        }
    };

    private void onWrong() {
        wvHanziWriter.post(() -> {
            HanjaQuizItem voca = hanjaList.get(hanjaPos);
            if (!wrongHanjaList.contains(voca)) {
                wrongHanjaList.add(voca);
                tvWrongCount.setText(String.valueOf(wrongHanjaList.size()));
                BaseVoca.blinkView(vWrong);
            }
        });
        wvHanziWriter.postDelayed(this::repeatCharacter, Constant.STOP_BLINK_DELAY_TIME);
    }

    private void onCorrect() {
        wvHanziWriter.post(() -> {
            tvCorrectCount.setText(String.valueOf(hanjaPos + 1 - wrongHanjaList.size()));
            BaseVoca.blinkView(vCorrect);
        });
        wvHanziWriter.postDelayed(this::startCharacter, Constant.STOP_BLINK_DELAY_TIME);
    }

    @OnClick({R.id.ic_undo})
    void onUndoClick() {
        buttonClicked = true;
        animateCharacter();
    }

    @OnClick({R.id.ic_visible})
    void onVisibleClick() {
        visible = !visible;
        wvHanziWriter.post(() -> {
            String showOutline = String.valueOf(visible);
            wvHanziWriter.evaluateJavascript(
                    String.format("javascript: showWriterQuiz(%s)", showOutline),
                    null
            );
            setVocaText();
            setVisibleIcon();
        });
    }

    @OnClick({R.id.ic_refresh})
    void onRefreshClick() {
        buttonClicked = true;
        refreshQuiz();
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

    @Override
    public void onHeaderTextRightClick() {

    }

    @Override
    protected void onDestroy() {
        wvHanziWriter.removeJavascriptInterface(WEB_INTERFACE_NAME);

        objectAnimator.removeAllListeners();
        objectAnimator.cancel();
        objectAnimator = null;

        super.onDestroy();
    }

    private boolean isActivityAvailable() {
        return !isFinishing() && !isDestroyed();
    }

    private class HanziWriterWebInterface {

        private String summaryData;

        @JavascriptInterface
        public void onHtmlLoaded() {
            if (isActivityAvailable()) {
                startCharacter();
            }
        }

        @JavascriptInterface
        public void onLoadCharDataError() {
            if (isActivityAvailable()) {
                noSupportCharacter();
            }
        }

        @JavascriptInterface
        public void onAnimateCharacterCompleted() {
            if (isActivityAvailable()) {
                if (TextUtils.isEmpty(summaryData)) {
                    refreshQuiz();
                } else {
                    try {
                        JSONObject jsonObject = new JSONObject(summaryData)
                                .getJSONObject("quizOnComplete");
                        int totalMistakes = jsonObject.getInt("totalMistakes");
                        if (totalMistakes > 0) {
                            onWrong();
                        } else {
                            if (practiceIndex < Constant.ANSWER_HANJA_QUIZ_REPEAT_COUNT) {
                                repeatCharacter();
                            } else {
                                onCorrect();
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    summaryData = null;
                }
            }
        }

        @JavascriptInterface
        public void onHighlightStrokeCompleted(int strokeNum) {
        }

        @JavascriptInterface
        public void onCorrectStroke(String strokeData) {
            if (isActivityAvailable()) {
                if (visible && practiceIndex > Constant.ANSWER_HANJA_QUIZ_START_INDEX) {
                    onVisibleClick();
                }
                if (practiceIndex < Constant.ANSWER_HANJA_QUIZ_REPEAT_COUNT) {
                    try {
                        JSONObject jsonObject = new JSONObject(strokeData)
                                .getJSONObject("quizOnCorrectStroke");
                        int strokesRemaining = jsonObject.getInt("strokesRemaining");
                        if (strokesRemaining > 0) {
                            int strokeNum = jsonObject.getInt("strokeNum");
                            highlightStroke(strokeNum + 1);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        @JavascriptInterface
        public void onQuizCompleted(String summaryData) {
            if (isActivityAvailable()) {
                stopTimer();
                this.summaryData = summaryData;
                animateCharacter();
            }
        }
    }
}
