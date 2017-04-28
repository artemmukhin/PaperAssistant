package com.team.pusto.paperassistant;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.team.pusto.paperassistant.classifierengine.Classifier;

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import org.opencv.android.OpenCVLoader;

import static android.content.ContentValues.TAG;


public class MainActivity extends Activity {
    final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;

    static {
        if (OpenCVLoader.initDebug()) {
            Log.i(TAG, "OpenCV initialize success");
        } else {
            Log.i(TAG, "OpenCV initialize failed");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

// Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }

        ImageView mImageView;
        mImageView = (ImageView) findViewById(R.id.imageView1);
        mImageView.setImageResource(R.drawable.not32);
    }

    public static Bitmap decodeSampledBitmapFromFile(String path, int reqWidth, int reqHeight) {
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        // Calculate inSampleSize
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        int inSampleSize = 1;

        if (height > reqHeight) {
            inSampleSize = Math.round((float)height / (float)reqHeight);
        }

        int expectedWidth = width / inSampleSize;

        if (expectedWidth > reqWidth) {
            //if(Math.round((float)width / (float)reqWidth) > inSampleSize) // If bigger SampSize..
            inSampleSize = Math.round((float)width / (float)reqWidth);
        }

        options.inSampleSize = inSampleSize;

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeFile(path, options);
    }

    public void scanPhotos() {
        File photosDir = new File(Environment.getExternalStorageDirectory(), "/DCIM/Camera");
        ArrayList<File> files = new ArrayList<File>(Arrays.asList(photosDir.listFiles()));


        Classifier classifier = new Classifier();
        classifier.addPhotos(files);
        ArrayList<File> paperFiles = classifier.getPapers();


        Bitmap bmp = decodeSampledBitmapFromFile(files.get(0).getAbsolutePath(), 200, 200);
        Mat imageMat = new Mat();
        Utils.bitmapToMat(bmp, imageMat);

        /*
        InputStream stream = null;
        Uri path = Uri.parse("android.resource://com.team.pusto.paperassistant/" + R.drawable.not32);
        try {
            stream = getContentResolver().openInputStream(path);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        BitmapFactory.Options bmpFactoryOptions = new BitmapFactory.Options();
        bmpFactoryOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;

        Bitmap bmp = BitmapFactory.decodeStream(stream, null, bmpFactoryOptions);


        Mat imageMat = new Mat();
        Utils.bitmapToMat(bmp, imageMat);

        // calculate histogram
        Mat hist = new Mat();
        ArrayList<Mat> imageList = new ArrayList<Mat>();
        imageList.add(imageMat);
        Imgproc.calcHist(imageList, new MatOfInt(0, 1, 2), null, hist, new MatOfInt(8, 8, 8),
                new MatOfFloat(0, 256, 0, 256, 0, 256));
        */

        // do smth with mat
        Mat newMat = new Mat();
        Imgproc.cvtColor(imageMat, newMat, Imgproc.COLOR_RGB2GRAY);

        // convert to bitmap:
        Bitmap bm = Bitmap.createBitmap(newMat.cols(), newMat.rows(),Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(newMat, bm);

        // find the imageview and draw it!
        ImageView iv = (ImageView) findViewById(R.id.imageView1);
        iv.setImageBitmap(bm);
    }

    public void buttonOnClick(View view) {
        //scanPhotos();
        Intent intent = new Intent(this, Gallery.class);
        startActivity(intent);
        //overridePendingTransition( R.anim.slide_in_left, R.anim.slide_out_left );
    }

    public void imageView1OnClick(View view) {
    }
}