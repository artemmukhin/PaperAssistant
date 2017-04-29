package com.team.pusto.paperassistant;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.AbsListView.MultiChoiceModeListener;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.team.pusto.paperassistant.classifierengine.Classifier;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import static com.team.pusto.paperassistant.MainActivity.decodeSampledBitmapFromFile;

public class Gallery extends AppCompatActivity {

    public GridView gridView;
    public GridViewAdapter gridAdapter;
    static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 324;
    public ArrayList<File> paperFiles;

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
        gridView.setMultiChoiceModeListener(new MyMultiChoiceModeListener());

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                ImageItem item = getItem(position);
                //Create intent
                Intent intent = new Intent(getApplicationContext(), Preview.class);
                intent.putExtra("title", item.getTitle());

                //PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), (int)id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                //Start details activity
                try {
                    startActivity(intent);
                } catch (Exception e){
                    int i = 1234;
                }
            }
        });
        getData();
    }

    Thread thread;
    final ArrayList<ImageItem> imageItems = new ArrayList<>();

    @Override
    protected void onResume(){
        super.onResume();
        if (thread.isAlive()) {
            try {
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
                ArrayList<File> files = new ArrayList<>();
                for (File file : allFiles) {
                    String name = file.getName();
                    files.add(file);
                }

                Classifier classifier = new Classifier();
                classifier.addPhotos(files);
                paperFiles = classifier.getPapers();

                if (paperFiles.size() == 0) {
                    Bitmap.Config conf = Bitmap.Config.ARGB_8888; // see other conf types
                    Bitmap bmp = Bitmap.createBitmap(200, 200, conf); // this creates a MUTABLE bitmap;
                    imageItems.add(new ImageItem(bmp, "Images are not found!"));
                } else {
                    //paperFiles.size()
                    for (int i = 0; i < paperFiles.size(); i++) {
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
        Bitmap bmp = decodeSampledBitmapFromFile(paperFiles.get(i).getAbsolutePath(), 600, 600);
        ImageItem im = new ImageItem(bmp, paperFiles.get(i).getAbsolutePath());
        Toast.makeText(this, getFileName(i), Toast.LENGTH_SHORT).show();
        return im;
    }
    private String getFileName(int i) {
        File file = paperFiles.get(i);
        return file.getName();
    }



    private class MyMultiChoiceModeListener implements MultiChoiceModeListener {

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
            SubMenu subMenu = menu.addSubMenu("Menu");
            subMenu.add("Rotate");
            subMenu.add("Extract");
            subMenu.add("Exclude");
            return true;

        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            // TODO Auto-generated method stub

            mode.setTitle(item.getTitle());
            if (item.getTitle().equals("Rotate")){
                rotatePhotos(mode);
            }
            if (item.getTitle().equals("Extract")){
                extractPhotos(mode);
            }
            if (item.getTitle().equals("Exclude")){
                excludePhotos(mode);
            }

            return true;
        }

        private void rotatePhotos(ActionMode mode) {

            //todo: in rotatePhotos() change files and not bitmap
            SparseBooleanArray booleanArray = gridView.getCheckedItemPositions();

            Matrix matrix = new Matrix();
            matrix.postRotate(90);

            for (int i = 0; i < imageItems.size(); i++) {
                if (booleanArray.get(i)) {
                    ImageItem ii = imageItems.get(i);
                    Bitmap rotatedsrc = Bitmap.createBitmap(ii.getImage()
                            , 0, 0
                            , ii.getImage().getWidth()
                            , ii.getImage().getHeight()
                            , matrix, true);
                    imageItems.get(i).setImage(rotatedsrc);
                }
            }
            gridAdapter.notifyDataSetChanged();
        }

        private void extractPhotos(ActionMode mode){
            mode.setTitle("Extracting...");
        }

        private void excludePhotos(ActionMode mode){
            mode.setTitle("Excluding...");
            SparseBooleanArray booleanArray = gridView.getCheckedItemPositions();
            int i = imageItems.size() - 1;
            while (i >= 0){
                if (booleanArray.get(i)){
                    imageItems.remove(i);
                    paperFiles.remove(i);
                }
                i--;
            }
            gridView.clearChoices();
            gridAdapter.notifyDataSetChanged();
            mode.finish();
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
    }
}
