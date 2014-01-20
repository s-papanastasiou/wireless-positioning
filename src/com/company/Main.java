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

    private final static String OFFLINE_MAP = "OfflineMap.csv";
    private final static String ONLINE_WIFI_DATA = "onlineWifiDataA.csv";
    private final static String SETTINGS_FILE = "settingsFileA.csv";
    private final static String INITIAL_POINTS = "initialPointsA.csv";
    private final static String INERTIAL_DATA = "inertialDataA.csv";
    private final static String IMAGE = "floor2final.png";
    private final static String IMAGE_DIRECTORY = "ImagesA";
    private final static String RESULTS_DIRECTORY = "ResultsA";
    private final static String SEPARATOR = ";";
    private static final double X_PIXELS = 1192.0 / 55;
    private static final double Y_PIXELS = 538.0 / 23.75;
    private static Cloud cloud;
    private static InertialPoint inertialPoint;

    public static void main(String[] args) {

        // Output directories //////////////////////////////////////////////////////////////////////////////////////////
        File imageDir = checkDir(IMAGE_DIRECTORY);
        File resultsDir = checkDir(RESULTS_DIRECTORY);

        // Loadings ////////////////////////////////////////////////////////////////////////////////////////////////////
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
            List<RSSIData> offlineDataList = RSSILoader.load(offlineFile, SEPARATOR);

            System.out.println("Loading online file");
            List<RSSIData> onlineDataList = RSSILoader.load(onlineWifiDataFile, SEPARATOR);

            System.out.println("Loading initial file");
            List<RSSIData> initialDataList = RSSILoader.load(initialPointsFile, SEPARATOR);

            System.out.println("Loading inertial file");
            List<Data> inertialDataList = loadInertialData(inertialDataFile);

            Logging log = new Logging(new File(resultsDir, "Results.csv"));
            String header = "isBSSIDMerged;isOrientationMerged;K;initReadings;partCount;CloudRange;CloudDisplacement;isForce;Mean";
            log.printLine(header);

            for (AppSettings appSettings : appSettingsList) {


                HashMap<String, KNNFloorPoint> offlineMap = KNNRSSI.compile(offlineDataList, appSettings.isBSSIDMerged(), appSettings.isOrientationMerged());
                List<KNNTrialPoint> onlinePoints = KNNRSSI.compileTrialList(onlineDataList, appSettings.isBSSIDMerged(), appSettings.isOrientationMerged());
                List<KNNTrialPoint> initialPoints = KNNRSSI.compileTrialList(initialDataList, appSettings.isBSSIDMerged(), appSettings.isOrientationMerged());

                List<Point> trialPoints = new ArrayList<>();
                List<Point> finalPoints = new ArrayList<>();

                Point initialPoint = initialPoint(initialPoints, appSettings.getInitRSSIReadings(), offlineMap, appSettings.getK(), Probabilistic.NO_ORIENTATION);
                long lastTimestamp = initialPoints.get(initialPoints.size() - 1).getTimestamp();
                inertialPoint = new InertialPoint(initialPoint, lastTimestamp);

                int currentInertialIndex = 0;
                int orientation = Probabilistic.NO_ORIENTATION;
                double mean = 0.0;

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

                    /*
                    while (inertialPoint.getTimestamp() < knnTrialPoint.getTimestamp())
                    {
                        data = inertialDataList.get(currentInertialIndex);
                        if(data.getTimestamp() < knnTrialPoint.getTimestamp()){

                            InertialData results = InertialData.getDatas(data.getInvertedMatrix(),
                                        data.getLinearAcceleration(), data.getOrientation(),
                                        appSettings.getBuildingOrientation());
                            inertialPoint = InertialPoint.move(inertialPoint, results, data.getTimestamp(), appSettings.getSpeedBreak());
                            orientation = InertialData.getOrientation(appSettings.isOrientationMerged(), data.getOrientation()[0], appSettings.getBuildingOrientation());
                            currentInertialIndex++;
                        }
                    }
                    */

                    Point probabilisticPoint = Probabilistic.run(knnTrialPoint.getFloorPoint(), offlineMap, appSettings.getK(), orientation);

                    if (cloud != null) {
                        cloud = ParticleFilter.filter(cloud, probabilisticPoint, inertialPoint, appSettings.getParticleCount(), appSettings.getCloudRange(), appSettings.getCloudDisplacementCoefficient());
                    } else {
                        List<Particle> particles = ParticleFilter.createParticles(initialPoint, appSettings.getParticleCount());
                        cloud = new Cloud(initialPoint, particles);
                    }

                    Point bestPoint = findBestPoint(offlineMap, cloud.getEstiPos(), appSettings.isForceToOfflineMap());

                    mean += distance(knnTrialPoint, bestPoint);

                    //store the points for drawing
                    finalPoints.add(new Point(bestPoint.getX() * X_PIXELS, bestPoint.getY() * Y_PIXELS));
                    trialPoints.add(new Point(knnTrialPoint.getFloorPoint().getxRef() * X_PIXELS,
                            knnTrialPoint.getFloorPoint().getyRef() * Y_PIXELS));

                }

                String results = String.format("%s;%s;%s;%s;%s;%s;%s;%s;%s", appSettings.isBSSIDMerged(),
                        appSettings.isOrientationMerged(), appSettings.getK(), appSettings.getInitRSSIReadings(),
                        appSettings.getParticleCount(), appSettings.getCloudRange(),
                        appSettings.getCloudDisplacementCoefficient(), appSettings.isForceToOfflineMap(), mean / onlinePoints.size());
                log.printLine(results);

                //display the image
                File outputImageFile = new File(imageDir, createTitle(appSettings, true));
                DisplayRoute.draw(trialPoints, finalPoints, outputImageFile, image);
            }

            log.close();
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
            isFileCheck = false;
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

        int lineNumber = 0;
        List<AppSettings> appSettingsList = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(settingsFile))) {

            String header = reader.readLine();
            int headerSize = header.split(SEPARATOR).length;

            String line;
            while ((line = reader.readLine()) != null) {

                lineNumber++;
                String[] columns = line.split(SEPARATOR);

                if (columns.length == headerSize) {

                    AppSettings appSettings =
                            new AppSettings(Boolean.parseBoolean(columns[0]), Boolean.parseBoolean(columns[1]),
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
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        System.out.println("Settings loaded: " + lineNumber);

        return appSettingsList;
    }

    private static List<Data> loadInertialData(File inertialDataFile) {

        int lineNumber = 0;
        List<Data> inertialDataList = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(inertialDataFile))) {

            String header = reader.readLine();
            int headerSize = header.split(SEPARATOR).length;

            String line;
            while ((line = reader.readLine()) != null) {

                lineNumber++;
                String[] columns = line.split(SEPARATOR);

                if (columns.length == headerSize) {

                    Data data =
                            new Data(TimeStamp.convertDateTime(columns[0]), Float.parseFloat(columns[1]),
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
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
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

    private static String createTitle(AppSettings appSettings, Boolean isImage) {

        String title;
        String base = String.format("-initR=%s-Part=%s-SpeedB=%s-CloudR=%s-CouldDis=%s",
                appSettings.getInitRSSIReadings(), appSettings.getParticleCount(), appSettings.getSpeedBreak(),
                appSettings.getCloudRange(), appSettings.getCloudDisplacementCoefficient());
        String force = "-force";

        if (appSettings.isForceToOfflineMap())
            base += force;

        if (isImage) {
            title = "image" + base;  //image file extension added by drawing function
        } else {
            title = "results" + base + ".csv";
        }

        return title;
    }

    private static File checkDir(String directory) {

        File dir = new File(directory);

        if (!dir.exists())
            dir.mkdir();

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
}
