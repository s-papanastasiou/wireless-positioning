/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package filehandling;

import datastorage.KNNFloorPoint;
import general.AvgValue;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Greg Albiston
 */
public class KNNFormatStorage {

    private static final Logger logger = LoggerFactory.getLogger(KNNFormatStorage.class);

    public static void store(File knnFile, final HashMap<String, KNNFloorPoint> knnRadioMap) {

        try {
            //store the file for future use to save time
            FileOutputStream file = new FileOutputStream(knnFile);
            try (BufferedOutputStream bufferOutput = new BufferedOutputStream(file)) {
                logger.info("Storing location data....");

                try (ObjectOutput output = new ObjectOutputStream(bufferOutput)) {
                    output.writeObject(knnRadioMap);
                    logger.info("Location data stored");
                }
            }

        } catch (IOException ex) {
            logger.error("Error storing converted data to file.");
        }
    }

    public static HashMap<String, KNNFloorPoint> load(File knnFile) {

        HashMap<String, KNNFloorPoint> knnRadioMap = new HashMap<>();

        try {
            logger.info("Loading location data....");

            FileInputStream file = new FileInputStream(knnFile);
            try (BufferedInputStream bufferInput = new BufferedInputStream(file); ObjectInput input = new ObjectInputStream(bufferInput)) {
                knnRadioMap = (HashMap<String, KNNFloorPoint>) input.readObject();
                logger.info("Location data loaded");
            }
        } catch (IOException | ClassNotFoundException ex) {
            logger.error("Error reading location data from file.");
        }

        return knnRadioMap;
    }

    public static HashMap<String, KNNFloorPoint> load(InputStream knnStream) {

        HashMap<String, KNNFloorPoint> knnRadioMap = new HashMap<>();

        try {
            //logger.info("Loading location data....");

            //FileInputStream file = new FileInputStream(knnStream);
            try (BufferedInputStream bufferInput = new BufferedInputStream(knnStream); ObjectInput input = new ObjectInputStream(bufferInput)) {
                knnRadioMap = (HashMap<String, KNNFloorPoint>) input.readObject();
                //  logger.info("Location data loaded");
            }
        } catch (IOException | ClassNotFoundException ex) {
            //logger.error("Error reading location data from file.");
        }

        return knnRadioMap;
    }

    public void print(File outputFile, List<KNNFloorPoint> knnFloorPoints, String fieldSeparator) {
        try {
            try (BufferedWriter dataWriter = new BufferedWriter(new FileWriter(outputFile, false))) {

                StringBuilder stb = new StringBuilder();
                stb.append("Location").append(fieldSeparator).append("Attributes").append(System.getProperty("line.separator"));
                for (KNNFloorPoint floorPoint : knnFloorPoints) {
                    stb.append(floorPoint).append(fieldSeparator).append(floorPoint.toStringAttributes(fieldSeparator)).append(System.getProperty("line.separator"));
                }
                dataWriter.append(stb);
            }
        } catch (IOException ex) {
            logger.error(ex.getMessage());
        }
    }

    public void printAnalysis(File outputFile, List<KNNFloorPoint> knnFloorPoints, String fieldSeparator) {
        try {
            try (BufferedWriter dataWriter = new BufferedWriter(new FileWriter(outputFile, false))) {

                StringBuilder stb = new StringBuilder();
                stb.append("Locations").append(fieldSeparator).append("Max Attribues").append(fieldSeparator).append("Min Attributes").append(fieldSeparator).append("Max Mean").append(fieldSeparator).append("Min Mean").append(fieldSeparator).append("Max Frequency").append(fieldSeparator).append("Min Frequency").append(fieldSeparator).append("Max Std Dev").append(fieldSeparator).append("Min Std Dev").append(fieldSeparator).append("Mean Std Dev").append(fieldSeparator).append("Max Variance").append(fieldSeparator).append("Min Variance").append(System.getProperty("line.separator"));
                int locations = knnFloorPoints.size();
                int maxAttr = 0;
                int minAttr = Integer.MAX_VALUE;
                double maxMean = -1000;
                double minMean = Double.MAX_VALUE;
                int maxFrequency = 0;
                int minFrequency = Integer.MAX_VALUE;
                double maxStdDev = 0;
                double minStdDev = Double.MAX_VALUE;
                double totalStdDev = 0;
                double totalAttr = 0;
                double maxVariance = 0;
                double minVariance = Double.MAX_VALUE;               

                for (KNNFloorPoint floorPoint : knnFloorPoints) {
                    HashMap<String, AvgValue> attr = floorPoint.getAttributes();
                    if (attr.size() > maxAttr) {
                        maxAttr = attr.size();
                    }

                    if (attr.size() < minAttr) {
                        minAttr = attr.size();
                    }

                    for (AvgValue value : attr.values()) {
                        if (value.getMean() > maxMean) {
                            maxMean = value.getMean();
                        }

                        if (value.getMean() < minMean) {
                            minMean = value.getMean();
                        }
                        
                        if (value.getFrequency() > maxFrequency) {
                            maxFrequency = value.getFrequency();
                        }
                        
                        if (value.getFrequency() < minFrequency) {
                            minFrequency = value.getFrequency();
                        }
                        
                        if (value.getStdDev() > maxStdDev) {
                            maxStdDev = value.getStdDev();
                        }
                        
                        if (value.getStdDev() < minStdDev) {
                            minStdDev = value.getStdDev();
                        }
                        
                        totalStdDev += value.getStdDev();
                        totalAttr += 1;
                        
                        if (value.getVariance() > maxVariance) {
                            maxVariance = value.getVariance();
                        }
                        
                        if (value.getVariance() < minVariance) {
                            minVariance = value.getVariance();
                        }
                    }
                }

                stb.append(locations).append(fieldSeparator).append(maxAttr).append(fieldSeparator).append(minAttr).append(fieldSeparator).append(maxMean).append(fieldSeparator).append(minMean).append(fieldSeparator).append(maxFrequency).append(fieldSeparator).append(minFrequency).append(fieldSeparator).append(maxStdDev).append(fieldSeparator).append(minStdDev).append(fieldSeparator).append(totalStdDev/totalAttr).append(fieldSeparator).append(maxVariance).append(fieldSeparator).append(minVariance).append(System.getProperty("line.separator"));
                
                dataWriter.append(stb);
                logger.info(stb.toString());
            }
        } catch (IOException ex) {
            logger.error(ex.getMessage());
        }

    }
}
