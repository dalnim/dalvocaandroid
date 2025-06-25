package com.dalread.activity;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.view.View;
import android.view.WindowManager;

import com.dalread.R;
import com.dalread.databinding.ActivityConversationRawDataBinding;
import com.dalread.databinding.ActivityTtsSaveBinding;
import com.dalread.helper.ExecutorHelper;
import com.dalread.model.UserVocabookLocal;
import com.dalread.model.UserVocabooksLocal;
import com.dalread.model.WordListType;
import com.dalread.util.BasePermissionUtils;
import com.dalread.util.BaseStorageUtil;
import com.dalread.util.Constant;
import com.dalread.util.CopyTextUtil;
import com.dalread.util.DateUtils;
import com.dalread.util.EditTextUtils;
import com.dalread.util.Loading;
import com.dalread.util.ToastUtil;
import com.dalread.util.UserUtil;
import com.dalread.util.Utils;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
//스크립트를 TTS로 읽고 m4a파일로 저장하는 임시 코드
public class TTSSaveActivity extends BaseConvActivity implements TextToSpeech.OnInitListener {
    private ActivityTtsSaveBinding binding;
    private TextToSpeech textToSpeech;
    private MediaRecorder mediaRecorder;
    private String outputFile;
    private boolean isRecording = false;

    public static Intent createIntent(Context context) {
        Intent intent = new Intent(context, TTSSaveActivity.class);
        return intent;
    }
    protected View getContentView() {
        binding = ActivityTtsSaveBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        return view;
    }
    @Override
    protected int getContentViewId() {
        return 0;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        textToSpeech = new TextToSpeech(this, this);
        binding.header.setTitle("TTS 저장");
    }
    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    protected void initLayout() {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        binding.btnPaste.setOnClickListener(v -> {
            CopyTextUtil.getTextFromClipboardWithDelay(this, (text) -> {
                binding.etConversationRawData.setText(text);
            });
        });
        binding.btnClear.setOnClickListener(v -> binding.etConversationRawData.setText(""));
        binding.btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((BasePermissionUtils.checkRecordAudio(TTSSaveActivity.this, true)) &&
                    (BasePermissionUtils.checkWriteExternalStorage(TTSSaveActivity.this, true))) {
                        String srtContents = binding.etConversationRawData.getText().toString().trim();
                        if (srtContents.isEmpty()) {
                            return;
                        }
                        String[] lines = srtContents.split("\n\n");
                        for (String line : lines) {
                            String[] parts = line.split("\n");
                            String timespan = parts[1];
                            String text = parts[2];

                            String[] timespanParts = timespan.split("-->");
                            if (timespanParts.length == 1) {
                                timespanParts = timespan.split("—>");
                            }
                            if (timespanParts.length != 2) {
                                continue;
                            }
                            long startMillis = parseTime(timespanParts[0].trim());
                            long endMillis = parseTime(timespanParts[1].trim());

                            Bundle params = new Bundle();
                            params.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "utteranceId");
                            textToSpeech.playSilentUtterance(endMillis - startMillis, TextToSpeech.QUEUE_ADD, "silence");
                            textToSpeech.speak(text, TextToSpeech.QUEUE_ADD, params, "utteranceId");
                        }
                }
            }
        });
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            textToSpeech.setLanguage(Locale.US);
            textToSpeech.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                @Override
                public void onStart(String utteranceId) {
                    try {
                        startRecording();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onDone(String utteranceId) {
                    try {
                        stopRecording();
                        runOnUiThread(() -> binding.btnOk.setText("Play"));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onError(String utteranceId) {

                }
            });
        }
    }

    private void startRecording() throws IOException {
        if (!isRecording) {
            // Configure media recorder
            outputFile = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/output.m4a";
            mediaRecorder = new MediaRecorder();
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.VOICE_RECOGNITION);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            mediaRecorder.setOutputFile(outputFile);

            // Start recording
            mediaRecorder.prepare();
            mediaRecorder.start();
            isRecording = true;
        }
    }

    private void stopRecording() throws IOException {
        if (isRecording) {
            // Stop recording
            mediaRecorder.stop();
            mediaRecorder.release();
            mediaRecorder = null;

//            // Play the recorded audio
//            MediaPlayer mediaPlayer = new MediaPlayer();
//            try {
//                mediaPlayer.setDataSource(outputFile);
//                mediaPlayer.prepare();
//                mediaPlayer.start();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }

            // Reset isRecording flag
            isRecording = false;
        }
    }

    private long parseTime(String time) {
        String[] parts = time.split(":");
        long hours = Long.parseLong(parts[0]);
        long minutes = Long.parseLong(parts[1]);
        String[] secondsParts = parts[2].split(",");
        long seconds = Long.parseLong(secondsParts[0]);
        long milliseconds = Long.parseLong(secondsParts[1]);
        return ((hours * 60 + minutes) * 60 + seconds) * 1000 + milliseconds;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        textToSpeech.shutdown();
    }
}
