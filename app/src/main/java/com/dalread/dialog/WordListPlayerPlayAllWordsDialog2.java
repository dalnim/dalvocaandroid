package com.dalread.dialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;

import com.dalread.R;
import com.dalread.base.BaseDialog;
import com.dalread.database.SharedPreferencesDB;
import com.dalread.databinding.DialogPlayerWordListPlayAllWords2Binding;
import com.dalread.model.VocaKnowGroupSelect;

public class WordListPlayerPlayAllWordsDialog2 extends BaseDialog implements View.OnClickListener {
    private DialogPlayerWordListPlayAllWords2Binding binding;
    private com.dalread.listener.OnClickListener listener;
    private SharedPreferencesDB sharedPreferencesDB;
    public WordListPlayerPlayAllWordsDialog2(@NonNull Context context, com.dalread.listener.OnClickListener listener) {
        super(context);
        this.listener = listener;
        this.sharedPreferencesDB = SharedPreferencesDB.getInstance(context);
        binding = DialogPlayerWordListPlayAllWords2Binding.inflate(LayoutInflater.from(getContext()), null, false);
        setContentView(binding.getRoot());
        setOnClickListeners();

        loadSelectedVocaFilter();
        setDialogSizeWider(true);


    }
    //일단 VocaFilter꺼를 같이 사용한다.
    private void loadSelectedVocaFilter() {
        binding.scVocaKnown.setChecked(sharedPreferencesDB.isVocaFilterVocaKnown());
        binding.scVocaUnknown.setChecked(sharedPreferencesDB.isVocaFilterVocaUnknown());
        binding.scVoca1st.setChecked(sharedPreferencesDB.isVocaFilterVoca1st());
        binding.scVoca2nd.setChecked(sharedPreferencesDB.isVocaFilterVoca2nd());
        binding.scVocaNotRated.setChecked(sharedPreferencesDB.isVocaFilterVocaNotRated());
        binding.scVocaBookmarked.setChecked(sharedPreferencesDB.isVocaFilterVocaBookmarked());
    }
    private void saveSelectedVocaFilter() {
        sharedPreferencesDB.setVocaFilterVoca1st(binding.scVoca1st.isChecked());
        sharedPreferencesDB.setVocaFilterVoca2nd(binding.scVoca2nd.isChecked());
        sharedPreferencesDB.setVocaFilterVocaKnown(binding.scVocaKnown.isChecked());
        sharedPreferencesDB.setVocaFilterVocaUnknown(binding.scVocaUnknown.isChecked());
        sharedPreferencesDB.setVocaFilterVocaNotRated(binding.scVocaNotRated.isChecked());
        sharedPreferencesDB.setVocaFilterVocaBookmarked(binding.scVocaBookmarked.isChecked());
    }
    private void setOnClickListeners() {
        binding.tvCancel.setOnClickListener(this);
        binding.tvApply.setOnClickListener(this);
    }
    @Override
    public void onClick(View view) {
        dismiss();
        if (listener == null)
            return;
        saveSelectedVocaFilter();
        switch (view.getId()) {
            case R.id.tvCancel:
                break;
            case R.id.tvApply:
                VocaKnowGroupSelect vocaKnowGroupSelect = new VocaKnowGroupSelect(binding.scVocaNotRated.isChecked(), binding.scVoca1st.isChecked(), binding.scVoca2nd.isChecked(), binding.scVocaUnknown.isChecked(), binding.scVocaKnown.isChecked(), false, binding.scVocaBookmarked.isChecked());
                listener.onClick(view, vocaKnowGroupSelect);
                break;
        }
    }
////    @OnClick({R.id.tvOpenBookmarkedItemsOnly, R.id.tvOpenSelectedItemsOnly, R.id.tvOpenAllItems})
//    void onClick1(View view) {
//        dismiss();
//        if (listener == null)
//            return;
//
//        VocaKnowGroupSelect vocaKnowGroupSelect = new VocaKnowGroupSelect();
//        switch (view.getId()) {
//            case R.id.tvOpenSelectedItemsOnly:
//                vocaKnowGroupSelect = new VocaKnowGroupSelect(binding.scVocaNotRated.isChecked(), binding.scVoca1st.isChecked(), binding.scVoca2nd.isChecked(), binding.scVocaUnknown.isChecked(), binding.scVocaKnown.isChecked(), false, binding.scVocaBookmarked.isChecked());
//                break;
//            case R.id.tvOpenBookmarkedItemsOnly:
//                vocaKnowGroupSelect.setValueToOpenBookmarkedItemsOnly();
//            case R.id.tvOpenAllItems:
//                vocaKnowGroupSelect.setValueToOpenAllItems();
//                break;
//        }
//        listener.onClick(view, vocaKnowGroupSelect);
//    }
}
