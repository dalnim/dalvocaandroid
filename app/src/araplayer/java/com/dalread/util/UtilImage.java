package com.dalread.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.dalread.R;
import com.dalread.database.sqlite.model.MultiPlayerVideoStoredModel;
import com.dalread.listener.OnLoadImageFromGlideListener;
import com.dalread.model.PlayerFileModel;

import java.util.HashMap;
import java.util.Map;

public class UtilImage extends BaseUtilImage {

    public static void saveLastWatchPositionAsThumbnailImage(Context context, PlayerFileModel file) {
        try {
            long frameTime = file.getVideoModel().getLastDuration();
            Bitmap bitmap;
            if (file.isLocal()) {
                MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
                mediaMetadataRetriever.setDataSource(context, Uri.parse(file.getPath()));
                bitmap = mediaMetadataRetriever.getFrameAtTime(frameTime * Constant.PLAYER.TIMER.SECOND, MediaMetadataRetriever.OPTION_CLOSEST);
            } else {
                bitmap = getBitmapFromMediaMetadataRetriever(context, file, frameTime);
            }
            if (bitmap != null) {
                BaseUtilImage.saveImage(StorageUtil.generateLastPositionVideoScreenFilepath(context, file.getPath(), file), bitmap);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void getThumbnailFromSavedVideoImageFile(Context context, ImageView imageView, PlayerFileModel playerFileModel, boolean isFullSize, OnLoadImageFromGlideListener listener) {
        if (playerFileModel == null) {
            return;
        }
        String videoPath = playerFileModel.getPath();
        String imageFilePath = StorageUtil.generateLastPositionVideoScreenFilepath(context, videoPath, playerFileModel);
        RequestOptions options = new RequestOptions();
        options = getImageSizeOption(options, isFullSize);
        if (StorageUtil.isFileExist(imageFilePath)) {
            options.diskCacheStrategy(DiskCacheStrategy.NONE);
            options.skipMemoryCache(true);
            getThumbnail(context, imageView, imageFilePath, 0, options, listener);
        } else {
            getThumbnailVideoFile(context, imageView, videoPath, playerFileModel.getVideoModel().getLastDuration(), playerFileModel.getVideoModel().getDuration(), isFullSize, listener);
        }
    }

    public static void getThumbnailFromSavedVideoImageFile(Context context, ImageView imageView, MultiPlayerVideoStoredModel model, boolean isFullSize, OnLoadImageFromGlideListener listener) {
        if (model == null) {
            return;
        }
        String videoPath = model.getFILE_PATH();
        String imageFilePath = StorageUtil.generateLastPositionVideoScreenFilepath(context, videoPath);
        RequestOptions options = new RequestOptions();
        options = getImageSizeOption(options, isFullSize);
        if (StorageUtil.isFileExist(imageFilePath)) {
            options.diskCacheStrategy(DiskCacheStrategy.NONE);
            options.skipMemoryCache(true);
            getThumbnail(context, imageView, imageFilePath, 0, options, listener);
        } else {
            getThumbnailVideoFile(context, imageView, videoPath, model.getLAST_TIME(), model.getLAST_TIME(), isFullSize, listener);
        }
    }

    public static void getCurrentFrameSnapShot(Context context, ImageView imageView, PlayerFileModel playerFileModel, long time, OnLoadImageFromGlideListener listener) {
        Bitmap bitmap = getBitmapFromMediaMetadataRetriever(context, playerFileModel, time);
        if (bitmap != null) {
            imageView.post(() -> {
                RequestOptions options = new RequestOptions();
                options = getImageSizeOption(options, true);
                Glide.with(context)
                        .load(bitmap)
                        .placeholder(R.drawable.btn_no_border_black_background)
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
            });
        }
    }

    public static Bitmap getBitmapFromMediaMetadataRetriever(Context context, PlayerFileModel playerFileModel, long time) {
        Bitmap bitmap = null;
        try {
            MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
            if (playerFileModel.isLocal()) {
                mediaMetadataRetriever.setDataSource(context, Uri.parse(playerFileModel.getPath()));
            } else {
                mediaMetadataRetriever = VideoUtil.getMediaMetadataRetrieverFromNetworkVideo(playerFileModel);
            }
            bitmap = mediaMetadataRetriever.getFrameAtTime(time * Constant.PLAYER.TIMER.SECOND, MediaMetadataRetriever.OPTION_CLOSEST);
            mediaMetadataRetriever.release();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }
}