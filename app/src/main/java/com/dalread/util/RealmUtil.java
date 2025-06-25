package com.dalread.util;

import android.content.Context;

import com.dalread.R;
import com.dalread.dialog.AlertDialog;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;

import io.realm.Realm;

public class RealmUtil {

    private static void copy(File src, File dst) throws IOException {
        InputStream in = new FileInputStream(src);
        OutputStream out = new FileOutputStream(dst);

        // Transfer bytes from in to out
        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        in.close();
        out.close();
    }

    private static boolean backup(Context context, Realm realm, String fileName) {
        File f = new File(realm.getPath());
        try {
            File dstFile = new File(BaseStorageUtil.getAppTempPath(context) + File.separator + fileName);
            if (!dstFile.exists()) {
                dstFile.getParentFile().mkdirs();
                dstFile.createNewFile();
            }
            copy(f, dstFile);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static void onBackupRealmDB(Context context) {
        final String fileName = context.getString(R.string.app_name) + "_" + DateUtils.getDateFullNoSpace(new Date()) + ".realm";
        boolean isSuccess = backup(context, BaseVoca.getRealm(), fileName);
        if (isSuccess) {
            AlertDialog alertDialog = new AlertDialog(context);
            alertDialog.show(context.getString(R.string.backup_succeeded, BaseStorageUtil.getAppTempPath(context) + File.separator + fileName), null, null);
        } else {
            ToastUtil.getInstance(context).show(R.string.backup_failed);
        }

    }
}
