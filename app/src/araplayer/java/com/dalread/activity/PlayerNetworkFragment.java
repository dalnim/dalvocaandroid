package com.dalread.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.dalread.R;
import com.dalread.adapter.ServerPlayerAdapter;
import com.dalread.base.BaseMainPlayerFragment;
import com.dalread.component.CenterLayoutManager;
import com.dalread.component.SeparatorDecoration;
import com.dalread.database.ServerModelQuery;
import com.dalread.databinding.FragmentNetworkPlayerBinding;
import com.dalread.listener.OnClickListener;
import com.dalread.model.ServerModel;
import com.dalread.util.Constant;
import com.dalread.util.DLog;
import com.dalread.util.Voca;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindColor;
import butterknife.BindDimen;
import butterknife.BindView;

public class PlayerNetworkFragment extends Fragment {

    private FragmentNetworkPlayerBinding binding;
    private ServerPlayerAdapter adapter;
    private List<ServerModel> list = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentNetworkPlayerBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initializeUI();
    }

    private void initializeUI() {
        adapter = new ServerPlayerAdapter(requireContext(), list, onServerClickListener);
        binding.rvContent.setAdapter(adapter);
        binding.rvContent.setLayoutManager(new CenterLayoutManager(requireContext()));
//        binding.rvContent.addItemDecoration(new SeparatorDecoration(requireContext(), getResources().getColor(R.color.color_divider), getResources().getDimensionPixelSize(R.dimen.divider_height)));
    }

    @Override
    public void onResume() {
        super.onResume();
        initData();
    }

    private void initData() {
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
                Activity activity = getActivity();
                if (activity instanceof PlayerNetworkActivity) {
                    PlayerNetworkActivity playerNetworkActivity = (PlayerNetworkActivity) activity;
                    if (serverModel.getType() == Constant.PLAYER.SERVER.TYPE.DOWNLOAD) {
                        playerNetworkActivity.openMainNetworkDownloadFragment(serverModel);
                    } else {
                        playerNetworkActivity.openMainNetworkItemFragment(serverModel);
                    }
                }


                break;
            case R.id.ivInfo:
                openServerPlayerScreen(serverModel);
                break;
        }
    };

    private void openServerPlayerScreen(ServerModel data) {
        Intent intent = new Intent(requireContext(), ServerPlayerActivity.class);
        intent.putExtra(Constant.PLAYER.INTENT.KEY_DATA, data);
        startActivity(intent);
    }
}
