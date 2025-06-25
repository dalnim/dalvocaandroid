package com.dalread.activity;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Bundle;
import android.speech.tts.UtteranceProgressListener;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.LinearInterpolator;

import androidx.annotation.Nullable;

import com.dalread.R;
import com.dalread.adapter.ChoiceQuizAdapter;
import com.dalread.base.EnumLanguage;
import com.dalread.base.PlayVocaActivity;
import com.dalread.databinding.ActivityQuizBinding;
import com.dalread.dialog.AlertDialog;
import com.dalread.interfaces.IVocaFullPlayTTSItem;
import com.dalread.listener.OnClickListener;
import com.dalread.model.VocaStudyChatExam;
import com.dalread.network.DalApiListener;
import com.dalread.util.BaseVoca;
import com.dalread.util.Constant;
import com.dalread.util.Loading;
import com.dalread.util.Utils;
import com.yuyakaido.android.cardstackview.CardStackLayoutManager;
import com.yuyakaido.android.cardstackview.CardStackListener;
import com.yuyakaido.android.cardstackview.Direction;
import com.yuyakaido.android.cardstackview.StackFrom;
import com.yuyakaido.android.cardstackview.SwipeAnimationSetting;
import com.yuyakaido.android.cardstackview.SwipeableMethod;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindArray;
import butterknife.BindString;

public class ChoiceQuizActivity extends PlayVocaActivity {
    @BindString(R.string.tpl_all_words_count)
    String tplAllWordsCount;
    @BindString(R.string.tpl_score_and_answer_again)
    String tplScoreAndAnswerAgain;

    @BindArray(R.array.quiz_colors)
    int[] quizColors;

    private Context context;
    private int langStudyCode;
    private List<IVocaFullPlayTTSItem> playlistItems;
    private boolean hasHanja;
    private List<VocaStudyChatExam> quizList;
    private int quizPos;
    private List<VocaStudyChatExam> wrongQuizList;
    private boolean showLastWordPronounce;
    private AlertDialog alertDialog;
    private CardStackLayoutManager layoutManager;
    private ChoiceQuizAdapter adapter;
    private ObjectAnimator objectAnimator;
    private boolean cancelAnimator;
    private boolean canClickCard;
    private int quizStatusId;
    private long solvingTimeStart;
    private long solvingTimeEnd;
    ActivityQuizBinding binding;
    @Override
    protected View getContentView() {
        binding = ActivityQuizBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        return view;
    }

    @Override
    protected int getContentViewId() {
        return 0;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initData();
        initView();
        getData();
    }

    @Override
    protected void onResume() {
        super.onResume();

        initPlayVocaHelper();
    }

    private void initData() {
        context = this;
        langStudyCode = getIntent().getIntExtra(Constant.BUNDLE.KEY_STUDY_LANG, 0);
        if (langStudyCode <= 0) {
            langStudyCode = sharedPreferences.getLangStudyCode();
        }
        playlistItems = (List<IVocaFullPlayTTSItem>) getIntent().getSerializableExtra(Constant.BUNDLE.KEY_VOCA_PLAYLIST);
        quizList = (List<VocaStudyChatExam>) getIntent().getSerializableExtra(Constant.BUNDLE.KEY_VOCA_STUDY_CHAT_EXAM);
        if (Utils.isEmpty(quizList)) {
            quizList = new ArrayList<>();
        }
        quizPos = -1;
        wrongQuizList = new ArrayList<>();
        showLastWordPronounce = EnumLanguage.JAPANESE.getFormatApi().equals(sharedPreferences.getStudyLanguage())
                || EnumLanguage.CHINESE_SIMPLIFIED.getFormatApi().equals(sharedPreferences.getStudyLanguage());
        playVocaHelper.setIncludeMeaning(false);
    }

    private void initView() {
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
        adapter = new ChoiceQuizAdapter(quizColors);
        adapter.setListener(onCardClickListener);
        binding.csvQuestion.setAdapter(adapter);
        objectAnimator = ObjectAnimator.ofInt(binding.pbTime, "progress", 100, 0);
        objectAnimator.setDuration(Constant.ANSWER_QUIZ_TIME);
        objectAnimator.setInterpolator(new LinearInterpolator());
        objectAnimator.addListener(animatorListener);
    }

    private void getData() {
        if (Utils.isEmpty(quizList)) {
            makeQuizFromPlaylistItems();
        } else {
            openQuizWithQuizList();
        }
    }

    private void makeQuizFromPlaylistItems() {
        if (Utils.isConnected(context)) {
            Loading.show(context);
            if (playlistItems == null) {
                application.getDalAiImpl().makeQuizAtQuizMenu(
                        langStudyCode,
                        Constant.EXAM_TYPE_QUESTION_WORD,
                        EnumLanguage.HANJA.getFormatApi().equals(langStudyCode) ? sharedPreferences.getMaxHanjaQuiz() : sharedPreferences.getMaxQuiz(),
                        getDataListener
                );
            } else {
                int countOfQuiz = 0;
                StringBuilder vocaIds = new StringBuilder();
                StringBuilder vocaTypes = new StringBuilder();
                for (IVocaFullPlayTTSItem voca : playlistItems) {
                    countOfQuiz++;
                    vocaIds.append(",").append(voca.getVIVocaId());
                    vocaTypes.append(",").append(voca.getVIVocaType());
                }
                if (countOfQuiz > 0) {
                    vocaIds = new StringBuilder(vocaIds.substring(1));
                    vocaTypes = new StringBuilder(vocaTypes.substring(1));
                }
                application.getDalAiImpl().makeWordQuizFromListOfVocaID(
                        langStudyCode,
                        Constant.MULTIPLE_CHOICE_QUESTION_TYPE.VOCA,
                        Math.max(countOfQuiz, 4),
                        vocaIds.toString(),
                        vocaTypes.toString(),
                        getDataListener
                );
            }
        } else {
            alertDialog.showNoInternet();
        }
    }

    private void openQuizWithQuizList() {
        binding.tvAllWordsCount.setText(String.format(tplAllWordsCount, quizList.size()));
        int studyLang = EnumLanguage.findByFormatApi(sharedPreferences.getStudyLanguage()).getIdApi();
        for (int i = 0; i < quizList.size(); i++) {
            VocaStudyChatExam voca = quizList.get(i);
            voca.setId(i + 1);
            voca.setPath(BaseVoca.getOutputRecordingFileName(studyLang, voca.getType(), voca.getVocaId(), getUserID()));
            if (!voca.getHanjaList().isEmpty()) {
                hasHanja = true;
            }
        }
        adapter.setQuizList(quizList);
        adapter.notifyDataSetChanged();
        bindData();
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
            alertDialog.showNoInternet();
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
                alertDialog.show(R.string.msg_finish_quiz, 0, null);
                alertDialog.show(
                        R.string.msg_finish_quiz,
                        0,
                        (dialog, which) -> {
//                            dialog.dismiss();
                            finish();
                        }
                );
                //Don't delete this, will start Writing quiz.
//                if (hasHanja) {
//                    alertDialog.show(R.string.msg_let_practice_writing, 0, (dialog, which) -> {
//                        dialog.dismiss();
//
//                        Intent i = new Intent(context, HanjaQuizActivity.class);
//                        i.putExtra(Constant.BUNDLE.KEY_QUIZ_LIST, (Serializable) quizList);
//                        openNewScreen(i);
//
//                        finish();
//                    });
//                } else {
//                    alertDialog.show(R.string.msg_finish_quiz, 0, null);
//                }
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

    private void initPlayVocaHelper() {
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
//                    if (!isPlaying) {
//                        preparePlayVoca(voca);
//                    }
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
                default:
                    break;
            }
        }
    };

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
            alertDialog.showNoInternet();
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
            if (isActivityAvailable()) {
                solvingTimeEnd = System.currentTimeMillis();
                playVocaHelper.stop();
                canClickCard = false;
                if (cancelAnimator) {
                    cancelAnimator = false;
                    return;
                }
                VocaStudyChatExam voca = quizList.get(quizPos);
                if (!voca.hasWrongAnswer()) {
                    voca.setHasWrongAnswer(true);
                    wrongQuizList.add(voca);
                    saveResutlOfEachQuestion(voca);
                }
                binding.tvWrongCount.setText(String.valueOf(wrongQuizList.size()));
                BaseVoca.blinkView(binding.vWrong);
                binding.csvQuestion.postDelayed(() -> {
                    layoutManager.setSwipeAnimationSetting(
                            new SwipeAnimationSetting.Builder()
                                    .setDirection(Direction.Left)
                                    .build()
                    );
                    binding.csvQuestion.swipe();
                }, Constant.STOP_BLINK_DELAY_TIME);
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
        objectAnimator.removeAllListeners();
        objectAnimator.cancel();
        objectAnimator = null;

        playVocaHelper.setIncludeMeaning(sharedPreferences.getIncludeMeaning());

        super.onDestroy();
    }

    private boolean isActivityAvailable() {
        return !isFinishing() && !isDestroyed();
    }
}
