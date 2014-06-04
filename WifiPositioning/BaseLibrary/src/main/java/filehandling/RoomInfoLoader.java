/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package filehandling;

import datastorage.RoomInfo;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Load room information for a floor from file.
 * 
 * @author Greg Albiston
 */
public class RoomInfoLoader {
    
    private static final Logger logger = LoggerFactory.getLogger(RoomInfoLoader.class);
    
       /**
     * Load room information from file to a map.
     * 
     * @param roomInfoFile File to load information.
     * @param fieldSeparator Field separator between columns.
     * @return 
     */
    public static HashMap<String, RoomInfo> load(final File roomInfoFile, final String fieldSeparator) {

        HashMap<String, RoomInfo> roomInfo = new HashMap<>();

        try {
            String line;
            String[] parts;
            try (BufferedReader roomReader = new BufferedReader(new InputStreamReader(new FileInputStream(roomInfoFile)))) {

                line = roomReader.readLine();
                parts = line.split(fieldSeparator);
                if (RoomInfo.headerCheck(parts)) {
                    while ((line = roomReader.readLine()) != null) {
                        parts = line.split(fieldSeparator);
                        roomInfo.put(parts[0], new RoomInfo(parts));
                    }
                } else {
                    logger.error("RoomInfo headings not as expected.");
                }
            }
        } catch (FileNotFoundException ex) {
            logger.error(ex.getMessage());
        } catch (IOException ex) {
            logger.error(ex.getMessage());
        }
        return roomInfo;
    }

    /**
     * Load room information from file.
     * Assumes comma used for field separator.
     *
     * @param roomInfoFilename Filename of the file to load.
     * @return 
     */
    public static HashMap<String, RoomInfo> load(final String roomInfoFilename) {

        File roomInfoFile = new File(roomInfoFilename);

        return load(roomInfoFile, ",");
    }

    /**
     * Load room information from file.
     * Assumes comma used for field separator.
     *
     * @param roomInfoFile File to load room information.     
     * @return 
     */
    public static HashMap<String, RoomInfo> load(final File roomInfoFile) {
        return load(roomInfoFile, ",");
    }

    /**
     * Load room information from file to a list.
     * 
     * @param roomInfoFile File to load information.
     * @param fieldSeparator Field separator between columns.
     * @return 
     */
    public static List<RoomInfo> loadList(final File roomInfoFile, final String fieldSeparator) {

        List<RoomInfo> roomInfo = new ArrayList<>();

        try {
            String line;
            String[] parts;
            try (BufferedReader roomReader = new BufferedReader(new FileReader(roomInfoFile))) {

                line = roomReader.readLine();
                parts = line.split(fieldSeparator);
                if (RoomInfo.headerCheck(parts)) {
                    while ((line = roomReader.readLine()) != null) {
                        parts = line.split(fieldSeparator);
                        roomInfo.add(new RoomInfo(parts));
                    }
                } else {
                    logger.error("RoomInfo headings not as expected.");
                }
            }
        } catch (FileNotFoundException ex) {
            logger.error(ex.getMessage());
        } catch (IOException ex) {
            logger.error(ex.getMessage());
        }
        return roomInfo;
    }

    /**
     * Load room information from file to a list.
     * Assumes comma used for field separator.
     *
     * @param roomInfoFile File to load room information.     
     * @return 
     */
    public static List<RoomInfo> loadList(final File roomInfoFile) {
        return loadList(roomInfoFile, ",");
    }
    
    /**
     * Provides a map with one room set to default values.
     * 
     * @return 
     */
    public static HashMap<String, RoomInfo> defaultHashMap() {
        
        HashMap<String, RoomInfo> roomInfo = new HashMap<>();
        roomInfo.put(RoomInfo.DEFAULT_NAME, new RoomInfo());

        return roomInfo;
    }
    
    /**
     * Provides a list with one room set to default values.
     * 
     * @return 
     */
    public static List<RoomInfo> defaultList() {
        List<RoomInfo> roomInfo = new ArrayList<>();

        roomInfo.add(new RoomInfo());

        return roomInfo;
    }
}
