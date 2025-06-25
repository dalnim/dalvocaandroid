package com.dalread.dialog;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.dalread.R;
import com.dalread.base.BaseDialog;
import com.dalread.interfaces.IVocaFullPlayTTSItem;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

@SuppressLint("NonConstantResourceId")
public class SyncACellDialog extends BaseDialog {

    @BindView(R.id.v_change_word_known_status)
    View vChangeWordKnownStatus;
    @BindView(R.id.tv_play_this_phrase)
    TextView tvPlayThisPhrase;

    private final com.dalread.listener.OnClickListener listener;
    private Object object;

    public SyncACellDialog(@NonNull Context context, com.dalread.listener.OnClickListener listener) {
        super(context, R.style.TransparentDialog);

        this.listener = listener;

        setContentView(R.layout.dialog_sync_a_cell);
        ButterKnife.bind(this);
    }

    @Override
    public void show() {
        super.show();

        vChangeWordKnownStatus.setVisibility(View.VISIBLE);
        object = null;
        tvPlayThisPhrase.setText(R.string.play_this_phrase);
    }

    public void show(Object object, boolean isPopup) {
        show();

        vChangeWordKnownStatus.setVisibility(isPopup ? View.GONE : View.VISIBLE);
        this.object = object;
        if (object instanceof IVocaFullPlayTTSItem) {
            tvPlayThisPhrase.setText(((IVocaFullPlayTTSItem) object).isVIPlaying() ? R.string.stop_this_phrase : R.string.play_this_phrase);
        }
    }

    @OnClick({R.id.tv_open, R.id.tv_sync, R.id.tv_change_word_known_status, R.id.tv_save_as_a_last_wordbook, R.id.tv_play_this_phrase, R.id.tv_cancel})
    void onClick(View view) {
        dismiss();
        if (listener != null) {
            listener.onClick(view, object);
        }
    }
}
