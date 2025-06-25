package com.dalread.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.core.content.ContextCompat;

import com.dalread.R;
import com.dalread.base.BaseHanjaFragment;
import com.dalread.databinding.FragmentOcrBinding;
import com.dalread.network.events.SuccessEvent;
import com.dalread.util.BaseUtilImage;
import com.dalread.util.DLog;
import com.dalread.util.FileUtil;
import com.dalread.util.PermissionUtils;
import com.dalread.util.ToastUtil;
import com.google.common.util.concurrent.ListenableFuture;

import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MainHanjaOcrFragment extends BaseHanjaFragment {
    private final int INTENT_GET_GALLERY_IMAGE = 100;
    private Executor executor = Executors.newSingleThreadExecutor();

    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    private ImageCapture imageCapture;

    private FragmentOcrBinding binding;

    @Override
    protected View getContentView() {
        binding = FragmentOcrBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        return view;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        if (PermissionUtils.checkCamera(getActivity(), true)) {
            startCamera();
        }
        binding.ivPickImage.setOnClickListener( v -> chooseImage(v));
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        enableButtons(true);
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Subscribe
    public void onEvent(SuccessEvent successEvent) {
    }

    private void startCamera() {
        cameraProviderFuture = ProcessCameraProvider.getInstance(getActivity());
        binding.captureButton.setOnClickListener( v -> takePicture());
        cameraProviderFuture.addListener(new Runnable() {
            @Override
            public void run() {
                try {
                    ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                    bindCameraUseCases(cameraProvider);
                } catch (ExecutionException | InterruptedException e) {
                    DLog.e("CameraX", "Error getting camera provider", e);
                }
            }
        }, ContextCompat.getMainExecutor(getActivity()));
    }
    private void bindCameraUseCases(ProcessCameraProvider cameraProvider) {
        Preview preview = new Preview.Builder().build();
        preview.setSurfaceProvider(binding.previewView.getSurfaceProvider());

        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();

        imageCapture = new ImageCapture.Builder()
                .setTargetRotation(getActivity().getWindowManager().getDefaultDisplay().getRotation())
                .build();
        try {
            cameraProvider.unbindAll();
            cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture);
        } catch (Exception e) {
            DLog.d("", "Use case binding failed", e);
        }
    }

    private void takePicture() {
        if (imageCapture != null) {
            enableButtons(false);
            ToastUtil.getInstance(getActivity()).show(R.string.toast_wait_please);
            imageCapture.takePicture(executor, new ImageCapture.OnImageCapturedCallback() {
                @Override
                public void onCaptureSuccess(ImageProxy image) {
                    // Handle the captured image here
                    DLog.e("CameraX", "Image captured");
                    super.onCaptureSuccess(image);
//                    ToastUtil.getInstance(getActivity()).show("OnImageCapturedCallback is called");
                    startOcr(BaseUtilImage.getBitmap(image));
                    enableButtons(true);
                }

                @Override
                public void onError(ImageCaptureException exception) {
                    DLog.e("CameraX", "Image capture failed", exception);
                    enableButtons(true);
                }
            });
        }
    }

    private void enableButtons(boolean value) {
        getActivity().runOnUiThread(() -> {
            binding.captureButton.setEnabled(value);
            binding.ivPickImage.setEnabled(value);
        });
    }

    public void chooseImage(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setDataAndType(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(intent, INTENT_GET_GALLERY_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        if (requestCode == INTENT_GET_GALLERY_IMAGE && data != null && data.getData() != null) {
            // Handle image selection
            Uri selectedImageUri = data.getData();
            try {
                Bitmap imageBitmap = MediaStore.Images.Media.getBitmap(requireContext().getContentResolver(), selectedImageUri);
                startOcr(imageBitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PermissionUtils.REQUEST_CODE_CAMERA:
                if (PermissionUtils.checkCamera(getActivity(), false)) {
                    startCamera();
                }
        }
    }

    private void startOcr(Bitmap imageBitmap) {
        File bitmapFile;

        bitmapFile = FileUtil.saveBitmapToFile(getActivity(), BaseUtilImage.getRotateBitmap(imageBitmap, 90));

        if (bitmapFile != null) {
            Intent intent = ActivityOcr.createIntent(requireContext(), bitmapFile.getAbsolutePath());
            startActivity(intent);
        }
    }
}
