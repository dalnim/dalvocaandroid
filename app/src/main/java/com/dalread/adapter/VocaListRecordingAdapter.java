package com.dalread.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.dalread.R;
import com.dalread.databinding.ItemVocaListRecordingBinding;
import com.dalread.interfaces.IVocaFullPlayTTSItem;
import com.dalread.listener.OnClickListener;
import com.dalread.model.VocaInBook;
import com.dalread.util.BaseVoca;
import com.dalread.util.LanguageUtil;

import java.util.ArrayList;
import java.util.List;

public class VocaListRecordingAdapter extends RecyclerView.Adapter {

    private static final int TYPE_ITEM = 0;
    private static final int TYPE_LOAD_MORE = TYPE_ITEM + 1;

    private Context context;
    private ArrayList<Object> data;
    private OnClickListener onClickListener;
    private boolean isRecording;
    private IVocaFullPlayTTSItem isRecordingVoca;
    public VocaListRecordingAdapter(Context context, OnClickListener onClickListener) {
        this.context = context;
        this.onClickListener = onClickListener;
        data = new ArrayList<>();
    }

    @Override
    public int getItemViewType(int position) {
        Object aData = data.get(position);
        if (aData instanceof VocaInBook)
            return TYPE_ITEM;
        return TYPE_LOAD_MORE;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM)
            return new ItemHolder(ItemVocaListRecordingBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
        return new LoadMoreHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_load_more, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ItemHolder) {
            ((ItemHolder) holder).bind((IVocaFullPlayTTSItem) data.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void setData(List<? extends IVocaFullPlayTTSItem> vocas) {
        data.clear();
        data.addAll(vocas);
    }

    public IVocaFullPlayTTSItem getItem(IVocaFullPlayTTSItem voca) {
        if (voca != null) {
            for (Object object : data) {
                if (object instanceof IVocaFullPlayTTSItem) {
                    IVocaFullPlayTTSItem item = (IVocaFullPlayTTSItem) object;
                    if (BaseVoca.isSameVoca(voca, item)) {
                        return item;
                    }
                }

            }
        }
        return null;
    }

    public IVocaFullPlayTTSItem getItem(int vocaId) {
        for (Object object : data) {
            if (object instanceof IVocaFullPlayTTSItem) {
                IVocaFullPlayTTSItem item = (IVocaFullPlayTTSItem) object;
                if (vocaId == item.getVIVocaId()) {
                    return item;
                }
            }
        }
        return null;
    }

    public void setLoadMore(boolean enable) {
        int pos = data.size();
        if (enable) {
            data.add(null);
            notifyItemInserted(pos);
        } else if (data.remove(null)) {
            notifyItemRemoved(pos);
        }
    }

    public void setIsRecordingVoca(IVocaFullPlayTTSItem voca) {
        this.isRecordingVoca = voca;
        notifyDataSetChanged();
    }

    public void notifyVocaChanged(IVocaFullPlayTTSItem voca) {
        if (voca != null)
            notifyItemChanged(data.indexOf(voca));
    }

    public class ItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private IVocaFullPlayTTSItem voca;
        private ItemVocaListRecordingBinding binding;
        ItemHolder(ItemVocaListRecordingBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            setOnClickListeners();
        }

        private void setOnClickListeners() {
            binding.ivSpeaker.setOnClickListener(this);
            binding.ivMic.setOnClickListener(this);
        }

        public void bind(IVocaFullPlayTTSItem voca) {
            this.voca = voca;

            String text = BaseVoca.getVocaDisplay(voca);
            if (TextUtils.isEmpty(text)) {
                text = voca.getVIVoca();
            }
            binding.tvVoca.setText(text);
            text = voca.getVIMeaning(LanguageUtil.getMotherTongueLanguage(binding.ivSpeaker.getContext()));
            binding.tvMeaning.setText(text);
            text = String.valueOf(voca.getVIIndex());
            binding.tvIndex.setText(text);
            BaseVoca.updateMicSpeakerVisibility(isRecordingVoca, voca, binding.ivSpeaker, binding.ivMic, true);
//            updateMicSpeakerVisibility();
        }

//        private void updateMicSpeakerVisibility() {
//            if (isRecordingVoca == null) {
//                binding.ivSpeaker.setVisibility(View.VISIBLE);
//                binding.ivMic.setVisibility(View.VISIBLE);
//                if (voca.hasVIVoiceFile()) {
//                    Voca.updateIconSpeaker(binding.ivSpeaker, voca);
//                } else {
//                    binding.ivSpeaker.setVisibility(View.INVISIBLE);
//                }
//                Voca.updateIconMic(binding.ivMic, voca);
//            } else {
//                binding.ivSpeaker.setVisibility(View.INVISIBLE);
//                if (Voca.isSameVoca(voca, isRecordingVoca)) {
//                    binding.ivMic.setVisibility(View.VISIBLE);
//                    Voca.updateIconMic(binding.ivMic, voca);
//                } else {
//                    binding.ivMic.setVisibility(View.INVISIBLE);
//                }
//            }
//        }

        @Override
        public void onClick(View view) {
            if (onClickListener == null)
                return;
            int viewId = view.getId();
            switch (viewId) {
                case R.id.ivSpeaker:
                case R.id.ivMic:
                    onClickListener.onClick(view, voca);
                    break;
                default:
                    break;
            }
        }
    }

    public static class LoadMoreHolder extends RecyclerView.ViewHolder {

        public LoadMoreHolder(View itemView) {
            super(itemView);
        }
    }
}
