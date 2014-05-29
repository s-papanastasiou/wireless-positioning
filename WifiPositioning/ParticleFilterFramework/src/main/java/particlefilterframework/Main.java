package particlefilterframework;

import configuration.FilterProperties;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.core.util.StatusPrinter;
import configuration.GenerateTrialProperties;
import configuration.FileController;
import simulation.Simulation;
import simulation.ParticleTrial;
import configuration.Logging;
import configuration.OnOffOptions;
import simulation.ProbabilisticTrial;
import configuration.SettingsProperties;
import java.io.File;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {

        // print internal state
        LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
        StatusPrinter.print(lc);

        SettingsProperties sp = new SettingsProperties();       
        
        FileController fc = new FileController(sp);

        if (fc.isSetupOk) {           
            
            //Particle Results Logging
            Logging particleResultsLog = new Logging(new File(fc.outputDir, "ParticleResults.csv"));
            particleResultsLog.printLine(sp.PAR_RESULTS_HEADER());

            //Probabilistic Results Logging
            Logging probabilisticResultsLog = new Logging(new File(fc.outputDir, "ProbablisticResults.csv"));
            probabilisticResultsLog.printLine(sp.PRO_RESULTS_HEADER());

            FilterProperties fp = new FilterProperties(fc.filterProperties, sp.isVerbose());
            
            //Loop through each set of settings
            if (sp.GENERATE_PARTICLE_TRIALS() || sp.GENERATE_PROB_TRIALS()) {
                logger.info("Generating settings: ENABLED");
                generateTrials(sp, fc, fp, particleResultsLog, probabilisticResultsLog);
            } else {
                logger.info("Generating settings: DISABLED");
            }

            if (sp.RUN_PARTICLE_TRIALS()) {
                logger.info("Specific Particle Trials: ENABLED");
                List<ParticleTrial> particleTrialList = ParticleTrial.load(sp, fc);
                if (!particleTrialList.isEmpty()) {
                    Simulation.runParticleList(sp, fc, fp, particleTrialList, particleResultsLog);
                }
            } else {
                logger.info("Specific Particle Trials: DISABLED");
            }

            if (sp.RUN_PROB_TRIALS()) {
                logger.info("Specific Probabilistic Trials: ENABLED");
                List<ProbabilisticTrial> probabilisticTrialList = ProbabilisticTrial.load(sp, fc);
                if (!probabilisticTrialList.isEmpty()) {
                    Simulation.runProbabList(sp, fc, probabilisticTrialList, probabilisticResultsLog);
                }
            } else {
                logger.info("Specific Probabilistic Trials: DISABLED");
            }
            particleResultsLog.close();
            probabilisticResultsLog.close();
        }

    }

    private static void generateTrials(SettingsProperties sp, FileController fc, FilterProperties fp, Logging particleResultsLog, Logging probabilisticResultsLog) {

        GenerateTrialProperties gtp = new GenerateTrialProperties(fc.generateTrial, sp.isVerbose());
        if (gtp.isLoaded()) {

            final List<OnOffOptions> options = OnOffOptions.allOptions();

            //Particle Test
            if (sp.GENERATE_PARTICLE_TRIALS()) {
                logger.info("Particle Test");
                for (int k_counter = gtp.K_MIN(); k_counter <= gtp.K_MAX(); k_counter += gtp.K_INC()) {
                    
                    for (OnOffOptions option : options) {
                        List<ParticleTrial> parTrialList = ParticleTrial.generate(option.isBSSIDMerged(), option.isOrientationMerged(), option.isForceToOfflineMap(), k_counter, gtp, sp.OUT_SEP());
                        Simulation.runParticleList(sp, fc, fp, parTrialList, particleResultsLog);
                    }
                }
            }

            if (sp.GENERATE_PROB_TRIALS()) {
                logger.info("Probablistic Test");
                for (int k_counter = gtp.K_MIN(); k_counter <= gtp.K_MAX(); k_counter += gtp.K_INC()) {                    
                    for (OnOffOptions option : options) {
                        List<ProbabilisticTrial> proTrialList = ProbabilisticTrial.generate(option.isBSSIDMerged(), option.isOrientationMerged(), option.isForceToOfflineMap(), k_counter, sp.OUT_SEP());
                        Simulation.runProbabList(sp, fc, proTrialList, probabilisticResultsLog);
                    }
                }
            }
        }
    }   
}
