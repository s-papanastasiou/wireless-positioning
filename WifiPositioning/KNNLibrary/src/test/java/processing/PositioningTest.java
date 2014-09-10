/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package processing;

import datastorage.KNNFloorPoint;
import datastorage.Location;
import datastorage.RSSIData;
import datastorage.ResultLocation;
import datastorage.RoomInfo;
import filehandling.RSSILoader;
import filehandling.RoomInfoLoader;
import general.Point;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import junit.framework.TestCase;

/**
 *
 * @author Gerg
 */
public class PositioningTest extends TestCase {
    
    
    public static final HashMap<String, KNNFloorPoint> offlineMap = createOfflineMap();
    
    public PositioningTest(String testName) {
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

    private static HashMap<String, KNNFloorPoint> createOfflineMap(){
        
        HashMap<String, KNNFloorPoint> map = new HashMap<>();
        
        int gridSpacing = 10;
        
        for(int i=1; i<=10; i++){
            for(int j=1; j<=10; j++){
                Location location = new Location("RoomA", i, j, 0, i*gridSpacing, j *gridSpacing, i*gridSpacing, j *gridSpacing);
                double value = i*10+j;
                KNNFloorPoint point = new KNNFloorPoint(location, "Trial", value);
                double randomFlux =  value + Math.random();
                point.add("Trial", randomFlux);
                map.put(point.getRoomRef(), point);
            }
        }
        return map;
    }
    
    /**
     * Test of locate method, of class Positioning.
     */
    /*
    public void testLocate() {
        System.out.println("locate");
        KNNFloorPoint trialPoint = null;
        HashMap<String, KNNFloorPoint> offlineMap = null;
        DistanceMeasure measure = null;
        int kLimit = 0;
        boolean isBiggerBetter = false;
        Point expResult = null;
        Point result = Positioning.locate(trialPoint, offlineMap, measure, kLimit, isBiggerBetter);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
*/
    /**
     * Test of locateVariance method, of class Positioning.
     */
    /*
    public void testLocateVariance() {
        System.out.println("locateVariance");
        KNNFloorPoint trialPoint = null;
        HashMap<String, KNNFloorPoint> offlineMap = null;
        DistanceMeasure measure = null;
        int kLimit = 0;
        double varLimit = 0.0;
        int varCount = 0;
        boolean isBiggerBetter = false;
        Point expResult = null;
        Point result = Positioning.locateVariance(trialPoint, offlineMap, measure, kLimit, varLimit, varCount, isBiggerBetter);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
*/
    /**
     * Test of estimate method, of class Positioning.
     */
    public void testEstimate() {
        System.out.println("estimate");
                
        KNNFloorPoint trialPoint = new KNNFloorPoint(new Location(), "Trial", 50);
                
        DistanceMeasure measure = DistanceMeasure.Euclidian;
        
        List<ResultLocation> result = Positioning.estimate(trialPoint, offlineMap, measure);
        ResultLocation result0 = result.get(0);
        ResultLocation result1 = result.get(1);
        ResultLocation result2 = result.get(2);
        ResultLocation result3 = result.get(3);
        ResultLocation result4 = result.get(4);
        ResultLocation result5 = result.get(5);
        
        
        assertEquals(true, true);        
    }

    
    /**
     * Test of nearestEstimates method, of class Positioning.
     */
    /*
    public void testNearestEstimates() {
        System.out.println("nearestEstimates");
        List<ResultLocation> resultLocations = null;
        int kLimit = 0;
        List<ResultLocation> expResult = null;
        List<ResultLocation> result = Positioning.nearestEstimates(resultLocations, kLimit);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
*/
    /**
     * Test of nearestVarianceEstimates method, of class Positioning.
     */
    /*
    public void testNearestVarianceEstimates() {
        System.out.println("nearestVarianceEstimates");
        KNNFloorPoint trialPoint = null;
        List<ResultLocation> resultLocations = null;
        int kLimit = 0;
        double varLimit = 0.0;
        int varCount = 0;
        HashMap<String, KNNFloorPoint> offlineMap = null;
        List<ResultLocation> expResult = null;
        List<ResultLocation> result = Positioning.nearestVarianceEstimates(trialPoint, resultLocations, kLimit, varLimit, varCount, offlineMap);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
 */   
}
