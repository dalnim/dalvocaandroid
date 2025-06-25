package com.dalread.adapter;

import android.annotation.SuppressLint;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.view.MotionEventCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.dalread.R;
import com.dalread.databinding.HeaderPreferredNativeSpeakerBinding;
import com.dalread.databinding.ItemPreferredNativeSpeakerBinding;
import com.dalread.helper.itemtouchhelper.ItemTouchHelperAdapter;
import com.dalread.helper.itemtouchhelper.ItemTouchHelperViewHolder;
import com.dalread.listener.OnClickListener;
import com.dalread.listener.OnDragListener;
import com.dalread.model.NativeSpeaker;
import com.dalread.util.BaseVoca;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;

public class PreferredNativeSpeakersAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements ItemTouchHelperAdapter {

    private static final int TYPE_HEADER_PREFERRED = 0;
    private static final int TYPE_ITEM_PREFERRED = TYPE_HEADER_PREFERRED + 1;
    private static final int TYPE_HEADER_ALL = TYPE_ITEM_PREFERRED + 1;
    private static final int TYPE_ITEM_ALL = TYPE_HEADER_ALL + 1;

    private final ArrayList<NativeSpeaker> allSpeakers;
    private final ArrayList<NativeSpeaker> preferredSpeakers;
    private OnDragListener onDragListener;
    private OnClickListener onClickListener;

    public PreferredNativeSpeakersAdapter(ArrayList<NativeSpeaker> allSpeakers, ArrayList<NativeSpeaker> preferredSpeakers) {
        this.allSpeakers = allSpeakers;
        this.preferredSpeakers = preferredSpeakers;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0)
            return TYPE_HEADER_PREFERRED;
        if (position <= preferredSpeakers.size())
            return TYPE_ITEM_PREFERRED;
        if (position == preferredSpeakers.size() + 1)
            return TYPE_HEADER_ALL;
        return TYPE_ITEM_ALL;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER_PREFERRED || viewType == TYPE_HEADER_ALL)
            return new HeaderHolder(HeaderPreferredNativeSpeakerBinding.inflate(LayoutInflater.from(parent.getContext())));
        if (viewType == TYPE_ITEM_PREFERRED)
            return new PreferredHolder(ItemPreferredNativeSpeakerBinding.inflate(LayoutInflater.from(parent.getContext())));
        return new AllHolder(ItemPreferredNativeSpeakerBinding.inflate(LayoutInflater.from(parent.getContext())));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof HeaderHolder) {
            ((HeaderHolder) holder).bind(getItemViewType(position) == TYPE_HEADER_PREFERRED);
        } else if (holder instanceof PreferredHolder) {
            ((PreferredHolder) holder).bind(preferredSpeakers.get(position - 1), onClickListener, onDragListener);
        } else if (holder instanceof AllHolder) {
            ((AllHolder) holder).bind(allSpeakers.get(position - preferredSpeakers.size() - 2), onClickListener);
        }
    }

    @Override
    public int getItemCount() {
        return allSpeakers.size() + preferredSpeakers.size() + 2;
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        int viewType = getItemViewType(toPosition);
        if (viewType == TYPE_ITEM_PREFERRED) {
            Collections.swap(preferredSpeakers, fromPosition - 1, toPosition - 1);
            notifyItemMoved(fromPosition, toPosition);
        }
    }

    public void setDragListener(OnDragListener onDragListener) {
        this.onDragListener = onDragListener;
    }

    public void setClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public boolean isSwipeable(int position) {
        return getItemViewType(position) == TYPE_ITEM_PREFERRED;
    }

    public NativeSpeaker getNativeSpeaker(int pos) {
        int viewType = getItemViewType(pos);
        if (viewType == TYPE_ITEM_PREFERRED)
            return preferredSpeakers.get(pos - 1);
        return null;
    }

    public static class HeaderHolder extends RecyclerView.ViewHolder {

        private final HeaderPreferredNativeSpeakerBinding binding;

        public HeaderHolder(@NonNull HeaderPreferredNativeSpeakerBinding binding) {
            super(binding.getRoot());

            this.binding = binding;
        }

        public void bind(boolean isPreferred) {
            binding.tvType.setText(isPreferred ? R.string.preferred_native_speakers_max : R.string.all_native_speakers);
        }
    }

    public static class BaseItemHolder extends RecyclerView.ViewHolder {

        protected ItemPreferredNativeSpeakerBinding binding;
        protected NativeSpeaker speaker;
        protected OnClickListener onClickListener;

        protected final String tplCountRecord;
        private final DecimalFormat df = new DecimalFormat("#,###");

        public BaseItemHolder(ItemPreferredNativeSpeakerBinding binding) {
            super(binding.getRoot());

            this.binding = binding;

            tplCountRecord = binding.getRoot().getContext().getString(R.string.tpl_count_record);

            View.OnClickListener listener = view -> {
                if (onClickListener != null) {
                    onClickListener.onClick(view, speaker);
                }
            };
            binding.icAvatar.setOnClickListener(listener);
            binding.llUsernameDesc.setOnClickListener(listener);
        }

        public void innerBind(NativeSpeaker speaker, OnClickListener onClickListener) {
            this.speaker = speaker;
            this.onClickListener = onClickListener;

            binding.icAvatar.setImageResource(BaseVoca.getAvatarResource(speaker.getSex(), speaker.getAge()));
            //
            String text = speaker.getName();
            if (!TextUtils.isEmpty(speaker.getOponentNameByMe())) {
                text = "(" + speaker.getOponentNameByMe() + ") " + text;
            }
            binding.tvPrimary.setText(text);
            //
            text = speaker.getNation();
            if (speaker.getCountRecord() > 0) {
                String countRecord = String.format(tplCountRecord, df.format(speaker.getCountRecord()));
                if (TextUtils.isEmpty(text)) {
                    text = countRecord;
                } else {
                    text += " " + countRecord;
                }
            }
            binding.tvSecondary.setText(text);
        }
    }

    public static class PreferredHolder extends BaseItemHolder implements ItemTouchHelperViewHolder {

        private OnDragListener onDragListener;

        @SuppressLint("ClickableViewAccessibility")
        public PreferredHolder(ItemPreferredNativeSpeakerBinding binding) {
            super(binding);

            binding.icCheck.setVisibility(View.GONE);
            binding.icDrag.setOnTouchListener((View view, MotionEvent motionEvent) -> {
                if (onDragListener != null) {
                    if (MotionEventCompat.getActionMasked(motionEvent) == MotionEvent.ACTION_DOWN) {
                        onDragListener.onDragStarted(PreferredHolder.this);
                    }
                }
                return false;
            });
        }

        public void bind(NativeSpeaker speaker, OnClickListener onClickListener, OnDragListener onDragListener) {
            this.onDragListener = onDragListener;

            innerBind(speaker, onClickListener);
        }

        @Override
        public void onItemSelected() {
        }

        @Override
        public void onItemClear() {
            if (onDragListener != null) {
                onDragListener.onDragStopped();
            }
        }
    }

    public static class AllHolder extends BaseItemHolder {

        public AllHolder(ItemPreferredNativeSpeakerBinding binding) {
            super(binding);

            binding.icDrag.setVisibility(View.GONE);
            binding.vContainer.setOnClickListener(view -> {
                if (onClickListener != null) {
                    onClickListener.onClick(view, speaker);
                }
            });
        }

        public void bind(NativeSpeaker speaker, OnClickListener onClickListener) {
            innerBind(speaker, onClickListener);
            binding.icCheck.setVisibility(speaker.isPreferred() ? View.VISIBLE : View.INVISIBLE);
        }
    }
}
