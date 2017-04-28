package com.team.pusto.paperassistant.classifierengine;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
import org.opencv.imgproc.Imgproc;

import java.io.File;
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
    }
}
