package com.djgeraldo.data;

/**
 * Created by sachin on 12-01-2018.
 */

public class VideoData {

    String name,videoImage,banner_image,video_link,date,description;
    int id;

    public VideoData(String name,String video_link,String description)
    {
        this.name = name;
        this.video_link = video_link;
        this.description = description;
    }

    public VideoData(int id,String name,String date,String image,String video_link)
    {
        this.id = id;
        this.name = name;
        this.date = date;
        this.videoImage = image;
        this.video_link = video_link;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBanner_image() {
        return banner_image;
    }

    public String getVideoImage() {
        return videoImage;
    }

    public String getDate() {
        return date;
    }

    public String getVideo_link() {
        return video_link;
    }

    public void setBanner_image(String banner_image) {
        this.banner_image = banner_image;
    }

    public void setVideoImage(String videoImage) {
        this.videoImage = videoImage;
    }

    public void setVideo_link(String video_link) {
        this.video_link = video_link;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setId(int id) {
        this.id = id;
    }
}
