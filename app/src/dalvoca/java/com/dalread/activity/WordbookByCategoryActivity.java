package com.dalread.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.dalread.R;
import com.dalread.adapter.WordbooksAdapter;
import com.dalread.base.BaseVocaActivity;
import com.dalread.component.CenterLayoutManager;
import com.dalread.component.SeparatorDecoration;
import com.dalread.dialog.AlertDialog;
import com.dalread.listener.OnWordbookClickListener;
import com.dalread.model.VocaBook;
import com.dalread.model.VocaBookNativeSpeaker;
import com.dalread.network.DalApiListener;
import com.dalread.network.events.BaseEvent;
import com.dalread.network.events.SuccessEvent;
import com.dalread.network.models.VocaBookListNativeSpeakerResponse;
import com.dalread.util.Constant;
import com.dalread.util.Loading;
import com.dalread.util.Utils;

import org.greenrobot.eventbus.Subscribe;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindColor;
import butterknife.BindDimen;
import butterknife.BindView;

public class WordbookByCategoryActivity extends BaseVocaActivity {

    @BindView(R.id.rv_book)
    RecyclerView rvBook;
    @BindColor(R.color.color_divider)
    int clDivider;
    @BindDimen(R.dimen.divider_height)
    float dividerHeight;

    private Context context;
    private AlertDialog alertDialog;
    protected WordbooksAdapter adapter;
    private CenterLayoutManager centerLayoutManager;
    private List<VocaBook> serverBooks;
    private VocaBook parentBook;
    private boolean dataChanged;
    private boolean practiceOnly;
    private boolean recordingAll;
    private int studyLang;
    private ArrayList<Integer> parentBookIds;
    private VocaBook selectedBook;
    private boolean autoMove;
    private boolean selectMulti;
    private List<VocaBook> selectedBooks;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_vocabooks;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initData();
        initToolbar();
        initRecyclerView();
        initDialog();
        initEventBus();
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
        if (isStudyMode()) {
            selectWordbookForStudyChat();
        } else if (selectMulti) {
            selectWordbooksForStudyChat();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (dataChanged) {
            dataChanged = false;
            getData();
        }
    }

    @Override
    protected void onDestroy() {
        unRegisterEventBus();
        super.onDestroy();
    }

    @Subscribe
    public void onEvent(SuccessEvent successEvent) {
        if (successEvent.getScreen() == BaseEvent.Screen.MAIN) {
            BaseEvent.EventType type = successEvent.getEventType();
            if (type == BaseEvent.EventType.DATA_CHANGED) {
                dataChanged = true;
            }
        }
    }

    private void initData() {
        context = this;
        Intent i = getIntent();
        parentBook = (VocaBook) i.getSerializableExtra(Constant.BUNDLE.KEY_VOCA_BOOK);
        practiceOnly = i.getBooleanExtra(Constant.BUNDLE.KEY_PRACTICE_ONLY, false);
        recordingAll = i.getBooleanExtra(Constant.BUNDLE.KEY_RECORDING_ALL, false);
        studyLang = i.getIntExtra(Constant.BUNDLE.KEY_STUDY_LANG, 0);
        parentBookIds = i.getIntegerArrayListExtra(Constant.BUNDLE.KEY_PARENT_BOOK_ID_LIST);
        autoMove = i.getBooleanExtra(Constant.BUNDLE.KEY_AUTO_MOVE, false);
        selectMulti = i.getBooleanExtra(Constant.BUNDLE.KEY_SELECT_MULTI, false);
        selectedBooks = new ArrayList<>();
    }

    private void initToolbar() {
        if (parentBook != null && toolbar != null) {
            toolbar.setTitle(parentBook.getName());
        }
    }

    private void initRecyclerView() {
        adapter = new WordbooksAdapter(isStudyMode() || selectMulti);
        adapter.setListener(onWordbookClickListener);
        rvBook.setAdapter(adapter);
        rvBook.setLayoutManager(centerLayoutManager = new CenterLayoutManager(context));
        rvBook.addItemDecoration(BaseBindUtils.getSeparatorDecoration(context));
    }

    private void initDialog() {
        alertDialog = new AlertDialog(context);
    }

    protected void getData() {
        if (Utils.isConnected(context)) {
            Loading.show(context);
            if (parentBook instanceof VocaBookNativeSpeaker) {
                application.getDalAiImpl().getServerVocaBookListForNativeSpeaker(
                        parentBook.getId(),
                        Constant.API_VALUE.VOCABOOK_PRACTICE_ONLY_NO,
                        new DalApiListener<VocaBookListNativeSpeakerResponse>() {

                            @Override
                            public void onSuccess(VocaBookListNativeSpeakerResponse response) {
                                if (response != null) {
                                    serverBooks = new ArrayList<>();
                                    serverBooks.addAll(response.getVocas());
                                }
                                Loading.hide();
                                bindData();
                            }

                            @Override
                            public void onFailure(String error) {
                                Loading.hide();
                            }
                        }
                );
            } else {
                application.getDalAiImpl().getServerVocaBookListByCategory(
                        isStudyMode() ? studyLang : sharedPreferences.getLangStudyCode(),
                        sharedPreferences.getMotherTongueLangCode(),
                        parentBook == null ? 0 : parentBook.getId(),
                        practiceOnly ? Constant.API_VALUE.VOCABOOK_PRACTICE_ONLY_YES : Constant.API_VALUE.VOCABOOK_PRACTICE_ONLY_NO,
                        new DalApiListener<List<VocaBook>>() {

                            @Override
                            public void onSuccess(List<VocaBook> response) {
                                serverBooks = response;
                                Loading.hide();
                                bindData();
                            }

                            @Override
                            public void onFailure(String error) {
                                Loading.hide();
                            }
                        }
                );
            }
        } else {
            alertDialog.showNoInternet();
            bindEmptyData();
        }
    }

    protected void bindData() {
        adapter.setData(serverBooks);
        adapter.notifyDataSetChanged();
        if (isStudyMode()) {
            blinkOrSelectCell();
        }
    }

    private void bindEmptyData() {
        serverBooks = null;
        bindData();
    }

    private void blinkOrSelectCell() {
        if (serverBooks == null || serverBooks.isEmpty()
                || parentBookIds == null || parentBookIds.isEmpty()) {
            return;
        }
        int bookId = parentBookIds.remove(parentBookIds.size() - 1);
        for (VocaBook book : serverBooks) {
            if (book.getId() == bookId) {
                if (parentBookIds.isEmpty()) {
                    autoMove = false;
                    onWordbookClickListener.onDetailsClick(book);
                }
                blinkCell(book);
                return;
            }
        }
    }

    private void blinkCell(VocaBook book) {
        book.setParentChecked(true);
        rvBook.post(() -> {
            int pos = adapter.notifyItemChanged(book);
            centerLayoutManager.smoothScrollToPosition(rvBook, null, pos);
        });
        if (autoMove) {
            rvBook.postDelayed(() -> onWordbookClickListener.onDetailsClick(book), 500);
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
                if (practiceOnly) {
                    intent = new Intent(context, PracticeWithPeopleActivity.class);
                } else if (recordingAll) {
                    intent = new Intent(context, VocaBookNativeSpeakerActivity.class);
                } else if (isStudyMode()) {
                    selectWordbook(book);
                    return;
                } else if (selectMulti) {
                    selectWordbooks(book);
                    return;
                } else {
                    intent = new Intent(context, VocaBookActivity.class);
                }
            } else {
                intent = new Intent(context, WordbookByCategoryActivity.class);
                intent.putExtra(Constant.BUNDLE.KEY_PRACTICE_ONLY, practiceOnly);
                intent.putExtra(Constant.BUNDLE.KEY_STUDY_LANG, studyLang);
                if (parentBookIds != null) {
                    intent.putExtra(Constant.BUNDLE.KEY_PARENT_BOOK_ID_LIST, new ArrayList<>(parentBookIds));
                }
                intent.putExtra(Constant.BUNDLE.KEY_AUTO_MOVE, autoMove);
                intent.putExtra(Constant.BUNDLE.KEY_SELECT_MULTI, selectMulti);
                autoMove = false;
            }
            intent.putExtra(Constant.BUNDLE.KEY_VOCA_BOOK, book);
            if (isStudyMode() || selectMulti) {
                openNewScreenForResult(intent, Constant.REQUEST_CODE.SELECT_BOOK);
            } else {
                openNewScreen(intent);
            }
        }
    };

    private void selectWordbook(VocaBook book) {
        if (book == selectedBook) {
            selectedBook.setChecked(false);
            adapter.notifyItemChanged(selectedBook);
            selectedBook = null;
            if (toolbar != null) {
                toolbar.hideRight();
            }
        } else {
            if (selectedBook != null) {
                selectedBook.setChecked(false);
                adapter.notifyItemChanged(selectedBook);
            }
            book.setChecked(true);
            adapter.notifyItemChanged(book);
            selectedBook = book;
            if (toolbar != null) {
                toolbar.showRight();
            }
        }
    }

    private void selectWordbooks(VocaBook book) {
        if (selectedBooks.contains(book)) {
            book.setChecked(false);
            adapter.notifyItemChanged(book);
            selectedBooks.remove(book);
            if (selectedBooks.isEmpty()) {
                if (toolbar != null) {
                    toolbar.hideRight();
                }
            }
        } else {
            book.setChecked(true);
            adapter.notifyItemChanged(book);
            selectedBooks.add(book);
            if (toolbar != null) {
                toolbar.showRight();
            }
        }
    }

    private void selectWordbookForStudyChat() {
        Intent data = new Intent();
        data.putExtra(Constant.BUNDLE.KEY_VOCA_BOOK_ID, selectedBook.getId());
        setResult(Activity.RESULT_OK, data);
        finish();
    }

    private void selectWordbooksForStudyChat() {
        Intent data = new Intent();
        data.putExtra(Constant.BUNDLE.KEY_VOCA_BOOKS, (Serializable) selectedBooks);
        setResult(Activity.RESULT_OK, data);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            setResult(Activity.RESULT_OK, data);
            finish();
        }
    }

    private boolean isStudyMode() {
        return studyLang > 0;
    }
}
