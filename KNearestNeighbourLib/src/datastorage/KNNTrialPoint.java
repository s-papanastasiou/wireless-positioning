/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package datastorage;

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
}
