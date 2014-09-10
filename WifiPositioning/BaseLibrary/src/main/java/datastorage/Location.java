/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package datastorage;

import general.Point;
import java.io.Serializable;
import java.util.Objects;

/**
 * Stores the essential information required on a location. Superclass for
 * various other classes.
 *
 * @author Greg Albiston
 */
public class Location implements Serializable {

    protected static final String[] LOC_HEADINGS = {"Room", "X Ref", "Y Ref", "W Ref"};

    private static final String unknownName = "Unknown";
    private static final int unknownValue = -1;
    
    protected String room;
    protected int xRef;
    protected int yRef;
    protected int wRef;    
    protected double globalX;
    protected double globalY;
    protected double drawX;
    protected double drawY;
    
    /**
     * Constructor - default values -1 and Unknown.
     */
    public Location() {
        this.room = unknownName;
        this.xRef = unknownValue;
        this.yRef = unknownValue;
        this.wRef = unknownValue;
        this.globalX = unknownValue;
        this.globalY = unknownValue;
        this.drawX = unknownValue;
        this.drawY = unknownValue;
    }

    /**
     * Constructor
     *
     * @param room Label of the room
     * @param xRef x reference
     * @param yRef y reference
     * @param wRef w reference
     */
    private Location(final Location location, final int wRef) {
        this.room = location.room;
        this.xRef = location.xRef;
        this.yRef = location.yRef;
        this.wRef = wRef;
        this.globalX = location.globalX;
        this.globalY = location.globalY;
        this.drawX = location.drawX;
        this.drawY = location.drawY;
    }

    /**
     * Constructor
     *
     * @param room Label of the room
     * @param xRef x reference
     * @param yRef y reference
     * @param wRef w reference
     * @param globalX
     * @param globalY
     * @param drawX
     * @param drawY
     */
    public Location(final String room, final int xRef, final int yRef, final int wRef, double globalX, double globalY, double drawX, double drawY) {
        this.room = room;
        this.xRef = xRef;
        this.yRef = yRef;
        this.wRef = wRef;
        this.globalX = globalX;
        this.globalY = globalY;
        this.drawX = drawX;
        this.drawY = drawY;
    }
    
    /**
     * Copy Constructor
     *
     * @param location
     * @param globalX
     * @param globalY
     * @param drawY
     * @param drawX
     */
    public Location(final Location location, double globalX, double globalY, double drawX, double drawY) {
        this.room = location.room;
        this.xRef = location.xRef;
        this.yRef = location.yRef;
        this.wRef = location.wRef;
        this.globalX = globalX;
        this.globalY = globalY;
        this.drawX = drawX;
        this.drawY = drawY;
    }

    /**
     * Copy Constructor
     *
     * @param location
     */
    public Location(final Location location) {
        this.room = location.room;
        this.xRef = location.xRef;
        this.yRef = location.yRef;
        this.wRef = location.wRef;
        this.globalX = location.globalX;
        this.globalY = location.globalY;
        this.drawX = location.drawX;
        this.drawY = location.drawY;
    }

    /**
     * Get room label.
     *
     * @return
     */
    public String getRoom() {
        return room;
    }

    /**
     * Get x reference.
     *
     * @return
     */
    public int getxRef() {
        return xRef;
    }

    /**
     * Get y reference.
     *
     * @return
     */
    public int getyRef() {
        return yRef;
    }

    /**
     * Get w reference.
     *
     * @return
     */
    public int getwRef() {
        return wRef;
    }

    public double getGlobalX() {
        return globalX;
    }

    public double getGlobalY() {
        return globalY;
    }

    public double getDrawX() {
        return drawX;
    }

    public double getDrawY() {
        return drawY;
    }

    /**
     * Get x and y reference as string.
     *
     * @return
     */
    public String getXYStr() {
        return xRef + "," + yRef;
    }

    /**
     * Get reference for the room.
     *
     * @return
     */
    public String getRoomRef() {
        return room + "-x" + xRef + "-y" + yRef + "-w" + wRef;
    }
    
    /**
     * Tests whether the room information corresponds to an unknown room.
     * Global and draw values could be known.
     * @return True if the room is unknown.
     */
    public boolean isUnknown(){
        return room.equals(unknownName) &&  xRef==unknownValue && yRef==unknownValue && wRef ==unknownValue;        
    }
    
    /**
     * Tests whether the room information corresponds to an unknown room but has meaningful global information.     
     * @return True if the room is unknown.
     */
    public boolean isUnknownGlobal(){
        return room.equals(unknownName) &&  xRef==unknownValue && yRef==unknownValue && wRef ==unknownValue && globalX !=unknownValue && globalY !=unknownValue;
    }

    /**
     * Tests whether the room information corresponds to an unknown room but has meaningful pixel information. 
     * Global and draw values could be known.
     * @return True if the room is unknown.
     */
    public boolean isUnknownPixel(){
        return room.equals(unknownName) &&  xRef==unknownValue && yRef==unknownValue && wRef ==unknownValue && drawX !=unknownValue && drawY !=unknownValue;
    }
    
    /**
     * Equals operation
     *
     * @param o Object to compare equality.
     * @return
     */
    @Override
    public boolean equals(final Object o) {

        boolean isEqual = false;

        if (o instanceof Location) {
            Location locationPoint = (Location) o;
            isEqual = locationPoint.room.equals(this.room) && locationPoint.xRef == this.xRef && locationPoint.yRef == this.yRef && locationPoint.wRef == this.wRef && locationPoint.globalX == this.globalX && locationPoint.globalY == this.globalY && locationPoint.drawX == this.drawX && locationPoint.drawY == this.drawY;
        }

        return isEqual;
    }

     /**
     * Auto-generated hashcode
     *
     * @return
     */    
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 47 * hash + Objects.hashCode(this.room);
        hash = 47 * hash + this.xRef;
        hash = 47 * hash + this.yRef;
        hash = 47 * hash + this.wRef;
        hash = 47 * hash + (int) (Double.doubleToLongBits(this.globalX) ^ (Double.doubleToLongBits(this.globalX) >>> 32));
        hash = 47 * hash + (int) (Double.doubleToLongBits(this.globalY) ^ (Double.doubleToLongBits(this.globalY) >>> 32));
        hash = 47 * hash + (int) (Double.doubleToLongBits(this.drawX) ^ (Double.doubleToLongBits(this.drawX) >>> 32));
        hash = 47 * hash + (int) (Double.doubleToLongBits(this.drawY) ^ (Double.doubleToLongBits(this.drawY) >>> 32));
        return hash;
    }

    public Point getGlobalPoint() {
        return new Point(globalX, globalY);
    }

    public Point getDrawPoint() {
        return new Point(drawX, drawY);
    } 
    
    /**
     * Euclidean distance between two locations
     *
     * @param location
     * @return
     */
    public double distance(Location location) {
        
        double x = this.globalX - location.globalX;
        double y = this.globalY - location.globalY;
        return Math.hypot(x, y);        
    }
    
    /**
     * String representation of location information.
     *
     * @return
     */
    @Override
    public String toString() {
        return room + " " + xRef + ":" + yRef + ":" + wRef;
    }

    /**
     * String representation of location information using provided separator.
     *
     * @param fieldSeparator Character to use as separator.
     * @return
     */
    public String toString(String fieldSeparator) {
        return room + fieldSeparator + xRef + fieldSeparator + yRef + fieldSeparator + wRef;
    }

    /**
     * Copies the location and overwrites the w reference with 0.
     *
     * @param location Location to adjust.
     * @return
     */
    public static Location NoOrientationLocation(Location location) {

        return new Location(location, 0);
    }

    /**
     * String representation with the w reference suppressed to 0. Static
     * function for distinct usage from subclasses.
     *
     * @param location Location to extract information.
     * @return
     */
    public static String NoOrientationRoomRef(Location location) {

        return location.getRoom() + "-x" + location.getxRef() + "-y" + location.getyRef() + "-w0";
    }   

    /**
     * Creates a location with unknown information but preserves the supplied global values.
     * @param globalX
     * @param globalY
     * @return 
     */
    public static Location createUnknownGlobalLocation(double globalX, double globalY){
        Location location = new Location();
        location.globalX = globalX;
        location.globalY = globalY;
        
        return location;
    }
    
    /**
     * Creates a location with unknown information but preserves the supplied pixel values.
     * @param drawX
     * @param drawY
     * @return 
     */
    public static Location createUnknownPixelLocation(double drawX, double drawY){
        Location location = new Location();
        location.drawX = drawX;
        location.drawY = drawY;
        
        return location;
    }
    
    /**
     * Creates a location with unknown information but preserves the supplied global values.
     * @param globalX
     * @param globalY
     * @param drawX
     * @param drawY
     * @return 
     */
    public static Location createUnknownLocation(double globalX, double globalY, double drawX, double drawY){
        Location location = new Location();
        location.globalX = globalX;
        location.globalY = globalY;
        location.drawX = drawX;
        location.drawY = drawY;
        
        return location;
    }
    
    /**
     * Formatted heading to show the information relating to a location.
     *
     * @param fieldSeparator Separator used between each heading.
     * @return
     */
    public static String toStringHeadings(String fieldSeparator) {

        String result = LOC_HEADINGS[0];
        for (int counter = 1; counter < LOC_HEADINGS.length; counter++) {
            result += fieldSeparator + LOC_HEADINGS[counter];
        }
        return result;
    }

}
