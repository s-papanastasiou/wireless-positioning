package me.gregalbiston.androiddetector.scan;

import android.hardware.SensorEvent;

/**
 * Stores the event information with the system time at which it occurred.
 * @author Greg Albiston
 */
public class GeomagneticResult {

    protected long timestamp;
    protected SensorEvent event;

    public GeomagneticResult(long timestamp_arg, SensorEvent event_arg) {
        timestamp = timestamp_arg;
        event = event_arg;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public SensorEvent getEvent() {
        return event;
    }

}
