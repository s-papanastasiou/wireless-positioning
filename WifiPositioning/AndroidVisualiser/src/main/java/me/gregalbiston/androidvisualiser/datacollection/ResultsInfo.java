package me.gregalbiston.androidvisualiser.datacollection;

import datastorage.Location;
import general.Point;
import general.ResultPoint;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Greg Albiston
 * Date: 03/08/13
 * Time: 13:00
 * Stores the screen points for display and log message.
 * Returned by RSSISanner and MagneticScanner function processResults
 * Parameter for PointsResults function add.
 */
public class ResultsInfo {
    public final Point screenPoint;
    public final ResultPoint finalPoint;
    public final List<? extends Location> estimatePoints;
    public final String message;

    public ResultsInfo(Point screenPoint, ResultPoint finalPoint, List<? extends Location> estimatePoints, String message) {
        this.screenPoint = screenPoint;
        this.finalPoint = finalPoint;
        this.estimatePoints = estimatePoints;
        this.message = message;
    }
}
