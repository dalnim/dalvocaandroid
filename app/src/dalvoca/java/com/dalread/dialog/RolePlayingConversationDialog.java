package com.dalread.dialog;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;

import com.dalread.R;
import com.dalread.base.BaseDialog;
import com.dalread.model.roleplaying.Conversation;
import com.dalread.util.Constant;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

@SuppressLint("NonConstantResourceId")
public class RolePlayingConversationDialog extends BaseDialog {

    @BindView(R.id.dv_see_examples)
    View dvSeeExamples;
    @BindView(R.id.tv_see_examples)
    View tvSeeExamples;

    private final com.dalread.listener.OnClickListener listener;
    private Conversation conversation;

    public RolePlayingConversationDialog(@NonNull Context context, com.dalread.listener.OnClickListener listener) {
        super(context, R.style.TransparentDialog);

        this.listener = listener;

        setContentView(R.layout.dialog_role_playing_conversation);
        ButterKnife.bind(this);
    }

    public void show(Conversation conversation, int studyRole) {
        this.conversation = conversation;
        if (studyRole == Constant.STUDY_ROLE_TUTOR) {
            dvSeeExamples.setVisibility(View.GONE);
            tvSeeExamples.setVisibility(View.GONE);
        } else {
            dvSeeExamples.setVisibility(View.VISIBLE);
            tvSeeExamples.setVisibility(View.VISIBLE);
        }
        show();
    }

    @OnClick({
            R.id.tv_change_word_known_status, R.id.tv_see_examples,
            R.id.tv_cancel
    })
    void onClick(View view) {
        dismiss();
        if (listener != null) {
            listener.onClick(view, conversation);
        }
    }
}
