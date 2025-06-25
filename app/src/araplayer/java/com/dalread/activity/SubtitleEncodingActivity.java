package com.dalread.activity;

import android.os.Handler;
import android.view.View;

import com.dalread.R;
import com.dalread.adapter.SubtitleEncodingPlayerAdapter;
import com.dalread.asyntask.OnAsyncTaskListener;
import com.dalread.base.BasePlayerActivity;
import com.dalread.component.CenterLayoutManager;
import com.dalread.component.SeparatorDecoration;
import com.dalread.component.Toolbar;
import com.dalread.databinding.ActivitySubtitleEncodingPlayerBinding;
import com.dalread.listener.OnClickListener;
import com.dalread.model.SubtitleEncodingModel;
import com.dalread.util.BaseBindUtils;
import com.dalread.util.Constant;
import com.dalread.util.DLog;
import com.dalread.util.Loading;
import com.dalread.util.SubtitleUtil;
import com.dalread.util.Utils;

import java.util.List;

import me.zhanghai.android.fastscroll.FastScrollerBuilder;

public class SubtitleEncodingActivity extends BasePlayerActivity implements OnAsyncTaskListener {
    private SubtitleEncodingPlayerAdapter adapter;
    private List<SubtitleEncodingModel> listItems;
    private String content;

    private ActivitySubtitleEncodingPlayerBinding binding;

    @Override
    protected View getContentView() {
        binding = ActivitySubtitleEncodingPlayerBinding.inflate(getLayoutInflater());
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
        Loading.show(this);
        new FastScrollerBuilder(binding.fssvPreview).build();
        adapter = new SubtitleEncodingPlayerAdapter(onItemClickListener);
        binding.rvList.setNestedScrollingEnabled(false);
        binding.rvList.setLayoutManager(new CenterLayoutManager(this));
        binding.rvList.addItemDecoration(new SeparatorDecoration(this, BaseBindUtils.getDividerColor(), BaseBindUtils.getDividerHeight(this)));
        binding.rvList.setAdapter(adapter);
        new Handler().postDelayed(() -> initData(), 500);
    }

    @Override
    public void initData() {
        playerFileModel = getIntent().getParcelableExtra(Constant.PLAYER.INTENT.KEY_VIDEO_FILE);
        listItems = SubtitleUtil.getSubtitleEncodingList(playerFileModel.getVideoModel().getSubtitleEncoding());
        adapter.setData(listItems, playerFileModel.getVideoModel().getSubtitleEncodingIndex());
        callAsyncTask(this);
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

    }

    @Override
    public void onHeaderTextRightClick() {

    }

    private OnClickListener onItemClickListener = (view, object) -> {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Loading.show(SubtitleEncodingActivity.this);
                int position = (int) object;
                DLog.d(getLogTag(), "onItemClick - position=" + position + " - value=" + listItems.get(position).getName());
                playerFileModel.getVideoModel().setSubtitleEncodingIndex(position);
                playerFileModel.getVideoModel().setSubtitleEncoding(listItems.get(position).getName());
                updateVideoModel(playerFileModel.getVideoModel());
                callAsyncTask(SubtitleEncodingActivity.this);
            }
        });
    };

    private void updateSubtitleEncoding(String content) {
        this.content = content;
        if (Utils.isEmpty(content)) {
            binding.tvContent.setText(R.string.subtile_encoding_parser_error);
        }
        runOnUiThread(() -> {
            binding.tvContent.setText(content);
            new Handler().postDelayed(() -> Loading.hide(), 100);
        });
    }

    @Override
    public void onInitAsyncTask() {

    }

    @Override
    public void onPrevExecuteAsyncTask() {

    }

    @Override
    public Object onPostExecuteAsyncTask(int searchType, Object data) {
        return "";//SubtitleUtil.parserContentSubTitle(playerFileModel, playerFileModel.getVideoModel().getSubPathIndex());
    }

    @Override
    public void onFinishAsyncTask(int searchType, Object resultData, Object data) {
        updateSubtitleEncoding((String) resultData);
    }
}
