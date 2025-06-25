package com.dalread.base;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.dalread.AraPlayerApplication;
import com.dalread.database.SharedPreferencesDB;
import com.dalread.util.DLog;

import org.greenrobot.eventbus.EventBus;

public abstract class BasePlayerFragment extends Fragment implements IPlayerActivity {
    protected Handler handler = new Handler(Looper.getMainLooper());
    protected AraPlayerApplication application;
    protected SharedPreferencesDB sharedPreferences;
    public EventBus eventBus;

    protected abstract View getContentView();

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if (context instanceof Activity) {
            application = (AraPlayerApplication) ((Activity) context).getApplication();
            sharedPreferences = SharedPreferencesDB.getInstance(application.getApplicationContext());
        } else {
            DLog.d("BasePlayerFragment onAttach", "getActivity() is null");
        }
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);
        if (getActivity() == null) {
            DLog.d("BasePlayerFragment onCreate", "getActivity() is null");
        }
//        application = (AraPlayerApplication) getActivity().getApplication();
//        sharedPreferences = SharedPreferencesDB.getInstance(application.getApplicationContext());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = getContentView();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
    }

    @Override
    public void onDestroyView() {
        unRegisterEventBus();
        super.onDestroyView();
    }

    protected String getLogTag() {
        return getClass().getSimpleName();
    }

    protected void initEventBus() {
        eventBus = EventBus.getDefault();
        if (!eventBus.isRegistered(this)) {
            eventBus.register(this);
        }
    }

    protected void unRegisterEventBus() {
        if (eventBus != null && eventBus.isRegistered(this)) {
            eventBus.unregister(this);
        }
    }

    protected void rotateScreenToLandScape() {
        //Need to set Reverse Landscape first to make it default
        applyUpdatedRotation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);

        rotateScreenToSensorLandscape();
    }

    private void rotateScreenToSensorLandscape() {
        applyUpdatedRotation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
    }

    protected void rotateScreenToPortrait() {
        applyUpdatedRotation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    protected void applyUpdatedRotation(int requestOrientation) {
        if (getActivity() != null) {
            getActivity().setRequestedOrientation(requestOrientation);
        } else {
            DLog.d("BasePlayerFragment applyUpdatedRotation", "getActivity() is null");
        }
    }

    protected boolean isShowAdvancedMode() {
        return sharedPreferences.isShowAdvancedMode();
    }
}
