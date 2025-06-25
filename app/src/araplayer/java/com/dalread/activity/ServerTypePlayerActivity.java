package com.dalread.activity;

import android.app.Activity;
import android.content.Intent;
import android.view.View;

import androidx.annotation.Nullable;

import com.dalread.R;
import com.dalread.adapter.ServerTypePlayerAdapter;
import com.dalread.base.BasePlayerActivity;
import com.dalread.component.CenterLayoutManager;
import com.dalread.component.SeparatorDecoration;
import com.dalread.component.Toolbar;
import com.dalread.databinding.ActivityPlayerServerTypeBinding;
import com.dalread.listener.OnClickListener;
import com.dalread.model.ServerTypeModel;
import com.dalread.util.BaseBindUtils;
import com.dalread.util.Constant;
import com.dalread.util.DLog;
import com.dalread.util.Utils;

import java.util.ArrayList;
import java.util.List;

public class ServerTypePlayerActivity extends BasePlayerActivity {
    private ServerTypePlayerAdapter adapter;
    private List<ServerTypeModel> list;
    private final int REQUEST_CODE_SERVER_SCREEN = 100;
    private ActivityPlayerServerTypeBinding binding;

    @Override
    protected View getContentView() {
        binding = ActivityPlayerServerTypeBinding.inflate(getLayoutInflater());
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
        adapter = new ServerTypePlayerAdapter(this, list, onServerAdapterClickListener);
        binding.rvContent.setAdapter(adapter);
        binding.rvContent.setLayoutManager(new CenterLayoutManager(this));
        binding.rvContent.addItemDecoration(new SeparatorDecoration(this, BaseBindUtils.getDividerColor(), BaseBindUtils.getDividerHeight(this)));
        initData();
    }

    @Override
    public void initData() {
        list = generateServerData();
        adapter.setData(list);
        adapter.notifyDataSetChanged();
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

    }

    private List<ServerTypeModel> generateServerData() {
        final List<ServerTypeModel> list = new ArrayList<>();
        final String[] arrayName = getResources().getStringArray(R.array.player_server);
        final int[] arrayType = new int[]{
                Constant.PLAYER.SERVER.TYPE.FTP, Constant.PLAYER.SERVER.TYPE.WEBDAV,
                Constant.PLAYER.SERVER.TYPE.SMB, Constant.PLAYER.SERVER.TYPE.DROPBOX};
        for (int i = 0; i < arrayName.length; i++) {
            list.add(new ServerTypeModel(arrayName[i], arrayType[i]));

            //Add only FTP for normal users.
            if (!Utils.isDebugOrAdminUser(this)) {
                break;
            }
        }

        return list;
    }

    private OnClickListener onServerAdapterClickListener = (view, object) -> {
        openServerPlayerScreen(object);
    };

    private void openServerPlayerScreen(Object data) {
        final ServerTypeModel item = (ServerTypeModel) data;
        DLog.d(getLogTag(), item.toString());
        Intent intent = new Intent(this, ServerPlayerActivity.class);
        intent.putExtra(Constant.PLAYER.INTENT.KEY_TYPE, item);
        startActivityForResult(intent, REQUEST_CODE_SERVER_SCREEN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SERVER_SCREEN) {
            if (resultCode == Activity.RESULT_OK) {
                finish();
            }
        }
    }
}
