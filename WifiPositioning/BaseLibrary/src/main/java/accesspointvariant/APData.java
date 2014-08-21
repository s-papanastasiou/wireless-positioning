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
    private Double mean;
    private Double variance;
    private Double stdDev;
    private Integer itemCount;
    
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
        this.stdDev = 0.0;
        this.mean = location.mean();
        this.itemCount = 1;
    }

    /**
     * Add a new piece of RSSI data to a new or existing location.
     * @param item RSSI data for the BSSID
     */
    public void add(final RSSIData item) {
        APLocation location = new APLocation(item, item.getRSSI());

        itemCount++;
        
        int locFreq;
        double locMean;
        if (locations.contains(location)) {
            int index = locations.indexOf(location);
            locations.get(index).add(item.getRSSI());
            locFreq = locations.get(index).frequency();
            locMean = locations.get(index).mean();
        } else {
            this.locations.add(location);
            locFreq = location.frequency();
            locMean = location.mean();
        }
        
        if(locFreq>maxRSSIFrequency)
            maxRSSIFrequency = locFreq;
        
        if(locMean>maxRSSIMean)
            maxRSSIMean = locMean;
        
        if(locMean<minRSSIMean)
            minRSSIMean = locMean;
        
        
        mean = calcTotal() / locations.size();        
        variance = calcMeanDiff() / locations.size();
        stdDev = Math.sqrt(variance); //Square root of the total mean differences divided by the number of values        
    }
    
    private Double calcTotal(){
        Double total = 0.0;
        for (APLocation location : locations) {
            total += location.mean();            
        }
        return total;
    }
    
    /**
     * Calculates the mean difference between all the values. Used to find the
     * variance.
     *
     * @return
     */
    private Double calcMeanDiff() {
        Double meanDiff = 0.0;

        for (APLocation location : locations) {
            Double value = location.mean();
            meanDiff += Math.pow(value - mean, 2);
        }

        return meanDiff;
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
     * Mean RSSI locations for this access point.
     * @return 
     */
    public Double getMean() {
        return mean;
    }

    /**
     * Variance of the mean RSSI locations for this access point.
     * @return 
     */
    public Double getVariance() {
        return variance;
    }

    /**
     * Standard deviation of the mean RSSI locations for this access point.
     * @return 
     */
    public Double getStdDev() {
        return stdDev;
    }

    /**
     * Count of items that have been added for this access point.
     * @return 
     */
    public Integer getItemCount() {
        return itemCount;
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
