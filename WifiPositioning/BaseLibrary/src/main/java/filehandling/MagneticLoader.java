/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package filehandling;

import datastorage.MagneticData;
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
public class MagneticLoader {

    private static final Logger logger = LoggerFactory.getLogger(MagneticLoader.class);
    
    public static List<MagneticData> load(final File dataFile, final List<String> filterSSIDs, final String seperator) {

        List<MagneticData> rawData = new ArrayList<>();
        
        int lineCounter = 1;
        try {            

            try (BufferedReader dataReader = new BufferedReader(new FileReader(dataFile))) {

                String line = dataReader.readLine(); //Read the header
                String[] parts = line.split(seperator);
                if (MagneticData.headerCheck(parts)) {
                    int headerSize = MagneticData.headerSize();
                    while ((line = dataReader.readLine()) != null) {
                        lineCounter++;
                        parts = line.split(seperator);
                        if (parts.length == headerSize) {
                            try {
                                rawData.add(new MagneticData(parts));
                            } catch (ParseException ex) {
                                logger.error("Error parsing line: {} {}", lineCounter, ex.getMessage());
                            }
                        } else {
                            logger.error("Data items count do not match headings count. Line: {}", lineCounter);
                        }
                    }

                    logger.info("Magnetic data read successfully. Lines read: {}", lineCounter);
                } else {
                    logger.error("Headings are not as expected.");
                }
            }
        } catch (IOException x) {
            logger.error(x.getMessage());
        }

        return rawData;
    }

    //extracts data based either data type - assumes comma separated
    public static List<MagneticData> load(final File dataFile) {

        return load(dataFile, new ArrayList<String>(), ",");
    }

    //extracts data based either data type - assumes comma separated
    public static List<MagneticData> load(final File dataFile, final String separator) {

        return load(dataFile, new ArrayList<String>(), separator);
    }
}
