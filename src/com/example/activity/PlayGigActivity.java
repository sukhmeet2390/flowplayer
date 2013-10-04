package com.example.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import com.example.R;

import com.example.database.ContentProvider.DbHelper;
import com.example.model.HotSpotRectangle;
import com.example.model.ViewScreen;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Stack;

/**
 * Created with IntelliJ IDEA.
 * User: sukhmeet
 * Date: 24/09/13
 * Time: 2:53 PM
 * To change this template use File | Settings | File Templates.
 */
public class PlayGigActivity extends Activity {


    private String TAG = "PLAYGIG";

    ImageView imageView;
    DbQueryActivity queries;
    Uri currentImage;
    ViewScreen currentScreen;
    long gigId;
    String gigName;
    Stack<ViewScreen> backScreenStack;

    @Override
    public void onBackPressed() {
        Log.d(TAG, "Back button pressed");
        if (backScreenStack.size() == 0) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            return;
        }
        currentScreen = backScreenStack.pop();
        currentImage = currentScreen.getCurrentImage();

        Bitmap bitmap = com.example.Util.getSampledBitmap(currentImage, this,imageView.getWidth(),imageView.getHeight());
        imageView.setImageBitmap(bitmap);
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.play_gig);
        init();

        Bundle extras = getIntent().getExtras();
        gigId = extras.getLong("gig_id", -1);

        String name = queries.findGigNameById(gigId, this);
        gigName = name;
        setTitle(name);

        currentImage = queries.findFirstImageByGigName(name, this);
        currentScreen = fetchScreenData(currentImage);

        setImageTouchListener();
    }
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(hasFocus) {
            Bitmap bitmap = com.example.Util.getSampledBitmap(currentImage,getApplicationContext(),imageView.getWidth(),imageView.getHeight());
            imageView.setImageBitmap(bitmap);

        }
    }

    // ---- END OF PUBLIC INTERFACE-----
    private void init() {
        currentScreen = new ViewScreen();
        queries = new DbQueryActivity(getApplicationContext());
        backScreenStack = new Stack<ViewScreen>();
        imageView = (ImageView) findViewById(R.id.imagePlay);
    }

    private void setImageTouchListener() {
        imageView.setOnTouchListener(new ImageView.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                float x = motionEvent.getX();
                float y = motionEvent.getY();
                float[] coordinates = com.example.Util.transformCoordinates(x, y, imageView);
                x = coordinates[0];
                y = coordinates[1];

                Uri nextImage = com.example.Util.getHitOrMiss(x, y, currentScreen.getHotSpotRectangles());
                if (nextImage!= null && !nextImage.toString().equals("null"))  {
                    backScreenStack.add(currentScreen);
                    currentImage = nextImage;
                    currentScreen = fetchScreenData(currentImage);
                    Bitmap bitmap = com.example.Util.getSampledBitmap(currentImage,getApplicationContext(), imageView.getWidth(),imageView.getHeight());
                    imageView.setImageBitmap(bitmap);

                }else{
                    //Toast.makeText(getApplicationContext(), " THis is the End !!", Toast.LENGTH_LONG).show();
                    return true;
                }
                return true;
            }
        });
    }

    private ViewScreen fetchScreenData(Uri nextImage) {
        ArrayList<HotSpotRectangle> hotSpotRectangleArrayList =queries.getHotSpotsOfImageByImageUri(nextImage, gigName, this);
        ViewScreen screen = com.example.Util.valuesToScreen(gigId, gigName, nextImage, hotSpotRectangleArrayList);
        return screen;
    }
}

