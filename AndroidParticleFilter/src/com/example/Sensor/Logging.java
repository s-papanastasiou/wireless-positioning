package com.example.Sensor;

import android.content.Context;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: pierre
 * Date: 09/10/13
 * Time: 14:58
 * To change this template use File | Settings | File Templates.
 */

public class Logging {

    private static final String MIME_TYPE = "text/csv";
    private static File logFile = null;
    private static BufferedWriter writer = null;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private static boolean checkWriteAvailability() {

        boolean isWritable = false;
        String state = Environment.getExternalStorageState();

        if (Environment.MEDIA_MOUNTED.equals(state)) {
            // We can read and write the media
            isWritable = true;
        }

        return isWritable;
    }

    public static void printLine(String message) {
        print(message + System.getProperty("line.separator"));
    }

    public static void print(String message) {
        if (writer == null)
            openWriter();

        try {
            writer.write(message);
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }


    }

    public static void stopLog(Context context) {
        try {
            if (writer != null) {
                writer.close();

                MediaScannerConnection.scanFile(context, new String[]{logFile.toString()}, new String[]{MIME_TYPE}, new MediaScannerConnection.OnScanCompletedListener() {
                    @Override
                    public void onScanCompleted(String path, Uri uri) {
                        //Do nothing as don't care
                    }
                });
                writer = null;
                logFile = null;
            }
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

    }

    private static void openWriter() {

        if (checkWriteAvailability()) {
            File storageDirectory = Environment.getExternalStorageDirectory();
            logFile = new File(storageDirectory, CompassActivity.LOG_FILENAME);

            try {
                writer = new BufferedWriter(new FileWriter(logFile));
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }

    }

}
