package me.gregalbiston.androiddetector.survey;

import android.os.Environment;
import filehandling.RoomInfo;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Gerg
 * Date: 19/06/13
 * Time: 14:14
 * Loads survey parameters (Room names and dimensions, level of accuracy and number of orientations.
 */
public class SurveySettings {

    protected List<String> accuracy = new ArrayList<String>(Arrays.asList("1", "2", "3", "4", "5"));
    protected List<String> orientation = new ArrayList<String>(Arrays.asList("1", "4", "8"));
    protected List<RoomInfo> roomInfo = new ArrayList<RoomInfo>();

    public SurveySettings(String directory, String dataFilename, String fieldSeparator) {

        String state = Environment.getExternalStorageState();

        if (Environment.MEDIA_MOUNTED.equals(state)) {
            File inputFile;
            File dir = Environment.getExternalStoragePublicDirectory(directory);

            inputFile = new File(dir, dataFilename);
            if (inputFile.exists())
                roomInfo = RoomInfo.loadList(inputFile, fieldSeparator);
            else
                roomInfo = RoomInfo.defaultList();
        }
    }

    public List<String> getAccuracy() {
        return accuracy;
    }

    public List<String> getOrientation() {
        return orientation;
    }

    public List<String> getRoomNames() {
        List<String> roomNames = new ArrayList<String>();

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
