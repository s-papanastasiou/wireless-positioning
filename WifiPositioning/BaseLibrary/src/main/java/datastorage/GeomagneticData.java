/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package datastorage;

import general.TimeStamp;
import java.text.ParseException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import org.apache.commons.lang3.ArrayUtils;

/**
 * Stores information relating to geomagnetic survey.
 *
 * @author Greg Albiston
 */
public class GeomagneticData extends Location {

    private final long timestamp;
    private final double xValue;
    private final double yValue;
    private final double zValue;
    private final int sensorAccuracy;

    private static final String[] LOCAL_HEADINGS_START = {"Timestamp"};
    private static final String[] LOCAL_HEADINGS_END = {"X Value", "Y Value", "Z Value", "Accuracy"};
    public static final String[] HEADINGS = ArrayUtils.addAll(LOCAL_HEADINGS_START, ArrayUtils.addAll(Location.LOC_HEADINGS, LOCAL_HEADINGS_END));

    public static final String X_KEY = "X";
    public static final String Y_KEY = "Y";
    public static final String Z_KEY = "Z";
    private static final String[] KEY_ARRAY = {X_KEY, Y_KEY, Z_KEY};
    public static final List<String> KEY_LIST = Arrays.asList(KEY_ARRAY);

    /**
     * Constructor
     *
     * @param parts String array of elements to build class. See HEADINGS for
     * order.
     * @param roomInfo Information about the rooms on the floor.
     * @throws ParseException
     */
    public GeomagneticData(final String[] parts, final HashMap<String, RoomInfo> roomInfo) throws ParseException {                            
        super(RoomInfo.createLocation(parts[1], Integer.parseInt(parts[2]), Integer.parseInt(parts[3]), Integer.parseInt(parts[4]), roomInfo));
        this.timestamp = TimeStamp.convertDateTime(parts[0]);
        this.xValue = Double.parseDouble(parts[5]);
        this.yValue = Double.parseDouble(parts[6]);
        this.zValue = Double.parseDouble(parts[7]);
        this.sensorAccuracy = Integer.parseInt(parts[8]);        
    }

    /**
     * Constructor
     *
     * @param timestamp Timestamp of when reading was taken.
     * @param location Location information of where the reading was taken.
     * @param xValue Geomagnetic field strength in x-axis.
     * @param yValue Geomagnetic field strength in y-axis.
     * @param zValue Geomagnetic field strength in z-axis.
     * @param sensorAccuracy Sensor accuracy reported by Android device for the
     * reading.
     */
    public GeomagneticData(long timestamp, Location location, double xValue, double yValue, double zValue, int sensorAccuracy) {
        super(location);
        this.timestamp = timestamp;
        this.xValue = xValue;
        this.yValue = yValue;
        this.zValue = zValue;
        this.sensorAccuracy = sensorAccuracy;
    }

    /**
     * Timestamp when the reading was taken.
     *
     * @return
     */
    public long getTimestamp() {
        return timestamp;
    }

    /**
     * X-axis geomagnetic field strength in micro Tesla.
     *
     * @return
     */
    public double getxValue() {
        return xValue;
    }

    /**
     * Y-axis geomagnetic field strength in micro Tesla.
     *
     * @return
     */
    public double getyValue() {
        return yValue;
    }

    /**
     * Z-axis geomagnetic field strength in micro Tesla.
     *
     * @return
     */
    public double getzValue() {
        return zValue;
    }

    /**
     * Sensor accuracy reported by Android device for the reading.
     *
     * @return
     */
    public double getSensorAccuracy() {
        return sensorAccuracy;
    }

    /**
     * String representation of location information using provided separator.
     *
     * @param fieldSeparator Character to use as separator.
     * @return
     */
    @Override
    public String toString(final String fieldSeparator) {
        return timestamp + fieldSeparator + super.toString(fieldSeparator) + fieldSeparator + xValue + fieldSeparator + yValue + fieldSeparator + zValue + fieldSeparator + sensorAccuracy;
    }

    /**
     * Formatted heading to show the information relating to a location.
     *
     * @param fieldSeparator Separator used between each heading.
     * @return
     */
    public static String toStringHeadings(final String fieldSeparator) {

        String result = HEADINGS[0];
        for (int counter = 1; counter < HEADINGS.length; counter++) {
            result += fieldSeparator + HEADINGS[counter];
        }
        return result;
    }

    /**
     * Checks whether the supplied array contains the expected headings for
     * geomagnetic data.
     *
     * @param parts String array to be checked.
     * @return
     */
    public static boolean headerCheck(final String[] parts) {
        boolean isCorrect = true;

        if (parts.length == HEADINGS.length) {
            for (int counter = 0; counter < parts.length; counter++) {
                if (!parts[counter].equals(HEADINGS[counter])) {
                    isCorrect = false;
                    break;
                }
            }
        } else {
            isCorrect = false;
        }

        return isCorrect;
    }

    /**
     * Length of standard header for geomagnetic data.
     *
     * @return
     */
    public static int headerSize() {
        return HEADINGS.length;
    }

}
