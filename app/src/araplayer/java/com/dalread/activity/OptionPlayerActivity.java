package com.dalread.activity;

import android.app.Activity;
import android.content.Intent;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.recyclerview.widget.RecyclerView;

import com.dalread.R;
import com.dalread.adapter.OptionPlayerAdapter;
import com.dalread.asyntask.OnAsyncTaskListener;
import com.dalread.base.BaseDialog;
import com.dalread.base.BaseDialogListener;
import com.dalread.base.BasePlayerActivity;
import com.dalread.base.EnumType;
import com.dalread.component.CenterLayoutManager;
import com.dalread.component.SeparatorDecoration;
import com.dalread.component.Toolbar;
import com.dalread.database.VideoModelQuery;
import com.dalread.database.sqlite.model.DicModel;
import com.dalread.databinding.ActivityOptionPlayerBinding;
import com.dalread.dialog.PlayerShowOptionMenuDialog;
import com.dalread.dialog.PlayerVideoFileNameDialog;
import com.dalread.dialog.SingleChoiceDialog;
import com.dalread.dialog.TypeInputDialog;
import com.dalread.dialog.YesNoDialog;
import com.dalread.helper.SubtitleFileChooserHelper;
import com.dalread.listener.OnYesNoClickListener;
import com.dalread.model.PlayerFileModel;
import com.dalread.model.SubtitleLanguageModel;
import com.dalread.model.VideoModel;
import com.dalread.util.BaseBindUtils;
import com.dalread.util.BaseStorageUtil;
import com.dalread.util.Constant;
import com.dalread.util.CopyTextUtil;
import com.dalread.util.CustomTranslate;
import com.dalread.util.DLog;
import com.dalread.util.FileUtil;
import com.dalread.util.Loading;
import com.dalread.util.StorageUtil;
import com.dalread.util.SubtitleUtil;
import com.dalread.util.SupportSubtitleFormat;
import com.dalread.util.TextViewUtil;
import com.dalread.util.ToastUtil;
import com.dalread.util.Utils;
import com.dalread.util.Voca;
import com.yanzhenjie.recyclerview.touch.OnItemMoveListener;

import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.OnClick;

public class OptionPlayerActivity extends BasePlayerActivity implements OnAsyncTaskListener, SubtitleFileChooserHelper.Listener {
    private OptionPlayerAdapter adapter;
    private List<SubtitleLanguageModel> subtitleLanguageModels;

//    private final int TYPE_COPY_ONLY_TEXT = 0;
    private final int TYPE_COPY_ADD_CONTENT = 0;
    private final int TYPE_TRANSLATE_SUBTITLE = TYPE_COPY_ADD_CONTENT + 1;
    private final int TYPE_INIT_DATA = TYPE_TRANSLATE_SUBTITLE + 1;

    private SingleChoiceDialog singleChoiceDialog;
    private SubtitleFileChooserHelper subtitleConnectHelper;
    private CustomTranslate customTranslate;
    private ArrayList<PlayerFileModel> embedSubtitleFiles;
    private boolean isCheckedScUseSubtitle2;
    private int subPathIndex = Constant.PLAYER.INTENT.SUBPATH_INDEX_1;
    private boolean isFirstOpenActivity = true;

    private ActivityOptionPlayerBinding binding;
    private ActivityResultLauncher<IntentSenderRequest> deleteSameFileNameIntentSenderLauncher;
    private ActivityResultLauncher<IntentSenderRequest> renameFileIntentSenderLauncher;
    private String renamedFileNewPath = null;
    private File tempFile = null;

    @Override
    protected View getContentView() {
        binding = ActivityOptionPlayerBinding.inflate(getLayoutInflater());
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
    protected void onResume() {
        super.onResume();
        runOnUiThread(this::updateSubtitleEncoding);
    }

    @Override
    public void initView() {
        isFirstOpenActivity = true;
        adapter = new OptionPlayerAdapter(this, binding.rvList);
        binding.rvList.setOnItemMoveListener(onItemMoveListener);
        binding.rvList.setAdapter(adapter);
        binding.rvList.setLayoutManager(new CenterLayoutManager(this));
        binding.rvList.addItemDecoration(new SeparatorDecoration(this, BaseBindUtils.getDividerColor(), BaseBindUtils.getDividerHeight(this)));
        binding.rvList.setItemViewSwipeEnabled(false);

        playerFileModel = getIntent().getParcelableExtra(Constant.PLAYER.INTENT.KEY_VIDEO_FILE);
        singleChoiceDialog = new SingleChoiceDialog(this);
        subtitleConnectHelper = new SubtitleFileChooserHelper(this, SubtitleFileChooserHelper.VIEW_TYPE.OPTION_VIEW, this);
        initData();

        deleteSameFileNameIntentSenderLauncher = registerForActivityResult(new ActivityResultContracts.StartIntentSenderForResult(), result -> {
            if (result.getResultCode() == Activity.RESULT_OK) {
                File oldFile = new File(playerFileModel.getPath());
                File newFile = new File(renamedFileNewPath);
                renameVideoFile(oldFile, newFile);
            }
        });

        renameFileIntentSenderLauncher = registerForActivityResult(new ActivityResultContracts.StartIntentSenderForResult(), result -> {
            if (result.getResultCode() == Activity.RESULT_OK) {
                File oldFile = new File(playerFileModel.getPath());
                if (tempFile != null) {
                    onVideoRename(oldFile, tempFile);
                    showToastResultRenameSucceed();
                }
            } else {
                if (tempFile != null && tempFile.exists()) {
                    tempFile.delete();
                }
                //Don't need to show fail message when a user refuse delete a file message.
//                showToastResultRenameFailed();
            }
        });
    }

    @Override
    public void initData() {
        isCheckedScUseSubtitle2 = playerFileModel.getVideoModel().isUserSecondSubtitle();
        createSubDatabase(playerFileModel);
        callAsyncTask(this, TYPE_INIT_DATA);
    }

    @Override
    public void onBackPressed() {
        onFinishActivity();
    }

    private void onFinishActivity() {
        Intent intent = getIntent();
        intent.putExtra(Constant.PLAYER.INTENT.KEY_VIDEO_FILE, playerFileModel);
        setResult(RESULT_OK, intent);
        finish();
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
        openOptionMenuDialog();
    }

    @OnClick({
            R.id.ll_video_file_name, R.id.ll_subtitle_file_name,
            R.id.llSubTitleEncoding, R.id.llSaveSubtitle, R.id.llCopySubTitle1, R.id.llCopySubTitle2,
//            R.id.sc_use_subtitle2,
            R.id.ll_subtitle_file_name2, R.id.llSubTitleEncoding2})
    void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_video_file_name:
                openVideoFileNameDialog();
                break;
            case R.id.ll_subtitle_file_name:
                subPathIndex = Constant.PLAYER.INTENT.SUBPATH_INDEX_1;
                openPlayerSubtitleOptionsHelper();
                break;
            case R.id.llSubTitleEncoding:
                subPathIndex = Constant.PLAYER.INTENT.SUBPATH_INDEX_1;
                openSubtitleEncoding(Constant.PLAYER.INTENT.SUBPATH_INDEX_1);
                break;
            case R.id.ll_subtitle_file_name2:
                if (playerFileModel.getVideoModel().hasSubPath1()) {
                    subPathIndex = Constant.PLAYER.INTENT.SUBPATH_INDEX_2;
                    openPlayerSubtitleOptionsHelper();
                } else {
                    ToastUtil.getInstance(this).show(R.string.choose_subtitle1_first);
                }
                break;
            case R.id.llSubTitleEncoding2:
                openSubtitleEncoding(Constant.PLAYER.INTENT.SUBPATH_INDEX_2);
                break;
            case R.id.llSaveSubtitle:
                openSaveSubtitle();
                break;
            case R.id.llCopySubTitle1:
                callParserSubtitle(TYPE_COPY_ADD_CONTENT);
                break;
//            case R.id.llCopySubTitle2:
//                callParserSubtitle(TYPE_COPY_ADD_CONTENT);
//                break;

//            case R.id.sc_use_subtitle2:
//                isCheckedScUseSubtitle2 = !isCheckedScUseSubtitle2;
//                switchVisibilitySubtitle2Menu(isCheckedScUseSubtitle2);
//                if (isCheckedScUseSubtitle2 == false) {
//                    detachSubtitleFile(Constant.PLAYER.INTENT.SUBPATH_INDEX_2);
//                }
//                break;
        }
    }

    private void openPlayerSubtitleOptionsHelper() {
        subtitleConnectHelper.setSubPathIndex(subPathIndex);
        subtitleConnectHelper.setPlayerFileModel(playerFileModel);
        subtitleConnectHelper.showSubtitleOptionsDialog();
    }
    private OnItemMoveListener onItemMoveListener = new OnItemMoveListener() {
        @Override
        public boolean onItemMove(RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder viewHolder1) {
            if (viewHolder.getItemViewType() != viewHolder1.getItemViewType()) return false;

            // 真实的Position：通过ViewHolder拿到的position都需要减掉HeadView的数量。
            int fromPosition = viewHolder.getAdapterPosition() - binding.rvList.getHeaderCount();
            int toPosition = viewHolder1.getAdapterPosition() - binding.rvList.getHeaderCount();

            swapPositionData(fromPosition, toPosition);
            return true;
        }

        @Override
        public void onItemDismiss(RecyclerView.ViewHolder viewHolder) {
        }
    };

    private void swapPositionData(int fromPosition, int toPosition) {
        runOnUiThread(() -> {
            Loading.show(OptionPlayerActivity.this);
            Collections.swap(subtitleLanguageModels, fromPosition, toPosition);
            for (int i = 0; i < subtitleLanguageModels.size(); i++) {
                subtitleLanguageModels.get(i).setIndex(i);
            }
            adapter.notifyItemMoved(fromPosition, toPosition);
            updateVideoModelWhenSwapSubtitleLanguagePosition();

            Loading.hide();
        });

    }

    private void updateVideoModelWhenSwapSubtitleLanguagePosition() {
        if (subtitleLanguageModels.size() == 2) {
            updateVideoModelWhenSwapSubtitleLanguagePositionDisplayFirst();
            updateVideoModel(playerFileModel.getVideoModel());
        }
    }

    private void updateVideoModelWhenSwapSubtitleLanguagePositionDisplayFirst() {
        SubtitleLanguageModel subtitleLanguageModel = subtitleLanguageModels.get(0);
        if (subtitleLanguageModel.getLanguage() == studyLanguage.getIdApi()) {
            playerFileModel.getVideoModel().setIsDisplaySubtitleLangStudyFirst(Constant.INT_BOOLEAN.TRUE);
        } else {
            playerFileModel.getVideoModel().setIsDisplaySubtitleLangStudyFirst(Constant.INT_BOOLEAN.FASLE);
        }

    }

    private void openSubtitleEncoding(int index) {
        playerFileModel.getVideoModel().setSubPathIndex(index);
        Intent intent = new Intent(this, SubtitleEncodingActivity.class);
        intent.putExtra(Constant.PLAYER.INTENT.KEY_VIDEO_FILE, playerFileModel);
        startActivity(intent);
    }

//    private void parseSubtitleOnlyTextPart(BaseSubtitleObject subTitle) {
//        if (subTitle == null) {
//            ToastUtil.getInstance(this).show(R.string.error_msg_parser_sub_title);
//            Loading.hide();
//            return;
//        }
//        String content = Constant.BASE_BLANK;
//        for (SubtitleCue s : subTitle.getCues()) {
//            String text = StringUtils.replaceHTML(s.getText());
//            if (!Utils.isEmpty(text)) {
//                String line = "[" + (Utils.isEmpty(s.getId()) ? "" : s.getId() + ".") + TimeUtil.getDisplay(s.getStartTime().getTime()) + " ~ " + TimeUtil.getDisplay(s.getEndTime().getTime()) + "]" + Constant.BREAK_CHARACTER + text;
//                DLog.d(getLogTag(), s.getLanguage() + " - " + line);
//                if (!Utils.isEmpty(content)) {
//                    content += Constant.BREAK_CHARACTER;
//                }
//                content += line;
//            }
//        }
//        Utils.copyToClipboard(OptionPlayerActivity.this, content, R.string.copied);
//        Loading.hide();
//    }

    private void parserSubtitleEncoding(String content) {
        CopyTextUtil.copyToClipboard(this, content, R.string.copied);
        Loading.hide();
    }

    private void updateSubtitleEncoding() {
        if (playerFileModel == null) return;

        VideoModel videoModel = VideoModelQuery.getByPath(Voca.getRealm(), playerFileModel.getPath());
        if (videoModel == null) return;

        playerFileModel.setVideoModel(videoModel);
        updateVisibilityCopySubtitle();
        updateVisibilitySubtitle_Languages();

        binding.llSubTitleEncoding.setVisibility(View.GONE);
        binding.tvVideoFileName.setText(playerFileModel.getPath());
        TextViewUtil.makeTextViewMiddleTruncate(binding.tvVideoFileName, 3);

        if (Utils.isEmpty(StorageUtil.getNameFromPath(playerFileModel.getSubPath1()))) {
            binding.tvSubtitleFileName.setText(getString(R.string.subtitle_file_name_description));
            binding.llSubTitleEncoding.setVisibility(View.GONE);
//            binding.scUseSubtitle2.setVisibility(View.GONE);
        } else {
            binding.tvSubtitleFileName.setText(playerFileModel.getSubPath1());
            binding.llSubTitleEncoding.setVisibility(View.VISIBLE);
//            binding.scUseSubtitle2.setVisibility(View.VISIBLE);
        }

//        binding.scUseSubtitle2.setChecked(isCheckedScUseSubtitle2);
        if (isFirstOpenActivity && isCheckedScUseSubtitle2 && !playerFileModel.getVideoModel().hasSubPath2()) {
            makeInVisibleSubtitle2Menu();
            isFirstOpenActivity = false;
        } else {
            binding.llSubtitle2.setVisibility(isCheckedScUseSubtitle2 ? View.VISIBLE : View.GONE);
            if (Utils.isEmpty(StorageUtil.getNameFromPath(playerFileModel.getSubPath2()))) {
                binding.tvSubtitleFileName2.setText(getString(R.string.subtitle_file_name_description));
                binding.llSubTitleEncoding2.setVisibility(View.GONE);

            } else {
                binding.tvSubtitleFileName2.setText(playerFileModel.getSubPath2());
                binding.llSubTitleEncoding2.setVisibility(View.VISIBLE);
                binding.tvSubTitleValue2.setText(playerFileModel.getVideoModel().getSubtitleEncoding2());
            }

            TextViewUtil.makeTextViewMiddleTruncate(binding.tvSubtitleFileName, 3);
        }
        // update Subtitle Language Header
        getSubtitleLanguages();
    }

    private void getSubtitleLanguages() {
        if ((!Utils.isEmpty(playerFileModel.getSubPath1())) || (!Utils.isEmpty(playerFileModel.getSubPath2()))) {
            subtitleLanguageModels = SubtitleUtil.getSubtitleLanguageModels(this, playerFileModel, getSubDatabase());
            adapter.notifyDataSetChanged(subtitleLanguageModels);
        }
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
        switch (searchType) {
            case TYPE_INIT_DATA:
                //DALNIM : We need to get the real embedded subtitle list inside the video.
//                return StorageUtil.searchFileName(this, StorageUtil.getRootFolder(), StorageUtil.getNameWithoutExtension(playerFileModel.getName()), false);
                return null;

//            case TYPE_COPY_ONLY_TEXT:
//                return SubtitleUtil.parserSubTitle(playerFileModel, playerFileModel.getVideoModel().getEncoding());
            case TYPE_COPY_ADD_CONTENT:
                return SubtitleUtil.parserContentSubTitle(playerFileModel, Constant.PLAYER.INTENT.SUBPATH_INDEX_1);
            case TYPE_TRANSLATE_SUBTITLE:
                return getSubtitleFromDB((Boolean) data);
        }
        return null;
    }

    @Override
    public void onFinishAsyncTask(int searchType, Object resultData, Object data) {
        switch (searchType) {
            case TYPE_INIT_DATA:
                //DALNIM : We need to get the real embedded subtitle list inside the video.
                embedSubtitleFiles = (ArrayList<PlayerFileModel>) resultData;
                Loading.hide();
                break;
//            case TYPE_COPY_ONLY_TEXT:
//                parseSubtitleOnlyTextPart((BaseSubtitleObject) resultData);
//                break;
            case TYPE_COPY_ADD_CONTENT:
                parserSubtitleEncoding((String) resultData);
                break;
            case TYPE_TRANSLATE_SUBTITLE:
                onTranslate((ArrayList<DicModel>) resultData, (Boolean) data);
                break;
        }
    }

    private void callParserSubtitle(int type) {
        callAsyncTask(this, type);
    }

    private void makeInVisibleSubtitle2Menu() {
        isCheckedScUseSubtitle2 = false;
        switchVisibilitySubtitle2Menu(isCheckedScUseSubtitle2);
    }
    private void switchVisibilitySubtitle2Menu(boolean isCheckedScUseSubtitle2) {
//        isCheckedScUseSubtitle2 = !isCheckedScUseSubtitle2;
//        binding.scUseSubtitle2.setChecked(isCheckedScUseSubtitle2);
        playerFileModel.getVideoModel().setUseSecondSubtitle(isCheckedScUseSubtitle2);
        updateVideoModel(playerFileModel.getVideoModel());
        if (isCheckedScUseSubtitle2) {
            binding.llSubtitle2.setVisibility(View.VISIBLE);
        } else {
            binding.llSubtitle2.setVisibility(View.GONE);
        }
    }

    private void openSaveSubtitle() {
        Intent intent = new Intent(this, CopySubtitlePlayerActivity.class);
        intent.putExtra(Constant.PLAYER.INTENT.KEY_VIDEO_FILE, playerFileModel);
        startActivity(intent);
    }

    private void updateVisibilitySubtitle_Languages() {
        if (getSubDatabase().isHasBothSubtitleLanguages()) {
            binding.llPlayerOptionSubtitleLanguages.setVisibility(View.VISIBLE);
        } else {
            binding.llPlayerOptionSubtitleLanguages.setVisibility(View.GONE);
        }
    }

    private void updateVisibilityCopySubtitle() {
        updateVisibilityCopySubtitleSQLite();
        updateVisibilityCopySubtitle1();
        updateVisibilityCopySubtitle2();
    }


    private void updateVisibilityCopySubtitleSQLite() {
        int visibilityHasRuby = playerFileModel.isHasSubRuby(this) ? View.VISIBLE : View.GONE;
        binding.llSaveSubtitle.setVisibility(visibilityHasRuby);
    }

    private void updateVisibilityCopySubtitle1() {
        if (playerFileModel.getVideoModel().hasSubPath1()) {
            binding.llCopySubTitle1.setVisibility(View.VISIBLE);
        } else {
            binding.llCopySubTitle1.setVisibility(View.GONE);
        }
    }

    private void updateVisibilityCopySubtitle2() {
        if (playerFileModel.getVideoModel().hasSubPath2()) {
            binding.llCopySubTitle2.setVisibility(View.VISIBLE);
        } else {
            binding.llCopySubTitle2.setVisibility(View.GONE);
        }
    }

    private ArrayList<DicModel> getSubtitleFromDB(boolean isNew) {
        ArrayList<DicModel> list = new ArrayList<>();
        list.addAll(getSubDatabase().getNoTranslationSubtitleDialogListByLanguage());

        if (list.isEmpty()) return null;
        if (customTranslate == null) {
            customTranslate = new CustomTranslate(this);
        }

        for (DicModel item : list) {
            customTranslate.translateText(item.getVocaDisplay(), (view, object) -> {
                DLog.d(getLogTag(), item.getId() + " - " + item.getVocaDisplay() + " translate to =" + object);
                item.setMeaning((String) object);
                getSubDatabase().updateTranslate(item);

            });
        }

        return list;
    }

//    private ArrayList<DicModel> getSubtitleFromDB(boolean isNew) {
//        ArrayList<DicModel> list = new ArrayList<>();
//        list.addAll(getSubDatabase().getSubtitleDialogListByLanguage(studyLanguage.getIdApi()));
//        list.addAll(getSubDatabase().getSubtitleDialogListByLanguage(tongueLanguage.getIdApi()));
//
//        if (list.isEmpty()) return null;
//        if (customTranslate == null) {
//            customTranslate = new CustomTranslate(this);
//        }
//        int maxId = getSubDatabase().getMaxIdByTable(Constant.PLAYER.SQL.COLUMN.ID, Constant.PLAYER.SQL.TABLE.SUBTITLE) + 1;
//        for (DicModel dic : list) {
//            if (dic.getLangStudy() == studyLanguage.getIdApi()) {
//                DicModel item = new DicModel(dic);
//                if (isNew) {
//                    item.setId(maxId++);
//                }
//                customTranslate.translateText(item.getVocaDisplay(), (view, object) -> {
//                    DLog.d(getLogTag(), item.getId() + " - " + item.getVocaDisplay() + " translate to =" + object);
//                    item.setVocaDisplay((String) object);
//                    item.setVocaDisplayRuby(item.getVocaDisplay());
//                    item.setSubtitleOriginal(item.getVocaDisplay());
//                    if (isNew) {
//                        item.setLangStudy(tongueLanguage.getIdApi());
//                        item.setVocaId(0);
//                        item.setKnow(0);
//                        item.setKnowPronounce(0);
//                        getSubDatabase().addTranslate(item);
//                    } else {
//                        updateTranslationRecord(list, item);
//                    }
//                });
//            }
//        }
//
//        return list;
//    }

    private void openOptionMenuDialog() {
        final PlayerShowOptionMenuDialog dialog = new PlayerShowOptionMenuDialog(this, playerFileModel, (dialog1, which) -> {
            switch (which) {
                case R.id.tv_translate_subtitle:
                    checkAndOpenWarningSubtitleAlready();
                    break;
            }
        });
        dialog.show();
    }

    private void onTranslate(ArrayList<DicModel> data, boolean isNew) {
        if (data == null || data.isEmpty()) {
            Loading.hide();
            return;
        }
        final SupportSubtitleFormat type = FileUtil.getSubtitleExtension(playerFileModel.getSubPath1());
        if (isNew) {
//            SubtitleUtil.createSubtitleLanguageModel(Voca.getRealm(), playerFileModel.getPath(), tongueLanguage.getIdApi(), type.getFileSuffix(), true);
        }
        getSubtitleLanguages();
        Loading.hide();
    }

//    private void updateTranslationRecord(ArrayList<DicModel> data, DicModel item) {
//        for (DicModel d : data) {
//            if (d.getLangStudy() == tongueLanguage.getIdApi() && d.checkTime(item.getStartTimeOriginal(), item.getEndTimeOriginal())) {
//                d.setVocaDisplay(item.getVocaDisplay());
//                d.setVocaDisplayRuby(d.getVocaDisplay());
//                d.setSubtitleOriginal(d.getVocaDisplay());
//                getSubDatabase().updateTranslate(d);
//                return;
//            }
//        }
//    }

//    private boolean checkSubtitleLanguageById(int language) {
//        for (SubtitleLanguageModel s : subtitleLanguageModels) {
//            if (s.getLanguage() == language)
//                return true;
//        }
//        return false;
//    }

    private void checkAndOpenWarningSubtitleAlready() {
//        if (subtitleLanguageModels.isEmpty() || !checkSubtitleLanguageById(tongueLanguage.getIdApi())) {
//            callAsyncTask(this, true, TYPE_TRANSLATE_SUBTITLE);
//            return;
//        }
        final YesNoDialog dialog = new YesNoDialog(this,
                R.string.warning,
                R.string.translate_warning_msg,
                null,
                new OnYesNoClickListener() {
                    @Override
                    public void onYesClick(View view, Object object) {
                        callAsyncTask(OptionPlayerActivity.this, false, TYPE_TRANSLATE_SUBTITLE);
                    }

                    @Override
                    public void onNoClick(View view, Object object) {

                    }
                });
        dialog.show();
    }

    private void openVideoFileNameDialog() {
        final PlayerVideoFileNameDialog dialog = new PlayerVideoFileNameDialog(this, (view, object) -> {
            switch (view.getId()) {
                case R.id.tv_copy_name:
                    Utils.copyToClipboard(OptionPlayerActivity.this, playerFileModel.getPath(), R.string.copied);
                    break;
                case R.id.tv_rename:
                    openVideoRenameDialog(playerFileModel.getName());
                    break;
            }
        });
        dialog.show();
    }

    private void openVideoRenameDialog(String value) {
        final String currentName = FilenameUtils.getBaseName(value);
        final String extension = FilenameUtils.getExtension(value);
        final TypeInputDialog dialog = new TypeInputDialog(this,
                R.string.rename,
                R.string.msg_video_file_name_rename,
                R.string.type_new_filename,
                currentName,
                new BaseDialogListener() {
            @Override
            public void onBaseDialogListenerShow(EnumType type, BaseDialog dialog, View v, int position, Object data) {

            }

            @Override
            public void onBaseDialogListenerOk(EnumType type, BaseDialog dialog, View v, int position, Object data) {
                String newName = (String) data;
                if (Utils.isEmpty(newName) || currentName.equals(newName)) {
                    return;
                }
                dialog.dismiss();
                String newFileName = StorageUtil.generateExtension(newName, extension);
                File oldFile = new File(playerFileModel.getPath());
                renamedFileNewPath = FilenameUtils.concat(FilenameUtils.getFullPath(playerFileModel.getPath()), newFileName);
                File newFile = new File(renamedFileNewPath);
                //Dalnim : don't rename subtitle file together.
//                if (!Utils.isEmpty(playerFileModel.getSubPath())) {
//                    String subtitleNameOld = StorageUtil.getNameWithoutExtension(playerFileModel.getSubPath());
//                    String subtitleName = FilenameUtils.getBaseName(playerFileModel.getSubPath());
//                    String subtitleFileExtension = FilenameUtils.getExtension(playerFileModel.getSubPath());
//                    String newSubtitleFileName = StorageUtil.generateExtension(newName, subtitleFileExtension);
//                    String newSubtitlePath = FilenameUtils.concat(FilenameUtils.getFullPath(playerFileModel.getSubPath()), newSubtitleFileName);
////                    playerFileModel.setSubPath(playerFileModel.getSubPath().replace(subtitleName, newName));
//                    playerFileModel.setSubPath(newSubtitlePath);
//                }
                if (newFile.exists()) {
                    openWarningSameNameDialog(oldFile, newFile);
                } else {
                    renameVideoFile(oldFile, newFile);
                }
            }

            @Override
            public void onBaseDialogListenerCancel(EnumType type, BaseDialog dialog, View v, int position, Object data) {

            }

            @Override
            public void onBaseDialogListenerClick(EnumType type, BaseDialog dialog, View v, int position, Object data) {

            }

            @Override
            public void onBaseDialogListenerClickMulti(EnumType type, BaseDialog dialog, View v, int position, Object[] data) {

            }
        });
        dialog.show();
    }

    private void openWarningSameNameDialog(File oldFile, File newFile) {
        final YesNoDialog dialog = new YesNoDialog(this, R.string.warning, R.string.msg_video_file_same_name, null, new OnYesNoClickListener() {
            @Override
            public void onYesClick(View view, Object object) {
                //Dalnim : don't rename subtitle file together.
//                final VideoModel oldVideo = VideoModelQuery.getByPath(Voca.getRealm(), newFile.getPath());
//                if (oldVideo != null && StorageUtil.checkSubDatabaseFile(OptionPlayerActivity.this, oldVideo.getSubPath())) {
//                    playerFileModel.setSubPath(oldVideo.getSubPath());
//                }
                boolean isDeleted = StorageUtil.removeVideoFile(OptionPlayerActivity.this, newFile.getPath(), deleteSameFileNameIntentSenderLauncher);
                if (isDeleted) {
                    renameVideoFile(oldFile, newFile);
                }
            }

            @Override
            public void onNoClick(View view, Object object) {

            }
        });
        dialog.show();
    }

    private void renameVideoFile(File oldFile, File newFile) {
        boolean isRenamed = oldFile.renameTo(newFile);
        if (isRenamed) {
            onVideoRename(oldFile, newFile);
            showToastResultRenameSucceed();
        } else {
//            new AlertDialog(this).show(R.string.warning, R.string.msg_confirm_rename_file_by_backup_and_delete_and_restore_method, 0, new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialogInterface, int i) {
//                    tempFile = newFile;
//                    BaseStorageUtil.copy(oldFile, tempFile);
//                    BaseStorageUtil.removeVideoFile(OptionPlayerActivity.this, oldFile.getPath(),renameFileIntentSenderLauncher);
//                    //TODO : I can't close this alert view when I click OK button.
//                }
//            });

            final YesNoDialog dialog = new YesNoDialog(this, R.string.warning, R.string.msg_confirm_rename_file_by_backup_and_delete_and_restore_method, null, new OnYesNoClickListener() {
                @Override
                public void onYesClick(View view, Object object) {
                    tempFile = newFile;
                    BaseStorageUtil.copy(oldFile, tempFile);
                    BaseStorageUtil.removeVideoFile(OptionPlayerActivity.this, oldFile.getPath(),renameFileIntentSenderLauncher);
                }

                @Override
                public void onNoClick(View view, Object object) {
                }
            });
            dialog.show();


        }
    }

    private void onVideoRename(File oldFile, File newFile) {
        VideoModelQuery.deleteByPath(Voca.getRealm(), oldFile.getPath());
        playerFileModel.setPath(newFile.getPath());
        playerFileModel.setName(newFile.getName());
        playerFileModel.getVideoModel().setName(newFile.getName());
        updateVideoModel(playerFileModel.getVideoModel());
        onResume();
    }

    private void showToastResultRenameSucceed() {
        ToastUtil.getInstance(this).show(R.string.toast_result_rename_succeed);
    }

    private void showToastResultRenameFailed() {
        ToastUtil.getInstance(this).show(R.string.toast_result_rename_failed);
    }

    @Override
    public void onDetachSubtitleFile(boolean canDetach) {
        if (canDetach) {
            detachSubtitleFile(subPathIndex);
        } else {
            //사실 두번째 자막이 있을때는 첫번째 자막을 detach할수 있는 메뉴를 안보여주기 때문에 이 코드가 동작할일은 없다.
            if (subPathIndex == Constant.PLAYER.INTENT.SUBPATH_INDEX_1) {
                ToastUtil.getInstance(context).show("Please detach second subtitle first");
            }
        }
    }

    private void detachSubtitleFile(int subPathIndex) {
        resetSubtitleFileAndDatabase(playerFileModel, "", subPathIndex);
        runOnUiThread(this::updateSubtitleEncoding);
    }
}
