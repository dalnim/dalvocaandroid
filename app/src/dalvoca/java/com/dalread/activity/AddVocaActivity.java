package com.dalread.activity;

import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.UtteranceProgressListener;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.PopupMenu;
import android.widget.PopupWindow;

import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dalread.BuildConfig;
import com.dalread.R;
import com.dalread.adapter.AddVocaAdapter;
import com.dalread.base.BaseDalVocaPlayVocaActivity;
import com.dalread.base.EnumLanguage;
import com.dalread.component.SeparatorDecoration;
import com.dalread.dialog.AlertDialog;
import com.dalread.listener.OnAddVocaListener;
import com.dalread.model.VocaSearch;
import com.dalread.model.VocaSearchOption;
import com.dalread.network.DalApiListener;
import com.dalread.network.events.BaseEvent;
import com.dalread.network.events.SuccessEvent;
import com.dalread.util.Constant;
import com.dalread.util.Loading;
import com.dalread.util.ToastUtil;
import com.dalread.util.Utils;
import com.dalread.util.Voca;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindColor;
import butterknife.BindDimen;
import butterknife.BindView;

public class AddVocaActivity extends BaseDalVocaPlayVocaActivity {

    @BindView(R.id.rv_voca)
    RecyclerView rvVoca;
    @BindColor(R.color.color_divider)
    int clDivider;
    @BindDimen(R.dimen.divider_height)
    float dividerHeight;

    private LinearLayoutManager layoutManager;
    private AddVocaAdapter adapter;
    private List<VocaSearch> vocas;
    private VocaSearchOption searchOption;
    private boolean isLoading;
    private PopupMenu popupMenu;
    private AlertDialog alertDialog;
    private boolean dataChanged;
    private int countChanged;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_add_voca;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
        initToolbar();
        initRecyclerView();
        initDialog();
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

    }

    @Override
    public void onHeaderIconRightClick() {
        if (popupMenu != null) {
            popupMenu.show();
        }
    }

    @Override
    public void onHeaderTextRightClick() {

    }

    @Override
    protected void onResume() {
        super.onResume();

        initPlayVocaHelper();
    }

    @Override
    public void onBackPressed() {
        if (dataChanged) {
            application.getEventBus().post(new SuccessEvent(BaseEvent.Screen.MAIN, BaseEvent.EventType.DATA_CHANGED, countChanged));
        }
        super.onBackPressed();
    }

    private void initData() {
        vocas = new ArrayList<>();
        searchOption = new VocaSearchOption();
        searchOption.setWhere(Constant.API_VALUE.VALUE_SEARCH_IN_VOCA);
        searchOption.setUseRegex(/*BuildConfig.DEBUG ? 1 : */0);
        Intent intent = getIntent();
        if (intent != null) {
            searchOption.setBookId(intent.getIntExtra(Constant.BUNDLE.KEY_VOCA_BOOK_ID, 0));
        }
        searchOption.setVocaType(Constant.API_VALUE.VALUE_VOCA_TYPE_WORD);
        searchOption.setCountRecord(Constant.LOADING_MAX_ITEM);
    }

    private void initToolbar() {
        View popupView = getLayoutInflater().inflate(R.layout.item_search_voca, null);
        CompoundButton cb = popupView.findViewById(R.id.rb_title);
        cb.setOnCheckedChangeListener(onCheckedChangeListener);
        PopupWindow popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        toolbar.setPopupWindow(popupWindow);
        toolbar.setSearchListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                searchOption.setText(s);
                getData(false);
                return true;
            }
        }, new SearchView.OnCloseListener() {

            @Override
            public boolean onClose() {
                searchOption.setText("");
                getData(false);
                return true;
            }
        });
        toolbar.showSearchView();

        popupMenu = new PopupMenu(this, toolbar.getIconRight());
        popupMenu.getMenuInflater().inflate(R.menu.menu_voca_type, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(onMenuItemClickListener);
    }

    private void initRecyclerView() {
        adapter = new AddVocaAdapter(this, sharedPreferences.getDisplayPronunciation());
        adapter.setListener(onAddVocaListener);
        rvVoca.setAdapter(adapter);
        rvVoca.setLayoutManager(layoutManager = new LinearLayoutManager(this));
        rvVoca.addItemDecoration(new SeparatorDecoration(this, clDivider, dividerHeight));
        rvVoca.addOnScrollListener(onScrollListener);
    }

    private void initDialog() {
        alertDialog = new AlertDialog(this);
    }

    private void getData(final boolean isLoadMore) {
        final int uid = getUserID();
        if (uid > 0) {
            if (Utils.isConnected(this)) {
                isLoading = true;
                rvVoca.post(new Runnable() {

                    @Override
                    public void run() {
                        if (isLoadMore) {
                            adapter.setLoadMore(true);
                        } else {
                            vocas.clear();
                            searchOption.setStartNo(0);
                        }
                        application.getDalAiImpl().getVocasFromAllVocas(
                                String.valueOf(uid),
                                sharedPreferences.getLangStudyCode(),
                                sharedPreferences.getMotherTongueLangCode(),
                                searchOption,
                                new DalApiListener<List<VocaSearch>>() {

                                    @Override
                                    public void onSuccess(List<VocaSearch> response) {
                                        isLoading = false;
                                        int studyLang = EnumLanguage.findByFormatApi(sharedPreferences.getStudyLanguage()).getIdApi();
                                        for (VocaSearch voca : response) {
                                            voca.setPath(Voca.getOutputRecordingFileName(studyLang, searchOption.getVocaType(), voca.getId(), uid));
                                            voca.setType(searchOption.getVocaType());
                                            vocas.add(voca);
                                        }
                                        searchOption.setStartNo(vocas.size());
                                        if (isLoadMore) {
                                            adapter.setLoadMore(false);
                                        } else {
                                            Loading.hide();
                                        }
                                        adapter.setData(vocas);
                                        adapter.notifyDataSetChanged();
                                    }

                                    @Override
                                    public void onFailure(String error) {
                                        isLoading = false;
                                        if (isLoadMore) {
                                            adapter.setLoadMore(false);
                                        } else {
                                            Loading.hide();
                                        }
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
        for (final VocaSearch voca : vocas) {
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

    private void registerVoca(final VocaSearch voca) {
        int uid = getUserID();
        if (uid > 0) {
            if (Utils.isConnected(this)) {
                voca.setBelongToBook(true);
                adapter.notifyRegisteredOrRemoved(voca);
                application.getDalAiImpl().addVocaInUserVocaBook(
                        String.valueOf(uid),
                        sharedPreferences.getLangStudyCode(),
                        searchOption.getBookId(),
                        String.valueOf(voca.getId()),
                        searchOption.getVocaType(),
                        new DalApiListener<Boolean>() {

                            @Override
                            public void onSuccess(Boolean response) {
                                if (response) {
                                    dataChanged = true;
                                    ++countChanged;
                                } else {
                                    voca.setBelongToBook(false);
                                    adapter.notifyRegisteredOrRemoved(voca);
                                    if (BuildConfig.DEBUG) {
                                        ToastUtil.getInstance(AddVocaActivity.this).show("Dev: Register failed");
                                    }
                                }
                            }

                            @Override
                            public void onFailure(String error) {
                                voca.setBelongToBook(false);
                                adapter.notifyRegisteredOrRemoved(voca);
                                if (BuildConfig.DEBUG) {
                                    ToastUtil.getInstance(AddVocaActivity.this).show("Dev: Register failed");
                                }
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

    private void removeVoca(final VocaSearch voca) {
        int uid = getUserID();
        if (uid > 0) {
            if (Utils.isConnected(this)) {
                voca.setBelongToBook(false);
                adapter.notifyRegisteredOrRemoved(voca);
                application.getDalAiImpl().deleteVocaInUserVocaBook(
                        String.valueOf(uid),
                        sharedPreferences.getLangStudyCode(),
                        searchOption.getBookId(),
                        String.valueOf(voca.getId()),
                        searchOption.getVocaType(),
                        new DalApiListener<Boolean>() {

                            @Override
                            public void onSuccess(Boolean response) {
                                dataChanged = true;
                                --countChanged;
                            }

                            @Override
                            public void onFailure(String error) {
                                voca.setBelongToBook(true);
                                adapter.notifyRegisteredOrRemoved(voca);
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

    private OnAddVocaListener onAddVocaListener = new OnAddVocaListener() {

        @Override
        public void onPlayAllClick() {
            Intent intent = new Intent(AddVocaActivity.this, PlaylistActivity.class);
            intent.putExtra(Constant.BUNDLE.KEY_VOCA_PLAYLIST, (Serializable) vocas);
            openNewScreen(intent);
        }

        @Override
        public void onPlayClick(VocaSearch voca) {
            boolean isPlaying = voca.isVIPlaying();
            playVocaHelper.stop();
            if (!isPlaying) {
                preparePlayVoca(voca);
            }
        }

        @Override
        public void onRegisterClick(VocaSearch voca) {
            if (voca.isBelongToBook()) {
                removeVoca(voca);
            } else {
                registerVoca(voca);
            }
        }

        @Override
        public void onInfoClick(VocaSearch voca) {
            Intent intent;

            intent = new Intent(AddVocaActivity.this, WordInfoActivity.class);
            intent.putExtra(Constant.BUNDLE.KEY_VOCA_ID, voca.getId());
            intent.putExtra(Constant.BUNDLE.KEY_VOCA_TYPE, searchOption.getVocaType());

            openNewScreen(intent);
        }
    };

    private RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);

            if (!isLoading && !vocas.isEmpty() && vocas.size() % searchOption.getCountRecord() == 0) {
                int count = layoutManager.getItemCount();
                int last = layoutManager.findLastVisibleItemPosition();
                if (count <= last + 3) { // load more when scrolled to the third-last item
                    getData(true);
                }
            }
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
            }
        }
    };

    private PopupMenu.OnMenuItemClickListener onMenuItemClickListener = new PopupMenu.OnMenuItemClickListener() {

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            int id = item.getItemId();
            switch (id) {
                case R.id.type_word:
                    searchOption.setVocaType(Constant.API_VALUE.VALUE_VOCA_TYPE_WORD);
                    toolbar.setTitle(R.string.all_words);
                    break;
                case R.id.type_sentence:
                    searchOption.setVocaType(Constant.API_VALUE.VALUE_VOCA_TYPE_SENTENCE);
                    toolbar.setTitle(R.string.all_sentences);
                    break;
            }
            getData(false);
            return true;
        }
    };
}
