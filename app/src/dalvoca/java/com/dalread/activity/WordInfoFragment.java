package com.dalread.activity;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.speech.tts.UtteranceProgressListener;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dalread.R;
import com.dalread.adapter.VocaExampleAdapter;
import com.dalread.base.BaseWordInfoFragment;
import com.dalread.base.EnumLanguage;
import com.dalread.component.SeparatorDecoration;
import com.dalread.dialog.AlertDialog;
import com.dalread.dialog.RecordAndUploadDialog;
import com.dalread.dialog.RegisterVocaDialog;
import com.dalread.dialog.SingleChoiceDialog;
import com.dalread.helper.PlayVocaHelper;
import com.dalread.interfaces.IVocaBasicItem;
import com.dalread.listener.OnClickDialogListener;
import com.dalread.listener.OnExampleClickListener;
import com.dalread.listener.OnKnowChangeListener;
import com.dalread.model.VocaBook;
import com.dalread.model.VocaDetailInfo;
import com.dalread.model.VocaDownload;
import com.dalread.network.DalApiListener;
import com.dalread.network.models.AllBookListResponse;
import com.dalread.util.Constant;
import com.dalread.util.LanguageUtil;
import com.dalread.util.PermissionUtils;
import com.dalread.util.ToastUtil;
import com.dalread.util.Utils;
import com.dalread.util.Voca;
import com.dalread.util.VocaKnow;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindColor;
import butterknife.BindDimen;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnLongClick;

@SuppressLint("NonConstantResourceId")
public class WordInfoFragment extends BaseWordInfoFragment {
    @BindView(R.id.tv_know_icon)
    TextView tvKnowIcon;
    @BindView(R.id.tv_word)
    TextView tvWord;
    @BindView(R.id.tv_meaning)
    TextView tvMeaning;
    @BindView(R.id.tv_count)
    TextView tvCount;
    @BindView(R.id.ic_play)
    ImageView icPlay;
    @BindView(R.id.v_hanja_list)
    View vHanjaList;
    @BindView(R.id.rv_hanja_list)
    RecyclerView rvHanjaList;
    @BindView(R.id.tv_comment)
    TextView tvComment;
    @BindView(R.id.tv_example)
    TextView tvExample;
    @BindView(R.id.rv_example)
    RecyclerView rvExample;

    @BindColor(R.color.color_divider)
    int clDivider;

    @BindDimen(R.dimen.divider_height)
    float dividerHeight;

    @BindString(R.string.tpl_study_count)
    String tplStudyCount;
    @BindString(R.string.app_name)
    String appName;

    private VocaDetailInfo voca;
    private VocaExampleAdapter adapter;
    private RegisterVocaDialog registerVocaDialog;
    private AlertDialog alertDialog;
    private ArrayList<VocaBook> userBooks;
    private String[] userBookNames;
    private SingleChoiceDialog userBooksDialog;
    private RecordAndUploadDialog recordAndUploadDialog;
    private PlayVocaHelper playVocaHelper;

    @Override
    protected int getContentViewId() {
        return R.layout.fragment_word_info;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initProperty();
        initLayout();

        getUserBooks();
    }

    @Override
    public void onResume() {
        super.onResume();

        initPlayVocaHelper();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            showRecordAndUploadDialog();
        }
    }

    @Override
    public void onDestroy() {
        recordAndUploadDialog.onDestroy();

        super.onDestroy();
    }

    @OnClick({R.id.ic_play, R.id.tv_naver, R.id.tv_know_icon, R.id.tv_record})
    void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.ic_play:
                playVoca();
                break;
            case R.id.tv_naver:
                openNaverDictionary();
                break;
            case R.id.tv_know_icon:
                registerVocaDialog.show(voca);
                break;
            case R.id.tv_record:
                showRecordAndUploadDialog();
                break;
            default:
                break;
        }
    }

    @OnLongClick({R.id.tv_word, R.id.tv_meaning, R.id.tv_comment})
    boolean onLongClick(View view) {
        ClipboardManager clipboard = (ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE);
        if (clipboard != null) {
            String text;
            if (view.getId() == R.id.tv_comment) {
                text = Voca.getMeaningDetailed(voca);
            } else {
                text = voca.getVIMeaning(LanguageUtil.getMotherTongueLanguage(activity)) + "\n" + Voca.getVocaDisplay(voca);
            }
            clipboard.setPrimaryClip(ClipData.newPlainText(appName, text));
            ToastUtil.getInstance(activity).show(R.string.copied);
            return true;
        }
        return false;
    }

    @Override
    public void requestGetData() {
        if (getArguments() != null) {
            voca = (VocaDetailInfo) getArguments().getSerializable(Constant.BUNDLE.KEY_VOCA);
        }
        initLayout();
    }

    private void initProperty() {
        if (getArguments() != null) {
            voca = (VocaDetailInfo) getArguments().getSerializable(Constant.BUNDLE.KEY_VOCA);
        }
        playVocaHelper = activity.getPlayVocaHelper();
        registerVocaDialog = new RegisterVocaDialog(activity, onKnowChangeListener);
        alertDialog = new AlertDialog(activity);
        userBooksDialog = new SingleChoiceDialog(activity);
        recordAndUploadDialog = new RecordAndUploadDialog(activity, Voca.getVoiceFolderOnLocal(activity), voca, onRecordClickListener);
    }

    private void initLayout() {
        if (voca != null) {
            VocaKnow.updateIconVocaKnow(activity, tvKnowIcon, voca.getVocaKnow());
            String text = Voca.getVocaDisplay(voca);
            if (sharedPreferences.getDisplayPronunciation() && !TextUtils.isEmpty(voca.getPronounce())) {
                text += " [" + voca.getPronounce() + "]";
            }
            tvWord.setText(text);
            text = voca.getVIMeaning(LanguageUtil.getMotherTongueLanguage(activity));
            tvMeaning.setText(text);
            text = String.format(tplStudyCount, voca.getStudyCount());
            tvCount.setText(text);
            text = Voca.getMeaningDetailed(voca);
            tvComment.setText(text);
            tvComment.setVisibility(TextUtils.isEmpty(text) ? View.GONE : View.VISIBLE);
            if (!voca.getExampleSentences().isEmpty()) {
                List<VocaDetailInfo> examples = voca.getExampleSentences();
                int studyLang = EnumLanguage.findByFormatApi(sharedPreferences.getStudyLanguage()).getIdApi();
                int uid = sharedPreferences.getRealUid();
                for (VocaDetailInfo example : examples) {
                    example.setPath(Voca.getOutputRecordingFileName(
                            studyLang,
                            example.getVocaType(),
                            example.getVocaId(),
                            uid
                    ));
                }
                adapter = new VocaExampleAdapter(getContext(), voca.getExampleSentences());
                adapter.setListener(onExampleClickListener);
                rvExample.setAdapter(adapter);
                rvExample.setLayoutManager(new LinearLayoutManager(activity));
                rvExample.addItemDecoration(new SeparatorDecoration(activity, clDivider, dividerHeight));
                tvExample.setVisibility(View.VISIBLE);
            }
            Voca.updateIconSpeaker(icPlay, voca);
        }
    }

    private void getUserBooks() {
        int uid = sharedPreferences.getRealUid();
        if (uid > 0 && Utils.isConnected(activity)) {
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
        boolean isExample = !utteranceId.equals(String.valueOf(voca.getVIId()));
        if (isExample) {
            List<VocaDetailInfo> vocas = voca.getExampleSentences();
            for (final VocaDetailInfo voca : vocas) {
                if (utteranceId.equals(String.valueOf(voca.getVIId()))) {
                    voca.setVIPlaying(playing);
                    rvExample.post(() -> adapter.notifyRegisteredOrRemoved(voca));
                    break;
                }
            }
        } else {
            voca.setVIPlaying(playing);
            icPlay.post(() -> {
                if (icPlay != null) {
                    Voca.updateIconSpeaker(icPlay, voca);
                }
            });
        }
    }

    private void playVoca() {
        boolean isPlaying = voca.isVIPlaying();
        playVocaHelper.stop();
        if (!isPlaying) {
            activity.preparePlayVoca(voca);
        }
    }

    private void openNaverDictionary() {
        String host = sharedPreferences.getStudyLanguage().equals(EnumLanguage.CHINESE_SIMPLIFIED.getFormatApi())
                ? Constant.URL_NAVER_DICTIONARY_ZH
                : Constant.URL_NAVER_DICTIONARY_EN;
        String url = String.format(host, voca.getVoca());
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(browserIntent);
    }

    private void showRecordAndUploadDialog() {
        if (PermissionUtils.checkRecordAudio(activity, true)) {
            if (PermissionUtils.checkWriteExternalStorage(activity, true)) {
                recordAndUploadDialog.show();
            }
        }
    }

    private final RecordAndUploadDialog.OnClickListener onRecordClickListener = this::uploadVoiceFile;

    private void uploadVoiceFile(final File recordFile) {
        final String vocaId = String.valueOf(voca.getVocaId());
        final int vocaType = voca.getVocaType();
        final String path = voca.getVIPath();
        if (recordFile != null && recordFile.exists()) {
            Uri uri = Uri.fromFile(recordFile);
            StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(Voca.getVoiceFolderPathOnFirebase(path) + uri.getLastPathSegment());
            StorageMetadata metadata = new StorageMetadata.Builder()
                    .setContentType("application/octet-stream")
                    .build();
            UploadTask uploadTask = storageReference.putFile(uri, metadata);
            uploadTask.addOnSuccessListener(taskSnapshot -> {
                Voca.executeRealmTransaction(realm -> {
                    VocaDownload vocaDownload = realm.where(VocaDownload.class)
                            .equalTo("name", path)
                            .findFirst();
                    if (vocaDownload == null) {
                        vocaDownload = new VocaDownload();
                        vocaDownload.setName(path);
                        vocaDownload.setVersion(0);
                        realm.copyToRealm(vocaDownload);
                    } else {
                        int version = vocaDownload.getVersion() + 1;
                        vocaDownload.setVersion(version);
                    }
                });
                application.getDalAiImpl().updateFinishStudy(
                        String.valueOf(sharedPreferences.getRealUid()),
                        sharedPreferences.getLangStudyCode(),
                        0,
                        vocaId,
                        vocaType,
                        0,
                        Constant.FILE.EXTENTION_SPEAKING,
                        recordFile.length(),
                        new DalApiListener<Boolean>() {

                            @Override
                            public void onSuccess(Boolean response) {
                                activity.initData();
                            }

                            @Override
                            public void onFailure(String error) {
                            }
                        }
                );
            }).addOnFailureListener(exception -> {
            });
        }
    }

    private final OnExampleClickListener onExampleClickListener = new OnExampleClickListener() {

        @Override
        public void onPlayClick(VocaDetailInfo voca) {
            boolean isPlaying = voca.isVIPlaying();
            playVocaHelper.stop();
            if (!isPlaying) {
                activity.preparePlayVoca(voca);
            }
        }

        @Override
        public void onGradeClick(VocaDetailInfo voca) {
            registerVocaDialog.show(voca);
        }

        @Override
        public void onInfoClick(VocaDetailInfo voca) {
            Intent intent;
            intent = new Intent(activity, WordInfoActivity.class);
            intent.putExtra(Constant.BUNDLE.KEY_VOCA_ID, voca.getVocaId());
            intent.putExtra(Constant.BUNDLE.KEY_VOCA_TYPE, voca.getVocaType());

            activity.openNewScreen(intent);
        }
    };

    private final OnKnowChangeListener onKnowChangeListener = new OnKnowChangeListener() {
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
//            if (voca instanceof VocaDetailInfo) {
//                changeVocaKnow((VocaDetailInfo) voca, vocaKnow);
//            }
//        }
//
//        @Override
//        public void onVocaKnowPronounceChange(AmkiItem voca, int vocaKnowPronounce) {
//        }
//
//        @Override
//        public void onAddToWordbook(AmkiItem voca) {
//            if (voca instanceof VocaDetailInfo) {
//                showUserBooks((VocaDetailInfo) voca);
//            }
//        }
    };

    private void showUserBooks(final VocaDetailInfo voca) {
        if (userBooks == null || userBooks.isEmpty()) {
            ToastUtil.getInstance(activity).show(R.string.msg_no_user_book);
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

    private void addVocaToUserBook(VocaDetailInfo voca, VocaBook userBook) {
        int uid = sharedPreferences.getRealUid();
        if (uid > 0) {
            if (Utils.isConnected(activity)) {
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
                                    activity.setDataChanged(true);
                                    ToastUtil.getInstance(activity).show(R.string.msg_add_to_book_success);
                                } else {
                                    ToastUtil.getInstance(activity).show(R.string.msg_add_to_book_failed);
                                }
                            }

                            @Override
                            public void onFailure(String error) {
                                ToastUtil.getInstance(activity).show(R.string.msg_add_to_book_failed);
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

    private void changeVocaKnow(VocaDetailInfo voca, int vocaKnow) {
        int uid = sharedPreferences.getRealUid();
        if (uid > 0) {
            if (Utils.isConnected(activity)) {
                application.getDalAiImpl().checkAndChangeVocaKnow(
                        activity,
                        vocaKnow,
                        VocaKnow.getVocaKnowPronounceByVocaKnow(vocaKnow),
                        String.valueOf(voca.getVocaId()),
                        voca.getVocaType(),
                        new DalApiListener<Integer>() {

                            @Override
                            public void onSuccess(Integer newVocaKnow) {
                                voca.setVocaKnow(newVocaKnow);
                                voca.setVocaKnowPronounce(VocaKnow.getVocaKnowPronounceByVocaKnow(newVocaKnow));
                                if (voca == WordInfoFragment.this.voca) {
                                    VocaKnow.updateIconVocaKnow(activity, tvKnowIcon, voca.getVocaKnow());
                                } else {
                                    adapter.notifyRegisteredOrRemoved(voca);
                                }
                                activity.setDataChanged(true);
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
