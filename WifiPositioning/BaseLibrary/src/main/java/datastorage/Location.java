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

    /**
     * Constructor - default values -1 and Unknown.
     */
    public Location() {
        this.room = "Unknown";
        this.xRef = -1;
        this.yRef = -1;
        this.wRef = -1;
    }

    /**
     * Constructor
     *
     * @param room Label of the room
     * @param xRef x reference
     * @param yRef y reference
     * @param wRef w reference
     */
    public Location(final String room, final int xRef, final int yRef, final int wRef) {
        this.room = room;
        this.xRef = xRef;
        this.yRef = yRef;
        this.wRef = wRef;
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
            if (locationPoint.room.equals(this.room) && locationPoint.xRef == this.xRef && locationPoint.yRef == this.yRef && locationPoint.wRef == this.wRef) {
                isEqual = true;
            }
        }

        return isEqual;
    }   

    /**
     * Auto-generated hashCode.
     * 
     * @return 
     */
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + Objects.hashCode(this.room);
        hash = 59 * hash + this.xRef;
        hash = 59 * hash + this.yRef;
        hash = 59 * hash + this.wRef;
        return hash;
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
     * Scales the references by the specified accuracy i.e. converts the values to a 1m grid spacing. e.g. 1,1 on 5m grid will become 5,5 on 1m grid
     * 
     * @param fieldSeparator Character to use as separator.
     * @param accuracy Accuracy to scale the references.
     * @return 
     */
    public String toString(String fieldSeparator, int accuracy) {
        return room + fieldSeparator + (xRef * accuracy) + fieldSeparator + (yRef * accuracy) + fieldSeparator + (wRef * accuracy);
    }
   
    /**
     * Convert a location to its 1m grid equivalent e.g. 1,1 on 5m grid will become 5,5 on 1m grid
     * 
     * @param location Original location to convert.
     * @param accuracy Accuracy to scale the location.
     * @return 
     */
    public static Location convert(Location location, int accuracy) {
        return new Location(location.room, location.xRef * accuracy, location.yRef * accuracy, location.wRef);
    }
    
    /**
     * Copies the location and overwrites the w reference with 0.
     * 
     * @param location Location to adjust.
     * @return 
     */
    public static Location NoOrientationLocation(Location location) {

        return new Location(location.getRoom(), location.getxRef(), location.getyRef(), 0);
    }

    /**
     * String representation with the w reference suppressed to 0.
     * Static function for distinct usage from subclasses.
     * 
     * @param location Location to extract information.
     * @return 
     */
    public static String NoOrientationRoomRef(Location location) {

        return location.getRoom() + "-x" + location.getxRef() + "-y" + location.getyRef() + "-w0";
    }

    /**
     * Converts the location x and y reference into a point.
     * 
     * @return 
     */    
    public Point getPoint() {
        return new Point(this.xRef, this.yRef);
    }

    /**
     * Calculates the distance between the location and this location.
     * 
     * @param location Location to compare.
     * @return 
     */
    public double distance(Location location) {

        double x, y;

        x = location.xRef - this.xRef;
        y = location.yRef - this.yRef;

        return Math.hypot(x, y);
    }
    
    /**
     * Calculates the distance between the point and this location.
     * 
     * @param point Point to compare.
     * @return 
     */
    public double distance(Point point) {

        double x, y;

        x = point.getX() - this.xRef;
        y = point.getY() - this.yRef;

        return Math.hypot(x, y);
    }

    /**
     * Scale coordinate by supplied pixels.
     *
     * @param xPixels Scaling factor for x reference.
     * @param yPixels Scaling factor for y reference.
     * @return
     */
    public Point drawPoint(double xPixels, double yPixels) {
        return new Point(this.xRef * xPixels, this.yRef * yPixels);
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
