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
import filehandling.FilterBSSID;
import filehandling.GeomagneticLoader;
import filehandling.KNNGeomagnetic;
import filehandling.KNNRSSI;
import filehandling.RSSILoader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import junit.framework.TestCase;
import static knnexecution.TrialDefaults.dataSep;
import static knnexecution.TrialDefaults.roomInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Gerg
 */
public class KNNTrial2 extends TestCase {

    private static final Logger logger = LoggerFactory.getLogger(KNNTrial2.class);

    private static final File outputPath = new File(TrialDefaults.workingPath, "Trial 2");

    private static final File pathTrials = new File(TrialDefaults.workingPath, "Path Trials");
    private static final String rssiPathFile = "TrialPathRSSI-";
    private static final String geoPathFile = "TrialPathGeo-";
    private static final String extension = ".csv";

    private static final Integer kValue = 5;

    private static final Integer lowerTrialPaths = 1;
    private static final Integer upperTrialPaths = 13;
    
    private static final boolean isEstimateImages = false;

    private static final double maxSize = 780;
    private static final Double[] locationPercents = {0.0, 0.1, 0.2, 0.3, 0.4};
    private static final List<List<String>> mergedBSSIDFilters = new ArrayList<>();
    private static final List<List<String>> unmergedBSSIDFilters = new ArrayList<>();

    public KNNTrial2(String testName) {
        super(testName);
        outputPath.mkdir();
        
        buildFilters();
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    private void buildFilters(){
        File filterFolder = new File(outputPath, "Filters");
        filterFolder.mkdir();
        for (Double percent : locationPercents) {
            
            Double c = maxSize*percent;
            List<String> unmergedFilter = FilterBSSID.generateByLocationCount(TrialDefaults.rssiDataList, c.intValue(), Boolean.FALSE);
            unmergedBSSIDFilters.add(unmergedFilter);
            File unmerged = new File(filterFolder, "Unmerged Filter - " + percent*100 +"%.txt");
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(unmerged, false))) {
                for(String filter: unmergedFilter){
                    writer.write(filter);
                    writer.write(System.lineSeparator());
                }
            } catch (IOException ex) {
                logger.info("{}", ex.getMessage());
            }

            List<String> mergedFilter = FilterBSSID.generateByLocationCount(TrialDefaults.rssiDataList, c.intValue(), Boolean.TRUE);
            mergedBSSIDFilters.add(mergedFilter);
            File merged = new File(filterFolder, "Merged Filter - " + percent*100 +"%.txt");
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(merged, false))) {
                for(String filter: mergedFilter){
                    writer.write(filter);
                    writer.write(System.lineSeparator());
                }
            } catch (IOException ex) {
                logger.info("{}", ex.getMessage());
            }
        }
    }
    
    /**
     * Test of runTrials method, of class KNearestNeighbour.
     */
    public void testRSSIUnmerged() {
        String trialPrefix = "Combined Unmerged";
        logger.info(trialPrefix);

        Boolean isBSSIDMerged = false;

        File trialFolder = new File(outputPath, trialPrefix);
        trialFolder.mkdir();

        for (int f = 0; f < locationPercents.length; f++) {
            List<String> filter = unmergedBSSIDFilters.get(f);
            HashMap<String, KNNFloorPoint> offlineMap = KNNRSSI.compile(TrialDefaults.rssiDataList, filter, isBSSIDMerged);

            //Need different folder
            double percent = locationPercents[f] * 100;
            File trialFilterFolder = new File(trialFolder, "Filter-" + percent + "%");
            trialFilterFolder.mkdir();
            
            for (int i = lowerTrialPaths; i <= upperTrialPaths; i++) {

                String rssiTrialName = rssiPathFile + i;
                logger.info(rssiTrialName);
                File rssiTrialFile = new File(pathTrials, rssiTrialName + extension);
                List<RSSIData> rssiTrialList = RSSILoader.load(rssiTrialFile, dataSep, roomInfo);

                List<KNNTrialPoint> rssiKNNTrialList = KNNRSSI.compileTrialList(rssiTrialList, isBSSIDMerged, Boolean.FALSE);

                File trialOutputPath = new File(trialFilterFolder, rssiTrialName);
                trialOutputPath.mkdir();

                KNNTrialSettings trialSettings = new KNNTrialSettings(kValue, isBSSIDMerged, isEstimateImages);
                KNearestNeighbour.runTrials(trialSettings, trialOutputPath, offlineMap, rssiKNNTrialList, TrialDefaults.roomInfo, TrialDefaults.floorPlanFile, TrialDefaults.fieldSeparator, trialPrefix + "-" + i + "-" + f);
            }
        }
    }

    public void testRSSIMerged() {
        String trialPrefix = "RSSI Merged";
        logger.info(trialPrefix);

        Boolean isBSSIDMerged = true;

        File trialFolder = new File(outputPath, trialPrefix);
        trialFolder.mkdir();

        for (int f = 0; f < locationPercents.length; f++) {
            List<String> filter = mergedBSSIDFilters.get(f);
            HashMap<String, KNNFloorPoint> offlineMap = KNNRSSI.compile(TrialDefaults.rssiDataList, filter, isBSSIDMerged);

            //Need different folder
            double percent = locationPercents[f] * 100;
            File trialFilterFolder = new File(trialFolder, "Filter-" + percent + "%");
            trialFilterFolder.mkdir();
            
            for (int i = lowerTrialPaths; i <= upperTrialPaths; i++) {
                String rssiTrialName = rssiPathFile + i;
                logger.info(rssiTrialName);
                File rssiTrialFile = new File(pathTrials, rssiTrialName + extension);
                List<RSSIData> rssiTrialList = RSSILoader.load(rssiTrialFile, dataSep, roomInfo);
                List<KNNTrialPoint> rssiKNNTrialList = KNNRSSI.compileTrialList(rssiTrialList, isBSSIDMerged, Boolean.FALSE);

                File trialOutputPath = new File(trialFilterFolder, rssiTrialName);
                trialOutputPath.mkdir();

                KNNTrialSettings trialSettings = new KNNTrialSettings(kValue, isBSSIDMerged, isEstimateImages);
                KNearestNeighbour.runTrials(trialSettings, trialOutputPath, offlineMap, rssiKNNTrialList, TrialDefaults.roomInfo, TrialDefaults.floorPlanFile, TrialDefaults.fieldSeparator, trialPrefix + "-" + i + "-" + f);
            }
        }
    }

    public void testCombinedMerged() {
        String trialPrefix = "Combined Merged";
        logger.info(trialPrefix);

        Boolean isBSSIDMerged = true;

        HashMap<String, KNNFloorPoint> geoOfflineMap = KNNGeomagnetic.compile(TrialDefaults.geomagneticDataList);

        File trialFolder = new File(outputPath, trialPrefix);
        trialFolder.mkdir();

        for (int f = 0; f < locationPercents.length; f++) {
            List<String> filter = mergedBSSIDFilters.get(f);
            HashMap<String, KNNFloorPoint> rssiOfflineMap = KNNRSSI.compile(TrialDefaults.rssiDataList, filter, isBSSIDMerged);
            HashMap<String, KNNFloorPoint> offlineMap = KNNFloorPoint.merge(rssiOfflineMap, geoOfflineMap);

            //Need different folder
            double percent = locationPercents[f] * 100;
            File trialFilterFolder = new File(trialFolder, "Filter-" + percent + "%");
            trialFilterFolder.mkdir();
            
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

                File trialOutputPath = new File(trialFilterFolder, "CombinedMerged" + i);
                trialOutputPath.mkdir();

                KNNTrialSettings trialSettings = new KNNTrialSettings(kValue, isBSSIDMerged, isEstimateImages);
                KNearestNeighbour.runTrials(trialSettings, trialOutputPath, offlineMap, knnTrialList, TrialDefaults.roomInfo, TrialDefaults.floorPlanFile, TrialDefaults.fieldSeparator, trialPrefix + "-" + i + "-" + f);
            }
        }
    }

    public void testCombinedUnmerged() {
        String trialPrefix = "Combined Unmerged";
        logger.info(trialPrefix);

        Boolean isBSSIDMerged = false;

        HashMap<String, KNNFloorPoint> geoOfflineMap = KNNGeomagnetic.compile(TrialDefaults.geomagneticDataList);

        File trialFolder = new File(outputPath, trialPrefix);
        trialFolder.mkdir();

        for (int f = 0; f < locationPercents.length; f++) {
            List<String> filter = unmergedBSSIDFilters.get(f);
            HashMap<String, KNNFloorPoint> rssiOfflineMap = KNNRSSI.compile(TrialDefaults.rssiDataList, filter, isBSSIDMerged);
            HashMap<String, KNNFloorPoint> offlineMap = KNNFloorPoint.merge(rssiOfflineMap, geoOfflineMap);

            //Need different folder
            double percent = locationPercents[f] * 100;
            File trialFilterFolder = new File(trialFolder, "Filter-" + percent + "%");

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

                File trialOutputPath = new File(trialFilterFolder, "CombinedUnmerged" + i);
                trialOutputPath.mkdir();

                KNNTrialSettings trialSettings = new KNNTrialSettings(kValue, isBSSIDMerged, isEstimateImages);
                KNearestNeighbour.runTrials(trialSettings, trialOutputPath, offlineMap, knnTrialList, TrialDefaults.roomInfo, TrialDefaults.floorPlanFile, TrialDefaults.fieldSeparator, trialPrefix + "-" + i + "-" + f);
            }

        }
    }

}
