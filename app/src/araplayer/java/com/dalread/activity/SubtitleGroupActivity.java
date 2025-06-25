package com.dalread.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.dalread.R;
import com.dalread.adapter.SubtitleGroupAdapter;
import com.dalread.asyntask.OnAsyncTaskListener;
import com.dalread.base.BasePlayerActivity;
import com.dalread.component.Toolbar;
import com.dalread.component.itemdecoration.SeparatorDecorationAddPaddingStartEnd;
import com.dalread.composition.VocaKnowActivity;
import com.dalread.database.sqlite.model.DicModel;
import com.dalread.databinding.ActivitySubtitleGroupBinding;
import com.dalread.dialog.PlayerShowSubtitleDialogHided;
import com.dalread.dialog.PlayerShowSubtitleDialogToDelete;
import com.dalread.dialog.SelectRange2Dialog;
import com.dalread.dialog.WordListPlayerPlayAllWordsDialog2;
import com.dalread.helper.SubtitleGroupHelper;
import com.dalread.helper.SubtitleListHelper;
import com.dalread.interfaces.IVocaBasicItem;
import com.dalread.interfaces.IVocaFullPlayTTSItem;
import com.dalread.listener.OnClickDialogListener;
import com.dalread.listener.OnClickListener;
import com.dalread.listener.OnYesNoClickListener;
import com.dalread.model.PlayerFileModel;
import com.dalread.model.SubtitleHideModel;
import com.dalread.model.VocaKnowGroupSelect;
import com.dalread.network.events.SuccessEvent;
import com.dalread.util.BaseBindUtils;
import com.dalread.util.Constant;
import com.dalread.util.DLog;
import com.dalread.util.DialogUtil;
import com.dalread.util.Loading;
import com.dalread.util.SubtitleContentUtil;
import com.dalread.util.ToastUtil;
import com.dalread.util.Utils;
import com.dalread.util.Voca;
import com.dalread.util.VocaKnow;

import org.greenrobot.eventbus.Subscribe;

import java.util.List;

public class SubtitleGroupActivity extends BasePlayerActivity implements OnAsyncTaskListener, View.OnClickListener, SubtitleGroupAdapter.ItemCheckedListener {
    public enum Type {
        DELETE,
        SELECT,
    }
    private ActivitySubtitleGroupBinding binding;
    private SubtitleGroupAdapter mAdapter;
    private PlayerFileModel playerFileModel;
    public SubtitleGroupHelper.TYPE subtitleGroupHelperType = SubtitleGroupHelper.TYPE.ALL_SUBTITLES;
    private PlayerShowSubtitleDialogToDelete subtitleDialogToDelete;
    private PlayerShowSubtitleDialogHided subtitleDialogHided;
    private SubtitleGroupHelper subtitleGroupHelper;
    private SubtitleListHelper subtitleListHelper;
    private SelectRange2Dialog selectRangeDialog; //자막 그룹에서 일정 영역의 자막만 선택할수 있게.
    private String keyword = "";
    private boolean isAllSubtitlesSelected = true;
    private boolean isSubtitleDeleted = false;
    private long countOfItemChecked = 0;
    private static Type type;
    private final int TYPE_INIT_DATA = 0;
    private final int TYPE_DELETE_ITEM_IN_LIST = TYPE_INIT_DATA + 1;
    private final int TYPE_SUBTITLE_TO_DELETE = TYPE_DELETE_ITEM_IN_LIST + 1;
    private final int TYPE_SUBTITLE_TO_SELECT = TYPE_SUBTITLE_TO_DELETE + 1;
    private final int TYPE_DIFFICULT_SUBTITLE = TYPE_SUBTITLE_TO_SELECT + 1;

    public static Intent createIntent(BasePlayerActivity activity, PlayerFileModel playerFileModel, Type typeLocal) {
        Intent intent = new Intent(activity, SubtitleGroupActivity.class);
        intent.putExtra(Constant.PLAYER.INTENT.KEY_DATA, playerFileModel);
        type = typeLocal;
        return intent;
    }

    public SubtitleGroupActivity() {
    }
    @Override
    protected View getContentView() {
        binding = ActivitySubtitleGroupBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    protected Toolbar getToolbar() {
        return binding.header;
    }

    @Override
    protected void setFullscreen() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
        initDialog();
        setUpRecyclerView();
        initToolbar();
        setUpSubtitleDialogs();
        setUpClickListeners();
        updateStatusAndIcon();
        callAsyncTask(this, TYPE_INIT_DATA);
    }
    private void initToolbar() {
        setToolbarTitle();
        hideToolbarRightTextview();
        binding.header.setSearchListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                keyword = s;
                if (TextUtils.isEmpty(keyword)) {
                    subtitleListHelper.resetSubtitleList();
                } else {
                    subtitleListHelper.filterSubtitleList(keyword);
                }

                bindData();
                return true;
            }
        }, new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                return true;
            }
        });
        binding.header.showSearchView();
    }

    private void setToolbarTitle() {
        binding.header.setTitle(type == Type.DELETE ? R.string.view_titile_delete_subtitles : R.string.view_titile_select_subtitles);
    }
    private void hideToolbarRightTextview() {
        if (type == Type.DELETE) {
            binding.header.hideTvRight();
        }
    }
    private void initDialog() {
        subtitleListHelper = new SubtitleListHelper(this, subDatabase, playerFileModel);
        subtitleDialogHided = new PlayerShowSubtitleDialogHided(this, onSubtitleDialogHidedListener);
        selectRangeDialog = new SelectRange2Dialog(this, selectRangeListener);
    }
    private SelectRange2Dialog.OnRangeSelectListener selectRangeListener = new SelectRange2Dialog.OnRangeSelectListener() {
        @Override
        public void onSelect(int from, int to) {
            if (Utils.isEmpty(getSubtitleList()))
                return;

            if (from < 1) {
                from = 1;
            }
            if (to < 1) {
                to = 1;
            }
            int size = getSubtitleList().size();
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
            DLog.d(getLogTag(), "onSelect to=" + to + " - from=" + from);
            subtitleListHelper.sublistFromSubtitleList(from -1, to);
            bindData();
        }

        @Override
        public void onDismiss(View v) {

        }
    };
    private void setUpRecyclerView() {
        mAdapter = new SubtitleGroupAdapter(this, vocaKnowActivity.onDoubleClickListenerOnBaseVocaKnow,this); //왜 이걸 init이나 create에서 하면 죽지?
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        binding.recyclerView.setAdapter(mAdapter);//왜 이걸 init이나 create에서 하면 죽지?
        binding.recyclerView.setLayoutManager(layoutManager);
        binding.recyclerView.addItemDecoration(new SeparatorDecorationAddPaddingStartEnd(context, BaseBindUtils.getDividerColor(this), BaseBindUtils.getDividerHeight(this)));
    }

    private void setUpSubtitleDialogs() {
        subtitleDialogToDelete = new PlayerShowSubtitleDialogToDelete(this, onSubtitleDialogToDeleteListener);
    }

    private void setUpClickListeners() {
        binding.ivShowAll.setOnClickListener(this);
        binding.ivDelete.setOnClickListener(this);
        binding.ivSelectAll.setOnClickListener(this);
        binding.ivSubtitleDialogHided.setOnClickListener(this);
        binding.ivVocaKnow.setOnClickListener(this);
        binding.ivMic.setOnClickListener(this);
        binding.ivSelectRange.setOnClickListener(this);
    }

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
        }
    }
    private void refreshSearchViewAdapterDataChanged(IVocaBasicItem voca) {
        if (Utils.isEmpty(getSubtitleList()))
            return;

        for (IVocaBasicItem item : getSubtitleList()) {
            if (Voca.isSameVoca(item, voca)) {
                int newBookmark = voca.getVIBookmark();
                int newVocaKnow = voca.getVIVocaKnow();
                int newVocaKnowPronounce = VocaKnow.getVocaKnowPronounceByVocaKnow(newVocaKnow);
                item.setVIBookmark(newBookmark);
                item.setVIVocaKnow(newVocaKnow);
                item.setVIVocaKnowPronounce(newVocaKnowPronounce);
                break;
            }
        }
        bindData();
    }
    public void createSubDatabase(PlayerFileModel playerFileModel) {
        super.createSubDatabase(playerFileModel);
    }

    private List<DicModel> getDictationData(SubtitleGroupHelper.TYPE type) {
        subtitleListHelper.initData();
        subtitleGroupHelper = new SubtitleGroupHelper(getSubtitleList(), subDatabase, new SubtitleHideModel());
        return subtitleGroupHelper.generateSubtitleDialogByType(type);
    }

    private List<DicModel> getExceptionSubtitles(SubtitleHideModel subtitleHideModel) {
        subtitleGroupHelper = new SubtitleGroupHelper(getSubtitleList(), subDatabase, subtitleHideModel);
        return subtitleGroupHelper.generateSubtitleDialogByType(SubtitleGroupHelper.TYPE.TO_DELETE_SUBTITLE);
    }

    private List<DicModel> getSubtitleToSelect(SubtitleHideModel subtitleHideModel) {
        SubtitleGroupHelper subtitleGroupHelper = new SubtitleGroupHelper(getSubtitleList(), subDatabase, subtitleHideModel);//, rubyTextModels);
        return subtitleGroupHelper.generateSubtitleDialogByType(SubtitleGroupHelper.TYPE.ALL_SUBTITLES_EXCEPT_AUTO_HIDED);
    }

    private void updateStatus() {
        String text = getString(R.string.subtitle_group_status_total_subtitle_count) + " : " + getSubtitelListTotal().size() + ", " + getString(R.string.subtitle_group_status_displayed_subtitle_count) + " : " + getSubtitleList().size() + ", " + getString(R.string.subtitle_group_status_selected_subtitle_count) + " : " + countOfItemChecked;
        binding.tvStatus.setText(text);
    }

    private OnClickDialogListener onSubtitleDialogToDeleteListener = new OnClickDialogListener() {
        @Override
        public void onClick(View view, Object object) {
            SubtitleHideModel subtitleHideModel = (SubtitleHideModel) object;
            switch (view.getId()) {
                case R.id.tv_apply:
                    callAsyncTask(SubtitleGroupActivity.this, subtitleHideModel, TYPE_SUBTITLE_TO_DELETE);
                    break;
            }
        }

        @Override
        public void onDismiss(View view, Object object) {

        }
    };
    private OnClickDialogListener onSubtitleDialogHidedListener = new OnClickDialogListener() {
        @Override
        public void onClick(View view, Object object) {
            callAsyncTask(SubtitleGroupActivity.this, object, TYPE_SUBTITLE_TO_SELECT);
        }

        @Override
        public void onDismiss(View view, Object object) {

        }
    };
    @Override
    public void onInitAsyncTask() {
        Loading.showWithoutMessage(this);
    }

    @Override
    public void onPrevExecuteAsyncTask() {

    }

    @Override
    public Object onPostExecuteAsyncTask(int searchType, Object data) {
        switch (searchType) {
            case TYPE_INIT_DATA:
            case TYPE_DELETE_ITEM_IN_LIST:
                return getDictationData(subtitleGroupHelperType);
            case TYPE_SUBTITLE_TO_DELETE:
                SubtitleHideModel subtitleHideModel = (SubtitleHideModel) data;
                return getExceptionSubtitles(subtitleHideModel);
            case TYPE_SUBTITLE_TO_SELECT:
                SubtitleHideModel subtitleHideModel1 = (SubtitleHideModel) data;
                return getSubtitleToSelect(subtitleHideModel1);
            case TYPE_DIFFICULT_SUBTITLE:
                subtitleGroupHelper = new SubtitleGroupHelper(getSubtitleList(), subDatabase, new SubtitleHideModel());//, rubyTextModels);
                subtitleGroupHelper.setVocaKnowGroupSelect((VocaKnowGroupSelect) data);
                return subtitleGroupHelper.generateSubtitleDialogByType(SubtitleGroupHelper.TYPE.DIFFICULTY_SUBTITLE);
        }
        return null;
    }

    @Override
    public void onFinishAsyncTask(int searchType, Object resultData, Object data) {
        switch (searchType) {
            case TYPE_INIT_DATA: //맨처음 로딩시에는 이전에 선택된 자막만 보이게 할려고 setSubtitleList()를 호출하지 않는다.
                bindData();
                break;
            case TYPE_SUBTITLE_TO_DELETE:
            case TYPE_SUBTITLE_TO_SELECT:
            case TYPE_DIFFICULT_SUBTITLE:
                setSubtitleList((List<DicModel>) resultData);
                bindData();
                break;
            case TYPE_DELETE_ITEM_IN_LIST:
                setSubtitleList((List<DicModel>) resultData);
                bindData();
                ToastUtil.getInstance(SubtitleGroupActivity.this).show(getString(R.string.toast_subtitle_group_deleted_subtitles) + data);
                break;
        }
        Loading.hide();
    }

    private void bindData() {
        mAdapter.setSubtitles(getSubtitleList(), keyword);
        mAdapter.notifyDataSetChanged();
        updateStatusAndIcon();
    }
    private List<DicModel> getSubtitelListTotal() {
        return subtitleListHelper.getSubtitleListTotal();
    }
    private List<DicModel> getSubtitleList() {
        return subtitleListHelper.getSubtitleList();
    }
    private void setSubtitleList(List<DicModel> list) {
        subtitleListHelper.setSubtitleList(list);
        updateStatusAndIcon();
    }

    private void askToSaveCheckedInDB() {
        List<DicModel> checkedSubtitleList = SubtitleContentUtil.getCheckedSubtitle(getSubtitleList());
        List<Integer> idsToDelete = SubtitleContentUtil.getIdListFromSubtitle(checkedSubtitleList);
        subDatabase.updateCheckedSubtitleInDB(idsToDelete);
        isSubtitleDeleted = true;
    }

    private void askToDeleteSubtitle() {
        List<DicModel> checkedSubtitleList = SubtitleContentUtil.getCheckedSubtitle(getSubtitleList());
        if (Utils.isNotEmpty(checkedSubtitleList)) {
            DialogUtil.askToDeleteDialogInSubtitle(this, new OnYesNoClickListener() {
                @Override
                public void onYesClick(View view, Object object) {
                    isSubtitleDeleted = true;
                    List<Integer> idsToDelete = SubtitleContentUtil.getIdListFromSubtitle(checkedSubtitleList);
                    subDatabase.deleteSubtitlesInSubtitleTable(idsToDelete);
                    callAsyncTask(SubtitleGroupActivity.this, idsToDelete.size(), TYPE_DELETE_ITEM_IN_LIST);
                }

                @Override
                public void onNoClick(View view, Object object) {

                }
            });
        }
    }

    @Override
    public void initView() {

    }

    @Override
    public void initData() {
        binding.tvGuide.setText(type == Type.DELETE ? R.string.voca_filter_text_view_guide_to_delete : R.string.voca_filter_text_view_guide_to_select);
        playerFileModel = getIntent().getExtras().getParcelable(Constant.PLAYER.INTENT.KEY_DATA);
        createSubDatabase(playerFileModel);
        vocaKnowActivity = new VocaKnowActivity(this, subDatabase);
    }

    @Override
    public void onHeaderLeftClick() {
        onBackPressed();
    }
    @Override
    public void onBackPressed() {
        setResult(isSubtitleDeleted ? RESULT_OK : RESULT_CANCELED);
        super.onBackPressed();
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
        if (type == Type.SELECT) {
            askToSaveCheckedInDB();
            onBackPressed();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivShowAll:
                showAllSubtitles();
                break;
            case R.id.ivDelete:
                askToDeleteSubtitle();
                break;
            case R.id.ivSelectAll:
                selectOrUnselectAllInTable();
                break;
            case R.id.ivSubtitleDialogHided:
                if (type == Type.DELETE) {
                    subtitleDialogToDelete.show();
                } else {
                    subtitleDialogHided.show();
                }
                break;
            case R.id.ivSelectRange:
                selectRangeDialog.show(1, getSubtitleList().size());
                break;
            case R.id.ivVocaKnow:
                showSubtitleDifficultDialog(SubtitleGroupHelper.TYPE.DIFFICULTY_SUBTITLE);
                break;
            case R.id.ivMic:
                showRecordedSubtitles();
                break;
        }
    }

    private void showRecordedSubtitles() {
        subtitleListHelper.filterRecorded();
        bindData();
    }

    private void showAllSubtitles() {
        ToastUtil.getInstance(this).show(R.string.subtitle_group_show_all_subtitles);
        subtitleListHelper.resetSubtitleList();
        bindData();
    }
    private void selectOrUnselectAllInTable() {
        isAllSubtitlesSelected = !isAllSubtitlesSelected;
        subtitleListHelper.setSelectOrUnselectAllSubtitles(isAllSubtitlesSelected);
        binding.ivSelectAll.setSelected(isAllSubtitlesSelected);
        String message = isAllSubtitlesSelected ?
                getString(R.string.subtitle_group_selected_all_displayed_subtitles)
                : getString(R.string.subtitle_group_unselected_all_displayed_subtitles);
        ToastUtil.getInstance(this).show(message);
        bindData();
    }

    @Override
    public void onItemChecked(boolean isChecked) {
        updateStatusAndIcon();
    }
    private void updateStatusAndIcon() {
        updateSelectAllDeleteIcon();
        updateStatus();
    }
//    private void updateSelectallDeleteIcon() {
//        countOfItemChecked = subtitleListHelper.getCountOfItemChecked();
//        if (countOfItemChecked > 0) {
//            binding.ivDelete.setVisibility(View.VISIBLE);
//            if (countOfItemChecked == getSubtitleList().size()) {
//                binding.ivSelectAll.setSelected(true);
//            }
//        } else {
//            binding.ivDelete.setVisibility(View.INVISIBLE);
//            binding.ivSelectAll.setSelected(false);
//        }
//        if (type == Type.DELETE) {
//            binding.ivDelete.setVisibility(View.INVISIBLE);
//        }
//    }
    private void updateSelectAllDeleteIcon() {
        countOfItemChecked = subtitleListHelper.getCountOfItemChecked();
        updateDeleteVisibility();
        updateSelectAllState();
    }

    private void updateDeleteVisibility() {
        if (countOfItemChecked > 0) {
            binding.ivDelete.setVisibility(View.VISIBLE);
        } else {
            binding.ivDelete.setVisibility(View.INVISIBLE);
        }
        hideDeleteIconForSelectType();
    }

    private void updateSelectAllState() {
        if (countOfItemChecked == getSubtitleList().size()) {
            binding.ivSelectAll.setSelected(true);
        } else {
            binding.ivSelectAll.setSelected(false);
        }
    }

    private void hideDeleteIconForSelectType() {
        if (type == Type.SELECT) {
            binding.ivDelete.setVisibility(View.INVISIBLE);
        }
    }

    private void showSubtitleDifficultDialog(SubtitleGroupHelper.TYPE subtitlePlayType) {
        WordListPlayerPlayAllWordsDialog2 dialog = new WordListPlayerPlayAllWordsDialog2(this, new OnClickListener() {
            @Override
            public void onClick(View view, Object object) {
                DLog.d(getLogTag(), "onWordListPlayerPlayAllWordsDialogListener");
                if (object instanceof VocaKnowGroupSelect) {
                    callAsyncTask(SubtitleGroupActivity.this, object, TYPE_DIFFICULT_SUBTITLE);
                }
            }
        });
        dialog.show();

    }
}
