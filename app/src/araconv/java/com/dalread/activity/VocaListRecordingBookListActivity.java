package com.dalread.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.dalread.R;
import com.dalread.asyntask.CustomAsyncTask;
import com.dalread.base.BaseVocaListRecordingBookListActivity;
import com.dalread.interfaces.IVocaFullPlayTTSItem;
import com.dalread.listener.OnAsyncTaskListenerWithType;
import com.dalread.model.VocaBook;
import com.dalread.util.BaseStorageUtil;
import com.dalread.util.FileUtil;
import com.dalread.util.Loading;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class VocaListRecordingBookListActivity extends BaseVocaListRecordingBookListActivity implements OnAsyncTaskListenerWithType {
    private static MainHomeActivity activity;
    public static Intent createIntent(Activity activity) {
        VocaListRecordingBookListActivity.activity = (MainHomeActivity) activity;
        Intent intent = new Intent(activity, VocaListRecordingBookListActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected void getData() {
        callAsyncTask(TYPE_INIT_DATA, null);
    }
    private void getServerVocaBookListByRecordingList() {
        vocaBookList = activity.subDatabase.getServerVocaBookListByRecordingList();
    }

    private void updateRecordedVoiceCountInVocaBookList() {
        for(VocaBook vocaBook : vocaBookList) {
            int cntOfRecordedVoiceInVocaBook = 0;
            List<IVocaFullPlayTTSItem> vocaList = activity.subDatabase.getVocaListByBookIdInVocaBook((int) vocaBook.getIBookId());
            Map<String, IVocaFullPlayTTSItem> mapVoca = vocaList.stream()
                    .collect(Collectors.toMap(e -> e.getVIVocaType() + "_" + e.getVIVocaId(), e -> e, (p1, p2) -> p1));
            for(String vocaTypeIdInFile : recordedVoiceFileList) {
                if (mapVoca.containsKey(vocaTypeIdInFile)) {
                    IVocaFullPlayTTSItem item = mapVoca.get(vocaTypeIdInFile);
                    cntOfRecordedVoiceInVocaBook++;
                }
            }
            vocaBook.setIBookRecordedVocaCount(cntOfRecordedVoiceInVocaBook);
        }
    }

    private void getRecordedVoiceCount() {
        recordedVoiceFileList = getVocaTypeIdListFromFileName();
        updateRecordedCount(recordedVoiceFileList.size());
    }

    @NotNull
    private List<String> getVocaTypeIdListFromFileName() {
        List<String> vocaTypeIdFileNameList = new ArrayList<>();
        List<File> recordedVoiceFileList = BaseStorageUtil.getRecordedVoiceFileList(activity);
        for(File file : recordedVoiceFileList) {
            vocaTypeIdFileNameList.add(FileUtil.getVocaType_IdFromFileName(file.getName()));
        }
        return vocaTypeIdFileNameList;
    }

    private void callAsyncTask(int type, Object data) {
        new CustomAsyncTask(this, this, data, type, true).execute();
    }

    @Override
    public void onInitAsyncTask(int searchType) {
        switch (searchType) {
            case TYPE_INIT_DATA:
                Loading.showDelay(this);
                break;
            case TYPE_SHOW_RECORDED_COUNT:
                Loading.show(this, R.string.toast_get_recorded_voice_count);
                break;
        }

    }

    @Override
    public void onInitAsyncTask() {

    }

    @Override
    public void onPrevExecuteAsyncTask() {

    }

    @Override
    public Object onPostExecuteAsyncTask(int searchType, Object data) {
        switch (searchType) {
            case TYPE_INIT_DATA:
                getServerVocaBookListByRecordingList();
                getRecordedVoiceCount();
                break;
            case TYPE_SHOW_RECORDED_COUNT:
                updateRecordedVoiceCountInVocaBookList();
                break;
        }
        return null;
    }

    @Override
    public void onFinishAsyncTask(int searchType, Object resultData, Object data) {
        switch (searchType) {
            case TYPE_INIT_DATA:
            case TYPE_SHOW_RECORDED_COUNT:
                adapter.setBooks(vocaBookList);
                binding.rvBook.setAdapter(adapter);
                break;
        }
        Loading.hide();
    }


    protected void hideUnusedUI() {
        binding.ivShowRecordedCount.setVisibility(View.VISIBLE);
    }

    protected void onClickShowRecordedCount() {
        callAsyncTask(TYPE_SHOW_RECORDED_COUNT, null);
    }
}
