package com.dalread.dialog;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.dalread.R;
import com.dalread.interfaces.IVocaFullPlayTTSItem;
import com.dalread.util.BaseVoca;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.io.File;

import butterknife.BindColor;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

@SuppressLint("NonConstantResourceId")
public class UploadOrRecordAgainDialog extends BottomSheetDialog {

    @BindView(R.id.ic_play)
    ImageView icPlay;

    @BindColor(R.color.color_play)
    int clPlay;
    @BindColor(R.color.color_stop)
    int clStop;

    private final Context context;
    private IVocaFullPlayTTSItem voca;
    private final OnClickListener listener;
    private final MediaPlayer mediaPlayer;

    public UploadOrRecordAgainDialog(@NonNull Context context, OnClickListener listener) {
        super(context);

        setCancelable(true);
        setCanceledOnTouchOutside(true);
        setContentView(R.layout.dialog_upload_or_record_again);
        ButterKnife.bind(this);

        this.context = context;
        this.listener = listener;

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        setOnDismissListener(dialog -> {
            if (mediaPlayer.isPlaying()) {
                stopVoca();
            }
        });
    }

    public void setVoca(IVocaFullPlayTTSItem voca) {
        this.voca = voca;
    }

    @OnClick({R.id.ic_close, R.id.ic_play, R.id.ic_record, R.id.btn_upload})
    void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.ic_play:
                if (mediaPlayer.isPlaying()) {
                    stopVoca();
                } else {
                    playVoca();
                }
                break;
            case R.id.ic_record:
                dismiss();
                if (listener != null) {
                    listener.onRecordClick();
                }
                break;
            case R.id.btn_upload:
                dismiss();
                if (listener != null) {
                    listener.onUploadClick();
                }
                break;
            default:
                dismiss();
                break;
        }
    }

    private void playVoca() {
        try {
            File file = BaseVoca.getVoiceFileOnLocal(context, voca.getVIPath());
            Uri uri = Uri.fromFile(file);
            mediaPlayer.setDataSource(context, uri);
            mediaPlayer.setLooping(true);
            mediaPlayer.prepare();
            mediaPlayer.start();
            icPlay.setColorFilter(clPlay);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void stopVoca() {
        mediaPlayer.stop();
        mediaPlayer.reset();
        icPlay.setColorFilter(clStop);
    }

    public interface OnClickListener {

        void onRecordClick();

        void onUploadClick();
    }
}
