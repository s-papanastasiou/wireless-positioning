package me.gregalbiston.androidvisualiser.logging;

import android.content.Context;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.view.View;
import android.widget.TextView;
import general.TimeStamp;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created with IntelliJ IDEA.
 * User: Greg Albiston
 * Date: 03/08/13
 * Time: 10:45
 * Base class for handling logging functions. Stores log messages and outputs to a text file in the public external file storage.
 */
public class Log {

    private static final Logger logger = LoggerFactory.getLogger(Log.class);
    
    protected TextView logTextView = null;
    protected String logText = "";

    protected BufferedWriter logWriter;

    protected Context context;
    protected String logFilename;

    Log(Context context, TextView logTextView, boolean isShowLog, String directory, String prefix, String fileLabel, String extension, String mimeType) {

        this.context = context;
        this.logTextView = logTextView;

        showLog(isShowLog);

        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {

            File logDir = Environment.getExternalStoragePublicDirectory(directory);

            if (!logDir.exists()) {
                createDir(logDir);
            }

            this.logFilename = createFilename(logDir, prefix, fileLabel, extension);
            this.logWriter = openOutputFile(context, logDir, logFilename, mimeType);

        } else {
            throw new AssertionError("Storage is not available to store log files.");
        }

    }

    protected static void createDir(File dir) {
        if (!dir.mkdir()) {
            throw new AssertionError("Storage is not available to create directory: " + dir.toString());
        }

    }

    public final void showLog(boolean isShowLog) {
        if (isShowLog)
            logTextView.setVisibility(View.VISIBLE);
        else
            logTextView.setVisibility(View.INVISIBLE);
    }

    protected static String createFilename(File dir, String filePrefix, String fileLabel, String fileExtension) {

        String filename = "";
        //Date stamp the log file
        boolean isExisting = true;
        File file;
        int counter = 0;
        while (isExisting) {
            filename = String.format("%s-%s-%d-%s%s", filePrefix, fileLabel, counter, TimeStamp.formatDate(System.currentTimeMillis()), fileExtension);
            file = new File(dir, filename);

            if (!file.exists()) {

                isExisting = false;
            } else {
                counter++;
            }
        }
        return filename;
    }

    public String printLogLine(String message) {
        String text = message + System.getProperty("line.separator");
        logText += text;

        //if available add the latest test to the log text view
        if (logTextView != null)
            logTextView.append(text);

        if (logWriter != null) {
            try {
                logWriter.write(text);
            } catch (IOException ex) {
                logger.error("Error writing line: {}", ex);
            }
        }
        return text;
    }

    public String printLogLine(int message) {
        return printLogLine(context.getString(message));
    }

    protected static BufferedWriter openOutputFile(Context context, File dir, String filename, String mimeType) {

        File file = new File(dir, filename);
        BufferedWriter writer = null;
        try {
            boolean isExisting = file.exists();      //check if the file existed before it is opened/created.

            writer = new BufferedWriter(new FileWriter(file.toString(), true));

            if (!isExisting)     //inform the media scanner that a file has been created.
                scanFile(context, file, mimeType);

        } catch (IOException ex) {
            logger.error("Error writing line: {}", ex);
        }

        return writer;
    }

    protected static void scanFile(Context context, File file, String mimeType) {
        //update the Media Scanner so new log shows over usb connection without a restart
        //Based on http://skotagiri.wordpress.com/2012/08/23/make-files-created-by-android-applications-visible-in-windows-without-a-reboot/

        //TODO Fix bug when connected via USB. Device restart is required to view contents of files. Create an AsyncTask to carry out the media scan??

        MediaScannerConnection.scanFile(context, new String[]{file.toString()}, new String[]{mimeType}, new MediaScannerConnection.OnScanCompletedListener() {
            @Override
            public void onScanCompleted(String path, Uri uri) {
                //Do nothing as don't care
            }
        });
    }

    protected static BufferedWriter reopen(Context context, String directory, String mimeType, String filename) {

        BufferedWriter writer;
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {

            File logDir = Environment.getExternalStoragePublicDirectory(directory);

            if (!logDir.exists()) {
                createDir(logDir);
            }

            writer = openOutputFile(context, logDir, filename, mimeType);


        } else {
            throw new AssertionError("Storage is not available to store log files.");
        }

        return writer;
    }

    protected void restart(Context context, TextView logTextView, boolean isShowLog) {
        this.context = context;
        this.logTextView = logTextView;
        this.logTextView.append(logText);
        showLog(isShowLog);
    }

    protected static void close(Context context, String logDirectory, String mimeType, String filename, BufferedWriter writer) {

        //close the log file
        try {
            writer.close();
        } catch (IOException ex) {
            logger.error("Error writing line: {}", ex);
        }

        //update the Media Scanner so new log shows over usb connection without a restart
        //Based on http://skotagiri.wordpress.com/2012/08/23/make-files-created-by-android-applications-visible-in-windows-without-a-reboot/
        File logDir = Environment.getExternalStoragePublicDirectory(logDirectory);
        File logFile = new File(logDir, filename);
        scanFile(context, logFile, mimeType);
    }

}
