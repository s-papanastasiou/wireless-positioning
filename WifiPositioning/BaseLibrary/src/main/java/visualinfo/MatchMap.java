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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeSet;
import javax.imageio.ImageIO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Identifies points with matching or similar values to others for that access
 * point and draws onto an image.
 *
 * @author Greg Albiston
 */
public class MatchMap {

    private static final Logger logger = LoggerFactory.getLogger(MatchMap.class);

    /**
     * Identifies points with matching or similar values to others for that
     * access point and draws onto an image, based on "MatchAnalysis-xx"
     * filename, and stores the new image on the specified file path.
     *
     * Also, outputs a list of matching locations to text file.
     *
     * @param workingPath Path to store the image.
     * @param floorPlanFile Floor plan image to draw upon.
     * @param rssiDataList List of locations to draw.
     * @param roomInfo Information about the floor plan for drawing.
     */
    public static void print(final File workingPath, final File floorPlanFile, final List<RSSIData> rssiDataList, final HashMap<String, RoomInfo> roomInfo) {
        print(workingPath, "MatchAnalysis", floorPlanFile, rssiDataList, roomInfo, 0.5, false, false, ",");
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
     * @param isBSSIDMerged True, if last hex pair of BSSID is to be ignored.
     * @param isOrientationMerged True, if W-Ref of location is to be ignored.
     * @param fieldSeparator Separator used between each heading in output file.
     */
    public static void print(final File workingPath, final String filename, final File floorPlanFile, final List<RSSIData> rssiDataList, final HashMap<String, RoomInfo> roomInfo, final Double rangeValue, final Boolean isBSSIDMerged, final Boolean isOrientationMerged, final String fieldSeparator) {

        //create sub-folder
        File matchPath = new File(workingPath, "MatchMaps");
        matchPath.mkdir();

        logger.info("Starting match analysis.");
        logger.info("Range value +/- {}", rangeValue);

        final String IMAGE_FILE_STEM = String.format("%s-%s ", filename, rangeValue);
        final String LOG_FILE = String.format("%s-%s.txt", filename, rangeValue);
        final String SUMMARY_FILE = String.format("%s-Summary.txt", filename);

        File logFile = new File(matchPath, LOG_FILE);
        File summaryFile = new File(matchPath, SUMMARY_FILE);

        BufferedWriter logWriter = null;
        BufferedWriter summaryWriter = null;

        try {
            logWriter = new BufferedWriter(new FileWriter(logFile, false));
            summaryWriter = new BufferedWriter(new FileWriter(summaryFile, false));

            List<APData> accessPoints = APFormat.compileList(rssiDataList, isBSSIDMerged, isOrientationMerged);

            int matchCounter = 0;
            int noMatchCounter = 0;
            for (APData ap : accessPoints) {
                try {
                    String bssid = ap.getBSSID().replace(':', '-');  //Replace any colons with - so filenames are ok
                    logger.info("Analysing - {}", bssid);
                    List<List<APLocation>> matches = compareLocations(ap, rangeValue);

                    printLocations(bssid, matches, logWriter, fieldSeparator);
                    printSummary(bssid, matches, ap.getLocations().size(), summaryWriter, fieldSeparator);

                    if (!matches.isEmpty()) {  //draw image of the matches
                        BufferedImage floorPlanImage = ImageIO.read(floorPlanFile);

                        for (int counter = 0; counter < matches.size(); counter++) {
                            List<APLocation> matchLocations = matches.get(counter);
                            if (!matchLocations.isEmpty()) {
                                //floorPlanImage = drawAPArea(ap, floorPlanImage, roomInfo);
                                floorPlanImage = drawLocations(counter, matches.size(), matchLocations, floorPlanImage);
                            }
                        }

                        File outputfile = new File(matchPath, IMAGE_FILE_STEM + bssid + ".png");
                        ImageIO.write(floorPlanImage, "png", outputfile);

                        matchCounter++;
                    } else {
                        noMatchCounter++;
                    }

                } catch (IOException ex) {
                    logger.error(ex.getMessage());
                }

                logger.info("Access Point total: {} Matches: {} No Matches: {}", accessPoints.size(), matchCounter, noMatchCounter);
            }
        } catch (IOException ex) {
            logger.error(ex.getMessage());
        } finally {
            try {
                if (logWriter != null) {
                    logWriter.close();
                }
                if (summaryWriter != null) {
                    summaryWriter.close();
                }

            } catch (IOException ex) {
                logger.error(ex.getMessage());
            }

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

    private static void printLocations(String bssid, List<List<APLocation>> matches, BufferedWriter writer, String fieldSeparator) throws IOException {

        String outputText = "";
        if (matches.isEmpty()) {
            outputText = String.format("BSSID: %s%sNo Matches%s", bssid, fieldSeparator, System.lineSeparator());

        } else {

            for (List<APLocation> match : matches) {
                outputText += String.format("BSSID: %s%sRSSI: %s%sMatches: %d", bssid, fieldSeparator, match.get(0).mean(), fieldSeparator, match.size());

                for (APLocation location : match) {
                    outputText = outputText.concat(String.format("%sLocation:%s Mean:%s", fieldSeparator, location.toStringLocation(), location.mean()));
                }
                outputText += System.lineSeparator();
            }
        }

        writer.write(outputText);
        logger.info(outputText);
        writer.flush();
    }

    private static void printSummary(String bssid, List<List<APLocation>> matches, Integer totalLocations, BufferedWriter writer, String fieldSeparator) throws IOException {

        Integer value = 0;

        for (List<APLocation> match : matches) {
            value += match.size();
        }

        String outputText = String.format("%s%s%s%s%s%s", bssid, fieldSeparator, value, fieldSeparator, totalLocations, System.lineSeparator());
        writer.write(outputText);
        logger.info(outputText);
        writer.flush();
    }

    /**
     * Compare mean values for access point locations and find matches within
     * the specified range.
     *
     * @param accessPoint
     * @param rangeValue
     * @return
     */
    private static List<List<APLocation>> compareLocations(final APData accessPoint, final double rangeValue) {

        HashMap<Double, List<APLocation>> buckets = sortLocations(accessPoint);
        List<List<APLocation>> matches = new ArrayList<>();
        TreeSet<Double> means = new TreeSet<>(buckets.keySet());

        for (Double targetMean : means) {

            List<APLocation> matchPoints = new ArrayList<>();

            //Check lower values
            Double element = targetMean;
            double lowerValue = targetMean - rangeValue;

            while (element != null) {

                if (element >= lowerValue) {
                    List<APLocation> locations = buckets.get(element);
                    matchPoints.addAll(locations);
                    element = means.lower(element);
                } else {
                    break;
                }
            }

            //Check upper values - move up immeadiately so don't target mean again.
            element = means.higher(targetMean);
            double upperValue = targetMean + rangeValue;
            while (element != null) {

                if (element < upperValue) {
                    List<APLocation> locations = buckets.get(element);
                    matchPoints.addAll(locations);
                    element = means.higher(element);
                } else {
                    break;
                }
            }

            //Check there is more than one point that matches
            if (matchPoints.size() > 1) {
                matches.add(matchPoints);
            }
        }

        return matches;

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
    private static BufferedImage drawLocations(final int index, final int total, final List<APLocation> matchLocations, BufferedImage floorPlanImage) {

        final int SIZE = 10;
        final int HALF_SIZE = SIZE / 2;

        Graphics2D floorPlan = floorPlanImage.createGraphics();
        floorPlan.setComposite(AlphaComposite.SrcOver);
        floorPlan.setColor(getColor(index, total));

        for (APLocation location : matchLocations) {
            Point pos = location.getDrawPoint();

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
