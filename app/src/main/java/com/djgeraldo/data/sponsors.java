package com.djgeraldo.data;

/**
 * Created by sachin on 28-12-2017.
 */

public class sponsors {

    int id;
    String name,description,address,logo,banner;

    public sponsors(String logo)
    {
        this.logo = logo;
    }

    public sponsors(int id,String name,String description,String address,String logo,String banner)
    {
        this.id = id;
        this.name = name;
        this.description = description;
        this.address = address;
        this.logo = logo;
        this.banner = banner;
    }

    public int getId() {
        return id;
    }

    public String getAddress() {
        return address;
    }

    public String getBanner() {
        return banner;
    }

    public String getDescription() {
        return description;
    }

    public String getLogo() {
        return logo;
    }

    public String getName() {
        return name;
    }
}
