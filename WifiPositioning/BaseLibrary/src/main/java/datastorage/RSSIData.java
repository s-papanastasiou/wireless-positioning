/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package datastorage;

import general.TimeStamp;
import java.text.ParseException;
import java.util.HashMap;
import org.apache.commons.lang3.ArrayUtils;

/**
 * Stores information relating to geomagnetic survey.
 *
 * @author Greg Albiston
 */
public class RSSIData extends Location {

    private final long timestamp;
    private final String BSSID;
    private final String SSID;
    private final int RSSI;
    private final int channel;

    private static final String[] LOCAL_HEADINGS_START = {"Timestamp"};
    private static final String[] LOCAL_HEADINGS_END = {"BSSID", "SSID", "RSSI", "Channel"};
    public static final String[] HEADINGS = ArrayUtils.addAll(LOCAL_HEADINGS_START, ArrayUtils.addAll(Location.LOC_HEADINGS, LOCAL_HEADINGS_END));

    /**
     * Constructor
     *
     * @param parts String array of elements to build class. See HEADINGS for
     * order.
     * @param roomInfo Information about the rooms on the floor.
     * @throws ParseException
     */
    public RSSIData(final String[] parts, final HashMap<String, RoomInfo> roomInfo) throws ParseException {
        super(RoomInfo.createLocation(parts[1], Integer.parseInt(parts[2]), Integer.parseInt(parts[3]), Integer.parseInt(parts[4]), roomInfo));
        this.timestamp = TimeStamp.convertDateTime(parts[0]);
        this.BSSID = parts[5];
        this.SSID = parts[6];
        this.RSSI = Integer.parseInt(parts[7]);
        this.channel = Integer.parseInt(parts[8]);
    }

    /**
     * Constructor Frequency is converted to channel number between 1-13 (14 is
     * not generally available).
     *
     * @param timestamp Timestamp of when the reading took place.
     * @param location Location information of where the reading was taken.
     * @param BSSID The MAC address of access point - hex pairs.
     * @param SSID The name of the network.
     * @param RSSI Received Signal Strength Indication - dBm
     * @param frequency The channel frequency over which the client is
     * communicating with the access point - MHz.
     */
    public RSSIData(long timestamp, Location location, String BSSID, String SSID, int RSSI, int frequency) {
        super(location);
        this.timestamp = timestamp;
        this.BSSID = BSSID;
        this.SSID = SSID;
        this.RSSI = RSSI;
        this.channel = ((frequency - 2412) / 5) + 1;  //calculates accurately for channels 1-13. Channel 14 is not generally available.        
    }

    /**
     * Get BSSID MAC address hex pairs.
     *
     * @return
     */
    public String getBSSID() {
        return BSSID;
    }

    /**
     * Get RSSI value.
     *
     * @return
     */
    public int getRSSI() {
        return RSSI;
    }

    /**
     * Get timestamp.
     *
     * @return
     */
    public long getTimestamp() {
        return timestamp;
    }

    /**
     * Get SSID network name.
     *
     * @return
     */
    public String getSSID() {
        return SSID;
    }

    /**
     * Get channel of communication.
     *
     * @return
     */
    public int getChannel() {
        return channel;
    }

    /**
     * String representation of location information using provided separator.
     *
     * @param fieldSeparator Field separator between columns.
     * @return
     */
    @Override
    public String toString(final String fieldSeparator) {
        return TimeStamp.formatDateTime(timestamp) + fieldSeparator + super.toString(fieldSeparator) + fieldSeparator + BSSID + fieldSeparator + SSID + fieldSeparator + RSSI + fieldSeparator + channel;
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
