package com.djgeraldo.data;

/**
 * Created by sachin on 16-01-2018.
 */

public class commentsModel
{
    String userName;
    String comment;

    public commentsModel(String userName,String comment)
    {
        this.userName = userName;
        this.comment = comment;
    }

    public String getComment() {
        return comment;
    }

    public String getUserName() {
        return userName;
    }
}
