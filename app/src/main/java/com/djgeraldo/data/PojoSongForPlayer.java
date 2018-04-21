package com.djgeraldo.data;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;


public class PojoSongForPlayer {

    private String s_name;
    private String s_duration;
    private String s_url;
    private String s_img;
    private String s_artist;
    private int s_id;

    public float audioProgress = 0.0f;
    public int audioProgressSec = 0;

    public PojoSongForPlayer(String s_name, String s_duration, String s_url, String s_img, String s_artist, int s_id) {
        this.s_name = s_name;
        this.s_id=s_id;
        this.s_artist=s_artist;
        this.s_duration = s_duration;
        this.s_url = s_url;
        this.s_img = s_img;
    }

    public PojoSongForPlayer(String s_name, String s_duration, String s_url, String s_img) {
        this.s_name=s_name;
        this.s_duration=s_duration;
        this.s_url=s_url;
        this.s_img=s_img;
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

    public Bitmap getSmallCover(Context context) {

        // ImageLoader.getInstance().getDiskCache().g
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 1;
        Bitmap curThumb = null;
        try {
//            Uri uri = Uri.parse("content://media/external/audio/media/" + getPath() + "/albumart");
            Uri uri = Uri.parse(getS_img());
            ParcelFileDescriptor pfd = context.getContentResolver().openFileDescriptor(uri, "r");
            if (pfd != null) {
                FileDescriptor fd = pfd.getFileDescriptor();
                curThumb = BitmapFactory.decodeFileDescriptor(fd);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return curThumb;
    }

    public Bitmap getCover(Context context) {

        // ImageLoader.getInstance().getDiskCache().g
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 1;
        Bitmap curThumb = null;
        try {
//            Uri uri = Uri.parse("content://media/external/audio/media/" + getPath() + "/albumart");
            Uri uri = Uri.parse(getS_img());
            ParcelFileDescriptor pfd = context.getContentResolver().openFileDescriptor(uri, "r");
            if (pfd != null) {
                FileDescriptor fd = pfd.getFileDescriptor();
                curThumb = BitmapFactory.decodeFileDescriptor(fd);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return curThumb;
    }
}
