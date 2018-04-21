package com.djgeraldo.data;

/**
 * Created by sachin on 12-01-2018.
 */

public class SocialMediaModel {
    int id;
    String link;

    public SocialMediaModel(int id,String link)
    {
        this.id = id;
        this.link = link;
    }

    public int getId() {
        return id;
    }

    public String getLink() {
        return link;
    }
}
