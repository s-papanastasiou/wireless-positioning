/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package configuration;

import datastorage.RoomInfo;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
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

    private Boolean isVerbose;
    private Boolean isOutputImage;
    private Boolean isTrialDetail;   

    private String OUTPUT_DIRECTORY;
    private String INPUT_DIRECTORY;
    private String OFFLINE_MAP;
    private String ONLINE_WIFI_DATA;
    private String INITIAL_POINTS;
    private String INERTIAL_DATA;
    private String FLOORPLAN_IMAGE;
    private String GENERATE_TRIAL_PROPERTIES;
    private String FILTER_PROPERTIES;

    private Double BUILD_ORIENT;

    private String SPECIFIC_PARTICLE;
    private String SPECIFIC_PROB;

    private Boolean GENERATE_PARTICLE_TRIALS;
    private Boolean GENERATE_PROB_TRIALS;
    private Boolean RUN_PARTICLE_TRIALS;
    private Boolean RUN_PROB_TRIALS;
    
    private HashMap<String, RoomInfo> ROOM_INFO;

    private final String[] PARTICLE_HEADER = {"BSSIDMerged", "OrientationMerged", "ForceToMap", "KValue", "InitialReadings", "SpeedBreak", "ParticleCount", "CloudRange", "CloudDisplacement"};

    private final String[] PROB_HEADER = {"BSSIDMerged", "OrientationMerged", "ForceToMap", "KValue"};

    public enum SettingKeys {

        VERBOSE,
        OUTPUT_IMAGE,
        OUTPUT_DETAIL,
        IN_SEP,
        OUT_SEP,
        DATE_FORMAT,
        ROOM_NAME,
        IMAGE_WIDTH,
        IMAGE_HEIGHT,
        FLOOR_WIDTH,
        FLOOR_HEIGHT,
        OUTPUT_DIRECTORY,
        INPUT_DIRECTORY,
        OFFLINE_MAP,
        ONLINE_WIFI_DATA,
        INITIAL_POINTS,
        INERTIAL_DATA,
        FLOORPLAN_IMAGE,
        GENERATE_TRIAL_PROPERTIES,
        FILTER_PROPERTIES,
        BUILD_ORIENT,
        SPECIFIC_PARTICLE,
        SPECIFIC_PROB,
        GENERATE_PARTICLE_TRIALS,
        GENERATE_PROB_TRIALS,
        RUN_PARTICLE_TRIALS,
        RUN_PROB_TRIALS
    };

    public SettingsProperties() {
        super();

        try {
            Properties props = load();
            checkAllKeys(props);
            assignKeys(props);
            status();
        } catch (NumberFormatException ex) {
            logger.info("{} parameter value incorrect.", propsFilename());
            logger.info(ex.getMessage());
            throw new AssertionError();
        }
    }

    @Override
    protected final void assignKeys(Properties props) {

        isVerbose = Boolean.parseBoolean(props.getProperty(SettingKeys.VERBOSE.name()));
        isOutputImage = Boolean.parseBoolean(props.getProperty(SettingKeys.OUTPUT_IMAGE.name()));
        isTrialDetail = Boolean.parseBoolean(props.getProperty(SettingKeys.OUTPUT_DETAIL.name()));

        IN_SEP = props.getProperty(SettingKeys.IN_SEP.name());
        OUT_SEP = props.getProperty(SettingKeys.OUT_SEP.name());

        TRIAL_HEADER = "Point_No" + OUT_SEP + "Trial_Room"+ OUT_SEP + "Trial_X" + OUT_SEP + "Trial_Y" + OUT_SEP + "Distance" + OUT_SEP + "Pos_Room" + OUT_SEP + "Pos_X" + OUT_SEP + "Pos_Y";
        PAR_RESULTS_HEADER = PARTICLE_HEADER[0] + OUT_SEP + PARTICLE_HEADER[1] + OUT_SEP + PARTICLE_HEADER[2] + OUT_SEP + PARTICLE_HEADER[3] + OUT_SEP + PARTICLE_HEADER[4] + OUT_SEP + PARTICLE_HEADER[5] + OUT_SEP + PARTICLE_HEADER[6] + OUT_SEP + PARTICLE_HEADER[7] + OUT_SEP + PARTICLE_HEADER[8] + OUT_SEP + "MeanDistance" + OUT_SEP + "StdDev";
        PRO_RESULTS_HEADER = PROB_HEADER[0] + OUT_SEP + PROB_HEADER[1] + OUT_SEP + PROB_HEADER[2] + OUT_SEP + PROB_HEADER[3] + OUT_SEP + "MeanDistance" + OUT_SEP + "StdDev";
        DATE_FORMAT = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy");

        String roomName = props.getProperty(SettingKeys.IMAGE_WIDTH.name());
        double imageWidth = Double.parseDouble(props.getProperty(SettingKeys.IMAGE_WIDTH.name()));
        double imageHeight = Double.parseDouble(props.getProperty(SettingKeys.IMAGE_HEIGHT.name()));
        double floorWidth = Double.parseDouble(props.getProperty(SettingKeys.FLOOR_WIDTH.name()));
        double floorHeight = Double.parseDouble(props.getProperty(SettingKeys.FLOOR_HEIGHT.name()));      
        ROOM_INFO = new HashMap<>();
        ROOM_INFO.put(roomName, new RoomInfo(roomName, floorWidth, floorHeight, imageWidth, imageHeight));

        OUTPUT_DIRECTORY = props.getProperty(SettingKeys.OUTPUT_DIRECTORY.name());
        INPUT_DIRECTORY = props.getProperty(SettingKeys.INPUT_DIRECTORY.name());
        OFFLINE_MAP = props.getProperty(SettingKeys.OFFLINE_MAP.name());
        ONLINE_WIFI_DATA = props.getProperty(SettingKeys.ONLINE_WIFI_DATA.name());
        INITIAL_POINTS = props.getProperty(SettingKeys.INITIAL_POINTS.name());
        INERTIAL_DATA = props.getProperty(SettingKeys.INERTIAL_DATA.name());
        FLOORPLAN_IMAGE = props.getProperty(SettingKeys.FLOORPLAN_IMAGE.name());
        GENERATE_TRIAL_PROPERTIES = props.getProperty(SettingKeys.GENERATE_TRIAL_PROPERTIES.name());
        FILTER_PROPERTIES = props.getProperty(SettingKeys.FILTER_PROPERTIES.name());

        BUILD_ORIENT = Double.parseDouble(props.getProperty(SettingKeys.BUILD_ORIENT.name()));

        SPECIFIC_PARTICLE = props.getProperty(SettingKeys.SPECIFIC_PARTICLE.name());
        SPECIFIC_PROB = props.getProperty(SettingKeys.SPECIFIC_PROB.name());

        GENERATE_PARTICLE_TRIALS = Boolean.parseBoolean(props.getProperty(SettingKeys.GENERATE_PARTICLE_TRIALS.name()));
        GENERATE_PROB_TRIALS = Boolean.parseBoolean(props.getProperty(SettingKeys.GENERATE_PROB_TRIALS.name()));
        RUN_PARTICLE_TRIALS = Boolean.parseBoolean(props.getProperty(SettingKeys.RUN_PARTICLE_TRIALS.name()));
        RUN_PROB_TRIALS = Boolean.parseBoolean(props.getProperty(SettingKeys.RUN_PROB_TRIALS.name()));
    }

    private void status() {
        if (isVerbose) {
            logger.debug("OUTPUT IMAGE: {}", isOutputImage);
            logger.debug("TRIAL DETAIL: {}", isTrialDetail);
            logger.debug("INPUT SEPARATOR: {}", IN_SEP);
            logger.debug("OUTPUT SEPARATOR: {}", OUT_SEP);
            logger.debug("INPUT DIRECTORY: {}", INPUT_DIRECTORY);
            logger.debug("OUTPUT DIRECTORY: {}", OUTPUT_DIRECTORY);
            logger.debug("BUILD ORIENT: {}", BUILD_ORIENT);
            logger.debug("ROOM INFO: {}", ROOM_INFO);            
            logger.debug("GENERATE TRIAL PROPERTIES: {}", GENERATE_TRIAL_PROPERTIES);
            logger.debug("FILTER PROPERTIES: {}", FILTER_PROPERTIES);
            logger.debug("SPECIFIC PARTICLE FILENAME: {}", SPECIFIC_PARTICLE);
            logger.debug("SPECIFIC PROB FILENAME: {}", SPECIFIC_PROB);
        }
    }

    @Override
    protected final void checkAllKeys(Properties props) {
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

    public Boolean isVerbose() {
        return isVerbose;
    }
    
    public Boolean isOutputImage() {
        return isOutputImage;
    }

    public Boolean isTrialDetail() {
        return isTrialDetail;
    }

    public String OUTPUT_DIRECTORY() {
        return OUTPUT_DIRECTORY;
    }

    public String INPUT_DIRECTORY() {
        return INPUT_DIRECTORY;
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

    public String GENERATE_TRIAL_PROPERTIES() {
        return GENERATE_TRIAL_PROPERTIES;
    }

    public String FILTER_PROPERTIES() {
        return FILTER_PROPERTIES;
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

    public Boolean GENERATE_PARTICLE_TRIALS() {
        return GENERATE_PARTICLE_TRIALS;
    }

    public Boolean GENERATE_PROB_TRIALS() {
        return GENERATE_PROB_TRIALS;
    }

    public Boolean RUN_PARTICLE_TRIALS() {
        return RUN_PARTICLE_TRIALS;
    }

    public Boolean RUN_PROB_TRIALS() {
        return RUN_PROB_TRIALS;
    }

    public String[] PARTICLE_HEADER() {
        return PARTICLE_HEADER;
    }

    public String[] PROB_HEADER() {
        return PROB_HEADER;
    }
    
    public HashMap<String, RoomInfo> ROOM_INFO(){
        return ROOM_INFO;
    }

    public static boolean headerCheck(String[] parts, String[] header) {
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

    public static String toStringHeadings(final String separator, final String[] header) {

        String result = header[0];
        for (int counter = 1; counter < header.length; counter++) {
            result += separator + header[counter];
        }
        return result;
    }

}
