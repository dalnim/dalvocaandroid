package com.dalread.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.dalread.R;
import com.dalread.base.EnumLanguage;
import com.dalread.databinding.ActivityGptWebTextShortCutEditBinding;
import com.dalread.model.GptTextShortCut;
import com.dalread.util.ToastUtil;
import com.dalread.util.Utils;

public class ChatGptTextShortCutEditDialog {
    private Activity activity;
    private Dialog dialog;
    private GptTextShortCut shortCut;
    private ActivityGptWebTextShortCutEditBinding binding;
    private OnTextShortCutEditListener listener;
    private boolean isEditMode;
    public interface OnTextShortCutEditListener {
        void onChanged(GptTextShortCut shortCut);
    }

    public ChatGptTextShortCutEditDialog(Activity activity, GptTextShortCut shortCut, boolean isEditMode, OnTextShortCutEditListener listener) {
        this.binding = ActivityGptWebTextShortCutEditBinding.inflate(LayoutInflater.from(activity));
        this.activity = activity;
        this.shortCut = shortCut;
        this.isEditMode = isEditMode;
        this.listener = listener;
    }

    public void showDialog() {
        dialog = new Dialog(activity, R.style.TransparentDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(binding.getRoot());
        binding.tbForPopupMenu1.setVisibility(isEditMode ? View.GONE : View.VISIBLE);
        binding.etShortcut.setText(shortCut.getMEANING(EnumLanguage.getMotherTongueLanguage(activity)));

        binding.btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        binding.btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String shortCutContent = binding.etShortcut.getText().toString();
                if (Utils.isEmpty(shortCutContent)) {
                    ToastUtil.getInstance(dialog.getContext()).show(R.string.toast_content_can_not_be_empty);
                } else {
                    shortCut.setTitle("");
                    shortCut.setMEANINGByMenuLang(activity, shortCutContent);
                    shortCut.setUsePopup1Menu(binding.tbForPopupMenu1.isChecked());
                    if (listener != null) {
                        listener.onChanged(shortCut);
                    }
                    dialog.dismiss();
                }
            }
        });

        dialog.show();
        int width = (int)(activity.getResources().getDisplayMetrics().widthPixels*0.9);
        dialog.getWindow().setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);
    }
}

