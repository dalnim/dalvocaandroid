package com.dalread.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.dalread.AraPlayerApplication;
import com.dalread.R;
import com.dalread.adapter.CastProfileAdapter;
import com.dalread.adapter.StudyChatAdapter;
import com.dalread.base.EnumLanguage;
import com.dalread.component.SpacingItemDecoration;
import com.dalread.database.DownloadModelQuery;
import com.dalread.database.VideoModelQuery;
import com.dalread.database.VideoSeasonModelQuery;
import com.dalread.database.sqlite.model.DicModel;
import com.dalread.dialog.RecyclerViewDialog;
import com.dalread.dialog.YesNoDialog;
import com.dalread.helper.SubtitleFileChooserHelper;
import com.dalread.listener.OnClickListener;
import com.dalread.listener.OnDoubleClickListener;
import com.dalread.listener.OnVocaStudyChatClickListener;
import com.dalread.listener.OnYesNoClickListener;
import com.dalread.model.DownloadModel;
import com.dalread.model.PlayerFileModel;
import com.dalread.model.SearchVideoModel;
import com.dalread.model.SeasonEpisodeInfo;
import com.dalread.model.VideoInformationModel;
import com.dalread.model.VideoModel;
import com.dalread.model.VocaStudyChat;
import com.dalread.model.VocaStudyChatExam;
import com.dalread.network.DalApiListener;
import com.dalread.network.events.BaseEvent;
import com.dalread.network.events.SuccessEvent;
import com.dalread.util.Constant;
import com.dalread.util.CustomTranslate;
import com.dalread.util.DLog;
import com.dalread.util.FileUtil;
import com.dalread.util.Loading;
import com.dalread.util.NetworkUtil;
import com.dalread.util.NumberUtil;
import com.dalread.util.StorageUtil;
import com.dalread.util.StringUtils;
import com.dalread.util.SupportVideoFormat;
import com.dalread.util.TimeUtil;
import com.dalread.util.TmdbUtil;
import com.dalread.util.ToastUtil;
import com.dalread.util.UtilImage;
import com.dalread.util.Utils;
import com.dalread.util.VideoUtil;
import com.dalread.util.ViewUtil;
import com.dalread.util.Voca;
import com.google.gson.GsonBuilder;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import okhttp3.ResponseBody;
import wseemann.media.FFmpegMediaMetadataRetriever;

public class VideoInformationActivity extends MediaInformationActivity implements View.OnClickListener, SubtitleFileChooserHelper.Listener {
    private SubtitleFileChooserHelper subtitleConnectHelper;
    private int subPathIndex = Constant.PLAYER.INTENT.SUBPATH_INDEX_1; //VideoInformationActivity에서는 첫번째 자막 파일만 선택하게 하자.

    private CastProfileAdapter castProfileAdapter;
    private boolean isSearchVideo = false;
    @Override
    public void initView() {
        castProfileAdapter = new CastProfileAdapter(this, generateCastProfileData(), onCastProfileClickListener);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        binding.rvListCredits.setLayoutManager(layoutManager);
        binding.rvListCredits.setAdapter(castProfileAdapter);
        binding.rvListCredits.addItemDecoration(new SpacingItemDecoration(getResources().getDimensionPixelSize(R.dimen.item_padding_left_right), LinearLayoutManager.HORIZONTAL));
        binding.rvListCredits.setOnClickListener(v -> openVideoCreditsScreen(videoInformationModel));
        subtitleConnectHelper = new SubtitleFileChooserHelper(this, SubtitleFileChooserHelper.VIEW_TYPE.VIDEO_INFORMATION_VIEW, this);
        super.initView();
    }

    @Override
    public void initData() {
        super.initData();
        initOnClickListener();
        initSearchVideoInformation();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isReload) {
            isReload = false;

            if (isSearchVideo)
                getVideoInformation();
            else
                updateUI();

            isSearchVideo = false;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_SEARCH_VIDEO) {
            switch (resultCode) {
                case RESULT_OK:
                    isReload = true;
                    isSearchVideo = true;
                    isUseTMDBImage = true;
                    break;
                case RESULT_CANCELED:
                    isReload = false;
                    break;
            }
        } else if (requestCode == REQUEST_THUMBNAIL_LIST) {
            switch (resultCode) {
                case RESULT_OK:
                    isReload = true;
                    break;
                case RESULT_CANCELED:
                    isReload = false;
                    break;
            }
        }
    }

    private void updateSubtitleConnectButtonTitle() {
        if (playerFileModel == null) {
            return;
        }

        VideoModel videoModel = VideoModelQuery.getByPath(Voca.getRealm(), playerFileModel.getPath());
        if (videoModel == null) {
            return;
        }

        playerFileModel.setVideoModel(videoModel);
        int resId = videoModel.hasSubPath1() ? R.string.subtitle_selector_subtitle_connected : R.string.subtitle_selector_subtitle_not_connected;
        binding.tvSubtitleSelector.setText(getString(resId));
    }

    private void initSearchVideoInformation() {
        final int movieId = playerFileModel.getVideoModel().getTmdbId();
        final String mediaType = playerFileModel.getVideoModel().getTmdbMediaType();
        final String jsonName = StorageUtil.generateTMDBJson(movieId, mediaType);
        final String jsonPath = StorageUtil.getTMDBPath(this, mediaType + "_" + movieId, jsonName);
        if (StorageUtil.isFileExist(jsonPath)) {
            String json = StorageUtil.readJsonFile(jsonPath);
            videoInformationModel = VideoInformationModel.parserVideoInformation(json, mediaType);
        }
        //Don't delete it. call updateUI() here first then get TMDB info and call updateUI() again later.
        //If I don't call updateUI() here first, I can see empty Video Information view until TMDB data is downloaded.
        //여기서 먼저 한번 updateUI()를 불러서 기본 UI를 먼저 그린다. TMDB데이타를 받아오면 updateUI() 다른곳에서 다시 호출해서 두번그린다. 안그러면 인터넷 느린곳에서는 TMDB데이타를 받아올때까지 빈 화면을 보여준다.
        updateUI();
        if (!playerFileModel.isHasTMDB() && playerFileModel.isSearchAuto()) {
            callAsyncTaskPlay(TYPE_GET_VIDEO_INFORMATION_FROM_WEB, false);
        }
    }

    @Override
    protected void checkSearchVideoInformationByFilename() {
        if (!isNetwork()) {
            return;
        }
        final String searchName = StringUtils.removeSpecial(FilenameUtils.getBaseName(playerFileModel.getName()));
        DLog.d(getLogTag(), "searchName=" + searchName);
        arraySearchName = searchName.split(" ");
        if (arraySearchName == null || arraySearchName.length <= 0) {
            getVideoInformation();
        } else {
            SeasonEpisodeInfo seasonEpisodeInfo = new SeasonEpisodeInfo(playerFileModel.getVideoModel().getName());
            searchVideoInformation(Normalizer.normalize(arraySearchName[searchCount], Normalizer.Form.NFC), seasonEpisodeInfo.getSeasonNumber(), seasonEpisodeInfo.getEpisodeNumber(), seasonEpisodeInfo.hasEpisode());
        }
    }

    private void searchVideoInformation(String keyword, int seasonNumber, int episodeNumber, boolean hasEpisode) {
        application.getDalAiImpl().searchMultiVideo(keyword, studyLanguage.getFormatOs(), new DalApiListener<SearchVideoModel>() {
            @Override
            public void onSuccess(SearchVideoModel response) {
                searchCount++;
                if (response == null || response.getResults() == null || response.getResults().isEmpty()) {
                    if ((hasEpisode)
                            && (playerFileModel.getVideoModel().getTmdbMediaType() != null)
                            && (playerFileModel.getVideoModel().isTVMediaType())) {
                        searchTVSeasonEpisodeInfo(seasonNumber, episodeNumber);
                    } else {
                        if (playerFileModel.isHasTMDB()) {
                            updateVideoModel(playerFileModel);
                        }
                        getVideoInformation();

                    }
                    return;
                }
                //if the video file has episode, then choose the MediaType is TV.
                List<VideoInformationModel> vModels = response.getResults();
                for (VideoInformationModel vModel : vModels) {
                    if (vModel.isMediaTypePerson()) {
                        continue;
                    }
                    playerFileModel.getVideoModel().setTmdbId(vModel.getId());
                    playerFileModel.getVideoModel().setTmdbMediaType(vModel.getMediaType());
                    playerFileModel.getVideoModel().setTmdbEpisodeOverview(vModel.getOverview());
                    playerFileModel.getVideoModel().setTmdbPosterPath(vModel.getPosterPath());
                    playerFileModel.getVideoModel().setTmdbKeyword(keyword);
                    if (hasEpisode == false) {
                        break;
                    } else if (vModel.isMediaTypeTV()) {
                        break;
                    }
                }

                // when arraySearchName size is 1
                if (searchCount >= arraySearchName.length) {
                    if (playerFileModel.isHasTMDB()) {
                        updateVideoModel(playerFileModel);
                    }
                    getVideoInformation();
                    return;
                }
                // call next search video
                searchVideoInformation(Normalizer.normalize(keyword + " " + arraySearchName[searchCount], Normalizer.Form.NFC), seasonNumber, episodeNumber, hasEpisode);
            }

            @Override
            public void onFailure(String error) {
                DLog.e(getLogTag(), "searchMultiVideo error=" + error);
                if (playerFileModel.isHasTMDB()) {
                    updateVideoModel(playerFileModel);
                    getVideoInformation();
                }
            }
        });
    }

    private void searchTVSeasonEpisodeInfo(int seasonNumber, int episodeNumber) {
        int tvID = playerFileModel.getVideoModel().getTmdbId();
        String id = tvID + "/" + Constant.PLAYER.THE_MOVIE_DB.KEY_SEASON + "/" + seasonNumber;
        application.getDalAiImpl().getVideoInformation(id, Constant.PLAYER.THE_MOVIE_DB.KEY_TV, studyLanguage.getFormatOs(), new DalApiListener<Object>() {
            @Override
            public void onSuccess(Object response) {
                String json = new GsonBuilder().create().toJson(response);
                VideoInformationModel season = VideoInformationModel.parserVideoInformation(json, Constant.PLAYER.THE_MOVIE_DB.KEY_TV);

                if (season.isHasEpisodes()) {
                    playerFileModel.getVideoModel().setTmdbSeasonId(season.getId());
                    playerFileModel.getVideoModel().setTmdbSeasonName(season.getName());
                    playerFileModel.getVideoModel().setTmdbSeasonPosterPath(season.getPosterPath());
                    playerFileModel.getVideoModel().setTmdbSeasonNumber(season.getSeasonNumber());
                    if (!(season.getOverview().equals("")))
                        playerFileModel.getVideoModel().appendTmdbEpisodeOverview(season.getOverview());
                    List<VideoInformationModel> episodes = season.getEpisodes();
                    if ((episodeNumber - 1) < episodes.size()) {
                        VideoInformationModel episode = episodes.get(episodeNumber - 1);
                        playerFileModel.getVideoModel().setTmdbEpisodeId(episode.getId());
                        playerFileModel.getVideoModel().setTmdbEpisodeName(episode.getName());
                        playerFileModel.getVideoModel().setTmdbEpisodePath(episode.getPath());
                        if (!(episode.getOverview().equals("")))
                            playerFileModel.getVideoModel().appendTmdbEpisodeOverview(episode.getOverview());
                        playerFileModel.getVideoModel().setTmdbEpisodeNumber(episode.getEpisodeNumber());
                    }
                    VideoSeasonModelQuery.addOrUpdate(Voca.getRealm(), playerFileModel);
                } else {

                    // If there is no episode under the season, then clear the TMDB meta data.
                    // Some video file has wrong file name(A tv has only Season 1, but the file name is Season 2)
                    playerFileModel.getVideoModel().clearTMDB();
                }

                updateVideoModel(playerFileModel);
                getVideoInformation();
            }

            @Override
            public void onFailure(String error) {
                DLog.e(getLogTag(), "getVideoInformation error=" + error);
                playerFileModel.getVideoModel().clearTMDB();
                updateVideoModel(playerFileModel);

                getVideoInformation();
            }
        });
    }

    private void getVideoInformation() {
        final int movieId = playerFileModel.getVideoModel().getTmdbId();
        if (!isNetwork() || movieId <= 0) {
            updateUI();
            return;
        }

        final String mediaType = playerFileModel.getVideoModel().getTmdbMediaType();
        final String jsonName = StorageUtil.generateTMDBJson(movieId, mediaType);
        final String jsonPath = StorageUtil.getTMDBPath(this, mediaType + "_" + movieId, jsonName);
        application.getDalAiImpl().getVideoInformation(String.valueOf(movieId), mediaType, studyLanguage.getFormatOs(), new DalApiListener<Object>() {
            @Override
            public void onSuccess(Object response) {
                String json = new GsonBuilder().create().toJson(response);
                videoInformationModel = VideoInformationModel.parserVideoInformation(json, mediaType);

                if (videoInformationModel.getId() > 0) {
                    StorageUtil.createFolder(StorageUtil.getTMDBPath(VideoInformationActivity.this, videoInformationModel));
                    StorageUtil.writeJsonFile(jsonPath, json);
                    if (Utils.isEmpty(playerFileModel.getVideoModel().getTmdbKeyword())) {
                        playerFileModel.getVideoModel().setTmdbKeyword(videoInformationModel.getName());
                    }
                    VideoSeasonModelQuery.updateVideoTitle(Voca.getRealm(), playerFileModel, getVideoTitleWithAlternativeTitlesToDisplay());
                    updateVideoModel(playerFileModel);
                }
                if (videoInformationModel.isMediaTypeTV() &&
                        playerFileModel.getVideoModel().getTmdbSeasonNumber() >= 0 &&
                        playerFileModel.getVideoModel().getTmdbEpisodeNumber() > 0) {
                    getCreditsFromTVType();
                } else {
                    appendMotherTongueOverview();
                }

            }

            @Override
            public void onFailure(String error) {
                DLog.e(getLogTag(), "getVideoInformation error=" + error);
//                updateUI();
            }
        });
    }

    private void appendMotherTongueOverview() {
        String id = String.valueOf(playerFileModel.getVideoModel().getTmdbId());
        String mediaType = playerFileModel.getVideoModel().getTmdbMediaType();
        application.getDalAiImpl().getVideoInformation(id, mediaType, motherTongueLanguage.getFormatOs(), new DalApiListener<Object>() {
            @Override
            public void onSuccess(Object response) {
                String json = new GsonBuilder().create().toJson(response);
                VideoInformationModel videoInformationModel = VideoInformationModel.parserVideoInformation(json, mediaType);
                String overviewMotherTongue = videoInformationModel.getOverview();

                playerFileModel.getVideoModel().setTmdbEpisodeOverviewMotherTongue(overviewMotherTongue);

                if (playerFileModel.getVideoModel().isTVMediaType()) {
                    appendMotherTongueOverviewSeasonAndEpisode();
                } else {
                    updateVideoModel(playerFileModel);
                    updateUI();
                }

            }

            @Override
            public void onFailure(String error) {
                DLog.e(getLogTag(), "getVideoInformation error=" + error);
            }
        });
    }

    private void appendMotherTongueOverviewSeasonAndEpisode() {
        String id = playerFileModel.getVideoModel().getTmdbId() + "/" + Constant.PLAYER.THE_MOVIE_DB.KEY_SEASON + "/" + playerFileModel.getVideoModel().getTmdbSeasonNumber();
        String mediaType = Constant.PLAYER.THE_MOVIE_DB.KEY_TV;
        application.getDalAiImpl().getVideoInformation(id, mediaType, motherTongueLanguage.getFormatOs(), new DalApiListener<Object>() {
            @Override
            public void onSuccess(Object response) {
                String json = new GsonBuilder().create().toJson(response);
                VideoInformationModel videoInformationModelSeason = VideoInformationModel.parserVideoInformation(json, mediaType);
                String seasonOverviewMotherTongue = videoInformationModelSeason.getOverview();

                if (!Utils.isEmpty(seasonOverviewMotherTongue))
                    playerFileModel.getVideoModel().appendTmdbEpisodeOverviewMotherTongue(seasonOverviewMotherTongue);

                if (videoInformationModelSeason.isHasEpisodes()) {
                    List<VideoInformationModel> episodes = videoInformationModelSeason.getEpisodes();
                    int episodeNumber = playerFileModel.getVideoModel().getTmdbEpisodeNumber();
                    if (((episodeNumber - 1) >= 0) && (episodeNumber - 1) < episodes.size()) {
                        VideoInformationModel videoInformationModelEpisode = episodes.get(episodeNumber - 1);
                        String episodeOverviewMotherTongue = videoInformationModelEpisode.getOverview();
                        if (!Utils.isEmpty(episodeOverviewMotherTongue))
                            playerFileModel.getVideoModel().appendTmdbEpisodeOverviewMotherTongue(episodeOverviewMotherTongue);
                    }

                }
                updateVideoModel(playerFileModel);
                updateUI();
            }

            @Override
            public void onFailure(String error) {
                DLog.e(getLogTag(), "getVideoInformation error=" + error);
            }
        });
    }

    @SuppressLint("StringFormatInvalid")
    protected void updateUI() {
        super.updateUI();
        if (isDestroyed() || isFinishing()) return;

        searchCount = 0;
        runOnUiThread(() -> {
            String nameDisplay = playerFileModel.getName();
            int percentageWatched = NumberUtil.percentageWatched(playerFileModel.getVideoModel().getLastDuration(), playerFileModel.getDuration());
            binding.pbWatchDuration.setVisibility( (percentageWatched > 0) && FileUtil.isValidVideoExtension(playerFileModel.getPath()) ? View.VISIBLE : View.GONE);
            binding.pbWatchDuration.setProgress(percentageWatched);

            if (videoInformationModel == null) {
                binding.llPoster.setVisibility(View.GONE);
                UtilImage.getThumbnailFromSavedVideoImageFile(this, binding.ivVideoBackdropImage, playerFileModel, false, null);
            } else {
                binding.llPoster.setVisibility(View.VISIBLE);
                updatePosterUI();
                updateTrailerUI();

                String imageName = playerFileModel.getVideoModel().getTmdbMediaType() + "_" + videoInformationModel.getId();
                String imagePath = null;

                if (!Utils.isEmpty(playerFileModel.getVideoModel().getTmdbEpisodePath())) {
                    imageName += "_" + Constant.PLAYER.THE_MOVIE_DB.BASE_FILE_SEASON + playerFileModel.getVideoModel().getTmdbSeasonNumber() + "_" + playerFileModel.getVideoModel().getTmdbSeasonId() + "_" + playerFileModel.getVideoModel().getTmdbEpisodeId();
                    imagePath = videoInformationModel.getImagePath_MediumSize(playerFileModel.getVideoModel().getTmdbEpisodePath());
                    imageName = StorageUtil.generateTMDBStill(imageName, imagePath);
                } else if (!Utils.isEmpty(videoInformationModel.getBackdropPath())) {
                    imagePath = videoInformationModel.getBackdropURL();
                    imageName = StorageUtil.generateTMDBBackdrop(imageName, imagePath);
                } else if (!Utils.isEmpty(videoInformationModel.getPosterPath())) {
                    imagePath = playerFileModel.getVideoModel().getTmdbMediaType() + "_" + videoInformationModel.getId();
                    imageName = videoInformationModel.getPosterURL();
                } else {
                    //Get the thumbnail from a video file.
                    imagePath = playerFileModel.getPath();
                }
                UtilImage.getThumbnailFullsize(application, binding.ivVideoBackdropImage, imagePath, null);
                updateSeasonPosterUI();
                updateSmallBackdropUI();
                UtilImage.getThumbnailFromSavedVideoImageFile(this, binding.ivVideoBackdropImage, playerFileModel, false, null);

                isUseTMDBImage = false;
                binding.tvTitleInfo.setText(getVideoTitleWithAlternativeTitlesToDisplay());
                binding.tvReleaseDate.setText((videoInformationModel.getDate() == null) ? "" : videoInformationModel.getDate());
                binding.tvDirector.setText(generateInformation(videoInformationModel.getDirector()));
                binding.tvWriter.setText(generateInformation(videoInformationModel.getWriter()));
                binding.tvCast.setText(generateInformation(videoInformationModel.getCredits().getCast(), Constant.PLAYER.THE_MOVIE_DB.MAX_CAST_DISPLAY));
                binding.tvGenre.setText(videoInformationModel.getGenre());

                if (playerFileModel.getVideoModel().isHasTmdbSeason()) {
                    binding.tvSeasons.setText(playerFileModel.getVideoModel().getTmdbSeasonName());
                    binding.tvEpisodes.setText(playerFileModel.getVideoModel().getTmdbEpisodeNumber() + " : " + playerFileModel.getVideoModel().getTmdbEpisodeName());
                }

                String overview = playerFileModel.getVideoModel().getTmdbEpisodeOverview() + "\n\n" + playerFileModel.getVideoModel().getTmdbEpisodeOverviewMotherTongue();
                binding.tvDescription.setText(overview);
            }

            ViewUtil.setVisibility(binding.tbTitle, binding.tvTitleInfo);
            ViewUtil.setVisibility(binding.tbReleaseDate, binding.tvReleaseDate);
            ViewUtil.setVisibility(binding.tbSeasons, binding.tvSeasons);
            ViewUtil.setVisibility(binding.tbEpisodes, binding.tvEpisodes);
            //Don't show Cast
//            ViewUtil.setVisibility(binding.tbDirector, binding.tvDirector);
//            ViewUtil.setVisibility(binding.tbWriter, binding.tvWriter);
//            ViewUtil.setVisibility(binding.tbCast, binding.tvCast);
            ViewUtil.setVisibility(binding.tbGenre, binding.tvGenre);
            ViewUtil.setVisibility(binding.llDescription, binding.tvDescription);
            castProfileAdapter.updateData(generateCastProfileData());
//            binding.tvCast.setVisibility(castProfileAdapter.getItemCount() > 0 ? View.VISIBLE : View.GONE);
            try {
                if (playerFileModel.isLocal()) {
                    FFmpegMediaMetadataRetriever mmr = new FFmpegMediaMetadataRetriever();
                    mmr.setDataSource(playerFileModel.getPath());
                    binding.tvResolution.setText(StorageUtil.getResolutionInfo(mmr));
                    binding.tvCodec.setText(StorageUtil.getCodecInfo(this, mmr));
                    ViewUtil.setVisibility(binding.tbCodec, binding.tvCodec);
                    if (StorageUtil.isRecommenedCodec(mmr)
                            && FileUtil.getVideoExtension(nameDisplay).equals(SupportVideoFormat.MP4)) {
                        binding.tvRecommendCodec.setVisibility(View.GONE);
                    } else {
                        binding.tvRecommendCodec.setText(R.string.recommend_codec);
                    }
                    mmr.release();
                } else {
                    MediaMetadataRetriever mmr = VideoUtil.getMediaMetadataRetrieverFromNetworkVideo(playerFileModel);
                    binding.tvResolution.setText(StorageUtil.getResolutionInfo(mmr));
                    VideoUtil.setVideoDurationFromMediaMetadataRetriever(playerFileModel.getVideoModel(), mmr);
                    mmr.release();
                }
                ViewUtil.setVisibility(binding.tbResolution, binding.tvResolution);
            } catch (Exception ex) {
                DLog.e(getLogTag(), ex.getMessage());
            }

            binding.tvVideoDuration.setText(playerFileModel.isDuration() ? TimeUtil.getDisplay(playerFileModel.getDuration()) : Constant.BASE_BLANK);
            makeVisibleWhenVideoHasPoster();
            runOnUiThread(this::updateSubtitleConnectButtonTitle);
//            updatePlayButtonIcon();
        });
    }

    protected void resetUI() {
        super.resetUI();
        binding.tvTitleInfo.setText(Constant.BASE_BLANK);
        binding.tvSeasons.setText(Constant.BASE_BLANK);
        binding.tvEpisodes.setText(Constant.BASE_BLANK);
        binding.tvDirector.setText(Constant.BASE_BLANK);
        binding.tvWriter.setText(Constant.BASE_BLANK);
        binding.tvCast.setText(Constant.BASE_BLANK);
        binding.tvGenre.setText(Constant.BASE_BLANK);
        binding.tvDescription.setText(Constant.BASE_BLANK);
        binding.tvReleaseDate.setText(Constant.BASE_BLANK);
    }

    private void makeVisibleWhenVideoHasPoster() {
        if (hasPoster()) {
            binding.llPoster.setVisibility(View.VISIBLE);
        } else {
            binding.llPoster.setVisibility(View.GONE);
        }

    }

    private boolean hasPoster() {
        return !Utils.isEmpty(playerFileModel.getVideoModel().getTmdbPosterPath()) || !Utils.isEmpty(playerFileModel.getVideoModel().getTmdbSeasonPosterPath());
    }

    private void updatePosterAndTrailerUI() {

    }

    private void updateTrailerUI() {
        try {
            binding.ivTrailer.setVisibility(View.GONE);
            binding.ivTrailerPlay.setVisibility(View.GONE);
            binding.tvTrailerText.setVisibility(View.GONE);
            if (!Utils.isEmpty(videoInformationModel.getTrailers().getTrailers().get(0).getKey())) {
                String trailerImagePath = "https://img.youtube.com/vi/" + videoInformationModel.getTrailers().getTrailers().get(0).getKey() + "/0.jpg";
                UtilImage.getThumbnail(application, binding.ivTrailer, 0, trailerImagePath, null);
                binding.ivTrailer.setVisibility(View.VISIBLE);
                binding.ivTrailerPlay.setVisibility(View.VISIBLE);
                binding.tvTrailerText.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            binding.ivTrailer.setVisibility(View.GONE);
            binding.ivTrailerPlay.setVisibility(View.GONE);
            binding.tvTrailerText.setVisibility(View.GONE);
        }
    }

    private void updateSmallBackdropUI() {
        binding.ivVideoBackdropImageSmall.setVisibility(View.GONE);
        String imagePath = "";

        if (!Utils.isEmpty(playerFileModel.getVideoModel().getTmdbEpisodePath())) {
            imagePath = playerFileModel.getVideoModel().getImagePathSmallSizeOnTmdbWebsite(playerFileModel.getVideoModel().getTmdbEpisodePath());
        } else if (!Utils.isEmpty(videoInformationModel.getBackdropPath())) {
            imagePath = videoInformationModel.getBackdropURL();
        }
        if (!Utils.isEmpty(imagePath)) {
            binding.ivVideoBackdropImageSmall.setVisibility(View.VISIBLE);
            UtilImage.getThumbnail(application, binding.ivVideoBackdropImageSmall, 0, imagePath, null);
        }
    }

    private void updatePosterUI() {
        binding.ivPoster.setVisibility(View.GONE);
        String imagePathPoster = playerFileModel.getVideoModel().getTmdbPosterPath();
        if (!Utils.isEmpty(imagePathPoster)) {
            binding.ivPoster.setVisibility(View.VISIBLE);
            UtilImage.getThumbnail(application, binding.ivPoster, 0, playerFileModel.getVideoModel().getImagePathSmallSizeOnTmdbWebsite(imagePathPoster), null);
        }
    }

    private void updateSeasonPosterUI() {
        binding.ivSeasonPoster.setVisibility(View.GONE);
        if (TmdbUtil.isDisplayTvSeasonPoster(playerFileModel.getVideoModel())) {
            binding.ivSeasonPoster.setVisibility(View.VISIBLE);
            String seasonImagePath = playerFileModel.getVideoModel().getTmdbSeasonPosterPath();
            UtilImage.getThumbnail(application, binding.ivSeasonPoster, 0, playerFileModel.getVideoModel().getImagePathSmallSizeOnTmdbWebsite(seasonImagePath), null);
        }
    }


    private String getVideoTitleWithAlternativeTitlesToDisplay() {
        Set<String> setTitle = new LinkedHashSet<>();
        setTitle.add(videoInformationModel.getAlternativeTitle(motherTongueLanguage.getFormatIso()));
        setTitle.add(videoInformationModel.getAlternativeTitle(studyLanguage.getFormatIso()));
        setTitle.add(videoInformationModel.getAlternativeTitle(EnumLanguage.ENGLISH.getFormatIso()));
        setTitle.add(videoInformationModel.getNameDisplay());
        setTitle.add(videoInformationModel.getOriginalNameDisplay());
        return setTitle.stream()
                .filter( e -> !Utils.isEmpty(e))
                .collect(Collectors.joining(" - "));
    }

    private void onBookmark() {
        if (playerFileModel.getVideoModel().isBookmark() == true) {
            binding.ivBookmark.setSelected(false);
            playerFileModel.getVideoModel().setBookmark(Constant.INT_BOOLEAN.FASLE);
        } else {
            binding.ivBookmark.setSelected(true);
            playerFileModel.getVideoModel().setBookmark(Constant.INT_BOOLEAN.TRUE);
        }
        updateVideoModel(playerFileModel);
    }
    private void onOpenTMDBWeb() {
        Utils.openWeb(this, Constant.PLAYER.THE_MOVIE_DB.WEB_SITE);
    }



    private void onHasSQLite(int type) {
        if (askToLogInByWatchVideoCount()) {
            alertDialog.showLogInRequired();
            return;
        }

        if (isHasSubRuby()) {
            openNotRatedOnlyWordsListPopupViewBeforePlayVideo();
//            openDalPlayer();
        } else {
            onHasSubtitleFile(type);
        }
    }

    private void onHasSubtitleFile(int type) {
        checkAndCreateVideoModel();

        if (openVideoWithoutAnalzing()) {
            openDalPlayer();
        } else {
            if (NetworkUtil.isNetworkConnetedIfNotShowWarningAsToast(this)) {
                callAsyncTaskPlay(type, true);
                showInterstitialAd();
            }
        }
    }

    private boolean openVideoWithoutAnalzing() {
        return Utils.isEmpty(playerFileModel.getSubPath1()) && !isHasSubRuby();
    }

    private void checkAndCreateVideoModel() {
//        VideoModel videoModel = VideoModelQuery.getByPath(Voca.getRealm(), playerFileModel.getPath());
//        if (videoModel == null) {
//            videoModel = new VideoModel(playerFileModel.getPath());
//        }
    }

    private void openDalPlayer() {
        //Don't delete this code. Sometimes openDalPlayer is never called again when Ads is related.
        if (isAnalyzing || isAdShowing) return;
//        closeInterstitialAd();
        if (!isVideoFromNetwork) {
            File f = new File(playerFileModel.getPath());
            if(!f.exists()) {
                ToastUtil.getInstance(this).show(R.string.file_is_not_existed);
                return;
            }
        }

        increaseCountOfWatchVideo();
        DLog.d(getLogTag(), "openDalPlayer");
        isReload = true;
        playerFileModel.getVideoModel().setNewFile(Constant.INT_BOOLEAN.FASLE);
        updateVideoModel(playerFileModel);
        eventBus.post(new SuccessEvent(BaseEvent.Screen.MAIN, BaseEvent.EventType.PLAYER_RELOAD_VIDEO, playerFileModel.getVideoModel()));
        startPlayer();
    }

    private void increaseCountOfWatchVideo() {
        int countOfWatchVideo = sharedPreferences.getCountOfWatchVideo();
        sharedPreferences.setCountOfWatchVideo(countOfWatchVideo + 1);
    }

    private boolean askToLogInByWatchVideoCount() {
        if (isLoggedIn())
            return false;

        int countOfWatchVideo = sharedPreferences.getCountOfWatchVideo();
        int maxCountOfWatchVideoToShowLogInPopup = 10;
        if (countOfWatchVideo >= maxCountOfWatchVideoToShowLogInPopup) {
            return Utils.isDebugOrAdminUser(this) ? false : true;
        }

        return false;
    }

    private void startPlayer() {
        Intent intent = new Intent(this, PlayerActivity.class);
        intent.putExtra(Constant.PLAYER.INTENT.KEY_VIDEO_FILE, playerFileModel);
        Loading.hide();
        startActivity(intent);
    }

    private void callAsyncTaskPlay(int type, boolean isLoading) {
        callAsyncTask(this, type, isLoading);
    }

    private OnYesNoClickListener onConfirmDownloadNetwork = new OnYesNoClickListener() {
        @Override
        public void onYesClick(View view, Object object) {
            checkDownloadFile((PlayerFileModel) object);
        }

        @Override
        public void onNoClick(View view, Object object) {

        }
    };

    private void checkDownloadFile(PlayerFileModel data) {
        DLog.d(getLogTag(), "checkDownloadFile - file=" + data.toString());
        File localFile = new File(StorageUtil.getVideoPath(this), data.getPath());
        final DownloadModel downloadModel = DownloadModelQuery.getById(Voca.getRealm(), data.getPath());
        if (downloadModel != null || localFile.length() > 0) {
            showDownloadWarningSameFileDialog(data);
        } else {
            downloadFile(data);
        }
    }

    private void showDownloadWarningSameFileDialog(Object data) {
        final YesNoDialog dialog = new YesNoDialog(this,
                R.string.warning,
                R.string.msg_download_overwrite_same_file,
                data,
                onDownloadWarningSameFileListener);
        dialog.show();
    }

    private OnYesNoClickListener onDownloadWarningSameFileListener = new OnYesNoClickListener() {
        @Override
        public void onYesClick(View view, Object object) {
            downloadFile(object);
        }

        @Override
        public void onNoClick(View view, Object object) {
        }
    };

    private void downloadFile(Object data) {
        final PlayerFileModel file = (PlayerFileModel) data;
        final DownloadModel checkSameModel = DownloadModelQuery.getById(Voca.getRealm(), file.getPath());
        // set currentSize is 0 when downloadModel is exist and isOverwrite is true
        if (checkSameModel != null) {
            if (!checkSameModel.isCompleted()) {
                checkSameModel.setStatus(Constant.PLAYER.SERVER.DOWNLOAD.STATUS.WAIT);
                DownloadModelQuery.update(Voca.getRealm(), checkSameModel);
//                addDownload(checkSameModel);
            }
            return;
        }
        final DownloadModel model = new DownloadModel();
        model.setId(file.getPath());
        model.setName(file.getName());
        model.setPath(file.getPath());
        model.setIdServer(playerFileModel.getServerModel().getId());
        model.setSize(file.getSize());
        DownloadModelQuery.add(Voca.getRealm(), model);
//        addDownload(model);
    }

//    private Object parserSubtitleFile(int type) {
//        if (type == TYPE_PARSE_SUBTITLE) {
//            return SubtitleUtil.parserContentSubTitle(playerFileModel,  Constant.PLAYER.INTENT.SUBPATH_INDEX_1); //playerFileModel.getVideoModel().getSubPathIndex());
//        }
//        return new File(StorageUtil.generateSubtitleSQLitePathUnderAndroidFolder(this, playerFileModel.getPath(), playerFileModel));
//    }

    private void makeRubyTextFromServer(Object resultData) {
        if (resultData == null) {
            isAnalyzing = false;
            openDalPlayer();
            return;
        }
        //Don't check the subtitle extension, just call the makeRubyTextFromSubtitle API
        makeRubyTextFromSubtitle(resultData);
    }

    private void makeRubyTextFromSubtitle(Object content) {
        ((AraPlayerApplication) application).getAraPlayerApiImpl().makeRubyTextFromSubtitle(this,
                playerFileModel,
                content,
                new DalApiListener<ResponseBody>() {
            @Override
            public void onSuccess(ResponseBody response) {
                downloadAndUnzipFile(response);
            }

            @Override
            public void onFailure(String error) {
                parserSubtitleError();
            }
        });
    }

    private void downloadAndUnzipFile(ResponseBody response) {
        if (response == null) {
            parserSubtitleError();
            return;
        }
        File file = StorageUtil.writeResponseBodyToDisk(this, response);
        String subtitleDatabasePath = StorageUtil.generateSubtitleSQLitePathUnderAndroidFolder(this, playerFileModel.getPath(), playerFileModel);
        String path = StorageUtil.getFilesStoragePath(this, playerFileModel);
        StorageUtil.unzip(this, file, new File(path), subtitleDatabasePath);
        // check sub database
        createSubDatabase(playerFileModel);
        checkAndCreateVideoModel();
        if (playerFileModel.getVideoModel().getTongueLang() != motherTongueLanguage.getIdApi()) {
            playerFileModel.getVideoModel().setTongueLang(motherTongueLanguage.getIdApi());
            updateVideoModel(playerFileModel);
        }
        if (isHasSubRuby()) {
            playerFileModel.getVideoModel().setAnalyzeAgain(Constant.INT_BOOLEAN.FASLE); //Not to display analyze again warning text message
            updateVideoModel(playerFileModel);
            // check translator subtitle when no translation automatically & study language is EN & translator subtitle is ON
            if (sharedPreferences.getTranslateSubtitleFromServer()) {
                ArrayList<DicModel> list = new ArrayList<>();
                list.addAll(getSubDatabase().getNoTranslationSubtitleDialogListByLanguage());
                if (Utils.isEmptyCollection(list)) {
                    openNotRatedOnlyWordsListPopupViewBeforePlayVideo();
                } else {
                    CustomTranslate customTranslate = new CustomTranslate(this);
                    for (DicModel item : list) {
                        customTranslate.translateText(item.getVocaDisplay(), (view, object) -> {
                            DLog.d(getLogTag(), item.getId() + " - " + item.getVocaDisplay() + " translate to =" + object);
                            item.setMeaning((String) object);
                            getSubDatabase().updateTranslate(item);
                            if (item == list.get(list.size() - 1)) {
                                openNotRatedOnlyWordsListPopupViewBeforePlayVideo();
                            }
                        });
                    }
                }
            } else {
                openNotRatedOnlyWordsListPopupViewBeforePlayVideo();
            }
        } else {
            ToastUtil.getInstance(this).show(R.string.error_msg_parser_sub_database_title);
            Loading.hide();
        }
    }

    private void parserSubtitleError() {
        ToastUtil.getInstance(this).show(R.string.error_msg_parser_sub_title);
        Loading.hide();
    }

//    private void openVideoInformationMenuDialog() {
//        final PlayerVideoInformationMenuDialog dialog = new PlayerVideoInformationMenuDialog(this, playerFileModel, videoInformationModel, (view, object) -> {
//            switch (view.getId()) {
//                case R.id.tv_choose_metadata:
//                    openVideoSearchScreen();
//                    break;
//                case R.id.tv_clear_metadata:
//                    onClearMetadata();
//                    break;
//                case R.id.tv_word_list:
//                    openWordsList();
//                    break;
//                case R.id.tv_subtitle_list:
//                    openDialogList();
//                    break;
//                case R.id.llEditSubtitle:
//                    openEditSubtitle();
//                    break;
//                case R.id.tv_bookmark_list:
//                    openBookmarkList();
//                    break;
//                case R.id.tv_thumbnail_list:
//                    openThumbnailListScreen();
//                    break;
//                case R.id.tv_quiz:
//                    openQuizPlayerScreenWithLocalData();
//                    break;
//                case R.id.ll_memo:
//                    editDialogType = EditDialogType.MEMO;
//                    showTypeInputDialog();
//                    break;
//                case R.id.llTtsTitle:
//                    editDialogType = EditDialogType.TTS_TITLE;
//                    showTypeInputDialog();
//                    break;
//                case R.id.llDisplayTitle:
//                    editDialogType = EditDialogType.DISPLAY_TITLE;
//                    showTypeInputDialog();
//                    break;
//                case R.id.llTtsArtist:
//                    editDialogType = EditDialogType.TTS_ARTIST;
//                    showTypeInputDialog();
//                    break;
//                case R.id.llArtist:
//                    editDialogType = EditDialogType.ARTIST;
//                    showTypeInputDialog();
//                    break;
//                case R.id.llAlbum:
//                    editDialogType = EditDialogType.ALBUM;
//                    showTypeInputDialog();
//                    break;
//                case R.id.tv_option:
//                    openOptionPlayer();
//                    break;
//            }
//        });
//        dialog.show();
//    }

    private void openVideoSearchScreen() {
        Intent intent = new Intent(this, VideoSearchActivity.class);
        intent.putExtra(Constant.PLAYER.INTENT.KEY_VIDEO_FILE, playerFileModel);
        startActivityForResult(intent, REQUEST_SEARCH_VIDEO);
    }

    private void openVideoCreditsScreen(VideoInformationModel data) {
        //Will delete VideoCreditsActivity later.
//        Intent intent = new Intent(this, VideoCreditsActivity.class);
//        intent.putExtra(Constant.PLAYER.INTENT.KEY_DATA, data);
//        startActivity(intent);
    }

    private String generateInformation(List<VideoInformationModel.People> items) {
        return generateInformation(items, -1);
    }

    private String generateInformation(List<VideoInformationModel.People> items, int maxCount) {
        int count = 0;
        String content = Constant.BASE_BLANK;
        for (VideoInformationModel.People p : items) {
            content = StringUtils.addString(content, p.getName());
            count++;
            if (maxCount > 0 && count >= maxCount)
                break;
        }
        return content;
    }

    private void openOptionPlayer() {
        isReload = true;
        Intent intent = new Intent(this, OptionPlayerActivity.class);
        intent.putExtra(Constant.PLAYER.INTENT.KEY_VIDEO_FILE, playerFileModel);
        startActivityForResult(intent, REQUEST_OPTION_SCREEN);
    }

    private void openWordsList() {
        openWordsList(true);
    }

    private void openWordsList(boolean isWordList) {
        openNewScreen(
                WordListPlayerActivity.createIntent(this, playerFileModel)
        );
    }

    private void openDialogList() {
        Intent intent = new Intent(this, DialogueListPlayerActivity.class);
        intent.putExtra(Constant.PLAYER.INTENT.KEY_VIDEO_FILE, playerFileModel);
        startActivity(intent);
    }

    private void openBookmarkList() {
        Intent intent = new Intent(this, BookmarkListActivity.class);
        intent.putExtra(Constant.PLAYER.INTENT.KEY_VIDEO_FILE, playerFileModel);
        startActivity(intent);
    }

    private void openThumbnailListScreen() {
        Intent intent = new Intent(this, ThumbnailListPlayerActivity.class);
        intent.putExtra(Constant.PLAYER.INTENT.KEY_VIDEO_FILE, playerFileModel);
        startActivityForResult(intent, REQUEST_THUMBNAIL_LIST);
    }

    private List<VideoInformationModel.People> generateCastProfileData() {
        final List<VideoInformationModel.People> list = new ArrayList<>();
        if (videoInformationModel != null) {
            if (!videoInformationModel.getCredits().getCast().isEmpty()) {
                list.addAll(videoInformationModel.getCredits().getCast());
            }
            if (!videoInformationModel.getCredits().getGuestStars().isEmpty()) {
                list.addAll(videoInformationModel.getCredits().getGuestStars());
            }
        }
        return list;
    }

    private OnClickListener onCastProfileClickListener = (view, object) -> {
        final VideoInformationModel.People item = (VideoInformationModel.People) object;
        switch (view.getId()) {
            case R.id.izb_profile:
                if (!Utils.isEmpty(item.getPath())) {
//                    binding.ivZoomedPhoto.setVisibility(View.VISIBLE);
//                    UtilImage.getThumbnailFullsize(this, binding.ivZoomedPhoto, item.getOriginalSizeImagePath());
                    showThumbnailFromPath(item.getOriginalSizeImagePath(), ((ImageView) view).getDrawable());
                }
                break;
            case R.id.ll_cast_name:
            case R.id.tv_real_name:
            case R.id.tv_character_name:
                Utils.openWeb(this, item.getPersonURL());
                break;
        }
    };

    private void onClearMetadata() {
        // delete files
        try {
            FileUtils.deleteDirectory(new File(StorageUtil.getTMDBPath(this, videoInformationModel)));
        } catch (IOException e) {
            e.printStackTrace();
        }
        playerFileModel.getVideoModel().clearTMDB();
        updateVideoModel(playerFileModel);
        videoInformationModel = null;
        updateUI();
    }

    private void openEditSubtitle() {
        final List<DicModel> subtitleDialogModels = getSubDatabase().getSubtitleDialogList();
        String ids = subtitleDialogModels.stream().map(e -> e.getSubtitleWordlistId()).collect(Collectors.joining(","));
        Intent intent = new Intent(this, EditSubtitleActivity.class);
        intent.putExtra(Constant.PLAYER.INTENT.KEY_VIDEO_FILE, playerFileModel);
        if (ids.length() > 0) {
            intent.putExtra(Constant.PLAYER.INTENT.KEY_DATA, ids);
        }
        startActivity(intent);
    }

    private void getCreditsFromTVType() {
        String url = Constant.PLAYER.THE_MOVIE_DB.SEARCH_SEASON_URL + videoInformationModel.getId() + "/" +
                Constant.PLAYER.THE_MOVIE_DB.KEY_SEASON + "/" + playerFileModel.getVideoModel().getTmdbSeasonNumber() + "/" +
                Constant.PLAYER.THE_MOVIE_DB.KEY_EPISODE + "/" + playerFileModel.getVideoModel().getTmdbEpisodeNumber() + "/" +
                Constant.PLAYER.THE_MOVIE_DB.APPEND_CREDITS;
        application.getDalAiImpl().getCredits(url, new DalApiListener<VideoInformationModel.Credits>() {
            @Override
            public void onSuccess(VideoInformationModel.Credits response) {
                videoInformationModel.setCredits(response);
                String json = new GsonBuilder().create().toJson(videoInformationModel);
                final int movieId = playerFileModel.getVideoModel().getTmdbId();
                final String mediaType = playerFileModel.getVideoModel().getTmdbMediaType();
                final String jsonPath = StorageUtil.getTMDBPath(VideoInformationActivity.this, mediaType + "_" + movieId, StorageUtil.generateTMDBJson(movieId, mediaType));
                StorageUtil.writeJsonFile(jsonPath, json);
                appendMotherTongueOverview();
            }

            @Override
            public void onFailure(String error) {
                appendMotherTongueOverview();
            }
        });
    }


    private void openNotRatedOnlyWordsListPopupViewBeforePlayVideo() {
        isAnalyzing = false;
        vocaStudyChatListNotRatedOnly = this.getSubDatabase().getVocaStudyChatListNotRatedOnly();
        if (Utils.isEmptyCollection(vocaStudyChatListNotRatedOnly)) {
            openDalPlayer();
        } else {
            recyclerWordListViewDialog = new RecyclerViewDialog(this, (view, object) -> {
                if (Utils.isEmptyCollection(vocaStudyChatListNotRatedOnly)) {
                    openDalPlayer();
                } else {
                    Loading.hide();
                    updateUI();
                }
            });
            studyChatAdapter = new StudyChatAdapter(this);

            studyChatAdapter.setDataNoLoop(vocaStudyChatListNotRatedOnly);
            setRecyclerViewDataDialog(getString(R.string.title_not_rated_words_only_in_subtitle), studyChatAdapter, null);
        }
    }

    private void setRecyclerViewDataDialog(String title, StudyChatAdapter adapter, OnClickListener callback) {
        adapter.setDisplayPronunciation(sharedPreferences.getDisplayPronunciation());
        adapter.setHighlightIndex(0);
        adapter.setHasHeader(true);
        adapter.setNotRatedOnlyWordsList(true);
        adapter.setListener(onVocaStudyChatClickListener);
        adapter.setDoubleClickListener(onVocaStudyChatDoubleClickListener);

        recyclerWordListViewDialog.setCanceledOnTouchOutside(false);
        recyclerWordListViewDialog.showCloseButton(false);
        recyclerWordListViewDialog.setAdapter(adapter, title);
        recyclerWordListViewDialog.show();
    }

    private OnVocaStudyChatClickListener onVocaStudyChatClickListener = new OnVocaStudyChatClickListener() {

        @Override
        public void onItemClick(VocaStudyChat voca) {

        }

        @Override
        public void onDoubleItemClick(VocaStudyChat voca) {
            removeVocaInList(voca);
        }

        @Override
        public void onPlayClick(final VocaStudyChat voca) {

        }

        @Override
        public void onBigIconClick(VocaStudyChat voca) {

        }

        @Override
        public void onAsteriskSentenceClick(final VocaStudyChat voca) {
        }

        @Override
        public void onVocaKnowClick(VocaStudyChat voca, int vocaKnow) {
            removeVocaInList(voca);
        }

        @Override
        public void onEvaluateGradeClick(VocaStudyChat voca, String grade) {

        }

        @Override
        public void onAnswerClick(VocaStudyChatExam voca) {

        }
    };

    private OnDoubleClickListener onVocaStudyChatDoubleClickListener = new OnDoubleClickListener() {
        @Override
        public void onClick(View view, Object data) {

        }

        @Override
        public void onDoubleClick(View view, Object data) {
            if (data instanceof VocaStudyChat) {
                VocaStudyChat voca = (VocaStudyChat) data;
//                onChangeVocaKnow(voca, null, VocaKnow.switchVocaKnow(voca.getVocaKnow()), true);
                removeVocaInList(voca);
            }
        }
    };

    private void removeVocaInList(VocaStudyChat voca) {
        vocaStudyChatListNotRatedOnly.remove(voca);
        if (Utils.isEmptyCollection(vocaStudyChatListNotRatedOnly)) {
            recyclerWordListViewDialog.dismiss();
        } else {
            studyChatAdapter.setDataNoLoop(vocaStudyChatListNotRatedOnly);
            studyChatAdapter.notifyDataSetChanged();
        }
    }

    private void showInterstitialAd() {
        //TODO : Don't delete this code. Will use later
//        Integer maxCountOfAnalyzeVideoToDisplayAds = 10;
//        Integer countOfAnalyzeVideo = sharedPreferences.getCountOfAnalyzeVideo();
//        if (countOfAnalyzeVideo > maxCountOfAnalyzeVideoToDisplayAds) {
//            isAdShowing = true;
//            if (mInterstitialAd.isLoaded()) {
//                mInterstitialAd.show();
//                adHandler.sendEmptyMessageDelayed(INTERSTITIAL_AD_AUTO_CLOSE_MESSAGE, INTERSTITIAL_AD_AUTO_CLOSE_TIME);
//            } else if (!mInterstitialAd.isLoaded() && !mInterstitialAd.isLoading()) {
//                mInterstitialAd.loadAd(new AdRequest.Builder().build());
//            }
//        } else {
//            sharedPreferences.setCountOfAnalyzeVideo(countOfAnalyzeVideo + 1);
//        }
    }

//    private void initInterstitialAd() {
//        mInterstitialAd = new InterstitialAd(this);
//        mInterstitialAd.setAdUnitId(MobileAd.getInterstateId(this));
//        mInterstitialAd.loadAd(new AdRequest.Builder().build());
//        mInterstitialAd.setAdListener(new AdListener() {
//            @Override
//            public void onAdLoaded() {
//                super.onAdLoaded();
//                if (isAnalyzing) {
//                    showInterstitialAd();
//                }
//            }
//
//            @Override
//            public void onAdClosed() {
//                super.onAdClosed();
//                isAdShowing = false;
//                adHandler.removeMessages(INTERSTITIAL_AD_AUTO_CLOSE_MESSAGE);
//            }
//        });
//
//        adHandler = new Handler(getMainLooper()) {
//            @Override
//            public void handleMessage(@NonNull Message msg) {
//                super.handleMessage(msg);
//                if (msg.what == INTERSTITIAL_AD_AUTO_CLOSE_MESSAGE) {
//                    isAdShowing = false;
//                    if (!isAnalyzing) {
//                        closeInterstitialAd();
//                        openDalPlayer();
//                    }
//                }
//            }
//        };
//    }

//    private void closeInterstitialAd() {
//        Activity currentActivity = BaseApplication.getInstance().getCurrentActivity();
//        if (currentActivity instanceof AdActivity) {
//            isAdShowing = false;
//            currentActivity.finish();
//            mInterstitialAd.loadAd(new AdRequest.Builder().build());
//        }
//    }

    private void initOnClickListener() {
        binding.ivTmdbLogo.setOnClickListener(this);
        binding.tlInformation.setOnClickListener(this);
        binding.tvCast.setOnClickListener(this);
        binding.ivTrailer.setOnClickListener(this);
        binding.ivPoster.setOnClickListener(this);
        binding.ivSeasonPoster.setOnClickListener(this);
        binding.ivVideoBackdropImageSmall.setOnClickListener(this);
        binding.llSubtitleSelector.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.iv_tmdb_logo:
                onOpenTMDBWeb();
                break;
            case R.id.tl_information:
                if (videoInformationModel != null)
                    Utils.openWeb(this, Constant.PLAYER.THE_MOVIE_DB.WEB_SITE + videoInformationModel.getMediaType() + "/" + playerFileModel.getVideoModel().getTmdbId());
                break;
            case R.id.tvCast:
                openVideoCreditsScreen(videoInformationModel);
                break;
            case R.id.iv_trailer:
                if (!Utils.isEmpty(videoInformationModel.getTrailer()))
                    Utils.openWeb(this, videoInformationModel.getTrailer());
                break;
            case R.id.iv_poster:
//                binding.ivZoomedPhoto.setVisibility(View.VISIBLE);
                String imagePathPoster = playerFileModel.getVideoModel().getTmdbPosterPath();
//                UtilImage.getThumbnailFullsize(this, binding.ivZoomedPhoto, playerFileModel.getVideoModel().getImagePathOriginalSizeOnTmdbWebsite(imagePathPoster));
                showThumbnailFromPath(playerFileModel.getVideoModel().getImagePathOriginalSizeOnTmdbWebsite(imagePathPoster), binding.ivPoster.getDrawable());
                break;
            case R.id.iv_season_poster:
//                binding.ivZoomedPhoto.setVisibility(View.VISIBLE);
                String seasonImagePath = playerFileModel.getVideoModel().getTmdbSeasonPosterPath();
//                UtilImage.getThumbnailFullsize(this, binding.ivZoomedPhoto, playerFileModel.getVideoModel().getImagePathOriginalSizeOnTmdbWebsite(seasonImagePath));
                showThumbnailFromPath(playerFileModel.getVideoModel().getImagePathOriginalSizeOnTmdbWebsite(seasonImagePath), binding.ivSeasonPoster.getDrawable());
                break;
            case R.id.iv_video_backdrop_image_small:
                showZoomImageOfBackDrop();
                break;
            case R.id.llSubtitleSelector:
                subtitleConnectHelper.setSubPathIndex(subPathIndex);
                subtitleConnectHelper.setPlayerFileModel(playerFileModel);
                subtitleConnectHelper.showSubtitleOptionsDialog();
                isReload = true;
                break;
        }
    }
//    @OnClick({R.id.iv_tmdb_logo, R.id.iv_trailer, R.id.iv_poster, R.id.iv_season_poster, R.id.iv_video_backdrop_image_small,
//            R.id.tl_information, R.id.tvCast, R.id.llSubtitleSelector
//    })
//    void onClick(View view) {
//        super.onClick(view);
//        switch (view.getId()) {
//            case R.id.iv_tmdb_logo:
//                onOpenTMDBWeb();
//                break;
//            case R.id.tl_information:
//                if (videoInformationModel != null)
//                    Utils.openWeb(this, Constant.PLAYER.THE_MOVIE_DB.WEB_SITE + videoInformationModel.getMediaType() + "/" + playerFileModel.getVideoModel().getTmdbId());
//                break;
//            case R.id.tvCast:
//                openVideoCreditsScreen(videoInformationModel);
//                break;
//            case R.id.iv_trailer:
//                if (!Utils.isEmpty(videoInformationModel.getTrailer()))
//                    Utils.openWeb(this, videoInformationModel.getTrailer());
//                break;
//            case R.id.iv_poster:
////                binding.ivZoomedPhoto.setVisibility(View.VISIBLE);
//                String imagePathPoster = playerFileModel.getVideoModel().getTmdbPosterPath();
////                UtilImage.getThumbnailFullsize(this, binding.ivZoomedPhoto, playerFileModel.getVideoModel().getImagePathOriginalSizeOnTmdbWebsite(imagePathPoster));
//                showThumbnailFromPath(playerFileModel.getVideoModel().getImagePathOriginalSizeOnTmdbWebsite(imagePathPoster), binding.ivPoster.getDrawable());
//                break;
//            case R.id.iv_season_poster:
////                binding.ivZoomedPhoto.setVisibility(View.VISIBLE);
//                String seasonImagePath = playerFileModel.getVideoModel().getTmdbSeasonPosterPath();
////                UtilImage.getThumbnailFullsize(this, binding.ivZoomedPhoto, playerFileModel.getVideoModel().getImagePathOriginalSizeOnTmdbWebsite(seasonImagePath));
//                showThumbnailFromPath(playerFileModel.getVideoModel().getImagePathOriginalSizeOnTmdbWebsite(seasonImagePath), binding.ivSeasonPoster.getDrawable());
//                break;
//            case R.id.iv_video_backdrop_image_small:
//                showZoomImageOfBackDrop();
//                break;
//            case R.id.llSubtitleSelector:
//                subtitleConnectHelper.setSubPathIndex(subPathIndex);
//                subtitleConnectHelper.setPlayerFileModel(playerFileModel);
//                subtitleConnectHelper.showSubtitleOptionsDialog();
//                isReload = true;
//                break;
//        }
//    }

    @Override
    public void onDetachSubtitleFile(boolean canDetach) {
        if (canDetach) {
            resetSubtitleFileAndDatabase(playerFileModel, "", Constant.PLAYER.INTENT.SUBPATH_INDEX_1);
            resetSubtitleFileAndDatabase(playerFileModel, "", Constant.PLAYER.INTENT.SUBPATH_INDEX_2);
            updateUI();
        }
    }
}
