package com.team.pusto.paperassistant.classifierengine;

import org.opencv.core.Mat;

import java.io.File;

class Photo {
    private File file;
    private Mat imageMat;
    private boolean isPaper;

    Photo(File file) {
        this.file = file;
        this.imageMat = new Mat();
        this.isPaper = false;
    }

    public File getFile() {
        return this.file;
    }
}
