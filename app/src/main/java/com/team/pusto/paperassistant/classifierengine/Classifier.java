package com.team.pusto.paperassistant.classifierengine;

import org.opencv.core.Mat;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
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
            HSVHistogram histogram = new HSVHistogram(image);
            papers.add(photo.getFile());
        }
        return papers;
    }

    public void loadIndexStore(boolean isPaper) throws IOException, ClassNotFoundException {
        FileInputStream fis;
        if (isPaper)
            fis = new FileInputStream(Indexer.indexPaperStorePath);
        else
            fis = new FileInputStream(Indexer.indexNotPaperStorePath);

        ObjectInputStream oin = new ObjectInputStream(fis);
        HistogramsArray histogramsArray = (HistogramsArray) oin.readObject();
        //oin.close();
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

        result = (isPaperCount > 5);
        return result;
    }
}
