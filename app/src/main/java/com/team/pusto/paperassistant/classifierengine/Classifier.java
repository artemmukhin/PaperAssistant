package com.team.pusto.paperassistant.classifierengine;

import android.util.Base64;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static android.content.ContentValues.TAG;
import static org.opencv.imgproc.Imgproc.compareHist;

public class Classifier {
    private List<Photo> photos;

    public void addPhotos(List<File> photoFiles) {
        photos = new ArrayList<>();
        for (File file: photoFiles) {
            photos.add(new Photo(file));
        }
    }

    public List<File> getPapers() {
        List<File> papers = new ArrayList<>();
        for (Photo photo: photos) {

            Mat image = photo.getImageMat();

            /*
            Imgproc.cvtColor(image, image, Imgproc.COLOR_RGB2HSV);

            List<Mat> planes = new ArrayList<Mat>();
            Core.split(image, planes);

            MatOfInt channels = new MatOfInt();
            Mat mask = new Mat();
            final MatOfInt histSize = new MatOfInt(256);
            final MatOfFloat histRange = new MatOfFloat(0f, 256f);

            Mat firstHist = new Mat();
            Mat secondHist = new Mat();
            Mat thirdHist = new Mat();

            image.convertTo(image, CvType.CV_32F);

            List<Mat> firstPlaneAsList = new ArrayList<>();
            firstPlaneAsList.add(planes.get(0));

            List<Mat> secondPlaneAsList = new ArrayList<>();
            secondPlaneAsList.add(planes.get(1));

            List<Mat> thirdPlaneAsList = new ArrayList<>();
            thirdPlaneAsList.add(planes.get(2));

            Imgproc.calcHist(firstPlaneAsList, channels, mask, firstHist, histSize, histRange);
            Imgproc.calcHist(secondPlaneAsList, channels, mask, secondHist, histSize, histRange);
            Imgproc.calcHist(thirdPlaneAsList, channels, mask, thirdHist, histSize, histRange);

            Core.normalize(firstHist, firstHist);
            Core.normalize(secondHist, secondHist);
            Core.normalize(thirdHist, thirdHist);

            //String json1 = matToJson(firstHist);
            //String json2 = matToJson(secondHist);
            //String json3 = matToJson(thirdHist);

            //Mat matFromJ = matFromJson(json1);
            //boolean bln = firstHist.equals(matFromJ);

            // запилить Mat в массив float

            //if ( firstHist.type() == CvType.CV_32F || firstHist.type() == CvType.CV_32FC2) {
                float[] histFloat1 = new float[firstHist.cols() * firstHist.rows() * (int) firstHist.elemSize()];
                firstHist.get(0, 0, histFloat1);

                float[] histFloat2 = new float[secondHist.cols() * secondHist.rows() * (int) secondHist.elemSize()];
                firstHist.get(0, 0, histFloat2);

                float[] histFloat3 = new float[thirdHist.cols() * thirdHist.rows() * (int) thirdHist.elemSize()];
                firstHist.get(0, 0, histFloat3);

                HSVHistogram histogram = new HSVHistogram(histFloat1, histFloat2, histFloat3);
                double d = HSVHistogram.distanceBetweenHistograms(histogram, histogram);
                //double distance = compareHist(firstHist, firstHist, Imgproc.CV_COMP_CHISQR);
            //}
            */
            HSVHistogram histogram = new HSVHistogram(image);

            papers.add(photo.getFile());
        }
        return papers;
    }

    public static String matToJson(Mat mat){
        JsonObject obj = new JsonObject();

        if (mat.isContinuous()) {
            int cols = mat.cols();
            int rows = mat.rows();
            int elemSize = (int) mat.elemSize();
            int type = mat.type();

            mat.convertTo(mat, CvType.CV_8U);
            type = mat.type();

            byte[] data = new byte[cols * rows * elemSize];
            mat.get(0, 0, data);

            //String dataString;
            /*
            if( type == CvType.CV_32S || type == CvType.CV_32SC2 || type == CvType.CV_32SC3 || type == CvType.CV_16S) {
                int[] data = new int[cols * rows * elemSize];
                mat.get(0, 0, data);
                dataString = new String(Base64.encode(SerializationUtils.toByteArray(data)));
            }
            else if( type == CvType.CV_32F || type == CvType.CV_32FC2) {
                float[] data = new float[cols * rows * elemSize];
                mat.get(0, 0, data);
                dataString = new String(Base64.encodeBase64(SerializationUtils.toByteArray(data)));
            }
            else if( type == CvType.CV_64F || type == CvType.CV_64FC2) {
                double[] data = new double[cols * rows * elemSize];
                mat.get(0, 0, data);
                dataString = new String(Base64.encodeBase64(SerializationUtils.toByteArray(data)));
            }
            else if( type == CvType.CV_8U ) {
                byte[] data = new byte[cols * rows * elemSize];
                mat.get(0, 0, data);
                dataString = new String(Base64.encodeBase64(data));
            }
            else {

                throw new UnsupportedOperationException("unknown type");
            }
            */

            obj.addProperty("rows", mat.rows());
            obj.addProperty("cols", mat.cols());
            obj.addProperty("type", mat.type());

            // We cannot set binary data to a json object, so:
            // Encoding data byte array to Base64.
            String dataString = new String(Base64.encode(data, Base64.DEFAULT));

            obj.addProperty("data", dataString);

            Gson gson = new Gson();
            String json = gson.toJson(obj);
            return json;
        }
        else {
            Log.e(TAG, "Mat not continuous.");
        }
        return "{}";
    }

    public static Mat matFromJson(String json){
        JsonParser parser = new JsonParser();
        JsonObject JsonObject = parser.parse(json).getAsJsonObject();

        int rows = JsonObject.get("rows").getAsInt();
        int cols = JsonObject.get("cols").getAsInt();
        int type = JsonObject.get("type").getAsInt();

        String dataString = JsonObject.get("data").getAsString();
        byte[] data = Base64.decode(dataString.getBytes(), Base64.DEFAULT);

        Mat mat = new Mat(rows, cols, type);
        mat.put(0, 0, data);

        return mat;
    }

    /*
    public static String matToJson(Mat mat){
        JsonObject obj = new JsonObject();

        if(mat.isContinuous()){
            int cols = mat.cols();
            int rows = mat.rows();
            int elemSize = (int) mat.elemSize();
            int type = mat.type();

            obj.addProperty("rows", rows);
            obj.addProperty("cols", cols);
            obj.addProperty("type", type);

            // We cannot set binary data to a json object, so:
            // Encoding data byte array to Base64.
            String dataString;

            if( type == CvType.CV_32S || type == CvType.CV_32SC2 || type == CvType.CV_32SC3 || type == CvType.CV_16S) {
                int[] data = new int[cols * rows * elemSize];
                mat.get(0, 0, data);
                dataString = new String(Base64.encodeBase64(SerializationUtils.toByteArray(data)));
            }
            else if( type == CvType.CV_32F || type == CvType.CV_32FC2) {
                float[] data = new float[cols * rows * elemSize];
                mat.get(0, 0, data);
                dataString = new String(Base64.encodeBase64(SerializationUtils.toByteArray(data)));
            }
            else if( type == CvType.CV_64F || type == CvType.CV_64FC2) {
                double[] data = new double[cols * rows * elemSize];
                mat.get(0, 0, data);
                dataString = new String(Base64.encodeBase64(SerializationUtils.toByteArray(data)));
            }
            else if( type == CvType.CV_8U ) {
                byte[] data = new byte[cols * rows * elemSize];
                mat.get(0, 0, data);
                dataString = new String(Base64.encodeBase64(data));
            }
            else {

                throw new UnsupportedOperationException("unknown type");
            }
            obj.addProperty("data", dataString);

            Gson gson = new Gson();
            String json = gson.toJson(obj);

            return json;
        } else {
            System.out.println("Mat not continuous.");
        }
        return "{}";
    }

    public static Mat matFromJson(String json) {
        JsonParser parser = new JsonParser();
        JsonObject JsonObject = parser.parse(json).getAsJsonObject();

        int rows = JsonObject.get("rows").getAsInt();
        int cols = JsonObject.get("cols").getAsInt();
        int type = JsonObject.get("type").getAsInt();

        Mat mat = new Mat(rows, cols, type);

        String dataString = JsonObject.get("data").getAsString();
        if( type == CvType.CV_32S || type == CvType.CV_32SC2 || type == CvType.CV_32SC3 || type == CvType.CV_16S) {
            int[] data = SerializationUtils.toIntArray(Base64.decodeBase64(dataString.getBytes()));
            mat.put(0, 0, data);
        }
        else if( type == CvType.CV_32F || type == CvType.CV_32FC2) {
            float[] data = SerializationUtils.toFloatArray(Base64.decodeBase64(dataString.getBytes()));
            mat.put(0, 0, data);
        }
        else if( type == CvType.CV_64F || type == CvType.CV_64FC2) {
            double[] data = SerializationUtils.toDoubleArray(Base64.decodeBase64(dataString.getBytes()));
            mat.put(0, 0, data);
        }
        else if( type == CvType.CV_8U ) {
            byte[] data = Base64.decodeBase64(dataString.getBytes());
            mat.put(0, 0, data);
        }
        else {

            throw new UnsupportedOperationException("unknown type");
        }
        return mat;
    }
    */
}
