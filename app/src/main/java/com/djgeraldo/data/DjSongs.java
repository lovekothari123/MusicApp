package com.djgeraldo.data;

/**
 * Created by sachin on 12-01-2018.
 */

public class DjSongs {
    private String s_name;
    private String s_duration;
    private String s_url;
    private String s_img;
    private String s_artist;
    private int s_id;
    private int nowPlaying;
    private int total_likes;
    private int like_status;

    public DjSongs(String s_name, String s_duration, String s_url, String s_img, String s_artist, int s_id,int like_status,int total_likes,int nowPlaying) {
        this.s_name = s_name;
        this.s_id=s_id;
        this.s_artist=s_artist;
        this.s_duration = s_duration;
        this.s_url = s_url;
        this.s_img = s_img;
        this.total_likes = total_likes;
        this.like_status = like_status;
        this.nowPlaying = nowPlaying;
    }

    public int getLike_status() {
        return like_status;
    }

    public int getTotal_likes() {
        return total_likes;
    }

    public void setTotal_likes(int total_likes) {
        this.total_likes = total_likes;
    }

    public void setLike_status(int like_status) {
        this.like_status = like_status;
    }

    public void setNowPlaying(int nowPlaying) {
        this.nowPlaying = nowPlaying;
    }

    public int getNowPlaying() {
        return nowPlaying;
    }

    public int getS_id() {
        return s_id;
    }

    public void setS_id(int s_id) {
        this.s_id = s_id;
    }

    public String getArtist() {
        return s_artist;
    }

    public void setArtist(String artist) {
        this.s_artist = artist;
    }

    public String getTitle() {
        return s_name;
    }

    public void setTitle(String title) {
        this.s_name = title;
    }

    public String getPath() {
        return s_url;
    }

    public void setPath(String path) {
        this.s_url = path;
    }

    public String getS_name() {
        return s_name;
    }

    public void setS_name(String s_name) {
        this.s_name = s_name;
    }

    public String getS_duration() {
        return s_duration;
    }

    public void setS_duration(String s_duration) {
        this.s_duration = s_duration;
    }

    public String getS_url() {
        return s_url;
    }

    public void setS_url(String s_url) {
        this.s_url = s_url;
    }

    public String getS_img() {
        return s_img;
    }

    public void setS_img(String s_img) {
        this.s_img = s_img;
    }
}
