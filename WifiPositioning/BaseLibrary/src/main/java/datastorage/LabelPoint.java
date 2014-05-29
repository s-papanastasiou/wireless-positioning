/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package datastorage;

import filehandling.RoomInfo;
import general.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

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
     * @param roomInfo Information about the rooms to convert radio map point to coordinates.
     * @return 
     */
    public static List<LabelPoint> list(final HashMap<String, ? extends Location> radioMap, final HashMap<String, RoomInfo> roomInfo) {

        List<LabelPoint> labelPoints = new ArrayList<>();

        Set<String> keys = radioMap.keySet();

        for (String key : keys) {
            Location point = radioMap.get(key);
            if (roomInfo.containsKey(point.getRoom())) {

                Point pos = RoomInfo.searchPoint(point, roomInfo);
                labelPoints.add(new LabelPoint(pos.getXint(), pos.getYint(), point.getXYStr()));
            }
        }

        return labelPoints;
    }
    
}
