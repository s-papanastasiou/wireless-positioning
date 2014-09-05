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
import general.Point;
import general.Rectangle;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import javax.imageio.ImageIO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Identifies points with matching or similar values from a list and draws onto
 * an image.
 *
 * @author Greg Albiston
 */
public class ValueMap {

    private static final Logger logger = LoggerFactory.getLogger(ValueMap.class);

    /**
     * Identifies points with matching or similar values from a list and draws
     * onto an image, based on "MatchAnalysis-xx" filename, and stores the new
     * image on the specified file path.
     *
     * Also, outputs a list of matching locations to text file.
     *
     * @param workingPath Path to store the image.
     * @param floorPlanFile Floor plan image to draw upon.
     * @param rssiDataList List of locations to draw.
     * @param roomInfo Information about the floor plan for drawing.
     */
    public static void print(final File workingPath, final File floorPlanFile, final List<RSSIData> rssiDataList, final HashMap<String, RoomInfo> roomInfo) {
        print(workingPath, "ValueAnalysis", floorPlanFile, rssiDataList, roomInfo, 0.5, -105.0, -1.0, 1.0, false, false, ",");
    }    
    
    /**
     * Identifies points with matching or similar values from a list and draws
     * onto an image and stores the new image on the specified file path.
     *
     * Also, outputs a list of matching locations to text file.
     *
     * @param workingPath Path to store the image.
     * @param filename Name of file to print, without file extension.
     * @param floorPlanFile Floor plan image to draw upon.
     * @param rssiDataList List of locations to draw.
     * @param roomInfo Information about the floor plan for drawing.
     * @param rangeValue Range of values considered to be matches.
     * @param lowerBound Lower value to be tested.
     * @param upperBound Upper value to be tested.
     * @param step Increment between values in the boundaries.
     * @param isBSSIDMerged True, if last hex pair of BSSID is to be ignored.
     * @param isOrientationMerged True, if W-Ref of location is to be ignored.
     * @param fieldSeparator Separator used between each heading in output file.
     */
    public static void print(final File workingPath, final String filename, final File floorPlanFile, final List<RSSIData> rssiDataList, final HashMap<String, RoomInfo> roomInfo, final Double rangeValue, final Double lowerBound, final Double upperBound, final Double step, final Boolean isBSSIDMerged, final Boolean isOrientationMerged, final String fieldSeparator) {

        //create sub-folder
        File matchPath = new File(workingPath, "ValueMaps");
        matchPath.mkdir();

        logger.info("Starting value analysis.");
        logger.info("Range value +/- {}", rangeValue);
        logger.info("Lower: {} Upper: {} Step: {}", lowerBound, upperBound, step);

        final String IMAGE_FILE_STEM = String.format("%s-%s ", filename, rangeValue);
        final String LOG_FILE = String.format("%s-%s-%s-%s-%s.txt", filename, rangeValue, lowerBound, upperBound, step);

        File logFile = new File(matchPath, LOG_FILE);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(logFile, false))) {

            List<APData> accessPoints = APFormat.compileList(rssiDataList, isBSSIDMerged, isOrientationMerged);

            Integer maxFrequency = 0;
            for(APData ap: accessPoints){
                if(ap.getMaxRSSIFrequency() > maxFrequency)
                    maxFrequency = ap.getMaxRSSIFrequency(); 
            }
            
            int matchCounter = 0;
            int noMatchCounter = 0;
            for (APData ap : accessPoints) {
                try {
                    String bssid = ap.getBSSID().replace(':', '-');  //Replace any colons with - so filenames are ok
                    logger.info("Analysing - {}", bssid);
                    HashMap<Double, List<APLocation>> matches = compareLocations(ap, rangeValue, lowerBound, upperBound, step);

                    printLocations(bssid, matches, writer, fieldSeparator);
                    writer.flush();
                    Set<Double> values = matches.keySet();
                    for (Double value : values) {
                        List<APLocation> match = matches.get(value);
                        if (!match.isEmpty()) {  //draw image of the matches
                            BufferedImage floorPlanImage = ImageIO.read(floorPlanFile);

                            if (!match.isEmpty()) {
                                //floorPlanImage = drawAPArea(ap, floorPlanImage, roomInfo);
                                floorPlanImage = drawLocations(match, floorPlanImage, maxFrequency);
                            }

                            File outputfile = new File(matchPath, IMAGE_FILE_STEM + bssid + "-" + value + ".png");
                            ImageIO.write(floorPlanImage, "png", outputfile);

                            matchCounter++;
                        } else {
                            noMatchCounter++;
                        }
                    }
                } catch (IOException ex) {
                    logger.error(ex.getMessage());
                }

                logger.info("Access Point total: {} Matches: {} No Matches: {}", accessPoints.size(), matchCounter, noMatchCounter);
            }
        } catch (IOException ex) {
            logger.error(ex.getMessage());
        }

        logger.info("Match analysis complete");
    }

    private static BufferedImage drawAPArea(APData accessPoint, BufferedImage floorPlanImage, HashMap<String, RoomInfo> roomInfo) {

        Graphics2D floorPlan = floorPlanImage.createGraphics();

        floorPlan.setColor(new Color(0.3f, 0.3f, 0.3f, 0.3f));
        for (APLocation apLocation : accessPoint.getLocations()) {
            if (roomInfo.containsKey(apLocation.getRoom())) {
                RoomInfo room = roomInfo.get(apLocation.getRoom());
                Rectangle rect = room.getDrawRect(apLocation);
                floorPlan.fillRect(rect.x, rect.y, rect.width, rect.height);
            }
        }

        floorPlan.dispose();
        return floorPlanImage;
    }

    private static void printLocations(String bssid, HashMap<Double, List<APLocation>> matches, BufferedWriter writer, String fieldSeparator) throws IOException {

        Set<Double> values = matches.keySet();
        for (Double value : values) {
            List<APLocation> match = matches.get(value);
            String outputText = "";
            if (match.isEmpty()) {
                outputText = String.format("BSSID: %s%sRSSI: %s%sNo Matches", bssid, fieldSeparator, value, fieldSeparator);

            } else {
                outputText += String.format("BSSID: %s%sRSSI: %s%sMatches: %d", bssid, fieldSeparator, value, fieldSeparator, match.size());

                for (APLocation location : match) {
                    outputText = outputText.concat(String.format("%sLocation:%s Mean:%s", fieldSeparator, location.toStringLocation(), location.mean()));
                }
            }

            writer.write(outputText);
            writer.newLine();
            logger.info(outputText);
        }
    }

    /**
     * Compare mean values for access point locations and find matches within
     * the specified range.
     *
     * @param accessPoint
     * @param rangeValue
     * @return
     */
    private static HashMap<Double, List<APLocation>> compareLocations(final APData accessPoint, final double rangeValue, final double lowerBound, final double upperBound, final double step) {

        HashMap<Double, List<APLocation>> buckets = sortLocations(accessPoint);

        HashMap<Double, List<APLocation>> matchPoints = new HashMap<>();
        for (double counter = lowerBound; counter <= upperBound; counter += step) {
            ArrayList<APLocation> matches = new ArrayList<>();

            TreeSet<Double> means = new TreeSet<>(buckets.keySet());

            double lowerValue = counter - rangeValue;
            double upperValue = counter + rangeValue;

            for (Double targetMean : means) {

                if (lowerValue <= targetMean && targetMean <= upperValue) {
                    matches.addAll(buckets.get(targetMean));
                } else if (targetMean > upperValue) { //Sorted so later values will not match either.
                    break;
                }
            }

            matchPoints.put(counter, matches);
        }

        return matchPoints;

    }

    /**
     * Split the access point locations into buckets based on mean values for
     * each location.
     *
     * @param accessPoint
     * @return
     */
    private static HashMap<Double, List<APLocation>> sortLocations(final APData accessPoint) {

        HashMap<Double, List<APLocation>> buckets = new HashMap<>();

        for (APLocation location : accessPoint.getLocations()) {
            Double mean = location.getAvgRSSI().getMean();
            if (buckets.containsKey(mean)) {
                List<APLocation> locations = buckets.get(mean);
                locations.add(location);
            } else {
                List<APLocation> locations = new ArrayList<>();
                locations.add(location);
                buckets.put(mean, locations);
            }
        }

        return buckets;
    }

    /*
     //Old and slow version
     private static List<List<APLocation>> compareLocations(final APData accessPoints, double rangeValue) {

     List<List<APLocation>> matches = new ArrayList<>();
     List<APLocation> locations = accessPoints.getLocations();

     for (int currCounter = 0; currCounter < locations.size(); currCounter++) {
     List<APLocation> matchPoints = new ArrayList<>();

     double upperValue = locations.get(currCounter).mean() + rangeValue;
     double lowerValue = locations.get(currCounter).mean() - rangeValue;

     for (int checkCounter = currCounter + 1; checkCounter < locations.size(); checkCounter++) {
     double checkMean = locations.get(checkCounter).mean();
     if (checkMean <= upperValue && checkMean >= lowerValue) {
     matchPoints.add(locations.get(checkCounter));
     locations.remove(checkCounter);     //Remove the point so that it isn't used to compare values
     }
     }

     if (!matchPoints.isEmpty()) //Matches have been found so add on the current location as well
     {
     matchPoints.add(locations.get(currCounter));
     matches.add(matchPoints);  //Store the list of matches
     }
     }

     return matches;
     }
     */
    private static BufferedImage drawLocations(final List<APLocation> matchLocations, BufferedImage floorPlanImage, Integer maxFrequency) {

        final int SIZE = 10;
        final int HALF_SIZE = SIZE / 2;

        Graphics2D floorPlan = floorPlanImage.createGraphics();
        floorPlan.setComposite(AlphaComposite.SrcOver);
        

        for (APLocation location : matchLocations) {
            Point pos = location.getDrawPoint();
            floorPlan.setColor(setColour(location.getAvgRSSI().getFrequency(), maxFrequency));
            //Draw oval                
            floorPlan.fillOval(pos.getXint() - HALF_SIZE, pos.getYint() - HALF_SIZE, SIZE, SIZE);

            //include text label for mean
            Double mean = location.getAvgRSSI().getMean();
            BigDecimal bd = new BigDecimal(mean);
            bd = bd.setScale(2, RoundingMode.HALF_UP);
            floorPlan.drawString(String.valueOf(bd), pos.getXint() + HALF_SIZE, pos.getYint());
        }

        floorPlan.dispose();

        return floorPlanImage;

    }
    
    private static Color setColour(int frequency, int total){
           
        
        final float saturation = 1.0f;
        final float brightness = 1.0f;
        final int alpha = 255; //1.0f * 255 - full alpha

        final float paletteRange = 330.0f / 360.0f; //Constrain the pallete to between Red (0 degrees) and Pink (330 degrees).

        float hue = ((float) frequency / total) * paletteRange;

        Color tempColor = Color.getHSBColor(hue, saturation, brightness);

        return new Color(tempColor.getRed(), tempColor.getGreen(), tempColor.getBlue(), alpha);                    
    }
}
