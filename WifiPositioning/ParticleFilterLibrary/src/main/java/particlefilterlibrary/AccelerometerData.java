package particlefilterlibrary;

/**
 * Created with IntelliJ IDEA.
 * User: pierre
 * Date: 04/11/2013
 * Time: 13:17
 * To change this template use File | Settings | File Templates.
 */
public class AccelerometerData {

    private final Long timestamp;
    private final float[] orientation = new float[3];
    private final float[] linearAcceleration = new float[3];
    private final float[] invertedMatrix = new float[16];

    public AccelerometerData(Long timestamp, float azimuth, float roll, float pitch, float linearAccelerationX,
                float linearAccelerationY, float linearAccelerationZ, float invertedMatrix0, float invertedMatrix1,
                float invertedMatrix2, float invertedMatrix3, float invertedMatrix4, float invertedMatrix5,
                float invertedMatrix6, float invertedMatrix7, float invertedMatrix8, float invertedMatrix9,
                float invertedMatrix10, float invertedMatrix11, float invertedMatrix12, float invertedMatrix13,
                float invertedMatrix14, float invertedMatrix15) {

        this.timestamp = timestamp;
        this.orientation[0] = azimuth;
        this.orientation[1] = roll;
        this.orientation[2] = pitch;
        this.linearAcceleration[0] = linearAccelerationX;
        this.linearAcceleration[1] = linearAccelerationY;
        this.linearAcceleration[2] = linearAccelerationZ;
        this.invertedMatrix[0] = invertedMatrix0;
        this.invertedMatrix[1] = invertedMatrix1;
        this.invertedMatrix[2] = invertedMatrix2;
        this.invertedMatrix[3] = invertedMatrix3;
        this.invertedMatrix[4] = invertedMatrix4;
        this.invertedMatrix[5] = invertedMatrix5;
        this.invertedMatrix[6] = invertedMatrix6;
        this.invertedMatrix[7] = invertedMatrix7;
        this.invertedMatrix[8] = invertedMatrix8;
        this.invertedMatrix[9] = invertedMatrix9;
        this.invertedMatrix[10] = invertedMatrix10;
        this.invertedMatrix[11] = invertedMatrix11;
        this.invertedMatrix[12] = invertedMatrix12;
        this.invertedMatrix[13] = invertedMatrix13;
        this.invertedMatrix[14] = invertedMatrix14;
        this.invertedMatrix[15] = invertedMatrix15;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public float[] getOrientation() {
        return orientation;
    }

    public float[] getLinearAcceleration() {
        return linearAcceleration;
    }

    public float[] getInvertedMatrix() {
        return invertedMatrix;
    }
}
