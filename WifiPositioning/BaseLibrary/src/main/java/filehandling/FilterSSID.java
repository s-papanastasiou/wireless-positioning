/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package filehandling;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Greg Albiston
 */
public class FilterSSID {            
    
    private static final Logger logger = LoggerFactory.getLogger(FilterSSID.class);
    
    public static List<String> load(File filterFile){
        
        List<String> filterSSIDs = new ArrayList<>();
        
        try {            
            
            String line;
        
            try (BufferedReader filterReader = new BufferedReader(new InputStreamReader(new FileInputStream(filterFile)))) {
                while ((line = filterReader.readLine()) != null) {                    
                    filterSSIDs.add(line);
                }
            }
        } catch (FileNotFoundException ex) {
            logger.error(ex.getMessage());
        } catch (IOException ex) {
            logger.error(ex.getMessage());
        }        
         return filterSSIDs;
    }
    
}
