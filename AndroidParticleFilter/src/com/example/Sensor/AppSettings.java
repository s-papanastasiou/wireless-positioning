package com.example.Sensor;

/**
 * Created with IntelliJ IDEA.
 * User: pierre
 * Date: 31/10/2013
 * Time: 14:48
 * To change this template use File | Settings | File Templates.
 */
public class AppSettings {

    private boolean isBSSIDMerged;
    private boolean isOrientationMerged;
    private int K;
    private int initRSSIReadings;
    private int particleCount;
    private boolean isForceToOfflineMap;
    private double buildingOrientation;

    public AppSettings(boolean BSSIDMerged, boolean orientationMerged, int k, int initRSSIReadings, int particleCount, boolean isForceToOfflineMap, double buildingOrientation) {
        this.isBSSIDMerged = BSSIDMerged;
        this.isOrientationMerged = orientationMerged;
        this.K = k;
        this.initRSSIReadings = initRSSIReadings;
        this.particleCount = particleCount;
        this.isForceToOfflineMap = isForceToOfflineMap;
        this.buildingOrientation = buildingOrientation;
    }

    public boolean isForceToOfflineMap() {
        return isForceToOfflineMap;
    }

    public int getParticleCount() {
        return particleCount;
    }

    public int getK() {
        return K;
    }

    public double getBuildingOrientation() {
        return buildingOrientation;
    }

    public boolean isBSSIDMerged() {
        return isBSSIDMerged;
    }

    public boolean isOrientationMerged() {
        return isOrientationMerged;
    }

    public int getInitRSSIReadings() {
        return initRSSIReadings;
    }
}