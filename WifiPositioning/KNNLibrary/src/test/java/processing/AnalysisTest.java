/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package processing;

import datastorage.KNNFloorPoint;
import datastorage.Location;
import datastorage.RSSIData;
import datastorage.RoomInfo;
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
    private static final String roomInfoFilename = "RoomInfo.csv";
    private static final File roomInfoFile = new File(workingPath, roomInfoFilename);
    private static final String roomInfoSep = ",";
    private static final HashMap<String, RoomInfo> roomInfo = RoomInfoLoader.load(roomInfoFile, roomInfoSep);
    private static final String rssiData = "RSSISurveyData.csv";
    private static final File dataFile = new File(workingPath, rssiData);
    private static final String dataSep = ",";
    private static final File outputPath = new File(workingPath, "KNNAnalysis");
    
    private static final List<RSSIData> rssiDataList = RSSILoader.load(dataFile, dataSep, roomInfo);
    
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
    public void testPrintNonUniques_3args() {
        System.out.println("printNonUniques3args");
        File outputFile = new File(outputPath, "NonUniques.csv");
                
        Analysis instance = new Analysis();
        instance.printNonUniques(outputFile, rssiDataList, dataSep);
        
    }

    /**
     * Test of printNonUniques method, of class Analysis.
     */
    public void testPrintNonUniques_4args() {
        System.out.println("printNonUniques4args");
        File outputFile = new File(outputPath, "NonUniques2.csv");
        
        Double variance = 2.0;
        
        Analysis instance = new Analysis();
        instance.printNonUniques(outputFile, rssiDataList, variance, dataSep);
       
    }

    /**
     * Test of nonUniques method, of class Analysis.
     */
    public void testNonUniques_List() {
        System.out.println("nonUniques");
        List<RSSIData> rssiDataList = testData();
        Analysis instance = new Analysis();
        HashMap<KNNFloorPoint, List<KNNFloorPoint>> expResult = null;
        HashMap<KNNFloorPoint, List<KNNFloorPoint>> result = instance.nonUniques(rssiDataList);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of nonUniques method, of class Analysis.
     */
    public void testNonUniques_List_Double() {
        System.out.println("nonUniques");
        List<RSSIData> rssiDataList = testData();
        Double variance = 2.0;
        Analysis instance = new Analysis();
        HashMap<KNNFloorPoint, List<KNNFloorPoint>> expResult = null;
        HashMap<KNNFloorPoint, List<KNNFloorPoint>> result = instance.nonUniques(rssiDataList, variance);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
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
