package com.dalread.listener;

public interface OnDownloadFileListener {
    void fileCountToDownload(int count);
    void onDownloadOneItemSuccess(int index, String fileName);
    void onDownloadOneItemFailure(int index, String fileName);
    void onFinish();
}
