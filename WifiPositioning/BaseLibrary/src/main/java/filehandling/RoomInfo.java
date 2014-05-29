/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package filehandling;

import datastorage.Location;
import general.Point;
import general.Rectangle;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Information about rooms within a floor.
 * TODO Adapt to having spacing different to 1m.
 *
 * @author Greg Albiston
 */
public class RoomInfo {

    private static final Logger logger = LoggerFactory.getLogger(RoomInfo.class);

    private static final String DEFAULT_NAME = "Unknown";
    private static final double DEFAULT_WIDTH = 100;
    private static final double DEFAULT_HEIGHT = 100;
    private static final double DEFAULT_UNITS = 1;
    public static final String[] HEADINGS = {"Name", "Width", "Height", "Left", "Top", "Right", "Bottom", "isRoom", "GlobalX", "GlobalY"};
    protected String name;
    protected double widthPhysical;
    protected double heightPhysical;
    protected double widthUnits;
    protected double heightUnits;
    protected Rectangle drawRoom;
    protected boolean isRoom;
    protected double globalX;  //offset to place the room on global co-ordinates, used to measure distance between two points
    protected double globalY;  //offset to place the room on global co-ordinates, used to measure distance between two points

    /**
     * Constructor
     * 
     * @param parts String array of elements to build class. See HEADINGS for
     * order.
     */
    public RoomInfo(final String[] parts) {

        name = parts[0];
        widthPhysical = Double.parseDouble(parts[1]);
        heightPhysical = Double.parseDouble(parts[2]);

        int startX = Integer.parseInt(parts[3]);
        int startY = Integer.parseInt(parts[4]);
        int endX = Integer.parseInt(parts[5]);
        int endY = Integer.parseInt(parts[6]);
        isRoom = Boolean.parseBoolean(parts[7]);

        drawRoom = new Rectangle(startX, startY, endX - startX, endY - startY);
        widthUnits = Math.round(drawRoom.width / widthPhysical);
        heightUnits = Math.round(drawRoom.height / heightPhysical);

        globalX = Double.parseDouble(parts[8]);
        globalY = Double.parseDouble(parts[9]);
    }

    /**
     * Constructor - default values.
     */
    public RoomInfo() {
        name = DEFAULT_NAME;
        widthPhysical = DEFAULT_WIDTH;
        heightPhysical = DEFAULT_HEIGHT;
        drawRoom = new Rectangle();
        widthUnits = DEFAULT_UNITS;
        heightUnits = DEFAULT_UNITS;
        isRoom = false;
        globalX = 0;
        globalY = 0;
    }

    /**
     * Rectangle used to draw the room.
     * 
     * @return 
     */
    public Rectangle getRoomRect() {
        return drawRoom;
    }
    
    /**
     * Rectangle drawn around a point.     
     * 
     * @param xRef x reference of the point
     * @param yRef y reference of the point
     * @return 
     */
    public Rectangle getPointRect(final int xRef, final int yRef) {

        Rectangle rect = new Rectangle();
        rect.x = (int) Math.round((drawRoom.x + (xRef * widthUnits) - (widthUnits / 2))); // drawRoom.x to get top left corner, offset by xRef and subtract half the size of one unit
        rect.y = (int) Math.round((drawRoom.y + (yRef * heightUnits) - (heightUnits / 2)));
        rect.width = (int) widthUnits;
        rect.height = (int) heightUnits;

        return rect;
    }

    /**
     * Get name of the room.
     * 
     * @return 
     */
    public String getName() {
        return name;
    }

    /**
     * Get physical width of the room.
     * 
     * @return 
     */
    public double getWidth() {
        return widthPhysical;
    }

    /**
     * Get physical height of the room.
     * 
     * @return 
     */
    public double getHeight() {
        return heightPhysical;
    }

    /**
     * Whether the room represents an enclosed room or open ended corridor.
     * Affects whether the Android Detector starts at 0 (corridor) or 1 (room) for references.
     * 
     * @return True, if a room. False, if a corridor.
     */
    public int isRoom() {
        return isRoom ? 1 : 0;
    }
    
    /**
     * Find a point on a floor based on reference for a room. 
     *
     * @param xRef
     * @param yRef
     * @return
     */
    public Point getPoint(final int xRef, final int yRef) {

        Point point = new Point();
        point.setX((int) (drawRoom.x + (xRef * widthUnits)));
        point.setY((int) (drawRoom.y + (yRef * heightUnits)));

        return point;
    }

    /**
     * Convert a point into a location within the room. Assumes that working on
     * a 1m grid
     *
     * @param point Point to be converted.
     * @return
     */
    public Location convertLocation(final Point point) {
        return convertLocation(point.getX(), point.getY());
    }

    /**
     * Convert a point into a location within the room.
     *
     * @param x x-coordinate to convert.
     * @param y y-coordinate to convert.
     * @return
     */
    public Location convertLocation(final double x, final double y) {

        int xRef = (int) ((((x - drawRoom.x) + 0.5) / widthUnits));
        int yRef = (int) ((((y - drawRoom.y) + 0.5) / heightUnits));

        //adjust for rooms not being able to be zero based.
        if (isRoom) {
            if (xRef == 0) {
                xRef = 1;
            }
            if (yRef == 0) {
                yRef = 1;
            }
        }

        return new Location(name, xRef, yRef, 1);
    }

    /**
     * Convert a location to its global 
     * Assumes working on a 1m grid.
     * 
     * @param location
     * @return
     */
    public Point convertGlobalPoint(Location location) {

        return new Point(globalX + location.getxRef(), globalY + location.getyRef());
    }

    /**
     * Load room information from file to a map.
     * 
     * @param roomInfoFile File to load information.
     * @param fieldSeparator Field separator between columns.
     * @return 
     */
    public static HashMap<String, RoomInfo> load(final File roomInfoFile, final String fieldSeparator) {

        HashMap<String, RoomInfo> roomInfo = new HashMap<>();

        try {
            String line;
            String[] parts;
            try (BufferedReader roomReader = new BufferedReader(new InputStreamReader(new FileInputStream(roomInfoFile)))) {

                line = roomReader.readLine();
                parts = line.split(fieldSeparator);
                if (headerCheck(parts)) {
                    while ((line = roomReader.readLine()) != null) {
                        parts = line.split(fieldSeparator);
                        roomInfo.put(parts[0], new RoomInfo(parts));
                    }
                } else {
                    logger.error("RoomInfo headings not as expected.");
                }
            }
        } catch (FileNotFoundException ex) {
            logger.error(ex.getMessage());
        } catch (IOException ex) {
            logger.error(ex.getMessage());
        }
        return roomInfo;
    }

    /**
     * Load room information from file.
     * Assumes comma used for field separator.
     *
     * @param roomInfoFilename Filename of the file to load.
     * @return 
     */
    public static HashMap<String, RoomInfo> load(final String roomInfoFilename) {

        File roomInfoFile = new File(roomInfoFilename);

        return load(roomInfoFile, ",");
    }

    /**
     * Load room information from file.
     * Assumes comma used for field separator.
     *
     * @param roomInfoFile File to load room information.     
     * @return 
     */
    public static HashMap<String, RoomInfo> load(final File roomInfoFile) {
        return load(roomInfoFile, ",");
    }

    /**
     * Load room information from file to a list.
     * 
     * @param roomInfoFile File to load information.
     * @param fieldSeparator Field separator between columns.
     * @return 
     */
    public static List<RoomInfo> loadList(final File roomInfoFile, final String fieldSeparator) {

        List<RoomInfo> roomInfo = new ArrayList<>();

        try {
            String line;
            String[] parts;
            try (BufferedReader roomReader = new BufferedReader(new FileReader(roomInfoFile))) {

                line = roomReader.readLine();
                parts = line.split(fieldSeparator);
                if (headerCheck(parts)) {
                    while ((line = roomReader.readLine()) != null) {
                        parts = line.split(fieldSeparator);
                        roomInfo.add(new RoomInfo(parts));
                    }
                } else {
                    logger.error("RoomInfo headings not as expected.");
                }
            }
        } catch (FileNotFoundException ex) {
            logger.error(ex.getMessage());
        } catch (IOException ex) {
            logger.error(ex.getMessage());
        }
        return roomInfo;
    }

    /**
     * Load room information from file to a list.
     * Assumes comma used for field separator.
     *
     * @param roomInfoFile File to load room information.     
     * @return 
     */
    public static List<RoomInfo> loadList(final File roomInfoFile) {
        return loadList(roomInfoFile, ",");
    }

    /**
     * Checks whether the supplied array contains the expected headings for
     * geomagnetic data.
     *
     * @param parts String array to be checked.
     * @return
     */
    private static boolean headerCheck(String[] parts) {

        return Arrays.equals(HEADINGS, parts);
    }

    /**
     * Provides a list with one room set to default values.
     * 
     * @return 
     */
    public static List<RoomInfo> defaultList() {
        List<RoomInfo> roomInfo = new ArrayList<>();

        roomInfo.add(new RoomInfo());

        return roomInfo;
    }

    /**
     * Finds the location that corresponds to the point.
     * 
     * @param point Point to find.
     * @param roomInfo Information about the rooms on the floor.
     * @return 
     */
    public static Location searchLocation(Point point, HashMap<String, RoomInfo> roomInfo) {
        Location location = new Location();

        Set<String> keys = roomInfo.keySet();

        for (String key : keys) {
            RoomInfo room = roomInfo.get(key);
            Rectangle rect = room.getRoomRect();
            if (rect.contains(point)) {
                location = room.convertLocation(point);
                break;
            }
        }

        return location;
    }

    /**
     * Finds the point that corresponds to the location.
     * 
     * @param location Location to find.
     * @param roomInfo Information about the rooms on the floor.
     * @return 
     */
    public static Point searchPoint(Location location, HashMap<String, RoomInfo> roomInfo) {
        RoomInfo room = roomInfo.get(location.getRoom());
        return room.getPoint(location.getxRef(), location.getyRef());
    }

    //
    /**
     * Calculates distance in metres between two locations using the global
     * offsets. 
     * Assumes that working on a 1m grid
     *
     * @param start Start point
     * @param end End point
     * @param roomInfo Information about rooms on the floor.
     * @return
     */
    public static double distanceMetres(Location start, Location end, HashMap<String, RoomInfo> roomInfo) {

        RoomInfo startRoom = roomInfo.get(start.getRoom());
        Point startPoint = startRoom.convertGlobalPoint(start);

        RoomInfo endRoom = roomInfo.get(end.getRoom());
        Point endPoint = endRoom.convertGlobalPoint(end);

        double xDist = startPoint.getX() - endPoint.getX();
        double yDist = startPoint.getY() - endPoint.getY();
        double distance = Math.hypot(xDist, yDist);

        return distance;
    }

    /**
     * Calculates pixel distance between two points.
     *
     * @param start Start point
     * @param end End point
     * @return
     */
    public static double distancePixel(Point start, Point end) {

        double xDist = start.getX() - end.getX();
        double yDist = start.getY() - end.getY();
        double distance = Math.hypot(xDist, yDist);

        return distance;
    }

}
