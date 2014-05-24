package me.gregalbiston.androiddetector.scan;

import me.gregalbiston.androiddetector.storage.FileOutput;

/**
 * Created with IntelliJ IDEA.
 * User: Gerg
 * Date: 03/09/13
 * Time: 16:59
 * Holds settings for Scanner.
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
