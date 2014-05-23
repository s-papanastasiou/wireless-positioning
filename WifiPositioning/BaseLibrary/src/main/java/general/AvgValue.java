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

    private double total;
    private double mean;
    private final List<Double> values = new ArrayList<>();
    private double stdDev;
    private double variance;

    /**
     * Constructor - zero value
     */
    public AvgValue() {
        total = 0;
        mean = 0;
        stdDev = 0;
        variance = 0;
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
        stdDev = 0;
        variance = 0;
    }

    /**
     * New value to be added for storage.
     *
     * @param value
     */
    public void add(final double value) {
        total += value;
        values.add(value);
        mean = (double) total / values.size();

        double meanDiff = calcMeanDiff();
        variance = meanDiff / values.size();
        stdDev = Math.sqrt(variance); //Square root of the total mean differences divided by the number of values
    }

    /**
     * Total sum of all the values stored.
     *
     * @return
     */
    public double getTotal() {
        return total;
    }

    /**
     * Arithmetic mean of all the values stored.
     *
     * @return
     */
    public double getMean() {
        return mean;
    }

    /**
     * Standard deviation of all the values stored.
     *
     * @return
     */
    public double getStdDev() {
        return stdDev;
    }

    /**
     * Variance of all the values stored.
     *
     * @return
     */
    public double getVariance() {
        return variance;
    }

    /**
     * Count of the values stored.
     *
     * @return
     */
    public int getFrequency() {
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
    private double calcMeanDiff() {
        double meanDiff = 0;

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
