package com.dalread.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import com.dalread.R;
import com.dalread.base.VocaActivity;
import com.dalread.dialog.AlertDialog;
import com.dalread.dialog.SingleChoiceDialog;
import com.dalread.model.VocaBook;
import com.dalread.network.DalApiListener;
import com.dalread.util.Constant;
import com.dalread.util.NetworkUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindArray;
import butterknife.OnClick;

public class WordbooksHangulActivity extends VocaActivity {

    @BindArray(R.array.practice_hangul_options)
    String[] practiceOptions;

    private Context context;
    private AlertDialog alertDialog;
    private ArrayList<VocaBook> vocaBooks;
    private SingleChoiceDialog singleChoiceDialog;
    private boolean isGetVocaBookFromNetwork;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_wordbooks_hangul;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
        initLayout();
        getData();
    }

    public static Intent createIntent(Context context) {
        Intent intent = new Intent(context, WordbooksHangulActivity.class);
        intent.putExtra(Constant.BUNDLE.KEY_GET_DATA_FROM_NETWORK, true);
        return intent;
    }

    public static Intent createIntent(Context context, boolean isGetVocaBookFromNetwork) {
        Intent intent = new Intent(context, WordbooksHangulActivity.class);
        intent.putExtra(Constant.BUNDLE.KEY_GET_DATA_FROM_NETWORK, isGetVocaBookFromNetwork);
        return intent;
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

    }

    private void initData() {
        context = this;
        vocaBooks = new ArrayList<>();
        isGetVocaBookFromNetwork = getIntent().getBooleanExtra(Constant.BUNDLE.KEY_GET_DATA_FROM_NETWORK, false);
    }

    private void initLayout() {
        // others
        alertDialog = new AlertDialog(context);
        singleChoiceDialog = new SingleChoiceDialog(context);
    }

    private void getData() {
        if (isGetVocaBookFromNetwork) {
            if (NetworkUtil.isNetworkConnetedIfNotShowWarningAsPopup(context)) {
                application.getDalAiImpl().getServerVocaBookListByCategory(
                        sharedPreferences.getLangStudyCode(),
                        sharedPreferences.getMotherTongueLangCode(),
                        0,
                        Constant.API_VALUE.VOCABOOK_PRACTICE_ONLY_NO,
                        Constant.API_VALUE.VOCABOOK_ALPHABET_ONLY_YES,
                        new DalApiListener<List<VocaBook>>() {

                            @Override
                            public void onSuccess(List<VocaBook> response) {
                                if (response != null) {
                                    vocaBooks.addAll(response);
                                }
                            }

                            @Override
                            public void onFailure(String error) {
                            }
                        }
                );
            }
        } else {

        }
    }

    private void openStudyWritingScreen(int pos) {
        if (!vocaBooks.isEmpty() && pos < vocaBooks.size()) {
            Intent intent = new Intent(context, StudyHandWritingHangulActivity.class);
            intent.putExtra(Constant.BUNDLE.KEY_VOCA_BOOK, vocaBooks.get(pos));
            openNewScreen(intent);
        }
    }

    private void openSubScreen(int pos) {
        if (!vocaBooks.isEmpty() && pos < vocaBooks.size()) {
            Intent intent = new Intent(context, WordbooksHangulSubActivity.class);
            intent.putExtra(Constant.BUNDLE.KEY_VOCA_BOOK, vocaBooks.get(pos));
            openNewScreen(intent);
        }
    }

    private void openPracticeTypingScreen() {
        openNewScreen(PracticeTypingHangulActivity.class);
    }

    private void openPracticeHandWritingScreen() {
        openNewScreen(PracticeHandWritingHangulActivity.class);
    }

    @OnClick({R.id.v_hangul_1, R.id.v_hangul_2, R.id.v_hangul_3, R.id.v_hangul_4, R.id.vPracticeHangul1, R.id.vPracticeHangul2})
    void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.v_hangul_1:
                openStudyWritingScreen(0);
                break;
            case R.id.v_hangul_2:
                openStudyWritingScreen(1);
                break;
            case R.id.v_hangul_3:
                openSubScreen(2);
                break;
            case R.id.v_hangul_4:
                openStudyWritingScreen(3);
                break;
            case R.id.vPracticeHangul1:
                openPracticeTypingScreen();
                break;
            case R.id.vPracticeHangul2:
                openPracticeHandWritingScreen();
                break;
            default:
                break;
        }
    }
}
