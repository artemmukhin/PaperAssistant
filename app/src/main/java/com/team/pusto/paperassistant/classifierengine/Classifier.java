package com.team.pusto.paperassistant.classifierengine;

import android.content.Context;
import android.content.res.AssetManager;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

public class Classifier {
    private List<Photo> photos;
    private HistogramsArray indexedPapers;
    private HistogramsArray indexedNotPapers;

    public void addPhotos(List<File> photoFiles) {
        photos = new ArrayList<>();
        indexedPapers = null;
        indexedNotPapers = null;
        for (File file: photoFiles) {
            photos.add(new Photo(file));
        }
    }

    public List<File> getPapers() {
        if (indexedPapers == null || indexedNotPapers == null)
            throw new RuntimeException("No index was loaded");

        List<File> papers = new ArrayList<>();
        for (Photo photo: photos) {
            Mat image = photo.getImageMat();
            String fileName = photo.getFile().getName();

            int width = image.width();
            int height = image.height();
            Rect roi = new Rect((int) (width / 8.0),
                                (int) (height / 8.0),
                                (int) ((6.0/8.0) * width),
                                (int) ((6.0/8.0) * height));

            Mat cropped = new Mat(image, roi);
            HSVHistogram histogram = new HSVHistogram(cropped, fileName);

            //    imageSmallCenter = image[height // 4: (3 * height) // 4, width // 4: (3 * width) // 4]

            roi = new Rect((int) (width / 4.0),
                            (int) (height / 4.0),
                            (int) ((2.0/4.0) * width),
                            (int) ((2.0/4.0) * height));

            Mat doubleCropped = new Mat(image, roi);
            Mat blurred = new Mat();
            Imgproc.cvtColor(doubleCropped, blurred, Imgproc.COLOR_RGB2GRAY);
            Imgproc.GaussianBlur(blurred, blurred, new Size(3, 3) , 0);
            Imgproc.threshold(blurred, blurred, 60, 255, Imgproc.THRESH_BINARY);
            blurred.convertTo(blurred, CvType.CV_8UC1);
            List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
            Imgproc.findContours(blurred, contours, new Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

            if (contours.size() < 6) {
                if (isHistogramPaper(histogram))
                    papers.add(photo.getFile());
            }
        }
        return papers;
    }


    public void loadIndexStore(Context myContext, boolean isPaper) throws IOException, ClassNotFoundException {
        AssetManager am = myContext.getAssets();
        InputStream is;

        if (isPaper)
            is = am.open("paper_store.uu");
        else
            is = am.open("notpaper_store.uu");

        ObjectInputStream oin = new ObjectInputStream(is);
        HistogramsArray histogramsArray = (HistogramsArray) oin.readObject();
        oin.close();

        if (isPaper)
            this.indexedPapers = histogramsArray;
        else
            this.indexedNotPapers = histogramsArray;
    }

    private boolean isHistogramPaper(HSVHistogram histogram) {
        boolean result = true;
        ArrayList<HSVHistogram> papers = indexedPapers.getHistograms();
        ArrayList<HSVHistogram> notPapers = indexedNotPapers.getHistograms();

        TreeMap<Double, HSVHistogram> distances = new TreeMap<Double, HSVHistogram>();

        for (HSVHistogram indexHist: papers) {
            double dist = HSVHistogram.distanceBetweenHistograms(histogram, indexHist);
            distances.put(dist, indexHist);
        }

        for (HSVHistogram indexHist: notPapers) {
            double dist = HSVHistogram.distanceBetweenHistograms(histogram, indexHist);
            distances.put(dist, indexHist);
        }

        SortedSet<Double> keys = new TreeSet<>(distances.keySet());
        int amount = 0;
        int isPaperCount = 0;
        int isNotPaperCount = 0;
        for (Double key : keys) {
            HSVHistogram hist = distances.get(key);
            if (hist.isChecked) {
                if (hist.isPaper)
                    isPaperCount++;
                else
                    isNotPaperCount++;
            }
            else {
                throw new RuntimeException("Indexed histograms are not checked");
            }
            amount++;
            if (amount == 10)
                break;
        }

        result = (isPaperCount > isNotPaperCount);
        return result;
    }
}
