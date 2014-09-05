/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package general;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Average Value - stores a list of values and calculates their arithmetic mean.
 *
 * @author Greg Albiston
 */
public class AvgValue implements Serializable {

    private Double total;
    private Double mean;
    private final List<Double> values = new ArrayList<>();
    private Double max;
    private Double min;
    private Double stdDev;
    private Double variance;
    

    /**
     * Constructor - zero value
     */
    public AvgValue() {
        total = 0.0;
        mean = 0.0;
        stdDev = 0.0;
        variance = 0.0;
        max = 0.0;
        min = Double.MAX_VALUE;
    }        

    /**
     * Constructor
     *
     * @param value Initial value.
     */
    public AvgValue(final double value) {
        total = value;
        mean = value;
        values.add(value);
        stdDev = 0.0;
        variance = 0.0;
        max = value;
        min = value;
    }

    /**
     * New value to be added for storage.
     *
     * @param value
     */
    public void add(final double value) {
        total += value;
        values.add(value);
        mean = total / values.size();
        
        variance = calcMeanDiff() / values.size();
        stdDev = Math.sqrt(variance); //Square root of the total mean differences divided by the number of values
        
        if(value > max)
            max = value;
        
        if(value < min)
            min = value;
    }

    /**
     * Total sum of all the values stored.
     *
     * @return
     */
    public Double getTotal() {
        return total;
    }

    /**
     * Arithmetic mean of all the values stored.
     *
     * @return
     */
    public Double getMean() {
        return mean;
    }
    
    /**
     * Maximum value stored.
     * @return 
     */
    public Double getMax(){
        return max;
    }
    
    /**
     * Minimum value stored.
     * @return 
     */
    public Double getMin(){
        if(min.equals(Double.MAX_VALUE))
            return 0.0;
        else
            return min;
    }

    /**
     * Standard deviation of all the values stored.
     *
     * @return
     */
    public Double getStdDev() {
        return stdDev;
    }

    /**
     * Variance of all the values stored.
     *
     * @return
     */
    public Double getVariance() {
        return variance;
    }

    /**
     * Count of the values stored.
     *
     * @return
     */
    public Integer getFrequency() {
        return values.size();
    }

    /**
     * List of the values stored.
     *
     * @return
     */
    public List<Double> getValues() {
        return values;
    }

    /**
     * Calculates the mean difference between all the values. Used to find the
     * variance.
     *
     * @return
     */
    private Double calcMeanDiff() {
        Double meanDiff = 0.0;

        for (Double value : values) {
            meanDiff += Math.pow(value - mean, 2);
        }

        return meanDiff;
    }

    /**
     * String representation of the average value.
     *
     * @return
     */
    @Override
    public String toString() {
        return String.format("%s %s %s", mean, values.size(), stdDev);
    }
}
