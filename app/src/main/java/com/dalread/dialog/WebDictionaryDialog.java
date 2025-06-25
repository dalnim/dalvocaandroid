package com.dalread.dialog;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;

import com.dalread.R;
import com.dalread.base.BasePlayerDialog;
import com.dalread.databinding.DialogWebDictionaryBinding;
import com.dalread.listener.OnClickDialogListener;

public class WebDictionaryDialog extends BasePlayerDialog implements View.OnClickListener {
    private OnClickDialogListener listener;
    private DialogWebDictionaryBinding binding;

    @Override
    protected View getContentView() {
        binding = DialogWebDictionaryBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        return view;
    }

    public WebDictionaryDialog(@NonNull Context context, String url, OnClickDialogListener listener) {
        super(context);

        this.listener = listener;

        setDialogSizeWider(true);

        WebSettings settings = binding.webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setBuiltInZoomControls(true);
        settings.setDisplayZoomControls(false);
        binding.webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        binding.webView.clearCache(true);
        binding.webView.setHorizontalScrollBarEnabled(false);
        binding.webView.setWebViewClient(new MyWebViewClient());
        binding.webView.setWebChromeClient(new MyChromeClient());
        binding.webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        binding.webView.loadUrl(url);
    }

    class MyWebViewClient extends WebViewClient {

        public MyWebViewClient() {

        }

        @SuppressWarnings("deprecation")
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
//            view.loadUrl(url);
            return false;
        }

        @TargetApi(Build.VERSION_CODES.N)
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
//            view.loadUrl(request.getUrl().toString());
            return false;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            binding.tvBack.setEnabled(view.canGoBack());
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
        binding.tvClose.setOnClickListener(this);
        binding.tvClose1.setOnClickListener(this);
        binding.tvMakeTrasnparency.setOnClickListener(this);
        binding.tvBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tvMakeTrasnparency) {
            if (binding.getRoot().getAlpha() == 1.0f)
                binding.getRoot().setAlpha(0.1f);
            else
                binding.getRoot().setAlpha(1.0f);
            return;
        } else if (v.getId() == R.id.tv_back) {
            if (binding.webView.canGoBack()) {
                binding.webView.goBack();
            }
            return;
        }
        dismiss();
        if (listener != null) {
            listener.onDismiss(v, -1);
        }

    }
//
//    @OnClick({R.id.tv_close, R.id.tv_close1, R.id.tvMakeTrasnparency, R.id.tv_back})
//    void onClick(View view) {
//        if (view.getId() == R.id.tvMakeTrasnparency) {
//            if (binding.getRoot().getAlpha() == 1.0f)
//                binding.getRoot().setAlpha(0.1f);
//            else
//                binding.getRoot().setAlpha(1.0f);
//            return;
//        } else if (view.getId() == R.id.tv_back) {
//            if (binding.webView.canGoBack()) {
//                binding.webView.goBack();
//            }
//            return;
//        }
//        dismiss();
//        if (listener != null) {
//            listener.onDismiss(view, -1);
//        }
//
//    }
}
