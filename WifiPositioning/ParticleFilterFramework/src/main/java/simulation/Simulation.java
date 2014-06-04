/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simulation;

import datastorage.KNNTrialPoint;
import java.util.Date;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import configuration.FileController;
import configuration.FilterProperties;
import configuration.Logging;
import configuration.SettingsProperties;
import datastorage.Location;

/**
 *
 * @author Greg Albiston, Johan Chateau & Pierre Rousseau
 */
public class Simulation {

    private static final Logger logger = LoggerFactory.getLogger(Simulation.class);
    
    public final static double HALF_PI = Math.PI / 2;   

    public static String getTrialResult(int lineNumber, KNNTrialPoint knnTrialPoint, double trialDistance, Location bestLocation, String OUT_SEP) {

        String trialRoom = knnTrialPoint.getRoom();
        int trialX = knnTrialPoint.getxRef();
        int trialY = knnTrialPoint.getyRef();
        String bestRoom = bestLocation.getRoom();
        int bestX = bestLocation.getxRef();
        int bestY = bestLocation.getyRef();

        return lineNumber + OUT_SEP + trialRoom + OUT_SEP + trialX + OUT_SEP + trialY + OUT_SEP + trialDistance + OUT_SEP + bestRoom + OUT_SEP + bestX + OUT_SEP + bestY;
    }

    public static void runProbabList(SettingsProperties sp, FileController fc, List<ProbabilisticTrial> proTrialList, Logging probabilisticResultsLog) {

        for (ProbabilisticTrial proTrial : proTrialList) {
            Date date = new Date();
            
            logger.info("Probabilistic Started: {} {}", proTrial.toString(), sp.formatDate(date));
            ProbSimulation.run(sp, fc, proTrial, probabilisticResultsLog);
            
            date = new Date();
            logger.info("Completed: {} {}", proTrial.toString(), sp.formatDate(date));
        }
    }                 

    public static void runParticleList(SettingsProperties sp, FileController fc, FilterProperties fp, List<ParticleTrial> parTrialList, Logging particleResultsLog) {
        for (ParticleTrial parTrial : parTrialList) {
            Date date = new Date();        
            
            logger.info("Particle Started: {} {}", parTrial.toString(), sp.formatDate(date));        
            ParticleSimulation.run(sp, fc, fp, parTrial, particleResultsLog);
            
            date = new Date();
            logger.info("Completed: {} {}", parTrial.toString(), sp.formatDate(date));                        
        }
    } 
}
