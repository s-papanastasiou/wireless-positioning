package com.company;

import com.company.support.FileController;
import com.company.support.Simulation;
import com.company.support.ParticleSettings;
import com.company.support.Logging;
import com.company.support.ProbabilisticSettings;
import com.company.support.TrialProperties;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class Main {

    //Generate settings automatically, ignoring any input file    
    private static boolean isOutputImage = false;
    private static boolean isTrialDetail = false;

    private final static String IN_SEP = ";";
    private final static String OUT_SEP = ",";

    // Logging headers /////////////////////////////////////////////////////////////////////////////////////////////        
    public final static String trialHeader = "Point_No" + OUT_SEP + "Trial_X" + OUT_SEP + "Trial_Y" + OUT_SEP + "Distance" + OUT_SEP + "Pos_X" + OUT_SEP + "Pos_Y";
    private final static String particleResultsHeader = "BSSIDMerged" + OUT_SEP + "OrientationMerged" + OUT_SEP + "KValue" + OUT_SEP + "InitialReadings" + OUT_SEP + "ParticleCount" + OUT_SEP + "CloudRange" + OUT_SEP + "CloudDisplacement" + OUT_SEP + "ForceToMap" + OUT_SEP + "MeanDistance" + OUT_SEP + "StdDev";
    private final static String probabilisticResultsHeader = "BSSIDMerged" + OUT_SEP + "OrientationMerged" + OUT_SEP + "KValue" + OUT_SEP + "ForceToMap" + OUT_SEP + "MeanDistance";

    public static void main(String[] args) {

        checkArgs(args);
        
        TrialProperties tp = new TrialProperties();
        FileController fc = new FileController(IN_SEP);        
        
        if (fc.isSetupOk) {

            //Particle Results Logging
            Logging particleResultsLog = new Logging(new File(fc.resultsDir, "ParticleResults.csv"));
            particleResultsLog.printLine(particleResultsHeader);

            //Probabilistic Results Logging
            Logging probabilisticResultsLog = new Logging(new File(fc.resultsDir, "ProbablisticResults.csv"));
            probabilisticResultsLog.printLine(probabilisticResultsHeader);

            //Loop through each set of settings                
            System.out.println("Generating settings");
            runSimulations(fc, tp, particleResultsLog, probabilisticResultsLog);

            particleResultsLog.close();
            probabilisticResultsLog.close();
        }

    }
 
    private static void runSimulations(FileController fc, TrialProperties tp, Logging particleResultsLog, Logging probabilisticResultsLog) {

        //Particle Test
        for (int k_counter = tp.getK_MIN(); k_counter <= tp.getK_MAX(); k_counter += tp.getK_INC()) {
            System.out.println("Particle Test");
            runParticle(fc, particleResultsLog, false, false, false, k_counter, tp);
            runParticle(fc, particleResultsLog, true, false, false, k_counter, tp);
            runParticle(fc, particleResultsLog, false, true, false, k_counter, tp);
            runParticle(fc, particleResultsLog, false, false, true, k_counter, tp);
            runParticle(fc, particleResultsLog, true, true, false, k_counter, tp);
            runParticle(fc, particleResultsLog, true, false, true, k_counter, tp);
            runParticle(fc, particleResultsLog, false, true, true, k_counter, tp);
            runParticle(fc, particleResultsLog, true, true, true, k_counter, tp);
        }

        for (int k_counter = tp.getK_MIN(); k_counter <= tp.getK_MAX(); k_counter += tp.getK_INC()) {
            System.out.println("Probablistic Test");
            runProbablistic(fc, probabilisticResultsLog, false, false, false, k_counter, tp);
            runProbablistic(fc, probabilisticResultsLog, true, false, false, k_counter, tp);
            runProbablistic(fc, probabilisticResultsLog, false, true, false, k_counter, tp);
            runProbablistic(fc, probabilisticResultsLog, false, false, true, k_counter, tp);
            runProbablistic(fc, probabilisticResultsLog, true, true, false, k_counter, tp);
            runProbablistic(fc, probabilisticResultsLog, true, false, true, k_counter, tp);
            runProbablistic(fc, probabilisticResultsLog, false, true, true, k_counter, tp);
            runProbablistic(fc, probabilisticResultsLog, true, true, true, k_counter, tp);
        }
    }

    private static void runProbablistic(FileController fc, Logging probabilisticResultsLog, boolean isBSSIDMerged, boolean isOrientationMerged, boolean isForcedToOfflineMap, int kValue, TrialProperties tp) {

        ProbabilisticSettings proSettings = new ProbabilisticSettings(isBSSIDMerged, isOrientationMerged, isForcedToOfflineMap, kValue, tp.getBuildingOrientation());

        String output = String.format("%s,%s,%s,%s", isBSSIDMerged, isOrientationMerged, isForcedToOfflineMap, kValue);
        System.out.println("Generated: " + output);
        Simulation.runProbabilistic(fc, proSettings, probabilisticResultsLog, OUT_SEP, isTrialDetail, isOutputImage);
        System.out.println("Simulation completed: " + output);
    }

    private static void runParticle(FileController fc, Logging particleResultsLog, boolean isBSSIDMerged, boolean isOrientationMerged, boolean isForcedToOfflineMap, int kValue, TrialProperties tp) {

        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy");
        Date date = new Date();
        List<ParticleSettings> parSettingsList = ParticleSettings.generate(isBSSIDMerged, isOrientationMerged, isForcedToOfflineMap, kValue, tp);
        String output = String.format("%s,%s,%s,%s", isBSSIDMerged, isOrientationMerged, isForcedToOfflineMap, kValue);
        System.out.println("Generated: " + output + " " + dateFormat.format(date));
        Simulation.runParticle(fc, parSettingsList, particleResultsLog, OUT_SEP, isTrialDetail, isOutputImage);
        date = new Date();
        System.out.println("Simulation completed: " + output + " " + dateFormat.format(date));
    }

    private static void checkArgs(String[] args){
                
        if(args.length==2){
            isOutputImage = Boolean.parseBoolean(args[0]);
            
            isTrialDetail = Boolean.parseBoolean(args[1]);
            
        }
        System.out.println("Output Image: " + isOutputImage);
        System.out.println("Trial Detail: " + isTrialDetail);        
    }
    
    
}
