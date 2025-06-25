package com.dalread.activity;

import android.os.Bundle;

import com.dalread.base.BaseVocaListRecordingActivity;
import com.dalread.base.EnumLanguage;
import com.dalread.database.sqlite.SubDatabase;
import com.dalread.interfaces.IVocaFullPlayTTSItem;
import com.dalread.model.VocaInBook;
import com.dalread.util.BaseStorageUtil;
import com.dalread.util.Constant;
import com.dalread.util.DLog;
import com.dalread.util.Loading;
import com.dalread.util.Voca;

import java.util.ArrayList;
import java.util.List;

public class VocaListRecordingActivity extends BaseVocaListRecordingActivity {
//    private static MainHomeActivity activity;
//    public static Intent createIntent(Activity activity, VocaBook book) {
//        VocaListRecordingActivity.activity = (MainHomeActivity) activity;
//        Intent intent = new Intent(activity, VocaListRecordingActivity.class);
//        intent.putExtra(Constant.BUNDLE.KEY_VOCA_BOOK, book);
//        return intent;
//    }
    private SubDatabase subDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        initData();
    }

    @Override
    protected void initData() {
        super.initData();
        initSubDatabase();
    }

    private void initSubDatabase() {
        if (subDatabase != null) {
            subDatabase.close();
        }
        subDatabase = null;
        String destPathWithFileName = BaseStorageUtil.getAraConvDBPathWithFileName(context);
        subDatabase = SubDatabase.getInstance(this, destPathWithFileName);
    }

    @Override
    protected void updateSqlDbAfterRecording(IVocaFullPlayTTSItem voca) {
        subDatabase.setHasVoiceFile(voca, Constant.INT_BOOLEAN.TRUE);
    }

    @Override
    protected void getData(final boolean isLoadMore) {
//        super.getData(isLoadMore);
        final int uid = getUserID();
        isLoading = true;
        binding.rvVoca.post(() -> {
            //TODO : Need to support LoadMore later.
//            if (isLoadMore) {
//                adapter.setLoadMore(true);
//            } else {
                allVocas.clear();
                recordVocas.clear();
                recordVocasReal.clear();
                loadingPos = 0;
                Loading.show(context);
//            }


            List<VocaInBook> vocaInBookList = subDatabase.getVocasFromAllVocaBook(String.valueOf(vocaBook.getIBookId()));
            Voca.resetVocaList(vocaInBookList);
            Voca.udpateVoiceFileInVocaList(getBaseContext(), vocaInBookList);
            isLoading = false;
            recordedCount = 0;
            totalCount = vocaInBookList.size();
            newVocas = new ArrayList<>();
            int studyLangCode = EnumLanguage.findByFormatApi(sharedPreferences.getStudyLanguage()).getIdApi();
            for (VocaInBook voca : vocaInBookList) {
                voca.setVIPath(Voca.getOutputRecordingFileName(studyLangCode, voca.getVocaType(), voca.getVocaId(), uid));
                allVocas.add(voca);
                newVocas.add(voca);
                if (voca.hasVIVoiceFile()) {
                    recordedCount++;
                } else {
                    recordVocas.add(voca);
                    recordVocasReal.add(voca);
                }
            }
            updateRecordCountText();
            loadingPos = allVocas.size();
            DLog.i(getLogTag(), "loadingPos = " + loadingPos);
//            if (isLoadMore) {
//                adapter.setLoadMore(false);
//            } else {
                Loading.hide();
//            }
            onGetDataFinish();
        });
    }

}
