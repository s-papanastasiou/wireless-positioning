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
    
    public static final int ERROR_VALUE = -1;
    public static final double ZERO_WEIGHT = 0.0001f; //nominal none zero value to prevent problems in inverse normalisation            

    /**
     * Finds the weighted centre of mass based on each pixel point and it's own
     * weight.
     *
     * @param positions List of positions to find centre.
     * @param isBiggerBetter True, if weight values further from zero are more
     * desirable.
     * @return
     */
 
    public static ResultPoint findWeightedCentre(final List<ResultLocation> positions, boolean isBiggerBetter) {        

        if (!positions.isEmpty()) {

            double xDraw = 0;
            double yDraw = 0;
            double xGlobal = 0;
            double yGlobal = 0;
            double totalWeight = 0;  //to normalise the values

            if (isBiggerBetter) {

                for (ResultLocation position : positions) {

                    Point draw = position.getDrawPoint();
                    Point global = position.getGlobalPoint();

                    double weight;

                    weight = position.getResult();
                    xDraw += draw.getX() * weight;
                    yDraw += draw.getY() * weight;
                    xGlobal += global.getX() * weight;
                    yGlobal += global.getY() * weight;
                    totalWeight += weight;

                }

            } else {
                //inversely normalise by finding the weight of all the items
                double startWeight = 0;   //equivalent to 1 by end

                for (ResultLocation position : positions) {
                    double weight = position.getResult();

                    //check for zero weights. if all are zero then will get zero result at end. 
                    //therefore adjust zero's to a nominal none zero value
                    //also for k=2 and one is zero weight then it would be equivalent to a k=1 test
                    if (weight == 0) {
                        startWeight += ZERO_WEIGHT;
                    } else {
                        startWeight += weight;
                    }
                }

                //Special condition when there is only a single point.
                if (positions.size() == 1) {
                    startWeight *= 2;
                }

                for (ResultLocation position : positions) {

                    Point draw = position.getDrawPoint();
                    Point global = position.getGlobalPoint();

                    double weight;
                    if (position.getResult() == 0) {
                        //weight = startWeight - zeroWeight;
                        weight = startWeight - ZERO_WEIGHT;
                    } else {
                        //weight = startWeight - position.getResult();  //subtract the current weight
                        double res = position.getResult();
                        weight = startWeight - res;
                    }

                    xDraw += draw.getX() * weight;
                    yDraw += draw.getY() * weight;
                    xGlobal += global.getX() * weight;
                    yGlobal += global.getY() * weight;
                    totalWeight += weight;
                }
            }

            //normalise the values by dividing by the total weight in the set
            Point draw = new Point(xDraw / totalWeight, yDraw / totalWeight);
            Point global = new Point(xGlobal / totalWeight, yGlobal / totalWeight);

            return new ResultPoint(draw, global);
        } else {
            return new ResultPoint(new Point(ERROR_VALUE, ERROR_VALUE), new Point(ERROR_VALUE, ERROR_VALUE));
        }

    }

    public static ResultPoint findInvertedWeightedCentre(final List<ResultLocation> positions, boolean isBiggerBetter) {

        double xDraw = 0.0;
        double yDraw = 0.0;
        double xGlobal = 0.0;
        double yGlobal = 0.0;
        double totalWeight = 0.0;

        for (ResultLocation pos : positions) {
            double weight;

            double result = pos.getResult();
            Point draw = pos.getDrawPoint();
            Point global = pos.getGlobalPoint();

            if (result == 0.0) {
                result = ZERO_WEIGHT;
            }

            if (isBiggerBetter) {
                result = 1 - result;
            }

            weight = 1 / result;
            xDraw += draw.getX() * weight;
            yDraw += draw.getY() * weight;
            
            xGlobal += global.getX() * weight;
            yGlobal += global.getY() * weight;

            totalWeight += weight;
        }
        Point resDraw = new Point(xDraw / totalWeight, yDraw / totalWeight);
        Point resGlobal = new Point(xGlobal / totalWeight, yGlobal / totalWeight);

        return new ResultPoint(resDraw, resGlobal);
    }

    /**
     * Finds the unweighted centre of mass based on each point. Points in the
     * same place will apply weighting.
     *
     * @param positions List of positions to find centre.
     * @return
     */
    public static ResultPoint findUnweightedCentre(final List<? extends Location> positions) {

        Point resDraw;
        Point resGlobal;
        if (!positions.isEmpty()) {

            double xDraw = 0;
            double yDraw = 0;

            double xGlobal = 0;
            double yGlobal = 0;

            for (Location position : positions) {

                Point draw = position.getDrawPoint();

                xDraw += draw.getX();
                yDraw += draw.getY();

                Point global = position.getGlobalPoint();

                xGlobal += global.getX();
                yGlobal += global.getY();
            }

            resDraw = new Point(xDraw / positions.size(), yDraw / positions.size());
            resGlobal = new Point(xGlobal / positions.size(), yGlobal / positions.size());
            return new ResultPoint(resDraw, resGlobal);
        } else {
            return new ResultPoint(new Point(ERROR_VALUE, ERROR_VALUE), new Point(ERROR_VALUE, ERROR_VALUE));
        }

    }

    public static ResultPoint findCentre(final List<ResultLocation> positions, boolean isBiggerBetter, LocateStyle locateStyle) {

        switch(locateStyle){
            case INVERTED:
                return findWeightedCentre(positions, isBiggerBetter);
            case UNWEIGHTED:
                return findUnweightedCentre(positions);
            default:
                return findInvertedWeightedCentre(positions, isBiggerBetter);                
        }               
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
