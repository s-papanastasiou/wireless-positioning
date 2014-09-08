/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package knnexecution;

import general.AvgValue;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Gerg
 */
public class KNNTrialResults {

    public final KNNExecuteSettings executeSettings;
    private final String trialName;
    private final List<KNNPointResult> results = new ArrayList<>();
    public final String fieldSeparator;
    private final String header;

    public KNNTrialResults(KNNExecuteSettings executeSettings, String trialName) {
        this.executeSettings = executeSettings;
        this.trialName = trialName;
        this.fieldSeparator = executeSettings.fieldSeparator;

        this.header = setHeading();
    }

    public void addResult(KNNPointResult result) {
        results.add(result);
    }

    private String setHeading() {

        //output columns - trial location, trial co-ordinates, k value, distance measure, var limit, var count, final location, final co-ordinates, distance between trial and final, k actual {estimate position, estimate co-ordinates, estimate distance value}
        StringBuilder stb = new StringBuilder();
        stb.append("Trial Room").append(fieldSeparator).append("Trial XRef").append(fieldSeparator).append("Trial YRef").append(fieldSeparator).append("Trial WRef").append(fieldSeparator);
        stb.append("Trial X").append(fieldSeparator).append("Trial Y").append(fieldSeparator).append(executeSettings.header()).append(fieldSeparator);

        stb.append("Final Room").append(fieldSeparator).append("Final XRef").append(fieldSeparator).append("Final YRef").append(fieldSeparator).append("Final WRef").append(fieldSeparator);
        stb.append("Final X").append(fieldSeparator).append("Final Y").append(fieldSeparator).append("Final to Trial Distance (Metres)").append(fieldSeparator).append("K Actual");

        for (int counter = 0; counter < executeSettings.kValue; counter++) {
            stb.append(fieldSeparator).append(counter).append(" Estimate Room").append(fieldSeparator).append(counter).append(" Estimate XRef").append(fieldSeparator).append(counter).append(" Estimate YRef").append(fieldSeparator).append(counter).append(" Estimate WRef").append(fieldSeparator);
            stb.append(counter).append(" Estimate X").append(fieldSeparator).append(counter).append(" Estimate Y").append(fieldSeparator);
            stb.append(counter).append(" Estimate Value").append(fieldSeparator).append(counter).append(" Estimate to Trial Distance (Metres)");
        }

        stb.append(System.lineSeparator());
        return stb.toString();
    }

    public void printResults(BufferedWriter writer) throws IOException {

        writer.write(header);

        for (KNNPointResult result : results) {
            writer.write(result.print(fieldSeparator));
        }

        //Flush the current set of results.
        writer.flush();
    }

    public static void printSummaryHeading(BufferedWriter writer, String fieldSeparator, Boolean isVariance) throws IOException {
        StringBuilder stb = new StringBuilder();
        stb.append("Trial Name").append(fieldSeparator).append(KNNExecuteSettings.header(fieldSeparator, isVariance)).append(fieldSeparator);
        stb.append(AvgValue.header(fieldSeparator));
        stb.append(System.lineSeparator());
        writer.write(stb.toString());
    }

    public void printSummary(BufferedWriter writer) throws IOException {

        AvgValue value = new AvgValue();
        for (KNNPointResult result : results) {            
            value.add(result.getMetreDistance());
        }
        StringBuilder stb = new StringBuilder();
        stb.append(trialName).append(fieldSeparator);
        stb.append(executeSettings.toString(fieldSeparator)).append(fieldSeparator).append(value.toString(fieldSeparator));
        stb.append(System.lineSeparator());
        
        writer.write(stb.toString());
        //Flush the current set of results.
        writer.flush();
    }

}
