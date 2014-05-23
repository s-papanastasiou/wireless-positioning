/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package knnframework;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Gerg
 */
public class Menus {
    
    private static final Logger logger = LoggerFactory.getLogger(KNNFramework.class);

    public static boolean Choice(String message) {
        boolean isRunning = true;
        boolean isChosen = false;
        BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
        while (isRunning) {
            System.out.println(message + " (Y/N):");
            try {
                String response = input.readLine().trim();
                if (response.equals("Y") | response.equals("y")) {
                    isChosen = true;
                    isRunning = false;
                } else if (response.equals("N") | response.equals("n")) {
                    isChosen = false;
                    isRunning = false;
                } else {
                    System.err.println("Choice not recognised. Y or N.");
                }

            } catch (IOException ex) {
                logger.error("Error reading line: {}", ex);
            }
        }

        return isChosen;
    }

    public static int Options(String... options) {
        boolean isRunning = true;
        int choice = 0;
        BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
        while (isRunning) {
            System.out.println("Select an option:");
            for (int counter = 0; counter < options.length; counter++) {
                System.out.printf("%d: %s\n", counter, options[counter]);
            }
            try {
                int response;
                try {
                    response = Integer.parseInt(input.readLine());
                } catch (NumberFormatException e) {
                    response = options.length;
                }

                if (response >= 0 & response < options.length) {
                    choice = response;
                    isRunning = false;
                } else {
                    System.err.printf("Choice not recognised. 0 to %d.\n", options.length - 1);
                }

            } catch (IOException ex) {
                logger.error("Error reading line: {}", ex);
            }
        }

        return choice;
    }

    //Int version
    public static int Value(String message, int minValue) {

        boolean isRunning = true;
        int value = 0;
        BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
        while (isRunning) {
            System.out.println(message);
            try {
                String response = input.readLine().trim();
                try {
                    value = Integer.parseInt(response);
                    if (value < minValue) {
                        System.err.println(String.format("Entered value must be at least %d.", minValue));
                    } else {
                        isRunning = false;
                    }
                } catch (NumberFormatException e) {
                    System.err.println("Only integer values accepted.");
                }

            } catch (IOException ex) {
                logger.error("Error reading line: {}", ex);
            }
        }

        return value;
    }

    //Double version
    public static double Value(String message, double minValue) {

        boolean isRunning = true;
        double value = 0;
        BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
        while (isRunning) {
            System.out.println(message);
            try {
                String response = input.readLine().trim();
                try {
                    value = Double.parseDouble(response);
                    if (value < minValue) {
                        System.err.println(String.format("Entered value must be at least %s.", minValue));
                    } else {
                        isRunning = false;
                    }
                } catch (NumberFormatException e) {
                    System.err.println("Only double values accepted.");
                }

            } catch (IOException ex) {
                logger.error("Error reading line: {}", ex);
            }
        }

        return value;
    }

    //Returns the full path of the file requested.
    public static File getFilename(String message, File path) {

        BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
        String filename;
        File filePath = null;
        boolean isRunning = true;

        while (isRunning) {

            System.out.println("Current directory: " + path);
            System.out.println(message);
            System.out.println("Commands: ls to list directory. cd to temporarily change directory. exit to exit.");
            try {
                filename = input.readLine().trim();
                
                if (filename.equals("ls")) {
                    for (String fileName : path.list()) {
                        System.out.println(fileName);
                    }
                } else if (filename.equals("cd")) {
                    path = changePath(path);
                } else if (filename.equals("exit")) {
                    isRunning = false;
                } else if (new File(path, filename).exists()) {
                    filePath = new File(path, filename);
                    isRunning = false;
                } else {
                    System.err.println("Entered name is not valid. Please try again.");
                }

            } catch (IOException ex) {
                logger.error("Error reading line: {}", ex);
            }
        }

        return filePath;
    }   

    //obtains the filename for the file and checks it doesn't already exist
    public static File createFilename(String message, File path, String fileExtension) {

        BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
        String filename;
        File filePath = null;
        boolean isRunning = true;

        while (isRunning) {
            System.out.println("Current directory: " + path);
            System.out.println(message);
            System.out.println("Commands: ls to list directory. cd to temporarily change directory. exit to exit.");
            try {
                filename = input.readLine().trim();
                
                File tempFilePath = new File(path, filename + fileExtension);
                if (filename.equals("ls")) {
                    for (String fileName : path.list()) {
                        System.out.println(fileName);
                    }
                } else if (filename.equals("cd")) {
                    path = changePath(path);
                } else if (filename.equals("exit")) {
                    isRunning = false;
                } else if (!tempFilePath.exists()) {
                    filePath = tempFilePath;
                    isRunning = false;
                } else {
                    System.err.println("Entered name is not valid. Please try again.");
                }

            } catch (IOException ex) {
                logger.error("Error reading line: {}", ex);
            }
        }

        return filePath;
    }

    public static File changePath(File workingPath) {
        boolean isRunning = true;

        while (isRunning) {

            System.out.println("Current path: " + workingPath);
            System.out.println("Enter new path (or press return to keep existing path): ");
            BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
            try {
                String temp = input.readLine().trim();
                File tempPath = new File(temp);
                
                if (temp.equals("")) {
                    isRunning = false;
                } else if (tempPath.isDirectory()) {
                    workingPath = tempPath;
                    System.out.println("Path changed.");
                    isRunning = false;
                } else {
                    System.err.println("Entered path is not valid. Please try again.");
                }

            } catch (IOException ex) {
                logger.error("Error reading line: {}", ex);
            }
        }
        return workingPath;
    }
    
    public static String changeSeparator(String oldSeparator) {
        

            String separator = oldSeparator;
            System.out.println(String.format("Enter new field separator for file reading (leave blank to remain unchanged as %s): ", oldSeparator));
            BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
            try {
                separator = input.readLine().trim();
                
                if(separator.equals(""))
                {
                    separator = oldSeparator;
                }
                

            } catch (IOException ex) {
                logger.error("Error reading line: {}", ex);
            }
        
        return separator;
    }
    
}
