package com.dalread.base;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.IdRes;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.dalread.BaseApplication;
import com.dalread.R;
import com.dalread.database.SharedPreferencesDB;
import com.dalread.listener.OnOpenNewScreen;

import org.greenrobot.eventbus.EventBus;

//Dalnim Create. Don't use ButterKnife but use viewBinding
public abstract class BaseFragment extends Fragment {
    protected EventBus eventBus;
//    private Unbinder unbinder;
    protected BaseApplication application;
    protected SharedPreferencesDB sharedPreferences;

//    protected abstract @LayoutRes int getContentViewId();
    protected abstract View getContentView();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);
        initEventBus();
        application = (BaseApplication) getActivity().getApplication();
        sharedPreferences = application.getSharedPref();// SharedPreferencesDB.getInstance(application.getApplicationContext());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        View view = inflater.inflate(getContentViewId(), container, false);
//        unbinder = ButterKnife.bind(this, view);
//        return view;
        return getContentView();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unRegisterEventBus();

        /*Fragments have a different view lifecycle than activities.
        When binding a fragment in onCreateView, set the views to null in onDestroyView.
        Butter Knife returns an Unbinder instance when you call bind to do this for you.
        Call its unbind method in the appropriate lifecycle callback.*/
//        unbinder.unbind();
    }

    protected String getLogTag() {
        return getClass().getSimpleName();
    }

    protected @IdRes int getFragmentContainerId() {
        return R.id.fragment_container;
    }

    protected void openNewScreen(Intent i) {
        if (this instanceof OnOpenNewScreen) {
            ((OnOpenNewScreen) this).onOpen();
        }
        startActivity(i);
    }

    private void initEventBus() {
        eventBus = EventBus.getDefault();
        if (!eventBus.isRegistered(this)) {
            eventBus.register(this);
        }
    }

    private void unRegisterEventBus() {
        if (eventBus != null && eventBus.isRegistered(this)) {
            eventBus.unregister(this);
        }
    }
}
