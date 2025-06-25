package com.dalread.util;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.dalread.R;
import com.dalread.dialog.ConfirmationDialog;

public class BasePermissionUtils {

    public static final int REQUEST_CODE_RECORD_AUDIO = 2910;
    public static final int REQUEST_CODE_WRITE_EXTERNAL_STORAGE = REQUEST_CODE_RECORD_AUDIO + 1;
    public static final int REQUEST_CODE_READ_PHONE_STAGE = REQUEST_CODE_WRITE_EXTERNAL_STORAGE + 1;
    public static final int REQUEST_CODE_CAMERA = REQUEST_CODE_READ_PHONE_STAGE + 1;
    public static final int REQUEST_CODE_RECORD_CAMERA_READ_PHONE = REQUEST_CODE_CAMERA + 1;
    public static final int REQUEST_CODE_STORAGE = REQUEST_CODE_RECORD_CAMERA_READ_PHONE + 1;
    public static final int REQUEST_CODE_READ_EXTERNAL_STORAGE = REQUEST_CODE_STORAGE + 1;
    public static final int REQUEST_CODE_EXTERNAL_STORAGE = REQUEST_CODE_READ_EXTERNAL_STORAGE + 1;
    public static final int REQUEST_CODE_MANAGE_EXTERNAL_STORAGE = REQUEST_CODE_EXTERNAL_STORAGE + 1;

    public static void onRequestPermissionsResult(Activity activity, int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE_RECORD_AUDIO) {
            for (int i = 0, len = permissions.length; i < len; i++) {
                String permission = permissions[i];
                if (permission.equalsIgnoreCase(Manifest.permission.RECORD_AUDIO)) {
                    if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                        boolean showRationale = activity.shouldShowRequestPermissionRationale(permission);
                        if (!showRationale) {
                            // Deny permission and the application will not ask again,
                            // show popup to remind user to enable permission
                            ConfirmationDialog dialog = new ConfirmationDialog(activity, R.string.permission_needed_title,
                                    R.string.permission_needed_message_record_audio, R.string.open_app_details_settings, R.string.cancel,
                                    new ConfirmationDialog.OnDialogClickListener() {
                                        @Override
                                        public void onPositive(DialogInterface dialog) {
                                            dialog.dismiss();
                                            Intent intent = new Intent();
                                            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                            Uri uri = Uri.fromParts("package", Utils.getApplicationId(), null);
                                            intent.setData(uri);
                                            activity.startActivity(intent);
                                        }

                                        @Override
                                        public void onNegative(DialogInterface dialog) {
                                            dialog.dismiss();
                                        }
                                    });
                            dialog.show();
                        }
                    }
                }
            }
        }
    }

    public static boolean checkRecordAudio(Activity activity, boolean request) {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (request) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_CODE_RECORD_AUDIO);
        }
        return false;
    }
////
////    public static boolean checkRecordAudio(Context context) {
////        return ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED;
////    }
////
////    public static boolean checkRecordAudio(Activity activity, int requestCode) {
////        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
////            return true;
////        }
////        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.RECORD_AUDIO}, requestCode);
////        return false;
////    }
////
    public static boolean checkWriteExternalStorage(Context context) {
        return (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
    }

    public static boolean checkWriteExternalStorage(Activity activity, boolean request) {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (request) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_WRITE_EXTERNAL_STORAGE);
        }
        return false;
    }
//
    public static boolean checkWriteExternalStorage(Activity activity, int requestCode) {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, requestCode);
        return false;
    }

    public static boolean checkExternalStoragePermission(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (AppFlavorUtil.isAraMultiPlayerApp()) {
                return (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_MEDIA_VIDEO) == PackageManager.PERMISSION_GRANTED);
            }
            return (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED) ||
                    (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_MEDIA_AUDIO) == PackageManager.PERMISSION_GRANTED) ||
                    (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_MEDIA_VIDEO) == PackageManager.PERMISSION_GRANTED);
        } else {
            return checkWriteExternalStorage(context) && (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
        }
    }

//    public static boolean checkReadExternalStorage(Activity activity, boolean request) {
//        if (checkReadExternalStorage(activity)) {
//            return true;
//        }
//        if (request) {
//            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_READ_EXTERNAL_STORAGE);
//        }
//        return false;
//    }
//
//    public static boolean checkReadExternalStorage(Activity activity, int requestCode) {
//        if (checkReadExternalStorage(activity)) {
//            return true;
//        }
//        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, requestCode);
//        return false;
//    }
////
////    public static boolean checkReadPhoneStage(Activity activity, boolean request) {
////        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
////            return true;
////        }
////        if (request) {
////            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_PHONE_STATE}, REQUEST_CODE_READ_PHONE_STAGE);
////        }
////        return false;
////    }
////
////    public static boolean checkReadPhoneStage(Context context) {
////        return (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED);
////    }
////
////    public static boolean checkReadPhoneStage(Activity activity, int requestCode) {
////        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
////            return true;
////        }
////        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_PHONE_STATE}, requestCode);
////        return false;
////    }
////
////    public static boolean checkStoragePermission(Context context) {
////        return checkReadPhoneStage(context)
////                && checkWriteExternalStorage(context);
////    }
////
////    public static boolean checkStoragePermission(Activity activity, boolean request) {
////        if (checkStoragePermission(activity)) {
////            return true;
////        }
////        if (request) {
////            ActivityCompat.requestPermissions(activity, new String[]{
////                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
////                    Manifest.permission.READ_PHONE_STATE
////            }, REQUEST_CODE_STORAGE);
////        }
////        return false;
////    }
////
//    public static boolean checkExternalStoragePermission(Context context) {
//        return checkReadExternalStorage(context)
//                && checkWriteExternalStorage(context);
//    }
////
////    public static boolean checkExternalStoragePermission(Activity activity, boolean request) {
////        if (checkExternalStoragePermission(activity)) {
////            return true;
////        }
////        if (request) {
////            ActivityCompat.requestPermissions(activity, new String[]{
////                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
////                    Manifest.permission.READ_EXTERNAL_STORAGE
////            }, REQUEST_CODE_EXTERNAL_STORAGE);
////        }
////        return false;
////    }
////
////    public static boolean checkRecordReadCameraPermission(Activity activity, boolean request) {
////        if (checkRecordReadCameraPermission(activity)) {
////            return true;
////        }
////        if (request) {
////            ActivityCompat.requestPermissions(activity, new String[]{
////                    Manifest.permission.READ_PHONE_STATE,
////                    Manifest.permission.RECORD_AUDIO,
////                    Manifest.permission.CAMERA
////            }, REQUEST_CODE_RECORD_CAMERA_READ_PHONE);
////        }
////        return false;
////    }
////
////    public static boolean checkRecordReadCameraPermission(Context context) {
////        return checkRecordAudio(context)
////                && checkReadPhoneStage(context)
////                && checkCamera(context);
////    }
////
//    public static boolean checkCamera(Activity activity, boolean request) {
//        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
//            return true;
//        }
//        if (request) {
//            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CAMERA}, REQUEST_CODE_CAMERA);
//        }
//        return false;
//    }
//
//    public static boolean checkCamera(Context context) {
//        return (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED);
//    }
////
////    public static boolean checkCamera(Activity activity, int requestCode) {
////        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
////            return true;
////        }
////        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CAMERA}, requestCode);
////        return false;
////    }
////
////    public static void checkSystemWritePermission(Context context) {
////        if (!isCanWriteSettings(context)) {
////            openAndroidPermissionsMenu(context);
////        }
////    }
////
////    public static void openAndroidPermissionsMenu(Context context) {
////        try {
////            Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
////            intent.setData(Uri.parse("package:" + context.getPackageName()));
////            context.startActivity(intent);
////        } catch (Exception ex) {
////            ex.printStackTrace();
////        }
////    }
////
////    public static boolean isCanWriteSettings(Context context) {
////        return Build.VERSION.SDK_INT < Build.VERSION_CODES.M || Settings.System.canWrite(context);
////    }
}
