package me.gregalbiston.androidknn.datacollection;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import datastorage.KNNFloorPoint;
import datastorage.Location;
import datastorage.ResultLocation;
import filehandling.RoomInfo;
import general.Locate;
import general.Point;
import java.util.ArrayList;
import java.util.List;
import me.gregalbiston.androidknn.VisActivity;
import me.gregalbiston.androidknn.dataload.DataManager;
import me.gregalbiston.androidknn.dataload.SettingsController;
import processing.LogResults;
import processing.Positioning;
import processing.PositioningSettings;

/**
 * Created with IntelliJ IDEA.
 * User: Gerg
 * Date: 22/07/13
 * Time: 14:08
 * Starts scan and registers broadcast receiver.
 * Processes the results that are passed back and returns ResultsInfo with drawing points and log text.
 * Based on: https://groups.google.com/forum/#!topic/android-developers/D3ItX5DPrmk
 */
public class RSSIScanner {

    public static void scan(final VisActivity visActivity, final Location location, final Point screenPoint) {
        final WifiManager wifiManager = (WifiManager) visActivity.getSystemService(Context.WIFI_SERVICE);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);


        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                visActivity.rssiScanResult(wifiManager.getScanResults(), location, screenPoint);
                visActivity.unregisterReceiver(this);
            }
        };

        visActivity.registerReceiver(broadcastReceiver, intentFilter);
        wifiManager.startScan();
    }

    public static ResultsInfo processResultsKNNDet(List<ScanResult> scanResults, Location location, Point screenPoint, DataManager dataManager) {

        SettingsController settingsController = dataManager.getSettingsController();

        //Obtain unfiltered version
        KNNFloorPoint scanPointUnfiltered = convertToFloorPoint(scanResults, location);

        //Filter by SSID if required
        KNNFloorPoint scanPoint;
        if (settingsController.getFilterSSID())
            scanPoint = convertToFloorPointFiltered(scanResults, location, dataManager.getFilterSSIDs());
        else
            scanPoint = scanPointUnfiltered;

        List<ResultLocation> estimates;
        Point finalPoint;

        //skip positioning process if ssid filter has removed all bssids
        if (!scanPoint.getAttributes().isEmpty()) {

            // find list of estimates
            estimates = Positioning.estimate(scanPoint, dataManager.getRSSIRadioMap(), settingsController.getDistMeasure());

            //find the nearest neighbours
            if (settingsController.getVariance())
                estimates = Positioning.nearestVarianceEstimates(scanPoint, estimates, settingsController.getkLimit(), settingsController.getVarLimit(), settingsController.getVarCount(), dataManager.getRSSIRadioMap());
            else
                estimates = Positioning.nearestEstimates(estimates, settingsController.getkLimit());

            //TODO Check whether this should be weighted or unweighted.
            //determine the onscreen point based on nearest estimates
            finalPoint = Locate.findUnweightedCentre(estimates, dataManager.getRoomInfo());
        } else {
            estimates = new ArrayList<>();
            finalPoint = new Point(-1, -1);
        }

        //Store the screen points for the estimates used in the final positioning
        List<Point> estimatePoints = new ArrayList<>();
        for (ResultLocation estimate : estimates) {
            estimatePoints.add(RoomInfo.searchPoint(estimate, dataManager.getRoomInfo()));
        }

        //transfer the current settings
        PositioningSettings positioningSettings;
        if (settingsController.getVariance())
            positioningSettings = new PositioningSettings(System.currentTimeMillis(), settingsController.getDistMeasure(), settingsController.getkLimit(), settingsController.getVarLimit(), settingsController.getVarCount());
        else
            positioningSettings = new PositioningSettings(System.currentTimeMillis(), settingsController.getDistMeasure(), settingsController.getkLimit());

        //only supply SSID filter list when the setting is on
        String message;
        if (settingsController.getFilterSSID())
            message = LogResults.logRSSI(scanPoint, screenPoint, estimates, estimatePoints, finalPoint, scanPointUnfiltered, dataManager.getFilterSSIDs(), positioningSettings);
        else
            message = LogResults.logRSSI(scanPoint, screenPoint, estimates, estimatePoints, finalPoint, scanPointUnfiltered, new ArrayList<String>(), positioningSettings);

        //process the results for the log
        return new ResultsInfo(screenPoint, finalPoint, estimatePoints, message);
    }


    //filter out access points which don't have the required SSID
    private static KNNFloorPoint convertToFloorPointFiltered(List<ScanResult> results, Location location, List<String> filterSSIDs) {

        KNNFloorPoint scanPoint = new KNNFloorPoint(location);
        for (ScanResult result : results) {
            if (filterSSIDs.contains(result.SSID)) {
                scanPoint.add(result.BSSID, result.level);
            }
        }

        return scanPoint;
    }

    private static KNNFloorPoint convertToFloorPoint(List<ScanResult> results, Location location) {

        KNNFloorPoint scanPoint = new KNNFloorPoint(location);

        for (ScanResult result : results) {
            scanPoint.add(result.BSSID, result.level);
        }

        return scanPoint;
    }

}
