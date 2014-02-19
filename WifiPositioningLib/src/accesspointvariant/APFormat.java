/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package accesspointvariant;

import static accesspointvariant.APData.toStringHeadings;
import datastorage.RSSIData;
import filehandling.RSSILoader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Gerg
 */
public class APFormat {
    
    private static final Logger logger = LoggerFactory.getLogger(APFormat.class);
    
    public static HashMap<String, APData> compile(final List<RSSIData> rssiDataList, final boolean isBSSIDMerge, final boolean isOrientationMerge) {
        HashMap<String, APData> bssidMap = new HashMap();
        logger.info("Compiling average access point data....");

        int beginIndex = 0;
        int endIndex = 17; //full BSSID - six hex pairs
        if (isBSSIDMerge) {
            logger.info("Merging BSSIDs from xx-xx-xx-xx-xx-xx to xx-xx-xx-xx-xx.");
            endIndex = 14;  //first five hex pairs
        }

        if (!isOrientationMerge) {
            logger.info("Splitting data by orientation (W ref) - $x in filename.");
        }

        for (RSSIData rssiData : rssiDataList) {

            String rssiDataKey = rssiData.getBSSID().substring(beginIndex, endIndex);  //Copy out the BSSID based on whether merging or not.            
            if (!isOrientationMerge) {
                rssiDataKey = rssiDataKey + "$" + rssiData.getwRef();       //Add the w-ref onto end of the key
            }

            if (bssidMap.containsKey(rssiDataKey)) //Update the existing entry
            {
                APData entry = bssidMap.get(rssiDataKey);
                entry.add(rssiData);
            } else //New entry created and added
            {
                APLocation location = new APLocation(rssiData, rssiData.getRSSI());
                APData entry = new APData(rssiDataKey, location);
                bssidMap.put(rssiDataKey, entry);
            }
        }
        logger.info("Average access point compilation complete");
        if (!isOrientationMerge) {
            logger.info("BSSID/Orientation count: {}", bssidMap.size());
        } else {
            logger.info("BSSID count: {}", bssidMap.size());
        }
        return bssidMap;
    }
    
    public static List<APData> compileList(final List<RSSIData> rssiDataList, final boolean isBSSIDMerge, final boolean isOrientationMerge) {
        List<APData> bssidList = new ArrayList();
        logger.info("Compiling average access point data....");

        int beginIndex = 0;
        int endIndex = 17; //full BSSID - six hex pairs
        if (isBSSIDMerge) {
            logger.info("Merging BSSIDs from xx-xx-xx-xx-xx-xx to xx-xx-xx-xx-xx.");
            endIndex = 14;  //first five hex pairs
        }

        if (!isOrientationMerge) {
            logger.info("Splitting data by orientation (W ref) - $x in filename.");
        }

        for (RSSIData rssiData : rssiDataList) {

            String bssid = rssiData.getBSSID().substring(beginIndex, endIndex);  //Copy out the BSSID based on whether merging or not.            
            if (!isOrientationMerge) {
                bssid = bssid + "$" + rssiData.getwRef();       //Add the w-ref onto end of the key
            }

            boolean isMatch = false;
            
            for(int counter = 0; counter < bssidList.size(); counter++)
            {
                APData entry = bssidList.get(counter);                
                if(entry.getBSSID().equals(bssid))
                {                    
                    entry.add(rssiData);
                    isMatch = true;
                    break;
                }                
            }
            
            if(!isMatch)
            {   
                APLocation location = new APLocation(rssiData, rssiData.getRSSI());
                bssidList.add(new APData(bssid, location));
            } 
                        
        }
        logger.info("Average access point compilation complete");
        if (!isOrientationMerge) {
            logger.info("BSSID/Orientation count: {}", bssidList.size());
        } else {
            logger.info("BSSID count: {}", bssidList.size());
        }
        return bssidList;
    }
    
    public static HashMap<String, APData> load(final File dataFile, final String fieldSeparator, final boolean isBSSIDMerged){
        
         //load raw data
        List<RSSIData> rssiDataList = RSSILoader.load(dataFile, fieldSeparator);
        
        return compile(rssiDataList, isBSSIDMerged, false);
    }
    
     public static List<APData> loadList(final File dataFile, final String fieldSeparator, final boolean isBSSIDMerged){
        
         //load raw data
        List<RSSIData> rssiDataList = RSSILoader.load(dataFile, fieldSeparator);
        
        return compileList(rssiDataList, isBSSIDMerged, false);
    }
    
    public static boolean print(File dataFile, HashMap<String, APData> apBoxMap, String fieldSeparator){
        
         boolean isSucces = false;
        
        try {            

            try (BufferedWriter dataWriter = new BufferedWriter(new FileWriter(dataFile, true))) {

                dataWriter.append(toStringHeadings(fieldSeparator) + System.getProperty("line.separator"));
                
                Set<String> keys = apBoxMap.keySet();
                for(String key: keys){
                    APData item = apBoxMap.get(key);
                    String bssid = item.getBSSID();
                    for(APLocation location: item.getLocations())
                        dataWriter.append(bssid + fieldSeparator + location.toString(fieldSeparator) + System.getProperty("line.separator"));
                    
                }
                isSucces = true;
            }
        } catch (IOException x) {
            logger.error(x.getMessage());
        }
        
        return isSucces;
        
    }
    
}
