package com.example.Sensor;

import android.graphics.drawable.Drawable;
import android.os.Environment;
import containers.RSSIData;
import datastorage.KNNFloorPoint;
import filehandling.KNNFormatStorage;
import filehandling.KNNRSSI;
import filehandling.RSSILoader;

import java.io.File;
import java.util.HashMap;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: pierre
 * Date: 07/10/13
 * Time: 15:52
 * To change this template use File | Settings | File Templates.
 */

public class FileController {

    public final static String OFFLINE_COMPRESSED = "OfflineCompressed.knn";

    //Loads the offline data. Checks whether a compressed version already exists. Loads from raw data and creates a new compressed file.
    public static HashMap<String, KNNFloorPoint> loadOfflineMap(String directory, String fileName, String separator, boolean isBSSIDMerged, boolean isOrientationMerged) {

        HashMap<String, KNNFloorPoint> offlineMap = null;

        if (checkReadAvailability()) {
            File storageDirectory = Environment.getExternalStoragePublicDirectory(directory);

            File compressFile = new File(storageDirectory, OFFLINE_COMPRESSED);

            if (!compressFile.exists()) {

                File offlineFile = new File(storageDirectory, fileName);

                if (offlineFile.exists()) {
                    List<RSSIData> rssiDataList = RSSILoader.load(offlineFile, separator);
                    offlineMap = KNNRSSI.compile(rssiDataList, isBSSIDMerged, isOrientationMerged);

                    KNNFormatStorage.store(compressFile, offlineMap);
                }
            } else {
                offlineMap = KNNFormatStorage.load(compressFile);
            }

        }

        return offlineMap;
    }

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

    private static boolean checkReadAvailability() {

        boolean isReadable = false;
        String state = Environment.getExternalStorageState();

        if (Environment.MEDIA_MOUNTED.equals(state)) {
            // We can read and write the media
            isReadable = true;
        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            // We can only read the media
            isReadable = true;
        }

        return isReadable;
    }
}

