package me.gregalbiston.probabilisticlibrary;

/**
 * Created with IntelliJ IDEA.
 * User: pierre
 * Date: 17/09/13
 * Time: 11:11
 * To change this template use File | Settings | File Templates.
 */

public class ProbResult implements Comparable {

    protected String key;
    protected double value;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public ProbResult(String key, double value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public int compareTo(Object o) throws NullPointerException, ClassCastException {
        int result = 1;

        if (o instanceof ProbResult) {
            ProbResult probResult = (ProbResult) o;

            if (this.value < probResult.value) {
                result = -1;
            } else if (this.value > probResult.value) {
                result = 1;
            } else {
                if (this.key.equals(probResult.key))
                    result = 0;
            }
        } else if (o == null) {
            throw new NullPointerException("Null not supported");
        } else {
            throw new ClassCastException("Comparison not supported");
        }

        return result;
    }
}
