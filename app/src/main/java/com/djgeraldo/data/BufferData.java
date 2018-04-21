package com.djgeraldo.data;

import android.app.ProgressDialog;
import android.media.MediaPlayer;
import android.widget.ProgressBar;
import android.widget.SeekBar;

/**
 * Created by Jaimin Patel/jaimin2305@gmail.com on 08-09-2016.
 */
public class BufferData {

    private static BufferData ourInstance = null;
    private SeekBar sb_progress;
    private ProgressBar sb_progress_single;
    private ProgressDialog universalProgressLoader;
    private MediaPlayer radioPlayer;
    public static BufferData getInstance() {
        if (ourInstance == null)
            ourInstance = new BufferData();
        return ourInstance;
    }

    private String androidId;
    private boolean isConnected;

    public void setAndroidId(String androidId)
    {
        this.androidId = androidId;
    }

    public String getAndroidId()
    {
        return androidId;
    }

    public void setConnected(boolean isConnected)
    {
        this.isConnected = isConnected;
    }

    public boolean getIsConnected()
    {
        return isConnected;
    }

    public void setRadioPlayer(MediaPlayer radioPlayer)
    {
        this.radioPlayer = radioPlayer;
    }

    public MediaPlayer getRadioPlayer()
    {
        return radioPlayer;
    }

    public ProgressDialog getUniversalProgressLoader() {
        return universalProgressLoader;
    }

    public void setUniversalProgressLoader(ProgressDialog universalProgressLoader) {
        this.universalProgressLoader = universalProgressLoader;
    }

    public ProgressBar getSb_progress_single()
    {
        return sb_progress_single;
    }
    public void setSb_progress_single(ProgressBar sb_progress_single)
    {
        this.sb_progress_single=sb_progress_single;
    }

    public SeekBar getSb_progress() {
        return sb_progress;
    }

    public void setSb_progress(SeekBar sb_progress) {
        this.sb_progress = sb_progress;
    }
}
