package com.dalread.activity;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;

import com.dalread.R;
import com.dalread.base.BaseVocaFragment;
import com.dalread.util.Constant;
import com.dalread.util.Utils;

import butterknife.BindView;

@SuppressLint("NonConstantResourceId")
public class WebDictionaryContentFragment extends BaseVocaFragment {

    @BindView(R.id.webView)
    WebView webView;
    @BindView(R.id.pb_loading)
    ProgressBar pbLoading;

    private String url = Constant.BASE_BLANK;

    @Override
    protected int getContentViewId() {
        return R.layout.fragment_web_dictionary;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initData();
        initView();
    }

    private void initData() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            url = bundle.getString(Constant.BUNDLE.KEY_URL);
        }

    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initView() {
        if (!Utils.isEmpty(url)) {
            webView.getSettings().setJavaScriptEnabled(true);
            webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
            webView.clearCache(true);
            webView.setHorizontalScrollBarEnabled(false);
            webView.setWebViewClient(new MyWebViewClient());
            webView.setWebChromeClient(new MyChromeClient());
            webView.loadUrl(url);
        }
    }

    class MyWebViewClient extends WebViewClient {

        public MyWebViewClient() {
        }

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

            if (pbLoading != null) {
                pbLoading.setProgress(newProgress);
                pbLoading.setVisibility(newProgress >= 100 ? View.GONE : View.VISIBLE);
            }
        }
    }
}
