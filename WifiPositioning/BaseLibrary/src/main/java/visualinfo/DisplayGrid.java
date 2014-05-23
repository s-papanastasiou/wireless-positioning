/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package visualinfo;

import datastorage.LabelPoint;
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
import java.util.Set;
import javax.imageio.ImageIO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Gerg
 */
public class DisplayGrid {
    
    private static final Logger logger = LoggerFactory.getLogger(DisplayGrid.class);
    
    public static void print(File workingPath, File floorPlan, List<RSSIData> rssiDataList, HashMap<String, RoomInfo> roomInfo, boolean isRoomOutlineDraw) {

        try {
            HashMap<String, Location> dataPoints = new HashMap<>();
            for (RSSIData rssiData : rssiDataList) {

                if (!dataPoints.containsKey(rssiData.getRoomRef())) //New entry created and added
                {
                    dataPoints.put(rssiData.getRoomRef(), rssiData);
                }
            }
          
            //Add in colour selection.
            BufferedImage gridImage = DisplayGrid.render(floorPlan, dataPoints, roomInfo, Color.RED, isRoomOutlineDraw, Color.BLUE);
            File outputfile = new File(workingPath, "DataGrid.png");
            ImageIO.write(gridImage, "png", outputfile);
        } catch (IOException ex) {            
            logger.error("Error saving DataGrid.png file");
        }
    }
    
    public static BufferedImage render(final File floorPlanFile, final HashMap<String, Location> radioMap, final HashMap<String, RoomInfo> roomInfo, Color pointColour, boolean isRoomOutlineDraw, Color roomColour) {

        BufferedImage floorPlanImage = null;
        
        try {
            floorPlanImage = ImageIO.read(floorPlanFile);
            Graphics2D floorPlan = floorPlanImage.createGraphics();
            
            floorPlan.setFont(new Font("Serif", Font.PLAIN, 8));
            
            if(isRoomOutlineDraw)
            {
                floorPlan.setColor(roomColour);
                Set<String> roomKeys =roomInfo.keySet();
                for(String roomKey: roomKeys)
                {
                    Rectangle roomRect = roomInfo.get(roomKey).getRoomRect();
                    floorPlan.drawRect(roomRect.x, roomRect.y, roomRect.width, roomRect.height);
                    String text = roomInfo.get(roomKey).getName();
                    floorPlan.drawString(text, roomRect.x + 4, roomRect.y + 12);
                }
            }
                
            
            floorPlan.setColor(pointColour);
            //Draw each location point
            Set<String> keys =radioMap.keySet();
                  
            for(String key: keys)
            {
                Location point = radioMap.get(key);
                if (roomInfo.containsKey(point.getRoom())) {
                    String text = point.getXYStr();

                    Point pos = findPoint(roomInfo, point); 
                    floorPlan.fillOval(pos.getXint() - 2, pos.getYint() - 2, 4, 4);
                    floorPlan.drawString(text, pos.getXint() - 4, pos.getYint() - 4);
                }
            }                        

            floorPlan.dispose();
            return floorPlanImage;
        } catch (IOException ex) {
            logger.error(ex.getMessage());
        }
        return floorPlanImage;
    }          
    
    public static List<LabelPoint> list(final HashMap<String, ? extends Location> radioMap, final HashMap<String, RoomInfo> roomInfo) {

        List<LabelPoint> labelPoints = new ArrayList<>();
                    
            Set<String> keys =radioMap.keySet();
            
            for(String key: keys)
            {
                Location point = radioMap.get(key);
                if (roomInfo.containsKey(point.getRoom())) {
                                        
                    Point pos = findPoint(roomInfo, point); 
                    labelPoints.add(new LabelPoint(pos.getXint(), pos.getYint(), point.getXYStr()));
                }
            }                        
      
        return labelPoints;
    }
    
    public static Point findPoint(final HashMap<String, RoomInfo> roomInfo, Location location){
        
        RoomInfo room = roomInfo.get(location.getRoom());
        return room.getPoint(location.getxRef(), location.getyRef());                 
    }      
    
}
