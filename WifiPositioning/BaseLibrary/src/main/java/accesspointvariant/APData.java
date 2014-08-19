/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package accesspointvariant;

import datastorage.RSSIData;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.ArrayUtils;

/**
 * AccessPoint Data
 * Stores locations for each access point based on the Basic Service Set Identification (BSSID).
 * Average RSSI information for each location of this AccessPoint can be obtained.
 * @author Greg Albiston
 */
public class APData {

    private final String BSSID;
    private final List<APLocation> locations = new ArrayList<>();
    private Integer maxRSSIFrequency;
    private Double maxRSSIMean;
    private Double minRSSIMean;
    
    private static final String[] LOCAL_HEADINGS = {"BSSID"};
    public static final String[] HEADINGS = ArrayUtils.addAll(LOCAL_HEADINGS, APLocation.HEADINGS);

    /**
     * Constructor - every BSSID must have at least one location of RSSI data.
     * 
     * @param BSSID Basic Service Set Identification hex pairs
     * @param location Location of the access point on grid with RSSI data.
     */
    public APData(final String BSSID, final APLocation location) {

        this.BSSID = BSSID;
        this.locations.add(location);
        this.maxRSSIFrequency = location.frequency();
        this.maxRSSIMean = location.mean();
        this.minRSSIMean = location.mean();
    }

    /**
     * Add a new piece of RSSI data to a new or existing location.
     * @param item RSSI data for the BSSID
     */
    public void add(final RSSIData item) {
        APLocation location = new APLocation(item, item.getRSSI());

        int freq;
        double mean;
        if (locations.contains(location)) {
            int index = locations.indexOf(location);
            locations.get(index).add(item.getRSSI());
            freq = locations.get(index).frequency();
            mean = locations.get(index).mean();
        } else {
            this.locations.add(location);
            freq = location.frequency();
            mean = location.mean();
        }
        
        if(freq>maxRSSIFrequency)
            maxRSSIFrequency = freq;
        
        if(mean>maxRSSIMean)
            maxRSSIMean = mean;
        
        if(mean<minRSSIMean)
            minRSSIMean = mean;
        
    }

    /**
     * List of all locations for this BSSID.
     * 
     * @return 
     */
    public List<APLocation> getLocations() {
        return locations;
    }

    /**
     * String representation of BSSID.
     * 
     * @return 
     */
    public String getBSSID() {
        return BSSID;
    }   

    /**
     * Maximum RSSI frequency of the locations for this BSSID.
     * 
     * @return 
     */
    public Integer getMaxRSSIFrequency() {
        return maxRSSIFrequency;
    }

    /**
     * Maximum RSSI mean of the locations for this BSSID.
     * 
     * @return 
     */
    public Double getMaxRSSIMean() {
        return maxRSSIMean;
    }

    /**
     * Minimum RSSI mean of the locations for this BSSID.
     * 
     * @return 
     */
    public Double getMinRSSIMean() {
        return minRSSIMean;
    }
    
    /**
     * Formatted heading to show the BSSID along with information relating to a location.
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
