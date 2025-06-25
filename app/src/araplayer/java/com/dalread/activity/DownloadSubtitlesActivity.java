package com.dalread.activity;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.Nullable;

import com.dalread.R;
import com.dalread.adapter.OpenSubtitlesAdapter;
import com.dalread.base.BaseDialog;
import com.dalread.base.BaseDialogListener;
import com.dalread.base.BasePlayerActivity;
import com.dalread.base.EnumLanguage;
import com.dalread.base.EnumType;
import com.dalread.component.SeparatorDecoration;
import com.dalread.component.Toolbar;
import com.dalread.databinding.ActivityDownloadSubtitleBinding;
import com.dalread.dialog.TypeInputDialog;
import com.dalread.dialog.YesNoDialog;
import com.dalread.listener.OnClickListener;
import com.dalread.listener.OnYesNoClickListener;
import com.dalread.network.DalApiListener;
import com.dalread.network.OpenSubtitleApi;
import com.dalread.network.events.BaseEvent;
import com.dalread.network.events.SuccessEvent;
import com.dalread.util.BaseBindUtils;
import com.dalread.util.Constant;
import com.dalread.util.FileUtil;
import com.dalread.util.Loading;
import com.dalread.util.SnackbarUtil;
import com.dalread.util.StorageUtil;
import com.dalread.util.ToastUtil;
import com.dalread.util.Utils;

import org.apache.commons.io.FilenameUtils;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import me.zhanghai.android.fastscroll.FastScrollerBuilder;

// Use opensubtitles.com API. (Used org's api)
public class DownloadSubtitlesActivity extends BasePlayerActivity {
    private Context context;
    private TypeInputDialog searchSubtitleDialog;
    private TypeInputDialog downloadSubtitleDialog;
    private String typeInputText;
    private OpenSubtitlesAdapter adapter;
    private List<OpenSubtitleApi.OSSubtitleResponse> subtitleLinkList;
    private int subPathIndex = Constant.PLAYER.INTENT.SUBPATH_INDEX_1;
    private boolean isSubtitleSelected = false;
    private ActivityDownloadSubtitleBinding binding;
    private OpenSubtitleApi openSubtitleApi;
    private String subtitleFileLinkToDownload = "";
    @Override
    protected View getContentView() {
        binding = ActivityDownloadSubtitleBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        return view;
    }

    @Override
    protected Toolbar getToolbar() {
        return binding.header;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding.tvVideoFileName.setText(playerFileModel.getName());
    }

    @Override
    protected void setFullscreen() {
    }

    @Override
    public void initView() {
        context = this;

        searchSubtitleDialog = new TypeInputDialog(context, searchSubtitleDialogListener);
        downloadSubtitleDialog = new TypeInputDialog(context, downloadSubtitleDialogListener);

        binding.rvSubtitle.addItemDecoration(new SeparatorDecoration(context, BaseBindUtils.getDividerColor(), BaseBindUtils.getDividerHeight(this)));
        adapter = new OpenSubtitlesAdapter();
        binding.rvSubtitle.setAdapter(adapter);
        adapter.setOnClickListener(onOpenSubtitleItemClickListener);


        new FastScrollerBuilder(binding.fssvPreview).build();

        hideDownloadButton();
        if (binding.header != null) {
            binding.header.hideTvRight();
        }

        initData();

        searchSubtitleDialog.setTitle(R.string.search_subtitles);
        searchSubtitleDialog.setSubTitle(R.string.msg_write_video_title);
        searchSubtitleDialog.setInput(FilenameUtils.removeExtension(playerFileModel.getName()));
        openSubtitleApi = new OpenSubtitleApi(this);

        loginOrStartSearchSubtitle();
    }

    private void loginOrStartSearchSubtitle() {
        if (alreadyLogin()) {
            startSearchSubltitle();
        } else {
            showLoginView();
        }
    }

    private void showLoginView() {
        this.startActivity(OSLoginActivity.createIntent(this));
    }

    private boolean alreadyLogin() {
        return !Utils.isEmpty(sharedPreferences.getOpenSubtitleLoginToken());
    }

    private void startSearchSubltitle() {
        searchSubtitleList(FilenameUtils.removeExtension(playerFileModel.getName()));
    }
    private void searchSubtitleList(String fileName) {
        Loading.show(this);
        updateVisible_isSubtitleSelected(false);
        OpenSubtitleApi.OSSearchParam searchParam = new OpenSubtitleApi.OSSearchParam(fileName, EnumLanguage.findByFormatApi(sharedPreferences.getStudyLanguage()).getFormatOs(), "");
        openSubtitleApi.searchSubtitleList(searchParam, new DalApiListener<OpenSubtitleApi.OSSearchResponse>() {
            @Override
            public void onSuccess(OpenSubtitleApi.OSSearchResponse response) {
                subtitleLinkList = response.data;

//                items = items.stream()
//                        .filter(e -> e.getLanguageName().toUpperCase().contains(studyLang) || e.getLanguageName().toUpperCase().contains(motherTongue))
//                        .filter(e -> FileUtil.getSubtitleExtension(e.getSubFileName()) != SupportSubtitleFormat.NONE)
//                        .collect(Collectors.toList());
//                Collections.sort(items, new SortByLanguageNameAndDownloadsCnt());


                refreshSubtitleList2();
                if (subtitleLinkList.size() > 0) {
                    Loading.hideDelay(1000);
                    setTextInPreview(getString(R.string.tv_select_subtitle_to_display));
                } else {
                    handleNoSubtitle();
                }
            }

            @Override
            public void onFailure(String error) {
                handleNoSubtitle();
            }
        });
    }

    private void updateVisible_isSubtitleSelected(boolean value) {
        isSubtitleSelected = value;
    }

    //Don't delete it 나중에 자막 리스트를 정렬할 필요가 있을때 참고할려고 놔둠
//    class SortByLanguageNameAndDownloadsCnt implements Comparator<OpenSubtitleItem> {
//        // Used for sorting in ascending order of SubDownloadsCnt
//        public int compare(OpenSubtitleItem a, OpenSubtitleItem b) {
//            String languageNameA = a.getLanguageName() == null ? "" : a.getLanguageName();
//            String languageNameB = b.getLanguageName() == null ? "" : b.getLanguageName();
//            if (languageNameA.equalsIgnoreCase(languageNameB)) {
//                Integer downloadsCntA = Utils.parseInt(a.getSubDownloadsCnt());
//                Integer downloadsCntB = Utils.parseInt(b.getSubDownloadsCnt());
//                return downloadsCntB.compareTo(downloadsCntA);
//            }
//            if (languageNameA.toUpperCase().contains(studyLang))
//                return -1;
//            if (languageNameB.toUpperCase().contains(studyLang))
//                return 1;
//            return 0;
//        }
//    }

    @Override
    public void initData() {
        playerFileModel = getIntent().getParcelableExtra(Constant.PLAYER.INTENT.KEY_VIDEO_FILE);
        subPathIndex = getIntent().getIntExtra(Constant.PLAYER.INTENT.KEY_SUBPATH_INDEX, Constant.PLAYER.INTENT.SUBPATH_INDEX_1);
    }

    @Subscribe
    public void onEvent(SuccessEvent event) {
        if (event.getScreen() == BaseEvent.Screen.OPEN_SUBTITLE_LOGIN) {
            if (event.getEventType() == BaseEvent.EventType.LOGIN) {
                startSearchSubltitle();
            }
        }
    }

    @Override
    public void onHeaderLeftClick() {
        onBackPressed();
    }
    @Override
    public void onHeaderLeft2Click() {
        showTypeInputDialog();
    }
    @Override
    public void onHeaderRightClick() {

    }

    @Override
    public void onHeaderIconRightClick() {
        showDownloadFileDialog();
    }

    @Override
    public void onHeaderTextRightClick() {

    }


    private void showDownloadButton() {
        if (binding.header != null) {
            binding.header.showIconRight();
        }
    }

    private void hideDownloadButton() {
        if (binding.header != null) {
            binding.header.hideIconRight();
        }
    }

    private void searchSubtitlesByTitle2(String title) {
        if (TextUtils.isEmpty(title)) return;
        searchSubtitleList(title);
    }

    private void refreshSubtitleList2() {
        adapter.setOpenSubtitleItems(subtitleLinkList);
        adapter.setSelectedItemPosition(-1);
        adapter.notifyDataSetChanged();
    }

    private void handleNoSubtitle() {
        setTextInPreview(getString(R.string.tv_no_subtitle));
        ToastUtil.getInstance(context).show(R.string.msg_no_subtitle);
        showTypeInputDialog();
        Loading.hide();
    }

    private void showTypeInputDialog() {
        if (Utils.isEmpty(searchSubtitleDialog.getInput())) {
            searchSubtitleDialog.setInput(FilenameUtils.removeExtension(playerFileModel.getName()));
        }
        searchSubtitleDialog.show();
    }

    private BaseDialogListener searchSubtitleDialogListener = new BaseDialogListener() {

        @Override
        public void onBaseDialogListenerShow(EnumType type, BaseDialog dialog, View v, int position, Object data) {
        }

        @Override
        public void onBaseDialogListenerOk(EnumType type, BaseDialog dialog, View v, int position, Object data) {
            if (data instanceof String) {
                typeInputText = (String) data;
                searchSubtitlesByTitle2((String) data);
            }
            searchSubtitleDialog.dismiss();
        }

        @Override
        public void onBaseDialogListenerCancel(EnumType type, BaseDialog dialog, View v, int position, Object data) {
        }

        @Override
        public void onBaseDialogListenerClick(EnumType type, BaseDialog dialog, View v, int position, Object data) {
        }

        @Override
        public void onBaseDialogListenerClickMulti(EnumType type, BaseDialog dialog, View v, int position, Object[] data) {
        }
    };

    private BaseDialogListener downloadSubtitleDialogListener = new BaseDialogListener() {

        @Override
        public void onBaseDialogListenerShow(EnumType type, BaseDialog dialog, View v, int position, Object data) {
        }

        @Override
        public void onBaseDialogListenerOk(EnumType type, BaseDialog dialog, View v, int position, Object data) {
            String downloadedSubtitleFilePath = FilenameUtils.concat(FilenameUtils.getPath(playerFileModel.getPath()), (String) data +  "." + FilenameUtils.getExtension(subtitleFileLinkToDownload));
            if (StorageUtil.isFileExist(downloadedSubtitleFilePath)) {
                showWarningSameName(downloadedSubtitleFilePath);
            } else {
                saveSubtitleToFile(downloadedSubtitleFilePath);
            }
            downloadSubtitleDialog.dismiss();
        }

        @Override
        public void onBaseDialogListenerCancel(EnumType type, BaseDialog dialog, View v, int position, Object data) {
        }

        @Override
        public void onBaseDialogListenerClick(EnumType type, BaseDialog dialog, View v, int position, Object data) {
        }

        @Override
        public void onBaseDialogListenerClickMulti(EnumType type, BaseDialog dialog, View v, int position, Object[] data) {
        }
    };

    private OnClickListener onOpenSubtitleItemClickListener = (view, object) -> {
        if (object instanceof OpenSubtitleApi.OSSubtitleResponse) {
            Loading.show(this, R.string.download_msg_downloading);
            setTextInPreview("");
            try {
                String file_id = String.valueOf(((OpenSubtitleApi.OSSubtitleResponse) object).attributes.files.get(0).getFile_id());
                OpenSubtitleApi.OSDownloadParam param = new OpenSubtitleApi.OSDownloadParam(file_id);
                openSubtitleApi.getSubtitleDownloadLink(param, new DalApiListener<OpenSubtitleApi.OSDownloadResponse>() {
                    @Override
                    public void onSuccess(OpenSubtitleApi.OSDownloadResponse response) {
                        subtitleFileLinkToDownload = response.getLink();
                        String message = getString(R.string.msg_info_remain_open_subtitle_request, response.getRequests() + response.getRemaining(), response.getRemaining());
                        openSubtitleApi.downloadSubtitle(subtitleFileLinkToDownload, new DalApiListener<String>() {
                            @Override
                            public void onSuccess(String response) {
                                updateVisible_isSubtitleSelected(true); //setTextInPreview 보다 먼저 해야함.
                                setTextInPreview(response);
                                alertDialog.show(getString(R.string.info), message, getString(R.string.ok), null);
                                Loading.hideDelay(1000);
                            }

                            @Override
                            public void onFailure(String error) {
                                SnackbarUtil.getInstance(DownloadSubtitlesActivity.this).show(R.string.download_msg_download_fail);
                                updateVisible_isSubtitleSelected(false);
                                Loading.hideDelay(1000);
                            }
                        });
                    }

                    @Override
                    public void onFailure(String error) {
                        updateVisible_isSubtitleSelected(false);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                Loading.hideDelay(1000);
            }
        }
    };

    private void setTextInPreview(String text) {
        binding.tvPreview.setText(text);
        if (isSubtitleSelected) {
            showDownloadButton();
        } else {
            hideDownloadButton();
        }
    }

    private void saveSubtitleToFile(String downloadedSubtitleFilePath) {
        String subtitleText = binding.tvPreview.getText().toString();
        if (FileUtil.saveTextToFile(downloadedSubtitleFilePath, subtitleText)) {
            updateToVideoModel(downloadedSubtitleFilePath);
        } else {
            SnackbarUtil.getInstance(this).show(R.string.msg_export_file_fail_rename_and_retry);
        }
    }

    private void showDownloadFileDialog() {
        downloadSubtitleDialog.setTitle(R.string.download_subtitle_file);
        downloadSubtitleDialog.setSubTitle(getString(R.string.download_subtitle_file_message, FilenameUtils.getExtension(subtitleFileLinkToDownload)));
        downloadSubtitleDialog.setInput(FilenameUtils.removeExtension(playerFileModel.getName()));
        downloadSubtitleDialog.show();
        Utils.showSoftKeyboard(this);
    }

    private void showWarningSameName(String fileName) {
        final YesNoDialog dialog = new YesNoDialog(this, R.string.warning, R.string.msg_export_file_warning, fileName, new OnYesNoClickListener() {
            @Override
            public void onYesClick(View view, Object object) {
                saveSubtitleToFile((String) object);
            }

            @Override
            public void onNoClick(View view, Object object) {
                showDownloadFileDialog();
            }
        });
        dialog.show();
    }

    private void updateToVideoModel(String subPath) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            try {
                if (subPathIndex == Constant.PLAYER.INTENT.SUBPATH_INDEX_1) {
                    playerFileModel.setSubPath1(subPath);
                } else {
                    playerFileModel.setSubPath2(subPath);
                }
                resetSubtitleFileAndDatabase(playerFileModel, subPath, subPathIndex);

                runOnUiThread(() -> {
                    updateVideoModel(playerFileModel.getVideoModel());
                    onBackPressed();
                });
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                Loading.hide();
                executorService.shutdown();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        logout();
    }
    //Don't delete it. 로그아웃할일은 없지만, 테스트할때 필요하므로 그냥둔다.
    private void logout() {
        openSubtitleApi.logout(new DalApiListener<Boolean>() {
            @Override
            public void onSuccess(Boolean response) {
                sharedPreferences.setOpenSubtitleLoginToken("");
            }

            @Override
            public void onFailure(String error) {
                sharedPreferences.setOpenSubtitleLoginToken("");
            }
        });
    }
}
