/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package filehandling;

import accesspointvariant.APData;
import accesspointvariant.APFormat;
import datastorage.RSSIData;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Load a white list of BSSIDs to filter when processing a radio map.
 * 
 * @author Greg Albiston
 */
public class FilterBSSID {            
    
    private static final Logger logger = LoggerFactory.getLogger(FilterBSSID.class);
    
    /**
     * Load BSSIDs to retain in radio map. Single BSSID in each line of file.
     * 
     * @param filterFile File to load BSSIDs.
     * @return 
     */
    public static List<String> load(File filterFile){
        
        List<String> filterBSSIDs = new ArrayList<>();
        
        try {            
            
            String line;
        
            try (BufferedReader filterReader = new BufferedReader(new InputStreamReader(new FileInputStream(filterFile)))) {
                while ((line = filterReader.readLine()) != null) {                    
                    filterBSSIDs.add(line);
                }
            }
        } catch (FileNotFoundException ex) {
            logger.error(ex.getMessage());
        } catch (IOException ex) {
            logger.error(ex.getMessage());
        }        
         return filterBSSIDs;
    }
    
    /**
     * Generate a list of BSSIDs to use for filtering data on loading.
     * 
     * @param rssiDataList List of data to use to generate BSSID list.
     * @param minSampleCount Minimum number of RSSI data items to be included on the BSSID list.
     * @param isBSSIDMerged Whether or not to merge BSSIDs (5 hex pairs compared to 6 hex pairs).
     * @return 
     */
    public static List<String> generateBySampleCount(List<RSSIData> rssiDataList, Integer minSampleCount, Boolean isBSSIDMerged){
        
        List<String> filterBSSIDs = new ArrayList<>();
        List<APData> apDataList = APFormat.compileList(rssiDataList, isBSSIDMerged, true);
        
        for(APData apData: apDataList){
            if(apData.getItemCount()>=minSampleCount){                
                filterBSSIDs.add(apData.getBSSID());
            }
        }
        return filterBSSIDs;
    }
    
    /**
     * Generate a list of BSSIDs to use for filtering data on loading.
     * 
     * @param rssiDataList List of data to use to generate BSSID list.
     * @param minLocationCount Minimum number of RSSI data items to be included on the BSSID list.
     * @param isBSSIDMerged Whether or not to merge BSSIDs (5 hex pairs compared to 6 hex pairs).
     * @return 
     */
    public static List<String> generateByLocationCount(List<RSSIData> rssiDataList, Integer minLocationCount, Boolean isBSSIDMerged){
        
        List<String> filterBSSIDs = new ArrayList<>();
        List<APData> apDataList = APFormat.compileList(rssiDataList, isBSSIDMerged, true);
        
        for(APData apData: apDataList){
            if(apData.getLocations().size()>=minLocationCount){                
                filterBSSIDs.add(apData.getBSSID());
            }
        }
        return filterBSSIDs;
    }
    /**
     * Generate a list of BSSIDs to use for filtering data on loading.
     * 
     * @param apDataList List of Ap Data points.     
     * @param minLocationCount Minimum number of RSSI data items to be included on the BSSID list.     
     * @return 
     */
    public static List<String> generateByLocationCount(List<APData> apDataList, Integer minLocationCount){
        
        List<String> filterBSSIDs = new ArrayList<>();        
        
        for(APData apData: apDataList){
            if(apData.getLocations().size()>=minLocationCount){                
                filterBSSIDs.add(apData.getBSSID());
            }
        }
        return filterBSSIDs;
    }
    
    /**
     * Store list of BSSIDs in a file.
     * @param outputFile File to store the BSSIDs.
     * @param filterBSSIDs BSSIDs list to be stored.
     * @return 
     */
    public static boolean store(File outputFile, List<String> filterBSSIDs){
        boolean isSuccess = false;
        
        try {            

            try (BufferedWriter dataWriter = new BufferedWriter(new FileWriter(outputFile, true))) {
                
                for(String BSSID:filterBSSIDs){
                    dataWriter.append(BSSID + System.getProperty("line.separator"));
                }
                isSuccess = true;
            }
        } catch (IOException x) {
            logger.error(x.getMessage());
        }
        
        return isSuccess;
    }    
}
