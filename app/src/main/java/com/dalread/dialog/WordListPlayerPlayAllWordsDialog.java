package com.dalread.dialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;

import com.dalread.R;
import com.dalread.base.BaseDialog;
import com.dalread.databinding.DialogPlayerWordListPlayAllWordsBinding;
import com.dalread.model.VocaKnowGroupSelect;

/*
 * @deprecated Replaced by {@link #WordListPlayerPlayAllWordsDialog2}
 * WordListPlayerPlayAllWordsDialog2을 쓰고, voca filter를 업데이트 해서 WordListPlayerPlayAllWordsDialog2들어가기전에 voca를 filter해서 들어가게.
 */
@Deprecated
public class WordListPlayerPlayAllWordsDialog extends BaseDialog implements View.OnClickListener {
    private DialogPlayerWordListPlayAllWordsBinding binding;
    private com.dalread.listener.OnClickListener listener;

    public WordListPlayerPlayAllWordsDialog(@NonNull Context context, com.dalread.listener.OnClickListener listener) {
        super(context);
        this.listener = listener;
        setDialogSizeWider(true);
        binding = DialogPlayerWordListPlayAllWordsBinding.inflate(LayoutInflater.from(getContext()), null, false);
        setContentView(binding.getRoot());
        setOnClickListeners();

    }
    private void setOnClickListeners() {
        binding.tvOpenSelectedItemsOnly.setOnClickListener(this);
        binding.tvOpenBookmarkedItemsOnly.setOnClickListener(this);
        binding.tvOpenAllItems.setOnClickListener(this);
    }
    @Override
    public void onClick(View view) {
        dismiss();
        if (listener == null)
            return;

        VocaKnowGroupSelect vocaKnowGroupSelect = new VocaKnowGroupSelect();
        switch (view.getId()) {
            case R.id.tvOpenSelectedItemsOnly:
                vocaKnowGroupSelect = new VocaKnowGroupSelect(binding.scVocaNotRated.isChecked(), binding.scVoca1st.isChecked(), binding.scVoca2nd.isChecked(), binding.scVocaUnknown.isChecked(), binding.scVocaKnown.isChecked(), false, binding.scVocaBookmarked.isChecked());
                break;
            case R.id.tvOpenBookmarkedItemsOnly:
                vocaKnowGroupSelect.setValueToOpenBookmarkedItemsOnly();
            case R.id.tvOpenAllItems:
                vocaKnowGroupSelect.setValueToOpenAllItems();
                break;
        }
        listener.onClick(view, vocaKnowGroupSelect);
    }
}
