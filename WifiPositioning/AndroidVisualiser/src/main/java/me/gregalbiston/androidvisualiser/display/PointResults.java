package me.gregalbiston.androidvisualiser.display;

import android.graphics.*;
import datastorage.Location;
import general.Point;
import java.util.ArrayList;
import java.util.List;
import me.gregalbiston.androidvisualiser.datacollection.ResultsInfo;

/**
 * Created with IntelliJ IDEA.
 * User: Greg Albiston
 * Date: 22/07/13
 * Time: 23:49
 * Draws the scan, final and estimation points.
 */
public class PointResults {

    private List<Point> scanPoints = new ArrayList<>();
    private List<Point> finalPoints = new ArrayList<>();

    private List<? extends Location> estimates = new ArrayList<>();

    private final Paint scanPaint = new Paint();
    private final Paint finalPaint = new Paint();
    private final Paint estimatePaint = new Paint();

    private boolean isShowEstimates = false;
    private boolean isShowPath = true;

    private final float SIZE = 4f;
    private final int STROKE_WIDTH = 5;


    private boolean isNew = true;

    public PointResults() {
        this(false, true, Color.GREEN, Color.RED, Color.YELLOW);
    }

    public PointResults(boolean isShowEstimates, boolean isShowPath, int colourFinal, int colourScan, int colourEstimates) {
        this.isShowEstimates = isShowEstimates;
        this.isShowPath = isShowPath;

        finalPaint.setColor(colourFinal);
        finalPaint.setStyle(Paint.Style.STROKE);
        finalPaint.setStrokeWidth(STROKE_WIDTH);

        scanPaint.setColor(colourScan);
        scanPaint.setStyle(Paint.Style.STROKE);
        scanPaint.setStrokeWidth(STROKE_WIDTH);

        estimatePaint.setColor(colourEstimates);

    }

    public void setColours(int colourFinal, int colourScan, int colourEstimates) {
        finalPaint.setColor(colourFinal);
        scanPaint.setColor(colourScan);
        estimatePaint.setColor(colourEstimates);
    }

    public void add(ResultsInfo results) {
        scanPoints.add(results.screenPoint);
        finalPoints.add(results.finalPoint);
        isNew = false;

        this.estimates = results.estimatePoints;
    }

    public void newRoute() {
        isNew = true;
        estimates = new ArrayList<>();

        scanPoints = new ArrayList<>();
        finalPoints = new ArrayList<>();
    }

    public void showEstimates(boolean isShowEstimates) {
        this.isShowEstimates = isShowEstimates;
    }

    public void showPath(boolean isShowPath) {
        this.isShowPath = isShowPath;
    }

    public void draw(Canvas canvas) {

        if (isShowPath) {

            //scan lines and points
            drawLines(canvas, scanPoints, scanPaint);
            drawPoints(canvas, scanPoints, scanPaint);

            //final lines and points
            drawLines(canvas, finalPoints, finalPaint);
            drawPoints(canvas, finalPoints, finalPaint);

        }

        if (isShowEstimates) {
            drawEstimates(canvas);
        }

        if (!isNew) {
            drawPoint(canvas, scanPoints.get(scanPoints.size() - 1), scanPaint);
            drawPoint(canvas, finalPoints.get(finalPoints.size() - 1), finalPaint);
        }
    }

    //draws points but not the last point in the list
    private void drawPoints(Canvas canvas, List<Point> points, Paint paint) {
        for (int counter = 0; counter < points.size() - 1; counter++) {
            Point cPoint = points.get(counter);   //current point

            RectF rect = new RectF(cPoint.getXfl() - SIZE, cPoint.getYfl() - SIZE, cPoint.getXfl() + SIZE, cPoint.getYfl() + SIZE);
            canvas.drawRect(rect, paint);
        }
    }

    //draws lines between points in the list
    private void drawLines(Canvas canvas, List<Point> points, Paint paint) {
        for (int counter = 0; counter < points.size() - 1; counter++) {
            Point cPoint = points.get(counter);   //current point
            Point nPoint = points.get(counter + 1);   //next point

            canvas.drawLine(cPoint.getXfl(), cPoint.getYfl(), nPoint.getXfl(), nPoint.getYfl(), paint);
        }
    }

    private void drawEstimates(Canvas canvas) {
        for (Location estimate : estimates) {
            Point point = estimate.getDrawPoint();
            RectF rect = new RectF(point.getXfl() - SIZE, point.getYfl() - SIZE, point.getXfl() + SIZE, point.getYfl() + SIZE);
            canvas.drawOval(rect, estimatePaint);
        }
    }

    private void drawPoint(Canvas canvas, Point point, Paint paint) {
        RectF rect = new RectF(point.getXfl() - SIZE, point.getYfl() - SIZE, point.getXfl() + SIZE, point.getYfl() + SIZE);
        canvas.drawOval(rect, paint);
    }

}
