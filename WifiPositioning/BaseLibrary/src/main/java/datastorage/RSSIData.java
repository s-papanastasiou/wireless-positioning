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
public class RSSIData extends Location {
    
    //private Date timestamp;     //String implemented for convenience not currently using timestamp.
    private final long timestamp;        
    private final String BSSID;
    private final String SSID;
    private final int RSSI;
    private final int channel; 
    
    public static final String[] HEADINGS = {"Timestamp", "Room", "X Ref", "Y Ref", "W Ref", "BSSID", "SSID", "RSSI", "Channel"};
    
    //private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
           
    public RSSIData(final String[] parts) throws ParseException
    {
        super(parts[1], Integer.parseInt(parts[2]), Integer.parseInt(parts[3]), Integer.parseInt(parts[4]));  
        this.timestamp =  TimeStamp.convertDateTime(parts[0]);               
        this.BSSID = parts[5];
        this.SSID = parts[6];
        this.RSSI = Integer.parseInt(parts[7]);
        this.channel = Integer.parseInt(parts[8]);        
    } 
    
    public RSSIData(long timestamp, Location location, String BSSID, String SSID, int RSSI, int frequency){
        super(location);        
        this.timestamp =  timestamp;
        this.BSSID = BSSID;
        this.SSID = SSID;
        this.RSSI = RSSI;
        this.channel = ((frequency-2412)/5)+1;  //calculates accurately for channels 1-13. Channel 14 is not generally available.
        
    }            

    public String getBSSID() {
        return BSSID;
    }

    public int getRSSI() {
        return RSSI;
    }        

    public long getTimestamp() {
        return timestamp;
    }

    public String getSSID() {
        return SSID;
    }

    public int getChannel() {
        return channel;
    }
     
    @Override
    public String toString(final String sep){                
        return TimeStamp.formatDateTime(timestamp) + sep + super.toString(sep) + sep + BSSID + sep + SSID + sep + RSSI + sep + channel;        
    }
    
    @Override
    public String toString(final String sep, final int accuracy){                
        return TimeStamp.formatDateTime(timestamp) + sep + super.toString(sep, accuracy) + sep + BSSID + sep + SSID + sep + RSSI + sep + channel;        
    }
    
    public static String toStringHeadings(final String separator){
        
        String result =HEADINGS[0];
        for(int counter = 1; counter < HEADINGS.length; counter++){
            result += separator + HEADINGS[counter];
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
