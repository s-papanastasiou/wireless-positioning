/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package datastorage;

import filehandling.KNNGeomagnetic;
import filehandling.KNNRSSI;
import java.util.List;
import junit.framework.TestCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Gerg
 */
public class KNNTrialPointTest extends TestCase {

    private static final Logger logger = LoggerFactory.getLogger(KNNTrialPointTest.class);

    public KNNTrialPointTest(String testName) {
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
     * Test of getTimestamp method, of class KNNTrialPoint.
     */
    public void testGetTimestamp() {
        System.out.println("getTimestamp");
        KNNTrialPoint instance = null;
        long expResult = 0L;
        long result = instance.getTimestamp();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of equals method, of class KNNTrialPoint.
     */
    public void testEquals() {
        System.out.println("equals");
        long timestamp = 0L;
        String roomRef = "";
        KNNTrialPoint instance = null;
        boolean expResult = false;
        boolean result = instance.equals(timestamp, roomRef);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of merge method, of class KNNTrialPoint.
     */
    public void testMerge() {
        System.out.println("merge");
        
        List<KNNTrialPoint> firstTrialPoints = KNNRSSI.compileTrialList(TrialDefaults.rssiDataList, Boolean.FALSE, Boolean.FALSE);
        List<KNNTrialPoint> secondTrialPoints = KNNGeomagnetic.compileTrialList(TrialDefaults.geomagneticDataList);
        
        List<KNNTrialPoint> result = KNNTrialPoint.merge(firstTrialPoints, secondTrialPoints);
        logger.info("{}");
    }

}
