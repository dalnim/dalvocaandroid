package com.dalread.base;

import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.speech.tts.UtteranceProgressListener;
import android.view.View;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dalread.R;
import com.dalread.adapter.VocaListRecordingAdapter;
import com.dalread.component.SeparatorDecoration;
import com.dalread.component.Toolbar;
import com.dalread.composition.PlayTTS;
import com.dalread.composition.VoiceRecording;
import com.dalread.databinding.ActivityVocaListRecordingBinding;
import com.dalread.dialog.AlertDialog;
import com.dalread.dialog.DownloadingVoiceFilesDialog;
import com.dalread.dialog.YesNoDialog;
import com.dalread.helper.VoiceFileDownloadHelper;
import com.dalread.helper.VoiceFileUploadHelper;
import com.dalread.interfaces.IVocaFullPlayTTSItem;
import com.dalread.listener.OnClickListener;
import com.dalread.listener.OnDownloadFileListener;
import com.dalread.listener.OnPlayMyVoiceOnce;
import com.dalread.listener.OnYesNoClickListener;
import com.dalread.model.VocaBook;
import com.dalread.model.VocaInBook;
import com.dalread.model.VocaRecordListForBook;
import com.dalread.network.DalApiListener;
import com.dalread.util.BasePermissionUtils;
import com.dalread.util.BaseVoca;
import com.dalread.util.Constant;
import com.dalread.util.DLog;
import com.dalread.util.Loading;
import com.dalread.util.ToastUtil;
import com.dalread.util.Utils;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
//이걸 상속받는 VocaListRecordingActivity는 쓸일이 없는거 같다. AraPlayer와 AraConv에서는 VocaListRecordingActivity를 지웠음.
public class BaseVocaListRecordingActivity extends BaseActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener { // BaseVocaListRecordingActivity {
    private PlayTTS playTTS;
    private VoiceRecording voiceRecording;
    private VoiceFileUploadHelper fileUploadHelper;
    protected VocaBook vocaBook;
    private LinearLayoutManager layoutManager;
    protected VocaListRecordingAdapter adapter;
    private OnPlayMyVoiceOnce onPlayMyVoiceOnce;
    protected List<VocaInBook> allVocas;
    protected List<VocaInBook> recordVocas;
    protected List<VocaInBook> recordVocasReal;
    protected ArrayList<IVocaFullPlayTTSItem> newVocas;
    protected int loadingPos;
    protected boolean isLoading;
    private AlertDialog alertDialog;
    private boolean isAll;
    private MediaRecorder recorder;
    private boolean recorderReady;
    private VocaInBook recordingVoca;
    protected int recordedCount;
    protected int totalCount;
//    private File voiceFolder;
    private boolean isShowIntroductionToast = true; // show toast for first recording only
    private DownloadingVoiceFilesDialog downloadingVoiceFilesDialog;

    protected ActivityVocaListRecordingBinding binding;


    @Override
    protected View getContentView() {
        binding = ActivityVocaListRecordingBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        return view;
    }

    @Override
    protected Toolbar getToolbar() {
        return binding.header;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initMediaPlayer();
        setOnClickListeners();
        initData();
        initRecyclerView();
        initOtherViews();
        getData(false);
    }
    private void initMediaPlayer() {
        playTTS = new PlayTTS(this);
        voiceRecording = new VoiceRecording(this);
    }
    private void setOnClickListeners() {
        binding.ivHelp.setOnClickListener(this);
        binding.ivDownloadFile.setOnClickListener(this);
        binding.scUploadRecordingAutomatically.setOnCheckedChangeListener(this);
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
            onGetDataFinish();
        }
    }

    protected void initData() {
        fileUploadHelper = new VoiceFileUploadHelper(getApplicationContext(), application);
        Intent intent = getIntent();
        if (intent != null) {
            Serializable serializable = intent.getSerializableExtra(Constant.BUNDLE.KEY_VOCA_BOOK);
            if (serializable != null) {
                vocaBook = (VocaBook) serializable;
            }
        }
        allVocas = new ArrayList<>();
        recordVocas = new ArrayList<>();
        recordVocasReal = new ArrayList<>();
//        voiceFolder = Voca.getVoiceFolderOnLocal(context);
    }

    private void initRecyclerView() {
        adapter = new VocaListRecordingAdapter(this, onClickListener);
        binding.rvVoca.setAdapter(adapter);
        binding.rvVoca.setLayoutManager(layoutManager = new LinearLayoutManager(context));
        binding.rvVoca.addItemDecoration(new SeparatorDecoration(context, ContextCompat.getColor(this, R.color.color_divider), this.getResources().getDimension(R.dimen.divider_height)));
        binding.rvVoca.addOnScrollListener(onScrollListener);
    }

    private void initOtherViews() {
        updateTextViewRight();
        if (vocaBook != null) {
            binding.tvBookName.setText(vocaBook.getName());
        }
        alertDialog = new AlertDialog(context);
        downloadingVoiceFilesDialog = new DownloadingVoiceFilesDialog(context);
        binding.scUploadRecordingAutomatically.setChecked(isUploadRecordingAutomatically());
    }

    private void updateTextViewRight() {
        if (binding.header != null) {
            binding.header.setTextRight(isAll ? R.string.record : R.string.all);
        }
    }

    protected void updateRecordCountText() {
        String text = getString(R.string.tpl_wb_recorded_word_count, String.valueOf(recordedCount), String.valueOf(totalCount));
        binding.tvRecordCount.setText(text);
    }

    protected void getData(final boolean isLoadMore) {
        if (vocaBook != null) {
            final int uid = getUserID();
            if (uid > 0) {
                if (Utils.isConnected(context)) {
                    isLoading = true;
                    binding.rvVoca.post(() -> {
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
                                String.valueOf(vocaBook.getId()),
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
                                            voca.setVIPath(BaseVoca.getOutputRecordingFileName(studyLang, voca.getVocaType(), voca.getVocaId(), uid));
                                            allVocas.add(voca);
                                            newVocas.add(voca);
                                            if (!voca.hasVIVoiceFile()) {
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

    protected void onGetDataFinish() {
        bindData();
        updateVisibilityVideoListAndMessage();
        //Don't download recorded voice files in this activity
//        playTTS.checkVersionAndDownload(newVocas);
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
        playTTS.playTTSHelper.initMediaPlayer();

        if (!playTTS.playTTSHelper.hasMotherTongueListener()) {
            playTTS.playTTSHelper.setMotherTongueListener(new UtteranceProgressListener() {

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
        if (!playTTS.playTTSHelper.hasStudyListener()) {
            playTTS.playTTSHelper.setStudyListener(new UtteranceProgressListener() {

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
        if (!playTTS.playTTSHelper.hasOnPlayMyVoiceOnceListener()) {
            playTTS.playTTSHelper.setOnPlayMyVoiceOnceListener(new OnPlayMyVoiceOnce() {
                @Override
                public void onDone(String utteranceId) {
                    playTTS.stopMyVoiceOnce();
                    IVocaFullPlayTTSItem voca = adapter.getItem(Integer.parseInt(utteranceId));
                    updateSpeakerUIStatus(voca, false);
                }
            });
        }
    }
    private void updateItemStatus(String utteranceId, boolean playing) {
        for (VocaInBook voca : allVocas) {
            if (utteranceId.equals(String.valueOf(voca.getVIId()))) {
                voca.setVIPlaying(playing);
                binding.rvVoca.post(() -> adapter.notifyVocaChanged((VocaInBook) voca));
                break;
            }
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

    private void updateSpeakerUIStatus(IVocaFullPlayTTSItem voca, boolean playing) {
        if (adapter.getItem(voca) != null) {
            BaseVoca.setIsPlayingTTS(recordVocas, voca, playing);
            voca.setVIPlaying(playing);
            adapter.notifyVocaChanged(voca);
//            voca.setVIPlaying(playing);
//            adapter.notifyDataSetChanged();
//            int pos = adapter.notifyPlaylistItemChanged(voca);
        }
    }

    private OnClickListener onClickListener = new OnClickListener() {
        @Override
        public void onClick(View view, Object data) {
            IVocaFullPlayTTSItem voca = (IVocaFullPlayTTSItem) data;
            switch (view.getId()) {
                case R.id.ivSpeaker:
                    stopRecordingAndResetMicIcon();
                    if (recordingVoca == null) {
//                        setRecordFile(voca);
                        playTTS.playTTSHelper.isPlayTTSAtFirst = false;

                        if (playTTS.canPlay()) {
                            updateSpeakerUIStatus(voca, true);
                            playTTS.playMyVoiceOnce(voca);
                        } else {
                            playTTS.stopMyVoiceOnce();
                            updateSpeakerUIStatus(voca, false);
                        }
                    }
                    break;
                case R.id.ivMic:
                    stopPlayingAdResetSpeakerIcon();
                    if (recordingVoca == null) {
                        recordingVoca = (VocaInBook) voca;
                    }
                    boolean wasRecording = voca.isVIRecording();
                    setRecordFile(voca);
                    voiceRecording.onClickMic();
                    if (voiceRecording.recording) {
                        updateMicUIStatus(voca, true);
                    } else {
                        updateMicUIStatus(voca, false);
                        // remove from record list
                        if (recordVocasReal.remove(recordingVoca)) {
                            recordedCount++;
                            updateRecordCountText();
                        }
                    }

                    if (wasRecording) {
                        playTTS.stopPlaylist();
                        if (isUploadRecordingAutomatically()) {
                            fileUploadHelper.uploadVoiceFile(recordingVoca, BaseVoca.onVoiceFileInfoDownloadListener);
                            ToastUtil.getInstance(getBaseContext()).show(R.string.toast_listen_my_recording_while_uploading_to_server);
                        } else {
                            ToastUtil.getInstance(getBaseContext()).show(R.string.toast_listen_my_recording);
                            updateRealmDbAfterRecording(recordingVoca);
                        }
                        if (playTTS.canPlay()) {
                            playTTS.playMyVoiceOnce(voca);
                        }
                        updateModelAfterRecording(recordingVoca); //recordingVoca.setVIVoiceFile(Constant.INT_BOOLEAN.TRUE);
                        updateSqlDbAfterRecording(recordingVoca);
                        adapter.notifyVocaChanged(recordingVoca);
                        recordingVoca = null;
                    }
                    break;
            }
        }
    };

    protected void updateSqlDbAfterRecording(IVocaFullPlayTTSItem voca) {

    }

    private void updateModelAfterRecording(IVocaFullPlayTTSItem voca) {
        voca.setVIVoiceFile(Constant.INT_BOOLEAN.TRUE);
    }

    private void updateRealmDbAfterRecording(IVocaFullPlayTTSItem voca) {
        String voiceFileName = BaseVoca.getMyOutputRecordingFileName(this, voca);
        VoiceFileDownloadHelper downloadFileHelper = new VoiceFileDownloadHelper(getBaseContext(), true, onDownloadFileListener);
        downloadFileHelper.getServerVoiceFileInfo(voiceFileName, BaseVoca.onVoiceFileInfoDownloadListener);
    }

    private void stopRecordingAndResetMicIcon() {
        if (voiceRecording.recording) {
            voiceRecording.stopRecording();
            BaseVoca.resetIsRecordingVoca(recordVocas);
            adapter.notifyDataSetChanged();
            recordingVoca = null;
        }
    }

    private void stopPlayingAdResetSpeakerIcon() {
        if (playTTS.isPlaying()) {
            playTTS.stopPlayVoca();
            BaseVoca.resetIsPlayingTTS(recordVocas);
            adapter.notifyDataSetChanged();
        }
    }

    public void setRecordFile(IVocaFullPlayTTSItem voca) {
        voiceRecording.recordFile = BaseVoca.getVoiceFileInApp(getApplicationContext(), voca.getVIPath());
    }

    private void updateMicUIStatus(IVocaFullPlayTTSItem voca, boolean recording) {
        if (adapter.getItem(voca) != null) {
            BaseVoca.setIsRecordingTTS(recordVocas, voca, recording);
            voca.setVIRecording(recording);
            if (recording)
                adapter.setIsRecordingVoca(voca);
            else
                adapter.setIsRecordingVoca(null);
        }
    }

//    @OnClick(R.id.ic_play_all)
//    void onClick() {
//        if (recordingVoca == null) {
////            Intent intent = new Intent(context, PlaylistNativeSpeakerActivity.class);
////            ArrayList<IVocaFullPlayTTSItem> items = new ArrayList<>();
////            for (VocaRecordInBook voca : allVocas) {
////                if (recordVocasReal.indexOf(voca) == -1) {
////                    items.add(voca);
////                }
////            }
////            intent.putExtra(Constant.BUNDLE.KEY_VOCA_PLAYLIST, items);
////            openNewScreen(intent);
//        }
//    }

    @Override
    public void onBackPressed() {
        if (recordingVoca == null) {
            super.onBackPressed();
        }
    }

    private void showIntroductionToast() {
        AlertDialog alertDialog = new AlertDialog(context);
        alertDialog.show(context.getString(R.string.recording_tap_to_stop_and_upload_file), null, null);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ivHelp:
                showIntroductionToast();
                break;
            case R.id.ivDownloadFile:
                popupToDownloadFile();
                break;
        }
    }

    private void popupToDownloadFile() {
        final YesNoDialog dialog = new YesNoDialog(this, R.string.info, R.string.dialog_message_download_my_voice_file, null, new OnYesNoClickListener() {
            @Override
            public void onYesClick(View view, Object object) {
                VoiceFileDownloadHelper downloadFileHelper = new VoiceFileDownloadHelper(getBaseContext(),true, onDownloadFileListener);
                downloadFileHelper.checkVersionAndDownloadByUid(newVocas, String.valueOf(getUserID()), BaseVoca.onVoiceFileInfoDownloadListener);
            }

            @Override
            public void onNoClick(View view, Object object) {

            }
        });
        dialog.show();
    }

    private OnDownloadFileListener onDownloadFileListener = new OnDownloadFileListener() {
        int fileCountToDownload;
        @Override
        public void fileCountToDownload(int count) {
            if (count == 0) {
                ToastUtil.getInstance(getBaseContext()).show(R.string.toast_no_data_to_download);
            } else {
                fileCountToDownload = count;
                downloadingVoiceFilesDialog.setMax(count);
                downloadingVoiceFilesDialog.show();
            }
        }

        @Override
        public void onDownloadOneItemSuccess(int index, String fileName) {
            downloadingVoiceFilesDialog.setProgress(index+1);
        }

        @Override
        public void onDownloadOneItemFailure(int index, String fileName) {
            downloadingVoiceFilesDialog.setProgress(index+1);
        }

        @Override
        public void onFinish() {
            if ((downloadingVoiceFilesDialog != null) && (downloadingVoiceFilesDialog.isShowing())) {
                downloadingVoiceFilesDialog.dismiss();
//                getData(true);
//                onGetDataFinish();
            }
            if (fileCountToDownload > 0) {
                getData(true);
                onGetDataFinish();
            }
        }
    };

    private void updateVisibilityVideoListAndMessage() {
        if (isAll) {
            binding.rvVoca.setVisibility(View.VISIBLE);
            binding.tvNoItemToRecordVoice.setVisibility(View.GONE);
        } else {
            if (recordVocas.size() > 0) {
                binding.rvVoca.setVisibility(View.VISIBLE);
                binding.tvNoItemToRecordVoice.setVisibility(View.GONE);
            } else {
                binding.rvVoca.setVisibility(View.GONE);
                binding.tvNoItemToRecordVoice.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
        int id = compoundButton.getId();
        switch (id) {
            case R.id.scUploadRecordingAutomatically:
                sharedPreferences.setUploadRecordingAutomatically(isChecked);
                break;
        }
    }

    private boolean isUploadRecordingAutomatically() {
        return sharedPreferences.isUploadRecordingAutomatically();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        BasePermissionUtils.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }
}
