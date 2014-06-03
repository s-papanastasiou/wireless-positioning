package androidparticlefilter;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.ToggleButton;
import datastorage.KNNFloorPoint;
import java.util.HashMap;
import particlefilterlibrary.NavigationResults;

/**
 * Main android activity for the application.
 *
 * @author Pierre Rousseau
 */
public class CompassActivity extends Activity {

    private SensorReadings sensorReadings;

    private HashMap<String, KNNFloorPoint> offlineMap;
    private Drawable floorPlan;
    private Visualisation visualisation;

    private static final String OFFLINE_MAP_FILE = "RSSIResults.csv";
    private static final String FLOOR_PLAN_FILE = "floor2final.png";
    private static final String FILE_DIRECTORY = "SensorPlanA";
    public static final String LOG_FILENAME = "CompassALog.csv";
    public final static String OFFLINE_COMPRESSED = "OfflineCompressed.knn";

    private final AppSettings appSettings = new AppSettings(false, true, 5, 10, 25, true, -0.523598776, 0.3, 0.1, 0.3, new Float[]{0.005f, 0.03f, -0.17f}, 40, Thresholds.boundaries(), Thresholds.particleCreation());

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Android onCreate.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        loadFiles();
    }

    /**
     * Android onStop.
     */
    @Override
    public void onStop() {
        super.onStop();
        ResultsLogging.stopLog(this);
    }

    /**
     * Load files from storage.
     * @return 
     */
    private boolean loadFiles() {

        boolean isSuccess = false;

        visualisation = (Visualisation) findViewById(R.id.view);

        offlineMap = FileController.loadOfflineMap(FILE_DIRECTORY, OFFLINE_MAP_FILE, ";", appSettings.isBSSIDMerged(), appSettings.isOrientationMerged());
        floorPlan = FileController.loadFloor(FILE_DIRECTORY, FLOOR_PLAN_FILE);
        if (floorPlan != null) {
            visualisation.setFloorPlan(floorPlan);
            isSuccess = true;
        }

        ResultsLogging.printLine("Log started");

        return isSuccess;
    }
/**
 * Sensor button handler.
 * @param v
 * @throws InterruptedException 
 */
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

    /**
     * Moving button handler.
     * @param v 
     */
    public void onMovingButtonClick(View v) {
        ToggleButton buttonMoving = (ToggleButton) v;
        if (buttonMoving.isChecked()) {
            ResultsLogging.printLine("Started moving");
        } else {
            ResultsLogging.printLine("Stopped moving");
        }

    }

    /**
     * Android onResume.
     */
    @Override
    protected void onResume() {
        super.onResume();
    }
/**
 * Android onPause.
 */
    @Override
    protected void onPause() {
        super.onPause();
    }

    /**
     * Display values on screen from each sample.
     * @param navigationResults 
     */
    public void showValues(NavigationResults navigationResults) {

        visualisation.setPoint(navigationResults.probabilisticPoint, navigationResults.particlePoint, navigationResults.inertialPoint, navigationResults.bestPoint);

        TextView datasView = (TextView) findViewById(R.id.datasView);
        datasView.setText(navigationResults.toString());
        ResultsLogging.printLine(navigationResults.toString());
    }
}
