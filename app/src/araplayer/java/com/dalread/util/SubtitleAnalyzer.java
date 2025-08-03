package com.dalread.util;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.dalread.database.sqlite.SubDatabase;
import com.dalread.model.PlayerFileModel;
import com.dalread.util.arasubtitle.AbstractTranslateFileService;
import com.dalread.util.arasubtitle.MOVIE_ASSService;
import com.dalread.util.arasubtitle.MOVIE_BracketSubtitleService;
import com.dalread.util.arasubtitle.MOVIE_SMIService;
import com.dalread.util.arasubtitle.MOVIE_SQLITEService;
import com.dalread.util.arasubtitle.MOVIE_SRTService;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 자막 분석을 담당하는 클래스
 * MediaInformationActivity에서 자막 분석 로직을 분리
 */
public class SubtitleAnalyzer {
    
    private final Context context;
    private final SubDatabase dicDatabase;
    private final SubDatabase subDatabase;
    private final PlayerFileModel playerFileModel;
    private final ExecutorService executorService;
    private final Handler mainHandler;
    
    public SubtitleAnalyzer(Context context, SubDatabase dicDatabase, SubDatabase subDatabase, 
                           PlayerFileModel playerFileModel) {
        this.context = context;
        this.dicDatabase = dicDatabase;
        this.subDatabase = subDatabase;
        this.playerFileModel = playerFileModel;
        this.executorService = Executors.newSingleThreadExecutor();
        this.mainHandler = new Handler(Looper.getMainLooper());
    }
    
    /**
     * 자막 파일을 분석하여 루비 텍스트를 생성
     * @param resultData 자막 파일 또는 분석할 데이터
     * @param onAnalysisComplete 분석 완료 콜백
     */
    public void analyzeSubtitle(Object resultData, OnAnalysisCompleteListener onAnalysisComplete) {
        executorService.execute(() -> {
            // UI 스레드에서 로딩 표시
            mainHandler.post(() -> {
                if (onAnalysisComplete != null) {
                    onAnalysisComplete.onAnalysisStarted();
                }
            });
            try {
                File subtitleFile = null;
                AbstractTranslateFileService fileService = new MOVIE_SRTService(); // 기본값으로 초기화
                if (resultData instanceof File) {
                    subtitleFile = (File) resultData;
                    if (SupportSubtitleFormat.isSubtitleFormat(FileUtil.getSubtitleExtension(subtitleFile.getName()))) {
                        String subtitleContent = FileUtil.getFileContentsFromFile(subtitleFile);
                        int subtitleFormat = new SubtitleFormatDetector().detectSubtitleFormatFromContent(subtitleContent);
                        switch (subtitleFormat) {
                            case SubtitleFormatDetector.FORMAT_SMI:
                                fileService = new MOVIE_SMIService();
                                break;
                            case SubtitleFormatDetector.FORMAT_SRT:
                                fileService = new MOVIE_SRTService();
                                break;
                            case SubtitleFormatDetector.FORMAT_ASS:
                                fileService = new MOVIE_ASSService();
                                break;
                            case SubtitleFormatDetector.FORMAT_BRACKET:
                                fileService = new MOVIE_BracketSubtitleService();
                                break;
                            default:
                                break;
                        }
                    } else {
                        fileService = new MOVIE_SQLITEService();
                    }
                }


                fileService.setDicDatabase(dicDatabase);
                fileService.setSubDatabase(subDatabase);
                fileService.translateFileWithFixedNameDTO(subtitleFile, null, fileService);
                
                // 분석 완료 처리
                final AbstractTranslateFileService finalFileService = fileService;
                mainHandler.post(() -> {
                    if (onAnalysisComplete != null) {
                        onAnalysisComplete.onAnalysisSuccess(finalFileService);
                    }
                });
            } catch (Exception e) {
                // 분석 실패 처리
                mainHandler.post(() -> {
                    if (onAnalysisComplete != null) {
                        onAnalysisComplete.onAnalysisError(e.getMessage());
                    }
                });
            }
        });
    }
    
    /**
     * 리소스 정리
     */
    public void shutdown() {
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }
    
    /**
     * 자막 분석 완료 리스너
     */
    public interface OnAnalysisCompleteListener {
        void onAnalysisStarted();
        void onAnalysisSuccess(AbstractTranslateFileService fileService);
        void onAnalysisError(String errorMessage);
    }
} 