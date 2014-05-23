/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package datastorage;

import general.AvgValue;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Set;

/**
 *
 * @author Greg Albiston
 *
 * Generic container to store either Magnetic or RSSI data. Key would be X,Y or
 * Z for Magnetic and the BSSID for RSSI. Value would be microTesla for Magnetic
 * and dbm for RSSI.
 *
 */
public class KNNFloorPoint extends Location implements Serializable {

    private HashMap<String, AvgValue> attributes = new HashMap<>();
    private final String roomReference;

    public final static int NO_ORIENTATION = -1;

    /**
     * KNNFloorPoint constructor
     */
    public KNNFloorPoint() {
        super(new Location());
        this.roomReference = super.getRoomRef();
    }

    /**
     * KNNFloorPoint constructor
     *
     * @param location Location of the floor point.
     */
    public KNNFloorPoint(Location location) {
        super(location);
        this.roomReference = super.getRoomRef();
    }

    /**
     *
     * @param Key
     * @param Value
     */
    public KNNFloorPoint(final String Key, final double Value) {
        super(new Location());
        this.attributes.put(Key, new AvgValue(Value));
        this.roomReference = super.getRoomRef();
    }

    //constructor for adding rssi data
    public KNNFloorPoint(final Location location, final String Key, final double Value) {
        super(location);
        this.attributes.put(Key, new AvgValue(Value));
        this.roomReference = super.getRoomRef();
    }

    //constructor for adding rssi data - allows room reference to be specifed
    public KNNFloorPoint(final Location location, final String Key, final double Value, final String roomReference) {
        super(location);
        this.attributes.put(Key, new AvgValue(Value));
        this.roomReference = roomReference;
    }

    //constructor for adding magnetic data
    public KNNFloorPoint(final Location location, final String KeyX, final double ValueX, final String KeyY, final double ValueY, final String KeyW, final double ValueW) {
        super(location);
        this.attributes.put(KeyX, new AvgValue(ValueX));
        this.attributes.put(KeyY, new AvgValue(ValueY));
        this.attributes.put(KeyW, new AvgValue(ValueW));
        this.roomReference = super.getRoomRef();
    }

    public KNNFloorPoint(final KNNFloorPoint floorPoint) {
        super(floorPoint);
        this.attributes = floorPoint.attributes;
        this.roomReference = floorPoint.roomReference;
    }

    public HashMap<String, AvgValue> getAttributes() {
        return attributes;
    }

    public String printAttributes() {
        String results = "";

        Set<String> keys = attributes.keySet();
        for (String key : keys) {
            AvgValue value = attributes.get(key);
            results += key + " " + value.toString() + System.lineSeparator();
        }

        return results;
    }

    public void add(final String Key, final double Value) {

        if (attributes.containsKey(Key)) {
            AvgValue value = attributes.get(Key);
            value.add(Value);
        } else {
            attributes.put(Key, new AvgValue(Value));
        }
    }

    //Variant that strips the key (BSSID) down from six hex pairs to five.
    public void add(final String Key, final double Value, final boolean isBSSIDMerged) {

        int beginIndex = 0;
        int endIndex = 17; //full BSSID - six hex pairs

        if (isBSSIDMerged) {
            endIndex = 14;
        }

        String bssid = Key.substring(beginIndex, endIndex);  //Copy out the BSSID based on whether merging or not.                           
        if (attributes.containsKey(bssid)) {
            AvgValue value = attributes.get(bssid);
            value.add(Value);
        } else {
            attributes.put(bssid, new AvgValue(Value));
        }
    }

    @Override
    public String getRoomRef() {
        return roomReference;
    }

    public static HashMap<String, KNNFloorPoint> filterMap(HashMap<String, KNNFloorPoint> offlineMap, int orientation) {
        HashMap<String, KNNFloorPoint> orientatedMap;

        if (orientation != NO_ORIENTATION) {
            orientatedMap = new HashMap<>();
            Set<String> keys = offlineMap.keySet();

            for (String key : keys) {
                KNNFloorPoint orientatedPoint = offlineMap.get(key);
                if (orientatedPoint.getwRef() == orientation) {
                    orientatedMap.put(key, orientatedPoint);
                }
            }
        } else {
            orientatedMap = offlineMap;
        }

        return orientatedMap;
    }

}
