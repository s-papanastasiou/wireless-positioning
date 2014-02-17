/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.company.methods;

import datastorage.KNNFloorPoint;
import general.Point;
import java.util.HashMap;
import java.util.Set;

/**
 *
 * @author SST3ALBISG
 */
public class ForceToMap {

    public static Point findBestPoint(HashMap<String, KNNFloorPoint> offlineMap, Point estiPos, boolean forceToOfflineMap) {
        Point bestPoint = new Point();
        if (forceToOfflineMap) {
            double lowestDistance = Double.MAX_VALUE;
            Set<String> keys = offlineMap.keySet();
            for (String key : keys) {
                KNNFloorPoint knnFloorPoint = offlineMap.get(key);
                Point point = new Point(knnFloorPoint.getxRef(), knnFloorPoint.getyRef());
                double distance = Math.hypot(point.getX() - estiPos.getX(), point.getY() - (estiPos.getY()));
                if (distance < lowestDistance) {
                    bestPoint = point;
                    lowestDistance = distance;
                }
            }
        } else {
            bestPoint = estiPos;
        }
        return bestPoint;
    }
    
}
