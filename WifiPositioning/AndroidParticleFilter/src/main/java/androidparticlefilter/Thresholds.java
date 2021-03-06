/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package androidparticlefilter;

import java.util.EnumMap;
import particlefilterlibrary.Threshold;

/**
 * Default values for Thresholds.
 * @author Greg Albiston
 */
public class Thresholds {

    /**
     * Particle weight boundaries.
     * Used to determine the range of each threshold that determine the number of new particles.
     * @return 
     */
    public static EnumMap<Threshold, Float> boundaries() {
        EnumMap<Threshold, Float> boundaries = new EnumMap<>(Threshold.class);
        boundaries.put(Threshold.UPPER, 0.75f);
        boundaries.put(Threshold.MID, 0.5f);
        boundaries.put(Threshold.LOWER, 0.25f);
        boundaries.put(Threshold.BASE, 0f);
        return boundaries;
    }
    
    /**
     * Particle weight creation.
     * Used to determine the number of particles created in each threshold.
     * @return 
     */
    public static EnumMap<Threshold, Integer> particleCreation() {
        EnumMap<Threshold, Integer> particleCreation = new EnumMap<>(Threshold.class);
        particleCreation.put(Threshold.UPPER, 4);
        particleCreation.put(Threshold.MID, 3);
        particleCreation.put(Threshold.LOWER, 2);
        particleCreation.put(Threshold.BASE, 1);        
        return particleCreation;
    }
}
