package com.djgeraldo.data;

/**
 * Created by sachin on 20-12-2017.
 */

public class EventsData {

    int id;
    String eImage,eTitle,eDescription,eStart,eEnd,eAddress,eLongt,eLat,time;

    public EventsData(int id,String eImage,String eTitle,String eDescription,String eStart,String eEnd,String eAddress,String eLongt,String eLat,String time)
    {
        this.id = id;
        this.eImage = eImage;
        this.eTitle = eTitle;
        this.eDescription = eDescription;
        this.eStart = eStart;
        this.eEnd = eEnd;
        this.eAddress = eAddress;
        this.eLongt = eLongt;
        this.eLat = eLat;
        this.time = time;
    }

    public String getTime() {
        return time;
    }

    public int getId() {
        return id;
    }

    public String geteAddress() {
        return eAddress;
    }

    public String geteDescription() {
        return eDescription;
    }

    public String geteEnd() {
        return eEnd;
    }

    public String geteImage() {
        return eImage;
    }

    public String geteLat() {
        return eLat;
    }

    public String geteLongt() {
        return eLongt;
    }

    public String geteStart() {
        return eStart;
    }

    public String geteTitle() {
        return eTitle;
    }
}
