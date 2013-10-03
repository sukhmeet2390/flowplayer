package com.example.model;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;


public class DragRectView extends ImageView {

    public int startX, startY, endX, endY;
    public float startDragX, dragX, startDragY, dragY;

    Paint paint = new Paint();

    public static int DRAG = 0;
    public static int DRAW = 1;
    public static int EXPAND = 2;
    public static int EXPAND_TYPE = 0;
    public static int BORDER = 0;
    public static int CORNER = 1;
    public static int CURRENT_MODE = DRAG;
    public static int MINIMUM_DISTANCE = 20;
    public static int EXPAND_BORDER = 0;
    public static int EXPAND_CORNER = 0;
    public static int CIRCLE_RADIUS = 5;
    public static boolean SELECTION_COMPLETE = false;


    public DragRectView(Context context, int startX) {
        super(context);
        this.startX = startX;
    }

    public DragRectView(Context context, AttributeSet attrs) {
        super(context, attrs);

        init();

    }

    public DragRectView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public int getStartX() {
        return startX;
    }


    public int getEndX() {
        return endX;
    }


    public int getStartY() {
        return startY;
    }


    public int getEndY() {
        return endY;
    }

    @Override
    public boolean onTouchEvent(final MotionEvent event) {
        try{
            switch(event.getAction()) {
                case MotionEvent.ACTION_DOWN:

                    float [] coordinates = new float[]{event.getX(), event.getY()};
                    Matrix matrix = new Matrix();
                    this.getImageMatrix().invert(matrix);
                    matrix.postTranslate(this.getScrollX(), this.getScrollY());
                    matrix.mapPoints(coordinates);

                    SELECTION_COMPLETE = false;
                    if(onTouchCorner(event.getX(), event.getY())) {
                        CURRENT_MODE = EXPAND;
                        EXPAND_TYPE = CORNER;
                        break;
                    }

                    if(touchedInside(event.getX(), event.getY())) {
                        CURRENT_MODE = DRAG;
                        startDragX = (int) event.getX();
                        startDragY = (int) event.getY();
                        break;
                    }
                    CURRENT_MODE = DRAW;
                    startX = (int) event.getX();
                    startY = (int) event.getY();
                    endX = (int) event.getX();
                    endY = (int) event.getY();
                    invalidate();
                    break;
                case MotionEvent.ACTION_MOVE:
                    if(CURRENT_MODE != EXPAND && ((distance(event.getX(), event.getY(), startX, startY) < MINIMUM_DISTANCE) ||
                            (Math.abs(startX - event.getX()) < MINIMUM_DISTANCE) ||
                            (Math.abs(startY- event.getY()) < MINIMUM_DISTANCE)
                    )) {
                        SELECTION_COMPLETE = false;
                        invalidate();
                        break;
                    }
                    SELECTION_COMPLETE = true;
                    if(CURRENT_MODE == DRAG) {
                        dragX = (int) event.getX()-startDragX;
                        dragY = (int) event.getY()-startDragY;
                        startDragX = (int) event.getX();
                        startDragY = (int) event.getY();
                        endX += dragX;
                        startX += dragX;
                        startY += dragY;
                        endY += dragY;
                    }
                    else if(CURRENT_MODE == DRAW) {
                        endX = (int) event.getX();
                        endY = (int) event.getY();
                    }
                    else if(CURRENT_MODE == EXPAND) {
                        if(EXPAND_TYPE == CORNER) {
                            if(EXPAND_CORNER == 0) {
                                if(startX < endX) startX = (int)event.getX();
                                else endX = (int)event.getX();
                                if(startY < endY) startY = (int)event.getY();
                                else endY = (int)event.getY();
                            }
                            else if(EXPAND_CORNER == 1) {
                                if(startX > endX) startX = (int)event.getX();
                                else endX = (int) event.getX();
                                if(startY < endY) startY = (int) event.getY();
                                else endY = (int) event.getY();
                            }
                            else if(EXPAND_CORNER == 2) {
                                if(startX > endX) startX = (int) event.getX();
                                else endX = (int) event.getX();
                                if(startY > endY) startY = (int) event.getY();
                                else endY = (int)event.getY();
                            }
                            else if(EXPAND_CORNER == 3) {
                                if(startX < endX) startX = (int)event.getX();
                                else endX =(int) event.getX();
                                if(startY > endY) startY = (int)event.getY();
                                else endY = (int)event.getY();
                            }
                        }
                    }
                    invalidate();
                    break;
                case MotionEvent.ACTION_UP:
                    SELECTION_COMPLETE = true;

                    break;
            }
        }
        catch (Exception e) {
            e.printStackTrace();

        }
        return true;
    }

    private boolean touchedInside(float x, float y) {
        return x < Math.max(startX, endX) && x > Math.min(startX, endX) && y < Math.max(startY, endY) && y > Math.min(startY, endY);
    }


    private boolean onTouchCorner(float x, float y) {
        if(detectCorner(x, y, Math.min(startX, endX), Math.min(startY, endY))) { // Top left corner
            EXPAND_CORNER = 0;
            return true;
        }
        if(detectCorner(x, y, Math.max(startX, endX), Math.min(startY, endY))) { //Top right corner
            EXPAND_CORNER = 1;
            return true;
        }
        if(detectCorner(x, y, Math.max(startX, endX), Math.max(startY, endY))) {//Bottom right corner
            EXPAND_CORNER = 2;
            return true;
        }
        if(detectCorner(x, y, Math.min(startX, endX), Math.max(startY, endY))) { //Bottom left corner
            EXPAND_CORNER = 3;
            return true;
        }
        return false;
    }

    private boolean detectCorner(float x1, float y1, float x2, float y2) {
        return distance(x1, y1, x2, y2) <= MINIMUM_DISTANCE;
    }

    private float distance(float x1, float y1, float x2, float y2) {
        return (float) Math.sqrt(((x2-x1)*(x2-x1)) + ((y2-y1)*(y2-y1)));
    }



    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(SELECTION_COMPLETE) {
            canvas.drawRect(Math.min(startX, endX), Math.min(startY, endY), Math.max(startX, endX), Math.max(startY, endY), paint);

        }
    }
    private void init(){
        DRAW = 1;
        DRAG = 0;
        EXPAND = 2;
        EXPAND_TYPE = 0;
        BORDER = 0;
        CORNER = 1;
        CURRENT_MODE = DRAG;
        MINIMUM_DISTANCE = 20;
        EXPAND_BORDER = 0;
        EXPAND_CORNER = 0;

        CIRCLE_RADIUS = 5;
        SELECTION_COMPLETE = false;
        paint.setColor(Color.rgb(87,151,238));
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(10);
        paint.setAlpha(125);
    }
}
// Source GIT HUB Libraries