/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package filehandling;

import datastorage.KNNFloorPoint;
import static datastorage.TrialDefaults.dataSep;
import static datastorage.TrialDefaults.geomagneticDataFile;
import static datastorage.TrialDefaults.roomInfo;
import static datastorage.TrialDefaults.rssiDataFile;
import static datastorage.TrialDefaults.workingPath;
import java.io.File;
import java.util.List;
import junit.framework.TestCase;

/**
 *
 * @author Gerg
 */
public class KNNFormatStorageTest extends TestCase {
            
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
    
    public void testRSSIUnmergedPrintAnalysis() {
        System.out.println("printRSSIUnmergedAnalysis");
        File outputFile = new File(outputPath, "KNNRSSIUnmerged-Analysis.csv");
        Boolean isBSSIDMerged = false;
        List<KNNFloorPoint> knnFloorPoints = KNNRSSI.loadList(rssiDataFile, fieldSeparator, roomInfo, isBSSIDMerged);
        
        KNNFormatStorage instance = new KNNFormatStorage();
        instance.printAnalysis(outputFile, knnFloorPoints, dataSep);        
    }
    
    public void testRSSIMergedPrintAnalysis() {
        System.out.println("printRSSIMergedAnalysis");
        File outputFile = new File(outputPath, "KNNRSSIMerged-Analysis.csv");
        Boolean isBSSIDMerged = true;
        List<KNNFloorPoint> knnFloorPoints = KNNRSSI.loadList(rssiDataFile, fieldSeparator, roomInfo, isBSSIDMerged);
        
        KNNFormatStorage instance = new KNNFormatStorage();
        instance.printAnalysis(outputFile, knnFloorPoints, dataSep);        
    }
    
    public void testGeomagneticPrintAnalysis() {
        System.out.println("printGeomagneticAnalysis");
        File outputFile = new File(outputPath, "KNNGEO-Analysis.csv");        
        List<KNNFloorPoint> knnFloorPoints = KNNGeomagnetic.loadList(geomagneticDataFile, fieldSeparator, roomInfo);        
        KNNFormatStorage instance = new KNNFormatStorage();
        instance.printAnalysis(outputFile, knnFloorPoints, dataSep);        
    }
    
}
