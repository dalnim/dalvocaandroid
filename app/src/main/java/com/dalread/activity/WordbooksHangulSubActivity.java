package com.dalread.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dalread.R;
import com.dalread.adapter.WordbooksHangulSubAdapter;
import com.dalread.base.VocaActivity;
import com.dalread.dialog.AlertDialog;
import com.dalread.listener.OnWordbooksHangulSubListener;
import com.dalread.model.VocaBook;
import com.dalread.network.DalApiListener;
import com.dalread.util.Constant;
import com.dalread.util.Utils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class WordbooksHangulSubActivity extends VocaActivity {

    @BindView(R.id.tvItemTitle)
    TextView tvItemTitle;
    @BindView(R.id.rv_voca)
    RecyclerView rvVoca;

    private Context context;
    private AlertDialog alertDialog;
    private VocaBook category;
    protected List<VocaBook> vocaBooks;
    private WordbooksHangulSubAdapter adapter;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_wordbooks_hangul_sub;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
        initLayout();
        getData(category.getId());
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
        category = (VocaBook) getIntent().getSerializableExtra(Constant.BUNDLE.KEY_VOCA_BOOK);
        vocaBooks = new ArrayList<>();
    }

    private void initLayout() {
        // toolbar
        tvItemTitle.setText(category.getName());
//        toolbar.setTitle("");
        // recycler view
        rvVoca.setLayoutManager(new GridLayoutManager(context, 2));
        rvVoca.setAdapter(adapter = new WordbooksHangulSubAdapter(HANGUL_BOOK_170, new OnWordbooksHangulSubListener() {

            @Override
            public void onClick(int pos) {
                openStudyWritingScreen(pos);
            }
        }));
        // others
        alertDialog = new AlertDialog(context);
    }


    protected void getData(int categoryId) {
        int uid = getUserID();
        if (uid > 0) {
            if (Utils.isConnected(context)) {
                application.getDalAiImpl().getServerVocaBookListByCategory(
                        sharedPreferences.getLangStudyCode(),
                        sharedPreferences.getMotherTongueLangCode(),
                        categoryId,
                        Constant.API_VALUE.VOCABOOK_PRACTICE_ONLY_NO,
                        Constant.API_VALUE.VOCABOOK_ALPHABET_ONLY_YES,
                        new DalApiListener<List<VocaBook>>() {

                            @Override
                            public void onSuccess(List<VocaBook> response) {
                                vocaBooks.addAll(response);
                            }

                            @Override
                            public void onFailure(String error) {

                            }
                        }
                );
            } else {
                alertDialog.showNoInternet();
            }
        } else {
            alertDialog.showLogInRequired();
        }
    }

    protected void openStudyWritingScreen(int pos) {
        if (!vocaBooks.isEmpty() && pos < vocaBooks.size()) {
            Intent intent = new Intent(context, StudyHandWritingHangulActivity.class);
            intent.putExtra(Constant.BUNDLE.KEY_VOCA_BOOK, vocaBooks.get(pos));
            openNewScreen(intent);
        }
    }

    private static final int[] HANGUL_BOOK_170 = {
            R.drawable.ic_hangul_01_ga,
            R.drawable.ic_hangul_02_na,
            R.drawable.ic_hangul_03_da,
            R.drawable.ic_hangul_04_la,
            R.drawable.ic_hangul_05_ma,
            R.drawable.ic_hangul_06_ba,
            R.drawable.ic_hangul_07_sa,
            R.drawable.ic_hangul_08_a,
            R.drawable.ic_hangul_09_ja,
            R.drawable.ic_hangul_10_cha,
            R.drawable.ic_hangul_11_ka,
            R.drawable.ic_hangul_12_ta,
            R.drawable.ic_hangul_13_pa,
            R.drawable.ic_hangul_14_ha
    };
}
