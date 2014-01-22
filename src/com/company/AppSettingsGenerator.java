/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.company;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Gerg
 */
public class AppSettingsGenerator {
    
    //Ranges
    private static final int K_MIN=1;
    private static final int K_MAX=20;    
    private static final int K_INC=1;
    
    private static final int initRSSIReadings_MIN=1;    
    private static final int initRSSIReadings_MAX=10;    
    private static final int initRSSIReadings_INC=1;
    
    private static final int particleCount_MIN=5;
    private static final int particleCount_MAX=200;
    private static final int particleCount_INC=5;
        
    private static final int speedBreak_MIN=5;
    private static final int speedBreak_MAX=200;
    private static final int speedBreak_INC=5;
    
    private static final double cloudRange_MIN=0.01;
    private static final double cloudRange_MAX=1.00;
    private static final double cloudRange_INC=0.05;
    
    private static final double cloudDisplacementCoefficient_MIN=0.1;
    private static final double cloudDisplacementCoefficient_MAX=1.0;
    private static final double cloudDisplacementCoefficient_INC=0.1;       
    
    private static final double buildingOrientation = -0.523598776;
    
    public static final List<AppSettings> generate(){
        
        List<AppSettings> settings= new ArrayList<>();
        
        settings.addAll(subGen(false, false, false));
        settings.addAll(subGen(true, false, false));
        settings.addAll(subGen(false, true, false));
        settings.addAll(subGen(false, false, true));
        settings.addAll(subGen(true, true, false));
        settings.addAll(subGen(true, false, true));        
        settings.addAll(subGen(false, true, true));
        settings.addAll(subGen(true, true, true));
                
        return settings;
    }
    
    private static List<AppSettings> subGen(boolean isBSSIDMerged, boolean isOrientationMerged, boolean isForcedToOfflineMap){
        
        List<AppSettings> settings= new ArrayList<>();
                      
        for(int k_counter = K_MIN; k_counter<=K_MAX; k_counter+=K_INC){
            for(int init_counter = initRSSIReadings_MIN; init_counter <= initRSSIReadings_MAX; init_counter += initRSSIReadings_INC){
                for(int particle_counter = particleCount_MIN; particle_counter <= particleCount_MAX; particle_counter += particleCount_INC){
                    for(int speed_counter = speedBreak_MIN; speed_counter <= speedBreak_MAX; speed_counter += speedBreak_INC){
                        for(double range_counter = cloudRange_MIN; range_counter <= cloudRange_MAX; range_counter += cloudRange_INC){
                            for(double displacement_counter = cloudDisplacementCoefficient_MIN; displacement_counter <= cloudDisplacementCoefficient_MAX; displacement_counter += cloudDisplacementCoefficient_INC){
                                AppSettings setting = new AppSettings(isBSSIDMerged, isOrientationMerged, k_counter, init_counter, particle_counter, speed_counter, range_counter, displacement_counter, isForcedToOfflineMap, buildingOrientation);
                                settings.add(setting);
                            }
                        }
                    }
                }
            }            
        }        
        return settings;
    }                
}
