/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package visualinfo;

import accesspointvariant.APData;
import accesspointvariant.APFormat;
import accesspointvariant.APLocation;
import datastorage.GeomagneticData;
import datastorage.Location;
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
     * Draw heat map of RSSI values onto an image, based on
     * "Heatmap-xx-xx-xx-xx-xx-xx.png", and stores the new image on the
     * specified file path.
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
    public static void printRSSI(File workingPath, File floorPlanFile, HashMap<String, RoomInfo> roomInfo, List<RSSIData> rssiDataList, boolean isBSSIDMerged, boolean isOrientationMerged, String fieldSeparator) {

        printRSSI(workingPath, "HeatmapRSSI", floorPlanFile, roomInfo, rssiDataList, isBSSIDMerged, isOrientationMerged, fieldSeparator);
    }

    /**
     * Draw heat map of RSSI values onto an image, based on
     * "FILENAME-xx-xx-xx-xx-xx-xx.png", and stores the new image on the
     * specified file path.
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
    public static void printRSSI(File workingPath, String filename, File floorPlanFile, HashMap<String, RoomInfo> roomInfo, List<RSSIData> rssiDataList, boolean isBSSIDMerged, boolean isOrientationMerged, String fieldSeparator) {

        //create sub-folder
        String bssidStub = "";
        String orientationStub = "";
        if (!isOrientationMerged) {
            orientationStub = "not";
        }

        if (!isBSSIDMerged) {
            bssidStub = "not";
        }

        File heatPath = new File(workingPath, String.format("RSSI Heatmaps (orientation %s merge, BSSID %s merge)", orientationStub, bssidStub));
        heatPath.mkdir();
        File absHeatPath = new File(heatPath, "Absolute Range");
        absHeatPath.mkdir();
        File relHeatPath = new File(heatPath, "Relative Range");
        relHeatPath.mkdir();

        HashMap<String, APData> apDataMap = APFormat.compile(rssiDataList, isBSSIDMerged, isOrientationMerged);
        String bssidDataFile = "BSSIDAverage.csv";
        logger.info("Outputting BSSID average data to file: {}", bssidDataFile);
        APFormat.print(new File(heatPath, bssidDataFile), apDataMap, fieldSeparator);

        int maxFrequency = findMaxFrequency(apDataMap);
        logger.info("Max Frequency: {}", maxFrequency);        

        float minValue = Float.MAX_VALUE;
        float maxValue = Float.MIN_VALUE;

        for (APData apData : apDataMap.values()) {
            if(apData.getMaxRSSIMean()>maxValue)
                maxValue = apData.getMaxRSSIMean().floatValue();
            
            if(apData.getMinRSSIMean()>minValue)
                minValue = apData.getMinRSSIMean().floatValue();            
        }

        for (APData apData : apDataMap.values()) {
            
            try {
                BufferedImage heatImage = drawRSSIHeat(floorPlanFile, roomInfo, apData, maxFrequency, -10.0f, -100.0f);
                File outputFile = new File(absHeatPath, filename + "-Abs-" + apData.getBSSID().replace(':', '-') + ".png");
                ImageIO.write(heatImage, "png", outputFile);
                logger.info("Heatmap created: {}", outputFile.toString());

                heatImage = drawRSSIHeat(floorPlanFile, roomInfo, apData, maxFrequency, maxValue, minValue);
                outputFile = new File(relHeatPath, filename + "-Rel-" + apData.getBSSID().replace(':', '-') + ".png");

                ImageIO.write(heatImage, "png", outputFile);
                logger.info("Heatmap created: {}", outputFile.toString());

            } catch (IOException ex) {
                logger.error("Error writing {}-{}.png", filename, apData.getBSSID().replace(':', '-'));
            }
        }
    }

    /**
     * Draw heat map of Geomagnetic values onto an image, based on
     * "HeatmapGeomagnetic-xx-xx-xx-xx-xx-xx.png", and stores the new image on
     * the specified file path.
     *
     * Also, writes the associated RSSI values information to file.
     *
     * @param workingPath Path to store the image.
     * @param floorPlanFile Floor plan image to draw upon.
     * @param roomInfo Information about the location coordinates.
     * @param geomagneticDataList List of locations to draw.
     * @param fieldSeparator Separator used between each heading in output file.
     */
    public static void printGeomagnetic(File workingPath, File floorPlanFile, HashMap<String, RoomInfo> roomInfo, List<GeomagneticData> geomagneticDataList, String fieldSeparator) {

        printGeomagnetic(workingPath, "HeatmapGeomagnetic", floorPlanFile, roomInfo, geomagneticDataList, fieldSeparator);
    }

    /**
     * Draw heat map of Geomagnetic values onto an image, based on
     * "FILENAME-xx-xx-xx-xx-xx-xx.png", and stores the new image on the
     * specified file path.
     *
     * Also, writes the associated RSSI values information to file.
     *
     * @param workingPath Path to store the image.
     * @param filename Name of file to print, without file extension.
     * @param floorPlanFile Floor plan image to draw upon.
     * @param roomInfo Information about the location coordinates.
     * @param geomagneticDataList List of locations to draw.
     * @param fieldSeparator Separator used between each heading in output file.
     */
    public static void printGeomagnetic(File workingPath, String filename, File floorPlanFile, HashMap<String, RoomInfo> roomInfo, List<GeomagneticData> geomagneticDataList, String fieldSeparator) {

        //create sub-folder       
        File heatPath = new File(workingPath, "Geomagnetic Heatmaps");
        heatPath.mkdir();
        File absHeatPath = new File(heatPath, "Absolute Range");
        absHeatPath.mkdir();
        File relHeatPath = new File(heatPath, "Relative Range");
        relHeatPath.mkdir();
        
        HashMap<Location, AvgValue> xValues = new HashMap<>();
        HashMap<Location, AvgValue> yValues = new HashMap<>();
        HashMap<Location, AvgValue> zValues = new HashMap<>();
                
        int maxFrequency = 0;
        float xMaxMean = Float.MIN_VALUE;
        float xMinMean = Float.MAX_VALUE;
        
        float yMaxMean = Float.MIN_VALUE;
        float yMinMean = Float.MAX_VALUE;
        
        float zMaxMean = Float.MIN_VALUE;
        float zMinMean = Float.MAX_VALUE;
        
        for (GeomagneticData data : geomagneticDataList) {
            if (xValues.containsKey(data)) {
                xValues.get(data).add(data.getxValue());
                yValues.get(data).add(data.getyValue());
                zValues.get(data).add(data.getzValue());
            } else {
                xValues.put(data, new AvgValue(data.getxValue()));
                yValues.put(data, new AvgValue(data.getyValue()));
                zValues.put(data, new AvgValue(data.getzValue()));
            }
            int frequency = xValues.get(data).getFrequency();
            if (frequency > maxFrequency) {
                maxFrequency = frequency;
            }
            
            float xMean = xValues.get(data).getMean().floatValue();
            if (xMean > xMaxMean)
                xMaxMean = xMean;
            if (xMean < xMinMean)            
                xMinMean = xMean;
            
            float yMean = yValues.get(data).getMean().floatValue();
            if (yMean > yMaxMean)
                yMaxMean = yMean;
            if (yMean < yMinMean)
                yMinMean = yMean;
            
            float zMean = zValues.get(data).getMean().floatValue();
            if (zMean > zMaxMean)
                zMaxMean = zMean;
            if (zMean < zMinMean)
                zMinMean = zMean;
        }

        logger.info("Max Frequency: {}", maxFrequency);

        try {
            BufferedImage heatImage = drawGeomagneticHeat(floorPlanFile, roomInfo, xValues, maxFrequency, 360, -360);
            File outputFile = new File(absHeatPath, filename + "-Abs-X.png");
            ImageIO.write(heatImage, "png", outputFile);
            logger.info("Heatmap created: {}", outputFile.toString());

            heatImage = drawGeomagneticHeat(floorPlanFile, roomInfo, xValues, maxFrequency, xMaxMean, xMinMean);
            outputFile = new File(relHeatPath, filename + "-Rel-X.png");
            ImageIO.write(heatImage, "png", outputFile);
            logger.info("Heatmap created: {}", outputFile.toString());

            heatImage = drawGeomagneticHeat(floorPlanFile, roomInfo, yValues, maxFrequency, 360, -360);
            outputFile = new File(absHeatPath, filename + "-Abs-Y.png");
            ImageIO.write(heatImage, "png", outputFile);
            logger.info("Heatmap created: {}", outputFile.toString());

            heatImage = drawGeomagneticHeat(floorPlanFile, roomInfo, yValues, maxFrequency, yMaxMean, yMinMean);
            outputFile = new File(relHeatPath, filename + "-Rel-Y.png");
            ImageIO.write(heatImage, "png", outputFile);
            logger.info("Heatmap created: {}", outputFile.toString());

            heatImage = drawGeomagneticHeat(floorPlanFile, roomInfo, zValues, maxFrequency, 360, -360);
            outputFile = new File(absHeatPath, filename + "-Abs-Z.png");
            ImageIO.write(heatImage, "png", outputFile);
            logger.info("Heatmap created: {}", outputFile.toString());

            heatImage = drawGeomagneticHeat(floorPlanFile, roomInfo, zValues, maxFrequency, zMaxMean, zMinMean);
            outputFile = new File(relHeatPath, filename + "-Rel-Z.png");
            ImageIO.write(heatImage, "png", outputFile);
            logger.info("Heatmap created: {}", outputFile.toString());

        } catch (IOException ex) {
            logger.error("Error writing {}.png", filename);
        }
    }

    //Search through the whole data set for the highest frequency of values - allows normalisation of values across all the heat maps
    private static int findMaxFrequency(HashMap<String, APData> apDataMap) {

        int maxFrequency = 0;
        Set<String> keys = apDataMap.keySet();

        for (String key : keys) {
            APData apData = apDataMap.get(key);

            //Find the highest frequency across the data set
            if (apData.getMaxRSSIFrequency() > maxFrequency) {
                maxFrequency = apData.getMaxRSSIFrequency();
            }
        }

        return maxFrequency;
    }

    private static BufferedImage drawRSSIHeat(File floorPlanFile, HashMap<String, RoomInfo> roomInfo, APData apData, int maxFrequency, float maxValue, float minValue) throws IOException {

        BufferedImage floorPlanImage = ImageIO.read(floorPlanFile);
        Graphics2D floorPlan = floorPlanImage.createGraphics();

        for (APLocation apLocation : apData.getLocations()) {
            if (roomInfo.containsKey(apLocation.getRoom())) {
                floorPlan.setColor(getColor(apLocation.getAvgRSSI(), maxFrequency, maxValue, minValue));
                RoomInfo room = roomInfo.get(apLocation.getRoom());
                Rectangle rect = room.getDrawRect(apLocation);
                floorPlan.fillRect(rect.x, rect.y, rect.width, rect.height);
            }

        }

        floorPlan.dispose();
        return floorPlanImage;
    }

    private static BufferedImage drawGeomagneticHeat(File floorPlanFile, HashMap<String, RoomInfo> roomInfo, HashMap<Location, AvgValue> values, int maxFrequency, float maxValue, float minValue) throws IOException {

        BufferedImage floorPlanImage = ImageIO.read(floorPlanFile);
        Graphics2D floorPlan = floorPlanImage.createGraphics();

        for (Location location : values.keySet()) {
            if (roomInfo.containsKey(location.getRoom())) {
                AvgValue value = values.get(location);
                floorPlan.setColor(getColor(value, maxFrequency, maxValue, minValue));
                RoomInfo room = roomInfo.get(location.getRoom());
                Rectangle rect = room.getDrawRect(location);
                floorPlan.fillRect(rect.x, rect.y, rect.width, rect.height);
            }
        }

        floorPlan.dispose();
        return floorPlanImage;
    }

    private static Color getColor(AvgValue avgValue, int maxFrequency, float maxValue, float minValue) {

        final float saturation = 1.0f;
        final float brightness = 1.0f;

        final float paletteRange = 140f / 360f; //Constrain the pallete to between Red (0 degrees) and Green (140 degrees).

        float range = maxValue - minValue;
        float hue = (float) (((avgValue.getMean() - minValue) / range) * paletteRange); //Lower numbers being red and higher being green.

        float alpha = (float) avgValue.getFrequency() / maxFrequency;
        Color tempColor = Color.getHSBColor(hue, saturation, brightness);

        return new Color(tempColor.getRed(), tempColor.getGreen(), tempColor.getBlue(), (int) (alpha * 255));
    }

}
