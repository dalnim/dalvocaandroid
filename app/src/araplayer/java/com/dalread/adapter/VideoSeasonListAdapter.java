package com.dalread.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;
import com.dalread.R;
import com.dalread.databinding.ItemPlayerSeasonLandscapeBinding;
import com.dalread.listener.OnClickListener;
import com.dalread.model.VideoSeasonModel;
import com.dalread.util.Constant;
import com.dalread.util.UtilImage;
import com.dalread.util.Utils;

import java.util.List;

public class VideoSeasonListAdapter extends RecyclerSwipeAdapter<VideoSeasonListAdapter.SeasonViewHolder> {

    private Context context;
    private List<VideoSeasonModel> videoSeasonModelList;
    private OnClickListener listener;


    public VideoSeasonListAdapter(Context context, OnClickListener listener) {
        this.context = context;
        this.listener = listener;
    }


    @Override
    public SeasonViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new SeasonViewHolder(ItemPlayerSeasonLandscapeBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
//        return new ViewHolder(inflater.inflate(R.layout.item_player_season_landscape, parent, false)) ;
    }

    @Override
    public void onBindViewHolder(SeasonViewHolder viewHolder, int position) {
        ((SeasonViewHolder)viewHolder).bindData(videoSeasonModelList.get(position));
    }

    @Override
    public int getItemCount() {
        if (Utils.isEmptyCollection(videoSeasonModelList))
            return 0;
        return videoSeasonModelList.size();
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.swipe;
    }

    public void setData(List<VideoSeasonModel> videoSeriesModelList) {
        this.videoSeasonModelList = videoSeriesModelList;
        notifyDataSetChanged();
    }


    public class SeasonViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
//        @BindView(R.id.ivTvSeasonPoster) ImageView ivTvSeasonPoster;
//        @BindView(R.id.ivTvSeasonPosterDefault) ImageView ivTvSeasonPosterDefault;
//        @BindView(R.id.tvShowTitle) TextView tvShowTitle;
//        @BindView(R.id.tvSeasonNumber) TextView tvSeriesCount;
        private VideoSeasonModel videoSeasonModel;
        private ItemPlayerSeasonLandscapeBinding binding;

        public SeasonViewHolder(@NonNull ItemPlayerSeasonLandscapeBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            initOnClickListener();
//            ButterKnife.bind(this, itemView);
        }
        private void initOnClickListener() {
            binding.llItem.setOnClickListener(this);
        }

        public void bindData(VideoSeasonModel item) {
            this.videoSeasonModel = item;
            updatePosterUI();
//            izbVideoThumbnail.setVisibility(View.VISIBLE);
            binding.tvShowTitle.setText(videoSeasonModel.getTmdbVideoName());
            binding.tvSeasonNumber.setText(context.getString(R.string.season) + Constant.BASE_ONE_SPACE + String.valueOf(videoSeasonModel.getSeasonNumber()));
        }

        private void updatePosterUI() {
            binding.tvShowTitle.setVisibility(View.GONE);
//            izbVideoThumbnail.setImageResource(R.drawable.ic_tv_season_default_poster);
            if (Utils.isEmpty(videoSeasonModel.getTmdbSeasonPosterPath())) {
                binding.ivTvSeasonPosterDefault.setVisibility(View.VISIBLE);
                binding.ivTvSeasonPoster.setVisibility(View.GONE);
                binding.tvShowTitle.setVisibility(View.VISIBLE);
                binding.tvShowTitle.setText(videoSeasonModel.getSeasonNameVideoFile());
                return;
            }
            binding.ivTvSeasonPosterDefault.setVisibility(View.GONE);
            binding.ivTvSeasonPoster.setVisibility(View.VISIBLE);
            String seasonImagePath = Constant.PLAYER.THE_MOVIE_DB.BASE_IMAGE_URL_SMALL_SIZE + videoSeasonModel.getTmdbSeasonPosterPath();
            UtilImage.getThumbnail(context, binding.ivTvSeasonPoster, 0, seasonImagePath, null);


//            final int mediaId = videoSeriesModel.getTmdbTvShowId();
//            final String mediaType = Constant.PLAYER.THE_MOVIE_DB.KEY_TV;
//
//            final String jsonName = StorageUtil.generateTMDBJson(mediaId, mediaType);
//            final String jsonPath = StorageUtil.getTMDBPath(context, mediaType + "_" + mediaId, jsonName);
//            VideoInformationModel videoInformationModel;
//            if (StorageUtil.isFileExist(jsonPath)) {
//                String json = StorageUtil.readJsonFile(jsonPath);
//                videoInformationModel = VideoInformationModel.parserVideoInformation(json, mediaType);
//                StorageUtil.getTMDBPath(context, videoInformationModel);
//                String seasonImageName = mediaType + "_" + mediaId  + "_" + Constant.PLAYER.THE_MOVIE_DB.BASE_FILE_SEASON + videoSeriesModel.getSeasonNumber() + "_" + videoSeriesModel.getTmdbSeasonId() + Constant.PLAYER.THE_MOVIE_DB.BASE_FILE_POSTER + "jpg";
//                String seasonImagePath = FilenameUtils.concat(StorageUtil.getTMDBPath(context, videoInformationModel), seasonImageName);
//
//                if (StorageUtil.isFileExist(seasonImagePath)) {
//                    StorageUtil.getThumbnail(context, izbVideoThumbnail, 0, seasonImagePath);
//                }
//            }
        }

//        @OnClick({R.id.llItem})
//        void onItemClick(View view) {
//            if (listener != null) {
//                listener.onClick(view, videoSeasonModel);
//            }
//        }

        @Override
        public void onClick(View v) {
            if (listener != null) {
                listener.onClick(v, videoSeasonModel);
            }
        }
    }
}
