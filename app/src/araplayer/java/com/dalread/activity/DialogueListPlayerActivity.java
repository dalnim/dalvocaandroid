package com.dalread.activity;

import android.annotation.SuppressLint;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.dalread.R;
import com.dalread.adapter.DialogueListPlayerAdapter;
import com.dalread.asyntask.OnAsyncTaskListener;
import com.dalread.base.VocaListPlayerActivity;
import com.dalread.database.sqlite.model.DicModel;
import com.dalread.dialog.ThumbnailListPlayerDialog;
import com.dalread.util.DLog;
import com.dalread.util.MergeUtil;
import com.dalread.util.Utils;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class DialogueListPlayerActivity extends VocaListPlayerActivity implements OnAsyncTaskListener {
    @Override
    public void initView() {
        super.initView();
        binding.nvBottom.setVisibility(View.GONE);
        initAutoScrollIntervalCountValue();
        updateAutoScrollIconToPlay();
        //Don't delete this (May use this bottom navigation view.
//        nvBottom.getMenu().clear();
//        nvBottom.inflateMenu(R.menu.menu_bottom_dialog_list_player);
    }

    protected void initRecycleView() {
        super.initRecycleView();
        vocaListPlayerAdapter = new DialogueListPlayerAdapter(this, playerFileModel, getSubDatabase(), sharedPreferences.getDisplayPronunciation(), onRecycleClickListener, onDoubleClickListener);
        binding.rvList.setAdapter(vocaListPlayerAdapter);
    }

    protected void generateAllData() {
        totalItems.clear();
        if (!Utils.isEmpty(subtitleLanguageModels)) {
            ArrayList<DicModel> tmp = new ArrayList<>();
            if (currentBottomNavigationId == R.id.nav_dialog_list_hided) {
                tmp.addAll(getSubDatabase().getSubtitleDialogList().stream()
                        .filter(e -> e.isShowUsed() == false).collect(Collectors.toList()));
            } else {
                tmp.addAll(getSubDatabase().getSubtitleDialogList());
            }
            MergeUtil.generateMeaning(tmp);
            totalItems.addAll(tmp);
        }
    }

    protected void initToolbar() {
        super.initToolbar();
        binding.header.setTitle(R.string.dialog_list);
    }

    @Override
    public void onHeaderLeft2Click() {
        onHeaderAutoScrollIconClick();
    }

    @Override
    public void onHeaderTextRightClick() {
        stopAutoScrolling();
        openMenuDialog();
    }

    @SuppressLint("StringFormatInvalid")
    protected void generateData() {
        resetData();
        if (Utils.isEmpty(totalItems))
            return;

        final ArrayList<Object> tmp = checkData(totalItems);
        if (tmp.size() > 0) {
            items.addAll(tmp);
        }

    }

    public ArrayList<Object> checkData(ArrayList<Object> totalData) {
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

    protected void handleItemClick(DicModel model) {
        DLog.d(getLogTag(), "handleItemClick - model=" + model.toString());
        showWordListPlayerDialog(model);
    }

    @Override
    protected Object getIconTag() {
        return binding.header.getIconLeft2().getTag();
    }

    @Override
    protected void updateAutoScrollIconToPause() {
        binding.header.setIconLeft2(R.drawable.ic_new_pause);
        binding.header.getIconLeft2().setTag(R.drawable.ic_new_pause);
    }

    @Override
    protected void updateAutoScrollIconToPlay() {
        binding.header.setIconLeft2(R.drawable.ic_new_play);
        binding.header.getIconLeft2().setTag(R.drawable.ic_new_play);
    }

    @Override
    protected RecyclerView getAutoScrollRecyclerView() {
        return binding.rvList;
    }

    @Override
    protected RecyclerView.Adapter getAutoScrollRecyclerViewAdapter() {
        return vocaListPlayerAdapter;
    }

    @Override
    protected RecyclerView.LayoutManager getAutoScrollLayoutManager() {
        return binding.rvList.getLayoutManager();
    }

    private void openMenuDialog() {
        final ThumbnailListPlayerDialog dialog = new ThumbnailListPlayerDialog(this, true, (view, object) -> {
            if (view.getId() == R.id.llAutoScrolling) {
                showAutoScrollIntervalDialog();
            }
        });

        dialog.show();
    }
}
