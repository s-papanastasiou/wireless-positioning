package androidparticlefilter;

import android.graphics.drawable.Drawable;
import android.os.Environment;
import datastorage.KNNFloorPoint;
import datastorage.RSSIData;
import datastorage.RoomInfo;
import filehandling.KNNFormatStorage;
import filehandling.KNNRSSI;
import filehandling.RSSILoader;
import java.io.File;
import java.util.HashMap;
import java.util.List;

/**
 * File controller for obtaining offline map and floor plan image.
 *
 * @author Pierre Rousseau
 */
public class FileController {   

    /**
     * Loads the offline data file. 
     * Checks whether a compressed version already exists. 
     * Loads from raw data and creates a new compressed file.
     * 
     * @param directory
     * @param fileName
     * @param separator
     * @param roomInfo
     * @param isBSSIDMerged
     * @param isOrientationMerged
     * @return 
     */    
    public static HashMap<String, KNNFloorPoint> loadOfflineMap(String directory, String fileName, String separator, HashMap<String, RoomInfo> roomInfo, boolean isBSSIDMerged, boolean isOrientationMerged) {

        HashMap<String, KNNFloorPoint> offlineMap = null;

        if (checkReadAvailability()) {
            File storageDirectory = Environment.getExternalStoragePublicDirectory(directory);

            File compressFile = new File(storageDirectory, CompassActivity.OFFLINE_COMPRESSED);

            if (!compressFile.exists()) {

                File offlineFile = new File(storageDirectory, fileName);

                if (offlineFile.exists()) {
                    List<RSSIData> rssiDataList = RSSILoader.load(offlineFile, separator, roomInfo);
                    offlineMap = KNNRSSI.compile(rssiDataList, isBSSIDMerged, isOrientationMerged);

                    KNNFormatStorage.store(compressFile, offlineMap);
                }
            } else {
                offlineMap = KNNFormatStorage.load(compressFile);
            }

        }

        return offlineMap;
    }

    /**
     * Loads the floor plan image file.
     * 
     * @param directory
     * @param floorPlanFilename
     * @return 
     */
    public static Drawable loadFloor(String directory, String floorPlanFilename) {

        Drawable floorImage = null;

        //check that external storage is available and can write
        if (checkReadAvailability()) {
            File dir = Environment.getExternalStoragePublicDirectory(directory);
            File floorImageFile = new File(dir, floorPlanFilename);
            if (floorImageFile.exists())
                floorImage = Drawable.createFromPath(floorImageFile.getPath());

        }

        return floorImage;
    }

    /**
     * Checks within device storage is available to read.
     * @return 
     */
    private static boolean checkReadAvailability() {
        
        String state = Environment.getExternalStorageState();
        // We can read and write the media OR We can read the media
        return Environment.MEDIA_MOUNTED.equals(state) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(state);                
    }
}

