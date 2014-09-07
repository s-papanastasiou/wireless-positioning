/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package knnexecution;

import datastorage.KNNFloorPoint;
import datastorage.KNNTrialPoint;
import datastorage.RSSIData;
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
            String trialName = rssiPathFile + i;
            logger.info(trialName);
            File rssiTrialFile = new File(pathTrials, trialName + extension);
            List<RSSIData> rssiTrialList = RSSILoader.load(rssiTrialFile, dataSep, roomInfo);
            List<KNNTrialPoint> rssiKNNTrialList = KNNRSSI.compileTrialList(rssiTrialList, isBSSIDMerged, Boolean.FALSE);
            
            File trialOutputPath = new File(trialFolder, trialName);
            trialOutputPath.mkdir();
            
            KNNTrialSettings trialSettings = new KNNTrialSettings(lowerKValue, upperKValue, isBSSIDMerged);        
            KNearestNeighbour.runTrials(trialSettings, trialOutputPath, offlineMap, rssiKNNTrialList, TrialDefaults.roomInfo, TrialDefaults.floorPlanFile, TrialDefaults.fieldSeparator);
        }
    }
   
}
