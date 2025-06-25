package com.dalread.activity;

import android.view.View;

import com.dalread.R;
import com.dalread.util.DLog;
import com.dalread.util.StorageUtil;
import com.dalread.util.Utils;

import wseemann.media.FFmpegMediaMetadataRetriever;

public class MusicInformationActivity extends MediaInformationActivity implements View.OnClickListener { //implements OnAsyncTaskListenerWithType {
    @Override
    public void initData() {
        super.initData();
        updateUI();
        initOnClickListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isReload) {
            isReload = false;
            updateUI();
        }

    }

    protected void updateUI() {
        super.updateUI();
        if (isDestroyed() || isFinishing()) return;

        runOnUiThread(() -> {
            binding.ivTmdbLogo.setVisibility(View.GONE);
            displayTtsTitleForAudio();

            try {
                if (playerFileModel.isLocal()) {
                    FFmpegMediaMetadataRetriever mmr = new FFmpegMediaMetadataRetriever();
                    mmr.setDataSource(playerFileModel.getPath());
                    binding.tvCodec.setText(StorageUtil.getCodecInfoAudioFile(this, mmr));
                }
            } catch (Exception ex) {
                DLog.e(getLogTag(), ex.getMessage());
            }
            updateMusicInformationUI();
        });

    }

    private void displayTtsTitleForAudio() {
        binding.tvTtsTitle.setVisibility(View.VISIBLE);
        binding.tvTtsTitle.setText(playerFileModel.getVideoModel().getTitleTts());

    }

    private void updateMusicInformationUI() {
        binding.tlMusicInformation.setVisibility(View.VISIBLE);

        updateInformationUI_DisplayTitle();
        updateInformationUI_TtsTitle();
        updateInformationUI_Artist();
        updateInformationUI_TtsArtist();
        updateInformationUI_Album();
    }

    private void updateInformationUI_Album() {
        binding.tbAlbum.setVisibility(Utils.isEmpty(playerFileModel.getVideoModel().getAlbum()) ? View.GONE : View.VISIBLE);
        binding.tvAlbum.setText(getAlbumFromDB());
    }

    private void updateInformationUI_Artist() {
        binding.tbArtist.setVisibility(Utils.isEmpty(playerFileModel.getVideoModel().getPureArtist()) ? View.GONE : View.VISIBLE);
        binding.tvArtist.setText(getArtistFromDB());
    }

    private void updateInformationUI_DisplayTitle() {
        binding.tbDisplayTitle.setVisibility(Utils.isEmpty(playerFileModel.getVideoModel().getTitleTts()) ? View.GONE : View.VISIBLE);
        binding.tvDisplayTitle.setText(getDisplayTitleFromDB());
    }

    private String getTtsTitleForInputDialog() {
        if (Utils.isEmpty(getTtsTitleFromDB())) {
            if (Utils.isEmpty(getDisplayTitleFromDB())) {
                return getMediaFileBaseName();
            } else {
                return getDisplayTitleFromDB();
            }
        }
        return getTtsTitleFromDB();
    }
    private String getTtsTitleFromDB() {
        if (playerFileModel.getVideoModel().getTitleTts().equals(getMediaFileBaseName()))
            return getMediaFileBaseName();

        return playerFileModel.getVideoModel().getTitleTts();
    }

    private String getDisplayTitleForInputDialog() {
        if (Utils.isEmpty(getDisplayTitleFromDB())) {
            return getMediaFileBaseName();
        }
        return getDisplayTitleFromDB();
    }
    private String getDisplayTitleFromDB() {
        return playerFileModel.getVideoModel().getDisplayTitle();
    }

    private String getTtsArtistForInputDialog() {
        if (Utils.isEmpty(getTtsArtistFromDB())) {
            if (Utils.isEmpty(getArtistFromDB())) {
                return getMediaFileBaseName();
            } else {
                return getArtistFromDB();
            }
        }
        return getTtsArtistFromDB();
    }
    private String getTtsArtistFromDB() {
        return playerFileModel.getVideoModel().getArtistTts();
    }

    private String getArtistForInputDialog() {
        if (Utils.isEmpty(getArtistFromDB())) {
            return getMediaFileBaseName();
        }
        return getArtistFromDB();
    }

    private String getMediaFileBaseName() {
        return playerFileModel.getVideoModel().getBaseName();
    }

    private String getArtistFromDB() {
        return playerFileModel.getVideoModel().getArtist();
    }

    private String getAlbumForInputDialog() {
        if (Utils.isEmpty(getAlbumFromDB())) {
            return getMediaFileBaseName();
        }
        return getAlbumFromDB();
    }

    private String getAlbumFromDB() {
        return playerFileModel.getVideoModel().getAlbum();
    }

    private void updateInformationUI_TtsTitle() {
        if (Utils.isEmpty(playerFileModel.getVideoModel().getPureTtsTitle())) {
            binding.tbTtsTitle.setVisibility(View.GONE);
        } else if (playerFileModel.getVideoModel().getPureTtsTitle().equals(playerFileModel.getVideoModel().getDisplayTitle())) {
            binding.tbTtsTitle.setVisibility(View.GONE);
        } else if (playerFileModel.getVideoModel().getTitleTts().equals(playerFileModel.getVideoModel().getDisplayTitle())) {
            binding.tbTtsTitle.setVisibility(View.GONE);
        } else {
            binding.tbTtsTitle.setVisibility(View.VISIBLE);
            binding.tvTtsTitle.setText(getTtsTitleFromDB());
        }
    }

    private void updateInformationUI_TtsArtist() {
        if (Utils.isEmpty(playerFileModel.getVideoModel().getPureTtsArtist())) {
            binding.tbTtsArtist.setVisibility(View.GONE);
        } else if (playerFileModel.getVideoModel().getPureTtsArtist().equals(playerFileModel.getVideoModel().getArtist())) {
            binding.tbTtsArtist.setVisibility(View.GONE);
        } else {
            binding.tbTtsArtist.setVisibility(View.VISIBLE);
            binding.tvTtsArtist.setText(getTtsArtistFromDB());
        }
    }

    @Override
    protected String getInputDialogInputValue() {
        String result = "";
        if (editDialogType == EditDialogType.MEMO) {
            result = getMemoFromDB();
        } else if (editDialogType == EditDialogType.TTS_TITLE) {
            result = getTtsTitleForInputDialog();
        } else if (editDialogType == EditDialogType.DISPLAY_TITLE) {
            result = getDisplayTitleForInputDialog();
        } else if (editDialogType == EditDialogType.TTS_ARTIST) {
            result = getTtsArtistForInputDialog();
        } else if (editDialogType == EditDialogType.ARTIST) {
            result = getArtistForInputDialog();
        } else if (editDialogType == EditDialogType.ALBUM) {
            result = getAlbumForInputDialog();
        }
        return result;
    }

    protected void updateMediaInfoValue(String value) {
        if (editDialogType == EditDialogType.TTS_TITLE) {
            binding.tvTtsTitle.setText(value);
            playerFileModel.getVideoModel().setTitleTts(value);
        } else if (editDialogType == EditDialogType.DISPLAY_TITLE) {
            binding.tvDisplayTitle.setText(value);
            playerFileModel.getVideoModel().setDisplayTitle(value);
        } else if (editDialogType == EditDialogType.TTS_ARTIST) {
            binding.tvTtsArtist.setText(value);
            playerFileModel.getVideoModel().setArtistTts(value);
        } else if (editDialogType == EditDialogType.ARTIST) {
            binding.tvArtist.setText(value);
            playerFileModel.getVideoModel().setArtist(value);
        } else if (editDialogType == EditDialogType.ALBUM) {
            binding.tvAlbum.setText(value);
            playerFileModel.getVideoModel().setAlbum(value);
        }
        updateMusicInformationUI();
    }

    private void initOnClickListener() {
        binding.tbTtsTitle.setOnClickListener(this);
        binding.tvTtsTitle.setOnClickListener(this);
        binding.tbDisplayTitle.setOnClickListener(this);
        binding.tvDisplayTitle.setOnClickListener(this);
        binding.tbTtsArtist.setOnClickListener(this);
        binding.tvTtsArtist.setOnClickListener(this);
        binding.tbArtist.setOnClickListener(this);
        binding.tvArtist.setOnClickListener(this);
        binding.tbAlbum.setOnClickListener(this);
        binding.tvAlbum.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.tbTtsTitle:
            case R.id.tvTtsTitle:
                editDialogType = EditDialogType.TTS_TITLE;
                showTypeInputDialog();
                break;
            case R.id.tbDisplayTitle:
            case R.id.tvDisplayTitle:
                editDialogType = EditDialogType.DISPLAY_TITLE;
                showTypeInputDialog();
                break;
            case R.id.tbTtsArtist:
            case R.id.tvTtsArtist:
                editDialogType = EditDialogType.TTS_ARTIST;
                showTypeInputDialog();
                break;
            case R.id.tbArtist:
            case R.id.tvArtist:
                editDialogType = EditDialogType.ARTIST;
                showTypeInputDialog();
                break;
            case R.id.tbAlbum:
            case R.id.tvAlbum:
                editDialogType = EditDialogType.ALBUM;
                showTypeInputDialog();
                break;
        }
    }
//    @OnClick({R.id.tvTtsTitle, R.id.tvDisplayTitle, R.id.tvTtsArtist, R.id.tvArtist, R.id.tvAlbum})
//    void onClick(View view) {
//        super.onClick(view);
//        switch (view.getId()) {
//            case R.id.tbTtsTitle:
//            case R.id.tvTtsTitle:
//                editDialogType = EditDialogType.TTS_TITLE;
//                showTypeInputDialog();
//                break;
//            case R.id.tbDisplayTitle:
//            case R.id.tvDisplayTitle:
//                editDialogType = EditDialogType.DISPLAY_TITLE;
//                showTypeInputDialog();
//                break;
//            case R.id.tbTtsArtist:
//            case R.id.tvTtsArtist:
//                editDialogType = EditDialogType.TTS_ARTIST;
//                showTypeInputDialog();
//                break;
//            case R.id.tbArtist:
//            case R.id.tvArtist:
//                editDialogType = EditDialogType.ARTIST;
//                showTypeInputDialog();
//                break;
//            case R.id.tbAlbum:
//            case R.id.tvAlbum:
//                editDialogType = EditDialogType.ALBUM;
//                showTypeInputDialog();
//                break;
//        }
//    }
}
