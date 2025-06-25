package com.dalread.util.stt;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.dalread.util.Utils;

public class SttEngineGoogleCloud extends SttEngine {
    private GoogleCloudSpeechService mSpeechService;
    private GoogleCloudVoiceRecorder mVoiceRecorder;

    private GoogleCloudVoiceRecorder.Callback mVoiceCallback;
    private ServiceConnection mServiceConnection;
    private GoogleCloudSpeechService.Listener mSpeechServiceListener;
    private boolean isServiceBinded;
    private boolean isStartedVoiceRecord;
    public SttEngineGoogleCloud(Activity activity, OnSttEngineListener onSttEngineListener, OnSttEngineStatusListener onSttEngineStatusListener) {
        super(activity, onSttEngineListener, onSttEngineStatusListener);
    }

    @Override
    protected void initData() {
        sttEngineType = SttEngineFactory.STT_ENGINE_TYPE.GOOGLE_CLOUD;
        initListener();
//        // Prepare Cloud Speech API //이거는 mServiceConnection이 null이 됨.
//        activity.bindService(new Intent(activity, GoogleCloudSpeechService.class), mServiceConnection, activity.BIND_AUTO_CREATE);
////        activity.bindService(new Intent(activity, SttEngineGoogleCloud.class), new ServiceConnection() {
////
////            @Override
////            public void onServiceConnected(ComponentName componentName, IBinder binder) {
////                mSpeechService = GoogleCloudSpeechService.from(binder);
////                mSpeechService.addListener(mSpeechServiceListener);
////                onSttEngineStatusListener.onReady();
//////            binding.itemResultGoogleCloudStt.status.setVisibility(View.VISIBLE);
////            }
////
////            @Override
////            public void onServiceDisconnected(ComponentName componentName) {
////                mSpeechService = null;
////            }
////
////        }, activity.BIND_AUTO_CREATE);

        super.initData();
    }

    private void initListener() {
        mVoiceCallback = new GoogleCloudVoiceRecorder.Callback() {
            @Override
            public void onVoiceStart() {
                showStatus(true);
                if (mSpeechService != null) {
                    mSpeechService.startRecognizing(mVoiceRecorder.getSampleRate());
                }
            }

            @Override
            public void onVoice(byte[] data, int size) {
                if (mSpeechService != null) {
                    mSpeechService.recognize(data, size);
                }
            }

            @Override
            public void onVoiceEnd() {
                showStatus(false);
                if (mSpeechService != null) {
                    mSpeechService.finishRecognizing();
                }
            }

        };

        mServiceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder binder) {
                mSpeechService = GoogleCloudSpeechService.from(binder);
                mSpeechService.addListener(mSpeechServiceListener);
                setUiState(STATE_READY);
//            binding.itemResultGoogleCloudStt.status.setVisibility(View.VISIBLE);
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {
                mSpeechService = null;
            }

        };

        mSpeechServiceListener = new GoogleCloudSpeechService.Listener() {
            @Override
            public void onSpeechRecognized(final String text, final boolean isFinal) {
                if (isFinal) {
                    mVoiceRecorder.dismiss();
                }
                if (!Utils.isEmpty(text)) {
                    SttModel model = getDifficultWordAndMeaning(makeModel(text));

                    if (!Utils.isEmpty(model.SMgetSentence())) {
                        if (isFinal) {
                            onSttEngineListener.onSttResult(model);
                        } else {
                            onSttEngineListener.onSttPartialResult(model);
                        }
                    }
                }
            }
        };
    }

    @Override
    protected void initStt() {
        // Prepare Cloud Speech API //이거는 mServiceConnection이 null이 됨.
        isServiceBinded = activity.bindService(new Intent(activity, GoogleCloudSpeechService.class), mServiceConnection, activity.BIND_AUTO_CREATE);
    }

    private void startVoiceRecorder() {
        if (mVoiceRecorder == null) {
            mVoiceRecorder = new GoogleCloudVoiceRecorder(mVoiceCallback);
        }
        mVoiceRecorder.start();
        setUiState(STATE_START);
    }

    private void stopVoiceRecorder() {
        if (mVoiceRecorder != null) {
            mVoiceRecorder.stop();
        }
        setUiState(STATE_END);
    }

    @Override
    public boolean releaseSttEngine() {
        stopVoiceRecorder();
        mVoiceRecorder = null;
        return true;
    }

    @Override
    public void recognizeMicrophone() {
        try {

            startOrStopVoiceRecorder();
        } catch (Exception e) {
            onSttEngineListener.setErrorState(e.getMessage());
        }
    }

    private void startOrStopVoiceRecorder() {
        if (isStartedVoiceRecord) {
            stopVoiceRecorder();
        } else {
            startVoiceRecorder();
        }
        isStartedVoiceRecord = !isStartedVoiceRecord;
    }
    @Override
    public void pause(boolean checked) {
        startOrStopVoiceRecorder();
        onSttEngineStatusListener.onPause(checked);
    }

    @Override
    protected SttModel makeModel(String str) {
        return SttModelGoogleCloud.withSttResult(str);
    }

    private final GoogleCloudVoiceRecorder.Callback mVoiceCallback1 = new GoogleCloudVoiceRecorder.Callback() {

        @Override
        public void onVoiceStart() {
            showStatus(true);
            if (mSpeechService != null) {
                mSpeechService.startRecognizing(mVoiceRecorder.getSampleRate());
            }
        }

        @Override
        public void onVoice(byte[] data, int size) {
            if (mSpeechService != null) {
                mSpeechService.recognize(data, size);
            }
        }

        @Override
        public void onVoiceEnd() {
            showStatus(false);
            if (mSpeechService != null) {
                mSpeechService.finishRecognizing();
            }
        }

    };

    private final ServiceConnection mServiceConnection1 = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder binder) {
            mSpeechService = GoogleCloudSpeechService.from(binder);
            mSpeechService.addListener(mSpeechServiceListener);
            onSttEngineStatusListener.onReady();
//            binding.itemResultGoogleCloudStt.status.setVisibility(View.VISIBLE);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mSpeechService = null;
        }

    };

    private final GoogleCloudSpeechService.Listener mSpeechServiceListener1 =
            new GoogleCloudSpeechService.Listener() {
                @Override
                public void onSpeechRecognized(final String text, final boolean isFinal) {
                    if (isFinal) {
                        mVoiceRecorder.dismiss();
                    }
                    if (!Utils.isEmpty(text)) {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
//                                if (isFinal) {
////                                    onSttEngineListener.onSttResult();
//                                    binding.itemResultGoogleCloudStt.text.setText(null);
//                                    mAdapter.addResult(text);
//                                    binding.recyclerView.smoothScrollToPosition(0);
//                                } else {
//                                    binding.itemResultGoogleCloudStt.text.setText(text);
//                                }
                            }
                        });
                    }
                }
            };

    private void showStatus(final boolean hearingVoice) {
//        activity.runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                binding.itemResultGoogleCloudStt.status.setTextColor(hearingVoice ? mColorHearing : mColorNotHearing);
//            }
//        });
    }

}
