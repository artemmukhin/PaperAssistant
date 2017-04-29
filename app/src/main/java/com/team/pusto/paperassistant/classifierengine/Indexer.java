package com.team.pusto.paperassistant.classifierengine;

import android.os.Environment;

import org.opencv.core.Mat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class Indexer {

    final static String indexPaperStorePath = Environment.getExternalStorageDirectory() + "/DCIM/paper_store";
    final static String indexNotPaperStorePath = Environment.getExternalStorageDirectory() + "/DCIM/notpaper_store";

    private List<Photo> photos;
    private boolean isPaper;

    public Indexer(boolean isPaper) {
        this.isPaper = isPaper;
    }

    public void addPhotos(List<File> photoFiles) {
        photos = new ArrayList<>();
        for (File file: photoFiles) {
            photos.add(new Photo(file));
        }
    }

    public void index() {
        ArrayList<HSVHistogram> histograms = new ArrayList<>();

        for (Photo photo: photos) {
            Mat image = photo.getImageMat();
            String fileName = photo.getFile().getName();
            HSVHistogram histogram = new HSVHistogram(image, fileName);
            histogram.setCheck(isPaper);

            histograms.add(histogram);
        }

        HistogramsArray store = new HistogramsArray(histograms);
        try {
            store.serialize(isPaper);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class HistogramsArray implements java.io.Serializable {
    private ArrayList<HSVHistogram> histograms;

    public HistogramsArray(ArrayList<HSVHistogram> histograms) {
        this.histograms = histograms;
    }

    public ArrayList<HSVHistogram> getHistograms() {
        return this.histograms;
    }

    public void serialize(boolean isPaper) throws IOException {
        FileOutputStream fos;
        if (isPaper)
            fos = new FileOutputStream(Indexer.indexPaperStorePath);
        else
            fos = new FileOutputStream(Indexer.indexNotPaperStorePath);

        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(this);
        oos.flush();
        oos.close();
        //byte[] data = SerializationUtils.serialize(yourObject);
    }
}