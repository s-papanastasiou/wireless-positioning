package me.gregalbiston.androiddetector.scan;

import android.hardware.SensorEvent;

/**
 * Created with IntelliJ IDEA.
 * User: Gerg
 * Date: 20/06/13
 * Time: 09:17
 * Stores the event information with the system time at which it occurred.
 */
public class MagneticResult {

    protected long timestamp;
    protected SensorEvent event;

    public MagneticResult(long timestamp_arg, SensorEvent event_arg) {
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
