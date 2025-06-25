package com.dalread.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.view.View;

import androidx.annotation.NonNull;

import com.dalread.base.BasePlayerDialog;
import com.dalread.databinding.DialogVocaStudyChatBinding;
import com.dalread.interfaces.IVocaBasicItem;
import com.dalread.listener.OnClickDialogListener;
import com.dalread.util.StringUtils;
import com.dalread.util.UserUtil;

import butterknife.ButterKnife;

public class VocaStudyChatDialog extends BasePlayerDialog implements View.OnClickListener, DialogInterface.OnDismissListener {

    private OnClickDialogListener listener;
//    private View view;
    private Object data;

    private DialogVocaStudyChatBinding binding;

    @Override
    protected View getContentView() {
        binding = DialogVocaStudyChatBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        return view;
    }

    public VocaStudyChatDialog(@NonNull Context context, Object data, OnClickDialogListener listener) {
//        super(context, R.style.TransparentDialog);
        super(context);
        this.listener = listener;
        this.data = data;
        setDialogSizeWider(true);
//        setContentView(R.layout.dialog_voca_study_chat);
//        ButterKnife.bind(this);
        setOnDismissListener(this);
        showOpenHanjaMenu(data);
        hideMenusOnReleaseMode();
    }
    private void hideMenusOnReleaseMode() {
        if (UserUtil.isDebugOrAdminUser(getContext())) {
            binding.llOpenPhraseInformation.setVisibility(View.VISIBLE);
        } else {
            binding.llOpenPhraseInformation.setVisibility(View.GONE);
        }
    }
    public void showOpenHanjaMenu(Object data) {
        binding.llOpenAraHanjaWithHanja.setVisibility(View.GONE);
        if ((data != null) && (data instanceof IVocaBasicItem)) {
            String voca = ((IVocaBasicItem) data).getVIVoca();
            if (StringUtils.containChineseCharacters(voca)) {
                binding.llOpenAraHanjaWithHanja.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    protected void initOnClickListener() {
        binding.llOpenPhraseInformation.setOnClickListener(this);
        binding.llOpenAraHanjaWithHanja.setOnClickListener(this);
        binding.tvEditPhrase.setOnClickListener(this);
        binding.tvPlayThisPhrase.setOnClickListener(this);
        binding.tvWebDictionary.setOnClickListener(this);
        binding.tvCopyThisPhrase.setOnClickListener(this);
        binding.tvCancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        dismiss();
        if (listener != null) {
            listener.onClick(v, data);
        }
    }

//    @OnClick({R.id.llOpenPhraseInformation, R.id.llOpenAraHanjaWithHanja, R.id.tv_edit_phrase, R.id.tv_play_this_phrase, R.id.tv_web_dictionary, R.id.tv_copy_this_phrase, R.id.tv_cancel})
//    void onClick(View view) {
//        dismiss();
//        if (listener != null) {
//            listener.onClick(view, data);
//        }
//    }

    @Override
    public void onDismiss(DialogInterface dialogInterface) {
        if (listener != null) {
            listener.onDismiss(view, -1);
        }
    }
}
