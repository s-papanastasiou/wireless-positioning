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

    public ProbabilisticSettings(boolean BSSIDMerged, boolean orientationMerged, boolean forceToOfflineMap, int k, double buildingOrientation) {
        this.isBSSIDMerged = BSSIDMerged;
        this.isOrientationMerged = orientationMerged;
        this.K = k;        
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

    public boolean isForceToOfflineMap() {
        return isForceToOfflineMap;
    }

    public double getBuildingOrientation() {
        return buildingOrientation;
    }    

    public String getProbablisticTitle(String OUT_SEP) {
        return "probablistic-" + isBSSIDMerged + OUT_SEP + isOrientationMerged + OUT_SEP
                + K + OUT_SEP + isForceToOfflineMap;
    }

    public String getProbablisticImageTitle() {
        return "probablistic-" + isBSSIDMerged + "-" + isOrientationMerged + "-"
                + K + "-" + isForceToOfflineMap;
    }

    @Override
    public String toString() {
        return isBSSIDMerged + ":" + isOrientationMerged + ":" + K + ":" + isForceToOfflineMap + ":" + buildingOrientation;
    }

}