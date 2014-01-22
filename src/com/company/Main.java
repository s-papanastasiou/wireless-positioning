package com.company;

import datastorage.KNNFloorPoint;
import datastorage.KNNTrialPoint;
import datastorage.RSSIData;
import filehandling.KNNRSSI;
import filehandling.RSSILoader;
import general.Point;
import general.TimeStamp;
import visualinfo.DisplayRoute;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class Main {

    private final static double HALF_PI = Math.PI / 2;

    private final static String OFFLINE_MAP = "offlineMap.csv";
    private final static String ONLINE_WIFI_DATA = "onlineWifiDataA.csv";
    private final static String SETTINGS_FILE = "settingsFileA.csv";
    private final static String INITIAL_POINTS = "initialPointsA.csv";
    private final static String INERTIAL_DATA = "inertialDataA.csv";
    private final static String IMAGE = "floor2final.png";
    private final static String RESULTS_DIRECTORY = "Results";
    private final static String PARTICLE_IMAGE_DIRECTORY = "ParticleImages";
    private final static String PARTICLE_RESULTS_DIRECTORY = "ParticleResults";
    private final static String PROBABILISTIC_IMAGE_DIRECTORY = "ProbablisticImages";
    private final static String PROBABLISTIC_RESULTS_DIRECTORY = "ProbablisticResults";
    private final static String PARTICLE_COMPASS_IMAGE_DIRECTORY = "ParticleCompassImages";
    private final static String PARTICLE_COMPASS_RESULTS_DIRECTORY = "ParticleCompassResults";
    private final static String PROBABILISTIC_COMPASS_IMAGE_DIRECTORY = "ProbablisticCompassImages";
    private final static String PROBABILISTIC_COMPASS_RESULTS_DIRECTORY = "ProbablisticCompassResults";
    
    private final static String INPUT_SEPARATOR = ";";
    private final static String OUT_SEP = ",";
    private static final double X_PIXELS = 1192.0 / 55;
    private static final double Y_PIXELS = 538.0 / 23.75;
    private static Cloud cloud;
    private static InertialPoint inertialPoint;

    //Generate settings automatically, ignoring any input file
    private static boolean isGenerateSettings = true;

    // Logging headers /////////////////////////////////////////////////////////////////////////////////////////////        
    private final static String trialHeader = "Point_No" + OUT_SEP + "Trial_X" + OUT_SEP + "Trial_Y" + OUT_SEP + "Distance" + OUT_SEP + "Pos_X" + OUT_SEP + "Pos_Y";
    private final static String particleResultsHeader = "BSSIDMerged" + OUT_SEP + "OrientationMerged" + OUT_SEP + "KValue" + OUT_SEP + "InitialReadings" + OUT_SEP + "ParticleCount" + OUT_SEP + "CloudRange" + OUT_SEP + "CloudDisplacement" + OUT_SEP + "ForceToMap" + OUT_SEP + "MeanDistance";
    private final static String probabilisticResultsHeader = "BSSIDMerged" + OUT_SEP + "OrientationMerged" + OUT_SEP + "KValue" + OUT_SEP + "ForceToMap" + OUT_SEP + "MeanDistance";

    public static void main(String[] args) {

        // Output directories //////////////////////////////////////////////////////////////////////////////////////////
        File resultsDir = checkDir(null, RESULTS_DIRECTORY);
        File particleImageDir = checkDir(resultsDir, PARTICLE_IMAGE_DIRECTORY);
        File particleResultsDir = checkDir(resultsDir, PARTICLE_RESULTS_DIRECTORY);
        File probabilisticImageDir = checkDir(resultsDir, PROBABILISTIC_IMAGE_DIRECTORY);
        File probabilisticResultsDir = checkDir(resultsDir, PROBABLISTIC_RESULTS_DIRECTORY);
        
        File particleCompassImageDir = checkDir(resultsDir, PARTICLE_COMPASS_IMAGE_DIRECTORY);
        File particleCompassResultsDir = checkDir(resultsDir, PARTICLE_COMPASS_RESULTS_DIRECTORY);
        File probabilisticCompassImageDir = checkDir(resultsDir, PROBABILISTIC_COMPASS_IMAGE_DIRECTORY);
        File probabilisticCompassResultsDir = checkDir(resultsDir, PROBABILISTIC_COMPASS_RESULTS_DIRECTORY);

        // External files //////////////////////////////////////////////////////////////////////////////////////////////
        File offlineFile = new File(OFFLINE_MAP);
        File onlineWifiDataFile = new File(ONLINE_WIFI_DATA);
        File settingsFile = new File(SETTINGS_FILE);
        File initialPointsFile = new File(INITIAL_POINTS);
        File inertialDataFile = new File(INERTIAL_DATA);
        File image = new File(IMAGE);

        boolean isFileCheck = checkFiles(offlineFile, onlineWifiDataFile, settingsFile, initialPointsFile, inertialDataFile, image);
        if (isFileCheck) {

            System.out.println("Loading settings file");
            List<AppSettings> appSettingsList = loadSettings(settingsFile);

            System.out.println("Loading offline file");
            List<RSSIData> offlineDataList = RSSILoader.load(offlineFile, INPUT_SEPARATOR);

            System.out.println("Loading online file");
            List<RSSIData> onlineDataList = RSSILoader.load(onlineWifiDataFile, INPUT_SEPARATOR);

            System.out.println("Loading initial file");
            List<RSSIData> initialDataList = RSSILoader.load(initialPointsFile, INPUT_SEPARATOR);

            System.out.println("Loading inertial file");
            List<Data> inertialDataList = loadInertialData(inertialDataFile);

            //Particle Results Logging
            Logging particleResultsLog = new Logging(new File(resultsDir, "ParticleResults.csv"));
            particleResultsLog.printLine(particleResultsHeader);

            //Probabilistic Results Logging
            Logging probabilisticResultsLog = new Logging(new File(resultsDir, "ProbablisticResults.csv"));
            probabilisticResultsLog.printLine(probabilisticResultsHeader);

            //Loop through each set of settings
            for (AppSettings appSettings : appSettingsList) {

                //Load offline map, online points (trial scan points) and intial points (stationary readings at start of trial).
                HashMap<String, KNNFloorPoint> offlineMap = KNNRSSI.compile(offlineDataList, appSettings.isBSSIDMerged(), appSettings.isOrientationMerged());
                List<KNNTrialPoint> onlinePoints = KNNRSSI.compileTrialList(onlineDataList, appSettings.isBSSIDMerged(), appSettings.isOrientationMerged());
                List<KNNTrialPoint> initialPoints = KNNRSSI.compileTrialList(initialDataList, appSettings.isBSSIDMerged(), appSettings.isOrientationMerged());

                //Lists to store the results.
                List<Point> trialPoints = new ArrayList<>();
                List<Point> particleFinalPoints = new ArrayList<>();
                List<Point> probabilisticFinalPoints = new ArrayList<>();

                //Calculate initial points to calculate where the particle filter starts.
                Point initialPoint = initialPoint(initialPoints, appSettings.getInitRSSIReadings(), offlineMap, appSettings.getK(), Probabilistic.NO_ORIENTATION);

                //Find the time of the last intialisation point. Intertial readings until this time are ignored. 
                long lastTimestamp = initialPoints.get(initialPoints.size() - 1).getTimestamp();
                inertialPoint = new InertialPoint(initialPoint, lastTimestamp);

                //Initiliase variables for trial
                int currentInertialIndex = 0;
                int orientation = Probabilistic.NO_ORIENTATION;
                double particleTotalDistance = 0.0;
                double probablisticTotalDistance = 0.0;

                //Setup trial logs to record point data within the trial.
                String particleTrialName = appSettings.getParticleTitle(OUT_SEP);
                String probabilisticTrialName = appSettings.getProbablisticTitle(OUT_SEP);

                //Trial Logging - split into different folders for when compass used or not
                File particleTrialDir;
                File probabilisticTrialDir;
                File partImageDir;
                File probImageDir;
                
                if(appSettings.isOrientationMerged()){
                    particleTrialDir = particleResultsDir;
                    probabilisticTrialDir = probabilisticResultsDir;
                    partImageDir = particleImageDir;
                    probImageDir = probabilisticImageDir;
                }else{
                    particleTrialDir = particleCompassResultsDir;
                    probabilisticTrialDir = probabilisticCompassResultsDir;
                    partImageDir = particleCompassImageDir;
                    probImageDir = probabilisticCompassImageDir;
                }
                
                Logging particleTrialLog = new Logging(new File(particleTrialDir, String.format("Trial %s.csv", particleTrialName)));
                Logging probabilisticTrialLog = new Logging(new File(probabilisticTrialDir, String.format("Trial %s.csv", probabilisticTrialName)));
                               
                particleTrialLog.printLine(trialHeader);
                probabilisticTrialLog.printLine(trialHeader);

                System.out.println("Running particle trial:" + particleTrialName);
                System.out.println("Running probabilistic trial:" + probabilisticTrialName);

                //Reset line numbering
                int lineNumber = 0;

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
                    String particleTrialResult = getTrialResult(lineNumber, knnTrialPoint, particleTrialDistance, bestPoint);
                    particleTrialLog.printLine(particleTrialResult);

                    String probabilisticTrialResult = getTrialResult(lineNumber, knnTrialPoint, probabilisticTrialDistance, probabilisticPoint);
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
                File particleOutputImageFile = new File(partImageDir, appSettings.getParticleImageTitle());
                DisplayRoute.draw(trialPoints, particleFinalPoints, particleOutputImageFile, image);

                File probabilisticOutputImageFile = new File(probImageDir, appSettings.getProbablisticImageTitle());
                DisplayRoute.draw(trialPoints, probabilisticFinalPoints, probabilisticOutputImageFile, image);
            }

            particleResultsLog.close();
            probabilisticResultsLog.close();
        }
    }

    private static boolean checkFiles(File offlineMap, File onlinePointsFile, File settingsFile, File initialPointsFile, File inertialDataFile, File image) {

        boolean isFileCheck = true;

        if (!offlineMap.isFile()) {
            System.out.println(String.format("%s not found", offlineMap.toString()));
            isFileCheck = false;
        }
        if (!onlinePointsFile.isFile()) {
            System.out.println(String.format("%s not found", onlinePointsFile.toString()));
            isFileCheck = false;
        }
        if (!settingsFile.isFile()) {
            System.out.println(String.format("%s not found", settingsFile.toString()));
            System.out.println(String.format("Switching to settings generation"));
            isGenerateSettings = true;
        }
        if (!initialPointsFile.isFile()) {
            System.out.println(String.format("%s not found", initialPointsFile.toString()));
            isFileCheck = false;
        }
        if (!inertialDataFile.isFile()) {
            System.out.println(String.format("%s not found", inertialDataFile.toString()));
            isFileCheck = false;
        }
        if (!image.isFile()) {
            System.out.println(String.format("%s not found", image.toString()));
            isFileCheck = false;
        }

        return isFileCheck;
    }

    private static List<AppSettings> loadSettings(File settingsFile) {

        List<AppSettings> appSettingsList = new ArrayList<>();
        //Load files
        if (isGenerateSettings) {
            System.out.println("Generating settings");
            appSettingsList = AppSettingsGenerator.generate();
        } else {

            int lineNumber = 0;

            try (BufferedReader reader = new BufferedReader(new FileReader(settingsFile))) {

                String header = reader.readLine();
                int headerSize = header.split(INPUT_SEPARATOR).length;

                String line;
                while ((line = reader.readLine()) != null) {

                    lineNumber++;
                    String[] columns = line.split(INPUT_SEPARATOR);

                    if (columns.length == headerSize) {

                        AppSettings appSettings
                                = new AppSettings(Boolean.parseBoolean(columns[0]), Boolean.parseBoolean(columns[1]),
                                        Integer.parseInt(columns[2]), Integer.parseInt(columns[3]),
                                        Integer.parseInt(columns[4]), Integer.parseInt(columns[5]),
                                        Double.parseDouble(columns[6]), Double.parseDouble(columns[7]),
                                        Boolean.parseBoolean(columns[8]), Double.parseDouble(columns[9]));

                        appSettingsList.add(appSettings);
                    } else {
                        System.out.println("Skipping line " + lineNumber);
                    }
                }
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }

            System.out.println("Settings loaded: " + lineNumber);
        }
        return appSettingsList;
    }

    private static List<Data> loadInertialData(File inertialDataFile) {

        int lineNumber = 0;
        List<Data> inertialDataList = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(inertialDataFile))) {

            String header = reader.readLine();
            int headerSize = header.split(INPUT_SEPARATOR).length;

            String line;
            while ((line = reader.readLine()) != null) {

                lineNumber++;
                String[] columns = line.split(INPUT_SEPARATOR);

                if (columns.length == headerSize) {

                    Data data
                            = new Data(TimeStamp.convertDateTime(columns[0]), Float.parseFloat(columns[1]),
                                    Float.parseFloat(columns[2]), Float.parseFloat(columns[3]),
                                    Float.parseFloat(columns[4]), Float.parseFloat(columns[5]),
                                    Float.parseFloat(columns[6]), Float.parseFloat(columns[7]),
                                    Float.parseFloat(columns[8]), Float.parseFloat(columns[9]),
                                    Float.parseFloat(columns[10]), Float.parseFloat(columns[11]),
                                    Float.parseFloat(columns[12]), Float.parseFloat(columns[13]),
                                    Float.parseFloat(columns[14]), Float.parseFloat(columns[15]),
                                    Float.parseFloat(columns[16]), Float.parseFloat(columns[17]),
                                    Float.parseFloat(columns[18]), Float.parseFloat(columns[19]),
                                    Float.parseFloat(columns[20]), Float.parseFloat(columns[21]),
                                    Float.parseFloat(columns[22]));

                    inertialDataList.add(data);
                } else {
                    System.out.println("Skipping line " + lineNumber);
                }
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }

        System.out.println("Data loaded: " + lineNumber);

        return inertialDataList;
    }

    private static double distance(KNNTrialPoint knnTrialPoint, Point point) {

        double x, y;

        x = point.getX() - knnTrialPoint.getFloorPoint().getxRef();
        y = point.getY() - knnTrialPoint.getFloorPoint().getyRef();

        return Math.hypot(x, y);
    }

    private static File checkDir(File parentDir, String directory) {

        File dir;
        if (parentDir != null) {
            dir = new File(parentDir, directory);
        } else {
            dir = new File(directory);
        }

        if (!dir.exists()) {
            dir.mkdir();
        }

        return dir;
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

    private static String getTrialResult(int lineNumber, KNNTrialPoint knnTrialPoint, double trialDistance, Point bestPoint) {

        int trialX = knnTrialPoint.getFloorPoint().getxRef();
        int trialY = knnTrialPoint.getFloorPoint().getyRef();
        double posX = bestPoint.getX();
        double posY = bestPoint.getY();

        return lineNumber + OUT_SEP + trialX + OUT_SEP + trialY + OUT_SEP + trialDistance + OUT_SEP + posX + OUT_SEP + posY;
    }

}
