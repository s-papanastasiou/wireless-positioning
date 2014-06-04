package me.gregalbiston.androiddetector.storage;

import android.content.Intent;

/**
 * Records the outcome of the Drive upload process in UploadFileToDrive.
 * @author Greg Albiston
 */
public class UploadResult {

    private boolean isAuthoriseError = false;
    private Intent intentError = null;
    private String result;

    public void setAuthoriseError(Intent intentError_arg) {
        isAuthoriseError = true;
        intentError = intentError_arg;
    }

    public boolean isAuthoriseError() {
        return isAuthoriseError;
    }

    public Intent getIntentError() {
        return intentError;
    }

    public void addResult(String result_arg) {
        result = result_arg;
    }


    public String getResult() {
        return result;
    }

}

