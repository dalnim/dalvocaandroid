package com.dalread.dialog;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;

import com.dalread.base.BasePlayerDialog;
import com.dalread.databinding.DialogPlayerShowMeaningBinding;
import com.dalread.util.Constant;

public class PlayerShowMeaningDialog extends BasePlayerDialog implements View.OnClickListener {
    private OnClickListener listener;

    private DialogPlayerShowMeaningBinding binding;

    @Override
    protected View getContentView() {
        binding = DialogPlayerShowMeaningBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        return view;
    }

//    @Override
//    protected int getContentViewId() {
//        return R.layout.dialog_player_show_meaning;
//    }

    public PlayerShowMeaningDialog(@NonNull Context context, int serverType, boolean isMedia, boolean isRefreshMeaning, OnClickListener listener) {
        super(context);
        this.listener = listener;
        switch (serverType) {
            case Constant.PLAYER.SERVER.TYPE.NONE:
                binding.llDownload.setVisibility(View.GONE);
                break;
            case Constant.PLAYER.SERVER.TYPE.FTP:
            case Constant.PLAYER.SERVER.TYPE.FREE_FTP_DOWNLOAD:
                binding.llOpenVideoInformation.setVisibility(View.GONE);
                break;
            case Constant.PLAYER.SERVER.TYPE.WEBDAV:
                binding.llOpenVideoInformation.setVisibility(isMedia ? View.VISIBLE : View.GONE);
                break;
        }
    }

    @Override
    protected void initOnClickListener() {
        binding.llOpenVideoInformation.setOnClickListener(this);
        binding.llDownload.setOnClickListener(this);
        binding.tvCancel.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        dismiss();
        if (listener != null) {
            listener.onClick(this, v.getId());
        }
    }


//    @OnClick({R.id.llOpenVideoInformation, R.id.llDownload, R.id.tv_cancel})
//    void onClick(View view) {
//        dismiss();
//        if (listener != null) {
//            listener.onClick(this, view.getId());
//        }
//    }
}
