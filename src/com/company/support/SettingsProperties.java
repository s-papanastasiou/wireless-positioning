/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.company.support;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

/**
 *
 * @author SST3ALBISG
 */
public class SettingsProperties extends BaseProperties {

    @Override
    protected final String propsFilename() {
        return "settings.properties";
    }
    
    // Logging headers /////////////////////////////////////////////////////////////////////////////////////////////        
    private String trialHeader;
    private String particleResultsHeader;
    private String probabilisticResultsHeader;
    private SimpleDateFormat DATE_FORMAT;

    private String IN_SEP;
    private String OUT_SEP;

    private boolean isOutputImage;
    private boolean isTrialDetail;

    private double X_PIXELS;
    private double Y_PIXELS;

    private double IMAGE_WIDTH;
    private double IMAGE_HEIGHT;
    private double FLOOR_WIDTH;
    private double FLOOR_HEIGHT;

    private String EXTERNAL_DIRECTORY;
    private String OFFLINE_MAP;
    private String ONLINE_WIFI_DATA;
    private String INITIAL_POINTS;
    private String INERTIAL_DATA;
    private String FLOORPLAN_IMAGE;

    public enum Keys {

        OUTPUT_IMAGE,
        OUTPUT_DETAIL,
        IN_SEP,
        OUT_SEP,
        DATE_FORMAT,
        IMAGE_WIDTH,
        IMAGE_HEIGHT,
        FLOOR_WIDTH,
        FLOOR_HEIGHT,
        EXTERNAL_DIRECTORY,
        OFFLINE_MAP,
        ONLINE_WIFI_DATA,
        INITIAL_POINTS,
        INERTIAL_DATA,
        FLOORPLAN_IMAGE
    };

    public SettingsProperties() {
        super();
        
        try {
            Properties props = load();
            assignKeys(props);
        } catch (NumberFormatException ex) {
            System.out.println(propsFilename() + " parameter value incorrect.");
            System.out.println(ex.getMessage());
            throw new AssertionError();
        }
    }

    @Override
    protected final void assignKeys(Properties props) {

        isOutputImage = Boolean.parseBoolean(props.getProperty(Keys.OUTPUT_IMAGE.name()));
        isTrialDetail = Boolean.parseBoolean(props.getProperty(Keys.OUTPUT_DETAIL.name()));

        IN_SEP = props.getProperty(Keys.IN_SEP.name());
        OUT_SEP = props.getProperty(Keys.OUT_SEP.name());

        trialHeader = "Point_No" + OUT_SEP + "Trial_X" + OUT_SEP + "Trial_Y" + OUT_SEP + "Distance" + OUT_SEP + "Pos_X" + OUT_SEP + "Pos_Y";
        particleResultsHeader = "BSSIDMerged" + OUT_SEP + "OrientationMerged" + OUT_SEP + "ForceToMap" + OUT_SEP + "KValue" + OUT_SEP + "InitialReadings" + OUT_SEP + "SpeedBreak" + OUT_SEP + "ParticleCount" + OUT_SEP + "CloudRange" + OUT_SEP + "CloudDisplacement" + OUT_SEP + "MeanDistance" + OUT_SEP + "StdDev";
        probabilisticResultsHeader = "BSSIDMerged" + OUT_SEP + "OrientationMerged" + OUT_SEP + "ForceToMap" + OUT_SEP + "KValue" + OUT_SEP + "MeanDistance" + OUT_SEP + "StdDev";
        DATE_FORMAT = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy");

        IMAGE_WIDTH = Double.parseDouble(props.getProperty(Keys.IMAGE_WIDTH.name()));
        IMAGE_HEIGHT = Double.parseDouble(props.getProperty(Keys.IMAGE_HEIGHT.name()));
        FLOOR_WIDTH = Double.parseDouble(props.getProperty(Keys.FLOOR_WIDTH.name()));
        FLOOR_HEIGHT = Double.parseDouble(props.getProperty(Keys.FLOOR_HEIGHT.name()));

        X_PIXELS = IMAGE_WIDTH / FLOOR_WIDTH;
        Y_PIXELS = IMAGE_HEIGHT / FLOOR_HEIGHT;
        
        EXTERNAL_DIRECTORY = props.getProperty(Keys.EXTERNAL_DIRECTORY.name());
        OFFLINE_MAP = props.getProperty(Keys.OFFLINE_MAP.name());
        ONLINE_WIFI_DATA = props.getProperty(Keys.ONLINE_WIFI_DATA.name());
        INITIAL_POINTS = props.getProperty(Keys.INITIAL_POINTS.name());
        INERTIAL_DATA = props.getProperty(Keys.INERTIAL_DATA.name());
        FLOORPLAN_IMAGE = props.getProperty(Keys.FLOORPLAN_IMAGE.name());        
    }

    public String TrialHeader() {
        return trialHeader;
    }

    public String ParticleResultsHeader() {
        return particleResultsHeader;
    }

    public String ProbabilisticResultsHeader() {
        return probabilisticResultsHeader;
    }

    public String formatDate(Date date) {
        return DATE_FORMAT.format(date);
    }

    public String IN_SEP() {
        return IN_SEP;
    }

    public String OUT_SEP() {
        return OUT_SEP;
    }

    public boolean isOutputImage() {
        return isOutputImage;
    }

    public boolean isTrialDetail() {
        return isTrialDetail;
    }

    public double X_PIXELS() {
        return X_PIXELS;
    }

    public double Y_PIXELS() {
        return Y_PIXELS;
    }

    public double IMAGE_WIDTH() {
        return IMAGE_WIDTH;
    }

    public double IMAGE_HEIGHT() {
        return IMAGE_HEIGHT;
    }

    public double FLOOR_WIDTH() {
        return FLOOR_WIDTH;
    }

    public double FLOOR_HEIGHT() {
        return FLOOR_HEIGHT;
    }

    public String EXTERNAL_DIRECTORY() {
        return EXTERNAL_DIRECTORY;
    }

    public String OFFLINE_MAP() {
        return OFFLINE_MAP;
    }

    public String ONLINE_WIFI_DATA() {
        return ONLINE_WIFI_DATA;
    }

    public String INITIAL_POINTS() {
        return INITIAL_POINTS;
    }

    public String INERTIAL_DATA() {
        return INERTIAL_DATA;
    }

    public String FLOORPLAN_IMAGE() {
        return FLOORPLAN_IMAGE;
    }
    
    
    
}
