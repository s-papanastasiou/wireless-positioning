/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package datastorage;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author SST3ALBISG
 */
public class KNNTrialPoint extends KNNFloorPoint{
    
    private final long timestamp;
        
    public KNNTrialPoint (final long timestamp, final Location location, final String key, final double value){
        super(location, key, value);
        this.timestamp = timestamp;        
    }
    
    public KNNTrialPoint(final long timestamp, final Location location, final String keyX, final Double valueX, final String keyY, final Double valueY, final String keyW, final Double valueW) {
        super(location, keyX, valueX, keyY, valueY, keyW, valueW);
        this.timestamp = timestamp;
    }
    
    /**
     * Copy constructor
     * 
     * @param trialPoint 
     */
    public KNNTrialPoint (KNNTrialPoint trialPoint){
        super(trialPoint);
        this.timestamp = trialPoint.timestamp;        
    }
    
    public long getTimestamp(){
        return timestamp;
    }
    
    public boolean equals(long timestamp, String roomRef){
        boolean isMatch = false;
        
        if(this.timestamp==timestamp){
            isMatch = super.getRoomRef().equals(roomRef);            
        }
        return isMatch;
    }
    
    /**
     * Merges two HashMaps of KNNFloorPoints together - deep copy
     *
     * @param firstTrialPoints
     * @param secondTrialPoints
     * @return
     */
    public static List<KNNTrialPoint> merge(List<KNNTrialPoint> firstTrialPoints, List<KNNTrialPoint> secondTrialPoints) {
        
        List<KNNTrialPoint> mergedTrialPoints = new ArrayList<>();
        for(KNNTrialPoint point: firstTrialPoints){
            mergedTrialPoints.add(new KNNTrialPoint(point));
        }           
        
        for (KNNTrialPoint point: secondTrialPoints) {                        
            
            boolean isNoMatch = true;
            for (KNNTrialPoint mergedTrialPoint : mergedTrialPoints) {
                KNNFloorPoint mergedPoint = mergedTrialPoint;
                if(mergedPoint.equals(point)){
                    mergedPoint.add(point.getAttributes());
                    isNoMatch = false;
                    break;
                }
            }
            
            if(isNoMatch){                
                mergedTrialPoints.add(point);
            }
        }
        
        return mergedTrialPoints;
    }
}
