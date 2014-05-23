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
 *
 * @author Greg Albiston
 */
public class MagneticStorer {
        
    private static final Logger logger = LoggerFactory.getLogger(MagneticStorer.class);
    
    public static boolean store(File dataFile, List<MagneticData> magneticDataList, int accuracy, boolean isNew){
         return store(dataFile, magneticDataList, ",", accuracy, isNew);
     }
    
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
