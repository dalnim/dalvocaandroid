package com.dalread.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.UtteranceProgressListener;
import android.text.TextUtils;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.dalread.R;
import com.dalread.adapter.VocaDoYouKnowAdapter;
import com.dalread.base.BaseHomeworkFragment;
import com.dalread.base.EnumLanguage;
import com.dalread.component.SeparatorDecoration;
import com.dalread.dialog.AlertDialog;
import com.dalread.dialog.ConfirmationDialog;
import com.dalread.listener.OnVocaDoYouKnowClickListener;
import com.dalread.model.VocaDetailInfo;
import com.dalread.model.VocaDoYouKnow;
import com.dalread.network.DalApiListener;
import com.dalread.util.Constant;
import com.dalread.util.Loading;
import com.dalread.util.Utils;
import com.dalread.util.Voca;
import com.dalread.util.VocaKnow;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import butterknife.BindColor;
import butterknife.BindDimen;
import butterknife.BindString;
import butterknife.BindView;

public class DoYouKnowFragment extends BaseHomeworkFragment {

    @BindView(R.id.v_refresh)
    SwipeRefreshLayout vRefresh;
    @BindView(R.id.rv_voca)
    RecyclerView rvVoca;

    @BindColor(R.color.color_divider)
    int clDivider;

    @BindDimen(R.dimen.divider_height)
    float dividerHeight;

    @BindString(R.string.tpl_voca_from)
    String tplVocaFrom;

    private AlertDialog alertDialog;
    private VocaDoYouKnowAdapter adapter;
    private ArrayList<VocaDoYouKnow> vocas;

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

        getData();
    }

    @Override
    public void onResume() {
        super.onResume();

        initPlayVocaHelper();
    }

    private void initDialog() {
        alertDialog = new AlertDialog(activity);
    }

    private void initRecyclerView() {
        adapter = new VocaDoYouKnowAdapter(activity, sharedPreferences.getDisplayPronunciation());
        adapter.setListener(onVocaClickListener);
        rvVoca.setAdapter(adapter);
        rvVoca.setLayoutManager(new LinearLayoutManager(activity));
        rvVoca.addItemDecoration(new SeparatorDecoration(activity, clDivider, dividerHeight));
    }

    private void initSwipeRefreshLayout() {
        vRefresh.setColorSchemeResources(R.color.colorBlue);
        vRefresh.setOnRefreshListener(onRefreshListener);
    }

    private void getData() {
        final int uid = sharedPreferences.getRealUid();
        if (uid > 0) {
            if (Utils.isConnected(activity)) {
                Loading.show(activity);
                application.getDalAiImpl().getVocasFromDoYouKnowThisVoca(new DalApiListener<List<VocaDoYouKnow>>() {

                    @Override
                    public void onSuccess(List<VocaDoYouKnow> response) {
                        Intent intent = activity.getIntent();
                        int vocaIdFromNotification, vocaTypeFromNotification;
                        try {
                            vocaIdFromNotification = Integer.parseInt(intent.getStringExtra(Constant.BUNDLE.KEY_VOCA_ID));
                            vocaTypeFromNotification = Integer.parseInt(intent.getStringExtra(Constant.BUNDLE.KEY_VOCA_TYPE));
                        } catch (Exception e) {
                            vocaIdFromNotification = vocaTypeFromNotification = 0;
                        }
                        VocaDoYouKnow vocaFromNotification = null;
                        vocas = new ArrayList<>();
                        int studyLang = EnumLanguage.findByFormatApi(sharedPreferences.getStudyLanguage()).getIdApi();
                        LinkedHashMap<String, ArrayList<VocaDoYouKnow>> dataMap = new LinkedHashMap<>();
                        for (VocaDoYouKnow voca : response) {
                            voca.setPath(Voca.getOutputRecordingFileName(studyLang, voca.getVocaType(), voca.getVocaId(), uid));
                            vocas.add(voca);
                            String key = voca.getName();
                            ArrayList<VocaDoYouKnow> vocaList = dataMap.get(key);
                            if (vocaList == null) {
                                vocaList = new ArrayList<>();
                                dataMap.put(key, vocaList);
                            }
                            vocaList.add(voca);

                            if (voca.getVocaId() == vocaIdFromNotification
                                    && voca.getVocaType() == vocaTypeFromNotification) {
                                vocaIdFromNotification = vocaTypeFromNotification = 0;
                                intent.removeExtra(Constant.BUNDLE.KEY_VOCA_ID);
                                intent.removeExtra(Constant.BUNDLE.KEY_VOCA_TYPE);
                                vocaFromNotification = voca;
                            }
                        }
                        adapter.setData(dataMap, tplVocaFrom);
                        Loading.hide();

                        if (vocaFromNotification != null) {
                            showDoYouKnowDialog(vocaFromNotification);
                        }
                    }

                    @Override
                    public void onFailure(String error) {
                        Loading.hide();
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
            final VocaDoYouKnow voca = vocas.get(i);
            if (utteranceId.equals(String.valueOf(voca.getVIId()))) {
                voca.setVIPlaying(playing);
                rvVoca.post(new Runnable() {

                    @Override
                    public void run() {
                        adapter.refreshVoca(voca);
                    }
                });
                break;
            }
        }
    }

    private OnVocaDoYouKnowClickListener onVocaClickListener = new OnVocaDoYouKnowClickListener() {

        @Override
        public void onPlayClick(VocaDoYouKnow voca) {
            boolean isPlaying = voca.isVIPlaying();
            activity.getPlayVocaHelper().stop();
            if (!isPlaying) {
                activity.preparePlayVoca(voca);
            }
        }

        @Override
        public void onGradeClick(VocaDoYouKnow voca) {
        }

        @Override
        public void onGradeClick(VocaDoYouKnow voca, int grade) {
            changeVocaKnow(voca, grade);
        }

        @Override
        public void onInfoClick(VocaDoYouKnow voca) {
            Intent intent;
            intent = new Intent(activity, WordInfoActivity.class);
            intent.putExtra(Constant.BUNDLE.KEY_VOCA_ID, voca.getVocaId());
            intent.putExtra(Constant.BUNDLE.KEY_VOCA_TYPE, voca.getVocaType());

            activity.openNewScreen(intent);
        }
    };

    private void changeVocaKnow(VocaDoYouKnow voca, int vocaKnow) {
        int uid = sharedPreferences.getRealUid();
        if (uid > 0) {
            if (Utils.isConnected(activity)) {
                application.getDalAiImpl().checkAndChangeVocaKnow(
                        activity,
                        vocaKnow,
                        VocaKnow.getVocaKnowPronounceByVocaKnow(vocaKnow),
                        String.valueOf(voca.getVocaId()),
                        voca.getVocaType(),
                        new DalApiListener<Integer>() {

                            @Override
                            public void onSuccess(Integer newGrade) {
                                adapter.removeVoca(voca);
                                if (newGrade == Constant.VOCA_KNOW.VOCA_KNOW_KNOWN && TextUtils.isEmpty(voca.getMeaning())) {
                                    showHelpUsDialog(voca);
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

    private SwipeRefreshLayout.OnRefreshListener onRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {

        @Override
        public void onRefresh() {
            getData();
            vRefresh.setRefreshing(false);
        }
    };

    private void showDoYouKnowDialog(final VocaDoYouKnow voca) {
        ConfirmationDialog confirmationDialog = new ConfirmationDialog(activity, new ConfirmationDialog.OnDialogClickListener() {

            @Override
            public void onPositive(DialogInterface dialog) {
                changeVocaKnow(voca, Constant.VOCA_KNOW.VOCA_KNOW_KNOWN);
                dialog.dismiss();
            }

            @Override
            public void onNegative(DialogInterface dialog) {
                dialog.dismiss();
            }
        });
        confirmationDialog.setMyTitle(R.string.do_you_know_this);
        String message = Voca.getVocaDisplay(voca);
        String meaning = voca.getMeaning();
        if (TextUtils.isEmpty(meaning)) {
            meaning = voca.getMeaningEnglish();
        }
        if (!TextUtils.isEmpty(meaning)) {
            message += " (" + meaning + ")";
        }
        String name = voca.getName();
        if (!TextUtils.isEmpty(name)) {
            message += "\n-" + name + "-";
        }
        confirmationDialog.setMessage(message);
        confirmationDialog.setPositiveText(R.string.yes);
        confirmationDialog.setNegativeText(R.string.no);
        confirmationDialog.show();
    }

    private void showHelpUsDialog(final VocaDoYouKnow voca) {
        ConfirmationDialog confirmationDialog = new ConfirmationDialog(activity, new ConfirmationDialog.OnDialogClickListener() {

            @Override
            public void onPositive(DialogInterface dialog) {
                getVocaDetailInfo(voca);
                dialog.dismiss();
            }

            @Override
            public void onNegative(DialogInterface dialog) {
                dialog.dismiss();
            }
        });
        confirmationDialog.setMyTitle(R.string.help_us);
        confirmationDialog.setMessage(R.string.msg_no_meaning);
        confirmationDialog.setPositiveText(R.string.yes);
        confirmationDialog.setNegativeText(R.string.no);
        confirmationDialog.show();
    }

    private void getVocaDetailInfo(VocaDoYouKnow voca) {
        final int uid = sharedPreferences.getRealUid();
        if (uid > 0 && Utils.isConnected(activity)) {
            Loading.show(activity);
            application.getDalAiImpl().getVocaDetailInfo(
                    String.valueOf(uid),
                    sharedPreferences.getLangStudyCode(),
                    sharedPreferences.getMotherTongueLangCode(),
                    voca.getVocaId(),
                    voca.getVocaType(),
                    new DalApiListener<VocaDetailInfo>() {

                        @Override
                        public void onSuccess(VocaDetailInfo response) {
                            Loading.hide();

                            Intent intent = new Intent(activity, EditMeaningActivity.class);
                            intent.putExtra(Constant.BUNDLE.KEY_VOCA, response);
                            activity.openNewScreen(intent);
                        }

                        @Override
                        public void onFailure(String error) {
                            Loading.hide();
                        }
                    }
            );
        }
    }
}
