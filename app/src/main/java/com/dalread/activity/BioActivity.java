package com.dalread.activity;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.dalread.R;
import com.dalread.base.BaseVocaActivity;
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

public class BioActivity extends BaseVocaActivity {

    @BindView(R.id.et_bio) EditText etBio;
    @BindView(R.id.tv_bio) TextView tvBio;

    private Context context;
    private User user;
    private byte[] rawUser;
    private boolean readOnly;
    private AlertDialog alertDialog;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_bio;
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
        readOnly = getIntent().getBooleanExtra(Constant.BUNDLE.KEY_READ_ONLY, false);
    }

    private void initLayout() {
        toolbar.hideIconLeft();
        alertDialog = new AlertDialog(context);
        if (readOnly) {
            etBio.setVisibility(View.GONE);
            tvBio.setVisibility(View.VISIBLE);
            tvBio.setMovementMethod(new ScrollingMovementMethod());
            tvBio.setTextIsSelectable(true);
        } else {
            etBio.setVisibility(View.VISIBLE);
            tvBio.setVisibility(View.GONE);
        }
    }

    private void bindData() {
        if (user != null) {
            String bio = user.getBio();
            if (readOnly) {
                tvBio.setText(bio);
            } else {
                etBio.setText(bio);
                if (!TextUtils.isEmpty(bio)) {
                    etBio.setSelection(bio.length());
                }
                etBio.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        Utils.showSoftKeyboard(context, etBio);
                    }
                }, Constant.ON_RESUME_DELAY);
            }
        }
    }

    @OnTextChanged(value = R.id.et_bio, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    void onBioChanged(Editable editable) {
        if (user != null) {
            String bio = editable.toString();
            user.setBio(bio);
            checkDataChanged();
        }
    }

    private void checkDataChanged() {
        boolean changed = !Arrays.equals(rawUser, BaseVoca.backupObject(user));
        toolbar.getIconRight().setVisibility(changed ? View.VISIBLE : View.GONE);
    }

    private void saveData() {
        if (user != null) {
            int uid = getUserID();
            if (uid > 0) {
                if (Utils.isConnected(context)) {
                    Loading.show(context);
                    application.getDalAiImpl().updateProfileInfo(user, new DalApiListener<Boolean>() {

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
