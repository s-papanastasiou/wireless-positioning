/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package general;

/**
 *
 * @author Greg Albiston
 * Implementation of Point with double data members.
 * Intended to be common to both command line and Android use of the library.
 * Native awt.PointF is not available in Android SDK.
 */
public class Point {
    
    private double x;
    private double y;
    
    public Point() {
        this.x = 0;
        this.y = 0;
    }
    
    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }
    
    public Point(float x, float y) {
        this.x = x;
        this.y = y;        
    }
    
    public Point(double x, double y) {
        this.x = x;
        this.y = y;        
    }
    
    public void setX(double x){
        this.x = x;
    }
    
    public void setY(double y){
        this.y = y;
    }        
    
    public double getX(){
        return x;
    }
    
    public double getY(){
        return y;
    }
    
    public float getXfl(){
        return (float)x;
    }
    
    public float getYfl(){
        return (float)y;
    }
    
    public int getXint(){
        return (int)x;
    }
    
    public int getYint(){
        return (int)y;
    }
    
    public String toString(String fieldSeparator){
        return x + fieldSeparator + y;
    }   
    
    /**
     * Scale co-ordinate by pixels.
     * 
     * @param xPixels
     * @param yPixels
     * @return 
     */
    public Point drawPoint(double xPixels, double yPixels){        
        return new Point(x * xPixels, y * yPixels);        
    }        
}
