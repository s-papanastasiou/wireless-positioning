/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package visualinfo;

import java.awt.Color;
import junit.framework.TestCase;

/**
 *
 * @author Greg Albiston
 */
public class DisplayGridTest extends TestCase {
    
    
    
    public DisplayGridTest(String testName) {
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
     * Test of print method, of class DisplayGrid.
     */
    public void testPrint_6args() {
        System.out.println("print");
        
        String filename = "DisplayGrid6argRSSI";
        boolean isRoomOutlineDraw = true;
        DisplayGrid.print(VisualInfoTestDefaults.workingPath, filename, VisualInfoTestDefaults.floorPlanFile, VisualInfoTestDefaults.rssiDataList, VisualInfoTestDefaults.roomInfo, isRoomOutlineDraw);        
    }

    /**
     * Test of print method, of class DisplayGrid.
     */
    public void testPrint_8args() {
        System.out.println("print");
        
        String filename = "DisplayGrid8argGeomagnetic";
        boolean isRoomOutlineDraw = true;
        Color pointColour = Color.BLUE;
        Color roomColour = Color.RED;
        DisplayGrid.print(VisualInfoTestDefaults.workingPath, filename, VisualInfoTestDefaults.floorPlanFile, VisualInfoTestDefaults.geomagneticDataList, VisualInfoTestDefaults.roomInfo, isRoomOutlineDraw, pointColour, roomColour);        
    }
    
}
