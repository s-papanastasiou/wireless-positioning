/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package accesspointvariant;

import datastorage.RSSIData;
import datastorage.RoomInfo;
import filehandling.RSSILoader;
import filehandling.RoomInfoLoader;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import junit.framework.TestCase;

/**
 *
 * @author N0472738
 */
public class APFormatTest extends TestCase {
   
    private static final String workingDirectory = "C:\\WirelessPositioningTestFiles";
    private static final File workingPath = new File(workingDirectory);
    private static final String roomInfoFilename = "RoomInfo.csv";
    private static final File roomInfoFile = new File(workingPath, roomInfoFilename);
    private static final String roomInfoSep = ",";
    private static final HashMap<String, RoomInfo> roomInfo = RoomInfoLoader.load(roomInfoFile, roomInfoSep);
    private static final String rssiData = "RSSISurveyData.csv";
    private static final File dataFile = new File(workingPath, rssiData);
    private static final String dataSep = ",";
     private static final File outputPath = new File(workingPath, "Access Point Files");
    
    private static final List<RSSIData> rssiDataList = RSSILoader.load(dataFile, dataSep, roomInfo);        
    
    public APFormatTest(String testName) {
        super(testName);
    }
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        outputPath.mkdir();
    }
    
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Test of compile method, of class APFormat.
     */
    /*
    public void testCompile() {
        System.out.println("compile");
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
        boolean isBSSIDMerged = false;
        boolean isOrientationMerged = false;
        HashMap<String, APData> expResult = null;
        HashMap<String, APData> result = APFormat.compile(rssiDataList, isBSSIDMerged, isOrientationMerged);
        assertEquals(expResult, result);
        
    }
*/
    /**
     * Test of compileList method, of class APFormat.
     */
    /*
    public void testCompileList() {
        System.out.println("compileList");
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
        boolean isBSSIDMerged = false;
        boolean isOrientationMerged = false;
        List<APData> expResult = null;
        List<APData> result = APFormat.compileList(rssiDataList, isBSSIDMerged, isOrientationMerged);
        assertEquals(expResult, result);

    }
*/
    /**
     * Test of load method, of class APFormat.
     */
    /*
    public void testLoad_4args() {
        System.out.println("load");
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
        boolean isBSSIDMerged = false;
        HashMap<String, APData> expResult = null;
        HashMap<String, APData> result = APFormat.load(dataFile, dataSep, roomInfo, isBSSIDMerged);
        assertEquals(expResult, result);
        
    }
*/
    /**
     * Test of load method, of class APFormat.
     */
    /*
    public void testLoad_5args() {
        System.out.println("load"); 
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
        boolean isBSSIDMerged = false;
        boolean isOrientationMerged = false;
        HashMap<String, APData> expResult = null;
        HashMap<String, APData> result = APFormat.load(dataFile, dataSep, roomInfo, isBSSIDMerged, isOrientationMerged);
        assertEquals(expResult, result);       
    }
*/
    /**
     * Test of loadList method, of class APFormat.
     */
    /*
    public void testLoadList_4args() {
        System.out.println("loadList");
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
        boolean isBSSIDMerged = false;
        List<APData> expResult = null;
        List<APData> result = APFormat.loadList(dataFile, dataSep, roomInfo, isBSSIDMerged);
        assertEquals(expResult, result);

    }
*/
    /**
     * Test of loadList method, of class APFormat.
     */
    /*
    public void testLoadList_5args() {
        System.out.println("loadList");
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
        boolean isBSSIDMerged = false;
        boolean isOrientationMerged = false;
        List<APData> expResult = null;
        List<APData> result = APFormat.loadList(dataFile, dataSep, roomInfo, isBSSIDMerged, isOrientationMerged);
        assertEquals(expResult, result);

    }
*/
    /**
     * Test of print method, of class APFormat with false BSSID merged and false Orientation merged.
     */
    public void testPrintFalseFalse() {
        System.out.println("print BSSID: False, Orientation: False");

        HashMap<String, APData> apDataMap = APFormat.load(dataFile, dataSep, roomInfo, false, false);
        File outputFile = new File(outputPath, "APData-false-false.csv");
        boolean expResult = true;        
        boolean result = APFormat.print(outputFile, apDataMap, dataSep);
        assertEquals(expResult, result);
        
    }
    
    /**
     * Test of print method, of class APFormat with true BSSID merged and false Orientation merged.
     */
    public void testPrintTrueFalse() {
        System.out.println("print BSSID: True, Orientation: False");

        HashMap<String, APData> apDataMap = APFormat.load(dataFile, dataSep, roomInfo, true, false);
        File outputFile = new File(outputPath, "APData-true-false.csv");
        boolean expResult = true;        
        boolean result = APFormat.print(outputFile, apDataMap, dataSep);
        assertEquals(expResult, result);
        
    }
    
    /**
     * Test of print method, of class APFormat with false BSSID merged and true Orientation merged.
     */
    public void testPrintFalseTrue() {
        System.out.println("print BSSID: False, Orientation: True");

        HashMap<String, APData> apDataMap = APFormat.load(dataFile, dataSep, roomInfo, false, true);
        File outputFile = new File(outputPath, "APData-false-true.csv");
        boolean expResult = true;        
        boolean result = APFormat.print(outputFile, apDataMap, dataSep);
        assertEquals(expResult, result);
        
    }
    
    /**
     * Test of print method, of class APFormat with true BSSID merged and true Orientation merged.
     */
    public void testPrintTrueTrue() {
        System.out.println("print BSSID: True, Orientation: True");
       
        HashMap<String, APData> apDataMap = APFormat.load(dataFile, dataSep, roomInfo, true, true);
        File outputFile = new File(outputPath, "APData-true-true.csv");
        boolean expResult = true;        
        boolean result = APFormat.print(outputFile, apDataMap, dataSep);
        assertEquals(expResult, result);
        
    }
}
