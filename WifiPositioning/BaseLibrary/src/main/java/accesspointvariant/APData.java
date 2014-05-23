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
    }

    /**
     * Add a new piece of RSSI data to a new or existing location.
     * @param item RSSI data for the BSSID
     */
    public void add(final RSSIData item) {
        APLocation location = new APLocation(item, item.getRSSI());

        if (locations.contains(location)) {
            int index = locations.indexOf(location);
            locations.get(index).add(item.getRSSI());
        } else {
            this.locations.add(location);
        }
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
