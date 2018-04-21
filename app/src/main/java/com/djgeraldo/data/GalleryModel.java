package com.djgeraldo.data;

/**
 * Created by sachin on 16-01-2018.
 */

public class GalleryModel {
    int gId;
    String gImage;
    String gTitle;

    public GalleryModel(int gId, String gImage, String gTitle) {
        this.gId = gId;
        this.gImage = gImage;
        this.gTitle = gTitle;
    }

    public GalleryModel(int gId, String gImage) {
        this.gId = gId;
        this.gImage = gImage;
    }

    public int getgId() {
        return gId;
    }

    public void setgId(int gId) {
        this.gId = gId;
    }

    public String getgImage() {
        return gImage;
    }

    public void setgImage(String gImage) {
        this.gImage = gImage;
    }

    public String getgTitle() {
        return gTitle;
    }

    public void setgTitle(String gTitle) {
        this.gTitle = gTitle;
    }
}
