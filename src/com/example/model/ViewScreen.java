package com.example.model;

import android.net.Uri;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: sukhmeet
 * Date: 24/09/13
 * Time: 3:56 PM
 * To change this template use File | Settings | File Templates.
 */
public class ViewScreen {
    public long screen_id;
    public String gig_name;
    public Uri currentImage;
    public Uri parentImage;
    public ArrayList<HotSpotRectangle> hotSpotRectangles;
    public Uri childImage;

    public ViewScreen() {
    }

    public ViewScreen(long screen_id, String gig_name, Uri imagePath, ArrayList<HotSpotRectangle> hotSpotRectangles) {
        this.screen_id = screen_id;
        this.gig_name = gig_name;
        this.currentImage = imagePath;
        this.hotSpotRectangles = hotSpotRectangles;
    }

    public long getScreen_id() {
        return screen_id;
    }

    public void setScreen_id(long screen_id) {
        this.screen_id = screen_id;
    }
    public String getGig_name() {
        return gig_name;
    }

    public void setGig_name(String gig_name) {
        this.gig_name = gig_name;
    }


    public Uri getCurrentImage() {
        return currentImage;
    }

    public void setCurrentImage(Uri currentImage) {
        this.currentImage = currentImage;
    }

    public ArrayList<HotSpotRectangle> getHotSpotRectangles() {
        return hotSpotRectangles;
    }

    public void setHotSpotRectangles(ArrayList<HotSpotRectangle> hotSpotRectangles) {
        this.hotSpotRectangles = hotSpotRectangles;
    }
}
