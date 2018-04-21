package com.djgeraldo.data;

/**
 * Created by sachin on 11-01-2018.
 */

public class MusicGenreModel {
    int gId;
    String gName;
    String image;
    int total_records;
     public MusicGenreModel(int gId,String gName,String image)
    {
        this.gId = gId;
        this.gName = gName;
        this.image = image;
    }

    public MusicGenreModel(int gId, String gName, String image, int total_records) {
        this.gId = gId;
        this.gName = gName;
        this.image = image;
        this.total_records = total_records;
    }

    public void setgId(int gId) {
        this.gId = gId;
    }

    public void setgName(String gName) {
        this.gName = gName;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getTotal_records() {
        return total_records;
    }

    public void setTotal_records(int total_records) {
        this.total_records = total_records;
    }

    public String getImage() {
        return image;
    }

    public int getgId() {
        return gId;
    }

    public String getgName() {
        return gName;
    }
}
