package com.dalread.activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.dalread.R;
import com.dalread.adapter.WorkbooksAdapterCommon;
import com.dalread.base.BaseConvFragment;
import com.dalread.component.SeparatorDecoration;
import com.dalread.databinding.FragmentMainBasicBinding;
import com.dalread.listener.OnDoubleClickListener;
import com.dalread.listener.OnToolbarLeftButtonChangeListener;
import com.dalread.model.VocaBook;
import com.dalread.network.events.BaseEvent;
import com.dalread.network.events.SuccessEvent;
import com.dalread.util.Constant;
import com.dalread.util.UserUtil;
import com.dalread.util.Utils;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MenuFragmentWorkbook extends BaseConvFragment {
    protected WorkbooksAdapterCommon adapter;
    private VocaBook parentBook;
    private List<VocaBook> vocaBookList;
    private MainHomeActivity activity;
    private FragmentMainBasicBinding binding;

    @Override
    protected View getContentView() {
        binding = FragmentMainBasicBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        return view;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        activity = (MainHomeActivity)getActivity();
        initLayout();
        initData();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() instanceof OnToolbarLeftButtonChangeListener) {
            if (parentBook == null) {
                ((OnToolbarLeftButtonChangeListener) getActivity()).onChangeToMenu();
            } else {
                ((OnToolbarLeftButtonChangeListener) getActivity()).onChangeToBack();
            }
        }
    }


    private void initData() {
        vocaBookList = new ArrayList<>();
        parentBook = (VocaBook) getArguments().getSerializable(Constant.BUNDLE.KEY_VOCA_BOOK);
        getData();
        bindData();
    }

    private void getData() {
        if (parentBook == null) {
            vocaBookList = activity.subDatabase.getServerVocaBookListByCategory();
            filterVocaBookListForAdmin();
        } else {
            vocaBookList = activity.subDatabase.getServerVocaBookListByCategory((int) parentBook.getIBookId());
        }
    }

    private void filterVocaBookListForAdmin() {
        if (UserUtil.isAdminUser(activity)) {
            vocaBookList = vocaBookList.stream().filter( e->e.getUSED() >= Constant.VOCABOOKS.USED.USE_FOR_ADMIN).collect(Collectors.toList());
        } else {
            vocaBookList = vocaBookList.stream().filter( e->e.getUSED() >= Constant.VOCABOOKS.USED.USE_FOR_BUY).collect(Collectors.toList());
        }
    }

    private void bindData() {
        adapter.setBooks(vocaBookList);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }



    private void initLayout() {
        adapter = new WorkbooksAdapterCommon(getActivity(), onDoubleClickListener);
        binding.rvBooks.setAdapter(adapter);
        binding.rvBooks.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.rvBooks.addItemDecoration(new SeparatorDecoration(getActivity(), ContextCompat.getColor(getActivity(), R.color.color_divider), getActivity().getResources().getDimension(R.dimen.divider_height)));
    }
    private OnDoubleClickListener onDoubleClickListener = new OnDoubleClickListener() {
        @Override
        public void onClick(View view, Object data) {
            VocaBook vocaBook = (VocaBook) data;
            if (vocaBook.isIBookHasSubList()) {
                openChildBook(vocaBook);
//                vocaBookList = activity.subDatabase.getServerVocaBookListByCategory((int) item.getIBookId());
//                bindData();
            } else {
                checkToOpenVocaListView(vocaBook);
            }
        }

        @Override
        public void onDoubleClick(View view, Object data) {

        }
    };

    private void openVocaListView(VocaBook vocaBook) {
        openNewScreen(
                ConvVocaListActivity.createIntentByBookId(activity, Integer.parseInt(String.valueOf(vocaBook.getIBookId())))
        );
    }
    private void checkToOpenVocaListView(VocaBook vocaBook) {
        if (vocaBook.getIBookUsed() == Constant.VOCABOOKS.USED.USE_FOR_FREE) {
            openVocaListView(vocaBook);
        } else {
            if (UserUtil.isLoggedIn(activity, true)) {
                openVocaListView(vocaBook);
            }
        }
    }

    private void openChildBook(VocaBook parentBook) {
        if (getActivity() instanceof AppCompatActivity) {
            Fragment fragment = new MenuFragmentWorkbook();
            Bundle bundle = new Bundle();
            bundle.putSerializable(Constant.BUNDLE.KEY_VOCA_BOOK, parentBook);
            fragment.setArguments(bundle);
            Utils.loadFragment((AppCompatActivity) getActivity(), fragment, getFragmentContainerId(), true, getLogTag() + parentBook.getIBookId());
        }
    }

    @Subscribe
    public void onEvent(SuccessEvent event) {
        if (event.getScreen() == BaseEvent.Screen.MAIN) {
            BaseEvent.EventType type = event.getEventType();
            switch (type) {
                case LOGOUT:
                case LOGIN:
                    bindData();
                    break;
                case MOTHER_TONGUE_LANGUAGE_CHANGED:
                    updateMotherTongue();
                    getData();
                    bindData();
                    break;
            }
        }
    }

    private void updateMotherTongue() {
        if (activity.subDatabase != null) {
            activity.subDatabase.updateMotherTongueLanguage(activity);
        }
    }
}
