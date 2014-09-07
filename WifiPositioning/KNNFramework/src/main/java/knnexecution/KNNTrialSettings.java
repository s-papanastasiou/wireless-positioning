/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package knnexecution;

import java.util.ArrayList;
import java.util.List;
import knnframework.Menus;
import processing.DistanceMeasure;

/**
 *
 * @author Greg Albiston
 */
public class KNNTrialSettings {

    public final int ALL_DIST_OPTIONS = 0;
    public final int kLowerValue;
    public final int kUpperValue;
    private final int distOption;
    public final double varLowerLimit;
    public final double varUpperLimit;
    public final double varLimitStep;
    public final int varLowerCount;
    public final int varUpperCount;
    public final boolean isVariance;
    public final boolean isBSSIDMerged;
    public final boolean isOrientationMerged;
    public final boolean isPrintImages;

    public KNNTrialSettings() {
        //Enter k value
        this.kLowerValue = Menus.Value("Enter the integer LOWER k value: ", 1);
        this.kUpperValue = Menus.Value("Enter the integer UPPER k value: ", kLowerValue);

        System.out.println("Selected the distance measure type to be used:");
        this.distOption = Menus.Options(getDistanceOptions());

        //Do you want to use variance
        if (Menus.Choice("Do you want to use the variance alternative?")) {
            this.isVariance = true;
            //enter variance limit
            this.varLowerLimit = Menus.Value("Enter the decimal LOWER variance limit (tolerable difference in dbms): ", 0.0f);
            this.varUpperLimit = Menus.Value("Enter the decimal UPPER variance limit (tolerable difference in dbms): ", varLowerLimit);
            this.varLimitStep = Menus.Value("Enter the decimal step change between each test of variance limit: ", 0.01f);

            //enter variance count
            this.varLowerCount = Menus.Value("Enter the integer LOWER maximum number of access points at each location that can exceed the variance limit: ", 0);
            this.varUpperCount = Menus.Value("Enter the integer UPPER maximum number of access points at each location that can exceed the variance limit: ", varLowerCount);
        } else {
            this.isVariance = false;
            this.varLowerLimit = 0;
            this.varUpperLimit = 1;
            this.varLimitStep = 2;
            this.varLowerCount = 0;
            this.varUpperCount = 0;
        }

        this.isBSSIDMerged = Menus.Choice("Do you want to merge BSSIDs?");

        this.isOrientationMerged = Menus.Choice("Do you want to merge the orientation?");

        this.isPrintImages = Menus.Choice("Do you want to print the floorplan images?");
    }

    public static KNNTrialSettings commandSetup(String[] args) {
        KNNTrialSettings knnSettings;
        switch (args.length) {
            case 3:
                knnSettings = new KNNTrialSettings(Integer.parseInt(args[1]), Integer.parseInt(args[2]));
                break;
            case 4:
                knnSettings = new KNNTrialSettings(Integer.parseInt(args[1]), Integer.parseInt(args[2], Integer.parseInt(args[3])));
                break;
            case 5:
                knnSettings = new KNNTrialSettings(Integer.parseInt(args[1]), Integer.parseInt(args[2]), Double.parseDouble(args[3]), Integer.parseInt(args[4]));
                break;
            case 9:
                knnSettings = new KNNTrialSettings(Integer.parseInt(args[1]), Integer.parseInt(args[2]), Integer.parseInt(args[3]), Double.parseDouble(args[4]), Double.parseDouble(args[5]), Double.parseDouble(args[6]), Integer.parseInt(args[7]), Integer.parseInt(args[8]));
                break;
            case 10:
                knnSettings = new KNNTrialSettings(Integer.parseInt(args[1]), Integer.parseInt(args[2]), Integer.parseInt(args[3]), Double.parseDouble(args[4]), Double.parseDouble(args[5]), Double.parseDouble(args[6]), Integer.parseInt(args[7]), Integer.parseInt(args[8]), Boolean.parseBoolean(args[9]));
                break;
            case 11:
                knnSettings = new KNNTrialSettings(Integer.parseInt(args[1]), Integer.parseInt(args[2]), Integer.parseInt(args[3]), Double.parseDouble(args[4]), Double.parseDouble(args[5]), Double.parseDouble(args[6]), Integer.parseInt(args[7]), Integer.parseInt(args[8]), Boolean.parseBoolean(args[9]), Boolean.parseBoolean(args[10]));
                break;
            default:
                System.err.println("Invalid argument length: " + args.length + ". Default to k=5 and Euclidean distance measures. See manual.");
                knnSettings = new KNNTrialSettings(5, 1);
                break;
        }

        return knnSettings;
    }

    public KNNTrialSettings(int kValue, int distOption) {
        this.kLowerValue = kValue;
        this.kUpperValue = kValue;
        this.distOption = checkDistanceOption(distOption);
        this.varLowerLimit = 0;
        this.varUpperLimit = 1;
        this.varLimitStep = 2;
        this.varLowerCount = 0;
        this.varUpperCount = 0;
        this.isVariance = false;
        this.isBSSIDMerged = false;
        this.isOrientationMerged = false;
        this.isPrintImages = false;
    }

    public KNNTrialSettings(int kLowerValue, int kUpperValue, boolean isPrintImages, int distOption) {
        this.kLowerValue = kLowerValue;
        this.kUpperValue = kUpperValue;
        this.distOption = checkDistanceOption(distOption);
        this.varLowerLimit = 0;
        this.varUpperLimit = 1;
        this.varLimitStep = 2;
        this.varLowerCount = 0;
        this.varUpperCount = 0;
        this.isVariance = false;
        this.isBSSIDMerged = false;
        this.isOrientationMerged = false;
        this.isPrintImages = isPrintImages;
    }
    
    public KNNTrialSettings(int kValue, boolean isBSSIDMerged, boolean isPrintImages) {
        this.kLowerValue = kValue;
        this.kUpperValue = kValue;
        this.distOption = ALL_DIST_OPTIONS;
        this.varLowerLimit = 0;
        this.varUpperLimit = 1;
        this.varLimitStep = 2;
        this.varLowerCount = 0;
        this.varUpperCount = 0;
        this.isVariance = false;
        this.isBSSIDMerged = isBSSIDMerged;
        this.isOrientationMerged = false;
        this.isPrintImages = isPrintImages;
    }
    
    public KNNTrialSettings(int kLowerValue, int kUpperValue, boolean isBSSIDMerged, boolean isPrintImages) {
        this.kLowerValue = kLowerValue;
        this.kUpperValue = kUpperValue;
        this.distOption = ALL_DIST_OPTIONS;
        this.varLowerLimit = 0;
        this.varUpperLimit = 1;
        this.varLimitStep = 2;
        this.varLowerCount = 0;
        this.varUpperCount = 0;
        this.isVariance = false;
        this.isBSSIDMerged = isBSSIDMerged;
        this.isOrientationMerged = false;
        this.isPrintImages = isPrintImages;
    }

    public KNNTrialSettings(int kLowerValue, int kUpperValue, int distOption, boolean isBSSIDMerged) {
        this.kLowerValue = kLowerValue;
        this.kUpperValue = kUpperValue;
        this.distOption = checkDistanceOption(distOption);
        this.varLowerLimit = 0;
        this.varUpperLimit = 1;
        this.varLimitStep = 2;
        this.varLowerCount = 0;
        this.varUpperCount = 0;
        this.isVariance = false;
        this.isBSSIDMerged = isBSSIDMerged;
        this.isOrientationMerged = false;
        this.isPrintImages = false;
    }

    public KNNTrialSettings(int kValue, int distOption, double varLimit, int varCount) {
        this.kLowerValue = kValue;
        this.kUpperValue = kValue;
        this.distOption = checkDistanceOption(distOption);
        this.varLowerLimit = varLimit;
        this.varUpperLimit = varLimit;
        this.varLimitStep = 1;
        this.varLowerCount = varCount;
        this.varUpperCount = varCount;
        this.isVariance = true;
        this.isBSSIDMerged = false;
        this.isOrientationMerged = false;
        this.isPrintImages = false;
    }

    public KNNTrialSettings(int kLowerValue, int kUpperValue, int distOption, double varLowerLimit, double varUpperLimit, double varLimitStep, int varLowerCount, int varUpperCount) {
        this.kLowerValue = kLowerValue;
        this.kUpperValue = kUpperValue;
        this.distOption = checkDistanceOption(distOption);
        this.varLowerLimit = varLowerLimit;
        this.varUpperLimit = varUpperLimit;
        this.varLimitStep = varLimitStep;
        this.varLowerCount = varLowerCount;
        this.varUpperCount = varUpperCount;
        this.isVariance = true;
        this.isBSSIDMerged = false;
        this.isOrientationMerged = false;
        this.isPrintImages = false;
    }

    public KNNTrialSettings(int kLowerValue, int kUpperValue, int distOption, double varLowerLimit, double varUpperLimit, double varLimitStep, int varLowerCount, int varUpperCount, boolean isBSSIDMerged) {
        this.kLowerValue = kLowerValue;
        this.kUpperValue = kUpperValue;
        this.distOption = checkDistanceOption(distOption);
        this.varLowerLimit = varLowerLimit;
        this.varUpperLimit = varUpperLimit;
        this.varLimitStep = varLimitStep;
        this.varLowerCount = varLowerCount;
        this.varUpperCount = varUpperCount;
        this.isVariance = true;
        this.isBSSIDMerged = isBSSIDMerged;
        this.isOrientationMerged = false;
        this.isPrintImages = false;
    }

    public KNNTrialSettings(int kLowerValue, int kUpperValue, int distOption, double varLowerLimit, double varUpperLimit, double varLimitStep, int varLowerCount, int varUpperCount, boolean isBSSIDMerged, boolean isOrientationMerged) {
        this.kLowerValue = kLowerValue;
        this.kUpperValue = kUpperValue;
        this.distOption = checkDistanceOption(distOption);
        this.varLowerLimit = varLowerLimit;
        this.varUpperLimit = varUpperLimit;
        this.varLimitStep = varLimitStep;
        this.varLowerCount = varLowerCount;
        this.varUpperCount = varUpperCount;
        this.isVariance = true;
        this.isBSSIDMerged = isBSSIDMerged;
        this.isOrientationMerged = isOrientationMerged;
        this.isPrintImages = false;
    }

    public static String[] getDistanceOptions() {
        //Distance measure
        List<String> options = new ArrayList<>();
        options.add("All");
        for (DistanceMeasure dist : DistanceMeasure.values()) {
            options.add(dist.toString());
        }
        return options.toArray(new String[0]);
    }

    private int checkDistanceOption(int option) {
        //if option is equal or smaller value than the number of DistanceMeasure items (as add an extra item for all) then return option.
        return option <= DistanceMeasure.values().length ? option : 0;
    }

    public DistanceMeasure[] getDistanceMeasure() {

        DistanceMeasure[] distArray;
        if (distOption != 0) {
            DistanceMeasure[] tempArray = DistanceMeasure.values();
            distArray = new DistanceMeasure[1];
            distArray[0] = tempArray[distOption - 1];
        } else {
            distArray = DistanceMeasure.values();
        }

        return distArray;
    }
}
