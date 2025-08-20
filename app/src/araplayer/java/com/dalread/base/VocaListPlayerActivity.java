package com.dalread.base;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.speech.tts.UtteranceProgressListener;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.PopupWindow;

import androidx.appcompat.widget.SearchView;

import com.dalread.R;
import com.dalread.activity.ExportWordListActivity;
import com.dalread.activity.PlayListByTtsActivity;
import com.dalread.adapter.StudyChatAdapter;
import com.dalread.adapter.VocaListPlayerAdapter;
import com.dalread.asyntask.CustomAsyncTask;
import com.dalread.component.CenterLayoutManager;
import com.dalread.component.SeparatorDecoration;
import com.dalread.component.Toolbar;
import com.dalread.database.sqlite.model.DicModel;
import com.dalread.databinding.ActivityWordListPlayerBinding;
import com.dalread.dialog.RecyclerViewDialog;
import com.dalread.dialog.RegisterVocaDialog;
import com.dalread.dialog.WordListPlayerDialog;
import com.dalread.dialog.WordListPlayerPlayAllWordsDialog;
import com.dalread.dialog.ZoomedPhotoDialog;
import com.dalread.interfaces.IVocaBasicItem;
import com.dalread.interfaces.IVocaFullItem;
import com.dalread.interfaces.IVocaFullPlayTTSItem;
import com.dalread.listener.OnAsyncTaskListenerWithType;
import com.dalread.listener.OnClickListener;
import com.dalread.listener.OnDoubleClickListener;
import com.dalread.listener.OnKnowChangeListener;
import com.dalread.model.SubtitleLanguageModel;
import com.dalread.model.VocaKnowGroupSelect;
import com.dalread.model.VocaStudyChat;
import com.dalread.model.VocaStudyChatRuby;
import com.dalread.model.WordListHeaderModel;
import com.dalread.model.WordListType;
import com.dalread.network.DalApiListener;
import com.dalread.network.events.BaseEvent;
import com.dalread.network.events.SuccessEvent;
import com.dalread.util.BaseBindUtils;
import com.dalread.util.BaseVocaList;
import com.dalread.util.Constant;
import com.dalread.util.DLog;
import com.dalread.util.LanguageUtil;
import com.dalread.util.Loading;
import com.dalread.util.SubtitleUtil;
import com.dalread.util.Utils;
import com.dalread.util.VocaKnow;

import org.greenrobot.eventbus.Subscribe;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import me.zhanghai.android.fastscroll.FastScrollerBuilder;

//This is parent of Word List(WordListPlayerActivity) and Subtitle list view(DialogueListPlayerActivity)
public class VocaListPlayerActivity extends BasePlayerActivity implements OnAsyncTaskListenerWithType {
    protected ArrayList<Object> totalItems = new ArrayList<>();
    protected ArrayList<Object> items = new ArrayList<>();
    protected ArrayList<Object> grade1 = new ArrayList<>();
    protected ArrayList<Object> grade2 = new ArrayList<>();
    protected ArrayList<Object> known = new ArrayList<>();
    protected ArrayList<Object> unknown = new ArrayList<>();
    protected ArrayList<Object> notRated = new ArrayList<>();
    protected int currentBottomNavigationId;
    protected VocaListPlayerAdapter vocaListPlayerAdapter;
    protected boolean isSearchTitle = true;
    protected boolean isSearchMeaning = false;
    protected String searchValue;
    protected WordListPlayerDialog wordListPlayerDialog;
    protected WordListPlayerPlayAllWordsDialog wordListPlayerPlayAllWordsDialog;
    protected RecyclerViewDialog recyclerViewDialog;
    protected RegisterVocaDialog registerVocaDialog;

    protected final int TYPE_GET_ALL = 0;
    protected final int TYPE_UPDATE_UI_VOCA_INFO = TYPE_GET_ALL + 1;
    protected final int TYPE_SEARCH = TYPE_UPDATE_UI_VOCA_INFO + 1;
    protected final int TYPE_UPDATE_ALL_KNOWN = TYPE_SEARCH + 1;
    protected final int TYPE_SYNC_DATA_BEFORE_EXIT = TYPE_UPDATE_ALL_KNOWN + 1;

    protected String allVocaIds, allVocaTypes;
    protected List<SubtitleLanguageModel> subtitleLanguageModels;
    protected boolean dataChanged;
//    private IVocaFullItem mIVocaFullItemEdited;

    protected ActivityWordListPlayerBinding binding;
    @Override
    protected View getContentView() {
        binding = ActivityWordListPlayerBinding.inflate(getLayoutInflater());
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
        playerFileModel = getIntent().getParcelableExtra(Constant.PLAYER.INTENT.KEY_VIDEO_FILE);
        if (playerFileModel == null) {
            DLog.e(getLogTag(), "playerFileModel is null");
            return;
        }
        initEventBus();
        initToolbar();
        initBottomNavigation();
        initDialog();
        createSubDatabase(playerFileModel);
        initRecycleView();

        binding.nvBottom.setVisibility(View.VISIBLE);

        subtitleLanguageModels = SubtitleUtil.getSubtitleLanguageModels(this, playerFileModel, getSubDatabase());
        initData();
    }

    @Override
    public void initData() {
        callAsyncTask(TYPE_GET_ALL);
    }

    @Override
    protected void onResume() {
        super.onResume();
        playTTS.setIncludeMeaning(false);
        initPlayVocaHelperListener();
        if (dataChanged) {
            initData();
        }
    }

    @Override
    public void onBackPressed() {
//        if (iv_zoomed_photo.getVisibility() == View.VISIBLE) {
//            restoreToPortraitFromShowImage();
//            iv_zoomed_photo.setVisibility(View.GONE);
//            return;
//        }
        callAsyncTask(this, null, TYPE_SYNC_DATA_BEFORE_EXIT, true);
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

    }

    @Subscribe
    public void onEvent(SuccessEvent successEvent) {
        if (successEvent.getScreen() == BaseEvent.Screen.MAIN) {
            BaseEvent.EventType type = successEvent.getEventType();
            if (type == BaseEvent.EventType.DATA_CHANGED) {
                dataChanged = true;
            }
        } else if (successEvent.getScreen() == BaseEvent.Screen.WORD_INFO_FRAGMENT) {
            IVocaFullItem iVocaFullItem = (IVocaFullItem) successEvent.getModel();
            switch (successEvent.getEventType()) {
                case VOCA_KNOW_CHANGED:
                    saveVocaKnowValue(iVocaFullItem);
                    updateUIVocaInfo(iVocaFullItem);
                    break;
                case BOOKMARK_CHANGED:
                    updateBookmark(iVocaFullItem);
                    break;
            }
        }
    }

    protected void callAsyncTask(int type) {
        callAsyncTask(type, null);
    }

    protected void callAsyncTask(int type, Object data) {
        callAsyncTask(type, data, true);
    }

    protected void callAsyncTask(int type, Object data, boolean isLoading) {
        new CustomAsyncTask(this, this, data, type, isLoading).execute();
    }

    protected void generateAllData() {

    }

    protected void initToolbar() {
        View popupView = getLayoutInflater().inflate(R.layout.item_search_vocabook, null);
        CheckBox cbTitle = popupView.findViewById(R.id.cb_title);
        CheckBox cbMeaning = popupView.findViewById(R.id.cb_meaning);
        cbTitle.setOnCheckedChangeListener(onCheckedChangeListener);
        cbMeaning.setOnCheckedChangeListener(onCheckedChangeListener);
        PopupWindow popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        binding.header.setPopupWindow(popupWindow);
        binding.header.setSearchListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                searchValue = s;
                callAsyncTask(TYPE_SEARCH);
                return true;
            }
        }, () -> {
            searchValue = Constant.BASE_BLANK;
            callAsyncTask(TYPE_SEARCH);
            return true;
        });
        binding.header.showSearchView();
    }

    protected CompoundButton.OnCheckedChangeListener onCheckedChangeListener = (buttonView, isChecked) -> {
        int id = buttonView.getId();
        switch (id) {
            case R.id.cb_title:
                if (canCheckValue(isChecked)) {
                    isSearchTitle = isChecked;
                    callAsyncTask(TYPE_SEARCH);
                } else {
                    buttonView.setChecked(true);
                }
                break;
            case R.id.cb_meaning:
                if (canCheckValue(isChecked)) {
                    isSearchMeaning = isChecked;
                    callAsyncTask(TYPE_SEARCH);
                } else {
                    buttonView.setChecked(true);
                }
                break;
        }
    };

    protected boolean canCheckValue(boolean isChecked) {
        DLog.d(getLogTag(), "isChecked=" + isChecked);
        if (isChecked)
            return true;
        DLog.d(getLogTag(), "isSearchTitle=" + isSearchTitle);
        DLog.d(getLogTag(), "isSearchMeaning=" + isSearchMeaning);
        return isSearchTitle && isSearchMeaning;
    }

    protected void initBottomNavigation() {
        binding.nvBottom.setOnNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id != currentBottomNavigationId) {
                currentBottomNavigationId = id;
                callAsyncTask(TYPE_GET_ALL);
                return true;
            }
            return false;
        });
        currentBottomNavigationId = R.id.nav_grade;
    }

    protected void initDialog() {
        recyclerViewDialog = new RecyclerViewDialog(this, (view, object) -> {
            if ((Boolean) object) {
                recyclerViewDialog.setUpdateData(false);
                callAsyncTask(TYPE_GET_ALL, object);
            }
            playTTS.stop();
        });

        registerVocaDialog = new RegisterVocaDialog(this, onKnowChangeListener);
    }

    protected void initRecycleView() {
        new FastScrollerBuilder(binding.rvList).build();
        binding.rvList.setLayoutManager(new CenterLayoutManager(this));
        binding.rvList.addItemDecoration(new SeparatorDecoration(this, BaseBindUtils.getDividerColor(), BaseBindUtils.getDividerHeight(this)));
    }

    protected void resetData() {
        allVocaIds = "";
        allVocaTypes = "";
        items.clear();
        known.clear();
        grade1.clear();
        grade2.clear();
        unknown.clear();
        notRated.clear();
    }
    @SuppressLint("StringFormatInvalid")
    protected void generateData() {

    }

    protected void updateVocaInfoInList(IVocaBasicItem iVocaBasicItem) {
        for (Object obj : items) {
            if (obj instanceof IVocaBasicItem) {
                IVocaBasicItem iVocaBasicItemInItems = (IVocaBasicItem) obj;
                if ((iVocaBasicItemInItems.getVIVocaType().equals(iVocaBasicItem.getVIVocaType())) && (iVocaBasicItemInItems.getVIVocaId().equals(iVocaBasicItem.getVIVocaId()))) {
                    iVocaBasicItemInItems.setVIVocaKnow(iVocaBasicItem.getVIVocaKnow());
                    iVocaBasicItemInItems.setVIVocaKnowPronounce(iVocaBasicItem.getVIVocaKnowPronounce());
                    iVocaBasicItemInItems.setVIBookmark(iVocaBasicItem.getVIBookmark());
                    break;
                }
            }
        }
    }

    protected ArrayList<Object> checkData(ArrayList<Object> totalData) {
        if (Utils.isEmpty(searchValue) || Utils.isEmpty(totalData))
            return totalData;
        final ArrayList<Object> temp = new ArrayList<>();
        final int searchType = getSearchType(searchValue);
        final int searchIn = getValueSearchIn();
        DLog.d("generateData", "searchType=" + searchType + " - searchIn=" + searchIn);
        for (Object obj : totalData) {
            if (obj instanceof DicModel) {
                final DicModel model = (DicModel) obj;
                if (checkSearch(searchType, searchIn, model, searchValue)) {
                    temp.add(model);
                }
            }
        }
        return temp;
    }

    protected boolean checkSearch(int type, int searchIn, IVocaBasicItem model, String searchValue) {
        final String text = searchValue.replace(Constant.SEARCH.KEY_PERCENT, Constant.BASE_BLANK);
        DLog.d("checkSearch", "text=" + text);
        String str1 = Constant.BASE_BLANK;
        String str2 = Constant.BASE_BLANK;
        if (searchIn == Constant.SEARCH.VALUE.TITLE) {
            str1 = model.getVIVoca().toLowerCase();
        } else if (searchIn == Constant.SEARCH.VALUE.MEANING) {
            if (!Utils.isEmpty(model.getVIMeaning(LanguageUtil.getMotherTongueLanguage(this)))) {
                str1 = model.getVIMeaning(LanguageUtil.getMotherTongueLanguage(this)).toLowerCase();
            }
        } else {
            str1 = model.getVIVoca().toLowerCase();
            if (!Utils.isEmpty(model.getVIMeaning(LanguageUtil.getMotherTongueLanguage(this)))) {
                str2 = model.getVIMeaning(LanguageUtil.getMotherTongueLanguage(this)).toLowerCase();
            }
        }

        if (searchIn == Constant.SEARCH.VALUE.ALL) {
            if (type == Constant.SEARCH.TYPE.START) {
                return str1.startsWith(text) || str2.startsWith(text);
            } else if (type == Constant.SEARCH.TYPE.END) {
                return str1.endsWith(text) || str2.endsWith(text);
            }
            return str1.contains(text) || str2.contains(text);
        } else {
            if (type == Constant.SEARCH.TYPE.START) {
                return str1.startsWith(text);
            } else if (type == Constant.SEARCH.TYPE.END) {
                return str1.endsWith(text);
            }
            return str1.contains(text);
        }
    }

    protected int getSearchType(String value) {
        if (value.startsWith(Constant.SEARCH.KEY_PERCENT))
            return Constant.SEARCH.TYPE.START;
        if (value.endsWith(Constant.SEARCH.KEY_PERCENT))
            return Constant.SEARCH.TYPE.END;
        return Constant.SEARCH.TYPE.MATCHED;
    }

    protected int getValueSearchIn() {
        if (isSearchTitle && isSearchMeaning)
            return Constant.SEARCH.VALUE.ALL;
        if (isSearchTitle)
            return Constant.SEARCH.VALUE.TITLE;
        return Constant.SEARCH.VALUE.MEANING;
    }

    @Override
    public void onInitAsyncTask() {

    }

    @Override
    public void onInitAsyncTask(int searchType) {
        if (searchType != TYPE_UPDATE_UI_VOCA_INFO)
            Loading.show(this);
    }

    @Override
    public void onPrevExecuteAsyncTask() {

    }

    @Override
    public Object onPostExecuteAsyncTask(int searchType, Object data) {
        switch (searchType) {
            case TYPE_GET_ALL:
                generateAllData();
                generateData();
                break;
            case TYPE_SEARCH:
                generateData();
                break;
            case TYPE_UPDATE_UI_VOCA_INFO:
                IVocaBasicItem iVocaBasicItem = (IVocaBasicItem) data;
                updateVocaInfoInList(iVocaBasicItem);
                return iVocaBasicItem;
            case TYPE_UPDATE_ALL_KNOWN:
                updateAllVocaKnow(data);
                break;
            case TYPE_SYNC_DATA_BEFORE_EXIT:
                syncVocaKnow(playerFileModel.getVideoModel());
                break;
        }
        return null;
    }

    @Override
    public void onFinishAsyncTask(int searchType, Object resultData, Object data) {
        switch (searchType) {
            case TYPE_GET_ALL:
            case TYPE_SEARCH:
                vocaListPlayerAdapter.setKeyword(searchValue);
                vocaListPlayerAdapter.setData(items);
                if (data == null) {
                    binding.rvList.post(() -> binding.rvList.scrollToPosition(0));
                }
                break;
            case TYPE_UPDATE_UI_VOCA_INFO:
                IVocaBasicItem iVocaBasicItem = (IVocaBasicItem) resultData;
                Integer index = items.indexOf(iVocaBasicItem);
                if (index < 0) {
                    vocaListPlayerAdapter.setData(items);
                } else {
                    vocaListPlayerAdapter.notifyItemChanged(index);
                }
                break;

            case TYPE_UPDATE_ALL_KNOWN:
                binding.rvList.post(() -> vocaListPlayerAdapter.notifyDataSetChanged());
                break;
            case TYPE_SYNC_DATA_BEFORE_EXIT:
                updateVideoModel(playerFileModel.getVideoModel());
                finish();
                break;
        }
        Loading.hide();
    }

    protected OnDoubleClickListener onDoubleClickListener = new OnDoubleClickListener() {
        @Override
        public void onClick(View view, Object object) {
            switch (view.getId()) {
                case R.id.llVocaKnow:
                case R.id.tvVocaKnowValue:
                    registerVocaDialog.show((IVocaBasicItem) object, false);
                    break;
                case R.id.vEntireItem:
                    //Don't call openPhraseInformation yet.
                    //openPhraseInformation((IVocaFullItem) object);
                    break;
            }
        }

        @Override
        public void onDoubleClick(View view, Object object) {
            switch (view.getId()) {
                case R.id.tvSubtitle:
                case R.id.tvSubtitleMeaning:

                default:
                    IVocaBasicItem iVocaBasicItem = (IVocaBasicItem) object;
                    int newVocaKnow = VocaKnow.switchVocaKnow(iVocaBasicItem.getVIVocaKnow());
                    updateVocaKnow(newVocaKnow, iVocaBasicItem);
                    break;
            }
        }
    };

    protected OnClickListener onRecycleClickListener = (view, object) -> {
        switch (view.getId()) {
            case R.id.vForeground:
//            case R.id.tvName:
                handleItemClick((IVocaBasicItem) object);
                break;
            case R.id.icPlayAll:
                handlePlayAll(object);
                break;
            case R.id.icPlay:
                handlePlay((DicModel) object, view);
                break;
            case R.id.btnKnown:
                changeValue((IVocaBasicItem) object, Constant.VOCA_KNOW.VOCA_KNOW_KNOWN);
                break;
            case R.id.btnGrade1:
                changeValue((IVocaBasicItem) object, Constant.VOCA_KNOW.VOCA_KNOW_AMKI_GRADE_1);
                break;
            case R.id.btnGrade2:
                changeValue((IVocaBasicItem) object, Constant.VOCA_KNOW.VOCA_KNOW_AMKI_GRADE_2);
                break;
            case R.id.btnUnknown:
                changeValue((IVocaBasicItem) object, Constant.VOCA_KNOW.VOCA_KNOW_UNKNOWN);
                break;
            case R.id.tvSubtitle:
                Utils.copyToClipboard(VocaListPlayerActivity.this, ((IVocaBasicItem) object).getVIVoca(), R.string.copied);
                break;
            case R.id.tvSubtitleMeaning:
                Utils.copyToClipboard(VocaListPlayerActivity.this, ((IVocaBasicItem) object).getVIMeaning(LanguageUtil.getMotherTongueLanguage(this)), R.string.copied);
                break;
            case R.id.izbVideoThumbnail:
                DicModel dic = (DicModel) object;
                long betweenStartAndEndTimeInVideo = (dic.getStartTime() + dic.getEndTime()) / 2;;
//                rotateToLandscapeToShowImage();
//                iv_zoomed_photo.setVisibility(View.VISIBLE);
//                UtilImage.getThumbnailVideoFile(VocaListPlayerActivity.this, iv_zoomed_photo, playerFileModel.getPath(), betweenStartAndEndTimeInVideo, playerFileModel.getVideoModel().getDuration(), true);
                ZoomedPhotoDialog zoomedPhotoDialog = new ZoomedPhotoDialog(this, playerFileModel);
                zoomedPhotoDialog.loadThumbnailFromTimeAndShow(betweenStartAndEndTimeInVideo, ((ImageView) view).getDrawable());
                break;
        }
    };

    private void updateBookmark(IVocaBasicItem iVocaBasicItem) {
        updateBookmarkValue(iVocaBasicItem);
        updateUIVocaInfo(iVocaBasicItem);
    }

    protected void handleItemClick(IVocaBasicItem model) {
    }


    protected void handlePlayAll(Object object) {
        DLog.d(getLogTag(), "handlePlayAll");
        ArrayList<Object> data = new ArrayList<>();

        if (object instanceof IVocaBasicItem) {
            data.addAll(items);
        } else if (object instanceof WordListHeaderModel) {
            final WordListHeaderModel header = (WordListHeaderModel) object;
            DLog.d(getLogTag(), "header=" + header.toString());
            if (header.getKnow() == Constant.AMKI_GRADE.VALUE_NONE) {
                data.addAll(items);
            } else {
                if (VocaKnow.isKnown(header.getKnow())) {
                    data.addAll(known);
                } else if (VocaKnow.isAmkiGrade1(header.getKnow())) {
                    data.addAll(grade1);
                } else if (VocaKnow.isAmkiGrade2(header.getKnow())) {
                    data.addAll(grade2);
                } else if (VocaKnow.isUnknown(header.getKnow())) {
                    data.addAll(unknown);
                } else {
                    data.addAll(notRated);
                }
            }

            if (data.size() > 0) {
                data.remove(0);
            }
        } else {
            data.addAll(items);
        }
        openPlayListByTtsActivity(data);
    }

    private void openPlayListByTtsActivity(ArrayList<Object> data) {
        List<IVocaFullPlayTTSItem> list = BaseVocaList.convertToVocaFullPlayerTtsItemList(data);
        startActivity(BaseVocaFilterActivity.createIntentWithVocaTypeIdList(VocaListPlayerActivity.this, WordListType.VOCA_TYPE_ID_LIST, BaseVocaFilterActivity.NextView.PLAY_ALL, BaseVocaList.convertToVocaTypeIdList(list), subDatabase.getDatabasePath()));
    }

//    private void openPlayListByTtsActivity(ArrayList<Object> data) {
//        Intent intent = new Intent(this, PlayListByTtsActivity.class);
////        List<IVocaBasicItem> vocaBasicItemList = new ArrayList<>();
////        for(Object object : data) {
////            if (object instanceof IVocaBasicItem) {
////                vocaBasicItemList.add((IVocaBasicItem) object);
////            }
////        }
////        List<VocaTypeId> vocaTypeIdList = BaseVocaList.convertToVocaTypeIdList(vocaBasicItemList);
////        intent.putExtra(Constant.BUNDLE.KEY_VOCA_PLAYLIST, (Serializable) vocaTypeIdList);
//
//        intent.putExtra(Constant.BUNDLE.KEY_VOCA_PLAYLIST, data);
//        playTTS.stopPlayVoca();
//        playTTS.setIncludeMeaning();
////        if (playVocaHelper != null) {
////            playVocaHelper.stop();
////            playVocaHelper.clearListener();
////        }
////        setIncludeMeaning();
//        openNewScreen(intent);
//    }

    private void openPlaylistActivity(ArrayList<Object> data) {
        Intent intent = new Intent(this, PlayListByTtsActivity.class);
        intent.putExtra(Constant.BUNDLE.KEY_VOCA_PLAYLIST, data);
        playTTS.stopPlayVoca();
        playTTS.setIncludeMeaning();
//        if (playVocaHelper != null) {
//            playVocaHelper.stop();
//            playVocaHelper.clearListener();
//        }
//        setIncludeMeaning();
        openNewScreen(intent);
    }

    protected void handlePlay(IVocaFullPlayTTSItem model, View view) {
        DLog.d(getLogTag(), "handlePlay - path=" + model.getVIPath());
        boolean isPlaying = model.isVIPlaying();
        playTTS.stop();
        playTTS.onSpeakerClick(model, (ImageView) view);
//        if (!isPlaying) {
//            playTTS.onSpeakerClick(model, (ImageView) view);
////            playTTS.preparePlayVocaSingle(model);
//        }
    }

    protected void changeValue(IVocaBasicItem iVocaBasicItem, int newVocaKnow) {
        updateVocaKnow(newVocaKnow, iVocaBasicItem);
    }

    private void updateVocaKnow(int newVocaKnow, IVocaBasicItem iVocaBasicItem) {
        if (updateModelWithNewVocaKnow(iVocaBasicItem, newVocaKnow, -1)) {
            saveVocaKnowValue(iVocaBasicItem);
            updateUIVocaInfo(iVocaBasicItem);
        }
    }

    protected void showWordListPlayerDialog(Object data) {
        if (wordListPlayerDialog == null) {
            wordListPlayerDialog = new WordListPlayerDialog(this, data, onWordListPlayerDialogListener);
        } else {
            wordListPlayerDialog.setData(data);
        }
        if (wordListPlayerDialog != null && wordListPlayerDialog.isShowing()) {
            wordListPlayerDialog.dismiss();
        }
        wordListPlayerDialog.show();
    }
    
    protected void openExportWordListActivity() {
        // 단어장 내보내기 액티비티 실행 - createIntent 메서드 사용
        Intent intent = ExportWordListActivity.createIntent(this, playerFileModel);
        startActivity(intent);
    }

    protected OnClickListener onWordListPlayerDialogListener = (view, object) -> {
        switch (view.getId()) {
            case R.id.llChangeWordsKnownStatus:
                openWordListPopUpFromSubtitle((DicModel) object, null);
                break;
            case R.id.llPlayAllWords:
                handlePlayAll(totalItems);
                break;
            case R.id.llExportWordList:
                openExportWordListActivity();
                break;
            case R.id.llKnownPhrases:
                changeAllVocaKnow(Constant.VOCA_KNOW.VOCA_KNOW_KNOWN);
                break;
            case R.id.llUnknownPhrases:
                changeAllVocaKnow(Constant.VOCA_KNOW.VOCA_KNOW_UNKNOWN);
                break;
        }
    };

    private void openWordListPlayerPlayAllWordsDialog() {
        if (wordListPlayerPlayAllWordsDialog == null) {
            wordListPlayerPlayAllWordsDialog = new WordListPlayerPlayAllWordsDialog(this, onWordListPlayerPlayAllWordsDialogListener);
        }
        wordListPlayerPlayAllWordsDialog.show();
    }

    protected OnClickListener onWordListPlayerPlayAllWordsDialogListener = (view, object) -> {
        DLog.d(getLogTag(), "onWordListPlayerPlayAllWordsDialogListener");
        if (object instanceof VocaKnowGroupSelect) {
//            openPlaylistActivity(getWordListByWordListGroup((VocaKnowGroupSelect) object));
            openPlaylistActivity(getWordListByWordListGroup((VocaKnowGroupSelect) object));
        }
    };

    @NotNull
    private ArrayList<Object> getWordListByWordListGroup(VocaKnowGroupSelect vocaKnowGroupSelect) {
        ArrayList<Object> data = new ArrayList<>();
        ArrayList<Object> grade1 = new ArrayList<>();
        ArrayList<Object> grade2 = new ArrayList<>();
        ArrayList<Object> unknown = new ArrayList<>();
        ArrayList<Object> known = new ArrayList<>();
        ArrayList<Object> notRated = new ArrayList<>();


        for (Object obj : totalItems) {
            final DicModel item = (DicModel) obj;
            if (VocaKnow.isKnown(item)) {
                known.add(item);
            } else if (VocaKnow.isAmkiGrade1(item)) {
                grade1.add(item);
            } else if (VocaKnow.isAmkiGrade2(item)) {
                grade2.add(item);
            } else if (VocaKnow.isUnknown(item)) {
                unknown.add(item);
            } else {
                notRated.add(item);
            }
        }

        if (vocaKnowGroupSelect.isAmki1st() && !Utils.isEmpty(grade1)) {
            data.addAll(grade1);
        }
        if (vocaKnowGroupSelect.isAmki2nd() && !Utils.isEmpty(grade2)) {
            data.addAll(grade2);
        }
        if (vocaKnowGroupSelect.isUnknown() && !Utils.isEmpty(unknown)) {
            data.addAll(unknown);
        }
        if (vocaKnowGroupSelect.isNotRated() && !Utils.isEmpty(notRated)) {
            data.addAll(notRated);
        }
        if (vocaKnowGroupSelect.isKnown() && !Utils.isEmpty(known)) {
            data.addAll(known);
        }


        return data;
    }

    // Not using at this moment. And I think "Subtitle list view" is only for displaying Subtitle and image only is enough.
    protected void openWordListPopUpFromSubtitle(DicModel model, OnClickListener callback) {
        if (Utils.isEmpty(model.getRubyIds()) ||
                Utils.isEmpty(model.getRubyTypes())) return;
        if (!isLoggedIn()) {
            alertDialog.showLogInRequired();
            return;
        }
        if (!isNetwork()) {
            alertDialog.showNoInternet();
            return;
        }
        DLog.d(getLogTag(), "getRubyList - model=" + model.toString());
        Loading.show(this);
        application.getDalAiImpl().getVocasFromVocaList(
                sharedPreferences.getLangStudyCode(),
                sharedPreferences.getRealUid(),
                sharedPreferences.getRealUid(),
                model.getRubyIds(),
                model.getRubyTypes(),
                new DalApiListener<List<VocaStudyChatRuby>>() {
                    @Override
                    public void onSuccess(List<VocaStudyChatRuby> response) {
                        if (response != null) {
                            for (VocaStudyChat childVoca : response) {
//                                childVoca.setTblParentSection(voca.getTblSection());
//                                childVoca.setTblParentRow(voca.getTblRow());
                                childVoca.setTblTypeSync(Constant.API_VALUE.TBL_TYPE_SYNC_RUBY_VOCA_LIST);
                                childVoca.setPnType(Constant.API_VALUE.PN_TYPE_RUBY_TEXT);
                            }
                            StudyChatAdapter adapter = new StudyChatAdapter(VocaListPlayerActivity.this);
                            adapter.setDataNoLoop(new ArrayList<>(response));
                            setRecyclerViewDataDialog(adapter, callback);
                        }
                        Loading.hide();
                    }

                    @Override
                    public void onFailure(String error) {
                        Loading.hide();
                    }
                }
        );
    }

    protected void setRecyclerViewDataDialog(StudyChatAdapter adapter, OnClickListener callback) {
        setRecyclerViewDataDialog(null, adapter, callback);
    }

    protected void setRecyclerViewDataDialog(String title, StudyChatAdapter adapter, OnClickListener callback) {
        adapter.setDisplayPronunciation(sharedPreferences.getDisplayPronunciation());
        adapter.setHighlightIndex(0);
        adapter.setHasHeader(true);
//        adapter.setListener(onVocaStudyChatClickListener);
        recyclerViewDialog.setAdapter(adapter, title);
        recyclerViewDialog.show();
        playTTS.stop();
        if (callback != null) {
            callback.onClick(null, adapter);
        }
    }

//    //Use this?
//    protected OnVocaStudyChatClickListener onVocaStudyChatClickListener = new OnVocaStudyChatClickListener() {
//
//        @Override
//        public void onItemClick(VocaStudyChat voca) {
//            ToastUtil.getInstance(VocaListPlayerActivity.this).show("onVocaStudyChatClickListener onItemClick");
//        }
//
//        @Override
//        public void onDoubleItemClick(VocaStudyChat voca) {
//            ToastUtil.getInstance(VocaListPlayerActivity.this).show("onVocaStudyChatClickListener onDoubleItemClick");
//        }
//
//        @Override
//        public void onPlayClick(final VocaStudyChat voca) {
//            handlePlay(voca);
//        }
//
//        @Override
//        public void onBigIconClick(VocaStudyChat voca) {
//            registerVocaDialog.show(voca, false);
//        }
//
//        @Override
//        public void onAsteriskSentenceClick(final VocaStudyChat voca) {
//            ToastUtil.getInstance(VocaListPlayerActivity.this).show("onVocaStudyChatClickListener onAsteriskSentenceClick");
//        }
//
//        @Override
//        public void onVocaKnowClick(VocaStudyChat voca, int vocaKnow) {
//            onKnowChangeListener.onVocaKnowChange(voca, vocaKnow);
//        }
//
//        @Override
//        public void onEvaluateGradeClick(VocaStudyChat voca, String grade) {
//            ToastUtil.getInstance(VocaListPlayerActivity.this).show("onVocaStudyChatClickListener onEvaluateGradeClick");
//        }
//
//        @Override
//        public void onAnswerClick(VocaStudyChatExam voca) {
//            ToastUtil.getInstance(VocaListPlayerActivity.this).show("onVocaStudyChatClickListener onAnswerClick");
//        }
//    };

    //DialogueListPlayerAdapter calls this
    private OnKnowChangeListener onKnowChangeListener = new OnKnowChangeListener() {
        @Override
        public void onVocaKnowChange(IVocaBasicItem iVocaBasicItem, int newVocaKnow) {
            updateVocaKnow(newVocaKnow, iVocaBasicItem);
        }

        @Override
        public void onVocaKnowPronounceChange(IVocaBasicItem iVocaBasicItem, int newVocaKnowPronounce) {
            //TODO : Not completed.
            if (updateModelWithNewVocaKnow(iVocaBasicItem, -1, newVocaKnowPronounce)) {
                saveVocaKnowValue(iVocaBasicItem);
                updateUIVocaInfo(iVocaBasicItem);
            }

        }

        @Override
        public void onAddToWordbook(IVocaBasicItem iVocaBasicItem) {

        }

        @Override
        public void onAddToBookmark(IVocaBasicItem iVocaBasicItem) {
            updateBookmark(iVocaBasicItem);
        }

        @Override
        public void onDeleteFromBookmark(IVocaBasicItem iVocaBasicItem) {
        }

        @Override
        public void onDismiss() {

        }
    };

    protected void updateUIVocaInfo(IVocaBasicItem iVocaBasicItem) {
        callAsyncTask(TYPE_UPDATE_UI_VOCA_INFO, iVocaBasicItem);
    }

    protected void changeAllVocaKnow(int vocaKnow) {
        if (!isLoggedIn()) {
            alertDialog.showLogInRequired();
            return;
        }
        if (!isNetwork()) {
            alertDialog.showNoInternet();
            return;
        }
        Loading.show(this);
        application.getDalAiImpl().changeMultipleVocaKnow(
                this,
                vocaKnow,
                VocaKnow.getVocaKnowPronounceByVocaKnow(vocaKnow),
                allVocaIds,
                allVocaTypes,
                false,
                new DalApiListener<Integer>() {

                    @Override
                    public void onSuccess(Integer newVocaKnow) {
                        callAsyncTask(TYPE_UPDATE_ALL_KNOWN, newVocaKnow);
                    }

                    @Override
                    public void onFailure(String error) {
                    }
                }
        );
    }

    protected void updateAllVocaKnow(Object object) {
        int newVocaKnowFromServer = (int) object;
        DLog.d(getLogTag(), "updateAllKnown - newVocaKnowFromServer=" + newVocaKnowFromServer);
        for (Object obj : items) {
            if (obj instanceof IVocaBasicItem) {
                IVocaBasicItem iVocaBasicItem = (IVocaBasicItem) obj;
                int newVocaKnow = VocaKnow.getUnknowValue();
                if (VocaKnow.isKnown(newVocaKnowFromServer)) {
                    newVocaKnow = VocaKnow.getKnowValue();
                }
                iVocaBasicItem.setVIVocaKnow(newVocaKnow);
                iVocaBasicItem.setVIVocaKnowPronounce(newVocaKnow);
            }
        }

        getSubDatabase().updateMultipleVocaKnowInDBByListObject(items, newVocaKnowFromServer );
    }

    protected void initPlayVocaHelperListener() {
        DLog.d(getLogTag(), "initPlayVocaHelper");
        DLog.d(getLogTag(), "isIncludeMyVoice=" + playTTS.playTTSHelper.isIncludeMyVoice());
        DLog.d(getLogTag(), "isIncludeMeaning=" + playTTS.playTTSHelper.isIncludeMeaning());
        DLog.d(getLogTag(), "playVocaHelper.studyTTS=" + playTTS.playTTSHelper.studyTTS);
        if (!playTTS.playTTSHelper.hasMotherTongueListener()) {
            playTTS.playTTSHelper.setMotherTongueListener(new UtteranceProgressListener() {

                @Override
                public void onStart(String utteranceId) {
                    updateTTSItemStatus(utteranceId, true);
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
                    updateTTSItemStatus(utteranceId, false);
                }

                @Override
                public void onError(String utteranceId) {
                    updateTTSItemStatus(utteranceId, false);
                }
            });
        }
    }

    protected void updateTTSItemStatus(String utteranceId, boolean playing) {
        if (recyclerViewDialog != null && recyclerViewDialog.isShowing()) {
            StudyChatAdapter adapter = (StudyChatAdapter) recyclerViewDialog.getAdapter();
            for (Object obj : adapter.getData()) {
                if (obj instanceof VocaStudyChat) {
                    VocaStudyChat voca = (VocaStudyChat) obj;
                    if (utteranceId.equals(String.valueOf(voca.getVIId()))) {
                        DLog.d(getLogTag(), "utteranceId=" + utteranceId + " - " + voca.getVIId() + " - voca");
                        voca.setVIPlaying(playing);
                        binding.rvList.post(() -> adapter.notifyItemChanged(voca));
                        break;
                    }
                }
            }
        } else {
            for (final Object obj : items) {
                if (obj instanceof DicModel) {
                    DicModel dic = (DicModel) obj;
                    if (utteranceId.equals(String.valueOf(dic.getVIId()))) {
                        DLog.d(getLogTag(), "utteranceId=" + utteranceId + " - " + dic.getVIId());
                        dic.setVIPlaying(playing);
                        binding.rvList.post(() -> vocaListPlayerAdapter.notifyItemChanged(dic.getPosition()));
                        break;
                    }
                }
            }
        }
    }
}
