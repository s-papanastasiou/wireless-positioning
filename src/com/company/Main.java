package com.company;

import com.company.support.TrialProperties;
import com.company.support.FileController;
import com.company.support.Simulation;
import com.company.support.ParticleTrial;
import com.company.support.Logging;
import com.company.support.OnOffOptions;
import com.company.support.ProbabilisticTrial;
import com.company.support.SettingsProperties;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class Main {

    private static boolean isGeneratedSettings = false;
    
    /*
    //Generate settings automatically, ignoring any input file    
    private static boolean isOutputImage = true;
    private static boolean isTrialDetail = true;


    private final static String IN_SEP = ";";
    private final static String OUT_SEP = ",";
    */           
    
    public static void main(String[] args) {

        //checkArgs(args);
        SettingsProperties sp = new SettingsProperties();
        TrialProperties tp = new TrialProperties();
        FileController fc = new FileController(sp);
        
        if (fc.isSetupOk) {

            //Particle Results Logging
            Logging particleResultsLog = new Logging(new File(fc.resultsDir, "ParticleResults.csv"));
            particleResultsLog.printLine(sp.PAR_RESULTS_HEADER());

            //Probabilistic Results Logging
            Logging probabilisticResultsLog = new Logging(new File(fc.resultsDir, "ProbablisticResults.csv"));
            probabilisticResultsLog.printLine(sp.PRO_RESULTS_HEADER());

            //Loop through each set of settings
            if(isGeneratedSettings){
                System.out.println("Generating settings");
                runSimulations(sp, fc, tp, particleResultsLog, probabilisticResultsLog);
            }else{
                //TODO: Allow import of specific settings from file rather than hard code.
                System.out.println("Specific settings");
                specificSimulations(sp, fc, particleResultsLog, probabilisticResultsLog);
            }
            particleResultsLog.close();
            probabilisticResultsLog.close();
        }

    }
 
    private static void runSimulations(SettingsProperties sp, FileController fc, TrialProperties tp, Logging particleResultsLog, Logging probabilisticResultsLog) {

        final List<OnOffOptions> options = OnOffOptions.allOptions();
        
        //Particle Test
        for (int k_counter = tp.K_MIN(); k_counter <= tp.K_MAX(); k_counter += tp.K_INC()) {
            System.out.println("Particle Test");
            
            for(OnOffOptions option: options){
                runParticle(sp, fc, particleResultsLog, option, k_counter, tp);
            }
        }
        
        for (int k_counter = tp.K_MIN(); k_counter <= tp.K_MAX(); k_counter += tp.K_INC()) {
            System.out.println("Probablistic Test");
            for(OnOffOptions option: options){
                runProbablistic(sp, fc, probabilisticResultsLog, option, k_counter, tp);
            }                      
        }
    }
    
    private static void runProbablistic(SettingsProperties sp, FileController fc, Logging probabilisticResultsLog, OnOffOptions option, int kValue, TrialProperties tp) {

        Date date = new Date();
        ProbabilisticTrial proTrial = new ProbabilisticTrial(option.isBSSIDMerged(), option.isOrientationMerged(), option.isForceToOfflineMap(), kValue, sp.OUT_SEP());

        String output = String.format("%s,%s,%s,%s", option.isBSSIDMerged(), option.isOrientationMerged(), option.isForceToOfflineMap(), kValue);
        System.out.println("Generated: " + output + " " + sp.formatDate(date));
        Simulation.runProbabilistic(sp, fc, proTrial, probabilisticResultsLog);
        System.out.println("Simulation completed: " + output + " " + sp.formatDate(date));
    }

    private static void runProbablistic(SettingsProperties sp, FileController fc, Logging probabilisticResultsLog, ProbabilisticTrial proTrial) {
        
        Date date = new Date();
        String output = String.format("%s,%s,%s,%s", proTrial.isBSSIDMerged(), proTrial.isOrientationMerged(), proTrial.isForceToOfflineMap(), proTrial.getK());
        System.out.println("Generated: " + output + " " + sp.formatDate(date));
        Simulation.runProbabilistic(sp, fc, proTrial, probabilisticResultsLog);
        System.out.println("Simulation completed: " + output + " " + sp.formatDate(date));
    }

    private static void runParticle(SettingsProperties sp, FileController fc, Logging particleResultsLog, OnOffOptions option, int kValue, TrialProperties tp) {
        
        Date date = new Date();
        List<ParticleTrial> parTrialList = ParticleTrial.generate(option.isBSSIDMerged(), option.isOrientationMerged(), option.isForceToOfflineMap(), kValue, tp, sp.OUT_SEP());
        String output = String.format("%s,%s,%s,%s", option.isBSSIDMerged(), option.isOrientationMerged(), option.isForceToOfflineMap(), kValue);
        System.out.println("Generated: " + output + " " + sp.formatDate(date));
        Simulation.runParticle(sp, fc, parTrialList, particleResultsLog);
        date = new Date();
        System.out.println("Simulation completed: " + output + " " + sp.formatDate(date));
    }
    
    private static void runParticle(SettingsProperties sp, FileController fc, Logging particleResultsLog, ParticleTrial parSettings) {
        
        Date date = new Date();
        List<ParticleTrial> parTrialList = new ArrayList(Arrays.asList(parSettings));
        String output = String.format("%s,%s,%s,%s", parSettings.isBSSIDMerged(), parSettings.isOrientationMerged(), parSettings.isForceToOfflineMap(), parSettings.getK());
        System.out.println("Generated: " + output + " " + sp.formatDate(date));
        Simulation.runParticle(sp, fc, parTrialList, particleResultsLog);
        date = new Date();
        System.out.println("Simulation completed: " + output + " " + sp.formatDate(date));
    }

    /*
    private static void checkArgs(String[] args){
                
        if(args.length==2){
            isOutputImage = Boolean.parseBoolean(args[0]);
            
            isTrialDetail = Boolean.parseBoolean(args[1]);
            
        }
        System.out.println("Output Image: " + isOutputImage);
        System.out.println("Trial Detail: " + isTrialDetail);        
    }*/
    
    private static void specificSimulations(SettingsProperties sp, FileController fc, Logging particleResultsLog, Logging probabilisticResultsLog){                           
        
        ProbabilisticTrial nonCompassProbabilistic = new ProbabilisticTrial(true, true, true, 4, sp.OUT_SEP());
        runProbablistic(sp, fc, probabilisticResultsLog, nonCompassProbabilistic);
        
        ProbabilisticTrial compassProbabilistic = new ProbabilisticTrial(true, false, true, 4, sp.OUT_SEP());
        runProbablistic(sp, fc, probabilisticResultsLog, compassProbabilistic);
                      
        
        ParticleTrial particle2 = new ParticleTrial(true, true, true, 4, 10, 30, 1, 0.01, 3.5, sp.OUT_SEP());
        runParticle(sp, fc, particleResultsLog, particle2);
        
        ParticleTrial particle1 = new ParticleTrial(true, false, true, 4, 10, 30, 1, 0.01, 3.5, sp.OUT_SEP());
        runParticle(sp, fc, particleResultsLog, particle1);
        
       
        //ParticleSettings compassParticle = new ParticleSettings(true, false, true, 6, 4, 1, 61, 0.3, 1.1,buildingOrientation, OUT_SEP);
        //runParticle(fc, particleResultsLog, compassParticle);
    }
    
    
}
