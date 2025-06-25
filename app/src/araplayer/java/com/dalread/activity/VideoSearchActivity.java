package com.dalread.activity;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dalread.R;
import com.dalread.adapter.VideoSearchAdapter;
import com.dalread.base.BasePlayerActivity;
import com.dalread.component.CenterLayoutManager;
import com.dalread.component.SeparatorDecoration;
import com.dalread.component.Toolbar;
import com.dalread.databinding.ActivityVideoSearchBinding;
import com.dalread.dialog.ZoomedPhotoDialog;
import com.dalread.listener.OnClickListener;
import com.dalread.model.SearchVideoModel;
import com.dalread.model.VideoInformationModel;
import com.dalread.network.DalApiListener;
import com.dalread.util.BaseBindUtils;
import com.dalread.util.Constant;
import com.dalread.util.DLog;
import com.dalread.util.Loading;
import com.dalread.util.StringUtils;
import com.dalread.util.Utils;
import com.google.gson.GsonBuilder;

import org.apache.commons.io.FilenameUtils;

import java.util.Stack;

public class VideoSearchActivity extends BasePlayerActivity {
    private VideoSearchAdapter adapter;
    private String searchValue;
    private SearchVideoModel searchVideoModel;

    private boolean loading = true;
    int pastVisibleItems, visibleItemCount, totalItemCount;
    private Stack<String> stackSearch = new Stack<>();
    private ActivityVideoSearchBinding binding;

    @Override
    protected View getContentView() {
        binding = ActivityVideoSearchBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        return view;
    }

    @Override
    protected Toolbar getToolbar() {
        return binding.header;
    }

    @Override
    protected void setFullscreen() {

    }

    @Override
    public void initView() {
//        studyLangToGetTMDBInfo = EnumLanguage.findByFormatApi(sharedPreferences.getStudyLanguage()).getFormatOs();
        playerFileModel = getIntent().getParcelableExtra(Constant.PLAYER.INTENT.KEY_VIDEO_FILE);
        if (Utils.isEmpty(playerFileModel.getVideoModel().getTmdbKeyword())) {
            searchValue = StringUtils.removeSpecial(FilenameUtils.getBaseName(playerFileModel.getName()));//FilenameUtils.getBaseName(playerFileModel.getName());

        } else {
            searchValue = playerFileModel.getVideoModel().getTmdbKeyword();
        }
        binding.tvVideoFileName.setText(playerFileModel.getName());
        searchVideoModel = new SearchVideoModel();
        adapter = new VideoSearchAdapter(this, onVideoSearchOnClickListener);
        binding.rvList.setAdapter(adapter);
        LinearLayoutManager mLayoutManager = new CenterLayoutManager(this);
        binding.rvList.setLayoutManager(mLayoutManager);
        binding.rvList.addItemDecoration(new SeparatorDecoration(this, BaseBindUtils.getDividerColor(), BaseBindUtils.getDividerHeight(this)));
        binding.rvList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                visibleItemCount = mLayoutManager.getChildCount();
                totalItemCount = mLayoutManager.getItemCount();
                pastVisibleItems = mLayoutManager.findFirstVisibleItemPosition();

                boolean isLoad = searchVideoModel.getTotalPages() > 0 && searchVideoModel.getPage() + 1 <= searchVideoModel.getTotalPages();

                if (loading) {
                    if (isLoad && (visibleItemCount + pastVisibleItems) >= totalItemCount) {
                        loading = false;
                        searchVideoInformation(true);
                    }
                }
            }
        });

        binding.header.showSearchView();
        binding.header.getViewSearch().onActionViewExpanded();
        binding.header.setSearchListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                searchValue = s;
                DLog.d(getLogTag(), "onQueryTextChange - searchValue=" + searchValue);
                searchVideoInformation();
                return true;
            }
        }, () -> {
            searchValue = Constant.BASE_BLANK;
            DLog.d(getLogTag(), "onQueryTextChange - clear=" + searchValue);
            binding.header.getViewSearch().setQuery(searchValue, false);
            searchVideoInformation();
            return true;
        });
        binding.header.getViewSearch().post(() -> {
            binding.header.getViewSearch().setQuery(searchValue, true);
        });
    }

    @Override
    public void initData() {

    }

    @Override
    public void onHeaderLeftClick() {
        checkOnBackPressed();
    }
    @Override
    public void onHeaderLeft2Click() {
    }
    @Override
    public void onHeaderRightClick() {

    }

    @Override
    public void onHeaderIconRightClick() {

    }

    @Override
    public void onHeaderTextRightClick() {

    }

    @Override
    public void onBackPressed() {
        checkOnBackPressed();
    }

    private void checkOnBackPressed() {
        if (stackSearch.empty()) {
            onFinishActivity(RESULT_CANCELED);
            return;
        }
        if (stackSearch.size() == 1) {
            Loading.show(this);
            searchVideoInformation();
        } else {
            getVideoInformation(stackSearch.pop(), playerFileModel.getVideoModel().getTmdbMediaType());
        }
    }

    private void onFinishActivity(int resultValue) {
        Intent intent = getIntent();
        setResult(resultValue, intent);
        finish();
    }

    private OnClickListener onVideoSearchOnClickListener = (view, object) -> {
        final VideoInformationModel item = (VideoInformationModel) object;
        if (view.getId() == R.id.iv_image) {
            ZoomedPhotoDialog zoomedPhotoDialog = new ZoomedPhotoDialog(this, playerFileModel);
            zoomedPhotoDialog.loadThumbnailFromPath(item.getOriginalSizePosterURL(), ((ImageView) view).getDrawable());
        } else {

            if (item.isMediaTypeTV()) {
                if (adapter.isEpisode()) {
                    playerFileModel.getVideoModel().setTmdbEpisodeId(item.getId());
                    playerFileModel.getVideoModel().setTmdbEpisodeName(item.getName());
                    playerFileModel.getVideoModel().setTmdbEpisodePath(item.getPath());
                    playerFileModel.getVideoModel().setTmdbEpisodeNumber(item.getEpisodeNumber());
                    if (!(item.getOverview().equals("")))
                        playerFileModel.getVideoModel().appendTmdbEpisodeOverview(item.getOverview());

                    updateVideoModel(playerFileModel.getVideoModel());
                    onFinishActivity(RESULT_OK);
                    return;
                }
                Utils.hideSoftKeyboard(this, binding.header);
                String seasonNumber = Constant.BASE_BLANK;
                if (adapter.isNone()) {
                    playerFileModel.getVideoModel().setTmdbId(item.getId());
                    playerFileModel.getVideoModel().setTmdbMediaType(item.getMediaType());
                    playerFileModel.getVideoModel().setTmdbKeyword(item.getNameDisplay());
                    playerFileModel.getVideoModel().setTmdbEpisodeOverview(item.getOverview());
                    playerFileModel.getVideoModel().setTmdbPosterPath(item.getPosterPath());
                } else if (adapter.isSeason()) {
                    playerFileModel.getVideoModel().setTmdbSeasonId(item.getId());
                    playerFileModel.getVideoModel().setTmdbSeasonName(item.getName());
                    playerFileModel.getVideoModel().setTmdbSeasonPosterPath(item.getPosterPath());
                    if (!(item.getOverview().equals("")))
                        playerFileModel.getVideoModel().appendTmdbEpisodeOverview(item.getOverview());

                    seasonNumber = String.valueOf(item.getSeasonNumber());
                    playerFileModel.getVideoModel().setTmdbSeasonNumber(item.getSeasonNumber());
                }
                getVideoInformation(String.valueOf(playerFileModel.getVideoModel().getTmdbId()), item.getMediaType(), seasonNumber);
            } else if (item.isMediaTypeMovie()) {
                playerFileModel.getVideoModel().setTmdbId(item.getId());
                playerFileModel.getVideoModel().setTmdbMediaType(item.getMediaType());
                playerFileModel.getVideoModel().setTmdbKeyword(item.getNameDisplay());
                playerFileModel.getVideoModel().setTmdbPosterPath(item.getPosterPath());
                playerFileModel.getVideoModel().setTmdbEpisodeId(0);
                playerFileModel.getVideoModel().setTmdbEpisodeName(Constant.BASE_BLANK);
                playerFileModel.getVideoModel().setTmdbEpisodePath(Constant.BASE_BLANK);
                playerFileModel.getVideoModel().setTmdbEpisodeOverview(item.getOverview());//Constant.BASE_BLANK);
                playerFileModel.getVideoModel().setTmdbEpisodeNumber(0);
                playerFileModel.getVideoModel().setTmdbSeasonId(0);
                playerFileModel.getVideoModel().setTmdbSeasonName(Constant.BASE_BLANK);
                playerFileModel.getVideoModel().setTmdbSeasonPosterPath(Constant.BASE_BLANK);
                playerFileModel.getVideoModel().setTmdbSeasonNumber(0);
                updateVideoModel(playerFileModel.getVideoModel());
                onFinishActivity(RESULT_OK);
            } else {
                // if the media type is not movie nor tv, then clear the TMDB.
                //TODO : need to update logic. Is RESULT_OK correct?
                playerFileModel.getVideoModel().clearTMDB();
                updateVideoModel(playerFileModel.getVideoModel());
                onFinishActivity(RESULT_OK);
            }
        }
    };

    private void searchVideoInformation()  {
        searchVideoInformation(false);
    }

    private void searchVideoInformation(boolean isLoadMore) {
        if (!isNetwork()) {
            loading = true;
            Loading.hide();
            return;
        }
        stackSearch.clear();
        int page = isLoadMore ? searchVideoModel.getPage() + 1 : 1;
        application.getDalAiImpl().searchMultiVideo(searchValue, studyLanguage.getFormatOs(), page, new DalApiListener<SearchVideoModel>() {
            @Override
            public void onSuccess(SearchVideoModel response) {
                if (response == null) {
                    adapter.setData(null);
                } else {
                    searchVideoModel.add(response, isLoadMore);
                    adapter.setData(searchVideoModel.getResults());
                }
                loading = true;
                Loading.hide();
            }

            @Override
            public void onFailure(String error) {
                DLog.e(getLogTag(), "searchMultiVideo error=" + error);
                loading = true;
                Loading.hide();
            }
        });
    }

    private void getVideoInformation(String id, String mediaType) {
        getVideoInformation(id, mediaType, null);
    }

    private void getVideoInformation(String id, String mediaType, String seasonNumber) {
        if (!isNetwork()) {
            return;
        }
        Loading.show(this);
        if (adapter.isNone()) {
            stackSearch.clear();
            stackSearch.push(searchValue);
        } else if (adapter.isSeason()) {
            stackSearch.push(id);
            id += "/" + Constant.PLAYER.THE_MOVIE_DB.KEY_SEASON + "/" + seasonNumber;
        }

        application.getDalAiImpl().getVideoInformation(id, mediaType, studyLanguage.getFormatOs(), new DalApiListener<Object>() {
            @Override
            public void onSuccess(Object response) {
                String json = new GsonBuilder().create().toJson(response);
                VideoInformationModel model = VideoInformationModel.parserVideoInformation(json, mediaType);

                int typeDisplay = Constant.PLAYER.THE_MOVIE_DB.TYPE.NONE;
                if (model.isHasSeasons()) {
                    typeDisplay = Constant.PLAYER.THE_MOVIE_DB.TYPE.SEASON;
                    searchVideoModel.add(model.getSeasons());
                } else if (model.isHasEpisodes()) {
                    typeDisplay = Constant.PLAYER.THE_MOVIE_DB.TYPE.EPISODE;
                    searchVideoModel.add(model.getEpisodes());
                }
                adapter.setData(searchVideoModel.getResults(), typeDisplay);
                Loading.hide();
            }

            @Override
            public void onFailure(String error) {
                DLog.e(getLogTag(), "getVideoInformation error=" + error);
                Loading.hide();
            }
        });
    }
}
