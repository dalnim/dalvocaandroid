package com.dalread.activity;

import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.dalread.R;
import com.dalread.adapter.PlayListByTtsAdapter;
import com.dalread.base.BaseWordInfoFragment;
import com.dalread.base.EnumLanguage;
import com.dalread.component.CenterLayoutManager;
import com.dalread.component.SeparatorDecoration;
import com.dalread.dialog.ChoosePlaylistDialog;
import com.dalread.dialog.SelectRangeDialog;
import com.dalread.listener.OnPlaylistItemClickListener;
import com.dalread.model.BackupRecording;
import com.dalread.model.VocaDetailInfo;
import com.dalread.util.BasePermissionUtils;
import com.dalread.util.BaseVoca;
import com.dalread.util.Constant;
import com.dalread.util.DLog;
import com.dalread.util.Loading;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindColor;
import butterknife.BindDimen;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.OnClick;

public class BackupRecordingListFragment extends BaseWordInfoFragment {

    @BindView(R.id.rv)
    RecyclerView rv;
    @BindView(R.id.tvSelectByRange)
    TextView tvStatus;
    @BindView(R.id.ic_play)
    ImageView icPlay;
    @BindView(R.id.ic_pause)
    ImageView icPause;
    @BindView(R.id.ic_stop)
    ImageView icStop;

    @BindColor(R.color.color_divider)
    int clDivider;
    @BindColor(R.color.colorBlack)
    int clEnable;
    @BindColor(R.color.color_stop)
    int clDisable;

    @BindDimen(R.dimen.divider_height)
    float dividerHeight;
    @BindString(R.string.play_index)
    String tplIndex;

    private static final int STATE_STOP = 0;
    private static final int STATE_PAUSE = STATE_STOP + 1;
    private static final int STATE_PLAY_SINGLE = STATE_PAUSE + 1;
    private static final int STATE_PLAY_MULTI = STATE_PLAY_SINGLE + 1;

    private int studyLang;
    private int uid;
    private VocaDetailInfo voca;
    private CollectionReference collectionReference;
    private File voiceFolder;
    private File endListFile;
    private CenterLayoutManager layoutManager;
    private PlayListByTtsAdapter adapter;
    private ArrayList<BackupRecording> fullList;
    private ArrayList<BackupRecording> playlistList;
    private ArrayList<Boolean> fullCheckedList;
    private boolean checkedChanged;
    private int checkedCount;
    private int playingState;
    private ChoosePlaylistDialog choosePlaylistDialog;
    private SelectRangeDialog selectRangeDialog;
    private MediaPlayer mediaPlayer;
    private int playingPos;
    private int downloadingPos;
    private int prerequisite;
    private BackupRecording playingRecording;

    @Override
    public void requestGetData() {
        getInfoList();
    }

    @Override
    protected int getContentViewId() {
        return R.layout.fragment_backup_recording_list;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initData();
        initLayout();

        getInfoList();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (requestCode > 0 && requestCode < fullList.size()) {
                playSingle(fullList.get(requestCode));
            } else {
                playPlaylist();
            }
        }
    }

    @Override
    public void onDestroy() {
        mediaPlayer.release();
        mediaPlayer = null;

        super.onDestroy();
    }

    private void initData() {
        studyLang = EnumLanguage.findByFormatApi(sharedPreferences.getStudyLanguage()).getIdApi();
        uid = sharedPreferences.getRealUid();
        Bundle bundle = getArguments();
        if (bundle != null) {
            voca = (VocaDetailInfo) bundle.getSerializable(Constant.BUNDLE.KEY_VOCA);
            if (voca != null) {
                collectionReference = FirebaseFirestore.getInstance().collection("backup_voice/" + studyLang + "/" + uid);
            }
        }
        fullList = new ArrayList<>();
        playlistList = new ArrayList<>();
        fullCheckedList = new ArrayList<>();
        voiceFolder = BaseVoca.getVoiceFolderOnLocal(getContext());
        endListFile = BaseVoca.getVoiceFileOnLocal(voiceFolder, Constant.FILE.END_LIST_FILE_NAME);
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        playingPos = -1;
        downloadingPos = -1;
    }

    private void initLayout() {
        adapter = new PlayListByTtsAdapter(activity,false);
//        adapter.setListener(onPlaylistItemClickListener);
        rv.setAdapter(adapter);
        rv.setLayoutManager(layoutManager = new CenterLayoutManager(getContext()));
        rv.addItemDecoration(new SeparatorDecoration(getContext(), clDivider, dividerHeight));
        choosePlaylistDialog = new ChoosePlaylistDialog(getContext(), choosePlaylistListener);
        selectRangeDialog = new SelectRangeDialog(getContext(), selectRangeListener);
    }

    private void getInfoList() {
        if (collectionReference != null) {
            Loading.show(getContext());
            String vocaFileName = voca.getVIPath();
            collectionReference.whereEqualTo("VOCA_FILENAME", vocaFileName)
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {

                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            parseInfoList(queryDocumentSnapshots.getDocuments());
                            bindInfoList();
                            Loading.hide();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {

                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Loading.hide();
                        }
                    });
        }
    }

    private void parseInfoList(List<DocumentSnapshot> documents) {
        for (DocumentSnapshot document : documents) {
            BackupRecording backupRecording = document.toObject(BackupRecording.class);
            if (backupRecording != null) {
                fullList.add(backupRecording);
                playlistList.add(backupRecording);
                fullCheckedList.add(true);
                backupRecording.setChecked(true);
                backupRecording.setIndex(fullList.size());
            }
        }
        checkedCount = playlistList.size();
    }

    private void bindInfoList() {
        adapter.setBackupRecordingData(fullList);
        adapter.notifyDataSetChanged();
    }

    @OnClick({R.id.ic_play, R.id.ic_pause, R.id.ic_stop, R.id.tvSelectByRange})
    void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.ic_play:
                tvStatus.setVisibility(View.INVISIBLE);
                if (playingState == STATE_STOP) {
                    if (checkedCount > 0) {
                        updatePlaylist();
                        displayPlaylistItems();
                        playPlaylist();
                    }
                } else if (playingState == STATE_PAUSE) {
                    resumePlaylist(-1);
                }
                break;
            case R.id.ic_pause:
                if (playingState == STATE_PLAY_MULTI) {
                    pausePlaylist();
                }
                break;
            case R.id.ic_stop:
                tvStatus.setVisibility(View.VISIBLE);
                if (playingState == STATE_PLAY_SINGLE) {
                    stopPlaylist();
                } else if (playingState == STATE_PAUSE || playingState == STATE_PLAY_MULTI) {
                    stopPlaylist();
                    displayFullItems();
                }
                break;
            case R.id.tvSelectByRange:
                if (playingState == STATE_STOP) {
                    choosePlaylistDialog.show();
                }
                break;
        }
    }

    private OnPlaylistItemClickListener onPlaylistItemClickListener = new OnPlaylistItemClickListener() {

        @Override
        public void onItemClick(Object item) {
            BackupRecording backupRecording = (BackupRecording) item;
            if (playingState == STATE_STOP) {
                backupRecording.setChecked(!backupRecording.isChecked());
                adapter.notifyPlaylistItemChanged(backupRecording);

                checkedChanged = true;
                boolean checked = backupRecording.isChecked();
                if (checked) {
                    checkedCount++;
                } else {
                    checkedCount--;
                }

                if (checkedCount == fullList.size()) {
                    updateStateAllText();
                } else if (checkedCount == 0) {
                    updateStateNoneText();
                } else {
                    updateStateLastText();
                    fullCheckedList.set(fullList.indexOf(backupRecording), checked);
                }
            }
        }

        @Override
        public void onPlayClick(Object item) {
            BackupRecording backupRecording = (BackupRecording) item;
            if (playingState == STATE_STOP) {
                playSingle(backupRecording);
            } else if (playingState == STATE_PAUSE) {
                resumePlaylist(backupRecording.isPlaying() ? -1 : playlistList.indexOf(backupRecording));
            } else if (playingState == STATE_PLAY_SINGLE) {
                boolean isPlaying = backupRecording.isPlaying();
                stopPlaylist();
                if (!isPlaying) {
                    playSingle(backupRecording);
                }
            }
        }
    };

    private DialogInterface.OnClickListener choosePlaylistListener = new DialogInterface.OnClickListener() {

        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case R.id.tv_all:
                    if (checkedCount < fullList.size()) {
                        updateStateAllText();
                        updateStateAllData();
                    }
                    break;
                case R.id.tv_none:
                    if (checkedCount > 0) {
                        updateStateNoneText();
                        updateStateNoneData();
                    }
                    break;
                case R.id.tv_range:
                    selectRangeDialog.show(1, fullList.size());
                    break;
                case R.id.tv_last:
                    if (checkedCount == 0 || checkedCount == fullList.size()) {
                        updateStateLastText();
                        updateStateLastData();
                    }
                    break;
                default:
                    break;
            }
        }
    };

    private SelectRangeDialog.OnRangeSelectListener selectRangeListener = new SelectRangeDialog.OnRangeSelectListener() {

        @Override
        public void onSelect(int from, int to) {
            updateStateRangeText();
            updateStateRangeData(from, to);
        }

        @Override
        public void onDismiss(View v) {

        }
    };

    private void updatePlaylist() {
        if (checkedChanged) {
            checkedChanged = false;
            playlistList.clear();
            for (BackupRecording backupRecording : fullList) {
                if (backupRecording.isChecked()) {
                    playlistList.add(backupRecording);
                }
            }
        }
    }

    private void displayPlaylistItems() {
        adapter.setBackupRecordingData(playlistList);
        adapter.notifyDataSetChanged();
    }

    private void displayFullItems() {
        adapter.setBackupRecordingData(fullList);
        adapter.notifyDataSetChanged();
        rv.post(new Runnable() {

            @Override
            public void run() {
                layoutManager.smoothScrollToPosition(rv, null, 0);
            }
        });
    }

    private void updateStateAllText() {
        tvStatus.setText(R.string.select_all);
        icPlay.setColorFilter(clEnable);
    }

    private void updateStateAllData() {
        playlistList.clear();
        for (BackupRecording backupRecording : fullList) {
            backupRecording.setChecked(true);
            playlistList.add(backupRecording);
        }
        checkedCount = fullList.size();
        checkedChanged = false;
        adapter.notifyDataSetChanged();
    }

    private void updateStateNoneText() {
        tvStatus.setText(R.string.unselect_all);
        icPlay.setColorFilter(clDisable);
    }

    private void updateStateNoneData() {
        playlistList.clear();
        for (BackupRecording backupRecording : fullList) {
            backupRecording.setChecked(false);
        }
        checkedCount = 0;
        checkedChanged = false;
        adapter.notifyDataSetChanged();
    }

    private void updateStateRangeText() {
        tvStatus.setText(R.string.select_range_title);
        icPlay.setColorFilter(clEnable);
    }

    private void updateStateRangeData(int from, int to) {
        if (from < 1) {
            from = 1;
        }
        if (to < 1) {
            to = 1;
        }
        int size = fullList.size();
        if (from > size) {
            from = size;
        }
        if (to > size) {
            to = size;
        }
        if (to < from) {
            int temp = from;
            from = to;
            to = temp;
        }
        playlistList.clear();
        fullCheckedList.clear();
        checkedCount = 0;
        from--;
        to--;
        for (int i = 0; i < size; i++) {
            BackupRecording backupRecording = fullList.get(i);
            if (from <= i && i <= to) {
                backupRecording.setChecked(true);
                playlistList.add(backupRecording);
                fullCheckedList.add(true);
                checkedCount++;
            } else {
                backupRecording.setChecked(false);
                fullCheckedList.add(false);
            }
        }
        checkedChanged = false;
        adapter.notifyDataSetChanged();
    }

    private void updateStateLastText() {
        tvStatus.setText(R.string.last_selected);
        icPlay.setColorFilter(clEnable);
    }

    private void updateStateLastData() {
        playlistList.clear();
        checkedCount = 0;
        int count = fullList.size();
        for (int i = 0; i < count; i++) {
            BackupRecording backupRecording = fullList.get(i);
            boolean checked = fullCheckedList.get(i);
            backupRecording.setChecked(checked);
            if (checked) {
                playlistList.add(backupRecording);
                checkedCount++;
            }
        }
        checkedChanged = false;
        adapter.notifyDataSetChanged();
    }

    private void playSingle(BackupRecording backupRecording) {
        if (BasePermissionUtils.checkWriteExternalStorage(activity, fullList.indexOf(backupRecording))) {
            ArrayList<BackupRecording> backupRecordings = new ArrayList<>();
            backupRecordings.add(backupRecording);
            preparePlayRecordingList(backupRecordings);
            playingState = STATE_PLAY_SINGLE;
            updateIcon();
        }
    }

    private void playPlaylist() {
        if (BasePermissionUtils.checkWriteExternalStorage(activity, fullList.size())) {
            preparePlayRecordingList(playlistList);
            playingState = STATE_PLAY_MULTI;
            updateIcon();
        }
    }

    private void pausePlaylist() {
        mediaPlayer.setOnCompletionListener(null);
        mediaPlayer.stop();
        mediaPlayer.reset();
        playingState = STATE_PAUSE;
        updateIcon();
    }

    private void resumePlaylist(int pos) {
        if (pos >= 0) {
            playingPos = pos - 1;
            if (playingRecording != null) {
                playingRecording.setPlaying(false);
                adapter.notifyPlaylistItemChanged(playingRecording);
                playingRecording = null;
            }
        } else {
            playingPos--;
        }
        playRecordingList(playlistList);
        playingState = STATE_PLAY_MULTI;
        updateIcon();
    }

    private void stopPlaylist() {
        if (playingRecording != null) {
            playingRecording.setPlaying(false);
            adapter.notifyPlaylistItemChanged(playingRecording);
            playingRecording = null;
        }
        mediaPlayer.setOnCompletionListener(null);
        mediaPlayer.stop();
        mediaPlayer.reset();
        playingPos = -1;
        downloadingPos = -1;
        playingState = STATE_STOP;
//        toolbar.setTitle(R.string.play_all_words);
        updateIcon();
    }

    private void updateIcon() {
        switch (playingState) {
            case STATE_STOP:
                icPlay.setColorFilter(clEnable);
                icPause.setColorFilter(clDisable);
                icStop.setColorFilter(clDisable);
                break;
            case STATE_PAUSE:
                icPlay.setColorFilter(clEnable);
                icPause.setColorFilter(clDisable);
                icStop.setColorFilter(clEnable);
                break;
            case STATE_PLAY_SINGLE:
                icPlay.setColorFilter(clDisable);
                icPause.setColorFilter(clDisable);
                icStop.setColorFilter(clEnable);
                break;
            case STATE_PLAY_MULTI:
                icPlay.setColorFilter(clDisable);
                icPause.setColorFilter(clEnable);
                icStop.setColorFilter(clEnable);
                break;
        }
    }

    private void preparePlayRecordingList(final ArrayList<BackupRecording> backupRecordings) {
        int maxPrerequisite = Math.min(Constant.NUMBER_OF_RECORDINGS_PREREQUISITE, backupRecordings.size());
        prerequisite = maxPrerequisite;
        for (int i = 0; i < maxPrerequisite; i++) {
            BackupRecording backupRecording = backupRecordings.get(i);
            final File file = BaseVoca.getBackupFileOnLocal(voiceFolder, studyLang, uid, backupRecording.getFILE_NAME());
            if (file.exists()) {
                prerequisite--;
            } else {
                if (!Loading.isShowing()) {
                    Loading.show(getContext());
                }
                final String fileName = file.getName();
                final StorageReference storageReference = FirebaseStorage.getInstance()
                        .getReference()
                        .child(
                                BaseVoca.getBackupPathOnFirebase(studyLang, uid) + fileName
                        );
                storageReference.getFile(file)
                        .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {

                            @Override
                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                DLog.i(getLogTag(), "Download " + fileName + " onSuccess");
                                prerequisite--;
                                downloadRemainAndPlayRecordingList(backupRecordings);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {

                            @Override
                            public void onFailure(@NonNull Exception e) {
                                DLog.i(getLogTag(), "Download " + fileName + " onFailure");
                                prerequisite--;
                                downloadRemainAndPlayRecordingList(backupRecordings);
                            }
                        });
            }
        }
        downloadRemainAndPlayRecordingList(backupRecordings);
    }

    private void downloadRemainAndPlayRecordingList(ArrayList<BackupRecording> backupRecordings) {
        if (prerequisite <= 0) {
            Loading.hide();
            downloadingPos = Constant.NUMBER_OF_RECORDINGS_PREREQUISITE;
            downloadRemainRecordingList(backupRecordings);
            playRecordingList(backupRecordings);
        }
    }

    private void downloadRemainRecordingList(final ArrayList<BackupRecording> backupRecordings) {
        if (activity == null || activity.isFinishing() || activity.isDestroyed())
            return;
        if (downloadingPos >= 0 && downloadingPos < backupRecordings.size()) {
            BackupRecording backupRecording = backupRecordings.get(downloadingPos);
            File file = BaseVoca.getBackupFileOnLocal(voiceFolder, studyLang, uid, backupRecording.getFILE_NAME());
            final String fileName = file.getName();
            final StorageReference storageReference = FirebaseStorage.getInstance()
                    .getReference()
                    .child(
                            BaseVoca.getBackupPathOnFirebase(studyLang, uid) + fileName
                    );
            storageReference.getFile(file)
                    .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {

                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            DLog.i(getLogTag(), "Download " + fileName + " onSuccess");
                            downloadingPos++;
                            downloadRemainRecordingList(backupRecordings);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {

                        @Override
                        public void onFailure(@NonNull Exception e) {
                            DLog.i(getLogTag(), "Download " + fileName + " onFailure");
                            downloadingPos++;
                            downloadRemainRecordingList(backupRecordings);
                        }
                    });
        }
    }

    private void playRecordingList(final ArrayList<BackupRecording> backupRecordings) {
        if (++playingPos >= backupRecordings.size()) {
            playingPos = 0;
            if (playingState == STATE_PLAY_MULTI) {
                try {
                    Uri uri = Uri.fromFile(endListFile);
                    mediaPlayer.setDataSource(getContext(), uri);
                    mediaPlayer.prepare();
                    mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            if (mp != null) {
                                mp.reset();
                            }
                            DLog.i(getLogTag(), "Playing " + (playingPos + 1) + "/" + backupRecordings.size());
                            play(backupRecordings.get(playingPos), backupRecordings);
                        }
                    });
                    mediaPlayer.start();
                    return;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        DLog.i(getLogTag(), "Playing " + (playingPos + 1) + "/" + backupRecordings.size());
        play(backupRecordings.get(playingPos), backupRecordings);
    }

    private void play(final BackupRecording backupRecording, final ArrayList<BackupRecording> backupRecordings) {
        final File file = BaseVoca.getBackupFileOnLocal(voiceFolder, studyLang, uid, backupRecording.getFILE_NAME());
        if (file != null && file.exists()) {
            try {
                Uri uri = Uri.fromFile(file);
                mediaPlayer.setDataSource(getContext(), uri);
                mediaPlayer.prepare();
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        if (mp != null) {
                            mp.reset();
                        }
                        backupRecording.setPlaying(false);
                        adapter.notifyPlaylistItemChanged(backupRecording);
                        if (playingState > STATE_PAUSE) {
                            playRecordingList(backupRecordings);
                        }
                    }
                });
                mediaPlayer.start();
                backupRecording.setPlaying(true);
                adapter.notifyPlaylistItemChanged(backupRecording);
                playingRecording = backupRecording;
            } catch (Exception e) {
                e.printStackTrace();
                playRecordingList(backupRecordings);
            }
        } else {
            playRecordingList(backupRecordings);
        }
    }
}
