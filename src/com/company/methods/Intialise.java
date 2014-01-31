/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.company.methods;

import datastorage.KNNFloorPoint;
import datastorage.KNNTrialPoint;
import general.Point;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author SST3ALBISG
 */
public class Intialise {

    public static Point initialPoint(List<KNNTrialPoint> initialPoints, int initReadings, HashMap<String, KNNFloorPoint> offlineMap, int k, int orientation) {
        double x = 0;
        double y = 0;
        if (initReadings == 0) {
            throw new AssertionError("Initial readings parameter is zero but must be at least one.");
        }
        if (initReadings <= initialPoints.size()) {
        } else {
            throw new AssertionError("Initial points is less than inital readings parameter");
        }
        for (int i = 0; i < initReadings; i++) {
            Point probabilisticPoint = Probabilistic.run(initialPoints.get(i).getFloorPoint(), offlineMap, k, orientation);
            x += probabilisticPoint.getX();
            y += probabilisticPoint.getY();
        }
        return new Point(x / initReadings, y / initReadings);
    }
    
}
