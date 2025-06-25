package com.dalread.activity;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;

import com.dalread.R;
import com.dalread.base.BasePlayerDialog;
import com.dalread.base.EnumLanguage;
import com.dalread.databinding.PopupSubtitleTranslateBinding;
import com.dalread.listener.OnClickDialogListener;
import com.dalread.model.TranslatorModel;
import com.dalread.util.Constant;
import com.dalread.util.Utils;

import java.util.ArrayList;
import java.util.List;

public class PopupSubtitleTranslateDialog extends BasePlayerDialog implements View.OnClickListener {
    private List<TranslatorModel> translatorModelList;
    private EnumLanguage studyLanguage, motherTongueLanguage, menuLanguage;
    private int currentBottomNavigationId;
    private String text;
    private OnClickDialogListener listener;
    private PopupSubtitleTranslateBinding binding;

    @Override
    protected View getContentView() {
        binding = PopupSubtitleTranslateBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        return view;
    }

    public PopupSubtitleTranslateDialog(@NonNull Context context, String text, OnClickDialogListener listener) {
        super(context);
        this.text = text;
        this.listener = listener;

        setDialogSizeWider(true);
        initData();
    }

    class MyWebViewClient extends WebViewClient {

        public MyWebViewClient() {

        }

        @SuppressWarnings("deprecation")
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return false;
        }

        @TargetApi(Build.VERSION_CODES.N)
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            view.loadUrl(request.getUrl().toString());
            return false;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onPageFinished(final WebView view, String url) {
            super.onPageFinished(view, url);
        }
    }

    class MyChromeClient extends WebChromeClient {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);
            binding.pbLoading.setProgress(newProgress);
            binding.pbLoading.setVisibility(newProgress >= 100 ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    protected void initOnClickListener() {
        binding.btnCancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        dismiss();
        if (listener != null) {
            listener.onDismiss(v, -1);
        }
    }

//    @OnClick({R.id.btnCancel})
//    void onClick(View view) {
//        dismiss();
//        if (listener != null) {
//            listener.onDismiss(view, -1);
//        }
//    }

    public void initData() {

        studyLanguage = EnumLanguage.getStudyLanguage(getContext());// EnumLanguage.findByFormatApi(SharedPreferencesDB.getInstance(getContext()).getStudyLanguage());
        motherTongueLanguage = EnumLanguage.getMotherTongueLanguage(getContext());// EnumLanguage.findByFormatApi(SharedPreferencesDB.getInstance(getContext()).getMotherTongueLanguage());
        menuLanguage = EnumLanguage.getMenuLanguage(getContext());
        translatorModelList = new ArrayList<>();
        translatorModelList.add(new TranslatorModel("Papago", Constant.PLAYER.TRANSLATOR.SERVER.PAPAGO));
        translatorModelList.add(new TranslatorModel("Google", Constant.PLAYER.TRANSLATOR.SERVER.GOOGLE));
        translatorModelList.add(new TranslatorModel("DeepL", Constant.PLAYER.TRANSLATOR.SERVER.DEEPL));
        currentBottomNavigationId = mSharedPref.getSelectedTranslatorId();
        if (translatorModelList.size() <= 1) {
            binding.navBottom.setVisibility(View.GONE);
        } else {
            Menu menu = binding.navBottom.getMenu();
            for (int i = 0; i < translatorModelList.size(); i++) {
                menu.add(Menu.NONE, i, Menu.NONE, translatorModelList.get(i).getName())
                        .setIcon(R.drawable.ic_translate_black);
            }
            if (currentBottomNavigationId >= 0 && currentBottomNavigationId < menu.size()) {
                MenuItem menuItem = menu.getItem(currentBottomNavigationId);
                menuItem.setChecked(true);
            }
            binding.navBottom.setVisibility(View.VISIBLE);
        }

        binding.navBottom.setOnNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id != currentBottomNavigationId) {
                currentBottomNavigationId = id;
                mSharedPref.setSelectedTranslatorId(currentBottomNavigationId);
                loadFragment();
                return true;
            }
            return false;
        });
        loadFragment();
    }

    private void loadFragment() {
        if (Utils.isEmpty(text))
            text = "";

        final String path = translatorModelList.get(currentBottomNavigationId).getPath();
        String url, study, mother;
        String tmpStudy = EnumLanguage.getTranslateLanguage(studyLanguage);
        String tmpMother = EnumLanguage.getTranslateLanguage(motherTongueLanguage);

        study = tmpStudy;
        mother = tmpMother;

        url = path
                .replace(Constant.PLAYER.TRANSLATOR.VIEW_REPLACE, tmpMother)
                .replace(Constant.PLAYER.TRANSLATOR.STUDY_REPLACE, study)
                .replace(Constant.PLAYER.TRANSLATOR.MOTHER_REPLACE, mother)
                .replace(Constant.PLAYER.TRANSLATOR.WORD_REPLACE, text)
                .replace("[", " ") //이건 파파고가 [] 문자가 있으면 처리를 못해서 해준다.
                .replace("]", " ") //이건 파파고가 [] 문자가 있으면 처리를 못해서 해준다.
                .replace(Constant.BASE_ONE_SPACE, Constant.BASE_ONE_SPACE_ESCAPE);

        loadWebView(url);

    }

    private void loadWebView(String url) {
        WebSettings settings = binding.webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);

        binding.webView.getSettings().setJavaScriptEnabled(true);
        binding.webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        binding.webView.clearCache(true);
        binding.webView.setHorizontalScrollBarEnabled(false);
        binding.webView.setWebViewClient(new MyWebViewClient());
        binding.webView.setWebChromeClient(new MyChromeClient());
        binding.webView.loadUrl(url);
        // Inject JavaScript to set default language to English (근데 잘  안되는거 같다.)
        String iso639 = menuLanguage.getFormatOs();
        binding.webView.evaluateJavascript("(function() { " +
                "var selectElem = document.querySelector('.mo_lang_select___1RL5O select');" +
                "selectElem.value = '" + iso639 + "';" +
                "selectElem.dispatchEvent(new Event('change'));" +
                "})()", null);
    }
}
