package com.dalread.util;

import android.content.Context;
import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import org.vosk.Model;
import org.vosk.Recognizer;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

/**
 * Vosk를 사용한 영어 STT (Speech-to-Text) 클래스
 */
public class VoskTranscriber {
    private static final String TAG = "AUDIO_GENERATION";
    
    private Context context;
    private Model model;
    private Recognizer recognizer;
    private boolean isInitialized = false;
    
    public VoskTranscriber(Context context) {
        this.context = context;
    }
    
    /**
     * Vosk 모델 초기화
     * @param modelPath 모델 디렉토리 경로 (assets 내부)
     * @return 초기화 성공 여부
     */
    public boolean initializeModel(String modelPath) {
        try {
            Log.i(TAG, "Vosk 모델 초기화 시작: " + modelPath);
            
            // assets에서 모델 디렉토리 복사
            File modelDir = new File(context.getFilesDir(), "vosk-model");
            if (!modelDir.exists()) {
                modelDir.mkdirs();
            }
            
            // 모델 디렉토리가 없으면 복사
            File targetModelDir = new File(modelDir, modelPath);
            if (!targetModelDir.exists()) {
                Log.i(TAG, "모델 디렉토리를 assets에서 복사 중...");
                copyAssetDirectory(modelPath, targetModelDir);
            } else {
                Log.i(TAG, "모델 디렉토리가 이미 존재함: " + targetModelDir.getAbsolutePath());
            }
            
            // 모델 디렉토리 내용 확인
            Log.i(TAG, "모델 디렉토리 내용:");
            if (targetModelDir.exists() && targetModelDir.isDirectory()) {
                File[] files = targetModelDir.listFiles();
                if (files != null) {
                    for (File file : files) {
                        if (file.isDirectory()) {
                            Log.i(TAG, "  - [DIR] " + file.getName());
                            // 하위 디렉토리 내용도 확인
                            File[] subFiles = file.listFiles();
                            if (subFiles != null) {
                                for (File subFile : subFiles) {
                                    Log.i(TAG, "    - " + subFile.getName() + " (" + subFile.length() + " bytes)");
                                }
                            }
                        } else {
                            Log.i(TAG, "  - " + file.getName() + " (" + file.length() + " bytes)");
                        }
                    }
                }
            }
            
            Log.i(TAG, "Vosk 모델 로드 시작: " + targetModelDir.getAbsolutePath());
            // Vosk 모델 로드
            model = new Model(targetModelDir.getAbsolutePath());
            Log.i(TAG, "Vosk 모델 로드 완료");
            
            // Recognizer 설정 조정
            recognizer = new Recognizer(model, 16000.0f);
            recognizer.setWords(false);  // 단어별 타임스탬프 활성화
            Log.i(TAG, "Vosk Recognizer 생성 완료 (단어 타임스탬프 활성화)");
            
            isInitialized = true;
            Log.i(TAG, "Vosk 모델 초기화 완료");
            return true;
            
        } catch (Exception e) {
            Log.e(TAG, "Vosk 모델 초기화 실패", e);
            return false;
        }
    }
    
    /**
     * 오디오 파일에서 텍스트 추출
     * @param audioFilePath 오디오 파일 경로
     * @return 추출된 텍스트
     */
    public String transcribeAudio(String audioFilePath) {
        if (!isInitialized) {
            Log.e(TAG, "Vosk 모델이 초기화되지 않았습니다");
            return null;
        }
        
        try {
            Log.i(TAG, "오디오 파일 경로 : " + audioFilePath);
            
            File audioFile = new File(audioFilePath);
            if (!audioFile.exists()) {
                Log.e(TAG, "오디오 파일이 존재하지 않습니다: " + audioFilePath);
                return null;
            }
            
            Log.i(TAG, "오디오 파일 크기: " + audioFile.length() + " bytes");
            
            // 오디오 파일 정보 추가 확인
            try {
                android.media.MediaMetadataRetriever retriever = new android.media.MediaMetadataRetriever();
                retriever.setDataSource(audioFilePath);
                String duration = retriever.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_DURATION);
                String sampleRate = retriever.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_SAMPLERATE);
                String bitRate = retriever.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_BITRATE);
                retriever.release();
                
                Log.i(TAG, "오디오 파일 정보 - 길이: " + duration + "ms, 샘플레이트: " + sampleRate + "Hz, 비트레이트: " + bitRate + "bps");
            } catch (Exception e) {
                Log.e(TAG, "오디오 파일 정보 확인 실패", e);
            }
            
            // Vosk로 직접 음성 인식 (FFmpeg 없이)
            String result = performRecognition(audioFilePath);
            
            Log.i(TAG, "음성 인식 완료: " + result);
            return result;
            
        } catch (Exception e) {
            Log.e(TAG, "음성 인식 실패", e);
            return null;
        }
    }
    
    /**
     * Assets에서 디렉토리를 내부 저장소로 복사
     */
    private void copyAssetDirectory(String assetDir, File destDir) throws IOException {
        if (!destDir.exists()) {
            destDir.mkdirs();
        }
        
        String[] files = context.getAssets().list(assetDir);
        if (files != null) {
            for (String file : files) {
                String assetPath = assetDir + "/" + file;
                File destFile = new File(destDir, file);
                
                // 하위 디렉토리가 있는지 확인
                String[] subFiles = context.getAssets().list(assetPath);
                if (subFiles != null && subFiles.length > 0) {
                    // 디렉토리인 경우 재귀적으로 복사
                    copyAssetDirectory(assetPath, destFile);
                } else {
                    // 파일인 경우 복사
                    try (InputStream is = context.getAssets().open(assetPath);
                         java.io.FileOutputStream fos = new java.io.FileOutputStream(destFile)) {
                        
                        byte[] buffer = new byte[8192];
                        int length;
                        while ((length = is.read(buffer)) > 0) {
                            fos.write(buffer, 0, length);
                        }
                    }
                }
            }
        }
    }
    

    
        
    
    /**
     * Vosk로 음성 인식 수행
     */
    private String performRecognition(String audioFilePath) throws IOException, JSONException {
        Log.i(TAG, "Vosk 음성 인식 시작");
        
        File audioFile = new File(audioFilePath);
        if (!audioFile.exists()) {
            Log.e(TAG, "오디오 파일이 존재하지 않음: " + audioFilePath);
            return null;
        }
        
        // 원본 오디오 파일을 바이트 배열로 읽기
        byte[] audioData = new byte[(int) audioFile.length()];
        try (FileInputStream fis = new FileInputStream(audioFile)) {
            int bytesRead = fis.read(audioData);
            Log.i(TAG, "원본 오디오 파일 읽기 완료: " + bytesRead + " bytes");
            
            // 원본 오디오 헤더 출력 (처음 20바이트)
            StringBuilder header = new StringBuilder("원본 오디오 헤더 앞부분: ");
            for (int i = 0; i < Math.min(200, audioData.length); i++) {
                header.append(String.format("%02X ", audioData[i] & 0xFF));
            }
            Log.i(TAG, header.toString());
        }
        
        byte[] pcmData = convertToPCM(audioFilePath);
        int chunkSize = 4096;
        int offset = 0;
        int totalChunks = (pcmData.length + chunkSize - 1) / chunkSize;
        int chunkIndex = 1;
        StringBuilder finalText = new StringBuilder();

        while (offset < pcmData.length) {
            int len = Math.min(chunkSize, pcmData.length - offset);
            byte[] chunk = new byte[len];
            System.arraycopy(pcmData, offset, chunk, 0, len);            
            // PCM 청크 내용 출력 (처음 ~~바이트만)
            StringBuilder chunkContent = new StringBuilder("PCM 청크 내용: ");
            for (int i = 0; i < Math.min(50, chunk.length); i++) {
                chunkContent.append(String.format("%02X ", chunk[i] & 0xFF));
            }            

            boolean accepted = recognizer.acceptWaveForm(chunk, len);
            if (accepted) {
                String result = recognizer.getResult();
                if (result.contains("\"text\" : \"\"")) {
                    // Log.i(TAG, "부분 결과 (빈 값): " + result);
                } else {                    
                    // Log.i(TAG, chunkContent.toString());
                    Log.i(TAG, String.format("(%d/%d) PCM 청크 처리: offset=%d, len=%d, 총 크기=%d", 
        chunkIndex, totalChunks, offset, len, pcmData.length));
                    Log.i(TAG, "부분 결과 (값 있음): " + result);
                    
                    // 결과에 텍스트가 있으면 누적
                    try {
                        JSONObject jsonResult = new JSONObject(result);
                        String text = jsonResult.getString("text");
                        if (text != null && !text.trim().isEmpty()) {
                            if (finalText.length() > 0) {
                                finalText.append(" ");
                            }
                            finalText.append(text);
                            Log.i(TAG, "누적된 텍스트: '" + finalText.toString() + "'");
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "결과 파싱 실패", e);
                    }
                }
            } else {
                String partial = recognizer.getPartialResult();
                if (partial.contains("\"partial\" : \"\"")) {
                    // Log.i(TAG, "부분 인식 결과 (빈 값): " + partial);
                } else {
                    // Log.i(TAG, chunkContent.toString());
                    Log.i(TAG, String.format("(%d/%d) PCM 청크 처리: offset=%d, len=%d, 총 크기=%d", 
        chunkIndex, totalChunks, offset, len, pcmData.length));
                    Log.i(TAG, "부분 인식 결과 (값 있음): " + partial);
                }
            }

            offset += len;
            chunkIndex++;
        }

        String convertToPCMFinalResult = recognizer.getFinalResult();
        Log.i(TAG, "convertToPCM 최종 결과: " + convertToPCMFinalResult);
        
        // 누적된 텍스트가 있으면 반환
        if (finalText.length() > 0) {
            Log.i(TAG, "최종 누적 텍스트: '" + finalText.toString() + "'");
            return finalText.toString();
        }
        
        return convertToPCMFinalResult;

        // // Vosk로 음성 인식 (원본 오디오 직접 전달)
        // Log.i(TAG, "Vosk acceptWaveForm 호출 (원본): " + audioData.length + " bytes");
        // boolean accepted = recognizer.acceptWaveForm(audioData, audioData.length);
        // Log.i(TAG, "Vosk acceptWaveForm 결과: " + accepted);

        // if (accepted) {
        //     String result = recognizer.getResult();
        //     Log.i(TAG, "Vosk 결과: " + result);
        //     JSONObject jsonResult = new JSONObject(result);
        //     String text = jsonResult.getString("text");
        //     Log.i(TAG, "추출된 텍스트: " + text);
        //     return text;
        // } else {
        //     // 부분 결과 처리
        //     String partial = recognizer.getPartialResult();
        //     Log.i(TAG, "Vosk 부분 결과: " + partial);
        //     JSONObject jsonPartial = new JSONObject(partial);
        //     String partialText = jsonPartial.getString("partial");
        //     Log.i(TAG, "부분 텍스트: " + partialText);
            
        //     // 최종 결과도 확인
        //     String finalResult = recognizer.getFinalResult();
        //     Log.i(TAG, "Vosk 최종 결과: " + finalResult);
            
        //     // 최종 결과에 텍스트가 있으면 최종 결과 사용, 없으면 부분 결과 사용
        //     if (finalResult != null && !finalResult.isEmpty()) {
        //         try {
        //             JSONObject jsonFinal = new JSONObject(finalResult);
        //             String finalText = jsonFinal.getString("text");
        //             Log.i(TAG, "최종 텍스트: " + finalText);
        //             if (finalText != null && !finalText.trim().isEmpty()) {
        //                 return finalText;
        //             }
        //         } catch (Exception e) {
        //             Log.e(TAG, "최종 결과 파싱 실패", e);
        //         }
        //     }
            
        //     return partialText;
        // }
    }
    
    public byte[] convertToPCM(String audioFilePath) throws IOException {
        MediaExtractor extractor = new MediaExtractor();
        extractor.setDataSource(audioFilePath);
    
        int audioTrackIndex = -1;
        for (int i = 0; i < extractor.getTrackCount(); i++) {
            MediaFormat format = extractor.getTrackFormat(i);
            String mime = format.getString(MediaFormat.KEY_MIME);
            if (mime.startsWith("audio/")) {
                audioTrackIndex = i;
                break;
            }
        }
    
        if (audioTrackIndex < 0) {
            throw new IOException("오디오 트랙을 찾을 수 없습니다.");
        }
        extractor.selectTrack(audioTrackIndex);
    
        MediaFormat format = extractor.getTrackFormat(audioTrackIndex);
        String mime = format.getString(MediaFormat.KEY_MIME);
    
        MediaCodec codec = MediaCodec.createDecoderByType(mime);
        codec.configure(format, null, null, 0);
        codec.start();
    
        ByteBuffer[] inputBuffers = codec.getInputBuffers();
        ByteBuffer[] outputBuffers = codec.getOutputBuffers();
        MediaCodec.BufferInfo info = new MediaCodec.BufferInfo();
    
        ByteArrayOutputStream pcmOutputStream = new ByteArrayOutputStream();
    
        boolean isEOS = false;
        while (!isEOS) {
            int inIndex = codec.dequeueInputBuffer(10000);
            if (inIndex >= 0) {
                ByteBuffer buffer = inputBuffers[inIndex];
                int sampleSize = extractor.readSampleData(buffer, 0);
                if (sampleSize < 0) {
                    codec.queueInputBuffer(inIndex, 0, 0, 0, MediaCodec.BUFFER_FLAG_END_OF_STREAM);
                    isEOS = true;
                } else {
                    codec.queueInputBuffer(inIndex, 0, sampleSize, extractor.getSampleTime(), 0);
                    extractor.advance();
                }
            }
    
            int outIndex = codec.dequeueOutputBuffer(info, 10000);
            while (outIndex >= 0) {
                ByteBuffer outBuffer = outputBuffers[outIndex];
    
                byte[] chunk = new byte[info.size];
                outBuffer.get(chunk);
                outBuffer.clear();
    
                pcmOutputStream.write(chunk);
    
                codec.releaseOutputBuffer(outIndex, false);
                outIndex = codec.dequeueOutputBuffer(info, 0);
            }
        }
    
        codec.stop();
        codec.release();
        extractor.release();
    
        return pcmOutputStream.toByteArray();
    }
    
    /**
     * Android MediaExtractor를 사용하여 16kHz PCM으로 변환
     */
    private byte[] convertToPCMWithMediaExtractor(String audioFilePath) {
        try {
            android.media.MediaExtractor extractor = new android.media.MediaExtractor();
            extractor.setDataSource(audioFilePath);
            
            // 오디오 트랙 찾기
            int audioTrackIndex = -1;
            for (int i = 0; i < extractor.getTrackCount(); i++) {
                android.media.MediaFormat format = extractor.getTrackFormat(i);
                String mimeType = format.getString(android.media.MediaFormat.KEY_MIME);
                if (mimeType.startsWith("audio/")) {
                    audioTrackIndex = i;
                    Log.i(TAG, "오디오 트랙 발견: " + mimeType);
                    break;
                }
            }
            
            if (audioTrackIndex == -1) {
                Log.e(TAG, "오디오 트랙을 찾을 수 없음");
                extractor.release();
                return null;
            }
            
            extractor.selectTrack(audioTrackIndex);
            android.media.MediaFormat format = extractor.getTrackFormat(audioTrackIndex);
            
            // 샘플링 레이트 확인
            int sampleRate = format.getInteger(android.media.MediaFormat.KEY_SAMPLE_RATE);
            int channelCount = format.getInteger(android.media.MediaFormat.KEY_CHANNEL_COUNT);
            Log.i(TAG, "원본 샘플링 레이트: " + sampleRate + "Hz, 채널: " + channelCount);
            
            // MediaCodec으로 디코딩
            String mimeType = format.getString(android.media.MediaFormat.KEY_MIME);
            android.media.MediaCodec decoder = android.media.MediaCodec.createDecoderByType(mimeType);
            
            // 디코더 설정 전에 포맷 정보 출력
            Log.i(TAG, "MediaCodec 설정 - MIME: " + mimeType);
            Log.i(TAG, "MediaCodec 설정 - 샘플레이트: " + format.getInteger(android.media.MediaFormat.KEY_SAMPLE_RATE));
            Log.i(TAG, "MediaCodec 설정 - 채널: " + format.getInteger(android.media.MediaFormat.KEY_CHANNEL_COUNT));
            
            // 디코더 설정 - 더 안전한 방법
            try {
                decoder.configure(format, null, null, 0);
                decoder.start();
                Log.i(TAG, "MediaCodec 디코더 시작 완료");
            } catch (Exception e) {
                Log.e(TAG, "MediaCodec 디코더 설정 실패", e);
                decoder.release();
                extractor.release();
                return null;
            }
            
            // PCM 데이터 수집
            java.io.ByteArrayOutputStream pcmStream = new java.io.ByteArrayOutputStream();
            android.media.MediaCodec.BufferInfo bufferInfo = new android.media.MediaCodec.BufferInfo();
            boolean isEOS = false;
            
            int totalInputSamples = 0;
            int totalOutputSamples = 0;
            
            while (!isEOS) {
                int inputBufferId = decoder.dequeueInputBuffer(10000);
                if (inputBufferId >= 0) {
                    java.nio.ByteBuffer inputBuffer = decoder.getInputBuffer(inputBufferId);
                    if (inputBuffer == null) {
                        Log.e(TAG, "입력 버퍼가 null입니다");
                        continue;
                    }
                    
                    int sampleSize = extractor.readSampleData(inputBuffer, 0);
                    
                    if (sampleSize < 0) {
                        decoder.queueInputBuffer(inputBufferId, 0, 0, 0, android.media.MediaCodec.BUFFER_FLAG_END_OF_STREAM);
                        isEOS = true;
                    } else {
                        totalInputSamples += sampleSize;
                        
                        // PCM 변환 전 오디오 값 출력 (처음 20바이트만)
                        if (totalInputSamples <= 300) { // 처음 몇 번만 출력
                            StringBuilder inputValues = new StringBuilder("입력 오디오 값: ");
                            for (int i = 0; i < Math.min(sampleSize, 20); i++) {
                                inputValues.append(String.format("%02X ", inputBuffer.get(i) & 0xFF));
                            }
                            Log.i(TAG, inputValues.toString());
                        }
                        
                        long presentationTimeUs = extractor.getSampleTime();
                        decoder.queueInputBuffer(inputBufferId, 0, sampleSize, presentationTimeUs, 0);
                        extractor.advance();
                    }
                }
                
                int outputBufferId = decoder.dequeueOutputBuffer(bufferInfo, 10000);
                if (outputBufferId >= 0) {
                    java.nio.ByteBuffer outputBuffer = decoder.getOutputBuffer(outputBufferId);
                    
                    if (outputBuffer == null) {
                        Log.e(TAG, "출력 버퍼가 null입니다");
                        decoder.releaseOutputBuffer(outputBufferId, false);
                        continue;
                    }

                    if (bufferInfo.size > 0) {
                        byte[] buffer = new byte[bufferInfo.size];
                        outputBuffer.position(0); // 추가
                        outputBuffer.get(buffer);
                        pcmStream.write(buffer);
                        totalOutputSamples += bufferInfo.size;
                    }

                    decoder.releaseOutputBuffer(outputBufferId, false);
                } else if (outputBufferId == android.media.MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                    // 출력 포맷 변경됨 (로그 제거)
                } else if (outputBufferId == android.media.MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED) {
                    // 출력 버퍼 변경됨 (로그 제거)
                } else if (outputBufferId == android.media.MediaCodec.INFO_TRY_AGAIN_LATER) {
                    // 출력 버퍼 대기 중 (로그 제거)
                }
            }
            
            // 총 입력/출력 샘플 (로그 제거)
            
            byte[] pcmData = pcmStream.toByteArray();
            Log.i(TAG, "PCM 변환 완료: " + pcmData.length + " bytes");
            
            // 최종 PCM 데이터 값 출력 (처음 50바이트)
            StringBuilder finalPcmValues = new StringBuilder("최종 PCM 데이터 값: ");
            for (int i = 0; i < Math.min(pcmData.length, 50); i++) {
                finalPcmValues.append(String.format("%02X ", pcmData[i] & 0xFF));
            }
            Log.i(TAG, finalPcmValues.toString());
            
            // PCM 데이터 통계
            int zeroCount = 0;
            int nonZeroCount = 0;
            for (int i = 0; i < Math.min(pcmData.length, 1000); i++) { // 처음 1000바이트만 확인
                if (pcmData[i] == 0) {
                    zeroCount++;
                } else {
                    nonZeroCount++;
                }
            }
            Log.i(TAG, "PCM 데이터 통계 (처음 1000바이트): 0값=" + zeroCount + ", 0이 아닌 값=" + nonZeroCount);
            
            // 16kHz로 리샘플링 (간단한 다운샘플링)
            if (sampleRate != 16000) {
                Log.i(TAG, "리샘플링 시작: " + sampleRate + "Hz -> 16kHz");
                
                // 리샘플링 전 PCM 값 출력 (처음 20바이트만)
                StringBuilder beforeResample = new StringBuilder("리샘플링 전 PCM 값: ");
                for (int i = 0; i < Math.min(pcmData.length, 20); i++) {
                    beforeResample.append(String.format("%02X ", pcmData[i] & 0xFF));
                }
                Log.i(TAG, beforeResample.toString());
                
                pcmData = resampleTo16kHz(pcmData, sampleRate, channelCount);
                Log.i(TAG, "16kHz 리샘플링 완료: " + pcmData.length + " bytes");
                
                // 리샘플링 후 PCM 값 출력 (처음 50바이트)
                StringBuilder afterResample = new StringBuilder("리샘플링 후 PCM 값: ");
                for (int i = 0; i < Math.min(pcmData.length, 50); i++) {
                    afterResample.append(String.format("%02X ", pcmData[i] & 0xFF));
                }
                Log.i(TAG, afterResample.toString());
                
                // 리샘플링 후 PCM 데이터 통계
                int resampledZeroCount = 0;
                int resampledNonZeroCount = 0;
                for (int i = 0; i < Math.min(pcmData.length, 1000); i++) {
                    if (pcmData[i] == 0) {
                        resampledZeroCount++;
                    } else {
                        resampledNonZeroCount++;
                    }
                }
                Log.i(TAG, "리샘플링 후 PCM 데이터 통계 (처음 1000바이트): 0값=" + resampledZeroCount + ", 0이 아닌 값=" + resampledNonZeroCount);
            }
            
            decoder.stop();
            decoder.release();
            extractor.release();
            
            return pcmData;
            
        } catch (Exception e) {
            Log.e(TAG, "MediaExtractor 변환 실패", e);
            return null;
        }
    }
    
    /**
     * 16kHz로 리샘플링 (간단한 다운샘플링)
     */
    private byte[] resampleTo16kHz(byte[] pcmData, int originalSampleRate, int channelCount) {
        if (originalSampleRate == 16000) {
            return pcmData;
        }
        
        // 16bit PCM 가정
        int bytesPerSample = 2 * channelCount;
        int originalSamples = pcmData.length / bytesPerSample;
        int targetSamples = (originalSamples * 16000) / originalSampleRate;
        
        byte[] resampledData = new byte[targetSamples * bytesPerSample];
        
        for (int i = 0; i < targetSamples; i++) {
            int originalIndex = (i * originalSampleRate) / 16000;
            if (originalIndex < originalSamples) {
                for (int j = 0; j < bytesPerSample; j++) {
                    resampledData[i * bytesPerSample + j] = pcmData[originalIndex * bytesPerSample + j];
                }
            }
        }
        
        return resampledData;
    }
    
    /**
     * 리소스 해제
     */
    public void release() {
        if (recognizer != null) {
            recognizer.close();
            recognizer = null;
        }
        if (model != null) {
            model.close();
            model = null;
        }
        isInitialized = false;
        Log.i(TAG, "Vosk 리소스 해제 완료");
    }
} 