/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.company.support;

import com.company.methods.Data;
import datastorage.RSSIData;
import filehandling.RSSILoader;
import java.io.File;
import java.util.List;

/**
 *
 * @author Gerg
 */
public class FileController {

    private final static String OFFLINE_MAP = "offlineMap.csv";
    private final static String ONLINE_WIFI_DATA = "onlineWifiDataA.csv";
    private final static String SETTINGS_FILE = "settingsFileA.csv";
    private final static String INITIAL_POINTS = "initialPointsA.csv";
    private final static String INERTIAL_DATA = "inertialDataA.csv";
    private final static String IMAGE = "floor2final.png";
    
    private final static String RESULTS_DIRECTORY = "C:\\TrialResults";
    private final static String PARTICLE_IMAGE_DIRECTORY = "ParticleImages";
    private final static String PARTICLE_RESULTS_DIRECTORY = "ParticleResults";
    private final static String PROBABILISTIC_IMAGE_DIRECTORY = "ProbablisticImages";
    private final static String PROBABLISTIC_RESULTS_DIRECTORY = "ProbablisticResults";
    private final static String PARTICLE_COMPASS_IMAGE_DIRECTORY = "ParticleCompassImages";
    private final static String PARTICLE_COMPASS_RESULTS_DIRECTORY = "ParticleCompassResults";
    private final static String PROBABILISTIC_COMPASS_IMAGE_DIRECTORY = "ProbablisticCompassImages";
    private final static String PROBABILISTIC_COMPASS_RESULTS_DIRECTORY = "ProbablisticCompassResults";

    public File offlineMapFile;
    public File onlinePointsFile;
    public File settingsFile;
    public File initialPointsFile;
    public File inertialDataFile;
    public File image;

    public File resultsDir;
    
    private File particleImageDir;
    private File particleResultsDir;
    private File probabilisticImageDir;
    private File probabilisticResultsDir;

    private File particleCompassImageDir;
    private File particleCompassResultsDir;
    private File probabilisticCompassImageDir;
    private File probabilisticCompassResultsDir;

    public List<RSSIData> offlineDataList;
    public List<RSSIData> onlineDataList;
    public List<RSSIData> initialDataList;
    public List<Data> inertialDataList;

    public File particleTrialDir;
    public File probabilisticTrialDir;
    public File partImageDir;
    public File probImageDir;

    public final boolean isSetupOk;

    public FileController(String IN_SEP) {
        setupDirectories();

        setupExternalFiles();

        if (checkFiles()) {
            setupLists(IN_SEP);
            isSetupOk = true;
        } else {
            isSetupOk = false;
        }
    }

    private void setupDirectories() {
        // Output directories //////////////////////////////////////////////////////////////////////////////////////////
        resultsDir = DataLoad.checkDir(null, RESULTS_DIRECTORY);
        
        //Non-compass method directories
        particleImageDir = DataLoad.checkDir(resultsDir, PARTICLE_IMAGE_DIRECTORY);
        particleResultsDir = DataLoad.checkDir(resultsDir, PARTICLE_RESULTS_DIRECTORY);
        probabilisticImageDir = DataLoad.checkDir(resultsDir, PROBABILISTIC_IMAGE_DIRECTORY);
        probabilisticResultsDir = DataLoad.checkDir(resultsDir, PROBABLISTIC_RESULTS_DIRECTORY);

        //Compass method directories
        particleCompassImageDir = DataLoad.checkDir(resultsDir, PARTICLE_COMPASS_IMAGE_DIRECTORY);
        particleCompassResultsDir = DataLoad.checkDir(resultsDir, PARTICLE_COMPASS_RESULTS_DIRECTORY);
        probabilisticCompassImageDir = DataLoad.checkDir(resultsDir, PROBABILISTIC_COMPASS_IMAGE_DIRECTORY);
        probabilisticCompassResultsDir = DataLoad.checkDir(resultsDir, PROBABILISTIC_COMPASS_RESULTS_DIRECTORY);
        
        //Actual output directories
        particleTrialDir = particleResultsDir;
        probabilisticTrialDir = probabilisticResultsDir;
        partImageDir = particleImageDir;
        probImageDir = probabilisticImageDir;
    }

    private void setupExternalFiles() {
        // External files //////////////////////////////////////////////////////////////////////////////////////////////
        offlineMapFile = new File(OFFLINE_MAP);
        onlinePointsFile = new File(ONLINE_WIFI_DATA);
        settingsFile = new File(SETTINGS_FILE);
        initialPointsFile = new File(INITIAL_POINTS);
        inertialDataFile = new File(INERTIAL_DATA);
        image = new File(IMAGE);
    }

    private boolean checkFiles() {

        boolean isFileCheck = true;

        if (!offlineMapFile.isFile()) {
            System.out.println(String.format("%s not found", offlineMapFile.toString()));
            isFileCheck = false;
        }
        if (!onlinePointsFile.isFile()) {
            System.out.println(String.format("%s not found", onlinePointsFile.toString()));
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

    private void setupLists(String IN_SEP) {

        System.out.println("Loading offline file");
        offlineDataList = RSSILoader.load(offlineMapFile, IN_SEP);

        System.out.println("Loading online file");
        onlineDataList = RSSILoader.load(onlinePointsFile, IN_SEP);

        System.out.println("Loading initial file");
        initialDataList = RSSILoader.load(initialPointsFile, IN_SEP);

        System.out.println("Loading inertial file");
        inertialDataList = DataLoad.loadInertialData(inertialDataFile, IN_SEP);
    }

    public void switchDirectories(boolean isOrientationMerged) {

        if (isOrientationMerged) {
            particleTrialDir = particleResultsDir;
            probabilisticTrialDir = probabilisticResultsDir;
            partImageDir = particleImageDir;
            probImageDir = probabilisticImageDir;
        } else {
            particleTrialDir = particleCompassResultsDir;
            probabilisticTrialDir = probabilisticCompassResultsDir;
            partImageDir = particleCompassImageDir;
            probImageDir = probabilisticCompassImageDir;
        }
    }

}
