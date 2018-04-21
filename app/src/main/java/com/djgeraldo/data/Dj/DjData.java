package com.djgeraldo.data.Dj;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by sachin on 20-01-2018.
 */

public class DjData {

    @SerializedName("feature_dj_id")
    @Expose
    private Integer featureDjId;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("image")
    @Expose
    private String image;
    @SerializedName("insta_url")
    @Expose
    private String instaUrl;
    @SerializedName("total_records")
    @Expose
    private Integer totalRecords;

    public Integer getFeatureDjId() {
        return featureDjId;
    }

    public void setFeatureDjId(Integer featureDjId) {
        this.featureDjId = featureDjId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
