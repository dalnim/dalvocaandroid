package com.dalread.util;

import android.content.Context;

import com.dalread.R;
import com.dalread.dialog.AlertDialog;

import java.io.File;
import java.util.Date;

/**
 * 멀티플레이어 SQLite DB를 Downloads 폴더로 내보내는 공통 로직.
 * 드로어 메뉴·그리드 전체 비디오 메뉴 양쪽에서 사용.
 */
public final class MultiPlayerSqliteExportHelper {

    private MultiPlayerSqliteExportHelper() {
    }

    /**
     * 멀티플레이어 SQLite DB 파일을 공용 Downloads 폴더로 복사하고,
     * 성공 시 전체 경로를 다이얼로그로, 실패 시 토스트로 알린다.
     */
    public static void exportToDownloads(Context context) {
        String srcPath = BaseStorageUtil.getAraMultiPlayerDBPathWithFileName(context);
        File srcFile = new File(srcPath);
        if (!srcFile.exists()) {
            ToastUtil.getInstance(context).show(R.string.export_multiplayer_db_failed);
            return;
        }
        String downloadDir = BaseStorageUtil.getDownloadFolderPath(context);
        String timeSuffix = DateUtils.getDateFullNoSpace(new Date());
        String exportFileName = "araonesoft.multiplayer_" + timeSuffix + ".sqlite";
        File dstFile = new File(downloadDir, exportFileName);
        File parent = dstFile.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }
        BaseStorageUtil.copy(srcFile, dstFile);
        if (dstFile.exists() && dstFile.length() > 0) {
            AlertDialog alertDialog = new AlertDialog(context);
            alertDialog.show(
                    context.getString(R.string.info),
                    context.getString(R.string.export_multiplayer_db_succeeded, dstFile.getAbsolutePath()),
                    context.getString(R.string.ok),
                    null
            );
        } else {
            ToastUtil.getInstance(context).show(R.string.export_multiplayer_db_failed);
        }
    }
}
