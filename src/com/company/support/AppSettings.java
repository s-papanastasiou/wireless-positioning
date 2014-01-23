package com.company.support;

/**
 * Created with IntelliJ IDEA. User: pierre Date: 31/10/2013 Time: 15:17 To
 * change this template use File | Settings | File Templates.
 */
public class AppSettings {

    private final boolean isBSSIDMerged;
    private final boolean isOrientationMerged;
    private final int K;
    private final int initRSSIReadings;
    private final int particleCount;
    private final int speedBreak;
    private final double cloudRange;
    private final double cloudDisplacementCoefficient;
    private final boolean isForceToOfflineMap;
    private final double buildingOrientation;

    public AppSettings(boolean BSSIDMerged, boolean orientationMerged, int k, int initRSSIReadings, int particleCount, int speedBreak, double cloudRange, double cloudDisplacementCoefficient, boolean forceToOfflineMap, double buildingOrientation) {
        this.isBSSIDMerged = BSSIDMerged;
        this.isOrientationMerged = orientationMerged;
        this.K = k;
        this.initRSSIReadings = initRSSIReadings;
        this.particleCount = particleCount;
        this.speedBreak = speedBreak;
        this.cloudRange = cloudRange;
        this.cloudDisplacementCoefficient = cloudDisplacementCoefficient;
        this.isForceToOfflineMap = forceToOfflineMap;
        this.buildingOrientation = buildingOrientation;

    }

    public boolean isBSSIDMerged() {
        return isBSSIDMerged;
    }

    public boolean isOrientationMerged() {
        return isOrientationMerged;
    }

    public int getK() {
        return K;
    }

    public int getInitRSSIReadings() {
        return initRSSIReadings;
    }

    public int getParticleCount() {
        return particleCount;
    }

    public int getSpeedBreak() {
        return speedBreak;
    }

    public double getCloudRange() {
        return cloudRange;
    }

    public double getCloudDisplacementCoefficient() {
        return cloudDisplacementCoefficient;
    }

    public boolean isForceToOfflineMap() {
        return isForceToOfflineMap;
    }

    public double getBuildingOrientation() {
        return buildingOrientation;
    }

    public String getParticleTitle(String OUT_SEP) {
        return "particle-" + isBSSIDMerged + OUT_SEP + isOrientationMerged + OUT_SEP
                + K + OUT_SEP + initRSSIReadings + OUT_SEP
                + particleCount + OUT_SEP + speedBreak + OUT_SEP + cloudRange + OUT_SEP
                + cloudDisplacementCoefficient + OUT_SEP + isForceToOfflineMap;
    }

    public String getParticleImageTitle() {
        return "particle-" + isBSSIDMerged + "-" + isOrientationMerged + "-"
                + K + "-" + initRSSIReadings + "-"
                + particleCount + "-" + speedBreak + "-" + cloudRange + "-"
                + cloudDisplacementCoefficient + "-" + isForceToOfflineMap;
    }

    public String getProbablisticTitle(String OUT_SEP) {
        return "probablistic-" + isBSSIDMerged + OUT_SEP + isOrientationMerged + OUT_SEP
                + K + OUT_SEP + isForceToOfflineMap;
    }

    public String getProbablisticImageTitle() {
        return "probablistic-" + isBSSIDMerged + "-" + isOrientationMerged + "-"
                + K + "-" + isForceToOfflineMap;
    }

    @Override
    public String toString() {
        return isBSSIDMerged + ":" + isOrientationMerged + ":" + K + ":" + initRSSIReadings + ":" + particleCount + ":" + speedBreak + ":" + cloudRange + ":" + cloudDisplacementCoefficient + ":" + isForceToOfflineMap + ":" + buildingOrientation;
    }

}
