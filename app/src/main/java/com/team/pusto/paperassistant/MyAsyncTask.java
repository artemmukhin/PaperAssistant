package com.team.pusto.paperassistant;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Environment;

import com.team.pusto.paperassistant.classifierengine.Classifier;

import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

import static com.team.pusto.paperassistant.MainActivity.decodeSampledBitmapFromFile;

/**
 * Created by Snyss on 4/29/2017.
 */

public class MyAsyncTask<Params, Progress, Result> extends
        AsyncTask<Params, Progress, Result> {

    private final String DIALOG_MESSAGE = "Updating contacts";

    private ProgressDialog mDialog = null;
    public ArrayList<File> paperFiles;
    public ArrayList<ImageItem> imageItems;

    private void setDialog(Context context) {
        this.mDialog = new ProgressDialog(context);
        this.mDialog.setMessage(DIALOG_MESSAGE);
        this.mDialog.setCancelable(false);
    }

    public MyAsyncTask(Context context, ArrayList<File> files, ArrayList<ImageItem> ii) {
        this.setDialog(context);
        this.paperFiles = files;
        this.imageItems = ii;
    }

    @Override
    protected void onPreExecute() {
        this.mDialog.show();
    }

    @Override
    protected Result doInBackground(Params... arg0) {
        // Place your background executed method here
        try {
            //Thread.sleep(2000);
            File photosDir = new File(Environment.getExternalStorageDirectory(), "/DCIM/Camera");

            ArrayList<File> allFiles = new ArrayList<>(Arrays.asList(photosDir.listFiles()));
            ArrayList<File> files = new ArrayList<>();
            for (File file : allFiles) {
                String name = file.getName();
                if (name.indexOf(".jpg") > 0 || name.indexOf(".JPG") > 0)
                    files.add(file);
            }

            Classifier classifier = new Classifier();
            classifier.addPhotos(files);
            paperFiles = classifier.getPapers();

            if (paperFiles == null) {
                Bitmap.Config conf = Bitmap.Config.ARGB_8888;
                Bitmap bmp = Bitmap.createBitmap(200, 200, conf);
                imageItems.add(new ImageItem(bmp, "Images are not found!"));
            } else {
                for (int i = 0; i < paperFiles.size(); i++) {
                    Bitmap bmp = decodeSampledBitmapFromFile(files.get(i).getAbsolutePath(), 200, 200);
                    imageItems.add(new ImageItem(bmp, "Image#" + i));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(Result result) {
        // Update the UI if u need to

        // And then dismiss the dialog
        if (this.mDialog.isShowing()) {
            this.mDialog.dismiss();
        }
    }
}
