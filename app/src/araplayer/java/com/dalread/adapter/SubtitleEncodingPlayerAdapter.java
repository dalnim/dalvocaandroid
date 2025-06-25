package com.dalread.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dalread.R;
import com.dalread.databinding.ItemPlayerSubtitleEncodingBinding;
import com.dalread.databinding.ItemPlayerSubtitleEncodingHeaderBinding;
import com.dalread.listener.OnClickListener;
import com.dalread.model.SubtitleEncodingModel;

import java.util.ArrayList;
import java.util.List;

public class SubtitleEncodingPlayerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int VIEW_TYPE_HEADER = R.layout.item_player_subtitle_encoding_header;
    private final int VIEW_TYPE_VALUE = R.layout.item_player_subtitle_encoding;
    private List<SubtitleEncodingModel> mListItems = new ArrayList<>();
    private OnClickListener listener;
    private int lastCheckedPosition = -1;

    public SubtitleEncodingPlayerAdapter(OnClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
//        View view = inflater.inflate(viewType, parent, false);
        if (viewType == VIEW_TYPE_HEADER) {
            return new HeaderViewHolder(ItemPlayerSubtitleEncodingHeaderBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
//            return new HeaderViewHolder(view);
        }
        return new ValueViewHolder(ItemPlayerSubtitleEncodingBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
//        return new ValueViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case VIEW_TYPE_HEADER:
                ((HeaderViewHolder) holder).bind(mListItems.get(position));
                break;
            case VIEW_TYPE_VALUE:
                ((ValueViewHolder) holder).bind(mListItems.get(position), position);
                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (mListItems.get(position).isHeader()) {
            return VIEW_TYPE_HEADER;
        }
        return VIEW_TYPE_VALUE;
    }

    @Override
    public int getItemCount() {
        return mListItems.size();
    }

    public void setData(List<SubtitleEncodingModel> data, int lastCheckedPosition) {
        this.mListItems = data;
        this.lastCheckedPosition = lastCheckedPosition;
        notifyDataSetChanged();
    }

    public class HeaderViewHolder extends RecyclerView.ViewHolder {
//        @BindView(R.id.tvName) TextView tvName;

        private ItemPlayerSubtitleEncodingHeaderBinding binding;
        HeaderViewHolder(ItemPlayerSubtitleEncodingHeaderBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }


//        public HeaderViewHolder(View itemView) {
//            super(itemView);
//            ButterKnife.bind(this, itemView);
//        }

        public void bind(SubtitleEncodingModel item) {
            binding.tvName.setText(item.getName());
        }
    }

    public class ValueViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
//        @BindView(R.id.tvName) TextView tvName;
//        @BindView(R.id.ivCheck) ImageView ivCheck;

        private ItemPlayerSubtitleEncodingBinding binding;
        ValueViewHolder(ItemPlayerSubtitleEncodingBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            setOnClickListeners();
        }

        private void setOnClickListeners() {
            binding.llItem.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            lastCheckedPosition = getAdapterPosition();
            if (listener != null) {
                listener.onClick(view, lastCheckedPosition);
            }
            notifyDataSetChanged();
        }

//        public ValueViewHolder(View itemView) {
//            super(itemView);
//            ButterKnife.bind(this, itemView);
//        }

//        @OnClick(R.id.llItem)
//        void onClick(View v) {
//            lastCheckedPosition = getAdapterPosition();
//            if (listener != null) {
//                listener.onClick(v, lastCheckedPosition);
//            }
//            notifyDataSetChanged();
//        }

        public void bind(SubtitleEncodingModel item, int position) {
            binding.tvName.setText(item.getName());
            binding.ivCheck.setVisibility(lastCheckedPosition == position ? View.VISIBLE : View.INVISIBLE);
        }
    }
}