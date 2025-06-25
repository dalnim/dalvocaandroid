package com.dalread.dialog;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;

import com.dalread.R;
import com.dalread.base.BaseDialog;
import com.dalread.database.SharedPreferencesDB;
import com.dalread.databinding.MenuDialogConvVocaBinding;
import com.dalread.model.WordListType;
import com.dalread.util.AppFlavorUtil;
import com.dalread.util.UserUtil;

public class ConvVocaMenuDialog extends BaseDialog implements View.OnClickListener {

    protected final com.dalread.listener.OnClickListener listener;
    protected MenuDialogConvVocaBinding binding;
    protected Context context;
    protected boolean show4Buttons;
    protected SharedPreferencesDB sharedPreferences;
    protected WordListType wordListType;
    protected boolean isShowWordList;
    private View getContentView() {
        binding = MenuDialogConvVocaBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        return view;
    }

    public ConvVocaMenuDialog(@NonNull Context context, com.dalread.listener.OnClickListener listener, WordListType wordListType, boolean isShowWordList) {
        super(context, R.style.TransparentDialog);
        this.listener = listener;
        this.context = context;
        this.wordListType = wordListType;
        this.isShowWordList = isShowWordList;

        setContentView(getContentView());
        setOnClickListeners();
        sharedPreferences = SharedPreferencesDB.getInstance(context);
        showOrHideMenus();
    }
    protected void showOrHideMenus() {
        if ((UserUtil.isEditContentFullOrAdminUser(context)) || (wordListType == WordListType.USER_VOCA_BOOK_LOCAL)) {
            binding.llEdit.setVisibility(View.VISIBLE);
        } else {
            binding.llEdit.setVisibility(UserUtil.canEditContentAfterAppUseCount(context) ? View.VISIBLE : View.GONE);
        }

        if (!AppFlavorUtil.isAraConvEnglishApp()) {
            binding.llWordList.setVisibility(View.GONE);
        } else {
            binding.llWordList.setVisibility(isShowWordList ? View.VISIBLE : View.GONE);
        }
    }

    private void setOnClickListeners() {
        binding.llWordList.setOnClickListener(this);
        binding.llCopy.setOnClickListener(this);
        binding.llTranslate.setOnClickListener(this);
        binding.llWebDictionary.setOnClickListener(this);
        binding.llEdit.setOnClickListener(this);
        binding.tvCancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        dismiss();
        if (listener != null) {
            listener.onClick(view, null);
        }
    }
}
