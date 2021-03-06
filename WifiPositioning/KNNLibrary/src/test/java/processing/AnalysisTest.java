/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package processing;

import datastorage.KNNFloorPoint;
import datastorage.Location;
import datastorage.RSSIData;
import datastorage.TrialDefaults;
import filehandling.KNNGeomagnetic;
import filehandling.KNNRSSI;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import junit.framework.TestCase;

/**
 *
 * @author Gerg
 */
public class AnalysisTest extends TestCase {   

    private static final File outputPath = new File(TrialDefaults.workingPath, "KNNAnalysis");
    
    public AnalysisTest(String testName) {
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
     * Test of printNonUniques method, of class Analysis.
     */
    public void testPrintMergedRSSINonUniques_3args() {
        System.out.println("printRSSIMergedNonUniques");

        //Tolerance range
        Analysis instance = new Analysis();
        Boolean isBSSIDMerged = true;
        Double lowerBound = 0.0;
        Double upperBound = 10.0;
        Double step = 0.5;
        List<KNNFloorPoint> knnFloorList = KNNRSSI.compileList(TrialDefaults.rssiDataList, isBSSIDMerged);

        //Create sub folder
        File outputFolder = new File(outputPath, "Merged");
        outputFolder.mkdir();

        instance.printNonUniques(outputFolder, "RSSIMergedNonUniquesFingerprint", TrialDefaults.floorPlanFile, knnFloorList, TrialDefaults.roomInfo, TrialDefaults.dataSep, lowerBound, upperBound, step);

    }

    public void testPrintUnmergedRSSINonUniques() {
        System.out.println("printRSSIUnmergedNonUniques");

        //Tolerance range
        Analysis instance = new Analysis();
        Boolean isBSSIDMerged = false;
        Double rssiLowerBound = 0.0;
        Double rssiUpperBound = 10.0;
        Double rssiStep = 0.5;
        List<KNNFloorPoint> knnFloorList = KNNRSSI.compileList(TrialDefaults.rssiDataList, isBSSIDMerged);

        //Create sub folder
        File outputFolder = new File(outputPath, "Unmerged");
        outputFolder.mkdir();

        instance.printNonUniques(outputFolder, "RSSIUnmergedNonUniquesFingerprint", TrialDefaults.floorPlanFile, knnFloorList, TrialDefaults.roomInfo, TrialDefaults.dataSep, rssiLowerBound, rssiUpperBound, rssiStep);

    }

    public void testPrintGeomagneticNonUniques() {
        System.out.println("printGeomagneticNonUniques");

        //Tolerance range
        Analysis instance = new Analysis();
        Double geoLowerBound = 0.0;
        Double geoUpperBound = 1.01;
        Double geoStep = 0.01;
        List<KNNFloorPoint> knnFloorList = KNNGeomagnetic.compileList(TrialDefaults.geomagneticDataList);

        //Create sub folder
        File outputFolder = new File(outputPath, "Geomagnetic");
        outputFolder.mkdir();

        instance.printNonUniques(outputFolder, "GeomagneticNonUniquesFingerprint", TrialDefaults.floorPlanFile, knnFloorList, TrialDefaults.roomInfo, TrialDefaults.dataSep, 0.0, 0.0, 0.0, geoLowerBound, geoUpperBound, geoStep);

    }

    public void testPrintCombinedUnmergedNonUniques() {
        System.out.println("printCombinedUnmergedNonUniques");

        //Tolerance RSSI range
        Analysis instance = new Analysis();
        Boolean isBSSIDMerged = false;
        Double rssiLowerBound = 0.0;
        Double rssiUpperBound = 10.0;
        Double rssiStep = 0.5;
        HashMap<String, KNNFloorPoint> rssiKNNFloorMap = KNNRSSI.compile(TrialDefaults.rssiDataList, isBSSIDMerged);

        //Tolerance Geomagnetic range
        Double geoLowerBound = 0.0;
        Double geoUpperBound = 1.01;
        Double geoStep = 0.01;
        HashMap<String, KNNFloorPoint> geoKNNFloorMap = KNNGeomagnetic.compile(TrialDefaults.geomagneticDataList);

        //Combined data set
        HashMap<String, KNNFloorPoint> knnFloorMap = KNNFloorPoint.merge(rssiKNNFloorMap, geoKNNFloorMap);
        List<KNNFloorPoint> knnFloorList = new LinkedList<>(knnFloorMap.values());

        //Create sub folder
        File outputFolder = new File(outputPath, "Combined - Unmerged");
        outputFolder.mkdir();

        instance.printNonUniques(outputFolder, "CombinedUnmergedNonUniquesFingerprint", TrialDefaults.floorPlanFile, knnFloorList, TrialDefaults.roomInfo, TrialDefaults.dataSep, rssiLowerBound, rssiUpperBound, rssiStep, geoLowerBound, geoUpperBound, geoStep);

    }

    public void testPrintCombinedMergedNonUniques() {
        System.out.println("printCombinedMergedNonUniques");

        //Tolerance RSSI range
        Analysis instance = new Analysis();
        Boolean isBSSIDMerged = true;
        Double rssiLowerBound = 0.0;
        Double rssiUpperBound = 10.0;
        Double rssiStep = 0.5;
        HashMap<String, KNNFloorPoint> rssiKNNFloorMap = KNNRSSI.compile(TrialDefaults.rssiDataList, isBSSIDMerged);

        //Tolerance Geomagnetic range
        Double geoLowerBound = 0.0;
        Double geoUpperBound = 1.01;
        Double geoStep = 0.01;
        HashMap<String, KNNFloorPoint> geoKNNFloorMap = KNNGeomagnetic.compile(TrialDefaults.geomagneticDataList);

        //Combined data set
        HashMap<String, KNNFloorPoint> knnFloorMap = KNNFloorPoint.merge(rssiKNNFloorMap, geoKNNFloorMap);
        List<KNNFloorPoint> knnFloorList = new LinkedList<>(knnFloorMap.values());

        //Create sub folder
        File outputFolder = new File(outputPath, "Combined - Merged");
        outputFolder.mkdir();

        instance.printNonUniques(outputFolder, "CombinedMergedNonUniquesFingerprint", TrialDefaults.floorPlanFile, knnFloorList, TrialDefaults.roomInfo, TrialDefaults.dataSep, rssiLowerBound, rssiUpperBound, rssiStep, geoLowerBound, geoUpperBound, geoStep);

    }

    /**
     * Test of printNonUniques method, of class Analysis.
     */
    /*
     public void testPrintNonUniques_4args() {
     System.out.println("printNonUniques4args");        
        
     Double variance = 2.0;
        
     Analysis instance = new Analysis();
     Boolean isBSSIDMerged = true;
     instance.printNonUniques(outputPath, "NonUniquesVarianceFingerprint", floorPlanFile, rssiDataList, roomInfo, dataSep, isBSSIDMerged, variance);
       
     }
     */
    /**
     * Test of nonUniques method, of class Analysis.
     */
    /*
     public void testNonUniques_List() {
     System.out.println("nonUniquesList");
     List<RSSIData> rssiDataList = testData();
     Analysis instance = new Analysis();
     HashMap<KNNFloorPoint, List<KNNFloorPoint>> expResult = null;
     Boolean isBSSIDMerged = false;
     HashMap<KNNFloorPoint, List<KNNFloorPoint>> result = instance.nonUniques(rssiDataList, isBSSIDMerged);
     assertEquals(expResult, result);
     // TODO review the generated test code and remove the default call to fail.
     fail("The test case is a prototype.");
     }
     */
    /**
     * Test of nonUniques method, of class Analysis.
     */
    /*
     public void testNonUniques_List_Double() {
     System.out.println("nonUniquesListDouble");
     List<RSSIData> rssiDataList = testData();
     Double variance = 2.0;
     Analysis instance = new Analysis();
     Boolean isBSSIDMerged = false;
     HashMap<KNNFloorPoint, List<KNNFloorPoint>> expResult = null;
     HashMap<KNNFloorPoint, List<KNNFloorPoint>> result = instance.nonUniques(rssiDataList, isBSSIDMerged, variance);
     assertEquals(expResult, result);
     // TODO review the generated test code and remove the default call to fail.
     fail("The test case is a prototype.");
     }
     */
    private List<RSSIData> testData() {

        List<RSSIData> test = new ArrayList<>();
        test.add(new RSSIData(0, new Location("RoomA", 1, 1, 1, 0, 0, 0, 0), "xx:xx:xx:xx:xx:AA", "Ignore", -80, 1600));
        test.add(new RSSIData(0, new Location("RoomA", 1, 1, 1, 0, 0, 0, 0), "xx:xx:xx:xx:xx:BB", "Ignore", -80, 1600));
        test.add(new RSSIData(0, new Location("RoomA", 1, 1, 1, 0, 0, 0, 0), "xx:xx:xx:xx:xx:CC", "Ignore", -80, 1600));
        test.add(new RSSIData(0, new Location("RoomA", 1, 2, 1, 0, 0, 0, 0), "xx:xx:xx:xx:xx:AA", "Ignore", -80, 1600));
        test.add(new RSSIData(0, new Location("RoomA", 1, 2, 1, 0, 0, 0, 0), "xx:xx:xx:xx:xx:BB", "Ignore", -80, 1600));
        test.add(new RSSIData(0, new Location("RoomA", 1, 2, 1, 0, 0, 0, 0), "xx:xx:xx:xx:xx:CC", "Ignore", -80, 1600));
        test.add(new RSSIData(0, new Location("RoomA", 1, 3, 1, 0, 0, 0, 0), "xx:xx:xx:xx:xx:AA", "Ignore", -80, 1600));
        test.add(new RSSIData(0, new Location("RoomA", 1, 3, 1, 0, 0, 0, 0), "xx:xx:xx:xx:xx:BB", "Ignore", -80, 1600));
        test.add(new RSSIData(0, new Location("RoomA", 1, 3, 1, 0, 0, 0, 0), "xx:xx:xx:xx:xx:DD", "Ignore", -80, 1600));
        test.add(new RSSIData(0, new Location("RoomA", 1, 4, 1, 0, 0, 0, 0), "xx:xx:xx:xx:xx:AA", "Ignore", -70, 1600));
        test.add(new RSSIData(0, new Location("RoomA", 1, 4, 1, 0, 0, 0, 0), "xx:xx:xx:xx:xx:BB", "Ignore", -70, 1600));
        test.add(new RSSIData(0, new Location("RoomA", 1, 4, 1, 0, 0, 0, 0), "xx:xx:xx:xx:xx:CC", "Ignore", -70, 1600));
        return test;
    }

}
