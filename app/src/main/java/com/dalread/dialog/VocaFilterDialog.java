package com.dalread.dialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;

import com.dalread.R;
import com.dalread.base.BaseDialog;
import com.dalread.database.SharedPreferencesDB;
import com.dalread.databinding.DialogVocaFilterBinding;
import com.dalread.model.VocaKnowGroupSelect;

public class VocaFilterDialog extends BaseDialog implements View.OnClickListener {
    private DialogVocaFilterBinding binding;
    private com.dalread.listener.OnClickListener listener;
    private SharedPreferencesDB sharedPreferencesDB;

    public VocaFilterDialog(@NonNull Context context, com.dalread.listener.OnClickListener listener) {
        super(context);
        binding = DialogVocaFilterBinding.inflate(LayoutInflater.from(getContext()), null, false);
        setContentView(binding.getRoot());
        this.listener = listener;
        this.sharedPreferencesDB = SharedPreferencesDB.getInstance(context);
        setDialogSizeWider(true);
        setOnClickListeners();
        loadSelectedVocaFilter();
    }
    private void loadSelectedVocaFilter() {
        binding.scVocaKnown.setChecked(sharedPreferencesDB.isVocaFilterVocaKnown());
        binding.scVocaUnknown.setChecked(sharedPreferencesDB.isVocaFilterVocaUnknown());
        binding.scVoca1st.setChecked(sharedPreferencesDB.isVocaFilterVoca1st());
        binding.scVoca2nd.setChecked(sharedPreferencesDB.isVocaFilterVoca2nd());
        binding.scVocaNotRated.setChecked(sharedPreferencesDB.isVocaFilterVocaNotRated());
        binding.scVocaBookmarked.setChecked(sharedPreferencesDB.isVocaFilterVocaBookmarked());
    }
    private void setOnClickListeners() {
        binding.btnCancel.setOnClickListener(this);
        binding.btnOk.setOnClickListener(this);
    }
    @Override
    public void onClick(View view) {
        dismiss();
        saveSelectedVocaFilter();
        if ((listener == null) || view.getId() == R.id.btn_cancel)
            return;

        VocaKnowGroupSelect vocaKnowGroupSelect = new VocaKnowGroupSelect(binding.scVocaNotRated.isChecked(), binding.scVoca1st.isChecked(), binding.scVoca2nd.isChecked(), binding.scVocaUnknown.isChecked(), binding.scVocaKnown.isChecked(), false, binding.scVocaBookmarked.isChecked());
        listener.onClick(view, vocaKnowGroupSelect);
    }

    private void saveSelectedVocaFilter() {
        sharedPreferencesDB.setVocaFilterVoca1st(binding.scVoca1st.isChecked());
        sharedPreferencesDB.setVocaFilterVoca2nd(binding.scVoca2nd.isChecked());
        sharedPreferencesDB.setVocaFilterVocaKnown(binding.scVocaKnown.isChecked());
        sharedPreferencesDB.setVocaFilterVocaUnknown(binding.scVocaUnknown.isChecked());
        sharedPreferencesDB.setVocaFilterVocaNotRated(binding.scVocaNotRated.isChecked());
        sharedPreferencesDB.setVocaFilterVocaBookmarked(binding.scVocaBookmarked.isChecked());
    }
}
