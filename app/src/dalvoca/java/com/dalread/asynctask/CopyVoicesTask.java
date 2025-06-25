package com.dalread.asynctask;

import android.content.res.AssetManager;
import android.os.AsyncTask;

import com.dalread.util.Constant;
import com.dalread.util.DLog;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class CopyVoicesTask extends AsyncTask<Void, String, Void> {

    private static final String TAG = "CopyVoicesTask";

    private AssetManager assetManager;
    private File outputFolder;

    public CopyVoicesTask(AssetManager assetManager, File outputFolder) {
        this.assetManager = assetManager;
        this.outputFolder = outputFolder;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        DLog.i(TAG, "onPreExecute");
    }

    @Override
    protected Void doInBackground(Void... voids) {
        if (assetManager != null && outputFolder != null) {
            final String assetFolderName = Constant.FILE.FOLDER_SOUND;
            String[] assetFileNames;
            try {
                assetFileNames = assetManager.list(assetFolderName);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
            if (assetFileNames != null) {
                int length = assetFileNames.length;
                String tplProgress = "%s/" + length;
                String fileName;
                for (int i = 0; i < length; i++) {
                    publishProgress(String.format(tplProgress, String.valueOf(i + 1)));
                    fileName = assetFileNames[i];
                    File outputFile = new File(outputFolder, fileName);
                    if (!outputFile.exists()) {
                        InputStream inputStream;
                        OutputStream outputStream;
                        try {
                            inputStream = assetManager.open(assetFolderName + File.separator + fileName);
                            outputStream = new FileOutputStream(outputFile);
                            byte[] buffer = new byte[1024];
                            int read;
                            while ((read = inputStream.read(buffer)) != -1) {
                                outputStream.write(buffer, 0, read);
                            }
                            inputStream.close();
                            outputStream.flush();
                            outputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);

        DLog.i(TAG, "onProgressUpdate: " + values[0]);
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        DLog.i(TAG, "onPostExecute");
    }
}
