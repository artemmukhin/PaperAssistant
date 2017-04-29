package com.team.pusto.paperassistant;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Environment;
import android.widget.GridView;

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
    public Context contextDialog;
    public Context contextClassifier;
    public GridView gridView;

    private void setDialog(Context context) {
        this.mDialog = new ProgressDialog(context);
        this.mDialog.setMessage(DIALOG_MESSAGE);
        this.mDialog.setCancelable(false);
    }

    public MyAsyncTask(Context contextDialog, Context contextCl, ArrayList<File> files, ArrayList<ImageItem> ii, GridView gridView) {
        this.setDialog(contextDialog);
        this.contextDialog = contextDialog;
        this.contextClassifier = contextCl;
        this.paperFiles = files;
        this.imageItems = ii;
        this.gridView = gridView;
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
            boolean isEqualContexts = contextClassifier == contextDialog;
            File photosDir = new File(Environment.getExternalStorageDirectory(), "/DCIM/PaperAssistant");

            ArrayList<File> allFiles = new ArrayList<>(Arrays.asList(photosDir.listFiles()));
            ArrayList<File> files = new ArrayList<>();
            for (File file : allFiles) {
                String name = file.getName();
                if (name.toUpperCase().indexOf(".JPG") > 0)
                    files.add(file);
            }

            Classifier classifier = new Classifier();
            classifier.loadIndexStore(contextClassifier, true);
            classifier.loadIndexStore(contextClassifier, false);
            classifier.addPhotos(files);
            paperFiles = classifier.getPapers();
            //paperFiles = files;

            if (paperFiles == null) {
                Bitmap.Config conf = Bitmap.Config.ARGB_8888;
                Bitmap bmp = Bitmap.createBitmap(200, 200, conf);
                imageItems.add(new ImageItem(bmp, "Images are not found!"));
            } else {
                for (int i = 0; i < paperFiles.size(); i++) {
                    Bitmap bmp = decodeSampledBitmapFromFile(paperFiles.get(i).getAbsolutePath(), 200, 200);
                    imageItems.add(new ImageItem(bmp, paperFiles.get(i).getAbsolutePath()));
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
        ((GridViewAdapter)gridView.getAdapter()).notifyDataSetChanged();
        ((Gallery)contextDialog).paperFiles = this.paperFiles;
        // And then dismiss the dialog
        if (this.mDialog.isShowing()) {
            this.mDialog.dismiss();
        }
    }
}
