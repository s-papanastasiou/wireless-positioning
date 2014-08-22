package me.gregalbiston.androidknn.display;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import datastorage.Location;
import me.gregalbiston.androidknn.R;
import me.gregalbiston.androidknn.VisActivity;
import me.gregalbiston.androidknn.datacollection.GeomagneticScanner;
import me.gregalbiston.androidknn.datacollection.RSSIScanner;

/**
 * Created with IntelliJ IDEA.
 * User: Greg Albiston
 * Date: 22/07/13
 * Time: 15:24
 * Floor view to show floor plan and handle touch interactions.
 */
public class FloorView extends View {

    protected Drawable floorPlanImage;

    private final int STATUS_BAR = 100;
    private final Point screenSize = new Point();

    private Grid grid = new Grid();
    private PointResults pointResults = new PointResults();

    private VisActivity visActivity;

    public FloorView(Context context, AttributeSet attrs) {
        super(context, attrs);

        //default floor plan loaded - allows main.xml to render
        Resources res = getResources();
        floorPlanImage = res.getDrawable(R.drawable.floor2);

        //Adjust to fill screen - not caring about aspect ratio at current time but could be issue later.
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        display.getSize(this.screenSize);
        floorPlanImage.setBounds(0, 0, screenSize.x, screenSize.y - STATUS_BAR);

    }

    public void setFloorPlan(Drawable floorPlanImage) {
        this.floorPlanImage = floorPlanImage;
        floorPlanImage.setBounds(0, 0, screenSize.x, screenSize.y - STATUS_BAR);
    }

    public void setGrid(Grid grid) {
        this.grid = grid;
    }

    public void setActivity(VisActivity visActivity) {
        this.visActivity = visActivity;
    }

    public void setPointResults(PointResults pointResults) {
        this.pointResults = pointResults;
    }


    @Override
    public boolean onTouchEvent(MotionEvent e) {

        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:

                general.Point screenPoint = new general.Point(e.getX(), e.getY());
                Location location = grid.find(screenPoint);

                RSSIScanner.scan(visActivity, location, screenPoint);
                GeomagneticScanner.scan(visActivity, location, screenPoint);
                break;
        }

        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        floorPlanImage.draw(canvas);
        grid.draw(canvas);
        pointResults.draw(canvas);

    }

}
