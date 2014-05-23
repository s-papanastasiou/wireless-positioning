package me.gregalbiston.androidparticlefilter;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.ToggleButton;
import datastorage.KNNFloorPoint;

import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: pierre
 * Date: 29/09/13
 * Time: 13:31
 * To change this template use File | Settings | File Templates.
 */

public class CompassActivity extends Activity {

    private SensorReadings sensorReadings;

    protected HashMap<String, KNNFloorPoint> offlineMap;
    protected Drawable floorPlan;
    protected Visualisation visualisation;

    private static final String OFFLINE_MAP_FILE = "RSSIResults.csv";
    private static final String FLOOR_PLAN_FILE = "floor2final.png";
    private static final String FILE_DIRECTORY = "SensorPlanA";
    public static final String LOG_FILENAME = "CompassALog.csv";

    private final AppSettings appSettings = new AppSettings(false, true, 5, 10, 25, true, -0.523598776);

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        loadFiles();
    }

    @Override
    public void onStop() {
        super.onStop();
        Logging.stopLog(this);
    }

    private boolean loadFiles() {

        boolean isSuccess = false;

        visualisation = (Visualisation) findViewById(R.id.view);

        offlineMap = FileController.loadOfflineMap(FILE_DIRECTORY, OFFLINE_MAP_FILE, ";", appSettings.isBSSIDMerged(), appSettings.isOrientationMerged());
        floorPlan = FileController.loadFloor(FILE_DIRECTORY, FLOOR_PLAN_FILE);
        if (floorPlan != null) {
            visualisation.setFloorPlan(floorPlan);
            isSuccess = true;
        }

        Logging.printLine("Log started");

        return isSuccess;
    }

    public void onSensorButtonClick(View v) throws InterruptedException {

        ToggleButton buttonOnOff = (ToggleButton) v;
        if (buttonOnOff.isChecked()) {
            sensorReadings = new SensorReadings(this, appSettings, offlineMap);
            sensorReadings.execute();
            TextView datasView = (TextView) findViewById(R.id.datasView);
            datasView.setText("Initialising");
            visualisation.clear();

        } else {
            sensorReadings.cancel(false);
        }
    }

    public void onMovingButtonClick(View v) {
        ToggleButton buttonMoving = (ToggleButton) v;
        if (buttonMoving.isChecked()) {
            Logging.printLine("Started moving");
        } else {
            Logging.printLine("Stopped moving");
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    public void showValues(NavigationResults navigationResults) {

        visualisation.setPoint(navigationResults.probabilisticPoint, navigationResults.particlePoint, navigationResults.inertialPoint, navigationResults.bestPoint);

        TextView datasView = (TextView) findViewById(R.id.datasView);
        datasView.setText(navigationResults.toString());
        Logging.printLine(navigationResults.toString());
    }
}