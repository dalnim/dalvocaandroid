package com.dalread.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.dalread.R;
import com.dalread.base.EnumLanguage;
import com.dalread.databinding.ItemChooseLanguageBinding;
import com.dalread.listener.OnClickListener;

import java.util.List;

import butterknife.ButterKnife;

public class ChooseLanguageAdapter extends RecyclerView.Adapter {

    private List<EnumLanguage> languages;
    private int checkedItem;
    private OnClickListener listener;

    public ChooseLanguageAdapter(OnClickListener listener, List<EnumLanguage> languages) {
        this.listener = listener;
        this.languages = languages;
        checkedItem = -1;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new LanguageHolder(ItemChooseLanguageBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof LanguageHolder) {
            ((LanguageHolder) holder).bind(languages.get(position), checkedItem == position);
        }
    }

    @Override
    public int getItemCount() {
        return languages == null ? 0 : languages.size();
    }

    public int getCheckedItem() {
        return checkedItem;
    }

    public class LanguageHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private boolean isChecked;

        private ItemChooseLanguageBinding binding;
        LanguageHolder(ItemChooseLanguageBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            setOnClickListeners();
        }

        private void setOnClickListeners() {
            binding.vContainer.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int viewId = view.getId();
            switch (viewId) {
                case R.id.v_container:
                    isChecked = !isChecked;
                    binding.icCheck.setVisibility(isChecked ? View.VISIBLE : View.INVISIBLE);
                    notifyItemChanged(checkedItem);
                    checkedItem = isChecked ? getBindingAdapterPosition() : -1;
                    if (listener != null) {
                        listener.onClick(view, checkedItem);
                    }
                    break;
                default:
                    break;
            }
        }

        public LanguageHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }

        public void bind(EnumLanguage language, boolean isChecked) {
            this.isChecked = isChecked;
            binding.tvLang.setText(language.getFormatApi());
            binding.tvDescription.setText(language.getFormatUser());
            binding.icCheck.setVisibility(isChecked ? View.VISIBLE : View.INVISIBLE);
        }
    }
}
