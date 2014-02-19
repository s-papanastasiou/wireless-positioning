/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package processing;

import datastorage.ResultLocation;
import general.Point;
import datastorage.KNNFloorPoint;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import general.AvgValue;
import distancealgorithms.Chebyshev;
import distancealgorithms.Euclidian;
import distancealgorithms.EuclidianSquared;
import distancealgorithms.Manhatten;
import filehandling.RoomInfo;
import general.Locate;

/**
 *
 * @author Gerg
 */
public class Positioning {      

    public static Point locate(final KNNFloorPoint trialPoint, final HashMap<String, KNNFloorPoint> knnRadioMap, final HashMap<String, RoomInfo> roomInfo, final DistanceMeasure measure, int kLimit, boolean isBiggerBetter) {
        List<ResultLocation> results = estimate(trialPoint, knnRadioMap, measure);
        results = nearestEstimates(results, kLimit);

        return Locate.findWeightedCentre(results, roomInfo, isBiggerBetter);
    }

    public static Point locateVariance(final KNNFloorPoint trialPoint, final HashMap<String, KNNFloorPoint> knnRadioMap, final HashMap<String, RoomInfo> roomInfo, final DistanceMeasure measure, int kLimit, double varLimit, int varCount, boolean isBiggerBetter) {
        List<ResultLocation> results = estimate(trialPoint, knnRadioMap, measure);
        results = nearestVarianceEstimates(trialPoint, results, kLimit, varLimit, varCount, knnRadioMap);

        return Locate.findWeightedCentre(results, roomInfo, isBiggerBetter);
    }

    public static List<ResultLocation> estimate(final KNNFloorPoint trialPoint, final HashMap<String, KNNFloorPoint> knnRadioMap, final DistanceMeasure measure) {
        List<ResultLocation> results = new ArrayList<>();

        //Loop over each floor point in the radio map 
        Set<String> keys = knnRadioMap.keySet();
        for (String key : keys) {
            KNNFloorPoint floorPoint = knnRadioMap.get(key);
            ResultLocation result = calcDistance(trialPoint, floorPoint, measure);
            results.add(result);
        }

        //Sort the completed estimations from all BSSIDs into ascending order
        Collections.sort(results, new Comparator<ResultLocation>() {
            @Override
            public int compare(final ResultLocation object1, final ResultLocation object2) {
                return object1.compareTo(object2);
            }
        });

        return results;
    }

    public static List<ResultLocation> nearestEstimates(List<ResultLocation> resultLocations, int kLimit) {
        //Print out the k nearest estimates or all if less than k
        int kValue = kLimit > resultLocations.size() ? resultLocations.size() : kLimit;

        List<ResultLocation> shortList = new ArrayList<>();
        for (int counter = 0; counter < kValue; counter++) {
            shortList.add(resultLocations.get(counter));    //build the short list based on k value
        }

        return shortList;
    }

    public static List<ResultLocation> nearestVarianceEstimates(final KNNFloorPoint trialPoint, List<ResultLocation> resultLocations, int kLimit, double varLimit, int varCount, final HashMap<String, KNNFloorPoint> knnRadioMap) {
        //Print out the k nearest estimates or all if less than k
        //int kValue = kLimit > positionEstimates.size() ? positionEstimates.size() : kLimit;

        List<ResultLocation> shortList = new ArrayList<>();

        int counter = 0;
        while (shortList.size() < kLimit && counter < resultLocations.size()) {      //continue till have the kValue or unitl whole list interated over - differen to paper which only does the first kLimit of checks
            ResultLocation result = resultLocations.get(counter);

            KNNFloorPoint estimation = knnRadioMap.get(result.getRoomRef());
                                
            if (varianceCheck(trialPoint.getAttributes(), estimation.getAttributes(), varLimit, varCount)) {
                shortList.add(resultLocations.get(counter));
            }

            counter++;
        }

        //paper says to return original list if less than varCount values - not logical

        return shortList;
    }

    private static boolean varianceCheck(HashMap<String, AvgValue> trialAttributes, HashMap<String, AvgValue> estimateAttributes, double varLimit, int varCount) {

        final double RSSI_MAX = 100f;
        boolean isWithinVariance = true;
        
        Set<String> trialKeys = trialAttributes.keySet();

        int count = 0;
        for (String trialKey : trialKeys) {
            double estValue = RSSI_MAX;      //give a high score for points which are missing from the estimate but present in the scan point - only applies to RSSI

            if (estimateAttributes.containsKey(trialKey)) {
                estValue = estimateAttributes.get(trialKey).getMean();
            }

            double scanValue = trialAttributes.get(trialKey).getMean();

            if (Math.abs(estValue - scanValue) > varLimit) //check if the difference between values is greater than permitted variance
            {
                count++;
            }
        }

        if (count >= varCount) {            //count if more than the permitted number of attributes with variance have been found
            isWithinVariance = false;
        }

        return isWithinVariance;
    }

    private static ResultLocation calcDistance(final KNNFloorPoint trialPoint, final KNNFloorPoint floorPoint, final DistanceMeasure measure) {

        HashMap<String, AvgValue> trialAccessPoints = trialPoint.getAttributes();
        HashMap<String, AvgValue> floorAccessPoints = floorPoint.getAttributes();        

        double distance;

        switch (measure) {
            case Euclidian:
                distance = Euclidian.distance(trialAccessPoints, floorAccessPoints);
                break;
            case EuclidianSquared:
                distance = EuclidianSquared.distance(trialAccessPoints, floorAccessPoints);
                break;
            case Manhatten:
                distance = Manhatten.distance(trialAccessPoints, floorAccessPoints);
                break;
            case Chebyshev:
                distance = Chebyshev.distance(trialAccessPoints, floorAccessPoints);
                break;

            default:
                throw new AssertionError("Positioning:DistMeasure " + measure);

        }
        return new ResultLocation(floorPoint, distance, floorPoint.getRoomRef());
    }    
   
}
