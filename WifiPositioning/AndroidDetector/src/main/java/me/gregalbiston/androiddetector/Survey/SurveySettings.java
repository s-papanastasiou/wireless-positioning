package me.gregalbiston.androiddetector.survey;

import android.os.Environment;
import datastorage.RoomInfo;
import filehandling.RoomInfoLoader;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Loads survey parameters (Room names and dimensions, level of accuracy and number of orientations.
 * @author Greg Albiston
 */
public class SurveySettings {

    protected List<String> accuracy = new ArrayList<>(Arrays.asList("1", "2", "3", "4", "5"));
    protected List<String> orientation = new ArrayList<>(Arrays.asList("1", "4", "8"));
    protected List<RoomInfo> roomInfo = new ArrayList<>();

    public SurveySettings(String directory, String dataFilename, String fieldSeparator) {

        String state = Environment.getExternalStorageState();

        if (Environment.MEDIA_MOUNTED.equals(state)) {
            File inputFile;
            File dir = Environment.getExternalStoragePublicDirectory(directory);

            inputFile = new File(dir, dataFilename);
            if (inputFile.exists())
                roomInfo = RoomInfoLoader.loadList(inputFile, fieldSeparator);
            else
                roomInfo = RoomInfoLoader.defaultList();
        }
    }

    public List<String> getAccuracy() {
        return accuracy;
    }

    public List<String> getOrientation() {
        return orientation;
    }

    public List<String> getRoomNames() {
        List<String> roomNames = new ArrayList<>();

        for (RoomInfo room : roomInfo) {
            roomNames.add(room.getName());
        }

        return roomNames;
    }

    public int[] selectRoom(int roomIndex, int accuracyIndex, int orientationIndex) {
        RoomInfo room = roomInfo.get(roomIndex);

        float acc = Float.parseFloat(accuracy.get(accuracyIndex));
        int xValue;
        int yValue;
        int wValue = Integer.parseInt(orientation.get(orientationIndex));

        //check whether no dimensions have been provided and unlock values if it has.
        if (room.getWidth() == 0)
            xValue = 100;
        else
            xValue = (int) (room.getWidth() / acc);

        if (room.getHeight() == 0)
            yValue = 100;
        else
            yValue = (int) (room.getHeight() / acc);


        return new int[]{xValue, yValue, wValue, room.isRoom()};
    }
}
