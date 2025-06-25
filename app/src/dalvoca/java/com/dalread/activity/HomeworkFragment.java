package com.dalread.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.UtteranceProgressListener;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.dalread.R;
import com.dalread.adapter.HomeworkAdapter;
import com.dalread.base.BaseHomeworkFragment;
import com.dalread.base.EnumLanguage;
import com.dalread.component.SeparatorDecoration;
import com.dalread.dialog.AlertDialog;
import com.dalread.dialog.ConfirmationDialog;
import com.dalread.listener.OnHomeworkClickListener;
import com.dalread.model.VocaFeedback;
import com.dalread.model.VocaStudy;
import com.dalread.network.DalApiListener;
import com.dalread.util.Constant;
import com.dalread.util.Loading;
import com.dalread.util.Utils;
import com.dalread.util.Voca;
import com.nikhilpanju.recyclerviewenhanced.RecyclerTouchListener;

import java.io.Serializable;
import java.util.List;

import butterknife.BindColor;
import butterknife.BindDimen;
import butterknife.BindView;
import io.realm.Realm;
import io.realm.RealmResults;

public class HomeworkFragment extends BaseHomeworkFragment {

    @BindView(R.id.v_refresh)
    SwipeRefreshLayout vRefresh;
    @BindView(R.id.rv_voca)
    RecyclerView rvVoca;

    @BindColor(R.color.color_divider)
    int clDivider;

    @BindDimen(R.dimen.divider_height)
    float dividerHeight;

    private AlertDialog alertDialog;
    private HomeworkAdapter adapter;
    private List<VocaStudy> vocas;
    private long feedbackCount;
    private RecyclerTouchListener recyclerTouchListener;
    private ConfirmationDialog confirmationDialog;

    @Override
    protected int getContentViewId() {
        return R.layout.fragment_homework;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initDialog();
        initRecyclerView();
        initSwipeRefreshLayout();

        getDataFromServer();
    }

    @Override
    public void onResume() {
        super.onResume();

        initPlayVocaHelper();

        // display star instead of voca when coming back from other screens
        if (vocas != null && adapter != null) {
            for (VocaStudy voca : vocas) {
                voca.setDisplayed(false);
            }
            adapter.notifyDataSetChanged();
        }

        addOnItemTouchListener();
    }

    @Override
    public void onPause() {
        removeOnItemTouchListener();

        super.onPause();
    }

    private void initDialog() {
        alertDialog = new AlertDialog(activity);
        confirmationDialog = new ConfirmationDialog(activity, onDeleteAllClickListener);
        confirmationDialog.setMyTitle(R.string.warning);
        confirmationDialog.setMessage(R.string.msg_delete_homework_list);
        confirmationDialog.setPositiveText(R.string.yes);
        confirmationDialog.setNegativeText(R.string.no);
    }

    private void initRecyclerView() {
        adapter = new HomeworkAdapter(sharedPreferences.getDisplayPronunciation());
        adapter.setListener(onHomeworkClickListener);
        rvVoca.setAdapter(adapter);
        rvVoca.setLayoutManager(new LinearLayoutManager(activity));
        rvVoca.addItemDecoration(new SeparatorDecoration(activity, clDivider, dividerHeight));
        recyclerTouchListener = new RecyclerTouchListener(activity, rvVoca)
                .setSwipeOptionViews(R.id.tv_delete)
                .setSwipeable(R.id.v_foreground, R.id.v_background, new RecyclerTouchListener.OnSwipeOptionsClickListener() {

                    @Override
                    public void onSwipeOptionClicked(int viewId, int position) {
                        onDeleteItemClick(vocas.get(position - 1));
                    }
                });
        recyclerTouchListener.setUnSwipeableRows(0);
    }

    private void initSwipeRefreshLayout() {
        vRefresh.setColorSchemeResources(R.color.colorBlue);
        vRefresh.setOnRefreshListener(onRefreshListener);
    }

    private void getDataFromServer() {
        int uid = sharedPreferences.getRealUid();
        if (uid > 0) {
            if (Utils.isConnected(activity)) {
                Loading.show(activity);
                application.getDalAiImpl().getToMemorizeVocaList(
                        String.valueOf(uid),
                        sharedPreferences.getLangStudyCode(),
                        sharedPreferences.getMotherTongueLangCode(),
                        new DalApiListener<List<VocaStudy>>() {

                            @Override
                            public void onSuccess(final List<VocaStudy> response) {
                                Loading.hide();
                                Voca.executeRealmTransaction(new Realm.Transaction() {

                                    @Override
                                    public void execute(@NonNull Realm realm) {
                                        realm.delete(VocaStudy.class);
                                        realm.copyToRealm(response);
                                    }
                                });
                                getDataFromLocal();
                            }

                            @Override
                            public void onFailure(String error) {
                                Loading.hide();
                                getDataFromLocal();
                            }
                        }
                );
            } else {
                alertDialog.showNoInternet();
                getDataFromLocal();
            }
        } else {
            alertDialog.showLogInRequired();
        }
    }

    private void getDataFromLocal() {
        Voca.executeRealmTransaction(new Realm.Transaction() {

            @Override
            public void execute(Realm realm) {
                RealmResults<VocaStudy> words = realm.where(VocaStudy.class)
                        .findAll();
                vocas = realm.copyFromRealm(words);
                feedbackCount = realm.where(VocaFeedback.class)
                        .count();
            }
        });
        if (vocas.isEmpty()) {
            bindEmptyData();
        } else {
            setVocaPath();
            bindData();
        }
    }

    private void setVocaPath() {
        int studyLang = EnumLanguage.findByFormatApi(sharedPreferences.getStudyLanguage()).getIdApi();
        int uid = sharedPreferences.getRealUid();
        for (VocaStudy voca : vocas) {
            voca.setPath(Voca.getOutputRecordingFileName(studyLang, voca.getType(), voca.getVocaId(), uid));
        }
    }

    private void bindData() {
        adapter.setData(vocas, feedbackCount);
        adapter.notifyDataSetChanged();
    }

    private void bindEmptyData() {
        vocas = null;
        feedbackCount = 0;
        bindData();
    }

    private void initPlayVocaHelper() {
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

    private void updateItemStatus(String utteranceId, boolean playing) {
        int count = vocas.size();
        for (int i = 0; i < count; i++) {
            VocaStudy voca = vocas.get(i);
            if (utteranceId.equals(String.valueOf(voca.getVIId()))) {
                voca.setVIPlaying(playing);
                final int pos = i + 1;
                rvVoca.post(new Runnable() {

                    @Override
                    public void run() {
                        adapter.notifyItemChanged(pos);
                    }
                });
                break;
            }
        }
    }

    private OnHomeworkClickListener onHomeworkClickListener = new OnHomeworkClickListener() {

        @Override
        public void onPlayAllClick() {
            Intent intent = new Intent(activity, PlaylistActivity.class);
            intent.putExtra(Constant.BUNDLE.KEY_VOCA_PLAYLIST, (Serializable) vocas);
            activity.openNewScreen(intent);
        }

        @Override
        public void onPlayClick(VocaStudy voca) {
            boolean isPlaying = voca.isVIPlaying();
            activity.getPlayVocaHelper().stop();
            if (!isPlaying) {
                activity.preparePlayVoca(voca);
            }
        }

        @Override
        public void onStudyClick(VocaStudy voca) {
            Intent intent = new Intent(activity, StudyActivity.class);
            intent.putExtra(Constant.BUNDLE.KEY_VOCA, voca);
            activity.openNewScreen(intent);
        }

        @Override
        public void onInfoClick(VocaStudy voca) {
            Intent intent;
            intent = new Intent(activity, WordInfoActivity.class);
            intent.putExtra(Constant.BUNDLE.KEY_VOCA_ID, voca.getVocaId());
            intent.putExtra(Constant.BUNDLE.KEY_VOCA_TYPE, voca.getType());
            activity.openNewScreen(intent);
        }
    };

    private SwipeRefreshLayout.OnRefreshListener onRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {

        @Override
        public void onRefresh() {
            getDataFromServer();
            vRefresh.setRefreshing(false);
        }
    };

    private void addOnItemTouchListener() {
        rvVoca.addOnItemTouchListener(recyclerTouchListener);
    }

    private void removeOnItemTouchListener() {
        rvVoca.removeOnItemTouchListener(recyclerTouchListener);
    }

    public void onDeleteItemClick(final VocaStudy voca) {
        int uid = sharedPreferences.getRealUid();
        if (uid > 0) {
            if (Utils.isConnected(activity)) {
                application.getDalAiImpl().removeMemorizeVoca(
                        String.valueOf(voca.getId()),
                        new DalApiListener<Boolean>() {

                            @Override
                            public void onSuccess(Boolean response) {
                                if (response) {
                                    Voca.executeRealmTransaction(new Realm.Transaction() {

                                        @Override
                                        public void execute(@NonNull Realm realm) {
                                            realm.where(VocaStudy.class)
                                                    .equalTo("id", voca.getId())
                                                    .findAll()
                                                    .deleteAllFromRealm();
                                        }
                                    });
                                    adapter.notifyItemRemoved(voca);
                                }
                            }

                            @Override
                            public void onFailure(String error) {
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

    public void onDeleteAllClick() {
        confirmationDialog.show();
    }

    private ConfirmationDialog.OnDialogClickListener onDeleteAllClickListener = new ConfirmationDialog.OnDialogClickListener() {

        @Override
        public void onPositive(DialogInterface dialog) {
            dialog.dismiss();

            int uid = sharedPreferences.getRealUid();
            if (uid > 0) {
                if (Utils.isConnected(activity)) {
                    String ids = "";
                    for (VocaStudy voca : vocas) {
                        ids += "," + voca.getId();
                    }
                    if (!TextUtils.isEmpty(ids)) {
                        ids = ids.substring(1);
                        application.getDalAiImpl().removeMemorizeVoca(
                                ids,
                                new DalApiListener<Boolean>() {

                                    @Override
                                    public void onSuccess(Boolean response) {
                                        if (response) {
                                            Voca.executeRealmTransaction(new Realm.Transaction() {

                                                @Override
                                                public void execute(@NonNull Realm realm) {
                                                    realm.delete(VocaStudy.class);
                                                }
                                            });
                                            getDataFromLocal();
                                        }
                                    }

                                    @Override
                                    public void onFailure(String error) {
                                    }
                                }
                        );
                    }
                } else {
                    alertDialog.showNoInternet();
                }
            } else {
                alertDialog.showLogInRequired();
            }
        }

        @Override
        public void onNegative(DialogInterface dialog) {
            dialog.dismiss();
        }
    };
}
