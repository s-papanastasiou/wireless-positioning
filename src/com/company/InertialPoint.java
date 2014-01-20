package com.company;

import general.Point;

/**
 * Created with IntelliJ IDEA.
 * User: pierre
 * Date: 22/10/13
 * Time: 10:46
 * To change this template use File | Settings | File Templates.
 */

public class InertialPoint {

    private Double x = 0.0;
    private Double y = 0.0;
    private Double vX = 0.0;
    private Double vY = 0.0;
    private InertialData inertialData = new InertialData();
    private Long t = System.nanoTime();
    private int count = 0;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public InertialPoint(Point point) {
        this.x = point.getX();
        this.y = point.getY();
    }

    public InertialPoint(Point point, Long t){
        this.x = point.getX();
        this.y = point.getY();
        this.t = t;
    }
    public InertialPoint() {
    }

    public String toString() {

        return String.format("x:%s;y:%s;vX:%s;vY:%s;t:%s;%s", x, y, vX, vY, t, inertialData.toString());
    }

    public static InertialPoint move(InertialPoint inertialPoint, InertialData inertialData, long timestamp, int speedBreak) {

        InertialPoint currentInertialPoint;
        if (inertialPoint == null)
            currentInertialPoint = new InertialPoint();
        else
            currentInertialPoint = inertialPoint;

        Double[] resultsX, resultsY;

        if (currentInertialPoint.count < speedBreak) {
            resultsX = inerNav(currentInertialPoint.t, timestamp, currentInertialPoint.inertialData.getAccelerationX(), inertialData.getAccelerationX(), currentInertialPoint.x, currentInertialPoint.vX);
            resultsY = inerNav(currentInertialPoint.t, timestamp, currentInertialPoint.inertialData.getAccelerationY(), inertialData.getAccelerationY(), currentInertialPoint.y, currentInertialPoint.vY);
            currentInertialPoint.count++;
        } else {
            resultsX = inerNav(currentInertialPoint.t, timestamp, currentInertialPoint.inertialData.getAccelerationX(), inertialData.getAccelerationX(), currentInertialPoint.x, 0.0);
            resultsY = inerNav(currentInertialPoint.t, timestamp, currentInertialPoint.inertialData.getAccelerationY(), inertialData.getAccelerationY(), currentInertialPoint.y, 0.0);
            currentInertialPoint.count = 0;
        }

        currentInertialPoint.x = resultsX[0];
        currentInertialPoint.y = resultsY[0];
        currentInertialPoint.vX = resultsX[1];
        currentInertialPoint.vY = resultsY[1];
        currentInertialPoint.inertialData = inertialData;
        currentInertialPoint.t = timestamp;

        return currentInertialPoint;
    }

    public Point getPoint() {
        return new Point(x, y);
    }

    public InertialData getInertialData() {
        return inertialData;
    }

    public Double getX() {
        return x;
    }

    public Double getY() {
        return y;
    }

    public Long getTimestamp(){
        return t;
    }

    public void setTimestamp(Long t) {
        this.t = t;
    }

    private static Double[] inerNav(long t1, long t2, Double at1, Double at2, Double prevCoordinate, Double prevVelocity) {

        Double A, B, C;

        /*
        if (at2 > 0) {
            Logging.printLine("test");
        } */

        //FIXME
        final double SEC_ADJUST = 1000000000.0;
        double tDiff = (t2 - t1) / SEC_ADJUST;

        double newT1 = 0;
        double newT2 = tDiff;

        A = (at2 - at1) / tDiff;
        B = ((at1 * newT2) - (at2 * newT1)) / tDiff;
        C = prevCoordinate - (prevVelocity * newT1) + ((B / 2.0) * Math.pow(newT1, 2)) + ((A / 3.0) * Math.pow(newT1, 3));


        Double x = ((A / 6.0) * Math.pow(newT2, 3)) + ((B / 2.0) * Math.pow(newT2, 2)) + (newT2 * ((prevVelocity - (A / 2.0) * Math.pow(newT1, 2)) - (B * newT1))) + C;
        Double V = prevVelocity + ((A / 2.0) * ((Math.pow(newT2, 2) - Math.pow(newT1, 2))) + B * tDiff);

        return new Double[]{x, V};
    }

}
