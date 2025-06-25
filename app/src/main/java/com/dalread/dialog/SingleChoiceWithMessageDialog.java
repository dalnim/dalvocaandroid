package com.dalread.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.recyclerview.widget.RecyclerView;

import com.dalread.R;
import com.dalread.databinding.ItemSingleChoiceBinding;
import com.dalread.listener.OnClickDialogListener;
import com.dalread.util.Utils;

public class SingleChoiceWithMessageDialog implements DialogInterface.OnDismissListener {

    private final AlertDialog.Builder builder;
    private Context context;
    private int pos;
    private OnClickDialogListener listener;
    private boolean isRightHandMode = true;

    public SingleChoiceWithMessageDialog(Context context) {
        this.context = context;
        builder = new AlertDialog.Builder(context, R.style.VocaAlertDialog);
    }

    public void show(@StringRes int titleId, @StringRes int msgId, CharSequence[] items, int checkedItem, @StringRes int positiveId, @StringRes int negativeId, final OnClickDialogListener listener) {
        show(titleId, msgId, items, checkedItem, positiveId, negativeId, false, listener);
    }

    public void show(@StringRes int titleId, @StringRes int msgId, CharSequence[] items, int checkedItem, @StringRes int positiveId, @StringRes int negativeId, boolean useNegativeCallback, final OnClickDialogListener listener) {
        AlertDialog dialog = initDialog(titleId, msgId, items, checkedItem, positiveId, negativeId, useNegativeCallback, listener);
        dialog.show();
        if (!isRightHandMode) {
            Button positiveBtn = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            ViewGroup parentView = (ViewGroup) positiveBtn.getParent();
            if (parentView instanceof LinearLayout) {
                if (parentView.getChildCount() > 2) {
                    // Following this link: https://chromium.googlesource.com/android_tools/+/25d57ead05d3dfef26e9c19b13ed10b0a69829cf/sdk/platforms/android-23/data/res/layout/alert_dialog_button_bar_material.xml
                    // Child view at index 1 is Space, it has 0dp width, 0dp height and the weight is 1.
                    parentView.getChildAt(1).setVisibility(View.GONE);
                }
                ((LinearLayout) parentView).setGravity(Gravity.START);
            }
        }
        Utils.setDialogSizeWider(context, dialog);
    }

    public void showWrapContentHeight(@StringRes int titleId, @StringRes int msgId, CharSequence[] items, int checkedItem, @StringRes int positiveId, @StringRes int negativeId, final OnClickDialogListener listener) {
        AlertDialog dialog = initDialog(titleId, msgId, items, checkedItem, positiveId, negativeId, false, listener);
        dialog.show();
    }

    private AlertDialog initDialog(@StringRes int titleId, @StringRes int msgId, CharSequence[] items, int checkedItem, @StringRes int positiveId, @StringRes int negativeId, boolean useNegativeCallback, final OnClickDialogListener listener){
        this.listener = listener;
        pos = checkedItem;
        View customView = LayoutInflater.from(context).inflate(R.layout.layout_single_choice_with_message_dialog, null, false);
        TextView tvMsg = customView.findViewById(R.id.tv_message);
        tvMsg.setText(msgId);
        RecyclerView rvItems = customView.findViewById(R.id.rv_items);
        SingleChoiceAdapter adapter = new SingleChoiceAdapter(items);
        rvItems.setAdapter(adapter);
        AlertDialog dialog = builder.setTitle(titleId)
                .setView(customView)
//                .setSingleChoiceItems(items, checkedItem, (dialog1, which) -> pos = which) //이걸 하면 선택된 POS가 맨위로 보이긴 하지만, 설명부분도 같이 사라진다.
                .setPositiveButton(positiveId, (dialog2, which) -> {
                    dialog2.dismiss();
                    if (pos >= 0 && listener != null) {
                        listener.onClick(null, pos);
                    }
                })
                .setNegativeButton(negativeId, (dialog3, which) -> {
                    dialog3.dismiss();
                    if (useNegativeCallback && listener != null) {
                        listener.onClick(null, -1);
                    }
                })
                .create();
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setOnDismissListener(this);
        return dialog;
    }

    @Override
    public void onDismiss(DialogInterface dialogInterface) {
        if (listener != null) {
            listener.onDismiss(null, -1);
        }
    }

    public void setRightHandMode(boolean rightHandMode) {
        isRightHandMode = rightHandMode;
    }

    private class SingleChoiceAdapter extends RecyclerView.Adapter<SingleChoiceAdapter.SingleChoiceAdapterVH> {
        private CharSequence[] items;

        public SingleChoiceAdapter(CharSequence[] items) {
            this.items = items;
        }

        @NonNull
        @Override
        public SingleChoiceAdapterVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new SingleChoiceAdapterVH(ItemSingleChoiceBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
        }

        @Override
        public int getItemCount() {
            return items.length;
        }

        @Override
        public void onBindViewHolder(@NonNull SingleChoiceAdapterVH holder, int position) {
            holder.bindData(items[position]);
        }

        private class SingleChoiceAdapterVH extends RecyclerView.ViewHolder {
            private ItemSingleChoiceBinding binding;

            public SingleChoiceAdapterVH(ItemSingleChoiceBinding binding) {
                super(binding.getRoot());
                this.binding = binding;
            }

            public void bindData(CharSequence charSequence) {
                binding.rb.setText(charSequence);
                binding.rb.setChecked(pos == getAdapterPosition());
                binding.rb.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    if (isChecked) {
                        int oldPos = pos;
                        pos = getAdapterPosition();
                        if (oldPos == pos) return;
                        notifyItemChanged(oldPos);
                        notifyItemChanged(pos);
                    }
                });
            }
        }
    }
}
