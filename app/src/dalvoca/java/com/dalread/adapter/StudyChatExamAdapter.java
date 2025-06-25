package com.dalread.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dalread.R;
import com.dalread.adapter.holder.StudyChatExamItemHolder;
import com.dalread.adapter.holder.StudyChatItemHolder;
import com.dalread.listener.OnVocaStudyChatClickListener;
import com.dalread.listener.OnVocaStudyChatExamClickListener;
import com.dalread.model.VocaStudyChatExam;
import com.dalread.util.Constant;
import com.dalread.util.Voca;

import java.util.ArrayList;
import java.util.List;

public class StudyChatExamAdapter extends RecyclerView.Adapter {

    private List<VocaStudyChatExam> vocas = new ArrayList<>();
    private boolean displayPronunciation;
    private int studyRole;
    private boolean isStudentAndTutorJoined;
    private int studyLang;
    private int uid;
    private boolean showStudentMeaning;
    private OnVocaStudyChatClickListener clickListener;
    private OnVocaStudyChatExamClickListener examClickListener;
    private Context context;

    @Override
    public int getItemViewType(int position) {
        return vocas.get(position).getExamType();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == Constant.EXAM_TYPE_NORMAL)
            return new StudyChatItemHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_study_chat, parent, false));
        return new StudyChatExamItemHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_study_chat_exam, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof StudyChatItemHolder) {
            ((StudyChatItemHolder) holder).bind(context,
                    vocas.get(position), studyRole, Constant.SHOW_ASTERISK_ON, displayPronunciation,
                    isStudentAndTutorJoined, studyLang, 0, false,
                    false, showStudentMeaning, clickListener
            );
        } else if (holder instanceof StudyChatExamItemHolder) {
            ((StudyChatExamItemHolder) holder).bind(context,
                    vocas.get(position), studyRole, displayPronunciation, isStudentAndTutorJoined,
                    showStudentMeaning, examClickListener
            );
        }
    }

    @Override
    public int getItemCount() {
        return vocas.size();
    }

    public int notifyItemChanged(VocaStudyChatExam voca) {
        if (vocas != null) {
            int pos = vocas.indexOf(voca);
            notifyItemChanged(pos);
            return pos;
        }
        return -1;
    }

    public void setVocas(List<VocaStudyChatExam> vocas) {
        this.vocas.clear();
        if (vocas != null) {
            for (VocaStudyChatExam voca : vocas) {
                voca.setPath(Voca.getOutputRecordingFileName(studyLang, voca.getType(), voca.getVocaId(), uid));
                this.vocas.add(voca);
            }
        }
    }

    public void setDisplayPronunciation(boolean displayPronunciation) {
        this.displayPronunciation = displayPronunciation;
    }

    public void setStudyRole(int studyRole) {
        this.studyRole = studyRole;
    }

    public void setStudentAndTutorJoined(boolean isStudentAndTutorJoined) {
        this.isStudentAndTutorJoined = isStudentAndTutorJoined;
    }

    public void setStudyLang(int studyLang) {
        this.studyLang = studyLang;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public void setShowStudentMeaning(boolean showStudentMeaning) {
        this.showStudentMeaning = showStudentMeaning;
    }

    public void setListener(OnVocaStudyChatClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public void setListener(OnVocaStudyChatExamClickListener listener) {
        this.examClickListener = listener;
    }
}
