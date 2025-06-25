package com.dalread.adapter;

import android.content.Context;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dalread.R;
import com.dalread.database.sqlite.SubDatabase;
import com.dalread.listener.OnClickListener;
import com.dalread.listener.OnDoubleClickListener;
import com.dalread.model.PlayerFileModel;

import java.util.ArrayList;

public class VocaListPlayerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    protected final int VIEW_TYPE_HEADER = R.layout.item_word_list_player_header;
    protected final int VIEW_TYPE_ITEM = R.layout.item_word_list_player_item;
    protected final int VIEW_TYPE_ITEM_FULL = R.layout.item_word_list_player_item_full;
    protected final int VIEW_TYPE_LOAD_MORE = R.layout.item_load_more;
    protected final int VIEW_TYPE_DIALOG_ITEM = R.layout.item_dialog_list_player_item;

    protected PlayerFileModel playerFileModel;
    protected ArrayList<Object> data;
    protected Context context;
    protected OnClickListener listener;
    protected OnDoubleClickListener onDoubleClickListener;
    protected boolean displayPronunciation;
    protected SubDatabase subDatabase;
    protected String keyword;

    public VocaListPlayerAdapter(Context context, SubDatabase subDatabase, boolean displayPronunciation, OnClickListener listener, OnDoubleClickListener onDoubleClickListener) {
        this.context = context;
        this.subDatabase = subDatabase;
        this.displayPronunciation = displayPronunciation;
        data = new ArrayList<>();
        this.listener = listener;
        this.onDoubleClickListener = onDoubleClickListener;
    }

    public VocaListPlayerAdapter(Context context, PlayerFileModel playerFileModel, SubDatabase subDatabase, boolean displayPronunciation, OnClickListener listener, OnDoubleClickListener onDoubleClickListener) {
        this.playerFileModel = playerFileModel;
        this.context = context;
        this.subDatabase = subDatabase;
        this.displayPronunciation = displayPronunciation;
        data = new ArrayList<>();
        this.listener = listener;
        this.onDoubleClickListener = onDoubleClickListener;
    }
    @Override
    public int getItemViewType(int position) {
        return VIEW_TYPE_LOAD_MORE;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return data.size();
    }


    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public void setData(ArrayList<Object> data) {
        this.data.clear();
        this.data.addAll(data);
        notifyDataSetChanged();
    }
}
