/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.company.support;

import java.util.Properties;

/**
 *
 * @author SST3ALBISG
 */
public class GenerateTrialProperties extends BaseProperties {

    @Override
    protected final String propsFilename() {
        return "trial.properties";
    }

    private int K_MIN;
    private int K_MAX;
    private int K_INC;

    private int initRSSIReadings_MIN;
    private int initRSSIReadings_MAX;
    private int initRSSIReadings_INC;

    private int particleCount_MIN;
    private int particleCount_MAX;
    private int particleCount_INC;

    private int speedBreak_MIN;
    private int speedBreak_MAX;
    private int speedBreak_INC;

    private Double cloudRange_MIN;
    private Double cloudRange_MAX;
    private Double cloudRange_INC;

    private Double cloudDispCoeff_MIN;
    private Double cloudDispCoeff_MAX;
    private Double cloudDispCoeff_INC;

    private Boolean isLoaded = false;
    
    public enum TrialKeys {

        K_MIN,
        K_MAX,
        K_INC,
        initRSSIReadings_MIN,
        initRSSIReadings_MAX,
        initRSSIReadings_INC,
        particleCount_MIN,
        particleCount_MAX,
        particleCount_INC,
        speedBreak_MIN,
        speedBreak_MAX,
        speedBreak_INC,
        cloudRange_MIN,
        cloudRange_MAX,
        cloudRange_INC,
        cloudDispCoeff_MIN,
        cloudDispCoeff_MAX,
        cloudDispCoeff_INC
    };

    public GenerateTrialProperties() {
        super();

        try {
            Properties props = load();
            if (!props.isEmpty()) {
                checkAllKeys(props);
                assignKeys(props);
                isLoaded = true;
            }
        } catch (NumberFormatException ex) {
            System.out.println(propsFilename() + " parameter value incorrect.");
            System.out.println(ex.getMessage());
            throw new AssertionError();
        }
    }

    @Override
    protected final void assignKeys(Properties props) throws NumberFormatException {
        K_MIN = Integer.parseInt(props.getProperty(TrialKeys.K_MIN.name()));
        K_MAX = Integer.parseInt(props.getProperty(TrialKeys.K_MAX.name()));
        K_INC = Integer.parseInt(props.getProperty(TrialKeys.K_INC.name()));
        initRSSIReadings_MIN = Integer.parseInt(props.getProperty(TrialKeys.initRSSIReadings_MIN.name()));
        initRSSIReadings_MAX = Integer.parseInt(props.getProperty(TrialKeys.initRSSIReadings_MAX.name()));
        initRSSIReadings_INC = Integer.parseInt(props.getProperty(TrialKeys.initRSSIReadings_INC.name()));
        particleCount_MIN = Integer.parseInt(props.getProperty(TrialKeys.particleCount_MIN.name()));
        particleCount_MAX = Integer.parseInt(props.getProperty(TrialKeys.particleCount_MAX.name()));
        particleCount_INC = Integer.parseInt(props.getProperty(TrialKeys.particleCount_INC.name()));
        speedBreak_MIN = Integer.parseInt(props.getProperty(TrialKeys.speedBreak_MIN.name()));
        speedBreak_MAX = Integer.parseInt(props.getProperty(TrialKeys.speedBreak_MAX.name()));
        speedBreak_INC = Integer.parseInt(props.getProperty(TrialKeys.speedBreak_INC.name()));
        cloudRange_MIN = Double.parseDouble(props.getProperty(TrialKeys.cloudRange_MIN.name()));
        cloudRange_MAX = Double.parseDouble(props.getProperty(TrialKeys.cloudRange_MAX.name()));
        cloudRange_INC = Double.parseDouble(props.getProperty(TrialKeys.cloudRange_INC.name()));
        cloudDispCoeff_MIN = Double.parseDouble(props.getProperty(TrialKeys.cloudDispCoeff_MIN.name()));
        cloudDispCoeff_MAX = Double.parseDouble(props.getProperty(TrialKeys.cloudDispCoeff_MAX.name()));
        cloudDispCoeff_INC = Double.parseDouble(props.getProperty(TrialKeys.cloudDispCoeff_INC.name()));
    }

    @Override
    protected final void checkAllKeys(Properties props) {
        for (TrialKeys key : TrialKeys.values()) {
            if (!props.containsKey(key)) {
                System.out.println(propsFilename() + " file not setup correctly: " + key);
                throw new AssertionError();
            }
        }
    }

    public int K_MIN() {
        return K_MIN;
    }

    public int K_MAX() {
        return K_MAX;
    }

    public int K_INC() {
        return K_INC;
    }

    public int InitRSSIReadings_MIN() {
        return initRSSIReadings_MIN;
    }

    public int InitRSSIReadings_MAX() {
        return initRSSIReadings_MAX;
    }

    public int InitRSSIReadings_INC() {
        return initRSSIReadings_INC;
    }

    public int ParticleCount_MIN() {
        return particleCount_MIN;
    }

    public int ParticleCount_MAX() {
        return particleCount_MAX;
    }

    public int ParticleCount_INC() {
        return particleCount_INC;
    }

    public int SpeedBreak_MIN() {
        return speedBreak_MIN;
    }

    public int SpeedBreak_MAX() {
        return speedBreak_MAX;
    }

    public int SpeedBreak_INC() {
        return speedBreak_INC;
    }

    public double CloudRange_MIN() {
        return cloudRange_MIN;
    }

    public double CloudRange_MAX() {
        return cloudRange_MAX;
    }

    public double CloudRange_INC() {
        return cloudRange_INC;
    }

    public double CloudDispCoeff_MIN() {
        return cloudDispCoeff_MIN;
    }

    public double CloudDispCoeff_MAX() {
        return cloudDispCoeff_MAX;
    }

    public double CloudDispCoeff_INC() {
        return cloudDispCoeff_INC;
    }

    public Boolean isLoaded() {
        return isLoaded;
    }

}
