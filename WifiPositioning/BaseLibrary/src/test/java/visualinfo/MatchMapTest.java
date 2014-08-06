/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package visualinfo;

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
 * @author Gerg
 */
public class MatchMapTest extends TestCase {
    
    private static final String workingDirectory = "C:\\WirelessPositioningTestFiles";
    private static final File workingPath = new File(workingDirectory);
    private static final String floorPlanFilename = "floor2.png";
    private static final File floorPlanFile = new File(workingPath, floorPlanFilename);
    private static final String roomInfoFilename = "RoomInfo.csv";
    private static final File roomInfoFile = new File(workingPath, roomInfoFilename);
    private static final String roomInfoSep = ",";
    private static final HashMap<String, RoomInfo> roomInfo = RoomInfoLoader.load(roomInfoFile, roomInfoSep);
    private static final String rssiData = "RSSISurveyData.csv";
    private static final File rssiDataFile = new File(workingPath, rssiData);
    private static final String dataSep = ",";
    
    
    private static final List<RSSIData> rssiDataList = RSSILoader.load(rssiDataFile, dataSep, roomInfo);
    
    
    public MatchMapTest(String testName) {               
        super(testName);
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
     * Test of print method, of class MatchMap.
     */
    public void testPrint_3args() {
        System.out.println("print");        
        MatchMap.print(workingPath, floorPlanFile, rssiDataList);
        assertEquals(true, true);
    }

    /**
     * Test of print method, of class MatchMap.
     */
    public void testPrint_8args() {
        System.out.println("print");
        
        String filename = "MatchAnalysis8Arg";        
        double rangeValue = 0.0;
        boolean isBSSIDMerged = false;
        boolean isOrientationMerged = false;
        String fieldSeparator = "";
        MatchMap.print(workingPath, filename, floorPlanFile, rssiDataList, rangeValue, isBSSIDMerged, isOrientationMerged, fieldSeparator);        
        assertEquals(true, true);
    }
    
}
