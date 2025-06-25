package com.dalread.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dalread.R;
import com.dalread.adapter.WordbooksAdapter;
import com.dalread.base.BaseDalVocaPlayVocaActivity;
import com.dalread.component.SeparatorDecoration;
import com.dalread.dialog.AlertDialog;
import com.dalread.listener.OnWordbookClickListener;
import com.dalread.model.VocaBook;
import com.dalread.network.DalApiListener;
import com.dalread.network.models.VocaBookListNativeSpeakerResponse;
import com.dalread.util.Constant;
import com.dalread.util.Loading;
import com.dalread.util.Utils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindColor;
import butterknife.BindDimen;
import butterknife.BindString;
import butterknife.BindView;

public class NativeSpeakerMainActivity extends BaseDalVocaPlayVocaActivity {

    @BindView(R.id.tv_recorded_count) TextView tvRecordedCount;
    @BindView(R.id.rv_book) RecyclerView rvBook;
    @BindColor(R.color.color_divider) int clDivider;
    @BindDimen(R.dimen.divider_height) float dividerHeight;
    @BindString(R.string.tpl_total_recorded_word_count) String tplRecordedCount;

    private Context context;
    private AlertDialog alertDialog;
    private List<VocaBook> serverBooks;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_main_native_speaker;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        initDialog();
        initRecyclerView();
        getData();
    }

    @Override
    public void onHeaderLeftClick() {
        onBackPressed();
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

    private void initDialog() {
        alertDialog = new AlertDialog(context);
    }

    private void initRecyclerView() {
        rvBook.setLayoutManager(new LinearLayoutManager(context));
        rvBook.addItemDecoration(BaseBindUtils.getSeparatorDecoration(context));
    }

    private void getData() {
        int uid = getUserID();
        if (uid > 0) {
            if (Utils.isConnected(context)) {
                Loading.show(context);
                application.getDalAiImpl().getServerVocaBookListForNativeSpeaker(
                        0,
                        Constant.API_VALUE.VOCABOOK_PRACTICE_ONLY_NO,
                        new DalApiListener<VocaBookListNativeSpeakerResponse>() {

                            @Override
                            public void onSuccess(VocaBookListNativeSpeakerResponse response) {
                                WordbooksAdapter adapter = new WordbooksAdapter();
                                if (response != null) {
                                    serverBooks = new ArrayList<>();
                                    serverBooks.addAll(response.getVocas());
                                    adapter.setData(serverBooks);

                                    String recordedCount = String.format(tplRecordedCount, response.getVocaRecordedTotal());
                                    tvRecordedCount.setText(recordedCount);
                                }
                                adapter.setListener(onWordbookClickListener);
                                rvBook.setAdapter(adapter);

                                Loading.hide();
                            }

                            @Override
                            public void onFailure(String error) {
                                Loading.hide();
                            }
                        }
                );
            } else {
                alertDialog.showNoInternet();
            }
        } else {
            alertDialog.showLogInRequired();
        }
    }

    private OnWordbookClickListener onWordbookClickListener = new OnWordbookClickListener() {

        @Override
        public void onAddClick() {
        }

        @Override
        public void onDetailsClick(VocaBook book) {
            Intent intent;
            if (book.getWordCount() > 0) {
                intent = new Intent(context, VocaBookNativeSpeakerActivity.class);
            } else {
                intent = new Intent(context, WordbookByCategoryActivity.class);
                intent.putExtra(Constant.BUNDLE.KEY_RECORDING_ALL, true);
            }
            intent.putExtra(Constant.BUNDLE.KEY_VOCA_BOOK, book);
            openNewScreen(intent);
        }
    };
}
