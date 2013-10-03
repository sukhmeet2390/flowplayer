package com.example.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import com.example.R;
import com.example.database.ContentProvider.GigContentProvider;
import com.example.model.DragRectView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

/**
 * Created with IntelliJ IDEA.
 * User: sukhmeet
 * Date: 01/10/13
 * Time: 3:55 PM
 * To change this template use File | Settings | File Templates.
 */
public class CreateActivity extends Activity {
    private static final String TAG = "CreateGig";

    private ImageView imageView;
    Uri currentImage, childImage;
    String gigName;
    DbQueryActivity queries;
    String[] parentStack;

    @Override
    public void onBackPressed() {
        if (currentImage != null) {
            getContentResolver().insert(GigContentProvider.DATA_URI, com.example.Util.dataToContentValues(gigName, currentImage.toString(), parentStack[0], childImage.toString(), -1, -1, -1, -1));
        }

        childImage = Uri.parse("null");

        String[] newArr = Arrays.copyOfRange(parentStack, 1, parentStack.length);

        Gson gson = new Gson();
        String value = gson.toJson(newArr);
        SharedPreferences prefs = getApplicationContext().getSharedPreferences("setting", Context.MODE_PRIVATE);
        SharedPreferences.Editor e = prefs.edit();
        e.putString("list", value);
        e.commit();

        super.onBackPressed();    //To change body of overridden methods use File | Settings | File Templates.
    }

    public void onCreate(Bundle savedInstanceState) {
        Log.e(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create);

        Bundle extras = getIntent().getExtras();
        initVariables();
        gigName = extras.getString("name");

    }

    @Override
    protected void onResume() {
        SharedPreferences prefs = getApplicationContext().getSharedPreferences("setting", Context.MODE_PRIVATE);
        String value = prefs.getString("list", null);
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();
        parentStack = gson.fromJson(value, String[].class);

        Log.d(TAG, "On resume");
        super.onResume();
    }

    public void onUploadButtonClick(View view) {
        Log.d(TAG, "Upload Button Click");
        openGallery();

    }

    public void onNextButtonClicked(View view) {
        Log.d(TAG, "Next Button Clicked");

        float[] coordinates = getTransformedCordinates();

        int x1 = (int) coordinates[0];
        int y1 = (int) coordinates[1];
        int x2 = (int) coordinates[2];
        int y2 = (int) coordinates[3];

        getContentResolver().insert(GigContentProvider.DATA_URI, com.example.Util.dataToContentValues(gigName, currentImage.toString(), parentStack[0], childImage.toString(), x1, y1, x2, y2));

        Intent createActivity = new Intent(this, CreateActivity.class);
        createActivity.putExtra("name", gigName);

        String[] newArr = com.example.Util.concat(new String[]{currentImage.toString()}, parentStack);
        Gson gson = new Gson();
        String value = gson.toJson(newArr);
        SharedPreferences prefs = getApplicationContext().getSharedPreferences("setting", Context.MODE_PRIVATE);
        SharedPreferences.Editor e = prefs.edit();
        e.putString("list", value);
        e.commit();


        Bundle b = new Bundle();
        String[] arr = com.example.Util.concat(new String[]{currentImage.toString()}, parentStack);
        b.putStringArray("myList", arr);
        createActivity.putExtras(b);
        startActivity(createActivity);
    }

    private float[] getTransformedCordinates() {


        int x1 = ((DragRectView) imageView).getStartX();
        int x2 = ((DragRectView) imageView).getEndX();
        int y1 = ((DragRectView) imageView).getStartY();
        int y2 = ((DragRectView) imageView).getEndY();

        float[] startCord = com.example.Util.transformCoordinates(x1, y1, imageView);
        float[] endCord = com.example.Util.transformCoordinates(x2, y2, imageView);

        float[] coordinates = new float[]{startCord[0], startCord[1], endCord[0], endCord[1]};
        return coordinates;

    }

    public void onFinishButtonClicked(View view) {
        Log.d(TAG, "Finish Button Clicked");
        if (currentImage != null) {
            getContentResolver().insert(GigContentProvider.DATA_URI, com.example.Util.dataToContentValues(gigName, currentImage.toString(), parentStack[0], childImage.toString(), -1, -1, -1, -1));
        }

        Intent mainActivity = new Intent(this, MainActivity.class);
        startActivity(mainActivity);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) return;
        Uri userSelectedImage = data.getData();
        String userSelectedImagePath = getPath(userSelectedImage);
        InputStream is = null;
        try{
            is = getApplicationContext().getContentResolver().openInputStream(userSelectedImage);

        }catch (IOException e){
            Log.e("TAG" ,"error while opening file"+userSelectedImagePath);
        }
        Bitmap bitmap = com.example.Util.decodeSampledBitmapFromStream(is, imageView.getWidth(),imageView.getHeight());
        imageView.setImageBitmap(bitmap);

        currentImage = userSelectedImage;
        childImage = Uri.parse("null");
        queries.updateChildImage(currentImage.toString(), parentStack[0], gigName);


    }

    /// END OF METHODS //
    private void openGallery() {
        try {
            Intent imageUploadIntent = new Intent();
            imageUploadIntent.setType("image/*");
            imageUploadIntent.setAction(Intent.ACTION_GET_CONTENT);

            startActivityForResult(Intent.createChooser(imageUploadIntent, "Select Picture"), 123); // some random number for id
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            e.printStackTrace();
        }
    }

    private String getPath(Uri selectedImageUri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(selectedImageUri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }


    private void initVariables() {
        queries = new DbQueryActivity(getApplicationContext());
        currentImage = null;
        gigName = null;
        imageView = (ImageView) findViewById(R.id.imageDisplay);

    }


}