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
 * @author Greg Albiston
 */
public class Chebyshev {
    public static double distance(final HashMap<String, AvgValue> trialAttributes, final HashMap<String, AvgValue> offlineAttributes) {
        
        final double NO_MATCH = 1000;
                       
        SortedSet<Double> distances = new TreeSet<>();
        Set<String> keys = trialAttributes.keySet();
        
        for(String key: keys)
        {
            if(offlineAttributes.containsKey(key))
            {
                AvgValue trialValue = trialAttributes.get(key);
                AvgValue offlineValue = offlineAttributes.get(key);
                distances.add(Math.abs(trialValue.getMean()-offlineValue.getMean()));
                
            }
            else{ //poor distance given. otherwise points would be rewards for lacking data detected by the scan point.
                distances.add(NO_MATCH);   //very harsh as one missing point will result in a max score              
            }                           
        }       
        
        //if(distances.isEmpty())
        //    distances.add(NO_BSSID_MATCH);
        
        return distances.last();
    }
}
