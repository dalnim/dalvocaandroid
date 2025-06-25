package com.dalread.activity;

import android.content.Intent;
import android.view.View;

import com.dalread.R;
import com.dalread.adapter.ServerPlayerAdapter;
import com.dalread.base.BaseMainPlayerFragment;
import com.dalread.component.CenterLayoutManager;
import com.dalread.component.SeparatorDecoration;
import com.dalread.database.ServerModelQuery;
import com.dalread.databinding.FragmentNetworkPlayerBinding;
import com.dalread.listener.OnClickListener;
import com.dalread.model.ServerModel;
import com.dalread.util.BaseBindUtils;
import com.dalread.util.Constant;
import com.dalread.util.DLog;
import com.dalread.util.Voca;

import java.util.ArrayList;
import java.util.List;

public class MainPlayerNetworkFragment extends BaseMainPlayerFragment {
    private ServerPlayerAdapter adapter;
    private List<ServerModel> list = new ArrayList<>();

    private FragmentNetworkPlayerBinding binding;

    @Override
    protected View getContentView() {
        binding = FragmentNetworkPlayerBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }


    @Override
    public void initView() {
        adapter = new ServerPlayerAdapter(activity, list, onServerClickListener);
        binding.rvContent.setAdapter(adapter);
        binding.rvContent.setLayoutManager(new CenterLayoutManager(activity));
        binding.rvContent.addItemDecoration(new SeparatorDecoration(activity, BaseBindUtils.getDividerColor(), BaseBindUtils.getDividerHeight(activity)));
    }

    @Override
    public void onResume() {
        super.onResume();
        initData();
    }

    @Override
    public void initData() {
        final List<ServerModel> tmp = ServerModelQuery.getAll(Voca.getRealm());
        if (tmp != null && !tmp.isEmpty()) {
            list.clear();
            for (ServerModel s : tmp) {
                list.add(new ServerModel(s));
            }
            ServerModel downloadFolder = new ServerModel(System.currentTimeMillis() - 1000, getString(R.string.download_status), "", "", "", "", Constant.PLAYER.SERVER.TYPE.DOWNLOAD);
            list.add(0, downloadFolder);
            adapter.setData(list);
            adapter.notifyDataSetChanged();
        }
    }

    private OnClickListener onServerClickListener = (view, object) -> {
        ServerModel serverModel = (ServerModel) object;
        switch (view.getId()) {
            case R.id.llItem:
                if (serverModel.getType() == Constant.PLAYER.SERVER.TYPE.DOWNLOAD) {
                    activity.openMainNetworkDownloadFragment(serverModel);
                } else {
                    activity.openMainNetworkItemFragment(serverModel);
                }

                break;
            case R.id.ivInfo:
                openServerPlayerScreen(serverModel);
                break;
        }
    };

    private void openServerPlayerScreen(ServerModel data) {
        DLog.d(getLogTag(), "openServerPlayerScreen");
        DLog.d(getLogTag(), data.toString());
        Intent intent = new Intent(activity, ServerPlayerActivity.class);
        intent.putExtra(Constant.PLAYER.INTENT.KEY_DATA, data);
        startActivity(intent);
    }
}
