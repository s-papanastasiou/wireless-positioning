package me.gregalbiston.androiddetector.storage;

import android.os.AsyncTask;
import android.os.Environment;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.FileContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import me.gregalbiston.androiddetector.DectectorActivity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Upload results to Drive. Updates existing file and inserts new if required.
 * @author Greg Albiston
 */
public class UploadFileToDrive extends AsyncTask<Object, UploadResult, Object> {

    private static final Logger logger = LoggerFactory.getLogger(UploadFileToDrive.class);
    
    protected DectectorActivity dectectorActivity;
    protected Drive service;
    protected ArrayList<String> filenames;
    protected String directory;


    public UploadFileToDrive(DectectorActivity dectectorActivity, Drive service, String directory, ArrayList<String> filenames) {
        super();
        this.dectectorActivity = dectectorActivity;
        this.service = service;
        this.filenames = filenames;
        this.directory = directory;
        dectectorActivity.showUploadAllProgressBar(filenames.size());
    }

    @Override
    protected Object doInBackground(Object... params) {

        String state = Environment.getExternalStorageState();

        if (Environment.MEDIA_MOUNTED.equals(state)) {   //TODO since only reading from drive there are probably other states that are ok

            java.io.File dir = Environment.getExternalStoragePublicDirectory(directory);
            for (String filename : filenames) {
                if (!isCancelled()) {
                    UploadResult uploadResult = new UploadResult();
                    try {
                        String result = uploadFile(dir, filename);
                        uploadResult.addResult(result);

                    } catch (UserRecoverableAuthIOException e) {
                        uploadResult.setAuthoriseError(e.getIntent());

                    } catch (IOException ex) {
                        logger.error("Error uploading to drive: {}", ex);
                    } finally {
                        publishProgress(uploadResult);
                    }

                }
            }
        } else {
            UploadResult uploadResult = new UploadResult();
            uploadResult.addResult("External storage is not available to read.");
            publishProgress(uploadResult);
        }

        return 0;
    }

    private String uploadFile(java.io.File dir, String filename) throws IOException {

        File file = checkDriveFileExists(filename);
        String result;
        if (file != null)
            result = updateFile(dir, filename, file);
        else
            result = insertFile(dir, filename);

        return result;
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

    private String insertFile(java.io.File dir, String filename) throws IOException {

        String result = "File not updated: " + filename;
        // File's binary content
        java.io.File localFile = new java.io.File(dir, filename);
        FileContent fileContent = new FileContent(DectectorActivity.OUTPUT_MIME_TYPE, localFile);

        // File's metadata.
        File file = new File();
        file.setTitle(localFile.getName());
        file.setMimeType(DectectorActivity.OUTPUT_MIME_TYPE);

        File insertedFile = service.files().insert(file, fileContent).execute();
        if (insertedFile != null) {
            result = "File created: " + insertedFile.getTitle();
        }
        return result;
    }

    private String updateFile(java.io.File dir, String filename, File file) throws IOException {

        String result = "File not updated: " + filename;

        // File's new content.
        java.io.File localFile = new java.io.File(dir, filename);
        FileContent fileContent = new FileContent(DectectorActivity.OUTPUT_MIME_TYPE, localFile);

        // Send the request to the API.
        File updatedFile = service.files().update(file.getId(), file, fileContent).execute();

        if (updatedFile != null) {
            result = ("File updated: " + updatedFile.getTitle());
        }
        return result;
    }

    @Override
    protected void onPreExecute() {
    }

    @Override
    protected void onProgressUpdate(UploadResult... results) {

        for (UploadResult result : results) {
            if (result.isAuthoriseError()) {
                dectectorActivity.requestAuthorisation(result.getIntentError());
                cancel(false);
            } else {
                dectectorActivity.showToast(result.getResult());
                dectectorActivity.incUploadAllProgressBar();
            }
        }
    }


    @Override
    protected void onPostExecute(Object dummy) {

        stoppingTask();
    }

    @Override
    protected void onCancelled() {

        stoppingTask();
    }

    private void stoppingTask() {
        dectectorActivity.hideUploadAllProgressBar();
        dectectorActivity.enableInteractions();
    }

}
