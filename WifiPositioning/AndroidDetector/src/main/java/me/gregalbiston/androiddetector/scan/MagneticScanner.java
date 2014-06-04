package me.gregalbiston.androiddetector.scan;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import datastorage.Location;
import datastorage.MagneticData;
import general.TimeStamp;
import java.util.ArrayList;
import java.util.List;
import me.gregalbiston.androiddetector.DectectorActivity;
import me.gregalbiston.androiddetector.R;
import me.gregalbiston.androiddetector.storage.FileOutput;

/**
 * Events are filtered according to whether isSampling is true.
 * Events occur much quicker than 1 per second.
 * Events stored in an array for later processing.
 * @author Greg Albiston
 */
public class MagneticScanner implements SensorEventListener {

    private final SensorManager sensorManager;
    private final List<MagneticResult> magneticResults = new ArrayList<>();
    private ListView magneticListView;
    private final Sensor magneticSensor;

    private final Location scanLocation;

    private boolean isSampling = true;


    public MagneticScanner(DectectorActivity dectectorActivity, Location scanLocation) {

        this.scanLocation = scanLocation;

        sensorManager = (SensorManager) dectectorActivity.getSystemService(Context.SENSOR_SERVICE);
        magneticSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        //Attach listener for sensor events
        sensorManager.registerListener(this, magneticSensor, SensorManager.SENSOR_DELAY_FASTEST);

        //On-screen message and clear previous content
        displayHoldingMessage(dectectorActivity);
    }

    public void finish(FileOutput fileOutput) {
        sensorManager.unregisterListener(this, magneticSensor);

        //Iterate over each recorded event

        ArrayList<MagneticData> results = new ArrayList<>();
        ArrayList<String> screenResults = new ArrayList<>();

        for (MagneticResult result : magneticResults) {
            SensorEvent event = result.getEvent();
            results.add(new MagneticData(result.getTimestamp(), scanLocation, event.values[0], event.values[1], event.values[2], event.accuracy));
            long timestamp = result.getTimestamp();
            screenResults.add(TimeStamp.formatShortDateTime(timestamp) + " " + scanLocation.toString() + DectectorActivity.OUTPUT_SEPARATOR + event.values[0] + DectectorActivity.OUTPUT_SEPARATOR + event.values[1] + DectectorActivity.OUTPUT_SEPARATOR + event.values[2] + DectectorActivity.OUTPUT_SEPARATOR + event.accuracy);
        }

        //Display the results
        Context context = magneticListView.getContext();
        ArrayAdapter magneticAdapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, screenResults);
        magneticListView.setAdapter(magneticAdapter);

        fileOutput.outputMagneticFile(results, DectectorActivity.FILE_DIRECTORY, DectectorActivity.OUTPUT_FILENAME_MAGNETIC, DectectorActivity.OUTPUT_SEPARATOR, DectectorActivity.OUTPUT_MIME_TYPE, context);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        if (isSampling) {
            magneticResults.add(new MagneticResult(System.currentTimeMillis(), event));
            isSampling = false;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void setSampling() {
        isSampling = true;
    }

    private void displayHoldingMessage(DectectorActivity dectectorActivity) {
        ArrayList<String> message = new ArrayList<>();
        String msg = dectectorActivity.getResources().getString(R.string.labelMagneticStart);
        message.add(msg);
        ArrayAdapter magneticAdapter = new ArrayAdapter<>(dectectorActivity, android.R.layout.simple_list_item_1, message);
        magneticListView = (ListView) dectectorActivity.findViewById(R.id.listViewMagnetic);
        magneticListView.setAdapter(magneticAdapter);

    }

}
