package me.gregalbiston.androidknn.dataload;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.hardware.SensorEvent;
import android.net.wifi.ScanResult;
import android.widget.TextView;
import datastorage.KNNFloorPoint;
import datastorage.Location;
import datastorage.MagneticData;
import datastorage.RSSIData;
import datastorage.RoomInfo;
import general.Point;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import me.gregalbiston.androidknn.R;
import me.gregalbiston.androidknn.VisActivity;
import me.gregalbiston.androidknn.datacollection.MagneticScanner;
import me.gregalbiston.androidknn.datacollection.RSSIScanner;
import me.gregalbiston.androidknn.datacollection.ResultsInfo;
import me.gregalbiston.androidknn.display.FloorView;
import me.gregalbiston.androidknn.display.Grid;
import me.gregalbiston.androidknn.display.PointResults;
import me.gregalbiston.androidknn.logging.ErrorLog;
import me.gregalbiston.androidknn.logging.OutputLog;

/**
 * Created with IntelliJ IDEA.
 * User: Greg Albiston
 * Date: 28/07/13
 * Time: 19:32
 * Controller class to hold retrieved preferences and loaded data.
 */
public class DataManager {

    protected HashMap<String, KNNFloorPoint> rssiRadioMap;
    protected HashMap<String, KNNFloorPoint> magneticRadioMap;
    protected HashMap<String, RoomInfo> roomInfo;
    protected List<String> filterSSIDs;

    protected FloorView floorView;

    protected Grid grid = null;
    protected PointResults rssiPointResults = null;
    protected PointResults magneticPointResults = null;

    protected Drawable floorPlan;

    protected OutputLog rssiLog;
    protected OutputLog magneticLog;

    protected ErrorLog errorLog;

    protected SettingsController settingsController;

    public DataManager(Context context, TextView logTextView, FloorView floorView) {

        settingsController = new SettingsController(this, context);

        setupLogs(context, logTextView);

        this.floorView = floorView;

        loadData();

        floorPlan = DataLoad.loadFloor(VisActivity.FILE_DIRECTORY, VisActivity.FLOOR_PLAN_FILE);
        if (floorPlan != null) {
            floorView.setFloorPlan(floorPlan);
        } else {
            errorLog.printLogLine(R.string.nofloorplan_message);
        }

        setupFloorView();
    }

    private void setupLogs(Context context, TextView logTextView) {

        logTextView.setText(""); //clear intial contents from the on-screen log

        this.errorLog = new ErrorLog(context, logTextView, settingsController.isShowLog);
        errorLog.printLogLine(R.string.app_start);

        String rawHeadings = RSSIData.toStringHeadings(VisActivity.FIELD_SEPARATOR);
        this.rssiLog = new OutputLog(context, logTextView, settingsController.isShowLog, "RSSI", rawHeadings);

        rawHeadings = MagneticData.toStringHeadings(VisActivity.FIELD_SEPARATOR);
        this.magneticLog = new OutputLog(context, logTextView, settingsController.isShowLog, "Magnetic", rawHeadings);
    }

    public void start(Context context, TextView logTextView, FloorView floorView) {

        this.floorView = floorView;
        errorLog.restart(context, logTextView, settingsController.isShowLog);
        rssiLog.restart(context, logTextView, settingsController.isShowLog);
        magneticLog.restart(context, logTextView, settingsController.isShowLog);
        setupFloorView();
    }

    public void stop() {
        errorLog.close();
        rssiLog.close();
        magneticLog.close();
    }

    public void newRoute() {

        rssiLog.newRoute("RSSI", RSSIData.toStringHeadings(VisActivity.FIELD_SEPARATOR));
        magneticLog.newRoute("Magnetic", MagneticData.toStringHeadings(VisActivity.FIELD_SEPARATOR));
        rssiPointResults.newRoute();
        magneticPointResults.newRoute();
        floorView.invalidate();
        errorLog.printLogLine(R.string.new_route);
    }

    private void loadData() {

        //load raw data from file and convert to usable format - store for future use in compressed form.

        //load RSSI data
        rssiRadioMap = DataLoad.loadData(VisActivity.FILE_DIRECTORY, VisActivity.KNN_DATA_FILE_RSSI);

        //load Magnetic data
        magneticRadioMap = DataLoad.loadData(VisActivity.FILE_DIRECTORY, VisActivity.KNN_DATA_FILE_MAGNETIC);

        if (rssiRadioMap == null) {
            errorLog.printLogLine(R.string.rssi_error_message);
        }

        if (magneticRadioMap == null) {
            errorLog.printLogLine(R.string.magnetic_error_message);
        }
        if (rssiRadioMap == null && magneticRadioMap == null) {
            errorLog.printLogLine(R.string.nodata_message);
            errorLog.close();
            rssiLog.close();
            magneticLog.close();
            throw new AssertionError(R.string.nodata_message);
        }

        //load room info
        //roomInfo = DataLoad.loadRoom(this, ROOM_INFO_FILE);
        roomInfo = DataLoad.loadRoom(VisActivity.FILE_DIRECTORY, VisActivity.ROOM_INFO_FILE, VisActivity.FIELD_SEPARATOR);

        if (roomInfo == null) {
            errorLog.printLogLine(R.string.noroominfo_message);
        }

        filterSSIDs = DataLoad.loadFilterSSID(VisActivity.FILE_DIRECTORY, VisActivity.FILTER_SSID_FILE);
        if (filterSSIDs == null) {
            errorLog.printLogLine(R.string.nofilter_message);
            filterSSIDs = new ArrayList<>();
        }
    }


    protected void showType(boolean isShowRSSI) {
        if (isShowRSSI)
            floorView.setPointResults(rssiPointResults);
        else
            floorView.setPointResults(magneticPointResults);

    }

    public void showLog(boolean isShowLog) {
        errorLog.showLog(isShowLog);
        rssiLog.showLog(isShowLog);
        magneticLog.showLog(isShowLog);
    }

    public void showPath(boolean isShowPath) {
        rssiPointResults.showPath(isShowPath);
        magneticPointResults.showPath(isShowPath);
        floorView.invalidate();
    }

    public void showEstimates(boolean isShowEstimates) {
        rssiPointResults.showEstimates(isShowEstimates);
        magneticPointResults.showEstimates(isShowEstimates);
        floorView.invalidate();
    }

    public void showGrid(boolean isShowGrid) {
        grid.showGrid(isShowGrid);
        floorView.invalidate();
    }

    private void setupFloorView() {

        if (grid == null) {

            grid = new Grid();

            if (settingsController.isShowRSSI) {
                if (roomInfo != null && rssiRadioMap != null)
                    grid = new Grid(rssiRadioMap, roomInfo, settingsController.isShowGrid, settingsController.colourGrid);
            } else {
                if (roomInfo != null && magneticRadioMap != null)
                    grid = new Grid(magneticRadioMap, roomInfo, settingsController.isShowGrid, settingsController.colourGrid);
            }
        }

        floorView.setGrid(grid);

        if (rssiPointResults == null) {
            rssiPointResults = new PointResults(settingsController.isShowGrid, settingsController.isShowPath, settingsController.colourFinal, settingsController.colourScan, settingsController.colourEstimates);
        }

        if (magneticPointResults == null) {
            magneticPointResults = new PointResults(settingsController.isShowGrid, settingsController.isShowPath, settingsController.colourFinal, settingsController.colourScan, settingsController.colourEstimates);
        }

        if (settingsController.isShowRSSI)
            floorView.setPointResults(rssiPointResults);
        else
            floorView.setPointResults(magneticPointResults);

        if (floorPlan != null) {
            floorView.setFloorPlan(floorPlan);
        }
    }

    public void setPointResultsColours(int colourFinal, int colourScan, int colourEstimates) {
        rssiPointResults.setColours(colourFinal, colourScan, colourEstimates);
        magneticPointResults.setColours(colourFinal, colourScan, colourEstimates);
        floorView.invalidate();
    }

    public void setGridColour(int colourGrid) {
        grid.setColour(colourGrid);
        floorView.invalidate();
    }

    public HashMap<String, KNNFloorPoint> getRSSIRadioMap() {
        return rssiRadioMap;
    }

    public HashMap<String, KNNFloorPoint> getMagneticRadioMap() {
        return magneticRadioMap;
    }

    public HashMap<String, RoomInfo> getRoomInfo() {
        return roomInfo;
    }

    public SettingsController getSettingsController() {
        return settingsController;
    }

    public List<String> getFilterSSIDs() {
        return filterSSIDs;
    }

    public void rssiScanResult(List<ScanResult> scanResults, Location location, Point screenPoint) {

        outputRawRSSIData(scanResults, location);
        ResultsInfo results = RSSIScanner.processResultsKNNDet(scanResults, location, screenPoint, this);
        rssiPointResults.add(results);
        floorView.invalidate();

        rssiLog.printLogLine(results.message);
    }

    //Convert ScanResult to RSSIData format and send to file
    private void outputRawRSSIData(List<ScanResult> scanResults, Location location) {
        long timestamp = System.currentTimeMillis();
        for (ScanResult scan : scanResults) {
            RSSIData rssiData = new RSSIData(timestamp, location, scan.BSSID, scan.SSID, scan.level, scan.frequency);
            rssiLog.printRawLine(rssiData.toString(VisActivity.FIELD_SEPARATOR));
        }
    }

    public void magneticScanResult(SensorEvent event, Location location, Point screenPoint) {

        outputRawMagneticData(event, location);
        ResultsInfo results = MagneticScanner.processResult(event, location, screenPoint, this);
        magneticPointResults.add(results);
        floorView.invalidate();

        magneticLog.printLogLine(results.message);
    }

    //Convert ScanResult to RSSIData format and send to file
    private void outputRawMagneticData(SensorEvent event, Location location) {
        long timestamp = System.currentTimeMillis();

        float[] values = event.values;
        MagneticData magneticData = new MagneticData(timestamp, location, values[0], values[1], values[2], event.accuracy);
        magneticLog.printRawLine(magneticData.toString(VisActivity.FIELD_SEPARATOR));
    }

     /*
    private FloorPoint testFloorPoint(){

        Location testLocation = new Location("CIB208",5,5,1);
        FloorPoint testPoint = new FloorPoint(testLocation);
        testPoint.add("00:13:5f:f9:f0:01", -66);
        testPoint.add("00:13:5f:f9:f0:00", -66);
        testPoint.add("00:13:5f:f9:f0:02", -66);
        testPoint.add("00:13:5f:f9:f0:03", -66);
        testPoint.add("00:13:5f:f9:f0:06", -67);
        testPoint.add("00:13:5f:f9:f0:07", -67);
        testPoint.add("00:13:5f:f9:f0:05", -66);
        testPoint.add("00:13:5f:f9:f0:04", -66);
        testPoint.add("ac:86:74:05:13:1b", -95);


        //37:59.3,CIB208,5,5,1,00:13:5f:f9:f0:01,ntu-student,-66,1
        //37:59.3,CIB208,5,5,1,00:13:5f:f9:f0:00,ntu-staff,-66,1
        //37:59.3,CIB208,5,5,1,00:13:5f:f9:f0:02,eduroam,-66,1
        //37:59.3,CIB208,5,5,1,00:13:5f:f9:f0:03,ntu-mobile,-66,1
        //37:59.3,CIB208,5,5,1,00:13:5f:f9:f0:06,,-67,1
        //37:59.3,CIB208,5,5,1,00:13:5f:f9:f0:07,,-67,1
        //37:59.3,CIB208,5,5,1,00:13:5f:f9:f0:05,ntu-wirelessHelp,-66,1
        //37:59.3,CIB208,5,5,1,00:13:5f:f9:f0:04,ntu-visitor,-66,1
        //37:59.3,CIB208,5,5,1,ac:86:74:05:13:1b,Blackwells Private,-95,5


        return testPoint;
    }
     */

}
