package com.photoscanner.app;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Custom view for displaying and adjusting detected document edges
 */
public class EdgeDetectionView extends View {
    private static final String TAG = "EdgeDetectionView";
    private static final int CORNER_RADIUS = 30;
    private static final int TOUCH_AREA_SIZE = 50;
    
    private Paint linePaint;
    private Paint cornerPaint;
    private Paint fillPaint;
    
    private Point[] corners;
    private int selectedCornerIndex = -1;
    private boolean edgesDetected = false;
    
    private Path edgePath;
    
    public EdgeDetectionView(Context context) {
        super(context);
        init();
    }
    
    public EdgeDetectionView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }
    
    public EdgeDetectionView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }
    
    private void init() {
        // Initialize paint for edge lines
        linePaint = new Paint();
        linePaint.setColor(Color.BLUE);
        linePaint.setStrokeWidth(5);
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setAntiAlias(true);
        
        // Initialize paint for corners
        cornerPaint = new Paint();
        cornerPaint.setColor(Color.RED);
        cornerPaint.setStyle(Paint.Style.FILL);
        cornerPaint.setAntiAlias(true);
        
        // Initialize paint for semi-transparent fill
        fillPaint = new Paint();
        fillPaint.setColor(Color.BLUE);
        fillPaint.setAlpha(40);
        fillPaint.setStyle(Paint.Style.FILL);
        
        edgePath = new Path();
    }
    
    public void setCorners(Point[] corners) {
        this.corners = corners;
        this.edgesDetected = (corners != null && corners.length == 4);
        updateEdgePath();
        invalidate();
    }
    
    public Point[] getCorners() {
        return corners;
    }
    
    private void updateEdgePath() {
        edgePath.reset();
        if (edgesDetected) {
            edgePath.moveTo(corners[0].x, corners[0].y);
            edgePath.lineTo(corners[1].x, corners[1].y);
            edgePath.lineTo(corners[2].x, corners[2].y);
            edgePath.lineTo(corners[3].x, corners[3].y);
            edgePath.close();
        }
    }
    
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        
        if (!edgesDetected) return;
        
        // Draw the semi-transparent fill
        canvas.drawPath(edgePath, fillPaint);
        
        // Draw the edge lines
        canvas.drawPath(edgePath, linePaint);
        
        // Draw the corner circles
        for (int i = 0; i < 4; i++) {
            canvas.drawCircle(corners[i].x, corners[i].y, CORNER_RADIUS, cornerPaint);
        }
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!edgesDetected) return false;
        
        float x = event.getX();
        float y = event.getY();
        
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // Check if the user is touching a corner
                selectedCornerIndex = getTouchedCornerIndex(x, y);
                return selectedCornerIndex != -1;
                
            case MotionEvent.ACTION_MOVE:
                if (selectedCornerIndex != -1) {
                    // Move the selected corner
                    corners[selectedCornerIndex].x = (int) x;
                    corners[selectedCornerIndex].y = (int) y;
                    updateEdgePath();
                    invalidate();
                    return true;
                }
                break;
                
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                selectedCornerIndex = -1;
                break;
        }
        
        return super.onTouchEvent(event);
    }
    
    private int getTouchedCornerIndex(float x, float y) {
        if (corners == null) return -1;
        
        for (int i = 0; i < corners.length; i++) {
            float dx = x - corners[i].x;
            float dy = y - corners[i].y;
            float distance = (float) Math.sqrt(dx * dx + dy * dy);
            
            if (distance < TOUCH_AREA_SIZE) {
                return i;
            }
        }
        
        return -1;
    }
    
    public boolean areEdgesDetected() {
        return edgesDetected;
    }
}

