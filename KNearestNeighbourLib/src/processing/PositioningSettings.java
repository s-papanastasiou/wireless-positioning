/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package processing;


/**
 *
 * @author Gerg
 */
public class PositioningSettings {
    
    protected long timestamp;
    protected DistanceMeasure distMeasure;
    protected int kLimit;
    protected double varLimit;
    protected int varCount;
    protected boolean isVariance;

    public PositioningSettings(long timestamp, DistanceMeasure distMeasure, int kLimit, double varLimit, int varCount) {
        this.timestamp = timestamp;
        this.distMeasure = distMeasure;
        this.kLimit = kLimit;
        this.varLimit = varLimit;
        this.varCount = varCount;
        this.isVariance = true;
    }

    public PositioningSettings(long timestamp, DistanceMeasure distMeasure, int kLimit) {
        this.timestamp = timestamp;
        this.distMeasure = distMeasure;
        this.kLimit = kLimit;
        this.varLimit = 0f;
        this.varCount = 0;
        this.isVariance =false;
    }
    
    
    
}
