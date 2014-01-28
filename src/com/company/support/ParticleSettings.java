/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.company.support;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Gerg
 */
public class ParticleSettings {

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

    ParticleSettings(boolean BSSIDMerged, boolean orientationMerged, boolean forceToOfflineMap, int k, int initRSSIReadings, int particleCount, int speedBreak, double cloudRange, double cloudDisplacementCoefficient, double buildingOrientation) {
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
        return "particle-" + isBSSIDMerged + OUT_SEP + isOrientationMerged + OUT_SEP + isForceToOfflineMap + OUT_SEP
                + K + OUT_SEP + initRSSIReadings + OUT_SEP
                + particleCount + OUT_SEP + speedBreak + OUT_SEP + cloudRange + OUT_SEP
                + cloudDisplacementCoefficient ;
    }

    public String getParticleImageTitle() {
        return "particle-" + isBSSIDMerged + "-" + isOrientationMerged + "-" + isForceToOfflineMap + "-"
                + K + "-" + initRSSIReadings + "-"
                + particleCount + "-" + speedBreak + "-" + cloudRange + "-"
                + cloudDisplacementCoefficient;
    }

    @Override
    public String toString() {
        return isBSSIDMerged + ":" + isOrientationMerged + ":" + isForceToOfflineMap + ":" + K + ":" + initRSSIReadings + ":" + particleCount + ":" + speedBreak + ":" + cloudRange + ":" + cloudDisplacementCoefficient + ":" + buildingOrientation;
    }
    
    public static List<ParticleSettings> generate(boolean isBSSIDMerged, boolean isOrientationMerged, boolean isForcedToOfflineMap, int kValue, TrialProperties tp) {

        List<ParticleSettings> settings = new ArrayList<>();

        for (int init_counter = tp.getInitRSSIReadings_MIN(); init_counter <= tp.getInitRSSIReadings_MAX(); init_counter += tp.getInitRSSIReadings_INC()) {
            for (int particle_counter = tp.getParticleCount_MIN(); particle_counter <= tp.getParticleCount_MAX(); particle_counter += tp.getParticleCount_INC()) {
                for (int speed_counter = tp.getSpeedBreak_MIN(); speed_counter <= tp.getSpeedBreak_MAX(); speed_counter += tp.getSpeedBreak_INC()) {
                    for (double range_counter = tp.getCloudRange_MIN(); range_counter <= tp.getCloudRange_MAX(); range_counter += tp.getCloudRange_INC()) {
                        for (double displacement_counter = tp.getCloudDispCoeff_MIN(); displacement_counter <= tp.getCloudDispCoeff_MAX(); displacement_counter += tp.getCloudDispCoeff_INC()) {
                            ParticleSettings setting = new ParticleSettings(isBSSIDMerged, isOrientationMerged, isForcedToOfflineMap, kValue, init_counter, particle_counter, speed_counter, range_counter, displacement_counter, tp.getBuildingOrientation());
                            settings.add(setting);
                            //System.out.println(setting.toString());
                        }
                    }
                }
            }
        }

        return settings;
    }

}