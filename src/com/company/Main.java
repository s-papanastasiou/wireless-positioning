package com.company;

import com.company.support.FileController;
import com.company.support.AppSettingsGenerator;
import com.company.support.Simulation;
import com.company.support.AppSettings;
import com.company.support.Logging;
import com.company.support.DataLoad;
import java.io.File;
import java.util.List;

public class Main {

    //Generate settings automatically, ignoring any input file
    private static boolean isGenerateSettings = true;

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
                runSimulations(fc, particleResultsLog, probabilisticResultsLog);

            } else {
                List<AppSettings> appSettingsList = DataLoad.loadSettings(fc.settingsFile, IN_SEP);
                Simulation.run(fc, appSettingsList, particleResultsLog, probabilisticResultsLog, OUT_SEP);
            }

            particleResultsLog.close();
            probabilisticResultsLog.close();
        }
    }

    private static void runSimulations(FileController fc, Logging particleResultsLog, Logging probabilisticResultsLog) {
        List<AppSettings> appSettingsList;
        appSettingsList = AppSettingsGenerator.generate(false, false, false);
        System.out.println("Generated: false, false, false");
        Simulation.run(fc, appSettingsList, particleResultsLog, probabilisticResultsLog, OUT_SEP);
        System.out.println("Simulation completed: false, false, false");

        appSettingsList = AppSettingsGenerator.generate(true, false, false);
        System.out.println("Generated: true, false, false");
        Simulation.run(fc, appSettingsList, particleResultsLog, probabilisticResultsLog, OUT_SEP);
        System.out.println("Simulation completed: true, false, false");

        appSettingsList = AppSettingsGenerator.generate(false, true, false);
        System.out.println("Generated: false, true, false");
        Simulation.run(fc, appSettingsList, particleResultsLog, probabilisticResultsLog, OUT_SEP);
        System.out.println("Simulation completed: false, true, false");

        appSettingsList = AppSettingsGenerator.generate(false, false, true);
        System.out.println("Generated: false, false, true");
        Simulation.run(fc, appSettingsList, particleResultsLog, probabilisticResultsLog, OUT_SEP);
        System.out.println("Simulation completed: false, false, true");

        appSettingsList = AppSettingsGenerator.generate(true, true, false);
        System.out.println("Generated: true, true, false");
        Simulation.run(fc, appSettingsList, particleResultsLog, probabilisticResultsLog, OUT_SEP);
        System.out.println("Simulation completed: true, true, false");

        appSettingsList = AppSettingsGenerator.generate(true, false, true);
        System.out.println("Generated: true, false, true");
        Simulation.run(fc, appSettingsList, particleResultsLog, probabilisticResultsLog, OUT_SEP);
        System.out.println("Simulation completed: true, false, true");

        appSettingsList = AppSettingsGenerator.generate(false, true, true);
        System.out.println("Generated: false, true, true");
        Simulation.run(fc, appSettingsList, particleResultsLog, probabilisticResultsLog, OUT_SEP);
        System.out.println("Simulation completed: false, true, true");

        appSettingsList = AppSettingsGenerator.generate(true, true, true);
        System.out.println("Generated: true, true, true");
        Simulation.run(fc, appSettingsList, particleResultsLog, probabilisticResultsLog, OUT_SEP);
        System.out.println("Simulation completed: true, true, true");
    }
}
