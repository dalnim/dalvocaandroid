package com.dalread.dialog;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;

import com.dalread.R;
import com.dalread.base.BaseDialog;
import com.dalread.database.SharedPreferencesDB;
import com.dalread.databinding.MenuDialogConvCommonBinding;
import com.dalread.model.WordListType;
import com.dalread.util.AppFlavorUtil;
import com.dalread.util.UserUtil;

public class ConvCommonMenuDialog extends BaseDialog implements View.OnClickListener {

    protected final com.dalread.listener.OnClickListener listener;
    protected MenuDialogConvCommonBinding binding;
    protected Context context;
    protected boolean show4Buttons;
    protected SharedPreferencesDB sharedPreferences;
    protected WordListType wordListType;

    private View getContentView() {
        binding = MenuDialogConvCommonBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        return view;
    }

    public ConvCommonMenuDialog(@NonNull Context context, com.dalread.listener.OnClickListener listener, WordListType wordListType) {
        super(context, R.style.TransparentDialog);
        this.listener = listener;
        this.context = context;
        this.wordListType = wordListType;

        setContentView(getContentView());
        setOnClickListeners();
        sharedPreferences = SharedPreferencesDB.getInstance(context);
        update4ButtonName(show4Buttons);
        showOrHideMenus();
    }
    protected void showOrHideMenus() {
        binding.llClearSearchHistory.setVisibility(wordListType.equals(WordListType.SEARCH_HISTORY) ? View.VISIBLE : View.GONE);
        if (!UserUtil.isDebugOrAdminUser(context)) {
            binding.llShow4Buttons.setVisibility(View.GONE);
            binding.llShowSearchDictonaryOption.setVisibility(View.GONE);
            binding.llChatGpt.setVisibility(View.GONE);
            binding.llBackToHome.setVisibility(View.GONE);
        }
        if (!AppFlavorUtil.isAraConvEnglishApp()) {
            if (UserUtil.isDebugOrAdminUser(context)) {
                binding.llWordList.setVisibility(View.VISIBLE);
                binding.llMakeRolePlaying.setVisibility(View.VISIBLE);
            } else {
                binding.llWordList.setVisibility(View.GONE);
                binding.llMakeRolePlaying.setVisibility(View.GONE);
            }
        }
    }
    public void setShow4Buttons(boolean show4Buttons) {
        this.show4Buttons = show4Buttons;
        update4ButtonName(show4Buttons);
    }

    private void update4ButtonName(boolean show4Buttons) {
        if (show4Buttons) {
            binding.tvShow4Buttons.setText(R.string.dialog_menu_hide_4_buttons);
        } else {
            binding.tvShow4Buttons.setText(R.string.dialog_menu_show_4_buttons);
        }
    }

    private void setOnClickListeners() {
        binding.llShow4Buttons.setOnClickListener(this);
        binding.llShowSearchDictonaryOption.setOnClickListener(this);
        binding.llShowPlayAllWords.setOnClickListener(this);
        binding.llWordList.setOnClickListener(this);
        binding.llOpenSetting.setOnClickListener(this);
        binding.llClearSearchHistory.setOnClickListener(this);
        binding.llCopy.setOnClickListener(this);
        binding.llMakeRolePlaying.setOnClickListener(this);
        binding.llChatGpt.setOnClickListener(this);
        binding.llBackToHome.setOnClickListener(this);
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
