package com.dalread.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.dalread.R;
import com.dalread.databinding.ItemChoiceQuizBinding;
import com.dalread.listener.OnClickListener;
import com.dalread.model.VocaStudyChatExam;
import com.dalread.util.BaseVoca;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class ChoiceQuizAdapter extends RecyclerView.Adapter {

    private int[] quizColors;
    private List<VocaStudyChatExam> quizList = new ArrayList<>();
    private OnClickListener listener;

    public ChoiceQuizAdapter(int[] quizColors) {
        this.quizColors = quizColors;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ItemHolder(ItemChoiceQuizBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
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

    class ItemHolder extends RecyclerView.ViewHolder {
        private VocaStudyChatExam voca;
        private ItemChoiceQuizBinding binding;
        ItemHolder(ItemChoiceQuizBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            ButterKnife.bind(this, binding.getRoot());
        }

        public void bind(VocaStudyChatExam voca, int quizColor) {
            this.voca = voca;

            binding.icPlay.setVisibility(View.INVISIBLE);
            binding.vItem.setBackgroundColor(quizColor);
            BaseVoca.updateIconSpeaker(binding.icPlay, voca);
            binding.tvQuestion.setText(voca.getQuestion());
            bindButtons();
        }

        private void bindButtons() {
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

        @OnClick({R.id.ic_play, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4})
        void onClick(View view) {
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
                default:
                    break;
            }
            if (listener != null) {
                listener.onClick(view, voca);
            }
        }
    }
}
