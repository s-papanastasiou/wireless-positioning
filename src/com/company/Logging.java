package com.company;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: pierre
 * Date: 31/10/2013
 * Time: 18:27
 * To change this template use File | Settings | File Templates.
 */
public class Logging {

    private BufferedWriter writer = null;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public Logging(File logFile) {

        try {
            writer = new BufferedWriter(new FileWriter(logFile, false));
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    public void printLine(String message) {
        print(message + System.getProperty("line.separator"));
    }

    public void print(String message) {
        try {
            if(writer!=null){
                writer.write(message);
                writer.flush();
            }else{
                System.out.println("Writer has been closed. No logging for message: " + message);
            }
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }


    }

    public void close() {
        try {
            if (writer != null) {
                writer.close();

                writer = null;
            }
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

    }

}

