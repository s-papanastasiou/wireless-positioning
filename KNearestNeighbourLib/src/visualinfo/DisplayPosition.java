/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package visualinfo;

import general.Point;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import javax.imageio.ImageIO;
import datastorage.Location;
import datastorage.ResultLocation;
import filehandling.RoomInfo;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Gerg
 */
public class DisplayPosition {
     
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(DisplayPosition.class);
    
    private static final int SIZE = 10;
    private static final int HALF_SIZE = SIZE / 2;
    
    public static final boolean DRAW_POLYGON = false;  //whether to draw polygon around all the estimation points

    
    //Render just the final position
    public static BufferedImage render(final File floorPlanFile, final HashMap<String, RoomInfo> roomInfo, final Point finalPosition) {
        
        BufferedImage floorPlanImage = null;
        
        try {
           
            //load floor plan image            
            floorPlanImage = ImageIO.read(floorPlanFile);                                  

            floorPlanImage = drawFinal(floorPlanImage, finalPosition);
       
        } catch (IOException ex) {            
            logger.error(ex.getMessage());
        }
        
        return floorPlanImage;
    }
    
    //Render just the final and test position
    public static BufferedImage render(final File floorPlanFile, final HashMap<String, RoomInfo> roomInfo, final Point finalPosition, final Location testLocation) {
        
        BufferedImage floorPlanImage = null;
        
        try {
           
            //load floor plan image            
            floorPlanImage = ImageIO.read(floorPlanFile);
                      
            floorPlanImage = drawTest(floorPlanImage, roomInfo, testLocation);

            floorPlanImage = drawFinal(floorPlanImage, finalPosition);
       
        } catch (IOException ex) {           
            logger.error(ex.getMessage());
        }
        
        return floorPlanImage;
    }
    
    //Render the final, test and estimate positions
    public static BufferedImage render(final File floorPlanFile, final HashMap<String, RoomInfo> roomInfo, final List<ResultLocation> positionEstimates, final Point finalPosition, final Location testLocation) {
        
        BufferedImage floorPlanImage = null;
        
        try {
           
            //load floor plan image            
            floorPlanImage = ImageIO.read(floorPlanFile);
            
            floorPlanImage = drawEstimates(floorPlanImage, roomInfo, positionEstimates);
            
            floorPlanImage = drawLines(floorPlanImage, roomInfo, positionEstimates, finalPosition);           

            floorPlanImage = drawTest(floorPlanImage, roomInfo, testLocation);

            floorPlanImage = drawFinal(floorPlanImage, finalPosition);
            
            
        } catch (IOException ex) {          
            logger.error(ex.getMessage());
        }
        return floorPlanImage;
    }

    private static BufferedImage drawLines(final BufferedImage floorPlanImage, final HashMap<String, RoomInfo> roomInfo, final List<ResultLocation> positionEstimates, Point finalPosition) throws IOException {

        Graphics2D floorPlan = floorPlanImage.createGraphics();
                                 
        for (int counter = 0; counter < positionEstimates.size(); counter++) {
            ResultLocation estimate = positionEstimates.get(counter);                                            
            
            if (roomInfo.containsKey(estimate.getRoom())) {

                floorPlan.setColor(getColor(counter));
                
                Point pos = RoomInfo.searchPoint(estimate, roomInfo);  
                //Draw line from the estimate to the final position
                floorPlan.drawLine(pos.getXint(), pos.getYint(), finalPosition.getXint(), finalPosition.getYint());
        
            } else {
                throw new AssertionError("Estimation not found in Room Info: " + estimate.toString());
            }
        }

        //Draw polygon connecting all the position estimates
       /*
        if(DRAW_POLYGON)
        {
            floorPlan.setColor(Color.BLACK);  
            floorPlan.drawPolygon(pointStore.getPolygon());
        }
        */
        floorPlan.dispose();
        return floorPlanImage;
    }

    private static BufferedImage drawEstimates(final BufferedImage floorPlanImage, final HashMap<String, RoomInfo> roomInfo, List<ResultLocation> positionEstimates) throws IOException {

        Graphics2D floorPlan = floorPlanImage.createGraphics();       
        
        for (int counter = 0; counter < positionEstimates.size(); counter++) {
            ResultLocation estimate = positionEstimates.get(counter);
             
            //find duplicates and remove - number of duplications recorded next to the point - also more efficient use of available colours
            int duplicateCount = 1;
            for(int i=counter+1;i<positionEstimates.size();i++)
            {
                ResultLocation matchEstimate = positionEstimates.get(i);
                
                if(matchEstimate.equals(estimate))
                {
                    duplicateCount++;
                    positionEstimates.remove(i);
                    i--;
                }                    
            }
            
            if (roomInfo.containsKey(estimate.getRoom())) {

                floorPlan.setColor(getColor(counter));
                
                Point pos = RoomInfo.searchPoint(estimate, roomInfo);                

                //Draw oval                
                floorPlan.fillOval(pos.getXint() - HALF_SIZE, pos.getYint() - HALF_SIZE, SIZE, SIZE);
                
                //include text label for duplicates           
                if(duplicateCount!=1)
                {
                    floorPlan.setColor(Color.BLACK); 
                    floorPlan.drawString(String.valueOf(duplicateCount), pos.getXint()+HALF_SIZE, pos.getYint());
                }
                
            } else {
                throw new AssertionError("Estimation not found in Room Info: " + estimate.toString());
            }
        }

        floorPlan.dispose();
        return floorPlanImage;
    }

    private static BufferedImage drawFinal(final BufferedImage floorPlanImage, final Point finalPosition) throws IOException {

        Graphics2D floorPlan = floorPlanImage.createGraphics();

        //Draw the final point
        floorPlan.setColor(Color.GREEN);
        //Draw oval        
        floorPlan.fillOval(finalPosition.getXint() - HALF_SIZE, finalPosition.getYint() - HALF_SIZE, SIZE, SIZE);

        floorPlan.dispose();
        return floorPlanImage;
    }

    private static BufferedImage drawTest(final BufferedImage floorPlanImage, final HashMap<String, RoomInfo> roomInfo, final Location testLocation) throws IOException {

        Graphics2D floorPlan = floorPlanImage.createGraphics();        
        
        if (roomInfo.containsKey(testLocation.getRoom())) {
            floorPlan.setColor(Color.MAGENTA);
            
            Point pos = RoomInfo.searchPoint(testLocation, roomInfo);
            //Draw oval                
            floorPlan.fillOval(pos.getXint() - HALF_SIZE, pos.getYint() - HALF_SIZE, SIZE, SIZE);

        }

        floorPlan.dispose();
        return floorPlanImage;
    }

    private static Color getColor(int index) {

        Color color;
        switch (index) {
            case 0:
                color = Color.BLUE;
                break;
            case 1:
                color = Color.CYAN;
                break;
            case 2:
                color = Color.RED;
                break;
            case 3:
                color = Color.PINK;
                break;
            case 4:
                color = Color.ORANGE;
                break;
            case 5:
                color = Color.LIGHT_GRAY;
                break;
            case 6:
                color = Color.DARK_GRAY;
                break;
            case 7:
                color = Color.YELLOW;
                break;
            case 8:
                color = Color.GRAY;
                break;
            default:
                color = Color.BLACK;
                break;
        }
        return color;
    }
}