package com.dalread.adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.dalread.R;
import com.dalread.databinding.ItemPlayerFolderBinding;
import com.dalread.databinding.ItemSubtitleFileBinding;
import com.dalread.listener.OnClickListener;
import com.dalread.model.PlayerFileModel;
import com.dalread.util.StorageUtil;
import com.dalread.util.Utils;

import org.apache.commons.io.FilenameUtils;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SubtitleFilesPlayerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_FILE = 0;
    private static final int TYPE_FOLDER = 1;

    private Context context;
    private List<PlayerFileModel> playerFileModels;
    private OnClickListener listener;
    private int lastCheckPosition = RecyclerView.NO_POSITION;
    protected String keyword;

    public SubtitleFilesPlayerAdapter(Context context, List<PlayerFileModel> playerFileModels, OnClickListener listener) {
        this.context = context;
        this.playerFileModels = playerFileModels;
        this.listener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        if (playerFileModels.get(position).isDirectory())
            return TYPE_FOLDER;
        return TYPE_FILE;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        return new HeaderHolder(ItemHanjaSentenceInfoWordInfoBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));

        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case TYPE_FOLDER:
                viewHolder = new FolderViewHolder(ItemPlayerFolderBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
//                viewHolder = new FolderViewHolder(inflater.inflate(R.layout.item_player_folder, parent, false));
                break;
            case TYPE_FILE:
                viewHolder = new FileViewHolder(ItemSubtitleFileBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
//                viewHolder = new FileViewHolder(inflater.inflate(R.layout.item_subtitle_file, parent, false));
                break;
            default:
                break;
        }
        return viewHolder ;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        switch (getItemViewType(position)) {
            case TYPE_FOLDER:
                ((FolderViewHolder) viewHolder).bindData(playerFileModels.get(position));
                break;
            case TYPE_FILE:
                ((FileViewHolder)viewHolder).bindData(playerFileModels.get(position), position);
                break;
            default:
                break;
        }
    }

    @Override
    public int getItemCount() {
        return playerFileModels.size();
    }

    public void setData(List<PlayerFileModel> playerFileModels) {
        this.playerFileModels = playerFileModels;
        lastCheckPosition = RecyclerView.NO_POSITION;
        notifyDataSetChanged();
    }

    public class FileViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

//        @BindView(R.id.tvSubtitleName) TextView tvSubtitleName;
//        @BindView(R.id.tvSubtitlePath) TextView tvSubtitlePath;
//
//        @BindView(R.id.tvSize) TextView tvSize;
//        @BindView(R.id.ivCheck) ImageView ivCheck;

        private PlayerFileModel playerFileModel;

        private ItemSubtitleFileBinding binding;
        FileViewHolder(ItemSubtitleFileBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            setOnClickListeners();
        }

        private void setOnClickListeners() {
            binding.llItem.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            final int newPosition = getAdapterPosition();
            if (lastCheckPosition != newPosition) {
                if (lastCheckPosition > RecyclerView.NO_POSITION) {
                    playerFileModels.get(lastCheckPosition).setCheck(false);
                    notifyItemChanged(lastCheckPosition);
                }
            }
            playerFileModels.get(newPosition).swapCheck();
            notifyItemChanged(newPosition);
            lastCheckPosition = newPosition;

            if (listener != null) {
                listener.onClick(view, playerFileModel);
            }
        }

        public void bindData(PlayerFileModel playerFileModel, int position) {
            this.playerFileModel = playerFileModel;
            highlightSearchResult(binding.tvSubtitlePath, FilenameUtils.getPath(playerFileModel.getPath()));
            highlightSearchResult(binding.tvSubtitleName, playerFileModel.getName());
//            TextViewUtil.makeTextViewEndTruncate(tvSubtitleName, 2);
            binding.tvSize.setText(StorageUtil.getDynamicSpace(playerFileModel.getSize()));
            if (playerFileModel.isCheck()) {
                binding.ivCheck.setVisibility(View.VISIBLE);
                lastCheckPosition = position;
            } else {
                binding.ivCheck.setVisibility(View.INVISIBLE);
            }
        }

        private void highlightSearchResult(TextView tv, String str) {
            if (Utils.isEmpty(keyword)) {
                tv.setText(str);
            } else {
                SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(str);
                Pattern p = Pattern.compile(keyword, Pattern.CASE_INSENSITIVE);
                Matcher m = p.matcher(str);
                while (m.find()){
                    spannableStringBuilder.setSpan(new ForegroundColorSpan(Color.RED), m.start(), m.end(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                }
                tv.setText(spannableStringBuilder);
            }
        }

//        @OnClick(R.id.llItem)
//        void onItemClick(View v) {
//            final int newPosition = getAdapterPosition();
//            if (lastCheckPosition != newPosition) {
//                if (lastCheckPosition > RecyclerView.NO_POSITION) {
//                    playerFileModels.get(lastCheckPosition).setCheck(false);
//                    notifyItemChanged(lastCheckPosition);
//                }
//            }
//            playerFileModels.get(newPosition).swapCheck();
//            notifyItemChanged(newPosition);
//            lastCheckPosition = newPosition;
//
//            if (listener != null) {
//                listener.onClick(v, playerFileModel);
//            }
//        }
    }

    public class FolderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

//        @BindView(R.id.ivIcon) ImageView ivIcon;
//        @BindView(R.id.tvTitle) TextView tvTitle;
//        @BindView(R.id.tvDescription) TextView tvDescription;
        private PlayerFileModel playerFileModel;

        private ItemPlayerFolderBinding binding;
        FolderViewHolder(ItemPlayerFolderBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            setOnClickListeners();
            binding.ivIcon.setImageResource(R.drawable.ic_folder_system);
        }

        private void setOnClickListeners() {
            binding.llFolder.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (listener != null) {
                listener.onClick(view, playerFileModel);
            }
        }

        public void bindData(PlayerFileModel playerFileModel) {
            this.playerFileModel = playerFileModel;
            binding.tvTitle.setText(playerFileModel.getName());
            if (playerFileModel.getCount() > 0) {
                binding.tvDescription.setText(context.getString(R.string.format_subtitle, playerFileModel.getCount()));
                binding.tvDescription.setVisibility(View.VISIBLE);
            } else {
                binding.tvDescription.setVisibility(View.GONE);
            }
        }

//        @OnClick(R.id.llFolder)
//        void onItemClick(View view) {
//            if (listener != null) {
//                listener.onClick(view, playerFileModel);
//            }
//        }
    }
}
