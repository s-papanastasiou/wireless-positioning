/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package knnexecution;

import datastorage.GeomagneticData;
import datastorage.KNNFloorPoint;
import datastorage.KNNTrialPoint;
import datastorage.RSSIData;
import filehandling.GeomagneticLoader;
import filehandling.KNNGeomagnetic;
import filehandling.KNNRSSI;
import filehandling.RSSILoader;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import junit.framework.TestCase;
import static knnexecution.TrialDefaults.dataSep;
import static knnexecution.TrialDefaults.roomInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Unweighted centre trials
 * @author Gerg
 */
public class KNNTrial3 extends TestCase {

    private static final Logger logger = LoggerFactory.getLogger(KNNTrial3.class);

    private static final File outputPath = new File(TrialDefaults.workingPath, "Trial 3");

    private static final File pathTrials = new File(TrialDefaults.workingPath, "Path Trials");
    private static final String rssiPathFile = "TrialPathRSSI-";
    private static final String geoPathFile = "TrialPathGeo-";
    private static final String extension = ".csv";
    private static final String allSummaryExtension = "-AllSummary.csv";
    private static final String allResultsExtension = "-AllResults.csv";    

    private static final boolean isEstimateImages = false;
    private static final boolean isWeightedCentre = false;

    private static final Integer lowerKValue = 1;
    private static final Integer upperKValue = 10;

    private static final Integer lowerTrialPaths = 1;
    private static final Integer upperTrialPaths = 13;

    public KNNTrial3(String testName) {
        super(testName);
        outputPath.mkdir();
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }
    
    /**
     * Test of runTrials method, of class KNearestNeighbour.
     */
    
    public void testRSSIUnmerged() {
        String trialPrefix = "RSSI Unmerged";
        logger.info(trialPrefix);

        Boolean isBSSIDMerged = false;

        HashMap<String, KNNFloorPoint> offlineMap = KNNRSSI.compile(TrialDefaults.rssiDataList, isBSSIDMerged);
        File trialFolder = new File(outputPath, trialPrefix);
        trialFolder.mkdir();
        List<KNNTrialResults> allTrialResults = new ArrayList<>();

        for (int i = lowerTrialPaths; i <= upperTrialPaths; i++) {
            String rssiTrialName = rssiPathFile + i;
            logger.info(rssiTrialName);
            File rssiTrialFile = new File(pathTrials, rssiTrialName + extension);
            List<RSSIData> rssiTrialList = RSSILoader.load(rssiTrialFile, dataSep, roomInfo);
            List<KNNTrialPoint> rssiKNNTrialList = KNNRSSI.compileTrialList(rssiTrialList, isBSSIDMerged, Boolean.FALSE);

            File trialOutputPath = new File(trialFolder, rssiTrialName);
            trialOutputPath.mkdir();

            KNNTrialSettings trialSettings = new KNNTrialSettings(lowerKValue, upperKValue, isBSSIDMerged, isEstimateImages, isWeightedCentre);
            List<KNNTrialResults> trialResults = KNearestNeighbour.runTrials(trialSettings, trialOutputPath, offlineMap, rssiKNNTrialList, TrialDefaults.roomInfo, TrialDefaults.floorPlanFile, TrialDefaults.fieldSeparator, trialPrefix + "-" + i);
            allTrialResults.addAll(trialResults);

        }
        KNNTrialResults.printAllSummaryResults(allTrialResults, new File(trialFolder, trialPrefix + allSummaryExtension));
        KNNTrialResults.printAllResults(allTrialResults, new File(trialFolder, trialPrefix + allResultsExtension));
    }

    public void testRSSIMerged() {
        String trialPrefix = "RSSI Merged";
        logger.info(trialPrefix);

        Boolean isBSSIDMerged = true;

        HashMap<String, KNNFloorPoint> offlineMap = KNNRSSI.compile(TrialDefaults.rssiDataList, isBSSIDMerged);
        File trialFolder = new File(outputPath, trialPrefix);
        trialFolder.mkdir();
        List<KNNTrialResults> allTrialResults = new ArrayList<>();

        for (int i = lowerTrialPaths; i <= upperTrialPaths; i++) {
            String rssiTrialName = rssiPathFile + i;
            logger.info(rssiTrialName);
            File rssiTrialFile = new File(pathTrials, rssiTrialName + extension);
            List<RSSIData> rssiTrialList = RSSILoader.load(rssiTrialFile, dataSep, roomInfo);
            List<KNNTrialPoint> rssiKNNTrialList = KNNRSSI.compileTrialList(rssiTrialList, isBSSIDMerged, Boolean.FALSE);

            File trialOutputPath = new File(trialFolder, rssiTrialName);
            trialOutputPath.mkdir();

            KNNTrialSettings trialSettings = new KNNTrialSettings(lowerKValue, upperKValue, isBSSIDMerged, isEstimateImages, isWeightedCentre);
            List<KNNTrialResults> trialResults = KNearestNeighbour.runTrials(trialSettings, trialOutputPath, offlineMap, rssiKNNTrialList, TrialDefaults.roomInfo, TrialDefaults.floorPlanFile, TrialDefaults.fieldSeparator, trialPrefix + "-" + i);
            allTrialResults.addAll(trialResults);

        }
        KNNTrialResults.printAllSummaryResults(allTrialResults, new File(trialFolder, trialPrefix + allSummaryExtension));
        KNNTrialResults.printAllResults(allTrialResults, new File(trialFolder, trialPrefix + allResultsExtension));
    }

    public void testGeomagnetic() {
        String trialPrefix = "Geomagnetic";
        logger.info(trialPrefix);

        HashMap<String, KNNFloorPoint> offlineMap = KNNGeomagnetic.compile(TrialDefaults.geomagneticDataList);
        File trialFolder = new File(outputPath, trialPrefix);
        trialFolder.mkdir();
        List<KNNTrialResults> allTrialResults = new ArrayList<>();

        for (int i = lowerTrialPaths; i <= upperTrialPaths; i++) {
            String geoTrialName = geoPathFile + i;
            logger.info(geoTrialName);
            File geoTrialFile = new File(pathTrials, geoTrialName + extension);
            List<GeomagneticData> geoTrialList = GeomagneticLoader.load(geoTrialFile, dataSep, roomInfo);
            List<KNNTrialPoint> geoKNNTrialList = KNNGeomagnetic.compileTrialList(geoTrialList);

            File trialOutputPath = new File(trialFolder, geoTrialName);
            trialOutputPath.mkdir();

            KNNTrialSettings trialSettings = new KNNTrialSettings(lowerKValue, upperKValue, false, isEstimateImages, isWeightedCentre);
            List<KNNTrialResults> trialResults = KNearestNeighbour.runTrials(trialSettings, trialOutputPath, offlineMap, geoKNNTrialList, TrialDefaults.roomInfo, TrialDefaults.floorPlanFile, TrialDefaults.fieldSeparator, trialPrefix + "-" + i);
            allTrialResults.addAll(trialResults);                        

        }
        KNNTrialResults.printAllSummaryResults(allTrialResults, new File(trialFolder, trialPrefix + allSummaryExtension));
        KNNTrialResults.printAllResults(allTrialResults, new File(trialFolder, trialPrefix + allResultsExtension));
        
    }

    public void testCombinedMerged() {
        String trialPrefix = "Combined Merged";
        logger.info(trialPrefix);

        Boolean isBSSIDMerged = true;

        HashMap<String, KNNFloorPoint> geoOfflineMap = KNNGeomagnetic.compile(TrialDefaults.geomagneticDataList);
        HashMap<String, KNNFloorPoint> rssiOfflineMap = KNNRSSI.compile(TrialDefaults.rssiDataList, isBSSIDMerged);
        HashMap<String, KNNFloorPoint> offlineMap = KNNFloorPoint.merge(rssiOfflineMap, geoOfflineMap);

        File trialFolder = new File(outputPath, trialPrefix);
        trialFolder.mkdir();
        List<KNNTrialResults> allTrialResults = new ArrayList<>();

        for (int i = lowerTrialPaths; i <= upperTrialPaths; i++) {
            String rssiTrialName = rssiPathFile + i;
            logger.info(rssiTrialName);
            File rssiTrialFile = new File(pathTrials, rssiTrialName + extension);
            List<RSSIData> rssiTrialList = RSSILoader.load(rssiTrialFile, dataSep, roomInfo);
            List<KNNTrialPoint> rssiKNNTrialList = KNNRSSI.compileTrialList(rssiTrialList, isBSSIDMerged, Boolean.FALSE);

            String geoTrialName = geoPathFile + i;
            logger.info(geoTrialName);
            File geoTrialFile = new File(pathTrials, geoTrialName + extension);
            List<GeomagneticData> geoTrialList = GeomagneticLoader.load(geoTrialFile, dataSep, roomInfo);
            List<KNNTrialPoint> geoKNNTrialList = KNNGeomagnetic.compileTrialList(geoTrialList);

            List<KNNTrialPoint> knnTrialList = KNNTrialPoint.merge(rssiKNNTrialList, geoKNNTrialList);

            File trialOutputPath = new File(trialFolder, "CombinedMerged" + i);
            trialOutputPath.mkdir();

            KNNTrialSettings trialSettings = new KNNTrialSettings(lowerKValue, upperKValue, isBSSIDMerged, isEstimateImages, isWeightedCentre);
            List<KNNTrialResults> trialResults = KNearestNeighbour.runTrials(trialSettings, trialOutputPath, offlineMap, knnTrialList, TrialDefaults.roomInfo, TrialDefaults.floorPlanFile, TrialDefaults.fieldSeparator, trialPrefix + "-" + i);
            allTrialResults.addAll(trialResults);

        }
        KNNTrialResults.printAllSummaryResults(allTrialResults, new File(trialFolder, trialPrefix + allSummaryExtension));
        KNNTrialResults.printAllResults(allTrialResults, new File(trialFolder, trialPrefix + allResultsExtension));
    }

    public void testCombinedUnmerged() {
        String trialPrefix = "Combined Unmerged";
        logger.info(trialPrefix);

        Boolean isBSSIDMerged = false;

        HashMap<String, KNNFloorPoint> geoOfflineMap = KNNGeomagnetic.compile(TrialDefaults.geomagneticDataList);
        HashMap<String, KNNFloorPoint> rssiOfflineMap = KNNRSSI.compile(TrialDefaults.rssiDataList, isBSSIDMerged);
        HashMap<String, KNNFloorPoint> offlineMap = KNNFloorPoint.merge(rssiOfflineMap, geoOfflineMap);

        File trialFolder = new File(outputPath, trialPrefix);
        trialFolder.mkdir();
        List<KNNTrialResults> allTrialResults = new ArrayList<>();

        for (int i = lowerTrialPaths; i <= upperTrialPaths; i++) {
            String rssiTrialName = rssiPathFile + i;
            logger.info(rssiTrialName);
            File rssiTrialFile = new File(pathTrials, rssiTrialName + extension);
            List<RSSIData> rssiTrialList = RSSILoader.load(rssiTrialFile, dataSep, roomInfo);
            List<KNNTrialPoint> rssiKNNTrialList = KNNRSSI.compileTrialList(rssiTrialList, isBSSIDMerged, Boolean.FALSE);

            String geoTrialName = geoPathFile + i;
            logger.info(geoTrialName);
            File geoTrialFile = new File(pathTrials, geoTrialName + extension);
            List<GeomagneticData> geoTrialList = GeomagneticLoader.load(geoTrialFile, dataSep, roomInfo);
            List<KNNTrialPoint> geoKNNTrialList = KNNGeomagnetic.compileTrialList(geoTrialList);

            List<KNNTrialPoint> knnTrialList = KNNTrialPoint.merge(rssiKNNTrialList, geoKNNTrialList);

            File trialOutputPath = new File(trialFolder, "CombinedUnmerged" + i);
            trialOutputPath.mkdir();

            KNNTrialSettings trialSettings = new KNNTrialSettings(lowerKValue, upperKValue, isBSSIDMerged, isEstimateImages, isWeightedCentre);
            List<KNNTrialResults> trialResults = KNearestNeighbour.runTrials(trialSettings, trialOutputPath, offlineMap, knnTrialList, TrialDefaults.roomInfo, TrialDefaults.floorPlanFile, TrialDefaults.fieldSeparator, trialPrefix + "-" + i);
            allTrialResults.addAll(trialResults);

        }
        KNNTrialResults.printAllSummaryResults(allTrialResults, new File(trialFolder, trialPrefix + allSummaryExtension));
        KNNTrialResults.printAllResults(allTrialResults, new File(trialFolder, trialPrefix + allResultsExtension));
    }


}
