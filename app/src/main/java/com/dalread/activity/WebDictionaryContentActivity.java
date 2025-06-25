package com.dalread.activity;

import android.os.Bundle;
import android.view.Menu;
import android.view.View;

import androidx.annotation.Nullable;

import com.dalread.R;
import com.dalread.base.BaseActivity;
import com.dalread.component.Toolbar;
import com.dalread.database.WebDictionaryQuery;
import com.dalread.databinding.ActivityPlayerWebDictionaryContentBinding;
import com.dalread.model.WebDictionaryModel;
import com.dalread.util.BaseVoca;
import com.dalread.util.Constant;
import com.dalread.util.LanguageUtil;
import com.dalread.util.Utils;

import java.util.ArrayList;
import java.util.List;

import butterknife.OnClick;

public class WebDictionaryContentActivity extends BaseActivity {
    private int currentBottomNavigationId;
    private String word;
    private List<WebDictionaryModel> webDictionaryList = new ArrayList<>();
    private ActivityPlayerWebDictionaryContentBinding binding;

    @Override
    protected View getContentView() {
        binding = ActivityPlayerWebDictionaryContentBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        return view;
    }

    @Override
    protected Toolbar getToolbar() {
        return binding.header;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
    }

    public void initData() {
        word = getIntent().getStringExtra(Constant.PLAYER.INTENT.KEY_WORD);
        binding.etSearch.setText(word);
        if (!Utils.isEmpty(word)) {
            binding.etSearch.setSelection(word.length());
        }
        webDictionaryList = WebDictionaryQuery.getFourItems(BaseVoca.getRealm(), LanguageUtil.getStudyLanguageCode(this), LanguageUtil.getMotherTongueLanguageCode(this));
        if (webDictionaryList == null || webDictionaryList.isEmpty()) return;
        if (webDictionaryList.size() <= 1) {
            binding.navBottom.setVisibility(View.GONE);
        } else {
            Menu menu = binding.navBottom.getMenu();
            for (int i = 0; i < webDictionaryList.size(); i++) {
                menu.add(Menu.NONE, i, Menu.NONE, webDictionaryList.get(i).getTitle())
                        .setIcon(R.mipmap.ic_dic_system);
            }
            binding.navBottom.setVisibility(View.VISIBLE);
        }
        binding.navBottom.setOnNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id != currentBottomNavigationId) {
                currentBottomNavigationId = id;
                loadFragment();
                return true;
            }
            return false;
        });
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
        word = binding.etSearch.getText().toString();
        loadFragment();
    }
    
    @OnClick(R.id.iv_copy)
    void copyClick() {
        if (Utils.isEmpty(word))
            return;
        Utils.copyToClipboard(this, word, R.string.copied);
    }

    private void loadFragment() {
        if (Utils.isEmpty(word) || webDictionaryList.isEmpty())
            return;
        final String url = webDictionaryList.get(currentBottomNavigationId).getUrl()
                .replace(Constant.PLAYER.WEB_DICTIONARY.WORD_REPLACE, word);
        Bundle b = new Bundle();
        b.putString(Constant.PLAYER.INTENT.KEY_URL, url);
        WebDictionaryContentFragment fragment = new WebDictionaryContentFragment();
        fragment.setArguments(b);
        Utils.loadFragment(this, fragment, getFragmentContainerId(), false);
    }
}
