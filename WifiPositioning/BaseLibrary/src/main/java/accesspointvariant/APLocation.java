/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package accesspointvariant;

import datastorage.Location;
import general.AvgValue;

/**
 *
 * @author Gerg
 */
public class APLocation extends Location {

    private AvgValue avgRSSI;
    
    public APLocation(final Location location, final int rssiValue) {
        super(location);
        this.avgRSSI = new AvgValue(rssiValue);
    }

    public void add(final int rssiValue) {
        avgRSSI.add(rssiValue);
    }

    public double mean() {
        return avgRSSI.getMean();
    }

    public int frequency() {
        return avgRSSI.getFrequency();
    }

    public double stdDev() {
        return avgRSSI.getStdDev();
    }
    
    public AvgValue getAvgRSSI(){
        return avgRSSI;
    }

    @Override
    public String toString() {
        
        return super.toString() + ":" + avgRSSI.getMean() + ":" + avgRSSI.getFrequency() + ":" + avgRSSI.getTotal() + ":" + avgRSSI.getStdDev();
    }

    @Override
    public String toString(String separator) {
        return super.toString(separator) + separator + avgRSSI.getMean() + separator + avgRSSI.getFrequency() + separator + avgRSSI.getTotal() + separator + avgRSSI.getStdDev();
    }
    
    public String toStringLocation(){
        return super.toString();
    }

    //adjusts the references by the specified accuracy i.e. converts the values to a 1m grid spacing
    @Override
    public String toString(String separator, int accuracy) {
        return super.toString(separator, accuracy) + separator + avgRSSI.getMean() + separator + avgRSSI.getFrequency() + separator + avgRSSI.getTotal() + separator + avgRSSI.getStdDev();
    }
    
    
}
