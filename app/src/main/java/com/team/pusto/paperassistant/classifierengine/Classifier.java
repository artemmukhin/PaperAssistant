package com.team.pusto.paperassistant.classifierengine;

import java.io.File;
import java.util.ArrayList;

public class Classifier {
    private ArrayList<Photo> photos;

    public void addPhotos(ArrayList<File> photoFiles) {
        photos = new ArrayList<>();
        for (File file: photoFiles) {
            photos.add(new Photo(file));
        }
    }

    public ArrayList<File> getPapers() {
        ArrayList<File> papers = new ArrayList<>();
        for (Photo photo: photos)
            papers.add(photo.getFile());

        return papers;
    }
}
