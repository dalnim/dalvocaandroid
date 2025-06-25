package com.dalread.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dalread.R;
import com.dalread.database.sqlite.model.DicModel;
import com.dalread.databinding.ItemSubtitleGroupBinding;
import com.dalread.listener.DoubleClick;
import com.dalread.listener.OnDoubleClickListener;
import com.dalread.util.StringUtils;
import com.dalread.util.UserUtil;
import com.dalread.util.VocaKnow;

import java.util.List;

public class SubtitleGroupAdapter extends RecyclerView.Adapter<SubtitleGroupAdapter.SubtitleViewHolder> {
    private String keyword = "";
    private List<DicModel> subtitles;
    private Context context;
    private static OnDoubleClickListener onDoubleClickListenerOnBaseVocaKnow;
    private ItemCheckedListener listener;

    public SubtitleGroupAdapter(Context context, OnDoubleClickListener onDoubleClickListenerOnBaseVocaKnowLocal, ItemCheckedListener listener) {
        this.context = context;
        onDoubleClickListenerOnBaseVocaKnow = onDoubleClickListenerOnBaseVocaKnowLocal;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SubtitleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemSubtitleGroupBinding binding = ItemSubtitleGroupBinding.inflate(inflater, parent, false);
        return new SubtitleViewHolder(binding);
    }


    @Override
    public void onBindViewHolder(@NonNull SubtitleViewHolder holder, int position) {
        DicModel subtitle = subtitles.get(position);
        holder.bind(context, position, subtitle, keyword, listener);
    }


    @Override
    public int getItemCount() {
        return subtitles.size();
    }

    public void setSubtitles(List<DicModel> subtitles, String keyword) {
        this.subtitles = subtitles;
        this.keyword = keyword;
    }

    static class SubtitleViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private Context context;
        private ItemSubtitleGroupBinding binding;
        private DicModel subtitle;

        public SubtitleViewHolder(@NonNull ItemSubtitleGroupBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
        private void setOnClickListeners() {
            binding.ivBookmark.setOnClickListener(this);
            binding.ivKnow.setOnClickListener(new DoubleClick(onDoubleClickListenerOnBaseVocaKnow, subtitle));
//            binding.ivKnow.setOnClickListener(this);
        }
        public void bind(Context context, int position, DicModel subtitle, String keyword, ItemCheckedListener listener) {
            this.context = context;
            this.subtitle = subtitle;
            setOnClickListeners();
            binding.tvIndex.setText(String.valueOf(position + 1));//String.valueOf(subtitle.getIndex()));
            if (TextUtils.isEmpty(keyword)) {
                binding.subtitleText.setText(subtitle.getTextOfVocaDiaplayMeaning());
            } else {
                binding.subtitleText.setText(StringUtils.getSpanDefaultColorOnKeyword(subtitle.getTextOfVocaDiaplayMeaning(), keyword));
            }
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            });

            binding.ivCheck.setOnCheckedChangeListener(null); // Remove the previous listener
            binding.ivCheck.setChecked(subtitle.isVIChecked());

            binding.ivCheck.setOnCheckedChangeListener((buttonView, isChecked) -> {
                subtitle.setVIChecked(isChecked);
                listener.onItemChecked(isChecked);
            });

            displayMicButton();
            VocaKnow.updateIconVocaKnow(binding.ivKnow, subtitle.getVocaKnow());
            VocaKnow.updateIconVocaKnowPronounce(context, binding.ivVocaKnowPronounce, subtitle );
            VocaKnow.updateIconVocaBookmark(binding.ivBookmark, subtitle.isVIBookmark(), true);
        }
        private void displayMicButton() {
//            binding.ivMic.setVisibility(UserUtil.isDebugOrAdminUser(context) ? View.VISIBLE : View.GONE);
            if (subtitle.hasVIVoiceFile()) {// TextUtils.isEmpty(item.getRecordedPath())) {
                binding.ivMic.setVisibility(View.VISIBLE);
            } else {
                binding.ivMic.setVisibility(View.GONE);

            }
        }
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.ivBookmark:
//                case R.id.ivKnow:
                    if (UserUtil.isLoggedIn(context, true)) {
                        if (onDoubleClickListenerOnBaseVocaKnow != null) {
                            onDoubleClickListenerOnBaseVocaKnow.onClick(view, subtitle);
                        }
                    }
                    break;

            }
        }
    }

    public interface ItemCheckedListener {
        void onItemChecked(boolean isChecked);
    }
}

