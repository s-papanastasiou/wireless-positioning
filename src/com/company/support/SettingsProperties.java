/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.company.support;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author SST3ALBISG
 */
public class SettingsProperties extends BaseProperties {

    private static final Logger logger = LoggerFactory.getLogger(SettingsProperties.class);
    
    @Override
    protected final String propsFilename() {
        return "settings.properties";
    }
    
    // Logging headers /////////////////////////////////////////////////////////////////////////////////////////////        
    private String TRIAL_HEADER;
    private String PAR_RESULTS_HEADER;
    private String PRO_RESULTS_HEADER;
    private SimpleDateFormat DATE_FORMAT;

    private String IN_SEP;
    private String OUT_SEP;

    private Boolean isOutputImage;
    private Boolean isTrialDetail;

    private Double X_PIXELS;
    private Double Y_PIXELS;

    private Double IMAGE_WIDTH;
    private Double IMAGE_HEIGHT;
    private Double FLOOR_WIDTH;
    private Double FLOOR_HEIGHT;

    private String EXTERNAL_DIRECTORY;
    private String OFFLINE_MAP;
    private String ONLINE_WIFI_DATA;
    private String INITIAL_POINTS;
    private String INERTIAL_DATA;
    private String FLOORPLAN_IMAGE;
    
    private Double BUILD_ORIENT;
    
    private String SPECIFIC_PARTICLE;
    private String SPECIFIC_PROB;
    
    private final String[] PARTICLE_HEADER = {"BSSIDMerged", "OrientationMerged", "ForceToMap", "KValue", "InitialReadings", "SpeedBreak", "ParticleCount", "CloudRange", "CloudDisplacement"};
    
    private final String[] PROB_HEADER = {"BSSIDMerged", "OrientationMerged", "ForceToMap", "KValue"};
    
    public enum SettingKeys {
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
        FLOORPLAN_IMAGE,
        BUILD_ORIENT,
        SPECIFIC_PARTICLE,
        SPECIFIC_PROB
    };
    
    public SettingsProperties() {
        super();
        
        try {
            Properties props = load();
            checkAllKeys(props);
            assignKeys(props);
        } catch (NumberFormatException ex) {
            logger.info("{} parameter value incorrect.", propsFilename());
            logger.info(ex.getMessage());
            throw new AssertionError();
        }        
    }

    @Override
    protected final void assignKeys(Properties props) {

        isOutputImage = Boolean.parseBoolean(props.getProperty(SettingKeys.OUTPUT_IMAGE.name()));
        isTrialDetail = Boolean.parseBoolean(props.getProperty(SettingKeys.OUTPUT_DETAIL.name()));

        IN_SEP = props.getProperty(SettingKeys.IN_SEP.name());
        OUT_SEP = props.getProperty(SettingKeys.OUT_SEP.name());

        TRIAL_HEADER = "Point_No" + OUT_SEP + "Trial_X" + OUT_SEP + "Trial_Y" + OUT_SEP + "Distance" + OUT_SEP + "Pos_X" + OUT_SEP + "Pos_Y";
        PAR_RESULTS_HEADER = PARTICLE_HEADER[0] + OUT_SEP + PARTICLE_HEADER[1] + OUT_SEP + PARTICLE_HEADER[2] + OUT_SEP + PARTICLE_HEADER[3] + OUT_SEP + PARTICLE_HEADER[4] + OUT_SEP + PARTICLE_HEADER[5] + OUT_SEP + PARTICLE_HEADER[6] + OUT_SEP + PARTICLE_HEADER[7] + OUT_SEP + PARTICLE_HEADER[8] + OUT_SEP + "MeanDistance" + OUT_SEP + "StdDev";
        PRO_RESULTS_HEADER = PROB_HEADER[0] + OUT_SEP + PROB_HEADER[1] + OUT_SEP + PROB_HEADER[2] + OUT_SEP + PROB_HEADER[3] + OUT_SEP + "MeanDistance" + OUT_SEP + "StdDev";
        DATE_FORMAT = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy");

        IMAGE_WIDTH = Double.parseDouble(props.getProperty(SettingKeys.IMAGE_WIDTH.name()));
        IMAGE_HEIGHT = Double.parseDouble(props.getProperty(SettingKeys.IMAGE_HEIGHT.name()));
        FLOOR_WIDTH = Double.parseDouble(props.getProperty(SettingKeys.FLOOR_WIDTH.name()));
        FLOOR_HEIGHT = Double.parseDouble(props.getProperty(SettingKeys.FLOOR_HEIGHT.name()));

        X_PIXELS = IMAGE_WIDTH / FLOOR_WIDTH;
        Y_PIXELS = IMAGE_HEIGHT / FLOOR_HEIGHT;
        
        EXTERNAL_DIRECTORY = props.getProperty(SettingKeys.EXTERNAL_DIRECTORY.name());
        OFFLINE_MAP = props.getProperty(SettingKeys.OFFLINE_MAP.name());
        ONLINE_WIFI_DATA = props.getProperty(SettingKeys.ONLINE_WIFI_DATA.name());
        INITIAL_POINTS = props.getProperty(SettingKeys.INITIAL_POINTS.name());
        INERTIAL_DATA = props.getProperty(SettingKeys.INERTIAL_DATA.name());
        FLOORPLAN_IMAGE = props.getProperty(SettingKeys.FLOORPLAN_IMAGE.name());
        
        BUILD_ORIENT = Double.parseDouble(props.getProperty(SettingKeys.BUILD_ORIENT.name()));
        
        SPECIFIC_PARTICLE = props.getProperty(SettingKeys.SPECIFIC_PARTICLE.name());
        SPECIFIC_PROB = props.getProperty(SettingKeys.SPECIFIC_PROB.name());
    }
    
    @Override
    protected final void checkAllKeys(Properties props)
    {                        
        for (SettingKeys key : SettingKeys.values()) {
            if (!props.containsKey(key.name())) {                
                logger.info("{} file not setup correctly: {}", propsFilename(), key);
                throw new AssertionError();                
            }
        }        
    }

    public String TRIAL_HEADER() {
        return TRIAL_HEADER;
    }

    public String PAR_RESULTS_HEADER() {
        return PAR_RESULTS_HEADER;
    }

    public String PRO_RESULTS_HEADER() {
        return PRO_RESULTS_HEADER;
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

    public Boolean isOutputImage() {
        return isOutputImage;
    }

    public Boolean isTrialDetail() {
        return isTrialDetail;
    }

    public Double X_PIXELS() {
        return X_PIXELS;
    }

    public Double Y_PIXELS() {
        return Y_PIXELS;
    }

    public Double IMAGE_WIDTH() {
        return IMAGE_WIDTH;
    }

    public Double IMAGE_HEIGHT() {
        return IMAGE_HEIGHT;
    }

    public Double FLOOR_WIDTH() {
        return FLOOR_WIDTH;
    }

    public Double FLOOR_HEIGHT() {
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

    public Double BUILD_ORIENT() {
        return BUILD_ORIENT;
    }
    
    public String SPECIFIC_PARTICLE() {
        return SPECIFIC_PARTICLE;
    }
    
    public String SPECIFIC_PROB() {
        return SPECIFIC_PROB;
    }
    
    public String[] getPARTICLE_HEADER() {
        return PARTICLE_HEADER;
    }

    public String[] getPROB_HEADER() {
        return PROB_HEADER;
    }
    
    public static boolean headerCheck(String[] parts, String[]header){
        boolean isCorrect = true;
        
        if (parts.length == header.length) {
            for (int counter = 0; counter < parts.length; counter++) {
                if (!parts[counter].equals(header[counter])) {
                    isCorrect = false;
                    break;
                }
            }
        } else {
            isCorrect = false;
        }

        return isCorrect;        
    } 
    
    public static String toStringHeadings(final String separator, final String [] header){
        
        String result = header[0];
        for(int counter = 1; counter < header.length; counter++){
            result += separator + header[counter];
        }        
        return result;
    }
    
}
