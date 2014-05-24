package me.gregalbiston.androidknn.datacollection;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import datastorage.KNNFloorPoint;
import datastorage.Location;
import datastorage.MagneticData;
import datastorage.ResultLocation;
import filehandling.RoomInfo;
import general.Locate;
import general.Point;
import processing.LogResults;
import processing.Positioning;
import processing.PositioningSettings;
import java.util.ArrayList;
import java.util.List;
import me.gregalbiston.androidknn.VisActivity;
import me.gregalbiston.androidknn.dataload.DataManager;
import me.gregalbiston.androidknn.dataload.SettingsController;

/**
 * Created with IntelliJ IDEA.
 * User: Gerg
 * Date: 03/08/13
 * Time: 12:09
 * Starts scan and registers event listener.
 * Processes the results which are passed back and returns ResultsInfo with drawing points and log text.
 */
public class MagneticScanner {

    public static void scan(final VisActivity visActivity, final Location location, final Point screenPoint) {

        //Attach listener for sensor events
        final SensorManager sensorManager = (SensorManager) visActivity.getSystemService(Context.SENSOR_SERVICE);
        Sensor magneticSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        sensorManager.registerListener(new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {

                sensorManager.unregisterListener(this);
                visActivity.magneticScanResult(event, location, screenPoint);
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        }, magneticSensor, SensorManager.SENSOR_DELAY_FASTEST);
    }

    public static ResultsInfo processResult(SensorEvent event, Location location, Point screenPoint, DataManager dataManager) {

        SettingsController settingsController = dataManager.getSettingsController();

        //Filter by SSID if required
        KNNFloorPoint scanPoint = convertToFloorPoint(event, location);

        List<ResultLocation> estimates;
        Point finalPoint;

        // find list of estimates
        estimates = Positioning.estimate(scanPoint, dataManager.getMagneticRadioMap(), settingsController.getDistMeasure());

        //find the nearest neighbours
        if (settingsController.getVariance())
            estimates = Positioning.nearestVarianceEstimates(scanPoint, estimates, settingsController.getkLimit(), settingsController.getVarLimit(), settingsController.getVarCount(), dataManager.getMagneticRadioMap());
        else
            estimates = Positioning.nearestEstimates(estimates, settingsController.getkLimit());

        //determine the onscreen point based on nearest estimates
        finalPoint = Locate.findUnweightedCentre(estimates, dataManager.getRoomInfo());

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
        String message = LogResults.logMagnetic(scanPoint, screenPoint, estimates, estimatePoints, finalPoint, positioningSettings);

        //process the results for the log
        return new ResultsInfo(screenPoint, finalPoint, estimatePoints, message);
    }

    private static KNNFloorPoint convertToFloorPoint(SensorEvent event, Location location) {

        float values[] = event.values;

        return new KNNFloorPoint(location, MagneticData.X_Key, values[0], MagneticData.Y_Key, values[1], MagneticData.Z_Key, values[2]);
    }

}
