package com.company;

import com.company.support.TrialProperties;
import com.company.support.FileController;
import com.company.support.Simulation;
import com.company.support.ParticleSettings;
import com.company.support.Logging;
import com.company.support.ProbabilisticSettings;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class Main {

    //Generate settings automatically, ignoring any input file    
    private static boolean isOutputImage = true;
    private static boolean isTrialDetail = true;
    private static boolean isGeneratedSettings = false;

    private final static String IN_SEP = ";";
    private final static String OUT_SEP = ",";

    // Logging headers /////////////////////////////////////////////////////////////////////////////////////////////        
    public final static String trialHeader = "Point_No" + OUT_SEP + "Trial_X" + OUT_SEP + "Trial_Y" + OUT_SEP + "Distance" + OUT_SEP + "Pos_X" + OUT_SEP + "Pos_Y";
    private final static String particleResultsHeader = "BSSIDMerged" + OUT_SEP + "OrientationMerged"+ OUT_SEP + "ForceToMap" + OUT_SEP + "KValue" + OUT_SEP + "InitialReadings"+ OUT_SEP + "SpeedBreak" + OUT_SEP + "ParticleCount" + OUT_SEP + "CloudRange" + OUT_SEP + "CloudDisplacement" + OUT_SEP + "MeanDistance" + OUT_SEP + "StdDev";
    private final static String probabilisticResultsHeader = "BSSIDMerged" + OUT_SEP + "OrientationMerged" + OUT_SEP + "ForceToMap" + OUT_SEP + "KValue"  + OUT_SEP + "MeanDistance" + OUT_SEP + "StdDev";
    private final static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy");
    
    public static void main(String[] args) {

        checkArgs(args);
        
        TrialProperties tp = new TrialProperties();
        FileController fc = new FileController(IN_SEP, isOutputImage, isTrialDetail);        
        
        if (fc.isSetupOk) {

            //Particle Results Logging
            Logging particleResultsLog = new Logging(new File(fc.resultsDir, "ParticleResults.csv"));
            particleResultsLog.printLine(particleResultsHeader);

            //Probabilistic Results Logging
            Logging probabilisticResultsLog = new Logging(new File(fc.resultsDir, "ProbablisticResults.csv"));
            probabilisticResultsLog.printLine(probabilisticResultsHeader);

            //Loop through each set of settings
            if(isGeneratedSettings){
                System.out.println("Generating settings");
                runSimulations(fc, tp, particleResultsLog, probabilisticResultsLog);
            }else{
                //TODO: Allow import of specific settings from file rather than hard code.
                System.out.println("Specific settings");
                specificSimulations(fc, particleResultsLog, probabilisticResultsLog);
            }
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

        Date date = new Date();
        ProbabilisticSettings proSettings = new ProbabilisticSettings(isBSSIDMerged, isOrientationMerged, isForcedToOfflineMap, kValue, tp.getBuildingOrientation(), OUT_SEP);

        String output = String.format("%s,%s,%s,%s", isBSSIDMerged, isOrientationMerged, isForcedToOfflineMap, kValue);
        System.out.println("Generated: " + output + " " + DATE_FORMAT.format(date));
        Simulation.runProbabilistic(fc, proSettings, probabilisticResultsLog, isTrialDetail, isOutputImage);
        System.out.println("Simulation completed: " + output + " " + DATE_FORMAT.format(date));
    }

    private static void runProbablistic(FileController fc, Logging probabilisticResultsLog, ProbabilisticSettings proSettings) {
        
        Date date = new Date();
        String output = String.format("%s,%s,%s,%s", proSettings.isBSSIDMerged(), proSettings.isOrientationMerged(), proSettings.isForceToOfflineMap(), proSettings.getK());
        System.out.println("Generated: " + output + " " + DATE_FORMAT.format(date));
        Simulation.runProbabilistic(fc, proSettings, probabilisticResultsLog, isTrialDetail, isOutputImage);
        System.out.println("Simulation completed: " + output + " " + DATE_FORMAT.format(date));
    }

    private static void runParticle(FileController fc, Logging particleResultsLog, boolean isBSSIDMerged, boolean isOrientationMerged, boolean isForcedToOfflineMap, int kValue, TrialProperties tp) {
        
        Date date = new Date();
        List<ParticleSettings> parSettingsList = ParticleSettings.generate(isBSSIDMerged, isOrientationMerged, isForcedToOfflineMap, kValue, tp, OUT_SEP);
        String output = String.format("%s,%s,%s,%s", isBSSIDMerged, isOrientationMerged, isForcedToOfflineMap, kValue);
        System.out.println("Generated: " + output + " " + DATE_FORMAT.format(date));
        Simulation.runParticle(fc, parSettingsList, particleResultsLog, isTrialDetail, isOutputImage);
        date = new Date();
        System.out.println("Simulation completed: " + output + " " + DATE_FORMAT.format(date));
    }
    
    private static void runParticle(FileController fc, Logging particleResultsLog, ParticleSettings parSettings) {
        
        Date date = new Date();
        List<ParticleSettings> parSettingsList = new ArrayList(Arrays.asList(parSettings));
        String output = String.format("%s,%s,%s,%s", parSettings.isBSSIDMerged(), parSettings.isOrientationMerged(), parSettings.isForceToOfflineMap(), parSettings.getK());
        System.out.println("Generated: " + output + " " + DATE_FORMAT.format(date));
        Simulation.runParticle(fc, parSettingsList, particleResultsLog, isTrialDetail, isOutputImage);
        date = new Date();
        System.out.println("Simulation completed: " + output + " " + DATE_FORMAT.format(date));
    }

    private static void checkArgs(String[] args){
                
        if(args.length==2){
            isOutputImage = Boolean.parseBoolean(args[0]);
            
            isTrialDetail = Boolean.parseBoolean(args[1]);
            
        }
        System.out.println("Output Image: " + isOutputImage);
        System.out.println("Trial Detail: " + isTrialDetail);        
    }
    
    private static void specificSimulations(FileController fc, Logging particleResultsLog, Logging probabilisticResultsLog){
                   
        double buildingOrientation = -0.523598776;   
        
        ProbabilisticSettings nonCompassProbabilistic = new ProbabilisticSettings(true, true, true, 4, buildingOrientation, OUT_SEP);
        runProbablistic(fc, probabilisticResultsLog, nonCompassProbabilistic);
        
        ProbabilisticSettings compassProbabilistic = new ProbabilisticSettings(true, false, true, 4,buildingOrientation, OUT_SEP);
        runProbablistic(fc, probabilisticResultsLog, compassProbabilistic);
                      
        
        ParticleSettings particle2 = new ParticleSettings(true, true, true, 4, 10, 30, 1, 0.01, 3.5,buildingOrientation, OUT_SEP);
        runParticle(fc, particleResultsLog, particle2);
        
        ParticleSettings particle1 = new ParticleSettings(true, false, true, 4, 10, 30, 1, 0.01, 3.5,buildingOrientation, OUT_SEP);
        runParticle(fc, particleResultsLog, particle1);
        
       
        //ParticleSettings compassParticle = new ParticleSettings(true, false, true, 6, 4, 1, 61, 0.3, 1.1,buildingOrientation, OUT_SEP);
        //runParticle(fc, particleResultsLog, compassParticle);
    }
    
    
}
