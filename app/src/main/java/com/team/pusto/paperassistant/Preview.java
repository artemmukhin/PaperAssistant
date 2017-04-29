package com.team.pusto.paperassistant;
import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.graphics.Bitmap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import static com.team.pusto.paperassistant.MainActivity.decodeSampledBitmapFromFile;

public class Preview extends AppCompatActivity {
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 324;
    private Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Toast.makeText(this, "onCreate1", Toast.LENGTH_LONG).show();
        setContentView(R.layout.activity_preview);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Toast.makeText(this, "onCreate2", Toast.LENGTH_LONG).show();

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
        /*
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        */
        Intent intent = getIntent();
        if (null != intent) {
            String pathToImage = intent.getStringExtra("title");
            Bitmap bmp = decodeSampledBitmapFromFile(pathToImage, 600, 600);
            //Bitmap imagesrc = intent.getParcelableExtra("image");
            //int numberData = intent.getIntExtra(KEY, defaultValue);
            //boolean booleanData = intent.getBooleanExtra(KEY, defaultValue);
            //char charData = intent.getCharExtra(KEY, defaultValue);
            ImageView previewImage = (ImageView) findViewById(R.id.imageView2);
            previewImage.setImageBitmap(bmp);
        }

        button = (Button) findViewById(R.id.button3);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = getIntent();
                if (null != intent) {
                    if (bmp == null) {
                        String pathToImage = intent.getStringExtra("title");
                        bmp = decodeSampledBitmapFromFile(pathToImage, 600, 600);
                    }

                    Matrix matrix = new Matrix();
                    matrix.postRotate(90);
                    Bitmap rotatedsrc = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);
                    bmp = rotatedsrc;
                    //int numberData = intent.getIntExtra(KEY, defaultValue);
                    //boolean booleanData = intent.getBooleanExtra(KEY, defaultValue);
                    //char charData = intent.getCharExtra(KEY, defaultValue);
                    ImageView previewImage = (ImageView) findViewById(R.id.imageView2);
                    previewImage.setImageBitmap(rotatedsrc);
                }
            }
        });
    }
    Bitmap bmp;

}
