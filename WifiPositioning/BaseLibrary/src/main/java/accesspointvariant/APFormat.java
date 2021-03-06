/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package accesspointvariant;

import static accesspointvariant.APData.toStringHeadings;
import datastorage.RSSIData;
import datastorage.RoomInfo;
import filehandling.RSSILoader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Various functions for converting RSSI data, including file loading, to APData
 * format.
 *
 * @author Greg Albiston
 */
public class APFormat {

    private static final Logger logger = LoggerFactory.getLogger(APFormat.class);

    /**
     * Convert list of RSSI data into map of APData. Keys of map are the BSSID
     * of the APData.
     *
     * @param rssiDataList RSSI data to be converted.
     * @param isBSSIDMerged True, if last hex pair of BSSID is to be ignored.
     * @param isOrientationMerged True, if W-Ref of location is to be ignored.
     * @return
     */
    public static HashMap<String, APData> compile(final List<RSSIData> rssiDataList, final boolean isBSSIDMerged, final boolean isOrientationMerged) {
        HashMap<String, APData> bssidMap = new HashMap();
        logger.info("Compiling average access point data....");

        int beginIndex = 0;
        int endIndex = 17; //full BSSID - six hex pairs
        if (isBSSIDMerged) {
            logger.info("Merging BSSIDs from xx-xx-xx-xx-xx-xx to xx-xx-xx-xx-xx.");
            endIndex = 14;  //first five hex pairs
        }

        if (!isOrientationMerged) {
            logger.info("Splitting data by orientation (W ref) - $x in filename.");
        }

        for (RSSIData rssiData : rssiDataList) {

            String rssiDataKey = rssiData.getBSSID().substring(beginIndex, endIndex);  //Copy out the BSSID based on whether merging or not.            
            if (!isOrientationMerged) {
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
        if (!isOrientationMerged) {
            logger.info("BSSID/Orientation count: {}", bssidMap.size());
        } else {
            logger.info("BSSID count: {}", bssidMap.size());
        }
        return bssidMap;
    }

    /**
     * Convert list of RSSI data into list of APData.
     *
     * @param rssiDataList RSSI data to be converted.
     * @param isBSSIDMerged True, if last hex pair of BSSID is to be ignored.
     * @param isOrientationMerged True, if W-Ref of location is to be ignored.
     * @return
     */
    public static List<APData> compileList(final List<RSSIData> rssiDataList, final boolean isBSSIDMerged, final boolean isOrientationMerged) {
        HashMap<String, APData> bssidMap = compile(rssiDataList, isBSSIDMerged, isOrientationMerged);        
        return new ArrayList<>(bssidMap.values());
    }
    
    /**
     * Convert list of RSSI data into list of APData.
     *
     * @param rssiDataList RSSI data to be converted.
     * @param isBSSIDMerged True, if last hex pair of BSSID is to be ignored.
     * @param isOrientationMerged True, if W-Ref of location is to be ignored.
     * @return
     */
    /*
    public static List<APData> compileList(final List<RSSIData> rssiDataList, final boolean isBSSIDMerged, final boolean isOrientationMerged) {
        List<APData> bssidList = new ArrayList();
        logger.info("Compiling average access point data....");

        int beginIndex = 0;
        int endIndex = 17; //full BSSID - six hex pairs
        if (isBSSIDMerged) {
            logger.info("Merging BSSIDs from xx-xx-xx-xx-xx-xx to xx-xx-xx-xx-xx.");
            endIndex = 14;  //first five hex pairs
        }

        if (!isOrientationMerged) {
            logger.info("Splitting data by orientation (W ref) - $x in filename.");
        }

        for (RSSIData rssiData : rssiDataList) {

            String bssid = rssiData.getBSSID().substring(beginIndex, endIndex);  //Copy out the BSSID based on whether merging or not.            
            if (!isOrientationMerged) {
                bssid = bssid + "$" + rssiData.getwRef();       //Add the w-ref onto end of the key
            }

            boolean isMatch = false;

            for (APData entry : bssidList) {
                if (entry.getBSSID().equals(bssid)) {
                    entry.add(rssiData);
                    isMatch = true;
                    break;
                }
            }

            if (!isMatch) {
                APLocation location = new APLocation(rssiData, rssiData.getRSSI());
                bssidList.add(new APData(bssid, location));
            }

        }
        
        
        logger.info("Average access point compilation complete");
        if (!isOrientationMerged) {
            logger.info("BSSID/Orientation count: {}", bssidList.size());
        } else {
            logger.info("BSSID count: {}", bssidList.size());
        }
        return bssidList;
    }
    */
    
    /**
     * Load from file RSSI data and convert to map in APData format.
     *
     * @param dataFile File of RSSI data.
     * @param fieldSeparator Field separator used in file.
     * @param isBSSIDMerged True, if last hex pair of BSSID is to be ignored.
     * @param roomInfo Information about the rooms on the floor.
     * @return
     */
    public static HashMap<String, APData> load(final File dataFile, final String fieldSeparator, final HashMap<String, RoomInfo> roomInfo, final boolean isBSSIDMerged) {

        //load raw data
        List<RSSIData> rssiDataList = RSSILoader.load(dataFile, fieldSeparator, roomInfo);

        return compile(rssiDataList, isBSSIDMerged, false);
    }

    /**
     * Load from file RSSI data and convert to map in APData format.
     *
     * @param dataFile File of RSSI data.
     * @param fieldSeparator Field separator used in file.
     * @param roomInfo Information about the rooms on the floor.
     * @param isBSSIDMerged True, if last hex pair of BSSID is to be ignored.
     * @param isOrientationMerged True, if W-Ref of location is to be ignored.     
     * @return
     */
    public static HashMap<String, APData> load(final File dataFile, final String fieldSeparator, final HashMap<String, RoomInfo> roomInfo, final boolean isBSSIDMerged, final boolean isOrientationMerged) {

        //load raw data
        List<RSSIData> rssiDataList = RSSILoader.load(dataFile, fieldSeparator, roomInfo);

        return compile(rssiDataList, isBSSIDMerged, isOrientationMerged);
    }
    
    /**
     * Load from file RSSI data and convert to list in APData format.
     *
     * @param dataFile File of RSSI data.
     * @param fieldSeparator Field separator used in file.
     * @param roomInfo Information about the rooms on the floor.
     * @param isBSSIDMerged True, if last hex pair of BSSID is to be ignored.
     * @return
     */
    public static List<APData> loadList(final File dataFile, final String fieldSeparator, final HashMap<String, RoomInfo> roomInfo, final boolean isBSSIDMerged) {

        //load raw data
        List<RSSIData> rssiDataList = RSSILoader.load(dataFile, fieldSeparator, roomInfo);

        return compileList(rssiDataList, isBSSIDMerged, false);
    }
    
    /**
     * Load from file RSSI data and convert to list in APData format.
     *
     * @param dataFile File of RSSI data.
     * @param fieldSeparator Field separator used in file.
     * @param roomInfo Information about the rooms on the floor.
     * @param isBSSIDMerged True, if last hex pair of BSSID is to be ignored.
     * @param isOrientationMerged True, if W-Ref of location is to be ignored.
     * @return
     */
    public static List<APData> loadList(final File dataFile, final String fieldSeparator, final HashMap<String, RoomInfo> roomInfo, final boolean isBSSIDMerged, final boolean isOrientationMerged) {

        //load raw data
        List<RSSIData> rssiDataList = RSSILoader.load(dataFile, fieldSeparator, roomInfo);

        return compileList(rssiDataList, isBSSIDMerged, isOrientationMerged);
    }

    /**
     * Prints the map of APDate to specified file using the supplied field separator.
     * 
     * @param outputFile  File to output AP data.
     * @param apDataMap Map of APData to be written to file.
     * @param fieldSeparator File separator to be used in file.
     * @return 
     */
    public static boolean print(File outputFile, HashMap<String, APData> apDataMap, String fieldSeparator) {

        boolean isSuccess = false;

        try {

            try (BufferedWriter outputWriter = new BufferedWriter(new FileWriter(outputFile, true))) {

                outputWriter.append(toStringHeadings(fieldSeparator) + System.getProperty("line.separator"));
                
                for (APData apData : apDataMap.values()) {                    
                    String bssid = apData.getBSSID();
                    for (APLocation location : apData.getLocations()) {
                        outputWriter.append(bssid + fieldSeparator + location.toString(fieldSeparator) + System.getProperty("line.separator"));
                    }

                }
                isSuccess = true;
            }
        } catch (IOException x) {
            logger.error(x.getMessage());
        }

        return isSuccess;

    }

}
