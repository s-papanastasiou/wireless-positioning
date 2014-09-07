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
                outcome = roomReference.compareTo(resLocation.roomReference);                 
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
        return super.toString() + " " + roomReference + " " + result;
    }

}
