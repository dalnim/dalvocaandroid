package com.dalread.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;
import androidx.camera.core.ImageProxy;
import androidx.core.content.ContextCompat;
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.dalread.R;
import com.dalread.listener.OnLoadImageFromGlideListener;
import com.github.chrisbanes.photoview.PhotoView;

import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

/**
 * Created by JetVHS on 2/28/2017.
 */
public class BaseUtilImage {

    public static void showImage(Activity activity, ImageView imageview, final ProgressBar progress, String url) {
        showImage(activity, imageview, R.mipmap.ic_bookmark_default, progress, url);
    }

    public static void showImage(Activity activity, ImageView imageview, int defaultImage, final ProgressBar progress, String url) {
        if (imageview == null || activity == null || activity.isDestroyed()) {
            return;
        }
        if (Utils.isEmpty(url)) {
            return;
        }
        if (progress != null) {
            progress.setVisibility(View.VISIBLE);
        }
        Glide.with(activity)
                .load(url)
                .placeholder(defaultImage)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        if (progress != null) {
                            progress.setVisibility(View.GONE);
                        }
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        if (progress != null) {
                            progress.setVisibility(View.GONE);
                        }
                        return false;
                    }
                })
                .into(imageview);
    }

//    public static void saveImage(Activity activity, String path, ImageView imageview, String url, String imageName) {
//        if (Utils.isEmpty(imageName) || activity == null || activity.isDestroyed()) return;
//        Glide.with(activity).asBitmap().load(url).into(new CustomTarget<Bitmap>() {
//            @Override
//            public void onResourceReady(Bitmap bitmap, Transition<? super Bitmap> transition) {
//                imageview.setImageBitmap(bitmap);
//                UtilImage.saveImage(path, bitmap, imageName);
//            }
//            @Override
//            public void onLoadCleared(Drawable placeholder) {
//            }
//        });
//    }

    public static void saveImage(String path, Bitmap image, String imageName) {
        File storageDir = new File(path);
        boolean success = true;
        if (!storageDir.exists()) {
            success = storageDir.getParentFile().mkdirs();
        }
        if (success) {
            File imageFile = new File(storageDir, imageName);
            try {
                OutputStream fOut = new FileOutputStream(imageFile);
                image.compress(
                        FilenameUtils.getExtension(imageName).equalsIgnoreCase("png") ? Bitmap.CompressFormat.PNG : Bitmap.CompressFormat.JPEG,
                        100, fOut);
                fOut.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void saveImage(String fullPath, Bitmap image) {
        try {
            File imageFile = new File(fullPath);
            if (!imageFile.getParentFile().exists()) {
                imageFile.getParentFile().mkdirs();
            }

            OutputStream fOut = new FileOutputStream(imageFile);
            image.compress(
                    FilenameUtils.getExtension(fullPath).equalsIgnoreCase("png") ? Bitmap.CompressFormat.PNG : Bitmap.CompressFormat.JPEG,
                    100, fOut);
            fOut.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    public static void getCurrentFrameSnapShot(Context context, ImageView imageView, PlayerFileModel playerFileModel, long time, OnLoadImageFromGlideListener listener) {
//        Bitmap bitmap = getBitmapFromMediaMetadataRetriever(context, playerFileModel, time);
//        if (bitmap != null) {
//            imageView.post(() -> {
//                RequestOptions options = new RequestOptions();
//                options = getImageSizeOption(options, true);
//                Glide.with(context)
//                        .load(bitmap)
//                        .placeholder(R.drawable.btn_no_border_black_background)
//                        .apply(options)
//                        .listener(new RequestListener<Drawable>() {
//                            @Override
//                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
//                                return false;
//                            }
//
//                            @Override
//                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
//                                if (listener != null) {
//                                    listener.onLoadSuccess(resource);
//                                }
//                                return false;
//                            }
//                        })
//                        .into(imageView);
//            });
//        }
//    }
//
//    public static Bitmap getBitmapFromMediaMetadataRetriever(Context context, PlayerFileModel playerFileModel, long time) {
//        Bitmap bitmap = null;
//        try {
//            MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
//            if (playerFileModel.isLocal()) {
//                mediaMetadataRetriever.setDataSource(context, Uri.parse(playerFileModel.getPath()));
//            } else {
//                String videoPath = playerFileModel.getServerModel().getPath(playerFileModel.getPath());
//                String auth = NetworkUtil.generateBasicAuth(playerFileModel.getServerModel().getAccountPassword());
//                Map<String, String> headers = new HashMap<>();
//                headers.put("User-Agent", NetworkUtil.getDefaultUserAgent());
//                headers.put("Authorization", auth);
//                mediaMetadataRetriever.setDataSource(videoPath, headers);
//            }
//            bitmap = mediaMetadataRetriever.getFrameAtTime(time * Constant.PLAYER.TIMER.SECOND, MediaMetadataRetriever.OPTION_CLOSEST);
//            mediaMetadataRetriever.release();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return bitmap;
//    }

    public static void getThumbnail(Context context, ImageView imageView, String path, OnLoadImageFromGlideListener listener) {
        getThumbnail(context, imageView, path, R.drawable.ic_video, new RequestOptions(), listener);
    }

    public static void getThumbnailCircle(Context context, ImageView imageView, int defaultImage, String path, OnLoadImageFromGlideListener listener) {
        RequestOptions options = new RequestOptions();
        options.circleCrop();
        getThumbnail(context, imageView, path, defaultImage, options, listener);
    }

    public static void getThumbnail(Context context, ImageView imageView, int defaultImage, String path, OnLoadImageFromGlideListener listener) {
        getThumbnail(context, imageView, path, defaultImage, new RequestOptions(), listener);
    }

    public static void getThumbnailVideoFile(Context context, ImageView imageView, String path, long time, long duration, boolean isFullSize, OnLoadImageFromGlideListener listener) {
        RequestOptions options = new RequestOptions();
        options = getImageSizeOption(options, isFullSize);

        if (duration > 0) {
            if (time <= 0) {
                long defaultTime = duration / 10;
                options.frame(defaultTime * Constant.PLAYER.TIMER.SECOND);
            } else {
                options.frame(time * Constant.PLAYER.TIMER.SECOND);
            }
        }

        getThumbnail(context, imageView, path, R.drawable.btn_no_border_black_background, options, listener);
    }

    protected static RequestOptions getImageSizeOption(RequestOptions options, boolean isFullSize) {
        if (isFullSize) {
            options.override(4000, 4000);
        }
        return options;
    }

    public static void getThumbnailFullsize(Context context, ImageView imageView, String path, OnLoadImageFromGlideListener listener) {
        RequestOptions options = new RequestOptions();
        options.centerInside();
        options = getImageSizeOption(options, true);

        getThumbnail(context, imageView, path, 0, options, listener);
    }

    public static void getThumbnailFullsize(Context context, ImageView imageView, String path, Drawable placeHolder, OnLoadImageFromGlideListener listener) {
        RequestOptions options = new RequestOptions();
        options.centerInside();
        options = getImageSizeOption(options, true);

        getThumbnail(context, imageView, path, placeHolder, options, listener);
    }

    public static void getThumbnail(Context context, ImageView imageView, String path, int defaultImage, RequestOptions options, OnLoadImageFromGlideListener listener) {
        if (context == null)
            return;

        if (defaultImage == 0)
            defaultImage = R.drawable.btn_no_border_black_background;

        Glide.with(context)
                .load(path)
                .placeholder(defaultImage)
                .apply(options)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        if (listener != null) {
                            listener.onLoadFailed();
                        }
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        if (listener != null) {
                            listener.onLoadSuccess(resource);
                        }
                        return false;
                    }
                })
                .into(imageView);
    }

    public static void getThumbnail(Context context, ImageView imageView, String path, Drawable placeHolder, RequestOptions options, OnLoadImageFromGlideListener listener) {
        if (context == null)
            return;

        if (placeHolder == null)
            placeHolder = context.getDrawable(R.drawable.btn_no_border_black_background);

        Glide.with(context)
                .load(path)
                .placeholder(placeHolder)
                .apply(options)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        if (listener != null) {
                            listener.onLoadFailed();
                        }
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        if (listener != null) {
                            listener.onLoadSuccess(resource);
                        }
                        return false;
                    }
                })
                .into(imageView);
    }
    //이건 이미지를 확대시 확대되어 PhotoView에 보이는 영역만 비트맵으로 만들어준다.
    public static Bitmap getBitmapFromVisibleDrawable(PhotoView photoView) {
        int visibleWidth = photoView.getWidth();
        int visibleHeight = photoView.getHeight();

        Bitmap bitmap = Bitmap.createBitmap(visibleWidth, visibleHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.translate(-photoView.getScrollX(), -photoView.getScrollY());
        photoView.draw(canvas);

        return bitmap;
    }

    //이건 이미지를 확대시 확대되어 PhotoView에 보이는 영역만 비트맵으로 만들려고 했는데, 이걸 하면 확대된 이미지가 옮겨지고 축척이 바뀌는 버그가 있다.
    public static Bitmap getBitmapFromVisibleDrawable(Drawable drawable, PhotoView photoView) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }
        if (drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            return null;
        }

        RectF visibleRect = new RectF();
        Matrix matrix = new Matrix(photoView.getImageMatrix());
        matrix.mapRect(visibleRect, new RectF(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight()));

        int visibleWidth = Math.round(visibleRect.width());
        int visibleHeight = Math.round(visibleRect.height());

        Bitmap bmp = Bitmap.createBitmap(visibleWidth, visibleHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bmp);

        Matrix canvasMatrix = new Matrix();
        canvasMatrix.postTranslate(-visibleRect.left, -visibleRect.top);
        canvas.setMatrix(canvasMatrix);

        drawable.setBounds(0, 0, visibleWidth, visibleHeight);
        drawable.draw(canvas);

        return bmp;
    }

    public static Bitmap getBitmap(ImageView imageView) {
        Drawable drawable = imageView.getDrawable();
        return getBitmapFromDrawable(drawable);
    }

    private static Bitmap getBitmapFromDrawable(Drawable drawable) {
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

//        Drawable drawable = binding.ivZoomedPhoto.getDrawable();
//        Bitmap bitmap = null;
//        if (drawable instanceof BitmapDrawable) {
//            BitmapDrawable bitmapDrawable = (BitmapDrawable) binding.ivZoomedPhoto.getDrawable();
//            bitmap = bitmapDrawable.getBitmap();
//        } else {
//            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
//        }
//        return bitmap;
    }

    public static Bitmap getRotateBitmap(Bitmap bmp, int degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        Bitmap rotatedBitmap = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);
        return rotatedBitmap;
    }

    public static Bitmap getBitmap(ImageProxy image) {
        ByteBuffer buffer = image.getPlanes()[0].getBuffer();
        buffer.rewind();
        byte[] bytes = new byte[buffer.capacity()];
        buffer.get(bytes);
        byte[] clonedBytes = bytes.clone();
        return BitmapFactory.decodeByteArray(clonedBytes, 0, clonedBytes.length);
    }

    public static Bitmap xmlToBitmapDrawable(Context context, @DrawableRes int drawableId) {
        Drawable drawable = ContextCompat.getDrawable(context, drawableId);
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        } else if (drawable instanceof VectorDrawableCompat || drawable instanceof VectorDrawable) {
            Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
            return bitmap;
        } else {
            throw new IllegalArgumentException("Unsupported drawable type");
        }
    }

}
