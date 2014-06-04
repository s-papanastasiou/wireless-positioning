package me.gregalbiston.androiddetector.scan;

import me.gregalbiston.androiddetector.storage.FileOutput;

/**
 * Holds settings for Scanner.
 * @author Greg Albiston
 */
public class ScanSettings {

    private final FileOutput fileOutput;
    private final boolean isRSSISelected;
    private final boolean isMagneticSelected;

    public ScanSettings(FileOutput fileOutput, boolean isRSSISelected, boolean isMagneticSelected) {
        this.fileOutput = fileOutput;
        this.isRSSISelected = isRSSISelected;
        this.isMagneticSelected = isMagneticSelected;
    }

    public boolean isRSSISelected() {
        return isRSSISelected;
    }

    public boolean isMagneticSelected() {
        return isMagneticSelected;
    }

    public FileOutput getFileOutput() {
        return fileOutput;
    }

}
