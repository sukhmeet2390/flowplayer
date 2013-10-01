package com.example.model;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ImageView;

/**
 * Created with IntelliJ IDEA.
 * User: sukhmeet
 * Date: 13/09/13
 * Time: 5:56 PM
 * To change this template use File | Settings | File Templates.
 */
public class DragRectView extends ImageView {

    private Paint paintRectangle;
    private int startX = 0;
    private int startY = 0;
    private int endX = 0;
    private int endY = 0;
    private String MODE;
    private int startDragX, startDragY;

    private int color = Color.CYAN;
    private int endDragX, endDragY;
    private boolean DRAWN_RECT;


    public DragRectView(Context context) {
        super(context);
        init();
    }

    public DragRectView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DragRectView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (!DRAWN_RECT)
            canvas.drawRect(Math.min(startX, endX), Math.min(startY, endY), Math.max(endX, startX), Math.max(endY, startY), paintRectangle);


    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Log.d("ACTION DOWN x", "x" + event.getX());
                Log.d("ACTION DOWN y", "y" + event.getY());
                int x = (int) event.getX();
                int y = (int) event.getY();
                if (touchedInside(x, y)) {

                    MODE = "MOVE";
                    startDragX = startX;
                    startDragY = startY;

                    break;
                } else {
                    startX = (int) event.getX();
                    startY = (int) event.getY();
                    DRAWN_RECT = false;
                    invalidate();
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (MODE == "MOVE") {
                    int dragDistanceX = (int) event.getX() - startDragX;
                    int dragDistanceY = (int) event.getY() - startDragY;

                    startX = startX + dragDistanceX;
                    startY = startY + dragDistanceY;
                    endX = endX + dragDistanceX;
                    endY = endY + dragDistanceY;

                    startDragX = (int) event.getX();
                    startDragY = (int) event.getY();

                    DRAWN_RECT = false;

                } else {
                    endX = (int) event.getX();
                    endY = (int) event.getY();
                }
                invalidate();
                break;
            case MotionEvent.ACTION_UP:

                System.out.println("Draw Rect x action UP");
                System.out.println("Draw Rect x " + startX);
                System.out.println("Draw Rect y " + startY);
                System.out.println("Draw Rect x2 " + endX);
                System.out.println("Draw Rect y2 " + endY);
                DRAWN_RECT = true;
                //invalidate();

                break;
        }
        return true;
    }

    public int getStartX() {
        return startX;
    }

    public int getStartY() {
        return startY;
    }

    public int getEndX() {
        return endX;
    }

    public int getEndY() {
        return endY;
    }

    private void init() {
        paintRectangle = new Paint();
        paintRectangle.setColor(color);
        paintRectangle.setStyle(Paint.Style.FILL);
        paintRectangle.setStrokeWidth(10);
        paintRectangle.setAlpha(125);
        DRAWN_RECT = false;
    }

    private boolean touchedInside(int x, int y) {
        return (x > Math.min(startX, endX) && x < Math.max(startX, endX)
                && y > Math.min(startY, endY) && y < Math.max(startY, endY));
    }
}
