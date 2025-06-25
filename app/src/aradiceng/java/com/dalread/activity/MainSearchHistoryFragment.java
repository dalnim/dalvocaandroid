package com.dalread.activity;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dalread.R;
import com.dalread.asyntask.CustomAsyncTask;
import com.dalread.asyntask.OnAsyncTaskListener;
import com.dalread.base.BaseIctTermFragment;
import com.dalread.databinding.ActivityGoogleCloudSttBinding;
import com.dalread.network.events.SuccessEvent;
import com.dalread.util.stt.GoogleCloudSpeechService;
import com.dalread.util.stt.GoogleCloudVoiceRecorder;
import com.dalread.util.Loading;
import com.dalread.util.stt.MessageDialogFragment;
import com.dalread.util.Utils;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;

public class MainSearchHistoryFragment extends BaseIctTermFragment implements OnAsyncTaskListener, MessageDialogFragment.Listener  {
    private static final String FRAGMENT_MESSAGE_DIALOG = "message_dialog";
    private static final String STATE_RESULTS = "results";
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 1;
    private GoogleCloudSpeechService mSpeechService;
    private GoogleCloudVoiceRecorder mVoiceRecorder;

    private boolean isServiceBinded;
    // Resource caches
    private int mColorHearing;
    private int mColorNotHearing;

    private ResultAdapter mAdapter;

    private MainHomeActivity activity;

    private final int TYPE_INIT_DATA = 0;
    private final int TYPE_CLEAR_SEARCH_HISTORY = 1;

    private ActivityGoogleCloudSttBinding binding;

    @Override
    protected View getContentView() {
        binding = ActivityGoogleCloudSttBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        return view;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        setHasOptionsMenu(true);
        activity = (MainHomeActivity)getActivity();
        activity.setSupportActionBar((Toolbar) activity.findViewById(R.id.toolbar));
        initAdapter();
        initLayout();
        initData();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private final GoogleCloudVoiceRecorder.Callback mVoiceCallback = new GoogleCloudVoiceRecorder.Callback() {

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

    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder binder) {
            mSpeechService = GoogleCloudSpeechService.from(binder);
            mSpeechService.addListener(mSpeechServiceListener);
            binding.itemResultGoogleCloudStt.status.setVisibility(View.VISIBLE);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mSpeechService = null;
        }

    };

    private void showStatus(final boolean hearingVoice) {
       activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                binding.itemResultGoogleCloudStt.status.setTextColor(hearingVoice ? mColorHearing : mColorNotHearing);
            }
        });
    }

    private void initAdapter() {
        final ArrayList<String> results = new ArrayList<>();
        mAdapter = new ResultAdapter(results);

    }

    private void initData() {
        final Resources resources = getResources();
        final Resources.Theme theme = activity.getTheme();
        mColorHearing = ResourcesCompat.getColor(resources, R.color.status_hearing, theme);
        mColorNotHearing = ResourcesCompat.getColor(resources, R.color.status_not_hearing, theme);
    }

    private void callAsyncTask(int type, Object data) {
        new CustomAsyncTask(activity, this, data, type, true).execute();
    }

    protected void initLayout() {
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(activity));
        binding.recyclerView.setAdapter(mAdapter);
    }


    @Override
    public void onInitAsyncTask() {

    }

    @Override
    public void onPrevExecuteAsyncTask() {

    }

    @Override
    public Object onPostExecuteAsyncTask(int searchType, Object data) {

        return null;
    }

    @Override
    public void onFinishAsyncTask(int searchType, Object resultData, Object data) {

        Loading.hide();
    }
    @Subscribe
    public void onEvent(SuccessEvent successEvent) {

    }
    @Override
    public void onStart() {
        super.onStart();

        // Prepare Cloud Speech API
        isServiceBinded = activity.getApplicationContext().bindService(new Intent(activity, GoogleCloudSpeechService.class), mServiceConnection, activity.BIND_AUTO_CREATE);

        // Start listening to voices
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.RECORD_AUDIO)
                == PackageManager.PERMISSION_GRANTED) {
            startVoiceRecorder();
        } else if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                Manifest.permission.RECORD_AUDIO)) {
            showPermissionMessageDialog();
        } else {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.RECORD_AUDIO},
                    REQUEST_RECORD_AUDIO_PERMISSION);
        }
    }
    @Override
    public void onStop() {
        // Stop listening to voice
        stopVoiceRecorder();

        // Stop Cloud Speech API
        mSpeechService.removeListener(mSpeechServiceListener);
        if (isServiceBinded) {
            activity.unbindService(mServiceConnection);
            isServiceBinded = false;
        }
        mSpeechService = null;

        super.onStop();
    }
    private void startVoiceRecorder() {
        if (mVoiceRecorder != null) {
            mVoiceRecorder.stop();
        }
        mVoiceRecorder = new GoogleCloudVoiceRecorder(mVoiceCallback);
        mVoiceRecorder.start();
    }

    private void stopVoiceRecorder() {
        if (mVoiceRecorder != null) {
            mVoiceRecorder.stop();
            mVoiceRecorder = null;
        }
    }
    private void showPermissionMessageDialog() {
        MessageDialogFragment
                .newInstance(getString(R.string.permission_message))
                .show(activity.getSupportFragmentManager(), FRAGMENT_MESSAGE_DIALOG);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION) {
            if (permissions.length == 1 && grantResults.length == 1
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startVoiceRecorder();
            } else {
                showPermissionMessageDialog();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main, menu);
        super.onCreateOptionsMenu(menu,inflater);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_file:
                mSpeechService.recognizeInputStream(getResources().openRawResource(R.raw.audio));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    @Override
    public void onMessageDialogDismissed() {
        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.RECORD_AUDIO},
                REQUEST_RECORD_AUDIO_PERMISSION);
    }

    private final GoogleCloudSpeechService.Listener mSpeechServiceListener =
            new GoogleCloudSpeechService.Listener() {
                @Override
                public void onSpeechRecognized(final String text, final boolean isFinal) {
                    if (isFinal) {
                        mVoiceRecorder.dismiss();
                    }
                    if (binding.itemResultGoogleCloudStt.text != null && !Utils.isEmpty(text)) {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (isFinal) {
                                    binding.itemResultGoogleCloudStt.text.setText(null);
                                    mAdapter.addResult(text);
                                    binding.recyclerView.smoothScrollToPosition(0);
                                } else {
                                    binding.itemResultGoogleCloudStt.text.setText(text);
                                }
                            }
                        });
                    }
                }
            };

    private static class ViewHolder extends RecyclerView.ViewHolder {

        TextView text;

        ViewHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.item_result_google_cloud_stt, parent, false));
            text = (TextView) itemView.findViewById(R.id.text);
        }

    }

    private static class ResultAdapter extends RecyclerView.Adapter<ViewHolder> {

        private final ArrayList<String> mResults = new ArrayList<>();

        ResultAdapter(ArrayList<String> results) {
            if (results != null) {
                mResults.addAll(results);
            }
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()), parent);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.text.setText(mResults.get(position));
        }

        @Override
        public int getItemCount() {
            return mResults.size();
        }

        void addResult(String result) {
            mResults.add(0, result);
            notifyItemInserted(0);
        }

        public ArrayList<String> getResults() {
            return mResults;
        }

    }
}
