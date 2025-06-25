package com.dalread.activity;

import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.UtteranceProgressListener;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dalread.R;
import com.dalread.adapter.StudyHistoryFeedbackAdapter;
import com.dalread.base.BaseVocaHistoryFragment;
import com.dalread.base.EnumLanguage;
import com.dalread.component.SeparatorDecoration;
import com.dalread.dialog.AlertDialog;
import com.dalread.listener.OnStudyHistoryFeedbackClickListener;
import com.dalread.model.VocaFeedback;
import com.dalread.network.DalApiListener;
import com.dalread.util.Constant;
import com.dalread.util.DateUtils;
import com.dalread.util.Loading;
import com.dalread.util.Utils;
import com.dalread.util.Voca;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindColor;
import butterknife.BindDimen;
import butterknife.BindView;

public class StudyHistoryFeedbackFragment extends BaseVocaHistoryFragment {

    @BindView(R.id.rv_history)
    RecyclerView rvVoca;

    @BindColor(R.color.color_divider)
    int clDivider;

    @BindDimen(R.dimen.divider_height)
    float dividerHeight;

    private StudyHistoryFeedbackAdapter adapter;
    private List<VocaFeedback> vocas;
    private AlertDialog alertDialog;

    @Override
    protected int getContentViewId() {
        return R.layout.fragment_study_history;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        init();
        getData();
    }

    @Override
    public void onResume() {
        super.onResume();

        initPlayVocaHelper();
    }

    private void init() {
        adapter = new StudyHistoryFeedbackAdapter(sharedPreferences.getDisplayPronunciation());
        adapter.setListener(onClickListener);
        rvVoca.setAdapter(adapter);
        rvVoca.setLayoutManager(new LinearLayoutManager(activity));
        rvVoca.addItemDecoration(new SeparatorDecoration(activity, clDivider, dividerHeight));
        alertDialog = new AlertDialog(activity);
    }

    private void getData() {
        final int uid = sharedPreferences.getRealUid();
        if (uid > 0) {
            if (Utils.isConnected(activity)) {
                Loading.show(activity);
                application.getDalAiImpl().getPronounceFeedback(
                        new DalApiListener<List<VocaFeedback>>() {

                            @Override
                            public void onSuccess(List<VocaFeedback> response) {
                                vocas = new ArrayList<>();
                                int studyLang = EnumLanguage.findByFormatApi(sharedPreferences.getStudyLanguage()).getIdApi();
                                for (VocaFeedback voca : response) {
                                    voca.setPath(Voca.getOutputRecordingFileName(studyLang, voca.getType(), voca.getVocaId(), uid));
                                    vocas.add(voca);
                                }
                                adapter.setData(vocas);
                                adapter.notifyDataSetChanged();
                                Loading.hide();
                            }

                            @Override
                            public void onFailure(String error) {
                                Loading.hide();
                            }
                        }
                );
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
        for (final VocaFeedback voca : vocas) {
            if (utteranceId.equals(String.valueOf(voca.getVIId()))) {
                voca.setVIPlaying(playing);
                rvVoca.post(new Runnable() {

                    @Override
                    public void run() {
                        adapter.notifyItemChanged(voca);
                    }
                });
                break;
            }
        }
    }

    private OnStudyHistoryFeedbackClickListener onClickListener = new OnStudyHistoryFeedbackClickListener() {

        @Override
        public void onPlayAllClick(Date date) {
            if (activity != null) {
                ArrayList<VocaFeedback> feedbacks = new ArrayList<>();
                for (VocaFeedback voca : vocas) {
                    try {
                        Date feedbackDate = new Date(DateUtils.secondsToMillis(voca.getFeedbackDate()));
                        if (DateUtils.isSameDate(feedbackDate, date)) {
                            feedbacks.add(voca);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                Intent intent = new Intent(activity, PlaylistActivity.class);
                intent.putExtra(Constant.BUNDLE.KEY_VOCA_PLAYLIST, feedbacks);
                activity.openNewScreen(intent);
            }
        }

        @Override
        public void onPlayClick(VocaFeedback voca) {
            if (activity != null) {
                boolean isPlaying = voca.isVIPlaying();
                activity.getPlayVocaHelper().stop();
                if (!isPlaying) {
                    activity.preparePlayVoca(voca);
                }
            }
        }

        @Override
        public void onInfoClick(VocaFeedback voca) {
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
