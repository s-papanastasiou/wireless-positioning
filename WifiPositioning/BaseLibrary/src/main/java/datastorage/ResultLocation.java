/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package datastorage;

/**
 * Results for a specific location.
 *
 * @author Greg Albiston
 */
public class ResultLocation extends Location implements Comparable<ResultLocation> {

    private final double result;
    private final double variance;
    private final String roomReference;

    /**
     * Constructor.
     *
     * @param location Location of the result.
     * @param result Value of the result.
     */
    public ResultLocation(Location location, double result) {
        super(location);
        this.result = result;
        this.variance = 0.0;
        this.roomReference = location.getRoomRef();
    }

    /**
     * Constructor.
     *
     * @param location Location of the result.
     * @param result Value of the result.
     * @param variance Variance of the offline point.
     */
    public ResultLocation(Location location, double result, double variance) {
        super(location);
        this.result = result;
        this.variance = variance;
        this.roomReference = location.getRoomRef();
    }

    /**
     * Constructor Room reference allows the retention of the full room
     * reference for subclasses of Location.
     *
     * @param location Location of the result.
     * @param result Value of the result.
     * @param roomReference Room reference for the result.
     */
    public ResultLocation(Location location, double result, String roomReference) {
        super(location);
        this.result = result;
        this.variance = 0.0;
        this.roomReference = roomReference;
    }

    /**
     * Constructor Room reference allows the retention of the full room
     * reference for subclasses of Location.
     *
     * @param location Location of the result.
     * @param result Value of the result.
     * @param roomReference Room reference for the result.
     * @param variance Variance of the offline point.
     */
    public ResultLocation(Location location, double result, String roomReference, double variance) {
        super(location);
        this.result = result;
        this.variance = variance;
        this.roomReference = roomReference;
    }

    /**
     * Get result value.
     *
     * @return
     */
    public double getResult() {
        return result;
    }

    /**
     * Get variance value.
     *
     * @return
     */
    public double getVariance() {
        return variance;
    }

    /**
     * Get room reference.
     *
     * @return
     */
    @Override
    public String getRoomRef() {
        return roomReference;
    }

    /**
     * Comparable implementation.
     *
     * @param resLocation Result location to compare against.
     * @return
     */
    @Override
    public int compareTo(ResultLocation resLocation) {

        int outcome = -1;

        if (resLocation != null) {
            if (this.result > resLocation.result) {
                outcome = 1;
            } else if (this.result < resLocation.result) {
                outcome = -1;
            } else {
                if (this.variance > resLocation.variance) {
                    outcome = 1;
                } else if (this.variance < resLocation.variance) {
                    outcome = -1;
                } else {
                    outcome = roomReference.compareTo(resLocation.roomReference);
                }
            }

        } else {
            throw new NullPointerException("Null passed for Result Location comparison");
        }

        return outcome;
    }

    /**
     * String representation of the result location.
     *
     * @return
     */
    @Override
    public String toString() {
        return super.toString() + " " + roomReference + " " + result + " " + variance;
    }

}
