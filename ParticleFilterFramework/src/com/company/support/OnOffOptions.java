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
 * @author SST3ALBISG
 */
public class OnOffOptions {
    
    boolean isBSSIDMerged;
    boolean isOrientationMerged;
    boolean isForceToOfflineMap;
    
    public OnOffOptions(boolean isBSSIDMerged, boolean isOrientationMerged, boolean isForceToOfflineMap) {        
        this.isBSSIDMerged = isBSSIDMerged;
        this.isOrientationMerged = isOrientationMerged;
        this.isForceToOfflineMap = isForceToOfflineMap;
    }

    public boolean isBSSIDMerged() {
        return isBSSIDMerged;
    }

    public boolean isOrientationMerged() {
        return isOrientationMerged;
    }       
    
    public boolean isForceToOfflineMap() {
        return isForceToOfflineMap;
    }
    
    public static List<OnOffOptions> allOptions(){
        
        List<OnOffOptions> grid = new ArrayList<>();
        
        grid.add(new OnOffOptions(false, false, false));
        grid.add(new OnOffOptions(true, false, false));
        grid.add(new OnOffOptions(false, true, false));
        grid.add(new OnOffOptions(false, false, true));
        grid.add(new OnOffOptions(true, true, false));
        grid.add(new OnOffOptions(true, false, true));
        grid.add(new OnOffOptions(false, true, true));
        grid.add(new OnOffOptions(true, true, true));            
        
        return grid;
    }
    
}
