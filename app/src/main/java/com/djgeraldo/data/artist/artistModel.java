package com.djgeraldo.data.artist;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by sachin on 20-01-2018.
 */

public class artistModel {
    @SerializedName("artist_id")
    @Expose
    private Integer artistId;
    @SerializedName("artist_name")
    @Expose
    private String artistName;
    @SerializedName("image")
    @Expose
    private String image;
    @SerializedName("insta_url")
    @Expose
    private String instaUrl;
    @SerializedName("total_records")
    @Expose
    private Integer totalRecords;

    public Integer getArtistId() {
        return artistId;
    }

    public void setArtistId(Integer artistId) {
        this.artistId = artistId;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getInstaUrl() {
        return instaUrl;
    }

    public void setInstaUrl(String instaUrl) {
        this.instaUrl = instaUrl;
    }

    public Integer getTotalRecords() {
        return totalRecords;
    }

    public void setTotalRecords(Integer totalRecords) {
        this.totalRecords = totalRecords;
    }
}
