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
public class KNNTrial1 extends TestCase {
    
    private static final Logger logger = LoggerFactory.getLogger(KNNTrial1.class);
    
    private static final File outputPath = new File(TrialDefaults.workingPath, "Trial 1");        
    
    private static final File pathTrials = new File(TrialDefaults.workingPath, "Path Trials");
    private static final String rssiPathFile = "TrialPathRSSI-";
    private static final String geoPathFile = "TrialPathGeo-";
    private static final String extension = ".csv";
    
    private static final Integer lowerKValue = 1;
    private static final Integer upperKValue = 10;
    
    private static final Integer lowerTrialPaths = 1;
    private static final Integer upperTrialPaths = 13;
    
    public KNNTrial1(String testName) {
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
        logger.info("RSSI Unmerged");
        
        Boolean isBSSIDMerged = false;
        
        HashMap<String, KNNFloorPoint> offlineMap = KNNRSSI.compile(TrialDefaults.rssiDataList, isBSSIDMerged);
        File trialFolder = new File(outputPath, "Unmerged");
        trialFolder.mkdir();
        for(int i=lowerTrialPaths; i<=upperTrialPaths; i++){
            String rssiTrialName = rssiPathFile + i;
            logger.info(rssiTrialName);
            File rssiTrialFile = new File(pathTrials, rssiTrialName + extension);
            List<RSSIData> rssiTrialList = RSSILoader.load(rssiTrialFile, dataSep, roomInfo);
            List<KNNTrialPoint> rssiKNNTrialList = KNNRSSI.compileTrialList(rssiTrialList, isBSSIDMerged, Boolean.FALSE);
            
            File trialOutputPath = new File(trialFolder, rssiTrialName);
            trialOutputPath.mkdir();
            
            KNNTrialSettings trialSettings = new KNNTrialSettings(lowerKValue, upperKValue, isBSSIDMerged);        
            KNearestNeighbour.runTrials(trialSettings, trialOutputPath, offlineMap, rssiKNNTrialList, TrialDefaults.roomInfo, TrialDefaults.floorPlanFile, TrialDefaults.fieldSeparator);
        }
    }
    
    public void testRSSIMerged() {
        logger.info("RSSI Merged");
        
        Boolean isBSSIDMerged = true;
        
        HashMap<String, KNNFloorPoint> offlineMap = KNNRSSI.compile(TrialDefaults.rssiDataList, isBSSIDMerged);
        File trialFolder = new File(outputPath, "Merged");
        trialFolder.mkdir();
        for(int i=lowerTrialPaths; i<=upperTrialPaths; i++){
            String rssiTrialName = rssiPathFile + i;
            logger.info(rssiTrialName);
            File rssiTrialFile = new File(pathTrials, rssiTrialName + extension);
            List<RSSIData> rssiTrialList = RSSILoader.load(rssiTrialFile, dataSep, roomInfo);
            List<KNNTrialPoint> rssiKNNTrialList = KNNRSSI.compileTrialList(rssiTrialList, isBSSIDMerged, Boolean.FALSE);
            
            File trialOutputPath = new File(trialFolder, rssiTrialName);
            trialOutputPath.mkdir();
            
            KNNTrialSettings trialSettings = new KNNTrialSettings(lowerKValue, upperKValue, isBSSIDMerged);        
            KNearestNeighbour.runTrials(trialSettings, trialOutputPath, offlineMap, rssiKNNTrialList, TrialDefaults.roomInfo, TrialDefaults.floorPlanFile, TrialDefaults.fieldSeparator);
        }
    }
    
    public void testGeomagnetic() {
        logger.info("Geomagnetic");
                
        
        HashMap<String, KNNFloorPoint> offlineMap = KNNGeomagnetic.compile(TrialDefaults.geomagneticDataList);
        File trialFolder = new File(outputPath, "Geomagnetic");
        trialFolder.mkdir();
        for(int i=lowerTrialPaths; i<=upperTrialPaths; i++){
            String geoTrialName = geoPathFile + i;
            logger.info(geoTrialName);
            File geoTrialFile = new File(pathTrials, geoTrialName + extension);
            List<GeomagneticData> geoTrialList = GeomagneticLoader.load(geoTrialFile, dataSep, roomInfo);
            List<KNNTrialPoint> geoKNNTrialList = KNNGeomagnetic.compileTrialList(geoTrialList);
            
            File trialOutputPath = new File(trialFolder, geoTrialName);
            trialOutputPath.mkdir();
            
            KNNTrialSettings trialSettings = new KNNTrialSettings(lowerKValue, upperKValue);        
            KNearestNeighbour.runTrials(trialSettings, trialOutputPath, offlineMap, geoKNNTrialList, TrialDefaults.roomInfo, TrialDefaults.floorPlanFile, TrialDefaults.fieldSeparator);
        }
    }
    
    public void testCombinedMerged() {
        logger.info("CombinedMerged");
                
        Boolean isBSSIDMerged = true;
        
        HashMap<String, KNNFloorPoint> geoOfflineMap = KNNGeomagnetic.compile(TrialDefaults.geomagneticDataList);
        HashMap<String, KNNFloorPoint> rssiOfflineMap = KNNRSSI.compile(TrialDefaults.rssiDataList, isBSSIDMerged);
        HashMap<String, KNNFloorPoint> offlineMap = KNNFloorPoint.merge(rssiOfflineMap, geoOfflineMap);
        
        File trialFolder = new File(outputPath, "CombinedMerged");
        trialFolder.mkdir();
        for(int i=lowerTrialPaths; i<=upperTrialPaths; i++){
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
            
            KNNTrialSettings trialSettings = new KNNTrialSettings(lowerKValue, upperKValue, isBSSIDMerged);        
            KNearestNeighbour.runTrials(trialSettings, trialOutputPath, offlineMap, knnTrialList, TrialDefaults.roomInfo, TrialDefaults.floorPlanFile, TrialDefaults.fieldSeparator);
        }
                
    }
    
    public void testCombinedUnmerged() {
        logger.info("CombinedUnmerged");
                
        Boolean isBSSIDMerged = false;
        
        HashMap<String, KNNFloorPoint> geoOfflineMap = KNNGeomagnetic.compile(TrialDefaults.geomagneticDataList);
        HashMap<String, KNNFloorPoint> rssiOfflineMap = KNNRSSI.compile(TrialDefaults.rssiDataList, isBSSIDMerged);
        HashMap<String, KNNFloorPoint> offlineMap = KNNFloorPoint.merge(rssiOfflineMap, geoOfflineMap);
        
        File trialFolder = new File(outputPath, "CombinedUnmerged");
        trialFolder.mkdir();
        for(int i=lowerTrialPaths; i<=upperTrialPaths; i++){
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
            
            KNNTrialSettings trialSettings = new KNNTrialSettings(lowerKValue, upperKValue, isBSSIDMerged);        
            KNearestNeighbour.runTrials(trialSettings, trialOutputPath, offlineMap, knnTrialList, TrialDefaults.roomInfo, TrialDefaults.floorPlanFile, TrialDefaults.fieldSeparator);
        }
                
    }
   
}
