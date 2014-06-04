/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package datastorage;

import general.Point;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

/**
 * Point with an attached label.
 * 
 * @author Greg Albiston
 */
public class LabelPoint extends Point {
    
    private final String label;

    /**
     * Constructor 
     * @param x x-coordinate
     * @param y y-coordinate
     * @param label Label for the point
     */
    public LabelPoint(int x, int y, String label) {
        super(x, y);
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
        
    /**
     * Converts radio map into a list of labelled points.
     * 
     * @param radioMap Radio map of points to be converted.     
     * @return 
     */
    public static List<LabelPoint> list(final HashMap<String, ? extends Location> radioMap) {

        List<LabelPoint> labelPoints = new ArrayList<>(radioMap.size());

        Collection<? extends Location> locations = radioMap.values();
        
        for(Location location: locations){
            Point pos = location.getDrawPoint();
            labelPoints.add(new LabelPoint(pos.getXint(), pos.getYint(), location.getXYStr()));
        }        

        return labelPoints;
    }
    
}
