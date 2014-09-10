/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package general;

import datastorage.Location;
import datastorage.ResultLocation;
import java.util.ArrayList;
import java.util.List;
import junit.framework.TestCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Gerg
 */
public class LocateTest extends TestCase {
    private static final Logger logger = LoggerFactory.getLogger(LocateTest.class);
    
    public LocateTest(String testName) {
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
     * Test of findWeightedCentre method, of class Locate.
     */
    public void testFindWeightedCentreSmallerBetter() {
        System.out.println("findWeightedCentreSmallerBetter");
        List<ResultLocation> positions = new ArrayList<>();
        Location A = new Location("A", 1, 1, 1, 200, 200, 200, 200);
        positions.add(new ResultLocation(A, 5));
        
        Location B = new Location("B", 2, 2, 2, 300, 300, 300, 300);
        positions.add(new ResultLocation(B, 1));
        
        Location C = new Location("C", 3, 3, 3, 400, 400, 400, 400);
        positions.add(new ResultLocation(C, 10));
        
        boolean isBiggerBetter = false;
        ResultPoint result = Locate.findWeightedCentre(positions, isBiggerBetter);
        ResultPoint expResult = new ResultPoint(new Point(284.375, 284.375),new Point(284.375, 284.375));
        logger.info("Result: {} Exp: {}", result.toString(),expResult.toString());
        assertEquals(result, expResult);
        
    }
    
    /**
     * Test of findWeightedCentre method, of class Locate.
     */
    public void testFindWeightedCentreSmallerBetter2() {
        System.out.println("findWeightedCentreSmallerBetter2");
        List<ResultLocation> positions = new ArrayList<>();
        Location A = new Location("A", 1, 1, 1, 200, 200, 200, 200);
        positions.add(new ResultLocation(A, 0.3));
        
        Location B = new Location("B", 2, 2, 2, 300, 300, 300, 300);
        positions.add(new ResultLocation(B, 0));
        
        Location C = new Location("C", 3, 3, 3, 400, 400, 400, 400);
        positions.add(new ResultLocation(C, 0.5));
        
        boolean isBiggerBetter = false;
        ResultPoint result = Locate.findWeightedCentre(positions, isBiggerBetter);
        ResultPoint expResult = new ResultPoint(new Point(287.5015623046724, 287.5015623046724),new Point(287.5015623046724, 287.5015623046724));
        
        logger.info("Result: {} Exp: {}", result.toString(),expResult.toString());
        assertEquals(result, expResult);
        
    }
    
    /**
     * Test of findWeightedCentre method, of class Locate.
     */
    public void testFindWeightedCentreSmallerBetter3() {
        System.out.println("findWeightedCentreSmallerBetter3");
        List<ResultLocation> positions = new ArrayList<>();
        Location A = new Location("A", 1, 1, 1, 200, 200, 200, 200);
        positions.add(new ResultLocation(A, 0.3));
                
        boolean isBiggerBetter = false;
        ResultPoint result = Locate.findWeightedCentre(positions, isBiggerBetter);
        ResultPoint expResult = new ResultPoint(new Point(200, 200), new Point(200, 200));
        
        logger.info("Result: {} Exp: {}", result.toString(),expResult.toString());
        assertEquals(result, expResult);
        
    }
    
    /**
     * Test of findWeightedCentre method, of class Locate.
     */
    public void testFindWeightedCentreBiggerBetter() {
        System.out.println("findWeightedCentreBiggerBetter");
        List<ResultLocation> positions = new ArrayList<>();
        Location A = new Location("A", 1, 1, 1, 200, 200, 200, 200);
        positions.add(new ResultLocation(A, 5));
        
        Location B = new Location("B", 2, 2, 2, 300, 300, 300, 300);
        positions.add(new ResultLocation(B, 1));
        
        Location C = new Location("C", 3, 3, 3, 400, 400, 400, 400);
        positions.add(new ResultLocation(C, 10));
        
        boolean isBiggerBetter = true;
        ResultPoint result = Locate.findWeightedCentre(positions, isBiggerBetter);
        ResultPoint expResult = new ResultPoint(new Point(331.25, 331.25),new Point(331.25, 331.25));
       
        logger.info("Result: {} Exp: {}", result.toString(),expResult.toString());
        assertEquals(result, expResult);        
    }
    
    /**
     * Test of findWeightedCentre method, of class Locate.
     */
    public void testFindWeightedCentreBiggerBetter2() {
        System.out.println("findWeightedCentreBiggerBetter2");
        List<ResultLocation> positions = new ArrayList<>();
        Location A = new Location("A", 1, 1, 1, 200, 200, 200, 200);
        positions.add(new ResultLocation(A, 0.3));
        
        Location B = new Location("B", 2, 2, 2, 300, 300, 300, 300);
        positions.add(new ResultLocation(B, 0));
        
        Location C = new Location("C", 3, 3, 3, 400, 400, 400, 400);
        positions.add(new ResultLocation(C, 0.5));
        
        boolean isBiggerBetter = true;
        ResultPoint result = Locate.findWeightedCentre(positions, isBiggerBetter);
        ResultPoint expResult = new ResultPoint(new Point(325, 325),new Point(325, 325));
        
        logger.info("Result: {} Exp: {}", result.toString(),expResult.toString());
        assertEquals(result, expResult);        
    }

    /**
     * Test of findWeightedCentre method, of class Locate.
     */
    public void testFindWeightedCentreBiggerBetter3() {
        System.out.println("findWeightedCentreBiggerBetter3");
        List<ResultLocation> positions = new ArrayList<>();
        Location A = new Location("A", 1, 1, 1, 200, 200, 200, 200);
        positions.add(new ResultLocation(A, 0.3));              
        
        boolean isBiggerBetter = true;
        ResultPoint result = Locate.findWeightedCentre(positions, isBiggerBetter);
        ResultPoint expResult = new ResultPoint(new Point(200, 200),new Point(200, 200));
        
        logger.info("Result: {} Exp: {}", result.toString(),expResult.toString());
        assertEquals(result, expResult);        
    }
    /**
     * Test of findUnweightedCentre method, of class Locate.
     */
    /*
    public void testFindUnweightedCentre() {
        System.out.println("findUnweightedCentre");
        List<? extends Location> positions = null;
        Point expResult = null;
        Point result = Locate.findUnweightedCentre(positions);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
*/
    /**
     * Test of forceToMap method, of class Locate.
     */
    /*
    public void testForceToMap() {
        System.out.println("forceToMap");
        HashMap<String, ? extends Location> offlineMap = null;
        Location estimatedLocation = null;
        boolean forceToOfflineMap = false;
        Location expResult = null;
        Location result = Locate.forceToMap(offlineMap, estimatedLocation, forceToOfflineMap);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    */
}
