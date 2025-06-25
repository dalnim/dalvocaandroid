package com.dalread.adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dalread.database.sqlite.SubDatabase;
import com.dalread.database.sqlite.model.DicModel;
import com.dalread.databinding.ItemDialogListPlayerItemBinding;
import com.dalread.listener.OnClickListener;
import com.dalread.listener.OnDoubleClickListener;
import com.dalread.model.PlayerFileModel;
import com.dalread.util.Constant;
import com.dalread.util.TimeUtil;
import com.dalread.util.UtilImage;
import com.dalread.util.Utils;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DialogueListPlayerAdapter extends VocaListPlayerAdapter {
    public DialogueListPlayerAdapter(Context context, PlayerFileModel playerFileModel, SubDatabase subDatabase, boolean displayPronunciation, OnClickListener listener, OnDoubleClickListener onDoubleClickListener) {
        super(context, playerFileModel, subDatabase, displayPronunciation, listener, onDoubleClickListener);
    }

    @Override
    public int getItemViewType(int position) {
        Object object = data.get(position);

        DicModel data = (DicModel) object;
        return VIEW_TYPE_DIALOG_ITEM;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ItemRubyViewHolder(ItemDialogListPlayerItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final Object object = data.get(position);
        ((ItemRubyViewHolder) holder).bind((DicModel) object, position);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void setData(ArrayList<Object> data) {
        this.data.clear();
        this.data.addAll(data);
        notifyDataSetChanged();
    }

    public class LoadMoreViewHolder extends RecyclerView.ViewHolder {
        public LoadMoreViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    public class ItemRubyViewHolder extends RecyclerView.ViewHolder implements View.OnTouchListener, View.OnClickListener {
        public DicModel item;
        public int position;

        private ItemDialogListPlayerItemBinding binding;

        ItemRubyViewHolder(ItemDialogListPlayerItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            setOnClickListeners();
            resizeBackdropImageView();
        }

        private void setOnClickListeners() {
            binding.izbVideoThumbnail.setOnClickListener(this);
            binding.tvSubtitle.setOnClickListener(this);
            binding.tvSubtitleMeaning.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (listener == null)
                return;

            listener.onClick(view, item);
        }

        private void resizeBackdropImageView() {
            binding.izbVideoThumbnail.post(new Runnable() {
                @Override
                public void run() {
                    binding.izbVideoThumbnail.getLayoutParams().height = (int) (binding.izbVideoThumbnail.getWidth() * Constant.PLAYER.THUMBNAIL_HEIGHT_RATIO_BY_WIDTH);
                }
            });
        }

        public void bind(DicModel dic, int position) {
            this.item = dic;
//            binding.izbVideoThumbnail.setOnClickListener(new DoubleClick(onDoubleClickListener, item));
//            binding.tvSubtitle.setOnClickListener(new DoubleClick(onDoubleClickListener, item));
//            binding.tvSubtitleMeaning.setOnClickListener(new DoubleClick(onDoubleClickListener, item));

            this.position = position;
            binding.tvIndex.setText(String.valueOf(item.getIndex()) + "/" + data.size() + ")");

            String subtitle = item.getVocaDisplay();

            if (Utils.isEmpty(keyword)) {
                binding.tvSubtitle.setText(subtitle);
            } else {
                SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(subtitle);
                Pattern p = Pattern.compile(keyword, Pattern.CASE_INSENSITIVE);
                Matcher m = p.matcher(subtitle);
                while (m.find()){
                    spannableStringBuilder.setSpan(new ForegroundColorSpan(Color.RED), m.start(), m.end(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                }
                binding.tvSubtitle.setText(spannableStringBuilder);
            }

            binding.tvSubtitle.setVisibility(Utils.isEmpty(subtitle) ? View.GONE : View.VISIBLE);

            String meaning = item.getMeaning();
            binding.tvSubtitleMeaning.setText(meaning);
            binding.tvSubtitleMeaning.setVisibility(meaning == null || meaning.length() == 0 ? View.GONE : View.VISIBLE);

            binding.tvVideoStartTime.setText(TimeUtil.getVideoTimeDisplay(dic.getStartTime()));
            binding.tvVideoEndTime.setText(TimeUtil.getVideoTimeDisplay(dic.getEndTime()));

            long betweenStartAndEndTimeInVideo = (dic.getStartTime() + dic.getEndTime()) / 2;
            binding.izbVideoThumbnail.post(() -> UtilImage.getThumbnailVideoFile(context, binding.izbVideoThumbnail, playerFileModel.getPath(), betweenStartAndEndTimeInVideo, playerFileModel.getVideoModel().getDuration(), false, null));
        }

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                if (listener != null) {
                    listener.onClick(view, item);
                }
                return true;
            }
            return false;
        }
    }
}
