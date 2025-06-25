package com.dalread.base;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.dalread.BaseApplication;
import com.dalread.database.SharedPreferencesDB;

import butterknife.ButterKnife;
import butterknife.Unbinder;

public abstract class BaseVocaFragment extends Fragment {

    private Unbinder unbinder;
    protected BaseApplication application;
    protected SharedPreferencesDB sharedPreferences;

    protected abstract @LayoutRes
    int getContentViewId();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);

        application = (BaseApplication) getActivity().getApplication();
        sharedPreferences = SharedPreferencesDB.getInstance(application.getApplicationContext());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(getContentViewId(), container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        /*Fragments have a different view lifecycle than activities.
        When binding a fragment in onCreateView, set the views to null in onDestroyView.
        Butter Knife returns an Unbinder instance when you call bind to do this for you.
        Call its unbind method in the appropriate lifecycle callback.*/
        unbinder.unbind();
    }

    protected String getLogTag() {
        return getClass().getSimpleName();
    }
}
