/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package knnexecution;

import datastorage.Location;
import processing.DistanceMeasure;

/**
 *
 * @author Gerg
 */
public class KNNExecuteSettings {

    public int kValue;
    public DistanceMeasure distMeasure;
    public double varLimit = 0;
    public int varCount = 0;
    public boolean isVariance = false;

    public KNNExecuteSettings(int kValue, DistanceMeasure distMeasure) {
        this.kValue = kValue;
        this.distMeasure = distMeasure;
    }

    public KNNExecuteSettings(int kValue, DistanceMeasure distMeasure, double varLimit, int varCount) {
        this.kValue = kValue;
        this.distMeasure = distMeasure;
        this.varLimit = varLimit;
        this.varCount = varCount;
        this.isVariance = true;
    }

    public String print(String separator) {
        String message;
        if (isVariance) {
            message = kValue + separator + distMeasure + separator + varLimit + separator + varCount;
        } else {
            message = kValue + separator + distMeasure;
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
}
