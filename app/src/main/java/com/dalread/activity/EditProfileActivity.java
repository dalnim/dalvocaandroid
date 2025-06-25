package com.dalread.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.dalread.R;
import com.dalread.base.BaseDialog;
import com.dalread.base.BaseDialogListener;
import com.dalread.base.BaseVocaActivity;
import com.dalread.base.EnumType;
import com.dalread.dialog.AlertDialog;
import com.dalread.dialog.SingleChoiceDialog;
import com.dalread.dialog.TypeInputDialog;
import com.dalread.listener.OnClickDialogListener;
import com.dalread.model.User;
import com.dalread.network.DalApiListener;
import com.dalread.network.events.BaseEvent;
import com.dalread.network.events.SuccessEvent;
import com.dalread.util.BaseVoca;
import com.dalread.util.Constant;
import com.dalread.util.Loading;
import com.dalread.util.Utils;

import org.greenrobot.eventbus.Subscribe;

import java.util.Arrays;

import butterknife.BindArray;
import butterknife.BindView;
import butterknife.OnClick;

public class EditProfileActivity extends BaseVocaActivity {

    @BindView(R.id.tv_name) TextView tvName;
    @BindView(R.id.tv_nation) TextView tvNation;
    @BindView(R.id.tv_city) TextView tvCity;
    @BindView(R.id.tv_gender) TextView tvGender;
    @BindView(R.id.tv_age) TextView tvAge;
    @BindView(R.id.tv_bio) TextView tvBio;

    @BindArray(R.array.gender_options) String[] genders;
    @BindArray(R.array.age_options) String[] ages;

    private Context context;
    private User user;
    private byte[] rawUser;
    private String[] nations;
    private int nationPos;
    private int genderPos;
    private int agePos;
    private AlertDialog alertDialog;
    private TypeInputDialog nameDialog;
    private TypeInputDialog cityDialog;
    private SingleChoiceDialog singleChoiceDialog;
    private boolean dataChanged;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_edit_profile;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
        initLayout();
        initEventBus();
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
        nations = BaseVoca.getCountryNameList(context);
        nationPos = -1;
        genderPos = -1;
        agePos = -1;
    }

    private void initLayout() {
        toolbar.hideTvRight();
        alertDialog = new AlertDialog(context);
        nameDialog = new TypeInputDialog(context, onNameListener);
        nameDialog.setTitle(R.string.name);
        nameDialog.setSubTitle("");
        cityDialog = new TypeInputDialog(context, onCityListener);
        cityDialog.setTitle(R.string.city);
        cityDialog.setSubTitle("");
        singleChoiceDialog = new SingleChoiceDialog(context);
    }

    private void bindData() {
        if (user != null) {
            // name
            String name = user.getName();
            tvName.setText(name);
            // nation
            String nation = BaseVoca.getCountryName(context, user.getNation());
            tvNation.setText(nation);
            for (int i = 0; i < nations.length; i++) {
                if (nations[i].equals(nation)) {
                    nationPos = i;
                    break;
                }
            }
            // city
            String city = user.getCity();
            tvCity.setText(city);
            // gender
            String gender = getString(user.getSex() == Constant.SEX.MALE ? R.string.male : R.string.female);
            tvGender.setText(gender);
            for (int i = 0; i < genders.length; i++) {
                if (genders[i].equals(gender)) {
                    genderPos = i;
                    break;
                }
            }
            // age
            String age = getString(user.getAge() == Constant.AGE.TEENAGER ? R.string.teenager : R.string.adult);
            tvAge.setText(age);
            for (int i = 0; i < ages.length; i++) {
                if (ages[i].equals(age)) {
                    agePos = i;
                    break;
                }
            }
            // bio
            String bio = user.getBio();
            tvBio.setText(bio);
        }
    }

    @Subscribe
    public void onEvent(SuccessEvent successEvent) {
        if (successEvent.getScreen() == BaseEvent.Screen.MAIN) {
            BaseEvent.EventType type = successEvent.getEventType();
            if (type == BaseEvent.EventType.DATA_CHANGED) {
                dataChanged = true;
                user = (User) successEvent.getModel();
                rawUser = BaseVoca.backupObject(user);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (dataChanged) {
            dataChanged = false;
            bindData();
        }
    }

    @Override
    protected void onDestroy() {
        unRegisterEventBus();
        super.onDestroy();
    }

    @OnClick({R.id.v_name, R.id.v_nation, R.id.v_city, R.id.v_gender, R.id.v_age, R.id.v_bio})
    void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.v_name:
                if (user != null) {
                    String name = user.getName();
                    nameDialog.setInput(name);
                    nameDialog.show();
                }
                break;
            case R.id.v_nation:
                if (user != null) {
                    singleChoiceDialog.show(
                            R.string.nation,
                            nations,
                            nationPos,
                            R.string.ok,
                            R.string.cancel,
                            onNationListener
                    );
                }
                break;
            case R.id.v_city:
                if (user != null) {
                    String city = user.getCity();
                    cityDialog.setInput(city);
                    cityDialog.show();
                }
                break;
            case R.id.v_gender:
                if (user != null) {
                    singleChoiceDialog.show(
                            R.string.gender,
                            genders,
                            genderPos,
                            R.string.ok,
                            R.string.cancel,
                            onGenderListener
                    );
                }
                break;
            case R.id.v_age:
                if (user != null) {
                    singleChoiceDialog.show(
                            R.string.age,
                            ages,
                            agePos,
                            R.string.ok,
                            R.string.cancel,
                            onAgeListener
                    );
                }
                break;
            case R.id.v_bio:
                if (user != null) {
                    openBioScreen();
                }
                break;
            default:
                break;
        }
    }

    private BaseDialogListener onNameListener = new BaseDialogListener() {

        @Override
        public void onBaseDialogListenerShow(EnumType type, BaseDialog currentDialog, View v, int position, Object data) {
        }

        @Override
        public void onBaseDialogListenerOk(EnumType type, BaseDialog dialog, View v, int position, Object data) {
            String name = (String) data;
            tvName.setText(name);

            user.setName(name);
            checkDataChanged();

            nameDialog.dismiss();
        }

        @Override
        public void onBaseDialogListenerCancel(EnumType type, BaseDialog dialog, View v, int position, Object data) {
        }

        @Override
        public void onBaseDialogListenerClick(EnumType type, BaseDialog dialog, View v, int position, Object data) {
        }

        @Override
        public void onBaseDialogListenerClickMulti(EnumType type, BaseDialog dialog, View v, int position, Object[] data) {
        }
    };


    private OnClickDialogListener onNationListener = new OnClickDialogListener() {

        @Override
        public void onClick(View view, Object object) {
            final int which = (int) object;
            if (which != nationPos) {
                String nation = nations[nationPos = which];
                tvNation.setText(nation);

                String countryCode = BaseVoca.getCountryCode(context, nation);
                user.setNation(countryCode);
                checkDataChanged();
            }
        }

        @Override
        public void onDismiss(View view, Object object) {

        }
    };

    private BaseDialogListener onCityListener = new BaseDialogListener() {

        @Override
        public void onBaseDialogListenerShow(EnumType type, BaseDialog currentDialog, View v, int position, Object data) {
        }

        @Override
        public void onBaseDialogListenerOk(EnumType type, BaseDialog dialog, View v, int position, Object data) {
            String city = (String) data;
            tvCity.setText(city);

            user.setCity(city);
            checkDataChanged();

            cityDialog.dismiss();
        }

        @Override
        public void onBaseDialogListenerCancel(EnumType type, BaseDialog dialog, View v, int position, Object data) {
        }

        @Override
        public void onBaseDialogListenerClick(EnumType type, BaseDialog dialog, View v, int position, Object data) {
        }

        @Override
        public void onBaseDialogListenerClickMulti(EnumType type, BaseDialog dialog, View v, int position, Object[] data) {
        }
    };


    private OnClickDialogListener onGenderListener = new OnClickDialogListener() {

        @Override
        public void onClick(View view, Object object) {
            final int which = (int) object;
            if (which != genderPos) {
                tvGender.setText(genders[genderPos = which]);

                user.setSex(genderPos == 0 ? Constant.SEX.MALE : Constant.SEX.FEMALE);
                checkDataChanged();
            }
        }

        @Override
        public void onDismiss(View view, Object object) {

        }
    };


    private OnClickDialogListener onAgeListener = new OnClickDialogListener() {

        @Override
        public void onClick(View view, Object object) {
            final int which = (int) object;
            if (which != agePos) {
                tvAge.setText(ages[agePos = which]);

                user.setAge(agePos == 0 ? Constant.AGE.TEENAGER : Constant.AGE.ADULT);
                checkDataChanged();
            }
        }

        @Override
        public void onDismiss(View view, Object object) {

        }
    };

    private void openBioScreen() {
        Intent intent = new Intent(context, BioActivity.class);
        intent.putExtra(Constant.BUNDLE.KEY_PROFILE, user);
        openNewScreen(intent);
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
                    application.getDalAiImpl().updateProfileInfo(user, new DalApiListener<Boolean>() {

                        @Override
                        public void onSuccess(Boolean response) {
                            Loading.hide();
                            if (response) {
                                application.getEventBus().post(new SuccessEvent(BaseEvent.Screen.MAIN, BaseEvent.EventType.DATA_CHANGED, null));
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
