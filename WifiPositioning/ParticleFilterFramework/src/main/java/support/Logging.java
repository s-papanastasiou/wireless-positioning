package support;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created with IntelliJ IDEA. User: pierre Date: 31/10/2013 Time: 18:27 To
 * change this template use File | Settings | File Templates.
 */
public class Logging {

    private static final Logger logger = LoggerFactory.getLogger(Logging.class);
    
    private BufferedWriter writer = null;
    private boolean isLogged = true;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public Logging(File logFile) {

        try {
            writer = new BufferedWriter(new FileWriter(logFile, false));
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    public Logging(boolean isLogged, File logFile) {

        this.isLogged = isLogged;
        try {
            if (isLogged) {
                writer = new BufferedWriter(new FileWriter(logFile, false));
            }
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }
    
    public boolean isLogging(){
        return isLogged;
    }

    public void printLine(String message) {
        print(message + System.getProperty("line.separator"));
    }

    public void print(String message) {
        try {
            if (isLogged) {
                if (writer != null) {
                    writer.write(message);
                    writer.flush();
                } else {
                    logger.info("Writer has been closed. No logging for message: {}", message);
                }
            }
        } catch (IOException e) {
            logger.error(e.getMessage());
        }

    }

    public void close() {
        try {
            if (writer != null) {
                writer.close();

                writer = null;
            }
        } catch (IOException e) {
            logger.error(e.getMessage());
        }

    }

}
