/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package knnexecution;

import datastorage.KNNFloorPoint;
import datastorage.KNNTrialPoint;
import datastorage.Location;
import datastorage.ResultLocation;
import filehandling.KNNFormatStorage;
import filehandling.KNNRSSI;
import datastorage.RoomInfo;
import general.Locate;
import general.Point;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.imageio.ImageIO;
import knnframework.Menus;
import knnframework.Settings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import processing.DistanceMeasure;
import processing.Positioning;
import visualinfo.DisplayPosition;
import visualinfo.DisplayRoute;

/**
 *
 * @author Greg Albiston
 */
public class KNearestNeighbour {

    private final static String FILE_ROOT = "results-table";
    private final static String FILE_EXTENSION = ".csv";
    private final static String ROUTE_ROOT = "route";
    private static final String K_NEAREST_NEIGHBOUR_FOLDER = "KNearestNeighbour";

    private static final Logger logger = LoggerFactory.getLogger(KNearestNeighbour.class);

    public static void command(File workingPath, Settings settings, String[] args) {

        //Create sub folder
        File knnPath = new File(workingPath, K_NEAREST_NEIGHBOUR_FOLDER);
        knnPath.mkdir();

        KNNTrialSettings knnSettings = KNNTrialSettings.commandSetup(args);
        //compile to KNN format and the settings           
        HashMap<String, KNNFloorPoint> radioMap = KNNRSSI.compile(settings.getRadioMapList(), knnSettings.isBSSIDMerged, knnSettings.isOrientationMerged);
        List<KNNTrialPoint> trialList = KNNRSSI.compileTrialList(settings.getTrialList(), knnSettings.isBSSIDMerged, knnSettings.isOrientationMerged);

        runTrials(knnSettings, knnPath, radioMap, trialList, settings.getRoomInfo(), settings.getFloorPlan(), ",");
    }

    public static void start(File workingPath, Settings settings) {

        //create sub-folder
        File knnPath = new File(workingPath, K_NEAREST_NEIGHBOUR_FOLDER);
        knnPath.mkdir();

        logger.info("K Nearest Neighbour Algorithm Testing");

        KNNTrialSettings knnSettings = new KNNTrialSettings();

        //compile to KNN format
        logger.info("Radio map");
        HashMap<String, KNNFloorPoint> radioMap = KNNRSSI.compile(settings.getRadioMapList(), knnSettings.isBSSIDMerged, knnSettings.isOrientationMerged);

        //store the compiled data question
        if (Menus.Choice("Do you want to store the compiled K Nearest Neighbour data?")) {
            File knnFile = Menus.createFilename("Enter the filename for stored data (without extension): ", knnPath, ".knn");
            KNNFormatStorage.store(knnFile, radioMap);
        }

        logger.info("Trial data");
        List<KNNTrialPoint> trialList = KNNRSSI.compileTrialList(settings.getTrialList(), knnSettings.isBSSIDMerged, knnSettings.isOrientationMerged);

        runTrials(knnSettings, knnPath, radioMap, trialList, settings.getRoomInfo(), settings.getFloorPlan(), ",");
    }

    public static void runTrials(KNNTrialSettings trialSettings, File workingPath, HashMap<String, KNNFloorPoint> offlineMap, List<KNNTrialPoint> trialList, HashMap<String, RoomInfo> roomInfo, File floorPlanFile, String fieldSeparator) {

        File summaryFile = new File(workingPath, "Trial Summary.csv");
        try (BufferedWriter summaryWriter = new BufferedWriter(new FileWriter(summaryFile))) {
            KNNTrialResults.printSummaryHeading(summaryWriter, fieldSeparator, trialSettings.isVariance);
            for (int k = trialSettings.kLowerValue; k <= trialSettings.kUpperValue; k++) {      //Each K value
                for (DistanceMeasure distMeasure : trialSettings.getDistanceMeasure()) {      //Each Distance measure
                    for (double varLimit = trialSettings.varLowerLimit; varLimit <= trialSettings.varUpperLimit; varLimit += trialSettings.varLimitStep) {    //Each variance limit
                        for (int varCount = trialSettings.varLowerCount; varCount <= trialSettings.varUpperCount; varCount++) {       //Each variance count

                            //Setup the arrays to store the points for drawing
                            List<Point> trialPoints = new ArrayList<>();
                            List<Point> finalPoints = new ArrayList<>();

                            //Setup the sub-folders and filenames
                            File trialPath;
                            File estimatesPath;
                            File resultsFile;
                            String filename;

                            if (trialSettings.isVariance) {
                                //logger.info(String.format("Trial: %s-%s-%s-%s", k, distMeasure, varLimit, varCount));
                                trialPath = new File(workingPath, String.format("%s-%s-%s-%s", k, distMeasure, varLimit, varCount));
                                estimatesPath = new File(trialPath, "estimates");
                                resultsFile = new File(trialPath, String.format("%s-%s-%s-%s-%s%s", FILE_ROOT, k, distMeasure, varLimit, varCount, FILE_EXTENSION));
                                filename = String.format("%s-%s-%s-%s-%s", ROUTE_ROOT, k, distMeasure, varLimit, varCount);
                            } else {
                                //logger.info(String.format("Trial: %s-%s", k, distMeasure));
                                trialPath = new File(workingPath, String.format("%s-%s", k, distMeasure));
                                estimatesPath = new File(trialPath, "estimates");
                                resultsFile = new File(trialPath, String.format("%s-%s-%s%s", FILE_ROOT, k, distMeasure, FILE_EXTENSION));
                                filename = String.format("%s-%s-%s", ROUTE_ROOT, k, distMeasure);
                            }

                            trialPath.mkdir();
                            estimatesPath.mkdir();

                            //Open the output file for the results
                            try (BufferedWriter writer = new BufferedWriter(new FileWriter(resultsFile))) {

                                //store the settings for this execution of the algorithm                                
                                KNNExecuteSettings executeSettings;
                                if (trialSettings.isVariance) {
                                    executeSettings = new KNNExecuteSettings(k, distMeasure, fieldSeparator, varLimit, varCount);
                                } else {
                                    executeSettings = new KNNExecuteSettings(k, distMeasure, fieldSeparator);
                                }

                                KNNTrialResults trialResults = new KNNTrialResults(executeSettings);
                                //iterate over each point in the trial
                                for (KNNTrialPoint trialPoint : trialList) {
                                    KNNPointResult result = execute(estimatesPath, floorPlanFile, trialPoint, offlineMap, roomInfo, executeSettings, trialSettings.isPrintImages);
                                    trialResults.addResult(result);
                                    trialPoints.add(result.getTrialLocation().getDrawPoint());
                                    finalPoints.add(result.getFinalLocation().getDrawPoint());
                                }

                                //Write the results to file.
                                trialResults.printResults(writer);
                                trialResults.printSummary(summaryWriter);

                                //draw the trial route to the floor plan
                                if (trialSettings.isPrintImages) {
                                    DisplayRoute.print(trialPath, filename, floorPlanFile, trialPoints, finalPoints);
                                }

                            } catch (IOException ex) {
                                logger.error("Error writing trial {} to file: {}", filename, ex);
                            }
                        }
                    }
                }
            }
        } catch (IOException ex) {
            logger.error("Error writing trial {} to file: {}", summaryFile, ex);
        }
    }

    //perform positioning       
    //output columns - k value, distance measure, var limit, var count, location position, location co-ordinates, trial position, trial co-ordinates, co-ordinate distance, {estimate position, estimate co-ordinates, estimate value}         
    //draw the points and estimates on an image
    private static KNNPointResult execute(File estimatesPath, File floorPlanFile, KNNFloorPoint trialLocation, HashMap<String, KNNFloorPoint> offlineMap, HashMap<String, RoomInfo> roomInfo, KNNExecuteSettings executeSettings, Boolean isPrintImages) throws IOException {

        //find the position estimates
        List<ResultLocation> positionEstimates = Positioning.estimate(trialLocation, offlineMap, executeSettings.distMeasure);

        //Find the final position based on the k nearest values  
        if (executeSettings.isVariance) {
            positionEstimates = Positioning.nearestVarianceEstimates(trialLocation, positionEstimates, executeSettings.kValue, executeSettings.varLimit, executeSettings.varCount, offlineMap);
        } else {
            positionEstimates = Positioning.nearestEstimates(positionEstimates, executeSettings.kValue);
        }

        //calculate the co-ordinates of the final point and distance to the trial point       
        //Find the position based on the centre of mass of the estimates.        
        //final location, final co-ordinates, distance between trial and final, actual K (number of estimates included in the positioning - could be less than k value for variance filtering)
        boolean isBiggerBetter = executeSettings.distMeasure == DistanceMeasure.Probabilistic;
        Point finalPoint = Locate.findWeightedCentre(positionEstimates, isBiggerBetter);
        Location finalLocation = RoomInfo.searchPixelLocation(finalPoint, roomInfo);
        double metreDistance = finalLocation.distance(trialLocation);

        if (isPrintImages) {
            //Draw the estimates and final point to the floor plan.        
            BufferedImage floorPlanImage = DisplayPosition.render(floorPlanFile, finalLocation.getDrawPoint(), trialLocation, positionEstimates);

            //Draw the image to file                     
            File outputFile = new File(estimatesPath, executeSettings.filename(trialLocation, "estimates", ".png"));
            ImageIO.write(floorPlanImage, "png", outputFile);
        }
        return new KNNPointResult(trialLocation, finalLocation, positionEstimates, metreDistance, executeSettings);
    }

    public static String help() {
        String text = "K Nearest Neighbour Algorithm" + System.lineSeparator();
        text += "This setup provides two variants." + System.lineSeparator();
        text += "First variant: takes an  k value (integer) and distance measure option (integer)." + System.lineSeparator();

        text += System.lineSeparator();
        text += "Distance Measure options are " + System.lineSeparator();
        String[] options = KNNTrialSettings.getDistanceOptions();
        for (int counter = 0; counter < options.length; counter++) {
            text += counter + ": " + options[counter] + System.lineSeparator();
        }

        text += System.lineSeparator();
        text += "Example: AlogrithmFramework.jar -knn 4 1" + System.lineSeparator();
        text += "This will run a single trial (k=4 using option 1)." + System.lineSeparator();

        text += System.lineSeparator();
        text += "A range of trials can be run by providing four arguments." + System.lineSeparator();

        text += System.lineSeparator();
        text += "Example: AlogrithmFramework.jar -knn 2 4 1" + System.lineSeparator();
        text += "This will run three trials (k=2, k=3, k=4 using option 1)." + System.lineSeparator();
        text += "The second value in the range must be greater than or equal to the first value." + System.lineSeparator();

        text += System.lineSeparator();
        text += "Second variant: takes a k value (integer), distance measure option (integer), var limit (double), var count (integer)." + System.lineSeparator();
        text += "Var limit is the difference in RSSI dbms between survey and trial access points that is tolerated. Var count is the number of access points that are tolerated before the location is discounted from the nearest estimates." + System.lineSeparator();

        text += System.lineSeparator();
        text += "Example: AlogrithmFramework.jar -knn 4 1 2.0 5" + System.lineSeparator();
        text += "This will run a single trial (k=4 using option 1 with a tolerance of 2.0 dbms for upto 5 access points at each location)." + System.lineSeparator();

        text += System.lineSeparator();
        text += "A range of trials can be run by providing nine arguments. A step change value is used to increment the var limit in each trial." + System.lineSeparator();

        text += System.lineSeparator();
        text += "Example: AlogrithmFramework.jar -knn 2 4 1 2.0 4.0 1.0 3 5" + System.lineSeparator();
        text += "This will run twenty seven trials (k=2, k=3, k=4 using option 1 with tolerances between 2.0 and 4.0 with a step change of 1.0 with upto 3, 4 and 5 access points tolerated)." + System.lineSeparator();
        text += "The second value in each range must be greater than or equal to the first value." + System.lineSeparator();

        text += System.lineSeparator();
        return text;
    }

}
