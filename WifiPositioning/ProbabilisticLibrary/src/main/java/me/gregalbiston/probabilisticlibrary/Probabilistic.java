package me.gregalbiston.probabilisticlibrary;

import datastorage.KNNFloorPoint;
import general.AvgValue;
import general.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Probabilistic {

    private static final Logger logger = LoggerFactory.getLogger(Probabilistic.class);
             
    public static Point run(KNNFloorPoint onlinepoint, HashMap<String, KNNFloorPoint> offlineMap, int kLimit, int orientation) {

        Point point;

        if (!onlinepoint.getAttributes().isEmpty()) {
                         
            HashMap<String, KNNFloorPoint> orientatedMap = KNNFloorPoint.filterMap(offlineMap, orientation);            
            TreeSet<ProbResult> results = new TreeSet<>();
            
            Set<String> keys = orientatedMap.keySet();

            for (String key : keys) {
                KNNFloorPoint offlinepoint = orientatedMap.get(key);

                double prob = probabilite(onlinepoint, offlinepoint);
                results.add(new ProbResult(key, prob));
            }

            List<ProbResult> kList = new ArrayList<>();

            for (int i = 0; i < kLimit; i++) {
                kList.add(results.pollLast());
            }
            point = position(kList, orientatedMap);
        } else {
            point = new Point(-1, -1);
            logger.error("Online point is empty");
        }

        return point;
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
