package me.gregalbiston.androiddetector.scan;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import datastorage.Location;
import datastorage.RSSIData;
import general.TimeStamp;
import java.util.ArrayList;
import java.util.List;
import me.gregalbiston.androiddetector.DectectorActivity;
import me.gregalbiston.androiddetector.R;
import me.gregalbiston.androiddetector.storage.FileOutput;

/**
 * RSSI Scanner begins scanning at end of constructor.
 * BroadcastReceiver continues to scan as fast as possible.
 * BroadcastReceiver unregisters itself once sampling stops.
 * To enable as fast as possible scanning, ScanResult's are added to a list with system time and processed as part of finish operation.
 * Based on: https://groups.google.com/forum/#!topic/android-developers/D3ItX5DPrmk
 * 
 *@author Greg Albiston
 */
public class RSSIScanner {

    private final WifiManager wifiManager;
    //private List<ScanResult> scanResults = new ArrayList<ScanResult>();
    private final List<RSSIResult> rssiResults = new ArrayList<>();
    private final ListView rssiListView;

    private final Location scanLocation;

    private boolean isSampling = true;

    public RSSIScanner(DectectorActivity dectectorActivity, Location scanLocation) {
        this.scanLocation = scanLocation;

        rssiListView = (ListView) dectectorActivity.findViewById(R.id.listViewRSSI);

        displayHoldingMessage(dectectorActivity);

        setupReceiver(dectectorActivity);

        wifiManager = (WifiManager) dectectorActivity.getSystemService(Context.WIFI_SERVICE);
        wifiManager.startScan();

    }

    private void setupReceiver(DectectorActivity dectectorActivity) {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);

        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                if (isSampling) {
                    rssiResults.add(new RSSIResult(System.currentTimeMillis(), wifiManager.getScanResults()));
                    wifiManager.startScan();
                } else {
                    context.unregisterReceiver(this);
                }
            }
        };

        dectectorActivity.registerReceiver(broadcastReceiver, intentFilter);
    }

    public void finish(FileOutput fileOutput) {
        //Stop broadcast receiver processing updates.
        isSampling = false;

        ArrayList<RSSIData> results = new ArrayList<>();
        ArrayList<String> screenResults = new ArrayList<>();
        //Iterate over results to display as strings
        for (RSSIResult rssiResult : rssiResults) {
            long timestamp = rssiResult.getTimestamp();
            List<ScanResult> scanResults = rssiResult.getResults();

            for (ScanResult scan : scanResults) {
                int channel = ((scan.frequency - 2412) / 5) + 1;  //calculates accurately for channels 1-13. Channel 14 is not generally available.
                results.add(new RSSIData(timestamp, scanLocation, scan.BSSID, scan.SSID, scan.level, channel));
                screenResults.add(TimeStamp.formatShortDateTime(timestamp) + " " + scanLocation.toString() + DectectorActivity.OUTPUT_SEPARATOR + scan.BSSID + DectectorActivity.OUTPUT_SEPARATOR + scan.SSID + DectectorActivity.OUTPUT_SEPARATOR + scan.level + DectectorActivity.OUTPUT_SEPARATOR + channel);
            }
        }

        //Display results
        Context context = rssiListView.getContext();
        ArrayAdapter resultsAdapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, screenResults);
        rssiListView.setAdapter(resultsAdapter);

        fileOutput.outputRSSIFile(results, DectectorActivity.FILE_DIRECTORY, DectectorActivity.OUTPUT_FILENAME_RSSI, DectectorActivity.OUTPUT_SEPARATOR, DectectorActivity.OUTPUT_MIME_TYPE, context);
    }

    private void displayHoldingMessage(DectectorActivity dectectorActivity) {
        //On-screen message and clear previous content
        ArrayList<String> message = new ArrayList<>();
        String msg = dectectorActivity.getResources().getString(R.string.labelRSSIStart);
        message.add(msg);
        ArrayAdapter messageAdapter = new ArrayAdapter<>(dectectorActivity, android.R.layout.simple_list_item_1, message);
        rssiListView.setAdapter(messageAdapter);
    }


}
