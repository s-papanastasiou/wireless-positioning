/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package filehandling;

import datastorage.GeomagneticData;
import datastorage.KNNFloorPoint;
import datastorage.RSSIData;
import datastorage.RoomInfo;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import junit.framework.TestCase;

/**
 *
 * @author Gerg
 */
public class KNNFormatStorageTest extends TestCase {
    
    public static final String workingDirectory = "C:\\WirelessPositioningTestFiles";
    public static final File workingPath = new File(workingDirectory);
    public static final String floorPlanFilename = "floor2.png";
    public static final File floorPlanFile = new File(workingPath, floorPlanFilename);
    public static final String roomInfoFilename = "RoomInfo.csv";
    public static final File roomInfoFile = new File(workingPath, roomInfoFilename);
    public static final String roomInfoSep = ",";
    public static final HashMap<String, RoomInfo> roomInfo = RoomInfoLoader.load(roomInfoFile, roomInfoSep);
    
    public static final String dataSep = ",";
            
    public static final String rssiData = "RSSISurveyData.csv";
    public static final File rssiDataFile = new File(workingPath, rssiData);
    public static final List<RSSIData> rssiDataList = RSSILoader.load(rssiDataFile, dataSep, roomInfo);
    public static final String geomagneticData = "GeomagneticSurveyData.csv";
    public static final File geomagneticDataFile = new File(workingPath, geomagneticData);
    public static final List<GeomagneticData> geomagneticDataList = GeomagneticLoader.load(geomagneticDataFile, dataSep, roomInfo);
    
    private static final File outputPath = new File(workingPath, "KNNFormat");
    public static final String fieldSeparator = ",";
    
    public KNNFormatStorageTest(String testName) {
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
     * Test of print method, of class KNNFormatStorage.
     */
    public void testRSSIUnmergedPrint() {
        System.out.println("printRSSIUnmerged");
        File outputFile = new File(outputPath, "KNNRSSIUnmerged.csv");
        Boolean isBSSIDMerged = false;
        List<KNNFloorPoint> knnFloorPoints = KNNRSSI.loadList(rssiDataFile, fieldSeparator, roomInfo, isBSSIDMerged);
        
        KNNFormatStorage instance = new KNNFormatStorage();
        instance.print(outputFile, knnFloorPoints, dataSep);        
    }
    
    public void testRSSIMergedPrint() {
        System.out.println("printRSSIMerged");
        File outputFile = new File(outputPath, "KNNRSSIMerged.csv");
        Boolean isBSSIDMerged = true;
        List<KNNFloorPoint> knnFloorPoints = KNNRSSI.loadList(rssiDataFile, fieldSeparator, roomInfo, isBSSIDMerged);
        
        KNNFormatStorage instance = new KNNFormatStorage();
        instance.print(outputFile, knnFloorPoints, dataSep);        
    }
    
    public void testGeomagneticPrint() {
        System.out.println("printGeomagnetic");
        File outputFile = new File(outputPath, "KNNGEO.csv");        
        List<KNNFloorPoint> knnFloorPoints = KNNGeomagnetic.loadList(geomagneticDataFile, fieldSeparator, roomInfo);        
        KNNFormatStorage instance = new KNNFormatStorage();
        instance.print(outputFile, knnFloorPoints, dataSep);        
    }
    
}
