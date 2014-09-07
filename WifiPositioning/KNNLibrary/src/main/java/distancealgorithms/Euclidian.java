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
 * @author Greg Albiston
 */
public class Euclidian {
    //Distance equations based on http://www.statsoft.com/textbook/k-nearest-neighbors/
    public static double distance( final HashMap<String, AvgValue> trailAttributes, final HashMap<String, AvgValue> offlineAtrributes) {
                
        final double NO_MATCH = Math.pow(100,2);
               
        double distance=0.0f;
        
        Set<String> keys = trailAttributes.keySet();
        
        for(String key: keys)
        {
            if(offlineAtrributes.containsKey(key))
            {
                AvgValue trialValue = trailAttributes.get(key);
                AvgValue offlineValue = offlineAtrributes.get(key);
                distance+=Math.pow(trialValue.getMean()-offlineValue.getMean(),2);
                
            }else{ //poor distance given. otherwise points would be rewards for lacking data detected by the scan point.
                distance+=NO_MATCH;
            }                            
        }        
        distance = Math.sqrt(distance);                
        
        return distance;
    }
}
