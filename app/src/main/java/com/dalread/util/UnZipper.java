package com.dalread.util;

import android.util.Log;

import org.apache.commons.io.IOUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Observable;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class UnZipper extends Observable {

    private static final String TAG = "UnZip";
    private String mFileName, mFilePath, mDestinationPath;

    public UnZipper (String fileName, String filePath, String destinationPath) {
        mFileName = fileName;
        mFilePath = filePath;
        mDestinationPath = destinationPath;
    }

    public String getFileName () {
        return mFileName;
    }

    public String getFilePath() {
        return mFilePath;
    }

    public String getDestinationPath () {
        return mDestinationPath;
    }

    public void unzip () {
        String fullPath = mFilePath + "/" + mFileName + ".zip";
        Log.d(TAG, "unzipping " + mFileName + " to " + mDestinationPath);

        File archive = new File(fullPath);
        try {
            ZipFile zipfile = new ZipFile(archive);
            for (Enumeration e = zipfile.entries(); e.hasMoreElements();) {
                ZipEntry entry = (ZipEntry) e.nextElement();
                unzipEntry(zipfile, entry, mDestinationPath);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error while extracting file " + archive, e);
        }
    }


    private void unzipEntry(ZipFile zipfile, ZipEntry entry,
                            String outputDir) throws IOException {

        if (entry.isDirectory()) {
            createDir(new File(outputDir, entry.getName()));
            return;
        }

        File outputFile = new File(outputDir, entry.getName());
        if (!outputFile.getParentFile().exists()) {
            createDir(outputFile.getParentFile());
        }

        Log.v(TAG, "Extracting: " + entry);
        BufferedInputStream inputStream = new BufferedInputStream(zipfile.getInputStream(entry));
        BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(outputFile));

        try {
            IOUtils.copy(inputStream, outputStream);
        } finally {
            outputStream.close();
            inputStream.close();
        }
    }

    private void createDir(File dir) {
        if (dir.exists()) {
            return;
        }
        Log.v(TAG, "Creating dir " + dir.getName());
        if (!dir.mkdirs()) {
            throw new RuntimeException("Can not create dir " + dir);
        }
    }
} 