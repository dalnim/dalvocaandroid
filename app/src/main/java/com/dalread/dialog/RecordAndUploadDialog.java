package com.dalread.dialog;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.dalread.R;
import com.dalread.composition.VoiceRecording;
import com.dalread.interfaces.IVocaFullPlayTTSItem;
import com.dalread.util.BaseVoca;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.io.File;

import butterknife.BindColor;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

@SuppressLint("NonConstantResourceId")
public class RecordAndUploadDialog extends BottomSheetDialog implements DialogInterface.OnDismissListener {

    @BindView(R.id.tv_header)
    TextView tvHeader;
    @BindView(R.id.ivMic)
    ImageView ivMic;
    @BindView(R.id.ivSpeaker)
    ImageView ivSpeaker;
    @BindView(R.id.tv_recording)
    TextView tvRecording;
    @BindView(R.id.btn_send)
    Button btnUpload;

    @BindColor(R.color.color_play)
    int clPlay;
    @BindColor(R.color.color_black_speaker_icon)
    int clStop;

    private final Context context;
    private final File voiceFolder;
    private final OnClickListener listener;
    private VoiceRecording voiceRecording;

    public RecordAndUploadDialog(@NonNull Context context, File voiceFolder, OnClickListener listener) {
        super(context, R.style.TransparentBottomSheetDialog);

        setCancelable(true);
        setCanceledOnTouchOutside(true);
        setContentView(R.layout.dialog_send_or_record_again);
        ButterKnife.bind(this);

        this.context = context;
        this.voiceFolder = voiceFolder;
        this.listener = listener;

        initLayout();
        initMediaPlayer();

        setOnDismissListener(this);
    }

    public RecordAndUploadDialog(@NonNull Context context, File voiceFolder, IVocaFullPlayTTSItem voca, OnClickListener listener) {
        this(context, voiceFolder, listener);

        setRecordFile(voca);
    }

    private void initLayout() {
        tvHeader.setText(R.string.record_and_upload);
        btnUpload.setText(R.string.upload);
    }

    private void initMediaPlayer() {
        if (context instanceof Activity) {
            voiceRecording = new VoiceRecording((Activity)context, ivMic, ivSpeaker);
        }
    }

    public void setRecordFile(IVocaFullPlayTTSItem voca) {
        voiceRecording.recordFile = BaseVoca.getVoiceFileOnLocal(voiceFolder, voca.getVIPath());
    }

    @Override
    public void show() {
        super.show();
        enableUploadButton(false);
    }

    private void enableUploadButton(boolean enable) {
        if (enable) {
            btnUpload.setEnabled(true);
            btnUpload.setAlpha(1);
        } else {
            btnUpload.setEnabled(false);
            btnUpload.setAlpha(.5f);
        }
    }

    @OnClick({R.id.ic_close, R.id.ivSpeaker, R.id.ivMic, R.id.btn_send})
    void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.ivSpeaker:
                voiceRecording.onClickSpeaker(true);
                break;
            case R.id.ivMic:
                voiceRecording.onClickMic();
                if (voiceRecording.recording) {
                    enableUploadButton(false);
                } else {
                    enableUploadButton(true);
                }
                break;
            case R.id.btn_send:
                dismiss();
                if (listener != null) {
                    if (voiceRecording.recording) {
                        voiceRecording.updateRecorder();
                    }
                    listener.onUploadClick(voiceRecording.recordFile);
                }
                break;
            default:
                dismiss();
                break;
        }
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        voiceRecording.dismiss();
    }


    public void onDestroy() {
        voiceRecording.destroy();
    }

    public interface OnClickListener {

        void onUploadClick(File recordFile);
    }
}
