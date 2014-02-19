package com.example.Sensor;

import general.Point;

/**
 * Created with IntelliJ IDEA.
 * User: pierre
 * Date: 10/10/13
 * Time: 11:30
 * To change this template use File | Settings | File Templates.
 */

public class NavigationResults {

    public Point probabilisticPoint;
    public Point particlePoint;
    public Point inertialPoint;
    public Point bestPoint;
    public InertialData inertialData;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public NavigationResults(Point probabilisticPoint, Point particlePoint, Point inertialPoint, Point bestPoint, InertialData inertialData) {

        this.probabilisticPoint = probabilisticPoint;
        this.particlePoint = particlePoint;
        this.inertialPoint = inertialPoint;
        this.bestPoint = bestPoint;
        this.inertialData = inertialData;
    }

    public String toString() {

        String idResults = String.format("Prob Point : %s %s %s", Math.round(1000 * probabilisticPoint.getX()) * 0.001, Math.round(1000 * probabilisticPoint.getY()) * 0.001, System.getProperty("line.separator"));
        idResults += String.format("Part Point : %s %s %s", Math.round(1000 * particlePoint.getX()) * 0.001, Math.round(1000 * particlePoint.getY()) * 0.001, System.getProperty("line.separator"));
        idResults += String.format("Iner Point : %s %s %s", Math.round(1000 * inertialPoint.getX()) * 0.001, Math.round(1000 * inertialPoint.getY()) * 0.001, System.getProperty("line.separator"));
        idResults += String.format("Corr Point : %s %s %s", bestPoint.getX(), bestPoint.getY(), System.getProperty("line.separator"));
        idResults += String.format("Azimuth : %s %s", Math.round(inertialData.getAzimuth()), System.getProperty("line.separator"));
        idResults += String.format("Pitch : %s %s", Math.round(inertialData.getPitch()), System.getProperty("line.separator"));
        idResults += String.format("Roll : %s %s", Math.round(inertialData.getRoll()), System.getProperty("line.separator"));
        idResults += String.format("AccX : %s %s", inertialData.getAccelerationX(), System.getProperty("line.separator"));
        idResults += String.format("AccY : %s %s", inertialData.getAccelerationY(), System.getProperty("line.separator"));
        idResults += String.format("AccZ : %s %s", inertialData.getAccelerationZ(), System.getProperty("line.separator"));

        return idResults;

    }
}
