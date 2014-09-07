/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package knnexecution;

import datastorage.Location;
import processing.DistanceMeasure;

/**
 *
 * @author Greg Albiston
 */
public class KNNExecuteSettings {

    public int kValue;
    public DistanceMeasure distMeasure;
    public double varLimit = 0;
    public int varCount = 0;
    public boolean isVariance = false;
    public String fieldSeparator;

    public KNNExecuteSettings(int kValue, DistanceMeasure distMeasure, String fieldSeparator) {
        this.kValue = kValue;
        this.distMeasure = distMeasure;
        this.fieldSeparator = fieldSeparator;
    }

    public KNNExecuteSettings(int kValue, DistanceMeasure distMeasure, String fieldSeparator, double varLimit, int varCount) {
        this.kValue = kValue;
        this.distMeasure = distMeasure;
        this.varLimit = varLimit;
        this.varCount = varCount;
        this.isVariance = true;
        this.fieldSeparator = fieldSeparator;
    }

    public String toString(String fieldSeparator) {
        
        String message = kValue + fieldSeparator + distMeasure;
        if (isVariance) {
            message += fieldSeparator + varLimit + fieldSeparator + varCount;
        }
        
        return message;
    }

    public String filename(Location trialLocation, String fileRoot, String fileExtension) {
        String filename;

        if (isVariance) {
            filename = String.format("%s-%s-%s-%s-%s-%s%s", fileRoot, trialLocation.getRoomRef(), kValue, distMeasure, varLimit, varCount, fileExtension);
        } else {
            filename = String.format("%s-%s-%s-%s%s", fileRoot, trialLocation.getRoomRef(), kValue, distMeasure, fileExtension);
        }

        return filename;
    }
    
    public String header(){
        return header(fieldSeparator, isVariance);
    }
    
    public static String header(String fieldSeparator, boolean isVariance){
        StringBuilder stb = new StringBuilder();
        stb.append("K Value").append(fieldSeparator).append("Distance Measure");

        if (isVariance) {
            stb.append(fieldSeparator).append("Variance Limit").append(fieldSeparator).append("Variance Count");
        }
        
        return stb.toString();
    }
}
