package com.dalread.listener;

import com.dalread.model.VocaDownload;

public interface OnVoiceFileInfoDownloadListener {
    void onSuccess(VocaDownload vocaDownload, String voiceFileName);
    void onFailure(String voiceFileName);
}
