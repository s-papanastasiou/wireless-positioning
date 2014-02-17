/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.company.support;

import com.company.Main;
import com.company.methods.ParticleFilter;
import com.company.methods.Probabilistic;
import com.company.methods.Particle;
import com.company.methods.Data;
import com.company.methods.InertialPoint;
import com.company.methods.Cloud;
import com.company.methods.ForceToMap;
import com.company.methods.InertialData;
import com.company.methods.Intialise;
import datastorage.KNNFloorPoint;
import datastorage.KNNTrialPoint;
import filehandling.KNNRSSI;
import general.AvgValue;
import general.Point;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import visualinfo.DisplayRoute;

/**
 *
 * @author Gerg
 */
public class Simulation {

    //Co-ordinates for image drawing
    private static final double X_PIXELS = 1192.0 / 55;
    private static final double Y_PIXELS = 538.0 / 23.75;

    private final static double HALF_PI = Math.PI / 2;

    private static double distance(KNNTrialPoint knnTrialPoint, Point point) {

        double x, y;

        x = point.getX() - knnTrialPoint.getFloorPoint().getxRef();
        y = point.getY() - knnTrialPoint.getFloorPoint().getyRef();

        return Math.hypot(x, y);
    }

    private static String getTrialResult(int lineNumber, KNNTrialPoint knnTrialPoint, double trialDistance, Point bestPoint, String OUT_SEP) {

        int trialX = knnTrialPoint.getFloorPoint().getxRef();
        int trialY = knnTrialPoint.getFloorPoint().getyRef();
        double posX = bestPoint.getX();
        double posY = bestPoint.getY();

        return lineNumber + OUT_SEP + trialX + OUT_SEP + trialY + OUT_SEP + trialDistance + OUT_SEP + posX + OUT_SEP + posY;
    }

    public static void runProbabilistic(FileController fc, ProbabilisticSettings proSettings, Logging probabilisticResultsLog, boolean isTrialDetail, boolean isOutputImage) {

        String OUT_SEP = proSettings.getOUT_SEP();

        //Load offline map, online points (trial scan points) and intial points (stationary readings at start of trial).
        HashMap<String, KNNFloorPoint> offlineMap = KNNRSSI.compile(fc.offlineDataList, proSettings.isBSSIDMerged(), proSettings.isOrientationMerged());
        List<KNNTrialPoint> onlinePoints = KNNRSSI.compileTrialList(fc.onlineDataList, proSettings.isBSSIDMerged(), proSettings.isOrientationMerged());

        //Lists to store the results.
        List<Point> trialPoints = new ArrayList<>();
        List<Point> probabilisticFinalPoints = new ArrayList<>();

        //Setup trial logs to record point data within the trial.        
        //Trial Logging - split into different folders for when compass used or not
        fc.switchDirectories(proSettings.isOrientationMerged());

        Logging probabilisticTrialLog = new Logging(isTrialDetail, new File(fc.probabilisticTrialDir, String.format("Trial %s.csv", proSettings.getTitle())));
        probabilisticTrialLog.printLine(Main.trialHeader);

        //System.out.println("Running probabilistic trial:" + probabilisticTrialName);            
        //Initiliase variables for trial
        int currentInertialIndex = 0;
        int orientation = Probabilistic.NO_ORIENTATION;
        AvgValue totalDistance = new AvgValue();

        //Reset line numbering
        int lineNumber = 0;

        final List<Data> inertialDataList = fc.inertialDataList;

        //Loop through each of the points in the trial.            
        for (KNNTrialPoint knnTrialPoint : onlinePoints) {

            for (int i = currentInertialIndex; i < inertialDataList.size(); i++) {
                Data sensorData = inertialDataList.get(i);
                if (sensorData.getTimestamp() < knnTrialPoint.getTimestamp()) {  //Move using the sensor data

                    sensorData.getOrientation()[0] += HALF_PI - proSettings.getBuildingOrientation();
                    orientation = InertialData.getOrientation(proSettings.isOrientationMerged(), sensorData.getOrientation()[0], proSettings.getBuildingOrientation());

                } else {      //Move using the wifi data
                    currentInertialIndex = i;   //store the next index to be used
                    break; //exit the loop
                }
            }

            Point probabilisticPoint = Probabilistic.run(knnTrialPoint.getFloorPoint(), offlineMap, proSettings.getK(), orientation);

            Point bestPoint = ForceToMap.findBestPoint(offlineMap, probabilisticPoint, proSettings.isForceToOfflineMap());

            double trialDistance = distance(knnTrialPoint, bestPoint);
            totalDistance.add(trialDistance);

            //Store the points for drawing
            if (isOutputImage) {
                trialPoints.add(new Point(knnTrialPoint.getFloorPoint().getxRef() * X_PIXELS, knnTrialPoint.getFloorPoint().getyRef() * Y_PIXELS));
                probabilisticFinalPoints.add(new Point(bestPoint.getX() * X_PIXELS, bestPoint.getY() * Y_PIXELS));
            }
            //Log the trial results
            if (probabilisticTrialLog.isLogging()) {
                String probabilisticTrialResult = getTrialResult(lineNumber, knnTrialPoint, trialDistance, bestPoint, OUT_SEP);
                probabilisticTrialLog.printLine(probabilisticTrialResult);
            }
            //Increment the line number
            lineNumber++;
        }

        probabilisticTrialLog.close();

        //Logging of the aggregate results for the trial - same as the name given to individual trial file names but with the mean distance error added.                       
        String probabilisticResults = proSettings.getValues() + OUT_SEP + totalDistance.getMean() + OUT_SEP + totalDistance.getStdDev();
        probabilisticResultsLog.printLine(probabilisticResults);

        //Draw the image for the trial  
        if (isOutputImage) {
            File probabilisticOutputImageFile = new File(fc.probImageDir, "Trial " + proSettings.getTitle());
            DisplayRoute.draw(trialPoints, probabilisticFinalPoints, probabilisticOutputImageFile, fc.image);
        }
    }

    public static void runParticle(FileController fc, List<ParticleSettings> parSettingsList, Logging particleResultsLog, boolean isTrialDetail, boolean isOutputImage) {

        for (ParticleSettings parSettings : parSettingsList) {

            String OUT_SEP = parSettings.getOUT_SEP();
            //Load offline map, online points (trial scan points) and intial points (stationary readings at start of trial).
            HashMap<String, KNNFloorPoint> offlineMap = KNNRSSI.compile(fc.offlineDataList, parSettings.isBSSIDMerged(), parSettings.isOrientationMerged());
            List<KNNTrialPoint> onlinePoints = KNNRSSI.compileTrialList(fc.onlineDataList, parSettings.isBSSIDMerged(), parSettings.isOrientationMerged());
            List<KNNTrialPoint> initialPoints = KNNRSSI.compileTrialList(fc.initialDataList, parSettings.isBSSIDMerged(), parSettings.isOrientationMerged());

            //Lists to store the results.
            List<Point> trialPoints = new ArrayList<>();
            List<Point> particleFinalPoints = new ArrayList<>();

            //Setup trial logs to record point data within the trial.            
            //Trial Logging - split into different folders for when compass used or not
            fc.switchDirectories(parSettings.isOrientationMerged());

            Logging particleTrialLog = new Logging(isTrialDetail, new File(fc.particleTrialDir, String.format("Trial %s.csv", parSettings.getTitle())));
            particleTrialLog.printLine(Main.trialHeader);

            //System.out.println("Running particle trial:" + particleTrialName);
            //Calculate initial points to calculate where the particle filter starts.
            Point initialPoint = Intialise.initialPoint(initialPoints, parSettings.getInitRSSIReadings(), offlineMap, parSettings.getK(), Probabilistic.NO_ORIENTATION);

            //Find the time of the last intialisation point. Intertial readings until this time are ignored. 
            long lastTimestamp = initialPoints.get(initialPoints.size() - 1).getTimestamp();
            InertialPoint inertialPoint = new InertialPoint(initialPoint, lastTimestamp);

            //Initiliase variables for trial
            int currentInertialIndex = 0;
            int orientation = Probabilistic.NO_ORIENTATION;
            AvgValue totalDistance = new AvgValue();

            Cloud cloud = null;

            //Reset line numbering
            int lineNumber = 0;

            final List<Data> inertialDataList = fc.inertialDataList;

            //Loop through each of the points in the trial.            
            for (KNNTrialPoint knnTrialPoint : onlinePoints) {

                for (int i = currentInertialIndex; i < inertialDataList.size(); i++) {
                    Data sensorData = inertialDataList.get(i);
                    if (sensorData.getTimestamp() < knnTrialPoint.getTimestamp()) {  //Move using the sensor data

                        sensorData.getOrientation()[0] += HALF_PI - parSettings.getBuildingOrientation();
                        InertialData results = InertialData.getDatas(sensorData.getInvertedMatrix(),
                                sensorData.getLinearAcceleration(), sensorData.getOrientation(),
                                parSettings.getBuildingOrientation());
                        inertialPoint = InertialPoint.move(inertialPoint, results, sensorData.getTimestamp(), parSettings.getSpeedBreak());
                        orientation = InertialData.getOrientation(parSettings.isOrientationMerged(), sensorData.getOrientation()[0], parSettings.getBuildingOrientation());

                    } else {      //Move using the wifi data
                        currentInertialIndex = i;   //store the next index to be used
                        break; //exit the loop
                    }
                }

                Point probabilisticPoint = Probabilistic.run(knnTrialPoint.getFloorPoint(), offlineMap, parSettings.getK(), orientation);

                if (cloud != null) {
                    cloud = ParticleFilter.filter(cloud, probabilisticPoint, inertialPoint, parSettings.getParticleCount(), parSettings.getCloudRange(), parSettings.getCloudDisplacementCoefficient());
                } else {
                    List<Particle> particles = ParticleFilter.createParticles(initialPoint, parSettings.getParticleCount());
                    cloud = new Cloud(initialPoint, particles);
                }

                Point bestPoint = ForceToMap.findBestPoint(offlineMap, cloud.getEstiPos(), parSettings.isForceToOfflineMap());

                double trialDistance = distance(knnTrialPoint, bestPoint);
                totalDistance.add(trialDistance);

                //Store the points for drawing
                if (isOutputImage) {
                    trialPoints.add(new Point(knnTrialPoint.getFloorPoint().getxRef() * X_PIXELS, knnTrialPoint.getFloorPoint().getyRef() * Y_PIXELS));
                    particleFinalPoints.add(new Point(bestPoint.getX() * X_PIXELS, bestPoint.getY() * Y_PIXELS));
                }
                //Log the trial results
                if (particleTrialLog.isLogging()) {
                    String particleTrialResult = getTrialResult(lineNumber, knnTrialPoint, trialDistance, bestPoint, OUT_SEP);
                    particleTrialLog.printLine(particleTrialResult);
                }
                //Increment the line number
                lineNumber++;
            }

            particleTrialLog.close();

            //Logging of the aggregate results for the trial - same as the name given to individual trial file names but with the mean distance error added.           
            String particleResults = parSettings.getValues() + OUT_SEP + totalDistance.getMean() + OUT_SEP + totalDistance.getStdDev();
            particleResultsLog.printLine(particleResults);

            //Draw the image for the trial
            if (isOutputImage) {
                File particleOutputImageFile = new File(fc.partImageDir, "Trial " + parSettings.getTitle());
                DisplayRoute.draw(trialPoints, particleFinalPoints, particleOutputImageFile, fc.image);
            }
        }
    }

}
