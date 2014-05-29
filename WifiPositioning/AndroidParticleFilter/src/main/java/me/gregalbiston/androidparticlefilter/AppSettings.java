package me.gregalbiston.androidparticlefilter;

import java.util.EnumMap;
import particlefilterlibrary.Threshold;

/**
 * Created with IntelliJ IDEA.
 * User: pierre
 * Date: 31/10/2013
 * Time: 14:48
 * To change this template use File | Settings | File Templates.
 */
public class AppSettings {

    private final boolean isBSSIDMerged;
    private final boolean isOrientationMerged;
    private final int K;
    private final int initRSSIReadings;
    private final int particleCount;
    private final boolean isForceToOfflineMap;
    private final double buildingOrientation;
    
    private final Double jitterOffset;
    private final Float[] accelerationOffset;
    private final int speedBreak;
    
    private final double cloudDisplacement;
    private final double cloudRange;    
    private final EnumMap<Threshold, Float> boundaries;
    private final EnumMap<Threshold, Integer> particleCreation;
        
    public AppSettings(boolean BSSIDMerged, boolean orientationMerged, int k, int initRSSIReadings, int particleCount, boolean isForceToOfflineMap, double buildingOrientation, double cloudDisplacement, double cloudRange, Double jitterOffset, Float[] accelerationOffset, int speedBreak, EnumMap<Threshold, Float> boundaries, EnumMap<Threshold, Integer> particleCreation) {
        this.isBSSIDMerged = BSSIDMerged;
        this.isOrientationMerged = orientationMerged;
        this.K = k;
        this.initRSSIReadings = initRSSIReadings;
        this.particleCount = particleCount;
        this.isForceToOfflineMap = isForceToOfflineMap;
        this.buildingOrientation = buildingOrientation;
        this.jitterOffset = jitterOffset;
        this.accelerationOffset = accelerationOffset;
        this.speedBreak = speedBreak;
        this.cloudDisplacement = cloudDisplacement;
        this.cloudRange = cloudRange;        
        this.boundaries = boundaries;
        this.particleCreation = particleCreation;
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

    public Double getJitterOffset() {
        return jitterOffset;
    }

    public Float[] getAccelerationOffset() {
        return accelerationOffset;
    }

    public int getSpeedBreak() {
        return speedBreak;
    }

    public double getCloudRange() {
        return cloudRange;
    }

    public double getCloudDisplacement() {
        return cloudDisplacement;
    }

    public EnumMap<Threshold, Float> getBoundaries() {
        return boundaries;
    }

    public EnumMap<Threshold, Integer> getParticleCreation() {
        return particleCreation;
    }
    
    
}