/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package visualinfo;

import accesspointvariant.APData;
import accesspointvariant.APFormat;
import accesspointvariant.APLocation;
import datastorage.RSSIData;
import filehandling.RoomInfo;
import general.Point;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.imageio.ImageIO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Greg Albiston
 */
public class MatchMap {

    private static final Logger logger = LoggerFactory.getLogger(MatchMap.class);
    
    public static void print(File workingPath, File floorPlanFile, final HashMap<String, RoomInfo> roomInfo, List<RSSIData> rssiDataList) {
        print(workingPath, floorPlanFile, roomInfo, rssiDataList, 0.0f, false, false, ",");
    }

    public static void print(File workingPath, File floorPlanFile, final HashMap<String, RoomInfo> roomInfo, List<RSSIData> rssiDataList, final double rangeValue, final boolean isBSSIDMerge, final boolean isOrientationMerge, final String fieldSeparator) {

        //create sub-folder
        File matchPath = new File(workingPath, "matchmaps");
        matchPath.mkdir();

        logger.info("Starting match analysis.");
        logger.info("Range value +/- {}", rangeValue);

        final String IMAGE_FILE_STEM = String.format("MatchAnalysis %s ", rangeValue);
        final String LOG_FILE = String.format("MatchAnalysis %s.txt", rangeValue);

        File logFile = new File(matchPath, LOG_FILE);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(logFile, false))) {

            List<APData> accessPoints = APFormat.compileList(rssiDataList, isBSSIDMerge, isOrientationMerge);
            
            int matchCounter = 0;
            int noMatchCounter = 0;
            for (APData ap : accessPoints) {
                try {
                    String bssid = ap.getBSSID().replace(':', '-');  //Replace any colons with - so filenames are ok
                    logger.info("Analysing - {}", bssid);
                    List<List<APLocation>> matches = compareLocations(ap, rangeValue);

                    printLocations(bssid, matches, writer, fieldSeparator);
                    writer.flush();

                    if (!matches.isEmpty()) {  //draw image of the matches
                        BufferedImage floorPlanImage = ImageIO.read(floorPlanFile);

                        for (int counter = 0; counter < matches.size(); counter++) {

                            List<APLocation> matchLocations = matches.get(counter);
                            if (!matchLocations.isEmpty()) {
                                floorPlanImage = drawLocations(counter, matches.size(), matchLocations, floorPlanImage, roomInfo);
                            }
                        }

                        File outputfile = new File(matchPath, IMAGE_FILE_STEM + bssid + ".png");
                        ImageIO.write(floorPlanImage, "png", outputfile);
                        
                        matchCounter++;
                    }else{
                        noMatchCounter++;
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

    private static void printLocations(String bssid, List<List<APLocation>> matches, BufferedWriter writer, String fieldSeparator) throws IOException {

        String outputText = "";
        if (matches.isEmpty()) {
            outputText = String.format("BSSID: %s%s No Matches%s", bssid, fieldSeparator, System.lineSeparator());

        } else {

            for (int counter = 0; counter < matches.size(); counter++) {
                outputText += String.format("BSSID: %s%sRSSI: %s%sMatches: %d", bssid, fieldSeparator, matches.get(counter).get(0).mean(), fieldSeparator, matches.get(counter).size());
                List<APLocation> locations = matches.get(counter);
                for (APLocation location : locations) {
                    outputText = outputText.concat(String.format("%sLocation:%s Mean:%s", fieldSeparator, location.toStringLocation(), location.mean()));
                }
                outputText += System.lineSeparator();
            }
        }

        writer.write(outputText);
        logger.info(outputText);
    }

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

    private static BufferedImage drawLocations(final int index, final int total, final List<APLocation> matchLocations, BufferedImage floorPlanImage, final HashMap<String, RoomInfo> roomInfo) {

        final int SIZE = 10;
        final int HALF_SIZE = SIZE / 2;

        Graphics2D floorPlan = floorPlanImage.createGraphics();

        floorPlan.setColor(getColor(index, total));

        for (int counter = 0; counter < matchLocations.size(); counter++) {

            APLocation location = matchLocations.get(counter);

            RoomInfo room = roomInfo.get(location.getRoom());

            Point pos = room.getPoint(location.getxRef(), location.getyRef());

            //Draw oval                
            floorPlan.fillOval(pos.getXint() - HALF_SIZE, pos.getYint() - HALF_SIZE, SIZE, SIZE);

            //include text label for index              
            floorPlan.drawString(String.valueOf(index), pos.getXint() + HALF_SIZE, pos.getYint());

        }

        floorPlan.dispose();

        return floorPlanImage;

    }

    private static Color getColor(int index, int total) {

        final float saturation = 1.0f;
        final float brightness = 1.0f;
        final int alpha = 255; //1.0f * 255 - full alpha

        final float paletteRange = 330.0f / 360.0f; //Constrain the pallete to between Red (0 degrees) and Pink (330 degrees).

        float hue = ((float) index / total) * paletteRange;

        Color tempColor = Color.getHSBColor(hue, saturation, brightness);

        return new Color(tempColor.getRed(), tempColor.getGreen(), tempColor.getBlue(), alpha);

    }
}
