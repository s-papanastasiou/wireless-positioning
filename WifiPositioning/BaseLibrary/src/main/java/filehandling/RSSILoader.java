/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package filehandling;

import datastorage.RSSIData;
import java.io.BufferedReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.io.File;
import java.io.FileReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Gerg
 */
public class RSSILoader {

    private static final Logger logger = LoggerFactory.getLogger(RSSILoader.class);
    
    public static List<RSSIData> load(final File dataFile, final List<String> filterSSIDs, final String seperator) {

        List<RSSIData> rawData = new ArrayList<>();

        int lineCounter = 1;
        try {
            
            try (BufferedReader dataReader = new BufferedReader(new FileReader(dataFile))) {

                String line = dataReader.readLine(); //Read the header
                String[] parts = line.split(seperator);
                if (RSSIData.headerCheck(parts)) {
                    int headerSize = RSSIData.headerSize();
                    while ((line = dataReader.readLine()) != null) {
                        lineCounter++;
                        parts = line.split(seperator);
                        if (parts.length == headerSize) {
                            try {
                                if (!filterSSIDs.isEmpty()) {
                                    if (filterSSIDs.contains(parts[6])) //sixth element is SSID - only add if it is in the filter list
                                    {
                                        rawData.add(new RSSIData(parts));
                                    }
                                } else {
                                    rawData.add(new RSSIData(parts));
                                }

                            } catch (ParseException ex) {
                                logger.error("Error parsing line: {} {}", lineCounter, ex.getMessage());
                            }
                        } else {
                            logger.error("Data items count do not match headings count. Line: {}", lineCounter);
                        }
                    }

                    logger.info("RSSI data read successfully. Lines read: {} Lines stored: {}", lineCounter, rawData.size());
                } else {
                    logger.error("Headings are not as expected.");
                    if(parts.length==1)
                        logger.error("Expecting field separator: {} Found: {}", seperator, line );
                    else
                        logger.error("Expecting: {} Found: {}", RSSIData.toStringHeadings(seperator), line );
                }
            }
        } catch (IOException x) {
            logger.error(x.getMessage());
        }

        return rawData;
    }

    //extracts data but can only apply to RSSI data - assumes comma separated
    public static List<RSSIData> load(final File dataFile, final List<String> filterSSIDs) {

        return load(dataFile, filterSSIDs, ",");
    }

    //extracts data based either data type - assumes comma separated
    public static List<RSSIData> load(final File dataFile) {

        return load(dataFile, new ArrayList<String>(), ",");
    }

    //extracts data based either data type - assumes comma separated
    public static List<RSSIData> load(final File dataFile, final String fieldSeparator) {

        return load(dataFile, new ArrayList<String>(), fieldSeparator);
    }
}
