/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package visualinfo;

import datastorage.Location;
import datastorage.RSSIData;
import filehandling.RoomInfo;
import general.Point;
import general.Rectangle;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.imageio.ImageIO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Overlay locations onto an image.
 *
 * @author Greg Albiston
 */
public class DisplayGrid {

    private static final int xOffset = 4;
    private static final int yOffset = 12;
    private static final int fontSize = 8;
    private static final int pointWidth = 4;
    private static final int pointHeight = 4;
    private static final int halfPointWidth = pointWidth / 2;
    private static final int halfPointHeight = pointHeight / 2;

    
    private static final Logger logger = LoggerFactory.getLogger(DisplayGrid.class);

    /**
     * Draw list of locations onto an image, called DataGrid.png, and stores the
     * new image on the specified file path. Use default colours of red for
     * points and blue for room outlines.
     *
     * @param workingPath Path to store the image.
     * @param filename Name of file to print, without file extension.
     * @param floorPlanFile Floor plan image to draw upon.
     * @param rssiDataList List of locations to draw.
     * @param roomInfo Information about the location coordinates.
     * @param isRoomOutlineDraw True, draw outline of rooms in roomInfo.
     */
    public static void print(File workingPath, String filename, File floorPlanFile, List<RSSIData> rssiDataList, HashMap<String, RoomInfo> roomInfo, boolean isRoomOutlineDraw) {
        render(floorPlanFile, rssiDataList, roomInfo, isRoomOutlineDraw, Color.RED, Color.BLUE);
    }

    /**
     * Draw list of locations onto an image, based on supplied filename, and stores the
     * new image on the specified file path.
     *
     * @param workingPath Path to store the image.
     * @param filename Name of file to print, without file extension.
     * @param floorPlanFile Floor plan image to draw upon.
     * @param rssiDataList List of locations to draw.
     * @param roomInfo Information about the location coordinates.
     * @param isRoomOutlineDraw True, draw outline of rooms in roomInfo.
     * @param pointColour Colour to draw points.
     * @param roomColour Colour to draw room outlines.
     */
    public static void print(File workingPath, String filename, File floorPlanFile, List<RSSIData> rssiDataList, HashMap<String, RoomInfo> roomInfo, boolean isRoomOutlineDraw, Color pointColour, Color roomColour) {

        String fullFileName = filename + ".png";
        try {

            //Add in colour selection.
            BufferedImage gridImage = render(floorPlanFile, rssiDataList, roomInfo, isRoomOutlineDraw, pointColour, roomColour);
            File outputfile = new File(workingPath, fullFileName);
            ImageIO.write(gridImage, "png", outputfile);
        } catch (IOException ex) {
            logger.error(String.format("Error saving %s file: {}", fullFileName), ex);
        }
    }

    /**
     * Draw list of locations onto an image, called DataGrid.png, and store the
     * new image on the specified file path.
     *
     * @param floorPlanFile Floor plan image to draw upon.
     * @param radioMap Map of locations to draw.
     * @param roomInfo Information
     * @param isRoomOutlineDraw
     * @param pointColour Colour to draw points.
     * @param roomColour Colour to draw room outlines.
     * @return
     */
    public static BufferedImage render(final File floorPlanFile, final HashMap<String, Location> radioMap, final HashMap<String, RoomInfo> roomInfo, boolean isRoomOutlineDraw, Color pointColour, Color roomColour) {
        return render(floorPlanFile, new ArrayList<>(radioMap.values()), roomInfo, isRoomOutlineDraw, pointColour, roomColour);
    }

    /**
     * Draw list of locations onto an image, called DataGrid.png, and store the
     * new image on the specified file path.
     *
     * @param floorPlanFile Floor plan image to draw upon.
     * @param locationList List of locations to draw.
     * @param roomInfo Information
     * @param isRoomOutlineDraw
     * @param pointColour Colour to draw points.
     * @param roomColour Colour to draw room outlines.
     * @return
     */
    public static BufferedImage render(final File floorPlanFile, final List<? extends Location> locationList, final HashMap<String, RoomInfo> roomInfo, boolean isRoomOutlineDraw, Color pointColour, Color roomColour) {

        BufferedImage floorPlanImage = null;

        try {
            floorPlanImage = ImageIO.read(floorPlanFile);
            Graphics2D floorPlan = floorPlanImage.createGraphics();

            floorPlan.setFont(new Font("Serif", Font.PLAIN, fontSize));

            if (isRoomOutlineDraw) {
                floorPlan.setColor(roomColour);

                List<RoomInfo> rooms = new ArrayList<>(roomInfo.values());
                for (RoomInfo room : rooms) {
                    Rectangle roomRect = room.getRoomRect();
                    floorPlan.drawRect(roomRect.x, roomRect.y, roomRect.width, roomRect.height);
                    floorPlan.drawString(room.getName(), roomRect.x + xOffset, roomRect.y + yOffset);
                }
            }

            floorPlan.setColor(pointColour);
            //Draw each location point  

            for (Location point : locationList) {
                if (roomInfo.containsKey(point.getRoom())) {
                    String text = point.getXYStr();

                    Point pos = RoomInfo.searchPoint(point, roomInfo);
                    floorPlan.fillOval(pos.getXint() - halfPointWidth, pos.getYint() - halfPointHeight, pointWidth, pointHeight);
                    floorPlan.drawString(text, pos.getXint() - xOffset, pos.getYint() - yOffset);
                }
            }

            floorPlan.dispose();
            return floorPlanImage;
        } catch (IOException ex) {
            logger.error(ex.getMessage());
        }
        return floorPlanImage;
    }

}
