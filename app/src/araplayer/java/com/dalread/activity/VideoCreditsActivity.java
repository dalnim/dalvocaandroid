package com.dalread.activity;

import android.view.View;
import android.widget.ImageView;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.dalread.R;
import com.dalread.adapter.VideoCreditAdapter;
import com.dalread.asyntask.OnAsyncTaskListener;
import com.dalread.base.BasePlayerActivity;
import com.dalread.component.CenterLayoutManager;
import com.dalread.component.SeparatorDecoration;
import com.dalread.component.Toolbar;
import com.dalread.databinding.ActivityVideoCreditsBinding;
import com.dalread.dialog.ZoomedPhotoDialog;
import com.dalread.listener.OnClickListener;
import com.dalread.model.VideoInformationModel;
import com.dalread.util.BaseBindUtils;
import com.dalread.util.Constant;
import com.dalread.util.Loading;
import com.dalread.util.Utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class VideoCreditsActivity extends BasePlayerActivity implements OnAsyncTaskListener {
    private VideoInformationModel data;
    private ArrayList<VideoInformationModel.People> items;
    private VideoCreditAdapter adapter;

    private final int TYPE_INIT_DATA = 0;

    private ActivityVideoCreditsBinding binding;

    @Override
    protected View getContentView() {
        binding = ActivityVideoCreditsBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        return view;
    }

    @Override
    protected Toolbar getToolbar() {
        return null;
    }

    @Override
    protected void setFullscreen() {

    }
    @Override
    public void onBackPressed() {
//        if (binding.ivZoomedPhoto.getVisibility() == View.VISIBLE) {
//            binding.ivZoomedPhoto.setVisibility(View.GONE);
//            return;
//        }
        super.onBackPressed();
    }
    @Override
    public void initView() {
        data = getIntent().getParcelableExtra(Constant.PLAYER.INTENT.KEY_DATA);
        adapter = new VideoCreditAdapter(this, onClickListener);
        binding.rvList.setAdapter(adapter);
        LinearLayoutManager mLayoutManager = new CenterLayoutManager(this);
        binding.rvList.setLayoutManager(mLayoutManager);
        binding.rvList.addItemDecoration(new SeparatorDecoration(this, BaseBindUtils.getDividerColor(), BaseBindUtils.getDividerHeight(this)));
        initData();
    }

    @Override
    public void initData() {
        callAsyncTask(this, TYPE_INIT_DATA);
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

    private OnClickListener onClickListener = (view, object) -> {
        final VideoInformationModel.People item = (VideoInformationModel.People) object;
        switch (view.getId()) {
            case R.id.llItem:
                Utils.openWeb(this, item.getPersonURL());
                break;
            case R.id.ivProfile:
                if (!Utils.isEmpty(item.getPath())) {
//                    binding.ivZoomedPhoto.setVisibility(View.VISIBLE);
//                    UtilImage.getThumbnailFullsize(this, binding.ivZoomedPhoto, item.getOriginalSizeImagePath());
                    ZoomedPhotoDialog zoomedPhotoDialog = new ZoomedPhotoDialog(this, playerFileModel);
                    zoomedPhotoDialog.loadThumbnailFromPath(item.getOriginalSizeImagePath(), ((ImageView) view).getDrawable());
                }
                break;
        }
    };

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
                return generateData();
        }
        return null;
    }

    @Override
    public void onFinishAsyncTask(int searchType, Object resultData, Object data) {
        switch (searchType) {
            case TYPE_INIT_DATA:
                items = new ArrayList<>();
                items.addAll((Collection<? extends VideoInformationModel.People>) resultData);
                adapter.setData(items);
                Loading.hide();
                break;
        }
    }

    private List<VideoInformationModel.People> generateData() {
        ArrayList<VideoInformationModel.People> items = new ArrayList<>();
        // Director
        if (!data.getDirector().isEmpty()) {
            items.add(new VideoInformationModel.People(getString(R.string.director)));
            items.addAll(data.getDirector());
        }
        // Cast
        if (!data.getCredits().getCast().isEmpty()) {
            items.add(new VideoInformationModel.People(getString(R.string.cast)));
            items.addAll(data.getCredits().getCast());
        }
        // Guest stars
        if (!data.getCredits().getGuestStars().isEmpty()) {
            items.add(new VideoInformationModel.People(getString(R.string.guest_stars)));
            items.addAll(data.getCredits().getGuestStars());
        }
        return items;
    }
}
