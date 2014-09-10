/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package knnexecution;

import datastorage.Location;
import datastorage.ResultLocation;
import java.util.List;

/**
 *
 * @author Gerg
 */
public class KNNPointResult {
    
    private final Location trialLocation;
    private final Location finalLocation;
    private final List<ResultLocation> positionEstimates;
    private final Double metreDistance;
    private final KNNExecuteSettings executeSettings;

    public KNNPointResult(Location trialLocation, Location finalLocation, List<ResultLocation> positionEstimates, Double metreDistance, KNNExecuteSettings executeSettings) {
        this.trialLocation = trialLocation;
        this.finalLocation = finalLocation;
        this.positionEstimates = positionEstimates;
        this.metreDistance = metreDistance;
        this.executeSettings = executeSettings;        
    }

    public Location getTrialLocation() {
        return trialLocation;
    }

    public Location getFinalLocation() {
        return finalLocation;
    }

    public List<ResultLocation> getPositionEstimates() {
        return positionEstimates;
    }

    public Double getMetreDistance() {
        return metreDistance;
    }

    public KNNExecuteSettings getExecuteSettings() {
        return executeSettings;
    }           
    
    public String print(String fieldSeparator){
        StringBuilder stb = new StringBuilder();
        
        //trial location, trial co-ordinates       
        stb.append(trialLocation.toString(fieldSeparator)).append(fieldSeparator).append(trialLocation.getDrawPoint().toString(fieldSeparator)).append(fieldSeparator);       
        //k value, distance measure, var limit, var count
        stb.append(executeSettings.toString(fieldSeparator)).append(fieldSeparator);
           
        //final location, final co-ordinates   
        stb.append(finalLocation.toString(fieldSeparator)).append(fieldSeparator).append(finalLocation.getDrawPoint().toString(fieldSeparator)).append(fieldSeparator);
        //distance from final to trial, number of position estimates
        stb.append(metreDistance).append(fieldSeparator).append(positionEstimates.size());

        //output all the estimates - no estimates then no output
        for (ResultLocation positionEstimate : positionEstimates) {            
            // estimate position, estimate co-ordinates, estimate distance value
            stb.append(fieldSeparator).append(positionEstimate.toString(fieldSeparator)).append(fieldSeparator).append(positionEstimate.getDrawPoint().toString(fieldSeparator));
            stb.append(fieldSeparator).append(positionEstimate.getResult()).append(fieldSeparator).append(trialLocation.distance(positionEstimate));
        }
                       
        //start next line
        stb.append(System.lineSeparator());
        
        return stb.toString();
    }
    
}
