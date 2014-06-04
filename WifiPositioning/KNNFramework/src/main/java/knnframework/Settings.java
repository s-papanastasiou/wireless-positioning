/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package knnframework;

import datastorage.RSSIData;
import datastorage.RoomInfo;
import filehandling.RoomInfoLoader;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author Gerg
 */
public class Settings {

    private List<RSSIData> radioMapList = new ArrayList<>();
    private boolean isRadioMapLoaded = false;
    private List<RSSIData> trialList = new ArrayList<>();
    private boolean isTrialLoaded = false;
    private List<String> filterSSIDList = new ArrayList<>();
    private boolean isSSIDFilterLoaded = false;
    private File floorPlan = null;
    private boolean isFloorPlanLoaded = false;
    private HashMap<String, RoomInfo> roomInfo = RoomInfoLoader.defaultHashMap();
    private boolean isRoomInfoLoaded = false;
    
    private final static String FILE_ERROR = "File Error";
    private final static String FILE_SUCCESS = "File Success";
    private final static String FILE_CANCEL = "File Cancelled";   

    public String statusReport() {

        String message;
        message = "SSID Filter Loaded: " + isSSIDFilterLoaded + System.lineSeparator();
        message += "Radio Map Loaded: " + isRadioMapLoaded + System.lineSeparator();
        message += "Trial Loaded: " + isTrialLoaded + System.lineSeparator();
        message += "Floor Plan Loaded: " + isFloorPlanLoaded + System.lineSeparator();
        message += "Room Info Loaded: " + isRoomInfoLoaded + System.lineSeparator();        
        
        return message;
    }

    public boolean isReady() {

        boolean isReady = false;

        if (isRadioMapLoaded && isTrialLoaded && isRoomInfoLoaded) {
            isReady = true;
        }

        return isReady;
    }

    public String isReadyError() {
        String message = "";

        if (!isRadioMapLoaded) {
            message += "Radiomap is not loaded. ";
        }

        if (!isTrialLoaded) {
            message += "Trial is not loaded. ";
        }
        
        if (!isRoomInfoLoaded) {
            message += "Room info is not loaded. ";
        }

        return message;
    }

    public boolean isPrintReady() {

        boolean isReady = false;

        if (isRadioMapLoaded && isFloorPlanLoaded && isRoomInfoLoaded) {
            isReady = true;
        }

        return isReady;
    }

    public String isPrintReadyError() {
        String message = "";

        if (!isRadioMapLoaded) {
            message += "Radiomap is not loaded. ";
        }

        if (!isFloorPlanLoaded) {
            message += "Floor plan is not loaded. ";
        }
        
        if (!isRoomInfoLoaded) {
            message += "Room info is not loaded. ";
        }

        return message;
    }
    
    public String addFilterSSID(List<String> filterSSIDList) {
        
        String message = FILE_ERROR;
        this.filterSSIDList = filterSSIDList;
        if (!this.filterSSIDList.isEmpty()) {
            isSSIDFilterLoaded = true;
            message = FILE_SUCCESS;
        }
        return message;
    }

    public String cancelSSIDFilter() {
        this.filterSSIDList = new ArrayList<>();
        isSSIDFilterLoaded = false;
        return FILE_CANCEL;
    }

    public String addRadioMapList(List<RSSIData> radioMapList) {
        String message = FILE_ERROR;
        this.radioMapList = radioMapList;
        if (!this.radioMapList.isEmpty()) {
            isRadioMapLoaded = true;
            message = FILE_SUCCESS;
        }
        return message;
    }

    public String cancelRadioMapList() {
        this.radioMapList = new ArrayList<>();
        isRadioMapLoaded = false;
        return FILE_CANCEL;
    }

    public String addTrialList(List<RSSIData> trialList) {
        String message = FILE_ERROR;
        this.trialList = trialList;
        if (!this.trialList.isEmpty()) {
            isTrialLoaded = true;
            message = FILE_SUCCESS;
        }
        return message;
    }

    public String cancelTrialList() {
        this.trialList = new ArrayList<>();
        isTrialLoaded = false;
        return FILE_CANCEL;
    }

    public String addFloorPlan(File floorplan) {
        String message = FILE_ERROR;
        this.floorPlan = floorplan;
        if (this.floorPlan == null) {
            isFloorPlanLoaded = false;
        } else {
            isFloorPlanLoaded = true;
            message = FILE_SUCCESS;
        }
        return message;
    }

    public String cancelFloorPlan() {
        this.floorPlan = null;
        isFloorPlanLoaded = false;
        return FILE_CANCEL;
    }

    public String addRoomInfo(HashMap<String, RoomInfo> roomInfo) {
        String message = FILE_ERROR;
        this.roomInfo = roomInfo;
        if (!this.roomInfo.isEmpty()) {
            isRoomInfoLoaded = true;
            message = FILE_SUCCESS;
        }
        return message;
    }

    public String cancelRoomInfo() {
        this.roomInfo = new HashMap<>();
        isRoomInfoLoaded = false;
        return FILE_CANCEL;
    }

    public List<RSSIData> getRadioMapList() {
        return radioMapList;
    }

    public List<RSSIData> getTrialList() {
        return trialList;
    }

    public List<String> getFilterSSIDList() {
        return filterSSIDList;
    }

    public File getFloorPlan() {
        return floorPlan;
    }

    public HashMap<String, RoomInfo> getRoomInfo() {
        return roomInfo;
    }

    public boolean isSSIDFilterLoaded() {
        return isSSIDFilterLoaded;
    }

    public boolean isRadioMapLoaded() {
        return isRadioMapLoaded;
    }

    public boolean isTrialLoaded() {
        return isTrialLoaded;
    }

    public boolean isFloorplanLoaded() {
        return isFloorPlanLoaded;
    }

    public boolean isRoomInfoLoaded() {
        return isRoomInfoLoaded;
    }
     
}
