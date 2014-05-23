/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package general;

/**
 * Implementation of Rectangle. Intended to be common to both Java and Android
 * use of the library. Native awt.Rectangle is not available in Android SDK.
 *
 * @author Greg Albiston
 */
public class Rectangle {

    public int x;
    public int y;
    public int width;
    public int height;

    /**
     * Constructor
     *
     * @param x Top left hand corner x-coordinate.
     * @param y Top left hand corner y-coordinate.
     * @param width Width of the rectangle.
     * @param height Height of the rectangle.
     */
    public Rectangle(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    /**
     * Constructor - zero values
     */
    public Rectangle() {
        this.x = 0;
        this.y = 0;
        this.width = 0;
        this.height = 0;
    }

    /**
     * True, if coordinates are in the rectangle.
     *
     * @param x x-coordinate to test.
     * @param y y-coordinate to test.
     * @return
     */
    public boolean contains(double x, double y) {
        return x >= this.x && x <= this.x + this.width && y >= this.y && y <= this.y + this.height;
    }

    /**
     * True, if coordinates are in the rectangle.
     *
     * @param point point to test.
     * @return
     */
    public boolean contains(Point point) {
        return contains(point.getX(), point.getY());
    }
}
