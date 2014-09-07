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
public class EuclidianSquared {
    
    public static double distance(final HashMap<String, AvgValue> trialAttributes, final HashMap<String, AvgValue> offlineAttributes) {
                
        final double NO_MATCH = Math.pow(100,2);
               
        double distance=0.0f;
        
        Set<String> keys = trialAttributes.keySet();
        
        for(String key: keys)
        {
            if(offlineAttributes.containsKey(key))
            {
                AvgValue trialValue = trialAttributes.get(key);
                AvgValue offlineValue = offlineAttributes.get(key);
                distance+=Math.pow(trialValue.getMean()-offlineValue.getMean(),2);
                
            }else{ //poor distance given. otherwise points would be rewards for lacking data detected by the scan point.
                distance+=NO_MATCH;
            }                            
        }                
        
        return distance;
    }
    
}
