package com.dalread.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.addisonelliott.segmentedbutton.SegmentedButton;
import com.addisonelliott.segmentedbutton.SegmentedButtonGroup;
import com.dalread.R;
import com.dalread.adapter.holder.StudyChatItemHolder;
import com.dalread.base.EnumLanguage;
import com.dalread.component.FuriganaView;
import com.dalread.listener.OnClickListener;
import com.dalread.listener.OnVocaStudyChatClickListener;
import com.dalread.model.VocaStudyChat;
import com.dalread.model.roleplaying.Conversation;
import com.dalread.model.roleplaying.Mission;
import com.dalread.model.roleplaying.RolePlayingContent;
import com.dalread.util.Constant;
import com.dalread.util.Voca;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RolePlayingAdapter extends RecyclerView.Adapter {

    private static final int TYPE_MISSION = 0;
    private static final int TYPE_CONVERSATION = TYPE_MISSION + 1;
    private static final int TYPE_SENTENCE = TYPE_CONVERSATION + 1;

    private Context context;
    private List<Object> items;
    private List<Object> keys;
    private Map<Object, List> map;
    private int studyRole;
    private int showAsterisk;
    private boolean displayPronunciation;
    private boolean isStudentAndTutorJoined;
    private int languageLevel;
    private int studyLang;
    private int uid;
    private boolean showStudentMeaning;
    private OnVocaStudyChatClickListener onVocaStudyChatClickListener;
    private OnClickListener onClickListener;

    public RolePlayingAdapter(Context context) {
        this.context = context;
        items = new ArrayList<>();
        keys = new ArrayList<>();
        map = new LinkedHashMap<>();
    }

    @Override
    public int getItemViewType(int position) {
        Object item = items.get(position);
        if (item instanceof Mission)
            return TYPE_MISSION;
        if (item instanceof ConversationItem)
            return TYPE_CONVERSATION;
        return TYPE_SENTENCE;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_MISSION)
            return new MissionHolder(getHolderView(parent, R.layout.item_mission));
        if (viewType == TYPE_CONVERSATION)
            return new ConversationHolder(getHolderView(parent, R.layout.item_conversation));
        return new StudyChatItemHolder(getHolderView(parent, R.layout.item_study_chat));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        if (viewType == TYPE_MISSION) {
            ((MissionHolder) holder).bind((Mission) items.get(position));
        } else if (viewType == TYPE_CONVERSATION) {
            ((ConversationHolder) holder).bind((ConversationItem) items.get(position));
        } else {
            ((StudyChatItemHolder) holder).bind(context,
                    (VocaStudyChat) items.get(position),
                    studyRole, showAsterisk, displayPronunciation, isStudentAndTutorJoined, studyLang,
                    0, false, false, showStudentMeaning,
                    onVocaStudyChatClickListener
            );
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    private View getHolderView(@NonNull ViewGroup parent, @LayoutRes int layout) {
        return LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
    }

    public List getSentences(VocaStudyChat voca) {
        ConversationItem conversationItem = (ConversationItem) keys.get(voca.getTblParentSection());
        return map.get(conversationItem);
    }

    public int notifyItemChanged(Conversation conversation) {
        ConversationItem conversationItem = (ConversationItem) keys.get(conversation.getTblRow());
        int pos = items.indexOf(conversationItem);
        if (pos > -1) {
            notifyItemChanged(pos);
            notifyBranchChanged(pos, conversation);
        }
        return pos;
    }

    public void setData(RolePlayingContent rolePlayingContent) {
        items.clear();
        map.clear();
        if (rolePlayingContent == null || rolePlayingContent.getMission() == null) return;
        languageLevel = rolePlayingContent.getLanguageLevel();
        items.add(rolePlayingContent.getMission());
        List<Conversation> conversations = rolePlayingContent.getConversationList();
        for (int i = 0; i < conversations.size(); i++) {
            Conversation conversation = conversations.get(i);
            conversation.setTblSection(0);
            conversation.setTblRow(i);
            ConversationItem conversationItem = new ConversationItem(conversation);
            items.add(conversationItem);
            keys.add(conversationItem);
            List<VocaStudyChat> sentences = conversation.getSentences();
            for (int j = 0; j < sentences.size(); j++) {
                VocaStudyChat sentence = sentences.get(j);
                sentence.setTblParentSection(0);
                sentence.setTblParentRow(i);
                sentence.setTblTypeSync(Constant.API_VALUE.TBL_TYPE_SYNC_EXAMPLE_SENTENCE);
                sentence.setPath(Voca.getOutputRecordingFileName(studyLang, sentence.getType(), sentence.getVocaId(), uid));
            }
            Voca.parseAdapterData(Voca.groupVocaListByGrade(context, new ArrayList<>(sentences)));
            map.put(conversationItem, sentences);
        }
    }

    private void notifyBranchChanged(int pos, Conversation conversation) {
        int startPos = pos + 1;
        int count = 0;
        while (startPos < items.size()) {
            Object object = items.get(startPos);
            if (object instanceof ConversationItem) {
                ConversationItem conversationItem = (ConversationItem) object;
                String branch = conversationItem.conversation.getBranch();
                if (TextUtils.isEmpty(branch)) {
                    count++;
                    conversationItem.conversation.setDisplayConversation("DISPLAY");
                } else {
                    if (branch.contains("BRANCH(")) {
                        break;
                    }
                    count++;
                    if (branch.equals(conversation.getBranchSelected())) {
                        conversationItem.conversation.setDisplayConversation("DISPLAY");
                    } else {
                        conversationItem.conversation.setDisplayConversation("HIDE");
                    }
                }
                conversationItem.conversation.setBranchSelected(conversation.getBranchSelected());
                startPos++;
            }
        }
        if (count > 0) {
            notifyItemRangeChanged(pos + 1, count);
        }
    }

    public int getLanguageLevel() {
        return languageLevel;
    }

    public void setStudyRole(int studyRole) {
        this.studyRole = studyRole;
    }

    public void setShowAsterisk(int showAsterisk) {
        this.showAsterisk = showAsterisk;
    }

    public void setDisplayPronunciation(boolean displayPronunciation) {
        this.displayPronunciation = displayPronunciation;
    }

    public void setStudentAndTutorJoined(boolean studentAndTutorJoined) {
        isStudentAndTutorJoined = studentAndTutorJoined;
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

    public void setOnVocaStudyChatClickListener(OnVocaStudyChatClickListener onVocaStudyChatClickListener) {
        this.onVocaStudyChatClickListener = onVocaStudyChatClickListener;
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    /**
     * Inner View Holder classes
     */

    class MissionHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_message)
        TextView tvMessage;

        MissionHolder(@NonNull View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }

        void bind(Mission mission) {
            tvMessage.setText(mission.getMessage());
        }
    }

    class ConversationHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.v_item)
        LinearLayout vItem;
        @BindView(R.id.ic_play)
        ImageView icPlay;
        @BindView(R.id.tv_index)
        TextView tvIndex;
        @BindView(R.id.ic_arrow)
        ImageView icArrow;
        @BindView(R.id.fv_title)
        FuriganaView fvTitle;
        @BindView(R.id.tv_title)
        TextView tvTitle;
        @BindView(R.id.fv_default_phrase_for_student)
        FuriganaView fvDefaultPhraseForStudent;
        @BindView(R.id.tv_default_phrase_for_student)
        TextView tvDefaultPhraseForStudent;
        @BindView(R.id.fv_value)
        FuriganaView fvValue;
        @BindView(R.id.tv_value)
        TextView tvValue;

        private SegmentedButtonGroup segmentedButtonGroup;
        private ConversationItem conversationItem;

        ConversationHolder(@NonNull View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }

        void bind(ConversationItem conversationItem) {
            this.conversationItem = conversationItem;

            ViewGroup.LayoutParams layoutParams = itemView.getLayoutParams();
            if (conversationItem.conversation.isDisplayConversation()) {
                layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                vItem.setLayoutParams(layoutParams);
            } else {
                layoutParams.height = 0;
                vItem.setLayoutParams(layoutParams);
                return;
            }

            if (conversationItem.conversation.isChecked()) { // use key checked for blink mode
                vItem.setBackgroundResource(R.color.color_blink);
                vItem.postDelayed(normalItemRunnable, Constant.STOP_BLINK_DELAY_TIME);
            } else {
                vItem.setBackgroundResource(
                        "A".equals(conversationItem.conversation.getPersonAB())
                                ? R.color.color_conversation_a
                                : R.color.color_conversation_b
                );
            }

            icPlay.setImageResource(isStudentAndTutorJoined ? R.drawable.ic_sync : R.drawable.ic_volume_up_outline_48dp);

            String strIndex = conversationItem.conversation.getIndex() + " " + conversationItem.conversation.getPersonAB();
            tvIndex.setText(strIndex);

            if (studyRole == Constant.STUDY_ROLE_TUTOR || conversationItem.conversation.getSentences().isEmpty()) {
                icArrow.setVisibility(View.INVISIBLE);
            } else {
                icArrow.setImageResource(
                        conversationItem.isExpanded
                                ? R.drawable.ic_keyboard_arrow_up_black_24dp
                                : R.drawable.ic_keyboard_arrow_down_black_24dp
                );
                icArrow.setVisibility(View.VISIBLE);
            }

            loadRubyOrNormalText(
                    fvTitle, conversationItem.conversation.getTitleRubyText(),
                    tvTitle, conversationItem.conversation.getTitle()
            );

            if (TextUtils.isEmpty(conversationItem.conversation.getDefaultPhraseForStudent())) {
                loadRubyOrNormalText(
                        fvDefaultPhraseForStudent, "",
                        tvDefaultPhraseForStudent, ""
                );
            } else {
                loadDefaultPhraseForStudent(conversationItem);
            }

            loadRubyOrNormalText(
                    fvValue, conversationItem.conversation.getValueRubyText(),
                    tvValue, conversationItem.conversation.getValue()
            );

            if (segmentedButtonGroup != null) {
                vItem.removeView(segmentedButtonGroup);
            }
            String branch = conversationItem.conversation.getBranch();
            if (branch != null && branch.endsWith(")")) {
                int pos = branch.indexOf("(");
                if (pos >= 0) {
                    String[] branches = branch.substring(pos + 1, branch.length() - 1).split("/");
                    if (branches.length > 0) {
                        conversationItem.conversation.setBranches(Arrays.asList(branches));
                        String branchSelected = conversationItem.conversation.getBranchSelected();
                        segmentedButtonGroup = (SegmentedButtonGroup) getHolderView(vItem, R.layout.layout_segment_button_group_conversation);
                        SegmentedButton segmentedButton;
                        for (String aBranch : branches) {
                            segmentedButton = (SegmentedButton) getHolderView(segmentedButtonGroup, R.layout.layout_segment_button_conversation);
                            segmentedButton.setText(aBranch);
                            segmentedButton.setOnClickListener(v -> {
                                if (aBranch.equals(conversationItem.conversation.getBranchSelected()))
                                    return;
                                conversationItem.conversation.setBranchSelected(aBranch);
                                notifyBranchChanged(getAdapterPosition(), conversationItem.conversation);
                                onClick(v);
                            });
                            segmentedButtonGroup.addView(segmentedButton);
                            if (aBranch.equals(branchSelected)) {
                                segmentedButtonGroup.setPosition(segmentedButtonGroup.getButtons().size() - 1, false);
                            }
                        }
                        vItem.addView(segmentedButtonGroup);
                    }
                }
            }
        }

        private void loadRubyOrNormalText(FuriganaView fv, String rubyText,
                                          TextView tv, String text) {
            loadRubyOrNormalText(fv, rubyText, tv, text, false);
        }

        private boolean loadRubyOrNormalText(FuriganaView fv, String rubyText,
                                             TextView tv, String text,
                                             boolean isDefaultPhraseForStudent) {
            fv.setIsKnownPronounceMeaning(Voca.checkStudyLanguageJPCN(String.valueOf(studyLang)));
            fv.resetText();
            tv.setText("");
            if (TextUtils.isEmpty(rubyText) && TextUtils.isEmpty(text)) {
                fv.setVisibility(View.GONE);
                tv.setVisibility(View.GONE);
                return false;
            }
            if (Constant.MAKE_RUBY_TEXT && !TextUtils.isEmpty(rubyText)) {
                if (studyLang == EnumLanguage.ENGLISH.getIdApi()) {
                    fv.setTutor(true);
                } else {
                    fv.setTutor(studyRole == Constant.STUDY_ROLE_TUTOR);
                }
                fv.setShowMeaningJPCN(!Voca.checkStudyLanguageJPCN(String.valueOf(studyLang)));
                if (isDefaultPhraseForStudent) {
                    rubyText = "[" + rubyText + "]";
                }
                fv.setJText(rubyText);
                fv.setVisibility((!isDefaultPhraseForStudent || isDefaultPhraseForStudentVisible()) ? View.VISIBLE : View.GONE);
                tv.setVisibility(View.GONE);
                conversationItem.conversation.setRubyVocaIdsAndTypes(fv.getListId(), fv.getListType());
                return true;
            }
            fv.setVisibility(View.GONE);
            if (isDefaultPhraseForStudent) {
                text = "[" + text + "]";
            }
            tv.setText(text);
            tv.setVisibility((!isDefaultPhraseForStudent || isDefaultPhraseForStudentVisible()) ? View.VISIBLE : View.GONE);
            return false;
        }

        private void loadDefaultPhraseForStudent(ConversationItem conversationItem) {
            Voca.setNormalTextView(tvDefaultPhraseForStudent);
            boolean isNormalText = !loadRubyOrNormalText(
                    fvDefaultPhraseForStudent, conversationItem.conversation.getDefaultPhraseForStudentRubyText(),
                    tvDefaultPhraseForStudent, conversationItem.conversation.getDefaultPhraseForStudent(),
                    true
            );
            if (isNormalText && studyRole == Constant.STUDY_ROLE_OBSERVER) {
                int studentLanguageLevel = languageLevel;
                int phraseLanguageLevel = conversationItem.conversation.getDefaultPhraseLanguageLevel();
                if (Constant.PERSON_AB.A.equals(conversationItem.conversation.getPersonAB())) {
                    if (studentLanguageLevel > Constant.API_VALUE.LANGUAGE_LEVEL_INTERMEDIATE) {
                        Voca.setItalicAndStrikeThroughTextView(tvDefaultPhraseForStudent);
                    }
                } else {
                    if (studentLanguageLevel > phraseLanguageLevel) {
                        Voca.setItalicAndStrikeThroughTextView(tvDefaultPhraseForStudent);
                    }
                }
            }
        }

        private boolean isDefaultPhraseForStudentVisible() {
            if (studyRole == Constant.STUDY_ROLE_STUDENT) {
                int studentLanguageLevel = languageLevel;
                int phraseLanguageLevel = conversationItem.conversation.getDefaultPhraseLanguageLevel();
                if (Constant.PERSON_AB.A.equals(conversationItem.conversation.getPersonAB())) {
                    if (isStudentAndTutorJoined && studentLanguageLevel > Constant.API_VALUE.LANGUAGE_LEVEL_INTERMEDIATE) {
                        return false;
                    } else {
                        return true;
                    }
                } else {
                    if (isStudentAndTutorJoined && studentLanguageLevel > phraseLanguageLevel) {
                        return false;
                    } else {
                        return true;
                    }
                }
            } else if (studyRole == Constant.STUDY_ROLE_TUTOR) {
                return false;
            } else { // Constant.STUDY_ROLE_OBSERVER
                return true;
            }
        }

        @OnClick({R.id.v_item, R.id.ic_play,
                R.id.fv_title, R.id.fv_default_phrase_for_student, R.id.fv_value})
        void onClick(View view) {
            if (onClickListener != null) {
                onClickListener.onClick(view, conversationItem.conversation);
            }
        }

        private Runnable normalItemRunnable = new Runnable() {

            @Override
            public void run() {
                vItem.setBackgroundResource(
                        "A".equals(conversationItem.conversation.getPersonAB())
                                ? R.color.color_conversation_a
                                : R.color.color_conversation_b
                );
                conversationItem.conversation.setChecked(false);
            }
        };
    }

    /**
     * Inner Model classes
     */

    private class ConversationItem {

        private Conversation conversation;
        private boolean isExpanded;

        ConversationItem(Conversation conversation) {
            this.conversation = conversation;
        }
    }
}
