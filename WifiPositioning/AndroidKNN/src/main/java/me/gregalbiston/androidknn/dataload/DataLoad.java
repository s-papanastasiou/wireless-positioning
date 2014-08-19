package me.gregalbiston.androidknn.dataload;

import android.graphics.drawable.Drawable;
import android.os.Environment;
import datastorage.KNNFloorPoint;
import filehandling.FilterSSID;
import filehandling.KNNFormatStorage;
import datastorage.RoomInfo;
import filehandling.RoomInfoLoader;

import java.io.File;
import java.util.HashMap;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Greg Albiston
 * Date: 22/07/13
 * Time: 14:43
 * Loads in files from external storage.
 */
public class DataLoad {

    public static HashMap<String, KNNFloorPoint> loadData(String directory, String dataFilename) {

        HashMap<String, KNNFloorPoint> knnRadioMap = null;
        String state = Environment.getExternalStorageState();

        if (Environment.MEDIA_MOUNTED.equals(state)) {
            File inputFile;
            File dir = Environment.getExternalStoragePublicDirectory(directory);

            inputFile = new File(dir, dataFilename);
            if (inputFile.exists())
                knnRadioMap = KNNFormatStorage.load(inputFile);
        }

        return knnRadioMap;
    }

    public static HashMap<String, RoomInfo> loadRoom(String directory, String roomInfoFilename, String fieldSeperator) {

        String state = Environment.getExternalStorageState();
        HashMap<String, RoomInfo> roomInfo = null;

        if (Environment.MEDIA_MOUNTED.equals(state)) {
            File dir = Environment.getExternalStoragePublicDirectory(directory);
            File roomInfoFile = new File(dir, roomInfoFilename);
            if (roomInfoFile.exists())
                roomInfo = RoomInfoLoader.load(roomInfoFile, fieldSeperator);
        }

        return roomInfo;
    }

    public static List<String> loadFilterSSID(String directory, String filterFilename) {

        String state = Environment.getExternalStorageState();
        List<String> filterSSID = null;

        if (Environment.MEDIA_MOUNTED.equals(state)) {
            File dir = Environment.getExternalStoragePublicDirectory(directory);
            File filterFile = new File(dir, filterFilename);
            if (filterFile.exists()) {
                filterSSID = FilterSSID.load(filterFile);
            }

        }

        return filterSSID;
    }


    public static Drawable loadFloor(String directory, String floorPlanFilename) {

        Drawable floorImage = null;

        String state = Environment.getExternalStorageState();

        //check that external storage is available and can write
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            File dir = Environment.getExternalStoragePublicDirectory(directory);
            File floorImageFile = new File(dir, floorPlanFilename);
            if (floorImageFile.exists())
                floorImage = Drawable.createFromPath(floorImageFile.getPath());

        }

        return floorImage;
    }

}
