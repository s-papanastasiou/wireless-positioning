/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package distancealgorithms;

import java.util.HashMap;
import java.util.Set;
import general.AvgValue;

/**
 *
 * @author Gerg
 */
public class Manhatten {
    public static double distance(final HashMap<String, AvgValue> scanAccessPoints, final HashMap<String, AvgValue> floorAccessPoints) {
        
        final double NO_BSSID_MATCH = 100;
               
        double distance=0.0f;
        
        Set<String> scanBSSIDs = scanAccessPoints.keySet();
        
        for(String scanBSSID: scanBSSIDs)
        {
            if(floorAccessPoints.containsKey(scanBSSID))
            {
                AvgValue testValue = scanAccessPoints.get(scanBSSID);
                AvgValue floorValue = floorAccessPoints.get(scanBSSID);
                distance+=Math.abs(testValue.getMean()-floorValue.getMean());
                
            }else{ //poor distance given. otherwise points would be rewards for lacking data detected by the scan point.
                distance+=NO_BSSID_MATCH;
            }                            
        }             
        
        return distance;
    }
}
