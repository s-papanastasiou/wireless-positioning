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
public class ProbabilisticTrial {

    private final boolean isBSSIDMerged;
    private final boolean isOrientationMerged;
    private final int K;    
    private final boolean isForceToOfflineMap;    
    private final String valuesStr;
    private final String titleStr;

    public ProbabilisticTrial(boolean BSSIDMerged, boolean orientationMerged, boolean forceToOfflineMap, int k, String OUT_SEP) {
        this.isBSSIDMerged = BSSIDMerged;
        this.isOrientationMerged = orientationMerged;
        this.K = k;        
        this.isForceToOfflineMap = forceToOfflineMap;        
        valuesStr = isBSSIDMerged + OUT_SEP + isOrientationMerged + OUT_SEP + isForceToOfflineMap + OUT_SEP + K ;
        titleStr = "probablistic-" + isBSSIDMerged + "-" + isOrientationMerged + "-" + isForceToOfflineMap + "-" + K;
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
     

    public String getTitle() {
        return titleStr;
    }
    
    public String getValues(){
        return valuesStr;
    }

    @Override
    public String toString() {
        return isBSSIDMerged + ":" + isOrientationMerged + ":" + isForceToOfflineMap + ":" + K;
    }

}