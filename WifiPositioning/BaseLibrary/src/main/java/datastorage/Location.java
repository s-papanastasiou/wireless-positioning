/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package datastorage;

import general.Point;
import java.io.Serializable;
import java.util.Objects;

/**
 *
 * @author Gerg
 */
public class Location implements Serializable {
     
    protected static final String[] LOC_HEADINGS = {"Room", "X Ref", "Y Ref", "W Ref"};
    
    protected String room;
    protected int xRef;
    protected int yRef;
    protected int wRef;
    
    //Default location values
    public Location(){
        this.room = "Unknown";
        this.xRef = -1;
        this.yRef = -1;
        this.wRef = -1;
    }
  
    public Location(final String room, final int xRef, final int yRef, final int wRef){
        this.room = room;
        this.xRef = xRef;
        this.yRef = yRef;
        this.wRef = wRef;                
    }
    
    public Location(final Location location){
        this.room = location.room;
        this.xRef = location.xRef;
        this.yRef = location.yRef;
        this.wRef = location.wRef;
    }        
    
    public String getRoom() {
        return room;
    }

    public int getxRef() {
        return xRef;
    }

    public int getyRef() {
        return yRef;
    }

    public int getwRef() {
        return wRef;
    }
    
    public String getXYStr(){
        return xRef + "," + yRef;
    }
    
    public String getRoomRef()
    {
        return room + "-x" + xRef + "-y" + yRef + "-w" + wRef;
    }
    
    @Override
    public boolean equals(final Object o){
        
        boolean isEqual = false;
        
        if(o instanceof Location)
        {
            Location locationPoint = (Location) o;
            if(locationPoint.room.equals(this.room)&&locationPoint.xRef==this.xRef&&locationPoint.yRef==this.yRef&&locationPoint.wRef==this.wRef)
            {
                isEqual = true;
            }                
        }
        
        return isEqual;
    }    

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + Objects.hashCode(this.room);
        hash = 59 * hash + this.xRef;
        hash = 59 * hash + this.yRef;
        hash = 59 * hash + this.wRef;
        return hash;
    }
    
    @Override
    public String toString(){
        return room + " " + xRef + ":" + yRef + ":" + wRef;
    } 
    
    public String toString(String fieldSeparator){
        return room + fieldSeparator + xRef + fieldSeparator + yRef + fieldSeparator + wRef;
    } 
    
    //adjusts the references by the specified accuracy i.e. converts the values to a 1m grid spacing
    public String toString(String fieldSeparator, int accuracy){
        return room + fieldSeparator + (xRef*accuracy) + fieldSeparator + (yRef*accuracy) + fieldSeparator + (wRef*accuracy);
    }
    
   
    //convert a location to its 1m grid equivalent e.g. 1,1 on 5m grid will become 5,5 on 1m grid
    public static Location convert(Location location, int accuracy)
    {
        return new Location(location.room, location.xRef*accuracy, location.yRef*accuracy, location.wRef);
    }
    
    //Overwrites the wRef with 0.
    public static Location NoOrientationLocation(Location location){
         
         return new Location(location.getRoom(), location.getxRef(), location.getyRef(), 0);
    }
    
    public static String NoOrientationRoomRef(Location location){
                 
        return location.getRoom() + "-x" + location.getxRef() + "-y" + location.getyRef() + "-w0";
    }
    
    public Point getPoint(){        
        return new Point(this.xRef, this.yRef);        
    }
    
    public double distance (Point point) {

        double x, y;

        x = point.getX() - this.xRef;
        y = point.getY() - this.yRef;

        return Math.hypot(x, y);
    }
    
    /**
     * Scale co-ordinate by pixels.
     * 
     * @param xPixels
     * @param yPixels
     * @return 
     */
    public Point drawPoint(double xPixels, double yPixels){        
        return new Point(this.xRef * xPixels, this.yRef * yPixels);        
    }
    
    /**
     * Formatted heading to show the information relating to a location.
     * 
     * @param fieldSeparator Separator used between each heading.
     * @return 
     */
    public static String toStringHeadings(String fieldSeparator){
        
        String result =LOC_HEADINGS[0];
        for(int counter = 1; counter < LOC_HEADINGS.length; counter++){
            result += fieldSeparator + LOC_HEADINGS[counter];
        }        
        return result;              
    }
    
}
