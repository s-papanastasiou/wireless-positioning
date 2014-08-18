/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package filehandling;

import datastorage.GeomagneticData;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Store magnetic data to file.
 * 
 * @author Greg Albiston
 */
public class GeomagneticStorer {
        
    private static final Logger logger = LoggerFactory.getLogger(GeomagneticStorer.class);
    
    /**
     * Store list of magnetic data to file.
     * Assumes comma separation between columns.
     * 
     * @param dataFile File to store the data.
     * @param magneticDataList List of magnetic data.     
     * @param isNew Whether writing a new file or appending the data to the end.
     * @return 
     */
    public static boolean store(File dataFile, List<GeomagneticData> magneticDataList, boolean isNew){
         return store(dataFile, magneticDataList, ",", isNew);
     }
    
    /**
     * Store list of magnetic data to file.
     * 
     * @param dataFile File to store the data.
     * @param magneticDataList List of magnetic data.
     * @param fieldSeperator Separator to use between columns.     
     * @param isNew Whether writing a new file or appending the data to the end.
     * @return 
     */
    public static boolean store(File dataFile, List<GeomagneticData> magneticDataList, String fieldSeperator, boolean isNew){
               
        boolean isSuccess = false;
        
        try {            

            try (BufferedWriter dataWriter = new BufferedWriter(new FileWriter(dataFile, true))) {

                if(isNew)
                    dataWriter.append(GeomagneticData.toStringHeadings(fieldSeperator) + System.getProperty("line.separator"));
                
                for(GeomagneticData magneticData:magneticDataList){
                    dataWriter.append(magneticData.toString(fieldSeperator) + System.getProperty("line.separator"));
                } 
                isSuccess = true;
            }
        } catch (IOException x) {
            logger.error(x.getMessage());
        }    
        
        return isSuccess;
    }    
}
