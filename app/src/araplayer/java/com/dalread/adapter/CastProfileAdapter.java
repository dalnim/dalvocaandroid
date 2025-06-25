package com.dalread.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dalread.R;
import com.dalread.databinding.ItemCastProfileBinding;
import com.dalread.listener.OnClickListener;
import com.dalread.model.VideoInformationModel;
import com.dalread.util.UtilImage;

import java.util.List;

public class CastProfileAdapter extends RecyclerView.Adapter<CastProfileAdapter.ItemHolder> {

    private Context context;
    private List<VideoInformationModel.People> list;
    private OnClickListener listener;

    public CastProfileAdapter(Context context, List<VideoInformationModel.People> list, OnClickListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    @Override
    public CastProfileAdapter.ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ItemHolder(ItemCastProfileBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
//        return new ItemHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cast_profile, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ItemHolder holder, int position) {
        holder.bind(list.get(position));
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    public VideoInformationModel.People getItem(int position) {
        return list.get(position);
    }

    public void updateData(List<VideoInformationModel.People> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public class ItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
//        @BindView(R.id.izb_profile) ImageView izbProfile;
        private VideoInformationModel.People item;
        private ItemCastProfileBinding binding;
        ItemHolder(ItemCastProfileBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            setOnClickListeners();
        }

        private void setOnClickListeners() {
            binding.tvRealName.setOnClickListener(this);
            binding.tvCharacterName.setOnClickListener(this);
            binding.llCastName.setOnClickListener(this);
            binding.izbProfile.setOnClickListener(this);
        }

//        public ItemHolder(View itemView) {
//            super(itemView);
//            ButterKnife.bind(this, itemView);
//        }

        public void bind(VideoInformationModel.People item) {
            this.item = item;
            UtilImage.getThumbnail(context, binding.izbProfile, R.mipmap.ic_no_avatar, item.getImagePath(), null);
            binding.tvRealName.setText(item.getName());
            binding.tvCharacterName.setText(item.getCharacter());
        }

        @Override
        public void onClick(View view) {
            if (listener != null) {
                listener.onClick(view, item);
            }
        }

//        @OnClick({R.id.ll_item})
//        void onClick(View v) {
//            if (listener != null) {
//                listener.onClick(v, item);
//            }
//        }
    }
}
