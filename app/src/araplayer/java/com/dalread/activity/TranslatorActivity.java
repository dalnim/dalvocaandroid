package com.dalread.activity;

import android.os.Bundle;
import android.view.Menu;
import android.view.View;

import com.dalread.R;
import com.dalread.base.BasePlayerActivity;
import com.dalread.base.EnumLanguage;
import com.dalread.component.Toolbar;
import com.dalread.databinding.ActivityPlayerTranslatorBinding;
import com.dalread.model.TranslatorModel;
import com.dalread.util.Constant;
import com.dalread.util.Utils;

import java.util.ArrayList;
import java.util.List;

import butterknife.OnClick;

public class TranslatorActivity extends BasePlayerActivity {

//    @BindView(R.id.nav_bottom) BottomNavigationView bottomNavigationView;
//    @BindView(R.id.et_search) EditText etSearch;

    private int currentBottomNavigationId;
    private List<TranslatorModel> translatorModelList;
    private boolean isFromStudyLanguage;
    private String word;

    private ActivityPlayerTranslatorBinding binding;

    @Override
    protected View getContentView() {
        binding = ActivityPlayerTranslatorBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        return view;
    }

    @Override
    protected Toolbar getToolbar() {
        return binding.header;
    }

    @Override
    protected void setFullscreen() {

    }

    @Override
    public void initView() {
        isFromStudyLanguage = getIntent().getExtras().getBoolean(Constant.PLAYER.INTENT.KEY_DATA, true);
        word = getIntent().getExtras().getString(Constant.PLAYER.INTENT.KEY_WORD, Constant.BASE_BLANK);
        binding.etSearch.setText(word);
        if (!Utils.isEmpty(word)) {
            binding.etSearch.setSelection(word.length());
        }
        initData();
    }

    @Override
    public void initData() {
        translatorModelList = new ArrayList<>();
        translatorModelList.add(new TranslatorModel("Translator 1", Constant.PLAYER.TRANSLATOR.SERVER.GOOGLE));
        if (motherTongueLanguage.getIdApi() == EnumLanguage.KOREAN.getIdApi()) {
            translatorModelList.add(new TranslatorModel("Translator 2", Constant.PLAYER.TRANSLATOR.SERVER.PAPAGO));
        }
        if (translatorModelList.size() <= 1) {
            binding.navBottom.setVisibility(View.GONE);
        } else {
            Menu menu = binding.navBottom.getMenu();
            for (int i = 0; i < translatorModelList.size(); i++) {
                menu.add(Menu.NONE, i, Menu.NONE, translatorModelList.get(i).getName())
                        .setIcon(R.drawable.ic_translate_black);
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
        if (Utils.isEmpty(word))
            return;

        final String path = translatorModelList.get(currentBottomNavigationId).getPath();
        String url, study, mother;
        String tmpStudy = EnumLanguage.getTranslateLanguage(studyLanguage);
        String tmpMother = EnumLanguage.getTranslateLanguage(motherTongueLanguage);

        if (isFromStudyLanguage) {
            study = tmpStudy;
            mother = tmpMother;
        } else {
            study = tmpMother;
            mother = tmpStudy;
        }
        url = path
                .replace(Constant.PLAYER.TRANSLATOR.VIEW_REPLACE, tmpMother)
                .replace(Constant.PLAYER.TRANSLATOR.STUDY_REPLACE, study)
                .replace(Constant.PLAYER.TRANSLATOR.MOTHER_REPLACE, mother)
                .replace(Constant.PLAYER.TRANSLATOR.WORD_REPLACE, word);
        Bundle b = new Bundle();
        b.putString(Constant.PLAYER.INTENT.KEY_URL, url);
        TranslatorFragment fragment = new TranslatorFragment();
        fragment.setArguments(b);
        Utils.loadFragment(this, fragment, getFragmentContainerId(), false);
    }
}
