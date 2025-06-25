package com.dalread.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.EditText;

import androidx.annotation.Nullable;

import com.dalread.R;
import com.dalread.base.BaseVocaActivity;
import com.dalread.util.Constant;
import com.dalread.util.Utils;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import butterknife.BindView;
import butterknife.OnClick;

@SuppressLint("NonConstantResourceId")
public class WebDictionaryContentActivity extends BaseVocaActivity {

    @BindView(R.id.nav_bottom)
    BottomNavigationView bottomNavigationView;
    @BindView(R.id.et_search)
    EditText etSearch;

    private int currentBottomNavigationPos;
    private String word;
    private final String flagWord = "flag_word";
    private final String[] webDictionaryList = {
            "https://hanja.dict.naver.com/search?query=" + flagWord,
            "https://m.dic.daum.net/search.do?q=" + flagWord + "&dic=hanja",
            "https://ko.wiktionary.org/wiki/" + flagWord,
            "https://namu.wiki/w/" + flagWord
    };

    @Override
    protected int getContentViewId() {
        return R.layout.activity_player_web_dictionary_content;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initData();
        initView();

        loadFragment();
    }

    @Override
    public void onHeaderLeftClick() {
        super.onBackPressed();
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

    @OnClick(R.id.iv_search)
    void searchClick() {
        word = etSearch.getText().toString();
        loadFragment();
    }

    @OnClick(R.id.iv_copy)
    void copyClick() {
        if (!Utils.isEmpty(word)) {
            Utils.copyToClipboard(this, word, R.string.copied);
        }
    }

    private void initData() {
        word = getIntent().getStringExtra(Constant.BUNDLE.KEY_VOCA);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int pos = item.getOrder();
            if (pos != currentBottomNavigationPos) {
                currentBottomNavigationPos = pos;
                loadFragment();
                return true;
            }
            return false;
        });
    }

    private void initView() {
        etSearch.setText(word);
        if (!Utils.isEmpty(word)) {
            etSearch.setSelection(word.length());
        }
    }

    private void loadFragment() {
        String url = webDictionaryList[currentBottomNavigationPos].replace(flagWord, word);

        Bundle bundle = new Bundle();
        bundle.putString(Constant.BUNDLE.KEY_URL, url);

        WebDictionaryContentFragment fragment = new WebDictionaryContentFragment();
        fragment.setArguments(bundle);

        Utils.loadFragment(this, fragment, getFragmentContainerId(), false);
    }
}
