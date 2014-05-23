/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package distancealgorithms;

import java.util.HashMap;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import general.AvgValue;

/**
 *
 * @author Gerg
 */
public class Chebyshev {
    public static double distance(final HashMap<String, AvgValue> scanAccessPoints, final HashMap<String, AvgValue> floorAccessPoints) {
        
        final double NO_BSSID_MATCH = 100;
                       
        SortedSet<Double> distances = new TreeSet<>();
        Set<String> scanBSSIDs = scanAccessPoints.keySet();
        
        for(String scanBSSID: scanBSSIDs)
        {
            if(floorAccessPoints.containsKey(scanBSSID))
            {
                AvgValue testValue = scanAccessPoints.get(scanBSSID);
                AvgValue floorValue = floorAccessPoints.get(scanBSSID);
                distances.add(Math.abs(testValue.getMean()-floorValue.getMean()));
                
            }
            else{ //poor distance given. otherwise points would be rewards for lacking data detected by the scan point.
                distances.add(NO_BSSID_MATCH);   //very harsh as one missing point will result in a max score              
            }                           
        }       
        
        //if(distances.isEmpty())
        //    distances.add(NO_BSSID_MATCH);
        
        return distances.last();
    }
}
