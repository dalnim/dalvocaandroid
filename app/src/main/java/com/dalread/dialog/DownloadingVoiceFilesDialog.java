package com.dalread.dialog;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.dalread.R;
import com.dalread.base.BaseDialog;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

@SuppressLint("NonConstantResourceId")
public class DownloadingVoiceFilesDialog extends BaseDialog {

    @BindView(R.id.tv_downloading)
    TextView tvDownloading;
    @BindView(R.id.pb_downloading)
    ProgressBar pbDownloading;

    @BindString(R.string.tpl_downloading)
    String tplDownloading;

    private boolean dismissed;
    private int max;

    public DownloadingVoiceFilesDialog(@NonNull Context context) {
        super(context, R.style.TransparentDialog);

        setContentView(R.layout.dialog_downloading_voice_files);
        setOnDismissListener(dialog -> dismissed = true);
        ButterKnife.bind(this);
    }

    public void setMax(int max) {
        this.max = max;
    }

    public void setProgress(int progress) {
        if (dismissed)
            return;

        String text = String.format(tplDownloading, progress, max);
        tvDownloading.setText(text);

        pbDownloading.setMax(max);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            pbDownloading.setProgress(progress, true);
        } else {
            pbDownloading.setProgress(progress);
        }

        show();
    }

    @OnClick(R.id.tv_background)
    void onClick() {
        dismiss();
    }
}
