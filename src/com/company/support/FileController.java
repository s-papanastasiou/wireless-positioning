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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Gerg
 */
public class FileController {

    private static final Logger logger = LoggerFactory.getLogger(FileController.class);
    
    private final static String RESULTS_DIRECTORY = "TrialResults";
    private final static String PARTICLE_IMAGE_DIRECTORY = "ParticleImages";
    private final static String PARTICLE_RESULTS_DIRECTORY = "ParticleResults";
    private final static String PROBABILISTIC_IMAGE_DIRECTORY = "ProbablisticImages";
    private final static String PROBABLISTIC_RESULTS_DIRECTORY = "ProbablisticResults";
    private final static String PARTICLE_COMPASS_IMAGE_DIRECTORY = "ParticleCompassImages";
    private final static String PARTICLE_COMPASS_RESULTS_DIRECTORY = "ParticleCompassResults";
    private final static String PROBABILISTIC_COMPASS_IMAGE_DIRECTORY = "ProbablisticCompassImages";
    private final static String PROBABILISTIC_COMPASS_RESULTS_DIRECTORY = "ProbablisticCompassResults";

    public File externalDir;
    public File offlineMapFile;
    public File onlinePointsFile;
    public File initialPointsFile;
    public File inertialDataFile;
    public File image;
    public File specificParticle;
    public File specificProb;

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

    public File particleTrialDir = null;
    public File probabilisticTrialDir = null;
    public File partImageDir = null;
    public File probImageDir = null;

    public final boolean isSetupOk;

    private final boolean isOutputImage;
    private final boolean isTrialDetail;

    public FileController(SettingsProperties sp) {
        this.isOutputImage = sp.isOutputImage();
        this.isTrialDetail = sp.isTrialDetail();
        setupDirectories(isOutputImage, isTrialDetail);

        setupExternalFiles(sp);

        if (checkFiles()) {
            setupLists(sp.IN_SEP());
            isSetupOk = true;
        } else {
            isSetupOk = false;
        }
    }

    private void setupDirectories(boolean isOutputImage, boolean isTrialDetail) {
        // Output directories //////////////////////////////////////////////////////////////////////////////////////////
        String workDirPath = System.getProperty("user.dir");
        File workDir = new File(workDirPath);
        resultsDir = DataLoad.checkDir(workDir, RESULTS_DIRECTORY);
        logger.info("Results directory: {}", resultsDir.getAbsolutePath());

        //Image directories
        if (isOutputImage) {
            particleImageDir = DataLoad.checkDir(resultsDir, PARTICLE_IMAGE_DIRECTORY);
            probabilisticImageDir = DataLoad.checkDir(resultsDir, PROBABILISTIC_IMAGE_DIRECTORY);
            particleCompassImageDir = DataLoad.checkDir(resultsDir, PARTICLE_COMPASS_IMAGE_DIRECTORY);
            probabilisticCompassImageDir = DataLoad.checkDir(resultsDir, PROBABILISTIC_COMPASS_IMAGE_DIRECTORY);

            //Actual output directories
            partImageDir = particleImageDir;
            probImageDir = probabilisticImageDir;
        }

        //Results directories
        if (isTrialDetail) {
            particleResultsDir = DataLoad.checkDir(resultsDir, PARTICLE_RESULTS_DIRECTORY);
            probabilisticResultsDir = DataLoad.checkDir(resultsDir, PROBABLISTIC_RESULTS_DIRECTORY);
            particleCompassResultsDir = DataLoad.checkDir(resultsDir, PARTICLE_COMPASS_RESULTS_DIRECTORY);
            probabilisticCompassResultsDir = DataLoad.checkDir(resultsDir, PROBABILISTIC_COMPASS_RESULTS_DIRECTORY);

            //Actual output directories
            particleTrialDir = particleResultsDir;
            probabilisticTrialDir = probabilisticResultsDir;

        }
    }

    private void setupExternalFiles(SettingsProperties sp) {
        // External files //////////////////////////////////////////////////////////////////////////////////////////////
        externalDir = new File(sp.EXTERNAL_DIRECTORY());
        offlineMapFile = new File(externalDir, sp.OFFLINE_MAP());
        onlinePointsFile = new File(externalDir, sp.ONLINE_WIFI_DATA());
        initialPointsFile = new File(externalDir, sp.INITIAL_POINTS());
        inertialDataFile = new File(externalDir, sp.INERTIAL_DATA());
        image = new File(externalDir, sp.FLOORPLAN_IMAGE());
        specificParticle = new File(externalDir, sp.SPECIFIC_PARTICLE());
        specificProb = new File(externalDir, sp.SPECIFIC_PROB());
    }

    private boolean checkFiles() {

        boolean isFileCheck = true;

        if (externalDir.isDirectory()) {

            if (!offlineMapFile.isFile()) {
                logger.info("{} not found", offlineMapFile.toString());
                isFileCheck = false;
            }
            if (!onlinePointsFile.isFile()) {
                logger.info("{} not found", onlinePointsFile.toString());
                isFileCheck = false;
            }
            if (!initialPointsFile.isFile()) {
                logger.info("{} not found", initialPointsFile.toString());
                isFileCheck = false;
            }
            if (!inertialDataFile.isFile()) {
                logger.info("{} not found", inertialDataFile.toString());
                isFileCheck = false;
            }
            if (!image.isFile()) {
                logger.info("{} not found", image.toString());
                isFileCheck = false;
            }
        } else {
            logger.info("{} not found", externalDir.toString());
            isFileCheck = false;
        }
        return isFileCheck;
    }

    private void setupLists(String IN_SEP) {

        logger.info("Loading offline file");
        offlineDataList = RSSILoader.load(offlineMapFile, IN_SEP);

        logger.info("Loading online file");
        onlineDataList = RSSILoader.load(onlinePointsFile, IN_SEP);

        logger.info("Loading initial file");
        initialDataList = RSSILoader.load(initialPointsFile, IN_SEP);

        logger.info("Loading inertial file");
        inertialDataList = DataLoad.loadInertialData(inertialDataFile, IN_SEP);
    }

    public void switchDirectories(boolean isOrientationMerged) {

        if (isOrientationMerged) {
            if (isTrialDetail) {
                particleTrialDir = particleResultsDir;
                probabilisticTrialDir = probabilisticResultsDir;
            }
            if (isOutputImage) {
                partImageDir = particleImageDir;
                probImageDir = probabilisticImageDir;
            }
        } else {
            if (isTrialDetail) {
                particleTrialDir = particleCompassResultsDir;
                probabilisticTrialDir = probabilisticCompassResultsDir;
            }
            if (isOutputImage) {
                partImageDir = particleCompassImageDir;
                probImageDir = probabilisticCompassImageDir;
            }
        }
    }

}
