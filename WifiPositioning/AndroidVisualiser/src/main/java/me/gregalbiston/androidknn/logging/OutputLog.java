package me.gregalbiston.androidknn.logging;

import android.content.Context;
import android.os.Environment;
import android.widget.TextView;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import me.gregalbiston.androidknn.VisActivity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Created with IntelliJ IDEA.
 * User: Greg Albiston
 * Date: 26/07/13
 * Time: 12:46
 * Extends log to provide all default values and handle control of raw data output.
 */
public class OutputLog extends Log {

    private static final Logger logger = LoggerFactory.getLogger(OutputLog.class);
    
    protected static final String LOG_FILE_PREFIX = "Log";
    protected static final String LOG_FILE_EXTENSION = ".txt";
    protected static final String LOG_DIRECTORY = VisActivity.FILE_DIRECTORY + "/Logs";
    protected static final String LOG_MIME_TYPE = "text/rtf";

    protected static final String RAW_FILE_PREFIX = "Raw";
    protected static final String RAW_FILE_EXTENSION = ".csv";
    protected static final String RAW_DIRECTORY = VisActivity.FILE_DIRECTORY + "/Raw";
    protected static final String RAW_MIME_TYPE = "text/csv";

    protected String rawFilename;

    protected BufferedWriter rawWriter;

    public OutputLog(Context context, TextView logTextView, boolean isShowLog, String fileLabel, String rawHeading) {

        super(context, logTextView, isShowLog, LOG_DIRECTORY, LOG_FILE_PREFIX, fileLabel, LOG_FILE_EXTENSION, LOG_MIME_TYPE);


        File rawDir = Environment.getExternalStoragePublicDirectory(RAW_DIRECTORY);

        if (!rawDir.exists()) {
            createDir(rawDir);
        }

        rawFilename = createFilename(rawDir, RAW_FILE_PREFIX, fileLabel, RAW_FILE_EXTENSION);
        rawWriter = openOutputFile(context, rawDir, rawFilename, RAW_MIME_TYPE);
        printRawLine(rawHeading);

    }

    public void newRoute(String fileLabel, String rawHeading) {
        close();

        logText = "";
        logTextView.setText(logText);

        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {

            //Create new log file
            File logDir = Environment.getExternalStoragePublicDirectory(LOG_DIRECTORY);
            logFilename = createFilename(logDir, LOG_FILE_PREFIX, fileLabel, LOG_FILE_EXTENSION);
            logWriter = openOutputFile(context, logDir, logFilename, LOG_MIME_TYPE);

            //Create new raw file
            File rawDir = Environment.getExternalStoragePublicDirectory(RAW_DIRECTORY);
            rawFilename = createFilename(rawDir, RAW_FILE_PREFIX, fileLabel, RAW_FILE_EXTENSION);
            rawWriter = openOutputFile(context, rawDir, rawFilename, RAW_MIME_TYPE);
            printRawLine(rawHeading);
        } else {
            throw new AssertionError("Storage is not available to store log files.");
        }
    }

    @Override
    public void restart(Context context, TextView logTextView, boolean isShowLog) {

        super.restart(context, logTextView, isShowLog);

        logWriter = reopen(context, LOG_DIRECTORY, LOG_MIME_TYPE, logFilename);
        rawWriter = reopen(context, RAW_DIRECTORY, RAW_MIME_TYPE, rawFilename);

    }


    public final void printRawLine(String message) {
        try {
            String text = message + System.getProperty("line.separator");
            rawWriter.write(text);
        } catch (IOException ex) {
            logger.error("Error writing line: {}", ex);
        }
    }

    public void close() {

        close(context, LOG_DIRECTORY, LOG_MIME_TYPE, logFilename, logWriter);
        close(context, RAW_DIRECTORY, RAW_MIME_TYPE, rawFilename, rawWriter);
    }
}
