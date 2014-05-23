/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package support;

import methods.Data;
import general.TimeStamp;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Greg Albiston
 */
public class DataLoad {

    private static final Logger logger = LoggerFactory.getLogger(DataLoad.class);
    
    public static List<Data> loadInertialData(File inertialDataFile, String INPUT_SEPARATOR) {

        int lineNumber = 0;
        List<Data> inertialDataList = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(inertialDataFile))) {

            String header = reader.readLine();
            int headerSize = header.split(INPUT_SEPARATOR).length;

            String line;
            while ((line = reader.readLine()) != null) {

                lineNumber++;
                String[] columns = line.split(INPUT_SEPARATOR);

                if (columns.length == headerSize) {

                    Data data
                            = new Data(TimeStamp.convertDateTime(columns[0]), Float.parseFloat(columns[1]),
                                    Float.parseFloat(columns[2]), Float.parseFloat(columns[3]),
                                    Float.parseFloat(columns[4]), Float.parseFloat(columns[5]),
                                    Float.parseFloat(columns[6]), Float.parseFloat(columns[7]),
                                    Float.parseFloat(columns[8]), Float.parseFloat(columns[9]),
                                    Float.parseFloat(columns[10]), Float.parseFloat(columns[11]),
                                    Float.parseFloat(columns[12]), Float.parseFloat(columns[13]),
                                    Float.parseFloat(columns[14]), Float.parseFloat(columns[15]),
                                    Float.parseFloat(columns[16]), Float.parseFloat(columns[17]),
                                    Float.parseFloat(columns[18]), Float.parseFloat(columns[19]),
                                    Float.parseFloat(columns[20]), Float.parseFloat(columns[21]),
                                    Float.parseFloat(columns[22]));

                    inertialDataList.add(data);
                } else {
                    logger.info("Skipping line: {}", lineNumber);
                }
            }
        } catch (IOException e) {
            logger.error(e.getMessage());
        }

        logger.info("Data loaded: {}", lineNumber);

        return inertialDataList;
    }

    public static File checkDir(File parentDir, String directory) {

        File dir;
        if (parentDir != null) {
            dir = new File(parentDir, directory);
        } else {
            dir = new File(directory);
        }

        if (!dir.exists()) {
            dir.mkdir();
        }

        return dir;
    }

}
