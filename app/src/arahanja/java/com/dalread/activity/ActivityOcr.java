package com.dalread.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.View;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.dalread.R;
import com.dalread.component.Toolbar;
import com.dalread.databinding.ActivityOcrBinding;
import com.dalread.interfaces.IVocaFullPlayTTSItem;
import com.dalread.network.events.SuccessEvent;
import com.dalread.util.BaseUtilImage;
import com.dalread.util.Constant;
import com.dalread.util.CopyTextUtil;
import com.dalread.util.Loading;
import com.dalread.util.Voca;
import com.dalread.viewmodel.GoogleMLViewModel;

import org.greenrobot.eventbus.Subscribe;

import java.util.List;

public class ActivityOcr extends BaseHanjaInfoActivity {
    private GoogleMLViewModel viewModel;
    private ActivityOcrBinding binding;
    private Bitmap bitmap;
    private String ocrResult = "";

    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();
    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }

    public static Intent createIntent(Context context, String filePath) {
        Intent intent = new Intent(context, ActivityOcr.class);
        intent.putExtra(Constant.BUNDLE.KEY_DATA, filePath);
        return intent;
    }

    protected View getContentView() {
        binding = ActivityOcrBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        return view;
    }

    @Override
    protected Toolbar getToolbar() {
        return binding.header;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(GoogleMLViewModel.class);
        viewModel.getProcessing().observe(this, processing -> {
            binding.ivConvertToHanja.setEnabled(!processing);
        });
        viewModel.getProgress().observe(this, progress -> {
            binding.OCRTextView.setText(progress);
        });
        viewModel.getResult().observe(this, result -> {
            updateOcrResult(result);

            Loading.hide();
        });
        binding.ivWordList.setOnClickListener( v -> openWordList(v));
        binding.ivCopy.setOnClickListener( v -> onCopy());
        binding.btnRotateForward.setOnClickListener( v -> onClickRotate(v, true));
        binding.btnRotateBackward.setOnClickListener( v -> onClickRotate(v, false));
        binding.ivConvertToHanja.setOnClickListener( v -> convertImageToText());
        loadBmp();
    }

    private void updateOcrResult(String result) {
        ocrResult = result;
        binding.OCRTextView.setText(result);
        binding.OCRTextView.setVisibility(View.GONE);

        List<String> listRubyTextAndUnknownWord = Voca.getRubyTextAndUnknownWord(result, "");
        String rubyText = listRubyTextAndUnknownWord.get(0);
        binding.tvContent.setFuriganViewForBookVoca(rubyText, Constant.SHOW_FURIGANA_DIFFICULT_WORDS_ONLY);
        binding.tvContent.setOnTextSelectedListener(onRubyTextSelectedListener);

        String unknownWordText = listRubyTextAndUnknownWord.get(1);
        List<String> listRubyTextDifficultWords = Voca.getRubyTextAndUnknownWord(unknownWordText,"");
        String rubyTextUnknown = listRubyTextDifficultWords.get(0);
        binding.tvContentDifficultWord.setFuriganViewForDifficultWords(rubyTextUnknown);
        binding.tvContentDifficultWord.setOnTextSelectedListener(onRubyTextSelectedListener);
    }


    private void loadBmp() {
        String filePath = getIntent().getStringExtra(Constant.BUNDLE.KEY_DATA);

        if (filePath != null) {
            bitmap = BitmapFactory.decodeFile(filePath);

            if (bitmap != null) {
                binding.ivZoomedPhoto.setImageBitmap(bitmap);
            }
        }
    }

    public void onClickOpenWordList(View view) {
        vocaKnowActivity.onDoubleClickListenerOnBaseVocaKnow.onClick(view, binding.OCRTextView.getText().toString());
    }

    private void onCopy() {
        CopyTextUtil.copyToClipboardShowWhatCopied(context, ocrResult);
    }
    private void openWordList(View view) {
        onDoubleClickListenerForCommon.onClick(view, ocrResult);
    }

    public void onClickRotate(View view, boolean isForward) {
        Bitmap bitmap = BaseUtilImage.getBitmap(binding.ivZoomedPhoto);
        binding.ivZoomedPhoto.setImageBitmap(BaseUtilImage.getRotateBitmap(bitmap, isForward ? 90 : -90));
    }

    private double getConfidenceThreshold() {
        double confidenceThreshold = 0.7;

        String confidenceText = String.valueOf(confidenceThreshold);
        try {
//            confidenceText = etConfidence.getText().toString();
            confidenceThreshold = Double.parseDouble(confidenceText);
        } catch (NumberFormatException e) {
        }
        return confidenceThreshold;
    }
    public void convertImageToText() {
        Loading.show(this);
        binding.OCRTextView.setText(R.string.ocr_hanja_processing_started);
        Bitmap bitmap1 = BaseUtilImage.getBitmapFromVisibleDrawable(binding.ivZoomedPhoto.getDrawable(), binding.ivZoomedPhoto); //이건 확대된 화면만 비트맵으로 만들고 싶은데, 문자 추출시 이미지 싸이즈랑 위치가 변한다.
        Bitmap bitmap2 = BaseUtilImage.getBitmap(binding.ivZoomedPhoto);

        Bitmap bitmap = BaseUtilImage.getBitmapFromVisibleDrawable(binding.ivZoomedPhoto); //이건 확대된 이미지만 제대로 추출된다.
        viewModel.setConfidenceThreshold(getConfidenceThreshold());
        viewModel.runTextRecognition(bitmap);
    }

    @Subscribe
    public void onEvent(SuccessEvent successEvent) {
        switch (successEvent.getScreen()) {
            case COMMON_VOCA_DATA:
                IVocaFullPlayTTSItem item= (IVocaFullPlayTTSItem) successEvent.getModel();
                switch (successEvent.getEventType()) {
                    case COMMON_VOCA_KNOW_CHANGED:
                    case COMMON_VOCA_KNOWPRONOUNCE_CHANGED:
                    case COMMON_VOCA_BOOKMKARK_CHANGED:
                        updateOcrResult(ocrResult);
                        break;
                }
                break;
        }
    }

    @Override
    protected void getData() {

    }

    @Override
    protected RecyclerView getRvSearch() {
        return binding.rvSearch;
    }

    @Override
    public void onHeaderLeftClick() {

    }

    @Override
    public void onHeaderLeft2Click() {

    }

    @Override
    public void onHeaderRightClick() {

    }

    @Override
    public void onHeaderIconRightClick() {

    }

    @Override
    public void onHeaderTextRightClick() {
        hanjaCommonMenuDialog.showOpenHanjaExcludeOptionDialogMenu(false);
        hanjaCommonMenuDialog.show();
    }

    @Override
    protected void showHanjaSearchResultView() {
        super.showHanjaSearchResultView();
        binding.llMain.setVisibility(View.GONE);
    }
    @Override
    protected void hideHanjaSearchResultView() {
        super.hideHanjaSearchResultView();
        binding.llMain.setVisibility(View.VISIBLE);
    }
}