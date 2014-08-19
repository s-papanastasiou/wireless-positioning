/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package filehandling;

import datastorage.GeomagneticData;
import datastorage.KNNFloorPoint;
import datastorage.RoomInfo;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Greg Albiston
 * 
 */
public class KNNGeomagnetic {
    
    private static final Logger logger = LoggerFactory.getLogger(KNNGeomagnetic.class);
    
    public static HashMap<String, KNNFloorPoint> load(final File dataFile, final HashMap<String, RoomInfo> roomInfo) {

        return load(dataFile, ",", roomInfo);
    }
    
    
    //loader that only loads data, using field separator, and does not store the compiled
    public static HashMap<String, KNNFloorPoint> load(final File dataFile, final String fieldSeparator, final HashMap<String, RoomInfo> roomInfo) {

        //load raw data
        List<GeomagneticData> magList = GeomagneticLoader.load(dataFile, fieldSeparator, roomInfo);

        //convert raw
        HashMap<String, KNNFloorPoint> knnRadioMap = compile(magList);

        return knnRadioMap;
    }
    
    //TODO - add compileList for magnetic data
    
    public static List<KNNFloorPoint> loadList(final File dataFile, final HashMap<String, RoomInfo> roomInfo) {

        return loadList(dataFile, ",", roomInfo);
    }
    
    
    //loader that only loads data, using field separator, and does not store the compiled
    public static List<KNNFloorPoint> loadList(final File dataFile, final String fieldSeparator, final HashMap<String, RoomInfo> roomInfo) {

        //load raw data
        List<GeomagneticData> magList = GeomagneticLoader.load(dataFile, fieldSeparator, roomInfo);

        //convert raw
        List<KNNFloorPoint> knnList = compileList(magList);

        return knnList;
    }

    
    //loader that tries to load from precompiled data unless missing or told that it is new data (in which case the compiled data is stored)
    public static HashMap<String, KNNFloorPoint> load(final File dataFile, final File knnFile, final HashMap<String, RoomInfo> roomInfo, boolean isNewData) {

        HashMap<String, KNNFloorPoint> knnRadioMap;

        if (isNewData || !knnFile.exists()) {
            //load raw data
            List<GeomagneticData> dataList = GeomagneticLoader.load(dataFile, roomInfo);

            //convert raw
            knnRadioMap = compile(dataList);

            KNNFormatStorage.store(knnFile, knnRadioMap);
        } else {
            knnRadioMap = KNNFormatStorage.load(knnFile);
        }

        return knnRadioMap;
    }
    
    public static HashMap<String, KNNFloorPoint> compile(final List<GeomagneticData> dataList) {

        HashMap<String, KNNFloorPoint> knnRadioMap = new HashMap();

        logger.info("Compiling Magnetic location data....");
        for (GeomagneticData magData : dataList) {

            if (knnRadioMap.containsKey(magData.getRoomRef())) //Update the existing entry
            {
                KNNFloorPoint entry = knnRadioMap.get(magData.getRoomRef());
                entry.add(GeomagneticData.X_Key, magData.getxValue());
                entry.add(GeomagneticData.Y_Key, magData.getyValue());
                entry.add(GeomagneticData.Z_Key, magData.getzValue());
            } else //New entry created and added
            {
                KNNFloorPoint entry = new KNNFloorPoint(magData, GeomagneticData.X_Key, magData.getxValue());
                entry.add(GeomagneticData.Y_Key, magData.getyValue());
                entry.add(GeomagneticData.Z_Key, magData.getzValue());
                knnRadioMap.put(magData.getRoomRef(), entry);
            }
        }
        logger.info("Location compilation complete");
        return knnRadioMap;
    }
    
    public static List<KNNFloorPoint> compileList(final List<GeomagneticData> dataList) {
             
        List<KNNFloorPoint> knnList = new ArrayList();

        logger.info("Compiling Magnetic location data list....");
               
        
        for (GeomagneticData magData : dataList) {

            boolean isMatch = false;
            
            for (KNNFloorPoint entry : knnList) {
                if(entry.getRoomRef().equals(magData.getRoomRef()))
                {
                    entry.add(GeomagneticData.X_Key, magData.getxValue());
                    entry.add(GeomagneticData.Y_Key, magData.getyValue());
                    entry.add(GeomagneticData.Z_Key, magData.getzValue());
                    isMatch = true;
                    break;
                }                
            }
            
            if(!isMatch)
            {     
                KNNFloorPoint entry = new KNNFloorPoint(magData, GeomagneticData.X_Key, magData.getxValue());
                entry.add(GeomagneticData.Y_Key, magData.getyValue());
                entry.add(GeomagneticData.Z_Key, magData.getzValue());                                
                knnList.add(entry);
            }            
        }
        logger.info("Location compilation complete");
        return knnList;
    }
    
}
