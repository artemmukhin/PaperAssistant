package com.team.pusto.paperassistant;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
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

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import static com.team.pusto.paperassistant.MainActivity.decodeSampledBitmapFromFile;

public class Gallery extends AppCompatActivity {

    public GridView gridView;
    public GridViewAdapter gridAdapter;
    static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 324;
    public ArrayList<File> paperFiles;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        this.context = this;

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

    public ArrayList<ImageItem> imageItems= new ArrayList<>();

    @Override
    protected void onResume(){
        super.onResume();

    }

    private void getData() {
        MyAsyncTask<Void, Void, ArrayList<File>> updateTask = new MyAsyncTask<Void, Void, ArrayList<File>>(context, getApplicationContext(), paperFiles, imageItems, gridView);
        updateTask.execute();
    }
    private ImageItem getItem(int i) {
        Bitmap bmp = decodeSampledBitmapFromFile(paperFiles.get(i).getAbsolutePath(), 600, 600);
        ImageItem im = new ImageItem(bmp, paperFiles.get(i).getAbsolutePath());
        //Toast.makeText(this, getFileName(i), Toast.LENGTH_SHORT).show();
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
            subMenu.add("Apply");
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
            mode.setTitle("Applying...");

            for(ImageItem imItem: imageItems) {
                String sourcePath = imItem.getTitle();
                File source = new File(sourcePath);

                File descPath = new File(Environment.getExternalStorageDirectory(), "/DCIM/Camera/papers"
                 + sourcePath.substring(sourcePath.lastIndexOf('/')));
                try {
                    FileUtils.copyDirectory(source, descPath);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            //File photosDir = new File(Environment.getExternalStorageDirectory(), "/DCIM/Camera/papers");
            //for(File file: paperFiles){
                //file.renameTo(new File(photosDir.getAbsoluteFile() + file.getName()));
            //}
        }

        private void excludePhotos(ActionMode mode){
            mode.setTitle("Excluding...");
            SparseBooleanArray booleanArray = gridView.getCheckedItemPositions();
            int i = imageItems.size() - 1;
            while (i >= 0){
                if (booleanArray.get(i)){
                    imageItems.remove(i);
                    //paperFiles.remove(i);
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
