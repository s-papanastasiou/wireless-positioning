/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package accesspointvariant;

import datastorage.RSSIData;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Gerg
 */
public class APData {

    private String BSSID;
    private List<APLocation> locations = new ArrayList<>();
    public static final String[] HEADINGS = {"BSSID", "Room", "X Ref", "Y Ref", "W Ref", "Mean", "Frequency", "Total", "StdDev"};


    public APData(final String BSSID, final APLocation location) {

        this.BSSID = BSSID;
        this.locations.add(location);
    }

    public void add(final RSSIData item) {
        APLocation location = new APLocation(item, item.getRSSI());

        if (locations.contains(location)) {
            int index = locations.indexOf(location);
            locations.get(index).add(item.getRSSI());
        } else {
            this.locations.add(location);
        }
    }

    public List<APLocation> getLocations() {
        return locations;
    }

    public String getBSSID() {
        return BSSID;
    }   
    
    public static String toStringHeadings(String separator){
        
        String result =HEADINGS[0];
        for(int counter = 1; counter < HEADINGS.length; counter++){
            result += separator + HEADINGS[counter];
        }        
        return result;              
    }
}
