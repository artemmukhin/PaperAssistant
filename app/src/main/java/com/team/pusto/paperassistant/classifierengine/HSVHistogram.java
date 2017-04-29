package com.team.pusto.paperassistant.classifierengine;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

public class HSVHistogram implements java.io.Serializable {
    final static double EPS = 1e-8;

    private float[] firstHist;
    private float[] secondHist;
    private float[] thirdHist;
    //private float[] fourthHist;

    boolean isChecked = false;
    Boolean isPaper = null;

    String fileName;

    public HSVHistogram(Mat image, String fileName) {
        this.fileName = fileName;

        //Imgproc.cvtColor(image, image, Imgproc.COLOR_RGB2HSV);
        List<Mat> planes = new ArrayList<Mat>();
        Core.split(image, planes);

        MatOfInt channels = new MatOfInt(0);
        Mat mask = new Mat();
        final MatOfInt histSize = new MatOfInt(8);
        final MatOfFloat histRange = new MatOfFloat(0f, 256f);

        Mat firstHist = new Mat();
        Mat secondHist = new Mat();
        Mat thirdHist = new Mat();
        //Mat fourthHist = new Mat();

        image.convertTo(image, CvType.CV_32F);

        List<Mat> firstPlaneAsList = new ArrayList<>();
        firstPlaneAsList.add(planes.get(0));

        List<Mat> secondPlaneAsList = new ArrayList<>();
        secondPlaneAsList.add(planes.get(1));

        List<Mat> thirdPlaneAsList = new ArrayList<>();
        thirdPlaneAsList.add(planes.get(2));

        //List<Mat> fourthPlaneAsList = new ArrayList<>();
        //fourthPlaneAsList.add(planes.get(3));

        Imgproc.calcHist(firstPlaneAsList, channels, mask, firstHist, histSize, histRange);
        Imgproc.calcHist(secondPlaneAsList, channels, mask, secondHist, histSize, histRange);
        Imgproc.calcHist(thirdPlaneAsList, channels, mask, thirdHist, histSize, histRange);
        //Imgproc.calcHist(fourthPlaneAsList, channels, mask, fourthHist, histSize, histRange);

        Core.normalize(firstHist, firstHist);
        Core.normalize(secondHist, secondHist);
        Core.normalize(thirdHist, thirdHist);
        //Core.normalize(fourthHist, fourthHist);

        float[] histFloat1 = new float[firstHist.cols() * firstHist.rows() * (int) firstHist.elemSize()];
        firstHist.get(0, 0, histFloat1);

        float[] histFloat2 = new float[secondHist.cols() * secondHist.rows() * (int) secondHist.elemSize()];
        secondHist.get(0, 0, histFloat2);

        float[] histFloat3 = new float[thirdHist.cols() * thirdHist.rows() * (int) thirdHist.elemSize()];
        thirdHist.get(0, 0, histFloat3);

        //float[] histFloat4 = new float[fourthHist.cols() * fourthHist.rows() * (int) fourthHist.elemSize()];
        //fourthHist.get(0, 0, histFloat4);

        this.firstHist = histFloat1;
        this.secondHist = histFloat2;
        this.thirdHist = histFloat3;
        //this.fourthHist = histFloat4;
    }

    // chi-squared distance
    public static double distanceBetweenHistograms(HSVHistogram h1, HSVHistogram h2) {
        double d1 = 0.0;
        for (int i = 0; i < h1.firstHist.length; i++) {
            double diff = (h1.firstHist[i] - h2.firstHist[i]) * (h1.firstHist[i] - h2.firstHist[i]) / (h1.firstHist[i] + h2.firstHist[i]);
            if (Math.abs(diff) > HSVHistogram.EPS)
                d1 += diff;
        }

        double d2 = 0.0;
        for (int i = 0; i < h1.secondHist.length; i++) {
            double diff = (h1.secondHist[i] - h2.secondHist[i]) * (h1.secondHist[i] - h2.secondHist[i]) / (h1.secondHist[i] + h2.secondHist[i]);
            if (Math.abs(diff) > HSVHistogram.EPS)
                d2 += diff;
        }

        double d3 = 0.0;
        for (int i = 0; i < h1.thirdHist.length; i++) {
            double diff = (h1.thirdHist[i] - h2.thirdHist[i]) * (h1.thirdHist[i] - h2.thirdHist[i]) / (h1.thirdHist[i] + h2.thirdHist[i]);
            if (Math.abs(diff) > HSVHistogram.EPS)
                d3 += diff;
        }

        /*
        double d4 = 0.0;
        for (int i = 0; i < h1.fourthHist.length; i++) {
            double diff = (h1.fourthHist[i] - h2.fourthHist[i]) * (h1.fourthHist[i] - h2.fourthHist[i]) / (h1.fourthHist[i] + h2.fourthHist[i]);
            if (Math.abs(diff) > HSVHistogram.EPS)
                d4 += diff;
        }
        */

        return d1 + d2 + d3;
    }

    public void setCheck(boolean isPaper) {
        this.isChecked = true;
        this.isPaper = isPaper;
    }
}
