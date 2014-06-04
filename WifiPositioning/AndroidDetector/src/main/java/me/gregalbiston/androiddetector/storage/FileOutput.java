package me.gregalbiston.androiddetector.storage;

import android.content.Context;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import datastorage.MagneticData;
import datastorage.RSSIData;
import filehandling.MagneticStorer;
import filehandling.RSSIStorer;
import general.TimeStamp;
import java.io.File;
import java.util.ArrayList;
import me.gregalbiston.androiddetector.DectectorActivity;

/**
 * Static to write file to local storage. Stores information for the filename.
 * Statics used to allow convenient usage in events.
 * 
 * @author Greg Albiston
 */
public class FileOutput {

    private String orientation;
    private String accuracy;

    public String getFilename(String label) {

        String formatDate = TimeStamp.DATE_FORMAT.format(System.currentTimeMillis());
        String detail = "#" + accuracy + "$" + orientation;


        return label + formatDate + detail + DectectorActivity.OUTPUT_EXTENSION;
    }

    public boolean outputRSSIFile(ArrayList<RSSIData> results, String directory, String label, String fieldSeparator, String mimeType, Context context) {

        //Create and store data as a file
        String filename = getFilename(label);
        File file = openFile(directory, filename);
        boolean isSuccess = RSSIStorer.store(file, results, fieldSeparator, !file.exists());

        if (isSuccess)
            scanFile(context, file, mimeType);

        return isSuccess;
    }

    public boolean outputMagneticFile(ArrayList<MagneticData> results, String directory, String label, String fieldSeparator, String mimeType, Context context) {

        //Create and store data as a file
        String filename = getFilename(label);
        File file = openFile(directory, filename);
        boolean isSuccess = MagneticStorer.store(file, results, fieldSeparator, !file.exists());

        if (isSuccess)
            scanFile(context, file, mimeType);

        return isSuccess;
    }

    private File openFile(String directory, String filename) {
        File file;
        String state = Environment.getExternalStorageState();

        if (Environment.MEDIA_MOUNTED.equals(state)) {

            File dir = Environment.getExternalStoragePublicDirectory(directory);

            if (!dir.exists()) {
                createDir(dir);
            }

            file = new File(dir, filename);

        } else {
            throw new AssertionError("Storage is not available to store log files.");
        }
        return file;
    }

    protected void scanFile(Context context, File file, String mimeType) {
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

    protected void createDir(File dir) {
        if (!dir.mkdir()) {
            throw new AssertionError("Storage is not available to create directory: " + dir.toString());
        }
    }

    public void setAccuracy(String accuracy_arg) {
        //Test that the accuracy value is a valid integer before storing
        try {
            Integer.parseInt(accuracy_arg);
            accuracy = accuracy_arg;
        } catch (NumberFormatException e) {
            throw new AssertionError("None integer accuracy value in File Output: " + accuracy_arg);
        }
    }

    public void setOrientation(String orientation_arg) {
        //Test that the orientation value is a valid integer before storing
        try {
            Integer.parseInt(orientation_arg);
            orientation = orientation_arg;
        } catch (NumberFormatException e) {
            throw new AssertionError("None integer orientation value in File Output: " + orientation_arg);
        }
    }

}
