/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package visualinfo;

import general.Point;
import java.awt.Color;
import java.util.List;
import junit.framework.TestCase;

/**
 *
 * @author Gerg
 */
public class DisplayRouteTest extends TestCase {
    
    public DisplayRouteTest(String testName) {
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
     * Test of print method, of class DisplayRoute.
     */
    public void testPrint_5args() {
        System.out.println("print5args");
        
        String filename = "";        
        List<Point> trialPoints = null;
        List<Point> finalPoints = null;
        DisplayRoute.print(VisualInfoTestDefaults.workingPath, filename, VisualInfoTestDefaults.floorPlanFile, trialPoints, finalPoints);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of print method, of class DisplayRoute.
     */
    public void testPrint_7args() {
        System.out.println("print7args");
        
        String filename = "";
        
        List<Point> trialPoints = null;
        List<Point> finalPoints = null;
        Color trialColor = Color.BLUE;
        Color finalColor = Color.RED;
        DisplayRoute.print(VisualInfoTestDefaults.workingPath, filename, VisualInfoTestDefaults.floorPlanFile, trialPoints, finalPoints, trialColor, finalColor);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
