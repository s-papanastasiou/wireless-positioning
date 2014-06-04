/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package datastorage;

import general.Point;
import general.Rectangle;
import general.RectangleD;
import java.util.Arrays;
import java.util.HashMap;
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

    public static final String DEFAULT_NAME = "Unknown";
    private static final double DEFAULT_WIDTH = 100;
    private static final double DEFAULT_HEIGHT = 100;
    private static final double DEFAULT_UNITS = 1;
    public static final String[] HEADINGS = {"Name", "Width", "Height", "Left", "Top", "Right", "Bottom", "isRoom", "GlobalX", "GlobalY"};
    protected String name;
    protected double physicalWidth;
    protected double physicalHeight;
    protected double widthUnits;
    protected double heightUnits;
    protected Rectangle drawRoom;
    protected boolean isRoom;
    protected double globalOffsetX;  //offset to place the room on global co-ordinates, used to measure distance between two points
    protected double globalOffsetY;  //offset to place the room on global co-ordinates, used to measure distance between two points
    protected RectangleD globalArea;
    /**
     * Constructor
     * 
     * @param parts String array of elements to build class. See HEADINGS for
     * order.
     */
    public RoomInfo(final String[] parts) throws NumberFormatException {

        name = parts[0];
        physicalWidth = Double.parseDouble(parts[1]);
        physicalHeight = Double.parseDouble(parts[2]);

        int startX = Integer.parseInt(parts[3]);
        int startY = Integer.parseInt(parts[4]);
        int endX = Integer.parseInt(parts[5]);
        int endY = Integer.parseInt(parts[6]);
        isRoom = Boolean.parseBoolean(parts[7]);

        drawRoom = new Rectangle(startX, startY, endX - startX, endY - startY);
        widthUnits = Math.round(drawRoom.width / physicalWidth);
        heightUnits = Math.round(drawRoom.height / physicalHeight);

        globalOffsetX = Double.parseDouble(parts[8]);
        globalOffsetY = Double.parseDouble(parts[9]);
        globalArea = new RectangleD(globalOffsetX, globalOffsetY, physicalWidth, physicalHeight);
    }

    /**
     * Constructor for a whole floor.
     * @param name
     * @param physicalWidth
     * @param physicalHeight
     * @param imageWidth
     * @param imageHeight 
     */
    public RoomInfo(String name, Double physicalWidth, Double physicalHeight, Double imageWidth, Double imageHeight) {
        this.name = name;
        this.physicalWidth = physicalWidth;
        this.physicalHeight = physicalHeight;
        
        drawRoom = new Rectangle(0, 0, imageWidth.intValue(), imageHeight.intValue());
        widthUnits = Math.round(drawRoom.width / physicalWidth);
        heightUnits = Math.round(drawRoom.height / physicalHeight);
        
        this.isRoom = false;
        this.globalOffsetX = 0;
        this.globalOffsetY = 0;
    }

    /**
     * Constructor - default values.
     */
    public RoomInfo() {
        name = DEFAULT_NAME;
        physicalWidth = DEFAULT_WIDTH;
        physicalHeight = DEFAULT_HEIGHT;
        drawRoom = new Rectangle();
        widthUnits = DEFAULT_UNITS;
        heightUnits = DEFAULT_UNITS;
        isRoom = false;
        globalOffsetX = 0;
        globalOffsetY = 0;
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
     * Rectangle drawn around a location.
     *  
     * @param location Location to draw.
     * @return 
     */
    public Rectangle getDrawRect(Location location) {

        Rectangle rect = new Rectangle();
        
        rect.x = (int) Math.round((location.drawX - (widthUnits / 2))); // drawRoom.x to get top left corner, offset by xRef and subtract half the size of one unit
        rect.y = (int) Math.round((location.drawY - (heightUnits / 2)));
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
        return physicalWidth;
    }

    /**
     * Get physical height of the room.
     * 
     * @return 
     */
    public double getHeight() {
        return physicalHeight;
    }
    
    public boolean containsGlobal(Point point){
        return globalArea.contains(point);
    }
    
    public boolean containsPixel(Point point){
        return drawRoom.contains(point);
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
     * Convert a global point into a location within the room.
     *
     * @param point Point to be converted.
     * @return
     */
    public Location createGlobalLocation(final Point point) {
        return createGlobalLocation(point, 0);
    } 
    
    /**
     * Convert a global point into a location within the room.
     *
     * @param point Global point
     * @param wRef w reference
     * @return
     */
    public Location createGlobalLocation(final Point point, int wRef) {

        int xRef = (int) (point.getX()- globalOffsetX);
        int yRef = (int) (point.getY()- globalOffsetY);

        //adjust for rooms not being able to be zero based.
        if (isRoom) {
            if (xRef == 0) {
                xRef = 1;
            }
            if (yRef == 0) {
                yRef = 1;
            }
        }
                
        return createLocation(xRef, yRef, wRef);
    } 
    
    /**
     * Convert a pixel point into a location within the room.
     *
     * @param point Point to be converted.
     * @return
     */
    public Location createPixelLocation(final Point point) {
        return createPixelLocation(point, 0);
    }      

    /**
     * Convert a pixel point into a location within the room.
     *
     * 
     * @param point Pixel point
     * @param wRef w reference
     * @return
     */
    public Location createPixelLocation(final Point point, int wRef) {

        int xRef = (int) ((((point.getX() - drawRoom.x) + 0.5) / widthUnits));
        int yRef = (int) ((((point.getY() - drawRoom.y) + 0.5) / heightUnits));

        //adjust for rooms not being able to be zero based.
        if (isRoom) {
            if (xRef == 0) {
                xRef = 1;
            }
            if (yRef == 0) {
                yRef = 1;
            }
        }
                
        return createLocation(xRef, yRef, wRef);
    }  
 
    public Location createLocation(int xRef, int yRef, int wRef){
        
        double globalX = globalOffsetX + xRef;
        double globalY = globalOffsetY + yRef;
        double drawX = (xRef * widthUnits) + drawRoom.x;
        double drawY = (yRef * widthUnits) + drawRoom.y;

        return new Location(name, xRef, yRef, wRef, globalX, globalY, drawX, drawY);        
    }
    

    /**
     * Checks whether the supplied array contains the expected headings for
     * room info data.
     *
     * @param parts String array to be checked.
     * @return
     */
    public static boolean headerCheck(String[] parts) {

        return Arrays.equals(HEADINGS, parts);
    }   

    /**
     * Finds the location that corresponds to the pixel point.
     * 
     * @param point Pixel point to find.
     * @param roomInfo Information about the rooms on the floor.
     * @return 
     */
    public static Location searchGlobalLocation(Point point, HashMap<String, RoomInfo> roomInfo) {
        Location location = new Location();

        Set<String> keys = roomInfo.keySet();

        for (String key : keys) {
            RoomInfo room = roomInfo.get(key);            
            if (room.containsGlobal(point)) {
                location = room.createGlobalLocation(point);
                break;
            }
        }

        return location;
    }
    
    /**
     * Finds the location that corresponds to the pixel point.
     * 
     * @param point Pixel point to find.
     * @param roomInfo Information about the rooms on the floor.
     * @return 
     */
    public static Location searchPixelLocation(Point point, HashMap<String, RoomInfo> roomInfo) {
        Location location = new Location();

        Set<String> keys = roomInfo.keySet();

        for (String key : keys) {
            RoomInfo room = roomInfo.get(key);            
            if (room.containsPixel(point)) {
                location = room.createPixelLocation(point);
                break;
            }
        }

        return location;
    }
    
    /**
     * Finds the location that corresponds to the pixel point.
     *      
     * @param room Label of the room
     * @param xRef x reference
     * @param yRef y reference
     * @param wRef w reference
     * @param roomInfo Information about the rooms on the floor.
     * @return 
     */
    public static Location createLocation(String room, int xRef, int yRef, int wRef, HashMap<String, RoomInfo> roomInfo) {
        Location location = new Location();   

        if(roomInfo.containsKey(room)){
            
            RoomInfo roomInf = roomInfo.get(room);
            roomInf.createLocation(xRef, yRef, wRef);            
        }

        return location;
    } 
}
