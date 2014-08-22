/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package processing;

import datastorage.KNNFloorPoint;
import datastorage.RSSIData;
import filehandling.KNNRSSI;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Gerg
 */
public class Analysis {
    
    private static final Logger logger = LoggerFactory.getLogger(Analysis.class);
    
    public HashMap<KNNFloorPoint, List<KNNFloorPoint>> nonUniques(List<RSSIData> rssiDataList){
        
        HashMap<KNNFloorPoint, List<KNNFloorPoint>> matches = new HashMap<>();
        
        List<KNNFloorPoint> knnFloorList = KNNRSSI.compileList(rssiDataList, false);
        List<KNNFloorPoint> comparisonList = KNNRSSI.compileList(rssiDataList, false);
        
        for(int i = 0; i < knnFloorList.size(); i++){
            KNNFloorPoint current = knnFloorList.get(i);
                
            List<KNNFloorPoint> dups = new ArrayList<>();
            for(int j =i+1; j < comparisonList.size(); j++){
                
                KNNFloorPoint test = comparisonList.get(j);
                if(current.equivalentTo(test)){                    
                    dups.add(test);
                }                
            }
            
            if(!dups.isEmpty()){                
                matches.put(current, dups);
                for(KNNFloorPoint dup: dups){
                    comparisonList.remove(dup);
                }
            }
        }
        
        
        return matches;
    }
    
    public HashMap<KNNFloorPoint, List<KNNFloorPoint>> nonUniques(List<RSSIData> rssiDataList, Double variance){
        
        HashMap<KNNFloorPoint, List<KNNFloorPoint>> matches = new HashMap<>();
        
        List<KNNFloorPoint> knnFloorList = KNNRSSI.compileList(rssiDataList, false);
        List<KNNFloorPoint> comparisonList = KNNRSSI.compileList(rssiDataList, false);
        
        for(int i = 0; i < knnFloorList.size(); i++){
            KNNFloorPoint current = knnFloorList.get(i);
                
            List<KNNFloorPoint> dups = new ArrayList<>();
            for(int j =i+1; j < comparisonList.size(); j++){
                
                KNNFloorPoint test = comparisonList.get(j);
                if(current.equivalentTo(test, variance)){                    
                    dups.add(test);
                }                
            }
            
            if(!dups.isEmpty()){                
                matches.put(current, dups);
                for(KNNFloorPoint dup: dups){
                    comparisonList.remove(dup);
                }
            }
        }        
        
        return matches;
    }
    
    public void printNonUniques(File outputFile, List<RSSIData> rssiDataList, String fieldSeperator){
    
        HashMap<KNNFloorPoint, List<KNNFloorPoint>> results = nonUniques(rssiDataList);
        
        printToFile(outputFile, results, fieldSeperator);        
    }
    
    public void printNonUniques(File outputFile, List<RSSIData> rssiDataList, Double variance, String fieldSeperator){
    
        HashMap<KNNFloorPoint, List<KNNFloorPoint>> results = nonUniques(rssiDataList, variance);
        
        printToFile(outputFile, results, fieldSeperator);        
    }
    
    private void printToFile(File outputFile, HashMap<KNNFloorPoint, List<KNNFloorPoint>> results, String fieldSeperator){
        try {            
            try (BufferedWriter dataWriter = new BufferedWriter(new FileWriter(outputFile, false))) {                
                                    
                StringBuilder stb = new StringBuilder();
                for(KNNFloorPoint result : results.keySet()){                    
                    List<KNNFloorPoint> dups = results.get(result);
                    stb.append(dups.size() + 1).append(fieldSeperator).append(result);
                    for(KNNFloorPoint dup: dups){
                        stb.append(fieldSeperator).append(dup);
                    }        
                    stb.append(System.getProperty("line.separator"));                    
                }
                dataWriter.append(stb);                
            }
        } catch (IOException x) {
            logger.error(x.getMessage());
        }
    }
}
