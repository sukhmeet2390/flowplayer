package com.example.activity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import com.example.R;
import com.example.database.ContentProvider.GigContentProvider;
import com.example.model.DragRectView;
import com.example.model.HotSpotRectangle;
import com.example.model.ViewScreen;

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
    Uri parentImage, currentImage, childImage;
    String gigName;
    DbQueryActivity queries;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create);

        Bundle extras = getIntent().getExtras();
        initVariables();
        gigName = extras.getString("name");
        String check = extras.getString("parentImage");

        parentImage = Uri.parse(check);


        imageView = (ImageView) findViewById(R.id.imageDisplay);

    }


    public void onUploadButtonClick(View view) {
        Log.d(TAG, "Upload Button Click");
        openGallery();

    }

    public void onNextButtonClicked(View view) {
        Log.d(TAG, "Next Button Clicked");

        float[] coordinates = getTransformedCordinates(R.id.imageDisplay);
        int x1 = (int) coordinates[0];
        int y1 = (int) coordinates[1];
        int x2 = (int) coordinates[2];
        int y2 = (int) coordinates[3];
        getContentResolver().insert(GigContentProvider.DATA_URI, com.example.Util.dataToContentValues(gigName, currentImage.toString(), parentImage.toString(), childImage.toString(), x1, y1, x2, y2));

        Intent createActivity = new Intent(this, CreateActivity.class);
        createActivity.putExtra("name", gigName);
        createActivity.putExtra("parentImage", currentImage.toString());
        startActivity(createActivity);
    }

    private float[] getTransformedCordinates(int viewId) {

        int x1 = ((DragRectView) findViewById(viewId)).getStartX();
        int x2 = ((DragRectView) findViewById(viewId)).getEndX();
        int y1 = ((DragRectView) findViewById(viewId)).getStartY();
        int y2 = ((DragRectView) findViewById(viewId)).getEndY();
        float[] startCord = transformCoordinates(x1, y1, viewId);
        float[] endCord = transformCoordinates(x2, y2, viewId);
        float[] coordinates = new float[]{startCord[0], startCord[1], endCord[0], endCord[1]};
        return coordinates;

    }

    public void onFinishButtonClicked(View view) {
        Log.d(TAG, "Finish Button Clicked");
        getContentResolver().insert(GigContentProvider.DATA_URI, com.example.Util.dataToContentValues(gigName, currentImage.toString(), parentImage.toString(), childImage.toString(), -1, -1, -1, -1));
        Intent mainActivity = new Intent(this, MainActivity.class);
        startActivity(mainActivity );


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) return;
        Uri userSelectedImage = data.getData();
        String userSelectedImagePath = getPath(userSelectedImage);
        Bitmap userSelectedBitMap = decodeSampledBitmap(userSelectedImagePath, imageView.getMeasuredWidth(), imageView.getMeasuredHeight());
        imageView.setImageBitmap(userSelectedBitMap);

        currentImage = userSelectedImage;
        childImage = Uri.parse("null");
        queries.updateChildImage(currentImage.toString(), parentImage.toString(), gigName);


    }


    private void updateChildrenForParent() {


        //currentScreen.currentImage = userSelectedImage;

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

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            // Calculate ratios of height and width to requested height and width
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            // Choose the smallest ratio as inSampleSize value, this will guarantee
            // a final image with both dimensions larger than or equal to the
            // requested height and width.
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }

        return inSampleSize;
    }


    private void initVariables() {
        queries = new DbQueryActivity(getApplicationContext());
        parentImage = null;
        currentImage = null;
        gigName = null;

    }

    public Bitmap decodeSampledBitmap(String path, int screenWidth, int screenHeight) {
        //screenWidth = dragWidth;
        //screenHeight = dragHeight;
        Log.d(TAG, " screen width " + screenWidth + " screen Height" + screenHeight);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        int inSampleSize = calculateInSampleSize(options, screenWidth, screenHeight);

        options.inJustDecodeBounds = false;
        options.inDither = false;
        options.inSampleSize = inSampleSize;
        options.inScaled = false;
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;

        return BitmapFactory.decodeFile(path, options);

    }

    public float[] transformCoordinates(float x, float y, int viewId) {
        float[] coordinates = new float[]{x, y};
        Matrix matrix = new Matrix();
        DragRectView imageView = ((DragRectView) findViewById(viewId));
        imageView.getImageMatrix().invert(matrix); // Inside a class that extends ImageView. Hence this->ImageView
        matrix.postTranslate(imageView.getScrollX(), imageView.getScrollY());
        matrix.mapPoints(coordinates);
        return coordinates;

    }
}