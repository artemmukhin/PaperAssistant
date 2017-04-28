package com.team.pusto.paperassistant.classifierengine;

import android.os.Environment;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class Indexer {
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
            HSVHistogram histogram = new HSVHistogram(image);
            histograms.add(histogram);
        }

        HistogramsArray store = new HistogramsArray(histograms);
        try {
            store.serialize();
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

    public void serialize() throws IOException {
        FileOutputStream fos = new FileOutputStream(Environment.getExternalStorageDirectory() + "/DCIM/index_store");
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(this);
        oos.flush();
        oos.close();
        //byte[] data = SerializationUtils.serialize(yourObject);
    }
}