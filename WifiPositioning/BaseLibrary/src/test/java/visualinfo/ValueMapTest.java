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
public class ValueMapTest extends TestCase {
    
    public ValueMapTest(String testName) {               
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
     * Test of print method, of class ValueMap.
     */
    public void testPrint_3args() {
        System.out.println("print");        
        ValueMap.print(VisualInfoTestDefaults.workingPath, VisualInfoTestDefaults.floorPlanFile, VisualInfoTestDefaults.rssiDataList, VisualInfoTestDefaults.roomInfo);
        assertEquals(true, true);
    }

    /**
     * Test of print method, of class ValueMap.
     */
    public void testPrint_8args() {
        System.out.println("print");
        
        String filename = "ValueAnalysis8Arg";        
        double rangeValue = 0.0;
        boolean isBSSIDMerged = false;
        boolean isOrientationMerged = false;
        
        Double lowerBound = -30.0;
        Double upperBound = -20.0;
        Double step = 1.0;
        ValueMap.print(VisualInfoTestDefaults.workingPath, filename, VisualInfoTestDefaults.floorPlanFile, VisualInfoTestDefaults.rssiDataList, VisualInfoTestDefaults.roomInfo, rangeValue, lowerBound, upperBound, step, isBSSIDMerged, isOrientationMerged, VisualInfoTestDefaults.fieldSeparator);        
        assertEquals(true, true);
    }
    
}
