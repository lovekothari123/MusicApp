package com.djgeraldo.data;

/**
 * Created by sachin on 16-01-2018.
 */

public class PlayListModel {

    int playlistid;
    String playlistname;
    int totalCount;

    public PlayListModel(int playlistid,String playlistname)
    {
        this.playlistid = playlistid ;
        this.playlistname = playlistname;
    }

    public PlayListModel(int playlistid, String playlistname, int totalCount) {
        this.playlistid = playlistid;
        this.playlistname = playlistname;
        this.totalCount = totalCount;
    }

    public void setPlaylistid(int playlistid) {
        this.playlistid = playlistid;
    }

    public void setPlaylistname(String playlistname) {
        this.playlistname = playlistname;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public int getPlaylistid() {
        return playlistid;
    }

    public String getPlaylistname() {
        return playlistname;
    }
}
