/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simulation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import configuration.FileController;
import configuration.GenerateTrialProperties;
import configuration.SettingsProperties;

/**
 *
 * @author Greg Albiston, Johan Chateau & Pierre Rousseau
 */
public class ParticleTrial {

    private static final Logger logger = LoggerFactory.getLogger(ParticleTrial.class);
    
    private final boolean isBSSIDMerged;
    private final boolean isOrientationMerged;
    private final int K;
    private final int initRSSIReadings;
    private final int particleCount;
    private final int speedBreak;
    private final double cloudRange;
    private final double cloudDisplacementCoefficient;
    private final boolean isForceToOfflineMap;
    private final String valuesStr;
    private final String titleStr;
    private final String prefix = "particle";
    private final String sep = "-";

    public ParticleTrial(boolean BSSIDMerged, boolean orientationMerged, boolean forceToOfflineMap, int k, int initRSSIReadings, int particleCount, int speedBreak, double cloudRange, double cloudDisplacementCoefficient, String OUT_SEP) {
        this.isBSSIDMerged = BSSIDMerged;
        this.isOrientationMerged = orientationMerged;
        this.isForceToOfflineMap = forceToOfflineMap;
        this.K = k;
        this.initRSSIReadings = initRSSIReadings;
        this.particleCount = particleCount;
        this.speedBreak = speedBreak;
        this.cloudRange = cloudRange;
        this.cloudDisplacementCoefficient = cloudDisplacementCoefficient;

        this.valuesStr = isBSSIDMerged + OUT_SEP + isOrientationMerged + OUT_SEP + isForceToOfflineMap + OUT_SEP
                + K + OUT_SEP + initRSSIReadings + OUT_SEP
                + particleCount + OUT_SEP + speedBreak + OUT_SEP + cloudRange + OUT_SEP
                + cloudDisplacementCoefficient;
        this.titleStr = prefix + sep + isBSSIDMerged + sep + isOrientationMerged + sep + isForceToOfflineMap + sep
                + K + sep + initRSSIReadings + sep
                + particleCount + sep + speedBreak + sep + cloudRange + sep
                + cloudDisplacementCoefficient;
    }

    public ParticleTrial(String[] parts, String OUT_SEP) throws ParseException {
        this.isBSSIDMerged = Boolean.parseBoolean(parts[0]);
        this.isOrientationMerged = Boolean.parseBoolean(parts[1]);
        this.isForceToOfflineMap = Boolean.parseBoolean(parts[2]);
        this.K = Integer.parseInt(parts[3]);
        this.initRSSIReadings = Integer.parseInt(parts[4]);
        this.particleCount = Integer.parseInt(parts[5]);
        this.speedBreak = Integer.parseInt(parts[6]);
        this.cloudRange = Double.parseDouble(parts[7]);
        this.cloudDisplacementCoefficient = Double.parseDouble(parts[8]);

        this.valuesStr = isBSSIDMerged + OUT_SEP + isOrientationMerged + OUT_SEP + isForceToOfflineMap + OUT_SEP
                + K + OUT_SEP + initRSSIReadings + OUT_SEP
                + particleCount + OUT_SEP + speedBreak + OUT_SEP + cloudRange + OUT_SEP
                + cloudDisplacementCoefficient;
        this.titleStr = prefix + sep + isBSSIDMerged + sep + isOrientationMerged + sep + isForceToOfflineMap + sep
                + K + sep + initRSSIReadings + sep
                + particleCount + sep + speedBreak + sep + cloudRange + sep
                + cloudDisplacementCoefficient;
    }

    public boolean isBSSIDMerged() {
        return isBSSIDMerged;
    }

    public boolean isOrientationMerged() {
        return isOrientationMerged;
    }

    public int getK() {
        return K;
    }

    public int getInitRSSIReadings() {
        return initRSSIReadings;
    }

    public int getParticleCount() {
        return particleCount;
    }

    public int getSpeedBreak() {
        return speedBreak;
    }

    public double getCloudRange() {
        return cloudRange;
    }

    public double getCloudDisplacementCoefficient() {
        return cloudDisplacementCoefficient;
    }

    public boolean isForceToOfflineMap() {
        return isForceToOfflineMap;
    }

    public String getTitle() {
        return titleStr;
    }

    public String getValues() {
        return valuesStr;
    }

    @Override
    public String toString() {
        return isBSSIDMerged + ":" + isOrientationMerged + ":" + isForceToOfflineMap + ":" + K + ":" + initRSSIReadings + ":" + particleCount + ":" + speedBreak + ":" + cloudRange + ":" + cloudDisplacementCoefficient;
    }

    public static List<ParticleTrial> generate(boolean isBSSIDMerged, boolean isOrientationMerged, boolean isForcedToOfflineMap, int kValue, GenerateTrialProperties tp, String OUT_SEP) {

        List<ParticleTrial> settings = new ArrayList<>();

        for (int init_counter = tp.InitRSSIReadings_MIN(); init_counter <= tp.InitRSSIReadings_MAX(); init_counter += tp.InitRSSIReadings_INC()) {
            for (int particle_counter = tp.ParticleCount_MIN(); particle_counter <= tp.ParticleCount_MAX(); particle_counter += tp.ParticleCount_INC()) {
                for (int speed_counter = tp.SpeedBreak_MIN(); speed_counter <= tp.SpeedBreak_MAX(); speed_counter += tp.SpeedBreak_INC()) {
                    for (double range_counter = tp.CloudRange_MIN(); range_counter <= tp.CloudRange_MAX(); range_counter += tp.CloudRange_INC()) {
                        for (double displacement_counter = tp.CloudDispCoeff_MIN(); displacement_counter <= tp.CloudDispCoeff_MAX(); displacement_counter += tp.CloudDispCoeff_INC()) {
                            ParticleTrial setting = new ParticleTrial(isBSSIDMerged, isOrientationMerged, isForcedToOfflineMap, kValue, init_counter, particle_counter, speed_counter, range_counter, displacement_counter, OUT_SEP);
                            settings.add(setting);
                            //logger.info(setting.toString());
                        }
                    }
                }
            }
        }

        return settings;
    }

    public static List<ParticleTrial> load(SettingsProperties sp, FileController fc) {

        String SEP = sp.IN_SEP();
        String[] HEADER = sp.PARTICLE_HEADER();
        File inputFile = fc.specificParticle;

        List<ParticleTrial> parTrialList = new ArrayList<>();

        if (inputFile.isFile()) {
            int lineCounter = 0;
            try {

                try (BufferedReader dataReader = new BufferedReader(new FileReader(inputFile))) {

                    String line = dataReader.readLine(); //Read the header
                    String[] parts = line.split(SEP);
                    if (SettingsProperties.headerCheck(parts, HEADER)) {
                        int headerSize = HEADER.length;
                        while ((line = dataReader.readLine()) != null) {
                            lineCounter++;
                            parts = line.split(SEP);
                            if (parts.length == headerSize) {
                                try {
                                    parTrialList.add(new ParticleTrial(parts, sp.OUT_SEP()));

                                } catch (ParseException ex) {
                                    logger.error("Error parsing line: {} {}", lineCounter, ex.getMessage());
                                }
                            } else {
                                logger.error("Data items count do not match headings count. Line: {}", lineCounter);
                            }
                        }

                        logger.info("Particle Trials read successfully. Lines read: {}", lineCounter);
                    } else {
                        logger.error("Headings are not as expected.");
                        if (parts.length == 1) {
                            logger.error("Expecting separator: {} Found: {}", SEP, line);
                        } else {
                            logger.error("Expecting: {} Found: {}", SettingsProperties.toStringHeadings(SEP, HEADER), line);
                        }
                    }
                }
            } catch (IOException x) {
                logger.error(x.getLocalizedMessage());
            }
        } else {
            logger.error("Particle Trial file not found: {}", inputFile.getPath());
        }

        return parTrialList;

    }

}
