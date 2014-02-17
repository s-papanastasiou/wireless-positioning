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
public class ParticleTrial {

    private final boolean isBSSIDMerged;
    private final boolean isOrientationMerged;
    private final int K;
    private final int initRSSIReadings;
    private final int particleCount;
    private final int speedBreak;
    private final double cloudRange;
    private final double cloudDisplacementCoefficient;
    private final boolean isForceToOfflineMap;      
    private final String valuesStr;
    private final String titleStr;
    

    public ParticleTrial(boolean BSSIDMerged, boolean orientationMerged, boolean forceToOfflineMap, int k, int initRSSIReadings, int particleCount, int speedBreak, double cloudRange, double cloudDisplacementCoefficient, String OUT_SEP) {
        this.isBSSIDMerged = BSSIDMerged;
        this.isOrientationMerged = orientationMerged;
        this.K = k;
        this.initRSSIReadings = initRSSIReadings;
        this.particleCount = particleCount;
        this.speedBreak = speedBreak;
        this.cloudRange = cloudRange;
        this.cloudDisplacementCoefficient = cloudDisplacementCoefficient;
        this.isForceToOfflineMap = forceToOfflineMap;             
        this.valuesStr = isBSSIDMerged + OUT_SEP + isOrientationMerged + OUT_SEP + isForceToOfflineMap + OUT_SEP
                + K + OUT_SEP + initRSSIReadings + OUT_SEP
                + particleCount + OUT_SEP + speedBreak + OUT_SEP + cloudRange + OUT_SEP
                + cloudDisplacementCoefficient;
        this.titleStr = "particle-" + isBSSIDMerged + "-" + isOrientationMerged + "-" + isForceToOfflineMap + "-"
                + K + "-" + initRSSIReadings + "-"
                + particleCount + "-" + speedBreak + "-" + cloudRange + "-"
                + cloudDisplacementCoefficient;
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

    public String getTitle() {
        return titleStr;
    }
    
    public String getValues(){
        return valuesStr;
    }    

    @Override
    public String toString() {
        return isBSSIDMerged + ":" + isOrientationMerged + ":" + isForceToOfflineMap + ":" + K + ":" + initRSSIReadings + ":" + particleCount + ":" + speedBreak + ":" + cloudRange + ":" + cloudDisplacementCoefficient;
    }
    
    public static List<ParticleTrial> generate(boolean isBSSIDMerged, boolean isOrientationMerged, boolean isForcedToOfflineMap, int kValue, TrialProperties tp, String OUT_SEP) {

        List<ParticleTrial> settings = new ArrayList<>();

        for (int init_counter = tp.InitRSSIReadings_MIN(); init_counter <= tp.InitRSSIReadings_MAX(); init_counter += tp.InitRSSIReadings_INC()) {
            for (int particle_counter = tp.ParticleCount_MIN(); particle_counter <= tp.ParticleCount_MAX(); particle_counter += tp.ParticleCount_INC()) {
                for (int speed_counter = tp.SpeedBreak_MIN(); speed_counter <= tp.SpeedBreak_MAX(); speed_counter += tp.SpeedBreak_INC()) {
                    for (double range_counter = tp.CloudRange_MIN(); range_counter <= tp.CloudRange_MAX(); range_counter += tp.CloudRange_INC()) {
                        for (double displacement_counter = tp.CloudDispCoeff_MIN(); displacement_counter <= tp.CloudDispCoeff_MAX(); displacement_counter += tp.CloudDispCoeff_INC()) {
                            ParticleTrial setting = new ParticleTrial(isBSSIDMerged, isOrientationMerged, isForcedToOfflineMap, kValue, init_counter, particle_counter, speed_counter, range_counter, displacement_counter, OUT_SEP);
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