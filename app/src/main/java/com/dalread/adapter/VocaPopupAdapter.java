package com.dalread.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.dalread.databinding.ItemVocaKnow4buttonsBinding;
import com.dalread.interfaces.IVocaFullPlayTTSItem;
import com.dalread.listener.OnDoubleClickListener;


import java.util.List;

public class VocaPopupAdapter extends RecyclerView.Adapter<VocaKnow4ButtonsHolder> {
    private List<IVocaFullPlayTTSItem> vocaList;
    private OnDoubleClickListener onDoubleClickListenerOnBaseVocaKnow;
    private Context context;
    private boolean show4Buttons = true;

    public VocaPopupAdapter(Context context, List<IVocaFullPlayTTSItem> vocaList, OnDoubleClickListener onDoubleClickListenerOnBaseVocaKnow, boolean show4Buttons) {
        this.context = context;
        this.vocaList = vocaList;
        this.show4Buttons = show4Buttons;
        this.onDoubleClickListenerOnBaseVocaKnow = onDoubleClickListenerOnBaseVocaKnow;

    }

    @NonNull
    @Override
    public VocaKnow4ButtonsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemVocaKnow4buttonsBinding binding = ItemVocaKnow4buttonsBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new VocaKnow4ButtonsHolder(parent.getContext(), binding, onDoubleClickListenerOnBaseVocaKnow);

    }

    @Override
    public void onBindViewHolder(@NonNull VocaKnow4ButtonsHolder holder, int position) {
        holder.bind((IVocaFullPlayTTSItem) vocaList.get(position), position);
        holder.setShow4Buttons(show4Buttons);
    }


    @Override
    public int getItemCount() {
        return vocaList.size();
    }

    public void setVocaList(List<IVocaFullPlayTTSItem> vocaList) {
        this.vocaList = vocaList;
    }
}
