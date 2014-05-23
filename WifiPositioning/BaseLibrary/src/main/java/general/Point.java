/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package general;

/**
 * Implementation of Point with double data members. Intended to be common to
 * both Java and Android use of the library. Native awt.PointF is not available
 * in Android SDK.
 *
 * @author Greg Albiston
 */
public class Point {

    private double x;
    private double y;

    /**
     * Constructor - zero value
     */
    public Point() {
        this.x = 0;
        this.y = 0;
    }

    /**
     * Constructor - integer
     *
     * @param x x-coordinate
     * @param y y-coordinate
     */
    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Constructor - float
     *
     * @param x x-coordinate
     * @param y y-coordinate
     */
    public Point(float x, float y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Constructor - double
     *
     * @param x x-coordinate
     * @param y y-coordinate
     */
    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Set the x-coordinate value.
     *
     * @param x
     */
    public void setX(double x) {
        this.x = x;
    }

    /**
     * Set the y-coordinate value.
     *
     * @param y
     */
    public void setY(double y) {
        this.y = y;
    }

    /**
     * Get the x-coordinate value.
     *
     * @return
     */
    public double getX() {
        return x;
    }

    /**
     * Get the y-coordinate value.
     *
     * @return
     */
    public double getY() {
        return y;
    }

    /**
     * Get the x-coordinate float value.
     *
     * @return
     */
    public float getXfl() {
        return (float) x;
    }

    /**
     * Get the y-coordinate float value.
     *
     * @return
     */
    public float getYfl() {
        return (float) y;
    }

    /**
     * Get the x-coordinate integer value.
     *
     * @return
     */
    public int getXint() {
        return (int) x;
    }

    /**
     * Get the y-coordinate integer value.
     *
     * @return
     */
    public int getYint() {
        return (int) y;
    }

    /**
     * String representation of the object.
     *
     * @param fieldSeparator Field separator used in the string.
     * @return
     */
    public String toString(String fieldSeparator) {
        return x + fieldSeparator + y;
    }

    /**
     * Scale coordinates by ratio of pixels.
     *
     * @param xPixels x-coordinate pixel ratio.
     * @param yPixels y-coordinate pixel ratio.
     * @return
     */
    public Point drawPoint(double xPixels, double yPixels) {
        return new Point(x * xPixels, y * yPixels);
    }
}
