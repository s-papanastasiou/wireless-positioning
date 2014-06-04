package me.gregalbiston.androiddetector.scan;

import android.net.wifi.ScanResult;

import java.util.List;

/**
 * Stores a list of scan results along with the system time at which they were returned.
 * @author Greg Albiston
 */
public class RSSIResult {

    protected long timestamp;
    protected List<ScanResult> results;

    public RSSIResult(long timestamp_arg, List<ScanResult> results_arg) {
        timestamp = timestamp_arg;
        results = results_arg;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public List<ScanResult> getResults() {
        return results;
    }

}
