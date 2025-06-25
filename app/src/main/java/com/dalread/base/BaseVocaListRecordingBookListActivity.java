package com.dalread.base;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.dalread.R;
import com.dalread.adapter.WorkbooksAdapterCommon;
import com.dalread.component.SeparatorDecoration;
import com.dalread.databinding.ActivityVocaListRecordingBookListBinding;
import com.dalread.dialog.AlertDialog;
import com.dalread.listener.OnDoubleClickListener;
import com.dalread.model.VocaBook;
import com.dalread.network.DalApiListener;
import com.dalread.network.models.VocaBookListNativeSpeakerResponse;
import com.dalread.util.BaseBindUtils;
import com.dalread.util.Constant;
import com.dalread.util.Loading;
import com.dalread.util.Utils;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseVocaListRecordingBookListActivity extends BaseActivity implements View.OnClickListener {
    protected final int TYPE_INIT_DATA = 0;
    protected final int TYPE_SHOW_RECORDED_COUNT = TYPE_INIT_DATA + 1;
    protected WorkbooksAdapterCommon adapter;

    private Context context;
    private AlertDialog alertDialog;
    protected List<VocaBook> vocaBookList;
    protected List<String> recordedVoiceFileList;
    protected ActivityVocaListRecordingBookListBinding binding;
    @Override
    protected View getContentView() {
        binding = ActivityVocaListRecordingBookListBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        return view;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setOnClickListeners();
        hideUnusedUI();
        initDialog();
        initRecyclerView();
        getData();
    }

    protected void hideUnusedUI() {
        binding.ivShowRecordedCount.setVisibility(View.INVISIBLE);
    }

    private void setOnClickListeners() {
        binding.ivShowRecordedCount.setOnClickListener(this);
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ivShowRecordedCount:
                onClickShowRecordedCount();
                break;
        }
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
        adapter = new WorkbooksAdapterCommon(BaseVocaListRecordingBookListActivity.this, onDoubleClickListener);
    }

    private void initRecyclerView() {
        binding.rvBook.setLayoutManager(new LinearLayoutManager(context));
        binding.rvBook.addItemDecoration(new SeparatorDecoration(this, BaseBindUtils.getDividerColor(), BaseBindUtils.getDividerHeight(this)));

    }

    protected void getData() {
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
//                                WorkbooksAdapterCommon adapter = new WorkbooksAdapterCommon(BaseVocaListRecordingBookListActivity.this, onDoubleClickListener);
                                if (response != null) {
                                    vocaBookList = new ArrayList<>();
                                    vocaBookList.addAll(response.getVocas());
                                    adapter.setBooks(vocaBookList);

                                    updateRecordedCount(response.getVocaRecordedTotal());
                                }
                                binding.rvBook.setAdapter(adapter);

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

    protected void onClickShowRecordedCount() {

    }

    protected void updateRecordedCount(int vocaRecordCount) {
        String recordedCount = getString(R.string.tpl_total_recorded_word_count, vocaRecordCount);
        binding.tvRecordedCount.setText(recordedCount);
    }

    protected OnDoubleClickListener onDoubleClickListener = new OnDoubleClickListener() {
        @Override
        public void onClick(View view, Object data) {
            VocaBook book = (VocaBook) data;
//            if (book.getWordCount() > 0) {
//                openVocaListRecordingActivity(book);
//            } else {
                Intent intent;
                if (book.getWordCount() > 0) {
                    intent = new Intent(context, BaseVocaListRecordingActivity.class);
                } else {
                    intent = new Intent(context, BaseVocaListRecordingBookListActivity.class);
                    intent.putExtra(Constant.BUNDLE.KEY_RECORDING_ALL, true);
                }
                intent.putExtra(Constant.BUNDLE.KEY_VOCA_BOOK, book);
                openNewScreen(intent);
            }
//        }

        @Override
        public void onDoubleClick(View view, Object data) {

        }
    };

//    protected void openVocaListRecordingActivity(VocaBook book) {
//        openNewScreen(
//                VocaListRecordingActivity.createIntent(this, book)
//        );
//    }
}
