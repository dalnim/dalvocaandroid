package com.dalread.activity;

import android.app.Activity;
import android.content.Intent;
import android.view.View;

import com.dalread.R;
import com.dalread.base.BasePlayerActivity;
import com.dalread.component.Toolbar;
import com.dalread.database.ServerModelQuery;
import com.dalread.databinding.ActivityPlayerServerBinding;
import com.dalread.model.ServerModel;
import com.dalread.model.ServerTypeModel;
import com.dalread.util.Constant;
import com.dalread.util.Utils;
import com.dalread.util.Voca;

public class ServerPlayerActivity extends BasePlayerActivity {
    private ServerTypeModel serverTypeModel;
    private ServerModel serverModel;
    private String[] serverNames;

    private ActivityPlayerServerBinding binding;
    @Override
    protected View getContentView() {
        binding = ActivityPlayerServerBinding.inflate(getLayoutInflater());
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
        serverNames = getResources().getStringArray(R.array.player_server);
        final Intent intent = getIntent();
        if (intent.hasExtra((Constant.PLAYER.INTENT.KEY_TYPE))) {
            serverTypeModel = intent.getParcelableExtra(Constant.PLAYER.INTENT.KEY_TYPE);
        }

        if (intent.hasExtra((Constant.PLAYER.INTENT.KEY_DATA))) {
            serverModel = intent.getParcelableExtra(Constant.PLAYER.INTENT.KEY_DATA);
        }
        updateLayoutData();
        initData();
    }

    @Override
    public void initData() {

    }

    private void updateLayoutData() {
        runOnUiThread(() -> {
            if (serverTypeModel != null) {
                binding.header.setTitle(serverTypeModel.getName());
            }
            if (serverModel != null) {
                binding.header.setTitle(serverNames[serverModel.getType()]);
                binding.etTitle.setText(serverModel.getTitle());
                binding.etHost.setText(serverModel.getHost());
                binding.etId.setText(serverModel.getAccount());
                binding.etPassword.setText(serverModel.getPassword());
                binding.etPort.setText(serverModel.getPort());
            }
        });
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
        runOnUiThread(() -> handleSave());
    }

    private void handleSave() {
        final String title = binding.etTitle.getText().toString().trim();
        final String host = binding.etHost.getText().toString().trim();
        final String id = binding.etId.getText().toString().trim();
        final String password = binding.etPassword.getText().toString().trim();
        final String port = binding.etPort.getText().toString().trim();

        if (Utils.isEmpty(title) || Utils.isEmpty(host) ||
                Utils.isEmpty(id) || Utils.isEmpty(password) ||
                Utils.isEmpty(port)) {
            return;
        }

        if (serverModel == null) {
            serverModel = new ServerModel();
            serverModel.setType(serverTypeModel.getType());
        }
        serverModel.setTitle(title);
        serverModel.setHost(host);
        serverModel.setAccount(id);
        serverModel.setPassword(password);
        serverModel.setPort(port);
        ServerModelQuery.add(Voca.getRealm(), serverModel);
        alertDialog.show(R.string.error_msg_word_change_success, 0, (dialog, which) -> {
            dialog.dismiss();
            if (serverTypeModel != null) {
                Intent returnIntent = new Intent();
                setResult(Activity.RESULT_OK, returnIntent);
            }
            finish();
        });
    }
}
