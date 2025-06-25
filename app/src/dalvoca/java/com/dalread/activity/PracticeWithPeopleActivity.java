package com.dalread.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.UtteranceProgressListener;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dalread.R;
import com.dalread.adapter.VocaPracticeAdapter;
import com.dalread.base.BaseDalVocaPlayVocaActivity;
import com.dalread.base.EnumLanguage;
import com.dalread.component.SeparatorDecoration;
import com.dalread.dialog.AlertDialog;
import com.dalread.dialog.RegisterVocaDialog;
import com.dalread.dialog.SingleChoiceDialog;
import com.dalread.interfaces.IVocaBasicItem;
import com.dalread.listener.OnClickDialogListener;
import com.dalread.listener.OnKnowChangeListener;
import com.dalread.listener.OnPracticeClickListener;
import com.dalread.model.VocaBook;
import com.dalread.model.VocaPractice;
import com.dalread.network.DalApiListener;
import com.dalread.network.models.AllBookListResponse;
import com.dalread.network.models.VocaPracticeListResponse;
import com.dalread.util.Constant;
import com.dalread.util.Loading;
import com.dalread.util.ToastUtil;
import com.dalread.util.Utils;
import com.dalread.util.Voca;
import com.dalread.util.VocaKnow;

import java.util.ArrayList;

import butterknife.BindColor;
import butterknife.BindDimen;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.OnClick;

public class PracticeWithPeopleActivity extends BaseDalVocaPlayVocaActivity {

    @BindView(R.id.ic_play_all)
    View icPlayAll;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.tv_count)
    TextView tvCount;
    @BindView(R.id.rv_voca)
    RecyclerView rvVoca;

    @BindString(R.string.tpl_wb_known_word_count)
    String tplKnownWordCount;

    @BindColor(R.color.color_divider)
    int clDivider;

    @BindDimen(R.dimen.divider_height)
    float dividerHeight;

    private Context context;
    private AlertDialog alertDialog;
    private VocaBook vocaBook;
    private ArrayList<VocaPractice> vocas;
    private VocaPracticeAdapter adapter;
    private ArrayList<VocaBook> userBooks;
    private String[] userBookNames;
    private RegisterVocaDialog registerVocaDialog;
    private SingleChoiceDialog userBooksDialog;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_practice_with_people;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        initData();
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

    @Override
    public void onResume() {
        super.onResume();

        initPlayVocaHelper();
    }

    private void initData() {
        Intent intent = getIntent();
        vocaBook = (VocaBook) intent.getSerializableExtra(Constant.BUNDLE.KEY_VOCA_BOOK);

        String text = vocaBook.getName();
        tvName.setText(text);
    }

    private void initDialog() {
        alertDialog = new AlertDialog(context);
        registerVocaDialog = new RegisterVocaDialog(context, onKnowChangeListener);
        userBooksDialog = new SingleChoiceDialog(context);
    }

    private void initRecyclerView() {
        adapter = new VocaPracticeAdapter(this, vocas = new ArrayList<>());
        adapter.setListener(onPracticeClickListener);
        rvVoca.setAdapter(adapter);
        rvVoca.setLayoutManager(new LinearLayoutManager(context));
        rvVoca.addItemDecoration(BaseBindUtils.getSeparatorDecoration(context));
    }

    private void getData() {
        final int uid = getUserID();
        if (uid > 0) {
            if (Utils.isConnected(context)) {
                Loading.show(context);
                application.getDalAiImpl().getListToPractice(
                        String.valueOf(uid),
                        sharedPreferences.getLangStudyCode(),
                        sharedPreferences.getMotherTongueLangCode(),
                        String.valueOf(vocaBook.getId()),
                        Voca.getBookType(false),
                        new DalApiListener<VocaPracticeListResponse>() {

                            @Override
                            public void onSuccess(VocaPracticeListResponse response) {
                                Loading.hide();

                                String text = String.format(tplKnownWordCount, response.getKnownVocaCount(), response.getVocaCount());
                                tvCount.setText(text);

                                int studyLang = EnumLanguage.findByFormatApi(sharedPreferences.getStudyLanguage()).getIdApi();
                                ArrayList<VocaPractice> vocaPractices = response.getVocas();
                                for (VocaPractice voca : vocaPractices) {
                                    voca.setPath(Voca.getOutputRecordingFileName(
                                            studyLang,
                                            voca.getVocaType(),
                                            voca.getVocaId(),
                                            uid
                                    ));
                                    vocas.add(voca);
                                }
                                adapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onFailure(String error) {
                                Loading.hide();
                            }
                        }
                );
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
        for (final VocaPractice voca : vocas) {
            if (utteranceId.equals(String.valueOf(voca.getVIId()))) {
                voca.setVIPlaying(playing);
                rvVoca.post(new Runnable() {

                    @Override
                    public void run() {
                        adapter.notifyItemChanged(voca);
                    }
                });
                break;
            }
        }
    }

    @OnClick({R.id.ic_play_all, R.id.v_self_practice})
    void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.ic_play_all:
                openPlaylistScreen();
                break;
            case R.id.v_self_practice:
                openSelfPracticeScreen();
                break;
            default:
                break;
        }
    }

    private OnPracticeClickListener onPracticeClickListener = new OnPracticeClickListener() {

        @Override
        public void onPlayClick(VocaPractice voca) {
            boolean isPlaying = voca.isVIPlaying();
            playVocaHelper.stop();
            if (!isPlaying) {
                preparePlayVoca(voca);
            }
        }

        @Override
        public void onGradeClick(VocaPractice voca) {
            registerVocaDialog.show(voca);
        }

        @Override
        public void onInfoClick(VocaPractice voca) {
            openInfoScreen(voca);
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
//            if (voca instanceof VocaPractice) {
//                changeVocaKnow((VocaPractice) voca, vocaKnow);
//            }
//        }
//
//        @Override
//        public void onVocaKnowPronounceChange(AmkiItem voca, int vocaKnowPronounce) {
//        }
//
//        @Override
//        public void onAddToWordbook(AmkiItem voca) {
//            if (voca instanceof VocaPractice) {
//                showUserBooks((VocaPractice) voca);
//            }
//        }
    };

    private void openPlaylistScreen() {
        Intent intent = new Intent(context, PlaylistActivity.class);
        intent.putExtra(Constant.BUNDLE.KEY_VOCA_PLAYLIST, vocas);
        intent.putExtra(Constant.BUNDLE.KEY_VOCA_BOOK_NAME, vocaBook.getName());
        openNewScreen(intent);
    }

    private void openSelfPracticeScreen() {
        Intent intent = new Intent(context, SelfPracticeSpeakingActivity.class);
        intent.putExtra(Constant.BUNDLE.KEY_VOCA_PRACTICE_LIST, vocas);
        intent.putExtra(Constant.BUNDLE.KEY_VOCA_BOOK, vocaBook);
        openNewScreen(intent);
    }

    private void openInfoScreen(VocaPractice voca) {
        Intent intent;
        intent = new Intent(context, WordInfoActivity.class);
        intent.putExtra(Constant.BUNDLE.KEY_VOCA_ID, voca.getVocaId());
        intent.putExtra(Constant.BUNDLE.KEY_VOCA_TYPE, voca.getVocaType());
        openNewScreen(intent);
    }

    private void showUserBooks(final VocaPractice voca) {
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

    private void addVocaToUserBook(VocaPractice voca, VocaBook userBook) {
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
                                    ToastUtil.getInstance(context).show( R.string.msg_add_to_book_success);
                                } else {
                                    ToastUtil.getInstance(context).show( R.string.msg_add_to_book_failed);
                                }
                            }

                            @Override
                            public void onFailure(String error) {
                                ToastUtil.getInstance(context).show( R.string.msg_add_to_book_failed);
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

    private void changeVocaKnow(VocaPractice voca, int vocaKnow) {
        int uid = getUserID();
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
                                adapter.notifyItemChanged(voca);
                                updateCount();
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

    private void updateCount() {
        int uid = getUserID();
        if (uid > 0 && Utils.isConnected(this)) {
            application.getDalAiImpl().getListToPractice(
                    String.valueOf(uid),
                    sharedPreferences.getLangStudyCode(),
                    sharedPreferences.getMotherTongueLangCode(),
                    String.valueOf(vocaBook.getId()),
                    Voca.getBookType(false),
                    new DalApiListener<VocaPracticeListResponse>() {

                        @Override
                        public void onSuccess(VocaPracticeListResponse response) {
                            String text = String.format(tplKnownWordCount, response.getKnownVocaCount(), response.getVocaCount());
                            tvCount.setText(text);
                        }

                        @Override
                        public void onFailure(String error) {
                        }
                    }
            );
        }
    }
}
