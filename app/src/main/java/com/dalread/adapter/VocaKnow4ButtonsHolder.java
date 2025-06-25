package com.dalread.adapter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.dalread.R;
import com.dalread.databinding.ItemVocaKnow4buttonsBinding;
import com.dalread.interfaces.IVocaFullPlayTTSItem;
import com.dalread.listener.DoubleClick;
import com.dalread.listener.OnDoubleClickListener;
import com.dalread.util.AppFlavorUtil;
import com.dalread.util.CopyTextUtil;
import com.dalread.util.TranslateUtil;
import com.dalread.util.UserUtil;
import com.dalread.util.Utils;
import com.dalread.util.Voca;
import com.dalread.util.VocaKnow;

public class VocaKnow4ButtonsHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private IVocaFullPlayTTSItem voca;
    private OnDoubleClickListener onDoubleClickListenerOnBaseVocaKnow;
    private Context context;
    private boolean show4Buttons = true;
    private ItemVocaKnow4buttonsBinding binding;
    public VocaKnow4ButtonsHolder(Context context, ItemVocaKnow4buttonsBinding binding, OnDoubleClickListener onDoubleClickListenerOnBaseVocaKnow) {
        super(binding.getRoot());
        this.context = context;
        this.binding = binding;
        this.onDoubleClickListenerOnBaseVocaKnow = onDoubleClickListenerOnBaseVocaKnow;
    }

    private void setOnClickListeners() {
        binding.vItem.setOnClickListener(this);
        binding.ivBookmark.setOnClickListener(this);
        binding.ivKnown.setOnClickListener(this);
        binding.ivGrade1.setOnClickListener(this);
        binding.ivGrade2.setOnClickListener(this);
        binding.ivUnknown.setOnClickListener(this);
        binding.ivCopy.setOnClickListener(this);
        binding.ivTranslate.setOnClickListener(this);
        binding.llKnowValue.setOnClickListener(new DoubleClick(onDoubleClickListenerOnBaseVocaKnow, voca));
        binding.ivKnow.setOnClickListener(new DoubleClick(onDoubleClickListenerOnBaseVocaKnow, voca));
    }

    public void bind(IVocaFullPlayTTSItem voca, int position) {
        this.voca = voca;
        setOnClickListeners();
        binding.tvIndex.setText((voca.getVIIndex().toString() + " " + voca.getPersonAB()).trim());
        VocaKnow.updateIconVocaBookmark(binding.ivBookmark, voca, true);
        VocaKnow.updateIconVocaKnowGrayCircle(binding.ivKnow, voca);
        setTextViewValue(binding.tvVoca, voca.getVIVoca());
        displayPronounce();
        displayMeaning(); //binding.tvMeaning.setText(voca.getVIMeaning(LanguageUtil.getMotherTongueLanguage(context)));
        displayMeaningDetailed(voca);
        displayMeaningEnglish(voca);
        display4Buttons();
        displayBookmark();
        displayVocaKnow();
    }
    private void setTextViewValue(TextView textView, String string) {
        if (Utils.hasValue(string)) {
            textView.setText(string);
        }
    }

    private void displayMeaningDetailed(IVocaFullPlayTTSItem voca) {
        String meaningDetailed = Voca.getMeaningDetailedOrEnglishMeaningDetailed(context, voca);
        binding.tvMeaningDetailed.setVisibility(Utils.isEmpty(meaningDetailed) ? View.GONE : View.VISIBLE);
        binding.tvMeaningDetailed.setText(meaningDetailed);
    }

    public void setShow4Buttons(boolean show4Buttons) {
        this.show4Buttons = show4Buttons;
        display4Buttons();
    }

    private void display4Buttons() {
        if (show4Buttons) {
            binding.ll4Buttons.setVisibility(View.VISIBLE);
        } else {
            binding.ll4Buttons.setVisibility(View.GONE);
        }
    }
    private void displayBookmark() {
        binding.ivBookmark.setVisibility(View.VISIBLE);
    }
    private void displayVocaKnow() {
        if (AppFlavorUtil.isAraHangulApp()) {
            binding.llKnowValue.setVisibility(View.GONE);
        } else {
            binding.llKnowValue.setVisibility(View.VISIBLE);
        }
    }
    private void displayPronounce() {
        Voca.displayShortOrLongPronounce(context, voca, binding.tvShortPronounce, binding.tvLongPronounce);
    }

    private void displayMeaning() {
        setTextViewValue(binding.tvMeaning, Voca.getMeaningOrEnglishMeaning(context, voca));
    }
    private void displayMeaningEnglish(IVocaFullPlayTTSItem voca) {
        if (Utils.needToDisplayMeaningEnglish(context) && !Utils.isEmpty(voca.getVIMeaningEng())) {
            binding.tvMeaningEnglish.setVisibility(View.VISIBLE);
            binding.tvMeaningEnglish.setText(Voca.wrapMeaningEnglish(voca));
        } else {
            binding.tvMeaningEnglish.setVisibility(View.GONE);
        }
        if (Utils.needToDisplayMeaningEnglish(context) && !Utils.isEmpty(voca.getVIMeaningEngDetailed())) {
            binding.tvMeaningEnglishDetailed.setVisibility(View.VISIBLE);
            binding.tvMeaningEnglishDetailed.setText(Voca.wrapMeaningEnglishDetailed(voca));
        } else {
            binding.tvMeaningEnglishDetailed.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ivBookmark:
                if (UserUtil.isLoggedIn(context, true)) {
                    if (onDoubleClickListenerOnBaseVocaKnow != null) {
                        onDoubleClickListenerOnBaseVocaKnow.onClick(view, voca);
                    }
                }
                break;
            case R.id.iv_known:
            case R.id.iv_grade_1:
            case R.id.iv_grade_2:
            case R.id.iv_unknown:
                if (UserUtil.isLoggedIn(context, true)) {
                    if (onDoubleClickListenerOnBaseVocaKnow != null) {
                        onDoubleClickListenerOnBaseVocaKnow.onClick(view, voca);
                    }
                    binding.ll4Buttons.setVisibility(View.GONE);
                }
                break;
            case R.id.ivTranslate:
                TranslateUtil.openWebTranslate(context, voca.getVIVoca(), () -> {});
//                OpenViewUtil.openExternalWebDictionary(context, voca);
                break;
            case R.id.ivCopy:
                CopyTextUtil.openCopyVoca(context, voca);
                break;
        }
    }
}
