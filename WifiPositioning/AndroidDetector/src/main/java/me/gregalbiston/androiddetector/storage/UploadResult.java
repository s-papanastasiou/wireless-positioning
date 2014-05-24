package me.gregalbiston.androiddetector.storage;

import android.content.Intent;

/**
 * Created with IntelliJ IDEA.
 * User: Gerg
 * Date: 20/06/13
 * Time: 07:20
 * Records the outcome of the Drive upload process in UploadFileToDrive.
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

