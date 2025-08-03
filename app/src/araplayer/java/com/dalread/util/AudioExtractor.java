package com.dalread.util;

import android.content.Context;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.media.MediaCodec;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.AudioManager;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Android MediaExtractor를 이용한 비디오에서 오디오 추출 클래스
 */
public class AudioExtractor {
    private static final String TAG = "Generate Subtitle";
    
    private Context context;
    private AudioExtractionListener listener;
    private boolean isExtracting = false;
    
    public interface AudioExtractionListener {
        void onExtractionStarted();
        void onExtractionSuccess(String audioPath);
        void onExtractionError(String error);
    }
    
    public AudioExtractor(Context context) {
        this.context = context;
    }
    
    public void setListener(AudioExtractionListener listener) {
        this.listener = listener;
    }
    
    /**
     * 비디오에서 오디오 추출 시작
     * @param videoPath 비디오 파일 경로
     * @param outputDir 출력 디렉토리
     */
    public void extractAudioFromVideo(String videoPath, String outputDir) {
        DLog.i(TAG, "=== 오디오 추출 시작 (MediaExtractor) ===");
        DLog.i(TAG, "비디오 파일: " + videoPath);
        DLog.i(TAG, "출력 디렉토리: " + outputDir);
        
        if (isExtracting) {
            DLog.e(TAG, "이미 오디오 추출이 진행 중입니다");
            if (listener != null) {
                listener.onExtractionError("이미 오디오 추출이 진행 중입니다");
            }
            return;
        }
        
        // 입력 파일 검증
        if (!validateInputFile(videoPath)) {
            return;
        }
        
        // 출력 디렉토리 검증 및 생성
        if (!validateAndCreateOutputDir(outputDir)) {
            return;
        }
        
        if (listener != null) {
            listener.onExtractionStarted();
        }
        
        isExtracting = true;
        
        // 백그라운드 스레드에서 오디오 추출 실행
        new Thread(() -> {
            try {
                String audioPath = extractAudioWithMediaExtractor(videoPath, outputDir);
                if (audioPath != null) {
                    DLog.i(TAG, "=== 오디오 추출 성공 ===");
                    DLog.i(TAG, "생성된 오디오 파일: " + audioPath);
                    
                    if (listener != null) {
                        listener.onExtractionSuccess(audioPath);
                    }
                } else {
                    DLog.e(TAG, "오디오 추출 실패");
                    if (listener != null) {
                        listener.onExtractionError("오디오 추출에 실패했습니다");
                    }
                }
            } catch (Exception e) {
                DLog.e(TAG, "오디오 추출 중 예외 발생: " + e.getMessage());
                if (listener != null) {
                    listener.onExtractionError("오디오 추출 중 오류가 발생했습니다: " + e.getMessage());
                }
            } finally {
                isExtracting = false;
            }
        }).start();
    }
    
    /**
     * MediaExtractor를 사용한 오디오 추출
     */
    private String extractAudioWithMediaExtractor(String videoPath, String outputDir) {
        MediaExtractor extractor = null;
        MediaMuxer muxer = null;
        MediaCodec decoder = null;
        MediaCodec encoder = null;
        
        try {
            // MediaExtractor 초기화
            extractor = new MediaExtractor();
            extractor.setDataSource(videoPath);
            
            DLog.i(TAG, "트랙 수: " + extractor.getTrackCount());
            
            // 오디오 트랙 찾기
            int audioTrackIndex = -1;
            MediaFormat audioFormat = null;
            
            for (int i = 0; i < extractor.getTrackCount(); i++) {
                MediaFormat format = extractor.getTrackFormat(i);
                String mime = format.getString(MediaFormat.KEY_MIME);
                
                DLog.i(TAG, "트랙 " + i + " MIME: " + mime);
                
                if (mime != null && mime.startsWith("audio/")) {
                    audioTrackIndex = i;
                    audioFormat = format;
                    DLog.i(TAG, "오디오 트랙 발견: " + i);
                    break;
                }
            }
            
            if (audioTrackIndex == -1) {
                DLog.e(TAG, "오디오 트랙을 찾을 수 없습니다");
                return null;
            }
            
            // 오디오 파일명 생성
            String audioFileName = generateAudioFileName(videoPath);
            String audioPath = outputDir + File.separator + audioFileName;
            
            DLog.i(TAG, "오디오 파일 경로: " + audioPath);
            
            // MediaMuxer 초기화
            muxer = new MediaMuxer(audioPath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
            
            // 오디오 트랙 선택
            extractor.selectTrack(audioTrackIndex);
            
            String mimeType = audioFormat.getString(MediaFormat.KEY_MIME);
            DLog.i(TAG, "오디오 MIME 타입: " + mimeType);
            
            // AAC 코덱이 아닌 경우 트랜스코딩 필요
            if (!"audio/mp4a-latm".equals(mimeType)) {
                DLog.i(TAG, "AAC가 아닌 코덱 발견, 트랜스코딩 시작...");
                return transcodeAudioToAAC(extractor, audioTrackIndex, audioFormat, muxer, audioPath);
            } else {
                // AAC 코덱인 경우 직접 복사
                DLog.i(TAG, "AAC 코덱 발견, 직접 복사 시작...");
                return copyAudioDirectly(extractor, audioTrackIndex, audioFormat, muxer, audioPath);
            }
            
        } catch (IOException e) {
            DLog.e(TAG, "MediaExtractor 오류: " + e.getMessage());
            return null;
        } catch (Exception e) {
            DLog.e(TAG, "오디오 추출 중 예외: " + e.getMessage());
            return null;
        } finally {
            // 리소스 정리
            if (muxer != null) {
                try {
                    muxer.stop();
                    muxer.release();
                } catch (Exception e) {
                    DLog.e(TAG, "MediaMuxer 정리 중 오류: " + e.getMessage());
                }
            }
            
            if (decoder != null) {
                try {
                    decoder.stop();
                    decoder.release();
                } catch (Exception e) {
                    DLog.e(TAG, "Decoder 정리 중 오류: " + e.getMessage());
                }
            }
            
            if (encoder != null) {
                try {
                    encoder.stop();
                    encoder.release();
                } catch (Exception e) {
                    DLog.e(TAG, "Encoder 정리 중 오류: " + e.getMessage());
                }
            }
            
            if (extractor != null) {
                try {
                    extractor.release();
                } catch (Exception e) {
                    DLog.e(TAG, "MediaExtractor 정리 중 오류: " + e.getMessage());
                }
            }
        }
    }
    
    /**
     * AAC가 아닌 오디오를 AAC로 트랜스코딩
     */
    private String transcodeAudioToAAC(MediaExtractor extractor, int audioTrackIndex, 
                                     MediaFormat inputFormat, MediaMuxer muxer, String outputPath) {
        MediaCodec decoder = null;
        MediaCodec encoder = null;
        
        try {
            // 디코더 생성
            String inputMime = inputFormat.getString(MediaFormat.KEY_MIME);
            decoder = MediaCodec.createDecoderByType(inputMime);
            decoder.configure(inputFormat, null, null, 0);
            decoder.start();
            
            // AAC 인코더 생성
            MediaFormat outputFormat = MediaFormat.createAudioFormat(
                MediaFormat.MIMETYPE_AUDIO_AAC,
                inputFormat.getInteger(MediaFormat.KEY_SAMPLE_RATE),
                inputFormat.getInteger(MediaFormat.KEY_CHANNEL_COUNT)
            );
            outputFormat.setInteger(MediaFormat.KEY_BIT_RATE, 128000); // 128kbps
            outputFormat.setInteger(MediaFormat.KEY_AAC_PROFILE, 2); // AAC LC Profile
            
            encoder = MediaCodec.createEncoderByType(MediaFormat.MIMETYPE_AUDIO_AAC);
            encoder.configure(outputFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
            encoder.start();
            
            // MediaMuxer에 트랙 추가
            int muxerTrackIndex = muxer.addTrack(outputFormat);
            muxer.start();
            
            DLog.i(TAG, "트랜스코딩 시작...");
            
            // 트랜스코딩 루프
            ByteBuffer[] inputBuffers = decoder.getInputBuffers();
            ByteBuffer[] outputBuffers = decoder.getOutputBuffers();
            ByteBuffer[] encoderInputBuffers = encoder.getInputBuffers();
            ByteBuffer[] encoderOutputBuffers = encoder.getOutputBuffers();
            
            MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
            MediaCodec.BufferInfo encoderBufferInfo = new MediaCodec.BufferInfo();
            
            boolean isEOS = false;
            int totalBytes = 0;
            
            while (!isEOS) {
                // 디코더 입력
                int inputBufferIndex = decoder.dequeueInputBuffer(10000);
                if (inputBufferIndex >= 0) {
                    ByteBuffer inputBuffer = inputBuffers[inputBufferIndex];
                    int sampleSize = extractor.readSampleData(inputBuffer, 0);
                    
                    if (sampleSize < 0) {
                        decoder.queueInputBuffer(inputBufferIndex, 0, 0, 0, MediaCodec.BUFFER_FLAG_END_OF_STREAM);
                        isEOS = true;
                    } else {
                        decoder.queueInputBuffer(inputBufferIndex, 0, sampleSize, 
                            extractor.getSampleTime(), extractor.getSampleFlags());
                        extractor.advance();
                    }
                }
                
                // 디코더 출력
                int outputBufferIndex = decoder.dequeueOutputBuffer(bufferInfo, 10000);
                if (outputBufferIndex >= 0) {
                    ByteBuffer outputBuffer = outputBuffers[outputBufferIndex];
                    
                    // 인코더 입력
                    int encoderInputBufferIndex = encoder.dequeueInputBuffer(10000);
                    if (encoderInputBufferIndex >= 0) {
                        ByteBuffer encoderInputBuffer = encoderInputBuffers[encoderInputBufferIndex];
                        encoderInputBuffer.clear();
                        encoderInputBuffer.put(outputBuffer);
                        encoder.queueInputBuffer(encoderInputBufferIndex, 0, bufferInfo.size, 
                            bufferInfo.presentationTimeUs, bufferInfo.flags);
                    }
                    
                    decoder.releaseOutputBuffer(outputBufferIndex, false);
                }
                
                // 인코더 출력
                int encoderOutputBufferIndex = encoder.dequeueOutputBuffer(encoderBufferInfo, 10000);
                if (encoderOutputBufferIndex >= 0) {
                    ByteBuffer encoderOutputBuffer = encoderOutputBuffers[encoderOutputBufferIndex];
                    
                    if (encoderBufferInfo.size > 0) {
                        muxer.writeSampleData(muxerTrackIndex, encoderOutputBuffer, encoderBufferInfo);
                        totalBytes += encoderBufferInfo.size;
                        DLog.i(TAG, "트랜스코딩된 데이터: " + encoderBufferInfo.size + " bytes, 총: " + totalBytes + " bytes");
                    }
                    
                    encoder.releaseOutputBuffer(encoderOutputBufferIndex, false);
                }
            }
            
            DLog.i(TAG, "트랜스코딩 완료 - 총 " + totalBytes + " bytes");
            
            // 생성된 파일 확인
            File audioFile = new File(outputPath);
            if (audioFile.exists() && audioFile.length() > 0) {
                DLog.i(TAG, "트랜스코딩된 오디오 파일 생성 성공 - 크기: " + audioFile.length() + " bytes");
                return outputPath;
            } else {
                DLog.e(TAG, "트랜스코딩된 오디오 파일이 생성되지 않았거나 비어있음");
                return null;
            }
            
        } catch (Exception e) {
            DLog.e(TAG, "트랜스코딩 중 오류: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * AAC 오디오를 직접 복사
     */
    private String copyAudioDirectly(MediaExtractor extractor, int audioTrackIndex, 
                                   MediaFormat audioFormat, MediaMuxer muxer, String outputPath) {
        try {
            // MediaMuxer에 트랙 추가
            int muxerTrackIndex = muxer.addTrack(audioFormat);
            muxer.start();
            
            DLog.i(TAG, "AAC 직접 복사 시작...");
            
            // 오디오 데이터 추출 및 복사
            ByteBuffer buffer = ByteBuffer.allocate(1024 * 1024); // 1MB 버퍼
            MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
            int sampleSize = 0;
            int totalBytes = 0;
            
            while ((sampleSize = extractor.readSampleData(buffer, 0)) >= 0) {
                bufferInfo.offset = 0;
                bufferInfo.size = sampleSize;
                bufferInfo.presentationTimeUs = extractor.getSampleTime();
                bufferInfo.flags = extractor.getSampleFlags();
                
                // 오디오 데이터를 MediaMuxer에 쓰기
                muxer.writeSampleData(muxerTrackIndex, buffer, bufferInfo);
                
                totalBytes += sampleSize;
                DLog.i(TAG, "복사된 데이터: " + sampleSize + " bytes, 총: " + totalBytes + " bytes");
                
                // 다음 샘플로 이동
                extractor.advance();
            }
            
            DLog.i(TAG, "AAC 직접 복사 완료 - 총 " + totalBytes + " bytes");
            
            // 생성된 파일 확인
            File audioFile = new File(outputPath);
            if (audioFile.exists() && audioFile.length() > 0) {
                DLog.i(TAG, "AAC 오디오 파일 생성 성공 - 크기: " + audioFile.length() + " bytes");
                return outputPath;
            } else {
                DLog.e(TAG, "AAC 오디오 파일이 생성되지 않았거나 비어있음");
                return null;
            }
            
        } catch (Exception e) {
            DLog.e(TAG, "AAC 직접 복사 중 오류: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * 입력 파일 검증
     */
    private boolean validateInputFile(String videoPath) {
        if (videoPath == null || videoPath.trim().isEmpty()) {
            DLog.e(TAG, "비디오 파일 경로가 비어있음");
            if (listener != null) {
                listener.onExtractionError("비디오 파일 경로가 유효하지 않습니다");
            }
            return false;
        }
        
        File videoFile = new File(videoPath);
        if (!videoFile.exists()) {
            DLog.e(TAG, "비디오 파일이 존재하지 않음: " + videoPath);
            if (listener != null) {
                listener.onExtractionError("비디오 파일을 찾을 수 없습니다: " + videoFile.getName());
            }
            return false;
        }
        
        if (!videoFile.canRead()) {
            DLog.e(TAG, "비디오 파일을 읽을 수 없음: " + videoPath);
            if (listener != null) {
                listener.onExtractionError("비디오 파일에 접근 권한이 없습니다");
            }
            return false;
        }
        
        if (videoFile.length() == 0) {
            DLog.e(TAG, "비디오 파일이 비어있음: " + videoPath);
            if (listener != null) {
                listener.onExtractionError("비디오 파일이 비어있습니다");
            }
            return false;
        }
        
        DLog.i(TAG, "입력 파일 검증 성공 - 크기: " + videoFile.length() + " bytes");
        return true;
    }
    
    /**
     * 출력 디렉토리 검증 및 생성
     */
    private boolean validateAndCreateOutputDir(String outputDir) {
        if (outputDir == null || outputDir.trim().isEmpty()) {
            DLog.e(TAG, "출력 디렉토리 경로가 비어있음");
            if (listener != null) {
                listener.onExtractionError("출력 디렉토리 경로가 유효하지 않습니다");
            }
            return false;
        }
        
        File outputDirectory = new File(outputDir);
        if (!outputDirectory.exists()) {
            boolean created = outputDirectory.mkdirs();
            if (!created) {
                DLog.e(TAG, "출력 디렉토리 생성 실패: " + outputDir);
                if (listener != null) {
                    listener.onExtractionError("출력 디렉토리를 생성할 수 없습니다");
                }
                return false;
            }
            DLog.i(TAG, "출력 디렉토리 생성 성공: " + outputDir);
        }
        
        if (!outputDirectory.canWrite()) {
            DLog.e(TAG, "출력 디렉토리에 쓰기 권한 없음: " + outputDir);
            if (listener != null) {
                listener.onExtractionError("출력 디렉토리에 쓰기 권한이 없습니다");
            }
            return false;
        }
        
        return true;
    }
    
    /**
     * 오디오 파일명 생성
     */
    private String generateAudioFileName(String videoPath) {
        String videoFileName = new File(videoPath).getName();
        String baseFileName;
        
        if (videoFileName.contains(".")) {
            baseFileName = videoFileName.substring(0, videoFileName.lastIndexOf('.'));
        } else {
            baseFileName = videoFileName;
        }
        
        // 특수문자 제거 및 안전한 파일명 생성
        baseFileName = baseFileName.replaceAll("[^a-zA-Z0-9가-힣]", "_");
        if (baseFileName.isEmpty()) {
            baseFileName = "extracted_audio";
        }
        
        return baseFileName + "_extracted.m4a";
    }
    
    /**
     * 오디오 추출 중단
     */
    public void cancelExtraction() {
        DLog.i(TAG, "오디오 추출 중단 요청");
        isExtracting = false;
    }
    
    /**
     * 추출 중인지 확인
     */
    public boolean isExtracting() {
        return isExtracting;
    }
} 