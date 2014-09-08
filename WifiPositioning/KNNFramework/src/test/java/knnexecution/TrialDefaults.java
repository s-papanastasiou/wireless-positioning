/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package knnexecution;

import datastorage.GeomagneticData;
import datastorage.RSSIData;
import datastorage.RoomInfo;
import filehandling.GeomagneticLoader;
import filehandling.RSSILoader;
import filehandling.RoomInfoLoader;
import java.io.File;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author Gerg
 */
public class TrialDefaults {
    
    public static final String workingDirectory = "C:\\WirelessPositioningTestFiles";
    public static final File workingPath = new File(workingDirectory);
    public static final String floorPlanFilename = "floor2.png";
    public static final File floorPlanFile = new File(workingPath, floorPlanFilename);
    public static final String roomInfoFilename = "RoomInfo.csv";
    public static final File roomInfoFile = new File(workingPath, roomInfoFilename);
    public static final String roomInfoSep = ",";
    public static final HashMap<String, RoomInfo> roomInfo = RoomInfoLoader.load(roomInfoFile, roomInfoSep);
    
    public static final String dataSep = ",";
            
    public static final String rssiData = "RSSISurveyData.csv";
    public static final File rssiDataFile = new File(workingPath, rssiData);
    public static final List<RSSIData> rssiDataList = RSSILoader.load(rssiDataFile, dataSep, roomInfo);
    public static final String geomagneticData = "GeomagneticSurveyData.csv";
    //public static final String geomagneticData = "GeomagneticSurveyData - No Y.csv";
    public static final File geomagneticDataFile = new File(workingPath, geomagneticData);
    public static final List<GeomagneticData> geomagneticDataList = GeomagneticLoader.load(geomagneticDataFile, dataSep, roomInfo);
        
    public static final String fieldSeparator = ",";
    
}
