package com.dalread.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.dalread.R;
import com.dalread.databinding.ItemQuizBinding;
import com.dalread.listener.OnClickListener;
import com.dalread.listener.OnKnowChangeListener;
import com.dalread.model.VocaStudyChatExam;
import com.dalread.util.BaseVoca;
import com.dalread.util.BaseVocaKnow;
import com.dalread.util.Constant;
import com.dalread.util.Utils;

import java.util.ArrayList;
import java.util.List;

public class QuizAdapter extends RecyclerView.Adapter {

    private int[] quizColors;
    private List<VocaStudyChatExam> quizList = new ArrayList<>();
    private OnClickListener listener;
    private OnKnowChangeListener onKnowChangeListener;
//    private OnKnowChangePlayerListener onKnowChangeListener;
    private Context context;

    public QuizAdapter(Context context, int[] quizColors) {
        this.context = context;
        this.quizColors = quizColors;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ItemHolder(ItemQuizBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ItemHolder) {
            ((ItemHolder) holder).bind(quizList.get(position), quizColors[position % quizColors.length]);
        }
    }

    @Override
    public int getItemCount() {
        return quizList.size();
    }

    public void setQuizList(List<VocaStudyChatExam> quizList) {
        this.quizList.clear();
        this.quizList.addAll(quizList);
    }

    public void setListener(OnClickListener listener) {
        this.listener = listener;
    }

    public void setOnKnowChangeListener(OnKnowChangeListener onKnowChangeListener) {
        this.onKnowChangeListener = onKnowChangeListener;
    }

    class ItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private VocaStudyChatExam voca;

        private ItemQuizBinding binding;
        ItemHolder(ItemQuizBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            setOnClickListeners();
        }

        private void setOnClickListeners() {
            binding.ivSpeaker.setOnClickListener(this);
            binding.btn1.setOnClickListener(this);
            binding.btn2.setOnClickListener(this);
            binding.btn3.setOnClickListener(this);
            binding.btn4.setOnClickListener(this);
            binding.tvVocaKnowKnown.setOnClickListener(this);
            binding.tvVocaKnowAmkiGrade1.setOnClickListener(this);
            binding.tvVocaKnowAmkiGrade2.setOnClickListener(this);
            binding.tvVocaKnowUnknown.setOnClickListener(this);
            binding.tvVocaKnowValue.setOnClickListener(this);

        }

        public void bind(VocaStudyChatExam voca, int quizColor) {
            this.voca = voca;
            binding.vItem.setBackgroundColor(quizColor);
            BaseVoca.updateIconSpeaker(binding.ivSpeaker, voca);
            binding.tvQuestion.setText(voca.getQuestion());
            bindButtons();
            show4KnowButtons();
        }

        private void show4KnowButtons() {
            //TODO : Need to show again when I update code for KNOW changes.
            binding.ivSpeaker.setVisibility(View.GONE);
            binding.llKnowBookmark.setVisibility(View.GONE);
            if (Utils.isConnected(context)) {
                binding.ll4KnowButtons.setVisibility(View.GONE);
            } else {
                binding.ll4KnowButtons.setVisibility(View.GONE);
            }
        }

        private void bindButtons() {
            updateBookmarkView(voca);
            updateKnowView();
            setBackgroundColortOnAnswerBtn();
            setAnswerTextOnAnswerBtn();
            setAnswerBtnEnable(true);
        }

        private void setBackgroundColortOnAnswerBtn() {
            binding.btn1.setBackgroundResource(R.drawable.btn_no_border_grey_background);
            binding.btn2.setBackgroundResource(R.drawable.btn_no_border_grey_background);
            binding.btn3.setBackgroundResource(R.drawable.btn_no_border_grey_background);
            binding.btn4.setBackgroundResource(R.drawable.btn_no_border_grey_background);
        }

        private void setAnswerTextOnAnswerBtn() {
            binding.btn1.setText(voca.getAnswer1());
            binding.btn2.setText(voca.getAnswer2());
            binding.btn3.setText(voca.getAnswer3());
            binding.btn4.setText(voca.getAnswer4());
        }

        private void updateAnswerBtnBackgroundColor(int answerNumber) {
            AppCompatButton answerBtn = binding.btn1;
            switch (answerNumber) {
                case 1:
                    answerBtn = binding.btn1;
                    break;
                case 2:
                    answerBtn = binding.btn2;
                    break;
                case 3:
                    answerBtn = binding.btn3;
                    break;
                case 4:
                    answerBtn = binding.btn4;
                    break;
            }

            answerBtn.setBackgroundResource(
                    getAnswerBtnBackgroundColor(answerNumber)
            );
        }

        private int getAnswerBtnBackgroundColor(int answerNumber) {
            if (voca.getCorrectAnswerNumber() == answerNumber) {
                setAnswerBtnEnable(false);
                return R.drawable.btn_no_border_blue_background;
            }
            return R.drawable.btn_no_border_red_background;
        }

        private void setAnswerBtnEnable(boolean isEnable) {
            binding.btn1.setEnabled(isEnable);
            binding.btn2.setEnabled(isEnable);
            binding.btn3.setEnabled(isEnable);
            binding.btn4.setEnabled(isEnable);
        }

        private void updateKnowView() {
            BaseVocaKnow.updateIconVocaKnow(context, binding.tvVocaKnowValue, voca.getVocaKnow());
            updateKnowPronounceView(voca);
        }

        private void updateBookmarkView(VocaStudyChatExam voca) {
            binding.ivBookmark.setVisibility(voca.isBookmark() ? View.VISIBLE : View.INVISIBLE);
        }

        private void updateKnowPronounceView(VocaStudyChatExam voca) {
            binding.ivKnowPronounce.setVisibility(
                    voca.isVocaKnowPronounce() ? View.INVISIBLE : View.VISIBLE);
        }

        @Override
        public void onClick(View view) {
            int oldKnow = voca.getVocaKnow();
            int oldKnowPronounce = voca.getVocaKnowPronounce();
            int id = view.getId();
            switch (id) {
                case R.id.btn1:
                    voca.setAnswer1Selected(true);
                    voca.setLastSelectedAnswer(1);
                    updateAnswerBtnBackgroundColor(1);
                    break;
                case R.id.btn2:
                    voca.setAnswer2Selected(true);
                    voca.setLastSelectedAnswer(2);
                    updateAnswerBtnBackgroundColor(2);
                    break;
                case R.id.btn3:
                    voca.setAnswer3Selected(true);
                    voca.setLastSelectedAnswer(3);
                    updateAnswerBtnBackgroundColor(3);
                    break;
                case R.id.btn4:
                    voca.setAnswer4Selected(true);
                    voca.setLastSelectedAnswer(4);
                    updateAnswerBtnBackgroundColor(4);
                    break;
                case R.id.tvVocaKnowKnown:
                    if (voca.isVocaKnow() && voca.isVocaKnowPronounce()) return;
                    if (onKnowChangeListener != null) {
                        onKnowChangeListener.onVocaKnowChange(voca, Constant.VOCA_KNOW.VOCA_KNOW_KNOWN);
//                        onKnowChangeListener.onVocaKnowChange(voca, null, Constant.VOCA_KNOW.VOCA_KNOW_KNOWN);
                    }
                    voca.setVocaKnow(Constant.VOCA_KNOW.VOCA_KNOW_KNOWN);
                    voca.setVocaKnowPronounce(Constant.VOCA_KNOW.VOCA_KNOW_KNOWN);
                    updateKnowView();
                    return;
                case R.id.tvVocaKnowAmkiGrade1:
                    if (voca.getVocaKnow() == Constant.VOCA_KNOW.VOCA_KNOW_AMKI_GRADE_1) return;
                    if (onKnowChangeListener != null) {
                        onKnowChangeListener.onVocaKnowChange(voca, Constant.VOCA_KNOW.VOCA_KNOW_AMKI_GRADE_1);
//                        onKnowChangeListener.onVocaKnowChange(voca, null, Constant.VOCA_KNOW.VOCA_KNOW_AMKI_GRADE_1);
                    }
                    voca.setVocaKnow(Constant.VOCA_KNOW.VOCA_KNOW_AMKI_GRADE_1);
                    updateKnowView();
                    return;
                case R.id.tvVocaKnowAmkiGrade2:
                    if (voca.getVocaKnow() == Constant.VOCA_KNOW.VOCA_KNOW_AMKI_GRADE_2) return;
                    if (onKnowChangeListener != null) {
                        onKnowChangeListener.onVocaKnowChange(voca, Constant.VOCA_KNOW.VOCA_KNOW_AMKI_GRADE_2);
                    }
                    voca.setVocaKnow(Constant.VOCA_KNOW.VOCA_KNOW_AMKI_GRADE_2);
                    updateKnowView();
                    return;
                case R.id.tvVocaKnowUnknown:
                    if (voca.getVocaKnow() == Constant.VOCA_KNOW.VOCA_KNOW_UNKNOWN) return;
                    if (onKnowChangeListener != null) {
                        onKnowChangeListener.onVocaKnowChange(voca, Constant.VOCA_KNOW.VOCA_KNOW_UNKNOWN);
                    }
                    voca.setVocaKnow(Constant.VOCA_KNOW.VOCA_KNOW_UNKNOWN);
                    updateKnowView();
                    return;
                case R.id.tvVocaKnowValue:
                    showSubtitleKnowDialog(voca);
                    break;
                default:
                    break;
            }
            if (listener != null) {
                listener.onClick(view, voca);
            }
        }

//        @OnClick({R.id.ic_play, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4,
//                R.id.iv_known, R.id.iv_grade_1, R.id.iv_grade_2, R.id.iv_exclude,
//                 R.id.tv_know_value
//            })
//        void onClick(View view) {
//            int oldKnow = voca.getVocaKnow();
//            int oldKnowPronounce = voca.getVocaKnowPronounce();
//            int id = view.getId();
//            switch (id) {
//                case R.id.btn1:
//                    voca.setAnswer1Selected(true);
//                    voca.setLastSelectedAnswer(1);
//                    bindButtons();
//                    break;
//                case R.id.btn2:
//                    voca.setAnswer2Selected(true);
//                    voca.setLastSelectedAnswer(2);
//                    bindButtons();
//                    break;
//                case R.id.btn3:
//                    voca.setAnswer3Selected(true);
//                    voca.setLastSelectedAnswer(3);
//                    bindButtons();
//                    break;
//                case R.id.btn4:
//                    voca.setAnswer4Selected(true);
//                    voca.setLastSelectedAnswer(4);
//                    bindButtons();
//                    break;
//                case R.id.iv_known:
//                    if (voca.isVocaKnow() && voca.isVocaKnowPronounce()) return;
//                    if (onKnowChangeListener != null) {
//                        onKnowChangeListener.onVocaKnowChange(voca, null, Constant.VOCA_KNOW.VOCA_KNOW_KNOWN);
//                    }
//                    voca.setVocaKnow(Constant.VOCA_KNOW.VOCA_KNOW_KNOWN);
//                    voca.setVocaKnowPronounce(Constant.VOCA_KNOW.VOCA_KNOW_KNOWN);
//                    updateKnowView();
//                    return;
//                case R.id.iv_grade_1:
//                    if (voca.getVocaKnow() == Constant.VOCA_KNOW.VOCA_KNOW_AMKI_GRADE_1) return;
//                    if (onKnowChangeListener != null) {
//                        onKnowChangeListener.onVocaKnowChange(voca, null, Constant.VOCA_KNOW.VOCA_KNOW_AMKI_GRADE_1);
//                    }
//                    voca.setVocaKnow(Constant.VOCA_KNOW.VOCA_KNOW_AMKI_GRADE_1);
//                    updateKnowView();
//                    return;
//                case R.id.iv_grade_2:
//                    if (voca.getVocaKnow() == Constant.VOCA_KNOW.VOCA_KNOW_AMKI_GRADE_2) return;
//                    if (onKnowChangeListener != null) {
//                        onKnowChangeListener.onVocaKnowChange(voca, null, Constant.VOCA_KNOW.VOCA_KNOW_AMKI_GRADE_2);
//                    }
//                    voca.setVocaKnow(Constant.VOCA_KNOW.VOCA_KNOW_AMKI_GRADE_2);
//                    updateKnowView();
//                    return;
//                case R.id.iv_exclude:
//                    if (voca.getVocaKnow() == Constant.VOCA_KNOW.VOCA_KNOW_UNKNOWN) return;
//                    if (onKnowChangeListener != null) {
//                        onKnowChangeListener.onVocaKnowChange(voca, null, Constant.VOCA_KNOW.VOCA_KNOW_UNKNOWN);
//                    }
//                    voca.setVocaKnow(Constant.VOCA_KNOW.VOCA_KNOW_UNKNOWN);
//                    updateKnowView();
//                    return;
//                case R.id.tv_know_value:
//                    showSubtitleKnowDialog(voca);
//                    break;
//                default:
//                    break;
//            }
//            if (listener != null) {
//                listener.onClick(view, voca);
//            }
//        }

        private void showSubtitleKnowDialog(VocaStudyChatExam voca) {
//            DicModel item = new DicModel(voca);
//            final PlayerShowSubtitleKnowDialog dialog = new PlayerShowSubtitleKnowDialog(context, item, new OnClickDialogListener() {
//                @Override
//                public void onClick(View view, Object object) {
//                    switch (view.getId()) {
//                        case R.id.v_bookmark:
//                            voca.setBookmark(item.getBookmark());
//                            updateBookmarkView(voca);
//                            if (listener != null) {
//                                listener.onClick(ivBookmark, voca);
//                            }
//                            break;
//                        case R.id.v_known_pronounce:
//                            if (onKnowChangeListener != null) {
//                                onKnowChangeListener.onVocaKnowPronounceChange(voca, null, item.getVocaKnowPronounce());
//                            }
//                            voca.setVocaKnowPronounce(item.getVocaKnowPronounce());
//                            updateKnowView();
//                            break;
//                        default:
//                            if (onKnowChangeListener != null) {
//                                onKnowChangeListener.onVocaKnowChange(voca, null, item.getVocaKnow());
//                            }
//                            voca.setVocaKnow(item.getVocaKnow());
//                            voca.setVocaKnowPronounce(item.getVocaKnowPronounce());
//                            updateKnowView();
//                            break;
//                    }
//                }
//
//                @Override
//                public void onDismiss(View view, Object object) {
//                }
//            });
//            dialog.show();
        }
    }
}
