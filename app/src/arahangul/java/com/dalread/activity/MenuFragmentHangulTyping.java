package com.dalread.activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.dalread.R;
import com.dalread.base.BaseConvFragment;
import com.dalread.databinding.FragmentMainMenuBinding;
import com.dalread.network.events.SuccessEvent;

import org.greenrobot.eventbus.Subscribe;

public class MenuFragmentHangulTyping extends BaseConvFragment implements View.OnClickListener {
    private MainHomeActivity activity;
    private FragmentMainMenuBinding binding;

    @Override
    protected View getContentView() {
        binding = FragmentMainMenuBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        return view;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        activity = (MainHomeActivity)getActivity();
        initData();
        initLayout();
        setOnClickListeners();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }


    private void initData() {
        getData();
    }

    private void initLayout() {
        binding.vConsonants.setVisibility(View.GONE);
        binding.vDoubleConsonants.setVisibility(View.GONE);
        binding.vVowels.setVisibility(View.GONE);
        binding.vCombinationVowels.setVisibility(View.GONE);
        binding.vCombineConsonantsandVowels.setVisibility(View.GONE);
        binding.vFinalConsonants.setVisibility(View.GONE);
    }
    private void getData() {

    }
    private void setOnClickListeners() {
        binding.vPracticeHangulWriting.setOnClickListener(this);
        binding.vPracticeHangulTyping.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.vPracticeHangulTyping:
                openWordbookListScreenByBookId();
                break;
            case R.id.vPracticeHangulWriting:
                openPracticeHandWritingScreen();
                break;
            default:
                break;
        }
    }

    private void openWordbookListScreenByBookId() {
        openNewScreen(
                WordbookListActivity.createIntent(activity)
        );
    }

    private void openPracticeHandWritingScreen() {
        openNewScreen(
                AraConvPracticeHandWritingHangulActivity.createIntent(activity)
        );
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Subscribe
    public void onEvent(SuccessEvent event) {

    }
}
