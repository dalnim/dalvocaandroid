package com.dalread.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.dalread.R;
import com.dalread.databinding.DialogAskRolePlayingPromptEditBinding;
import com.dalread.util.ToastUtil;
import com.dalread.util.Utils;

public class AskRolePlayingPromptEditDialog {
    private Activity activity;
    private Dialog dialog;
    private String content;
    private DialogAskRolePlayingPromptEditBinding binding;
    private AskRolePlayingPromptEditListener listener;
    public interface AskRolePlayingPromptEditListener {
        void onChanged(String value);
    }

    public AskRolePlayingPromptEditDialog(Activity activity, String content, AskRolePlayingPromptEditListener listener) {
        this.binding = DialogAskRolePlayingPromptEditBinding.inflate(LayoutInflater.from(activity));
        this.activity = activity;
        this.content = content;
        this.listener = listener;
    }

    public void showDialog() {
        dialog = new Dialog(activity, R.style.TransparentDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(binding.getRoot());
        binding.etShortcut.setText(content);

        binding.btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        binding.btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String editedContent = binding.etShortcut.getText().toString();
                if (Utils.isEmpty(editedContent)) {
                    ToastUtil.getInstance(dialog.getContext()).show(R.string.toast_content_can_not_be_empty);
                } else {

                    if (listener != null) {
                        listener.onChanged(editedContent);
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

