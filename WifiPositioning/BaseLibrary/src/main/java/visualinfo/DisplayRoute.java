/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package visualinfo;

import general.Point;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import javax.imageio.ImageIO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Draws paths of points onto an image.
 *
 * @author Greg Albiston
 */
public class DisplayRoute {

    private static final Logger logger = LoggerFactory.getLogger(DisplayRoute.class);

    private final static int HALF_SIZE = 4;
    private final static int SIZE = HALF_SIZE * 2;
    private final static int CIRCLE_SIZE = HALF_SIZE * 3;
    private final static int HALF_CIRCLE_SIZE = CIRCLE_SIZE / 2;

    private final static int POINT_WIDTH = 1;
    private final static int LINE_WIDTH = 3;

    /**
     * Draws the trial and final routes onto the specified image and stores in
     * the specified file. Defaults colour red for trial path and blue for final
     * path.
     *
     * @param workingPath File to store image.
     * @param filename Name of file to print, without file extension.
     * @param floorPlanFile Image file to draw upon.     
     * @param trialPoints List of points in the trial.
     * @param finalPoints List of points in final estimated points.
     */
    public static void print(File workingPath, String filename, File floorPlanFile, List<Point> trialPoints, List<Point> finalPoints) {
        print(workingPath, filename, floorPlanFile, trialPoints, finalPoints, Color.RED, Color.BLUE);
    }

    /**
     * Draws the trial and final routes onto the specified image and stores in
     * the specified file.
     *
     * @param workingPath Path to store the image.
     * @param filename Name of file to print, without file extension.   
     * @param floorPlanFile Image file to draw upon.
     * @param trialPoints List of points in the trial.
     * @param finalPoints List of points in final estimated points.workingPath@param routeFile File to store image.
     * @param trialColor Colour of trial path.
     * @param finalColor Colour of final path.
     */
    public static void print(File workingPath, String filename, File floorPlanFile, List<Point> trialPoints, List<Point> finalPoints, Color trialColor, Color finalColor) {

        try {
            BufferedImage floorPlanImage = ImageIO.read(floorPlanFile);
            Graphics2D floorPlan = floorPlanImage.createGraphics();

            floorPlan.setFont(new Font("Arial", Font.PLAIN, 12));

            floorPlan.setColor(trialColor);
            drawLines(floorPlan, trialPoints);
            drawPoints(floorPlan, trialPoints);

            floorPlan.setColor(finalColor);
            drawLines(floorPlan, finalPoints);
            drawPoints(floorPlan, finalPoints);

            floorPlan.dispose();

            File outputfile = new File(workingPath, filename + ".png");
            ImageIO.write(floorPlanImage, "png", outputfile);

        } catch (IOException ex) {
            logger.error(ex.getMessage());
        }
    }

    //draws points
    private static void drawPoints(Graphics2D floorPlan, List<Point> points) {

        floorPlan.setStroke(new BasicStroke(POINT_WIDTH));
        //Draw rectangle for all earlier points   
        for (int counter = 1; counter < points.size() - 1; counter++) {
            Point point = points.get(counter);   //current point            
            floorPlan.fillRect(point.getXint() - HALF_SIZE, point.getYint() - HALF_SIZE, SIZE, SIZE);
            floorPlan.drawString(String.valueOf(counter), point.getXint() - HALF_SIZE, point.getYint() - SIZE);
        }

        //First point a triangle and last point is drawn with an oval.
        if (!points.isEmpty()) {

            //First Point
            Point firstPoint = points.get(0);
            int x[] = {firstPoint.getXint() - SIZE, firstPoint.getXint(), firstPoint.getXint() + SIZE};
            int y[] = {firstPoint.getYint() + SIZE, firstPoint.getYint() - SIZE, firstPoint.getYint() + SIZE};
            floorPlan.fillPolygon(x, y, 3);
            floorPlan.drawString(String.valueOf(0), firstPoint.getXint() - HALF_SIZE, firstPoint.getYint() - SIZE);

            //Last Point
            Point lastPoint = points.get(points.size() - 1);
            floorPlan.fillOval(lastPoint.getXint() - HALF_CIRCLE_SIZE, lastPoint.getYint() - HALF_CIRCLE_SIZE, CIRCLE_SIZE, CIRCLE_SIZE);
            floorPlan.drawString(String.valueOf(points.size() - 1), lastPoint.getXint() - HALF_SIZE, lastPoint.getYint() - SIZE);
        }
    }

    private final static float dash[] = {10.0f};
    private final static BasicStroke dashed
            = new BasicStroke(LINE_WIDTH,
                    BasicStroke.CAP_BUTT,
                    BasicStroke.JOIN_MITER,
                    10.0f, dash, 0.0f);

    //draws lines between points in the list
    private static void drawLines(Graphics2D floorPlan, List<Point> points) {

        //floorPlan.setStroke(new BasicStroke(LINE_WIDTH));
        floorPlan.setStroke(dashed);
        for (int counter = 0; counter < points.size() - 1; counter++) {
            Point cPoint = points.get(counter);   //current point
            Point nPoint = points.get(counter + 1);   //next point

            floorPlan.drawLine(cPoint.getXint(), cPoint.getYint(), nPoint.getXint(), nPoint.getYint());
        }
    }
}
