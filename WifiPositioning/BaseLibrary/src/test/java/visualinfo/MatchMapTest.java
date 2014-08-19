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
public class MatchMapTest extends TestCase {
    
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
        MatchMap.print(VisualInfoTestDefaults.workingPath, VisualInfoTestDefaults.floorPlanFile, VisualInfoTestDefaults.rssiDataList, VisualInfoTestDefaults.roomInfo);
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
        
        MatchMap.print(VisualInfoTestDefaults.workingPath, filename, VisualInfoTestDefaults.floorPlanFile, VisualInfoTestDefaults.rssiDataList, VisualInfoTestDefaults.roomInfo, rangeValue, isBSSIDMerged, isOrientationMerged, VisualInfoTestDefaults.fieldSeparator);        
        assertEquals(true, true);
    }
    
}
