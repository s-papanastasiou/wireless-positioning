/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package filehandling;

import datastorage.RSSIData;
import datastorage.RoomInfo;
import java.io.BufferedReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Load RSSI data from file.
 * 
 * @author Greg Albiston
 */
public class RSSILoader {

    private static final Logger logger = LoggerFactory.getLogger(RSSILoader.class);
    
    /**
     * Load list of RSSI data from file.
     * 
     * @param dataFile File of geomagnetic data with header row.     
     * @param filterBSSIDs List of BSSIDs to only include from loaded file - empty to permit all BSSIDs.
     * @param seperator Field separator between columns.
     * @param roomInfo Information about the rooms on the floor.
     * @return 
     */
    public static List<RSSIData> load(final File dataFile, final List<String> filterBSSIDs, final String seperator, final HashMap<String, RoomInfo> roomInfo) {

        List<RSSIData> rawData = new ArrayList<>();

        int lineCounter = 1;
        try {
            
            try (BufferedReader dataReader = new BufferedReader(new FileReader(dataFile))) {

                String line = dataReader.readLine(); //Read the header
                String[] parts = line.split(seperator);
                if (RSSIData.headerCheck(parts)) {
                    int headerSize = RSSIData.headerSize();
                    while ((line = dataReader.readLine()) != null) {
                        lineCounter++;
                        parts = line.split(seperator);
                        if (parts.length == headerSize) {
                            try {
                                if (!filterBSSIDs.isEmpty()) {
                                    if (filterBSSIDs.contains(parts[5])) //fifth element is BSSID - only add if it is in the filter list
                                    {
                                        rawData.add(new RSSIData(parts, roomInfo));
                                    }
                                } else {
                                    rawData.add(new RSSIData(parts, roomInfo));
                                }

                            } catch (ParseException ex) {
                                logger.error("Error parsing line: {} {}", lineCounter, ex.getMessage());
                            }
                        } else {
                            logger.error("Data items count do not match headings count. Line: {}", lineCounter);
                        }
                    }

                    logger.info("RSSI data read successfully. Lines read: {} Lines stored: {}", lineCounter, rawData.size());
                } else {
                    logger.error("Headings are not as expected.");
                    if(parts.length==1)
                        logger.error("Expecting field separator: {} Found: {}", seperator, line );
                    else
                        logger.error("Expecting: {} Found: {}", RSSIData.toStringHeadings(seperator), line );
                }
            }
        } catch (FileNotFoundException ex) {
            logger.error(ex.getMessage());
        } catch (IOException x) {
            logger.error(x.getMessage());
        }

        return rawData;
    }

    /**
     * Load list of RSSI data from file.
     * Assumes comma separation between columns.
     * 
     * @param dataFile File of geomagnetic data with header row.          
     * @param filterBSSIDs List of BSSIDs to only include from loaded file - empty to permit all BSSIDs.
     * @param roomInfo Information about the rooms on the floor.
     * @return 
     */
    public static List<RSSIData> load(final File dataFile, final List<String> filterBSSIDs, final HashMap<String, RoomInfo> roomInfo) {

        return load(dataFile, filterBSSIDs, ",", roomInfo);
    }

    /**
     * Load list of RSSI data from file.
     * Assumes comma separation between columns and assumes all SSIDs permitted.
     * 
     * @param dataFile File of geomagnetic data with header row.          
     * @param roomInfo Information about the rooms on the floor.
     * @return 
     */
    public static List<RSSIData> load(final File dataFile, final HashMap<String, RoomInfo> roomInfo) {

        return load(dataFile, new ArrayList<String>(), ",", roomInfo);
    }

    /**
     * Load list of RSSI data from file.
     * Assumes all SSIDs permitted.
     * 
     * @param dataFile File of RSSI data with header row.          
     * @param fieldSeparator Separator between columns.
     * @param roomInfo Information about the rooms on the floor.
     * @return 
     */
    public static List<RSSIData> load(final File dataFile, final String fieldSeparator, final HashMap<String, RoomInfo> roomInfo) {

        return load(dataFile, new ArrayList<String>(), fieldSeparator, roomInfo);
    }
}
