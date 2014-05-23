/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package support;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Gerg
 */
public class ProbabilisticTrial {
    
    private static final Logger logger = LoggerFactory.getLogger(ProbabilisticTrial.class);
    
    private final boolean isBSSIDMerged;
    private final boolean isOrientationMerged;
    private final int K;    
    private final boolean isForceToOfflineMap;    
    private final String valuesStr;
    private final String titleStr;
    private final String prefix = "probabilistic";
    private final String sep = "-";
    
    public ProbabilisticTrial(boolean BSSIDMerged, boolean orientationMerged, boolean forceToOfflineMap, int k, String OUT_SEP) {
        this.isBSSIDMerged = BSSIDMerged;
        this.isOrientationMerged = orientationMerged;
        this.K = k;        
        this.isForceToOfflineMap = forceToOfflineMap;        
        this.valuesStr = isBSSIDMerged + OUT_SEP + isOrientationMerged + OUT_SEP + isForceToOfflineMap + OUT_SEP + K;
        this.titleStr = prefix + sep + isBSSIDMerged + sep + isOrientationMerged + sep + isForceToOfflineMap + sep + K;
    }
    
    public ProbabilisticTrial(String[] parts, String OUT_SEP) throws ParseException {
        this.isBSSIDMerged = Boolean.parseBoolean(parts[0]);
        this.isOrientationMerged = Boolean.parseBoolean(parts[1]);
        this.isForceToOfflineMap = Boolean.parseBoolean(parts[2]);
        this.K = Integer.parseInt(parts[3]);
        
        this.valuesStr = isBSSIDMerged + OUT_SEP + isOrientationMerged + OUT_SEP + isForceToOfflineMap + OUT_SEP + K;
        this.titleStr = prefix + sep + isBSSIDMerged + sep + isOrientationMerged + sep + isForceToOfflineMap + sep + K;
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
        return isBSSIDMerged + ":" + isOrientationMerged + ":" + isForceToOfflineMap + ":" + K;
    }
    
    public static List<ProbabilisticTrial> load(SettingsProperties sp, FileController fc) {
        
        String SEP = sp.IN_SEP();
        String[] HEADER = sp.PROB_HEADER();
        File inputFile = fc.specificProb;
        
        List<ProbabilisticTrial> proTrialList = new ArrayList<>();
        
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
                                    proTrialList.add(new ProbabilisticTrial(parts, sp.OUT_SEP()));
                                    
                                } catch (ParseException ex) {
                                    logger.error("Error parsing line: {} {}", lineCounter, ex.getMessage());
                                }
                            } else {
                                logger.error("Data items count do not match headings count. Line: {}", lineCounter);
                            }
                        }
                        
                        logger.info("Probabilistic Trials read successfully. Lines read: {}", lineCounter);
                    } else {
                        logger.error("Headings are not as expected.");
                        if (parts.length == 1) {
                            logger.error("Expecting separator: {} Found: ", SEP, line);
                        } else {
                            logger.error("Expecting: {} Found: {}", SettingsProperties.toStringHeadings(SEP, HEADER), line);
                        }
                    }
                }
            } catch (IOException x) {
                logger.error(x.getMessage());
            }
        } else {
            logger.error("Probabilistic Trial file not found: {}", inputFile.getPath());
        }
        return proTrialList;        
    }   
    
    public static List<ProbabilisticTrial> generate(boolean isBSSIDMerged, boolean isOrientationMerged, boolean isForcedToOfflineMap, int kValue, String OUT_SEP){
    
        List<ProbabilisticTrial> trialList = new ArrayList<>();
        
        ProbabilisticTrial trial = new ProbabilisticTrial(isBSSIDMerged, isOrientationMerged, isForcedToOfflineMap, kValue, OUT_SEP);
        trialList.add(trial);
        
        return trialList;
    }
}
