package androidparticlefilter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import datastorage.KNNFloorPoint;
import datastorage.Location;
import datastorage.RoomInfo;
import general.Locate;
import general.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import particlefilterlibrary.Cloud;
import particlefilterlibrary.InertialData;
import particlefilterlibrary.InertialPoint;
import particlefilterlibrary.NavigationResults;
import particlefilterlibrary.Particle;
import particlefilterlibrary.ParticleFilter;
import distancealgorithms.Probabilistic;

/**
 * Asynchronous task and Sensor Event Listener to process sensor readings as
 * they become available.
 *
 * @author Pierre Rousseau
 */
public class SensorReadings extends AsyncTask<String, NavigationResults, Void> implements SensorEventListener {

    private final CompassActivity compassActivity;
    private final List<Location> initialPoints = new ArrayList<>();
    private InertialPoint inertialPoint = null;
    boolean isInitialising = true;
    private Integer deviceOrientation = null;      //Orientation direction for filtering offline map
    private final HashMap<String, KNNFloorPoint> offlineMap;
    private final HashMap<String, RoomInfo> roomInfo;    
    private final WifiManager wifiManager;
    private final SensorManager mSensorManager;
    private final Sensor linearAcceleration;
    private final Sensor magnetometer;
    private final Sensor gravity;
    private Cloud cloud = null;
    private float[] mLinearAcceleration = null;
    private float[] mGeomagnetic = null;
    private float[] mGravity = null;
    private List<ScanResult> scanResults;
    private boolean isWifiResultsReady = false;

    private final AppSettings appSettings;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Constructor
     *
     * @param compassActivity Main screen activity to publish result.
     * @param appSettings
     * @param offlineMap
     */
    public SensorReadings(CompassActivity compassActivity, AppSettings appSettings, HashMap<String, KNNFloorPoint> offlineMap, HashMap<String, RoomInfo> roomInfo) {

        this.compassActivity = compassActivity;
        this.appSettings = appSettings;
        this.offlineMap = offlineMap;
        this.roomInfo = roomInfo;

        wifiManager = (WifiManager) compassActivity.getSystemService(Context.WIFI_SERVICE);
        mSensorManager = (SensorManager) compassActivity.getSystemService(Context.SENSOR_SERVICE);
        linearAcceleration = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        magnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        gravity = mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
    }

    /**
     * Android onPreExecute.
     */
    @Override
    protected void onPreExecute() {

    }

    /**
     * Android doInBackground.
     *
     * @param params
     */
    @Override
    protected Void doInBackground(String... params) {

        mSensorManager.registerListener(this, linearAcceleration, SensorManager.SENSOR_DELAY_FASTEST);
        mSensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_FASTEST);
        mSensorManager.registerListener(this, gravity, SensorManager.SENSOR_DELAY_FASTEST);

        setInitialPos(appSettings.getInitRSSIReadings());

        BroadcastReceiver probReceiver = null;

        while (!isCancelled()) {

            processSensorValues();

            if (isInitialising) {

                //find the initial point
                if (initialPoints.size() == appSettings.getInitRSSIReadings()) {

                    Point initialPoint = Locate.findUnweightedCentre(initialPoints);

                    inertialPoint = new InertialPoint(initialPoint);
                    probReceiver = setupProbReceiver();
                    isInitialising = false;
                }
            }

            if (isWifiResultsReady) {

                KNNFloorPoint onlinePoint = processScanResults(scanResults, appSettings.isBSSIDMerged());

                Location latestPoint = Probabilistic.run(onlinePoint, offlineMap, roomInfo, appSettings.getK(), deviceOrientation);

                NavigationResults navigationResults = doParticleFilter(latestPoint, inertialPoint, appSettings.isForceToOfflineMap());
                publishProgress(navigationResults);
                wifiManager.startScan();

                isWifiResultsReady = false;
            }
        }

        //unregister the broadcast receiver and sensor listeners
        if (probReceiver != null) {
            compassActivity.unregisterReceiver(probReceiver);
        }

        mSensorManager.unregisterListener(this, linearAcceleration);
        mSensorManager.unregisterListener(this, magnetometer);
        mSensorManager.unregisterListener(this, gravity);

        return null;
    }

    /**
     * Android onProgressUpdate.
     *
     * @param navigationResults
     */
    @Override
    protected void onProgressUpdate(NavigationResults... navigationResults) {
        compassActivity.showValues(navigationResults[0]);
    }

    /**
     * Android onSensorChanged.
     *
     * @param event
     */
    @Override
    public void onSensorChanged(SensorEvent event) {

        if (event.sensor.getType() == Sensor.TYPE_GRAVITY) {
            mGravity = event.values;
        } else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            mGeomagnetic = event.values;
        } else if (event.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
            mLinearAcceleration = event.values;
        }

    }

    /**
     * Android onAccuracyChanged.
     *
     * @param sensor
     * @param accuracy
     */
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    /**
     * Initial position based on provided readings.
     * @param initReadings 
     */
    private void setInitialPos(final int initReadings) {

        //loop until the sensors have provided an orientation
        while (deviceOrientation == null) {
            processSensorValues();
        }

        /////
        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                KNNFloorPoint onlinePoint = processScanResults(wifiManager.getScanResults(), appSettings.isBSSIDMerged());

                Location initialPoint = Probabilistic.run(onlinePoint, offlineMap, roomInfo, appSettings.getK(), deviceOrientation);

                //Logging.printLine(String.format("Location :;%s;%s", initialPoint.getX(), initialPoint.getY()));
                initialPoints.add(initialPoint);

                if (initialPoints.size() == initReadings) {
                    context.unregisterReceiver(this);
                } else {
                    wifiManager.startScan();
                }
            }
        };

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);

        compassActivity.registerReceiver(broadcastReceiver, intentFilter);
        wifiManager.startScan();

    }

    /**
     * Setup Broadcast Receiver to receive results of wi-fi scan.
     * Triggers refresh of position in do in background.
     * @return 
     */
    private BroadcastReceiver setupProbReceiver() {

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);

        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                scanResults = wifiManager.getScanResults();
                isWifiResultsReady = true;
            }
        };

        compassActivity.registerReceiver(broadcastReceiver, intentFilter);
        wifiManager.startScan();

        return broadcastReceiver;

    }

    /**
     * Handler for results of sensors.
     * Move inertial point after values for all three sets of data (gravity, geomagnetic and linear acceleration) have been received.
     */
    private void processSensorValues() {

        if (mGravity != null && mGeomagnetic != null && mLinearAcceleration != null) {

            float R[] = new float[16];
            float I[] = new float[16];
            float iR[] = new float[16];
            boolean success = SensorManager.getRotationMatrix(R, I, mGravity, mGeomagnetic);

            if (success) {
                float orientation[] = new float[3];
                SensorManager.getOrientation(R, orientation);
                deviceOrientation = InertialData.getOrientation(appSettings.isOrientationMerged(), orientation[0], appSettings.getBuildingOrientation());

                if (!isInitialising) {
                    boolean invert = android.opengl.Matrix.invertM(iR, 0, R, 0);
                    if (invert) {

                        InertialData results = InertialData.getDatas(iR, mLinearAcceleration, orientation, appSettings.getBuildingOrientation(), appSettings.getJitterOffset(), appSettings.getAccelerationOffset());
                        inertialPoint = InertialPoint.move(inertialPoint, results, System.nanoTime(), appSettings.getSpeedBreak());

                    }
                }
            }
            mGravity = null;
            mGeomagnetic = null;
            mLinearAcceleration = null;
        }

    }

    /**
     * Activate particle filter to move the cloud of particles.
     * @param probabilisticLocation
     * @param inertialPoint
     * @param isForceToOfflineMap
     * @return 
     */
    private NavigationResults doParticleFilter(Location probabilisticLocation, InertialPoint inertialPoint, boolean isForceToOfflineMap) {

        if (cloud != null) {
            //Logging.printLine("Before: " + cloud.getParticleCount());
            cloud = ParticleFilter.filter(cloud, probabilisticLocation.getGlobalPoint(), inertialPoint, appSettings.getParticleCount(), appSettings.getCloudRange(), appSettings.getCloudDisplacement(), appSettings.getBoundaries(), appSettings.getParticleCreation());
            //Logging.printLine("After: " + cloud.getParticleCount());
        } else {
            List<Particle> particles = ParticleFilter.createParticles(inertialPoint.getPoint(), appSettings.getParticleCount());
            cloud = new Cloud(inertialPoint.getPoint(), particles);
        }

        Location estimatedLocation = RoomInfo.searchGlobalLocation(cloud.getEstiPos(), roomInfo);
        Location bestLocation = Locate.forceToMap(offlineMap, estimatedLocation, isForceToOfflineMap);        

        return new NavigationResults(probabilisticLocation, estimatedLocation, inertialPoint, bestLocation, inertialPoint.getInertialData());
    }

    /**
     * Convert scan result into a single floor point to compare to offline map.
     * @param scanResults
     * @param isBSSIDMerged
     * @return 
     */
    private KNNFloorPoint processScanResults(List<ScanResult> scanResults, boolean isBSSIDMerged) {

        KNNFloorPoint knnFloorPoint = new KNNFloorPoint();

        for (ScanResult scanResult : scanResults) {
            knnFloorPoint.add(scanResult.BSSID, scanResult.level, isBSSIDMerged);
        }

        return knnFloorPoint;

    }   
   
}
