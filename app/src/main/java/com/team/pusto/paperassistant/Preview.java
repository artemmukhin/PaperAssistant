package com.team.pusto.paperassistant;
import android.graphics.Bitmap;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.graphics.Bitmap;
import android.widget.ImageView;

public class Preview extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
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
            Bitmap imagesrc = intent.getParcelableExtra("image");
            //int numberData = intent.getIntExtra(KEY, defaultValue);
            //boolean booleanData = intent.getBooleanExtra(KEY, defaultValue);
            //char charData = intent.getCharExtra(KEY, defaultValue);
            ImageView previewImage = (ImageView) findViewById(R.id.imageView2);
            previewImage.setImageBitmap(imagesrc);
        }
    }

}
