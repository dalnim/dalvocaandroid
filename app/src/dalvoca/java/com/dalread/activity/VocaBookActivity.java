package com.dalread.activity;

import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.UtteranceProgressListener;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.PopupMenu;
import android.widget.PopupWindow;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dalread.R;
import com.dalread.adapter.VocaBookAdapter;
import com.dalread.base.BaseDalVocaPlayVocaActivity;
import com.dalread.base.EnumLanguage;
import com.dalread.component.SeparatorDecoration;
import com.dalread.component.Toolbar;
import com.dalread.dialog.AlertDialog;
import com.dalread.dialog.RegisterVocaDialog;
import com.dalread.dialog.SetToAllPhrasesDialog;
import com.dalread.dialog.SingleChoiceDialog;
import com.dalread.helper.itemtouchhelper.ItemTouchHelperCallback;
import com.dalread.interfaces.IVocaBasicItem;
import com.dalread.listener.OnClickDialogListener;
import com.dalread.listener.OnDragListener;
import com.dalread.listener.OnKnowChangeListener;
import com.dalread.listener.OnVocaClickListener;
import com.dalread.model.VocaBook;
import com.dalread.model.VocaCountInBook;
import com.dalread.model.VocaInBook;
import com.dalread.network.DalApiListener;
import com.dalread.network.events.BaseEvent;
import com.dalread.network.events.SuccessEvent;
import com.dalread.network.models.AllBookListResponse;
import com.dalread.util.Constant;
import com.dalread.util.DLog;
import com.dalread.util.Loading;
import com.dalread.util.ToastUtil;
import com.dalread.util.Utils;
import com.dalread.util.Voca;
import com.dalread.util.VocaKnow;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.nikhilpanju.recyclerviewenhanced.RecyclerTouchListener;

import org.greenrobot.eventbus.Subscribe;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindColor;
import butterknife.BindDimen;
import butterknife.BindView;

public class VocaBookActivity extends BaseDalVocaPlayVocaActivity {

    @BindView(R.id.rv_voca)
    RecyclerView rvVoca;
    @BindView(R.id.nav_bottom)
    BottomNavigationView bottomNavigationView;

    @BindColor(R.color.color_divider)
    int clDivider;

    @BindDimen(R.dimen.divider_height)
    float dividerHeight;

    private VocaBook book;
    private boolean isUserBook;
    private LinearLayoutManager layoutManager;
    private RecyclerTouchListener recyclerTouchListener;
    protected VocaBookAdapter adapter;
    protected List<VocaInBook> vocas;
    private int loadingPos;
    private boolean isLoading;
    private boolean dataChanged;
    private RegisterVocaDialog registerVocaDialog;
    protected AlertDialog alertDialog;
    protected PopupMenu popupMenu;
    private List<VocaBook> userBooks;
    private String[] userBookNames;
    private SingleChoiceDialog userBooksDialog;
    private boolean vocaAddedOrRemoved;
    private boolean draggable;
    private int currentBottomNavigationId;
    private int loadingCount;
    private ArrayList<Object> grade1;
    private ArrayList<Object> grade2;
    private ArrayList<Object> grade99;
    private ArrayList<Object> known;
    private ArrayList<Object> unknown;
    private boolean canLoadMore;
    private SetToAllPhrasesDialog setToAllPhrasesDialog;
    private String allVocaIds;
    private String allVocaTypes;
    private boolean isBlinkMode;
    private boolean isAsteriskMode;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_voca_book;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        init();
        initBottomNavigation();
        initEventBus();

        getCount(false);
        getData(false);
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
        if (popupMenu != null) {
            popupMenu.show();
        } else if (setToAllPhrasesDialog != null) {
            setToAllPhrasesDialog.setBlinkMode(isBlinkMode);
            setToAllPhrasesDialog.setAsteriskMode(isAsteriskMode);
            setToAllPhrasesDialog.show();
        }
    }

    @Override
    public void onHeaderIconRightClick() {

    }

    @Override
    public void onHeaderTextRightClick() {

    }

    @Override
    protected void onResume() {
        super.onResume();

        if (dataChanged) {
            dataChanged = false;
            getCount(false);
            getData(false);
        }

        initPlayVocaHelper();

        if (isUserBook) {
            rvVoca.addOnItemTouchListener(recyclerTouchListener);
        }
    }

    @Override
    protected void onPause() {
        if (isUserBook) {
            rvVoca.removeOnItemTouchListener(recyclerTouchListener);
        }

        super.onPause();
    }

    @Override
    public void onBackPressed() {
        if (draggable) {
            adapter.setDraggable(draggable = false);
            adapter.notifyDataSetChanged();
        } else {
            if (vocaAddedOrRemoved) {
                application.getEventBus().post(new SuccessEvent(BaseEvent.Screen.MAIN, BaseEvent.EventType.DATA_CHANGED, true));
            }
            super.onBackPressed();
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

    private void init() {
        Intent intent = getIntent();
        if (intent != null) {
            Serializable serializable = intent.getSerializableExtra(Constant.BUNDLE.KEY_VOCA_BOOK);
            if (serializable != null) {
                book = (VocaBook) serializable;
                DLog.i(getLogTag(), "voca = " + book.getWordCount());
                isUserBook = Voca.isUserBook(book);
                if (isUserBook) {
                    toolbar.setTitle(R.string.user_wordbooks);
                } else {
                    toolbar.setTitle(R.string.server_wordbooks);
                }
                initPopupMenu(toolbar);
            }
            serializable = intent.getSerializableExtra(Constant.BUNDLE.KEY_VOCA_USER_BOOK_LIST);
            if (serializable == null) {
                getUserBooks();
            } else {
                initUserBookNames((List<VocaBook>) serializable);
            }
        }

        View popupView = getLayoutInflater().inflate(R.layout.item_search_vocabook, null);
        CheckBox cbTitle = popupView.findViewById(R.id.cb_title);
        CheckBox cbMeaning = popupView.findViewById(R.id.cb_meaning);
        cbTitle.setOnCheckedChangeListener(onCheckedChangeListener);
        cbMeaning.setOnCheckedChangeListener(onCheckedChangeListener);
        PopupWindow popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        initRecyclerView();
        toolbar.setPopupWindow(popupWindow);
        toolbar.setSearchListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                searchData(s);
                return true;
            }
        }, new SearchView.OnCloseListener() {

            @Override
            public boolean onClose() {
                searchData(Constant.BASE_BLANK);
                return true;
            }
        });
        toolbar.showSearchView();

//        initRecyclerView();

        registerVocaDialog = new RegisterVocaDialog(this, onKnowChangeListener);
        alertDialog = new AlertDialog(this);
        userBooksDialog = new SingleChoiceDialog(this);
        grade1 = new ArrayList<>();
        grade2 = new ArrayList<>();
        grade99 = new ArrayList<>();
        known = new ArrayList<>();
        unknown = new ArrayList<>();
    }

    protected void initPopupMenu(Toolbar toolbar) {
        toolbar.setIconRight(R.drawable.ic_more_vert_white_24dp);
        if (isUserBook) {
            popupMenu = new PopupMenu(this, toolbar.getIconRight());
            popupMenu.getMenuInflater().inflate(R.menu.menu_voca_book, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(onMenuItemClickListener);
        } else {
            setToAllPhrasesDialog = new SetToAllPhrasesDialog(this, onAllGradeChangeListener);
        }
    }

    protected void initRecyclerView() {
        adapter = new VocaBookAdapter(this, sharedPreferences.getDisplayPronunciation());
        isBlinkMode = sharedPreferences.getBlinkModeInWordList();
        isAsteriskMode = sharedPreferences.getAsteriskModeInWordList();
        adapter.setBlinkMode(isBlinkMode);
        adapter.setAsteriskMode(isAsteriskMode);
        final ItemTouchHelper itemTouchHelper = new ItemTouchHelper(
                new ItemTouchHelperCallback(adapter)
        );
        itemTouchHelper.attachToRecyclerView(rvVoca);
        adapter.setOnDragListener(new OnDragListener() {

            @Override
            public void onDragStarted(RecyclerView.ViewHolder viewHolder) {
                itemTouchHelper.startDrag(viewHolder);
            }

            @Override
            public void onDragStopped() {
            }
        });
        adapter.setData(book, vocas = new ArrayList<>());
        adapter.setOnVocaClickListener(onVocaClickListener);
        rvVoca.setAdapter(adapter);
        rvVoca.setLayoutManager(layoutManager = new LinearLayoutManager(this));
        rvVoca.addItemDecoration(new SeparatorDecoration(this, clDivider, dividerHeight));
        rvVoca.addOnScrollListener(onScrollListener);
        recyclerTouchListener = new RecyclerTouchListener(this, rvVoca)
                .setSwipeOptionViews(R.id.tv_delete)
                .setSwipeable(R.id.v_foreground, R.id.v_background, new RecyclerTouchListener.OnSwipeOptionsClickListener() {

                    @Override
                    public void onSwipeOptionClicked(int viewId, int position) {
                        boolean notifyItemRemoved = currentBottomNavigationId != R.id.nav_grade;
                        VocaInBook voca = (VocaInBook) adapter.removeVoca(position, notifyItemRemoved);
                        vocas.remove(voca);
                        if (!notifyItemRemoved) {
                            sortData(false);
                        }
                        removeVocaFromUserBook(voca);
                    }
                });
    }

    private void initBottomNavigation() {
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id != currentBottomNavigationId) {
                    currentBottomNavigationId = id;
                    sortData(true);
                    if (popupMenu != null) {
                        MenuItem sortingItem = popupMenu.getMenu().findItem(R.id.edit_sorting);
                        if (sortingItem != null) {
                            sortingItem.setVisible(id == R.id.nav_original);
                        }
                    }
                    return true;
                }
                return false;
            }
        });
        currentBottomNavigationId = R.id.nav_original;
    }

    private void getCount(final boolean isChangeGrade) {
        if (book != null) {
            int uid = getUserID();
            if (uid > 0 && Utils.isConnected(this)) {
                if (!isChangeGrade) {
                    loadingCount++;
                }
                application.getDalAiImpl().getVocasKnownWordCountFromAllVocaBook(
                        String.valueOf(uid),
                        sharedPreferences.getLangStudyCode(),
                        String.valueOf(book.getId()),
                        Voca.getBookType(isUserBook),
                        new DalApiListener<VocaCountInBook>() {

                            @Override
                            public void onSuccess(VocaCountInBook response) {
                                book.setCountInBook(response);
                                if (response != null) {
                                    book.setWordCount(response.getAllWordCount());
                                }
                                if (isChangeGrade) {
                                    adapter.notifyItemChanged(0);
                                } else {
                                    onInitDataFinish();
                                }
                            }

                            @Override
                            public void onFailure(String error) {
                                if (!isChangeGrade) {
                                    onInitDataFinish();
                                }
                            }
                        }
                );
            }
        }
    }

    private void getData(final boolean isLoadMore) {
        if (book != null) {
            final int uid = getUserID();
            if (uid > 0) {
                if (Utils.isConnected(this)) {
                    loadingCount++;
                    isLoading = true;
                    rvVoca.post(new Runnable() {

                        @Override
                        public void run() {
                            if (isLoadMore) {
                                adapter.setLoadMore(true);
                            } else {
                                vocas.clear();
                                allVocaIds = "";
                                allVocaTypes = "";
                                loadingPos = 0;
                                Loading.show(VocaBookActivity.this);
                            }
                            application.getDalAiImpl().getVocasFromAllVocaBook(
                                    String.valueOf(uid),
                                    sharedPreferences.getLangStudyCode(),
                                    sharedPreferences.getMotherTongueLangCode(),
                                    String.valueOf(book.getId()),
                                    Voca.getBookType(isUserBook),
                                    loadingPos,
                                    Constant.LOADING_MAX_ITEM + 1,
                                    Constant.MAKE_RUBY_TEXT ? 1 : 0,
                                    new DalApiListener<List<VocaInBook>>() {

                                        @Override
                                        public void onSuccess(List<VocaInBook> response) {
                                            isLoading = false;
                                            if (response.size() == Constant.LOADING_MAX_ITEM + 1) {
                                                response.remove(Constant.LOADING_MAX_ITEM);
                                                canLoadMore = true;
                                            }
                                            int studyLang = EnumLanguage.findByFormatApi(sharedPreferences.getStudyLanguage()).getIdApi();
                                            for (VocaInBook voca : response) {
                                                voca.setPath(Voca.getOutputRecordingFileName(studyLang, voca.getVocaType(), voca.getVocaId(), uid));
                                                vocas.add(voca);
                                                allVocaIds += "," + voca.getVocaId();
                                                allVocaTypes += "," + voca.getVocaType();
                                            }
                                            if (!TextUtils.isEmpty(allVocaIds) && allVocaIds.startsWith(",")) {
                                                allVocaIds = allVocaIds.substring(1);
                                            }
                                            if (!TextUtils.isEmpty(allVocaTypes) && allVocaTypes.startsWith(",")) {
                                                allVocaTypes = allVocaTypes.substring(1);
                                            }
                                            loadingPos = vocas.size();
                                            DLog.i(getLogTag(), "loadingPos = " + loadingPos);
                                            if (isLoadMore) {
                                                adapter.setLoadMore(false);
                                            } else {
                                                Loading.hide();
                                            }
                                            onInitDataFinish();
                                        }

                                        @Override
                                        public void onFailure(String error) {
                                            isLoading = false;
                                            if (isLoadMore) {
                                                adapter.setLoadMore(false);
                                            } else {
                                                Loading.hide();
                                            }
                                            onInitDataFinish();
                                        }
                                    }
                            );
                        }
                    });
                } else {
                    alertDialog.showNoInternet();
                }
            } else {
                alertDialog.showLogInRequired();
            }
        }
    }

    private void getUserBooks() {
        int uid = getUserID();
        if (uid > 0 && Utils.isConnected(this)) {
            application.getDalAiImpl().getAllVocaBookList(
                    String.valueOf(uid),
                    sharedPreferences.getLangStudyCode(),
                    sharedPreferences.getMotherTongueLangCode(),
                    new DalApiListener<AllBookListResponse>() {

                        @Override
                        public void onSuccess(AllBookListResponse response) {
                            initUserBookNames(response.getUserBooks());
                        }

                        @Override
                        public void onFailure(String error) {
                        }
                    }
            );
        }
    }

    private void initUserBookNames(List<VocaBook> books) {
        userBooks = books;
        int count = userBooks.size();
        userBookNames = new String[count];
        for (int i = 0; i < count; i++) {
            VocaBook userBook = userBooks.get(i);
            userBookNames[i] = userBook.getName();
        }
    }

    private void onInitDataFinish() {
        if (--loadingCount <= 0) {
            sortData(false);
        }
    }

    private void setUnswipeableRows() {
        ArrayList<Integer> unswipeableRows = new ArrayList<>();
        int count = adapter.getItemCount();
        for (int i = 0; i < count; i++) {
            if (!adapter.isSwipeable(i)) {
                unswipeableRows.add(i);
            }
        }
        recyclerTouchListener.setUnSwipeableRows(unswipeableRows.toArray(new Integer[unswipeableRows.size()]));
    }

    private void initPlayVocaHelper() {
        if (!playVocaHelper.hasMotherTongueListener()) {
            playVocaHelper.setMotherTongueListener(new UtteranceProgressListener() {

                @Override
                public void onStart(String utteranceId) {
                    updateItemStatus(utteranceId, true);
                }

                @Override
                public void onDone(String utteranceId) {
                }

                @Override
                public void onError(String utteranceId) {
                }
            });
        }
        if (!playVocaHelper.hasStudyListener()) {
            playVocaHelper.setStudyListener(new UtteranceProgressListener() {

                @Override
                public void onStart(String utteranceId) {
                }

                @Override
                public void onDone(String utteranceId) {
                    updateItemStatus(utteranceId, false);
                }

                @Override
                public void onError(String utteranceId) {
                    updateItemStatus(utteranceId, false);
                }
            });
        }
    }

    private void updateItemStatus(String utteranceId, boolean playing) {
        for (final VocaInBook voca : vocas) {
            if (utteranceId.equals(String.valueOf(voca.getVIId()))) {
                voca.setVIPlaying(playing);
                rvVoca.post(new Runnable() {

                    @Override
                    public void run() {
                        adapter.notifyRegisteredOrRemoved(voca);
                    }
                });
                break;
            }
        }
    }

    private OnVocaClickListener onVocaClickListener = new OnVocaClickListener() {

        @Override
        public void onPlayAllClick() {
            Intent intent = new Intent(VocaBookActivity.this, PlaylistActivity.class);
            intent.putExtra(Constant.BUNDLE.KEY_VOCA_PLAYLIST, (Serializable) vocas);
            intent.putExtra(Constant.BUNDLE.KEY_VOCA_BOOK_NAME, book.getName());
            openNewScreen(intent);
        }

        @Override
        public void onPlayAllClick(int vocaKnow) {
            Intent intent = new Intent(VocaBookActivity.this, PlaylistActivity.class);
            if (vocaKnow == Constant.VOCA_KNOW.VOCA_KNOW_AMKI_GRADE_1) {
                intent.putExtra(Constant.BUNDLE.KEY_VOCA_PLAYLIST, grade1);
            } else if (vocaKnow == Constant.VOCA_KNOW.VOCA_KNOW_AMKI_GRADE_2) {
                intent.putExtra(Constant.BUNDLE.KEY_VOCA_PLAYLIST, grade2);
            } else if (vocaKnow == Constant.VOCA_KNOW.VOCA_KNOW_UNKNOWN) {
                intent.putExtra(Constant.BUNDLE.KEY_VOCA_PLAYLIST, grade99);
            } else if (vocaKnow == Constant.VOCA_KNOW.VOCA_KNOW_KNOWN) {
                intent.putExtra(Constant.BUNDLE.KEY_VOCA_PLAYLIST, known);
            } else {
                intent.putExtra(Constant.BUNDLE.KEY_VOCA_PLAYLIST, unknown);
            }
            intent.putExtra(Constant.BUNDLE.KEY_VOCA_BOOK_NAME, book.getName());
            openNewScreen(intent);
        }

        @Override
        public void onPlayClick(VocaInBook voca) {
            boolean isPlaying = voca.isVIPlaying();
            playVocaHelper.stop();
            if (!isPlaying) {
                preparePlayVoca(voca);
            }
        }

        @Override
        public void onVocaKnowClick(VocaInBook voca) {
            registerVocaDialog.show(voca);
        }

        @Override
        public void onVocaKnowClick(VocaInBook voca, int vocaKnow) {
            onKnowChangeListener.onVocaKnowChange(voca, vocaKnow);
        }

        @Override
        public void onInfoClick(VocaInBook voca) {
            handleInfoClick(voca);
        }

        @Override
        public void onDisplayOrderChange(VocaInBook voca, int newOrder) {
            updateDisplayOrder(voca, newOrder);
        }
    };

    protected void handleInfoClick(VocaInBook voca) {
        Intent intent;
        intent = new Intent(this, WordInfoActivity.class);
        intent.putExtra(Constant.BUNDLE.KEY_VOCA_ID, voca.getVocaId());
        intent.putExtra(Constant.BUNDLE.KEY_VOCA_TYPE, voca.getVocaType());
        openNewScreen(intent);
    }

    private RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);

            if (!isLoading && canLoadMore) {
                int count = layoutManager.getItemCount();
                int last = layoutManager.findLastVisibleItemPosition();
                if (count <= last + 2) { // load more when scrolled to the second-last item
                    DLog.i(getLogTag(), "count = " + count);
                    DLog.i(getLogTag(), "last + 2 = " + (last + 2));
                    canLoadMore = false;
                    getData(true);
                }
            }
        }
    };

    private OnKnowChangeListener onKnowChangeListener = new OnKnowChangeListener() {
        @Override
        public void onVocaKnowChange(IVocaBasicItem iVocaBasicItem, int newVocaKnow) {

        }

        @Override
        public void onVocaKnowPronounceChange(IVocaBasicItem iVocaBasicItem, int newVocaKnowPronounce) {

        }

        @Override
        public void onAddToWordbook(IVocaBasicItem iVocaBasicItem) {

        }

        @Override
        public void onAddToBookmark(IVocaBasicItem iVocaBasicItem) {

        }

        @Override
        public void onDeleteFromBookmark(IVocaBasicItem iVocaBasicItem) {

        }

        @Override
        public void onDismiss() {

        }

        //TODO : use new interface methods
//        @Override
//        public void onVocaKnowChange(AmkiItem voca, int vocaKnow) {
//            if (voca instanceof VocaInBook) {
//                changeVocaKnow((VocaInBook) voca, vocaKnow);
//            }
//        }
//
//        @Override
//        public void onVocaKnowPronounceChange(AmkiItem voca, int vocaKnowPronounce) {
//            if (voca instanceof VocaInBook) {
//                VocaInBook vocaInBook = (VocaInBook) voca;
//                vocaInBook.setVocaKnowPronounce(vocaKnowPronounce);
//                adapter.notifyRegisteredOrRemoved(vocaInBook);
//                changeVocaKnow(vocaInBook, vocaInBook.getVocaKnow(), vocaKnowPronounce, null);
//            }
//        }
//
//        @Override
//        public void onAddToWordbook(AmkiItem voca) {
//            if (voca instanceof VocaInBook) {
//                showUserBooks((VocaInBook) voca);
//            }
//        }
    };

    private void showUserBooks(final VocaInBook voca) {
        if (userBooks == null || userBooks.isEmpty()) {
            ToastUtil.getInstance(this).show(R.string.msg_no_user_book);
        } else {
            userBooksDialog.show(
                    R.string.add_to_book,
                    userBookNames,
                    -1,
                    R.string.ok,
                    R.string.cancel,
                    new OnClickDialogListener() {
                        @Override
                        public void onClick(View view, Object object) {
                            final int which = (int) object;
                            addVocaToUserBook(voca, userBooks.get(which));
                        }

                        @Override
                        public void onDismiss(View view, Object object) {

                        }
                    }
            );
        }
    }

    private void addVocaToUserBook(VocaInBook voca, VocaBook userBook) {
        int uid = getUserID();
        if (uid > 0) {
            if (Utils.isConnected(this)) {
                application.getDalAiImpl().addVocaInUserVocaBook(
                        String.valueOf(uid),
                        sharedPreferences.getLangStudyCode(),
                        String.valueOf(userBook.getId()),
                        String.valueOf(voca.getVocaId()),
                        voca.getVocaType(),
                        new DalApiListener<Boolean>() {

                            @Override
                            public void onSuccess(Boolean response) {
                                if (response) {
                                    ToastUtil.getInstance(VocaBookActivity.this).show(R.string.msg_add_to_book_success);
                                    vocaAddedOrRemoved = true;
                                } else {
                                    ToastUtil.getInstance(VocaBookActivity.this).show(R.string.msg_add_to_book_failed);
                                }
                            }

                            @Override
                            public void onFailure(String error) {
                                ToastUtil.getInstance(VocaBookActivity.this).show(R.string.msg_add_to_book_failed);
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

    private void changeVocaKnow(VocaInBook voca, int vocaKnow) {
        changeVocaKnow(
                voca,
                vocaKnow,
                VocaKnow.getVocaKnowPronounceByVocaKnow(vocaKnow),
                new DalApiListener<Integer>() {

                    @Override
                    public void onSuccess(Integer newVocaKnow) {
                        voca.setVocaKnow(newVocaKnow);
                        voca.setVocaKnowPronounce(VocaKnow.getVocaKnowPronounceByVocaKnow(newVocaKnow));
                        adapter.notifyRegisteredOrRemoved(voca);
                        if (book.getCountInBook() != null) {
                            int newKnownWordCount = book.getCountInBook().getKnownWordCount()
                                    + (newVocaKnow == Constant.VOCA_KNOW.VOCA_KNOW_KNOWN ? 1 : -1);
                            book.getCountInBook().setKnownWordCount(newKnownWordCount);
                            adapter.notifyItemChanged(0);
                        }
                        if (currentBottomNavigationId == R.id.nav_grade) {
                            sortData(false);
                        }
                    }

                    @Override
                    public void onFailure(String error) {
                    }
                }
        );
    }

    private void changeVocaKnow(VocaInBook voca, int vocaKnow, int vocaKnowPronounce, DalApiListener<Integer> listener) {
        int uid = getUserID();
        if (uid > 0) {
            if (Utils.isConnected(this)) {
                application.getDalAiImpl().checkAndChangeVocaKnow(
                        this,
                        vocaKnow,
                        vocaKnowPronounce,
                        String.valueOf(voca.getVocaId()),
                        voca.getVocaType(),
                        listener
                );
            } else {
                alertDialog.showNoInternet();
            }
        } else {
            alertDialog.showLogInRequired();
        }
    }

    private PopupMenu.OnMenuItemClickListener onMenuItemClickListener = new PopupMenu.OnMenuItemClickListener() {

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            int id = item.getItemId();
            switch (id) {
                case R.id.from_dictionary:
                    Intent intent = new Intent(VocaBookActivity.this, AddVocaActivity.class);
                    if (book != null) {
                        intent.putExtra(Constant.BUNDLE.KEY_VOCA_BOOK_ID, book.getId());
                    }
                    openNewScreen(intent);
                    return true;
                case R.id.from_wordbook:
                    intent = new Intent(VocaBookActivity.this, SelectVocaBookInVocaBooksActivity.class);
                    if (book != null) {
                        intent.putExtra(Constant.BUNDLE.KEY_VOCA_BOOK_ID, book.getId());
                    }
                    openNewScreen(intent);
                    return true;
                case R.id.edit_sorting:
                    adapter.setDraggable(draggable = !draggable);
                    adapter.notifyDataSetChanged();
                    return true;
            }
            return false;
        }
    };

    private void updateDisplayOrder(VocaInBook voca, int newOrder) {
        if (Utils.isConnected(this)) {
            int uid = getUserID();
            if (uid > 0) {
                application.getDalAiImpl().changeVocaDisplayOrderInUserVocaBook(
                        String.valueOf(uid),
                        sharedPreferences.getLangStudyCode(),
                        String.valueOf(book.getId()),
                        String.valueOf(voca.getVocaId()),
                        voca.getVocaType(),
                        newOrder,
                        null
                );
            } else {
                alertDialog.showLogInRequired();
            }
        } else {
            alertDialog.showNoInternet();
        }
    }

    private void sortData(boolean scrollToTop) {
        if (currentBottomNavigationId == R.id.nav_alphabet) {
            Collections.sort(vocas, (o1, o2) -> {
                String n1 = Voca.getVocaDisplay(o1).toLowerCase();
                String n2 = Voca.getVocaDisplay(o2).toLowerCase();
                return n1.compareTo(n2);
            });
            adapter.setData(book, vocas);
        } else if (currentBottomNavigationId == R.id.nav_grade) {
            Collections.sort(vocas, (o1, o2) -> {
                String n1 = Voca.getVocaDisplay(o1).toLowerCase();
                String n2 = Voca.getVocaDisplay(o2).toLowerCase();
                return n1.compareTo(n2);
            });
            grade1.clear();
            grade2.clear();
            grade99.clear();
            known.clear();
            unknown.clear();
            for (VocaInBook voca : vocas) {
                int vocaKnow = voca.getVocaKnow();
                if (vocaKnow == Constant.VOCA_KNOW.VOCA_KNOW_KNOWN) {
                    known.add(voca);
                } else if (vocaKnow == Constant.VOCA_KNOW.VOCA_KNOW_AMKI_GRADE_1) {
                    grade1.add(voca);
                } else if (vocaKnow == Constant.VOCA_KNOW.VOCA_KNOW_AMKI_GRADE_2) {
                    grade2.add(voca);
                } else if (vocaKnow == Constant.VOCA_KNOW.VOCA_KNOW_UNKNOWN) {
                    grade99.add(voca);
                } else {
                    unknown.add(voca);
                }
            }
            adapter.setData(book, grade1, grade2, grade99, known, unknown);
        } else {
            Collections.sort(vocas, (o1, o2) -> Integer.compare(o1.getDisplayOrder(), o2.getDisplayOrder()));
            adapter.setData(book, vocas);
        }
        adapter.notifyDataSetChanged();
        if (scrollToTop) {
            rvVoca.post(() -> layoutManager.smoothScrollToPosition(rvVoca, null, 0));
        }
        setUnswipeableRows();
    }

    private void removeVocaFromUserBook(VocaInBook voca) {
        if (book != null) {
            int uid = getUserID();
            if (uid > 0 && Utils.isConnected(this)) {
                application.getDalAiImpl().deleteVocaInUserVocaBook(
                        String.valueOf(uid),
                        sharedPreferences.getLangStudyCode(),
                        String.valueOf(book.getId()),
                        String.valueOf(voca.getVocaId()),
                        voca.getVocaType(),
                        new DalApiListener<Boolean>() {

                            @Override
                            public void onSuccess(Boolean response) {
                                if (response) {
                                    vocaAddedOrRemoved = true;
                                    getCount(true);
                                }
                            }

                            @Override
                            public void onFailure(String error) {
                            }
                        }
                );
            }
        }
    }

    private SetToAllPhrasesDialog.OnVocaKnowChangeListener onAllGradeChangeListener = vocaKnow -> {
        switch (vocaKnow) {
            case Constant.AMKI_GRADE.VALUE_BLINK:
                updateBlinkMode();
                break;
            case Constant.AMKI_GRADE.VALUE_ASTERISK:
                updateAsteriskMode();
                break;
            case Constant.AMKI_GRADE.VALUE_BACK_TO_HOME:
                backToHome();
                break;
            default:
                changeAllVocaKnow(vocaKnow);
                break;
        }
    };

    private void changeAllVocaKnow(int vocaKnow) {
        int uid = getUserID();
        if (uid > 0) {
            if (Utils.isConnected(this)) {
                application.getDalAiImpl().changeMultipleVocaKnow(
                        this,
                        vocaKnow,
                        VocaKnow.getVocaKnowPronounceByVocaKnow(vocaKnow),
                        allVocaIds,
                        allVocaTypes,
                        new DalApiListener<Integer>() {

                            @Override
                            public void onSuccess(Integer newVocaKnow) {
                                if (book.getCountInBook() != null) {
                                    int newKnownWordCount = newVocaKnow == Constant.VOCA_KNOW.VOCA_KNOW_KNOWN
                                            ? book.getCountInBook().getAllWordCount()
                                            : 0;
                                    book.getCountInBook().setKnownWordCount(newKnownWordCount);
                                }
                                for (VocaInBook voca : vocas) {
                                    voca.setVocaKnow(newVocaKnow);
                                    voca.setVocaKnowPronounce(VocaKnow.getVocaKnowPronounceByVocaKnow(newVocaKnow));
                                }
                                sortData(false);
                            }

                            @Override
                            public void onFailure(String error) {
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

    private void updateBlinkMode() {
        isBlinkMode = !isBlinkMode;
        setToAllPhrasesDialog.setBlinkMode(isBlinkMode);
        adapter.setBlinkMode(isBlinkMode);
        sharedPreferences.setBlinkModeInWordList(isBlinkMode);
    }

    private void updateAsteriskMode() {
        isAsteriskMode = !isAsteriskMode;
        setToAllPhrasesDialog.setAsteriskMode(isAsteriskMode);
        adapter.setAsteriskMode(isAsteriskMode);
        sharedPreferences.setAsteriskModeInWordList(isAsteriskMode);
    }

    /**
     * show dialog confirm when tap to "Back to Home"
     */
    private void backToHome() {
//        final ConfirmationDialog confirmDialog = new ConfirmationDialog(this,
//                R.string.back_to_home_dialog_title,
//                R.string.back_to_home_dialog_msg,
//                R.string.back_to_home_dialog_yes_btn,
//                R.string.back_to_home_dialog_no_btn,
//                new ConfirmationDialog.OnDialogClickListener() {
//                    @Override
//                    public void onPositive(DialogInterface dialog) {
//                        dialog.dismiss();
        openNewScreen(MainHomeActivity.class);
        finishAffinity();
//                    }
//
//                    @Override
//                    public void onNegative(DialogInterface dialog) {
//                        dialog.dismiss();
//                    }
//                });
//        confirmDialog.show();
    }

    private CompoundButton.OnCheckedChangeListener onCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            int id = buttonView.getId();
            switch (id) {
                case R.id.cb_title:
                    if (canCheckValue(isChecked)) {
                        VocaBookActivity.this.adapter.setSearchTitle(isChecked);
                        searchData(adapter.getSearchValue());
                    } else {
                        buttonView.setChecked(true);
                    }
                    break;
                case R.id.cb_meaning:
                    if (canCheckValue(isChecked)) {
                        VocaBookActivity.this.adapter.setSearchMeaning(isChecked);
                        searchData(adapter.getSearchValue());
                    } else {
                        buttonView.setChecked(true);
                    }
                    break;
            }
        }
    };

    private boolean canCheckValue(boolean isChecked) {
        DLog.d(getLogTag(), "isChecked=" + isChecked);
        if (isChecked)
            return true;
        DLog.d(getLogTag(), "isSearchTitle=" + adapter.isSearchTitle());
        DLog.d(getLogTag(), "isSearchMeaning=" + adapter.isSearchMeaning());
        return adapter.isSearchTitle() && adapter.isSearchMeaning();
    }

    private void searchData(String keyword) {
        VocaBookActivity.this.adapter.setSearchValue(keyword);
        sortData(false);
    }
}
