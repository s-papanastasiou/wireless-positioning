/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package filehandling;

import datastorage.Location;
import datastorage.KNNFloorPoint;
import datastorage.RSSIData;
import datastorage.KNNTrialPoint;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Gerg
 */
public class KNNRSSI {

    private static final Logger logger = LoggerFactory.getLogger(KNNRSSI.class);

    //loader that only loads data and does not store the compiled
    public static HashMap<String, KNNFloorPoint> load(final File dataFile) {

        return load(dataFile, ",", false, false);
    }

    //loader that only loads data and does not store the compiled
    public static HashMap<String, KNNFloorPoint> load(final File dataFile, final String fieldSeparator, final boolean isBSSIDMerged) {

        return load(dataFile, ",", isBSSIDMerged, false);
    }

    //loader that only loads data and does not store the compiled
    public static HashMap<String, KNNFloorPoint> load(final File dataFile, final String fieldSeparator, final boolean isBSSIDMerged, final boolean isOrientationMerged) {

        //load raw data
        List<RSSIData> rssiList = RSSILoader.load(dataFile, fieldSeparator);

        //convert raw
        HashMap<String, KNNFloorPoint> knnRadioMap = compile(rssiList, isBSSIDMerged, isOrientationMerged);

        return knnRadioMap;
    }

    //loader that only loads data and does not store the compiled - returns a list
    public static List<KNNFloorPoint> loadList(final File dataFile) {

        return loadList(dataFile, ",", false, false);
    }

    public static List<KNNFloorPoint> loadList(final File dataFile, final String fieldSeparator, final boolean isBSSIDMerged) {

        return loadList(dataFile, ",", isBSSIDMerged, false);
    }

    //loader that only loads data, using field separator, and does not store the compiled - returns a list
    public static List<KNNFloorPoint> loadList(final File dataFile, final String fieldSeparator, final boolean isBSSIDMerged, final boolean isOrientationMerged) {

        //load raw data
        List<RSSIData> rssiList = RSSILoader.load(dataFile, fieldSeparator);

        //convert raw
        List<KNNFloorPoint> knnRadioList = compileList(rssiList, isBSSIDMerged, isOrientationMerged);

        return knnRadioList;
    }

    //loader that tries to load from precompiled data unless missing or told that it is new data (in which case the compiled data is stored)
    public static HashMap<String, KNNFloorPoint> load(final File dataFile, File knnFile, boolean isNewData) {

        return load(dataFile, knnFile, isNewData, new ArrayList<String>());
    }

    public static HashMap<String, KNNFloorPoint> load(final File dataFile, File knnFile, boolean isNewData, List<String> filterSSIDs) {

        return load(dataFile, knnFile, isNewData, filterSSIDs, false, false);
    }

    //loader that tries to load from precompiled data unless missing or told that it is new data (in which case the compiled data is stored)
    //can only be used on RSSI data which is filtered through list of SSIDs
    public static HashMap<String, KNNFloorPoint> load(final File dataFile, File knnFile, final boolean isNewData, final List<String> filterSSIDs, final boolean isBSSIDMerged, final boolean isOrientationMerged) {

        HashMap<String, KNNFloorPoint> knnRadioMap;

        if (isNewData || !knnFile.exists()) {
            //load raw data
            List<RSSIData> dataList = RSSILoader.load(dataFile, filterSSIDs);

            //convert raw
            knnRadioMap = compile(dataList, isBSSIDMerged, isOrientationMerged);

            KNNFormatStorage.store(knnFile, knnRadioMap);
        } else {
            knnRadioMap = KNNFormatStorage.load(knnFile);
        }

        return knnRadioMap;
    }

    public static HashMap<String, KNNFloorPoint> compile(final List<RSSIData> dataList, final boolean isBSSIDMerged) {

        return compile(dataList, isBSSIDMerged, false);
    }

    public static HashMap<String, KNNFloorPoint> compile(final List<RSSIData> dataList, final boolean isBSSIDMerged, final boolean isOrientationMerged) {

        HashMap<String, KNNFloorPoint> knnRadioMap = new HashMap();

        //logger.info("Compiling RSSI location data....");
        int beginIndex = 0;
        int endIndex = 17; //full BSSID - six hex pairs

        if (isBSSIDMerged) {
            endIndex = 14;
        }

        for (RSSIData rssiData : dataList) {

            String bssid = rssiData.getBSSID().substring(beginIndex, endIndex);  //Copy out the BSSID based on whether merging or not.                

            String roomRef;
            if (isOrientationMerged) //Change the room ref based on whether compressing orientations together     
            {
                roomRef = Location.NoOrientationRoomRef(rssiData);
            } else {
                roomRef = rssiData.getRoomRef();
            }

            if (knnRadioMap.containsKey(roomRef)) //Update the existing entry
            {
                KNNFloorPoint entry = knnRadioMap.get(roomRef);
                entry.add(bssid, rssiData.getRSSI());
            } else //New entry created and added
            {
                Location location;
                if (isOrientationMerged) //Change the location based on whether compressing orientations together
                {
                    location = Location.NoOrientationLocation(rssiData);
                } else {
                    location = rssiData;
                }

                //create a new floor point - override the room reference with that used as the key in the knnRadioMap.
                KNNFloorPoint entry = new KNNFloorPoint(location, bssid, rssiData.getRSSI(), roomRef);
                knnRadioMap.put(roomRef, entry);
            }
        }
        //logger.info("Location compilation complete");
        return knnRadioMap;
    }

    public static List<KNNFloorPoint> compileList(final List<RSSIData> dataList, final boolean isBSSIDMerged) {
        return compileList(dataList, isBSSIDMerged, false);
    }

    public static List<KNNFloorPoint> compileList(final List<RSSIData> dataList, final boolean isBSSIDMerged, final boolean isOrientationMerged) {

        List<KNNFloorPoint> knnList = new ArrayList();

        //logger.info("Compiling RSSI location data list....");
        int beginIndex = 0;
        int endIndex = 17; //full BSSID - six hex pairs

        if (isBSSIDMerged) {
            endIndex = 14;
        }

        for (RSSIData rssiData : dataList) {

            boolean isMatch = false;
            String bssid = rssiData.getBSSID().substring(beginIndex, endIndex);  //Copy out the BSSID based on whether merging or not.                

            String roomRef;
            if (isOrientationMerged) //Change the room ref based on whether compressing orientations together     
            {
                roomRef = Location.NoOrientationRoomRef(rssiData);
            } else {
                roomRef = rssiData.getRoomRef();
            }

            for (int counter = 0; counter < knnList.size(); counter++) {
                KNNFloorPoint entry = knnList.get(counter);
                if (entry.getRoomRef().equals(roomRef)) {
                    entry.add(bssid, rssiData.getRSSI());
                    isMatch = true;
                    break;
                }
            }

            if (!isMatch) {
                Location location;
                if (isOrientationMerged) //Change the location based on whether compressing orientations together
                {
                    location = Location.NoOrientationLocation(rssiData);
                } else {
                    location = rssiData;
                }

                knnList.add(new KNNFloorPoint(location, bssid, rssiData.getRSSI()));
            }
        }
        //logger.info("Location compilation complete");
        return knnList;
    }

    //Stores the timestamp as well as the floor point so that only readings at the same time are stored together. Used for generating lists of trial points.
    public static List<KNNTrialPoint> compileTrialList(final List<RSSIData> dataList, final boolean isBSSIDMerged, final boolean isOrientationMerged) {

        List<KNNTrialPoint> knnList = new ArrayList();

        //logger.info("Compiling RSSI trial location data list....");
        int beginIndex = 0;
        int endIndex = 17; //full BSSID - six hex pairs

        if (isBSSIDMerged) {
            endIndex = 14;
        }

        for (RSSIData rssiData : dataList) {

            boolean isMatch = false;
            String bssid = rssiData.getBSSID().substring(beginIndex, endIndex);  //Copy out the BSSID based on whether merging or not.                

            String roomRef;
            if (isOrientationMerged) //Change the room ref based on whether compressing orientations together     
            {
                roomRef = Location.NoOrientationRoomRef(rssiData);
            } else {
                roomRef = rssiData.getRoomRef();
            }

            //Check against timestamp and room ref of floor point for matches
            long timestamp = rssiData.getTimestamp();
            for (int counter = 0; counter < knnList.size(); counter++) {
                KNNTrialPoint entry = knnList.get(counter);
                if (entry.equals(timestamp, roomRef)) {
                    entry.add(bssid, rssiData.getRSSI());
                    isMatch = true;
                    break;
                }
            }

            if (!isMatch) {
                Location location;
                if (isOrientationMerged) //Change the location based on whether compressing orientations together
                {
                    location = Location.NoOrientationLocation(rssiData);
                } else {
                    location = rssiData;
                }

                knnList.add(new KNNTrialPoint(timestamp, location, bssid, rssiData.getRSSI()));
            }
        }
        //logger.info("Location compilation complete");
        return knnList;
    }

}
