/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package processing;

import datastorage.GeomagneticData;
import datastorage.KNNFloorPoint;
import datastorage.Location;
import datastorage.RSSIData;
import datastorage.RoomInfo;
import filehandling.GeomagneticLoader;
import filehandling.KNNGeomagnetic;
import filehandling.KNNRSSI;
import filehandling.RSSILoader;
import filehandling.RoomInfoLoader;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import junit.framework.TestCase;

/**
 *
 * @author Gerg
 */
public class AnalysisTest extends TestCase {
    
    private static final String workingDirectory = "C:\\WirelessPositioningTestFiles";
    private static final File workingPath = new File(workingDirectory);
    public static final String floorPlanFilename = "floor2.png";
    public static final File floorPlanFile = new File(workingPath, floorPlanFilename);
    private static final String roomInfoFilename = "RoomInfo.csv";
    private static final File roomInfoFile = new File(workingPath, roomInfoFilename);
    private static final String roomInfoSep = ",";
    private static final HashMap<String, RoomInfo> roomInfo = RoomInfoLoader.load(roomInfoFile, roomInfoSep);
    private static final String rssiData = "RSSISurveyData.csv";
    private static final File dataFile = new File(workingPath, rssiData);
    private static final String dataSep = ",";
    private static final File outputPath = new File(workingPath, "KNNAnalysis");
    
    
    private static final List<RSSIData> rssiDataList = RSSILoader.load(dataFile, dataSep, roomInfo);
    
     public static final String geomagneticData = "GeomagneticSurveyData.csv";
    public static final File geomagneticDataFile = new File(workingPath, geomagneticData);
    public static final List<GeomagneticData> geomagneticDataList = GeomagneticLoader.load(geomagneticDataFile, dataSep, roomInfo);
    
    
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
                
        Analysis instance = new Analysis();
        Boolean isBSSIDMerged = true;
        Double lowerBound = 0.0;
        Double upperBound = 10.0;
        Double step = 0.5;
        List<KNNFloorPoint> knnFloorList = KNNRSSI.compileList(rssiDataList, isBSSIDMerged);
        instance.printNonUniques(outputPath, "RSSIMergedNonUniquesFingerprint", floorPlanFile, knnFloorList, roomInfo, dataSep, lowerBound, upperBound, step);
        
    }
    
    public void testPrintUnmergedRSSINonUniques() {
        System.out.println("printRSSIUnmergedNonUniques");        
                
        Analysis instance = new Analysis();
        Boolean isBSSIDMerged = false;
        Double lowerBound = 0.0;
        Double upperBound = 10.0;
        Double step = 0.5;
        List<KNNFloorPoint> knnFloorList = KNNRSSI.compileList(rssiDataList, isBSSIDMerged);
        instance.printNonUniques(outputPath, "RSSIUnmergedNonUniquesFingerprint", floorPlanFile, knnFloorList, roomInfo, dataSep, lowerBound, upperBound, step);
        
    }
    
    public void testPrintGeomagneticNonUniques() {
        System.out.println("printGeomagneticNonUniques");        
                
        Analysis instance = new Analysis();        
        Double lowerBound = 0.0;
        Double upperBound = 10.0;
        Double step = 0.5;
        List<KNNFloorPoint> knnFloorList = KNNGeomagnetic.compileList(geomagneticDataList);
        instance.printNonUniques(outputPath, "GeomagneticNonUniquesFingerprint", floorPlanFile, knnFloorList, roomInfo, dataSep, lowerBound, upperBound, step);
        
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
    private List<RSSIData> testData(){
        
        List<RSSIData> test = new ArrayList<>();
        test.add(new RSSIData(0, new Location("RoomA", 1,1,1,0,0,0,0), "xx:xx:xx:xx:xx:AA", "Ignore", -80, 1600));
        test.add(new RSSIData(0, new Location("RoomA", 1,1,1,0,0,0,0), "xx:xx:xx:xx:xx:BB", "Ignore", -80, 1600));
        test.add(new RSSIData(0, new Location("RoomA", 1,1,1,0,0,0,0), "xx:xx:xx:xx:xx:CC", "Ignore", -80, 1600));
        test.add(new RSSIData(0, new Location("RoomA", 1,2,1,0,0,0,0), "xx:xx:xx:xx:xx:AA", "Ignore", -80, 1600));
        test.add(new RSSIData(0, new Location("RoomA", 1,2,1,0,0,0,0), "xx:xx:xx:xx:xx:BB", "Ignore", -80, 1600));
        test.add(new RSSIData(0, new Location("RoomA", 1,2,1,0,0,0,0), "xx:xx:xx:xx:xx:CC", "Ignore", -80, 1600));
        test.add(new RSSIData(0, new Location("RoomA", 1,3,1,0,0,0,0), "xx:xx:xx:xx:xx:AA", "Ignore", -80, 1600));
        test.add(new RSSIData(0, new Location("RoomA", 1,3,1,0,0,0,0), "xx:xx:xx:xx:xx:BB", "Ignore", -80, 1600));
        test.add(new RSSIData(0, new Location("RoomA", 1,3,1,0,0,0,0), "xx:xx:xx:xx:xx:DD", "Ignore", -80, 1600));
        test.add(new RSSIData(0, new Location("RoomA", 1,4,1,0,0,0,0), "xx:xx:xx:xx:xx:AA", "Ignore", -70, 1600));
        test.add(new RSSIData(0, new Location("RoomA", 1,4,1,0,0,0,0), "xx:xx:xx:xx:xx:BB", "Ignore", -70, 1600));
        test.add(new RSSIData(0, new Location("RoomA", 1,4,1,0,0,0,0), "xx:xx:xx:xx:xx:CC", "Ignore", -70, 1600));
        return test;
    }
    
}
