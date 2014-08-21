/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package general;

import datastorage.Location;
import datastorage.ResultLocation;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * Finds the centre point of a list of points.
 *
 * @author Greg Albiston
 * @see http://mamikon.com/USArticles/EasyCentroids.pdf
 */
public class Locate {

    public final static int ERROR_VALUE = -1;

    /**
     * Finds the weighted centre of mass based on each pixel point and it's own
     * weight.
     *
     * @param positions List of positions to find centre.     
     * @param isBiggerBetter True, if weight values further from zero are more
     * desirable.
     * @return
     */
    public static Point findWeightedCentre(final List<ResultLocation> positions, boolean isBiggerBetter) {

        Point result = new Point();

        if (!positions.isEmpty()) {

            final double zeroWeight = 0.0001f; //nominal none zero value to prevent problems in inverse normalisation
            double x = 0;
            double y = 0;
            double totalWeight = 0;  //to normalise the values
            double startWeight = 0;   //used when inversely normalising - equivalent to 1 by end

            if (!isBiggerBetter) {
                //inversely normalise by finding the weight of all the items

                for (ResultLocation position : positions) {
                    double weight = position.getResult();

                    //check for zero weights. if all are zero then will get zero result at end. 
                    //therefore adjust zero's to a nominal none zero value
                    //also for k=2 and one is zero weight then it would be equivalent to a k=1 test
                    if (weight == 0) {
                        startWeight += zeroWeight;
                    } else {
                        startWeight += weight;
                    }
                }
            }

            for (ResultLocation position : positions) {

                Point pos = position.getDrawPoint();

                double weight;

                if (isBiggerBetter) {
                    weight = position.getResult();
                    x += pos.getX() * weight;
                    y += pos.getY() * weight;
                    totalWeight += weight;

                } else {

                    if (position.getResult() == 0) {
                        weight = startWeight - zeroWeight;
                    } else {
                        weight = startWeight - position.getResult();  //subtract the current weight
                    }

                    x += pos.getX() * weight;
                    y += pos.getY() * weight;
                    totalWeight += weight;
                }
            }

            //normalise the values by dividing by the total weight in the set
            result.setX(x / totalWeight);
            result.setY(y / totalWeight);

        } else {
            result.setX(ERROR_VALUE);
            result.setY(ERROR_VALUE);
        }

        return result;
    }
    
    /**
     * Finds the unweighted centre of mass based on each point. Points in the
     * same place will apply weighting.
     *
     * @param positions List of positions to find centre.     
     * @return
     */
    public static Point findUnweightedCentre(final List<? extends Location> positions) {

        Point result = new Point();

        if (!positions.isEmpty()) {

            double x = 0;
            double y = 0;

            for (Location position : positions) {

                Point pos = position.getDrawPoint();

                x += pos.getX();
                y += pos.getY();
            }
            result.setX(x / positions.size());
            result.setY(y / positions.size());
        } else {
            result.setX(-1);
            result.setY(-1);
        }
        return result;
    }

    /**
     * Forces an estimated position into the closest known (surveyed) position
     * in the provided offline map.
     *
     * @param offlineMap Map of known points.
     * @param estimatedLocation Estimated location to be adjusted.
     * @param forceToOfflineMap True, if forcing to offline map. False, will
     * return estimated position.
     * @return
     */
    public static Location forceToMap(HashMap<String, ? extends Location> offlineMap, Location estimatedLocation, boolean forceToOfflineMap) {
        Location bestLocation = new Location();
        if (forceToOfflineMap) {
            double lowestDistance = Double.MAX_VALUE;
            Set<String> keys = offlineMap.keySet();
            for (String key : keys) {
                Location location = offlineMap.get(key);
                double distance = location.distance(estimatedLocation);
                if (distance < lowestDistance) {
                    bestLocation = location;
                    lowestDistance = distance;
                }
            }
        } else {
            bestLocation = estimatedLocation;
        }
        return bestLocation;
    }        

}
