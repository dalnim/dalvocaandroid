package com.dalread.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.UtteranceProgressListener;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.dalread.DalFlavor;
import com.dalread.R;
import com.dalread.adapter.ConvVocaListAdapter;
import com.dalread.asyntask.CustomAsyncTask;
import com.dalread.base.EnumLanguage;
import com.dalread.component.SeparatorDecoration;
import com.dalread.composition.VoiceRecording;
import com.dalread.databinding.ActivityConvVocaListBinding;
import com.dalread.dialog.ConvCommonMenuDialog;
import com.dalread.dialog.WordListPlayerPlayAllWordsDialog;
import com.dalread.helper.VoiceFileDownloadHelper;
import com.dalread.interfaces.IVocaBasicItem;
import com.dalread.interfaces.IVocaFullItem;
import com.dalread.interfaces.IVocaFullPlayTTSItem;
import com.dalread.listener.OnAsyncTaskListenerWithType;
import com.dalread.listener.OnClickListener;
import com.dalread.listener.OnDoubleClickListener;
import com.dalread.model.EditVoca;
import com.dalread.model.VocaKnowGroupSelect;
import com.dalread.model.VocaTypeId;
import com.dalread.model.WordListType;
import com.dalread.network.events.SuccessEvent;
import com.dalread.util.BaseVoca;
import com.dalread.util.BaseVocaList;
import com.dalread.util.Constant;
import com.dalread.util.DLog;
import com.dalread.util.LanguageUtil;
import com.dalread.util.Loading;
import com.dalread.util.PermissionUtils;
import com.dalread.util.ToastUtil;
import com.dalread.util.UserUtil;
import com.dalread.util.Utils;
import com.dalread.util.Voca;
import com.dalread.util.VocaKnow;

import org.greenrobot.eventbus.Subscribe;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import butterknife.BindString;
import me.zhanghai.android.fastscroll.FastScrollerBuilder;

@SuppressLint("NonConstantResourceId")
public class ConvVocaListActivity extends BaseConvActivity implements OnAsyncTaskListenerWithType {
    @BindString(R.string.tpl_wb_known_word_count)
    String tplKnownWordCount;

    private List<IVocaFullPlayTTSItem> vocaList;
    protected List<IVocaFullPlayTTSItem> grade1 = new ArrayList<>();
    protected List<IVocaFullPlayTTSItem> grade2 = new ArrayList<>();
    protected List<IVocaFullPlayTTSItem> known = new ArrayList<>();
    protected List<IVocaFullPlayTTSItem> unknown = new ArrayList<>();
    protected List<IVocaFullPlayTTSItem> notRated = new ArrayList<>();

    private ConvVocaListAdapter adapter;
    private boolean show4Buttons;
    private String vocaTypeListWithComma;
    private String vocaIDListWithComma;

    private final int TYPE_INIT_DATA = 0;
    private final int TYPE_GENERATE_DATA_FOR_PLAY_LIST = TYPE_INIT_DATA + 1;
    private final int TYPE_DISPLAY_VOICE_RECORDED_IN_UI = TYPE_GENERATE_DATA_FOR_PLAY_LIST + 1;
    private ActivityConvVocaListBinding binding;
    private int bookId = -1;
    private VoiceRecording voiceRecording;
    private WordListType wordListType;
    protected WordListPlayerPlayAllWordsDialog wordListPlayerPlayAllWordsDialog;

    public static Intent createIntentByBookId(Context context, int bookId) {
        Intent intent = new Intent(context, ConvVocaListActivity.class);
        intent.putExtra(Constant.BUNDLE.KEY_VOCA_TYPE, Constant.API_VALUE.VALUE_VOCA_TYPE_SENTENCE);
        intent.putExtra(Constant.BUNDLE.KEY_WORDLIST_TYPE, WordListType.BOOK);
        intent.putExtra(Constant.BUNDLE.KEY_VOCA_BOOK_ID, bookId);
//        intent.putExtra(Constant.BUNDLE.KEY_WORDLIST_TYPE, WordListType.VOCA_TYPE_ID_LIST);
        return intent;
    }

    public static Intent createIntentByBookmark(Context context) {
        Intent intent = new Intent(context, ConvVocaListActivity.class);
        intent.putExtra(Constant.BUNDLE.KEY_WORDLIST_TYPE, WordListType.BOOKMARK);
        return intent;
    }

    protected View getContentView() {
        binding = ActivityConvVocaListBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        return view;
    }

    @Override
    protected int getContentViewId() {
        return 0;//R.layout.activity_hanja_word_list;
    }


    @Override
    public void onHeaderTextRightClick() {
        openMenuDialog();
    }

    private void openMenuDialog() {
        final ConvCommonMenuDialog dialog = new ConvCommonMenuDialog(this, new OnClickListener() {
            @Override
            public void onClick(View view, Object object) {
                switch (view.getId()) {
                    case R.id.llShow4Buttons:
                        refresh4Buttons();
                        break;
                    case R.id.llOpenSetting:
                        DalFlavor.openSettings(ConvVocaListActivity.this);
                        break;
//                    case R.id.tvAllToKnown:
//                        getVocaTypeAndID();
//                        changeMultipleVocaKnow(vocaTypeListWithComma, vocaIDListWithComma, Constant.VOCA_KNOW.VOCA_KNOW_KNOWN);
//                        break;
//                    case R.id.tvAllToUnknown:
//                        getVocaTypeAndID();
//                        changeMultipleVocaKnow(vocaTypeListWithComma, vocaIDListWithComma, Constant.VOCA_KNOW.VOCA_KNOW_UNKNOWN);
//                        break;
                    case R.id.llBackToHome:
                        backToHome();
                        break;
                }
            }
        });

        dialog.setShow4Buttons(show4Buttons);
        dialog.show();
    }

//    @Override
//    protected RecyclerView.Adapter<RecyclerView.ViewHolder> getAdapter() {
//        return hanjaWordlistAdapter;
//    }

    @Subscribe
    public void onEvent(SuccessEvent successEvent) {
        switch (successEvent.getScreen()) {
            case COMMON_VOCA_DATA:
                IVocaFullPlayTTSItem item= (IVocaFullPlayTTSItem) successEvent.getModel();
                switch (successEvent.getEventType()) {
                    case COMMON_VOCA_KNOW_CHANGED:
                    case COMMON_VOCA_KNOWPRONOUNCE_CHANGED:
                    case COMMON_VOCA_BOOKMKARK_CHANGED:
                        refreshSearchViewAdapterDataChanged(item);
                        break;
                }
                break;
            case EDIT_VOCA:
                EditVoca editVoca = (EditVoca) successEvent.getModel();
                switch (successEvent.getEventType()) {
                    case DATA_CHANGED:
                        IVocaBasicItem voca = editVoca.getiVocaFullItem();
                        refreshSearchViewAdapterDataChanged(voca);
                        subDatabase.updateVoca(voca);
                        break;
                }
                break;
        }
    }

    private void refreshSearchViewAdapterDataChanged(IVocaBasicItem voca) {
        if (Utils.isEmpty(vocaList))
            return;

        for (IVocaBasicItem item : vocaList) {
            if (Voca.isSameVoca(item, voca)) {
                int newBookmark = voca.getVIBookmark();
                int newVocaKnow = voca.getVIVocaKnow();
                int newVocaKnowPronounce = VocaKnow.getVocaKnowPronounceByVocaKnow(newVocaKnow);
                item.setVIBookmark(newBookmark);
                item.setVIVocaKnow(newVocaKnow);
                item.setVIVocaKnowPronounce(newVocaKnowPronounce);
                item.setVIPronounce(voca.getVIPronounce());
                item.setVIMeaning(LanguageUtil.getMotherTongueLanguage(this), voca.getVIMeaning(LanguageUtil.getMotherTongueLanguage(this)));
                item.setVIMeaningDetailed(LanguageUtil.getMotherTongueLanguage(this), voca.getVIMeaningDetailed(LanguageUtil.getMotherTongueLanguage(this)));
                break;
            }
        }
        bindData(vocaList);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle();
        initMediaPlayer();
        initPlayVocaHelper();
        //TODO : check this.
        addMobileAdsView();
        loadBanner();
        callAsyncTask(TYPE_INIT_DATA, null);

    }

    private void setTitle() {
        if (toolbar != null) {
            String title = "";
            if (wordListType.equals(WordListType.BOOK)) {
                if (bookId > 0) {
                    title = subDatabase.getBookTitle(bookId, EnumLanguage.getMotherTongueLanguage(this));

                }
            } else if (wordListType.equals(WordListType.BOOKMARK)) {
                title = getString(R.string.bookmark_list);
            }
            toolbar.setTitle(title);
        }
    }
    private void initMediaPlayer() {
        voiceRecording = new VoiceRecording(this);
    }
    private void initAdapter() {
        adapter = new ConvVocaListAdapter(this, vocaKnowActivity.onDoubleClickListenerOnBaseVocaKnow, onDoubleClickListener);
    }

    private void initPlayVocaHelper() {
        if (!playTTS.playTTSHelper.hasMotherTongueListener()) {
            playTTS.playTTSHelper.setMotherTongueListener(new UtteranceProgressListener() {

                @Override
                public void onStart(String utteranceId) {
//                    updateItemStatus(utteranceId, true);
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
//                    updateItemStatus(utteranceId, false);
                }

                @Override
                public void onError(String utteranceId) {
//                    updateItemStatus(utteranceId, false);
                }
            });
        }
    }

    private OnDoubleClickListener onDoubleClickListener = new OnDoubleClickListener() {
        @Override
        public void onClick(View view, Object data) {
            IVocaFullPlayTTSItem voca = (IVocaFullPlayTTSItem) data;
            switch (view.getId()) {
                case R.id.ivSpeaker:
                    stopRecordingAndResetMicIcon();
                    setRecordFile(voca);
//                    voiceRecording.onClickSpeaker();
//                    initPlayVocaHelper();// TODO : I call this just in case the listner is null. Need to update not the listner to null
                    playTTS.playTTSHelper.isPlayTTSAtFirst = false;
                    if (playTTS.canPlay()) {
                        updateSpeakerUIStatus(voca, true);
                        playTTS.playSingle(voca);
//                        Voca.resetIsPlayingTTS(vocaList);
                    } else if (playTTS.isPlayStatePlaySingle()) {
                        boolean isPlaying = voca.isVIPlaying();
                        playTTS.stopPlayVoca();
                        updateSpeakerUIStatus(voca, false);
                        if (!isPlaying) {
                            updateSpeakerUIStatus(voca, true);
                            playTTS.playSingle(voca);
                        }
                    }
                    break;
                case R.id.ivMic:
                    stopPlayingAdResetSpeakerIcon();
                    boolean wasRecording = voca.isVIRecording();
                    setRecordFile(voca);
                    voiceRecording.onClickMic();
                    if (voiceRecording.recording) {
                        updateMicUIStatus(voca, true);
                    } else {
                        updateMicUIStatus(voca, false);
                    }

                    if (wasRecording) {
                        ToastUtil.getInstance(ConvVocaListActivity.this).show(R.string.toast_listen_my_voice_recording);
                        updateAfterRecording(voca);
                        playTTS.stopPlaylist();
                        if (playTTS.canPlay()) {
                            playTTS.playMyVoiceOnce(voca);
                        }

                    }
                    break;
                case R.id.icPlayAll:
                    openWordListPlayerPlayAllWordsDialog();
//                    handlePlayAll(null);
                    break;
                case R.id.ivShowRecordedVoice:
                    callAsyncTask(TYPE_DISPLAY_VOICE_RECORDED_IN_UI, null);
                    break;
                case R.id.ivEditView:
                    openEditMeaningScreen(voca);
                    break;
            }
        }

        @Override
        public void onDoubleClick(View view, Object data) {

        }
    };

    private void updateRealmDbAfterRecording(IVocaFullPlayTTSItem voca) {
        String voiceFileName = BaseVoca.getMyOutputRecordingFileName(this, voca);
        VoiceFileDownloadHelper downloadFileHelper = new VoiceFileDownloadHelper(getBaseContext(), true, null);
        downloadFileHelper.getServerVoiceFileInfo(voiceFileName, BaseVoca.onVoiceFileInfoDownloadListener);
    }

    //TODO : Don't get dicModel from database here, get it before calling this method and send it as a parameter.
    public void openEditMeaningScreen(IVocaFullItem voca) {
        if (UserUtil.isLoggedIn(this, true)) {
            try {
                Intent intent = new Intent(this, EditMeaningActivity.class);
                intent.putExtra(Constant.BUNDLE.KEY_VOCA, voca);
                openNewScreen(intent);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public void setRecordFile(IVocaFullPlayTTSItem voca) {
        if (voca instanceof DIC_SENTENCE_MODEL) {
            ((DIC_SENTENCE_MODEL)voca).setPath(Voca.getMyOutputRecordingFileName(context, voca));
        }

        voiceRecording.recordFile = Voca.getVoiceFileInApp(this, voca.getVIPath());
//        voiceRecording.recordFile = Voca.getVoiceFileOnLocal(Voca.getVoiceFolderInApp(this), voca.getVIPath());
    }

    private void stopRecordingAndResetMicIcon() {
        if (voiceRecording.recording) {
            voiceRecording.stopRecording();
            Voca.resetIsRecordingVoca(vocaList);
            adapter.notifyDataSetChanged();
        }
    }

    private void stopPlayingAdResetSpeakerIcon() {
        if (playTTS.isPlaying()) {
            playTTS.stopPlayVoca();
            Voca.resetIsPlayingTTS(vocaList);
            adapter.notifyDataSetChanged();
        }
    }

    private void updateSpeakerUIStatus(IVocaFullPlayTTSItem voca, boolean playing) {
        if (adapter.getItem(voca) != null) {
            Voca.setIsPlayingTTS(vocaList, voca, playing);
            adapter.notifyDataSetChanged();
        }
    }

    private void updateMicUIStatus(IVocaFullPlayTTSItem voca, boolean recording) {
        if (adapter.getItem(voca) != null) {
            Voca.setIsRecordingTTS(vocaList, voca, recording);
            if (recording)
                adapter.setIsRecordingVoca(voca);
            else
                adapter.setIsRecordingVoca(null);
//            adapter.notifyDataSetChanged();
        }
    }

    private void updateAfterRecording(IVocaFullPlayTTSItem voca) {
        updateModelAfterRecording(voca);
        updateSqlDbAfterRecording(voca);
        updateRealmDbAfterRecording(voca);
        bindVoca(voca);
    }

    private void updateModelAfterRecording(IVocaFullPlayTTSItem voca) {
        voca.setVIVoiceFile(Constant.INT_BOOLEAN.TRUE);
    }
    private void updateSqlDbAfterRecording(IVocaFullPlayTTSItem voca) {
        subDatabase.setHasVoiceFile(voca, Constant.INT_BOOLEAN.TRUE);
    }

    protected void initData() {
        super.initData();
        show4Buttons = false;
        initAdapter();
        binding.layoutItemContentBookTitle.vItem.setVisibility(View.GONE);
        bookId = getIntent().getIntExtra(Constant.BUNDLE.KEY_VOCA_BOOK_ID, -1);
        show4Buttons = getIntent().getBooleanExtra(Constant.BUNDLE.KEY_SHOW_4_BUTTONS, false);
        wordListType = (WordListType) getIntent().getSerializableExtra(Constant.BUNDLE.KEY_WORDLIST_TYPE);
    }


    protected void initLayout() {
        super.initLayout();
        new FastScrollerBuilder(binding.rvInfo).build();

        binding.rvInfo.setLayoutManager(new LinearLayoutManager(context));
        binding.rvInfo.addItemDecoration(BaseBindUtils.getSeparatorDecoration(context));
        binding.rvInfo.setAdapter(adapter);
    }


    @SuppressWarnings("unchecked")
    @Override
    protected void getData() {

    }

//    @Override
//    protected List<HanjaQuizItem> getQuizHanjaList() {
//        return new ArrayList<>();
//    }



//    protected final DialogInterface.OnClickListener onDialogItemClickListener = (dialog, which) -> {
//        switch (which) {
////            case R.id.tvShow4Buttons:
////                refresh4Buttons();
////                break;
////            case R.id.tvAllToKnown:
////                getVocaTypeAndID();
////                changeMultipleVocaKnow(vocaTypeListWithComma, vocaIDListWithComma, Constant.VOCA_KNOW.VOCA_KNOW_KNOWN);
////                break;
////            case R.id.tvAllToUnknown:
////                getVocaTypeAndID();
////                changeMultipleVocaKnow(vocaTypeListWithComma, vocaIDListWithComma, Constant.VOCA_KNOW.VOCA_KNOW_UNKNOWN);
////                break;
//            case R.id.tvBackToHome:
//                backToHome();
//                break;
//            default:
//                break;
//        }
//    };

    private void bindData(List<IVocaFullPlayTTSItem> iVocaFullItemList) {
        adapter.setData(iVocaFullItemList);
        adapter.setShow4Buttons(show4Buttons);
        adapter.notifyDataSetChanged();
    }

    private void bindVoca(IVocaFullPlayTTSItem voca) {
        adapter.notifyItemChanged(voca);
    }


    private void refresh4Buttons() {
        show4Buttons = !show4Buttons;
        adapter.setShow4Buttons(show4Buttons);
        adapter.notifyDataSetChanged();
//        hanjaWordListDialog.update4ButtonName(show4Buttons);
    }

//    private void getVocaTypeAndID() {
//        StringJoiner sjVocaType = new StringJoiner(",");
//        StringJoiner sjVocaID = new StringJoiner(",");
//        for(HanjaItem hanjaItem : vocaList) {
//            sjVocaType.add(String.valueOf(hanjaItem.getHI_VOCA_TYPE()));
//            sjVocaID.add(String.valueOf(hanjaItem.getHI_ID()));
//        }
//
//        vocaTypeListWithComma = sjVocaType.toString();
//        vocaIDListWithComma = sjVocaID.toString();
//    }

    private void callAsyncTask(int type, Object data) {
        new CustomAsyncTask(this, this, data, type, true).execute();
    }

    @Override
    public void onInitAsyncTask(int searchType) {
        switch (searchType) {
            case TYPE_DISPLAY_VOICE_RECORDED_IN_UI:
                ToastUtil.getInstance(getBaseContext()).show(R.string.toast_set_mic_black_my_recorded_voice_item);
//                Loading.show(this, "내가 녹음한 항목의 마이크와 스피커는 검게 표시해줍니다.");
                break;
            default:
                Loading.showDelay(this);
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
                if (wordListType.equals(WordListType.BOOK)) {
                    if (bookId > 0) {
                        vocaList = subDatabase.getVocaListByBookId(bookId);
                    }
                } else if (wordListType.equals(WordListType.BOOKMARK)) {
                    vocaList = subDatabase.getVocaBookmarkList();
                }
                Voca.resetVocaList(vocaList);
                return vocaList;
            case TYPE_GENERATE_DATA_FOR_PLAY_LIST:
                return BaseVocaList.getWordListByWordListGroup((VocaKnowGroupSelect) data, vocaList);
            case TYPE_DISPLAY_VOICE_RECORDED_IN_UI:
                int cntOfVoiceFiles = Voca.udpateVoiceFileInVocaList(getBaseContext(), vocaList);
                if (cntOfVoiceFiles == 0) {
                    ToastUtil.getInstance(getBaseContext()).show(R.string.toast_no_my_recorded_voice_file);
                }
                return vocaList;
        }
        return null;
    }

    @Override
    public void onFinishAsyncTask(int searchType, Object resultData, Object data) {
        switch (searchType) {
            case TYPE_INIT_DATA:
            case TYPE_DISPLAY_VOICE_RECORDED_IN_UI:
                vocaList = (List<IVocaFullPlayTTSItem>)resultData;
                bindData(vocaList);
                break;
            case TYPE_GENERATE_DATA_FOR_PLAY_LIST:
                openPlaylistActivity((List<IVocaFullPlayTTSItem>) resultData);
                break;
        }
        Loading.hide();
    }

    @Override
    public void onHeaderLeftClick() {
        onBackPressed();
    }

    private void openWordListPlayerPlayAllWordsDialog() {
        if (wordListPlayerPlayAllWordsDialog == null) {
            wordListPlayerPlayAllWordsDialog = new WordListPlayerPlayAllWordsDialog(this, onWordListPlayerPlayAllWordsDialogListener);
        }
        wordListPlayerPlayAllWordsDialog.show();
    }
    protected OnClickListener onWordListPlayerPlayAllWordsDialogListener = (view, object) -> {
        DLog.d(getLogTag(), "onWordListPlayerPlayAllWordsDialogListener");
        switch (view.getId()) {
            case R.id.tvOpenSelectedItemsOnly:
                callAsyncTask(TYPE_GENERATE_DATA_FOR_PLAY_LIST, (VocaKnowGroupSelect) object);
                break;
            case R.id.tvOpenBookmarkedItemsOnly:
                openPlaylistActivity(vocaList.stream().filter(e -> Voca.isBookmark(e)).collect(Collectors.toList()));
                break;
            case R.id.tvOpenAllItems:
                openPlaylistActivity(vocaList);
                break;
        }
    };

    private void openPlaylistActivity(List<? extends IVocaFullItem> data) {
        Intent intent = new Intent(this, PlayListByTtsActivity.class);
        List<VocaTypeId> vocaTypeIdList = BaseVocaList.convertToVocaTypeIdList(data);
        intent.putExtra(Constant.BUNDLE.KEY_VOCA_PLAYLIST, (Serializable) vocaTypeIdList);
//        intent.putExtra(Constant.BUNDLE.KEY_VOCA_KNOW_ACTIVITY, (Serializable) vocaKnowActivity);
        playTTS.stopPlayVoca();
        playTTS.setIncludeMeaning();
        openNewScreen(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        voiceRecording.destroy();
    }

    @Override
    public void onStop() {
        DLog.d(getLogTag(), "onStop");
        super.onStop();
        if (!sharedPreferences.getKeepPlayingOnBackgroundMode()) {
            playTTS.stopPlayVoca();
            Voca.resetVocaList(vocaList);
            bindData(vocaList);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionUtils.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
//        if (requestCode == PermissionUtils.REQUEST_CODE_RECORD_AUDIO) {
//            for (int i = 0, len = permissions.length; i < len; i++) {
//                String permission = permissions[i];
//                if (permission.equalsIgnoreCase(Manifest.permission.RECORD_AUDIO)) {
//                    if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
//                        boolean showRationale = shouldShowRequestPermissionRationale(permission);
//                        if (!showRationale) {
//                            // Deny permission and the application will not ask again,
//                            // show popup to remind user to enable permission
//                            ConfirmationDialog dialog = new ConfirmationDialog(this, R.string.permission_needed_title,
//                                    R.string.record_audio_permission_needed_message, R.string.open_app_details_settings, R.string.cancel,
//                                    new ConfirmationDialog.OnDialogClickListener() {
//                                @Override
//                                public void onPositive(DialogInterface dialog) {
//                                    dialog.dismiss();
//                                    Intent intent = new Intent();
//                                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
//                                    Uri uri = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null);
//                                    intent.setData(uri);
//                                    ConvVocaListActivity.this.startActivity(intent);
//                                }
//
//                                @Override
//                                public void onNegative(DialogInterface dialog) {
//                                    dialog.dismiss();
//                                }
//                            });
//                            dialog.show();
//                        }
//                    }
//                }
//            }
//        }
    }
}
