
package com.djgeraldo.manager;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.MediaPlayer;
import android.os.PowerManager;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.SeekBar;


import com.djgeraldo.activities.ApplicationDMPlayer;
import com.djgeraldo.data.BufferData;
import com.djgeraldo.data.PojoSongForPlayer;
import com.djgeraldo.phonemidea.Const;
import com.djgeraldo.phonemidea.DMPlayerUtility;
import com.djgeraldo.phonemidea.Prefs;
import com.djgeraldo.phonemidea.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class MediaController implements NotificationManager.NotificationCenterDelegate, SensorEventListener {

    private boolean isPaused = true;
    private MediaPlayer audioPlayer = null;
    private AudioTrack audioTrackPlayer = null;
    private int lastProgress = 0;
    private boolean useFrontSpeaker;

    private SensorManager sensorManager;
    private Sensor proximitySensor;
    private boolean ignoreProximity;
    private PowerManager.WakeLock proximityWakeLock;

    private final Object playerSync = new Object();
    private final Object playerSongDetailSync = new Object();
    private boolean playMusicAgain = false;

    private int lastTag = 0;
    public int currentPlaylistNum;
    private boolean shuffleMusic;

    private final Object progressTimerSync = new Object();
    private Timer progressTimer = null;

    private final Object sync = new Object();
    private int ignoreFirstProgress = 0;
    private long lastPlayPcm;
    private long currentTotalPcmDuration;

    public int type = 0;
    public int id = -1;
    public String path = "";
    private int repeatMode;

    private static volatile MediaController Instance = null;

    public static MediaController getInstance() {
        MediaController localInstance = Instance;
        if (localInstance == null) {
            synchronized (MediaController.class) {
                localInstance = Instance;
                if (localInstance == null) {
                    Instance = localInstance = new MediaController();
                }
            }
        }
        return localInstance;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void didReceivedNotification(int id, Object... args) {

    }

    @Override
    public void newSongLoaded(Object... args) {

    }

    public int generateObserverTag() {
        return lastTag++;
    }

    public PojoSongForPlayer getPlayingSongDetail() {
        return MusicPreferance.playingSongDetail;
    }

    public boolean isPlayingAudio(PojoSongForPlayer messageObject) {
        return !(audioTrackPlayer == null && audioPlayer == null || messageObject == null || MusicPreferance.playingSongDetail == null || MusicPreferance.playingSongDetail != null);
    }

    public boolean isAudioPaused() {
        return isPaused;
    }

    public void playNextSong() {
        playNextSong(false);
    }

    public void playPreviousSong() {
        ArrayList<PojoSongForPlayer> currentPlayList = shuffleMusic ? MusicPreferance.shuffledPlaylist : MusicPreferance.playlist;

//        String isShuffel = Prefs.getPrefInstance().getValue(ApplicationDMPlayer.applicationContext, Const.SHUFFEL, "");
//        if (isShuffel.equals("1")) {
//            Random rand = new Random();
//            int x = rand.nextInt(currentPlayList.size());
//            currentPlaylistNum = x;
//        } else {
            currentPlaylistNum--;
//        }
//        currentPlaylistNum--;
        if (currentPlaylistNum < 0) {
            currentPlaylistNum = currentPlayList.size() - 1;
        }
        if (currentPlaylistNum < 0 || currentPlaylistNum >= currentPlayList.size()) {
            return;
        }
        playMusicAgain = true;
        MusicPreferance.playingSongDetail.audioProgress = 0.0f;
        MusicPreferance.playingSongDetail.audioProgressSec = 0;
        playAudio(currentPlayList.get(currentPlaylistNum));
    }

    private void stopProgressTimer() {
        synchronized (progressTimerSync) {
            if (progressTimer != null) {
                try {
                    progressTimer.cancel();
                    progressTimer = null;
                } catch (Exception e) {
                    Log.e("tmessages", e.toString());
                }
            }
        }
    }

    private void stopProximitySensor() {
        if (ignoreProximity) {
            return;
        }
        try {
            useFrontSpeaker = false;
            NotificationManager.getInstance().postNotificationName(NotificationManager.audioRouteChanged, useFrontSpeaker);
            if (sensorManager != null && proximitySensor != null) {
                sensorManager.unregisterListener(this);
            }
            if (proximityWakeLock != null && proximityWakeLock.isHeld()) {
                proximityWakeLock.release();
            }
        } catch (Throwable e) {
            Log.e("tmessages", e.toString());
        }
    }

    public boolean playAudio(PojoSongForPlayer mSongDetail)
    {
        MediaPlayer audio = BufferData.getInstance().getRadioPlayer();
        Utils.getInstance().d("get audio player on play method");
        if(audio != null )
        {
            audio.stop();
            audio.release();
            audio = null;
            BufferData.getInstance().setRadioPlayer(audio);
        }
        Utils.getInstance().d("jp testing" + "playAudio start");

        if (mSongDetail == null) {
            return false;
        }
        if ((audioTrackPlayer != null || audioPlayer != null) && MusicPreferance.playingSongDetail != null && mSongDetail.getPath() == MusicPreferance.playingSongDetail.getPath()) {
            Utils.getInstance().d("jp1 testing" + "audioTrackPlayer start");
            if (isPaused) {
                Utils.getInstance().d("jp1 testing" + "isPaused start");
                resumeAudio(mSongDetail);
            }
            return true;
        }
        if (audioTrackPlayer != null) {
//            MusicPlayerService.setIgnoreAudioFocus();
        }
        cleanupPlayer(!playMusicAgain, false);
        playMusicAgain = false;
        File file = null;

        try {
            Utils.getInstance().d("jp testing" + "audioPlayer starting");
            audioPlayer = new MediaPlayer();
            audioPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            audioPlayer.setDataSource(mSongDetail.getPath());
//            audioPlayer.prepare();
            audioPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    MusicPreferance.playingSongDetail.audioProgress = 0.0f;
                    MusicPreferance.playingSongDetail.audioProgressSec = 0;
                    if (!MusicPreferance.playlist.isEmpty() && MusicPreferance.playlist.size() > 0) {
                        Utils.getInstance().d("jp testing" + "playNextSong");
                        playNextSong(true);
                    } else {
                        cleanupPlayer(true, true);
                    }
                }
            });


            audioPlayer.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
                @Override
                public void onBufferingUpdate(MediaPlayer mediaPlayer, int i) {
                    SeekBar progress = BufferData.getInstance().getSb_progress();
                    ProgressBar progress_single = BufferData.getInstance().getSb_progress_single();
                    if (progress != null) {
                        progress.setSecondaryProgress(i);
                        progress_single.setSecondaryProgress(i);
                    }
                }
            });


            audioPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    Utils.getInstance().d("Prepared");
                    Utils.getInstance().d("jp testing" + "onPrepared starting");

                    audioPlayer.start();
                    startProgressTimer();
//                    ProgressDialog progressDialog = BufferData.getInstance().getUniversalProgressLoader();
//                    if (progressDialog != null) {
//                        if (progressDialog.isShowing()) {
//                            progressDialog.dismiss();
//                        }
//                    }
                }
            });
            audioPlayer.prepareAsync();

            Utils.getInstance().d("jp testing" + "onPrepared out of");


        } catch (Exception e) {
            if (audioPlayer != null) {
                audioPlayer.release();
                audioPlayer = null;
                isPaused = false;
                MusicPreferance.playingSongDetail = null;
            }
            return false;
        }
        isPaused = false;
        lastProgress = 0;
        MusicPreferance.playingSongDetail = mSongDetail;
        NotificationManager.getInstance().postNotificationName(NotificationManager.audioDidStarted, mSongDetail);

        if (audioPlayer != null) {
            try {
                if (MusicPreferance.playingSongDetail.audioProgress != 0) {
                    int seekTo = (int) (audioPlayer.getDuration() * MusicPreferance.playingSongDetail.audioProgress);
                    audioPlayer.seekTo(seekTo);
                }
            } catch (Exception e2) {
                MusicPreferance.playingSongDetail.audioProgress = 0;
                MusicPreferance.playingSongDetail.audioProgressSec = 0;
            }
        } else if (audioTrackPlayer != null) {
            if (MusicPreferance.playingSongDetail.audioProgress == 1) {
                MusicPreferance.playingSongDetail.audioProgress = 0;
            }

        }

        if (MusicPreferance.playingSongDetail != null) {
            Intent intent = new Intent(ApplicationDMPlayer.applicationContext, MusicPlayerService.class);
            ApplicationDMPlayer.applicationContext.startService(intent);
        } else {
            Intent intent = new Intent(ApplicationDMPlayer.applicationContext, MusicPlayerService.class);
            ApplicationDMPlayer.applicationContext.stopService(intent);
        }

        NotificationManager.getInstance().notifyNewSongLoaded(NotificationManager.newaudioloaded, mSongDetail);

        return true;
    }

    private void playNextSong(boolean byStop) {
        ArrayList<PojoSongForPlayer> currentPlayList = shuffleMusic ? MusicPreferance.shuffledPlaylist : MusicPreferance.playlist;

        String isShuffel = Prefs.getPrefInstance().getValue(ApplicationDMPlayer.applicationContext, Const.SHUFFEL, "");
        String isRepeat = Prefs.getPrefInstance().getValue(ApplicationDMPlayer.applicationContext, Const.REPEAT, "");



        Utils.getInstance().d("repeat" + isRepeat);
        Utils.getInstance().d("shuffel" + isShuffel);

        if (isRepeat.equals("1") && byStop == true) {
            Utils.getInstance().d("In all Repeat." + byStop);
            cleanupPlayer(false, false);
            playAudio(currentPlayList.get(currentPlaylistNum));
            return;
        }
        if (isShuffel.equals("1")) {
            Utils.getInstance().d("In all Shufle.");
            Random rand = new Random();
            int x = rand.nextInt(currentPlayList.size());
            currentPlaylistNum = x;
        } else {
            currentPlaylistNum++;
        }


        if (byStop && repeatMode == 2) {
            cleanupPlayer(false, false);
            playAudio(currentPlayList.get(currentPlaylistNum));
            return;
        }

        if (currentPlaylistNum >= currentPlayList.size()) {
            currentPlaylistNum = 0;
            if (byStop && repeatMode == 0) {
                stopProximitySensor();
                if (audioPlayer != null || audioTrackPlayer != null) {
                    if (audioPlayer != null) {
                        try {
                            audioPlayer.stop();
                        } catch (Exception e) {
                        }
                        try {
                            audioPlayer.release();
                            audioPlayer = null;
                        } catch (Exception e) {
                        }
                    } else if (audioTrackPlayer != null) {
                        synchronized (playerSongDetailSync) {
                            try {
                                audioTrackPlayer.pause();
                                audioTrackPlayer.flush();
                            } catch (Exception e) {
                            }
                            try {
                                audioTrackPlayer.release();
                                audioTrackPlayer = null;
                            } catch (Exception e) {
                            }
                        }
                    }
                    stopProgressTimer();
                    lastProgress = 0;
                    isPaused = true;
                    MusicPreferance.playingSongDetail.audioProgress = 0.0f;
                    MusicPreferance.playingSongDetail.audioProgressSec = 0;
                    NotificationManager.getInstance().postNotificationName(NotificationManager.audioPlayStateChanged, MusicPreferance.playingSongDetail.getPath());
                }
                return;
            }
        }
        if (currentPlaylistNum < 0 || currentPlaylistNum >= currentPlayList.size()) {
            return;
        }
//        isPaused = true;
        lastProgress = 0;
        playMusicAgain = true;
        MusicPreferance.playingSongDetail.audioProgress = 0.0f;
        MusicPreferance.playingSongDetail.audioProgressSec = 0;
        playAudio(currentPlayList.get(currentPlaylistNum));
    }

    public boolean pauseAudio(PojoSongForPlayer messageObject) {
        stopProximitySensor();
        if (audioTrackPlayer == null && audioPlayer == null || messageObject == null || MusicPreferance.playingSongDetail == null || MusicPreferance.playingSongDetail != null
                && MusicPreferance.playingSongDetail.getPath() != messageObject.getPath()) {
            return false;
        }
        stopProgressTimer();
        try {
            if (audioPlayer != null) {
                audioPlayer.pause();
            } else if (audioTrackPlayer != null) {
                audioTrackPlayer.pause();
            }
            isPaused = true;
            NotificationManager.getInstance().postNotificationName(NotificationManager.audioPlayStateChanged, MusicPreferance.playingSongDetail.getPath());
        } catch (Exception e) {
            Log.e("tmessages", e.toString());
            isPaused = true;
            return false;
        }
        return true;
    }

    public boolean resumeAudio(PojoSongForPlayer messageObject) {
        Utils.getInstance().d("jp testing" + "resumeAudio start");
        if (audioTrackPlayer == null && audioPlayer == null || messageObject == null || MusicPreferance.playingSongDetail == null || MusicPreferance.playingSongDetail != null
                && MusicPreferance.playingSongDetail.getPath() != messageObject.getPath()) {
            return false;
        }
        try {
            startProgressTimer();
            if (audioPlayer != null) {
                audioPlayer.start();
            } else if (audioTrackPlayer != null) {
                audioTrackPlayer.play();
            }
            isPaused = false;
            NotificationManager.getInstance().postNotificationName(NotificationManager.audioPlayStateChanged, MusicPreferance.playingSongDetail.getPath());
        } catch (Exception e) {
            return false;
        }
        Utils.getInstance().d("jp testing" + "resumeAudio end");
        return true;
    }

   /* public void stopAudio() {
        stopProximitySensor();
        if (audioTrackPlayer == null && audioPlayer == null || MusicPreferance.playingSongDetail == null) {
            return;
        }
        try {
            if (audioPlayer != null) {
                audioPlayer.stop();
            } else if (audioTrackPlayer != null) {
                audioTrackPlayer.pause();
                audioTrackPlayer.flush();
            }
        } catch (Exception e) {
        }
        try {
            if (audioPlayer != null) {
                audioPlayer.release();
                audioPlayer = null;
            } else if (audioTrackPlayer != null) {
                synchronized (playerSongDetailSync) {
                    audioTrackPlayer.release();
                    audioTrackPlayer = null;
                }
            }
        } catch (Exception e) {
        }
        stopProgressTimer();
        isPaused = false;

        Intent intent = new Intent(ApplicationDMPlayer.applicationContext, MusicPlayerService.class);
        ApplicationDMPlayer.applicationContext.stopService(intent);
    }*/

    private void startProgressTimer() {
        Utils.getInstance().d("jp testing" + "startProgressTimer starting");
        synchronized (progressTimerSync) {
            if (progressTimer != null) {
                try {
                    progressTimer.cancel();
                    progressTimer = null;
                } catch (Exception e) {
                    // FileLog.e("tmessages", e);
                }
            }
            progressTimer = new Timer();
            progressTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    synchronized (sync) {
                        DMPlayerUtility.runOnUIThread(new Runnable() {
                            @Override
                            public void run() {
//                                Utils.getInstance().d("jp testing"+"run starting");
                                if (MusicPreferance.playingSongDetail != null && (audioPlayer != null || audioTrackPlayer != null) && !isPaused) {
                                    try {
                                        if (ignoreFirstProgress != 0) {
                                            ignoreFirstProgress--;
                                            return;
                                        }
                                        int progress;
                                        float value;
                                        if (audioPlayer != null) {
                                            progress = audioPlayer.getCurrentPosition();
                                            value = (float) lastProgress / (float) audioPlayer.getDuration();
                                            if (progress <= lastProgress) {
                                                return;
                                            }
                                        } else {
                                            progress = (int) (lastPlayPcm / 48.0f);
                                            value = (float) lastPlayPcm / (float) currentTotalPcmDuration;
                                            if (progress == lastProgress) {
                                                return;
                                            }
                                        }
                                        lastProgress = progress;
                                        MusicPreferance.playingSongDetail.audioProgress = value;
                                        MusicPreferance.playingSongDetail.audioProgressSec = lastProgress / 1000;
                                        NotificationManager.getInstance().postNotificationName(NotificationManager.audioProgressDidChanged,
                                                MusicPreferance.playingSongDetail.getPath(), value);
                                    } catch (Exception e) {
                                    }
                                }
                            }
                        });
                    }
                }
            }, 0, 500);
        }
        Utils.getInstance().d("jp testing" + "startProgressTimer end");
//        HomeActivity.visiblePlayerTwo();
    }

    public boolean setPlaylist(ArrayList<PojoSongForPlayer> allSongsList, PojoSongForPlayer current, int type_, int id_) {
        Utils.getInstance().d("jp testing" + "setPlaylist start");
        type = type_;
        id = id_;

//        if (audioPlayer != null) {
//            audioPlayer.reset();
//            audioPlayer.release();
//        }

//        cleanupPlayer(true, true);
        if (MusicPreferance.playingSongDetail == current) {
            return playAudio(current);
        }
        playMusicAgain = !MusicPreferance.playlist.isEmpty();
        MusicPreferance.playlist.clear();
        if (allSongsList != null && allSongsList.size() >= 1) {
            MusicPreferance.playlist.addAll(allSongsList);
        }

        currentPlaylistNum = MusicPreferance.playlist.indexOf(current);
        if (currentPlaylistNum == -1)
        {
            MusicPreferance.playlist.clear();
            MusicPreferance.shuffledPlaylist.clear();
            return false;
        }
        if (shuffleMusic) {
            currentPlaylistNum = 0;
        }
        Utils.getInstance().d("jp testing" + "setPlaylist end");
        return playAudio(current);
    }

    public boolean seekToProgress(PojoSongForPlayer mSongDetail, float progress) {
        if (audioTrackPlayer == null && audioPlayer == null) {
            return false;
        }
        try {
            if (audioPlayer != null) {
                int seekTo = (int) (audioPlayer.getDuration() * progress);
//                int seekTo = (int) (Float.parseFloat(mSongDetail.duration) * progress);
                audioPlayer.seekTo(seekTo);
                lastProgress = seekTo;
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public void cleanupPlayer(Context context, boolean notify, boolean stopService) {
        MusicPreferance.saveLastSong(context, getPlayingSongDetail());
        MusicPreferance.saveLastSongListType(context, type);
        MusicPreferance.saveLastAlbID(context, id);
        MusicPreferance.saveLastPosition(context, currentPlaylistNum);
        MusicPreferance.saveLastPath(context, path);
        cleanupPlayer(notify, stopService);
    }

    public void cleanupPlayer(boolean notify, boolean stopService) {
        pauseAudio(getPlayingSongDetail());
        stopProximitySensor();
        if (audioPlayer != null) {
            try {
                audioPlayer.reset();
            } catch (Exception e) {
            }
            try {
                audioPlayer.stop();
            } catch (Exception e) {
            }
            try {
                audioPlayer.release();
                audioPlayer = null;
            } catch (Exception e) {
            }
        } else if (audioTrackPlayer != null) {
            synchronized (playerSongDetailSync) {
                try {
                    audioTrackPlayer.pause();
                    audioTrackPlayer.flush();
                } catch (Exception e) {
                }
                try {
                    audioTrackPlayer.release();
                    audioTrackPlayer = null;
                } catch (Exception e) {
                }
            }
        }
        stopProgressTimer();
        isPaused = true;
        if (stopService) {
            Intent intent = new Intent(ApplicationDMPlayer.applicationContext, MusicPlayerService.class);
            ApplicationDMPlayer.applicationContext.stopService(intent);
        }
    }


}
