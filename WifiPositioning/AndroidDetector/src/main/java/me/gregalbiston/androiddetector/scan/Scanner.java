package me.gregalbiston.androiddetector.scan;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.Spinner;
import datastorage.Location;
import me.gregalbiston.androiddetector.DetectorActivity;
import me.gregalbiston.androiddetector.R;

public class Scanner extends CountDownTimer {

    private final MagneticScanner magneticScanner;
    private final RSSIScanner RSSIScanner;

    private ProgressBar progressBar;

    //private ScanDetails scanDetails;
    private Location scanLocation;

    private final DetectorActivity dectectorActivity;
    private final ScanSettings scanSettings;


    public Scanner(int duration, int frequency, DetectorActivity dectectorActivity, ScanSettings scanSettings) {
        super((duration * 1000) + 500, 1000 / frequency);       //add an extra half second to the duration to allow results to come back

        this.scanSettings = scanSettings;

        setupScanDetails(dectectorActivity);
        setupProgressBar(dectectorActivity, duration * frequency);

        if (scanSettings.isMagneticSelected())
            magneticScanner = new MagneticScanner(dectectorActivity, scanLocation);
        else
            magneticScanner = null;

        WifiManager wifiManager = (WifiManager) dectectorActivity.getSystemService(Context.WIFI_SERVICE);


        wifiManager.setWifiEnabled(true);
        RSSIScanner = new RSSIScanner(dectectorActivity, scanLocation);

        this.dectectorActivity = dectectorActivity;
    }

    @Override
    public void onTick(long millisUntilFinished) {
        progressBar.incrementProgressBy(1);

        //RSSI scanner samples as fast as possible. It is too slow otherwise.

        //Magnetic scanner on samples when required. It is too fast otherwise.
        if (scanSettings.isMagneticSelected())
            magneticScanner.setSampling();
    }

    @Override
    public void onFinish() {
        progressBar.setVisibility(View.INVISIBLE);

        if (scanSettings.isRSSISelected()) {
            RSSIScanner.finish(scanSettings.getFileOutput());
        }

        if (scanSettings.isMagneticSelected()) {
            magneticScanner.finish(scanSettings.getFileOutput());
        }

        dectectorActivity.enableInteractions();

    }

    private void setupProgressBar(DetectorActivity dectectorActivity, int limit) {
        progressBar = (ProgressBar) dectectorActivity.findViewById(R.id.progressBarScan);
        progressBar.setVisibility(View.VISIBLE);
        progressBar.setProgress(0);
        progressBar.setMax(limit);
    }

    private void setupScanDetails(DetectorActivity dectectorActivity) {
    
        Spinner room = (Spinner) dectectorActivity.findViewById(R.id.spinnerRooms);
        NumberPicker xPicker = (NumberPicker) dectectorActivity.findViewById(R.id.numberPickerXXX);
        String[] xDisplayedValues = xPicker.getDisplayedValues();
        int xValue = Integer.parseInt(xDisplayedValues[xPicker.getValue()]);
        
        NumberPicker yPicker = (NumberPicker) dectectorActivity.findViewById(R.id.numberPickerYYY);
        String[] yDisplayedValues = yPicker.getDisplayedValues();
        int yValue = Integer.parseInt(yDisplayedValues[yPicker.getValue()]);
        
        NumberPicker wPicker = (NumberPicker) dectectorActivity.findViewById(R.id.numberPickerWWW);
        String[] wDisplayedValues = wPicker.getDisplayedValues();
        int wValue = Integer.parseInt(wDisplayedValues[wPicker.getValue()]);

        scanLocation = new Location(room.getSelectedItem().toString(), xValue, yValue, wValue, 0, 0, 0, 0);
    }

}
