package com.dalread.activity;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.UtteranceProgressListener;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.LinearInterpolator;

import androidx.annotation.Nullable;

import com.dalread.R;
import com.dalread.adapter.QuizAdapter;
import com.dalread.base.BaseVocaKnowActivity;
import com.dalread.base.EnumLanguage;
import com.dalread.component.Toolbar;
import com.dalread.composition.PlayTTS;
import com.dalread.databinding.ActivityQuizBinding;
import com.dalread.dialog.AlertDialog;
import com.dalread.helper.PlayVocaHelper;
import com.dalread.listener.OnClickListener;
import com.dalread.model.AmkiItem;
import com.dalread.model.VocaStudyChatExam;
import com.dalread.network.DalApiListener;
import com.dalread.network.events.BaseEvent;
import com.dalread.network.events.SuccessEvent;
import com.dalread.util.BaseVoca;
import com.dalread.util.Constant;
import com.dalread.util.DLog;
import com.dalread.util.Loading;
import com.dalread.util.Utils;
import com.yuyakaido.android.cardstackview.CardStackLayoutManager;
import com.yuyakaido.android.cardstackview.CardStackListener;
import com.yuyakaido.android.cardstackview.Direction;
import com.yuyakaido.android.cardstackview.StackFrom;
import com.yuyakaido.android.cardstackview.SwipeAnimationSetting;
import com.yuyakaido.android.cardstackview.SwipeableMethod;

import org.greenrobot.eventbus.Subscribe;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindArray;
import butterknife.BindString;

public class QuizPlayerActivity extends BaseVocaKnowActivity {
    @BindString(R.string.tpl_all_words_count)
    String tplAllWordsCount;
    @BindString(R.string.tpl_score_and_answer_again)
    String tplScoreAndAnswerAgain;
    @BindArray(R.array.quiz_colors)
    int[] quizColors;

    private int langStudyCode, langMeaningCode;
    private boolean hasHanja;
    private List<VocaStudyChatExam> quizList;
    private int quizPos;
    private List<VocaStudyChatExam> wrongQuizList;
    private boolean showLastWordPronounce;
    private AlertDialog alertDialog;
    private CardStackLayoutManager layoutManager;
    private QuizAdapter adapter;
    private ObjectAnimator objectAnimator;
    private boolean cancelAnimator;
    private boolean canClickCard;
    private int quizStatusId;
    private long solvingTimeStart;
    private long solvingTimeEnd;
    private int multipleChoiceQuestionType, maxQuizCount;
    private PlayVocaHelper playVocaHelper;
    private PlayTTS playTTS;
    private ActivityQuizBinding binding;

    @Override
    protected View getContentView() {
        binding = ActivityQuizBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        return view;
    }

    @Override
    protected Toolbar getToolbar() {
        return binding.header;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        playTTS = new PlayTTS(this);
        playVocaHelper = new PlayVocaHelper(this);
//        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initPlayVocaHelperListener();
    }

    @Override
    public void initLayout() {
        super.initLayout();
        alertDialog = new AlertDialog(context);
        layoutManager = new CardStackLayoutManager(context, cardStackListener);
        layoutManager.setStackFrom(StackFrom.Bottom);
        layoutManager.setVisibleCount(3);
        layoutManager.setTranslationInterval(8f);
        layoutManager.setScaleInterval(.95f);
        layoutManager.setSwipeThreshold(.3f);
        layoutManager.setMaxDegree(20f);
        layoutManager.setDirections(Direction.HORIZONTAL);
        layoutManager.setCanScrollHorizontal(true);
        layoutManager.setCanScrollVertical(false);
        layoutManager.setSwipeableMethod(SwipeableMethod.Automatic);
        layoutManager.setOverlayInterpolator(new LinearInterpolator());
        binding.csvQuestion.setLayoutManager(layoutManager);
        adapter = new QuizAdapter(this, quizColors);
        adapter.setListener(onCardClickListener);
//        adapter.setOnKnowChangeListener(onKnowChangeListener);
        binding.csvQuestion.setAdapter(adapter);
        objectAnimator = ObjectAnimator.ofInt(binding.pbTime, "progress", 100, 0);
        objectAnimator.setDuration(Constant.ANSWER_QUIZ_TIME);
        objectAnimator.setInterpolator(new LinearInterpolator());
        objectAnimator.addListener(animatorListener);
        updateData();
    }

    @Override
    public void initData() {

        quizList = new ArrayList<>();
        this.langStudyCode = EnumLanguage.findByFormatApi(sharedPreferences.getStudyLanguage()).getIdApi(); //ENGLISH.getIdApi();
        this.langMeaningCode = EnumLanguage.findByFormatApi(sharedPreferences.getMenuLanguage()).getIdApi(); //EnumLanguage.KOREAN.getIdApi();
        getQuizList();
        quizPos = -1;
        wrongQuizList = new ArrayList<>();
        showLastWordPronounce = EnumLanguage.JAPANESE.getFormatApi().equals(sharedPreferences.getStudyLanguage())
                || EnumLanguage.CHINESE_SIMPLIFIED.getFormatApi().equals(sharedPreferences.getStudyLanguage());
//        playVocaHelper.setIncludeMeaning(false);

    }

    private void getQuizList() {
        String quizSource = getIntent().getStringExtra(Constant.INTENT.KEY_QUIZ_SOURCE);
        if (quizSource.equals(Constant.INTENT.QUIZ_SOURCE.LOCAL)) {
//            path = getIntent().getStringExtra(Constant.PLAYER.INTENT.KEY_PATH);
            quizList = (List<VocaStudyChatExam>) getIntent().getSerializableExtra(Constant.PLAYER.INTENT.KEY_DATA);
//            createSubDatabase(path); //Dalnim : Why need this?
        } else {
//            generateQuizType(sharedPreferences.getPlayerQuizType());KEY_MULTIPLE_CHOICE_QUIZ_TYPE
            multipleChoiceQuestionType = getIntent().getIntExtra(Constant.INTENT.KEY_MULTIPLE_CHOICE_QUESTION_TYPE, Constant.MULTIPLE_CHOICE_QUESTION_TYPE.VOCA);
            maxQuizCount = Utils.parseInt(Constant.PLAYER.QUIZ.MAX_QUIZ_COUNT_RANGE[sharedPreferences.getPlayerMaxQuizCount()]);
            if (Utils.isConnected(context)) {
                Loading.show(context);
                    application.getDalAiImpl().makeQuizAtQuizMenu(
                            langStudyCode,
                            langMeaningCode,
                            multipleChoiceQuestionType,
                            EnumLanguage.HANJA.getFormatApi().equals(langStudyCode) ?
                                    sharedPreferences.getMaxHanjaQuiz() :
                                    maxQuizCount,
                            getDataListener
                    );
            } else {
                alertDialog.showNoInternet();
            }
        }
    }

    private DalApiListener<List<VocaStudyChatExam>> getDataListener = new DalApiListener<List<VocaStudyChatExam>>() {

        @Override
        public void onSuccess(List<VocaStudyChatExam> response) {
            if (response == null || response.isEmpty()) {
                binding.tvAllWordsCount.setText(String.format(tplAllWordsCount, 0));
            } else {
                updateQuizStartFinishStatus(false);
                binding.tvAllWordsCount.setText(String.format(tplAllWordsCount, response.size()));
                int studyLang = EnumLanguage.findByFormatApi(sharedPreferences.getStudyLanguage()).getIdApi();
                for (int i = 0; i < response.size(); i++) {
                    VocaStudyChatExam voca = response.get(i);
                    voca.setId(i + 1);
                    voca.setPath(BaseVoca.getOutputRecordingFileName(studyLang, voca.getType(), voca.getVocaId(), getUserID()));
//                    voca.setBookmark(getBookmarkFromData(voca.getVocaId()));
                    quizList.add(voca);
                    if (!voca.getHanjaList().isEmpty()) {
                        hasHanja = true;
                    }
                }
                adapter.setQuizList(quizList);
                adapter.notifyDataSetChanged();
                bindData();
            }
            Loading.hide();
        }

        @Override
        public void onFailure(String error) {
            Loading.hide();
        }
    };

//    private int generateMultipleChoiceQuestionType() {
//        int type;
//        switch (sharedPreferences.getPlayerQuizType()) {
//            case 0:
//                type = Constant.MULTIPLE_CHOICE_QUESTION_TYPE.VOCA;
//                break;
//            case 1:
//                type = Constant.MULTIPLE_CHOICE_QUESTION_TYPE.MEANING;
//                break;
//            default:
//                type = Constant.MULTIPLE_CHOICE_QUESTION_TYPE.RANDOM;
//                break;
//        }
//        return type;
//    }

    @Override
    public void onBackPressed() {
        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }

    @Subscribe
    public void onEvent(SuccessEvent successEvent) {
        if (successEvent.getScreen() == BaseEvent.Screen.MAIN) {
            BaseEvent.EventType type = successEvent.getEventType();
            DLog.d(getLogTag(), "successEvent=" + successEvent);
        }
    }

    private void updateQuizStartFinishStatus(boolean isFinish) {
        if (Utils.isConnected(context)) {
            application.getDalAiImpl().updateQuizStartFinishStatus(
                    quizStatusId,
                    isFinish ? 1 : 0,
                    langStudyCode,
                    isFinish ? null : new DalApiListener<Integer>() {

                        @Override
                        public void onSuccess(Integer response) {
                            quizStatusId = response;
                        }

                        @Override
                        public void onFailure(String error) {
                        }
                    }
            );
        } else {
//            alertDialog.showNoInternet();
        }
    }

    private void bindData() {
        String text;
        if (quizPos >= 0) {
            VocaStudyChatExam voca = quizList.get(quizPos);
            text = BaseVoca.getVocaDisplay(voca);
            if (!TextUtils.isEmpty(voca.getMeaning())) {
                text += "\n" + voca.getMeaning();
            }
            if (showLastWordPronounce && !TextUtils.isEmpty(voca.getPronounce())) {
                text += "\n" + voca.getPronounce();
            }
            binding.tvLastWord.setText(text);
        } else {
            binding.tvLastWord.setText("");
            binding.tvWrongCount.setText("0");
            binding.tvCorrectCount.setText("0");
        }
        //
        text = String.valueOf(quizList.size() - 1 - quizPos);
        binding.tvQuestionCount.setText(text);
        //
        if (++quizPos < quizList.size()) {
            objectAnimator.start();
        } else {
            if (wrongQuizList.isEmpty()) {
                updateQuizStartFinishStatus(true);
                if (hasHanja) {
                    alertDialog.show(R.string.msg_let_practice_writing, 0, (dialog, which) -> {
                        dialog.dismiss();
                        Intent i = new Intent(context, HanjaQuizWritingActivity.class);
                        i.putExtra(Constant.BUNDLE.KEY_QUIZ_LIST, (Serializable) quizList);
                        openNewScreen(i);
                        finish();
                    });
                } else {
                    alertDialog.show(R.string.msg_finish_quiz, 0, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            onBackPressed();
                        }
                    });
                }
            } else {
                alertDialog.show(
                        String.format(tplScoreAndAnswerAgain, Constant.TEST_MAX_SCORE / quizList.size() * (quizList.size() - wrongQuizList.size())),
                        "",
                        (dialog, which) -> {
                            quizList.clear();
                            for (VocaStudyChatExam voca : wrongQuizList) {
                                voca.setAnswer1Selected(false);
                                voca.setAnswer2Selected(false);
                                voca.setAnswer3Selected(false);
                                voca.setAnswer4Selected(false);
                                voca.setLastSelectedAnswer(0);
                                voca.setHasWrongAnswer(false);
                                quizList.add(voca);
                            }
                            quizPos = -1;
                            wrongQuizList.clear();
                            adapter.setQuizList(quizList);
                            adapter.notifyDataSetChanged();
                            bindData();
                            dialog.dismiss();
                        }
                );
            }
        }
    }

    private void initPlayVocaHelperListener() {
        if (!playVocaHelper.hasMotherTongueListener()) {
            playVocaHelper.setMotherTongueListener(new UtteranceProgressListener() {

                @Override
                public void onStart(String utteranceId) {
                    updateItemStatus(utteranceId, true);
                }

                @Override
                public void onDone(String utteranceId) {
                }

                @Override
                public void onError(String utteranceId) {
                }
            });
        }
        if (!playVocaHelper.hasStudyListener()) {
            playVocaHelper.setStudyListener(new UtteranceProgressListener() {

                @Override
                public void onStart(String utteranceId) {
                }

                @Override
                public void onDone(String utteranceId) {
                    updateItemStatus(utteranceId, false);
                }

                @Override
                public void onError(String utteranceId) {
                    updateItemStatus(utteranceId, false);
                }
            });
        }
    }

    private void updateItemStatus(String utteranceId, boolean playing) {
        for (int i = 0; i < quizList.size(); i++) {
            VocaStudyChatExam voca = quizList.get(i);
            if (utteranceId.equals(String.valueOf(voca.getVIId()))) {
                voca.setVIPlaying(playing);
                int pos = i;
                binding.csvQuestion.post(() -> adapter.notifyItemChanged(pos));
                break;
            }
        }
    }

    private CardStackListener cardStackListener = new CardStackListener() {

        @Override
        public void onCardDragging(Direction direction, float ratio) {
        }

        @Override
        public void onCardSwiped(Direction direction) {
        }

        @Override
        public void onCardRewound() {
        }

        @Override
        public void onCardCanceled() {
        }

        @Override
        public void onCardAppeared(View view, int position) {
        }

        @Override
        public void onCardDisappeared(View view, int position) {
            if (isActivityAvailable()) {
                bindData();
            }
        }
    };

    private OnClickListener onCardClickListener = (view, object) -> {
        if (canClickCard && object instanceof VocaStudyChatExam) {
            VocaStudyChatExam voca = (VocaStudyChatExam) object;
            switch (view.getId()) {
                case R.id.ic_play:
                    boolean isPlaying = voca.isVIPlaying();
                    playVocaHelper.stop();
                    if (!isPlaying) {
//                        playTTS.preparePlayVoca((IVocaFullPlayTTSItem)voca);
                    }
                    break;
                case R.id.btn1:
                case R.id.btn2:
                case R.id.btn3:
                case R.id.btn4:
                    if (voca.getCorrectAnswerNumber() == voca.getLastSelectedAnswer()) {
                        objectAnimator.cancel();
                        Direction direction;
                        if (voca.hasWrongAnswer()) {
                            direction = Direction.Left;
                            binding.tvWrongCount.setText(String.valueOf(wrongQuizList.size()));
                            BaseVoca.blinkView(binding.vWrong);
                        } else {
                            direction = Direction.Right;
                            binding.tvCorrectCount.setText(String.valueOf(quizPos + 1 - wrongQuizList.size()));
                            BaseVoca.blinkView(binding.vCorrect);
                            saveResutlOfEachQuestion(voca);
                        }
                        binding.csvQuestion.postDelayed(() -> {
                            layoutManager.setSwipeAnimationSetting(
                                    new SwipeAnimationSetting.Builder()
                                            .setDirection(direction)
                                            .build()
                            );
                            binding.csvQuestion.swipe();
                        }, Constant.STOP_BLINK_DELAY_TIME);
                    } else if (!voca.hasWrongAnswer()) {
                        voca.setHasWrongAnswer(true);
                        wrongQuizList.add(voca);
                        saveResutlOfEachQuestion(voca);
                    }
                    break;
                case R.id.iv_bookmark:
//                    if (getSubDatabase() != null) {
//                        getSubDatabase().updateBookmarkDic(voca);
//                    }
                    break;
                default:
                    break;
            }
        }
    };


//    private OnKnowChangeListener onKnowChangeListener = new OnKnowChangeListener() {
//        @Override
//        public void onVocaKnowChange(IVocaBasicItem iVocaBasicItem, int newVocaKnow) {
//            onChangeVocaKnow(iVocaBasicItem, newVocaKnow);
//        }
//
//        @Override
//        public void onVocaKnowPronounceChange(IVocaBasicItem iVocaBasicItem, int newVocaKnowPronounce) {
//            onChangeVocaKnowPronounce((VocaStudyChat) iVocaBasicItem, newVocaKnowPronounce);
//        }
//
//        @Override
//        public void onAddToWordbook(IVocaBasicItem iVocaBasicItem) {
//
//        }
//
//        @Override
//        public void onAddToBookmark(IVocaBasicItem iVocaBasicItem) {
//
//        }
//
//        @Override
//        public void onDeleteFromBookmark(IVocaBasicItem iVocaBasicItem) {
//
//        }
//
//        @Override
//        public void onDismiss() {
//
//        }
//    };

//    private OnKnowChangePlayerListener onKnowChangeListener = new OnKnowChangePlayerListener() {
//
//        @Override
//        public void onVocaKnowChange(AmkiItem voca, DicModel dicModel, int vocaKnow) {
//            onChangeVocaKnow(voca, vocaKnow);
//        }
//
//        @Override
//        public void onVocaKnowPronounceChange(AmkiItem voca, DicModel dicModel, int vocaKnowPronounce) {
//            onChangeVocaKnowPronounce((VocaStudyChat) voca, vocaKnowPronounce);
//        }
//
//        @Override
//        public void onAddToWordbook(AmkiItem voca) {
//        }
//
//        @Override
//        public void onAddToBookmark(AmkiItem voca, DicModel dicModel) {
//        }
//
//        @Override
//        public void onDeleteFromBookmark(AmkiItem voca, DicModel dicModel) {
//        }
//
//    };

    private void saveResutlOfEachQuestion(VocaStudyChatExam voca) {
        if (Utils.isConnected(context)) {
            int solvingTime = (int) TimeUnit.MILLISECONDS.toSeconds(solvingTimeEnd - solvingTimeStart);
            application.getDalAiImpl().saveResutlOfEachQuestion(
                    0,
                    voca.getVocaId(),
                    voca.getType(),
                    BaseVoca.getVocaDisplay(voca),
                    voca.hasWrongAnswer() ? 0 : 1,
                    solvingTime,
                    quizStatusId,
                    langStudyCode,
                    null
            );
        } else {
//            alertDialog.showNoInternet();
        }
    }

    private Animator.AnimatorListener animatorListener = new Animator.AnimatorListener() {

        @Override
        public void onAnimationStart(Animator animation) {
            solvingTimeStart = System.currentTimeMillis();
            cancelAnimator = false;
            canClickCard = true;
        }

        @Override
        public void onAnimationEnd(Animator animation) {
//            if (isActivityAvailable()) {
//                solvingTimeEnd = System.currentTimeMillis();
//                playVocaHelper.stop();
//                canClickCard = false;
//                if (cancelAnimator) {
//                    cancelAnimator = false;
//                    return;
//                }
//                VocaStudyChatExam voca = quizList.get(quizPos);
//                if (!voca.hasWrongAnswer()) {
//                    voca.setHasWrongAnswer(true);
//                    wrongQuizList.add(voca);
//                    saveResutlOfEachQuestion(voca);
//                }
//                binding.tvWrongCount.setText(String.valueOf(wrongQuizList.size()));
//                Voca.blinkView(binding.vWrong);
//                binding.csvQuestion.postDelayed(() -> {
//                    layoutManager.setSwipeAnimationSetting(
//                            new SwipeAnimationSetting.Builder()
//                                    .setDirection(Direction.Left)
//                                    .build()
//                    );
//                    binding.csvQuestion.swipe();
//                }, Constant.STOP_BLINK_DELAY_TIME);
//            }
        }

        @Override
        public void onAnimationCancel(Animator animation) {
            cancelAnimator = true;
        }

        @Override
        public void onAnimationRepeat(Animator animation) {
        }
    };

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
//        objectAnimator.removeAllListeners();
//        objectAnimator.cancel();
//        objectAnimator = null;
//        playVocaHelper.setIncludeMeaning(sharedPreferences.getIncludeMeaning());
        super.onDestroy();
    }

    private boolean isActivityAvailable() {
        return !isFinishing() && !isDestroyed();
    }

//    private void generateQuizType(int quizType) {
//        DLog.d(getLogTag(), "generateQuizType");
//        final int langStudyCode = EnumLanguage.findByFormatApi(sharedPreferences.getStudyLanguage()).getIdApi(); //ENGLISH.getIdApi();
//        final int langMeaningCode = EnumLanguage.findByFormatApi(sharedPreferences.getMenuLanguage()).getIdApi(); //EnumLanguage.KOREAN.getIdApi();
//        switch (quizType) {
//            case 0:
//                this.langStudyCode = langStudyCode;
//                this.langMeaningCode = langMeaningCode;
//                break;
//            case 1:
//                this.langStudyCode = langMeaningCode;
//                this.langMeaningCode = langStudyCode;
//                break;
//            default:
//                final Random random = new Random();
//                generateQuizType(random.nextInt(1));
//                break;
//        }
//    }

    private void updateData() {
        if (Utils.isEmpty(quizList)) {
            binding.tvAllWordsCount.setText(String.format(tplAllWordsCount, 0));
        } else {
            updateQuizStartFinishStatus(false);
            binding.tvAllWordsCount.setText(String.format(tplAllWordsCount, quizList.size()));
            adapter.setQuizList(quizList);
            adapter.notifyDataSetChanged();
            bindData();
        }
        Loading.hide();
    }

    private void onChangeVocaKnow(AmkiItem item, int vocaKnow) {
//        int newVocaKnowPronounce = item.getAmkiKnowPronounce();
//        int newVocaKnow = vocaKnow;
//        if (newVocaKnow == Constant.VOCA_KNOW.VOCA_KNOW_KNOWN) {
//            newVocaKnowPronounce = newVocaKnow;
//        }
//        int amkiId = item.getAmkiId();
//        int amkiType = item.getAmkiType();
//        if (!isNetwork()) {
//            alertDialog.showNoInternet();
//            return;
//        }
//        onVocaKnownChanged((VocaStudyChat) item, newVocaKnow, newVocaKnowPronounce);
//        application.getDalAiImpl().changeMultipleVocaKnow(
//                String.valueOf(newVocaKnow),
//                String.valueOf(VocaKnow.getVocaKnowPronounceByVocaKnow(newVocaKnow)),
//                String.valueOf(amkiId),
//                String.valueOf(amkiType),
//                null);
    }

//    private void onVocaKnownChanged(VocaStudyChat voca, int vocaKnow, int vocaKnowPronounce) {
//        voca.setVocaKnow(vocaKnow);
//        voca.setVocaKnowPronounce(vocaKnowPronounce);
////        if (getSubDatabase() != null) {
//////            getSubDatabase().updateVocaKnowAndVocaKnowPronounce(voca);
////        }
//    }
//
//    private void onChangeVocaKnowPronounce(IVocaBasicItem voca, int knownPronounce) {
//        DLog.d(getLogTag(), "onChangeVocaKnowPronounce - knownPronounce=" + knownPronounce);
//        voca.setVIVocaKnowPronounce(knownPronounce);
////        if (getSubDatabase() != null) {
//////            getSubDatabase().updateVocaKnowAndVocaKnowPronounce(voca);
////        }
//        int vocaId = voca.getVIVocaId();
//        String vocaDisplay = voca.getVIVoca();
//        if (knownPronounce == Constant.VOCA_KNOW.VOCA_KNOW_KNOWN) {
//            application.getDalAiImpl().setPronounceKnown(vocaDisplay, String.valueOf(vocaId), null);
//        } else {
//            application.getDalAiImpl().setPronounceUnknown(vocaDisplay, String.valueOf(vocaId), null);
//        }
//    }

//    private int getBookmarkFromData(int vocaId) {
//        if (dicModelList != null) {
//            for (DicModel item : dicModelList) {
//                if (item.getVocaId() == vocaId) {
//                    return item.getBookmark();
//                }
//            }
//        }
//        return 0;
//    }
}
