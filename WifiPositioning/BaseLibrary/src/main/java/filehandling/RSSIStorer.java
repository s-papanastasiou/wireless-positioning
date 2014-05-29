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
 * Store RSSI data to file.
 * 
 * @author Greg Albiston
 */
public class RSSIStorer {
    
    private static final Logger logger = LoggerFactory.getLogger(RSSIStorer.class);
    
    /**
     * Store list of magnetic data to file.
     * Assumes comma separation between columns.
     * 
     * @param dataFile File to store the data.
     * @param rssiDataList List of RSSI data.
     * @param accuracy Scales the references by the specified accuracy i.e. converts the values to a 1m grid spacing. e.g. 1,1 on 5m grid will become 5,5 on 1m grid
     * @param isNew Whether writing a new file or appending the data to the end.
     * @return 
     */
     public static boolean store(File dataFile, List<RSSIData> rssiDataList, int accuracy, boolean isNew){
         return store(dataFile, rssiDataList, ",", accuracy, isNew);
     }
       
     /**
     * Store list of RSSI data to file.     
     * 
     * @param dataFile File to store the data.
     * @param rssiDataList List of RSSI data.
     * @param fieldSeperator Separator between columns.
     * @param accuracy Scales the references by the specified accuracy i.e. converts the values to a 1m grid spacing. e.g. 1,1 on 5m grid will become 5,5 on 1m grid
     * @param isNew Whether writing a new file or appending the data to the end.
     * @return 
     */
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
