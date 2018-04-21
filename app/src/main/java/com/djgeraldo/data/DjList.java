package com.djgeraldo.data;

/**
 * Created by sachin on 12-01-2018.
 */

public class DjList {
    int id;
    String image;
    String name;
    String instaUrl;

    public DjList(int id,String image,String name,String instaUrl)
    {
        this.id = id;
        this.image = image;
        this.name = name;
        this.instaUrl = instaUrl;
    }

    public String getInstaUrl() {
        return instaUrl;
    }

    public String getImage() {
        return image;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }
}
