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
        this.room = "Unknown";
        this.xRef = -1;
        this.yRef = -1;
        this.wRef = -1;
        this.globalX = -1;
        this.globalY = -1;
        this.drawX = -1;
        this.drawY = -1;
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
            if (locationPoint.room.equals(this.room) && locationPoint.xRef == this.xRef && locationPoint.yRef == this.yRef && locationPoint.wRef == this.wRef && locationPoint.globalX == this.globalX && locationPoint.globalY == this.globalY) {
                isEqual = true;
            }
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
        int hash = 3;
        hash = 67 * hash + Objects.hashCode(this.room);
        hash = 67 * hash + this.xRef;
        hash = 67 * hash + this.yRef;
        hash = 67 * hash + this.wRef;
        hash = 67 * hash + (int) (Double.doubleToLongBits(this.globalX) ^ (Double.doubleToLongBits(this.globalX) >>> 32));
        hash = 67 * hash + (int) (Double.doubleToLongBits(this.globalY) ^ (Double.doubleToLongBits(this.globalY) >>> 32));
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
