/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package accesspointvariant;

import datastorage.Location;
import general.AvgValue;
import org.apache.commons.lang3.ArrayUtils;

/**
 * Stores average RSSI information at a location.
 * 
 * @author Greg Albiston
 */
public class APLocation extends Location {
    
    private static final String[] LOCAL_HEADINGS = {"Mean", "Frequency", "Total", "StdDev"};
    public static final String[] HEADINGS = ArrayUtils.addAll(Location.LOC_HEADINGS, LOCAL_HEADINGS);
    
    private final AvgValue avgRSSI;
    
    /**
     * Constructor
     * 
     * @param location
     * @param rssiValue 
     */
    public APLocation(final Location location, final int rssiValue) {
        super(location);
        this.avgRSSI = new AvgValue(rssiValue);
    }

    /**
     * Add an additional RSSI value to the location.
     * 
     * @param rssiValue 
     */
    public void add(final int rssiValue) {
        avgRSSI.add(rssiValue);
    }

    /**
     * Arithmetic mean of RSSI at the location.
     * 
     * @return 
     */
    public double mean() {
        return avgRSSI.getMean();
    }

    /**
     * Count of RSSI values added to the location.
     * 
     * @return 
     */
    public int frequency() {
        return avgRSSI.getFrequency();
    }

    /**
     * Standard deviation of the RSSI values at the location.
     * 
     * @return 
     */
    public double stdDev() {
        return avgRSSI.getStdDev();
    }
    
    /**
     * Get the AvgRSSI object holding RSSI values at the location.
     * 
     * @return 
     */
    public AvgValue getAvgRSSI(){
        return avgRSSI;
    }

    /**
     * String representation of the object separated by colons.
     * 
     * @return 
     */
    @Override
    public String toString() {
        
        return super.toString() + ":" + avgRSSI.getMean() + ":" + avgRSSI.getFrequency() + ":" + avgRSSI.getTotal() + ":" + avgRSSI.getStdDev();
    }

    /**
     * String representation of the object using provided separator.
     * 
     * @param fieldSeparator Separator used in string.
     * @return 
     */
    @Override
    public String toString(String fieldSeparator) {
        return super.toString(fieldSeparator) + fieldSeparator + avgRSSI.getMean() + fieldSeparator + avgRSSI.getFrequency() + fieldSeparator + avgRSSI.getTotal() + fieldSeparator + avgRSSI.getStdDev();
    }
    
    public String toStringLocation(){
        return super.toString();
    }

    /**
     * String representation of the object using provided separator.
     * Adjusted by the specified accuracy i.e. converts the values to a 1m grid spacing
     * 
     * @param fieldSeparator Separator used in string.
     * @param accuracy Multiple accuracy for grid spacing. e.g. 10 will give 10m grid spacing.
     * @return 
     */
    @Override
    public String toString(String fieldSeparator, int accuracy) {
        return super.toString(fieldSeparator, accuracy) + fieldSeparator + avgRSSI.getMean() + fieldSeparator + avgRSSI.getFrequency() + fieldSeparator + avgRSSI.getTotal() + fieldSeparator + avgRSSI.getStdDev();
    }
    
    /**
     * Formatted heading to show the locations.
     * 
     * @param fieldSeparator Separator used between each heading.
     * @return 
     */
    public static String toStringHeadings(String fieldSeparator){
        
        String result =HEADINGS[0];
        for(int counter = 1; counter < HEADINGS.length; counter++){
            result += fieldSeparator + HEADINGS[counter];
        }        
        return result;              
    }
}
