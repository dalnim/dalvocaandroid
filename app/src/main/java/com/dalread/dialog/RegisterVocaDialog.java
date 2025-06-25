package com.dalread.dialog;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;

import com.dalread.R;
import com.dalread.base.BaseDialog;
import com.dalread.databinding.DialogRegisterVocaBinding;
import com.dalread.interfaces.IVocaBasicItem;
import com.dalread.listener.OnKnowChangeListener;
import com.dalread.util.BaseVocaKnow;
import com.dalread.util.Constant;
import com.dalread.util.UserUtil;

import butterknife.ButterKnife;
import butterknife.OnClick;

@SuppressLint("NonConstantResourceId")
public class RegisterVocaDialog extends BaseDialog {
    private final OnKnowChangeListener onKnowChangeListener;
    private IVocaBasicItem iVocaBasicItem;
    private Context context;
    private DialogRegisterVocaBinding binding;
    public RegisterVocaDialog(@NonNull Context context, OnKnowChangeListener onKnowChangeListener) {
        super(context, R.style.TransparentDialog);
        this.context = context;
        this.onKnowChangeListener = onKnowChangeListener;
        binding = DialogRegisterVocaBinding.inflate(LayoutInflater.from(context));
        setContentView(binding.getRoot());
        setDialogSizeWider(true);
        setCanceledOnTouchOutside(true);
        // To use @OnClick
        ButterKnife.bind(this);
    }

    public void show(IVocaBasicItem voca) {
        show(voca, true);
    }

    public void show(IVocaBasicItem iVocaBasicItem, boolean showAddToMyWordbook) {
        if (!UserUtil.isLoggedIn(context, true))
            return;
        this.iVocaBasicItem = iVocaBasicItem;
        swichKnownPronounceMenu(BaseVocaKnow.isUnknownPronounceWhenKnownWord(iVocaBasicItem));
        swichBookmarkMenu(iVocaBasicItem.isVIBookmark());
        setVisibilityAddMyWorkbookMenu(showAddToMyWordbook);
        hideSomeMenusOnReleaseMode();
        super.show();
    }

    private void hideSomeMenusOnReleaseMode() {
        setVisibilityKnownPronounceMenu(false); //아는 발음 아이콘을 일단 안보이게. newVocaKnow 를 사용하는 버그가 있다.
        setVisibilityBookmarkMenu(true);
        if (!UserUtil.isDebugOrAdminUser(context)) {
            setVisibilityAddMyWorkbookMenu(false);
        }
    }

    public void setVisibilityAddMyWorkbookMenu(boolean show) {
        binding.vAddMyWorkbook.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    public void setVisibilityBookmarkMenu(boolean show) {
        binding.vBookmark.setVisibility(show ? View.VISIBLE : View.GONE);
    }
    public void setVisibilityKnownPronounceMenu(boolean show) {
        if (show) {
            binding.vKnownPronounce.setVisibility(BaseVocaKnow.isKnown(iVocaBasicItem) ? View.VISIBLE : View.GONE);
        } else {
            binding.vKnownPronounce.setVisibility(View.GONE);
        }
    }

    private void swichKnownPronounceMenu(boolean isUnknownPronounceWhenKnownWord) {
        if (isUnknownPronounceWhenKnownWord) {
            binding.icKnownPronounce.setImageResource(R.drawable.ic_known_pronounce);
            binding.tvKnownPronounce.setText(R.string.known_pronunciation);
        } else {
            binding.icKnownPronounce.setImageResource(R.drawable.ic_unknown_pronounce);
            binding.tvKnownPronounce.setText(R.string.unknown_pronunciation);
        }
    }

    private void swichBookmarkMenu(boolean isAddBookmark) {
        if (isAddBookmark) {
            binding.ivBookmark.setImageResource(R.drawable.ic_favorite_new_off);
            binding.tvBookmark.setText(R.string.remove_bookmark);
        } else {
            binding.ivBookmark.setImageResource(R.drawable.ic_favorite_new_on);
            binding.tvBookmark.setText(R.string.add_bookmark);
        }
    }

    @OnClick({R.id.v_known, R.id.v_exclude, R.id.v_grade_1, R.id.v_grade_2,
            R.id.v_known_pronounce, R.id.v_add_my_workbook, R.id.v_bookmark,
            R.id.v_action})
    void onClick(View view) {
        int id = view.getId();
        //Don't dismiss Dialog for bookmark
        if (id != R.id.v_bookmark) {
            dismiss();
        }

        if (onKnowChangeListener == null) {
            dismiss();
            return;
        }

        switch (id) {
            case R.id.v_known:
                onKnowChangeListener.onVocaKnowChange(iVocaBasicItem, Constant.VOCA_KNOW.VOCA_KNOW_KNOWN);
                break;
            case R.id.v_exclude:
                onKnowChangeListener.onVocaKnowChange(iVocaBasicItem, Constant.VOCA_KNOW.VOCA_KNOW_UNKNOWN);
                break;
            case R.id.v_grade_1:
                onKnowChangeListener.onVocaKnowChange(iVocaBasicItem, Constant.VOCA_KNOW.VOCA_KNOW_AMKI_GRADE_1);
                break;
            case R.id.v_grade_2:
                onKnowChangeListener.onVocaKnowChange(iVocaBasicItem, Constant.VOCA_KNOW.VOCA_KNOW_AMKI_GRADE_2);
                break;
            case R.id.v_known_pronounce:
                swichKnownPronounceMenu(binding.tvKnownPronounce.getText().equals(getContext().getString(R.string.known_pronunciation)));
                onKnowChangeListener.onVocaKnowPronounceChange(iVocaBasicItem, iVocaBasicItem.getVIVocaKnowPronounce() == Constant.VOCA_KNOW.VOCA_KNOW_KNOWN ? Constant.VOCA_KNOW.VOCA_KNOW_UNKNOWN : Constant.VOCA_KNOW.VOCA_KNOW_KNOWN);
                break;
            case R.id.v_bookmark:
                swichBookmarkMenu(binding.tvBookmark.getText().equals(getContext().getString(R.string.add_bookmark)));
                onKnowChangeListener.onAddToBookmark(iVocaBasicItem);
                break;
            case R.id.v_add_my_workbook:
                onKnowChangeListener.onAddToWordbook(iVocaBasicItem);
                break;
            case R.id.v_action:
                onKnowChangeListener.onDismiss();
                break;
        }
    }
}
