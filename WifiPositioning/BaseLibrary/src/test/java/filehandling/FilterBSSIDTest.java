/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package filehandling;

import datastorage.RSSIData;
import java.io.File;
import java.util.List;
import junit.framework.TestCase;

/**
 *
 * @author N0472738
 */
public class FilterBSSIDTest extends TestCase {
    
    public FilterBSSIDTest(String testName) {
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
     * Test of load method, of class FilterBSSID.
     */
    public void testLoad() {
        System.out.println("load");
        File filterFile = null;
        List<String> expResult = null;
        List<String> result = FilterBSSID.load(filterFile);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of generate method, of class FilterBSSID.
     */
    public void testGenerate() {
        System.out.println("generate");
        List<RSSIData> rssiDataList = null;
        Integer minLocations = null;
        Boolean isBSSIDMerged = null;
        List<String> expResult = null;
        List<String> result = FilterBSSID.generate(rssiDataList, minLocations, isBSSIDMerged);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of store method, of class FilterBSSID.
     */
    public void testStore() {
        System.out.println("store");
        File outputFile = null;
        List<String> filterBSSIDs = null;
        boolean expResult = false;
        boolean result = FilterBSSID.store(outputFile, filterBSSIDs);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
