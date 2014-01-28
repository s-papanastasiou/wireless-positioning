/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.company.support;

/**
 *
 * @author Gerg
 */
public class ProbabilisticSettings {

    private final boolean isBSSIDMerged;
    private final boolean isOrientationMerged;
    private final int K;    
    private final boolean isForceToOfflineMap;
    private final double buildingOrientation;
    private final String OUT_SEP;
    private final String valuesStr;
    private final String titleStr;

    public ProbabilisticSettings(boolean BSSIDMerged, boolean orientationMerged, boolean forceToOfflineMap, int k, double buildingOrientation, String OUT_SEP) {
        this.isBSSIDMerged = BSSIDMerged;
        this.isOrientationMerged = orientationMerged;
        this.K = k;        
        this.isForceToOfflineMap = forceToOfflineMap;
        this.buildingOrientation = buildingOrientation;
        this.OUT_SEP = OUT_SEP;  
        valuesStr = isBSSIDMerged + OUT_SEP + isOrientationMerged + OUT_SEP + K + OUT_SEP + isForceToOfflineMap;
        titleStr = "probablistic-" + isBSSIDMerged + "-" + isOrientationMerged + "-" + K + "-" + isForceToOfflineMap;
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

    public boolean isForceToOfflineMap() {
        return isForceToOfflineMap;
    }

    public double getBuildingOrientation() {
        return buildingOrientation;
    }    

    public String getTitle() {
        return titleStr;
    }
    
    public String getValues(){
        return valuesStr;
    }

    public String getOUT_SEP() {
        return OUT_SEP;
    }        

    @Override
    public String toString() {
        return isBSSIDMerged + ":" + isOrientationMerged + ":" + K + ":" + isForceToOfflineMap + ":" + buildingOrientation;
    }

}