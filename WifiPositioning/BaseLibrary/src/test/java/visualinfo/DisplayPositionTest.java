/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package visualinfo;

import datastorage.Location;
import datastorage.ResultLocation;
import general.Point;
import java.util.ArrayList;
import java.util.List;
import junit.framework.TestCase;

/**
 *
 * @author Greg Albiston
 */
public class DisplayPositionTest extends TestCase {
    
    public DisplayPositionTest(String testName) {
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
     * Test of print method, of class DisplayPosition.
     */
    public void testPrint_4args() {
        System.out.println("print4args");
        
        String filename = "Display4Args";
        Point finalPosition = new Point(300,300);
        DisplayPosition.print(VisualInfoTestDefaults.workingPath, filename, VisualInfoTestDefaults.floorPlanFile, finalPosition);       
    }

    /**
     * Test of print method, of class DisplayPosition.
     */
    public void testPrint_5args() {
        System.out.println("print5args");
        
        String filename = "Display5Args";
        Point finalPosition = new Point(300,300);
        Location testLocation = new Location("TestRoom", 1, 2, 3, 5, 10, 450, 450);
        DisplayPosition.print(VisualInfoTestDefaults.workingPath, filename, VisualInfoTestDefaults.floorPlanFile, finalPosition, testLocation);        
    }

    /**
     * Test of print method, of class DisplayPosition.
     */
    public void testPrint_6args() {
        System.out.println("print");
        
        String filename = "Display6Args";
        Point finalPosition = new Point(300,300);
        Location testLocation = new Location("TestRoom", 1, 2, 3, 5, 10, 450, 450);
        List<ResultLocation> positionEstimates = new ArrayList<>();
        positionEstimates.add(new ResultLocation(new Location("TestRoom", 1, 2, 3, 5, 10, 250, 300), 60.0, "TestRoom"));
        positionEstimates.add(new ResultLocation(new Location("TestRoom", 1, 2, 3, 5, 10, 300, 250), 60.0, "TestRoom"));
        positionEstimates.add(new ResultLocation(new Location("TestRoom", 1, 2, 3, 5, 10, 350, 300), 60.0, "TestRoom"));
        positionEstimates.add(new ResultLocation(new Location("TestRoom", 1, 2, 3, 5, 10, 300, 350), 60.0, "TestRoom"));
        DisplayPosition.print(VisualInfoTestDefaults.workingPath, filename, VisualInfoTestDefaults.floorPlanFile, finalPosition, testLocation, positionEstimates);        
    }
    
}
