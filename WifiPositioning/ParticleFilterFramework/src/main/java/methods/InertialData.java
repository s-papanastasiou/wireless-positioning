package methods;

/**
 * Created with IntelliJ IDEA.
 * User: pierre
 * Date: 03/10/13
 * Time: 14:53
 * To change this template use File | Settings | File Templates.
 */

public class InertialData {

    private final static double DEG_IN_CIRCLE = 360;
    private final static double RAD2DEG = 180.0 / Math.PI;
    
    public final static double HALF_PI = Math.PI / 2;
    public static int NO_ORIENTATION = -1;    

    private Double accelerationX = 0.0;
    private Double accelerationY = 0.0;
    private Double accelerationZ = 0.0;
    private Double azimuth = 0.0;
    private Double pitch = 0.0;
    private Double roll = 0.0;


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public InertialData() {
    }

    public InertialData(double accelerationX, double accelerationY, double accelerationZ, double azimuth, double pitch, double roll) {

        this.accelerationX = accelerationX;
        this.accelerationY = accelerationY;
        this.accelerationZ = accelerationZ;
        this.azimuth = azimuth;
        this.pitch = pitch;
        this.roll = roll;
    }

    @Override
    public String toString() {

        return String.format("accX:%s;accY:%s;accZ:%s;azi:%s;pit:%s;roll:%s", accelerationX, accelerationY, accelerationZ, azimuth, pitch, roll);
    }

    public static InertialData getDatas(float[] iR, float[] mLinearAcceleration, float[] orientation, Double buildingOrientation, Double jitterOffset, Float[] accelerationOffset) {

        float aX, aY, aZ, trueX, trueY, trueZ;

        aX = mLinearAcceleration[0] * iR[0] + mLinearAcceleration[1] * iR[1] + mLinearAcceleration[2] * iR[2] + accelerationOffset[0];
        aY = mLinearAcceleration[0] * iR[4] + mLinearAcceleration[1] * iR[5] + mLinearAcceleration[2] * iR[6] + accelerationOffset[1];
        aZ = mLinearAcceleration[0] * iR[8] + mLinearAcceleration[1] * iR[9] + mLinearAcceleration[2] * iR[10] + accelerationOffset[2];

        trueX = (float) (0.01 * Math.floor(100 * (aY * Math.sin(buildingOrientation) - aX * Math.cos(buildingOrientation))));
        trueY = (float) (0.01 * Math.floor(100 * (aX * Math.sin(buildingOrientation) + aY * Math.cos(buildingOrientation))));
        trueZ = (float) (0.01 * Math.floor(100 * (-aZ)));

        //compensate for small non-zero values when tablet is stationary
        if ((trueX < jitterOffset) && (trueX > -jitterOffset))
            trueX = 0;
        if ((trueY < jitterOffset) && (trueY > -jitterOffset))
            trueY = 0;
        if ((trueZ < jitterOffset) && (trueZ > -jitterOffset))
            trueZ = 0;

        return new InertialData(trueX, trueY, trueZ, degrees360(orientation[0] - buildingOrientation + HALF_PI), -orientation[2] * RAD2DEG, -orientation[1] * RAD2DEG);
    }

    public static double degrees360(double azimuth) {

        if (azimuth < 0)
            azimuth = (azimuth * RAD2DEG) + DEG_IN_CIRCLE;
        else
            azimuth = azimuth * RAD2DEG;

        return azimuth;
    }

    // Is useful if we want to pick only the orientated point of the offlineMap
    public static int getOrientation(boolean isOrientationMerged, double azimuth, double buildingOrientation) {

        int orientation = NO_ORIENTATION;

        if (!isOrientationMerged) {

            double trueOrientation = degrees360(azimuth);// - buildingOrientation + HALF_PI);

            if (trueOrientation > 360)
                trueOrientation = trueOrientation - 360;

            if ((trueOrientation > 315) || (trueOrientation <= 45))
                orientation = 1;
            else if ((trueOrientation > 45) && (trueOrientation <= 135))
                orientation = 2;
            else if ((trueOrientation > 135) && (trueOrientation <= 225))
                orientation = 3;
            else if ((trueOrientation > 225) && (trueOrientation <= 315))
                orientation = 4;
        }

        return orientation;
    }

    public Double getAccelerationX() {
        return accelerationX;
    }

    public Double getAccelerationY() {
        return accelerationY;
    }

    public Double getAccelerationZ() {
        return accelerationZ;
    }

    public Double getAzimuth() {
        return azimuth;
    }

    public Double getPitch() {
        return pitch;
    }

    public Double getRoll() {
        return roll;
    }
}
