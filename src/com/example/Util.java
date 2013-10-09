package com.example;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;
import com.example.database.ContentProvider.DbHelper;
import com.example.model.HotSpotRectangle;
import com.example.model.ViewScreen;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: sukhmeet
 * Date: 01/10/13
 * Time: 4:04 PM
 * To change this template use File | Settings | File Templates.
 */
public class Util {
    private String TAG = "UTIL";

    public static ContentValues nameToContentValues(String name) {
        ContentValues values = new ContentValues();
        values.put(DbHelper.C_NAME, name);
        return values;
    }

    public static ContentValues dataToContentValues(String name, String imagePath, String parentImagePath, String childImagePath,
                                                    int x1, int y1, int x2, int y2) {
        ContentValues contentValues = new ContentValues();

        contentValues.put(DbHelper.C_NAME, name);
        contentValues.put(DbHelper.C_IMAGE_URI, imagePath);
        contentValues.put(DbHelper.C_PARENT_URI, parentImagePath);

        contentValues.put(DbHelper.C_X1, x1);
        contentValues.put(DbHelper.C_Y1, y1);
        contentValues.put(DbHelper.C_X2, x2);
        contentValues.put(DbHelper.C_Y2, y2);
        contentValues.put(DbHelper.C_CHILD_URI, childImagePath);

        return contentValues;
    }

    public static String[] concat(String[] A, String[] B) {
        int aLen = A.length;
        int bLen = B.length;
        String[] C = new String[aLen + bLen];
        System.arraycopy(A, 0, C, 0, aLen);
        System.arraycopy(B, 0, C, aLen, bLen);
        return C;
    }

    static public float[] transformCoordinates(float x, float y, ImageView imageView) {
        float[] coordinates = new float[]{x, y};
        Matrix matrix = new Matrix();
        imageView.getImageMatrix().invert(matrix);
        matrix.postTranslate(imageView.getScrollX(), imageView.getScrollY());
        matrix.mapPoints(coordinates);
        return coordinates;
    }

    public static Uri getHitOrMiss(float x, float y, ArrayList<HotSpotRectangle> hotSpotRectangles) {
        Uri uri = null;

        for (HotSpotRectangle hotRectangle : hotSpotRectangles) {
            if (x > Math.min(hotRectangle.getRectangle().getX1(), hotRectangle.getRectangle().getX2()) && x < Math.max(hotRectangle.getRectangle().getX1(), hotRectangle.getRectangle().getX2()) &&
                    y > Math.min(hotRectangle.getRectangle().getY1(), hotRectangle.getRectangle().getY2()) && y < Math.max(hotRectangle.getRectangle().getY1(), hotRectangle.getRectangle().getY2())) {
                return hotRectangle.getGotoImage();
            }
        }
        return uri;
    }

    public static ViewScreen valuesToScreen(long id, String name, Uri image, ArrayList<HotSpotRectangle> hotSpotRectangles) {
        ViewScreen screen = new ViewScreen();
        screen.setScreen_id(id);
        screen.setGig_name(name);
        screen.setCurrentImage(image);
        screen.setHotSpotRectangles(hotSpotRectangles);
        return screen;
    }

    public static Bitmap decodeSampledBitmap(Uri path, int screenWidth, int screenHeight) {

        Log.d("UTIL", " screen width " + screenWidth + " screen Height" + screenHeight);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path.toString(), options);

        int inSampleSize = calculateInSampleSize(options, screenWidth, screenHeight);

        options.inJustDecodeBounds = false;
        options.inDither = false;
        options.inSampleSize = inSampleSize;
        options.inScaled = false;
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;

        return BitmapFactory.decodeFile(path.toString(), options);

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

    public static Bitmap decodeSampledBitmapFromStream(InputStream imageStream, int reqWidth, int reqHeight) {
        byte[] byteArray = new byte[0];
        byte[] buffer = new byte[1024];
        int length, count = 0;
        try {
            while ((length = imageStream.read(buffer)) > -1) {
                if (length != 0) {
                    if (count + length > byteArray.length) {
                        byte[] newBuffer = new byte[(count + length) * 2];
                        System.arraycopy(byteArray, 0, newBuffer, 0, count);
                        byteArray = newBuffer;
                    }
                    System.arraycopy(buffer, 0, byteArray, count, length);
                    count += length;
                }
            }
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeByteArray(byteArray, 0, count, options);
            options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
            options.inJustDecodeBounds = false;
            return BitmapFactory.decodeByteArray(byteArray, 0, count, options);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Bitmap getSampledBitmap(Uri image, Context context, int viewWidth, int viewHeight) {
        InputStream is = null;
        try {
            is = context.getContentResolver().openInputStream(image);

        } catch (IOException e) {
            Log.e("TAG", "error while opening file" + image.toString());
        }
        Bitmap bitmap = decodeSampledBitmapFromStream(is, viewWidth, viewHeight);
        return bitmap;
    }

    static public boolean checkConnectivity(Context context) {
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            return connectivityManager.getActiveNetworkInfo().isConnectedOrConnecting();
        } catch (Exception e) {
            return false;
        }
    }
}
