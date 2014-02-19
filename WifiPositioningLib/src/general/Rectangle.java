/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package general;

/**
 *
 * @author Gerg
 */
public class Rectangle {
    
    public int x;
    public int y;
    public int width;
    public int height;

    public Rectangle(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public Rectangle() {
        this.x = 0;
        this.y = 0;
        this.width = 0;
        this.height = 0;
    }        
    
    public boolean contains(float x, float y){
        boolean isContained = false;
        
        if(x>=this.x&&x<=this.x+this.width)
            isContained = true;
        
        if(isContained&&y>=this.y&&y<=this.y+this.height)
            isContained = true;
        else
            isContained = false;
        
        return isContained;
    }
}
