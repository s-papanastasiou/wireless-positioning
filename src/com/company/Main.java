package com.company;

import com.company.support.GenerateTrialProperties;
import com.company.support.FileController;
import com.company.support.Simulation;
import com.company.support.ParticleTrial;
import com.company.support.Logging;
import com.company.support.OnOffOptions;
import com.company.support.ProbabilisticTrial;
import com.company.support.SettingsProperties;
import java.io.File;
import java.util.Date;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    
    public static void main(String[] args) {

        SettingsProperties sp = new SettingsProperties();
        GenerateTrialProperties gtp = new GenerateTrialProperties();
        FileController fc = new FileController(sp);
        
        if (fc.isSetupOk) {                       
            
            //Particle Results Logging
            Logging particleResultsLog = new Logging(new File(fc.resultsDir, "ParticleResults.csv"));
            particleResultsLog.printLine(sp.PAR_RESULTS_HEADER());

            //Probabilistic Results Logging
            Logging probabilisticResultsLog = new Logging(new File(fc.resultsDir, "ProbablisticResults.csv"));
            probabilisticResultsLog.printLine(sp.PRO_RESULTS_HEADER());

            //Loop through each set of settings
            if (gtp.isLoaded()) {
                logger.info("Generating settings");
                runSimulations(sp, fc, gtp, particleResultsLog, probabilisticResultsLog);
            }

            logger.info("Specific Particle Trial settings");
            List<ParticleTrial> particleTrialList = ParticleTrial.load(sp, fc);
            if (!particleTrialList.isEmpty()) {            
                runParticle(sp, fc, particleResultsLog, particleTrialList);
            }
            
            logger.info("Specific Probabilistic Trial settings");
            List<ProbabilisticTrial> probabilisticTrialList = ProbabilisticTrial.load(sp, fc);
            if (!probabilisticTrialList.isEmpty()) {                
                runProbablistic(sp, fc, particleResultsLog, probabilisticTrialList);
            }
            
            particleResultsLog.close();
            probabilisticResultsLog.close();
        }

    }

    private static void runSimulations(SettingsProperties sp, FileController fc, GenerateTrialProperties tp, Logging particleResultsLog, Logging probabilisticResultsLog) {

        final List<OnOffOptions> options = OnOffOptions.allOptions();

        //Particle Test
        for (int k_counter = tp.K_MIN(); k_counter <= tp.K_MAX(); k_counter += tp.K_INC()) {
            logger.info("Particle Test");

            for (OnOffOptions option : options) {
                runParticle(sp, fc, particleResultsLog, option, k_counter, tp);
            }
        }

        for (int k_counter = tp.K_MIN(); k_counter <= tp.K_MAX(); k_counter += tp.K_INC()) {
            logger.info("Probablistic Test");
            for (OnOffOptions option : options) {
                runProbablistic(sp, fc, probabilisticResultsLog, option, k_counter);
            }
        }
    }

    private static void runProbablistic(SettingsProperties sp, FileController fc, Logging probabilisticResultsLog, OnOffOptions option, int kValue) {

        Date date = new Date();
        ProbabilisticTrial proTrial = new ProbabilisticTrial(option.isBSSIDMerged(), option.isOrientationMerged(), option.isForceToOfflineMap(), kValue, sp.OUT_SEP());

        String output = String.format("%s,%s,%s,%s", option.isBSSIDMerged(), option.isOrientationMerged(), option.isForceToOfflineMap(), kValue);
        logger.info("Generated: " + output + " " + sp.formatDate(date));
        Simulation.runProbabilistic(sp, fc, proTrial, probabilisticResultsLog);
        logger.info("Simulation completed: " + output + " " + sp.formatDate(date));
    }

    private static void runProbablistic(SettingsProperties sp, FileController fc, Logging probabilisticResultsLog, List<ProbabilisticTrial> proTrialList) {

        for (ProbabilisticTrial proTrial : proTrialList) {
            Date date = new Date();
            String output = String.format("%s,%s,%s,%s", proTrial.isBSSIDMerged(), proTrial.isOrientationMerged(), proTrial.isForceToOfflineMap(), proTrial.getK());
            logger.info("Specific: " + output + " " + sp.formatDate(date));
            Simulation.runProbabilistic(sp, fc, proTrial, probabilisticResultsLog);
            logger.info("Simulation completed: " + output + " " + sp.formatDate(date));
        }
    }

    private static void runParticle(SettingsProperties sp, FileController fc, Logging particleResultsLog, OnOffOptions option, int kValue, GenerateTrialProperties tp) {

        Date date = new Date();
        List<ParticleTrial> parTrialList = ParticleTrial.generate(option.isBSSIDMerged(), option.isOrientationMerged(), option.isForceToOfflineMap(), kValue, tp, sp.OUT_SEP());
        String output = String.format("%s,%s,%s,%s", option.isBSSIDMerged(), option.isOrientationMerged(), option.isForceToOfflineMap(), kValue);
        logger.info("Generated: " + output + " " + sp.formatDate(date));
        Simulation.runParticle(sp, fc, parTrialList, particleResultsLog);
        date = new Date();
        logger.info("Simulation completed: " + output + " " + sp.formatDate(date));
    }

    private static void runParticle(SettingsProperties sp, FileController fc, Logging particleResultsLog, List<ParticleTrial> parTrialList) {

        for (ParticleTrial parTrial : parTrialList) {
            Date date = new Date();
            String output = String.format("%s,%s,%s,%s", parTrial.isBSSIDMerged(), parTrial.isOrientationMerged(), parTrial.isForceToOfflineMap(), parTrial.getK());
            logger.info("Specific: " + output + " " + sp.formatDate(date));

            Simulation.runParticle(sp, fc, parTrial, particleResultsLog);

            date = new Date();
            logger.info("Simulation completed: " + output + " " + sp.formatDate(date));
        }
    }

}
