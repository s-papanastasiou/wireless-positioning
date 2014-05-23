/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package knnframework;

import java.io.File;
import knnexecution.KNearestNeighbour;

/**
 *
 * @author Gerg
 */
public class AlgorithmList {

    private static final String K_NEAREST_NEIGHBOUR = "K Nearest Neighbour";
    private static final String K_NEAREST_NEIGHBOUR_COMMAND = "-knn";

    public static void commandLine(File workingPath, Settings settings, String[] args) {

        switch (args[0]) {
            case "--help":
                System.out.print(printHelp());
                break;
            case K_NEAREST_NEIGHBOUR_COMMAND:                
                KNearestNeighbour.command(workingPath, settings, args);
                break;
            default:
                System.err.println("Unrecognised command for argument 0. See --help");
                break;
        }
    }
    
    public static String printHelp(){
        return KNearestNeighbour.help();
    }

    public static void list(File workingPath, Settings settings) {

        String[] options = {"Exit", K_NEAREST_NEIGHBOUR};

        boolean isRunning = true;

        while (isRunning) {
            switch (Menus.Options(options)) {
                case 0:
                    isRunning = false;
                    break;
                case 1:                    
                    KNearestNeighbour.start(workingPath, settings);
                    break;
                case 2:

                    break;
                case 3:

                    break;
                case 4:

                    break;
                default:
                    System.out.println("Problem with algorithm list");
                    break;
            }
        }
    }
}
