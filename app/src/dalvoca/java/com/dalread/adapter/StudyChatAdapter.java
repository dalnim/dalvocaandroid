package com.dalread.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dalread.R;
import com.dalread.adapter.holder.AmkiGradeHeaderHolder;
import com.dalread.adapter.holder.StudyChatItemHolder;
import com.dalread.listener.OnVocaStudyChatClickListener;
import com.dalread.model.AmkiGradeHeader;
import com.dalread.model.AmkiItem;
import com.dalread.model.VocaStudyChat;
import com.dalread.util.BaseVoca;
import com.dalread.util.Constant;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StudyChatAdapter extends RecyclerView.Adapter {

    private static final int TYPE_BOOK_NAME = 0;
    private static final int TYPE_HEADER = 1;
    private static final int TYPE_ITEM = 2;

    private Context context;
    private List<Object> data = new ArrayList<>();
    private LinkedHashMap<AmkiGradeHeader, List<AmkiItem>> map = new LinkedHashMap<>();
    private List<VocaStudyChat> vocas = new ArrayList<>();
    private boolean displayPronunciation;
    private int studyRole;
    private int showAsterisk;
    private String bookName;
    private boolean isStudentAndTutorJoined;
    private int studyLang;
    private int highlightIndex;
    private boolean hasBookName;
    private boolean hasHeader;
    private boolean isDialog;
    private boolean showStudentMeaning;
    private OnVocaStudyChatClickListener listener;

    public StudyChatAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getItemViewType(int position) {
        if (hasBookName && position == 0)
            return TYPE_BOOK_NAME;
        if (data.get(position) instanceof AmkiGradeHeader)
            return TYPE_HEADER;
        return TYPE_ITEM;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_BOOK_NAME)
            return new BookNameHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_book_name_in_study, parent, false));
        if (viewType == TYPE_HEADER)
            return new AmkiGradeHeaderHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.header_targets, parent, false));
        return new StudyChatItemHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_study_chat, parent, false), isDialog);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof BookNameHolder) {
            ((BookNameHolder) holder).bind();
        } else if (holder instanceof AmkiGradeHeaderHolder) {
            ((AmkiGradeHeaderHolder) holder).bind((AmkiGradeHeader) data.get(position));
        } else if (holder instanceof StudyChatItemHolder) {
            ((StudyChatItemHolder) holder).bind(context,
                    (VocaStudyChat) data.get(position),
                    studyRole, showAsterisk, displayPronunciation, isStudentAndTutorJoined,
                    studyLang, highlightIndex, hasBookName, hasHeader, showStudentMeaning,
                    listener
            );
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void setData(List<VocaStudyChat> vocas) {
        if (vocas == null || vocas.isEmpty()) return;
        for (VocaStudyChat voca : vocas) {
            voca.setTblTypeSync(Constant.API_VALUE.TBL_TYPE_SYNC_TOPIC);
        }
        setDataInner(vocas);
    }

    public void setDataNoLoop(List<VocaStudyChat> vocas) {
        if (vocas == null || vocas.isEmpty()) return;
        setDataInner(vocas);
    }

    public void setData(List<VocaStudyChat> vocas, int tblParentSection) {
        if (vocas == null || vocas.isEmpty()) return;
        for (VocaStudyChat voca : vocas) {
            voca.setTblParentSection(tblParentSection);
            voca.setTblTypeSync(Constant.API_VALUE.TBL_TYPE_SYNC_VOCA_LIST_BY_CATEGORY);
            voca.setPnType(Constant.API_VALUE.PN_TYPE_STUDY_WORDS);
        }
        setDataInner(vocas);
    }

    public void setDataAllSentenceList(List<VocaStudyChat> vocas) {
        if (vocas == null || vocas.isEmpty()) return;
        for (VocaStudyChat voca : vocas) {
            voca.setTblTypeSync(Constant.API_VALUE.TBL_TYPE_SYNC_RL_ALL_SENTENCES_IN_MAIN_VIEW);
            voca.setPnType(Constant.API_VALUE.PN_TYPE_RL_ALL_SENTENCES);
        }
        setDataInner(vocas);
    }

    public void setDataAllSentenceListDialog(List<VocaStudyChat> vocas) {
        if (vocas == null || vocas.isEmpty()) return;
        for (VocaStudyChat voca : vocas) {
            voca.setTblTypeSync(Constant.API_VALUE.TBL_TYPE_SYNC_ALL_SENTENCE_LIST);
            voca.setPnType(Constant.API_VALUE.PN_TYPE_STUDY_SENTENCES);
        }
        setDataInner(vocas);
    }

    public void setDataAllVocaList(List<VocaStudyChat> vocas) {
        if (vocas == null || vocas.isEmpty()) return;
        for (VocaStudyChat voca : vocas) {
            voca.setTblTypeSync(Constant.API_VALUE.TBL_TYPE_SYNC_RL_ALL_VOCAS_IN_MAIN_VIEW);
            voca.setPnType(Constant.API_VALUE.PN_TYPE_RL_ALL_VOCAS);
        }
        setDataInner(vocas);
    }

    public void setDataAllVocaListDialog(List<VocaStudyChat> vocas) {
        if (vocas == null || vocas.isEmpty()) return;
        for (VocaStudyChat voca : vocas) {
            voca.setTblTypeSync(Constant.API_VALUE.TBL_TYPE_SYNC_RUBY_VOCA_LIST_ROLE_PLAYING_ALL);
            voca.setPnType(Constant.API_VALUE.PN_TYPE_RUBY_TEXT_ALL);
        }
        setDataInner(vocas);
    }

    public void setDataConversation(List<VocaStudyChat> vocas, int tblParentSection, int tblParentRow) {
        if (vocas == null || vocas.isEmpty()) return;
        for (VocaStudyChat voca : vocas) {
            voca.setTblParentSection(tblParentSection);
            voca.setTblParentRow(tblParentRow);
            voca.setTblTypeSync(Constant.API_VALUE.TBL_TYPE_SYNC_RUBY_VOCA_LIST_CONVERSATION);
            voca.setPnType(Constant.API_VALUE.PN_TYPE_RUBY_TEXT_CONVERSATION);
        }
        setDataInner(vocas);
    }

    private void setDataInner(List<VocaStudyChat> vocas) {
        this.vocas.clear();
        for (VocaStudyChat voca : vocas) {
            if (voca.getVocaId() > 0) {
                this.vocas.add(voca);
            }
        }
        map = BaseVoca.groupVocaListByGrade(context, new ArrayList<>(vocas));
        data = BaseVoca.parseAdapterData(map);
        if (hasBookName) {
            data.add(0, "bookName");
        }
    }

    public void setDataReview(List<VocaStudyChat> vocas) {
        if (vocas == null || vocas.isEmpty()) return;
        for (VocaStudyChat voca : vocas) {
            voca.setTblTypeSync(Constant.API_VALUE.TBL_TYPE_SYNC_TOPIC_REVIEW_MODE);
        }
        this.vocas.clear();
        this.vocas.addAll(vocas);
        map.clear();
        map.put(new AmkiGradeHeader(), new ArrayList<>(vocas));
        data.clear();
        data.addAll(vocas);
        if (hasBookName) {
            data.add(0, "bookName");
        }
    }

    public int notifyItemChanged(VocaStudyChat voca) {
        return notifyItemChanged(voca, false);
    }

    public int notifyItemChanged(VocaStudyChat voca, boolean blink) {
        int pos = data.indexOf(voca);
        if (pos > -1) {
            if (blink) {
                voca.setVIChecked(true);
            }
            notifyItemChanged(pos);
        }
        return pos;
    }

    public VocaStudyChat getVocaByVocaId(int vocaId, int vocaType) {
        for (int i = 0; i < data.size(); i++) {
            if (data.get(i) instanceof VocaStudyChat) {
                VocaStudyChat voca = (VocaStudyChat) data.get(i);
                if (voca.getVocaId() == vocaId && voca.getType() == vocaType) {
                    return voca;
                }
            }
        }
        return null;
    }

    public VocaStudyChat getVocaByIndex(int section, int row) {
        List<AmkiGradeHeader> headers = new ArrayList<>(map.keySet());
        if (section < headers.size()) {
            List<AmkiItem> vocas = map.get(headers.get(section));
            if (vocas != null && row < vocas.size()) {
                return (VocaStudyChat) vocas.get(row);
            }
        }
        return null;
    }

    public List<VocaStudyChat> getVocas() {
        return vocas;
    }

    public List<Object> getData() {
        return data;
    }

    public boolean isOpenCorrectSection(VocaStudyChat voca) {
        if (!vocas.isEmpty()) {
            return vocas.get(0).getTblParentSection().equals(voca.getTblParentSection());
        }
        return false;
    }

    public void setDisplayPronunciation(boolean displayPronunciation) {
        this.displayPronunciation = displayPronunciation;
    }

    public void setStudyRole(int studyRole) {
        this.studyRole = studyRole;
    }

    public void setShowAsterisk(int showAsterisk) {
        this.showAsterisk = showAsterisk;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public void setStudentAndTutorJoined(boolean isStudentAndTutorJoined) {
        this.isStudentAndTutorJoined = isStudentAndTutorJoined;
    }

    public void setStudyLang(int studyLang) {
        this.studyLang = studyLang;
    }

    public void setHighlightIndex(int highlightIndex) {
        this.highlightIndex = highlightIndex;
    }

    public int setHighlightIndexAndNotify(int highlightIndex) {
        setHighlightIndex(highlightIndex);
        if (highlightIndex > 0 && vocas != null) {
            for (VocaStudyChat voca : vocas) {
                if (voca.getIndex() == highlightIndex)
                    return notifyItemChanged(voca);
            }
        }
        return -1;
    }

    public void setHasHeader(boolean hasHeader) {
        this.hasHeader = hasHeader;
    }

    public void setHasBookName(boolean hasBookName) {
        this.hasBookName = hasBookName;
    }

    public void setIsDialog(boolean isDialog) {
        this.isDialog = isDialog;
    }

    public void setShowStudentMeaning(boolean showStudentMeaning) {
        this.showStudentMeaning = showStudentMeaning;
    }

    public void setListener(OnVocaStudyChatClickListener listener) {
        this.listener = listener;
    }

    class BookNameHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_book_name)
        TextView tvBookName;

        BookNameHolder(@NonNull View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }

        void bind() {
            tvBookName.setText(bookName);
        }
    }

}
