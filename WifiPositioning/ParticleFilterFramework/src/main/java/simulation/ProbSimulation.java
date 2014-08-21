/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package simulation;

import particlefilterlibrary.AccelerometerData;
import particlefilterlibrary.InertialData;
import configuration.FileController;
import configuration.Logging;
import configuration.SettingsProperties;
import datastorage.KNNFloorPoint;
import datastorage.KNNTrialPoint;
import datastorage.Location;
import filehandling.KNNRSSI;
import general.AvgValue;
import general.Locate;
import general.Point;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import distancealgorithms.Probabilistic;
import visualinfo.DisplayRoute;

/**
 *
 * @author SST3ALBISG
 */
public class ProbSimulation {
 
    public static void run(SettingsProperties sp, FileController fc, ProbabilisticTrial proTrial, Logging probabilisticResultsLog) {

        String OUT_SEP = sp.OUT_SEP();
        final double BUILD_ORIENT = sp.BUILD_ORIENT();
        final double ADJUSTED_ORIENT = Simulation.HALF_PI - BUILD_ORIENT;

        //Load offline map, online points (trial scan points) and intial points (stationary readings at start of trial).
        HashMap<String, KNNFloorPoint> offlineMap = KNNRSSI.compile(fc.offlineDataList, proTrial.isBSSIDMerged(), proTrial.isOrientationMerged());
        List<KNNTrialPoint> onlinePoints = KNNRSSI.compileTrialList(fc.onlineDataList, proTrial.isBSSIDMerged(), proTrial.isOrientationMerged());

        //Lists to store the results.
        List<Point> trialPoints = new ArrayList<>();
        List<Point> probabilisticFinalPoints = new ArrayList<>();

        //Setup trial logs to record point data within the trial.        
        //Trial Logging - split into different folders for when compass used or not
        fc.switchDirectories(proTrial.isOrientationMerged());

        Logging probabilisticTrialLog = new Logging(sp.isTrialDetail(), new File(fc.probabilisticTrialDir, String.format("Trial %s.csv", proTrial.getTitle())));
        probabilisticTrialLog.printLine(sp.TRIAL_HEADER());

        //logger.info("Running probabilistic trial: {}", probabilisticTrialName);            
        //Initiliase variables for trial
        int currentInertialIndex = 0;
        int orientation = KNNFloorPoint.NO_ORIENTATION;
        AvgValue totalDistance = new AvgValue();

        //Reset line numbering
        int lineNumber = 0;

        final List<AccelerometerData> inertialDataList = fc.inertialDataList;

        //Loop through each of the points in the trial.            
        for (KNNTrialPoint knnTrialPoint : onlinePoints) {

            for (int i = currentInertialIndex; i < inertialDataList.size(); i++) {
                AccelerometerData sensorData = inertialDataList.get(i);
                if (sensorData.getTimestamp() < knnTrialPoint.getTimestamp()) {  //Move using the sensor data

                    sensorData.getOrientation()[0] += ADJUSTED_ORIENT;
                    orientation = InertialData.getOrientation(proTrial.isOrientationMerged(), sensorData.getOrientation()[0], BUILD_ORIENT);

                } else {      //Move using the wifi data
                    currentInertialIndex = i;   //store the next index to be used
                    break; //exit the loop
                }
            }

            Location probabilisticLocation = Probabilistic.run(knnTrialPoint, offlineMap, sp.ROOM_INFO(),  proTrial.getK(), orientation);            
            Location bestLocation = Locate.forceToMap(offlineMap, probabilisticLocation, proTrial.isForceToOfflineMap());
            
            double trialDistance = knnTrialPoint.distance(bestLocation);
            totalDistance.add(trialDistance);

            //Store the points for drawing
            if (sp.isOutputImage()) {
                trialPoints.add(knnTrialPoint.getDrawPoint());
                probabilisticFinalPoints.add(bestLocation.getDrawPoint());
            }
            //Log the trial results
            if (probabilisticTrialLog.isLogging()) {
                String probabilisticTrialResult = Simulation.getTrialResult(lineNumber, knnTrialPoint, trialDistance, bestLocation, OUT_SEP);
                probabilisticTrialLog.printLine(probabilisticTrialResult);
            }
            //Increment the line number
            lineNumber++;
        }

        probabilisticTrialLog.close();

        //Logging of the aggregate results for the trial - same as the name given to individual trial file names but with the mean distance error added.                       
        String probabilisticResults = proTrial.getValues() + OUT_SEP + totalDistance.getMean() + OUT_SEP + totalDistance.getStdDev();
        probabilisticResultsLog.printLine(probabilisticResults);

        //Draw the image for the trial
        if (sp.isOutputImage()) {
            DisplayRoute.print(fc.probImageDir, "Trial " + proTrial.getTitle(), fc.image, trialPoints, probabilisticFinalPoints);
        }
    }
    
}
