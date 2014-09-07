package distancealgorithms;

import datastorage.KNNFloorPoint;
import datastorage.Location;
import datastorage.ResultLocation;
import datastorage.RoomInfo;
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

    public static Location run(KNNFloorPoint onlinepoint, HashMap<String, KNNFloorPoint> offlineMap, HashMap<String, RoomInfo> roomInfo, int kLimit, int orientation) {

        Location location;

        if (!onlinepoint.getAttributes().isEmpty()) {

            HashMap<String, KNNFloorPoint> orientatedMap = KNNFloorPoint.filterMap(offlineMap, orientation);
            TreeSet<ResultLocation> results = new TreeSet<>();

            Set<String> keys = orientatedMap.keySet();

            for (String key : keys) {
                KNNFloorPoint offlinepoint = orientatedMap.get(key);

                double prob = probabilite(onlinepoint, offlinepoint);
                results.add(new ResultLocation(offlinepoint, prob, offlinepoint.getRoomRef()));
            }

            List<ResultLocation> kList = new ArrayList<>();

            for (int i = 0; i < kLimit; i++) {
                kList.add(results.pollLast());
            }
            Point point = position(kList);
            location = RoomInfo.searchGlobalLocation(point, roomInfo);
        } else {
            location = new Location();
            logger.error("Online point is empty");
        }

        return location;
    }

    private static Point position(List<ResultLocation> kList) {
        double x = 0;
        double y = 0;
        double sum = 0;

        for (ResultLocation result : kList) {
            x += result.getResult() * result.getGlobalX();
            y += result.getResult() * result.getGlobalY();
            sum += result.getResult();
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

    //Distance equations based on http://www.statsoft.com/textbook/k-nearest-neighbors/
    /**
     * @author Greg Albiston
     * @param trialAttributes
     * @param offlineAttributes
     * @return
     */
    public static double distance(final HashMap<String, AvgValue> trialAttributes, final HashMap<String, AvgValue> offlineAttributes) {

        final double NO_MATCH = Math.exp(-100);
        final double sqrPI = Math.sqrt(2 * Math.PI);
        double probTotal = 0.0;

        Set<String> keys = trialAttributes.keySet();

        for (String key : keys) {
            if (offlineAttributes.containsKey(key)) {
                AvgValue offline = offlineAttributes.get(key);
                AvgValue trial = trialAttributes.get(key);

                double diff = trial.getMean() - offline.getMean();
                double sqrDiff = Math.pow(diff, 2);

                if (offline.getVariance() == 0) { //No variance so only square difference has an effect
                    probTotal += Math.exp(-sqrDiff);
                } else {
                    double a = 1 / (offline.getStdDev() * sqrPI);

                    double b;

                    if (sqrDiff != 0) { //zero difference means result is exponent 1
                        b = Math.exp(-sqrDiff / (2 * offline.getVariance()));
                    } else {
                        b = 1;
                    }
                    double prob = (a * b);
                    probTotal += prob;
                }                

            } else { //poor distance given. otherwise points would be rewards for lacking data detected by the trial point.
                probTotal += NO_MATCH;
            }
        }

        double distance = probTotal / keys.size();
        return distance;
    }
}
