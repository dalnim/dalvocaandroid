package com.dalread.component;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.TextView;

import androidx.appcompat.widget.SearchView;

public class EmptySubmitSearchView extends SearchView {

    SearchAutoComplete mSearchSrcTextView;
    OnQueryTextListener listener;

    public EmptySubmitSearchView(Context context) {
        super(context);
    }

    public EmptySubmitSearchView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public EmptySubmitSearchView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setOnQueryTextListener(OnQueryTextListener onQueryTextListener) {
        super.setOnQueryTextListener(onQueryTextListener);

        listener = onQueryTextListener;

        mSearchSrcTextView = findViewById(androidx.appcompat.R.id.search_src_text);
        if (mSearchSrcTextView != null) {
            mSearchSrcTextView.setOnEditorActionListener(new TextView.OnEditorActionListener() {

                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (listener != null) {
                        listener.onQueryTextSubmit(getQuery().toString());
                    }
                    return true;
                }
            });
        }
    }

    public void clearSearchText() {
        mSearchSrcTextView.setText("");
    }
}
