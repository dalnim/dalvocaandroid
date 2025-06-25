package com.dalread.dialog;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.dalread.R;
import com.dalread.asyntask.CustomAsyncTask;
import com.dalread.asyntask.OnAsyncTaskListener;
import com.dalread.base.BaseDialog;
import com.dalread.base.BasePlayerActivity;
import com.dalread.databinding.DialogZoomedPhotoBinding;
import com.dalread.listener.OnLoadImageFromGlideListener;
import com.dalread.listener.OnYesNoClickListener;
import com.dalread.model.PlayerFileModel;
import com.dalread.util.BaseStorageUtil;
import com.dalread.util.Loading;
import com.dalread.util.StorageUtil;
import com.dalread.util.SupportImageFormat;
import com.dalread.util.ToastUtil;
import com.dalread.util.UtilImage;
import com.dalread.util.Utils;

import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.FileOutputStream;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class ZoomedPhotoDialog extends BaseDialog implements View.OnClickListener, OnAsyncTaskListener, OnLoadImageFromGlideListener {
    private final int TYPE_EXPORT = 0;
    private final int TYPE_LOAD_CURRENT_FRAME = 1;
    private BasePlayerActivity activity;

    private DialogZoomedPhotoBinding binding;
    private int currentOrientation;
    private String savedFileName;
    private PlayerFileModel playerFileModel;
    private String folderPath;

    private View getContentView() {
        binding = DialogZoomedPhotoBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    public ZoomedPhotoDialog(BasePlayerActivity activity, PlayerFileModel playerFileModel) {
        super(activity, R.style.Match);
        this.activity = activity;
        this.playerFileModel = playerFileModel;
        setFolderPath(playerFileModel);
        setContentView(getContentView());
        initOnClickListener();
    }
    private void initOnClickListener() {
        binding.btnRotate.setOnClickListener(this);
        binding.btnSave.setOnClickListener(this);
    }
    private void setFolderPath(PlayerFileModel playerFileModel) {
        if (playerFileModel == null) {
            folderPath = BaseStorageUtil.getDownloadFolderPath(activity);
        } else {
            folderPath = StorageUtil.getCurrentPath(playerFileModel); //Or use BaseStorageUtil.getPictureFolderPath(activity)
        }
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_rotate:
                rotateImage();
                break;
            case R.id.btn_save:
                showSaveFileDialog();
                break;
        }
    }

    private void rotateImage() {
        Bitmap bmp = getCurrentImageBitmap();
        rotateImage(bmp);
    }

    private void rotateImage(Bitmap bmp) {
        Matrix matrix = new Matrix();
        matrix.postRotate(-90);
        Bitmap rotatedBitmap = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);
        binding.ivZoomedPhoto.setImageBitmap(rotatedBitmap);
    }

    private void showSaveFileDialog() {
        getFileNameWithIncrementNumberToExport();
        String description = playerFileModel == null ? getContext().getString(R.string.export_as_a_png_file_description_to_picture_folder, folderPath) : getContext().getString(R.string.export_as_a_png_file_description);
        final PlayerExportFileDialog dialog = new PlayerExportFileDialog(activity,
                R.string.export_as_a_png_file,
                description,
                savedFileName,
                (view, object) -> {
                    savedFileName = StorageUtil.generatePNGFileName((String) object);
                    final String path = folderPath + File.separator + savedFileName;
                    if (StorageUtil.isFileExist(path)) {
                        showWarningSameName(savedFileName);
                    } else {
                        callExportFile();
                    }
                });
        dialog.show();
        Utils.showSoftKeyboard(activity);
    }

    private void getFileNameWithIncrementNumberToExport() {
        if (playerFileModel == null) {
            savedFileName = StorageUtil.getBaseFileNameWithNumberIncrement(folderPath, FilenameUtils.getBaseName("IMG_" + System.currentTimeMillis()), SupportImageFormat.PNG.getFileSuffix());
        } else {
            savedFileName = StorageUtil.getBaseFileNameWithNumberIncrement(folderPath, FilenameUtils.getBaseName(playerFileModel.getName()), SupportImageFormat.PNG.getFileSuffix());
        }
    }

    private void showWarningSameName(String fileName) {
        final YesNoDialog dialog = new YesNoDialog(activity, R.string.warning, R.string.msg_export_file_warning, fileName, new OnYesNoClickListener() {
            @Override
            public void onYesClick(View view, Object object) {
                callExportFile();
            }

            @Override
            public void onNoClick(View view, Object object) {
                showSaveFileDialog();
            }
        });
        dialog.show();
    }

    private void callExportFile() {
        Utils.hideSoftKeyboard(activity);
        new CustomAsyncTask(activity, this, null, TYPE_EXPORT, true).execute();
    }

    private void finishExportFile(boolean isSuccess) {
        ToastUtil.getInstance(activity).show(isSuccess ? R.string.msg_export_file_success : R.string.msg_export_file_fail);
    }

    @Override
    public void show() {
        currentOrientation = activity.getRequestedOrientation();
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Window window = getWindow();
        if (window == null) return;
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        Loading.showDelayWithoutMessage(activity);
        super.show();
    }

    public void loadCurrentFrameSnapShotAndShow(long currentFrame) {
        new CustomAsyncTask(activity, this, currentFrame, TYPE_LOAD_CURRENT_FRAME, true).execute();
        show();
    }

    public void loadThumbnailFromFileAndShow(PlayerFileModel playerFileModel, Drawable thumbnailDrawable) {
        displayThumbnailFromSmallImage(thumbnailDrawable);
        UtilImage.getThumbnailFromSavedVideoImageFile(activity, binding.ivTemp, playerFileModel, true, this);
        show();
    }

    public void loadThumbnailFromPath(String path, Drawable thumbnailDrawable) {
        displayThumbnailFromSmallImage(thumbnailDrawable);
        UtilImage.getThumbnailFullsize(activity, binding.ivTemp, path, binding.ivZoomedPhoto.getDrawable(), this);
        show();
    }

    public void loadThumbnailFromTimeAndShow(long time, Drawable thumbnailDrawable) {
        displayThumbnailFromSmallImage(thumbnailDrawable);
        UtilImage.getThumbnailVideoFile(activity, binding.ivTemp, activity.playerFileModel.getPath(), time, activity.playerFileModel.getVideoModel().getDuration(), true, this);
        show();
    }

    private void displayThumbnailFromSmallImage(Drawable drawable) {
        if (drawable != null) {
            Bitmap thumbBitmap = getBitmapFromDrawable(drawable);
            if (thumbBitmap != null  && thumbBitmap.getWidth() > thumbBitmap.getHeight()) {
                rotateImage(thumbBitmap);
            } else {
                binding.ivZoomedPhoto.setImageBitmap(thumbBitmap);
                Loading.hide();
            }
        }
    }

    private Bitmap getCurrentImageBitmap() {
        Drawable drawable = binding.ivZoomedPhoto.getDrawable();
        return getBitmapFromDrawable(drawable);
    }

    private Bitmap getBitmapFromDrawable(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }
        if (drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            return null;
        }
        Bitmap bmp = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bmp);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bmp;
    }

    @Override
    public void dismiss() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        activity.setRequestedOrientation(currentOrientation);
        super.dismiss();
    }

    @Override
    public void onInitAsyncTask() {
        Loading.showWithoutMessage(activity);
//        binding.pbLoading.setVisibility(View.VISIBLE);
    }

    @Override
    public void onPrevExecuteAsyncTask() {

    }

    @Override
    public Object onPostExecuteAsyncTask(int searchType, Object data) {
        switch (searchType) {
            case TYPE_EXPORT: {
                File dest = new File(folderPath, savedFileName);
                Bitmap bitmap = getCurrentImageBitmap();
                try {
                    dest.getParentFile().mkdirs();
                    FileOutputStream out = new FileOutputStream(dest);
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                    out.flush();
                    out.close();
                    finishExportFile(true);
                } catch (Exception e) {
                    finishExportFile(false);
                    e.printStackTrace();
                }
                break;
            }
            case TYPE_LOAD_CURRENT_FRAME: {
                long currentFrame = (long) data;
                UtilImage.getCurrentFrameSnapShot(activity, binding.ivTemp, activity.playerFileModel, currentFrame, this);
                break;
            }
        }
        return null;
    }

    @Override
    public void onFinishAsyncTask(int searchType, Object resultData, Object data) {
        if (searchType == TYPE_EXPORT) {
            Loading.hide();
        }
    }

    @Override
    public void onLoadSuccess(Drawable drawable) {
        Bitmap bitmap = getBitmapFromDrawable(drawable);
        binding.ivZoomedPhoto.post(() -> {
            if (bitmap != null && bitmap.getWidth() > bitmap.getHeight()) {
                rotateImage(bitmap);
            } else {
                binding.ivZoomedPhoto.setImageDrawable(drawable);
            }
            Loading.hide();
        });
    }

    @Override
    public void onLoadFailed() {
        Loading.hide();
    }
}
