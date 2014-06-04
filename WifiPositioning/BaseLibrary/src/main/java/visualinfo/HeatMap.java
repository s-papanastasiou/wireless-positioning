/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package visualinfo;

import accesspointvariant.APData;
import accesspointvariant.APFormat;
import accesspointvariant.APLocation;
import datastorage.RSSIData;
import datastorage.RoomInfo;
import general.AvgValue;
import general.Rectangle;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import javax.imageio.ImageIO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Draws heat map of RSSI values onto an image.
 * 
 * @author Greg Albiston
 */
public class HeatMap {
    
    private static final Logger logger = LoggerFactory.getLogger(HeatMap.class);
    
    /**
     * Draw heat map of RSSI values onto an image, based on "Heatmap-xx-xx-xx-xx-xx-xx.png", and stores the
     * new image on the specified file path.
     * 
     * Also, writes the associated RSSI values information to file.
     * 
     * @param workingPath Path to store the image. 
     * @param floorPlanFile Floor plan image to draw upon.
     * @param roomInfo Information about the location coordinates.     
     * @param rssiDataList List of locations to draw.                  
     * @param isBSSIDMerged True, if last hex pair of BSSID is to be ignored.
     * @param isOrientationMerged True, if W-Ref of location is to be ignored.
     * @param fieldSeparator Separator used between each heading in output file.
     */
    public static void print(File workingPath, File floorPlanFile, HashMap<String, RoomInfo> roomInfo, List<RSSIData> rssiDataList, boolean isBSSIDMerged, boolean isOrientationMerged, String fieldSeparator) {
        
        print(workingPath, "Heatmap", floorPlanFile, roomInfo, rssiDataList, isBSSIDMerged, isOrientationMerged, fieldSeparator);    
    }
    
    /**
     * Draw heat map of RSSI values onto an image, based on "Heatmap-xx-xx-xx-xx-xx-xx.png", and stores the
     * new image on the specified file path.
     * 
     * Also, writes the associated RSSI values information to file.
     * 
     * @param workingPath Path to store the image. 
     * @param filename Name of file to print, without file extension.
     * @param floorPlanFile Floor plan image to draw upon.
     * @param roomInfo Information about the location coordinates.     
     * @param rssiDataList List of locations to draw.                  
     * @param isBSSIDMerged True, if last hex pair of BSSID is to be ignored.
     * @param isOrientationMerged True, if W-Ref of location is to be ignored.
     * @param fieldSeparator Separator used between each heading in output file.
     */
    public static void print(File workingPath, String filename, File floorPlanFile, HashMap<String, RoomInfo> roomInfo, List<RSSIData> rssiDataList, boolean isBSSIDMerged, boolean isOrientationMerged, String fieldSeparator) {
                
       //create sub-folder
        String bssidStub = "";
        String orientationStub = "";
        if(!isOrientationMerged)
            orientationStub = "not";
        
        if(!isBSSIDMerged)
            bssidStub = "not";
                
        File heatPath = new File(workingPath, String.format("heatmaps (orientation %s merge, BSSID %s merge)", orientationStub, bssidStub));
        heatPath.mkdir();
        
        HashMap<String, APData> apDataMap = APFormat.compile(rssiDataList, isBSSIDMerged, isOrientationMerged);
        String bssidDataFile = "BSSIDAverage.csv";
        logger.info("Outputting BSSID average data to file: {}", bssidDataFile);
        APFormat.print(new File(heatPath,bssidDataFile), apDataMap, fieldSeparator);
        
        int maxFrequency = findMaxFrequency(apDataMap);                
        logger.info("Max Frequency: {}", maxFrequency);
        
        Set<String> keys = apDataMap.keySet();
        for (String key : keys) {
            APData apData = apDataMap.get(key);
            try {
                BufferedImage heatImage = drawRSSIHeat(floorPlanFile, roomInfo, apData, maxFrequency);                
                File outputFile = new File(heatPath, filename + "-" + apData.getBSSID().replace(':', '-') + ".png");                        
                ImageIO.write(heatImage, "png", outputFile);
                logger.info("Heatmap created: {}", outputFile.toString());
                
            } catch (IOException ex) {                
                logger.error("Error writing {}-{}.png", filename, apData.getBSSID().replace(':', '-'));
            }
        } 
    }
   
    //Search through the whole data set for the highest frequency of values - allows normalisation of values across all the heat maps
    private static int findMaxFrequency(HashMap<String, APData> apDataMap){
    
        int maxFrequency = 0;        
        Set<String> keys = apDataMap.keySet();

        for (String key : keys) {
            APData apData = apDataMap.get(key);
        
            //Find the highest frequency across the data set
            for(APLocation apLocation : apData.getLocations()) {
                if(apLocation.frequency()>maxFrequency)
                    maxFrequency = apLocation.frequency();
            }
        }
        
        return maxFrequency;
    }
                               
    private static BufferedImage drawRSSIHeat(File floorPlanFile, HashMap<String, RoomInfo> roomInfo, APData apData, int maxFrequency) throws IOException {

        BufferedImage floorPlanImage = ImageIO.read(floorPlanFile);
        Graphics2D floorPlan = floorPlanImage.createGraphics();      
        
        for (APLocation apLocation : apData.getLocations()) {
            if (roomInfo.containsKey(apLocation.getRoom())) {
                floorPlan.setColor(getRSSIColor(apLocation.getAvgRSSI(), maxFrequency));
                RoomInfo room = roomInfo.get(apLocation.getRoom());
                Rectangle rect = room.getPointRect(apLocation.getxRef(), apLocation.getyRef());
                floorPlan.fillRect(rect.x, rect.y, rect.width, rect.height);
            }

        }

        floorPlan.dispose();
        return floorPlanImage;
    }

    private static Color getRSSIColor(AvgValue avgValue, int maxFrequency) {
              
        final float saturation = 1.0f;
        final float brightness = 1.0f;
        
        final float minValue = -100f;
        final float paletteRange = 140f / 360f; //Constrain the pallete to between Red (0 degrees) and Green (140 degrees).
        
        float hue = (float) ((1 - (avgValue.getMean() / minValue)) * paletteRange ); //RSSI is negative value so want to invert with -100 being red and -10 being green.
        

        float alpha = (float) avgValue.getFrequency() / maxFrequency;
        Color tempColor = Color.getHSBColor(hue, saturation, brightness);

        return new Color(tempColor.getRed(), tempColor.getGreen(), tempColor.getBlue(), (int) (alpha * 255));
    }   
    
}
