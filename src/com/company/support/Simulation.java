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

    public static void run(FileController fc, List<AppSettings> appSettingsList, Logging particleResultsLog, Logging probabilisticResultsLog, String OUT_SEP) {

        for (AppSettings appSettings : appSettingsList) {

            //Load offline map, online points (trial scan points) and intial points (stationary readings at start of trial).
            HashMap<String, KNNFloorPoint> offlineMap = KNNRSSI.compile(fc.offlineDataList, appSettings.isBSSIDMerged(), appSettings.isOrientationMerged());
            List<KNNTrialPoint> onlinePoints = KNNRSSI.compileTrialList(fc.onlineDataList, appSettings.isBSSIDMerged(), appSettings.isOrientationMerged());
            List<KNNTrialPoint> initialPoints = KNNRSSI.compileTrialList(fc.initialDataList, appSettings.isBSSIDMerged(), appSettings.isOrientationMerged());

            //Lists to store the results.
            List<Point> trialPoints = new ArrayList<>();
            List<Point> particleFinalPoints = new ArrayList<>();
            List<Point> probabilisticFinalPoints = new ArrayList<>();

            //Setup trial logs to record point data within the trial.
            String particleTrialName = appSettings.getParticleTitle(OUT_SEP);
            String probabilisticTrialName = appSettings.getProbablisticTitle(OUT_SEP);

            //Trial Logging - split into different folders for when compass used or not
            fc.switchDirectories(appSettings.isOrientationMerged());

            Logging particleTrialLog = new Logging(new File(fc.particleTrialDir, String.format("Trial %s.csv", particleTrialName)));
            Logging probabilisticTrialLog = new Logging(new File(fc.probabilisticTrialDir, String.format("Trial %s.csv", probabilisticTrialName)));

            particleTrialLog.printLine(Main.trialHeader);
            probabilisticTrialLog.printLine(Main.trialHeader);

            System.out.println("Running particle trial:" + particleTrialName);
            System.out.println("Running probabilistic trial:" + probabilisticTrialName);

            //Calculate initial points to calculate where the particle filter starts.
            Point initialPoint = initialPoint(initialPoints, appSettings.getInitRSSIReadings(), offlineMap, appSettings.getK(), Probabilistic.NO_ORIENTATION);

            //Find the time of the last intialisation point. Intertial readings until this time are ignored. 
            long lastTimestamp = initialPoints.get(initialPoints.size() - 1).getTimestamp();
            InertialPoint inertialPoint = new InertialPoint(initialPoint, lastTimestamp);

            //Initiliase variables for trial
            int currentInertialIndex = 0;
            int orientation = Probabilistic.NO_ORIENTATION;
            double particleTotalDistance = 0.0;
            double probablisticTotalDistance = 0.0;
            Cloud cloud = null;

            //Reset line numbering
            int lineNumber = 0;

            final List<Data> inertialDataList = fc.inertialDataList;
            
            //Loop through each of the points in the trial.            
            for (KNNTrialPoint knnTrialPoint : onlinePoints) {

                for (int i = currentInertialIndex; i < inertialDataList.size(); i++) {
                    Data sensorData = inertialDataList.get(i);
                    if (sensorData.getTimestamp() < knnTrialPoint.getTimestamp()) {  //Move using the sensor data

                        sensorData.getOrientation()[0] += HALF_PI - appSettings.getBuildingOrientation();
                        InertialData results = InertialData.getDatas(sensorData.getInvertedMatrix(),
                                sensorData.getLinearAcceleration(), sensorData.getOrientation(),
                                appSettings.getBuildingOrientation());
                        inertialPoint = InertialPoint.move(inertialPoint, results, sensorData.getTimestamp(), appSettings.getSpeedBreak());
                        orientation = InertialData.getOrientation(appSettings.isOrientationMerged(), sensorData.getOrientation()[0], appSettings.getBuildingOrientation());

                    } else {      //Move using the wifi data
                        currentInertialIndex = i;   //store the next index to be used
                        break; //exit the loop
                    }
                }

                Point probabilisticPoint = Probabilistic.run(knnTrialPoint.getFloorPoint(), offlineMap, appSettings.getK(), orientation);

                double probabilisticTrialDistance = distance(knnTrialPoint, probabilisticPoint);
                probablisticTotalDistance += probabilisticTrialDistance;

                if (cloud != null) {
                    cloud = ParticleFilter.filter(cloud, probabilisticPoint, inertialPoint, appSettings.getParticleCount(), appSettings.getCloudRange(), appSettings.getCloudDisplacementCoefficient());
                } else {
                    List<Particle> particles = ParticleFilter.createParticles(initialPoint, appSettings.getParticleCount());
                    cloud = new Cloud(initialPoint, particles);
                }

                Point bestPoint = findBestPoint(offlineMap, cloud.getEstiPos(), appSettings.isForceToOfflineMap());

                double particleTrialDistance = distance(knnTrialPoint, bestPoint);
                particleTotalDistance += particleTrialDistance;

                //Store the points for drawing
                trialPoints.add(new Point(knnTrialPoint.getFloorPoint().getxRef() * X_PIXELS, knnTrialPoint.getFloorPoint().getyRef() * Y_PIXELS));
                particleFinalPoints.add(new Point(bestPoint.getX() * X_PIXELS, bestPoint.getY() * Y_PIXELS));
                probabilisticFinalPoints.add(new Point(probabilisticPoint.getX() * X_PIXELS, probabilisticPoint.getY() * Y_PIXELS));

                //Log the trial results
                String particleTrialResult = getTrialResult(lineNumber, knnTrialPoint, particleTrialDistance, bestPoint, OUT_SEP);
                particleTrialLog.printLine(particleTrialResult);

                String probabilisticTrialResult = getTrialResult(lineNumber, knnTrialPoint, probabilisticTrialDistance, probabilisticPoint, OUT_SEP);
                probabilisticTrialLog.printLine(probabilisticTrialResult);

                //Increment the line number
                lineNumber++;
            }

            particleTrialLog.close();
            probabilisticTrialLog.close();

            //Logging of the aggregate results for the trial - same as the name given to individual trial file names but with the mean distance error added.           
            String particleResults = particleTrialName + OUT_SEP + Double.toString(particleTotalDistance / (double) onlinePoints.size());
            particleResultsLog.printLine(particleResults);

            String probabilisticResults = probabilisticTrialName + OUT_SEP + Double.toString(probablisticTotalDistance / (double) onlinePoints.size());
            probabilisticResultsLog.printLine(probabilisticResults);

            //Draw the image for the trial
            File particleOutputImageFile = new File(fc.partImageDir, "Trial " + appSettings.getParticleImageTitle());
            DisplayRoute.draw(trialPoints, particleFinalPoints, particleOutputImageFile, fc.image);

            File probabilisticOutputImageFile = new File(fc.probImageDir, "Trial " +  appSettings.getProbablisticImageTitle());
            DisplayRoute.draw(trialPoints, probabilisticFinalPoints, probabilisticOutputImageFile, fc.image);
        }
    }

}
