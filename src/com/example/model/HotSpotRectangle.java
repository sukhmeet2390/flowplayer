package com.example.model;

import android.net.Uri;

/**
 * Created with IntelliJ IDEA.
 * User: sukhmeet
 * Date: 24/09/13
 * Time: 3:11 PM
 * To change this template use File | Settings | File Templates.
 */
public class HotSpotRectangle {
    private Rectangle rectangle;
    private Uri gotoImage;


    public HotSpotRectangle() {
        this.rectangle = new Rectangle(-1, -1, -1, -1);
        this.gotoImage = null;
    }

    public HotSpotRectangle(float x1, float y1, float x2, float y2, Uri gotoImage) {

    }

    public Rectangle getRectangle() {
        return rectangle;
    }


    public Uri getGotoImage() {
        return gotoImage;
    }

    public void setGotoImage(Uri gotoImage) {
        this.gotoImage = gotoImage;
    }
}
