package com.dalread.activity;

import static com.dalread.util.PermissionUtils.REQUEST_CODE_CAMERA;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.dalread.component.Toolbar;
import com.dalread.databinding.ActivityOcrBackupBinding;
import com.dalread.network.events.SuccessEvent;
import com.dalread.util.BaseUtilImage;
import com.dalread.util.Loading;
import com.dalread.util.PermissionUtils;
import com.dalread.util.ToastUtil;
import com.dalread.viewmodel.GoogleMLViewModel;
import com.google.common.util.concurrent.ListenableFuture;

import org.greenrobot.eventbus.Subscribe;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class OcrBackup extends BaseHanjaInfoActivity {
    private final int INTENT_TAKE_PHOTO = 100;
    private final int INTENT_GET_GALLERY_IMAGE = 200;
    private GoogleMLViewModel viewModel;
    private Executor executor = Executors.newSingleThreadExecutor();
    private ActivityOcrBackupBinding binding;


    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();
    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }

    public static Intent createIntent(Context context) {
        Intent intent = new Intent(context, OcrBackup.class);
        return intent;
    }

    protected View getContentView() {
        binding = ActivityOcrBackupBinding.inflate(getLayoutInflater());
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
            binding.OCRTextView.setText(result);
            Loading.hide();
        });
        if (PermissionUtils.checkCamera(this, true)) {
            startCamera();
        } else {
            ToastUtil.getInstance(this).show("Can't use Camera");
        }
    }

    public void onClickOpenWordList(View view) {
        vocaKnowActivity.onDoubleClickListenerOnBaseVocaKnow.onClick(view, binding.OCRTextView.getText().toString());
    }

    private void startCamera() {
        final ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        cameraProviderFuture.addListener(new Runnable() {
            @Override
            public void run() {
                try {

                    ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                    bindPreview(cameraProvider);

                } catch (ExecutionException | InterruptedException e) {
                    // No errors need to be handled for this Future.
                    // This should never be reached.
                }
            }
        }, ContextCompat.getMainExecutor(this));
    }

    void bindPreview(@NonNull ProcessCameraProvider cameraProvider) {

        Preview preview = new Preview.Builder()
                .build();

        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();

        ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                .build();

        ImageCapture.Builder builder = new ImageCapture.Builder();

//        //Vendor-Extensions (The CameraX extensions dependency in build.gradle)
//        HdrImageCaptureExtender hdrImageCaptureExtender = HdrImageCaptureExtender.create(builder);
//
//        // Query if extension is available (optional).
//        if (hdrImageCaptureExtender.isExtensionAvailable(cameraSelector)) {
//            // Enable the extension if available.
//            hdrImageCaptureExtender.enableExtension(cameraSelector);
//        }

        final ImageCapture imageCapture = builder
                .setTargetRotation(this.getWindowManager().getDefaultDisplay().getRotation())
                .build();
        preview.setSurfaceProvider(binding.previewView.getSurfaceProvider());
        Camera camera = cameraProvider.bindToLifecycle((LifecycleOwner)this, cameraSelector, preview, imageAnalysis, imageCapture);

        binding.ivTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Set the captured image in the ivZoomedPhoto
                imageCapture.takePicture(executor, new ImageCapture.OnImageCapturedCallback() {
                    @Override
                    public void onCaptureSuccess(@NonNull @NotNull ImageProxy imageProxy) {
                        super.onCaptureSuccess(imageProxy);
                        ToastUtil.getInstance(OcrBackup.this).show("OnImageCapturedCallback is called");
                        runOnUiThread(() -> {
                            binding.ivZoomedPhoto.setImageBitmap(BaseUtilImage.getBitmap(imageProxy));
                        });
                    }

                    @Override
                    public void onError(@NonNull @NotNull ImageCaptureException exception) {
                        super.onError(exception);
                    }
                });

                //Don't Delete this.
//                //Save as a jpg file under Download folder
//                String exportFilenameWithPath = "arahanja_ocr_" + DateUtils.getDateDayTimeNoSpace(new Date()) + ".jpg";
//                File file = new File(BaseStorageUtil.getDownloadFolderPath(OCR.this), exportFilenameWithPath);
//                ImageCapture.OutputFileOptions outputFileOptions = new ImageCapture.OutputFileOptions.Builder(file).build();
                //After saving file
//                imageCapture.takePicture(outputFileOptions, executor, new ImageCapture.OnImageSavedCallback () {
//                    @Override
//                    public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
//                        ToastUtil.getInstance(OCR.this).show("Image Saved successfully : " + exportFilenameWithPath);
////                        new Handler().post(new Runnable() {
////                            @Override
////                            public void run() {
////                                Toast.makeText(OCR.this, "Image Saved successfully", Toast.LENGTH_SHORT).show();
////                            }
////                        });
//                    }
//                    @Override
//                    public void onError(@NonNull ImageCaptureException error) {
//                        error.printStackTrace();
//                    }
//                });
            }
        });
    }

    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, INTENT_TAKE_PHOTO);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CODE_CAMERA:
                if (PermissionUtils.checkCamera(this)) {
                    startCamera();
                    openCamera();
                }
        }
    }

    public void chooseImage(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setDataAndType(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(intent, INTENT_GET_GALLERY_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == INTENT_GET_GALLERY_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri selectedImageUri = data.getData();
            binding.ivZoomedPhoto.setImageURI(selectedImageUri);
        } else if (requestCode == INTENT_TAKE_PHOTO && resultCode == RESULT_OK) {
            // Bundle로 데이터를 입력
            Bundle extras = data.getExtras();
            // Bitmap으로 컨버전
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            // 이미지뷰에 Bitmap으로 이미지를 입력
            binding.ivZoomedPhoto.setImageBitmap(imageBitmap);
        }
    }

    public void onClickRotate(View view) {
        Bitmap bitmap = BaseUtilImage.getBitmap(binding.ivZoomedPhoto);
        binding.ivZoomedPhoto.setImageBitmap(BaseUtilImage.getRotateBitmap(bitmap, -90));
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
    public void convertImageToText(View view) {
        Loading.show(this);
        binding.OCRTextView.setText("Started to convert to Hanja");
        Bitmap bitmap1 = BaseUtilImage.getBitmapFromVisibleDrawable(binding.ivZoomedPhoto.getDrawable(), binding.ivZoomedPhoto); //이건 확대된 화면만 비트맵으로 만들고 싶은데, 문자 추출시 이미지 싸이즈랑 위치가 변한다.
        Bitmap bitmap2 = BaseUtilImage.getBitmap(binding.ivZoomedPhoto);

        Bitmap bitmap = BaseUtilImage.getBitmapFromVisibleDrawable(binding.ivZoomedPhoto);
        viewModel.setConfidenceThreshold(getConfidenceThreshold());
        viewModel.runTextRecognition(bitmap);
    }

    @Subscribe
    public void onEvent(SuccessEvent successEvent) {
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