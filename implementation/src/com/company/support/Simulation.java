/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.company.support;

import com.company.methods.ParticleSimulation;
import com.company.methods.ProbSimulation;
import datastorage.KNNTrialPoint;
import general.Point;
import java.util.Date;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Gerg
 */
public class Simulation {

    private static final Logger logger = LoggerFactory.getLogger(Simulation.class);
    
    public final static double HALF_PI = Math.PI / 2;   

    public static String getTrialResult(int lineNumber, KNNTrialPoint knnTrialPoint, double trialDistance, Point bestPoint, String OUT_SEP) {

        int trialX = knnTrialPoint.getxRef();
        int trialY = knnTrialPoint.getyRef();
        double posX = bestPoint.getX();
        double posY = bestPoint.getY();

        return lineNumber + OUT_SEP + trialX + OUT_SEP + trialY + OUT_SEP + trialDistance + OUT_SEP + posX + OUT_SEP + posY;
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
