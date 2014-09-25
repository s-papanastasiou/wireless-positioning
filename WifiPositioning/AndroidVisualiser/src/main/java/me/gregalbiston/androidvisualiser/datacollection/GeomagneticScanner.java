package me.gregalbiston.androidvisualiser.datacollection;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import datastorage.GeomagneticData;
import datastorage.KNNFloorPoint;
import datastorage.Location;
import datastorage.ResultLocation;
import general.Locate;
import general.Point;
import java.util.List;

import general.ResultPoint;
import me.gregalbiston.androidvisualiser.VisActivity;
import me.gregalbiston.androidvisualiser.dataload.DataManager;
import me.gregalbiston.androidvisualiser.dataload.SettingsController;
import processing.LogResults;
import processing.Positioning;
import processing.PositioningSettings;

/**
 * Created with IntelliJ IDEA.
 * User: Greg Albiston
 * Date: 03/08/13
 * Time: 12:09
 * Starts scan and registers event listener.
 * Processes the results which are passed back and returns ResultsInfo with drawing points and log text.
 */
public class GeomagneticScanner {

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

        // find list of estimates
        estimates = Positioning.estimate(scanPoint, dataManager.getGeomagneticRadioMap(), settingsController.getDistMeasure());

        //find the nearest neighbours
        if (settingsController.getVariance())
            estimates = Positioning.nearestVarianceEstimates(scanPoint, estimates, settingsController.getkLimit(), settingsController.getVarLimit(), settingsController.getVarCount(), dataManager.getGeomagneticRadioMap());
        else
            estimates = Positioning.nearestEstimates(estimates, settingsController.getkLimit());

        //determine the onscreen point based on nearest estimates
        ResultPoint finalPoint = Locate.findInvertedWeightedCentre(estimates, false);

        //transfer the current settings
        PositioningSettings positioningSettings;
        if (settingsController.getVariance())
            positioningSettings = new PositioningSettings(System.currentTimeMillis(), settingsController.getDistMeasure(), settingsController.getkLimit(), settingsController.getVarLimit(), settingsController.getVarCount());
        else
            positioningSettings = new PositioningSettings(System.currentTimeMillis(), settingsController.getDistMeasure(), settingsController.getkLimit());

        //only supply SSID filter list when the setting is on
        String message = LogResults.logMagnetic(scanPoint, screenPoint, estimates, finalPoint, positioningSettings);

        //process the results for the log
        return new ResultsInfo(screenPoint, finalPoint, estimates, message);
    }

    private static KNNFloorPoint convertToFloorPoint(SensorEvent event, Location location) {

        float values[] = event.values;

        return new KNNFloorPoint(location, GeomagneticData.X_KEY, (double)values[0], GeomagneticData.Y_KEY, (double)values[1], GeomagneticData.Z_KEY, (double)values[2]);
    }

}
