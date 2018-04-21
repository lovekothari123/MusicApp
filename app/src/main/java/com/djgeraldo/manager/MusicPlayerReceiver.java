/*
 * This is the source code of DMPLayer for Android v. 1.0.0.
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright @Dibakar_Mistry, 2015.
 */
package com.djgeraldo.manager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.KeyEvent;


public class MusicPlayerReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {


//		if (TelephonyManager.EXTRA_STATE_RINGING.equals(intent.getStringExtra(TelephonyManager.EXTRA_STATE))){
//			if (MediaController.getInstance().isPlayingAudio(MediaController.getInstance().getPlayingSongDetail())
//					&& !MediaController.getInstance().isAudioPaused()) {
//				MediaController.getInstance().pauseAudio(MediaController.getInstance().getPlayingSongDetail());
//			}
//
//		}
        if (intent.getAction().equals(Intent.ACTION_MEDIA_BUTTON)) {
            if (intent.getExtras() == null) {
                return;
            }
            KeyEvent keyEvent = (KeyEvent) intent.getExtras().get(Intent.EXTRA_KEY_EVENT);
            if (keyEvent == null) {
                return;
            }
            if (keyEvent.getAction() != KeyEvent.ACTION_DOWN)
                return;

            switch (keyEvent.getKeyCode()) {
                case KeyEvent.KEYCODE_HEADSETHOOK:
                case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
                    if (MediaController.getInstance().isAudioPaused())
                    {
                        MediaController.getInstance().playAudio(MediaController.getInstance().getPlayingSongDetail());
//                        MediaPlayer mediaPlayer = BufferData.getInstance().getRadioPlayer();
//                        if (mediaPlayer != null) {
//                            if (mediaPlayer.isPlaying()) {
//                                mediaPlayer.pause();
//                                mediaPlayer.release();
//                                mediaPlayer = null;
//                                BufferData.getInstance().setRadioPlayer(mediaPlayer);
//                                MainActivity.checkRadio();
////                                Prefs.getPrefInstance().setValue(context, Const.ONAIRNEW, "0");
//                            }
//                        }
                    } else {
                        MediaController.getInstance().pauseAudio(MediaController.getInstance().getPlayingSongDetail());
                    }
                    break;
                case KeyEvent.KEYCODE_MEDIA_PLAY:
//                    MediaPlayer mediaPlayer = BufferData.getInstance().getRadioPlayer();
//                    if (mediaPlayer != null) {
//                        if (mediaPlayer.isPlaying()) {
//                            mediaPlayer.pause();
//                            mediaPlayer.release();
//                            mediaPlayer = null;
//                            BufferData.getInstance().setRadioPlayer(mediaPlayer);
//                            MainActivity.checkRadio();
////                            HomeActivity.offRadio();
////                            Prefs.getPrefInstance().setValue(context, Const.ONAIRNEW, "0");
//                        }
//                    }
                    MediaController.getInstance().playAudio(MediaController.getInstance().getPlayingSongDetail());

                    break;
                case KeyEvent.KEYCODE_MEDIA_PAUSE:
                    MediaController.getInstance().pauseAudio(MediaController.getInstance().getPlayingSongDetail());
                    break;
                case KeyEvent.KEYCODE_MEDIA_STOP:
                    break;
                case KeyEvent.KEYCODE_MEDIA_NEXT:
//                    MediaPlayer mediaPlayer2 = BufferData.getInstance().getRadioPlayer();
//                    if (mediaPlayer2 != null) {
//                        if (mediaPlayer2.isPlaying()) {
//                            mediaPlayer2.pause();
//                            mediaPlayer2.release();
//                            mediaPlayer2 = null;
//                            BufferData.getInstance().setRadioPlayer(mediaPlayer2);
//                            MainActivity.checkRadio();
////                            HomeActivity.offRadio();
////                            Prefs.getPrefInstance().setValue(context, Const.ONAIRNEW, "0");
//                        }
//                    }
                    MediaController.getInstance().playNextSong();
                    break;
                case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
//                    MediaPlayer mediaPlayer3 = BufferData.getInstance().getRadioPlayer();
//                    if (mediaPlayer3 != null) {
//                        if (mediaPlayer3.isPlaying()) {
//                            mediaPlayer3.pause();
//                            mediaPlayer3.release();
//                            mediaPlayer3 = null;
//                            BufferData.getInstance().setRadioPlayer(mediaPlayer3);
//                            MainActivity.checkRadio();
////                            HomeActivity.offRadio();
////                            Prefs.getPrefInstance().setValue(context, Const.ONAIRNEW, "0");
//                        }
//                    }
                    MediaController.getInstance().playPreviousSong();
                    break;
            }
        } else {
            if (intent.getAction().equals(MusicPlayerService.NOTIFY_PLAY)) {
                MediaController.getInstance().playAudio(MediaController.getInstance().getPlayingSongDetail());
//                MediaPlayer mediaPlayer = BufferData.getInstance().getRadioPlayer();
//                if (mediaPlayer != null) {
//                    if (mediaPlayer.isPlaying()) {
//                        mediaPlayer.pause();
//                        mediaPlayer.release();
//                        mediaPlayer = null;
//                        BufferData.getInstance().setRadioPlayer(mediaPlayer);
//
////                            HomeActivity.offRadio();
////                            Prefs.getPrefInstance().setValue(context, Const.ONAIRNEW, "0");
//                    }
//                }
//                MainActivity.checkRadio();
            } else if (intent.getAction().equals(MusicPlayerService.NOTIFY_PAUSE)
                    || intent.getAction().equals(android.media.AudioManager.ACTION_AUDIO_BECOMING_NOISY)) {
                MediaController.getInstance().pauseAudio(MediaController.getInstance().getPlayingSongDetail());
            } else if (intent.getAction().equals(MusicPlayerService.NOTIFY_NEXT)) {
                MediaController.getInstance().playNextSong();
//                MediaPlayer mediaPlayer = BufferData.getInstance().getRadioPlayer();
//                if (mediaPlayer != null) {
//                    if (mediaPlayer.isPlaying()) {
//                        mediaPlayer.pause();
//                        mediaPlayer.release();
//                        mediaPlayer = null;
//                        BufferData.getInstance().setRadioPlayer(mediaPlayer);
//
////                            HomeActivity.offRadio();
////                            Prefs.getPrefInstance().setValue(context, Const.ONAIRNEW, "0");
//                    }
//                }
//                MainActivity.checkRadio();
            } else if (intent.getAction().equals(MusicPlayerService.NOTIFY_CLOSE)) {
//				MediaController.getInstance().cleanupPlayer(context,true, true);
                MediaController.getInstance().cleanupPlayer(context, true, true);
                MusicPreferance.playingSongDetail = null;
                MusicPreferance.saveLastSong(context, null);
                MusicPreferance.playlist.clear();
                MusicPreferance.shuffledPlaylist.clear();
            } else if (intent.getAction().equals(MusicPlayerService.NOTIFY_PREVIOUS)) {
                MediaController.getInstance().playPreviousSong();
//                MediaPlayer mediaPlayer = BufferData.getInstance().getRadioPlayer();
//                if (mediaPlayer != null) {
//                    if (mediaPlayer.isPlaying()) {
//                        mediaPlayer.pause();
//                        mediaPlayer.release();
//                        mediaPlayer = null;
//                        BufferData.getInstance().setRadioPlayer(mediaPlayer);
//
////                            HomeActivity.offRadio();
////                            Prefs.getPrefInstance().setValue(context, Const.ONAIRNEW, "0");
//                    }
//                }
//                MainActivity.checkRadio();
            }
        }

    }
}
