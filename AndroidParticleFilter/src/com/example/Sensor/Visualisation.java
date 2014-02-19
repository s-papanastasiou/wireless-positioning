package com.example.Sensor;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

/**
 * Created with IntelliJ IDEA.
 * User: pierre
 * Date: 08/10/13
 * Time: 17:27
 * To change this template use File | Settings | File Templates.
 */

public class Visualisation extends View {

    protected Drawable floorPlanImage;
    private final int STATUS_BAR = 100;
    private Point screenSize = new Point();
    private general.Point probabilisticPoint = new general.Point();
    private general.Point particlePoint = new general.Point();
    private general.Point inertialPoint = new general.Point();
    private general.Point bestPoint = new general.Point();
    private static final float RADIUS = 5;
    private Paint probabilisticPaint = new Paint();
    private Paint particlePaint = new Paint();
    private Paint inertialPaint = new Paint();
    private Paint bestPaint = new Paint();
    private static final double X_PIXELS = 1280.0 / 53.4;
    private static final double Y_PIXELS = 630.0 / 24.0;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public Visualisation(Context context, AttributeSet attrs) {

        super(context, attrs);

        //Adjust to fill screen - not caring about aspect ratio at current time but could be issue later.
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        display.getSize(this.screenSize);

        probabilisticPaint.setColor(Color.RED);
        particlePaint.setColor(Color.BLUE);
        inertialPaint.setColor(Color.GREEN);
        bestPaint.setColor(Color.MAGENTA);

    }

    public void setFloorPlan(Drawable floorPlanImage) {
        this.floorPlanImage = floorPlanImage;
        floorPlanImage.setBounds(0, 0, screenSize.x, screenSize.y - STATUS_BAR);
    }

    public void setPoint(general.Point probabilisticPoint, general.Point particlePoint, general.Point inertialPoint, general.Point corridorPoint) {

        this.probabilisticPoint = new general.Point(probabilisticPoint.getX() * X_PIXELS, probabilisticPoint.getY() * Y_PIXELS);
        this.particlePoint = new general.Point(particlePoint.getX() * X_PIXELS, particlePoint.getY() * Y_PIXELS);
        this.inertialPoint = new general.Point(inertialPoint.getX() * X_PIXELS, inertialPoint.getY() * Y_PIXELS);
        this.bestPoint = new general.Point(corridorPoint.getX() * X_PIXELS, corridorPoint.getY() * Y_PIXELS);
        this.invalidate();
    }

    public void clear() {
        this.probabilisticPoint = new general.Point();
        this.particlePoint = new general.Point();
        this.inertialPoint = new general.Point();
        this.bestPoint = new general.Point();
        this.invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        floorPlanImage.draw(canvas);

        canvas.drawCircle(probabilisticPoint.getXfl(), probabilisticPoint.getYfl(), RADIUS, probabilisticPaint);
        canvas.drawCircle(particlePoint.getXfl(), particlePoint.getYfl(), RADIUS + 2, particlePaint);
        canvas.drawCircle(inertialPoint.getXfl(), inertialPoint.getYfl(), RADIUS, inertialPaint);
        canvas.drawCircle(bestPoint.getXfl(), bestPoint.getYfl(), RADIUS + 2, bestPaint);
    }
}
