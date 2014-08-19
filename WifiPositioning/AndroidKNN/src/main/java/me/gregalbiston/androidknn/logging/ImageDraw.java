package me.gregalbiston.androidknn.logging;

import android.graphics.drawable.Drawable;
import me.gregalbiston.androidknn.VisActivity;
import me.gregalbiston.androidknn.display.PointResults;

/**
 * Created with IntelliJ IDEA.
 * User: Greg Albiston
 * Date: 03/08/13
 * Time: 16:58
 * Write results to an image file.
 */
public class ImageDraw {

    protected static final String LOG_FILE_PREFIX = "Error";
    protected static final String LOG_FILE_LABEL = "Log";
    protected static final String LOG_FILE_EXTENSION = ".txt";
    protected static final String LOG_DIRECTORY = VisActivity.FILE_DIRECTORY + "/FloorPlans";
    protected static final String LOG_MIME_TYPE = "text/rtf";

    public static void draw(PointResults pointResults, Drawable floorPlan, String fileLabel){



    }

}
