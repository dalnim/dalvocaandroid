package com.dalread.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;
import com.dalread.BaseApplication;
import com.dalread.R;
import com.dalread.activity.MainHomeActivity;
import com.dalread.databinding.ItemPlayerMusicCategoryListBinding;
import com.dalread.databinding.ItemPlayerSeasonBinding;
import com.dalread.databinding.ItemPlaylistSongBinding;
import com.dalread.listener.OnClickListener;
import com.dalread.listener.OnLongClickListener;
import com.dalread.listener.OnScrollListener;
import com.dalread.model.MusicCategoryModel;
import com.dalread.model.PlayerFileModel;
import com.dalread.model.PlaylistModel;
import com.dalread.util.DLog;
import com.dalread.util.FileUtil;
import com.dalread.util.ImageUtils;
import com.dalread.util.NumberUtil;
import com.dalread.util.PlayerLanguageUtil;
import com.dalread.util.StorageUtil;
import com.dalread.util.TextViewUtil;
import com.dalread.util.UtilImage;
import com.dalread.util.Utils;
import com.dalread.util.ViewUtil;
import com.dalread.util.Voca;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;

public class NetworkSubAdapter extends RecyclerSwipeAdapter<RecyclerView.ViewHolder> {
    private static final int TYPE_FILE = 0;
    private static final int TYPE_FOLDER = TYPE_FILE + 1;
    private Context context;
    private List<PlayerFileModel> playerFileModels;
    private OnClickListener listener;

    private TextUtils.TruncateAt ellipsize = TextUtils.TruncateAt.MIDDLE;
    private boolean isShowIconArrow = true;

    public NetworkSubAdapter(Context context, List<PlayerFileModel> playerFileModels, OnClickListener listener) {
        this.context = context;
        this.playerFileModels = playerFileModels;
        this.listener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        PlayerFileModel playerFileModel = playerFileModels.get(position);
        if (playerFileModel.isNone() || playerFileModel.isDirectory()) {
            return TYPE_FOLDER;
        }
        return TYPE_FILE;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case TYPE_FOLDER:
                viewHolder = new FolderViewHolder(inflater.inflate(R.layout.item_player_folder, parent, false));
                break;
            case TYPE_FILE:
                viewHolder = new FileViewHolder(inflater.inflate(R.layout.item_player_list, parent, false));
                break;
        }
        return viewHolder ;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        switch (getItemViewType(position)) {
            case TYPE_FOLDER:
                ((FolderViewHolder) viewHolder).bindData(playerFileModels.get(position), position);
                break;
            case TYPE_FILE:
                ((FileViewHolder)viewHolder).bindData(playerFileModels.get(position), position);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return playerFileModels.size();
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.swipe;
    }

    public void setData(List<PlayerFileModel> playerFileModels) {
        this.playerFileModels = playerFileModels;
        notifyDataSetChanged();
    }


    public class FileViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.ivVideoThumbnail) ImageView ivVideoThumbnail;
        @BindView(R.id.ivVideoThumbnailNormal) ImageView ivVideoThumbnailNormal;
        @BindView(R.id.izbVideoThumbnail) ImageView izbVideoThumbnail;
        @BindView(R.id.tvTitle) TextView tvTitle;
        @BindView(R.id.tvDifficult) TextView tvDifficult;
        @BindView(R.id.tvSubtitleExt) TextView tvSubtitleExt;
        @BindView(R.id.tvSubtitleExt2) TextView tvSubtitleExt2;

        @BindView(R.id.tvSize) TextView tvSize;
        @BindView(R.id.tvDate) TextView tvDate;
        @BindView(R.id.pbWatchDuration) ProgressBar pbWatchDuration;
        @BindView(R.id.tvNewFile) TextView tvNewFile;
        @BindView(R.id.iv_bookmark) ImageView ivBookmark;
        @BindView(R.id.ivArrow) ImageView ivArrow;
        @BindView(R.id.iv_memo) ImageView iv_memo;
        @BindView(R.id.tv_memo) TextView tv_memo;
        @BindView(R.id.llItem) LinearLayout llItem;
        @BindView(R.id.cb_select_item) CheckBox cbSelectItem;
        private PlayerFileModel playerFileModel;

        public FileViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bindData(PlayerFileModel item, int position) {
            this.playerFileModel = item;
            this.playerFileModel.setIndex(position);
            ViewUtil.setViewListVisibility(View.GONE, ivVideoThumbnail, ivVideoThumbnailNormal, izbVideoThumbnail, tvNewFile, iv_memo);
            ivArrow.setVisibility(isShowIconArrow && !playerFileModel.isVideo() ? View.VISIBLE : View.GONE);
//            if (isLocal) {
            if (playerFileModel.isVideo()) {
                izbVideoThumbnail.post(() -> {
                    UtilImage.getThumbnailFromSavedVideoImageFile(context, izbVideoThumbnail, playerFileModel, false, null);
                });

                izbVideoThumbnail.setVisibility(View.VISIBLE);
                tvNewFile.setVisibility(playerFileModel.getVideoModel().isNewFile() ? View.VISIBLE : View.GONE);
                ivBookmark.setVisibility(playerFileModel.getVideoModel().isBookmark() ? View.VISIBLE : View.GONE);
                String memo = playerFileModel.getVideoModel().getMemo();
                if (Utils.isEmpty(memo)) {
                    tv_memo.setText("");
                } else {
                    iv_memo.setVisibility(View.VISIBLE);
                    tv_memo.setText(memo);
                }
            } else {
                ivVideoThumbnailNormal.setImageResource(generateIconType(playerFileModel));
                ivVideoThumbnailNormal.setVisibility(View.VISIBLE);
            }
//        }
//            else {
//                ivVideoThumbnailNormal.setImageResource(generateIconType(playerFileModel));
//                ivVideoThumbnailNormal.setVisibility(View.VISIBLE);
//            }
            tvTitle.setText(playerFileModel.getName());
            TextViewUtil.makeTextViewResizable(tvTitle, 3, ellipsize);
            Voca.updateTextViewSubtitleExtension(context, tvSubtitleExt, playerFileModel.getVideoModel().getSubPath1());
            Voca.updateTextViewSubtitleExtension(context, tvSubtitleExt2, playerFileModel.getVideoModel().getSubPath2());
//            if ((playerFileModel.getVideoModel() == null) || (playerFileModel.getVideoModel().getSubPath() == null) || (playerFileModel.getVideoModel().getSubPath().equals("")) || (playerFileModel.getVideoModel().getVocaKnowCount() == 0)) {
            if ((playerFileModel.getVideoModel() == null) || (playerFileModel.getVideoModel().getVocaKnowCount() == 0)) {
                tvDifficult.setVisibility(View.GONE);
                tvDate.setText(DateFormat.getDateInstance(DateFormat.DEFAULT).format(playerFileModel.getCreatedDate())); //https://docs.oracle.com/javase/tutorial/i18n/format/dateFormat.html
                tvDate.setVisibility(View.VISIBLE);
            } else {
                Voca.updateTextViewDifficultLabelForSubtitle(context, tvDifficult, playerFileModel.getVideoModel().getVocaKnowCount(), playerFileModel.getVideoModel().getVocaKnowAll());

                tvDate.setVisibility(View.GONE);
            }
            tvSize.setText(StorageUtil.getDynamicSpace(playerFileModel.getSize()));

//            if (playerFileModel.isVideo() || playerFileModel.isMusic()) {
//                cbSelectItem.setVisibility(View.VISIBLE);
//            } else {
//                cbSelectItem.setVisibility(View.GONE);
//            }
        }

        private int generateIconType(PlayerFileModel file) {
            int id = R.drawable.ic_etc;
            if (file.isVideo()) {
                id = R.drawable.ic_video;
            } else if (file.isSubtitle()) {
                id = R.drawable.ic_file_subtitle;
            }
            return id;
        }

        @OnClick({R.id.llItem, R.id.pbWatchDuration, R.id.izbVideoThumbnail})
        void onItemClick(View view) {
            if (view.getId() == R.id.llItem && (playerFileModel.isVideo() || playerFileModel.isMusic())) {
                selectItem();
            }
            invokeListener(view);
        }

        private void invokeListener(View view) {
            if (listener != null) {
                listener.onClick(view, playerFileModel);
            }
        }

        private void selectItem() {
            notifyItemChanged(getBindingAdapterPosition());
        }
    }

    public class FolderViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.ivIcon) ImageView ivIcon;
        @BindView(R.id.tvTitle) TextView tvTitle;
        @BindView(R.id.tvDescription) TextView tvDescription;
        private PlayerFileModel playerFileModel;

        public FolderViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bindData(PlayerFileModel playerFileModel, int position) {
            this.playerFileModel = playerFileModel;
            this.playerFileModel.setIndex(position);
            tvTitle.setText(playerFileModel.getName());
            if (playerFileModel.isNone()) {
                ivIcon.setVisibility(View.GONE);
            } else {
                ivIcon.setImageResource(playerFileModel.isDirectoryBlack() ? R.drawable.ic_folder_system : R.drawable.ic_folder);
                ivIcon.setVisibility(View.VISIBLE);
            }
            if (playerFileModel.getCount() > 0) {
                tvDescription.setText(context.getString(R.string.format_video, playerFileModel.getCount()));
                tvDescription.setVisibility(View.VISIBLE);
            } else {
                tvDescription.setVisibility(View.GONE);
            }
        }

        @OnClick(R.id.llFolder)
        void onItemClick(View view) {
            if (listener != null) {
                listener.onClick(view, playerFileModel);
            }
        }
    }


    public void setEllipsize(TextUtils.TruncateAt ellipsize) {
        this.ellipsize = ellipsize;
    }

    public void setShowIconArrow(boolean showIconArrow) {
        isShowIconArrow = showIconArrow;
    }
}
