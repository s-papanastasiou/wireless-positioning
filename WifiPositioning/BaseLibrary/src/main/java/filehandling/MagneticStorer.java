/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package filehandling;

import datastorage.MagneticData;
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
public class MagneticStorer {
        
    private static final Logger logger = LoggerFactory.getLogger(MagneticStorer.class);
    
    /**
     * Store list of magnetic data to file.
     * Assumes comma separation between columns.
     * 
     * @param dataFile File to store the data.
     * @param magneticDataList List of magnetic data.
     * @param accuracy Scales the references by the specified accuracy i.e. converts the values to a 1m grid spacing. e.g. 1,1 on 5m grid will become 5,5 on 1m grid
     * @param isNew Whether writing a new file or appending the data to the end.
     * @return 
     */
    public static boolean store(File dataFile, List<MagneticData> magneticDataList, int accuracy, boolean isNew){
         return store(dataFile, magneticDataList, ",", accuracy, isNew);
     }
    
    /**
     * Store list of magnetic data to file.
     * 
     * @param dataFile File to store the data.
     * @param magneticDataList List of magnetic data.
     * @param fieldSeperator Separator to use between columns.
     * @param accuracy Scales the references by the specified accuracy i.e. converts the values to a 1m grid spacing. e.g. 1,1 on 5m grid will become 5,5 on 1m grid
     * @param isNew Whether writing a new file or appending the data to the end.
     * @return 
     */
    public static boolean store(File dataFile, List<MagneticData> magneticDataList, String fieldSeperator, int accuracy, boolean isNew){
               
        boolean isSuccess = false;
        
        try {            

            try (BufferedWriter dataWriter = new BufferedWriter(new FileWriter(dataFile, true))) {

                if(isNew)
                    dataWriter.append(MagneticData.toStringHeadings(fieldSeperator) + System.getProperty("line.separator"));
                
                for(MagneticData magneticData:magneticDataList){
                    dataWriter.append(magneticData.toString(fieldSeperator, accuracy) + System.getProperty("line.separator"));
                } 
                isSuccess = true;
            }
        } catch (IOException x) {
            logger.error(x.getMessage());
        }    
        
        return isSuccess;
    }    
}
