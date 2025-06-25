package com.dalread.activity;

import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.dalread.R;
import com.dalread.adapter.BookmarkPlayerAdapter;
import com.dalread.base.BasePlayerActivity;
import com.dalread.component.CenterLayoutManager;
import com.dalread.component.SeparatorDecoration;
import com.dalread.component.Toolbar;
import com.dalread.database.BookmarkPlayerModelQuery;
import com.dalread.databinding.ActivityBookmarkListPlayerBinding;
import com.dalread.dialog.PlayerBookmarkRepeatDialog;
import com.dalread.dialog.PlayerBookmarkSelectDialog;
import com.dalread.listener.OnClickListener;
import com.dalread.model.BookmarkPlayerModel;
import com.dalread.util.BaseBindUtils;
import com.dalread.util.Constant;
import com.dalread.util.DLog;
import com.dalread.util.Loading;
import com.dalread.util.Voca;
import com.yanzhenjie.recyclerview.OnItemMenuClickListener;
import com.yanzhenjie.recyclerview.SwipeMenuCreator;
import com.yanzhenjie.recyclerview.SwipeMenuItem;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;
import com.yanzhenjie.recyclerview.touch.OnItemMoveListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BookmarkListActivity extends BasePlayerActivity implements View.OnClickListener {

//    @BindView(R.id.tvRepeatTimes) TextView tvRepeatTimes;
//    @BindView(R.id.rvList) SwipeRecyclerView rvList;
//    @BindColor(R.color.color_divider) int clDivider;
//    @BindDimen(R.dimen.divider_height) float dividerHeight;

    private BookmarkPlayerAdapter adapter;
    private List<BookmarkPlayerModel> bookmarkPlayerModels;
    private int select = Constant.PLAYER.BOOKMARK.SELECT.ALL;
    private int time = Constant.PLAYER.BOOKMARK.REPEAT.TIME_3;
    private boolean isBookmarkPlay;

    private ActivityBookmarkListPlayerBinding binding;

    @Override
    protected View getContentView() {
        binding = ActivityBookmarkListPlayerBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    protected Toolbar getToolbar() {
        return binding.header;
    }
    
//    @Override
//    protected int getContentViewId() {
//        return R.layout.activity_bookmark_list_player;
//    }

    @Override
    protected void setFullscreen() {

    }

    @Override
    public void initView() {
        Loading.show(this);
        adapter = new BookmarkPlayerAdapter(this, binding.rvList, onItemRemoveListener);
        binding.rvList.setOnItemMenuClickListener(mItemMenuClickListener);
        binding.rvList.setSwipeMenuCreator(mSwipeMenuCreator);
        binding.rvList.setOnItemMoveListener(onItemMoveListener);
        binding.rvList.setAdapter(adapter);
        binding.rvList.setLayoutManager(new CenterLayoutManager(this));
        binding.rvList.addItemDecoration(new SeparatorDecoration(this, BaseBindUtils.getDividerColor(), BaseBindUtils.getDividerHeight(this)));
        binding.rvList.setItemViewSwipeEnabled(false);
        updateRepeatTime();
        initOnClickListener();
    }

    @Override
    public void initData() {
        playerFileModel = getIntent().getParcelableExtra(Constant.PLAYER.INTENT.KEY_VIDEO_FILE);
        getData();
        adapter.notifyDataSetChanged(bookmarkPlayerModels);
        Loading.hide();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
    }

    @Override
    public void onHeaderLeftClick() {
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
        handlePlayClick();
    }

    @Override
    public void onHeaderTextRightClick() {
        runOnUiThread(() -> {
            Loading.show(BookmarkListActivity.this);
            final boolean isEdit = isEdit();
            if (isEdit) {
                binding.header.getTvRight().setText(R.string.done);
            } else {
                binding.header.getTvRight().setText(R.string.edit);
            }
            adapter.changeMode(isEdit);
            binding.rvList.setAdapter(adapter);
            adapter.notifyDataSetChanged(bookmarkPlayerModels);
            Loading.hide();
        });
    }

    private void initOnClickListener() {
        binding.llSelect.setOnClickListener(this);
        binding.llRepeat.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.llSelect:
                showDialogSelect();
                break;
            case R.id.llRepeat:
                showDialogRepeat();
                break;
        }
    }
//    @OnClick({R.id.llSelect, R.id.llRepeat})
//    void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.llSelect:
//                showDialogSelect();
//                break;
//            case R.id.llRepeat:
//                showDialogRepeat();
//                break;
//        }
//    }

    private boolean isEmptyData() {
        return bookmarkPlayerModels == null || bookmarkPlayerModels.size() <= 0;
    }

    private void showDialogSelect() {
        PlayerBookmarkSelectDialog dialog = new PlayerBookmarkSelectDialog(this, (dialogInterface, i) -> {
            switch (i) {
                case R.id.tvAll:
                    select = Constant.PLAYER.BOOKMARK.SELECT.ALL;
                    break;
                case R.id.tvNone:
                    select = Constant.PLAYER.BOOKMARK.SELECT.NONE;
                    break;
            }
            if (i != R.id.tv_cancel) {
                updateSelect();
            }
        });
        dialog.show();
    }

    private void updateSelect() {
        if (isEmptyData()) return;
        runOnUiThread(() -> {
            Loading.show(BookmarkListActivity.this);
            boolean isSelected = select == Constant.PLAYER.BOOKMARK.SELECT.ALL;
            for (BookmarkPlayerModel model : bookmarkPlayerModels) {
                model.setSelected(isSelected);
            }
            binding.rvList.setAdapter(adapter);
            adapter.notifyDataSetChanged(bookmarkPlayerModels);
            updatePlayView(isSelected);
            Loading.hide();
        });
    }

    private void showDialogRepeat() {
        PlayerBookmarkRepeatDialog dialog = new PlayerBookmarkRepeatDialog(this, (dialogInterface, i) -> {
            switch (i) {
                case R.id.tv1Time:
                    time = Constant.PLAYER.BOOKMARK.REPEAT.TIME_1;
                    break;
                case R.id.tv3Time:
                    time = Constant.PLAYER.BOOKMARK.REPEAT.TIME_3;
                    break;
                case R.id.tv5Time:
                    time = Constant.PLAYER.BOOKMARK.REPEAT.TIME_5;
                    break;
                case R.id.tv10Time:
                    time = Constant.PLAYER.BOOKMARK.REPEAT.TIME_10;
                    break;
                case R.id.tv20Time:
                    time = Constant.PLAYER.BOOKMARK.REPEAT.TIME_20;
                    break;
            }
            if (i != R.id.tv_cancel) {
                updateRepeatTime();
            }
        });
        dialog.show();
    }

    private void updateRepeatTime() {
        runOnUiThread(() -> binding.tvRepeatTimes.setText(getString(R.string.repeat_times, String.valueOf(time))));
    }

    private boolean isEdit() {
        return binding.header.getTvRight().getText().equals(getString(R.string.edit));
    }

    private void getData() {
        final List<BookmarkPlayerModel> temp = BookmarkPlayerModelQuery.getAllByPath(Voca.getRealm(), playerFileModel.getPath());
        bookmarkPlayerModels = new ArrayList<>();
        boolean isSelected = select == Constant.PLAYER.BOOKMARK.SELECT.ALL;
        for (BookmarkPlayerModel model : temp) {
            DLog.d(getLogTag(), "model=" + model.toString());
            if (isSelected) {
                bookmarkPlayerModels.add(new BookmarkPlayerModel(model, true));
            } else {
                bookmarkPlayerModels.add(new BookmarkPlayerModel(model));
            }
        }
        updatePlayView(checkPlayClick());
    }

    private OnClickListener onItemRemoveListener = (view, object) -> {
        switch (view.getId()) {
            case R.id.ivRemove:
                deleteBookmarkPlayerModel((Integer) object, true);
                break;
            case R.id.llItem:
                updatePlayView(checkPlayClick());
                break;
        }
    };

    private SwipeMenuCreator mSwipeMenuCreator = (swipeLeftMenu, swipeRightMenu, position) -> {
        int width = 100;
        int height = ViewGroup.LayoutParams.MATCH_PARENT;
        {
            SwipeMenuItem deleteItem = new SwipeMenuItem(BookmarkListActivity.this).setBackground(
                    R.color.color_red)
                    .setText(R.string.delete)
                    .setTextColor(Color.WHITE)
                    .setWidth(width)
                    .setHeight(height);
            swipeRightMenu.addMenuItem(deleteItem);
        }
    };

    private OnItemMenuClickListener mItemMenuClickListener = (menuBridge, position) -> {
        menuBridge.closeMenu();

        int direction = menuBridge.getDirection(); // 左侧还是右侧菜单。
        int menuPosition = menuBridge.getPosition(); // 菜单在RecyclerView的Item中的Position。

        if (direction == SwipeRecyclerView.RIGHT_DIRECTION) {
            deleteBookmarkPlayerModel(position);
        }
    };

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
            int adapterPosition = viewHolder.getAdapterPosition();
            int position = adapterPosition - binding.rvList.getHeaderCount();
            deleteBookmarkPlayerModel(position);
        }
    };

    private void swapPositionData(int fromPosition, int toPosition) {
        runOnUiThread(() -> {
            Loading.show(BookmarkListActivity.this);
            Collections.swap(bookmarkPlayerModels, fromPosition, toPosition);
            for (int i = 0; i < bookmarkPlayerModels.size(); i++) {
                bookmarkPlayerModels.get(i).setIndex(i);
                BookmarkPlayerModelQuery.update(Voca.getRealm(), bookmarkPlayerModels.get(i));
            }
            adapter.notifyItemMoved(fromPosition, toPosition);
            Loading.hide();
        });

    }

    private void handlePlayClick() {
        DLog.d(getLogTag(), "handlePlayClick");
        if (isEmptyData()) return;
        if (!isBookmarkPlay) return;
        runOnUiThread(() -> {
            Loading.show(this);
            for (BookmarkPlayerModel model : bookmarkPlayerModels) {
                BookmarkPlayerModelQuery.update(Voca.getRealm(), model);
            }
            openBookmarkPlayer();
        });
    }

    private boolean checkPlayClick() {
        for (BookmarkPlayerModel model : bookmarkPlayerModels) {
            if (model.isSelected()) {
                return true;
            }
        }
        return false;
    }

    private void deleteBookmarkPlayerModel(int position) {
        deleteBookmarkPlayerModel(position, false);
    }

    private void deleteBookmarkPlayerModel(int position, boolean isUpdate) {
        DLog.d(getLogTag(), "deleteBookmarkPlayerModel - position=" + position);
        runOnUiThread(() -> {
            Loading.show(BookmarkListActivity.this);
            BookmarkPlayerModelQuery.deleteById(Voca.getRealm(), bookmarkPlayerModels.get(position).getId());
            bookmarkPlayerModels.remove(position);
            adapter.notifyItemRemoved(position);
            if (isUpdate) {
                adapter.notifyItemRangeChanged(position, bookmarkPlayerModels.size());
            }
            Loading.hide();
        });
    }

    private void updatePlayView(boolean isPlay) {
        isBookmarkPlay = isPlay;
        binding.header.getIconRight().setImageResource(isPlay ? R.drawable.ic_play_white_24dp : R.drawable.ic_play_gray_24dp);
    }

    private void openBookmarkPlayer() {
        Intent intent = new Intent(this, BookmarkPlayerActivity.class);
        intent.putExtra(Constant.PLAYER.INTENT.KEY_VIDEO_FILE, playerFileModel);
        intent.putExtra(Constant.PLAYER.INTENT.KEY_TIME, time);
        startActivity(intent);
        Loading.hide();
    }
}
