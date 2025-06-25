package com.dalread.dialog;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;

import com.dalread.R;
import com.dalread.base.BaseDialog;
import com.dalread.database.SharedPreferencesDB;
import com.dalread.databinding.MenuDialogHanjaBookMenuBinding;
import com.dalread.util.UserUtil;

public class HanjaBookMenuDialog extends BaseDialog implements View.OnClickListener {
    private com.dalread.listener.OnClickListener listener;
    private MenuDialogHanjaBookMenuBinding binding;
    private SharedPreferencesDB sharedPreferences;
    private Context context;
    private View getContentView() {
        binding = MenuDialogHanjaBookMenuBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        return view;
    }

    public HanjaBookMenuDialog(@NonNull Context context, com.dalread.listener.OnClickListener listener) {
        super(context);
        this.context = context;
        this.listener = listener;
        setContentView(getContentView());
        setOnClickListeners();
        sharedPreferences = SharedPreferencesDB.getInstance(context);
        udpateBookStyleTitle();
        hideMenusOnReleaseMode();
    }

    private void hideMenusOnReleaseMode() {
        binding.llWordList.setVisibility(View.GONE);
        if (UserUtil.isDebugOrAdminUser(context)) {
            binding.llCopyBookAllContents.setVisibility(View.VISIBLE);
        } else {
            binding.llCopyBookAllContents.setVisibility(View.GONE);
        }
    }

    private void switchBookStyle() {
        if (sharedPreferences.getBookStyle() == BookStyle.PAGE.getValue()) {
            sharedPreferences.setBookStyle(BookStyle.TABLE.getValue());
        } else {
            sharedPreferences.setBookStyle(BookStyle.PAGE.getValue());
        }
        udpateBookStyleTitle();
    }

    private void udpateBookStyleTitle() {
        if (sharedPreferences.getBookStyle() == BookStyle.PAGE.getValue()) {
            binding.tvBookStyle.setText(R.string.dialog_menu_book_style_table_view);
        } else {
            binding.tvBookStyle.setText(R.string.dialog_menu_book_style_page);
        }
    }

    private void setOnClickListeners() {
        binding.llWordList.setOnClickListener(this);
        binding.llHome.setOnClickListener(this);
        binding.tvCancel.setOnClickListener(this);
        binding.llRefreshVocaFromServer.setOnClickListener(this);
        binding.llCopyBookAllContents.setOnClickListener(this);
        binding.llOpenNormalTextViewForHurigana.setOnClickListener(this);
        binding.llBookStyle.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        dismiss();
        if (view.getId() == R.id.llBookStyle) {
            switchBookStyle();
        }
        if (listener != null) {
            listener.onClick(view, null);
        }
    }
}

