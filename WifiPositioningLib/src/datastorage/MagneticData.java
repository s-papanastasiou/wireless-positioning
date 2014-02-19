/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package datastorage;

import general.TimeStamp;
import java.text.ParseException;

/**
 *
 * @author Gerg
 */
public class MagneticData  extends Location {
       
    private final String timestamp;        
    private final double xValue;
    private final double yValue;
    private final double zValue;
    private final int sensorAccuracy;    
    
    public static final String[] HEADINGS = {"Timestamp", "Room", "X Ref", "Y Ref", "W Ref", "X Value", "Y Value", "Z Value", "Accuracy"};
    
    public static final String X_Key = "X";
    public static final String Y_Key = "Y";
    public static final String Z_Key = "Z";
        
    public MagneticData(final String[] parts) throws ParseException
    {
        super(parts[1], Integer.parseInt(parts[2]), Integer.parseInt(parts[3]), Integer.parseInt(parts[4]));  
        this.timestamp =  parts[0];  
        this.xValue = Double.parseDouble(parts[5]);
        this.yValue = Double.parseDouble(parts[6]);
        this.zValue = Double.parseDouble(parts[7]);
        this.sensorAccuracy = Integer.parseInt(parts[8]);
    }   
    
    public MagneticData(long timestamp, Location location, double xValue, double yValue, double zValue, int sensorAccuracy){
        super(location);        
        this.timestamp =  TimeStamp.formatDateTime(timestamp);
        this.xValue = xValue;
        this.yValue = yValue;
        this.zValue = zValue;
        this.sensorAccuracy = sensorAccuracy;
    } 

    public String getTimestamp() {
        return timestamp;
    }      

    public double getxValue() {
        return xValue;
    }

    public double getyValue() {
        return yValue;
    }

    public double getzValue() {
        return zValue;
    }

    public double getSensorAccuracy() {
        return sensorAccuracy;
    }
    
    @Override
    public String toString(final String sep){                
        return timestamp + sep + super.toString(sep) + sep + xValue + sep + yValue + sep + zValue + sep + sensorAccuracy;        
    }
    
    @Override
    public String toString(final String sep, final int accuracy){                
        return timestamp + sep + super.toString(sep, accuracy) + sep + xValue + sep + yValue + sep + zValue + sep + sensorAccuracy;        
    }
    
    public static String toStringHeadings(final String sep){
        
        String result =HEADINGS[0];
        for(int counter = 1; counter < HEADINGS.length; counter++){
            result += sep + HEADINGS[counter];
        }
        return result;
    }
    
    public static boolean headerCheck(final String[] parts){
        boolean isCorrect = true;
        
        if (parts.length == HEADINGS.length) {
            for (int counter = 0; counter < parts.length; counter++) {
                if (!parts[counter].equals(HEADINGS[counter])) {
                    isCorrect = false;
                    break;
                }
            }
        } else {
            isCorrect = false;
        }

        return isCorrect;
    }
    
    public static int headerSize(){
        return HEADINGS.length;
    }
    
}
