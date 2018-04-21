package com.djgeraldo.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.djgeraldo.R;
import com.djgeraldo.adapter.MySliderAdapter;
import com.djgeraldo.custom.ClickableViewPager;
import com.djgeraldo.custom.OnSwipeTouchListener;
import com.djgeraldo.data.BufferData;
import com.djgeraldo.data.ImageSliderModel;
import com.djgeraldo.data.PojoSongForPlayer;
import com.djgeraldo.data.VideoData;
import com.djgeraldo.fragments.ArtistFragment;
import com.djgeraldo.fragments.ArtistSongsFragment;
import com.djgeraldo.fragments.DjSongsFragment;
import com.djgeraldo.fragments.EnglishSingles;
import com.djgeraldo.fragments.FeaturedDjFragment;
import com.djgeraldo.fragments.LatinSingles;
import com.djgeraldo.fragments.MenuFragment;
import com.djgeraldo.fragments.MusicGenres;
import com.djgeraldo.fragments.MusicGenresSongs;
import com.djgeraldo.fragments.MyFav;
import com.djgeraldo.fragments.MyPlaylist;
import com.djgeraldo.fragments.PlaylistSongs;
import com.djgeraldo.fragments.SliderVideo;
import com.djgeraldo.manager.MediaController;
import com.djgeraldo.manager.MusicPreferance;
import com.djgeraldo.manager.NotificationManager;
import com.djgeraldo.phonemidea.CheckNetwork;
import com.djgeraldo.phonemidea.Const;
import com.djgeraldo.phonemidea.PhoneMediaControl;
import com.djgeraldo.phonemidea.Prefs;
import com.djgeraldo.phonemidea.Utils;
import com.github.florent37.viewanimator.AnimationListener;
import com.github.florent37.viewanimator.ViewAnimator;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.messaging.FirebaseMessaging;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,SeekBar.OnSeekBarChangeListener,NotificationManager.NotificationCenterDelegate,DatePickerDialog.OnDateSetListener,TimePickerDialog.OnTimeSetListener{

   static Context context;
   SharedPreferences preferences;
    private final Object progressTimerSync = new Object();
    private final Object sync = new Object();
    private Timer progressTimer = null;
   ProgressDialog mProgressDialog,mDialog;
    ArrayList<PojoSongForPlayer> allSongs = new ArrayList<>();
    ArrayList<PojoSongForPlayer> songList = new ArrayList<>();
    ArrayList<VideoData> dataSlider = new ArrayList<>();
    Fragment frag;
    FragmentTransaction ft;
    FragmentManager fm;
    static Fragment menuFragment;
    static FragmentTransaction menuFt;
    static  FragmentManager menuFm;
   static RelativeLayout rl_playerLayout,rl_fragmentLayout,rl_fragment_header;
    ImageView iv_menulist,iv_back,iv_player,bottom_playlist,bottom_next,bottom_prv;
    String token,androidId;
   static LinearLayout adViewContainer;
   ArrayList<ImageSliderModel> imageArray = new ArrayList<>();
    AdView adView;
    private int progressValue = 0;
    LinearLayout l_bottom_player;
    boolean isFragmentLoaded = false;
    int TAG_Observer;
    Handler handler;
    int currentPage = 0;
    JSONObject object;
    PojoSongForPlayer songDetail = null;
//    Bottom Menu
    LinearLayout mixesMenu,artistMenu,djsMenu,playListMenu,favMenu,player_header;
//    Bottom Player
    ImageView iv_bottom_playpause;
    TextView bottom_song_name,bottom_artist_name,final_songduration,start_songDuration;
    SeekBar seekBar;
    //mainPlayer
    ImageView iv_main_playpause,iv_prv,iv_next,iv_repeat,iv_shuffle;
    TextView songname,artistname;
    ProgressBar bottom_seekbar;
    MySliderAdapter mySliderAdapter;
    ClickableViewPager mPager;
    GifImageView imgGif;
    GifDrawable drawable;
    ImageView iv_englishMenu,iv_latinMenu;

    public static void runOnUIThread(Runnable runnable) {
        runOnUIThread(runnable, 0);
    }

    public static void runOnUIThread(Runnable runnable, long delay) {
        if (delay == 0) {
            ApplicationDMPlayer.applicationHandler.post(runnable);
        } else {
            ApplicationDMPlayer.applicationHandler.postDelayed(runnable, delay);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MobileAds.initialize(getApplicationContext(), "ca-app-pub-4328731208314277/2632012147");
        FirebaseMessaging.getInstance().subscribeToTopic("dj_geraldo");
        isInternetOn();
        context = MainActivity.this;
        getInit();
        getIntentData();
        listener();
        androidId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyAndroidId", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("androidId", androidId);
        editor.putBoolean("isConnected", isInternetOn());
        editor.commit();
        BufferData.getInstance().setAndroidId(androidId);
        BufferData.getInstance().setConnected(isInternetOn());
        Log.d("mytag","Android id is:"+androidId);
        adViewContainer = (LinearLayout)findViewById(R.id.adViewContainer);
        adView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
        adView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                Log.d("MainActivity","Ad is loaded");
            }

            @Override
            public void onAdClosed() {
                Log.d("MainActivity","Ad is closed");
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
            }

            @Override
            public void onAdLeftApplication() {
            }

            @Override
            public void onAdOpened() {
            }
        });
         object = new JSONObject();
        try {
            object.put("u_id", androidId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        preferences = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        token = preferences.getString("token", "");
        Log.d("MainActivity", "Token is:" + token);
        Log.d("Android id is:", androidId);
        if (CheckNetwork.isInternetAvailable(context)) {
            object = new JSONObject();
            try {
                object.put("u_id", androidId);
                object.put("gcm_id", token);
                object.put("device", "android");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            new userAsyncTask(object).execute();
        } else {
            Toast.makeText(context, "No Internet Connection", Toast.LENGTH_SHORT).show();
        }
    }

    public final boolean isInternetOn() {
        ConnectivityManager connec = (ConnectivityManager) getSystemService(getBaseContext().CONNECTIVITY_SERVICE);
        if (connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTED ||
                connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTING ||
                connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTING ||
                connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTED) {
            Log.d("MainActivity", "Connected");
            return true;
        } else if (
                connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.DISCONNECTED ||
                        connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.DISCONNECTED) {
            Log.d("MainActivity", "Not Connected");
            return false;
        }
        return false;
    }
    public void getInit()
    {
        if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)) {
            checkPermission();
        }
        mDialog = new ProgressDialog(context);
        mDialog.setMessage("Loading");
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.setIndeterminate(true);
        mDialog.setCancelable(false);
        BufferData.getInstance().setUniversalProgressLoader(mDialog);
        getImageSlider();
        imgGif = (GifImageView)findViewById(R.id.imgGif);
        drawable= (GifDrawable) imgGif.getDrawable();
        drawable.stop();
        iv_englishMenu = (ImageView)findViewById(R.id.englishMenu);
        iv_latinMenu = (ImageView)findViewById(R.id.latinMenu);
        mPager = (ClickableViewPager)findViewById(R.id.mPager);
        rl_playerLayout = (RelativeLayout)findViewById(R.id.playLayout);
        rl_fragmentLayout = (RelativeLayout)findViewById(R.id.fragment);
        rl_fragment_header = (RelativeLayout)findViewById(R.id.header);
        iv_menulist = (ImageView)findViewById(R.id.menulist);
        iv_back = (ImageView)findViewById(R.id.btnback);
        iv_player = (ImageView)findViewById(R.id.btnplayer);
        l_bottom_player = (LinearLayout)findViewById(R.id.bottom_player);
        mixesMenu = (LinearLayout)findViewById(R.id.mixesMenu);
        artistMenu = (LinearLayout)findViewById(R.id.artistMenu);
        djsMenu = (LinearLayout)findViewById(R.id.djsMenu);
        playListMenu = (LinearLayout)findViewById(R.id.playListMenu);
        favMenu = (LinearLayout)findViewById(R.id.favMenu);
        player_header = (LinearLayout)findViewById(R.id.player_header);
        iv_main_playpause = (ImageView)findViewById(R.id.main_playpause);
        iv_bottom_playpause = (ImageView)findViewById(R.id.bottom_playpause);
        bottom_song_name = (TextView)findViewById(R.id.bottom_song_name);
        bottom_artist_name = (TextView)findViewById(R.id.bottom_artist_name);
        start_songDuration = (TextView)findViewById(R.id.start_songDuration);
        final_songduration = (TextView)findViewById(R.id.final_songduration);
        songname = (TextView)findViewById(R.id.songname);
        artistname = (TextView)findViewById(R.id.artistname);
        seekBar = (SeekBar)findViewById(R.id.seekbar);
        BufferData.getInstance().setSb_progress(seekBar);
        seekBar.setProgress(0);
        bottom_seekbar = (ProgressBar)findViewById(R.id.bottom_seekbar);
        BufferData.getInstance().setSb_progress_single(bottom_seekbar);
        bottom_playlist = (ImageView)findViewById(R.id.bottom_playlist);
        bottom_seekbar.setProgress(0);
        songname.setSelected(true);
        bottom_song_name.setSelected(true);
        artistname.setSelected(true);
        bottom_artist_name.setSelected(true);
        iv_prv = (ImageView)findViewById(R.id.main_prv);
        iv_next = (ImageView)findViewById(R.id.main_next);
        iv_repeat = (ImageView)findViewById(R.id.repeat);
        iv_shuffle = (ImageView)findViewById(R.id.shuffle);
        bottom_next = (ImageView)findViewById(R.id.bottom_next);
        bottom_prv = (ImageView)findViewById(R.id.bottom_prv);
        String isRepeat = Prefs.getPrefInstance().getValue(this, Const.REPEAT, "");
        String isShuffel = Prefs.getPrefInstance().getValue(this, Const.SHUFFEL, "");
        if (isRepeat.equals("1")) {
            iv_repeat.setSelected(true);
        } else {
            iv_repeat.setSelected(false);
        }
        if (isShuffel.equals("1")) {
            iv_shuffle.setSelected(true);
        } else {
            iv_shuffle.setSelected(false);
        }
//        pushFragment(new MenuFragment(context,rl_fragment_header),"Menu",false);
    }

    public void checkPermission() {
        boolean hasPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_PHONE_STATE)
                == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_WIFI_STATE)
                        == PackageManager.PERMISSION_GRANTED;
        final String[] permissions = new String[]{Manifest.permission.READ_PHONE_STATE, Manifest.permission.ACCESS_WIFI_STATE};
        if (hasPermission) {
            ActivityCompat.requestPermissions(this, permissions, 0);
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage("Call Permission is Required.");
            builder.setTitle("Dj Greldo");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ActivityCompat.requestPermissions(MainActivity.this, permissions, 0);
                }
            });
            builder.show();
        }
    }

    private void getImageSlider() {
        if (CheckNetwork.isInternetAvailable(context)) {
            new sliderData().execute();
        } else {
            Toast.makeText(context,"No Internet Conncetion",Toast.LENGTH_SHORT).show();
        }
    }

    public void removeFragment(){
        if (getSupportFragmentManager().getBackStackEntryCount()!=0){
//            fm.popBackStack();
            getSupportFragmentManager().popBackStackImmediate();
        }
    }

    public void listener()
    {
        iv_latinMenu.setOnClickListener(this);
        iv_englishMenu.setOnClickListener(this);
        iv_menulist.setOnClickListener(this);
        iv_back.setOnClickListener(this);
        iv_player.setOnClickListener(this);
        mixesMenu.setOnClickListener(this);
        artistMenu.setOnClickListener(this);
        djsMenu.setOnClickListener(this);
        playListMenu.setOnClickListener(this);
        favMenu.setOnClickListener(this);
        iv_main_playpause.setOnClickListener(this);
        iv_main_playpause.setSelected(false);
        iv_bottom_playpause.setOnClickListener(this);
        iv_bottom_playpause.setSelected(false);
        iv_prv.setOnClickListener(this);
        iv_next.setOnClickListener(this);
        seekBar.setOnSeekBarChangeListener(this);
        iv_repeat.setOnClickListener(this);
        iv_shuffle.setOnClickListener(this);
        bottom_playlist.setOnClickListener(this);
        bottom_prv.setOnClickListener(this);
        bottom_next.setOnClickListener(this);
        iv_repeat.setSelected(false);
        iv_shuffle.setSelected(false);
        mPager.setOnItemClickListener(new ClickableViewPager.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Log.d("ClickableViewPager pos:", " " + position);
                switch (position) {
                    case 0:
                        final VideoData item1 = dataSlider.get(0);
                        Log.d("First Position Slider", item1.getName());
                        String videoLink = item1.getVideo_link();
                        String title = item1.getName();
                        if (mDialog != null) {
                            if (!mDialog.isShowing()) {
                                mDialog.show();
                            }
                        }
                        handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            public void run() {

                                frag = new SliderVideo(context,rl_fragment_header, item1.getName(), item1.getVideo_link(), "Videos",adViewContainer);
                                fm = ((MainActivity) context).getSupportFragmentManager();
                                ft = fm.beginTransaction();
                                ft.replace(R.id.fragmentContainer, frag);
                                ft.addToBackStack(null);
                                ft.commitAllowingStateLoss();
                                getFragmentLayout();
                                mDialog.dismiss();
                            }
                        }, 500); // 500 milliseconds delay

                        break;
                    case 1:
                        final PojoSongForPlayer item2 = songList.get(0);
                        Log.d("Second Position Slider", item2.getS_name());
                        if (mDialog != null) {
                            if (!mDialog.isShowing()) {
                                mDialog.show();
                            }
                        }
                        handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            public void run() {
                                if (MediaController.getInstance().isPlayingAudio(item2) && !MediaController.getInstance().isAudioPaused()) {
                                    MediaController.getInstance().pauseAudio(item2);

                                } else {
                                    MediaController.getInstance().setPlaylist(songList, item2, PhoneMediaControl.SonLoadFor.All.ordinal(), -1);
                                }
                                mDialog.dismiss();
                            }
                        }, 500); // 500 milliseconds delay
                        break;
                    case 2:
                        final VideoData item3 = dataSlider.get(1);
                        Log.d("Third Position Slider", item3.getName());
                        if (mDialog != null) {
                            if (!mDialog.isShowing()) {
                                mDialog.show();
                            }
                        }
                        handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            public void run() {
                                frag = new SliderVideo(context,rl_fragment_header, item3.getName(), item3.getVideo_link(), "Videos",adViewContainer);
                                fm = ((MainActivity) context).getSupportFragmentManager();
                                ft = fm.beginTransaction();
                                ft.replace(R.id.fragmentContainer, frag);
                                ft.addToBackStack(null);
                                ft.commitAllowingStateLoss();
                                getFragmentLayout();
                                mDialog.dismiss();
                            }
                        }, 500); // 500 milliseconds delay
                        break;
                    case 3:
                        final PojoSongForPlayer item4 = songList.get(1);
                        Log.d("Forth Position Slider", item4.getS_name());

                        if (mDialog != null) {
                            if (!mDialog.isShowing()) {
                                mDialog.show();
                            }
                        }
                        handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            public void run() {
                                if (MediaController.getInstance().isPlayingAudio(item4) && !MediaController.getInstance().isAudioPaused()) {
                                    MediaController.getInstance().pauseAudio(item4);

                                } else {
                                    MediaController.getInstance().setPlaylist(songList, item4, PhoneMediaControl.SonLoadFor.All.ordinal(), -1);
                                }
                                mDialog.dismiss();
                            }
                        }, 500); // 500 milliseconds delay

                        break;
                    case 4:
                        final VideoData item5 = dataSlider.get(0);
                        Log.d("Fifth Position Slider", item5.getName());
                        if (mDialog != null) {
                            if (!mDialog.isShowing()) {
                                mDialog.show();
                            }
                        }
                        handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            public void run() {

                                frag = new SliderVideo(context,rl_fragment_header, item5.getName(), item5.getVideo_link(), "Videos",adViewContainer);
                                fm = ((MainActivity) context).getSupportFragmentManager();
                                ft = fm.beginTransaction();
                                ft.replace(R.id.fragmentContainer, frag);
                                ft.addToBackStack(null);
                                ft.commitAllowingStateLoss();
                                getFragmentLayout();
                                mDialog.dismiss();
                            }
                        }, 500); // 500 milliseconds delay

                        break;
                }
            }
        });

        l_bottom_player.setOnTouchListener(new OnSwipeTouchListener(this)
        {
            public void onSwipeUp() {
                super.onSwipeUp();
//                Toast.makeText(context,"Up",Toast.LENGTH_SHORT).show();
                rl_playerLayout.setVisibility(View.VISIBLE);
                ViewAnimator.animate(rl_playerLayout)
                        .duration(500)
                        .translationY(2000, 0)
                        .andAnimate(rl_fragmentLayout)
                        .duration(500)
                        .translationY(0, -2000)
                        .onStop(new AnimationListener.Stop() {
                            @Override
                            public void onStop() {
//                                stopProgressTimer();
                                rl_fragmentLayout.setVisibility(View.GONE);
                                ViewAnimator.animate(rl_fragmentLayout).duration(500).translationY(-2000, 0).start();
                            }
                        })
                        .start();
            }

        });

        player_header.setOnTouchListener(new OnSwipeTouchListener(this)
        {
            @Override
            public void onSwipeDown() {
                super.onSwipeDown();
                if (isFragmentLoaded) {
                    rl_fragmentLayout.setVisibility(View.VISIBLE);
                    ViewAnimator.animate(rl_fragmentLayout)
                            .duration(500)
                            .translationY(-2000, 0)
                            .andAnimate(rl_playerLayout)
                            .translationY(0, 2000)
                            .duration(500)
                            .onStop(new AnimationListener.Stop() {
                                @Override
                                public void onStop() {
                                    rl_playerLayout.setVisibility(View.GONE);
                                    ViewAnimator.animate(rl_playerLayout).duration(500).translationY(-2000, 0).start();
                                }
                            })
                            .start();
                }
            }
        }
        );

    }

    public static void getFragmentLayout()
    {
        rl_playerLayout.setVisibility(View.GONE);
        rl_fragmentLayout.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onDestroy() {
        removeObserver();
        currentPage = 0;
        if (MediaController.getInstance().isAudioPaused()) {
            MediaController.getInstance().cleanupPlayer(context, true, true);
            MusicPreferance.playingSongDetail = null;
            MusicPreferance.saveLastSong(context, null);
            MusicPreferance.playlist.clear();
            MusicPreferance.shuffledPlaylist.clear();
            seekBar.setProgress(0);
            bottom_seekbar.clearFocus();
            seekBar.clearFocus();
            bottom_seekbar.setProgress(0);
        }
        super.onDestroy();
    }

    public static void getMenuFragment()
   {

        menuFragment = new MenuFragment(context,rl_fragment_header);
        menuFm = ((MainActivity)context).getSupportFragmentManager();
        menuFt = menuFm.beginTransaction();
        menuFt.replace(R.id.fragmentContainer,menuFragment);
        menuFt.addToBackStack(null);
        menuFt.commit();
        getFragmentLayout();
   }

    public static void getPlayLayout()
    {
        rl_fragmentLayout.setVisibility(View.GONE);
        rl_playerLayout.setVisibility(View.VISIBLE);
    }


    public static void removeAd()
    {
        adViewContainer.setVisibility(View.GONE);
    }

    public static void getAd()
    {
        adViewContainer.setVisibility(View.VISIBLE);
    }

    private void pushFragment(Fragment fragment, String tag, boolean addToBackStack) {
        removeFragment();
        isFragmentLoaded = true;
        fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.fragmentContainer, fragment, tag);
        fm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        if (addToBackStack) {
            ft.addToBackStack(tag);
        }
        ft.commitAllowingStateLoss();
    }



    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.repeat:
                if (iv_repeat.isSelected()) {
                    iv_repeat.setSelected(!iv_repeat.isSelected());
                    Prefs.getPrefInstance().setValue(this, Const.REPEAT, "0");

                } else {
                    iv_repeat.setSelected(!iv_repeat.isSelected());
                    Prefs.getPrefInstance().setValue(this, Const.REPEAT, "1");
                }
                break;
            case R.id.shuffle:
                if (iv_shuffle.isSelected()) {
                    iv_shuffle.setSelected(!iv_shuffle.isSelected());
                    Prefs.getPrefInstance().setValue(this, Const.SHUFFEL, "0");
                } else {
                    iv_shuffle.setSelected(!iv_shuffle.isSelected());
                    Prefs.getPrefInstance().setValue(this, Const.SHUFFEL, "1");
                }
                break;
            case R.id.menulist:
//                isFragmentLoaded = true;
//                getMenuFragment();
                removeFragment();
                pushFragment(new MenuFragment(context,rl_fragment_header),"Menu",false);
                rl_fragmentLayout.setVisibility(View.VISIBLE);
                rl_playerLayout.setVisibility(View.GONE);
                break;
            case R.id.btnback:
//                rl_fragmentLayout.setVisibility(View.GONE);
//                rl_playerLayout.setVisibility(View.VISIBLE);
                break;
            case R.id.btnplayer:
                rl_fragmentLayout.setVisibility(View.GONE);
                rl_playerLayout.setVisibility(View.VISIBLE);
                break;
            case R.id.mixesMenu:
                removeFragment();
                pushFragment(new MusicGenres(context,rl_fragment_header,true,false),"Music Genres",false);
                rl_fragmentLayout.setVisibility(View.VISIBLE);
                rl_playerLayout.setVisibility(View.GONE);
//                frag = new MusicGenres(context,rl_fragment_header);
//                fm = getSupportFragmentManager();
//                ft = fm.beginTransaction();
//                ft.replace(R.id.fragmentContainer, frag);
//                ft.addToBackStack(null);
//                ft.commit();
//                getFragmentLayout();
                break;
            case R.id.artistMenu:
                removeFragment();
                pushFragment(new ArtistFragment(context,rl_fragment_header),"Artist",false);
                rl_fragmentLayout.setVisibility(View.VISIBLE);
                rl_playerLayout.setVisibility(View.GONE);
//                frag = new ArtistFragment(context,rl_fragment_header);
//                fm = getSupportFragmentManager();
//                ft = fm.beginTransaction();
//                ft.replace(R.id.fragmentContainer, frag);
//                ft.addToBackStack(null);
//                ft.commit();
//                getFragmentLayout();
                break;
            case R.id.djsMenu:
                removeFragment();
                pushFragment(new FeaturedDjFragment(context,rl_fragment_header),"Featured Dj",false);
                rl_fragmentLayout.setVisibility(View.VISIBLE);
                rl_playerLayout.setVisibility(View.GONE);
//                frag = new FeaturedDjFragment(context,rl_fragment_header);
//                fm = getSupportFragmentManager();
//                ft = fm.beginTransaction();
//                ft.replace(R.id.fragmentContainer, frag);
//                ft.addToBackStack(null);
//                ft.commit();
//                getFragmentLayout();
                break;
            case R.id.playListMenu:
                removeFragment();
                pushInnerFragment(new MenuFragment(context,rl_fragment_header,true),"Playlist",false);
//                getFragmentLayout();
                rl_playerLayout.setVisibility(View.GONE);
                rl_fragmentLayout.setVisibility(View.VISIBLE);
                break;
            case R.id.bottom_playlist:
                removeFragment();
                pushInnerFragment(new MyPlaylist(context,rl_fragment_header,false),"Playlist",true);
                getFragmentLayout();
//                frag = new MyPlaylist(context,rl_fragment_header,false);
//                fm = getSupportFragmentManager();
//                ft = fm.beginTransaction();
//                ft.replace(R.id.fragmentContainer, frag);
//                ft.addToBackStack(null);
//                ft.commit();
//                getFragmentLayout();
                break;
            case R.id.favMenu:
                removeFragment();
                pushFragment(new MenuFragment(context,rl_fragment_header,"1"),"Fav",false);
//                pushFragment(new MyFav(context,rl_fragment_header),"Favorites",false);
                getFragmentLayout();
//                frag = new MyFav(context,rl_fragment_header);
//                fm = getSupportFragmentManager();
//                ft = fm.beginTransaction();
//                ft.replace(R.id.fragmentContainer, frag);
//                ft.addToBackStack(null);
//                ft.commit();
//                getFragmentLayout();
                break;
            case R.id.englishMenu:
                removeFragment();
                pushInnerFragment(new EnglishSingles(context,rl_fragment_header,true),"English Singles",true);
                getFragmentLayout();
                break;
            case R.id.latinMenu:
                removeFragment();
                pushInnerFragment(new LatinSingles(context,rl_fragment_header,true),"Latin Singles",true);
                getFragmentLayout();
                break;
            case R.id.main_playpause:
                if (MediaController.getInstance().getPlayingSongDetail() != null)
                {
                    PlayPauseEvent(v);
                } else {
                    if (CheckNetwork.isInternetAvailable(context))
                    {
                    new AllSongsAsyncTask(object).execute();}
                    else
                    {
                        Toast.makeText(context,"No Internet Connection",Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case R.id.bottom_playpause:
                if (MediaController.getInstance().getPlayingSongDetail() != null)
                {
                    PlayPauseEvent(v);
                } else {
                    if (CheckNetwork.isInternetAvailable(context)) {
                        new AllSongsAsyncTask(object).execute();
                    }
                    else
                    {
                        Toast.makeText(context,"No Internet Connection",Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case R.id.main_prv:
                if (MediaController.getInstance().getPlayingSongDetail() != null) {
                    ProgressDialog progressDialog = BufferData.getInstance().getUniversalProgressLoader();
                    if (progressDialog != null) {
                        Utils.getInstance().d("not null");
                        if (!progressDialog.isShowing()) {
                            Utils.getInstance().d("not showing");
//                            mProgressDialog.show();
                            progressDialog.show();
                            progressDialog.setCanceledOnTouchOutside(true);
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    MediaController.getInstance().playPreviousSong();
                                    loadSongsDetails(MediaController.getInstance().getPlayingSongDetail());
                                }
                            }, 500);

                        }

                    }
                }
                break;
            case R.id.main_next:
                if (MediaController.getInstance().getPlayingSongDetail() != null) {
                    ProgressDialog progressDialog = BufferData.getInstance().getUniversalProgressLoader();
                    if (progressDialog != null) {
                        Utils.getInstance().d("not null");
                        if (!progressDialog.isShowing()) {
                            Utils.getInstance().d("not showing");
                            progressDialog.show();
                            progressDialog.setCanceledOnTouchOutside(true);
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    MediaController.getInstance().playNextSong();
                                    loadSongsDetails(MediaController.getInstance().getPlayingSongDetail());
                                }
                            }, 1000);
                        }
                    }
                }
                break;
            case R.id.bottom_next:
                if (MediaController.getInstance().getPlayingSongDetail() != null) {
                    ProgressDialog progressDialog = BufferData.getInstance().getUniversalProgressLoader();
                    if (progressDialog != null) {
                        Utils.getInstance().d("not null");
                        if (!progressDialog.isShowing()) {
                            Utils.getInstance().d("not showing");
                            progressDialog.show();
                            progressDialog.setCanceledOnTouchOutside(true);
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    MediaController.getInstance().playNextSong();
                                    loadSongsDetails(MediaController.getInstance().getPlayingSongDetail());
                                }
                            }, 1000);
                        }
                    }
                }
                break;
            case R.id.bottom_prv:
                if (MediaController.getInstance().getPlayingSongDetail() != null) {
                    ProgressDialog progressDialog = BufferData.getInstance().getUniversalProgressLoader();
                    if (progressDialog != null) {
                        Utils.getInstance().d("not null");
                        if (!progressDialog.isShowing()) {
                            Utils.getInstance().d("not showing");
//                            mProgressDialog.show();
                            progressDialog.show();
                            progressDialog.setCanceledOnTouchOutside(true);
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    MediaController.getInstance().playPreviousSong();
                                    loadSongsDetails(MediaController.getInstance().getPlayingSongDetail());
                                }
                            }, 500);

                        }

                    }
                }
                break;

        }
    }

        private void getIntentData() {
        try {
            Uri data = getIntent().getData();
            if (data != null) {
                if (data.getScheme().equalsIgnoreCase("file")) {
                    String path = data.getPath().toString();
                    if (!TextUtils.isEmpty(path)) {
                        MediaController.getInstance().cleanupPlayer(context, true, true);
//                        MusicPreferance.getPlaylist(context, path);
                        updateTitle(false);
                        MediaController.getInstance().playAudio(MusicPreferance.playingSongDetail);

                    }
                }
                if (data.getScheme().equalsIgnoreCase("http")){}
//                    LogWriter.info(TAG, data.getPath().toString());
                if (data.getScheme().equalsIgnoreCase("content")){}
//                    LogWriter.info(TAG, data.getPath().toString());

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void pushInnerFragment(Fragment frag,String tag,Boolean addtobackstack)
    {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.fragmentContainer,frag,tag);
        if(addtobackstack)
        {
            ft.addToBackStack(tag);
        }
        ft.commit();
    }


    @Override
    protected void onResume() {
        super.onResume();
        addObserver();
        loadAlreadyPlayng();
    }

    @Override
    protected void onPause() {
        super.onPause();
        removeObserver();
    }

    @Override
    public void onBackPressed() {
//        Fragment f = getSupportFragmentManager().findFragmentById(R.id.fragmentContainer);
//        if (rl_playerLayout.getVisibility() == View.VISIBLE) {
//            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
//            alertDialog.setTitle("Dj Gelardo");
//            alertDialog.setMessage("Are you sure want to exit?");
//            alertDialog.setPositiveButton("YES",
//                    new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialog, int which) {
//                            dialog.dismiss();
//                            finish();
//                        }
//                    });
//            alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    dialog.dismiss();
//                }
//            });
//
//            alertDialog.show();
//        } else if (f instanceof MenuFragment) {
//            getPlayLayout();
//        } else {
//            super.onBackPressed();
//        }

        if(adViewContainer.getVisibility()==View.GONE){
            adViewContainer.setVisibility(View.VISIBLE);
        }

        if (rl_playerLayout.getVisibility() == View.VISIBLE) {
            android.support.v7.app.AlertDialog.Builder alertDialog = new android.support.v7.app.AlertDialog.Builder(this);
            alertDialog.setTitle("DJ Graldo");
            alertDialog.setMessage("Are you sure want to exit?");
            alertDialog.setPositiveButton("YES",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            finish();
                        }
                    });
            alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            alertDialog.show();

        } else if (rl_fragment_header.getTag().equals("Outer")) {
            rl_playerLayout.setVisibility(View.VISIBLE);
            rl_fragmentLayout.setVisibility(View.GONE);

        } else if (rl_fragment_header.getTag().equals("Inner")) {
            super.onBackPressed();
        }
    }


    public void addObserver() {
        TAG_Observer = MediaController.getInstance().generateObserverTag();
        NotificationManager.getInstance().addObserver(this, NotificationManager.audioDidReset);
        NotificationManager.getInstance().addObserver(this, NotificationManager.audioPlayStateChanged);
        NotificationManager.getInstance().addObserver(this, NotificationManager.audioDidStarted);
        NotificationManager.getInstance().addObserver(this, NotificationManager.audioProgressDidChanged);
        NotificationManager.getInstance().addObserver(this, NotificationManager.newaudioloaded);
    }

    public void removeObserver() {
        NotificationManager.getInstance().removeObserver(this, NotificationManager.audioDidReset);
        NotificationManager.getInstance().removeObserver(this, NotificationManager.audioPlayStateChanged);
        NotificationManager.getInstance().removeObserver(this, NotificationManager.audioDidStarted);
        NotificationManager.getInstance().removeObserver(this, NotificationManager.audioProgressDidChanged);
        NotificationManager.getInstance().removeObserver(this, NotificationManager.newaudioloaded);
    }

    private void PlayPauseEvent(View v)
    {
        if (MediaController.getInstance().isAudioPaused()) {
            MediaController.getInstance().playAudio(MediaController.getInstance().getPlayingSongDetail());
            iv_main_playpause.setSelected(true);
            iv_bottom_playpause.setSelected(true);
            drawable.start();
        } else {
            MediaController.getInstance().pauseAudio(MediaController.getInstance().getPlayingSongDetail());
            iv_main_playpause.setSelected(false);
            iv_bottom_playpause.setSelected(false);
            drawable.pause();
        }
    }

    @SuppressLint("DefaultLocale")
    public void loadSongsDetails(PojoSongForPlayer mDetail) {
        bottom_song_name.setText(mDetail.getS_name());
        songname.setText(mDetail.getS_name());
        artistname.setText(mDetail.getArtist());
        bottom_artist_name.setText(mDetail.getArtist());
        if (final_songduration != null) {
            long duration = Long.valueOf(mDetail.getS_duration());
            if (duration != 0) {
                final_songduration.setText(Utils.getInstance().milliToString(duration));
            } else {
                final_songduration.setText("00:00:00");
            }
        }
        updateProgress(mDetail);
    }

    private void updateProgress(final PojoSongForPlayer mSongDetail)
    {
        if (seekBar != null || bottom_seekbar != null)
        {
            progressValue = (int) (mSongDetail.audioProgress * 100);
            seekBar.setProgress(progressValue);
            bottom_seekbar.setProgress(progressValue);
            Log.d("mytag","Progress Value is:"+progressValue);

            String timeString = String.format("%02d:%02d:%02d", mSongDetail.audioProgressSec / 3600, (mSongDetail.audioProgressSec % 3600) / 60, mSongDetail.audioProgressSec % 60);
            start_songDuration.setText(timeString);

            Log.d("mytag","Time String is:"+timeString);

            final ProgressDialog progressDialog = BufferData.getInstance().getUniversalProgressLoader();
            if (progressDialog != null) {
                if (progressDialog.isShowing()) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            String duration = start_songDuration.getText().toString();
                            String strTime = duration.substring(duration.length() - 2);
                            int sec1 = mSongDetail.audioProgressSec % 60;
                            int sec = Integer.parseInt(strTime);
                            if (sec > 1 && sec1 > 1) {
                                progressDialog.dismiss();
                            }
                        }
                    }, 500);
                }
            }
        }

    }

    @SuppressLint("DefaultLocale")
    private void updateTitle(boolean shutdown) {
        PojoSongForPlayer mSongDetail = MediaController.getInstance().getPlayingSongDetail();
        if (mSongDetail == null && shutdown) {
            return;
        } else {
            updateProgress(mSongDetail);
            if (MediaController.getInstance().isAudioPaused()) {
                iv_bottom_playpause.setSelected(false);
                iv_main_playpause.setSelected(false);
            } else {
                iv_main_playpause.setSelected(true);
                iv_bottom_playpause.setSelected(true);
            }
            PojoSongForPlayer audioInfo = MediaController.getInstance().getPlayingSongDetail();
            loadSongsDetails(audioInfo);

            if (final_songduration != null) {
                long duration = Long.valueOf(audioInfo.getS_duration());
                if (duration != 0) {
                    final_songduration.setText(Utils.getInstance().milliToString(duration));
                } else {
                    final_songduration.setText("00:00:00");
                }
            }
        }
    }

    private void loadAlreadyPlayng() {
        boolean isPaused = MediaController.getInstance().isAudioPaused();
        PojoSongForPlayer mSongDetail = MusicPreferance.getLastSong(context);
        if (mSongDetail != null && !isPaused) {
            songDetail = mSongDetail;
            updateTitle(false);
            loadSongsDetails(mSongDetail);
        }
    }

    private void startImageSliderTimer() {
        synchronized (progressTimerSync) {
            if (progressTimer != null) {
                try {
                    progressTimer.cancel();
                    progressTimer = null;
                } catch (Exception e) {
                }
            }
            progressTimer = new Timer();
            progressTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    synchronized (sync) {
                        runOnUIThread(new Runnable() {
                            @Override
                            public void run() {
                                int currentItem = mPager.getCurrentItem() + 1;
                                int totalItem = mySliderAdapter.getCount();
//
                                if (currentItem == totalItem) {
                                    mPager.setCurrentItem(0);
                                } else {
                                    mPager.setCurrentItem(currentItem);
                                }
                            }
                        });
                    }
                }
            }, 0, 4000);
        }

    }

    private void stopProgressTimer() {
        synchronized (progressTimerSync) {
            if (progressTimer != null) {
                try {
                    progressTimer.cancel();
                    progressTimer = null;
                } catch (Exception e) {
                    Log.e("message", e.toString());
                }
            }
        }
    }


    @Override
    public void didReceivedNotification(int id, Object... args) {
        if (id == NotificationManager.audioDidStarted || id == NotificationManager.audioPlayStateChanged || id == NotificationManager.audioDidReset) {
            updateTitle(id == NotificationManager.audioDidReset && (Boolean) args[1]);
        } else if (id == NotificationManager.audioProgressDidChanged) {
            PojoSongForPlayer mSongDetail = MediaController.getInstance().getPlayingSongDetail();
            updateProgress(mSongDetail);
        }
    }

    @Override
    public void newSongLoaded(Object... args) {
        songDetail = (PojoSongForPlayer) args[0];
        drawable.start();
        Fragment f = getSupportFragmentManager().findFragmentById(R.id.fragmentContainer);
        if (f instanceof MusicGenresSongs) {
            ((MusicGenresSongs) f).songLoaded(songDetail.getS_url());
        }
        if (f instanceof EnglishSingles) {
            ((EnglishSingles) f).songLoaded(songDetail.getS_url());
        }
        if (f instanceof LatinSingles) {
            ((LatinSingles) f).songLoaded(songDetail.getS_url());
        }
        if (f instanceof PlaylistSongs) {
            ((PlaylistSongs) f).songLoaded(songDetail.getS_url());
        }
        if (f instanceof MyFav) {
            ((MyFav) f).songLoaded(songDetail.getS_url());
        }
        if (f instanceof DjSongsFragment)
        {
            ((DjSongsFragment) f).songLoaded(songDetail.getS_url());
        }
        if (f instanceof ArtistSongsFragment)
        {
            ((ArtistSongsFragment) f).songLoaded(songDetail.getS_url());
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        if (mDialog != null) {
            if (!mDialog.isShowing()) {
                mDialog.show();
            }
        }
        int prog = seekBar.getProgress();
        MediaController.getInstance().seekToProgress(MediaController.getInstance().getPlayingSongDetail(), (float) prog / 100);
        handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                mDialog.dismiss();
            }
        }, 1000); // 1000 milliseconds delay
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {

    }

    @Override
    public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {

    }


    //http://durisimomobileapps.net/djtico/api/songs
    private class AllSongsAsyncTask extends AsyncTask<String, Void, String>
    {
        JSONObject object;

        private AllSongsAsyncTask(JSONObject object)
        {
            this.object = object;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog = new ProgressDialog(context);
            mProgressDialog.setMessage("Loading");
            mProgressDialog.setCanceledOnTouchOutside(true);
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            String result = null;
            try {
                MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                String url = "http://durisimomobileapps.net/djgeraldo/api/songs";
                OkHttpClient client = new OkHttpClient();
                RequestBody body = RequestBody.create(JSON, String.valueOf(object));
                Request request = new Request.Builder()
                        .url(url)
                        .post(body)
                        .build();
                Response response = client.newCall(request).execute();
                result = response.body().string();
                return result;
            } catch (Exception e) {
                return "Response is null";
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            Log.d("mytag", "All songs Response : " + s);
            if (!s.equals(null)) {
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    int status = jsonObject.getInt("status");
                    if (status == 1) {
                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject idJson = jsonArray.getJSONObject(i);
                            int id = idJson.getInt("song_id");
                            Log.d("Song id is", "" + id);
                            JSONObject songNameJson = jsonArray.getJSONObject(i);
                            String song = songNameJson.getString("song_name");
                            Log.d("mytag", "Song Name IS " + song);
                            JSONObject artistJson = jsonArray.getJSONObject(i);
                            String artist = artistJson.getString("artist_name");
                            Log.d("mytag", "artistApi Name IS " + artist);
                            JSONObject imageJson = jsonArray.getJSONObject(i);
                            String image = imageJson.getString("image");
                            Log.d("Image is", image);
                            JSONObject durationJson = jsonArray.getJSONObject(i);
                            String duration = durationJson.getString("song_time");
                            String durInMili = String.valueOf(Utils.getInstance().strToMilli(duration));
                            Log.d("Duration is", duration);
                            JSONObject songUrlJson = jsonArray.getJSONObject(i);
                            String songUrl = songUrlJson.getString("song_url");
                            Log.d("Song Url is", songUrl);
                            JSONObject likeNoJson = jsonArray.getJSONObject(i);
                            int likes = likeNoJson.getInt("like_status");
                            Log.d("like no. is", "" + likes);
                            JSONObject totalLikes = jsonArray.getJSONObject(i);
                            int total = totalLikes.getInt("total_likes");
                            Log.d("Total Likes :", "" + total);
                            allSongs.add(new PojoSongForPlayer(song, durInMili, songUrl, image, artist, id));

//                            allSongs.add(new Song(id, image, song, artistApi, songUrl, durInMili, likes, total, 0, 0, 0, 0, 0));
                            Log.d("MainActivity", "All songs size:" + allSongs.size());
                        }
                        if (allSongs.size() > 0)
                        {
                            PojoSongForPlayer currentData = allSongs.get(0);
                            if (currentData != null)
                            {
                                if (MediaController.getInstance().isPlayingAudio(currentData) && !MediaController.getInstance().isAudioPaused()) {
                                    MediaController.getInstance().pauseAudio(currentData);
                                } else {
                                    MediaController.getInstance().setPlaylist(allSongs, currentData, PhoneMediaControl.SonLoadFor.All.ordinal(), -1);
                                }
                                songDetail = currentData;
                                updateTitle(false);
                                loadSongsDetails(currentData);
                                mProgressDialog.dismiss();
                            }
                        }

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {

            }
            System.out.println(s);
        }
    }


    private class sliderData extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            mProgressDialog = new ProgressDialog(context);
//            mProgressDialog.setMessage("Loading");
//            mProgressDialog.setCanceledOnTouchOutside(false);
//            mProgressDialog.setIndeterminate(true);
//            mProgressDialog.setCancelable(true);
//            mProgressDialog.show();
        }


        @Override
        protected String doInBackground(String... strings) {
            String result = null;
            try {
                MediaType JSON = MediaType.parse("application/json; charset=utf-8");
//                String url = "http://durisimomobileapps.net/djtico/api/slider";
                String url = "http://durisimomobileapps.net/djgeraldo/api/slider";
                OkHttpClient client = new OkHttpClient();
//                    RequestBody body = RequestBody.create(JSON, String.valueOf(object));
                Request request = new Request.Builder()
                        .url(url)
                        .build();
                Response response = client.newCall(request).execute();
                result = response.body().string();
                return result;
            } catch (Exception e) {
                return null;
            }
        }


        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
//                mProgressDialog.dismiss();
            Log.d("mytag", "Slider Response : " + s);
            if (!s.equals(null)) {
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    int status = jsonObject.getInt("status");
                    if (status == 1) {
                        JSONArray data = jsonObject.getJSONArray("data");

                        for (int i = 0; i < data.length(); i++) {
                            JSONObject slider_image_data = data.optJSONObject(i);
                            String image = slider_image_data.getString("image");
                            imageArray.add(new ImageSliderModel(image));
                            Log.d("MainActivity", "Size is:" + imageArray.size());
                        }
                        mySliderAdapter = new MySliderAdapter(context, imageArray);
                        mPager.setAdapter(mySliderAdapter);
                        startImageSliderTimer();
                        JSONArray jsonArray2 = jsonObject.getJSONArray("video");
                        for (int j = 0; j < jsonArray2.length(); j++) {
                            JSONObject video_data = jsonArray2.getJSONObject(j);
                            String name = video_data.getString("name");
                            Log.d("Video Entry", name);
                            String description = video_data.getString("description");
                            String image = video_data.getString("image");
                            String banner_image = video_data.getString("banner_image");
                            String video_link = video_data.getString("video_link");

                            dataSlider.add(new VideoData(name, video_link, description));
//                    Log.d("dataSlider data",dataSlider.get(j).getSongTitle())
                        }

                        Log.d("Video Data", " Size is:" + dataSlider.size());
//                        Log.d("Video Data 0", dataSlider.get(0).getName());
//                        Log.d("Video Data 1", dataSlider.get(1).getName());
//                        Log.d("Video Data 2", dataSlider.get(2).getName());

                        JSONArray jsonArray3 = jsonObject.getJSONArray("audio");
                        for (int k = 0; k < jsonArray3.length(); k++) {
                            JSONObject audio_data = jsonArray3.getJSONObject(k);

                            int song_id = audio_data.getInt("song_id");
                            String song_name = audio_data.getString("song_name");
                            String song_url = audio_data.getString("song_url");
                            String image = audio_data.getString("image");
                            String artists_name = audio_data.getString("artist_name");
                            String song_duration = audio_data.getString("song_time");
                            int total_likes = audio_data.getInt("total_likes");
//                            PojoSongForPlayer mDetail = new PojoSongForPlayer(myBean.getSongTitle(),durInMili, myBean.getSongUrl(), myBean.getI(),myBean.getSongArtist(),myBean.getSongId());
                            String durInMili = String.valueOf(Utils.getInstance().strToMilli(song_duration));
                            songList.add(new PojoSongForPlayer(song_name, durInMili, song_url, image, artists_name, song_id));
                            Log.d("songList Details:", "Size is:" + songList.size());
                            Log.d("songList details", "Song name is:" + songList.get(0).getS_name());
                        }

                    }
                    System.out.println(s);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {

            }
        }
    }


    //http://durisimomobileapps.net/djtico/api/user/register
    private class userAsyncTask extends AsyncTask<String, Void, String> {
        JSONObject jsonObject;

        public userAsyncTask(JSONObject jsonObject) {
            this.jsonObject = jsonObject;
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                MediaType JSON = MediaType.parse("application/json; charset=utf-8");
//                String url = "http://durisimomobileapps.net/djtico/api/user/register";
                String url = "http://durisimomobileapps.net/djgeraldo/api/user/register";
                OkHttpClient client = new OkHttpClient();
                RequestBody body = RequestBody.create(JSON, String.valueOf(jsonObject));
                Request request = new Request.Builder()
                        .url(url)
                        .post(body)
                        .build();
                Response response = client.newCall(request).execute();
                String result = response.body().string();
                return result;
            } catch (Exception e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.d("Register User", "Response is:" + s);
            if (s == null) {
                Log.d("Register Response is:", "null");
            } else {
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    int status = jsonObject.getInt("status");
                    Log.d("MainActivity", "Status" + status);
                    String msg = jsonObject.getString("msg");
                    Log.d("MainActivity", "Msg " + msg);
                    JSONObject object = jsonObject.getJSONObject("data");
                    String user_id = object.getString("u_id");
                    Log.d("User Id is:", user_id);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
