/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package processing;

import datastorage.ResultLocation;
import general.AvgValue;
import datastorage.KNNFloorPoint;
import general.Point;
import general.TimeStamp;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Gerg
 */
public class LogResults {
    
    private static final Logger logger = LoggerFactory.getLogger(LogResults.class);
    
    public static void testPoint(BufferedWriter writer, String testPointMessage, String distanceMeasure) throws IOException{        
        print(writer, String.format("Test Point: %s Measure: %s", testPointMessage, distanceMeasure));
    }
    
    public static void allEstimates(BufferedWriter writer, List<ResultLocation> positionEstimates) throws IOException{
        print(writer, "All Estimates - " + positionEstimates.size());
        for (ResultLocation estimate : positionEstimates) {
            print(writer, estimate.toString());
        }        
    }
    
    public static void nearestEstimates(BufferedWriter writer, List<ResultLocation> positionEstimates) throws IOException{
        print(writer, "Nearest Estimates - " + positionEstimates.size());
        for (ResultLocation estimate : positionEstimates) {
            print(writer, estimate.toString());
        }        
    }
    
    public static void finalPointCoordinates(BufferedWriter writer, Point finalPoint) throws IOException{
        print(writer, "Final Point coordinates: x: " + finalPoint.getX() + " y:" + finalPoint.getY());
    }
    
    public static void finalPixelDistance(BufferedWriter writer, double pixelDistance) throws IOException{
        print(writer, "Pixel Distance - Final to Test: " + pixelDistance);
    }
    
    
     private static void print(BufferedWriter writer, String message) throws IOException {
        logger.info(message);
        writer.write(message);
        writer.newLine();
    } 
     
    public static String logRSSI(KNNFloorPoint scanPoint, general.Point screenPoint, List<ResultLocation> estimates, List<general.Point> estimatePoints, general.Point finalPoint, KNNFloorPoint scanPointUnfiltered, List<String> filterSSIDs, PositioningSettings positioningSettings){
        String message = "";
        String endl = System.getProperty("line.separator");
        
        message +="<scan_entry data_format=\"RSSI\" >" + endl;

        //Scan Info
        message +="<info>"  + endl;
        message +=String.format("<timestamp=\"%s\" />", TimeStamp.formatShortDateTime(positioningSettings.timestamp)) + endl;
        message +=String.format("<screen_point x=\"%s\" y=\"%s\" />", screenPoint.getX(), screenPoint.getY()) + endl;
        message +=String.format("<room_point room=\"%s\" xRef=\"%d\" yRef=\"%d\" wRef=\"%d\" />", scanPoint.getRoom(), scanPoint.getxRef(), scanPoint.getyRef(), scanPoint.getwRef()) + endl;
        message +="</info>" + endl;

        message +="<access_points>" + endl;

        if (filterSSIDs.isEmpty()) {

            HashMap<String, AvgValue> filteredAttributes = scanPoint.getAttributes();
            Set<String> filterKeys = filteredAttributes.keySet();
            message +=String.format("<no_filter count=\"%s\" >", filterKeys.size()) + endl;
            for (String key : filterKeys)
                message +=String.format("<ap bssid=\"%s\" rssi=\"%s\" />", key, filteredAttributes.get(key).getMean()) + endl;
            message +="</no_filter>" + endl;

        } else {

            //SSID Filter
            message +=String.format("<ssid_filter count=\"%s\" >", filterSSIDs.size()) + endl;
            for (String ssid : filterSSIDs)
                message +=String.format("<ssid name=\"%s\" />", ssid) + endl;
            message +="</ssid_filter>" + endl;

            //Pre-Filter BSSIDs
            HashMap<String, AvgValue> unfilteredAttributes = scanPointUnfiltered.getAttributes();
            Set<String> unfilterKeys = unfilteredAttributes.keySet();
            message +=String.format("<pre_filter count=\"%s\" >", unfilterKeys.size()) + endl;
            for (String key : unfilterKeys)
                message +=String.format("<ap bssid=\"%s\" rssi=\"%s\" />", key, unfilteredAttributes.get(key).getMean()) + endl;
            message +="</pre_filter>" + endl;

            //Post-Filter BSSIDs
            HashMap<String, AvgValue> filteredAttributes = scanPoint.getAttributes();
            Set<String> filterKeys = filteredAttributes.keySet();
            message +=String.format("<post_filter count=\"%s\" >", filterKeys.size()) + endl;
            for (String key : filterKeys)
                message +=String.format("<ap bssid=\"%s\" rssi=\"%s\" />", key, filteredAttributes.get(key).getMean()) + endl;
            message +="</post_filter>" + endl;
        }
        message +="</access_points>" + endl;

        //Estimation Settings
        if (positioningSettings.isVariance) {
            message +=String.format("<settings distance_measure=\"%s\" k_limit=\"%s\" var_limit=\"%s\" var_count=\"%s\" />", positioningSettings.distMeasure, positioningSettings.kLimit, positioningSettings.varLimit, positioningSettings.varCount) + endl;
        } else {
            message +=String.format("<settings distance_measure=\"%s\" k_limit=\"%s\" var_limit=\"\" var_count=\"\" />", positioningSettings.distMeasure, positioningSettings.kLimit) + endl;
        }

        //Nearest Estimates
        message +="<nearest_estimates>" + endl;
        if (!estimates.isEmpty()) {
            for (int counter = 0; counter < estimates.size(); counter++) {                
                message +=String.format("<estimate room=\"%s\" xRef=\"%d\" yRef=\"%d\" wRef=\"%d\" x=\"%s\" y=\"%s\" >", estimates.get(counter).getRoom(), estimates.get(counter).getxRef(), estimates.get(counter).getyRef(), estimates.get(counter).getwRef(), estimatePoints.get(counter).getX(), estimatePoints.get(counter).getY()) + endl;
                /*
                HashMap<String, AvgValue> attributes  = estimates.get(counter).getAttributes();
                Set<String> keys = attributes.keySet();
                for(String key: keys){
                    AvgValue avgValue = attributes.get(key);
                    message += String.format("<ap bssid=\"%s\" mean=\"%s\" total=\"%s\" frequency=\"%s\" std_dev=\"%s\" />",key, avgValue.getMean(), avgValue.getTotal(), avgValue.getFrequency(), avgValue.getStdDev()) + endl;
                }
                */ 
                message += "</estimate>" + endl;
            }
        }
        message +="</nearest_estimates>" + endl;

        //Location Estimate
        if (finalPoint.getX() == -1) {
            message +="<location x=\"\" y=\"\" />" + endl;
        } else {
            message +=String.format("<location x=\"%s\" y=\"%s\" />", finalPoint.getX(), finalPoint.getY()) + endl;
        }
        message +="</scan_entry>" + endl;
                        
        return message;
    } 
    
    public static String logMagnetic(KNNFloorPoint scanPoint, general.Point screenPoint, List<ResultLocation> estimates, List<general.Point> estimatePoints, general.Point finalPoint, PositioningSettings positioningSettings){
        String message = "";
        String endl = System.getProperty("line.separator");
        
        message +="<scan_entry data_format=\"Magnetic\" >" + endl;

        //Scan Info
        message +="<info>"  + endl;
        message +=String.format("<timestamp=\"%s\" />", TimeStamp.formatShortDateTime(positioningSettings.timestamp)) + endl;
        message +=String.format("<screen_point x=\"%s\" y=\"%s\" />", screenPoint.getX(), screenPoint.getY()) + endl;
        message +=String.format("<room_point room=\"%s\" xRef=\"%d\" yRef=\"%d\" wRef=\"%d\" />", scanPoint.getRoom(), scanPoint.getxRef(), scanPoint.getyRef(), scanPoint.getwRef()) + endl;
        message +="</info>" + endl;

        message +="<axis_readings>" + endl;
            HashMap<String, AvgValue> attributes = scanPoint.getAttributes();
            Set<String> filterKeys = attributes.keySet();
            
            for (String key : filterKeys)
                message +=String.format("<reading axis=\"%s\" value=\"%s\" />", key, attributes.get(key).getMean()) + endl;            
        message +="</axis_readings>" + endl;

        //Estimation Settings
        if (positioningSettings.isVariance) {
            message +=String.format("<settings distance_measure=\"%s\" k_limit=\"%s\" var_limit=\"%s\" var_count=\"%s\" />", positioningSettings.distMeasure, positioningSettings.kLimit, positioningSettings.varLimit, positioningSettings.varCount) + endl;
        } else {
            message +=String.format("<settings distance_measure=\"%s\" k_limit=\"%s\" var_limit=\"\" var_count=\"\" />", positioningSettings.distMeasure, positioningSettings.kLimit) + endl;
        }

        //Nearest Estimates
        message +="<nearest_estimates>" + endl;
        if (!estimates.isEmpty()) {
            for (int counter = 0; counter < estimates.size(); counter++) {                
                message +=String.format("<estimate room=\"%s\" xRef=\"%d\" yRef=\"%d\" wRef=\"%d\" x=\"%s\" y=\"%s\" >", estimates.get(counter).getRoom(), estimates.get(counter).getxRef(), estimates.get(counter).getyRef(), estimates.get(counter).getwRef(), estimatePoints.get(counter).getX(), estimatePoints.get(counter).getY()) + endl;
                /*
                HashMap<String, AvgValue> estAttributes  = estimates.get(counter).getAttributes();
                Set<String> keys = estAttributes.keySet();
                for(String key: keys){
                    AvgValue avgValue = estAttributes.get(key);
                    message += String.format("<reading axis=\"%s\" mean=\"%s\" total=\"%s\" frequency=\"%s\" std_dev=\"%s\" />",key, avgValue.getMean(), avgValue.getTotal(), avgValue.getFrequency(), avgValue.getStdDev()) + endl;
                }
                */ 
                message += "</estimate>" + endl;
            }
        }
        message +="</nearest_estimates>" + endl;

        //Location Estimate
        if (finalPoint.getX() == -1) {
            message +="<location x=\"\" y=\"\" />" + endl;
        } else {
            message +=String.format("<location x=\"%s\" y=\"%s\" />", finalPoint.getX(), finalPoint.getY()) + endl;
        }
        message +="</scan_entry>" + endl;
                        
        return message;
    } 
    
    
}
