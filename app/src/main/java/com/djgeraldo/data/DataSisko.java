package com.djgeraldo.data;

/**
 * Created by sachin on 29-11-2017.
 */

public class DataSisko {
    int i,i1;
    String s,s1;
    int id;
    String Image,Title;

    public DataSisko(int i,String s)
    {
        this.i=i;
        this.s = s;
    }

    public DataSisko(int id,String image,String title)
    {
        this.id = id;
        this.Image = image;
        this.Title = title;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return Title;
    }

    public String getImage() {
        return Image;
    }

    public int getI() {
        return i;
    }

    public String getS() {
        return s;
    }

    public int getI1() {
        return i1;
    }

    public String getS1() {
        return s1;
    }

    public DataSisko(int i,int i1,String s,String s1)
    {
        this.i = i;
        this.i1 = i1;
        this.s = s;
        this.s1 = s1;
    }
}
