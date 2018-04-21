package com.djgeraldo.data.Genre;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by sachin on 20-01-2018.
 */

public class GenreData {

    @SerializedName("genres_id")
    @Expose
    private Integer genresId;
    @SerializedName("genres_name")
    @Expose
    private String genresName;
    @SerializedName("image")
    @Expose
    private String image;
    @SerializedName("total_records")
    @Expose
    private Integer totalRecords;

    public Integer getGenresId() {
        return genresId;
    }

    public void setGenresId(Integer genresId) {
        this.genresId = genresId;
    }

    public String getGenresName() {
        return genresName;
    }

    public void setGenresName(String genresName) {
        this.genresName = genresName;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Integer getTotalRecords() {
        return totalRecords;
    }

    public void setTotalRecords(Integer totalRecords) {
        this.totalRecords = totalRecords;
    }

}
