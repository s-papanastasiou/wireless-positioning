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
public class SettingsGenerator {

    //Ranges
    private static final int initRSSIReadings_MIN = 1;
    private static final int initRSSIReadings_MAX = 10;
    private static final int initRSSIReadings_INC = 1;

    private static final int particleCount_MIN = 10;
    private static final int particleCount_MAX = 200;
    private static final int particleCount_INC = 10;

    private static final int speedBreak_MIN = 10;
    private static final int speedBreak_MAX = 200;
    private static final int speedBreak_INC = 10;

    private static final double cloudRange_MIN = 0.01;
    private static final double cloudRange_MAX = 1.00;
    private static final double cloudRange_INC = 0.05;

    private static final double cloudDisplacementCoefficient_MIN = 0.1;
    private static final double cloudDisplacementCoefficient_MAX = 1.0;
    private static final double cloudDisplacementCoefficient_INC = 0.1;

    public static List<ParticleSettings> particle(boolean isBSSIDMerged, boolean isOrientationMerged, boolean isForcedToOfflineMap, int kValue, double buildingOrientation) {

        List<ParticleSettings> settings = new ArrayList<>();

        for (int init_counter = initRSSIReadings_MIN; init_counter <= initRSSIReadings_MAX; init_counter += initRSSIReadings_INC) {
            for (int particle_counter = particleCount_MIN; particle_counter <= particleCount_MAX; particle_counter += particleCount_INC) {
                for (int speed_counter = speedBreak_MIN; speed_counter <= speedBreak_MAX; speed_counter += speedBreak_INC) {
                    for (double range_counter = cloudRange_MIN; range_counter <= cloudRange_MAX; range_counter += cloudRange_INC) {
                        for (double displacement_counter = cloudDisplacementCoefficient_MIN; displacement_counter <= cloudDisplacementCoefficient_MAX; displacement_counter += cloudDisplacementCoefficient_INC) {
                            ParticleSettings setting = new ParticleSettings(isBSSIDMerged, isOrientationMerged, isForcedToOfflineMap, kValue, init_counter, particle_counter, speed_counter, range_counter, displacement_counter, buildingOrientation);
                            settings.add(setting);
                            //System.out.println(setting.toString());
                        }
                    }
                }
            }
        }

        return settings;
    }

    public static List<ProbabilisticSettings> probablisitic(boolean isBSSIDMerged, boolean isOrientationMerged, boolean isForcedToOfflineMap, int kValue, double buildingOrientation) {

        List<ProbabilisticSettings> settings = new ArrayList<>();

        for (int init_counter = initRSSIReadings_MIN; init_counter <= initRSSIReadings_MAX; init_counter += initRSSIReadings_INC) {
            for (int speed_counter = speedBreak_MIN; speed_counter <= speedBreak_MAX; speed_counter += speedBreak_INC) {
                ProbabilisticSettings setting = new ProbabilisticSettings(isBSSIDMerged, isOrientationMerged, isForcedToOfflineMap, kValue, init_counter, speed_counter, buildingOrientation);
                settings.add(setting);
                //System.out.println(setting.toString());
            }
        }

        return settings;
    }

}
