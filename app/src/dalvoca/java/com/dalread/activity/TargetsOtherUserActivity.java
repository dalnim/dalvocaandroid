package com.dalread.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.UtteranceProgressListener;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.dalread.R;
import com.dalread.adapter.TargetsAdapter;
import com.dalread.base.BaseDalVocaPlayVocaActivity;
import com.dalread.base.EnumLanguage;
import com.dalread.component.SeparatorDecoration;
import com.dalread.dialog.AlertDialog;
import com.dalread.listener.OnTargetsClickListener;
import com.dalread.model.VocaMemorize;
import com.dalread.network.DalApiListener;
import com.dalread.util.Constant;
import com.dalread.util.Loading;
import com.dalread.util.Utils;
import com.dalread.util.Voca;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindColor;
import butterknife.BindDimen;
import butterknife.BindView;

public class TargetsOtherUserActivity extends BaseDalVocaPlayVocaActivity {

    @BindView(R.id.v_refresh)
    SwipeRefreshLayout vRefresh;

    @BindView(R.id.rv_voca)
    RecyclerView rvVoca;

    @BindColor(R.color.color_divider)
    int clDivider;

    @BindDimen(R.dimen.divider_height)
    float dividerHeight;

    private Context context;
    private TargetsAdapter adapter;
    private List<VocaMemorize> grade1;
    private AlertDialog alertDialog;
    private int otherUid;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_targets;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = this;
        otherUid = getIntent().getIntExtra(Constant.BUNDLE.KEY_OTHER_USER_ID, 0);

        toolbar.setTitle(getIntent().getStringExtra(Constant.BUNDLE.KEY_OTHER_USER_NAME));
        initRecyclerView();
        initSwipeRefreshLayout();
        initDialog();

        getData();
    }

    @Override
    public void onHeaderLeftClick() {
        onBackPressed();
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
    public void onResume() {
        super.onResume();

        initPlayVocaHelper();
    }

    private void initRecyclerView() {
        adapter = new TargetsAdapter(this, sharedPreferences.getDisplayPronunciation(), true);
        adapter.setListener(listener);
        rvVoca.setAdapter(adapter);
        rvVoca.setLayoutManager(new LinearLayoutManager(context));
        rvVoca.addItemDecoration(BaseBindUtils.getSeparatorDecoration(context));
    }

    private void initSwipeRefreshLayout() {
        vRefresh.setColorSchemeResources(R.color.colorBlue);
        vRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                getData();
                vRefresh.setRefreshing(false);
            }
        });
    }

    private void initDialog() {
        alertDialog = new AlertDialog(context);
    }

    private void getData() {
        if (otherUid > 0) {
            if (Utils.isConnected(context)) {
                Loading.show(context);
                application.getDalAiImpl().getVocasFromTargetVoca(
                        String.valueOf(otherUid),
                        sharedPreferences.getLangStudyCode(),
                        sharedPreferences.getMotherTongueLangCode(),
                        new DalApiListener<List<VocaMemorize>>() {

                            @Override
                            public void onSuccess(final List<VocaMemorize> response) {
                                Loading.hide();
                                grade1 = new ArrayList<>();
                                int studyLang = EnumLanguage.findByFormatApi(sharedPreferences.getStudyLanguage()).getIdApi();
                                for (VocaMemorize voca : response) {
                                    if (voca.getVocaKnow() == Constant.VOCA_KNOW.VOCA_KNOW_AMKI_GRADE_1) {
                                        voca.setPath(Voca.getOutputRecordingFileName(studyLang, voca.getVocaType(), voca.getVocaId(), otherUid));
                                        grade1.add(voca);
                                    }
                                }
                                bindData();
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

    private void bindData() {
        adapter.setData(grade1, null, null);
        adapter.notifyDataSetChanged();
    }

    private void initPlayVocaHelper() {
        if (!getPlayVocaHelper().hasMotherTongueListener()) {
            getPlayVocaHelper().setMotherTongueListener(new UtteranceProgressListener() {

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
        if (!getPlayVocaHelper().hasStudyListener()) {
            getPlayVocaHelper().setStudyListener(new UtteranceProgressListener() {

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

    private void updateItemStatus(String utteranceId, boolean playing) {
        for (final VocaMemorize voca : grade1) {
            if (utteranceId.equals(String.valueOf(voca.getVIId()))) {
                voca.setVIPlaying(playing);
                rvVoca.post(new Runnable() {

                    @Override
                    public void run() {
                        adapter.notifyPlayedOrStopped(voca);
                    }
                });
                return;
            }
        }
    }

    private OnTargetsClickListener listener = new OnTargetsClickListener() {

        @Override
        public void onPlayAllSoundClick(int grade) {
        }

        @Override
        public void onPlaySoundClick(VocaMemorize voca) {
            boolean isPlaying = voca.isVIPlaying();
            getPlayVocaHelper().stop();
            if (!isPlaying) {
                preparePlayVoca(voca);
            }
        }

        @Override
        public void onInfoClick(VocaMemorize voca) {
            Intent intent;
            intent = new Intent(context, WordInfoActivity.class);
            intent.putExtra(Constant.BUNDLE.KEY_VOCA_ID, voca.getVocaId());
            intent.putExtra(Constant.BUNDLE.KEY_VOCA_TYPE, voca.getVocaType());
            openNewScreen(intent);
        }

        @Override
        public void onGradeClick(VocaMemorize voca) {
            sendStudyListToSendStudent(voca);
            voca.setVIChecked(true);
            adapter.notifyPlayedOrStopped(voca);
        }
    };

    private void sendStudyListToSendStudent(VocaMemorize voca) {
        int uid = getUserID();
        if (uid > 0) {
            if (Utils.isConnected(context)) {
                application.getDalAiImpl().sendStudyListToSendStudent(
                        String.valueOf(uid),
                        sharedPreferences.getLangStudyCode(),
                        sharedPreferences.getMotherTongueLangCode(),
                        String.valueOf(voca.getVocaId()),
                        voca.getVocaType(),
                        String.valueOf(otherUid)
                );
            } else {
                alertDialog.showNoInternet();
            }
        } else {
            alertDialog.showLogInRequired();
        }
    }
}
