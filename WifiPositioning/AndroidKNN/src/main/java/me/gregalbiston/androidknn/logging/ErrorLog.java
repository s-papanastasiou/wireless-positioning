package me.gregalbiston.androidknn.logging;

import android.content.Context;
import android.widget.TextView;
import me.gregalbiston.androidknn.VisActivity;

/**
 * Created with IntelliJ IDEA.
 * User: Greg Albiston
 * Date: 03/08/13
 * Time: 10:43
 * Error log class for output of none data messages. Extends basic log to provide all default values.
 */
public class ErrorLog extends Log {

    protected static final String LOG_FILE_PREFIX = "Error";
    protected static final String LOG_FILE_LABEL = "Log";
    protected static final String LOG_FILE_EXTENSION = ".txt";
    protected static final String LOG_DIRECTORY = VisActivity.FILE_DIRECTORY + "/ErrorLogs";
    protected static final String LOG_MIME_TYPE = "text/rtf";

    public ErrorLog(Context context, TextView logTextView, boolean isShowLog) {
        super(context, logTextView, isShowLog, LOG_DIRECTORY, LOG_FILE_PREFIX, LOG_FILE_LABEL, LOG_FILE_EXTENSION, LOG_MIME_TYPE);
    }

    @Override
    public void restart(Context context, TextView logTextView, boolean isShowLog) {
        super.restart(context, logTextView, isShowLog);
        logWriter = reopen(context, LOG_DIRECTORY, LOG_MIME_TYPE, logFilename);
    }

    public void close() {
        close(context, LOG_DIRECTORY, LOG_MIME_TYPE, logFilename, logWriter);
    }

}
