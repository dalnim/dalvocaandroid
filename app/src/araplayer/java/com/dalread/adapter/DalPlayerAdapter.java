package com.dalread.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;
import com.dalread.BaseApplication;
import com.dalread.R;
import com.dalread.activity.MainHomeActivity;
import com.dalread.activity.MultiPlayerSelectVideoActivity;
import com.dalread.databinding.ItemPlayerLanguageFolderBinding;
import com.dalread.databinding.ItemPlayerListBinding;
import com.dalread.databinding.ItemPlayerMusicCategoryListBinding;
import com.dalread.databinding.ItemPlayerSeasonBinding;
import com.dalread.databinding.ItemPlaylistSongBinding;
import com.dalread.listener.OnClickListener;
import com.dalread.listener.OnLongClickListener;
import com.dalread.listener.OnScrollListener;
import com.dalread.model.MusicCategoryModel;
import com.dalread.model.PlayerFileModel;
import com.dalread.model.IPlaylistDisplay;
import com.dalread.util.AppFlavorUtil;
import com.dalread.util.AraThemeUtil;
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

public class DalPlayerAdapter extends RecyclerSwipeAdapter<RecyclerView.ViewHolder> {
    private static final int TYPE_FILE = 0;
    private static final int TYPE_FOLDER = TYPE_FILE + 1;
    private static final int TYPE_ADS_BANNER = TYPE_FOLDER + 1;
    private static final int TYPE_SEASON_LIST = TYPE_ADS_BANNER + 1;
    private static final int TYPE_LANGUAGE_FOLDER = TYPE_SEASON_LIST + 1;
    private static final int TYPE_SEASON_ITEM = TYPE_LANGUAGE_FOLDER + 1;
    private static final int TYPE_MUSIC_CATEGORY_LIST = TYPE_SEASON_ITEM + 1;
    private static final int TYPE_PLAYLIST = TYPE_MUSIC_CATEGORY_LIST + 1;
    private Context context;
    private List<PlayerFileModel> playerFileModels;
    private OnClickListener listener;
    private OnClickListener onClickListenerVideoSeries;
    private OnClickListener onClickListenerMusicCategory;
    private OnScrollListener onRvSeasonScrollListener;
    private OnLongClickListener longClickListener;
    private OnClickListener onClickEditPlaylist;
//    private boolean isLocal;
    private int appMediaType;
    private TextUtils.TruncateAt ellipsize = TextUtils.TruncateAt.MIDDLE;
    private boolean isShowIconInformation = true;
    private boolean isShowIconArrow = true;
    private int adsHeight = 0;
    private boolean isEditMode = false;
    private List<PlayerFileModel> selectedVideoList = new ArrayList<>();
    private MusicCategoryModel currentMusicCategoryModel;

    public DalPlayerAdapter(Context context, List<PlayerFileModel> playerFileModels, int appMediaType, OnClickListener listener) {
        this(context, playerFileModels, appMediaType, listener, null, null, null);
    }

    public DalPlayerAdapter(Context context, List<PlayerFileModel> playerFileModels, int appMediaType, OnClickListener listener, OnClickListener onClickListenerVideoSeries, OnScrollListener onRvSeasonScrollListener, OnLongClickListener longClickListener) {
        this.context = context;
        this.playerFileModels = playerFileModels;
        this.listener = listener;
        this.onClickListenerVideoSeries = onClickListenerVideoSeries;
//        this.isLocal = isLocal;
        this.appMediaType = appMediaType;
        this.onRvSeasonScrollListener = onRvSeasonScrollListener;
        this.longClickListener = longClickListener;
    }

    @Override
    public int getItemViewType(int position) {
        PlayerFileModel playerFileModel = playerFileModels.get(position);
        if (playerFileModel.isAdsBanner()) {
            return TYPE_ADS_BANNER;
        } else if (playerFileModel.isSeasonList()) {
            return TYPE_SEASON_LIST;
        } else if (playerFileModel.isSeasonItem()) {
            return TYPE_SEASON_ITEM;
        } else if (playerFileModel.isLanguageFolder()) {
            return TYPE_LANGUAGE_FOLDER;
        } else if (playerFileModel.isNone() || playerFileModel.isDirectory()) {
            return TYPE_FOLDER;
        } else if (playerFileModel.isMusicCategoryList()) {
            return TYPE_MUSIC_CATEGORY_LIST;
        } else if (playerFileModel.isPlaylist()) {
            return TYPE_PLAYLIST;
        }
//        if (playerFileModel.isSeries())
//            return TYPE_SERIES;
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
            case TYPE_SEASON_ITEM:
                viewHolder = new FileViewHolder(ItemPlayerListBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
                break;
            case TYPE_SEASON_LIST:
                viewHolder = new SeasonViewHolder(ItemPlayerSeasonBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
                break;
            case TYPE_MUSIC_CATEGORY_LIST:
                viewHolder = new MusicCategoryListViewHolder(ItemPlayerMusicCategoryListBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
                break;
            case TYPE_LANGUAGE_FOLDER:
                viewHolder =  new LanguageFolderViewHolder(ItemPlayerLanguageFolderBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
                break;
            case TYPE_PLAYLIST:
                viewHolder = new PlaylistViewHolder(ItemPlaylistSongBinding.inflate(inflater, parent, false));
                break;
            case TYPE_ADS_BANNER:
                viewHolder = new AdsBannerViewHolder(inflater.inflate(R.layout.item_player_ads_banner, parent, false));
            default:
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
            case TYPE_SEASON_ITEM:
                ((FileViewHolder)viewHolder).bindData(playerFileModels.get(position), position);
                break;
            case TYPE_SEASON_LIST:
                ((SeasonViewHolder)viewHolder).bindData(playerFileModels.get(position), position);
                break;
            case TYPE_MUSIC_CATEGORY_LIST:
                ((MusicCategoryListViewHolder)viewHolder).bindData(playerFileModels.get(position), position);
                break;
            case TYPE_LANGUAGE_FOLDER:
                ((LanguageFolderViewHolder)viewHolder).bindData(playerFileModels.get(position), position);
                break;
            case TYPE_PLAYLIST:
                ((PlaylistViewHolder)viewHolder).bindData(playerFileModels.get(position));
                break;
            case TYPE_ADS_BANNER:
                ((AdsBannerViewHolder)viewHolder).bindData(playerFileModels.get(position), position);
                break;
            default:
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

    public void setCurrentMusicCategoryModel(MusicCategoryModel currentMusicCategoryModel) {
        this.currentMusicCategoryModel = currentMusicCategoryModel;
    }
    public void setOnClickListenerMusicCategory(OnClickListener onClickListenerMusicCategory) {
        this.onClickListenerMusicCategory = onClickListenerMusicCategory;
    }

    public class FileViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        private PlayerFileModel playerFileModel;

        private ItemPlayerListBinding binding;
        FileViewHolder(ItemPlayerListBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            setOnClickListeners();
            initColor();
        }

        private void initColor() {
            if (AppFlavorUtil.isAraMultiPlayerApp()) {
                binding.getRoot().setCardBackgroundColor(ContextCompat.getColor(context, R.color.multiPlayerBackgroundBlackColor));
                AraThemeUtil.setBackgroundColor(context, binding.llItem, R.color.multiPlayerBackgroundBlackColor);
                ColorStateList colorStateList = context.getResources().getColorStateList(R.color.iconPrimaryWhiteColor, context.getTheme());

                binding.cbSelectItem.setButtonTintList(colorStateList);
                AraThemeUtil.setTextColor(context, binding.tvTitle, R.color.textPrimaryWhiteColor);
                AraThemeUtil.setBackgroundColor(context, binding.tvTitle, R.color.multiPlayerBackgroundBlackColor);
                AraThemeUtil.setTextColor(context, binding.tvMemo, R.color.textPrimaryWhiteColor);
                AraThemeUtil.setTextColor(context, binding.tvDifficult, R.color.textPrimaryWhiteColor);
                AraThemeUtil.setTextColor(context, binding.tvDate, R.color.textPrimaryWhiteColor);
                AraThemeUtil.setTextColor(context, binding.tvSize, R.color.textPrimaryWhiteColor);
            }
        }

        private void setOnClickListeners() {
            binding.llItem.setOnClickListener(this);
            binding.pbWatchDuration.setOnClickListener(this);
            binding.izbVideoThumbnail.setOnClickListener(this);

            binding.llItem.setOnLongClickListener(this);
        }

        public void bindData(PlayerFileModel item, int position) {
            this.playerFileModel = item;
            this.playerFileModel.setIndex(position);
            if (isShowIconInformation) {
                int percentageWatched = NumberUtil.percentageWatched(playerFileModel.getVideoModel().getWatchDuration(), playerFileModel.getDuration());
                binding.pbWatchDuration.setVisibility((percentageWatched > 0) && FileUtil.isValidVideoExtension(playerFileModel.getPath()) ? View.VISIBLE : View.GONE);
                binding.pbWatchDuration.setProgress(percentageWatched);
            }
            ViewUtil.setViewListVisibility(View.GONE, binding.ivVideoThumbnail, binding.ivVideoThumbnailNormal, binding.izbVideoThumbnail, binding.tvNewFile, binding.ivMemo);
            binding.ivArrow.setVisibility(isShowIconArrow && !playerFileModel.isVideo() ? View.VISIBLE : View.GONE);
//            if (isLocal) {
            if (playerFileModel.isVideo()) {
                binding.izbVideoThumbnail.post(() -> {
                    //가끔 You cannot start a load for a destroyed activity 에러가 나서 이걸 한다. (비디오 리스트에서 비디오를 클릭하고 재생뷰로 돌아갈때 발생한다)
                    if (context instanceof Activity && !((Activity) context).isDestroyed()) {
                        UtilImage.getThumbnailFromSavedVideoImageFile(context, binding.izbVideoThumbnail, playerFileModel, false, null);
                    }
//                    UtilImage.getThumbnailFromSavedVideoImageFile(context, binding.izbVideoThumbnail, playerFileModel, false, null);
                });

                binding.izbVideoThumbnail.setVisibility(View.VISIBLE);
                binding.tvNewFile.setVisibility(playerFileModel.getVideoModel().isNewFile() ? View.VISIBLE : View.GONE);
                binding.ivBookmark.setVisibility(playerFileModel.getVideoModel().isBookmark() ? View.VISIBLE : View.GONE);
                String memo = playerFileModel.getVideoModel().getMemo();
                if (Utils.isEmpty(memo)) {
                    binding.tvMemo.setText("");
                } else {
                    binding.tvMemo.setVisibility(View.VISIBLE);
                    binding.tvMemo.setText(memo);
                }
            } else {
                binding.ivVideoThumbnailNormal.setImageResource(generateIconType(playerFileModel));
                binding.ivVideoThumbnailNormal.setVisibility(View.VISIBLE);
            }
//        }
//            else {
//                ivVideoThumbnailNormal.setImageResource(generateIconType(playerFileModel));
//                ivVideoThumbnailNormal.setVisibility(View.VISIBLE);
//            }
            binding.tvTitle.setText(playerFileModel.getName());
            TextViewUtil.makeTextViewResizable(binding.tvTitle, 3, ellipsize);
            Voca.updateTextViewSubtitleExtension(context, binding.tvSubtitleExt, playerFileModel.getVideoModel().getSubPath1());
            Voca.updateTextViewSubtitleExtension(context, binding.tvSubtitleExt2, playerFileModel.getVideoModel().getSubPath2());
//            if ((playerFileModel.getVideoModel() == null) || (playerFileModel.getVideoModel().getSubPath() == null) || (playerFileModel.getVideoModel().getSubPath().equals("")) || (playerFileModel.getVideoModel().getVocaKnowCount() == 0)) {
            if ((playerFileModel.getVideoModel() == null) || (playerFileModel.getVideoModel().getVocaKnowCount() == 0)) {
                binding.tvDifficult.setVisibility(View.GONE);
                binding.tvDate.setText(DateFormat.getDateInstance(DateFormat.DEFAULT).format(playerFileModel.getCreatedDate())); //https://docs.oracle.com/javase/tutorial/i18n/format/dateFormat.html
                binding.tvDate.setVisibility(View.VISIBLE);
            } else {
                Voca.updateTextViewDifficultLabelForSubtitle(context, binding.tvDifficult, playerFileModel.getVideoModel().getVocaKnowCount(), playerFileModel.getVideoModel().getVocaKnowAll());

                binding.tvDate.setVisibility(View.GONE);
            }
            binding.tvSize.setText(StorageUtil.getDynamicSpace(playerFileModel.getSize()));

            if (isEditMode && (playerFileModel.isVideo() || playerFileModel.isMusic())) {
                binding.cbSelectItem.setVisibility(View.VISIBLE);
                binding.cbSelectItem.setChecked(selectedVideoList.contains(playerFileModel));
            } else {
                binding.cbSelectItem.setVisibility(View.GONE);
            }
            if (AppFlavorUtil.isAraMultiPlayerApp()) {
                binding.tvSubtitleExt.setVisibility(View.INVISIBLE);
                binding.tvSubtitleExt2.setVisibility(View.INVISIBLE);
            }
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

        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.llItem && isEditMode && (playerFileModel.isVideo() || playerFileModel.isMusic())) {
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
            if (selectedVideoList.contains(playerFileModel)) {
                selectedVideoList.remove(playerFileModel);
            } else {
                selectedVideoList.add(playerFileModel);
            }
            notifyItemChanged(getBindingAdapterPosition());
        }

        @Override
        public boolean onLongClick(View v) {
            if (v.getId() == R.id.llItem) {
                boolean canEditMediaList = false;
                if (context instanceof MainHomeActivity) {
                    canEditMediaList = ((MainHomeActivity) context).canEditMediaList();
                } else if (context instanceof MultiPlayerSelectVideoActivity) {
                    canEditMediaList = true;
                }

                if ((!playerFileModel.isVideo() && !playerFileModel.isMusic()) || longClickListener == null || !canEditMediaList) {
                    invokeListener(v);
                } else {
                    if (isEditMode) {
                        selectItem();
                        invokeListener(v);
                    } else {
                        selectedVideoList.add(playerFileModel);
                        longClickListener.onLongClick(v, playerFileModel);
                    }
                }
                return true;
            }
            return false;
        }
    }

    public class SeasonViewHolder extends RecyclerView.ViewHolder {
        private PlayerFileModel playerFileModel;
        private ItemPlayerSeasonBinding binding;

        public SeasonViewHolder(ItemPlayerSeasonBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            ButterKnife.bind(this, binding.getRoot());
        }

        public void bindData(PlayerFileModel item, int position) {
            this.playerFileModel = item;
            binding.rvVideoSeasonList.setAdapter(null);
            binding.rvVideoSeasonList.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
            VideoSeasonListAdapter videoSeasonListAdapter = new VideoSeasonListAdapter(context, new OnClickListener() {
                @Override
                public void onClick(View view, Object object) {
                    if (onClickListenerVideoSeries != null) {
                        onClickListenerVideoSeries.onClick(view, object);
                    }
                }
            });
            binding.rvVideoSeasonList.setAdapter(videoSeasonListAdapter);
            binding.rvVideoSeasonList.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                    if (onRvSeasonScrollListener != null) {
                        onRvSeasonScrollListener.onScrollStateChanged(newState);
                    }
                }
            });

            if (item.getVideoSeasonModelList() == null || item.getVideoSeasonModelList().isEmpty()) {
                binding.rvVideoSeasonList.setVisibility(View.GONE);
                binding.tvNoDataTvSeason.setVisibility(View.VISIBLE);
            } else {
                videoSeasonListAdapter.setData(item.getVideoSeasonModelList());
                binding.rvVideoSeasonList.setVisibility(View.VISIBLE);
                binding.tvNoDataTvSeason.setVisibility(View.GONE);
            }
        }


        @OnClick({R.id.llFolder})
        void onItemClick(View view) {
            if (listener != null) {
                listener.onClick(view, playerFileModel);
            }
        }
    }

    public class MusicCategoryListViewHolder extends RecyclerView.ViewHolder {
        private PlayerFileModel playerFileModel;
        private ItemPlayerMusicCategoryListBinding binding;

        public MusicCategoryListViewHolder(ItemPlayerMusicCategoryListBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            ButterKnife.bind(this, binding.getRoot());
        }

        public void bindData(PlayerFileModel item, int position) {
            this.playerFileModel = item;
            binding.rvMusicCategoryList.setAdapter(null);
            binding.rvMusicCategoryList.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
            MusicCategoryListAdapter musicCategoryListAdapter = new MusicCategoryListAdapter(context, currentMusicCategoryModel, new OnClickListener() {
                @Override
                public void onClick(View view, Object object) {
                    if (onClickListenerMusicCategory != null) {
                        onClickListenerMusicCategory.onClick(view, object);
                    }
                }
            });
            binding.rvMusicCategoryList.setAdapter(musicCategoryListAdapter);
            binding.rvMusicCategoryList.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                    if (onRvSeasonScrollListener != null) {
                        onRvSeasonScrollListener.onScrollStateChanged(newState);
                    }
                }
            });
            musicCategoryListAdapter.setData(item.getMusicCategoryList());
            binding.rvMusicCategoryList.setVisibility(View.VISIBLE);
        }


        @OnClick({R.id.llItem})
        void onItemClick(View view) {
            if (listener != null) {
                listener.onClick(view, playerFileModel);
            }
        }
    }

    public class PlaylistViewHolder extends RecyclerView.ViewHolder {
        private PlayerFileModel playerFileModel;
        private ItemPlaylistSongBinding binding;

        public PlaylistViewHolder(ItemPlaylistSongBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            ButterKnife.bind(this, binding.getRoot());
        }

        public void bindData(PlayerFileModel item) {
            this.playerFileModel = item;
            IPlaylistDisplay display = item.getPlaylistDisplay();
            if (display == null) return;
            binding.tvPlaylistName.setText(display.getName());
            int songTextId;
            if (display.getFilePathCount() > 1) {
                songTextId = R.string.more_than_1_songs;
            } else {
                songTextId = R.string.less_or_equal_than_1_songs;
            }
            binding.tvTotalSong.setText(String.format(context.getString(songTextId), display.getFilePathCount()));
        }

        @OnClick({R.id.llPlaylist})
        void onItemClick(View view) {
            if (listener != null) {
                listener.onClick(view, playerFileModel);
            }
        }

        @OnClick({R.id.ivEdit})
        void onClickIvEdit(View view) {
            if (onClickEditPlaylist != null) {
                onClickEditPlaylist.onClick(view, playerFileModel.getPlaylistDisplay());
            }
        }
    }

    public class LanguageFolderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private PlayerFileModel playerFileModel;

        private ItemPlayerLanguageFolderBinding binding;
        LanguageFolderViewHolder(ItemPlayerLanguageFolderBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            initColor();
            setOnClickListeners();
        }
        private void setOnClickListeners() {
            binding.root.setOnClickListener(this);
        }

        private void initColor() {
            if (AppFlavorUtil.isAraMultiPlayerApp()) {
                AraThemeUtil.setBackgroundColor(context, binding.root, R.color.multiPlayerBackgroundBlackColor);
                AraThemeUtil.setTextColor(context, binding.tvVideoFolderForStudyLanguage, R.color.textPrimaryWhiteColor);
            }
        }


        public void bindData(PlayerFileModel item, int position) {
            this.playerFileModel = item;

            binding.ivVideoFolderForStudyLanguage.setBackgroundResource(ImageUtils.getStudyLanguageFlag());
            binding.tvVideoFolderForStudyLanguage.setText(PlayerLanguageUtil.getStudyLanguageFolderText(appMediaType));
        }

        @Override
        public void onClick(View view) {
            if (listener != null) {
                listener.onClick(view, playerFileModel);
            }
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

    public class AdsBannerViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.llAdsBanner) FrameLayout llAdsBanner;
        private AdView mAdView;
        private PlayerFileModel playerFileModel;

        public AdsBannerViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bindData(PlayerFileModel playerFileModel, int position) {
            this.playerFileModel = playerFileModel;
            this.playerFileModel.setIndex(position);
            mAdView = playerFileModel.getAdView();
            if (llAdsBanner.getChildCount() > 0) {
                llAdsBanner.removeAllViews();
            }
            if (mAdView != null && mAdView.getParent() != null) {
                ((ViewGroup) mAdView.getParent()).removeView(mAdView);
            }
            // Admob AraPlayer Banner Step 1 - Create an AdView and set the ad unit ID on it.
//            mAdView.setAdUnitId(MobileAd.getAdsBannerId(context));
            llAdsBanner.addView(mAdView);
            loadBanner();
        }

        protected void loadBanner() {
            AdRequest adRequest = new AdRequest.Builder().build();
//            AdSize adSize = BaseMobileAd.getAdSize(context);
            // Admob Step 4 - Set the adaptive ad size on the ad view.
//            mAdView.setAdSize(adSize);

            // Admob Step 5 - Start loading the ad in the background.
            mAdView.loadAd(adRequest);

            mAdView.setAdListener(new AdListener() {
                @Override
                public void onAdLoaded() {
                    // Code to be executed when an ad finishes loading.
                    DLog.d("loadBanner", "onAdLoaded");
                    int verticalPadding = context.getResources().getDimensionPixelOffset(R.dimen.ad_view_vertical_padding);
                    llAdsBanner.setPadding(0, verticalPadding, 0, verticalPadding);
                    llAdsBanner.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

                        @Override
                        public void onGlobalLayout() {
                            if (adsHeight != llAdsBanner.getHeight()) {
                                adsHeight = llAdsBanner.getHeight();
                                llAdsBanner.setMinimumHeight(adsHeight);
                            }
                            llAdsBanner.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        }
                    });
                }

                @Override
                public void onAdFailedToLoad(LoadAdError adError) {
                    // Code to be executed when an ad request fails.
                    DLog.d("loadBanner", "onAdFailedToLoad");
                    llAdsBanner.setPadding(0, 0, 0, 0);
                }

                @Override
                public void onAdOpened() {
                    // Code to be executed when an ad opens an overlay that
                    // covers the screen.
                    DLog.d("loadBanner", "onAdOpened");
//                    BaseApplication.getInstance().getSharedPref().setLastAdsClickedTime(0);
                    BaseApplication.getInstance().getSharedPref().setLastAdsClickedTime(System.currentTimeMillis());
                }

                @Override
                public void onAdClicked() {
                    // Code to be executed when the user clicks on an ad.
                    DLog.d("loadBanner", "onAdClicked");
                    //TODO : This code is not called when I click on a banner Ads. (onAdOpened() is called instead)
                    BaseApplication.getInstance().getSharedPref().setLastAdsClickedTime(System.currentTimeMillis());
                }

                @Override
                public void onAdClosed() {
                    // Code to be executed when the user is about to return
                    // to the app after tapping on an ad.
                    DLog.d("loadBanner", "onAdClosed");
                }
            });
        }
//
//        @OnClick(R.id.llFolder)
//        void onItemClick(View view) {
//            if (listener != null) {
//                listener.onClick(view, playerFileModel);
//            }
//        }
    }

    public void setEllipsize(TextUtils.TruncateAt ellipsize) {
        this.ellipsize = ellipsize;
    }

    public void setShowIconInformation(boolean showIconInformation) {
        isShowIconInformation = showIconInformation;
    }

    public void setShowIconArrow(boolean showIconArrow) {
        isShowIconArrow = showIconArrow;
    }

    public void setEditMode(boolean editMode) {
        if (isEditMode == editMode) return;
        isEditMode = editMode;
        if (!editMode) selectedVideoList.clear();
        notifyDataSetChanged();
    }

    public boolean isEditMode() {
        return isEditMode;
    }

    public boolean isSelectAllVideoFile() {
        boolean isSelectAll;
        int totalVideoFile = getAllVideoFiles().size();
        isSelectAll = selectedVideoList.size() == totalVideoFile;
        return isSelectAll;
    }

    public void selectAllVideo(boolean selectAll) {
        selectedVideoList.clear();
        if (selectAll) {
            selectedVideoList.addAll(getAllVideoFiles());
        }
        notifyDataSetChanged();
    }

    private List<PlayerFileModel> getAllVideoFiles() {
        return playerFileModels.stream().filter(it -> it.isVideo() || it.isMusic()).collect(Collectors.toList());
    }

    public List<PlayerFileModel> getSelectedVideoList() {
        return selectedVideoList;
    }

    public void setOnClickEditPlaylist(OnClickListener listener) {
        onClickEditPlaylist = listener;
    }
}
