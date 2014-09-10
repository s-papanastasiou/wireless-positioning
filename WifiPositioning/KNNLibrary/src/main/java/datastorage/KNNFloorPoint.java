/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package datastorage;

import general.AvgValue;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 *
 * @author Greg Albiston
 *
 * Generic container to store either Magnetic or RSSI data. key would be X,Y or
 * Z for Magnetic and the BSSID for RSSI. value would be microTesla for Magnetic
 * and dbm for RSSI.
 *
 */
public class KNNFloorPoint extends Location implements Serializable {
    
    private HashMap<String, AvgValue> attributes = new HashMap<>();
    private final String roomReference;
    
    public final static int NO_ORIENTATION = -1;

    /**
     * Default constructor
     */
    public KNNFloorPoint() {
        super();
        this.roomReference = super.getRoomRef();
    }

    /**
     * Default constructor - location specified
     *
     * @param location Location of the floor point.
     */
    public KNNFloorPoint(Location location) {
        super(location);
        this.roomReference = super.getRoomRef();
    }

    /**
     * Constructor
     *
     * @param key
     * @param value
     */
    public KNNFloorPoint(final String key, final Double value) {
        super();
        this.attributes.put(key, new AvgValue(value));
        this.roomReference = super.getRoomRef();
    }

    /**
     * Constructor - location specified
     *
     * @param location
     * @param key
     * @param value
     */
    public KNNFloorPoint(final Location location, final String key, final Double value) {
        super(location);
        this.attributes.put(key, new AvgValue(value));
        this.roomReference = super.getRoomRef();
    }
    
    public KNNFloorPoint(final Location location, final String key, final Integer value) {
        super(location);
        this.attributes.put(key, new AvgValue(value));
        this.roomReference = super.getRoomRef();
    }

    //
    /**
     * Constructor - location and room reference specified
     *
     * @param location
     * @param key
     * @param value
     * @param roomReference
     */
    public KNNFloorPoint(final Location location, final String key, final Integer value, final String roomReference) {
        super(location);
        this.attributes.put(key, new AvgValue(value));
        this.roomReference = roomReference;
    }

    /**
     * Constructor for adding RSSI data - allows room reference to be specified
     *
     * @param location
     * @param key
     * @param value
     * @param roomReference
     */
    public KNNFloorPoint(final Location location, final String key, final Double value, final String roomReference) {
        super(location);
        this.attributes.put(key, new AvgValue(value));
        this.roomReference = roomReference;
    }

    /**
     * Constructor - allows multiple values to be added
     *
     * @param location
     * @param keyX
     * @param valueX
     * @param keyY
     * @param valueY
     * @param keyW
     * @param valueW
     */
    public KNNFloorPoint(final Location location, final String keyX, final Double valueX, final String keyY, final Double valueY, final String keyW, final Double valueW) {
        super(location);
        this.attributes.put(keyX, new AvgValue(valueX));
        this.attributes.put(keyY, new AvgValue(valueY));
        this.attributes.put(keyW, new AvgValue(valueW));
        this.roomReference = super.getRoomRef();
    }

    /**
     * Copy constructor
     *
     * @param floorPoint
     */
    public KNNFloorPoint(final KNNFloorPoint floorPoint) {
        super(floorPoint);
        
        this.attributes = new HashMap<>();
        for (String key : floorPoint.attributes.keySet()) {
            AvgValue value = new AvgValue(floorPoint.attributes.get(key));
            this.attributes.put(key, value);
        }
        
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
    
    public void add(final String key, final Double value) {
        
        if (attributes.containsKey(key)) {
            AvgValue avgValue = attributes.get(key);
            avgValue.add(value);
        } else {
            attributes.put(key, new AvgValue(value));
        }
    }
    
    public void add(final String key, final Integer value) {
        add(key, value.doubleValue());
    }
    
    public void add(HashMap<String, AvgValue> attributes) {
        
        for (String key : attributes.keySet()) {
            AvgValue value = attributes.get(key);
            if (this.attributes.containsKey(key)) {
                AvgValue avgValue = this.attributes.get(key);
                avgValue.add(value);
            } else {
                this.attributes.put(key, new AvgValue(value));
            }
        }
    }
    
    public void add(final String key, final Integer value, final Boolean isBSSIDMerged) {
        add(key, value.doubleValue(), isBSSIDMerged);
    }

    //Variant that strips the key (BSSID) down from six hex pairs to five.
    public void add(final String key, final Double value, final Boolean isBSSIDMerged) {
        
        int beginIndex = 0;
        int endIndex = 17; //full BSSID - six hex pairs

        if (isBSSIDMerged) {
            endIndex = 14;
        }
        
        String bssid = key.substring(beginIndex, endIndex);  //Copy out the BSSID based on whether merging or not.                           
        if (attributes.containsKey(bssid)) {
            AvgValue avgValue = attributes.get(bssid);
            avgValue.add(value);
        } else {
            attributes.put(bssid, new AvgValue(value));
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
    
    public Boolean equivalentTo(KNNFloorPoint other) {
        
        Boolean result = true;
        
        HashMap<String, AvgValue> otherAttributes = other.attributes;
        if (this.attributes.size() == otherAttributes.size()) {
            
            for (String key : this.attributes.keySet()) {
                if (!otherAttributes.containsKey(key)) {
                    result = false;
                    break;
                }
            }
            
        } else {
            result = false;
        }
        
        return result;
    }   
    
    public double findAvgVariance(){
        
        double variance = 0.0;
        for(AvgValue value: attributes.values()){
            variance += value.getVariance();
        }
        if(variance>0){
            variance /= attributes.size();
        }
        return variance;
    }
    
    public Boolean matchingAttributes(KNNFloorPoint other, Double rssiTolerance, Double geoTolerance) {
        Boolean result;
        
        HashMap<String, AvgValue> otherAttributes = other.attributes;
        if (this.attributes.size() == otherAttributes.size()) {
            //Test the geo keys against tolerance
            result = testAttributes(GeomagneticData.KEY_LIST, this.attributes, otherAttributes, geoTolerance);
            
            if (result) {
                //Test the RSSI keys against tolerance
                List<String> rssiKeys = new ArrayList<>();
                for (String key : this.attributes.keySet()) {
                    
                    if (!GeomagneticData.KEY_LIST.contains(key)) {
                        rssiKeys.add(key);
                    }
                }
                result = testAttributes(rssiKeys, this.attributes, otherAttributes, rssiTolerance);
            }
        } else {
            result = false;
        }
        
        return result;
    }
    
    private static Boolean testAttributes(List<String> keys, HashMap<String, AvgValue> attributes, HashMap<String, AvgValue> otherAttributes, Double tolerance) {
        
        Boolean result = true;
        for (String key : keys) {
            if (attributes.containsKey(key)) {
                if (otherAttributes.containsKey(key)) {
                    Double mean = attributes.get(key).getMean();
                    Double otherMean = otherAttributes.get(key).getMean();
                    if (!(otherMean - tolerance <= mean && mean < otherMean + tolerance)) {
                        result = false;
                        break;
                    }
                } else {
                    result = false;
                    break;
                }
            }
        }
        return result;
    }
    
    public String toStringAttributes(String fieldSeparator) {
        
        StringBuilder stb = new StringBuilder();
        for (String key : attributes.keySet()) {
            stb.append(key).append(" ").append(attributes.get(key)).append(fieldSeparator);
        }
        return stb.substring(0, stb.length() - 1);
    }

    /**
     * Merges two HashMaps of KNNFloorPoints together - deep copy
     *
     * @param firstFloorPoints
     * @param secondFloorPoints
     * @return
     */
    public static HashMap<String, KNNFloorPoint> merge(HashMap<String, KNNFloorPoint> firstFloorPoints, HashMap<String, KNNFloorPoint> secondFloorPoints) {
        
        HashMap<String, KNNFloorPoint> mergedFloorPoints = new HashMap<>();
        
        for (String key : firstFloorPoints.keySet()) {
            KNNFloorPoint value = new KNNFloorPoint(firstFloorPoints.get(key));
            mergedFloorPoints.put(key, value);
        }
        
        for (String key : secondFloorPoints.keySet()) {
            
            KNNFloorPoint value = secondFloorPoints.get(key);
            
            if (mergedFloorPoints.containsKey(key)) {
                KNNFloorPoint mergedValue = mergedFloorPoints.get(key);
                mergedValue.add(value.getAttributes());
            } else {
                KNNFloorPoint newValue = new KNNFloorPoint(value);
                mergedFloorPoints.put(key, newValue);
            }
        }
        
        return mergedFloorPoints;
    }       
    
}
