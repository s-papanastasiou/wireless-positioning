/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package methods;

import support.FilterProperties;
import support.FileController;
import support.Logging;
import support.ParticleTrial;
import support.SettingsProperties;
import support.Simulation;
import datastorage.KNNFloorPoint;
import datastorage.KNNTrialPoint;
import filehandling.KNNRSSI;
import general.AvgValue;
import general.Locate;
import general.Point;
import java.io.File;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import visualinfo.DisplayRoute;

/**
 *
 * @author SST3ALBISG
 */
public class ParticleSimulation {
    public static void run(SettingsProperties sp, FileController fc, FilterProperties fp, ParticleTrial parTrial, Logging particleResultsLog) {

        String OUT_SEP = sp.OUT_SEP();

        final Double BUILD_ORIENT = sp.BUILD_ORIENT();
        final Double ADJUSTED_ORIENT = Simulation.HALF_PI - BUILD_ORIENT;
        final Double JITTER_OFFSET = fp.JITTER_OFFSET();
        final Float[] ACCELERATION_OFFSET = fp.ACCELERATION_OFFSET();
        final EnumMap<FilterProperties.Threshold, Float> CLOUD_BOUNDARY = fp.CLOUD_BOUNDARY();
        final EnumMap<FilterProperties.Threshold, Integer> CLOUD_PARTICLE_CREATION = fp.CLOUD_PARTICLE_CREATION();

        //Load offline map, online points (trial scan points) and intial points (stationary readings at start of trial).
        HashMap<String, KNNFloorPoint> offlineMap = KNNRSSI.compile(fc.offlineDataList, parTrial.isBSSIDMerged(), parTrial.isOrientationMerged());
        List<KNNTrialPoint> onlinePoints = KNNRSSI.compileTrialList(fc.onlineDataList, parTrial.isBSSIDMerged(), parTrial.isOrientationMerged());
        List<KNNTrialPoint> initialPoints = KNNRSSI.compileTrialList(fc.initialDataList, parTrial.isBSSIDMerged(), parTrial.isOrientationMerged());

        //Lists to store the results.
        List<Point> trialPoints = new ArrayList<>();
        List<Point> particleFinalPoints = new ArrayList<>();

            //Setup trial logs to record point data within the trial.            
        //Trial Logging - split into different folders for when compass used or not
        fc.switchDirectories(parTrial.isOrientationMerged());

        Logging particleTrialLog = new Logging(sp.isTrialDetail(), new File(fc.particleTrialDir, String.format("Trial %s.csv", parTrial.getTitle())));
        particleTrialLog.printLine(sp.TRIAL_HEADER());

            //logger.info("Running particle trial: {}", particleTrialName);
        //Calculate initial points to calculate where the particle filter starts.
        Point initialPoint = Intialise.initialPoint(initialPoints, parTrial.getInitRSSIReadings(), offlineMap, parTrial.getK(), Probabilistic.NO_ORIENTATION);

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

                    sensorData.getOrientation()[0] += ADJUSTED_ORIENT;
                    InertialData results = InertialData.getDatas(sensorData.getInvertedMatrix(),
                            sensorData.getLinearAcceleration(), sensorData.getOrientation(),
                            BUILD_ORIENT, JITTER_OFFSET, ACCELERATION_OFFSET);
                    inertialPoint = InertialPoint.move(inertialPoint, results, sensorData.getTimestamp(), parTrial.getSpeedBreak());
                    orientation = InertialData.getOrientation(parTrial.isOrientationMerged(), sensorData.getOrientation()[0], BUILD_ORIENT);

                } else {      //Move using the wifi data
                    currentInertialIndex = i;   //store the next index to be used
                    break; //exit the loop
                }
            }

            Point probabilisticPoint = Probabilistic.run(knnTrialPoint, offlineMap, parTrial.getK(), orientation);

            if (cloud != null) {
                cloud = ParticleFilter.filter(cloud, probabilisticPoint, inertialPoint, parTrial.getParticleCount(), parTrial.getCloudRange(), parTrial.getCloudDisplacementCoefficient(), CLOUD_BOUNDARY, CLOUD_PARTICLE_CREATION);
            } else {
                List<Particle> particles = ParticleFilter.createParticles(initialPoint, parTrial.getParticleCount());
                cloud = new Cloud(initialPoint, particles);
            }

            Point bestPoint = Locate.forceToMap(offlineMap, cloud.getEstiPos(), parTrial.isForceToOfflineMap());

            double trialDistance = knnTrialPoint.distance(bestPoint);
            totalDistance.add(trialDistance);

            //Store the points for drawing
            if (sp.isOutputImage()) {
                trialPoints.add(knnTrialPoint.drawPoint(sp.X_PIXELS(), sp.Y_PIXELS()));
                particleFinalPoints.add(bestPoint.drawPoint(sp.X_PIXELS(), sp.Y_PIXELS()));
            }
            //Log the trial results
            if (particleTrialLog.isLogging()) {
                String particleTrialResult = Simulation.getTrialResult(lineNumber, knnTrialPoint, trialDistance, bestPoint, OUT_SEP);
                particleTrialLog.printLine(particleTrialResult);
            }
            //Increment the line number
            lineNumber++;
        }

        particleTrialLog.close();

        //Logging of the aggregate results for the trial - same as the name given to individual trial file names but with the mean distance error added.           
        String particleResults = parTrial.getValues() + OUT_SEP + totalDistance.getMean() + OUT_SEP + totalDistance.getStdDev();
        particleResultsLog.printLine(particleResults);

        //Draw the image for the trial
        if (sp.isOutputImage()) {
            DisplayRoute.draw(fc.partImageDir, "Trial " + parTrial.getTitle(), fc.image, trialPoints, particleFinalPoints);
        }
    }
}
