package com.dalread.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;

import com.dalread.R;
import com.dalread.asyntask.OnAsyncTaskListener;
import com.dalread.base.BasePlayerVideoFragment;
import com.dalread.base.EnumLanguage;
import com.dalread.database.WebDictionaryQuery;
import com.dalread.database.sqlite.model.DicModel;
import com.dalread.databinding.FragmentPlayerEditSubtitleBinding;
import com.dalread.dialog.ConfirmationDialog;
import com.dalread.dialog.WebDictionaryDialog;
import com.dalread.dialog.YesNoDialog;
import com.dalread.listener.OnClickDialogListener;
import com.dalread.listener.OnYesNoClickListener;
import com.dalread.model.MultipleWordMeaningWithIDModel;
import com.dalread.model.WebDictionaryModel;
import com.dalread.network.DalApiListener;
import com.dalread.network.events.BaseEvent;
import com.dalread.network.events.SuccessEvent;
import com.dalread.util.Constant;
import com.dalread.util.DLog;
import com.dalread.util.Loading;
import com.dalread.util.MergeUtil;
import com.dalread.util.StringUtils;
import com.dalread.util.TimeUtil;
import com.dalread.util.ToastUtil;
import com.dalread.util.Utils;
import com.dalread.util.ViewUtil;
import com.dalread.util.Voca;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ui.StyledPlayerView;
import com.google.gson.Gson;
import com.jaygoo.widget.RangeSeekBar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.OnClick;

public class EditSubtitleFragment extends BasePlayerVideoFragment<FragmentPlayerEditSubtitleBinding> implements OnAsyncTaskListener {

    private final HashMap<Integer, Integer> listId = new HashMap<>();
    private final int TYPE_INIT_DATA = 0;
    private final int minSpaceToInsertMilli = 1000;
    private String studyLanguageContent;
    private String tongueLanguageContent;
    private FragmentPlayerEditSubtitleBinding binding;

    @Override
    protected View getContentView() {
        binding = FragmentPlayerEditSubtitleBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void initView() {
        super.initView();
        final Bundle bundle = getArguments();
        subtitleIndex = bundle.getInt(Constant.PLAYER.INTENT.KEY_INDEX, 0);
        final String idList = bundle.getString(Constant.PLAYER.INTENT.KEY_DATA, Constant.BASE_BLANK);
//        if (activity.playerFileModel == null || Utils.isEmpty(idList)) {
        if (activity.playerFileModel == null) {
            onBackPressed();
            return;
        }

//        refreshVisibilityUI();

        setMinMaxSubWhenNoIDList(idList);
        getPlayerWidthPercent();
        binding.etStudyLanguageContent.setCustomSelectionActionModeCallback(onWebDictionaryListener);
        binding.etTongueLanguageContent.setCustomSelectionActionModeCallback(onWebDictionaryListener);
        initData();
        initListenerAll();
        activity.callAsyncTask(this, idList, TYPE_INIT_DATA);
    }

    private void refreshVisibilityUI() {
        if (isABRepeatMode()) {
            setVisibleBtnExitCCRepeat(View.INVISIBLE);
//            viewBinding.btnExitCCRepeat.setVisibility(View.INVISIBLE);
            binding.btnAddSubtitle.setVisibility(View.VISIBLE);
        } else {
            binding.btnAddSubtitle.setVisibility(View.INVISIBLE);
//            viewBinding.btnExitCCRepeat.setVisibility(View.VISIBLE);
        }

        if (isCCRepeatMode()) {
            setVisibleIvABRepeat(View.INVISIBLE);
//            viewBinding.ivABRepeat.setVisibility(View.INVISIBLE);
            setVisibleBtnExitCCRepeat(View.VISIBLE);
//            viewBinding.btnExitCCRepeat.setVisibility(View.VISIBLE);
        } else {
            setVisibleIvABRepeat(View.VISIBLE);
//            viewBinding.ivABRepeat.setVisibility(View.VISIBLE);
            setVisibleBtnExitCCRepeat(View.INVISIBLE);
//            viewBinding.btnExitCCRepeat.setVisibility(View.INVISIBLE);
        }

        binding.llABRepeat.setVisibility(Utils.isDebugOrAdminUser(getContext()) ? View.VISIBLE : View.GONE);
    }

    private void setMinMaxSubWhenNoIDList(String idList) {
        if (Utils.isEmpty(idList)) {
            // TODO : Need to consider activity.playerFileModel.getVideoModel().getDelaySubtitles();
            setDefaultMinMaxSub();
        }
    }

    private void setDefaultMinMaxSub() {
        minSub = 0;
        setDefaultMaxSub();
    }

    private void setDefaultMaxSub() {
        maxSub = activity.playerFileModel.getDuration();
    }

    @Override
    public void initData() {
        updateForwardBackwardValue();
        setEditSubtitle();
        resizePlayerView(getLLPlayer());
        initExoPlayer();
    }

    public void onBackPressed() {
        killPlayer();
        if (!listId.isEmpty()) {
            String ids = Constant.BASE_BLANK;
            for (Map.Entry<Integer, Integer> entry : listId.entrySet()) {
                Integer key = entry.getKey();
                ids = StringUtils.addString(ids, String.valueOf(key));
            }
            setAnalyzeAgain();
            activity.updateVideoModel(activity.playerFileModel.getVideoModel());
            DLog.e(getLogTag(), "ids=" + ids);

            application.getEventBus().post(new SuccessEvent(BaseEvent.Screen.EDIT_SUBTITLE, BaseEvent.EventType.PLAYER_EDIT_SUBTITLE, ids));
//            // post to PlayerFragment
//            application.getEventBus().post(new SuccessEvent(BaseEvent.Screen.DAL_PLAYER, BaseEvent.EventType.PLAYER_EDIT_SUBTITLE, ids));
//            // post to VideoInformationActivity
//            application.getEventBus().post(new SuccessEvent(BaseEvent.Screen.VIDEO_INFORMATION, BaseEvent.EventType.PLAYER_EDIT_SUBTITLE, true));
        }
        activity.finish();
    }

    @OnClick({R.id.iconLeft, R.id.tv_right,
            R.id.iv_prev, R.id.iv_next,
            R.id.llPlayer, R.id.ivCenterPlay,
            R.id.btnCCRepeatReset,
            R.id.ivAudioSpeedMinus, R.id.ivAudioSpeedPlus,
            R.id.iv_copy_study_language, R.id.iv_copy_tongue_language,
            R.id.tv_study_language_translate, R.id.tv_tongue_language_translate,
            R.id.btn_delete, R.id.btn_divide, R.id.btn_merge_prev_dialog, R.id.btn_merge_next_dialog,
            R.id.btn_insert_before, R.id.btn_insert_after, R.id.btn_save, R.id.ivABRepeat,
            R.id.btnAddSubtitle, R.id.btnExitCCRepeat, R.id.ivWebDictionary})
    void onClick(View view) {
        switch (view.getId()) {
            case R.id.iconLeft:
                onBackButton();
                break;
            case R.id.tv_right:
            case R.id.btn_save:
                onSaveButton();
                break;
            case R.id.iv_prev:
                onPrevSubtitles();
                break;
            case R.id.iv_next:
                onNextSubtitles();
                break;
//            case R.id.llPlayer:
//                onPlayerClick();
//                break;
            case R.id.ivCenterPlay:
                handlePlayClick((Integer) getIvCenterPlay().getTag() == R.drawable.ic_new_play, true);
                break;
            case R.id.btnCCRepeatReset:
                onResetButton();
                break;
            case R.id.ivAudioSpeedMinus:
                handSpeedMinusClick();
                break;
            case R.id.ivAudioSpeedPlus:
                handleSpeedPlusClick();
                break;
            case R.id.iv_copy_study_language:
                Utils.copyToClipboard(activity, binding.etStudyLanguageContent.getText().toString(), R.string.copied);
                break;
            case R.id.iv_copy_tongue_language:
                Utils.copyToClipboard(activity, binding.etTongueLanguageContent.getText().toString(), R.string.copied);
                break;
            case R.id.tv_study_language_translate:
                openTranslatorScreen(true);
                break;
            case R.id.tv_tongue_language_translate:
                openTranslatorScreen(false);
                break;
            case R.id.btn_delete:
            case R.id.btn_divide:
            case R.id.btn_merge_prev_dialog:
            case R.id.btn_merge_next_dialog:
                openWarningDialog(view.getId());
                break;
            case R.id.btn_insert_before:
                onInsert(true);
                break;
            case R.id.btn_insert_after:
                onInsert(false);
                break;
            case R.id.ivABRepeat:
                resetRepeat();
//                isRepeat = false;
                handleABRepeatClick();
                refreshVisibilityUI();
                break;
            case R.id.btnAddSubtitle:
                onInsertABRepeat();
                break;
            case R.id.btnExitCCRepeat:
                resetRepeat();
//                isRepeat = false;
                setDefaultMinMaxSub();
                setVisibleABRepeatButton();
                refreshVisibilityUI();
                break;
            case R.id.ivWebDictionary:
                openWebDictionary();
                break;
        }
    }

    protected void openWebDictionary() {
            final WebDictionaryModel webDictionary = WebDictionaryQuery.getFirst(Voca.getRealm(),
                    activity.studyLanguage.getIdApi(), activity.motherTongueLanguage.getIdApi());
            String url;
            if (webDictionary == null) {
                if (activity.motherTongueLanguage.getIdApi() == EnumLanguage.KOREAN.getIdApi()) {
                    url = Constant.PLAYER.WEB_DICTIONARY.URL_DEFAULT;
                } else {
                    openWebDictionary();
                    return;
                }
            } else {
                url = webDictionary.getUrl();
            }
            final WebDictionaryDialog dialog = new WebDictionaryDialog(activity, url, new OnClickDialogListener() {
                @Override
                public void onClick(View view, Object object) {

                }

                @Override
                public void onDismiss(View view, Object object) {

                }
            });
            dialog.show();

    }


    @Override
    public void onInitAsyncTask() {
        Loading.show(activity);
    }

    @Override
    public void onPrevExecuteAsyncTask() {

    }

    @Override
    public Object onPostExecuteAsyncTask(int searchType, Object data) {
        switch (searchType) {
            case TYPE_INIT_DATA:
                return getSubtitleData(data);
        }
        return null;
    }

    @Override
    public void onFinishAsyncTask(int searchType, Object resultData, Object data) {
        switch (searchType) {
            case TYPE_INIT_DATA:
                subtitleList = (ArrayList<DicModel>) resultData;
                updateCountSubtitle();
                refreshVisibilityUI();
                break;
        }
        Loading.hide();
    }

    private ArrayList<DicModel> getSubtitleData(Object data) {
        final String idList = (String) data;
        ArrayList<DicModel> list = new ArrayList<>();
        list.addAll(activity.getSubDatabase().getSubtitleDialogListByIds(idList));
        MergeUtil.generateMeaning(list);
        return list;
    }

    @Override
    protected void updateCountSubtitle() {
        DicModel dicModel = getDicModel(subtitleIndex);
        if (dicModel == null)
            return;

        activity.runOnUiThread(() -> {
            DLog.d(getLogTag(), "id=" + subtitleList.get(subtitleIndex));
            udpateStartEndTimeDetailed(dicModel.getStartTime(), dicModel.getEndTime());
            binding.tvCountSubtitles.setText(getString(R.string.format_edit_subtitles, (subtitleIndex + 1), subtitleList.size()));

            updateTextSubtitleDialog(dicModel, binding.etStudyLanguageContent, binding.etTongueLanguageContent);
            ViewUtil.setViewListVisibility(View.VISIBLE, binding.btnMergePrevDialog, binding.btnMergeNextDialog);
            if ((subtitleIndex - 1) < 0) {
                ViewUtil.setViewListVisibility(View.INVISIBLE, binding.btnMergePrevDialog);
            } else if (subtitleIndex >= subtitleList.size() - 1) {
                ViewUtil.setViewListVisibility(View.INVISIBLE, binding.btnMergeNextDialog);
            } else {
                updatePrevNextTextSubtitleDialog(getDicModel(subtitleIndex - 1), binding.tvStudyLanguagePrevious, binding.tvTongueLanguagePrevious);
                updatePrevNextTextSubtitleDialog(getDicModel(subtitleIndex + 1), binding.tvStudyLanguageNext, binding.tvTongueLanguageNext);
            }
            getMinMaxTime(subtitleList.get(subtitleIndex));
//            minSub = getStartTimeWithoutBeforeTime(subtitleList.get(subtitleIndex));
//            maxSub = getEndTimeWithoutAfterTime(subtitleList.get(subtitleIndex));
            if (isCCRepeatMode()) {
//                seekToInPlayer_MinSubWithDelayTime_IfSubtitleEndTimeExceedDelaySubtitle(exoPlayer.getCurrentPosition());
                seekToInPlayer_MinSubWithDelayTime();
            }
//            exoPlayer.seekTo((long) minSub);
            updateSeekBarPlay((long) minSub);
            updateRangeSeek();
            playPlayer();
            binding.btnSave.setVisibility(View.INVISIBLE);


        });
    }

    private void updatePrevNextTextSubtitleDialog(DicModel item, TextView tvStudy, TextView tvTongue) {
        if (item != null) {
//            tvStudy.setText(Constant.BASE_BLANK);
//            tvTongue.setText(Constant.BASE_BLANK);
//        } else {
            tvStudy.setText(item.getVocaDisplay().replaceAll(System.getProperty("line.separator"), ""));
            tvTongue.setText(item.getMeaning().replaceAll(System.getProperty("line.separator"), ""));
        }

//        if ((item == null) || (item.getVocaDisplay().equals(""))) {
//            tvStudy.setVisibility(View.GONE);
//        } else {
//            tvStudy.setVisibility(View.VISIBLE);
//            tvStudy.setText(item.getVocaDisplay().replaceAll(System.getProperty("line.separator"), ""));
//        }
//
//        if ((item == null) || (item.getMeaning().equals(""))) {
//            tvTongue.setVisibility(View.GONE);
//        } else {
//            tvTongue.setVisibility(View.VISIBLE);
//            tvTongue.setText(item.getMeaning().replaceAll(System.getProperty("line.separator"), ""));
//        }
    }

    private void updateTextSubtitleDialog(DicModel item, EditText etStudy, EditText etTongue) {
        if (item == null) {
            etStudy.setText(Constant.BASE_BLANK);
            etTongue.setText(Constant.BASE_BLANK);
        } else {
            etStudy.setText(item.getVocaDisplay());
            etTongue.setText(item.getMeaning());
        }
    }

    private void onBackButton() {
        checkAndUpdateSubtitle(true, false, false, true);
        killPlayer();
    }

    private void onSaveButton() {
        checkAndUpdateSubtitle(false, true, false, false);
        Utils.hideSoftKeyboard(activity);
    }

    private void onPrevSubtitles() {
//        setInvisibleABRepeatButton();
        checkAndUpdateSubtitle(true, false, false, false);
    }

    private void setInvisibleABRepeatButton() {
        setVisibleIvABRepeat(View.INVISIBLE);
//        viewBinding.ivABRepeat.setVisibility(View.INVISIBLE);
    }

    private void setVisibleABRepeatButton() {
        setVisibleIvABRepeat(View.VISIBLE);
//        viewBinding.ivABRepeat.setVisibility(View.VISIBLE);
    }

    private void onNextSubtitles() {
//        setInvisibleABRepeatButton();
        checkAndUpdateSubtitle(true, false, true, false);
    }

    private void openTranslatorScreen(boolean isFromStudyLanguage) {
        pausePlayer();
        Intent intent = new Intent(activity, TranslatorActivity.class);
        intent.putExtra(Constant.PLAYER.INTENT.KEY_DATA, isFromStudyLanguage);
        intent.putExtra(Constant.PLAYER.INTENT.KEY_WORD, isFromStudyLanguage ?
                binding.etStudyLanguageContent.getText().toString() : binding.etTongueLanguageContent.getText().toString());
        startActivity(intent);
    }

    private void checkAndUpdateSubtitle(boolean isShowDialog, boolean isSave, boolean isNext, boolean isFinish) {
        final String studyLanguageContent = binding.etStudyLanguageContent.getText().toString();
        final String tongueLanguageContent = binding.etTongueLanguageContent.getText().toString();

        if (isDialogChanged()) {
            if (isShowDialog) {

                ConfirmationDialog confirmationDialog = new ConfirmationDialog(activity,
                        R.string.warning,
                        R.string.msg_warning_you_have_changed_subtitle,
                        R.string.yes_upper,
                        R.string.no_upper,
                        new ConfirmationDialog.OnDialogClickListener() {
                            @Override
                            public void onPositive(DialogInterface dialog) {
                                dialog.dismiss();
                                callUpdateMultipleWordMeaning(isSave, isNext, studyLanguageContent, tongueLanguageContent, isFinish);
                            }

                            @Override
                            public void onNegative(DialogInterface dialog) {
                                dialog.dismiss();
                                checkPrevNextSubtitle(isNext, isFinish);
                            }
                        });
                confirmationDialog.show();
            } else {
                callUpdateMultipleWordMeaning(isSave, isNext, studyLanguageContent, tongueLanguageContent, isFinish);
            }
            setAnalyzeAgain();
        } else if (!isSave) {
            checkPrevNextSubtitle(isNext, isFinish);
        }
        binding.btnSave.setVisibility(View.INVISIBLE);
    }

//    private void setAnalyzeAgain() {
//        activity.playerFileModel.getVideoModel().setAnalyzeAgain(Constant.INT_BOOLEAN.TRUE);
//        activity.updateVideoModel(activity.playerFileModel.getVideoModel());
//    }

    private boolean isDialogChanged() {
        if (Utils.isEmpty(subtitleList) || !Utils.isIndexInsideRange(subtitleList, subtitleIndex))
            return false;

        final String studyLanguageContent = binding.etStudyLanguageContent.getText().toString();
        final String tongueLanguageContent = binding.etTongueLanguageContent.getText().toString();

        return !studyLanguageContent.equals(subtitleList.get(subtitleIndex).getVocaDisplay()) ||
                !tongueLanguageContent.equals(subtitleList.get(subtitleIndex).getMeaning());
    }

    private void callUpdateMultipleWordMeaning(boolean isSave, boolean isNext, String studyLanguageContent, String tongueLanguageContent, boolean isFinish) {
        int uid = activity.getUserID();
        if (uid > 0) {
            if (Utils.isConnected(activity)) {
                application.getDalAiImpl().updateMultipleWordMeaningWithID(
                        generateJSON(studyLanguageContent, tongueLanguageContent),
                        new DalApiListener<MultipleWordMeaningWithIDModel.ResponseModel>() {
                            @Override
                            public void onSuccess(MultipleWordMeaningWithIDModel.ResponseModel response) {
                                if (response != null && response.isHasNewVocaIdList()) {
//                            subtitleList.get(subtitleIndex).setVocaIdServer(response.getVocaId());
                                    subtitleList.get(subtitleIndex).setVocaId(response.getVocaId());
                                }
                                subtitleList.get(subtitleIndex).setVocaDisplay(studyLanguageContent);
                                String studyLanguageContentForRuby = StringUtils.replaceLineBreaksToBRtag(studyLanguageContent);
                                subtitleList.get(subtitleIndex).setVocaDisplayRuby(studyLanguageContentForRuby);
                                subtitleList.get(subtitleIndex).setMeaning(tongueLanguageContent);
                                listId.put(subtitleList.get(subtitleIndex).getId(), subtitleList.get(subtitleIndex).getId());
                                activity.getSubDatabase().updateMultipleWordMeaning(subtitleList.get(subtitleIndex));

                                ToastUtil.getInstance(activity).show(R.string.saved);
                                if (!isSave) {
                                    checkPrevNextSubtitle(isNext, isFinish);
                                }
                            }

                            @Override
                            public void onFailure(String error) {
                                DLog.e(getLogTag(), "error=" + error);
                            }
                        });
            } else {
                activity.alertDialog.showNoInternet();
            }
        } else {
            activity.alertDialog.showLogInRequired();
        }
    }

    private void checkPrevNextSubtitle(boolean isNext, boolean isFinish) {
        if (Utils.isEmpty(subtitleList))
            return;

        if (isFinish) {
            onBackPressed();
        } else {
            if (isNext) {
                subtitleIndex++;
                if (subtitleIndex < 0) {
                    subtitleIndex = 0;
                }
                if (subtitleIndex >= subtitleList.size()) {
                    subtitleIndex = 0;
                }
            } else {
                subtitleIndex--;
                if (subtitleIndex < 0) {
                    subtitleIndex = subtitleList.size() - 1;
                }
            }
            updateCountSubtitle();
            setRepeat();
            refreshVisibilityUI();
        }
    }

    private MultipleWordMeaningWithIDModel generateJSON(String studyLanguageContent, String tongueLanguageContent) {
        MultipleWordMeaningWithIDModel data = new MultipleWordMeaningWithIDModel();
        data.setUid(sharedPreferences.getUidDefault());
        data.setStudyCode(activity.studyLanguage.getIdApi());
        data.setMeaningCode(activity.motherTongueLanguage.getIdApi());
        List<MultipleWordMeaningWithIDModel.InfoListModel> infoList = new ArrayList<>();
        MultipleWordMeaningWithIDModel.InfoListModel item = new MultipleWordMeaningWithIDModel.InfoListModel();
        item.setIndex(subtitleList.get(subtitleIndex).getIndex());
        item.setVocaType(subtitleList.get(subtitleIndex).getVocaType());
//        item.setVocaId(subtitleList.get(subtitleIndex).getVocaId());
        item.setVocaId(-1);
        item.setVoca(studyLanguageContent);
        item.setMeaning(tongueLanguageContent);
        infoList.add(item);
        data.setInfoList(infoList);
        return data;
    }

//    private void onPlayerClick() {
//        if (numberOfTaps == 0) {
//            numberOfTaps++;
//            mHandler.postDelayed(() -> {
//                if (numberOfTaps > 1) {
//                    int visibility = getLLRepeat().getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE;
//                    ViewUtil.setViewListVisibility(visibility, getLLRepeat(), getLLPlay());
////                    llRepeat.setVisibility(visibility);
////                    llSpeed.setVisibility(visibility);
////                    llPlay.setVisibility(visibility);
//                } else {
//                    getIvCenterPlay().performClick();
//                }
//                numberOfTaps = 0;
//            }, ViewConfiguration.getDoubleTapTimeout());
//        } else {
//            numberOfTaps++;
//        }
//    }

    private void openWarningDialog(int viewId) {
        final YesNoDialog dialog = new YesNoDialog(activity,
                R.string.warning,
                R.string.msg_edit_subtitle_dialog,
                null,
                new OnYesNoClickListener() {
                    @Override
                    public void onYesClick(View view, Object object) {
                        if (viewId == R.id.btn_divide) {
                            onDivide();
                        } else if (viewId == R.id.btn_delete) {
                            onDelete();
                        } else {
                            onMergeWidthDialog(viewId);
                        }
                    }

                    @Override
                    public void onNoClick(View view, Object object) {

                    }
                });
        dialog.show();
    }

    //Dalnim add
    private void onDelete() {
        DicModel dicModel = getDicModel(subtitleIndex);
        activity.getSubDatabase().deleteSubtitle(dicModel.getId());
        subtitleList.remove(dicModel);
        subtitleIndex--;
        subtitleIndex = Voca.getDefaultIndexIfOutOfIndex(subtitleList, subtitleIndex);
        updateCountSubtitle();
        binding.svMain.fullScroll(View.FOCUS_UP);
        ToastUtil.getInstance(activity).show(R.string.msg_deleted);
    }

    private void onDivide() {
        DicModel dicModel = getDicModel(subtitleIndex);

        Gson gson = new Gson();
        DicModel newDicModel = gson.fromJson(gson.toJson(dicModel), DicModel.class);

        int midTime = (int) ((dicModel.getStartTime() + dicModel.getEndTime()) / 2);
        int midTimeOrginal = (int) (dicModel.getStartTimeOriginal() + dicModel.getEndTimeOriginal()) / 2;

        dicModel.setEndTime(midTime);
        dicModel.setEndTimeOriginal(midTimeOrginal);
        activity.getSubDatabase().updateSubtitle(dicModel);

        setNewIDForNewDicModel(newDicModel);
        newDicModel.setStartTime(midTime);
        newDicModel.setEndTimeOriginal(midTime);
        activity.getSubDatabase().addSubtitle(newDicModel);
        subtitleList.add(newDicModel);
        Collections.sort(subtitleList, (p1, p2) -> (int) (p1.getStartTime() - p2.getStartTime()));

        updateCountSubtitle();
        binding.svMain.fullScroll(View.FOCUS_UP);
        ToastUtil.getInstance(activity).show(R.string.msg_divided);
    }

//    //Dalnim add
//    private void setNewIDForNewDicModel(DicModel newDicModel) {
//        newDicModel.setVocaType(Constant.API_VALUE.VALUE_VOCA_TYPE_SUBTITLE);
//        newDicModel.setVocaTypeBase(Constant.API_VALUE.VALUE_VOCA_TYPE_SUBTITLE);
//        newDicModel.setId(activity.getSubDatabase().getNewIDInSubtitleTable());
//        newDicModel.setVocaId(activity.getSubDatabase().getNewVocaIDInSubtitleTable());
//        newDicModel.setLangStudy(EnumLanguage.findByFormatApi(Constant.API.STUDY_LANG).getIdApi());
////        dicModelNew.setVocaIdServer(getNewIDInSubtitleTable(Constant.PLAYER.SQL.COLUMN.VOCA_ID_TO_SEND_SERVER));
//    }
//    //Dalnim add
//    private int getNewIDInSubtitleTable(String column) {
//        int value = activity.getSubDatabase().getMinColumnValueInTbl(column, Constant.PLAYER.SQL.TABLE.SUBTITLE);
//        if (value >= 0) {
//            return -1;
//        }
//        return value - 1;
//    }

//    //Dalnim add
//    private int getNewIDInSubtitleTable() {
//        String column = Constant.PLAYER.SQL.COLUMN.ID;
//        int value = activity.getSubDatabase().getMaxColumnValueInTbl(column, Constant.PLAYER.SQL.TABLE.SUBTITLE);
//        return value + 1;
//    }
//
//    //Dalnim add
//    private int getNewVocaIDInSubtitleTable() {
//        String column = Constant.PLAYER.SQL.COLUMN.VOCA_ID;
//        int value = activity.getSubDatabase().getMinColumnValueInTbl(column, Constant.PLAYER.SQL.TABLE.SUBTITLE);
//        if (value >= 0) {
//            return -1;
//        }
//        return value - 1;
//    }


    private void onMergeWidthDialog(int viewId) {
        boolean isPrev = viewId == R.id.btn_merge_prev_dialog;
        int nextOrPrevSubtitleIndex = isPrev ? subtitleIndex - 1 : subtitleIndex + 1;
        final DicModel dicModelNextOrPrev = getDicModel(nextOrPrevSubtitleIndex);
        if (dicModelNextOrPrev == null) return;

        DicModel dicModel = getDicModel(subtitleIndex);
        //Dalnim : Get the smallest minus value for VocaIDToserver(not just -1), but don't change VOCA ID, it's used in local SQLIte only.
        //And need to send server the dialog ang get new voca sender id for this. Otherwise the KNOW is not saved in server until I change the KNOW value.
//        dicModel.setVocaIdServer(activity.getSubDatabase().getMinColumnValueInTbl(Constant.PLAYER.SQL.COLUMN.VOCA_ID_TO_SEND_SERVER, Constant.PLAYER.SQL.TABLE.SUBTITLE) - 1);
        dicModel.setVocaId(activity.getSubDatabase().getMinColumnValueInTbl(Constant.PLAYER.SQL.COLUMN.VOCA_ID, Constant.PLAYER.SQL.TABLE.SUBTITLE) - 1);
        String voca, meaning, memo;
        if (isPrev) {
            voca = dicModel.getVocaDisplay().trim().equals("") ? dicModelNextOrPrev.getVocaDisplay() : dicModelNextOrPrev.getVocaDisplay() + "\n" + dicModel.getVocaDisplay();
            meaning = dicModel.getMeaning().trim().equals("") ? dicModelNextOrPrev.getMeaning() : dicModelNextOrPrev.getMeaning() + "\n" + dicModel.getMeaning();
            memo = dicModel.getMemo().trim().equals("") ? dicModelNextOrPrev.getMemo() : dicModelNextOrPrev.getMemo() + "\n" + dicModel.getMemo();

            dicModel.setStartTime(dicModelNextOrPrev.getStartTime());
            dicModel.setStartTimeOriginal(dicModelNextOrPrev.getStartTimeOriginal());
            subtitleIndex--;
        } else {
            voca = dicModelNextOrPrev.getVocaDisplay().trim().equals("") ? dicModel.getVocaDisplay() : dicModel.getVocaDisplay() + "\n" + dicModelNextOrPrev.getVocaDisplay();
            meaning = dicModelNextOrPrev.getMeaning().trim().equals("") ? dicModel.getMeaning() : dicModel.getMeaning() + "\n" + dicModelNextOrPrev.getMeaning();
            memo = dicModelNextOrPrev.getMemo().trim().equals("") ? dicModel.getMemo() : dicModel.getMemo() + "\n" + dicModelNextOrPrev.getMemo();

            dicModel.setEndTime(dicModelNextOrPrev.getEndTime());
            dicModel.setEndTimeOriginal(dicModelNextOrPrev.getEndTimeOriginal());
        }
        dicModel.setVocaDisplay(voca.trim());
        dicModel.setMeaning(meaning.trim());
        dicModel.setMemo(memo.trim());
        dicModel.setVocaDisplayRuby(voca.trim());
        activity.getSubDatabase().updateSubtitle(dicModel);
        activity.getSubDatabase().deleteSubtitle(dicModelNextOrPrev.getId());

        subtitleList.remove(nextOrPrevSubtitleIndex);
        subtitleIndex = Voca.getDefaultIndexIfOutOfIndex(subtitleList, subtitleIndex);
        updateCountSubtitle();
        binding.svMain.fullScroll(View.FOCUS_UP);
        ToastUtil.getInstance(activity).show(R.string.msg_merged);
    }

    private boolean isNotAbleToInsert(boolean isPrev) {
        if (Utils.isEmpty(subtitleList))
            return false;

        DicModel dicModel = getDicModel(subtitleIndex);
        boolean blnResult = false;
        if ((subtitleIndex == 0) && (dicModel.getStartTime() < minSpaceToInsertMilli)) {
            blnResult = true;
        }

        if ((subtitleIndex >= subtitleList.size() - 1) && (exoPlayer.getDuration() - dicModel.getEndTime() < minSpaceToInsertMilli)) {
            blnResult = true;
        }

        int nextOrPrevSubtitleIndex = isPrev ? subtitleIndex - 1 : subtitleIndex + 1;
        if (nextOrPrevSubtitleIndex < 0) {
            if (dicModel.getStartTime() < minSpaceToInsertMilli) {
                blnResult = true;
            }
        } else if (nextOrPrevSubtitleIndex >= subtitleList.size()) {
            if (exoPlayer.getDuration() - dicModel.getEndTime() < minSpaceToInsertMilli) {
                blnResult = true;
            }
        } else {
            final DicModel dicModelNextOrPrev = getDicModel(nextOrPrevSubtitleIndex);
            if (isPrev) {
                if (dicModel.getStartTime() - dicModelNextOrPrev.getEndTime() < minSpaceToInsertMilli) {
                    blnResult = true;
                }
            } else {
                if (dicModelNextOrPrev.getStartTime() - dicModel.getEndTime() < minSpaceToInsertMilli) {
                    blnResult = true;
                }
            }
        }

        return blnResult;
    }

    //    //Dalnim add
    protected void onInsertABRepeat() {
        final String studyLanguageContent = binding.etStudyLanguageContent.getText().toString();
        final String tongueLanguageContent = binding.etTongueLanguageContent.getText().toString();
        super.onInsertABRepeat(studyLanguageContent, tongueLanguageContent);
        binding.svMain.fullScroll(View.FOCUS_UP);
        binding.btnAddSubtitle.setVisibility(View.INVISIBLE);
        setAnalyzeAgain();
    }
//    //Dalnim add
//    private void onInsertABRepeat() {
////        DicModel dicModel = subtitleList.get(subtitleIndex);
//        long startTimeNew = (long) minSub;
//        long endTimeNew = (long) maxSub;
//
//        if (startTimeNew < 0)
//            startTimeNew = 0;
//
//        if (endTimeNew > exoPlayer.getDuration())
//            endTimeNew = exoPlayer.getDuration();
//
//        final String studyLanguageContent = viewBinding.etStudyLanguageContent.getText().toString();
//        final String tongueLanguageContent = viewBinding.etTongueLanguageContent.getText().toString();
//
//        Gson gson = new Gson();
//        DicModel newDicModel = new DicModel();// gson.fromJson(gson.toJson(dicModel), DicModel.class);
//        setNewIDForNewDicModel(newDicModel);
//        newDicModel.setVocaDisplay(studyLanguageContent);
//        newDicModel.setVocaDisplayRuby(studyLanguageContent);
//        newDicModel.setSubtitleOriginal(studyLanguageContent);
//        newDicModel.setMeaning(tongueLanguageContent);
//        newDicModel.setStartTime(startTimeNew);
//        newDicModel.setStartTimeOriginal(startTimeNew);
//        newDicModel.setEndTime(endTimeNew);
//        newDicModel.setEndTimeOriginal(endTimeNew);
//        newDicModel.setMemo("");
//
//        activity.getSubDatabase().addSubtitle(newDicModel);
//        subtitleIndex++;
//        subtitleIndex = Voca.getDefaultIndexIfOutOfIndex(subtitleList, subtitleIndex);
//        subtitleList.add(subtitleIndex, newDicModel);
////        Collections.sort(subtitleList, (p1, p2) -> (int) (p1.getStartTime() - p2.getStartTime()));
//
//
//        updateCountSubtitle();
//        viewBinding.svMain.fullScroll(View.FOCUS_UP);
//        ToastUtil.getInstance(activity).show(R.string.msg_inserted);
//        viewBinding.btnAddSubtitle.setVisibility(View.INVISIBLE);
//        setAnalyzeAgain();
//    }

    //Dalnim add
    private void onInsert2(boolean isPrev) {
        if (isNotAbleToInsert(isPrev)) {
            ToastUtil.getInstance(activity).show(R.string.msg_not_enough_time_room_to_insert);
            return;
        }
        long startTimeNew = (long) minSub;
        long endTimeNew = (long) maxSub;

        if (startTimeNew < 0)
            startTimeNew = 0;

        if (endTimeNew > exoPlayer.getDuration())
            endTimeNew = exoPlayer.getDuration();

        final String studyLanguageContent = binding.etStudyLanguageContent.getText().toString();
        final String tongueLanguageContent = binding.etTongueLanguageContent.getText().toString();

        DicModel newDicModel = new DicModel();// gson.fromJson(gson.toJson(dicModel), DicModel.class);
//
//        DicModel dicModel = subtitleList.get(subtitleIndex);
//        long startTimeNew = 0;
//        long endTimeNew = 0;
//        if (isPrev) {
////            subtitleIndex--;
//            startTimeNew = dicModel.getStartTime() - minSpaceToInsertMilli;
//            endTimeNew = dicModel.getStartTime();
//            if (startTimeNew < 0)
//                startTimeNew = 0;
//        } else {
//            subtitleIndex++;
//            startTimeNew = dicModel.getEndTime();
//            endTimeNew = dicModel.getEndTime() + minSpaceToInsertMilli;
//            if (endTimeNew > exoPlayer.getDuration())
//                endTimeNew = exoPlayer.getDuration();
//        }
//
//        Gson gson = new Gson();
//        DicModel newDicModel = gson.fromJson(gson.toJson(dicModel), DicModel.class);
        setNewIDForNewDicModel(newDicModel);
        newDicModel.setVocaDisplay("");
        newDicModel.setVocaDisplayRuby("");
        newDicModel.setMeaning("");
        newDicModel.setStartTime(startTimeNew);
        newDicModel.setStartTimeOriginal(startTimeNew);
        newDicModel.setEndTime(endTimeNew);
        newDicModel.setEndTimeOriginal(endTimeNew);

        activity.getSubDatabase().addSubtitle(newDicModel);
        subtitleList.add(subtitleIndex, newDicModel);
//        Collections.sort(subtitleList, (p1, p2) -> (int) (p1.getStartTime() - p2.getStartTime()));

        subtitleIndex = Voca.getDefaultIndexIfOutOfIndex(subtitleList, subtitleIndex);
        updateCountSubtitle();
        binding.svMain.fullScroll(View.FOCUS_UP);
        ToastUtil.getInstance(activity).show(R.string.msg_inserted);
    }

    //Dalnim add
    private void onInsert(boolean isPrev) {
        if (isNotAbleToInsert(isPrev)) {
            ToastUtil.getInstance(activity).show(R.string.msg_not_enough_time_room_to_insert);
            return;
        }
        DicModel dicModel = getDicModel(subtitleIndex);
        long startTimeNew = 0;
        long endTimeNew = 0;
        if (isPrev) {
//            subtitleIndex--;
            startTimeNew = dicModel.getStartTime() - minSpaceToInsertMilli;
            endTimeNew = dicModel.getStartTime();
            if (startTimeNew < 0)
                startTimeNew = 0;
        } else {
            subtitleIndex++;
            startTimeNew = dicModel.getEndTime();
            endTimeNew = dicModel.getEndTime() + minSpaceToInsertMilli;
            if (endTimeNew > exoPlayer.getDuration())
                endTimeNew = exoPlayer.getDuration();
        }

        Gson gson = new Gson();
        DicModel newDicModel = gson.fromJson(gson.toJson(dicModel), DicModel.class);
        setNewIDForNewDicModel(newDicModel);
        newDicModel.setVocaDisplay("");
        newDicModel.setVocaDisplayRuby("");
        newDicModel.setMeaning("");
        newDicModel.setStartTime(startTimeNew);
        newDicModel.setStartTimeOriginal(startTimeNew);
        newDicModel.setEndTime(endTimeNew);
        newDicModel.setEndTimeOriginal(endTimeNew);

        activity.getSubDatabase().addSubtitle(newDicModel);
        subtitleList.add(subtitleIndex, newDicModel);
//        Collections.sort(subtitleList, (p1, p2) -> (int) (p1.getStartTime() - p2.getStartTime()));

        subtitleIndex = Voca.getDefaultIndexIfOutOfIndex(subtitleList, subtitleIndex);
        updateCountSubtitle();
        binding.svMain.fullScroll(View.FOCUS_UP);
        ToastUtil.getInstance(activity).show(R.string.msg_inserted);
    }

    @Override
    protected LinearLayout getRoot() {
        return binding.root;
    }

    @Override
    protected View getLLPlayer() {
        return binding.llPlayer;
    }

    @Override
    protected StyledPlayerView getPlayerView() {
        return binding.playerView;
    }

    @Override
    protected View getLLCenter() {
        return binding.llCenter;
    }

    @Override
    protected ImageView getIvCenterPlay() {
        return binding.ivCenterPlay;
    }

    @Override
    protected TextView getTvCenterText() {
        return binding.tvCenterText;
    }

    @Override
    protected View getLLRepeat() {
        return binding.llRepeat;
    }

    @Override
    protected Button getBtnCCRepeatReset() {
        return binding.btnCCRepeatReset;
    }

    @Override
    protected RangeSeekBar getSbRepeatRange() {
        return binding.sbRepeatRange;
    }

    @Override
    @Nullable
    protected LinearLayout getLLRepeatRange() {
        return null;
    }

    @Override
    protected TextView getTvRepeatMin() {
        return binding.tvRepeatMin;
    }

    @Override
    protected TextView getTvRepeatMax() {
        return binding.tvRepeatMax;
    }

    @Override
    protected View getLLPlay() {
        return binding.llPlay;
    }

    @Override
    protected RangeSeekBar getSbPlay() {
        return binding.sbPlay;
    }

    @Override
    protected TextView getTvVideoStartTime() {
        return binding.tvVideoStartTime;
    }

    @Override
    protected TextView getTvVideoEndTime() {
        return binding.tvVideoEndTime;
    }

    @Override
    protected FragmentPlayerEditSubtitleBinding inflateViewBinding(LayoutInflater inflater, ViewGroup container) {
        return FragmentPlayerEditSubtitleBinding.inflate(inflater, container, false);
    }

    @Override
    protected void onPlayerVideoPositionListener(long position) {
        updateSeekBarPlay(position);
        repeatSubtitle(position);
    }

    @Override
    protected void onPlayerVideoAutoSaveTime(DicModel data) {
        listId.put(data.getId(), data.getId());
    }

    @Override
    protected void onPlayerVideoPlayRangeChanged(float leftValue) {
        //Dalnim Tried to do this, but it's freezing when I use this code.
//        subtitleIndex = getSubtitleIndexFromCurrentTime1((long)leftValue, subtitleList);
        updateCountSubtitle();
        for (int i = 0; i < subtitleList.size(); i++) {
            if (subtitleList.get(i).checkTime((long) leftValue, (long) leftValue, getDicModel(i + 1))) {
                if (subtitleIndex != i) {
                    subtitleIndex = i;
                    updateCountSubtitle();
                }
                break;
            }
        }
        refreshVisibilityUI();
    }

    private void udpateStartEndTimeDetailed(long startTime, long endTime) {
        binding.tvVideoStartTimeDetail.setText(TimeUtil.getVideoTimeDisplay_detailed(startTime));
        binding.tvVideoEndTimeDetail.setText(TimeUtil.getVideoTimeDisplay_detailed(endTime));
    }

    @Override
    protected void onPlayerVideoPlayRangeChangedStopTracking() {

    }

    @Override
    protected void onPlayerVideoUpdateRangeSeekProgress() {

    }

    @Override
    protected void onPlayerVideoPlayClick() {

    }

    @Override
    protected void onPlayerVideoPlayerStateChanged(boolean playWhenReady, int playbackState) {
        if (playbackState == ExoPlayer.STATE_ENDED) {
            if (isABRepeatMode_A_Button_Clicked()) {
                beginABRepeatWhenItReachEndOfVideo();
            } else {
                startOverWhenItReachEndOfMedia();
            }
        }
    }

    @Override
    protected void onPlayerVideoPositionDiscontinuity(int reason) {

    }

    @Override
    protected void onPlayerVideoRepeatSubtitle(long position) {
        updateRangeSeek();
        hideABRepeatButtonIfSubtitleExist();
        if (isRepeat) {
//            seekToInPlayer_MinSubWithDelayTime_IfSubtitleEndTimeExceedDelaySubtitle(position);

            if ((position + Constant.PLAYER.VIDEO_POSITION_BONUS) >= maxSub) {
                seekToInPlayer_MinSubWithDelayTime();
    //            exoPlayer.seekTo((long) minSub);
            }
        }
//        refreshSubtitleUnlessSameSubtitle(position);
//        updateCountSubtitle();
    }

    private void refreshSubtitleUnlessSameSubtitle(long position) {
        if (Utils.isEmpty(subtitleList))
            return;

        int newSubtitleIndex = getSubtitleIndexFromCurrentTime();
        if (subtitleIndex == newSubtitleIndex) {
            DLog.d("showSubtitle111", "don't update subtitle because they are same contents and same subtitleIndex : " + subtitleIndex);
            DLog.d("showSubtitle111", "subtitleContent" + subtitleContent);
            return;
        }
        subtitleIndex = newSubtitleIndex;
        DicModel dicModel = getDicModel(subtitleIndex);
        getMinMaxTime(dicModel);
    }
    private void hideABRepeatButtonIfSubtitleExist() {
        if (mIsOnEmptyDialog) {
            setVisibleIvABRepeat(View.VISIBLE);
        } else {
            setVisibleIvABRepeat(View.INVISIBLE);
        }
    }

    private ActionMode.Callback onWebDictionaryListener = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            activity.getMenuInflater().inflate(R.menu.menu_edit_subtitle_text_selection, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.menu_item_edit_subtitle_web_dictinary:
                    openWebDictionaryScreen(getSelectedText());
                    return true;
                default:
                    break;
            }
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {

        }
    };

    /**
     * Returns the selected text
     *
     * @return String selectedText
     */
    private String getSelectedText() {
        String selectedText = Constant.BASE_BLANK;
        final EditText et = getEditTextFocused();
        if (et.isFocused()) {
            final int textStartsubtitleIndex = et.getSelectionStart();
            final int textEndsubtitleIndex = et.getSelectionEnd();
            int min = Math.max(0, Math.min(textStartsubtitleIndex, textEndsubtitleIndex));
            int max = Math.max(0, Math.max(textStartsubtitleIndex, textEndsubtitleIndex));
            selectedText = et.getText().subSequence(min, max).toString().trim();
        }
        return selectedText;
    }

    private EditText getEditTextFocused() {
        return binding.etStudyLanguageContent.isFocused() ? binding.etStudyLanguageContent : binding.etTongueLanguageContent;
    }

    private void openWebDictionaryScreen(String word) {
        if (Utils.isEmpty(word)) return;
        Intent intent = new Intent(activity, WebDictionaryContentActivity.class);
        intent.putExtra(Constant.PLAYER.INTENT.KEY_WORD, word);
        startActivity(intent);
    }

    private void initListenerAll() {
        initTextListenerStudyLanguageContent();
        initTextListenerTongueLanguageContent();
        getLLPlayer().setOnTouchListener(this);
    }

    //Dalnim add
    private void initTextListenerStudyLanguageContent() {
        TextWatcher tw = new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
                studyLanguageContent = binding.etStudyLanguageContent.getText().toString();
                studyLanguageContent = s.toString().trim();
                makeSaveButtonVisisbleWhenDialogChanged();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                // TODO Auto-generated method stub

            }
        };

        binding.etStudyLanguageContent.addTextChangedListener(tw);
    }

    //Dalnim add
    private void initTextListenerTongueLanguageContent() {
        binding.etTongueLanguageContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                tongueLanguageContent = s.toString().trim();
                makeSaveButtonVisisbleWhenDialogChanged();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                // TODO Auto-generated method stub

            }
        });
    }

    //Dalnim add
    private void makeSaveButtonVisisbleWhenDialogChanged() {
        DicModel dicModel = getDicModel(subtitleIndex);
        if (dicModel == null)
            return;

        if ((!dicModel.getVocaDisplay().equals(binding.etStudyLanguageContent.getText().toString()))
                || (!dicModel.getMeaning().equals(binding.etTongueLanguageContent.getText().toString()))) {
            binding.btnSave.setVisibility(View.VISIBLE);
        } else {
            binding.btnSave.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected void enterABRepeatModeAB_A() {
        super.enterABRepeatModeAB_A();
        setDefaultMaxSub();
        setVisibleBtnExitCCRepeat(View.INVISIBLE);
    }

    @Override
    protected void enterABRepeatModeAB_B(boolean isEndOfDuration) {
        super.enterABRepeatModeAB_B(isEndOfDuration);
        if (Utils.isEmpty(studyLanguageContent) && Utils.isEmpty(tongueLanguageContent)) {
            setVisibleBtnExitCCRepeat(View.INVISIBLE);
        } else {
            setVisiblebtnAddSubtitle(View.VISIBLE);
        }
    }

    @Override
    protected void exitABRepeatMode() {
        super.exitABRepeatMode();
        setDefaultMinMaxSub();
        setVisibleBtnExitCCRepeat(View.INVISIBLE);
    }

    @Override
    protected void setABRepeatImage(@DrawableRes int resId) {
        binding.ivABRepeat.setImageResource(resId);
    }

    private void setVisiblebtnAddSubtitle(int visibility) {
        binding.btnAddSubtitle.setVisibility(visibility);
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        super.onTouch(view, event);
        return true;
    }

    @Override
    protected boolean isMenuUIVisible() {
        if ((getLLRepeat().getVisibility() == View.VISIBLE)) {
            return true;
        }
        return false;
    }

    @Override
    protected void handleDoubleClickOnPlayingScreen() {
        int visibility = getLLRepeat().getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE;
        ViewUtil.setViewListVisibility(visibility, getLLRepeat(), getLLPlay());
    }

    @Override
    protected void handleClickOnPlayingScreen() {
        getIvCenterPlay().performClick();
    }

    @Override
    protected void handleSwipeToMovePrevNextDialog(boolean isLeft) {
        if (isLeft) {
            onPrevSubtitles();
        } else {
            onNextSubtitles();
        }

    }

    private void setVisibleIvABRepeat(int visibility) {
        binding.ivABRepeat.setVisibility(visibility);
    }

    private void setVisibleBtnExitCCRepeat(int visibility) {
        binding.btnExitCCRepeat.setVisibility(visibility);
    }
}
