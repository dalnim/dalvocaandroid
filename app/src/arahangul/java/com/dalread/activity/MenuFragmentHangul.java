package com.dalread.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.dalread.R;
import com.dalread.base.BaseConvFragment;
import com.dalread.databinding.FragmentMainMenuBinding;
import com.dalread.model.VocaBook;
import com.dalread.network.events.SuccessEvent;
import com.dalread.util.Constant;
import com.dalread.util.Utils;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

public class MenuFragmentHangul extends BaseConvFragment implements View.OnClickListener {
    private List<VocaBook> vocaBooks;
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
        vocaBooks = new ArrayList<>();
        getData();
    }

    private void initLayout() {
        binding.vPracticeHangulWriting.setVisibility(View.GONE);
        binding.vPracticeHangulTyping.setVisibility(View.GONE);
    }

    private void getData() {
        vocaBooks = activity.subDatabase.getServerVocaBookListByCategoryForHangulWriting();
    }
    private void setOnClickListeners() {
        binding.vPracticeHangulWriting.setOnClickListener(this);
        binding.vPracticeHangulTyping.setOnClickListener(this);

        binding.vConsonants.setOnClickListener(this);
        binding.vDoubleConsonants.setOnClickListener(this);
        binding.vVowels.setOnClickListener(this);
        binding.vCombinationVowels.setOnClickListener(this);
        binding.vCombineConsonantsandVowels.setOnClickListener(this);
        binding.vFinalConsonants.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.vConsonants:
                openStudyWritingScreen(0);
                break;
            case R.id.vDoubleConsonants:
                openStudyWritingScreen(1);
                break;
            case R.id.vVowels:
                openStudyWritingScreen(2);
                break;
            case R.id.vCombinationVowels:
                openStudyWritingScreen(3);
                break;
            case R.id.vCombineConsonantsandVowels:
                openSubScreen(4);
                break;
            case R.id.vFinalConsonants:
                openStudyWritingScreen(5);
                break;

            case R.id.vPracticeHangulTyping:
                openWordbookListScreenByBookId();
//                openPracticeTypingScreen();
                break;
            case R.id.vPracticeHangulWriting:
                openPracticeHandWritingScreen();
                break;
            default:
                break;
        }
    }

    private void openStudyWritingScreen(int pos) {
        if (Utils.isIndexInsideRange(vocaBooks, pos)) {
            Intent intent = new Intent(getContext(), AraConvStudyHandWritingHangulActivity.class);
            intent.putExtra(Constant.BUNDLE.KEY_VOCA_BOOK, vocaBooks.get(pos));
            openNewScreen(intent);
        }
    }

    private void openSubScreen(int pos) {
        if (Utils.isIndexInsideRange(vocaBooks, pos)) {
            Intent intent = new Intent(getContext(), AraConvWordbooksHangulSubActivity.class);
            intent.putExtra(Constant.BUNDLE.KEY_VOCA_BOOK, vocaBooks.get(pos));
            openNewScreen(intent);
        }
    }

    private void openWordbookListScreenByBookId() {
        openNewScreen(
                WordbookListActivity.createIntent(activity)
        );
    }

    private void openPracticeTypingScreen() {
        openNewScreen(
                AraConvPracticeTypingHangulActivity.createIntent(activity)
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
