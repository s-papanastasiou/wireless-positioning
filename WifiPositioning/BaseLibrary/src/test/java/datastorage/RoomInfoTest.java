/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package datastorage;

import general.Point;
import general.Rectangle;
import java.util.HashMap;
import junit.framework.TestCase;

/**
 *
 * @author SST3ALBISG
 */
public class RoomInfoTest extends TestCase {

    private final String parts[];    

    public RoomInfoTest(String testName) {
        super(testName);
        this.parts = new String[]{"TestRoom", "10.0", "12.0", "10", "20", "210", "320", "true", "30.0", "40.0"};
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
     * Test of getRoomRect method, of class RoomInfo.
     */
    public void testGetRoomRect() {
        System.out.println("getRoomRect");
        RoomInfo instance = new RoomInfo(parts);
        Rectangle expResult = new Rectangle(10, 20, 200, 300);
        Rectangle result = instance.getRoomRect();
        assertEquals(expResult, result);
    }

    /**
     * Test of getDrawRect method, of class RoomInfo.
     */
    public void testGetDrawRect() {
        System.out.println("getDrawRect");        
        Location location = new Location(parts[0], 1, 2, 3, 40, 64, 30, 70);
        RoomInfo instance = new RoomInfo(parts);
        Rectangle expResult = new Rectangle(20, 58, 10, 13);
        Rectangle result = instance.getDrawRect(location);        
        assertEquals(expResult, result);        
    }

    /**
     * Test of getName method, of class RoomInfo.
     */
    public void testGetName() {
        System.out.println("getName");
        RoomInfo instance = new RoomInfo(parts);
        String expResult = parts[0];
        String result = instance.getName();
        assertEquals(expResult, result);
    }

    /**
     * Test of getWidth method, of class RoomInfo.
     */
    public void testGetWidth() {
        System.out.println("getWidth");
        RoomInfo instance = new RoomInfo(parts);
        double expResult = Double.parseDouble(parts[1]);
        double result = instance.getWidth();
        assertEquals(expResult, result, 0.0);
    }

    /**
     * Test of getHeight method, of class RoomInfo.
     */
    public void testGetHeight() {
        System.out.println("getHeight");
        RoomInfo instance = new RoomInfo(parts);
        double expResult = Double.parseDouble(parts[2]);
        double result = instance.getHeight();
        assertEquals(expResult, result, 0.0);        
    }

    /**
     * Test of containsGlobal method, of class RoomInfo.
     */
    public void testContainsGlobal() {
        System.out.println("containsGlobal");
        Point point = new Point(35, 45);
        RoomInfo instance = new RoomInfo(parts);
        boolean expResult = true;
        boolean result = instance.containsGlobal(point);
        assertEquals(expResult, result);
    }

    /**
     * Test of notContainsGlobal method, of class RoomInfo.
     */
    public void testNotContainsGlobal() {
        System.out.println("notContainsGlobal");
        Point point = new Point(0, 0);
        RoomInfo instance = new RoomInfo(parts);
        boolean expResult = false;
        boolean result = instance.containsGlobal(point);
        assertEquals(expResult, result);
    }

    /**
     * Test of containsPixel method, of class RoomInfo.
     */
    public void testContainsPixel() {
        System.out.println("containsPixel");
        Point point = new Point(100, 100);
        RoomInfo instance = new RoomInfo(parts);
        boolean expResult = true;
        boolean result = instance.containsPixel(point);
        assertEquals(expResult, result);
    }

    /**
     * Test of notContainsPixel method, of class RoomInfo.
     */
    public void testNotContainsPixel() {
        System.out.println("notContainsPixel");
        Point point = new Point(0, 0);
        RoomInfo instance = new RoomInfo(parts);
        boolean expResult = false;
        boolean result = instance.containsPixel(point);
        assertEquals(expResult, result);
    }

    /**
     * Test of isRoom method, of class RoomInfo.
     */
    public void testIsRoom() {
        System.out.println("isRoom");
        RoomInfo instance = new RoomInfo(parts);
        int expResult = 1;
        int result = instance.isRoom();
        assertEquals(expResult, result);
    }

    /**
     * Test of NotIsRoom method, of class RoomInfo.
     */
    public void testNotIsRoom() {
        System.out.println("notIsRoom");
        String[] local = parts;
        local[7] = "0";
        RoomInfo instance = new RoomInfo(local);
        int expResult = 0;
        int result = instance.isRoom();
        assertEquals(expResult, result);
    }

    /**
     * Test of createGlobalLocation method, of class RoomInfo.
     */
    public void testCreateGlobalLocation_Point() {
        System.out.println("createGlobalLocation");
        Point point = new Point(40, 64);
        RoomInfo instance = new RoomInfo(parts);
        Location expResult = new Location(parts[0], 1, 2, 0, 40, 64, 30, 70);
        Location result = instance.createGlobalLocation(point);
        assertEquals(expResult, result);  
    }

    /**
     * Test of createGlobalLocation method, of class RoomInfo.
     */
    public void testCreateGlobalLocation_Point_int() {
        System.out.println("createGlobalLocationPoint Int");                
        Point point = new Point(40, 64);
        int wRef = 3;
        RoomInfo instance = new RoomInfo(parts);
        Location expResult = new Location(parts[0], 1, 2, wRef, 40, 64, 30, 70);
        Location result = instance.createGlobalLocation(point, wRef);
        assertEquals(expResult, result);        
    }

    /**
     * Test of createPixelLocation method, of class RoomInfo.
     */
    public void testCreatePixelLocation_Point() {
        System.out.println("createPixelLocation");
        Point point = new Point(40, 64);
        RoomInfo instance = new RoomInfo(parts);
        Location expResult = new Location(parts[0], 1, 2, 0, 40, 64, 30, 70);
        Location result = instance.createPixelLocation(point);
        assertEquals(expResult, result);        
    }

    /**
     * Test of createPixelLocation method, of class RoomInfo.
     */
    public void testCreatePixelLocation_Point_int() {
        System.out.println("createPixelLocationPoint Int");                
        Point point = new Point(40, 64);
        int wRef = 3;
        RoomInfo instance = new RoomInfo(parts);
        Location expResult = new Location(parts[0], 1, 2, wRef, 40, 64, 30, 70);
        Location result = instance.createPixelLocation(point, wRef);
        assertEquals(expResult, result); 
    }

    /**
     * Test of createLocation method, of class RoomInfo.
     */
    public void testCreateLocation_3args() {
        System.out.println("createLocation");
        int xRef = 1;
        int yRef = 2;
        int wRef = 3;
        RoomInfo instance = new RoomInfo(parts);
        Location expResult = new Location(parts[0], xRef, yRef, wRef, 40, 64, 30, 70);
        Location result = instance.createLocation(xRef, yRef, wRef);
        assertEquals(expResult, result);        
    }

    /**
     * Test of headerCheck method, of class RoomInfo.
     */
    public void testHeaderCheck() {
        System.out.println("headerCheck");
        String[] heading = {"Name", "Width", "Height", "Left", "Top", "Right", "Bottom", "isRoom", "GlobalX", "GlobalY"};
        boolean expResult = true;
        boolean result = RoomInfo.headerCheck(heading);
        assertEquals(expResult, result);        
    }
    
    /**
     * Test of headerCheck method, of class RoomInfo.
     */
    public void testNotHeaderCheck() {
        System.out.println("headerCheck");
        String[] heading = {"N", "W", "H", "L", "T", "R", "B", "is", "GX", "GY"};
        boolean expResult = false;
        boolean result = RoomInfo.headerCheck(heading);
        assertEquals(expResult, result);        
    }

    /**
     * Test of searchGlobalLocation method, of class RoomInfo.
     */
    public void testSearchGlobalLocation() {
        System.out.println("searchGlobalLocation");
        Point point = new Point(35, 45);
        HashMap<String, RoomInfo> roomInfo = new HashMap<>();
        roomInfo.put(parts[0], new RoomInfo(parts));
        Location expResult = new Location(parts[0], 1, 2, 0, 40, 64, 30, 70);
        Location result = RoomInfo.searchGlobalLocation(point, roomInfo);
        assertEquals(expResult, result);        
    }

    /**
     * Test of searchPixelLocation method, of class RoomInfo.
     */
    public void testSearchPixelLocation() {
        System.out.println("searchPixelLocation");
        Point point = new Point(35, 45);
        HashMap<String, RoomInfo> roomInfo = new HashMap<>();
        roomInfo.put(parts[0], new RoomInfo(parts));
        Location expResult = new Location(parts[0], 1, 2, 0, 40, 64, 30, 70);
        Location result = RoomInfo.searchPixelLocation(point, roomInfo);
        assertEquals(expResult, result);        
    }

    /**
     * Test of createLocation method, of class RoomInfo.
     */
    public void testCreateLocation_5args() {
        System.out.println("createLocation");
        String room = parts[0];
        int xRef = 1;
        int yRef = 2;
        int wRef = 3;
        HashMap<String, RoomInfo> roomInfo = new HashMap<>();
        roomInfo.put(parts[0], new RoomInfo(parts));
        Location expResult = new Location(parts[0], 1, 2, 0, 40, 64, 30, 70);
        Location result = RoomInfo.createLocation(room, xRef, yRef, wRef, roomInfo);
        assertEquals(expResult, result);        
    }

    /**
     * Test of numberFormatException arg1, of class RoomInfo.
     */
    public void testNumberFormatExceptionArg1() {
        System.out.println("NumberFormatException Arg 1");
        String[] local = parts;
        local[1] = "10A";
        try {
            RoomInfo instance = new RoomInfo(local);           
            fail("Failed to throw exception.");
        } catch (NumberFormatException ex) {

        }
    }
    
    /**
     * Test of numberFormatException arg2, of class RoomInfo.
     */
    public void testNumberFormatExceptionArg2() {
        System.out.println("NumberFormatException Arg 2");
        String[] local = parts;
        local[2] = "10A";
        try {
            RoomInfo instance = new RoomInfo(local);           
            fail("Failed to throw exception.");
        } catch (NumberFormatException ex) {

        }
    }
    
    /**
     * Test of numberFormatException arg3, of class RoomInfo.
     */
    public void testNumberFormatExceptionArg3() {
        System.out.println("NumberFormatException Arg 3");
        String[] local = parts;
        local[3] = "10.0";
        try {
            RoomInfo instance = new RoomInfo(local);           
            fail("Failed to throw exception.");
        } catch (NumberFormatException ex) {

        }
    }
    
    /**
     * Test of numberFormatException arg4, of class RoomInfo.
     */
    public void testNumberFormatExceptionArg4() {
        System.out.println("NumberFormatException Arg 4");
        String[] local = parts;
        local[4] = "10.0";
        try {
            RoomInfo instance = new RoomInfo(local);           
            fail("Failed to throw exception.");
        } catch (NumberFormatException ex) {

        }
    }
    
    /**
     * Test of numberFormatException arg5, of class RoomInfo.
     */
    public void testNumberFormatExceptionArg5() {
        System.out.println("NumberFormatException Arg 5");
        String[] local = parts;
        local[5] = "10.0";
        try {
            RoomInfo instance = new RoomInfo(local);           
            fail("Failed to throw exception.");
        } catch (NumberFormatException ex) {

        }
    }
    
    /**
     * Test of numberFormatException arg6, of class RoomInfo.
     */
    public void testNumberFormatExceptionArg6() {
        System.out.println("NumberFormatException Arg 6");
        String[] local = parts;
        local[6] = "10.0";
        try {
            RoomInfo instance = new RoomInfo(local);           
            fail("Failed to throw exception.");
        } catch (NumberFormatException ex) {

        }
    }
    
    /**
     * Test of numberFormatException arg8, of class RoomInfo.
     */
    public void testNumberFormatExceptionArg8() {
        System.out.println("NumberFormatException Arg 8");
        String[] local = parts;
        local[8] = "10A";
        try {
            RoomInfo instance = new RoomInfo(local);           
            fail("Failed to throw exception.");
        } catch (NumberFormatException ex) {

        }
    }
    
    /**
     * Test of numberFormatException arg9, of class RoomInfo.
     */
    public void testNumberFormatExceptionArg9() {
        System.out.println("NumberFormatException Arg 9");
        String[] local = parts;
        local[9] = "10A";
        try {
            RoomInfo instance = new RoomInfo(local);           
            fail("Failed to throw exception.");
        } catch (NumberFormatException ex) {

        }
    }
}
