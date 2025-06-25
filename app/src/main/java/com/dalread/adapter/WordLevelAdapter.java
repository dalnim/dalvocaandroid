package com.dalread.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.dalread.R;
import com.dalread.model.WordLevelModel;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by JetVHS on 3/1/2017.
 */
public class WordLevelAdapter extends RecyclerView.Adapter<WordLevelAdapter.MyViewHolder> {

    private Context mContext;
    private List<WordLevelModel> wordLevelModels;

    public WordLevelAdapter(Context mContext, List<WordLevelModel> wordLevelModels) {
        this.mContext = mContext;
        this.wordLevelModels = wordLevelModels;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_word_level, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final WordLevelModel obj = wordLevelModels.get(position);
        holder.tvTitle.setText(obj.getTitle());
        holder.tvValue.setText(String.valueOf(obj.getCount()));
    }

    @Override
    public int getItemCount() {
        return wordLevelModels.size();
    }

    public void updateData(List<WordLevelModel> wordLevelModels) {
        this.wordLevelModels = wordLevelModels;
        notifyDataSetChanged();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tvTitle) TextView tvTitle;
        @BindView(R.id.tvValue) TextView tvValue;

        public MyViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

}