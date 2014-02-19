/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package datastorage;

import general.Point;

/**
 *
 * @author Gerg
 */
public class LabelPoint extends Point {
    
    public String label;

    public LabelPoint(int x, int y,String label) {
        super(x, y);
        this.label = label;
    }
        
    
}
