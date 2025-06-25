package com.dalread.activity;

import android.content.Intent;

import com.dalread.listener.OnWordbookClickListener;
import com.dalread.model.VocaBook;
import com.dalread.util.Constant;

import java.io.Serializable;
import java.util.Iterator;

public class SelectVocaBookInVocaBooksActivity extends VocaBooksActivity {

    private int targetBookId;

    @Override
    protected void bindData() {
        targetBookId = getIntent().getIntExtra(Constant.BUNDLE.KEY_VOCA_BOOK_ID, 0);
        adapter.setSelecting(targetBookId > 0);
        if (userBooks != null) {
            for (Iterator<VocaBook> i = userBooks.iterator(); i.hasNext(); ) {
                VocaBook book = i.next();
                if (book.getId() == targetBookId) {
                    i.remove();
                    break;
                }
            }
        }

        super.bindData();
    }

    @Override
    protected OnWordbookClickListener getOnWordbookClickListener() {
        return new OnWordbookClickListener() {

            @Override
            public void onAddClick() {
            }

            @Override
            public void onDetailsClick(VocaBook book) {
                Intent intent = new Intent(SelectVocaBookInVocaBooksActivity.this, SelectVocaInVocaBookActivity.class);
                intent.putExtra(Constant.BUNDLE.KEY_VOCA_BOOK, book);
                intent.putExtra(Constant.BUNDLE.KEY_VOCA_USER_BOOK_LIST, (Serializable) userBooks);
                intent.putExtra(Constant.BUNDLE.KEY_VOCA_BOOK_ID, targetBookId);
                startActivity(intent);
            }
        };
    }
}
