/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package knnframework;

import datastorage.KNNFloorPoint;
import datastorage.KNNTrialPoint;
import datastorage.RSSIData;
import datastorage.RoomInfo;
import filehandling.FilterBSSID;
import filehandling.KNNRSSI;
import filehandling.RSSILoader;
import filehandling.RoomInfoLoader;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.imageio.ImageIO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import visualinfo.DisplayGrid;
import visualinfo.HeatMap;
import visualinfo.MatchMap;
import visualinfo.ValueMap;

/**
 *
 * @author Greg Albiston
 */
public class KNNFramework {

    private static final Logger logger = LoggerFactory.getLogger(KNNFramework.class);

    /**
     * @param args the command line arguments
     */
    private final static String[] options = {"Exit", "Instructions", "Change Working Directory", "Change Field Separator", "Load Default Filesnames", "Load BSSID Filter", "Load Radio Map", "Load Trial", "Load Floor Plan", "Print Data Grid", "Print Heatmap", "Print Matchmap", "Test Algorithms"};
    private final static String defaultFilterBSSID = "FilterBSSID.txt";
    private final static String defaultRadioMap = "RSSISurveyData.csv";
    private final static String defaultTrial = "RSSITrialData.csv";
    private final static String defaultFloorPlan = "floor2.png";
    private final static String defaultRoomInfo = "RoomInfo.csv";
    public final static String DEFAULT_FIELD_SEPARATOR = ",";

    //Johan and Pierre files
    //private final static String defaultRadioMap = "RSSIResults.csv";
    //private final static String defaultRoomInfo = "RoomInfoJohan.csv";    
    //public final static String DEFAULT_FIELD_SEPARATOR = ";";
    //Test loading of merged and unmerged data and to compare between trial and floor points
    private static void Test() {

        File file = new File("TestData.csv");
        HashMap<String, RoomInfo> roomInfo = RoomInfoLoader.defaultHashMap();
        List<KNNFloorPoint> listFloor = KNNRSSI.loadList(file, ",", roomInfo, true, true);        
        List<RSSIData> dataList = RSSILoader.load(file, ",", roomInfo);
        List<KNNTrialPoint> listTrial = KNNRSSI.compileTrialList(dataList, true, true);

        HashMap<String, KNNFloorPoint> mapMerged = KNNRSSI.load(file, ",", roomInfo, false, false);
        HashMap<String, KNNFloorPoint> mapUnMerged = KNNRSSI.load(file, ",", roomInfo, false, false);
    }

    public static void main(String[] args) {

        //       Test();
        System.out.println("Algorithm Framework Running");

        boolean isRunning = true;

        //assign path to the user's working directory
        File workingPath = new File(System.getProperty("user.dir"));

        String fieldSeparator = DEFAULT_FIELD_SEPARATOR;

        //load default files that are in the working directory
        Settings settings = loadDefaults(workingPath, fieldSeparator);

        if (args.length != 0) {
            if (settings.isReady()) {
                AlgorithmList.commandLine(workingPath, settings, args);
            } else {
                System.err.println(settings.isReadyError());
            }

        } else {  //Command prompt
            while (isRunning) {

                System.out.println("Working Directory: " + workingPath);
                System.out.println("Field Separator: " + fieldSeparator);
                System.out.print(settings.statusReport());

                switch (Menus.Options(options)) {
                    case 0:  //Exit
                        isRunning = false;
                        System.out.println("Exiting. Thank you.");
                        break;
                    case 1:  //Instruction
                        System.out.println(AlgorithmList.printHelp());
                        break;
                    case 2:  //Change working directory
                        workingPath = Menus.changePath(workingPath);
                        break;
                    case 3:  //Change field separator
                        fieldSeparator = Menus.changeSeparator(fieldSeparator);
                        break;
                    case 4:  //Load default files
                        settings = loadDefaults(workingPath, fieldSeparator);
                        break;
                    case 5:  //Load BSSID filter
                        System.out.println(settings.addFilterBSSID(loadFilterBSSID(workingPath)));  //TODO this doesn't work - also needs to reload the trial and radio map data with the filter applied
                        break;
                    case 6:  //Load Radio Map
                        System.out.println(settings.addRadioMapList(loadRadioMap(workingPath, settings, fieldSeparator)));
                        break;
                    case 7:  //Load Trial data
                        System.out.println(settings.addTrialList(loadTrial(workingPath, settings, fieldSeparator)));
                        break;
                    case 8:  //Load floor plan image
                        System.out.println(settings.addFloorPlan(loadFloorPlan(workingPath)));
                        System.out.println(settings.addRoomInfo(loadRoomInfo(workingPath, fieldSeparator)));
                        break;
                    case 9:  //Print grid
                        if (settings.isPrintReady()) {
                            boolean isRoomOutlineDraw = Menus.Choice("Do you want to draw the room outlines?");
                            DisplayGrid.print(workingPath, "PointGrid", settings.getFloorPlan(), settings.getRadioMapList(), settings.getRoomInfo(), isRoomOutlineDraw);
                        } else {
                            System.err.println(settings.isPrintReadyError());
                        }
                        break;
                    case 10:  //Print heatmaps
                        if (settings.isPrintReady()) {
                            boolean isOrientationMerged = Menus.Choice("Merge the orientations together (W ref)?");
                            boolean isBSSIDMerged = Menus.Choice("Merge BSSIDs where first five hex pairs match?");
                            HeatMap.printRSSI(workingPath, settings.getFloorPlan(), settings.getRoomInfo(), settings.getRadioMapList(), isBSSIDMerged, isOrientationMerged, fieldSeparator);
                        } else {
                            System.err.println(settings.isPrintReadyError());
                        }
                        break;
                    case 11:  //Print variance maps
                        if (settings.isPrintReady()) {
                            Double rangeValue = Menus.Value("Enter the range value for matches:", 0.0); 
                            Double lowerBound = Menus.Value("Enter the range value for matches:", -90.0);
                            Double upperBound = Menus.Value("Enter the range value for matches:", -90.0);
                            Double step = Menus.Value("Enter the range value for matches:", 0.00001);
                            boolean isOrientationMerged = Menus.Choice("Merge the orientations together (W ref)?");
                            boolean isBSSIDMerged = Menus.Choice("Merge BSSIDs where first five hex pairs match?");
                            MatchMap.print(workingPath, "MatchMap", settings.getFloorPlan(), settings.getRadioMapList(), settings.getRoomInfo(), rangeValue, isBSSIDMerged, isOrientationMerged, fieldSeparator);
                            ValueMap.print(workingPath, "ValueMap", settings.getFloorPlan(), settings.getRadioMapList(), settings.getRoomInfo(), rangeValue, lowerBound, upperBound, step, isBSSIDMerged, isOrientationMerged, fieldSeparator);
                        } else {
                            System.err.println(settings.isPrintReadyError());
                        }
                        break;

                    case 12:  //Algorithm list
                        if (settings.isReady()) {
                            AlgorithmList.list(workingPath, settings);
                        } else {
                            System.out.print(settings.isReadyError());
                        }
                        break;
                    default:
                        System.out.println("Problem with selection on main menu");
                        break;
                }
            }
        }
    }

    private static Settings loadDefaults(File workingPath, String fieldSeparator) {

        System.out.println("Attempting to load default files.");
        Settings settings = new Settings();

        //BSSID Filter
        File filterFile = new File(workingPath, defaultFilterBSSID);
        if (filterFile.exists()) {
            settings.addFilterBSSID(FilterBSSID.load(filterFile));
        } else {
            System.out.println(defaultFilterBSSID + " not found.");
        }

        //Room Info
        File roomInfoFile = new File(workingPath, defaultRoomInfo);
        if (roomInfoFile.exists()) {
            settings.addRoomInfo(RoomInfoLoader.load(roomInfoFile, fieldSeparator));

        } else {
            System.out.println(defaultRoomInfo + " not found.");
        }

        //Radio Map
        File radioMapFile = new File(workingPath, defaultRadioMap);
        if (radioMapFile.exists()) {
            System.out.print("Radio map ");
            settings.addRadioMapList(RSSILoader.load(radioMapFile, settings.getFilterBSSIDList(), fieldSeparator, settings.getRoomInfo()));
        } else {
            System.out.println(defaultRadioMap + " not found.");
        }

        //Trial
        File trialFile = new File(workingPath, defaultTrial);
        if (trialFile.exists()) {
            System.out.print("Trial ");
            settings.addTrialList(RSSILoader.load(trialFile, settings.getFilterBSSIDList(), fieldSeparator, settings.getRoomInfo()));
        } else {
            System.out.println(defaultTrial + " not found.");
        }

        //Floor Plan
        File floorPlanFile = new File(workingPath, defaultFloorPlan);
        boolean isSuccess;
        try {
            ImageIO.read(floorPlanFile);  //Test that an image file has been provided.
            isSuccess = true;
        } catch (IOException ex) {
            logger.error("Floor Plan image failed to load. {}", ex);
            isSuccess = false;
        }
        if (isSuccess) {
            settings.addFloorPlan(floorPlanFile);
        } else {
            System.out.println(defaultFloorPlan + " not found or error reading.");
        }

        return settings;
    }

    private static List<String> loadFilterBSSID(File workingPath) {
        List<String> filterBSSIDList = new ArrayList<>();
        File filePath = Menus.getFilename("Enter BSSID filter filename: ", workingPath);

        if (!filePath.isFile()) {
            filterBSSIDList = FilterBSSID.load(filePath);
        }

        return filterBSSIDList;
    }

    private static List<RSSIData> loadRadioMap(File workingPath, Settings settings, String fieldSeparator) {
        List<RSSIData> radioMapList = new ArrayList<>();
        File filePath = Menus.getFilename("Enter radio map filename: ", workingPath);

        if (!filePath.isFile()) {
            if (settings.isBSSIDFilterLoaded()) {
                System.out.println("BSSID filter applied");
            }

            System.out.print("Radio Map ");
            radioMapList = RSSILoader.load(filePath, settings.getFilterBSSIDList(), fieldSeparator, settings.getRoomInfo());

        }

        return radioMapList;
    }

    private static List<RSSIData> loadTrial(File workingPath, Settings settings, String fieldSeparator) {
        List<RSSIData> trialList = new ArrayList<>();
        File filePath = Menus.getFilename("Enter trial data filename: ", workingPath);

        if (!filePath.isFile()) {
            if (settings.isBSSIDFilterLoaded()) {
                System.out.println("BSSID filter applied");
            }

            System.out.print("Trial ");
            trialList = RSSILoader.load(filePath, settings.getFilterBSSIDList(), fieldSeparator, settings.getRoomInfo());
        }

        return trialList;
    }

    private static File loadFloorPlan(File workingPath) {

        File filePath = Menus.getFilename("Enter floor plan filename: ", workingPath);
        if (!filePath.isFile()) {
            try {

                BufferedImage floorPlanImage = ImageIO.read(filePath);  //Test that an image file has been provided.
            } catch (IOException ex) {
                logger.error("Error loading floor plan image: {}", filePath);
                System.err.println("Error loading floor plan image.");
                filePath = null;
            }
        }

        return filePath;
    }

    private static HashMap<String, RoomInfo> loadRoomInfo(File workingPath, String fieldSeparator) {
        HashMap<String, RoomInfo> roomInfo = new HashMap<>();
        File filePath = Menus.getFilename("Enter room info filename: ", workingPath);

        if (!filePath.isFile()) {
            roomInfo = RoomInfoLoader.load(filePath, fieldSeparator);
        }

        return roomInfo;
    }
}
