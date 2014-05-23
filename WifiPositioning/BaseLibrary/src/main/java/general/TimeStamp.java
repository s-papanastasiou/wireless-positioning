/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package general;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Gerg
 */
public class TimeStamp {
    
    private static final Logger logger = LoggerFactory.getLogger(TimeStamp.class);
    
    public final static SimpleDateFormat DATE_TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    public final static SimpleDateFormat SHORT_DATE_TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public final static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    protected final static SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm:ss.SSS");
    protected final static SimpleDateFormat LEGACY_TIME_FORMAT = new SimpleDateFormat("mm:ss.S");
    
    public static String formatDateTime(long timestamp){
   
        return DATE_TIME_FORMAT.format(timestamp);
    }    
    
    public static String formatDate(long timestamp){
   
        return DATE_FORMAT.format(timestamp);
    }  
    
    public static String formatTime(long timestamp){
   
        return TIME_FORMAT.format(timestamp);
    }  
    
    public static String formatShortDateTime(long timestamp){
   
        return SHORT_DATE_TIME_FORMAT.format(timestamp);
    }
    
    public static long convertDateTime(String dateTimeString){
           
        long timestamp;
        
        try{            
            timestamp = DATE_TIME_FORMAT.parse(dateTimeString).getTime();
        }catch(ParseException e){
            try{
                timestamp = Long.parseLong(dateTimeString);
            }catch(NumberFormatException t){
                try {
                    timestamp = LEGACY_TIME_FORMAT.parse(dateTimeString).getTime(); //FIXME figures are currently negative - need to ensure comparison between smaller (earlier) and bigger (later) is still correct - otherwise trials will run backwards
                } catch (ParseException ex) {
                    logger.info("Failed to convert timestamp.");
                    timestamp = 0;
                }                
            }
        }
        
        return timestamp;
    }
            
}
