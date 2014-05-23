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
 *
 * @author Greg Albiston
 */
public class RoomInfo {

    private static final Logger logger = LoggerFactory.getLogger(RoomInfo.class);
    
    private static final String DEFAULT_NAME = "Unknown";
    private static final double DEFAULT_WIDTH = 100;
    private static final double DEFAULT_HEIGHT = 100;
    private static final double DEFAULT_UNITS = 1;
    public static final String[] header = {"Name", "Width", "Height", "Left", "Top", "Right", "Bottom", "isRoom", "GlobalX", "GlobalY"};
    protected String name;
    protected double widthPhysical;
    protected double heightPhysical;
    protected double widthUnits;
    protected double heightUnits;
    protected Rectangle drawRoom;
    protected boolean isRoom;    
    protected double globalX;  //offset to place the room on global co-ordinates, used to measure distance between two points
    protected double globalY;  //offset to place the room on global co-ordinates, used to measure distance between two points

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

    public Rectangle getRoomRect() {
        return drawRoom;
    }

    //assumes that working on a 1m grid
    public Rectangle getPointRect(final int xRef, final int yRef) {

        Rectangle rect = new Rectangle();
        rect.x = (int) Math.round((drawRoom.x + (xRef * widthUnits) - (widthUnits / 2))); // drawRoom.x to get top left corner, offset by xRef and subtract half the size of one unit
        rect.y = (int) Math.round((drawRoom.y + (yRef * heightUnits) - (heightUnits / 2)));
        rect.width = (int) widthUnits;   //width of one unit for the square
        rect.height = (int) heightUnits;

        return rect;
    }

    public String getName() {
        return name;
    }

    public double getWidth() {
        return widthPhysical;
    }

    public double getHeight() {
        return heightPhysical;
    }

    public int isRoom() {
        return isRoom ? 1 : 0;
    }

    //assumes that working on a 1m grid
    public Point getPoint(final int xRef, final int yRef) {

        Point point = new Point();
        point.setX((int) (drawRoom.x + (xRef * widthUnits)));
        point.setY((int) (drawRoom.y + (yRef * heightUnits)));

        return point;
    }  

    public Location findLocation(String room, final Point point) {
        return findLocation(room, point.getX(), point.getY());
    }
    
    //assumes that working on a 1m grid
    public Location findLocation(String room, final double x, final double y) {

        int xRef = (int) ((((x - drawRoom.x) + 0.5) / widthUnits) );
        int yRef = (int) ((((y - drawRoom.y) + 0.5) / heightUnits) );

        //adjust for rooms not being able to be zero based.
        if(isRoom)
        {
            if(xRef==0)
                xRef=1;
            if(yRef==0)
                yRef=1;
        }
        
        return new Location(room, xRef, yRef, 1);
    }
    
    //assuming that all points are on a 1m grid spacing
    public Point getGlobalCoord(int xRef, int yRef){
        
        return new Point(globalX + xRef, globalY + yRef);                        
    }

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
    
    public static HashMap<String, RoomInfo> load(final String roomInfoFilename) {
        
        File roomInfoFile = new File(roomInfoFilename);
        
        return load(roomInfoFile, ",");
    }

    public static HashMap<String, RoomInfo> load(final File roomInfoFile) {
        return load(roomInfoFile, ",");
    }

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
    
    public static List<RoomInfo> loadList(final File roomInfoFile) {
        return loadList(roomInfoFile, ",");
    }
    
    private static boolean headerCheck(String[] parts) {

        return Arrays.equals(header, parts);
    }

    public static List<RoomInfo> defaultList() {
        List<RoomInfo> roomInfo = new ArrayList<>();

        roomInfo.add(new RoomInfo());

        return roomInfo;
    }        
    
    public static Location searchLocation(Point point, HashMap<String, RoomInfo> roomInfo)
    {
        Location location = new Location();

        Set<String> keys = roomInfo.keySet();

        for (String key : keys) {
            RoomInfo room = roomInfo.get(key);
            Rectangle rect = room.getRoomRect();
            if (rect.contains(point)) {
                location = room.findLocation(key, point);
                break;
            }
        }

        return location;        
    }
    
    public static Point searchPoint(Location location, HashMap<String, RoomInfo> roomInfo)
    {
        RoomInfo room = roomInfo.get(location.getRoom());
        return room.getPoint(location.getxRef(), location.getyRef());
    }
    
    //calculates distance in metres between two locations using the global offsets
    public static double distanceMetres(Location start, Location end, HashMap<String, RoomInfo> roomInfo) {
        
        RoomInfo startRoom = roomInfo.get(start.getRoom());
        Point startPoint = startRoom.getGlobalCoord(start.getxRef(), start.getyRef());
        
        RoomInfo endRoom = roomInfo.get(end.getRoom());
        Point endPoint = endRoom.getGlobalCoord(end.getxRef(), end.getyRef());
                        
        double xDist = startPoint.getX() - endPoint.getX();
        double yDist = startPoint.getY() - endPoint.getY();
        double distance = Math.hypot(xDist, yDist);

        return distance;
    }
    
    //calculates pixel distance between two locations
    public static double distancePixel(Point start, Point end) {
        
        double xDist = start.getX() - end.getX();
        double yDist = start.getY() - end.getY();
        double distance = Math.hypot(xDist, yDist);

        return distance;
    }        
    
}
