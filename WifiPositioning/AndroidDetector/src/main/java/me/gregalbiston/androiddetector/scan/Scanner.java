package me.gregalbiston.androiddetector.scan;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.Spinner;
import datastorage.Location;
import me.gregalbiston.androiddetector.DectectorActivity;
import me.gregalbiston.androiddetector.R;

public class Scanner extends CountDownTimer {

    private final MagneticScanner magneticScanner;
    private final RSSIScanner RSSIScanner;

    private ProgressBar progressBar;

    //private ScanDetails scanDetails;
    private Location scanLocation;

    private final DectectorActivity dectectorActivity;
    private final ScanSettings scanSettings;


    public Scanner(int duration, int frequency, DectectorActivity dectectorActivity, ScanSettings scanSettings) {
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

    private void setupProgressBar(DectectorActivity dectectorActivity, int limit) {
        progressBar = (ProgressBar) dectectorActivity.findViewById(R.id.progressBarScan);
        progressBar.setVisibility(View.VISIBLE);
        progressBar.setProgress(0);
        progressBar.setMax(limit);
    }

    private void setupScanDetails(DectectorActivity dectectorActivity) {
        Spinner room = (Spinner) dectectorActivity.findViewById(R.id.spinnerRooms);
        NumberPicker xValue = (NumberPicker) dectectorActivity.findViewById(R.id.numberPickerXXX);
        NumberPicker yValue = (NumberPicker) dectectorActivity.findViewById(R.id.numberPickerYYY);
        NumberPicker wValue = (NumberPicker) dectectorActivity.findViewById(R.id.numberPickerWWW);


        scanLocation = new Location(room.getSelectedItem().toString(), xValue.getValue(), yValue.getValue(), wValue.getValue());
    }


}
