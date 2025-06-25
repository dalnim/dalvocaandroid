package com.dalread.activity;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.Nullable;

import com.dalread.R;
import com.dalread.base.VocaActivity;
import com.dalread.dialog.AlertDialog;
import com.dalread.model.User;
import com.dalread.network.DalApiListener;
import com.dalread.network.events.BaseEvent;
import com.dalread.network.events.SuccessEvent;
import com.dalread.util.BaseVoca;
import com.dalread.util.Constant;
import com.dalread.util.Loading;
import com.dalread.util.Utils;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.OnTextChanged;

public class DescByMeActivity extends VocaActivity {

    @BindView(R.id.et_desc) EditText etDesc;

    private Context context;
    private User user;
    private byte[] rawUser;
    private AlertDialog alertDialog;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_desc_by_me;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
        initLayout();
        bindData();
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
        saveData();
    }

    private void initData() {
        context = this;
        user = (User) getIntent().getSerializableExtra(Constant.BUNDLE.KEY_PROFILE);
        rawUser = BaseVoca.backupObject(user);
    }

    private void initLayout() {
        toolbar.hideTvRight();
        alertDialog = new AlertDialog(context);
    }

    private void bindData() {
        if (user != null) {
            String desc = user.getOponentDescByMe();
            etDesc.setText(desc);
            if (!TextUtils.isEmpty(desc)) {
                etDesc.setSelection(desc.length());
            }
            etDesc.postDelayed(new Runnable() {

                @Override
                public void run() {
                    Utils.showSoftKeyboard(context, etDesc);
                }
            }, Constant.ON_RESUME_DELAY);
        }
    }

    @OnTextChanged(value = R.id.et_desc, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    void onDescChanged(Editable editable) {
        if (user != null) {
            String desc = editable.toString();
            user.setOponentDescByMe(desc);
            checkDataChanged();
        }
    }

    private void checkDataChanged() {
        boolean changed = !Arrays.equals(rawUser, BaseVoca.backupObject(user));
        toolbar.getTvRight().setVisibility(changed ? View.VISIBLE : View.GONE);
    }

    private void saveData() {
        if (user != null) {
            int uid = getUserID();
            if (uid > 0) {
                if (Utils.isConnected(context)) {
                    Loading.show(context);
                    application.getDalAiImpl().updateOpponentNameByMe(
                            user.getUid(),
                            user.getOponentNameByMe(),
                            user.getOponentDescByMe(),
                            new DalApiListener<Boolean>() {

                                @Override
                                public void onSuccess(Boolean response) {
                                    Loading.hide();
                                    if (response) {
                                        application.getEventBus().post(new SuccessEvent(BaseEvent.Screen.MAIN, BaseEvent.EventType.DATA_CHANGED, user));
                                        onBackPressed();
                                    }
                                }

                                @Override
                                public void onFailure(String error) {
                                    Loading.hide();
                                }
                            });
                } else {
                    alertDialog.showNoInternet();
                }
            } else {
                alertDialog.showLogInRequired();
            }
        }
    }
}
