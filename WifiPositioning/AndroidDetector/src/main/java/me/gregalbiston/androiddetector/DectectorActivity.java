package me.gregalbiston.androiddetector;

import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.*;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import me.gregalbiston.androiddetector.scan.ScanSettings;
import me.gregalbiston.androiddetector.scan.Scanner;
import me.gregalbiston.androiddetector.storage.FileOutput;
import me.gregalbiston.androiddetector.storage.UploadFileToDrive;
import me.gregalbiston.androiddetector.survey.SurveySettings;
import me.gregalbiston.androiddetector.survey.UpdateRoomInfo;

public class DectectorActivity extends Activity {
    /**
     * Called when the activity is first created.
     */

    private WifiManager wifiManager;
    private boolean isRSSISelected = true;
    private boolean isMagneticSelected = true;

    private GoogleAccountCredential credential = null;
    private Drive service;
    static final int REQUEST_ACCOUNT_SELECT = 1;
    static final int REQUEST_AUTHORIZATION_UPLOAD = 2;
    static final int REQUEST_ACCOUNT_UPLOAD = 3;

    private SurveySettings surveySettings;

    private boolean isScannedOnce = false;  //Identifies whether at least one scan has been carried out with the current accuracy and orientation

    private final FileOutput fileOutput = new FileOutput();

    //Static variables to control output files and survey parameters files. Place here for easier adjustment.
    public final static String FILE_DIRECTORY = "Detector";
    public final static String ROOM_INFO_FILENAME = "RoomInfo.csv";
    public final static String ROOM_INFO_MIME_TYPE = "text/csv";
    public final static String ROOM_INFO_FIELD_SEPARATOR = ",";

    public final static String OUTPUT_SEPARATOR = ",";
    public final static String OUTPUT_MIME_TYPE = "text/csv";
    public final static String OUTPUT_EXTENSION = ".csv";
    public final static String OUTPUT_LINE_SEPARATOR = System.getProperty("line.separator");
    public final static String OUTPUT_FILENAME_RSSI = "RSSI Results";
    public final static String OUTPUT_FILENAME_MAGNETIC = "Magnetic Results";

    private boolean isConnected;
    private boolean isSignedIn = false;
    private static final boolean ENABLE_GOOGLE_DRIVE = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        setupControls();
        wifiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);

        //check for internet connection
        if (ENABLE_GOOGLE_DRIVE)
            isConnected = checkConnectivity();
        else
            isConnected = false;
    }

    public void startScan(View view) {
        //Only scan if one of the options is selected.
        if (isRSSISelected | isMagneticSelected) {

            //Enable wifi if not already active
            if (!wifiManager.isWifiEnabled()) {
                wifiManager.setWifiEnabled(true);
            }

            //Retrieve controls and values.
            NumberPicker durationPicker = (NumberPicker) findViewById(R.id.numberPickerDuration);
            int duration = durationPicker.getValue();

            NumberPicker frequencyPicker = (NumberPicker) findViewById(R.id.numberPickerFrequency);
            int frequency = frequencyPicker.getValue();


            //Disable buttons
            disableInteractions();

            //Setup and run the RSSI and Geo-Magnetic Scanner
            Scanner scanner = new Scanner(duration, frequency, this, new ScanSettings(fileOutput, isRSSISelected, isMagneticSelected));
            scanner.start();
            showToast("Scan started");
            isScannedOnce = true;
        } else {
            showToast("Either one or both modes must be selected");
        }
    }

    public void toggleRSSI(View view) {
        isRSSISelected = !isRSSISelected;
    }

    public void toggleMagnetic(View view) {
        isMagneticSelected = !isMagneticSelected;
    }

    public void startUpload(View view) {

        if (isConnected) {
            if (isScannedOnce) {
                if (credential != null) {
                    uploadFileToDrive();
                } else {
                    credential = GoogleAccountCredential.usingOAuth2(this, DriveScopes.DRIVE);                    
                    startActivityForResult(credential.newChooseAccountIntent(), REQUEST_ACCOUNT_UPLOAD);
                }

            } else {
                showToast("Must scan before upload");
            }
        }
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        if (isConnected) {
            switch (requestCode) {
                case REQUEST_ACCOUNT_UPLOAD:
                    if (resultCode == RESULT_OK && data != null && data.getExtras() != null) {
                        String accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                        if (accountName != null) {
                            applyAccountSelection(accountName);
                            uploadFileToDrive();
                        }
                    }
                    break;
                case REQUEST_AUTHORIZATION_UPLOAD:
                    if (resultCode == Activity.RESULT_OK) {
                        uploadFileToDrive();
                    } else {
                        startActivityForResult(credential.newChooseAccountIntent(), REQUEST_ACCOUNT_UPLOAD);
                    }
                    break;
                case REQUEST_ACCOUNT_SELECT:
                    if (resultCode == RESULT_OK && data != null && data.getExtras() != null) {
                        String accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                        if (accountName != null) {
                            applyAccountSelection(accountName);
                        }
                    }
                    break;
            }
        }
    }

    private void applyAccountSelection(String accountName) {
        credential.setSelectedAccountName(accountName);
        service = getDriveService(credential);
        TextView uploadAccount = (TextView) findViewById(R.id.textViewUploadAccount);
        uploadAccount.setText(accountName);

        //Activate buttons requiring account authentication.
        enableAccountButtons();
        isSignedIn = true;
    }

    private void uploadFileToDrive() {

        if (isConnected) {
            //Disable buttons while uploading
            disableInteractions();

            ArrayList<String> filenames = new ArrayList<>();
            if (isRSSISelected)
                filenames.add(fileOutput.getFilename(DectectorActivity.OUTPUT_FILENAME_RSSI));

            if (isMagneticSelected)
                filenames.add(fileOutput.getFilename(DectectorActivity.OUTPUT_FILENAME_MAGNETIC));

            //Commence upload
            showToast("Uploading");
            UploadFileToDrive uploadFileToDrive = new UploadFileToDrive(this, service, FILE_DIRECTORY, filenames);
            uploadFileToDrive.execute();
        } else {
            showToast("No connection or GoogleDrive disabled at compile time");
        }
    }

    public void showToast(final String toast) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), toast, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private Drive getDriveService(GoogleAccountCredential credential) {
        return new Drive.Builder(AndroidHttp.newCompatibleTransport(), new GsonFactory(), credential).build();
    }

    public void changeAccount(View view) {

        if (isConnected) {
            credential = GoogleAccountCredential.usingOAuth2(this, DriveScopes.DRIVE);
            startActivityForResult(credential.newChooseAccountIntent(), REQUEST_ACCOUNT_SELECT);
        }
    }

    public void setupControls() {

        NumberPicker duration = (NumberPicker) findViewById(R.id.numberPickerDuration);
        duration.setMinValue(1);
        duration.setMaxValue(120);

        NumberPicker frequency = (NumberPicker) findViewById(R.id.numberPickerFrequency);
        frequency.setMinValue(1);
        frequency.setMaxValue(100);

        surveySettings = new SurveySettings(FILE_DIRECTORY, ROOM_INFO_FILENAME, ROOM_INFO_FIELD_SEPARATOR);

        List<String> accuracyValues = surveySettings.getAccuracy();
        List<String> orientationValues = surveySettings.getOrientation();
        List<String> roomNames = surveySettings.getRoomNames();

        //Setup the accuracy spinner
        Spinner accuracy = (Spinner) findViewById(R.id.spinnerAccuracy);
        ArrayAdapter accuracyAdapter;
        accuracyAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, accuracyValues);
        accuracyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        accuracy.setAdapter(accuracyAdapter);

        //Accuracy spinner event handling
        accuracy.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //upload current data if a file has been scanned
                startUpload(view);

                //Retrieve the orientation and set for future filenames
                String accuracyValue = (String) parent.getItemAtPosition(position);
                fileOutput.setAccuracy(accuracyValue);
                isScannedOnce = false;

                Spinner room = (Spinner) findViewById(R.id.spinnerRooms);
                int roomIndex = room.getSelectedItemPosition();

                Spinner orient = (Spinner) findViewById(R.id.spinnerOrientation);
                int orientationIndex = orient.getSelectedItemPosition();

                setGrid(roomIndex, position, orientationIndex);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        //Setup the orientation spinner
        Spinner orientation = (Spinner) findViewById(R.id.spinnerOrientation);
        ArrayAdapter orientationAdapter;
        orientationAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, orientationValues);
        orientationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        orientation.setAdapter(orientationAdapter);

        //Orientation spinner event handling
        orientation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //upload current data
                startUpload(view);

                //Retrieve the orientation and set for future filenames

                String orientationValue = (String) parent.getItemAtPosition(position);
                fileOutput.setOrientation(orientationValue);
                isScannedOnce = false;

                Spinner room = (Spinner) findViewById(R.id.spinnerRooms);
                int roomIndex = room.getSelectedItemPosition();

                Spinner acc = (Spinner) findViewById(R.id.spinnerAccuracy);
                int accuracyIndex = acc.getSelectedItemPosition();

                setGrid(roomIndex, accuracyIndex, position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        //Setup the room spinner
        Spinner rooms = (Spinner) findViewById(R.id.spinnerRooms);
        ArrayAdapter roomsAdapter;
        roomsAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, roomNames);
        roomsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        rooms.setAdapter(roomsAdapter);
        //Room spinner event handling
        rooms.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                Spinner acc = (Spinner) findViewById(R.id.spinnerAccuracy);
                int accuracyIndex = acc.getSelectedItemPosition();

                Spinner orient = (Spinner) findViewById(R.id.spinnerOrientation);
                int orientationIndex = orient.getSelectedItemPosition();

                setGrid(position, accuracyIndex, orientationIndex);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //Default the grid values
        setGrid(0, 0, 0);
    }

    private void setGrid(int roomIndex, int accuracyIndex, int orientationIndex) {

        int[] newLimits = surveySettings.selectRoom(roomIndex, accuracyIndex, orientationIndex);
        assert newLimits[3] == 1 | newLimits[3] == 0 : "MyActivity.setGrid newLimits[3] is not 0 or 1";

        NumberPicker xPicker = (NumberPicker) findViewById(R.id.numberPickerXXX);
        xPicker.setMaxValue(newLimits[0]);
        xPicker.setMinValue(newLimits[3]);

        NumberPicker yPicker = (NumberPicker) findViewById(R.id.numberPickerYYY);
        yPicker.setMaxValue(newLimits[1]);
        yPicker.setMinValue(newLimits[3]);

        NumberPicker wPicker = (NumberPicker) findViewById(R.id.numberPickerWWW);
        wPicker.setMinValue(1);
        wPicker.setMaxValue(newLimits[2]);
    }

    public void updateSettings(View view) {
        if (isConnected) {
            disableInteractions();
            showToast("Retrieving Survey Settings");
            UpdateRoomInfo updateRoomInfo = new UpdateRoomInfo(this, service, FILE_DIRECTORY, ROOM_INFO_FILENAME, ROOM_INFO_MIME_TYPE);
            updateRoomInfo.execute();
        }
    }

    public void updateAll(View view) {
        if (isConnected) {
            disableInteractions();
            showToast("Uploading all files");
            showToast("This may take some time");
            ArrayList<String> filenames = new ArrayList<>();
            //find all the files in the output directory
            File dir = Environment.getExternalStoragePublicDirectory(FILE_DIRECTORY);
            Collections.addAll(filenames, dir.list());

            filenames.remove(ROOM_INFO_FILENAME);   //remove room info file from upload

            UploadFileToDrive uploadFileToDrive = new UploadFileToDrive(this, service, FILE_DIRECTORY, filenames);
            uploadFileToDrive.execute();
        }
    }

    public void requestAuthorisation(Intent intent) {
        startActivityForResult(intent, REQUEST_AUTHORIZATION_UPLOAD);
    }

    private void disableAccountButtons() {
        Button changeAccountButton = (Button) findViewById(R.id.buttonChangeAccount);
        changeAccountButton.setEnabled(false);

        Button parametersButton = (Button) findViewById(R.id.buttonSettings);
        parametersButton.setEnabled(false);

        Button uploadAllButton = (Button) findViewById(R.id.buttonUploadAll);
        uploadAllButton.setEnabled(false);
    }

    private void enableAccountButtons() {
        Button changeAccountButton = (Button) findViewById(R.id.buttonChangeAccount);
        changeAccountButton.setEnabled(true);

        if (isSignedIn) {
            Button parametersButton = (Button) findViewById(R.id.buttonSettings);
            parametersButton.setEnabled(true);

            Button uploadAllButton = (Button) findViewById(R.id.buttonUploadAll);
            uploadAllButton.setEnabled(true);
        }
    }

    private void disableInteractions() {
        //Disable scan button
        Button scanButton = (Button) findViewById(R.id.buttonScan);
        scanButton.setEnabled(false);

        //Disable upload button
        Button uploadButton = (Button) findViewById(R.id.buttonUpload);
        uploadButton.setEnabled(false);

        Spinner spinnerAccuracy = (Spinner) findViewById(R.id.spinnerAccuracy);
        spinnerAccuracy.setEnabled(false);

        Spinner spinnerOrientation = (Spinner) findViewById(R.id.spinnerOrientation);
        spinnerOrientation.setEnabled(false);

        ToggleButton buttonRSSI = (ToggleButton) findViewById(R.id.toggleButtonRSSI);
        buttonRSSI.setEnabled(false);

        ToggleButton buttonMagnetic = (ToggleButton) findViewById(R.id.toggleButtonMagnetic);
        buttonMagnetic.setEnabled(false);

        disableAccountButtons();
    }


    public void enableInteractions() {
        //Enable scan button
        Button scanButton = (Button) findViewById(R.id.buttonScan);
        scanButton.setEnabled(true);

        //Enable upload button
        if (isSignedIn) {
            Button uploadButton = (Button) findViewById(R.id.buttonUpload);
            uploadButton.setEnabled(true);
        }

        Spinner spinnerAccuracy = (Spinner) findViewById(R.id.spinnerAccuracy);
        spinnerAccuracy.setEnabled(true);

        Spinner spinnerOrientation = (Spinner) findViewById(R.id.spinnerOrientation);
        spinnerOrientation.setEnabled(true);

        ToggleButton buttonRSSI = (ToggleButton) findViewById(R.id.toggleButtonRSSI);
        buttonRSSI.setEnabled(true);

        ToggleButton buttonMagnetic = (ToggleButton) findViewById(R.id.toggleButtonMagnetic);
        buttonMagnetic.setEnabled(true);

        enableAccountButtons();
    }

    public void showUploadAllProgressBar(int maxLimit) {
        ProgressBar uploadAll = (ProgressBar) findViewById(R.id.progressBarUploadAll);
        uploadAll.setVisibility(View.VISIBLE);
        uploadAll.setMax(maxLimit);
        uploadAll.setProgress(0);
    }

    public void incUploadAllProgressBar() {
        ProgressBar uploadAll = (ProgressBar) findViewById(R.id.progressBarUploadAll);
        uploadAll.incrementProgressBy(1);
    }

    public void hideUploadAllProgressBar() {
        ProgressBar uploadAll = (ProgressBar) findViewById(R.id.progressBarUploadAll);
        uploadAll.setVisibility(View.INVISIBLE);
    }

    public boolean checkConnectivity() {

        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork.isConnected();
    }

}
