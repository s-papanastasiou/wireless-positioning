package androidparticlefilter;

import java.util.EnumMap;
import particlefilterlibrary.Threshold;

/**
 * All settings used by the application.
 * 
 * @author Pierre Rousseau
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
      
    /**
     * Constructor
     * 
     * @param BSSIDMerged
     * @param orientationMerged
     * @param k
     * @param initRSSIReadings
     * @param particleCount
     * @param isForceToOfflineMap
     * @param buildingOrientation
     * @param cloudDisplacement
     * @param cloudRange
     * @param jitterOffset
     * @param accelerationOffset
     * @param speedBreak
     * @param boundaries
     * @param particleCreation 
     */
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

    /**
     * Force the result to be a point on the offline map.
     * 
     * @return 
     */
    public boolean isForceToOfflineMap() {
        return isForceToOfflineMap;
    }

    /**
     * Number of particles to create at startup and retain during each sampling.
     * @return 
     */
    public int getParticleCount() {
        return particleCount;
    }

    /**
     * K-nearest neighbour value.
     * @return 
     */
    public int getK() {
        return K;
    }

    /**
     * Orientation of building in radians.
     * @return 
     */
    public double getBuildingOrientation() {
        return buildingOrientation;
    }

    /**
     * Merge BSSIDs at the same location if the first five hex pairs match, instead of all six hex pairs.
     * Ignores the last set of hex pairs.
     * @return 
     */
    public boolean isBSSIDMerged() {
        return isBSSIDMerged;
    }

    /**
     * Merge different orientation readings at the same location into a single value.
     * @return 
     */
    public boolean isOrientationMerged() {
        return isOrientationMerged;
    }

    /**
     * Number of RSSI readings to take into account when providing initial position.
     * @return 
     */
    public int getInitRSSIReadings() {
        return initRSSIReadings;
    }

    /**
     * High pass filter value to compensate for hand jitter.
     * @return 
     */
    public Double getJitterOffset() {
        return jitterOffset;
    }

    /**
     * High pass filter value to prevent drift when stationary.
     * @return 
     */
    public Float[] getAccelerationOffset() {
        return accelerationOffset;
    }

    /**
     * Maximum number of samples an inertial point can be moved.
     * @return 
     */
    public int getSpeedBreak() {
        return speedBreak;
    }

    /**
     * Maximum range cloud can travel.
     * @return 
     */
    public double getCloudRange() {
        return cloudRange;
    }

    /**
     * Maximum displacement of the cloud.
     * @return 
     */
    public double getCloudDisplacement() {
        return cloudDisplacement;
    }

    /**
     * Particle weight boundaries.
     * Used to determine the range of each threshold that determine the number of new particles.
     * @return 
     */
    public EnumMap<Threshold, Float> getBoundaries() {
        return boundaries;
    }

    /**
     * Particle weight creation.
     * Used to determine the number of particles created in each threshold.
     * @return 
     */
    public EnumMap<Threshold, Integer> getParticleCreation() {
        return particleCreation;
    }
    
    
}