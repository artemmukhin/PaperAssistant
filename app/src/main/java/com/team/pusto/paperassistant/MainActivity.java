package com.team.pusto.paperassistant;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
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
import android.widget.Toast;

import com.team.pusto.paperassistant.classifierengine.Classifier;

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import org.opencv.android.OpenCVLoader;

import static android.content.ContentValues.TAG;


public class MainActivity extends Activity {
    final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;
    final int CHOOSE_FILE_CODE = 2;
    String pathToSelectedDir = null;

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

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {

            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
            }
        }
/*
        Uri selectedUri = Uri.parse(Environment.getExternalStorageDirectory() + "");
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(selectedUri, "resource/folder");

        if (intent.resolveActivityInfo(getPackageManager(), 0) != null)
        {
            startActivity(intent);
        }
        else
        {
            // if you reach this place, it means there is no any file
            // explorer app installed on your device
        }
*/


        Intent intent = new Intent()
                .setType("*/folder")
                .setAction(Intent.ACTION_GET_CONTENT);

        startActivityForResult(Intent.createChooser(intent, "Select a file"), CHOOSE_FILE_CODE);

        ImageView mImageView;
        mImageView = (ImageView) findViewById(R.id.imageView1);
        mImageView.setImageResource(R.drawable.not32);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CHOOSE_FILE_CODE && resultCode == RESULT_OK) {
            Uri selectedfile = data.getData(); //The uri with the location of the file
            String pathToDir = selectedfile.getPath();
            if (pathToDir.toUpperCase().contains(".JPG")) // it is file, not dir
                pathToDir = pathToDir.substring(0, pathToDir.lastIndexOf("/"));
            pathToSelectedDir = pathToDir;
        }
    }

    public static Bitmap decodeSampledBitmapFromFile(String path, int reqWidth, int reqHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        final int height = options.outHeight;
        final int width = options.outWidth;
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        int inSampleSize = 1;

        if (height > reqHeight) {
            inSampleSize = Math.round((float) height / (float) reqHeight);
        }

        int expectedWidth = width / inSampleSize;

        if (expectedWidth > reqWidth) {
            inSampleSize = Math.round((float) width / (float) reqWidth);
        }

        options.inSampleSize = inSampleSize;
        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeFile(path, options);
    }

    public void buttonOnClick(View view) {
        Intent intent = new Intent(this, Gallery.class);
        if (pathToSelectedDir != null)
            intent.putExtra("pathToSelectedDir", pathToSelectedDir);
        else
            intent.putExtra("pathToSelectedDir", Environment.getExternalStorageDirectory() + "/DCIM/PaperAssistant");
        startActivity(intent);
    }

    public void imageView1OnClick(View view) {
    }
}