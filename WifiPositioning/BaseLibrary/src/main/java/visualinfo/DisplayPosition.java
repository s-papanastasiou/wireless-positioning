/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package visualinfo;

import general.Point;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import javax.imageio.ImageIO;
import datastorage.Location;
import datastorage.ResultLocation;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Greg Albiston
 */
public class DisplayPosition {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(DisplayPosition.class);

    private static final int SIZE = 10;
    private static final int HALF_SIZE = SIZE / 2;

    
    /**
     * Draws the point onto the specified image and stores in
     * the specified file.
     *
     * @param workingPath Path to store the image.
     * @param filename Name of file to print, without file extension.   
     * @param floorPlanFile Floor plan to draw upon.
     * @param finalPosition Final position to be drawn.
     */
    public static void print(final File workingPath, final String filename, final File floorPlanFile, final Point finalPosition) {

        try {
            BufferedImage image = render(floorPlanFile, finalPosition);
            File outputFile = new File(workingPath, filename + ".png");
            ImageIO.write(image, "png", outputFile);
            
        } catch (IOException ex) {
            logger.error(ex.getMessage());
        }
    }
    
    /**
     * Draws the point onto the specified image and stores in
     * the specified file.
     *
     * @param workingPath Path to store the image.
     * @param filename Name of file to print, without file extension.   
     * @param floorPlanFile Floor plan to draw upon.
     * @param finalPosition Final position to be drawn.
     * @param testLocation Test location to be drawn.     
     */
    public static void print(final File workingPath, final String filename, final File floorPlanFile, final Point finalPosition, final Location testLocation) {

        try {
            BufferedImage image = render(floorPlanFile, finalPosition, testLocation);
            File outputFile = new File(workingPath, filename + ".png");
            ImageIO.write(image, "png", outputFile);
            
        } catch (IOException ex) {
            logger.error(ex.getMessage());
        }
    }
    
    /**
     * Draws the point onto the specified image and stores in
     * the specified file.
     *
     * @param workingPath Path to store the image.
     * @param filename Name of file to print, without file extension.   
     * @param floorPlanFile Floor plan to draw upon.
     * @param finalPosition Final position to be drawn.
     * @param testLocation Test location to be drawn.
     * @param positionEstimates List of position estimates.
     */
    public static void print(final File workingPath, final String filename, final File floorPlanFile, final Point finalPosition, final Location testLocation, final List<ResultLocation> positionEstimates) {

        try {
            BufferedImage image = render(floorPlanFile, finalPosition, testLocation, positionEstimates);
            File outputFile = new File(workingPath, filename + ".png");
            ImageIO.write(image, "png", outputFile);
            
        } catch (IOException ex) {
            logger.error(ex.getMessage());
        }
    }
    
    /**
     * Render just the final and test position
     * 
     * @param floorPlanFile Floor plan to draw upon.
     * @param finalPosition Final position to be drawn.     
     * @return Drawn floor plan.
     */
    public static BufferedImage render(final File floorPlanFile, final Point finalPosition) {

        return render(floorPlanFile, finalPosition, null, null);
    }

    /**
     * Render just the final and test position
     * 
     * @param floorPlanFile Floor plan to draw upon.
     * @param finalPosition Final position to be drawn.
     * @param testLocation Test location to be drawn.
     * @return Drawn floor plan.
     */
    public static BufferedImage render(final File floorPlanFile, final Point finalPosition, final Location testLocation) {

        return render(floorPlanFile, finalPosition, testLocation, null);
    }

    /**
     * Render just the final and test position
     * 
     * @param floorPlanFile Floor plan to draw upon.
     * @param finalPosition Final position to be drawn.
     * @param testLocation Test location to be drawn.
     * @param positionEstimates List of position estimates.
     * @return Drawn floor plan.
     */
    public static BufferedImage render(final File floorPlanFile, final Point finalPosition, final Location testLocation, final List<ResultLocation> positionEstimates) {

        BufferedImage floorPlanImage = null;

        try {

            //load floor plan image            
            floorPlanImage = ImageIO.read(floorPlanFile);

            if (positionEstimates!=null){
                floorPlanImage = drawEstimates(floorPlanImage, positionEstimates);
                floorPlanImage = drawLines(floorPlanImage, positionEstimates, finalPosition);
            }
            
            if (testLocation!=null)
                floorPlanImage = drawTest(floorPlanImage, testLocation);

            floorPlanImage = drawFinal(floorPlanImage, finalPosition);

        } catch (IOException ex) {
            logger.error(ex.getMessage());
        }
        return floorPlanImage;
    }

    private static BufferedImage drawLines(final BufferedImage floorPlanImage, final List<ResultLocation> positionEstimates, Point finalPosition) throws IOException {

        Graphics2D floorPlan = floorPlanImage.createGraphics();

        for (int counter = 0; counter < positionEstimates.size(); counter++) {
            ResultLocation estimate = positionEstimates.get(counter);

            floorPlan.setColor(getColor(counter));

            Point pos = estimate.getDrawPoint();
            //Draw line from the estimate to the final position
            floorPlan.drawLine(pos.getXint(), pos.getYint(), finalPosition.getXint(), finalPosition.getYint());

        }
        
        floorPlan.dispose();
        return floorPlanImage;
    }

    private static BufferedImage drawEstimates(final BufferedImage floorPlanImage, List<ResultLocation> positionEstimates) throws IOException {

        Graphics2D floorPlan = floorPlanImage.createGraphics();

        for (int counter = 0; counter < positionEstimates.size(); counter++) {
            ResultLocation estimate = positionEstimates.get(counter);

            //find duplicates and remove - number of duplications recorded next to the point - also more efficient use of available colours
            int duplicateCount = 1;
            for (int i = counter + 1; i < positionEstimates.size(); i++) {
                ResultLocation matchEstimate = positionEstimates.get(i);

                if (matchEstimate.equals(estimate)) {
                    duplicateCount++;
                    positionEstimates.remove(i);
                    i--;
                }
            }

            floorPlan.setColor(getColor(counter));

            Point pos = estimate.getDrawPoint();

            //Draw oval                
            floorPlan.fillOval(pos.getXint() - HALF_SIZE, pos.getYint() - HALF_SIZE, SIZE, SIZE);

            //include text label for duplicates           
            if (duplicateCount != 1) {
                floorPlan.setColor(Color.BLACK);
                floorPlan.drawString(String.valueOf(duplicateCount), pos.getXint() + HALF_SIZE, pos.getYint());
            }

        }

        floorPlan.dispose();
        return floorPlanImage;
    }

    private static BufferedImage drawFinal(final BufferedImage floorPlanImage, final Point finalPosition) throws IOException {

        Graphics2D floorPlan = floorPlanImage.createGraphics();

        //Draw the final point
        floorPlan.setColor(Color.GREEN);
        //Draw oval        
        floorPlan.fillOval(finalPosition.getXint() - HALF_SIZE, finalPosition.getYint() - HALF_SIZE, SIZE, SIZE);

        floorPlan.dispose();
        return floorPlanImage;
    }

    private static BufferedImage drawTest(final BufferedImage floorPlanImage, final Location testLocation) throws IOException {

        Graphics2D floorPlan = floorPlanImage.createGraphics();

        floorPlan.setColor(Color.MAGENTA);

        Point pos = testLocation.getDrawPoint();
        //Draw oval                
        floorPlan.fillOval(pos.getXint() - HALF_SIZE, pos.getYint() - HALF_SIZE, SIZE, SIZE);

        floorPlan.dispose();
        return floorPlanImage;
    }

    private static Color getColor(int index) {

        Color color;
        switch (index) {
            case 0:
                color = Color.BLUE;
                break;
            case 1:
                color = Color.CYAN;
                break;
            case 2:
                color = Color.RED;
                break;
            case 3:
                color = Color.PINK;
                break;
            case 4:
                color = Color.ORANGE;
                break;
            case 5:
                color = Color.LIGHT_GRAY;
                break;
            case 6:
                color = Color.DARK_GRAY;
                break;
            case 7:
                color = Color.YELLOW;
                break;
            case 8:
                color = Color.GRAY;
                break;
            default:
                color = Color.BLACK;
                break;
        }
        return color;
    }
}
