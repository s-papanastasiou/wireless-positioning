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
import com.company.methods.InertialData;
import datastorage.KNNFloorPoint;
import datastorage.KNNTrialPoint;
import filehandling.KNNRSSI;
import general.Point;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
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

    //private static Cloud cloud;
    //private static InertialPoint inertialPoint;
    private static double distance(KNNTrialPoint knnTrialPoint, Point point) {

        double x, y;

        x = point.getX() - knnTrialPoint.getFloorPoint().getxRef();
        y = point.getY() - knnTrialPoint.getFloorPoint().getyRef();

        return Math.hypot(x, y);
    }

    private static Point initialPoint(List<KNNTrialPoint> initialPoints, int initReadings, HashMap<String, KNNFloorPoint> offlineMap, int k, int orientation) {

        double x = 0;
        double y = 0;

        if (initReadings <= initialPoints.size()) {

            for (int i = 0; i < initReadings; i++) {

                Point probabilisticPoint = Probabilistic.run(initialPoints.get(i).getFloorPoint(), offlineMap, k, orientation);
                x += probabilisticPoint.getX();
                y += probabilisticPoint.getY();
            }

        }

        return new Point(x / initReadings, y / initReadings);
    }

    private static Point findBestPoint(HashMap<String, KNNFloorPoint> offlineMap, Point estiPos, boolean forceToOfflineMap) {

        Point bestPoint = new Point();

        if (forceToOfflineMap) {

            double lowestDistance = Double.MAX_VALUE;

            Set<String> keys = offlineMap.keySet();
            for (String key : keys) {

                KNNFloorPoint knnFloorPoint = offlineMap.get(key);
                Point point = new Point(knnFloorPoint.getxRef(), knnFloorPoint.getyRef());

                double distance = Math.hypot(point.getX() - estiPos.getX(), point.getY() - (estiPos.getY()));

                if (distance < lowestDistance) {
                    bestPoint = point;
                    lowestDistance = distance;
                }
            }
        } else {

            bestPoint = estiPos;
        }

        return bestPoint;
    }

    private static String getTrialResult(int lineNumber, KNNTrialPoint knnTrialPoint, double trialDistance, Point bestPoint, String OUT_SEP) {

        int trialX = knnTrialPoint.getFloorPoint().getxRef();
        int trialY = knnTrialPoint.getFloorPoint().getyRef();
        double posX = bestPoint.getX();
        double posY = bestPoint.getY();

        return lineNumber + OUT_SEP + trialX + OUT_SEP + trialY + OUT_SEP + trialDistance + OUT_SEP + posX + OUT_SEP + posY;
    }

    public static void runProbabilistic(FileController fc, List<ProbabilisticSettings> proSettingsList, Logging probabilisticResultsLog, String OUT_SEP) {

        for (ProbabilisticSettings proSettings : proSettingsList) {
            //Load offline map, online points (trial scan points) and intial points (stationary readings at start of trial).
            HashMap<String, KNNFloorPoint> offlineMap = KNNRSSI.compile(fc.offlineDataList, proSettings.isBSSIDMerged(), proSettings.isOrientationMerged());
            List<KNNTrialPoint> onlinePoints = KNNRSSI.compileTrialList(fc.onlineDataList, proSettings.isBSSIDMerged(), proSettings.isOrientationMerged());
            List<KNNTrialPoint> initialPoints = KNNRSSI.compileTrialList(fc.initialDataList, proSettings.isBSSIDMerged(), proSettings.isOrientationMerged());

            //Lists to store the results.
            List<Point> trialPoints = new ArrayList<>();
            List<Point> probabilisticFinalPoints = new ArrayList<>();

            //Setup trial logs to record point data within the trial.
            String probabilisticTrialName = proSettings.getProbablisticTitle(OUT_SEP);

            //Trial Logging - split into different folders for when compass used or not
            fc.switchDirectories(proSettings.isOrientationMerged());

            Logging probabilisticTrialLog = new Logging(new File(fc.probabilisticTrialDir, String.format("Trial %s.csv", probabilisticTrialName)));

            probabilisticTrialLog.printLine(Main.trialHeader);

            System.out.println("Running probabilistic trial:" + probabilisticTrialName);

            //Calculate initial points to calculate where the particle filter starts.
            Point initialPoint = initialPoint(initialPoints, proSettings.getInitRSSIReadings(), offlineMap, proSettings.getK(), Probabilistic.NO_ORIENTATION);

            //Find the time of the last intialisation point. Intertial readings until this time are ignored. 
            long lastTimestamp = initialPoints.get(initialPoints.size() - 1).getTimestamp();
            InertialPoint inertialPoint = new InertialPoint(initialPoint, lastTimestamp);

            //Initiliase variables for trial
            int currentInertialIndex = 0;
            int orientation = Probabilistic.NO_ORIENTATION;
            double probablisticTotalDistance = 0.0;

            //Reset line numbering
            int lineNumber = 0;

            final List<Data> inertialDataList = fc.inertialDataList;

            //Loop through each of the points in the trial.            
            for (KNNTrialPoint knnTrialPoint : onlinePoints) {

                for (int i = currentInertialIndex; i < inertialDataList.size(); i++) {
                    Data sensorData = inertialDataList.get(i);
                    if (sensorData.getTimestamp() < knnTrialPoint.getTimestamp()) {  //Move using the sensor data

                        sensorData.getOrientation()[0] += HALF_PI - proSettings.getBuildingOrientation();
                        InertialData results = InertialData.getDatas(sensorData.getInvertedMatrix(),
                                sensorData.getLinearAcceleration(), sensorData.getOrientation(),
                                proSettings.getBuildingOrientation());
                        inertialPoint = InertialPoint.move(inertialPoint, results, sensorData.getTimestamp(), proSettings.getSpeedBreak());
                        orientation = InertialData.getOrientation(proSettings.isOrientationMerged(), sensorData.getOrientation()[0], proSettings.getBuildingOrientation());

                    } else {      //Move using the wifi data
                        currentInertialIndex = i;   //store the next index to be used
                        break; //exit the loop
                    }
                }

                Point probabilisticPoint = Probabilistic.run(knnTrialPoint.getFloorPoint(), offlineMap, proSettings.getK(), orientation);

                Point bestPoint = findBestPoint(offlineMap, probabilisticPoint, proSettings.isForceToOfflineMap());

                double probabilisticTrialDistance = distance(knnTrialPoint, bestPoint);
                probablisticTotalDistance += probabilisticTrialDistance;

                //Store the points for drawing
                trialPoints.add(new Point(knnTrialPoint.getFloorPoint().getxRef() * X_PIXELS, knnTrialPoint.getFloorPoint().getyRef() * Y_PIXELS));
                probabilisticFinalPoints.add(new Point(probabilisticPoint.getX() * X_PIXELS, probabilisticPoint.getY() * Y_PIXELS));

                //Log the trial results
                String probabilisticTrialResult = getTrialResult(lineNumber, knnTrialPoint, probabilisticTrialDistance, probabilisticPoint, OUT_SEP);
                probabilisticTrialLog.printLine(probabilisticTrialResult);

                //Increment the line number
                lineNumber++;
            }

            probabilisticTrialLog.close();

            //Logging of the aggregate results for the trial - same as the name given to individual trial file names but with the mean distance error added.                       
            String probabilisticResults = probabilisticTrialName + OUT_SEP + Double.toString(probablisticTotalDistance / (double) onlinePoints.size());
            probabilisticResultsLog.printLine(probabilisticResults);

            //Draw the image for the trial            
            File probabilisticOutputImageFile = new File(fc.probImageDir, "Trial " + proSettings.getProbablisticImageTitle());
            DisplayRoute.draw(trialPoints, probabilisticFinalPoints, probabilisticOutputImageFile, fc.image);
        }
    }

    public static void runParticle(FileController fc, List<ParticleSettings> parSettingsList, Logging particleResultsLog, String OUT_SEP) {

        for (ParticleSettings parSettings : parSettingsList) {

            //Load offline map, online points (trial scan points) and intial points (stationary readings at start of trial).
            HashMap<String, KNNFloorPoint> offlineMap = KNNRSSI.compile(fc.offlineDataList, parSettings.isBSSIDMerged(), parSettings.isOrientationMerged());
            List<KNNTrialPoint> onlinePoints = KNNRSSI.compileTrialList(fc.onlineDataList, parSettings.isBSSIDMerged(), parSettings.isOrientationMerged());
            List<KNNTrialPoint> initialPoints = KNNRSSI.compileTrialList(fc.initialDataList, parSettings.isBSSIDMerged(), parSettings.isOrientationMerged());

            //Lists to store the results.
            List<Point> trialPoints = new ArrayList<>();
            List<Point> particleFinalPoints = new ArrayList<>();

            //Setup trial logs to record point data within the trial.
            String particleTrialName = parSettings.getParticleTitle(OUT_SEP);

            //Trial Logging - split into different folders for when compass used or not
            fc.switchDirectories(parSettings.isOrientationMerged());

            Logging particleTrialLog = new Logging(new File(fc.particleTrialDir, String.format("Trial %s.csv", particleTrialName)));

            particleTrialLog.printLine(Main.trialHeader);

            System.out.println("Running particle trial:" + particleTrialName);

            //Calculate initial points to calculate where the particle filter starts.
            Point initialPoint = initialPoint(initialPoints, parSettings.getInitRSSIReadings(), offlineMap, parSettings.getK(), Probabilistic.NO_ORIENTATION);

            //Find the time of the last intialisation point. Intertial readings until this time are ignored. 
            long lastTimestamp = initialPoints.get(initialPoints.size() - 1).getTimestamp();
            InertialPoint inertialPoint = new InertialPoint(initialPoint, lastTimestamp);

            //Initiliase variables for trial
            int currentInertialIndex = 0;
            int orientation = Probabilistic.NO_ORIENTATION;
            double particleTotalDistance = 0.0;

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

                Point bestPoint = findBestPoint(offlineMap, cloud.getEstiPos(), parSettings.isForceToOfflineMap());

                double particleTrialDistance = distance(knnTrialPoint, bestPoint);
                particleTotalDistance += particleTrialDistance;

                //Store the points for drawing
                trialPoints.add(new Point(knnTrialPoint.getFloorPoint().getxRef() * X_PIXELS, knnTrialPoint.getFloorPoint().getyRef() * Y_PIXELS));
                particleFinalPoints.add(new Point(bestPoint.getX() * X_PIXELS, bestPoint.getY() * Y_PIXELS));

                //Log the trial results
                String particleTrialResult = getTrialResult(lineNumber, knnTrialPoint, particleTrialDistance, bestPoint, OUT_SEP);
                particleTrialLog.printLine(particleTrialResult);

                //Increment the line number
                lineNumber++;
            }

            particleTrialLog.close();

            //Logging of the aggregate results for the trial - same as the name given to individual trial file names but with the mean distance error added.           
            String particleResults = particleTrialName + OUT_SEP + Double.toString(particleTotalDistance / (double) onlinePoints.size());
            particleResultsLog.printLine(particleResults);

            //Draw the image for the trial
            File particleOutputImageFile = new File(fc.partImageDir, "Trial " + parSettings.getParticleImageTitle());
            DisplayRoute.draw(trialPoints, particleFinalPoints, particleOutputImageFile, fc.image);

        }
    }

}
