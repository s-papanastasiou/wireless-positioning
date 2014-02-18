/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.company.support;

import java.io.File;
import java.util.EnumMap;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author SST3ALBISG
 */
public class FilterProperties extends BaseProperties {
    
    private static final Logger logger = LoggerFactory.getLogger(FilterProperties.class);        
    
    private Double JITTER_OFFSET;
    private final Float[] ACCELERATION_OFFSET = new Float[3];
    private final EnumMap<Threshold, Float> CLOUD_BOUNDARY = new EnumMap<>(Threshold.class);
    private final EnumMap<Threshold, Integer> CLOUD_PARTICLE_CREATION = new EnumMap<>(Threshold.class);
    
    private Boolean isLoaded = false;
    private File propsFile;
    
    public enum Threshold {UPPER, MID, LOWER, BASE}
    
    public enum FilterKeys {
        JITTER_OFFSET,
        ACCELERATION_OFFSET_X,
        ACCELERATION_OFFSET_Y,
        ACCELERATION_OFFSET_Z,
        CLOUD_BOUNDARY_UPPER,
        CLOUD_BOUNDARY_MID,
        CLOUD_BOUNDARY_LOWER,
        CLOUD_PARTICLE_CREATION_UPPER,
        CLOUD_PARTICLE_CREATION_MID,
        CLOUD_PARTICLE_CREATION_LOWER,
        CLOUD_PARTICLE_CREATION_BASE        
    };
    
    public FilterProperties(File propsFile) {
        super();

        try {
            this.propsFile = propsFile;
            Properties props = load(propsFile);
            if (!props.isEmpty()) {
                checkAllKeys(props);
                assignKeys(props);
                status();
                isLoaded = true;
            }
        } catch (NumberFormatException ex) {
            logger.info("{} parameter value incorrect.", propsFilename());
            logger.info(ex.getMessage());
            throw new AssertionError();
        }
    }
    
     @Override
    protected final String propsFilename() {
        return propsFile.getName();
    }
    
    @Override
    protected final void assignKeys(Properties props) throws NumberFormatException {
        JITTER_OFFSET = Double.parseDouble(props.getProperty(FilterKeys.JITTER_OFFSET.name()));
        ACCELERATION_OFFSET[0] = Float.parseFloat(props.getProperty(FilterKeys.ACCELERATION_OFFSET_X.name()));
        ACCELERATION_OFFSET[1] = Float.parseFloat(props.getProperty(FilterKeys.ACCELERATION_OFFSET_Y.name()));
        ACCELERATION_OFFSET[2] = Float.parseFloat(props.getProperty(FilterKeys.ACCELERATION_OFFSET_Z.name()));        
        
        Float up = Float.parseFloat(props.getProperty(FilterKeys.CLOUD_BOUNDARY_UPPER.name()));
        Float mid = Float.parseFloat(props.getProperty(FilterKeys.CLOUD_BOUNDARY_MID.name()));
        Float low = Float.parseFloat(props.getProperty(FilterKeys.CLOUD_BOUNDARY_LOWER.name()));        
        
        CLOUD_BOUNDARY.put(Threshold.UPPER, up);
        CLOUD_BOUNDARY.put(Threshold.MID, mid);
        CLOUD_BOUNDARY.put(Threshold.LOWER, low);
        CLOUD_BOUNDARY.put(Threshold.BASE, 0f);
        
        Integer upper = Integer.parseInt(props.getProperty(FilterKeys.CLOUD_PARTICLE_CREATION_UPPER.name()));
        Integer middle = Integer.parseInt(props.getProperty(FilterKeys.CLOUD_PARTICLE_CREATION_MID.name()));
        Integer lower = Integer.parseInt(props.getProperty(FilterKeys.CLOUD_PARTICLE_CREATION_LOWER.name()));
        Integer base = Integer.parseInt(props.getProperty(FilterKeys.CLOUD_PARTICLE_CREATION_BASE.name()));
        
        CLOUD_PARTICLE_CREATION.put(Threshold.UPPER, upper);
        CLOUD_PARTICLE_CREATION.put(Threshold.MID, middle);
        CLOUD_PARTICLE_CREATION.put(Threshold.LOWER, lower);
        CLOUD_PARTICLE_CREATION.put(Threshold.BASE, base);
        
    }

    @Override
    protected final void checkAllKeys(Properties props) {
        for (FilterKeys key : FilterKeys.values()) {
            if (!props.containsKey(key.name())) {
                logger.info("{} file not setup correctly: {}", propsFilename(), key);
                throw new AssertionError();
            }
        }
    }
    
    private void status(){
        logger.debug("JITTER OFFSET: {}", JITTER_OFFSET);
        logger.debug("ACCELERATION OFFSET x: {} y: {} z: {}", ACCELERATION_OFFSET[0], ACCELERATION_OFFSET[1], ACCELERATION_OFFSET[2]);
        logger.debug("CLOUD BOUNDARY upper: {} mid: {} lower: {} base: {}", CLOUD_BOUNDARY.get(Threshold.UPPER), CLOUD_BOUNDARY.get(Threshold.MID), CLOUD_BOUNDARY.get(Threshold.LOWER), CLOUD_BOUNDARY.get(Threshold.BASE));
        logger.debug("CLOUD PARTICLE CREATION upper : {} mid: {} lower: {} base: {}", CLOUD_PARTICLE_CREATION.get(Threshold.UPPER), CLOUD_PARTICLE_CREATION.get(Threshold.MID), CLOUD_PARTICLE_CREATION.get(Threshold.LOWER), CLOUD_PARTICLE_CREATION.get(Threshold.BASE));
    }

    public Double JITTER_OFFSET() {
        return JITTER_OFFSET;
    }

    public Float[] ACCELERATION_OFFSET() {
        return ACCELERATION_OFFSET;
    }

    public EnumMap<Threshold, Float> CLOUD_BOUNDARY() {
        return CLOUD_BOUNDARY;
    }

    public EnumMap<Threshold, Integer> CLOUD_PARTICLE_CREATION() {
        return CLOUD_PARTICLE_CREATION;
    }

    public Boolean isLoaded() {
        return isLoaded;
    }
    
    
    
}
