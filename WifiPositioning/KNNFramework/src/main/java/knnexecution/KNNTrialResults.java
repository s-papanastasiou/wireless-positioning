/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package knnexecution;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Gerg
 */
public class KNNTrialResults {

    private final Integer kValue;
    private final boolean isVariance;
    private final List<KNNPointResult> results = new ArrayList<>();
    private final String fieldSeparator;
    private Boolean isHeaderNeeded = true;
    private final String header;

    public KNNTrialResults(Integer kValue, Boolean isVariance, String fieldSeparator) {
        this.kValue = kValue;
        this.isVariance = isVariance;
        this.fieldSeparator = fieldSeparator;

        this.header = setHeading();
    }

    public void addResult(KNNPointResult result) {        
        results.add(result);
    }

    private String setHeading() {

        //output columns - trial location, trial co-ordinates, k value, distance measure, var limit, var count, final location, final co-ordinates, distance between trial and final, k actual {estimate position, estimate co-ordinates, estimate distance value}
        StringBuilder stb = new StringBuilder();
        stb.append("Trial Room").append(fieldSeparator).append("Trial XRef").append(fieldSeparator).append("Trial YRef").append(fieldSeparator).append("Trial WRef").append(fieldSeparator);
        stb.append("Trial X").append(fieldSeparator).append("Trial Y").append(fieldSeparator).append("K Value").append(fieldSeparator).append("Distance Measure").append(fieldSeparator);

        if (isVariance) {
            stb.append("Variance Limit").append(fieldSeparator).append("Variance Count").append(fieldSeparator);
        }

        stb.append("Final Room").append(fieldSeparator).append("Final XRef").append(fieldSeparator).append("Final YRef").append(fieldSeparator).append("Final WRef").append(fieldSeparator);
        stb.append("Final X").append(fieldSeparator).append("Final Y").append(fieldSeparator).append("Final to Trial Distance (Metres)").append(fieldSeparator).append("K Actual");
        for (int counter = 0; counter < kValue; counter++) {
            stb.append(fieldSeparator).append(counter).append(" Estimate Room").append(fieldSeparator).append(counter).append(" Estimate XRef").append(fieldSeparator).append(counter).append(" Estimate YRef").append(fieldSeparator).append(counter).append(" Estimate WRef").append(fieldSeparator);
            stb.append(counter).append(" Estimate X").append(fieldSeparator).append(counter).append(" Estimate Y").append(fieldSeparator);
            stb.append(counter).append(" Estimate Value").append(fieldSeparator).append(counter).append(" Estimate to Trial Distance (Metres)");
        }

        stb.append(System.lineSeparator());
        return stb.toString();
    }
    
    public void print(BufferedWriter writer) throws IOException{
        
        if(isHeaderNeeded){
            writer.write(header);
            isHeaderNeeded = false;
        }
        
        for(KNNPointResult result: results){
            writer.write(result.print(fieldSeparator));
        }
        
        //Flush the current set of results.
        writer.flush();
    }

}
