package com.dalread.activity;

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
import com.dalread.base.BaseFragment;
import com.dalread.databinding.FragmentWebDictionaryBinding;
import com.dalread.util.Constant;
import com.dalread.util.DLog;
import com.dalread.util.Utils;

import butterknife.BindView;

public class WebDictionaryContentFragment extends BaseFragment {

    @BindView(R.id.webView) WebView webView;
    @BindView(R.id.pb_loading) ProgressBar pbLoading;
    private String url = Constant.BASE_BLANK;
    private FragmentWebDictionaryBinding binding;
    @Override
    protected View getContentView() {
        binding = FragmentWebDictionaryBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        return view;
    }

//    @Override
//    protected int getContentViewId() {
//        return R.layout.fragment_web_dictionary;
//    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
    }
    public void initView() {
        url = getArguments().getString(Constant.PLAYER.INTENT.KEY_URL);
        DLog.d(getLogTag(), "url=" + url);
    }


    public void initData() {
        if (Utils.isEmpty(url))
            return;

        webView.getSettings().setJavaScriptEnabled(true);
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        webView.clearCache(true);
        webView.setHorizontalScrollBarEnabled(false);
        webView.setWebViewClient(new MyWebViewClient());
        webView.setWebChromeClient(new MyChromeClient());
        webView.loadUrl(url);
    }

//    @Override
//    protected View getContentView() {
//        return null;
//    }

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
            if (pbLoading != null) {
                pbLoading.setProgress(newProgress);
                pbLoading.setVisibility(newProgress >= 100 ? View.GONE : View.VISIBLE);
            }
        }
    }
}
