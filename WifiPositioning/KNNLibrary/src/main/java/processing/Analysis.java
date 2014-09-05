/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package processing;

import datastorage.KNNFloorPoint;
import datastorage.Location;
import datastorage.RoomInfo;
import general.AvgValue;
import general.Point;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.SortedSet;
import java.util.TreeSet;
import javax.imageio.ImageIO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Gerg
 */
public class Analysis {

    private static final Logger logger = LoggerFactory.getLogger(Analysis.class);
    private static final String logExtension = ".csv";
    private static final String logDetail = "-Detail";
    private static final String exactExtension = "-Exact";
    private static final String summaryExtension = "-Summary";

    public HashMap<KNNFloorPoint, List<KNNFloorPoint>> nonUniques(List<KNNFloorPoint> knnFloorList) {

        HashMap<KNNFloorPoint, List<KNNFloorPoint>> matches = new HashMap<>();

        ListIterator it = knnFloorList.listIterator();

        while (it.hasNext()) {
            int index = it.nextIndex();
            KNNFloorPoint current = (KNNFloorPoint) it.next();

            List<KNNFloorPoint> dups = new ArrayList<>();
            while (it.hasNext()) {
                KNNFloorPoint test = (KNNFloorPoint) it.next();
                if (current.equivalentTo(test)) {
                    dups.add(test);
                    it.remove();
                }
            }

            if (!dups.isEmpty()) {
                matches.put(current, dups);
            }

            while (it.previousIndex() > index) {
                it.previous();
            }
        }

        return matches;
    }

    public void printNonUniques(File outputPath, String filename, File floorPlanFile, List<KNNFloorPoint> knnFloorList, HashMap<String, RoomInfo> roomInfo, String fieldSeparator, Double lowerBound, Double upperBound, Double step) {

        HashMap<Double, List<Double>> summaryInfo = new HashMap<>();

        HashMap<KNNFloorPoint, List<KNNFloorPoint>> results = nonUniques(knnFloorList);
        List<Double> summary = summarise(results);
        summaryInfo.put(Double.NaN, summary);
        File outputFile = new File(outputPath, filename + logExtension);
        printToFile(outputFile, results, fieldSeparator);
        File outputDetailFile = new File(outputPath, filename + logDetail + logExtension);
        printDetailToFile(outputDetailFile, results, fieldSeparator);
        printToImage(outputPath, filename, floorPlanFile, results, roomInfo);

        //Check range of exact matches
        for (Double tolerance = lowerBound; tolerance <= upperBound; tolerance += step) {
            HashMap<KNNFloorPoint, List<KNNFloorPoint>> exactResults = exactMatch(results, tolerance);
            summary = summarise(exactResults);
            summaryInfo.put(tolerance, summary);

            String exactExtensionTol = exactExtension + tolerance;
            File exactOutputFile = new File(outputPath, filename + exactExtensionTol + logExtension);
            printToFile(exactOutputFile, exactResults, fieldSeparator);
            File exactOutputDetailFile = new File(outputPath, filename + exactExtensionTol + logDetail + logExtension);
            printDetailToFile(exactOutputDetailFile, exactResults, fieldSeparator);
            printToImage(outputPath, filename + exactExtensionTol, floorPlanFile, exactResults, roomInfo);
        }
        File outputSummaryFile = new File(outputPath, filename + summaryExtension + logExtension);
        printSummary(outputSummaryFile, summaryInfo, fieldSeparator);
    }

    private List<Double> summarise(HashMap<KNNFloorPoint, List<KNNFloorPoint>> results) {
        List<Double> summary = new LinkedList<>();

        AvgValue avgValue = new AvgValue();

        for (List<KNNFloorPoint> result : results.values()) {
            avgValue.add(result.size() + 1);
        }
        summary.add((double) avgValue.getFrequency());
        summary.add(avgValue.getMax());
        summary.add(avgValue.getMin());
        summary.add(avgValue.getMean());
        summary.add(avgValue.getStdDev());
        summary.add(avgValue.getVariance());
        summary.add(avgValue.getTotal());

        return summary;
    }

    private HashMap<KNNFloorPoint, List<KNNFloorPoint>> exactMatch(HashMap<KNNFloorPoint, List<KNNFloorPoint>> results, Double tolerance) {

        List<List<KNNFloorPoint>> allPoints = new LinkedList<>();
        for (KNNFloorPoint result : results.keySet()) {
            List<KNNFloorPoint> points = new LinkedList<>();
            points.add(result);
            points.addAll(results.get(result));
            allPoints.add(points);
        }

        HashMap<KNNFloorPoint, List<KNNFloorPoint>> exactMatches = new HashMap<>();

        for (List<KNNFloorPoint> points : allPoints) {

            ListIterator it = points.listIterator();

            while (it.hasNext()) {
                int index = it.nextIndex();
                KNNFloorPoint current = (KNNFloorPoint) it.next();

                List<KNNFloorPoint> dups = new ArrayList<>();
                while (it.hasNext()) {
                    KNNFloorPoint test = (KNNFloorPoint) it.next();
                    if (current.matchingAttributes(test, tolerance)) {
                        dups.add(test);
                        it.remove();
                    }
                }

                if (!dups.isEmpty()) {
                    exactMatches.put(current, dups);
                }

                while (it.previousIndex() > index) {
                    it.previous();
                }
            }

        }
        return exactMatches;
    }

    private void printSummary(File outputFile, HashMap<Double, List<Double>> summary, String fieldSeparator) {
        try {
            try (BufferedWriter dataWriter = new BufferedWriter(new FileWriter(outputFile, false))) {

                StringBuilder stb = new StringBuilder();
                stb.append("Tolerance").append(fieldSeparator).append("Size").append(fieldSeparator).append("Max Count").append(fieldSeparator).append("Min Count").append(fieldSeparator).append("Mean Count").append(fieldSeparator).append("Std Dev Count").append(fieldSeparator).append("Variance Count").append(fieldSeparator).append("Total Count").append(System.getProperty("line.separator"));

                SortedSet<Double> keys = new TreeSet(summary.keySet());
                for (Double key : keys) {

                    if (key.equals(Double.NaN)) {
                        stb.append("Any");
                    } else {
                        stb.append(key);
                    }
                    List<Double> values = summary.get(key);
                    for(Double value : values){
                        stb.append(fieldSeparator).append(value);
                    }
                    stb.append(System.getProperty("line.separator"));
                }
                dataWriter.append(stb);

            }
        } catch (IOException ex) {
            logger.error(ex.getMessage());
        }
    }

    private void printToFile(File outputFile, HashMap<KNNFloorPoint, List<KNNFloorPoint>> results, String fieldSeparator) {
        try {
            try (BufferedWriter dataWriter = new BufferedWriter(new FileWriter(outputFile, false))) {

                StringBuilder stb = new StringBuilder();
                stb.append("Index").append(fieldSeparator).append("Count").append(fieldSeparator).append("Matches").append(System.getProperty("line.separator"));
                int index = 0;
                for (KNNFloorPoint result : results.keySet()) {
                    List<KNNFloorPoint> dups = results.get(result);
                    stb.append(index).append(fieldSeparator).append(dups.size() + 1).append(fieldSeparator).append(result);
                    for (KNNFloorPoint dup : dups) {
                        stb.append(fieldSeparator).append(dup);
                    }
                    stb.append(System.getProperty("line.separator"));
                    index++;
                }
                dataWriter.append(stb);

            }
        } catch (IOException ex) {
            logger.error(ex.getMessage());
        }
    }

    private void printDetailToFile(File outputFile, HashMap<KNNFloorPoint, List<KNNFloorPoint>> results, String fieldSeparator) {
        try {
            try (BufferedWriter dataWriter = new BufferedWriter(new FileWriter(outputFile, false))) {

                StringBuilder stb = new StringBuilder();
                stb.append("Index").append(fieldSeparator).append("Match").append(fieldSeparator).append("Attributes").append(System.getProperty("line.separator"));
                int index = 0;
                for (KNNFloorPoint result : results.keySet()) {
                    List<KNNFloorPoint> dups = results.get(result);
                    //Key
                    stb.append(index).append(fieldSeparator).append(result).append(fieldSeparator).append(result.toStringAttributes(fieldSeparator)).append(System.getProperty("line.separator"));

                    //Values
                    for (KNNFloorPoint dup : dups) {
                        stb.append(index).append(fieldSeparator).append(dup).append(fieldSeparator).append(dup.toStringAttributes(fieldSeparator)).append(System.getProperty("line.separator"));
                    }
                    index++;
                }
                dataWriter.append(stb);

            }
        } catch (IOException ex) {
            logger.error(ex.getMessage());
        }
    }

    private void printToImage(File outputPath, String filename, File floorPlanFile, HashMap<KNNFloorPoint, List<KNNFloorPoint>> results, HashMap<String, RoomInfo> roomInfo) {

        try {
            BufferedImage floorPlanImage = ImageIO.read(floorPlanFile);
            floorPlanImage = drawLocations(results, roomInfo, floorPlanImage);
            File outputFile = new File(outputPath, filename + ".png");
            ImageIO.write(floorPlanImage, "png", outputFile);
        } catch (IOException ex) {
            logger.error(ex.getMessage());
        }
    }

    private static BufferedImage drawLocations(HashMap<KNNFloorPoint, List<KNNFloorPoint>> results, HashMap<String, RoomInfo> roomInfo, BufferedImage floorPlanImage) {

        Graphics2D floorPlan = floorPlanImage.createGraphics();
        floorPlan.setComposite(AlphaComposite.SrcOver);

        int total = results.size();

        int index = 0;
        for (KNNFloorPoint key : results.keySet()) {

            floorPlan.setColor(getColor(index, total));

            Location drawLoc = RoomInfo.createLocation(key.getRoom(), key.getxRef(), key.getyRef(), key.getwRef(), roomInfo);
            drawPoint(drawLoc.getDrawPoint(), floorPlan, index);

            List<KNNFloorPoint> matchLocations = results.get(key);
            for (KNNFloorPoint location : matchLocations) {
                drawLoc = RoomInfo.createLocation(location.getRoom(), location.getxRef(), location.getyRef(), location.getwRef(), roomInfo);
                drawPoint(drawLoc.getDrawPoint(), floorPlan, index);
            }

            index++;
        }
        floorPlan.dispose();

        return floorPlanImage;

    }

    private static final int SIZE = 10;
    private static final int HALF_SIZE = SIZE / 2;

    private static void drawPoint(Point pos, Graphics2D floorPlan, int index) {

        //Draw oval                
        floorPlan.fillOval(pos.getXint() - HALF_SIZE, pos.getYint() - HALF_SIZE, SIZE, SIZE);

        //include text label for index              
        floorPlan.drawString(String.valueOf(index), pos.getXint() + HALF_SIZE, pos.getYint());
    }

    private static Color getColor(int index, int total) {

        final float saturation = 1.0f;
        final float brightness = 1.0f;
        final int alpha = 255; //1.0f * 255 - full alpha

        final float paletteRange = 330.0f / 360.0f; //Constrain the pallete to between Red (0 degrees) and Pink (330 degrees).

        float hue = ((float) index / total) * paletteRange;

        Color tempColor = Color.getHSBColor(hue, saturation, brightness);

        return new Color(tempColor.getRed(), tempColor.getGreen(), tempColor.getBlue(), alpha);

    }

}
