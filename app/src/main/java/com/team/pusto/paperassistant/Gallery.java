package com.team.pusto.paperassistant;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Bundle;
import android.os.Debug;
import android.os.Environment;
import android.os.Vibrator;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.team.pusto.paperassistant.classifierengine.Classifier;
//import com.team.pusto.paperassistant.classifierengine.ListAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import static com.team.pusto.paperassistant.MainActivity.decodeSampledBitmapFromFile;

public class Gallery extends AppCompatActivity {

    public GridView gridView;
    public GridViewAdapter gridAdapter;
    static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 324;
    public  ArrayList<File> files;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        //Debug.waitForDebugger();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
            }
        }

        gridView = (GridView) findViewById(R.id.gridView);
        //imageItems = new ArrayList<>();
        gridAdapter = new GridViewAdapter(this, R.layout.grid_item_layout, imageItems);

        gridView.setAdapter(gridAdapter);
        gridView.setChoiceMode(GridView.CHOICE_MODE_MULTIPLE_MODAL);
        gridView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                // TODO Auto-generated method stub
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                // TODO Auto-generated method stub

            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                // TODO Auto-generated method stub

                mode.setTitle("One item selected");
                return true;

            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                // TODO Auto-generated method stub


                int selectCount = gridView.getCheckedItemCount();
                switch (selectCount) {
                    case 1:
                        mode.setTitle("One item selected");

                        break;
                    default:
                        mode.setTitle("" + selectCount + " items selected");

                        break;
                }

                return true;
            }

            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position,
                                                  long id, boolean checked) {
                // TODO Auto-generated method stub



                int selectCount = gridView.getCheckedItemCount();
                switch (selectCount) {
                    case 1:
                        mode.setTitle("One item selected");
                        break;
                    default:
                        mode.setTitle("" + selectCount + " items selected");
                        break;
                }

            }
        });

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                ImageItem item = getItem(position);
                //Create intent
                Intent intent = new Intent(getApplicationContext(), Preview.class);
                intent.putExtra("title", item.getTitle());
                intent.putExtra("image", item.getImage());

                PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), (int)id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                //Start details activity
                startActivity(intent);
            }
        });
        getData();
        //ImageView imageView = (ImageView) findViewById(R.id.ImageView);
/*
        gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(Gallery.this, "Yay", Toast.LENGTH_SHORT).show();
                return true;
            }
        });
        */
    }

    Thread thread;
    final ArrayList<ImageItem> imageItems = new ArrayList<>();

    @Override
    protected void onResume(){
        super.onResume();
        if (thread.isAlive()) {
            try {
                //Toast.makeText(this, "Files are scanning...", Toast.LENGTH_LONG).show();
                thread.join();
            } catch (InterruptedException e) {
            }
        }

        gridAdapter.notifyDataSetChanged();
        gridView.invalidateViews();
    }

    private void getData() {

//        Thread thread = new Thread() {
//            @Override
//            public void run() {
//
//            }
//        };

        thread = new Thread(new Runnable() {
            public void run() {
                //Debug.waitForDebugger();
                File photosDir = new File(Environment.getExternalStorageDirectory(), "/DCIM/Camera");

                ArrayList<File> allFiles = new ArrayList<>(Arrays.asList(photosDir.listFiles()));
                files = new ArrayList<>();
                for (File file : allFiles) {
                    String name = file.getName();

                    files.add(file);
                }

                Classifier classifier = new Classifier();
                classifier.addPhotos(files);
                ArrayList<File> paperFiles = classifier.getPapers();

                if (paperFiles.size() == 0) {
                    Bitmap.Config conf = Bitmap.Config.ARGB_8888; // see other conf types
                    Bitmap bmp = Bitmap.createBitmap(200, 200, conf); // this creates a MUTABLE bitmap;
                    imageItems.add(new ImageItem(bmp, "Images are not found!"));
                } else {
                    //paperFiles.size()
                    for (int i = 0; i < 20; i++) {
                        Bitmap bmp = decodeSampledBitmapFromFile(files.get(i).getAbsolutePath(), 200, 200);
                        if (files.get(i).getName().indexOf(".jpg") > 0 || files.get(i).getName().indexOf(".JPG") > 0)
                            imageItems.add(new ImageItem(bmp, "Image#" + i));
                        //gridAdapter.notifyDataSetChanged();
//                        gridView.post(new Runnable() {
//                            public void run() {
//                                Debug.waitForDebugger();
//                                gridView.setAdapter(gridAdapter);
//                            }
//                        });
                    }
                }
            }
        });

        thread.start();
    }
    private ImageItem getItem(int i) {
        Bitmap bmp = decodeSampledBitmapFromFile(files.get(i).getAbsolutePath(), 600, 600);
        ImageItem im = new ImageItem(bmp, "Image#" + i);
        Toast.makeText(this, getFileName(i), Toast.LENGTH_SHORT).show();
        return im;
    }
    private String getFileName(int i) {
        File file = files.get(i);
        return file.getName();
    }

}
