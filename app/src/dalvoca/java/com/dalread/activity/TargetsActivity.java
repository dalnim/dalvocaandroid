package com.dalread.activity;

import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.UtteranceProgressListener;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.CompoundButton;
import android.widget.PopupWindow;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.dalread.R;
import com.dalread.adapter.TargetsAdapter;
import com.dalread.base.BaseDalVocaPlayVocaActivity;
import com.dalread.base.EnumLanguage;
import com.dalread.component.SeparatorDecoration;
import com.dalread.dialog.AlertDialog;
import com.dalread.dialog.RegisterVocaDialog;
import com.dalread.dialog.SingleChoiceDialog;
import com.dalread.interfaces.IVocaBasicItem;
import com.dalread.listener.OnClickDialogListener;
import com.dalread.listener.OnKnowChangeListener;
import com.dalread.listener.OnTargetsClickListener;
import com.dalread.model.VocaBook;
import com.dalread.model.VocaMemorize;
import com.dalread.model.VocaSearchOption;
import com.dalread.network.DalApiListener;
import com.dalread.network.models.AllBookListResponse;
import com.dalread.util.Constant;
import com.dalread.util.Loading;
import com.dalread.util.ToastUtil;
import com.dalread.util.Utils;
import com.dalread.util.Voca;
import com.dalread.util.VocaKnow;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindColor;
import butterknife.BindDimen;
import butterknife.BindView;
import io.realm.Realm;
import io.realm.RealmQuery;

public class TargetsActivity extends BaseDalVocaPlayVocaActivity {

    @BindView(R.id.v_refresh)
    SwipeRefreshLayout vRefresh;

    @BindView(R.id.rv_voca)
    RecyclerView rvVoca;

    @BindColor(R.color.color_divider)
    int clDivider;

    @BindDimen(R.dimen.divider_height)
    float dividerHeight;

    private TargetsAdapter adapter;
    private List<VocaMemorize> grade1;
    private List<VocaMemorize> grade2;
    private List<VocaMemorize> grade99;
    private VocaSearchOption searchOption;
    private RegisterVocaDialog registerVocaDialog;
    private AlertDialog alertDialog;
    private ArrayList<VocaBook> userBooks;
    private String[] userBookNames;
    private SingleChoiceDialog userBooksDialog;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_targets;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initToolbar();
        initRecyclerView();
        initSwipeRefreshLayout();
        initSearchOption();
        initSearchView();
        initDialog();

        getBookList();
        getDataFromServer();
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

    @Override
    public void onResume() {
        super.onResume();

        initPlayVocaHelper();
    }

    @Override
    protected void onDestroy() {
        if (toolbar != null) {
            toolbar.clearSearchListener();
            toolbar.hideSearchView();
        }

        super.onDestroy();
    }

    private void initToolbar() {
        toolbar.setIconLeft(R.drawable.ic_back);
        toolbar.setTitle(R.string.nav_title_targets);
    }

    private void initRecyclerView() {
        adapter = new TargetsAdapter(this, sharedPreferences.getDisplayPronunciation(), false);
        adapter.setListener(listener);
        rvVoca.setAdapter(adapter);
        rvVoca.setLayoutManager(new LinearLayoutManager(this));
        rvVoca.addItemDecoration(new SeparatorDecoration(this, clDivider, dividerHeight));
    }

    private void initSwipeRefreshLayout() {
        vRefresh.setColorSchemeResources(R.color.colorBlue);
        vRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                getDataFromServer();
                vRefresh.setRefreshing(false);
            }
        });
    }

    private void initSearchOption() {
        searchOption = new VocaSearchOption();
        searchOption.setWhere(Constant.API_VALUE.VALUE_SEARCH_IN_VOCA);
    }

    private void initSearchView() {
        View popupView = getLayoutInflater().inflate(R.layout.item_search_memorize, null);
        CompoundButton cb = popupView.findViewById(R.id.rb_title);
        cb.setOnCheckedChangeListener(onCheckedChangeListener);
        cb = popupView.findViewById(R.id.rb_start_with);
        cb.setOnCheckedChangeListener(onCheckedChangeListener);
        cb = popupView.findViewById(R.id.rb_end_with);
        cb.setOnCheckedChangeListener(onCheckedChangeListener);
        PopupWindow popupWindow = new PopupWindow(popupView, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        toolbar.setPopupWindow(popupWindow);
        toolbar.setSearchListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                searchOption.setText(s);
                getDataFromLocal();
                return true;
            }
        }, new SearchView.OnCloseListener() {

            @Override
            public boolean onClose() {
                searchOption.setText("");
                getDataFromLocal();
                return true;
            }
        });
        toolbar.showSearchView();
    }

    private void initDialog() {
        registerVocaDialog = new RegisterVocaDialog(this, onKnowChangeListener);
        alertDialog = new AlertDialog(this);
        userBooksDialog = new SingleChoiceDialog(this);
    }

    private void getBookList() {
        int uid = sharedPreferences.getRealUid();
        if (uid > 0 && Utils.isConnected(this)) {
            application.getDalAiImpl().getAllVocaBookList(
                    String.valueOf(uid),
                    sharedPreferences.getLangStudyCode(),
                    sharedPreferences.getMotherTongueLangCode(),
                    new DalApiListener<AllBookListResponse>() {

                        @Override
                        public void onSuccess(AllBookListResponse response) {
                            userBooks = response.getUserBooks();
                            int count = userBooks.size();
                            userBookNames = new String[count];
                            for (int i = 0; i < count; i++) {
                                VocaBook userBook = userBooks.get(i);
                                userBookNames[i] = userBook.getName();
                            }
                        }

                        @Override
                        public void onFailure(String error) {
                        }
                    }
            );
        }
    }

    private void getDataFromServer() {
        int uid = getUserID();
        if (uid > 0) {
            if (Utils.isConnected(this)) {
                Loading.show(this);
                application.getDalAiImpl().getVocasFromTargetVoca(
                        String.valueOf(uid),
                        sharedPreferences.getLangStudyCode(),
                        sharedPreferences.getMotherTongueLangCode(),
                        new DalApiListener<List<VocaMemorize>>() {

                            @Override
                            public void onSuccess(final List<VocaMemorize> response) {
                                Loading.hide();
                                Voca.executeRealmTransaction(new Realm.Transaction() {

                                    @Override
                                    public void execute(@NonNull Realm realm) {
                                        realm.delete(VocaMemorize.class);
                                        realm.copyToRealm(response);
                                    }
                                });
                                getDataFromLocal();
                            }

                            @Override
                            public void onFailure(String error) {
                                Loading.hide();
                                getDataFromLocal();
                            }
                        }
                );
            } else {
                alertDialog.showNoInternet();
                getDataFromLocal();
            }
        } else {
            alertDialog.showLogInRequired();
        }
    }

    private void getDataFromLocal() {
        Voca.executeRealmTransaction(new Realm.Transaction() {

            @Override
            public void execute(@NonNull Realm realm) {
                RealmQuery<VocaMemorize> query = realm.where(VocaMemorize.class)
                        .equalTo(Constant.VOCA.KEY_VOCA_KNOW, Constant.VOCA_KNOW.VOCA_KNOW_AMKI_GRADE_1);
                updateQuery(query);
                grade1 = realm.copyFromRealm(query.findAll());

                query = realm.where(VocaMemorize.class)
                        .equalTo(Constant.VOCA.KEY_VOCA_KNOW, Constant.VOCA_KNOW.VOCA_KNOW_AMKI_GRADE_2);
                updateQuery(query);
                grade2 = realm.copyFromRealm(query.findAll());

                query = realm.where(VocaMemorize.class)
                        .equalTo(Constant.VOCA.KEY_VOCA_KNOW, Constant.VOCA_KNOW.VOCA_KNOW_UNKNOWN);
                updateQuery(query);
                grade99 = realm.copyFromRealm(query.findAll());
            }
        });
        if (grade1.isEmpty() && grade2.isEmpty() && grade99.isEmpty()) {
            bindEmptyData();
        } else {
            setVocaPath();
            bindData();
        }
    }

    private void updateQuery(RealmQuery<VocaMemorize> query) {
        String text = searchOption.getText();
        if (!TextUtils.isEmpty(text)) {
            if (Constant.API_VALUE.VALUE_SEARCH_IN_VOCA.equals(searchOption.getWhere())) {
                if (searchOption.isStartWith()) {
                    query.beginsWith(Constant.VOCA.KEY_VOCA, text);
                    if (searchOption.isEndWith()) {
                        query.endsWith(Constant.VOCA.KEY_VOCA, text);
                    }
                } else if (searchOption.isEndWith()) {
                    query.endsWith(Constant.VOCA.KEY_VOCA, text);
                } else if (searchOption.useLike()) {
                    query.like(Constant.VOCA.KEY_VOCA, text);
                } else {
                    query.contains(Constant.VOCA.KEY_VOCA, text);
                }
            } else {
                if (searchOption.isStartWith()) {
                    query.beginsWith(Constant.VOCA.KEY_MEANING, text);
                    if (searchOption.isEndWith()) {
                        query.endsWith(Constant.VOCA.KEY_MEANING, text);
                    }
                } else if (searchOption.isEndWith()) {
                    query.endsWith(Constant.VOCA.KEY_MEANING, text);
                } else if (searchOption.useLike()) {
                    query.like(Constant.VOCA.KEY_MEANING, text);
                } else {
                    query.contains(Constant.VOCA.KEY_MEANING, text);
                }
            }
        }
    }

    private void setVocaPath() {
        int studyLang = EnumLanguage.findByFormatApi(sharedPreferences.getStudyLanguage()).getIdApi();
        int uid = sharedPreferences.getRealUid();
        for (VocaMemorize voca : grade1) {
            voca.setPath(Voca.getOutputRecordingFileName(studyLang, voca.getVocaType(), voca.getVocaId(), uid));
        }
        for (VocaMemorize voca : grade2) {
            voca.setPath(Voca.getOutputRecordingFileName(studyLang, voca.getVocaType(), voca.getVocaId(), uid));
        }
        for (VocaMemorize voca : grade99) {
            voca.setPath(Voca.getOutputRecordingFileName(studyLang, voca.getVocaType(), voca.getVocaId(), uid));
        }
    }

    private void bindData() {
        adapter.setData(grade1, grade2, grade99);
        adapter.notifyDataSetChanged();
    }

    private void bindEmptyData() {
        grade1 = null;
        grade2 = null;
        grade99 = null;
        bindData();
    }

    private void initPlayVocaHelper() {
        if (!getPlayVocaHelper().hasMotherTongueListener()) {
            getPlayVocaHelper().setMotherTongueListener(new UtteranceProgressListener() {

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
        if (!getPlayVocaHelper().hasStudyListener()) {
            getPlayVocaHelper().setStudyListener(new UtteranceProgressListener() {

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
        for (final VocaMemorize voca : grade1) {
            if (utteranceId.equals(String.valueOf(voca.getVIId()))) {
                voca.setVIPlaying(playing);
                rvVoca.post(new Runnable() {

                    @Override
                    public void run() {
                        adapter.notifyPlayedOrStopped(voca);
                    }
                });
                return;
            }
        }
        for (final VocaMemorize voca : grade2) {
            if (utteranceId.equals(String.valueOf(voca.getVIId()))) {
                voca.setVIPlaying(playing);
                rvVoca.post(new Runnable() {

                    @Override
                    public void run() {
                        adapter.notifyPlayedOrStopped(voca);
                    }
                });
                return;
            }
        }
        for (final VocaMemorize voca : grade99) {
            if (utteranceId.equals(String.valueOf(voca.getVIId()))) {
                voca.setVIPlaying(playing);
                rvVoca.post(new Runnable() {

                    @Override
                    public void run() {
                        adapter.notifyPlayedOrStopped(voca);
                    }
                });
                return;
            }
        }
    }

    private OnTargetsClickListener listener = new OnTargetsClickListener() {

        @Override
        public void onPlayAllSoundClick(int grade) {
            Intent intent = new Intent(TargetsActivity.this, PlaylistActivity.class);
            if (grade == Constant.VOCA_KNOW.VOCA_KNOW_AMKI_GRADE_1) {
                intent.putExtra(Constant.BUNDLE.KEY_VOCA_PLAYLIST, (Serializable) grade1);
            } else if (grade == Constant.VOCA_KNOW.VOCA_KNOW_AMKI_GRADE_2) {
                intent.putExtra(Constant.BUNDLE.KEY_VOCA_PLAYLIST, (Serializable) grade2);
            } else {
                intent.putExtra(Constant.BUNDLE.KEY_VOCA_PLAYLIST, (Serializable) grade99);
            }
            openNewScreen(intent);
        }

        @Override
        public void onPlaySoundClick(VocaMemorize voca) {
            boolean isPlaying = voca.isVIPlaying();
            getPlayVocaHelper().stop();
            if (!isPlaying) {
                preparePlayVoca(voca);
            }
        }

        @Override
        public void onInfoClick(VocaMemorize voca) {
            Intent intent;
            intent = new Intent(TargetsActivity.this, WordInfoActivity.class);
            intent.putExtra(Constant.BUNDLE.KEY_VOCA_ID, voca.getVocaId());
            intent.putExtra(Constant.BUNDLE.KEY_VOCA_TYPE, voca.getVocaType());
            openNewScreen(intent);
        }

        @Override
        public void onGradeClick(VocaMemorize voca) {
            registerVocaDialog.show(voca);
        }
    };

    private CompoundButton.OnCheckedChangeListener onCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            int id = buttonView.getId();
            switch (id) {
                case R.id.rb_title:
                    searchOption.setWhere(isChecked ? Constant.API_VALUE.VALUE_SEARCH_IN_VOCA : Constant.API_VALUE.VALUE_SEARCH_IN_MEANING);
                    break;
                case R.id.rb_start_with:
                    searchOption.setStartWith(isChecked);
                    break;
                case R.id.rb_end_with:
                    searchOption.setEndWith(isChecked);
                    break;
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
//            if (voca instanceof VocaMemorize) {
//                changeVocaKnow((VocaMemorize) voca, vocaKnow);
//            }
//        }
//
//        @Override
//        public void onVocaKnowPronounceChange(AmkiItem voca, int vocaKnowPronounce) {
//        }
//
//        @Override
//        public void onAddToWordbook(AmkiItem voca) {
//            if (voca instanceof VocaMemorize) {
//                showUserBooks((VocaMemorize) voca);
//            }
//        }
    };

    private void showUserBooks(final VocaMemorize voca) {
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
                    });
        }
    }

    private void addVocaToUserBook(VocaMemorize voca, VocaBook userBook) {
        int uid = sharedPreferences.getRealUid();
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
                                    ToastUtil.getInstance(TargetsActivity.this).show(R.string.msg_add_to_book_success);
                                } else {
                                    ToastUtil.getInstance(TargetsActivity.this).show(R.string.msg_add_to_book_failed);
                                }
                            }

                            @Override
                            public void onFailure(String error) {
                                ToastUtil.getInstance(TargetsActivity.this).show(R.string.msg_add_to_book_failed);
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

    private void changeVocaKnow(VocaMemorize voca, int vocaKnow) {
        int uid = sharedPreferences.getRealUid();
        if (uid > 0) {
            if (Utils.isConnected(this)) {
                application.getDalAiImpl().checkAndChangeVocaKnow(
                        this,
                        vocaKnow,
                        VocaKnow.getVocaKnowPronounceByVocaKnow(vocaKnow),
                        String.valueOf(voca.getVocaId()),
                        voca.getVocaType(),
                        new DalApiListener<Integer>() {

                            @Override
                            public void onSuccess(Integer newVocaKnow) {
                                voca.setVocaKnow(newVocaKnow);
                                voca.setVocaKnowPronounce(VocaKnow.getVocaKnowPronounceByVocaKnow(newVocaKnow));
                                Voca.executeRealmTransaction(realm -> realm.copyToRealmOrUpdate(voca));
                                getDataFromLocal();
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
}
