package me.gregalbiston.androidknn.dataload;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import me.gregalbiston.androidknn.R;
import processing.DistanceMeasure;

/**
 * Created with IntelliJ IDEA.
 * User: Greg Albiston
 * Date: 01/08/13
 * Time: 10:48
 * Handles all the preferences contained in the menu settings area.
 */
public class SettingsController implements SharedPreferences.OnSharedPreferenceChangeListener {

    protected static final String DATA_TYPE = "data_type";
    protected static final String SHOW_LOG = "show_log";
    protected static final String SHOW_PATH = "show_path";
    protected static final String SHOW_ESTIMATES = "show_estimates";
    protected static final String SHOW_GRID = "show_grid";
    protected static final String USE_VARIANCE = "use_variance";
    protected static final String FILTER_BSSID = "filter_ssid";
    protected static final String DIST_MEASURE = "dist_measure";
    protected static final String K_LIMIT = "k_limit";
    protected static final String VAR_COUNT = "var_count";
    protected static final String VAR_LIMIT = "var_limit";
    protected static final String COLOUR_FINAL = "colour_final";
    protected static final String COLOUR_SCAN = "colour_scan";
    protected static final String COLOUR_ESTIMATES = "colour_estimates";
    protected static final String COLOUR_GRID = "colour_grid";

    protected Boolean isShowRSSI;
    protected Boolean isShowPath;
    protected Boolean isShowLog;
    protected Boolean isShowEstimates;
    protected Boolean isShowGrid;
    protected Boolean isFilterBSSID;
    protected Boolean isVariance;
    protected int kLimit;
    protected int varCount;
    protected double varLimit;
    protected DistanceMeasure distMeasure;

    protected int colourFinal;
    protected int colourScan;
    protected int colourEstimates;

    protected int colourGrid;

    protected DataManager dataManager;


    public SettingsController(DataManager dataManager, Context context) {

        this.dataManager = dataManager;

        //set the preferences to default values if not already set
        PreferenceManager.setDefaultValues(context, R.xml.settings, false);

        //get the latest values
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        preferences.registerOnSharedPreferenceChangeListener(this);

        isShowRSSI = preferences.getBoolean(DATA_TYPE, true);
        isShowLog = preferences.getBoolean(SHOW_LOG, true);
        isShowPath = preferences.getBoolean(SHOW_PATH, true);
        isShowEstimates = preferences.getBoolean(SHOW_ESTIMATES, true);
        isShowGrid = preferences.getBoolean(SHOW_GRID, true);
        isVariance = preferences.getBoolean(USE_VARIANCE, false);
        isFilterBSSID = preferences.getBoolean(FILTER_BSSID, false);
        kLimit = Integer.parseInt(preferences.getString(K_LIMIT, "5"));
        varCount = Integer.parseInt(preferences.getString(VAR_COUNT, "5"));
        varLimit = Double.parseDouble(preferences.getString(VAR_LIMIT, "1.0"));

        String distString = preferences.getString(DIST_MEASURE, DistanceMeasure.Manhattan.toString());
        int distIndex = Integer.parseInt(distString);
        DistanceMeasure[] distValues = DistanceMeasure.values();
        distMeasure = distValues[distIndex];


        //find the current colour settings for points
        String colour = preferences.getString(COLOUR_FINAL, "Blue");
        colourFinal = Color.parseColor(colour.toLowerCase());

        colour = preferences.getString(COLOUR_FINAL, "Red");
        colourScan = Color.parseColor(colour.toLowerCase());

        colour = preferences.getString(COLOUR_FINAL, "Magenta");
        colourEstimates = Color.parseColor(colour.toLowerCase());

        colour = preferences.getString(COLOUR_GRID, "Blue");
        colourGrid = Color.parseColor(colour.toLowerCase());

    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        switch (key) {
            case DATA_TYPE:       //TODO change is applied immediately therefore current radio map and newRoute order might need considering
                isShowRSSI = sharedPreferences.getBoolean(DATA_TYPE, true);
                dataManager.showType(isShowRSSI);
                break;
            case SHOW_LOG:
                isShowLog = sharedPreferences.getBoolean(SHOW_LOG, true);
                dataManager.showLog(isShowLog);
                break;
            case SHOW_PATH:
                isShowPath = sharedPreferences.getBoolean(SHOW_PATH, true);
                dataManager.showPath(isShowPath);
                break;
            case SHOW_ESTIMATES:
                isShowEstimates = sharedPreferences.getBoolean(SHOW_ESTIMATES, true);
                dataManager.showEstimates(isShowEstimates);
                break;
            case SHOW_GRID:     //certain colours aren't showing the grid
                isShowGrid = sharedPreferences.getBoolean(SHOW_GRID, true);
                dataManager.showGrid(isShowGrid);
                break;
            case COLOUR_GRID:
                String colourG = sharedPreferences.getString(COLOUR_GRID, "Blue");
                colourGrid = Color.parseColor(colourG.toLowerCase());
                dataManager.setGridColour(colourGrid);
                break;
            case COLOUR_FINAL:
            case COLOUR_SCAN:
            case COLOUR_ESTIMATES:
                String colour = sharedPreferences.getString(COLOUR_FINAL, "Blue");
                colourFinal = Color.parseColor(colour.toLowerCase());

                colour = sharedPreferences.getString(COLOUR_SCAN, "Red");
                colourScan = Color.parseColor(colour.toLowerCase());

                colour = sharedPreferences.getString(COLOUR_ESTIMATES, "Magenta");
                colourEstimates = Color.parseColor(colour.toLowerCase());
                dataManager.setPointResultsColours(colourFinal, colourScan, colourEstimates);
                break;
            case USE_VARIANCE:                                            //TODO showing as Magnetic/RSSI rather than ON/OFF
                isVariance = sharedPreferences.getBoolean(USE_VARIANCE, false);
                break;
            case FILTER_BSSID:
                isFilterBSSID = sharedPreferences.getBoolean(FILTER_BSSID, false);
                break;
            case DIST_MEASURE:
                String distString = sharedPreferences.getString(DIST_MEASURE, DistanceMeasure.Manhattan.toString());
                int distIndex = Integer.parseInt(distString);
                DistanceMeasure[] distValues = DistanceMeasure.values();
                distMeasure = distValues[distIndex];
                break;
            case K_LIMIT:
                kLimit = Integer.parseInt(sharedPreferences.getString(K_LIMIT, "5"));
                break;
            case VAR_COUNT:
                varCount = Integer.parseInt(sharedPreferences.getString(VAR_COUNT, "5"));
                break;
            case VAR_LIMIT:
                varLimit = Double.parseDouble(sharedPreferences.getString(VAR_LIMIT, "1.0"));
                break;
            default:
                throw new AssertionError("Unrecognised shared preference " + key);

        }

    }

    public Boolean getFilterBSSID() {
        return isFilterBSSID;
    }

    public Boolean getVariance() {
        return isVariance;
    }

    public int getkLimit() {
        return kLimit;
    }

    public int getVarCount() {
        return varCount;
    }

    public double getVarLimit() {
        return varLimit;
    }

    public DistanceMeasure getDistMeasure() {
        return distMeasure;
    }
}
