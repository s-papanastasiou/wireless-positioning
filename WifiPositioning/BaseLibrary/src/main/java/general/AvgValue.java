/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package general;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Gerg
 */
public class AvgValue implements Serializable {

    //class holding the average value information for the location
    //designed around COUNT principle for efficiency
    private double total;
    private double mean;
    private List<Double> values = new ArrayList<>();
    private double stdDev;
    private double variance;

    public AvgValue(){
        total = 0;
        mean = 0;        
        stdDev = 0;
        variance = 0;
    }
    
    public AvgValue(final double value) {
        total = value;
        mean = value;
        values.add(value);
        stdDev = 0;
        variance = 0;
    }

    public void add(final double value) {
        total += value;
        values.add(value);
        mean = (double) total / values.size();

        double meanDiff = calcMeanDiff();
        variance = meanDiff / values.size();
        stdDev = Math.sqrt(variance); //Square root of the total mean differences divided by the number of values
    }

    public double getTotal() {
        return total;
    }

    public double getMean() {
        return mean;
    }

    public double getStdDev() {
        return stdDev;
    }
    
    public double getVariance(){
        return variance;
    }
    
    public int getFrequency(){
        return values.size();
    }

    private double calcMeanDiff() {
        double meanDiff = 0;

        for (Double value : values) {
            meanDiff += Math.pow(value - mean, 2);
        }

        return meanDiff;
    }
    
    @Override
    public String toString(){
        return String.format("%s %s %s", mean, values.size(), stdDev);
    }
}

