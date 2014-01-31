/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.company.support;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 *
 * @author SST3ALBISG
 */
public class TrialProperties {

    
    private int K_MIN;
    private int K_MAX;
    private int K_INC;
    private Double buildingOrientation;

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

    private static final String propsFilename = "trial.properties";
    
    public static enum Keys {

        K_MIN,
        K_MAX,
        K_INC,
        buildingOrientation,
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

    
    public TrialProperties() {
        
        String workDirPath = System.getProperty("user.dir");
        File workDir = new File(workDirPath);
        File propsFile = new File(workDir, propsFilename);
        InputStream in;
        try {
            if(propsFile.isFile()){
                in = new FileInputStream(propsFile);
                System.out.println("Properties file located.");
            }else{
                in = getClass().getClassLoader().getResourceAsStream(propsFilename);
                System.out.println("Default properties file used.");
            }
            Properties props = new Properties();

            props.load(in);
            in.close();
            
            checkAllKeys(props);
            K_MIN = Integer.parseInt(props.getProperty(Keys.K_MIN.name()));
            K_MAX = Integer.parseInt(props.getProperty(Keys.K_MAX.name()));
            K_INC = Integer.parseInt(props.getProperty(Keys.K_INC.name()));
            buildingOrientation = Double.parseDouble(props.getProperty(Keys.buildingOrientation.name()));
            initRSSIReadings_MIN = Integer.parseInt(props.getProperty(Keys.initRSSIReadings_MIN.name()));
            initRSSIReadings_MAX = Integer.parseInt(props.getProperty(Keys.initRSSIReadings_MAX.name()));
            initRSSIReadings_INC = Integer.parseInt(props.getProperty(Keys.initRSSIReadings_INC.name()));
            particleCount_MIN = Integer.parseInt(props.getProperty(Keys.particleCount_MIN.name()));
            particleCount_MAX = Integer.parseInt(props.getProperty(Keys.particleCount_MAX.name()));
            particleCount_INC = Integer.parseInt(props.getProperty(Keys.particleCount_INC.name()));
            speedBreak_MIN = Integer.parseInt(props.getProperty(Keys.speedBreak_MIN.name()));
            speedBreak_MAX = Integer.parseInt(props.getProperty(Keys.speedBreak_MAX.name()));
            speedBreak_INC = Integer.parseInt(props.getProperty(Keys.speedBreak_INC.name()));
            cloudRange_MIN = Double.parseDouble(props.getProperty(Keys.cloudRange_MIN.name()));
            cloudRange_MAX = Double.parseDouble(props.getProperty(Keys.cloudRange_MAX.name()));
            cloudRange_INC = Double.parseDouble(props.getProperty(Keys.cloudRange_INC.name()));
            cloudDispCoeff_MIN = Double.parseDouble(props.getProperty(Keys.cloudDispCoeff_MIN.name()));
            cloudDispCoeff_MAX = Double.parseDouble(props.getProperty(Keys.cloudDispCoeff_MAX.name()));
            cloudDispCoeff_INC = Double.parseDouble(props.getProperty(Keys.cloudDispCoeff_INC.name()));
                        
        } catch (IOException ex) {
            System.out.println("Cannot read properties file.");
            System.out.println(ex.getMessage());
            throw new AssertionError();
        } catch (NumberFormatException ex) {
            System.out.println("Properties parameter value incorrect.");
            System.out.println(ex.getMessage());
            throw new AssertionError();
        }
    }        

    private void checkAllKeys(Properties props) {
        for (Keys key : Keys.values()) {
            if (!props.containsKey(key.name())) {
                System.out.println("Properties file not setup correctly: " + key.name());
                throw new AssertionError();
            }
        }
    }

    public int getK_MIN() {
        return K_MIN;
    }

    public int getK_MAX() {
        return K_MAX;
    }

    public int getK_INC() {
        return K_INC;
    }

    public double getBuildingOrientation() {
        return buildingOrientation;
    }

    public int getInitRSSIReadings_MIN() {
        return initRSSIReadings_MIN;
    }

    public int getInitRSSIReadings_MAX() {
        return initRSSIReadings_MAX;
    }

    public int getInitRSSIReadings_INC() {
        return initRSSIReadings_INC;
    }

    public int getParticleCount_MIN() {
        return particleCount_MIN;
    }

    public int getParticleCount_MAX() {
        return particleCount_MAX;
    }

    public int getParticleCount_INC() {
        return particleCount_INC;
    }

    public int getSpeedBreak_MIN() {
        return speedBreak_MIN;
    }

    public int getSpeedBreak_MAX() {
        return speedBreak_MAX;
    }

    public int getSpeedBreak_INC() {
        return speedBreak_INC;
    }

    public double getCloudRange_MIN() {
        return cloudRange_MIN;
    }

    public double getCloudRange_MAX() {
        return cloudRange_MAX;
    }

    public double getCloudRange_INC() {
        return cloudRange_INC;
    }

    public double getCloudDispCoeff_MIN() {
        return cloudDispCoeff_MIN;
    }

    public double getCloudDispCoeff_MAX() {
        return cloudDispCoeff_MAX;
    }

    public double getCloudDispCoeff_INC() {
        return cloudDispCoeff_INC;
    }

    public static String getPropsFilename() {
        return propsFilename;
    }   
    
}
