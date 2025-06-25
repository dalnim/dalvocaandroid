package com.dalread.helper;

import android.content.Context;

import com.dalread.database.sqlite.SubDatabase;
import com.dalread.database.sqlite.model.DicModel;
import com.dalread.model.PlayerFileModel;
import com.dalread.util.SubtitlePositionTimeUtil;
import com.dalread.util.Utils;
import com.dalread.util.Voca;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SubtitleListHelper {
    private List<DicModel> subtitleListTotal; //SQLite에 있는 전체 자막 (숨김 자막도 포함)
    private List<DicModel> subtitleList;
    private SubDatabase subDatabase;
    private PlayerFileModel playerFileModel;
    private Context context;

    public SubtitleListHelper(Context context, SubDatabase subDatabase, PlayerFileModel playerFileModel) {
        this.context = context;
        this.subDatabase = subDatabase;
        this.playerFileModel = playerFileModel;
        subtitleListTotal = new ArrayList<>();
        subtitleList = new ArrayList<>();
    }

//    public SubtitleListHelper(SubDatabase subDatabase) {
//        this.subDatabase = subDatabase;
//        initData();
//    }

    public void initData() {
        initSubtitleListTotal();
        subtitleList = subtitleListTotal;
        SubtitlePositionTimeUtil.resetSubtitlePosition(subtitleListTotal);
    }

    private void initSubtitleListTotal() {
        subtitleListTotal = subDatabase.getSubtitleDialogList();
        for(DicModel dicModel : subtitleListTotal) {
            addRecordedFileInDicModel(dicModel);
            dicModel.setVIChecked(dicModel.isShowUsed() ? true : false);
        }
    }

    private void addRecordedFileInDicModel(DicModel d1) {
        File recordedFile = Voca.getVoiceFileOnLocal(context, playerFileModel, Voca.getVoiceFolderOnLocal(context), d1.getVIPath(), d1.getVocaId());
        if (recordedFile.exists()) {
            d1.setRecordedPath(recordedFile.getPath());
        }
    }

    public List<DicModel> getSubtitleListTotal() {
        return subtitleListTotal;
    }

    public void setSubtitleListTotal(List<DicModel> subtitleListTotal) {
        this.subtitleListTotal = subtitleListTotal;
    }

    public List<DicModel> getSubtitleList() {
        return subtitleList;
    }

    public void setSubtitleList(List<DicModel> subtitleList) {
        this.subtitleList = subtitleList;
        checkAllItemsInSubtitleList();
    }
    private void checkAllItemsInSubtitleList() {
        subtitleListTotal.stream().forEach(e -> e.setVIChecked(false));
        subtitleList.stream().forEach(e -> e.setVIChecked(true));
    }

    public void resetSubtitleList() {
        subtitleList = subtitleListTotal;
    }
    public void filterSubtitleList(String keyword) {
        if (Utils.isEmpty(keyword))
            return;

        subtitleList = subtitleList.stream()
                .peek(dicModel -> dicModel.setVIChecked(false))
                .filter(dicModel -> dicModel.getVocaDisplay().toLowerCase().contains(keyword.toLowerCase()))
                .collect(Collectors.toList());
    }

    public void sublistFromSubtitleList(int from, int to) {

        if (Utils.isIndexInsideRange(subtitleList, from)
            && Utils.isIndexInsideRange(subtitleList, to - 1)) {
            subtitleList = subtitleList.subList(from, to);
        }
    }

    public DicModel getDicModelFromTotal(int index) {
        if (index < 0)
            return null;

        if (Utils.isIndexInsideRange(subtitleListTotal, index)) {
            return subtitleListTotal.get(index);
        }
        return null;
    }

    public void filterRecorded() {
        subtitleList = subtitleList.stream()
                .filter(e -> e.hasVIVoiceFile())
                .collect(Collectors.toList());
    }

    public void setSelectOrUnselectAllSubtitles(boolean select) {
        subtitleList.forEach(subtitle -> subtitle.setVIChecked(select));
    }

    public long getCountOfItemChecked() {
        return subtitleList.stream().filter(e -> e.isVIChecked()).count();
    }
}
