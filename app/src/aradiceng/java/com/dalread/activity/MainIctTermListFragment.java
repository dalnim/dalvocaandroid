package com.dalread.activity;

import android.content.Context;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dalread.R;
import com.dalread.adapter.IctTermListAdapter;
import com.dalread.base.BaseIctTermFragment;
import com.dalread.component.SeparatorDecoration;
import com.dalread.database.sqlite.model.DIC_ICT_TERM;
import com.dalread.databinding.ActivityIctTermListBinding;
import com.dalread.dialog.SingleChoiceDialog;
import com.dalread.interfaces.IVocaBasicItem;
import com.dalread.listener.OnClickDialogListener;
import com.dalread.listener.OnClickListener;
import com.dalread.network.events.SuccessEvent;
import com.dalread.util.Utils;
import com.dalread.util.stt.SttEngine;
import com.dalread.util.stt.SttEngineFactory;
import com.dalread.util.stt.SttModel;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

public class MainIctTermListFragment extends BaseIctTermFragment { //implements RecognitionListener {
    private SttEngine sttEngine;
//    private SttEngineFactory.STT_ENGINE_TYPE sttEngineType;
    private List<SttModel> modelList;
    private IctTermListAdapter adapter;
    private MainHomeActivity activity;

    private ActivityIctTermListBinding binding;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    protected View getContentView() {
        binding = ActivityIctTermListBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        return view;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        activity = (MainHomeActivity)getActivity();
        initSttEngine();
        initAdapter();
        initLayout();
        initData();
        return view;
    }

    private void initSttEngine() {
        setSttEngine(SttEngineFactory.STT_ENGINE_TYPE.VOSK);
    }

    private void setSttEngine(SttEngineFactory.STT_ENGINE_TYPE sttEngineType) {
        sttEngine = SttEngineFactory.getSttEngine(sttEngineType, activity, onSttEngineListener, onSttEngineStatusListener);
    }

    private SttEngine.OnSttEngineListener onSttEngineListener = new SttEngine.OnSttEngineListener() {
        @Override
        public void onGuideText(String text) {
            binding.partialText.setText(text);
        }

        @Override
        public void onSttPartialResult(SttModel model) {
            if (!Utils.isEmpty(model.SMgetSentence())) {
                binding.tvDifficultWordAndMeaning.setText(model.SMgetDifficultWordAndMeaning());
                binding.partialText.setText(model.SMgetSentence() + "\n");
            }
        }

        @Override
        public void onSttResult(SttModel model) {
            if (!Utils.isEmpty(model.SMgetSentence())) {
                modelList.add(model);
                addItemAndbindData(model);
                clearPartialTextView();
            }
        }

        @Override
        public void onNoClick(Object object) {

        }

        @Override
        public void setErrorState(String message) {
            MainIctTermListFragment.this.setErrorState(message);
        }
    };

    private SttEngine.OnSttEngineStatusListener onSttEngineStatusListener = new SttEngine.OnSttEngineStatusListener() {
        @Override
        public void onPrepare() {
            binding.partialText.setText(R.string.preparing);
            binding.partialText.setMovementMethod(new ScrollingMovementMethod());
            binding.layoutStt.ivStt.setVisibility(View.INVISIBLE);
        }

        @Override
        public void onReady() {
            binding.partialText.setText(activity.getString(R.string.ready) + " : " + sttEngine.getSttEngineType().getName());
            binding.layoutStt.btnPause.setVisibility(View.INVISIBLE);
            binding.layoutStt.ivStt.setVisibility(View.VISIBLE);
        }

        @Override
        public void onPause(boolean checked) {
            binding.layoutStt.ivStt.setVisibility(checked ? View.INVISIBLE : View.VISIBLE);
        }

        @Override
        public void onStart() {
            initList();
            onUpdateMic(true);
            binding.partialText.setText(getString(R.string.say_something));
            binding.layoutStt.btnPause.setVisibility(View.VISIBLE);
            bindData(modelList);
        }

        @Override
        public void onEnd() {
            onUpdateMic(false);
            binding.partialText.setText(R.string.ready);
            binding.layoutStt.btnPause.setVisibility(View.INVISIBLE);
        }
    };

    private void onUpdateMic(Boolean isStart) {
        if (isStart) {
            binding.layoutStt.tvHeader.setText(R.string.click_mic_again_to_finish_stt);
            binding.layoutStt.ivStt.setImageResource(R.drawable.ic_mic_black_24dp);
        } else {
            binding.layoutStt.tvHeader.setText(R.string.click_mic_to_start_stt);
            binding.layoutStt.ivStt.setImageResource(R.drawable.ic_mic_none_black_24dp);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        sttEngine.releaseSttEngine();
        binding = null;
    }

    private void initAdapter() {
        adapter = new IctTermListAdapter(activity, onClickListener);
    }
    private OnClickListener onClickListener = new OnClickListener() {
        @Override
        public void onClick(View view, Object object) {
            if (object instanceof IVocaBasicItem) {
                IVocaBasicItem item = (IVocaBasicItem) object;
                switch (view.getId()) {
                    case R.id.ivBookmark:
                        activity.subDatabase.swapBookmark(item);
                        item.swapVIBookmark();
                        break;
                    default:
                        openNewScreen(
                                IctTermInfoActivity.createIntent(activity, (DIC_ICT_TERM) object)
                        );
                        break;
                }
            }
        }
    };

    private void addItemAndbindData(SttModel itemToBind) {
        adapter.addItemInList(itemToBind);
        adapter.notifyItemInserted(modelList.size() - 1);
        binding.rvInfo.smoothScrollToPosition(modelList.size() - 1);
    }
    private void bindData(List<SttModel> itemListToBind) {
        adapter.setItemList(itemListToBind);
        adapter.notifyDataSetChanged();
    }

    private void initData() {
        initList();
        binding.layoutStt.ivStt.setOnClickListener(view -> sttEngine.recognizeMicrophone());
        binding.layoutStt.btnPause.setOnCheckedChangeListener((view, isChecked) -> sttEngine.pause(isChecked));
    }
    private void initList() {
        modelList = new ArrayList<>();
    }

    protected void initLayout() {
        binding.layoutStt.btnPause.setVisibility(View.INVISIBLE);
        layoutManager = new LinearLayoutManager(activity);
        binding.rvInfo.setLayoutManager(layoutManager);
        binding.rvInfo.addItemDecoration(new SeparatorDecoration(activity, activity.clDivider, activity.dividerHeight));
        binding.rvInfo.setAdapter(adapter);
    }
    @Subscribe
    public void onEvent(SuccessEvent successEvent) {

    }
    public void refreshIctTermList() {
        activity.toolbar.showSearchView();
        activity.toolbar.showTitle();
    }

    private void clearPartialTextView() {
        binding.partialText.setText("");
        binding.tvDifficultWordAndMeaning.setText("");
    }

    private void setErrorState(String message) {
        binding.partialText.setText(message);
    }

    @Override
    public void onStart() {
        super.onStart();
    }
    @Override
    public void onResume() {
        super.onResume();
    }
    @Override
    public void onPause() {
        super.onPause();
    }
    @Override
    public void onStop() {
        sttEngine.releaseSttEngine();
        super.onStop();
    }

    public void chooseSttEngine(Context context) {
        String[] displayOptions = context.getResources().getStringArray(R.array.choose_stt_engine);
        SingleChoiceDialog singleChoiceDialog = new SingleChoiceDialog(context);
        singleChoiceDialog.showWrapContentHeight(
                R.string.dialog_title_choose_stt_engine,
                displayOptions,
                sttEngine.getSttEngineType().getIndex(),
                R.string.ok,
                R.string.cancel,
                new OnClickDialogListener() {
                    @Override
                    public void onClick(View view, Object object) {
                        final int which = (int) object;
                        SttEngineFactory.STT_ENGINE_TYPE sttEngineType = SttEngineFactory.STT_ENGINE_TYPE.VOSK;
                        switch (which) {
                            case 0:
                                sttEngineType = SttEngineFactory.STT_ENGINE_TYPE.VOSK;
                                break;
                            case 1:
                                sttEngineType = SttEngineFactory.STT_ENGINE_TYPE.GOOGLE_CLOUD;
                                break;
                        }
                        setSttEngine(sttEngineType);
                    }
                    @Override
                    public void onDismiss(View view, Object object) {

                    }
                });
    }
}
