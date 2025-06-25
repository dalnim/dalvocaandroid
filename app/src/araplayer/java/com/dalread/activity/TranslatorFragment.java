package com.dalread.activity;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.os.Build;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.dalread.base.BasePlayerFragment;
import com.dalread.databinding.FragmentTranslatorBinding;
import com.dalread.util.Constant;
import com.dalread.util.DLog;
import com.dalread.util.Utils;

public class TranslatorFragment extends BasePlayerFragment {
    private String url = Constant.BASE_BLANK;

    private FragmentTranslatorBinding binding;

    @Override
    protected View getContentView() {
        binding = FragmentTranslatorBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void initView() {
        url = getArguments().getString(Constant.PLAYER.INTENT.KEY_URL);
        DLog.d(getLogTag(), "url=" + url);
        if (Utils.isEmpty(url)) return;
        initData();
    }

    @Override
    public void initData() {
        binding.webView.getSettings().setJavaScriptEnabled(true);
        binding.webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        binding.webView.clearCache(true);
        binding.webView.setHorizontalScrollBarEnabled(false);
        binding.webView.setWebViewClient(new MyWebViewClient());
        binding.webView.setWebChromeClient(new MyChromeClient());
        binding.webView.loadUrl(url);
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
            if (binding.pbLoading != null) {
                binding.pbLoading.setProgress(newProgress);
                binding.pbLoading.setVisibility(newProgress >= 100 ? View.GONE : View.VISIBLE);
            }
        }
    }
}
