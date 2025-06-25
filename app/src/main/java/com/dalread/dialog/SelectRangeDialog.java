package com.dalread.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.view.View;

import androidx.annotation.NonNull;

import com.dalread.R;
import com.dalread.base.BaseDialog;
import com.dalread.databinding.DialogSelectRangeBinding;

//@SuppressLint("NonConstantResourceId")
public class SelectRangeDialog extends BaseDialog implements View.OnClickListener, DialogInterface.OnDismissListener {
    protected DialogSelectRangeBinding binding;
//    @BindView(R.id.et_from)
//    EditText etFrom;
//    @BindView(R.id.et_to)
//    EditText etTo;
    private Context context;
    private final OnRangeSelectListener listener;

    private View getContentView() {
        binding = DialogSelectRangeBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        return view;
    }

    public SelectRangeDialog(@NonNull Context context, OnRangeSelectListener listener) {
        super(context, R.style.TransparentDialog);
        this.context = context;
        this.listener = listener;
        setContentView(getContentView());
//        binding.tvDescription.setVisibility(View.GONE);
//        setContentView(R.layout.dialog_select_range);
//        ButterKnife.bind(this);
        setOnClickListeners();
        setOnDismissListener(this);
    }

    public void setTitle(int idRes) {
        binding.tvTitle.setText(idRes);
    }
//    public void setDesc(int idRes) {
//        binding.tvDescription.setVisibility(View.VISIBLE);
//        binding.tvDescription.setText(idRes);
//    }

    public void show(int from, int to) {
        String text = String.valueOf(from);
        binding.etFrom.setText(text);
        binding.etFrom.setSelection(text.length());
        text = String.valueOf(to);
        binding.etTo.setText(text);
        binding.etTo.setSelection(text.length());
        show();
    }
//
//    @OnClick(R.id.btn_negative)
//    void onCancel(View view) {
//        dismiss();
//    }
//
//    @OnClick(R.id.btn_positive)
//    void onOK(View view) {
//        dismiss();
//        if (listener != null) {
//            int from;
//            try {
//                from = Integer.parseInt(binding.etFrom.getText().toString());
//            } catch (Exception e) {
//                from = 0;
//            }
//            int to;
//            try {
//                to = Integer.parseInt(binding.etTo.getText().toString());
//            } catch (Exception e) {
//                to = 0;
//            }
//            listener.onSelect(from, to);
//        }
//    }

    @Override
    public void onDismiss(DialogInterface dialogInterface) {
        if (listener != null) {
            listener.onDismiss(null);
        }
    }
    private void setOnClickListeners() {
        binding.btnNegative.setOnClickListener(this);
        binding.btnPositive.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        dismiss();
        switch (view.getId()) {
            case R.id.btn_positive:
                if (listener != null) {
                    int from;
                    try {
                        from = Integer.parseInt(binding.etFrom.getText().toString());
                    } catch (Exception e) {
                        from = 0;
                    }
                    int to;
                    try {
                        to = Integer.parseInt(binding.etTo.getText().toString());
                    } catch (Exception e) {
                        to = 0;
                    }
                    listener.onSelect(from, to);
                }
                break;
        }
    }

    public interface OnRangeSelectListener {
        void onSelect(int from, int to);

        void onDismiss(View v);
    }
}
