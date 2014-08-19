/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package visualinfo;

import junit.framework.TestCase;

/**
 *
 * @author Greg Albiston
 */
public class HeatMapTest extends TestCase {    
    
    public HeatMapTest(String testName) {
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
     * Test of printRSSI method, of class HeatMap.
     */
    public void testPrintRSSI_7args() {
        System.out.println("printRSSI");        
        
        boolean isBSSIDMerged = false;
        boolean isOrientationMerged = false;
  
        HeatMap.printRSSI(VisualInfoTestDefaults.workingPath, VisualInfoTestDefaults.floorPlanFile, VisualInfoTestDefaults.roomInfo, VisualInfoTestDefaults.rssiDataList, isBSSIDMerged, isOrientationMerged, VisualInfoTestDefaults.fieldSeparator);
    }

    /**
     * Test of printRSSI method, of class HeatMap.
     */
    public void testPrintRSSI_8args() {
        System.out.println("printRSSI");
        String filename = "HeatMapRSSI8Arg";   
        
        boolean isBSSIDMerged = false;
        boolean isOrientationMerged = false;
        
        HeatMap.printRSSI(VisualInfoTestDefaults.workingPath, filename, VisualInfoTestDefaults.floorPlanFile, VisualInfoTestDefaults.roomInfo, VisualInfoTestDefaults.rssiDataList, isBSSIDMerged, isOrientationMerged, VisualInfoTestDefaults.fieldSeparator);
    }

    /**
     * Test of printGeomagnetic method, of class HeatMap.
     */
    public void testPrintGeomagnetic_5args() {
        System.out.println("printGeomagnetic");
        
        HeatMap.printGeomagnetic(VisualInfoTestDefaults.workingPath, VisualInfoTestDefaults.floorPlanFile, VisualInfoTestDefaults.roomInfo, VisualInfoTestDefaults.geomagneticDataList, VisualInfoTestDefaults.fieldSeparator);       
    }

    /**
     * Test of printGeomagnetic method, of class HeatMap.
     */
    public void testPrintGeomagnetic_6args() {
        System.out.println("printGeomagnetic");
        
        String filename = "HeatMapGeomagnetic8Arg";
        
        HeatMap.printGeomagnetic(VisualInfoTestDefaults.workingPath, filename, VisualInfoTestDefaults.floorPlanFile, VisualInfoTestDefaults.roomInfo, VisualInfoTestDefaults.geomagneticDataList, VisualInfoTestDefaults.fieldSeparator);
    }
    
}
