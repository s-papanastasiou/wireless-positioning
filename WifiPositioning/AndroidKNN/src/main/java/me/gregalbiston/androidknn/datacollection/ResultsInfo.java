package me.gregalbiston.androidknn.datacollection;

import general.Point;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Gerg
 * Date: 03/08/13
 * Time: 13:00
 * Stores the screen points for display and log message.
 * Returned by RSSISanner and MagneticScanner function processResults
 * Parameter for PointsResults function add.
 */
public class ResultsInfo {
    public Point screenPoint;
    public Point finalPoint;
    public List<Point> estimatePoints;
    public String message;

    public ResultsInfo(Point screenPoint, Point finalPoint, List<Point> estimatePoints, String message) {
        this.screenPoint = screenPoint;
        this.finalPoint = finalPoint;
        this.estimatePoints = estimatePoints;
        this.message = message;
    }
}
