package com.dalread.activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dalread.R;
import com.dalread.adapter.VocaBookNativeSpeakerAdapter;
import com.dalread.base.BasePlayVocaNativeSpeakerActivity;
import com.dalread.base.EnumLanguage;
import com.dalread.component.SeparatorDecoration;
import com.dalread.dialog.AlertDialog;
import com.dalread.interfaces.IVocaFullPlayTTSItem;
import com.dalread.listener.OnPlayVocaListener;
import com.dalread.listener.OnVocaClickListener;
import com.dalread.model.VocaBook;
import com.dalread.model.VocaInBook;
import com.dalread.model.VocaRecordInBook;
import com.dalread.model.VocaRecordListForBook;
import com.dalread.network.DalApiListener;
import com.dalread.util.Constant;
import com.dalread.util.DLog;
import com.dalread.util.Loading;
import com.dalread.util.PermissionUtils;
import com.dalread.util.ToastUtil;
import com.dalread.util.Utils;
import com.dalread.util.Voca;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindColor;
import butterknife.BindDimen;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.OnClick;


public class VocaBookNativeSpeakerActivity extends BasePlayVocaNativeSpeakerActivity {

    @BindView(R.id.rv_voca)
    RecyclerView rvVoca;
    @BindView(R.id.tv_book_name)
    TextView tvBookName;
    @BindView(R.id.tv_record_count)
    TextView tvRecordCount;

    @BindString(R.string.tpl_wb_recorded_word_count)
    String tplCount;

    @BindColor(R.color.color_divider)
    int clDivider;

    @BindDimen(R.dimen.divider_height)
    float dividerHeight;

    private VocaBook book;
    private LinearLayoutManager layoutManager;
    private VocaBookNativeSpeakerAdapter adapter;
    private List<VocaInBook> allVocas;
    private List<VocaInBook> recordVocas;
    private List<VocaInBook> recordVocasReal;
    private ArrayList<IVocaFullPlayTTSItem> newVocas;
    private int loadingPos;
    private boolean isLoading;
    private AlertDialog alertDialog;
    private boolean isAll;
    private MediaRecorder recorder;
    private boolean recorderReady;
    private VocaRecordInBook recordingVoca;
    private int recordedCount;
    private int totalCount;
    private File voiceFolder;
    private boolean blockToast; // show toast for first recording only

    @Override
    protected int getContentViewId() {
        return R.layout.activity_voca_book_native_speaker;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initData();
        initRecyclerView();
        initOtherViews();
        getData(false);
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
        if (recordingVoca == null) {
            if (isAll) {
                recordVocas = new ArrayList<>(recordVocasReal);
            }
            isAll = !isAll;
            updateTextViewRight();
            bindData();
        }
    }

    private void initData() {
        Intent intent = getIntent();
        if (intent != null) {
            Serializable serializable = intent.getSerializableExtra(Constant.BUNDLE.KEY_VOCA_BOOK);
            if (serializable != null) {
                book = (VocaBook) serializable;
            }
        }
        allVocas = new ArrayList<>();
        recordVocas = new ArrayList<>();
        recordVocasReal = new ArrayList<>();
        voiceFolder = Voca.getVoiceFolderOnLocal(context);
    }

    private void initRecyclerView() {
        adapter = new VocaBookNativeSpeakerAdapter();
        adapter.setOnVocaClickListener(onVocaClickListener);
        rvVoca.setAdapter(adapter);
        rvVoca.setLayoutManager(layoutManager = new LinearLayoutManager(context));
        rvVoca.addItemDecoration(BaseBindUtils.getSeparatorDecoration(context));
        rvVoca.addOnScrollListener(onScrollListener);
    }

    private void initOtherViews() {
        updateTextViewRight();
        if (book != null) {
            tvBookName.setText(book.getName());
        }
        alertDialog = new AlertDialog(context);
    }

    private void updateTextViewRight() {
        if (toolbar != null) {
            toolbar.setTextRight(isAll ? R.string.record : R.string.all);
        }
    }

    private void updateRecordCountText() {
        String text = String.format(tplCount, recordedCount, totalCount);
        tvRecordCount.setText(text);
    }

    private void getData(final boolean isLoadMore) {
        if (book != null) {
            final int uid = getUserID();
            if (uid > 0) {
                if (Utils.isConnected(context)) {
                    isLoading = true;
                    rvVoca.post(() -> {
                        if (isLoadMore) {
                            adapter.setLoadMore(true);
                        } else {
                            allVocas.clear();
                            recordVocas.clear();
                            recordVocasReal.clear();
                            loadingPos = 0;
                            Loading.show(context);
                        }
                        application.getDalAiImpl().getVocaListToRecordAllFromServerWordBook(
                                String.valueOf(uid),
                                sharedPreferences.getLangStudyCode(),
                                sharedPreferences.getMotherTongueLangCode(),
                                String.valueOf(book.getId()),
                                loadingPos,
                                Constant.LOADING_MAX_ITEM,
                                new DalApiListener<VocaRecordListForBook>() {

                                    @Override
                                    public void onSuccess(VocaRecordListForBook response) {
                                        isLoading = false;
                                        recordedCount = response.getRecordedCount();
                                        totalCount = response.getTotalCount();
                                        updateRecordCountText();
                                        newVocas = new ArrayList<>();
                                        int studyLang = EnumLanguage.findByFormatApi(sharedPreferences.getStudyLanguage()).getIdApi();
                                        for (VocaInBook voca : response.getVocas()) {
                                            voca.setPath(Voca.getOutputRecordingFileName(studyLang, voca.getVocaType(), voca.getVocaId(), uid));
                                            allVocas.add(voca);
                                            newVocas.add(voca);
                                            if (!voca.hasVoiceFile()) {
                                                recordVocas.add(voca);
                                                recordVocasReal.add(voca);
                                            }
                                        }
                                        loadingPos = allVocas.size();
                                        DLog.i(getLogTag(), "loadingPos = " + loadingPos);
                                        if (isLoadMore) {
                                            adapter.setLoadMore(false);
                                        } else {
                                            Loading.hide();
                                        }
                                        onGetDataFinish();
                                    }

                                    @Override
                                    public void onFailure(String error) {
                                        isLoading = false;
                                        if (isLoadMore) {
                                            adapter.setLoadMore(false);
                                        } else {
                                            Loading.hide();
                                        }
                                        onGetDataFinish();
                                    }
                                }
                        );
                    });
                } else {
                    alertDialog.showNoInternet();
                }
            } else {
                alertDialog.showLogInRequired();
            }
        }
    }

    private void onGetDataFinish() {
        bindData();
        checkVersionAndDownload(newVocas);
    }

    private void bindData() {
        adapter.setData(isAll ? allVocas : recordVocas);
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();

        initPlayVocaHelper();
    }

    private void initPlayVocaHelper() {
        playVocaHelper.initPlayer();
        if (!playVocaHelper.hasOnPlayVocaListener()) {
            playVocaHelper.setOnPlayVocaListener(new OnPlayVocaListener() {

                @Override
                public void onPlay(final IVocaFullPlayTTSItem voca) {
                    voca.setVIPlaying(true);
                    rvVoca.post(() -> adapter.notifyVocaChanged((VocaRecordInBook) voca));
                }

                @Override
                public void onStop(final IVocaFullPlayTTSItem voca) {
                    voca.setVIPlaying(false);
                    rvVoca.post(() -> adapter.notifyVocaChanged((VocaRecordInBook) voca));
                }
            });
        }
    }

    private RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {

        @Override
        public void onScrolled(@NotNull RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);

            if (!isLoading && !allVocas.isEmpty() && allVocas.size() % Constant.LOADING_MAX_ITEM == 0) {
                int count = layoutManager.getItemCount();
                int last = layoutManager.findLastVisibleItemPosition();
                if (count <= last + 2) { // load more when scrolled to the second-last item
                    DLog.i(getLogTag(), "count = " + count);
                    DLog.i(getLogTag(), "last + 2 = " + (last + 2));
                    getData(true);
                }
            }
        }
    };

    private OnVocaClickListener onVocaClickListener = new OnVocaClickListener() {

        @Override
        public void onPlayAllClick() {
        }

        @Override
        public void onPlayAllClick(int vocaKnow) {
        }

        @Override
        public void onPlayClick(VocaInBook voca) {
            if (recordingVoca == null) {
                boolean isPlaying = voca.isVIPlaying();
                playVocaHelper.stop();
                if (!isPlaying) {
                    startPlayVoca(voca);
                }
            }
        }

        @Override
        public void onVocaKnowClick(VocaInBook voca) {
        }

        @Override
        public void onVocaKnowClick(VocaInBook voca, int vocaKnow) {
        }

        @Override
        public void onInfoClick(VocaInBook voca) {
            if (!playVocaHelper.isPlaying()) {
                if (recordingVoca == null) {
                    recordingVoca = (VocaRecordInBook) voca;
                    prepareRecord();
                } else if (recordingVoca.getVocaId() == voca.getVocaId() && recorderReady) {
                    stopRecord();
                }
            }
        }

        @Override
        public void onDisplayOrderChange(VocaInBook voca, int newOrder) {
        }
    };

    private void stopRecord() {
        // stop recorder
        try {
            recorder.stop();
        } catch (Exception e) {
            return; // we will have an exception if calling stop right after start: https://developer.android.com/reference/android/media/MediaRecorder#stop()
        }
        recorder.reset();
        recorderReady = false;

        // update UI
        recordingVoca.setVIChecked(false);
        recordingVoca.setHasVoiceFile(true);

        // auto play
        boolean autoPlaySuccess = false;
        if (sharedPreferences.getAutoPlayInRecordingAll()) {
            autoPlaySuccess = playMyVoiceOnce(recordingVoca);
        }
        if (!autoPlaySuccess) {
            adapter.notifyVocaChanged(recordingVoca);
        }

        // keep info
        final String vocaId = String.valueOf(recordingVoca.getVocaId());
        final int vocaType = recordingVoca.getVocaType();
        final String path = recordingVoca.getVIPath();

        // remove from record list
        if (recordVocasReal.remove(recordingVoca)) {
            recordedCount++;
            updateRecordCountText();
        }
        recordingVoca = null;

//        // upload
//        final File recordFile = Voca.getVoiceFileOnLocal(voiceFolder, path);
//        if (recordFile != null && recordFile.exists()) {
//            Uri uri = Uri.fromFile(recordFile);
//            StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(Voca.getVoiceFolderPathOnFirebase(path) + uri.getLastPathSegment());
//            StorageMetadata metadata = new StorageMetadata.Builder()
//                    .setContentType("application/octet-stream")
//                    .build();
//            UploadTask uploadTask = storageReference.putFile(uri, metadata);
//            uploadTask.addOnSuccessListener(taskSnapshot -> {
//                application.getDalAiImpl().updateVoiceFileInfo(
//                        String.valueOf(getUserID()),
//                        sharedPreferences.getLangStudyCode(),
//                        vocaId,
//                        vocaType,
//                        Constant.FILE.EXTENTION_SPEAKING,
//                        recordFile.length(),
//                        new DalApiListener<Boolean>() {
//
//                            @Override
//                            public void onSuccess(Boolean response) {
//                                if (response) {
//                                    Voca.executeRealmTransaction(realm -> {
//                                        VocaDownload vocaDownload = realm.where(VocaDownload.class)
//                                                .equalTo("name", path)
//                                                .findFirst();
//                                        if (vocaDownload == null) {
//                                            vocaDownload = new VocaDownload();
//                                            vocaDownload.setName(path);
//                                            vocaDownload.setVersion(0);
//                                            realm.copyToRealm(vocaDownload);
//                                        } else {
//                                            int version = vocaDownload.getVersion() + 1;
//                                            vocaDownload.setVersion(version);
//                                        }
//                                    });
//                                }
//                            }
//
//                            @Override
//                            public void onFailure(String error) {
//                            }
//                        }
//                );
//                application.getEventBus().post(new SuccessEvent(BaseEvent.Screen.MAIN, BaseEvent.EventType.DATA_CHANGED, null));
//            });
//        }
    }

    private void prepareRecord() {
        if (PermissionUtils.checkRecordAudio(this, true)) {
            if (PermissionUtils.checkWriteExternalStorage(this, true)) {
                startRecord();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PermissionUtils.REQUEST_CODE_RECORD_AUDIO:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (PermissionUtils.checkWriteExternalStorage(this, true)) {
                        startRecord();
                    }
                }
                break;
            case PermissionUtils.REQUEST_CODE_WRITE_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startRecord();
                }
                break;
        }
    }

    private void startRecord() {
        recorder = Voca.setupMediaRecorder();
        File recordFile = Voca.getVoiceFileOnLocal(voiceFolder, recordingVoca.getVIPath());
        if (recordFile != null) {
            recorder.setOutputFile(recordFile.getPath());
        }
        try {
            recorder.prepare();
            recorderReady = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (recorderReady) {
            // start recorder
            try {
                recorder.start();
            } catch (IllegalStateException e) {
                ToastUtil.getInstance(context).show(R.string.msg_cannot_record_while_calling);
                recorder.reset();
                recorderReady = false;
                recordingVoca = null;
                return;
            }

            // update UI
            recordingVoca.setVIChecked(true);
            adapter.notifyVocaChanged(recordingVoca);
            if (!blockToast) {
                blockToast = true;
                showToast();
            }
        }
    }

    @OnClick(R.id.ic_play_all)
    void onClick() {
        if (recordingVoca == null) {
            Intent intent = new Intent(context, PlaylistNativeSpeakerActivity.class);
            ArrayList<IVocaFullPlayTTSItem> items = new ArrayList<>();
            for (VocaInBook voca : allVocas) {
                if (recordVocasReal.indexOf(voca) == -1) {
                    items.add(voca);
                }
            }
            intent.putExtra(Constant.BUNDLE.KEY_VOCA_PLAYLIST, items);
            openNewScreen(intent);
        }
    }

    @Override
    public void onBackPressed() {
        if (recordingVoca == null) {
            super.onBackPressed();
        }
    }

    private void showToast() {
        ToastUtil.getInstance(context).show(R.string.recording_tap_to_stop);
    }
}
