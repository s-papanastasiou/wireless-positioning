/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package datastorage;

/**
 *
 * @author Greg Albiston
 */
public class ResultLocation extends Location implements Comparable {
    
    private final double result;
    private final String roomReference;
    
    public ResultLocation(Location location, double result, String roomReference){
        super(location);
        this.result = result;
        this.roomReference = roomReference;
    }

    public double getResult() {
        return result;
    }

    @Override
    public String getRoomRef() {
        return roomReference;
    }        

    
    @Override
    public int compareTo(Object o) {
        
    int outcome=-1;
        
        if(o instanceof ResultLocation)
        {
            
                ResultLocation resLocation = (ResultLocation)o;
                if(this.result>resLocation.result)
                    outcome = 1;
                else if(this.result<resLocation.result)
                    outcome = -1;
                else
                    if(roomReference.equals(resLocation.roomReference))
                        outcome = 0;
        }
        else
        {
            if(o==null)
                throw new NullPointerException("Null passed for Result Location comparison");
            else
                throw new ClassCastException("Only comparison with other Result Locations supported");
        }
        
        return outcome;
    }        
    
    @Override
    public String toString(){
        return super.toString() + " " + roomReference + " " + result;
    }
    
}
