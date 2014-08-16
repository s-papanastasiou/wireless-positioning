package me.gregalbiston.androiddetector.survey;

import android.content.Context;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpResponse;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;

import java.io.*;
import java.util.List;
import me.gregalbiston.androiddetector.DetectorActivity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Accesses file stored on Drive to get latest values for performing the survey and stores to local file.
 * @author Greg Albiston
 */
public class UpdateRoomInfo extends AsyncTask<Object, Void, String> {

    private static final Logger logger = LoggerFactory.getLogger(UpdateRoomInfo.class);

    protected DetectorActivity dectectorActivity;
    protected Drive service;
    protected String directory;
    protected String filename;
    protected String mimeType;

    public UpdateRoomInfo(DetectorActivity dectectorActivity, Drive service, String directory, String filename, String mimeType) {
        super();
        this.dectectorActivity = dectectorActivity;
        this.service = service;
        this.directory = directory;
        this.filename = filename;
        this.mimeType = mimeType;
    }

    @Override
    protected String doInBackground(Object... params) {
        try {

            File file = checkDriveFileExists(filename);

            if (file.getDownloadUrl() != null && file.getDownloadUrl().length() > 0) {

                HttpResponse resp = service.getRequestFactory().buildGetRequest(new GenericUrl(file.getDownloadUrl())).execute();

                BufferedReader reader = new BufferedReader(new InputStreamReader(resp.getContent()));

                BufferedWriter writer;
                String state = Environment.getExternalStorageState();
                if (Environment.MEDIA_MOUNTED.equals(state)) {

                    java.io.File dir = Environment.getExternalStoragePublicDirectory(directory);

                    if (!dir.exists()) {
                        createDir(dir);
                    }

                    writer = createOutputFile(dectectorActivity, dir, filename, mimeType);
                    String line;
                    do {
                        line = reader.readLine();
                        if (line != null) {
                            line += DetectorActivity.OUTPUT_LINE_SEPARATOR;
                            writer.write(line);
                        }

                    } while (line != null);

                    reader.close();
                    writer.close();

                } else {
                    throw new AssertionError("Storage is not available to store settings file.");
                }
            }

        } catch (IOException ex) {
            logger.error("Writing to storage error: {}", ex);
        }

        return "Complete";
    }

    protected static void createDir(java.io.File dir) {
        if (!dir.mkdir()) {
            throw new AssertionError("Storage is not available to create directory: " + dir.toString());
        }

    }

    protected static BufferedWriter createOutputFile(Context context, java.io.File dir, String filename, String mimeType) {

        java.io.File file = new java.io.File(dir, filename);
        BufferedWriter writer = null;
        try {
            file.delete();

            writer = new BufferedWriter(new FileWriter(file));

            //inform the media scanner that a file has been created.
            scanFile(context, file, mimeType);

        } catch (IOException ex) {
            logger.error("Writing to storage error: {}", ex);
        }

        return writer;
    }

    protected static void scanFile(Context context, java.io.File file, String mimeType) {
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


    private File checkDriveFileExists(String filename) throws IOException {
        Drive.Files.List request = service.files().list().setQ("title = '" + filename + "' and trashed = false");
        FileList fileList = request.execute();
        List<File> files = fileList.getItems();

        File file = null;
        if (!files.isEmpty()) {
            file = files.get(0);
        }

        return file;
    }

    @Override
    protected void onPreExecute() {
    }


    @Override
    protected void onPostExecute(String result) {
        dectectorActivity.setupControls();
        dectectorActivity.showToast("Updated Room Info");
        dectectorActivity.enableInteractions();
    }

}