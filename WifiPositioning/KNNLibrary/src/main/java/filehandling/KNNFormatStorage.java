/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package filehandling;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import datastorage.KNNFloorPoint;
import java.io.File;
import java.io.InputStream;
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
    
}
