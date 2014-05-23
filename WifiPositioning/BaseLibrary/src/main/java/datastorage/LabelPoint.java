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
 *
 * @author Greg Albiston
 */
public class LabelPoint extends Point {
    
    public String label;

    public LabelPoint(int x, int y,String label) {
        super(x, y);
        this.label = label;
    }
        
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
