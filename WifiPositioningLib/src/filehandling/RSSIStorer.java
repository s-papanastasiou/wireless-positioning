/*
 * 
 * Stores RSSI data to file. All locations are converted to 1m spacing.
 * 
 */
package filehandling;

import datastorage.RSSIData;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Gerg
 */
public class RSSIStorer {
    
    private static final Logger logger = LoggerFactory.getLogger(RSSIStorer.class);
    
     public static boolean store(File dataFile, List<RSSIData> rssiDataList, int accuracy, boolean isNew){
         return store(dataFile, rssiDataList, ",", accuracy, isNew);
     }
       
     public static boolean store(File dataFile, List<RSSIData> rssiDataList, String fieldSeperator, int accuracy, boolean isNew){
                
        boolean isSucces = false;
        
        try {            

            try (BufferedWriter dataWriter = new BufferedWriter(new FileWriter(dataFile, true))) {

                if(isNew)
                    dataWriter.append(RSSIData.toStringHeadings(fieldSeperator) + System.getProperty("line.separator"));
                
                for(RSSIData rssiData:rssiDataList){
                    dataWriter.append(rssiData.toString(fieldSeperator, accuracy) + System.getProperty("line.separator"));
                }
                isSucces = true;
            }
        } catch (IOException x) {
            logger.error(x.getMessage());
        }
        
        return isSucces;
    }          
}
