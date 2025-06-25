package com.dalread.activity;

import android.content.DialogInterface;
import android.view.View;
import android.widget.CompoundButton;

import androidx.recyclerview.widget.RecyclerView;

import com.dalread.R;
import com.dalread.adapter.CopySubtitlePlayerAdapter;
import com.dalread.asyntask.OnAsyncTaskListener;
import com.dalread.base.BasePlayerActivity;
import com.dalread.base.EnumLanguage;
import com.dalread.component.CenterLayoutManager;
import com.dalread.component.SeparatorDecoration;
import com.dalread.component.Toolbar;
import com.dalread.database.SharedPreferencesDB;
import com.dalread.database.sqlite.model.SubtitleModel;
import com.dalread.databinding.ActivityPlayerCopySubtitleBinding;
import com.dalread.dialog.AlertDialog;
import com.dalread.dialog.PlayerCopySubtitleDialog;
import com.dalread.dialog.PlayerExportFileDialog;
import com.dalread.dialog.YesNoDialog;
import com.dalread.listener.OnClickListener;
import com.dalread.listener.OnYesNoClickListener;
import com.dalread.model.SubtitleLanguageModel;
import com.dalread.network.events.BaseEvent;
import com.dalread.network.events.SuccessEvent;
import com.dalread.util.BaseBindUtils;
import com.dalread.util.Constant;
import com.dalread.util.DLog;
import com.dalread.util.FileUtil;
import com.dalread.util.Loading;
import com.dalread.util.LyricUtils;
import com.dalread.util.StorageUtil;
import com.dalread.util.StringUtils;
import com.dalread.util.UserUtil;
import com.dalread.util.Utils;
import com.yanzhenjie.recyclerview.touch.OnItemMoveListener;

import org.apache.commons.io.FilenameUtils;
import org.greenrobot.eventbus.Subscribe;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Collectors;

import butterknife.OnCheckedChanged;
import me.zhanghai.android.fastscroll.FastScrollerBuilder;

public class CopySubtitlePlayerActivity extends BasePlayerActivity implements OnAsyncTaskListener {
    private boolean isIncludeIndex = true;
    private boolean isIncludeTime = true;
    private boolean isIncludeHidedSubtitles = false;
    private boolean isSeparateByTab = false;

    private final int TYPE_INIT_DATA = 0;
    private final int TYPE_DISPLAY_PREVIEW = TYPE_INIT_DATA + 1;
    private final int TYPE_EXPORT_FILE = TYPE_DISPLAY_PREVIEW + 1;

    private enum SubtitleLanguageType {NONE, BOTH, STUDY_LANG, MOTHEHR_TONGUE}
    private enum ExportFileFormatType {NONE, SRT, LRC}

    private ExportFileFormatType currentExportFileFormatType;
    private List<SubtitleModel> subtitleModels;
    private String content;
    private List<SubtitleLanguageModel> subtitleLanguageModelList;
    private CopySubtitlePlayerAdapter adapter;

    private ActivityPlayerCopySubtitleBinding binding;
    private String exportFilenameWithPath;
    private boolean isHasBothSubtitleLanguages = false;
    @Override
    protected View getContentView() {
        binding = ActivityPlayerCopySubtitleBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        return view;
    }

    @Override
    protected Toolbar getToolbar() {
        return binding.header;
    }

    @Override
    protected void setFullscreen() {

    }

    @Override
    public void initView() {
        initEventBus();
        new FastScrollerBuilder(binding.fssvPreview).build();
        adapter = new CopySubtitlePlayerAdapter(this, binding.rvListDisplayOrder, onItemClickListener);
        binding.rvListDisplayOrder.setOnItemMoveListener(onItemMoveListener);
        binding.rvListDisplayOrder.setAdapter(adapter);
        binding.rvListDisplayOrder.setLayoutManager(new CenterLayoutManager(this));
        binding.rvListDisplayOrder.addItemDecoration(new SeparatorDecoration(this, BaseBindUtils.getDividerColor(), BaseBindUtils.getDividerHeight(this)));
        binding.rvListDisplayOrder.setItemViewSwipeEnabled(false);
        playerFileModel = getIntent().getParcelableExtra(Constant.PLAYER.INTENT.KEY_VIDEO_FILE);
        createSubDatabase(playerFileModel);
        initData();
    }

    @Override
    public void initData() {
        DLog.d(getLogTag(), "callInitData");
        currentExportFileFormatType = ExportFileFormatType.NONE;
        callAsyncTask(this, TYPE_INIT_DATA);
    }

    @Override
    public void onHeaderLeftClick() {
        finish();
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
        openMenu();
    }

    @OnCheckedChanged({R.id.sc_include_index, R.id.sc_include_time, R.id.sc_include_hided_subtitles, R.id.sc_separate_by_tab})
    void OnCheckedChanged(CompoundButton button, boolean checked) {
        switch (button.getId()) {
            case R.id.sc_include_index:
                isIncludeIndex = checked;
                callUpdatePreview();
                break;
            case R.id.sc_include_time:
                isIncludeTime = checked;
                callUpdatePreview();
                break;
            case R.id.sc_separate_by_tab:
                isSeparateByTab = checked;
                callUpdatePreview();
                break;
            case R.id.sc_include_hided_subtitles:
                isIncludeHidedSubtitles = checked;
                callInitData();
                break;
        }
    }

    private OnClickListener onItemClickListener = (view, object) -> {
        int position = (int) object;
        if (!checkLanguageSelect()) {
            subtitleLanguageModelList.get(position).setSelected(true);
            adapter.notifyItemChanged(position);
            return;
        }
        callUpdatePreview();
    };

    private OnItemMoveListener onItemMoveListener = new OnItemMoveListener() {
        @Override
        public boolean onItemMove(RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder viewHolder1) {
            if (viewHolder.getItemViewType() != viewHolder1.getItemViewType()) return false;

            // 真实的Position：通过ViewHolder拿到的position都需要减掉HeadView的数量。
            int fromPosition = viewHolder.getAdapterPosition() - binding.rvListDisplayOrder.getHeaderCount();
            int toPosition = viewHolder1.getAdapterPosition() - binding.rvListDisplayOrder.getHeaderCount();

            swapPositionData(fromPosition, toPosition);
            return true;
        }

        @Override
        public void onItemDismiss(RecyclerView.ViewHolder viewHolder) {

        }
    };

    private void swapPositionData(int fromPosition, int toPosition) {
        Collections.swap(subtitleLanguageModelList, fromPosition, toPosition);
        runOnUiThread(() -> {
            Loading.show(this);
            for (int i = 0; i < subtitleLanguageModelList.size(); i++) {
                subtitleLanguageModelList.get(i).setIndex(i);
            }
            adapter.notifyItemMoved(fromPosition, toPosition);
            Loading.hide();
        });
        callAsyncTask(this, TYPE_DISPLAY_PREVIEW);
        //Son's code. (Didn't refresh the swapped subtitle)
//        runOnUiThread(() -> {
//            Loading.show(this);
//            Collections.swap(listLanguage, fromPosition, toPosition);
//            for (int i = 0; i < listLanguage.size(); i++) {
//                listLanguage.get(i).setIndex(i);
//                CopySubtitleLanguageModelQuery.update(Voca.getRealm(), listLanguage.get(i));
//            }
//            adapter.notifyItemMoved(fromPosition, toPosition);
//            Loading.hide();
//        });
    }

    @Subscribe
    public void onEvent(SuccessEvent event) {
        if (event.getScreen() == BaseEvent.Screen.MAIN) {
            if (event.getEventType() == BaseEvent.EventType.PURCHASED_EXPORT_SUBTITLE) {
                runOnUiThread(this::showCopySubtitleDialog);
            }
        }
    }

    private void openMenu() {
        if (UserUtil.isLoggedIn(this, true)) {
            showCopySubtitleDialog();
        }
//        if (UserUtil.isLoggedIn(this, true)) {
//            if (UserUtil.isDebugOrAdminUser(this)) {
//                if (sharedPreferences.isPurchasedExportSubtitle()) {
//
//                } else {
//                    SkuDetails skuDetails = application.billingClientHelper.getSkuDetailsFromSku(BuildConfig.BUY_EXPORT_SUBTITLE_ID);
//                    if (skuDetails != null) {
//                        BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
//                                .setSkuDetails(skuDetails)
//                                .build();
//                        application.billingClientHelper.launchBillingFlow(this, billingFlowParams);
//                    } else {
//                        ToastUtil.getInstance(this).show(R.string.not_available);
//                    }
//                }
//            } else {
//                showCopySubtitleDialog();
//
//            }
//        }
    }

    private void showCopySubtitleDialog() {
        final PlayerCopySubtitleDialog dialog = new PlayerCopySubtitleDialog(this, playerFileModel, onPlayerCopySubtitleListener);
        dialog.show();
    }

    private DialogInterface.OnClickListener onPlayerCopySubtitleListener = (dialogInterface, i) -> {
        switch (i) {
            case R.id.tv_copy:
                copySubtitle();
                break;
            case R.id.tv_export_srt_file:
                saveSubtitle();
                break;
        }
    };

    private void saveSubtitle() {
        showExportFileDialog();
//        if (UserUtil.isDebugOrAdminUser(this)) {
//            if (sharedPreferences.isPurchasedExportSubtitle()) {
//                showExportFileDialog();
//            } else {
//                SkuDetails skuDetails = application.billingClientHelper.getSkuDetailsFromSku(BuildConfig.BUY_EXPORT_SUBTITLE_ID);
//                if (skuDetails != null) {
//                    BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
//                            .setSkuDetails(skuDetails)
//                            .build();
//                    application.billingClientHelper.launchBillingFlow(this, billingFlowParams);
//                } else {
//                    ToastUtil.getInstance(this).show(R.string.not_available);
//                }
//            }
//        } else {
//            showExportFileDialog();
//        }

    }

    private void copySubtitle() {
        Utils.copyToClipboard(this, content, R.string.copied);
        Loading.hide();
    }

    private void showExportFileDialog() {
        if (FileUtil.isAudioFormat(playerFileModel.getVideoModel().getName()))
            currentExportFileFormatType = ExportFileFormatType.LRC;
        else
            currentExportFileFormatType = ExportFileFormatType.SRT;

        final String fileName = FilenameUtils.getBaseName(playerFileModel.getName());
        isHasBothSubtitleLanguages = getSubDatabase().isHasBothSubtitleLanguages();
        final PlayerExportFileDialog dialog = new PlayerExportFileDialog(this,
                getDialogTitle(),
                getDialogDesc(),
                fileName,
                (view, object) -> {
                    String fileNameWithoutExt = (String) object;
                    final String path = StorageUtil.getCurrentPath(playerFileModel) + File.separator + getGenerateExportFileName(fileNameWithoutExt);
                    if (StorageUtil.isFileExist(path)) {
                        showWarningSameName(fileNameWithoutExt);
                    } else {
                        callExportFile((String) object);
                    }
                });
        dialog.show();
        Utils.showSoftKeyboard(this);
    }

    private String getGenerateExportFileName(String fileName) {
        if (currentExportFileFormatType == ExportFileFormatType.LRC)
            return StorageUtil.generateLRCFileName(fileName);

        return StorageUtil.generateSRTFileName(fileName);
    }
    private int getDialogTitle() {
        if (currentExportFileFormatType == ExportFileFormatType.LRC)
            return R.string.export_as_a_lrc_file;

        return R.string.export_as_a_srt_file;
    }

    private int getDialogDesc() {
        if (currentExportFileFormatType == ExportFileFormatType.LRC)
            return isHasBothSubtitleLanguages ? R.string.export_as_a_lrc_file_description_each_language :  R.string.export_as_a_lrc_file_description;

        return isHasBothSubtitleLanguages ? R.string.export_as_a_srt_file_description_each_language :  R.string.export_as_a_srt_file_description;
    }


    private void showWarningSameName(String fileName) {
        final YesNoDialog dialog = new YesNoDialog(this, R.string.warning, R.string.msg_export_file_warning, fileName, new OnYesNoClickListener() {
            @Override
            public void onYesClick(View view, Object object) {
                String fileNameWithoutExt = (String) object;
                callExportFile(fileNameWithoutExt);
            }

            @Override
            public void onNoClick(View view, Object object) {
                showExportFileDialog();
            }
        });
        dialog.show();
        Utils.hideSoftKeyboard(this);
    }

    private void callInitData() {

    }

    private void callExportFile(String fileName) {
        callAsyncTask(this, fileName, TYPE_EXPORT_FILE);
    }

    private void callUpdatePreview() {
        displayLanguageListInSubtitle();
        callAsyncTask(this, TYPE_DISPLAY_PREVIEW);
    }

    private void displayLanguageListInSubtitle() {

        if ((subtitleLanguageModelList == null) || (subtitleLanguageModelList.size() != 2)) {
            binding.llDisplayOrder.setVisibility(View.GONE);
            return;
        }

        SubtitleLanguageModel lang1 = subtitleLanguageModelList.get(0);
        SubtitleLanguageModel lang2 = subtitleLanguageModelList.size() == 2 ? subtitleLanguageModelList.get(1) : null;
        boolean hasSubtitle = false;
        boolean hasMeaning = false;
        for(SubtitleModel subtitleModel : subtitleModels) {
            if (lang1.getLanguage() == sharedPreferences.getLangStudyCode()) {
                if (!Utils.isEmpty(subtitleModel.getSubtitle())) {
                    hasSubtitle = true;
//                    lang1.setHasData(true);
                }
            } else {
                if (!Utils.isEmpty(subtitleModel.getMeaning())) {
                    hasMeaning = true;
//                    lang1.setHasData(true);
                }
            }
            if (lang2 != null &&
                    (lang2.getLanguage() == sharedPreferences.getLangStudyCode())) {
                if (!Utils.isEmpty(subtitleModel.getSubtitle())) {
                    hasSubtitle = true;
//                    lang2.setHasData(true);
                }
            } else {
                if (!Utils.isEmpty(subtitleModel.getMeaning())) {
                    hasMeaning = true;
//                    lang2.setHasData(true);
                }
            }

            if (hasSubtitle && hasMeaning)
                break;
        }

        if (subtitleLanguageModelList == null || subtitleLanguageModelList.size() == 0) {
            binding.llDisplayOrder.setVisibility(View.GONE);
            return;
        } else if (lang1.isHasData() && lang2.isHasData()) {
            binding.llDisplayOrder.setVisibility(View.VISIBLE);
        } else {
            binding.llDisplayOrder.setVisibility(View.GONE);
        }

        adapter.notifyDataSetChanged(subtitleLanguageModelList);
    }

    @Override
    public void onInitAsyncTask() {
        Loading.show(this);
    }

    @Override
    public void onPrevExecuteAsyncTask() {

    }

    @Override
    public Object onPostExecuteAsyncTask(int searchType, Object data) {
        try {
            switch (searchType) {
                case TYPE_INIT_DATA:
                    List<SubtitleModel> subtitleModelList =  getSubDatabase().getSubtitleModelByUsed(isIncludeHidedSubtitles, playerFileModel.getVideoModel().getDelaySubtitles());
                    initSubtitleLanguages(subtitleModelList);
                    return subtitleModelList;
                case TYPE_DISPLAY_PREVIEW:
                    return parserPreview();
                case TYPE_EXPORT_FILE:
                    String fileNameWithoutExt = (String) data;
//                    exportFilenameWithPath = StorageUtil.getCurrentPath(playerFileModel) + File.separator + StorageUtil.generateSRTFileName(fileNameWithoutExt);
                    exportFilenameWithPath = getExportFilenameWithPath(fileNameWithoutExt); //FilenameUtils.getPath(playerFileModel.getPath()) + StorageUtil.generateSRTFileName(fileNameWithoutExt);
                    boolean isExportFile = true;
                    isExportFile = isExportFile && StorageUtil.writeToFile(exportFilenameWithPath, parserSubtitleToExport(SubtitleLanguageType.BOTH));
                    if (isHasBothSubtitleLanguages) {
                        String exportStudyLangFilenameWithPath = getExportFilenameWithPath(fileNameWithoutExt + "_" + EnumLanguage.getNameByFormatOs(SharedPreferencesDB.getInstance(this).getStudyLanguage())); //StorageUtil.getCurrentPath(playerFileModel) + File.separator + StorageUtil.generateSRTFileName(fileNameWithoutExt + "_" + EnumLanguage.getNameByFormatOs(SharedPreferencesDB.getInstance(this).getStudyLanguage()));;
                        String exportMotherTongueFilenameWithPath = getExportFilenameWithPath(fileNameWithoutExt + "_" + EnumLanguage.getNameByFormatOs(SharedPreferencesDB.getInstance(this).getMotherTongueLanguage())); //StorageUtil.getCurrentPath(playerFileModel) + File.separator + StorageUtil.generateSRTFileName(fileNameWithoutExt + "_" + EnumLanguage.getNameByFormatOs(SharedPreferencesDB.getInstance(this).getDisplayLanguage()));;
                        isExportFile = isExportFile && StorageUtil.writeToFile(exportStudyLangFilenameWithPath, parserSubtitleToExport(SubtitleLanguageType.STUDY_LANG));
                        isExportFile = isExportFile && StorageUtil.writeToFile(exportMotherTongueFilenameWithPath, parserSubtitleToExport(SubtitleLanguageType.MOTHEHR_TONGUE));
                    }

                    return isExportFile;
            }
            return null;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    private String getExportFilenameWithPath(String fileName) {
        return FilenameUtils.getPath(playerFileModel.getPath()) + getGenerateExportFileName(fileName);
    }

    private void initSubtitleLanguages(List<SubtitleModel> subtitleModelList) {
        subtitleLanguageModelList = new ArrayList<>();
        boolean hasSubtitle = false;
        boolean hasMeaning = false;
        for (SubtitleModel subtitleModel : subtitleModelList) {
            if (!Utils.isEmpty(subtitleModel.getSubtitle())) {
                hasSubtitle = true;
            }
            if (!Utils.isEmpty(subtitleModel.getMeaning())) {
                hasMeaning = true;
            }
            if (hasSubtitle && hasMeaning) {
                break;
            }
        }
        int index = 0;
        SubtitleLanguageModel subtitleLanguage1 = new SubtitleLanguageModel(index, EnumLanguage.findByFormatApi(sharedPreferences.getStudyLanguage()).getIdApi(), true, hasSubtitle);
        if (hasSubtitle) {
            subtitleLanguageModelList.add(subtitleLanguage1);
            index++;
        }

        SubtitleLanguageModel subtitleLanguage2 = new SubtitleLanguageModel(index, EnumLanguage.findByFormatApi(sharedPreferences.getMotherTongueLanguage()).getIdApi(), true, hasMeaning);
        if (hasMeaning) {
            subtitleLanguageModelList.add(subtitleLanguage2);
        }
    }

    @Override
    public void onFinishAsyncTask(int searchType, Object resultData, Object data) {
        switch (searchType) {
            case TYPE_INIT_DATA:
                subtitleModels = (List<SubtitleModel>) resultData;
                callUpdatePreview();
                break;
            case TYPE_DISPLAY_PREVIEW:
                this.content = (String) resultData;
                updateSubtitleEncoding();
                break;
            case TYPE_EXPORT_FILE:
                finishExportFile((Boolean) resultData);
                break;
        }
    }

    private String parserPreview() {
        String content = Constant.BASE_BLANK;
        if (subtitleModels != null && !subtitleModels.isEmpty()) {
            currentExportFileFormatType = ExportFileFormatType.NONE;
            content = parserSubtitle(SubtitleLanguageType.NONE);
        }
        return content;
    }

    private void updateSubtitleEncoding() {
        runOnUiThread(() -> {
            if (Utils.isEmpty(content)) {
                content = Constant.BASE_BLANK;
            }
            binding.tvPreview.setText(content);
            Loading.hideDelay();
        });
    }

    private void finishExportFile(boolean isSuccess) {
        String message = getString(R.string.msg_export_file_fail_rename_and_retry);
        if (isSuccess) {
            message = getString(R.string.msg_export_file_success, exportFilenameWithPath);
        }
        AlertDialog alertDialog = new AlertDialog(this);
        alertDialog.show(message, null, null);

        Loading.hide();
    }

    private String parserSubtitleToExport(SubtitleLanguageType subtitleLanguageType) {
        return parserSubtitle(subtitleLanguageType);
    }

    private boolean checkLanguageSelect() {
        boolean isChange = false;
        for (SubtitleLanguageModel lang : subtitleLanguageModelList) {
            if (lang.isSelected()) {
                isChange = true;
            }
        }
        return isChange;
    }

    private String parserSubtitle(SubtitleLanguageType subtitleLanguageType) {
        StringJoiner sjContent = new StringJoiner("\n\n");
        if (currentExportFileFormatType == ExportFileFormatType.LRC)
            sjContent = new StringJoiner("\n");

        Integer index = 1;

        String strDelimiter = chooseLineDelimiter();
        List<String> listOneLine = new ArrayList<>();
        SubtitleLanguageModel lang1 = subtitleLanguageModelList.get(0);
        SubtitleLanguageModel lang2 = subtitleLanguageModelList.size() == 2 ? subtitleLanguageModelList.get(1) : null;
        boolean isHasSubtitle = false;
        for (SubtitleModel item : subtitleModels) {
            if (Utils.isEmpty(item.getSubtitle()) && (Utils.isEmpty(item.getMeaning())))
                continue;

            listOneLine.clear();
            if (currentExportFileFormatType == ExportFileFormatType.SRT || isIncludeIndex) {
                if (currentExportFileFormatType != ExportFileFormatType.LRC)
                    listOneLine.add(index.toString());
            }
            if (currentExportFileFormatType == ExportFileFormatType.SRT || isIncludeTime) {
                if (currentExportFileFormatType != ExportFileFormatType.LRC)
                    listOneLine.add(item.getDisplayFormatTimes());
            }

//            isHasSubtitle = false;
            if (currentExportFileFormatType == ExportFileFormatType.NONE) {
                isHasSubtitle = isHasSubtitle(listOneLine, lang1, lang2, item);
            } else {
                isHasSubtitle = isHasSubtitleForSRTFormat(listOneLine, lang1, lang2, item, subtitleLanguageType);
            }

            if (isHasSubtitle == false)
                continue;

            String strListOneLine = listOneLine.stream().collect(Collectors.joining(strDelimiter));
            if (currentExportFileFormatType == ExportFileFormatType.LRC) {
                strListOneLine = strListOneLine.replace(System.getProperty("line.separator"), " ");
                sjContent.add(item.getDisplayFormatTimesForStartTimeLyric() + strListOneLine + "\n"+item.getDisplayFormatTimesForEndTimeLyric());
            } else {
                sjContent.add(strListOneLine);
            }
            index++;
        }
        return getExtraInfoForLrc() + sjContent.toString();
    }

    private String getExtraInfoForLrc() {
        String extraInfoForLrc = "";
        if (currentExportFileFormatType == ExportFileFormatType.LRC) {
            StringJoiner sjExtraInfoForLrc = new StringJoiner("\n");
            if (!Utils.isEmpty(playerFileModel.getVideoModel().getDisplayTitle())) {
                sjExtraInfoForLrc.add(LyricUtils.makeLrcHeaderByTag(Constant.LYRIC.ID_TAG_TITLE, playerFileModel.getVideoModel().getDisplayTitle()));
            }
            if (!Utils.isEmpty(playerFileModel.getVideoModel().getPureTtsTitle())) {
                sjExtraInfoForLrc.add(LyricUtils.makeLrcHeaderByTag(Constant.LYRIC.ID_TAG_TITLE_TTS, playerFileModel.getVideoModel().getTitleTts()));
            }
            if (!Utils.isEmpty(playerFileModel.getVideoModel().getPureArtist())) {
                sjExtraInfoForLrc.add(LyricUtils.makeLrcHeaderByTag(Constant.LYRIC.ID_TAG_ARTIST, playerFileModel.getVideoModel().getArtist()));
            }
            if (!Utils.isEmpty(playerFileModel.getVideoModel().getPureTtsArtist())) {
                sjExtraInfoForLrc.add(LyricUtils.makeLrcHeaderByTag(Constant.LYRIC.ID_TAG_ARTIST_TTS, playerFileModel.getVideoModel().getArtistTts()));
            }
            if (!Utils.isEmpty(playerFileModel.getVideoModel().getAlbum())) {
                sjExtraInfoForLrc.add(LyricUtils.makeLrcHeaderByTag(Constant.LYRIC.ID_TAG_ALBUM, playerFileModel.getVideoModel().getAlbum()));
            }
            if (playerFileModel.getVideoModel().getDuration() > 0) {
                sjExtraInfoForLrc.add(LyricUtils.makeLrcHeaderByTag(Constant.LYRIC.ID_TAG_LENGTH, String.valueOf(playerFileModel.getVideoModel().getDuration())));
            }
            extraInfoForLrc = sjExtraInfoForLrc.length() == 0 ? sjExtraInfoForLrc.toString() : sjExtraInfoForLrc.toString() + "\n";
        }
        return StringUtils.covertStringToNFC(extraInfoForLrc);
    }

    private boolean isHasSubtitleForSRTFormat(List<String> listOneLine, SubtitleLanguageModel lang1, SubtitleLanguageModel lang2, SubtitleModel item, SubtitleLanguageType subtitleLanguageType) {
        boolean isHasSubtitle = false;
        if (lang1 != null && lang1.getLanguage() == sharedPreferences.getLangStudyCode()) {
            if (subtitleLanguageType != SubtitleLanguageType.MOTHEHR_TONGUE) {
                if (!Utils.isEmpty(item.getSubtitle())) {
                    isHasSubtitle = true;
                    listOneLine.add(item.getSubtitle());
                }
            }
        } else {
            if (subtitleLanguageType != SubtitleLanguageType.STUDY_LANG) {
                if (!Utils.isEmpty(item.getMeaning())) {
                    isHasSubtitle = true;
                    listOneLine.add(item.getMeaning());
                }
            }
        }


        if (lang2 != null && lang2.getLanguage() == sharedPreferences.getLangStudyCode()) {
            if (subtitleLanguageType != SubtitleLanguageType.MOTHEHR_TONGUE) {
                if (!Utils.isEmpty(item.getSubtitle())) {
                    isHasSubtitle = true;
                    listOneLine.add(item.getSubtitle());
                }
            }
        } else {
            if (subtitleLanguageType != SubtitleLanguageType.STUDY_LANG) {
                if (!Utils.isEmpty(item.getMeaning())) {
                    isHasSubtitle = true;
                    listOneLine.add(item.getMeaning());
                }
            }
        }

        return isHasSubtitle;
    }

    private boolean isHasSubtitle(List<String> listOneLine, SubtitleLanguageModel lang1, SubtitleLanguageModel lang2, SubtitleModel item) {
        boolean isHasSubtitle = false;
        if (lang1 != null && lang1.isSelected()) {
            if (lang1.getLanguage() == sharedPreferences.getLangStudyCode()) {
                if (!Utils.isEmpty(item.getSubtitle())) {
                    isHasSubtitle = true;
                    listOneLine.add(item.getSubtitle());
                }
            } else {
                if (!Utils.isEmpty(item.getMeaning())) {
                    isHasSubtitle = true;
                    listOneLine.add(item.getMeaning());
                }
            }
        }

        if (lang2 != null && lang2.isSelected()) {
            if (lang2.getLanguage() == sharedPreferences.getLangStudyCode()) {
                if (!Utils.isEmpty(item.getSubtitle())) {
                    isHasSubtitle = true;
                    listOneLine.add(item.getSubtitle());
                }
            } else {
                if (!Utils.isEmpty(item.getMeaning())) {
                    isHasSubtitle = true;
                    listOneLine.add(item.getMeaning());
                }
            }
        }
        return isHasSubtitle;
    }

    @NotNull
    private String chooseLineDelimiter() {
        if (currentExportFileFormatType == ExportFileFormatType.SRT)
            return "\n";
        if (isSeparateByTab)
            return "\t";
        return "\n";
    }

//    private class SubtitleLanguage {
//        private int index;
//        private int language;
//        private boolean selected;
//        private boolean hasData;
//
//        public SubtitleLanguage(int index, int language, boolean selected, boolean hasData) {
//            this.index = index;
//            this.language = language;
//            this.selected = selected;
//            this.hasData = hasData;
//        }
//
//        public int getIndex() {
//            return index;
//        }
//
//        public void setIndex(int index) {
//            this.index = index;
//        }
//
//        public int getLanguage() {
//            return language;
//        }
//
//        public void setLanguage(int language) {
//            this.language = language;
//        }
//
//        public boolean isSelected() {
//            return selected;
//        }
//
//        public void setSelected(boolean selected) {
//            this.selected = selected;
//        }
//
//        public boolean isHasData() {
//            return hasData;
//        }
//
//        public void setHasData(boolean hasData) {
//            this.hasData = hasData;
//        }
//    }
}
