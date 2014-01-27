package com.company;

import com.company.support.FileController;
import com.company.support.SettingsGenerator;
import com.company.support.Simulation;
import com.company.support.ParticleSettings;
import com.company.support.Logging;
import com.company.support.ProbabilisticSettings;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class Main {

    //Generate settings automatically, ignoring any input file
    private static boolean isGenerateSettings = true;
    private static final boolean isOutputImage = false;

    private final static String IN_SEP = ";";
    private final static String OUT_SEP = ",";

    // Logging headers /////////////////////////////////////////////////////////////////////////////////////////////        
    public final static String trialHeader = "Point_No" + OUT_SEP + "Trial_X" + OUT_SEP + "Trial_Y" + OUT_SEP + "Distance" + OUT_SEP + "Pos_X" + OUT_SEP + "Pos_Y";
    private final static String particleResultsHeader = "BSSIDMerged" + OUT_SEP + "OrientationMerged" + OUT_SEP + "KValue" + OUT_SEP + "InitialReadings" + OUT_SEP + "ParticleCount" + OUT_SEP + "CloudRange" + OUT_SEP + "CloudDisplacement" + OUT_SEP + "ForceToMap" + OUT_SEP + "MeanDistance";
    private final static String probabilisticResultsHeader = "BSSIDMerged" + OUT_SEP + "OrientationMerged" + OUT_SEP + "KValue" + OUT_SEP + "ForceToMap" + OUT_SEP + "MeanDistance";

    public static void main(String[] args) {

        FileController fc = new FileController(IN_SEP);

        if (fc.isSetupOk) {

            //Particle Results Logging
            Logging particleResultsLog = new Logging(new File(fc.resultsDir, "ParticleResults.csv"));
            particleResultsLog.printLine(particleResultsHeader);

            //Probabilistic Results Logging
            Logging probabilisticResultsLog = new Logging(new File(fc.resultsDir, "ProbablisticResults.csv"));
            probabilisticResultsLog.printLine(probabilisticResultsHeader);

            if (fc.settingsFile.isFile()) {
                System.out.println("Loading settings from file");
            } else {
                System.out.println(String.format("%s not found", fc.settingsFile.toString()));
                System.out.println(String.format("Switching to settings generation"));
                isGenerateSettings = true;
            }

            //Loop through each set of settings
            if (isGenerateSettings) {
                System.out.println("Generating settings");
                runSimulations(fc, particleResultsLog, probabilisticResultsLog);

            } else {
                System.out.println("Running from file disabled");
                /*
                System.out.println("Using file settings");
                List<ParticleSettings> appSettingsList = DataLoad.loadSettings(fc.settingsFile, IN_SEP);
                Simulation.runParticle(fc, appSettingsList, particleResultsLog, OUT_SEP);
                Simulation.runProbabilistic(fc, appSettingsList, probabilisticResultsLog, OUT_SEP);
*/
            }

            particleResultsLog.close();
            probabilisticResultsLog.close();
        }
    }

    private static final int K_MIN = 1;
    private static final int K_MAX = 10;
    private static final int K_INC = 1;
    private static final double buildingOrientation = -0.523598776;
    
    private static void runSimulations(FileController fc, Logging particleResultsLog, Logging probabilisticResultsLog) {

        //Particle Test
        for (int k_counter = K_MIN; k_counter <= K_MAX; k_counter += K_INC) {
            System.out.println("Particle Test");
            runParticle(fc, particleResultsLog, false, false, false, k_counter);
            runParticle(fc, particleResultsLog, true, false, false, k_counter);
            runParticle(fc, particleResultsLog, false, true, false, k_counter);
            runParticle(fc, particleResultsLog, false, false, true, k_counter);
            runParticle(fc, particleResultsLog, true, true, false, k_counter);
            runParticle(fc, particleResultsLog, true, false, true, k_counter);
            runParticle(fc, particleResultsLog, false, true, true, k_counter);
            runParticle(fc, particleResultsLog, true, true, true, k_counter);
        }

        for (int k_counter = K_MIN; k_counter <= K_MAX; k_counter += K_INC) {
            System.out.println("Probablistic Test");
            
            runProbablistic(fc, probabilisticResultsLog, false, false, false, k_counter);
            runProbablistic(fc, probabilisticResultsLog, true, false, false, k_counter);
            runProbablistic(fc, probabilisticResultsLog, false, true, false, k_counter);
            runProbablistic(fc, probabilisticResultsLog, false, false, true, k_counter);
            runProbablistic(fc, probabilisticResultsLog, true, true, false, k_counter);
            runProbablistic(fc, probabilisticResultsLog, true, false, true, k_counter);
            runProbablistic(fc, probabilisticResultsLog, false, true, true, k_counter);
            runProbablistic(fc, probabilisticResultsLog, true, true, true, k_counter);                                               
        }
    }
        
        private static void runProbablistic(FileController fc, Logging probabilisticResultsLog, boolean isBSSIDMerged, boolean isOrientationMerged, boolean isForcedToOfflineMap, int kValue){
            
            ProbabilisticSettings proSettings = new ProbabilisticSettings(isBSSIDMerged, isOrientationMerged, isForcedToOfflineMap, kValue, buildingOrientation);
                            
            String output = String.format("%s,%s,%s,%s", isBSSIDMerged, isOrientationMerged, isForcedToOfflineMap, kValue);
            System.out.println("Generated: " + output);
            Simulation.runProbabilistic(fc, proSettings, probabilisticResultsLog, OUT_SEP, isOutputImage);
            System.out.println("Simulation completed: " + output);
        }

     private static void runParticle(FileController fc, Logging particleResultsLog, boolean isBSSIDMerged, boolean isOrientationMerged, boolean isForcedToOfflineMap, int kValue){
            
            SimpleDateFormat dateFormat = new SimpleDateFormat ("hh:mm:ss dd/MM/yyyy");
            Date date = new Date();
            List<ParticleSettings> parSettingsList = SettingsGenerator.particle(isBSSIDMerged, isOrientationMerged, isForcedToOfflineMap, kValue, buildingOrientation);
            String output = String.format("%s,%s,%s,%s", isBSSIDMerged, isOrientationMerged, isForcedToOfflineMap, kValue);
            System.out.println("Generated: " + output + " " + dateFormat.format(date));
            Simulation.runParticle(fc, parSettingsList, particleResultsLog, OUT_SEP, isOutputImage);
            date = new Date();
            System.out.println("Simulation completed: " + output + " " + dateFormat.format(date));
        }

}
