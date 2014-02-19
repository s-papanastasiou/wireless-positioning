package com.example.ProbabilisticMethod;

import com.example.Sensor.InertialData;
import com.example.Sensor.Logging;
import datastorage.KNNFloorPoint;
import general.AvgValue;
import general.Point;

import java.util.*;

public class Probabilistic {

    public static Point run(KNNFloorPoint onlinepoint, HashMap<String, KNNFloorPoint> offlineMap, int kLimit, int orientation) {

        Point point;

        if (!onlinepoint.getAttributes().isEmpty()) {

            TreeSet<ProbResult> results = new TreeSet<ProbResult>();
            HashMap<String, KNNFloorPoint> orientatedMap;

            if (orientation != InertialData.NO_ORIENTATION)
                orientatedMap = filterMap(offlineMap, orientation);
            else
                orientatedMap = offlineMap;

            Set<String> keys = orientatedMap.keySet();

            for (String key : keys) {
                KNNFloorPoint offlinepoint = orientatedMap.get(key);

                double prob = probabilite(onlinepoint, offlinepoint);
                results.add(new ProbResult(key, prob));
            }

            List<ProbResult> kList = new ArrayList<ProbResult>();

            for (int i = 0; i < kLimit; i++) {
                kList.add(results.pollLast());
            }
            point = position(kList, orientatedMap);
        } else {
            point = new Point(-1, -1);
            Logging.printLine("Crasssshhhhhhh!!!!!!!!!! No wifi?!?!?!");
        }

        return point;
    }

    private static HashMap<String, KNNFloorPoint> filterMap(HashMap<String, KNNFloorPoint> offlineMap, int orientation) {
        HashMap<String, KNNFloorPoint> orientatedMap;

        orientatedMap = new HashMap<String, KNNFloorPoint>();
        Set<String> keys = offlineMap.keySet();

        for (String key : keys) {
            KNNFloorPoint orientatedPoint = offlineMap.get(key);
            if (orientatedPoint.getwRef() == orientation) {
                orientatedMap.put(key, orientatedPoint);
            }
        }

        return orientatedMap;
    }

    private static Point position(List<ProbResult> kList, HashMap<String, KNNFloorPoint> orientatedMap) {
        double x = 0;
        double y = 0;
        double sum = 0;

        for (ProbResult result : kList) {
            KNNFloorPoint point = orientatedMap.get(result.key);
            x += result.value * point.getxRef();
            y += result.value * point.getyRef();
            sum += result.value;
        }


        return new Point(x / sum, y / sum);
    }

    private static double probabilite(KNNFloorPoint onlinepoint, KNNFloorPoint offlinepoint) {
        double p = 0;
        final double sqrPI = Math.sqrt(2 * Math.PI);

        HashMap<String, AvgValue> bssidsonline = onlinepoint.getAttributes();
        Set<String> bssidKeysonline = bssidsonline.keySet();

        HashMap<String, AvgValue> bssidsoffline = offlinepoint.getAttributes();
        for (String bssidKeyonline : bssidKeysonline) {
            if (bssidsoffline.containsKey(bssidKeyonline)) {
                AvgValue online = bssidsonline.get(bssidKeyonline);
                AvgValue offline = bssidsoffline.get(bssidKeyonline);

                if ((offline.getMean() != 0) && (offline.getVariance() > 0)) {
                    double c = 1 / (offline.getStdDev() * sqrPI);
                    double d = (online.getMean() - offline.getMean());
                    double powD = Math.pow(d, 2);
                    double e = Math.exp(-powD / (2 * offline.getVariance()));
                    p += (c * e);
                }
            }
        }

        return p;
    }
}
