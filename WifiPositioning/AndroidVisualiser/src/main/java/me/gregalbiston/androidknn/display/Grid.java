package me.gregalbiston.androidknn.display;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import datastorage.KNNFloorPoint;
import datastorage.LabelPoint;
import datastorage.Location;
import datastorage.RoomInfo;
import general.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Greg Albiston
 * Date: 22/07/13
 * Time: 22:33
 * Displays grid points on screen and identifies the screen co-ordinates of points.
 */
public class Grid {

    private final Paint paint = new Paint();
    private final float HALF_SIZE = 2f;
    private final float SIZE = 4f;
    private final int STROKE_WIDTH = 1;
    private List<LabelPoint> labelPoints = new ArrayList<>();
    private HashMap<String, RoomInfo> roomInfo = new HashMap<>();

    private boolean isShowGrid = false;

    public Grid(HashMap<String, KNNFloorPoint> radioMap, HashMap<String, RoomInfo> roomInfo, boolean isShowGrid, int colourGrid) {
        this.roomInfo = roomInfo;

        this.isShowGrid = isShowGrid;

        labelPoints = LabelPoint.list(radioMap);

        paint.setColor(colourGrid);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(STROKE_WIDTH);
    }

    public Grid() {
    }

    public void showGrid(boolean isShowGrid) {
        this.isShowGrid = isShowGrid;
    }

    public void setColour(int colourGrid) {
        paint.setColor(colourGrid);
    }

    public void draw(Canvas canvas) {

        if (isShowGrid) {
            for (LabelPoint point : labelPoints) {
                RectF rect = new RectF(point.getXfl() - HALF_SIZE, point.getYfl() - HALF_SIZE, point.getXfl() + HALF_SIZE, point.getYfl() + HALF_SIZE);
                canvas.drawOval(rect, paint);
                canvas.drawText(point.getLabel(), point.getXfl() - SIZE, point.getYfl() - SIZE, paint);
            }
        }
    }

    public Location find(Point screenPoint) {

        return RoomInfo.searchPixelLocation(screenPoint, roomInfo);
    }

}
