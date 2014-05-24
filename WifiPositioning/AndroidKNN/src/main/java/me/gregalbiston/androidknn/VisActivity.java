package me.gregalbiston.androidknn;


import android.app.Activity;
import android.content.Intent;
import android.hardware.SensorEvent;
import android.net.wifi.ScanResult;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import datastorage.Location;
import general.Point;
import java.util.List;
import me.gregalbiston.androidknn.dataload.DataManager;
import me.gregalbiston.androidknn.display.FloorView;


public class VisActivity extends Activity {
    /**
     * Called when the activity is first created.
     */

    public static final String FILE_DIRECTORY = "Visualiser";
    public static final String KNN_DATA_FILE_RSSI = "RSSIKNNData.knn";
    public static final String KNN_DATA_FILE_MAGNETIC = "MagneticKNNData.knn";
    public static final String ROOM_INFO_FILE = "RoomInfo.csv";
    public static final String FILTER_SSID_FILE = "FilterSSID.txt";

    public static final String FIELD_SEPARATOR = ",";

    public static final String FLOOR_PLAN_FILE = "floorplan.png";

    protected static DataManager dataManager = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

    }

    @Override
    public void onStart() {
        super.onStart();

        FloorView floorView = (FloorView) findViewById(R.id.view);
        floorView.setActivity(this);
        TextView logTextView = (TextView) findViewById(R.id.logTextView);
        logTextView.setMovementMethod(new ScrollingMovementMethod());

        if (dataManager == null) {
            dataManager = new DataManager(this, logTextView, floorView);

        } else {
            dataManager.start(this, logTextView, floorView);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }


    @Override
    public void onStop() {
        super.onStop();
        dataManager.stop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onPause() {
        super.onPause();

    }

    public void rssiScanResult(List<ScanResult> scanResults, Location location, Point screenPoint) {
        dataManager.rssiScanResult(scanResults, location, screenPoint);
    }

    public void magneticScanResult(SensorEvent event, Location location, Point screenPoint) {
        dataManager.magneticScanResult(event, location, screenPoint);
    }

    /*
    public void showToast(final String toast) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), toast, Toast.LENGTH_SHORT).show();
            }
        });
    }
    */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.menu_new_route:
                dataManager.newRoute();
                break;

            case R.id.menu_settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                break;

            case R.id.menu_exit:
                break;
        }
        return true;
    }

}
