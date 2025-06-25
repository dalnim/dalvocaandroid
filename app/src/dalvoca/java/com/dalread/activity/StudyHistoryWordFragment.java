package com.dalread.activity;

import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.UtteranceProgressListener;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dalread.R;
import com.dalread.adapter.StudyHistoryWordAdapter;
import com.dalread.base.BasePlayVocaActivity;
import com.dalread.base.BaseVocaHistoryFragment;
import com.dalread.base.EnumLanguage;
import com.dalread.component.SeparatorDecoration;
import com.dalread.dialog.AlertDialog;
import com.dalread.listener.OnStudyHistoryClickListener;
import com.dalread.model.VocaStudyHistory;
import com.dalread.network.DalApiListener;
import com.dalread.util.Constant;
import com.dalread.util.Loading;
import com.dalread.util.Utils;
import com.dalread.util.Voca;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindColor;
import butterknife.BindDimen;
import butterknife.BindView;

public class StudyHistoryWordFragment extends BaseVocaHistoryFragment {

    @BindView(R.id.rv_history)
    RecyclerView rvHistory;

    @BindColor(R.color.color_divider)
    int clDivider;

    @BindDimen(R.dimen.divider_height)
    float dividerHeight;

    private StudyHistoryWordAdapter adapter;
    private List<VocaStudyHistory> histories;
    private LinearLayoutManager layoutManager;
    private int loadingPos;
    private boolean isLoading;
    private AlertDialog alertDialog;

    @Override
    protected int getContentViewId() {
        return R.layout.fragment_study_history;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        init();
        getData(false);
    }

    @Override
    public void onResume() {
        super.onResume();

        initPlayVocaHelper();
    }

    private void init() {
        adapter = new StudyHistoryWordAdapter(sharedPreferences.getDisplayPronunciation());
        adapter.setData(histories = new ArrayList<>());
        adapter.setListener(onClickListener);
        rvHistory.setAdapter(adapter);
        rvHistory.setLayoutManager(layoutManager = new LinearLayoutManager(getContext()));
        rvHistory.addItemDecoration(new SeparatorDecoration(getContext(), clDivider, dividerHeight));
        rvHistory.addOnScrollListener(onScrollListener);
        alertDialog = new AlertDialog(getContext());
    }

    private void getData(final boolean isLoadMore) {
        final int uid = sharedPreferences.getRealUid();
        if (uid > 0) {
            if (Utils.isConnected(getContext())) {
                isLoading = true;
                rvHistory.post(new Runnable() {

                    @Override
                    public void run() {
                        if (isLoadMore) {
                            adapter.setLoadMore(true);
                        } else {
                            histories.clear();
                            loadingPos = 0;
                            Loading.show(getContext());
                        }
                        application.getDalAiImpl().getStudentStudiedHistoryByVoca(
                                String.valueOf(uid),
                                sharedPreferences.getLangStudyCode(),
                                sharedPreferences.getMotherTongueLangCode(),
                                loadingPos,
                                Constant.LOADING_MAX_ITEM,
                                new DalApiListener<List<VocaStudyHistory>>() {

                                    @Override
                                    public void onSuccess(List<VocaStudyHistory> response) {
                                        isLoading = false;
                                        int studyLang = EnumLanguage.findByFormatApi(sharedPreferences.getStudyLanguage()).getIdApi();
                                        for (VocaStudyHistory voca : response) {
                                            voca.setPath(Voca.getOutputRecordingFileName(studyLang, voca.getType(), voca.getVocaId(), uid));
                                            histories.add(voca);
                                        }
                                        loadingPos = histories.size();
                                        if (isLoadMore) {
                                            adapter.setLoadMore(false);
                                        } else {
                                            Loading.hide();
                                        }
                                        adapter.setData(histories);
                                        adapter.notifyDataSetChanged();
                                    }

                                    @Override
                                    public void onFailure(String error) {
                                        isLoading = false;
                                        if (isLoadMore) {
                                            adapter.setLoadMore(false);
                                        } else {
                                            Loading.hide();
                                        }
                                    }
                                }
                        );
                    }
                });
            } else {
                alertDialog.showNoInternet();
            }
        } else {
            alertDialog.showLogInRequired();
        }
    }

    private void initPlayVocaHelper() {
        if (activity != null) {
            if (!activity.getPlayVocaHelper().hasMotherTongueListener()) {
                activity.getPlayVocaHelper().setMotherTongueListener(new UtteranceProgressListener() {

                    @Override
                    public void onStart(String utteranceId) {
                        updateItemStatus(utteranceId, true);
                    }

                    @Override
                    public void onDone(String utteranceId) {
                    }

                    @Override
                    public void onError(String utteranceId) {
                    }
                });
            }
            if (!activity.getPlayVocaHelper().hasStudyListener()) {
                activity.getPlayVocaHelper().setStudyListener(new UtteranceProgressListener() {

                    @Override
                    public void onStart(String utteranceId) {
                    }

                    @Override
                    public void onDone(String utteranceId) {
                        updateItemStatus(utteranceId, false);
                    }

                    @Override
                    public void onError(String utteranceId) {
                        updateItemStatus(utteranceId, false);
                    }
                });
            }
        }
    }

    private void updateItemStatus(String utteranceId, boolean playing) {
        for (final VocaStudyHistory voca : histories) {
            if (utteranceId.equals(String.valueOf(voca.getVIId()))) {
                voca.setVIPlaying(playing);
                rvHistory.post(new Runnable() {

                    @Override
                    public void run() {
                        adapter.notifyPlayingStatusChanged(voca);
                    }
                });
                break;
            }
        }
    }

    private RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);

            if (!isLoading && !histories.isEmpty() && histories.size() % Constant.LOADING_MAX_ITEM == 0) {
                int count = layoutManager.getItemCount();
                int last = layoutManager.findLastVisibleItemPosition();
                if (count <= last + 2) { // load more when scrolled to the second-last item
                    getData(true);
                }
            }
        }
    };

    private OnStudyHistoryClickListener onClickListener = new OnStudyHistoryClickListener() {

        @Override
        public void onPlayAllClick(Date date) {
            if (activity != null) {
                Intent intent = new Intent(activity, PlaylistActivity.class);
                intent.putExtra(Constant.BUNDLE.KEY_VOCA_PLAYLIST, (Serializable) histories);
                activity.openNewScreen(intent);
            }
        }

        @Override
        public void onPlayClick(VocaStudyHistory voca) {
            if (activity != null) {
                boolean isPlaying = voca.isVIPlaying();
                activity.getPlayVocaHelper().stop();
                if (!isPlaying && activity instanceof BasePlayVocaActivity) {
                    activity.preparePlayVoca(voca);
                }
            }
        }

        @Override
        public void onInfoClick(VocaStudyHistory voca) {
            if (activity != null) {
                Intent intent;
                intent = new Intent(activity, WordInfoActivity.class);
                intent.putExtra(Constant.BUNDLE.KEY_VOCA_ID, voca.getVocaId());
                intent.putExtra(Constant.BUNDLE.KEY_VOCA_TYPE, voca.getType());

                activity.openNewScreen(intent);
            }
        }
    };
}
