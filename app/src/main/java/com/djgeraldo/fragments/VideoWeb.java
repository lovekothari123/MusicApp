package com.djgeraldo.fragments;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.djgeraldo.R;
import com.djgeraldo.activities.MainActivity;
import com.djgeraldo.custom.CustomHeader;
import com.djgeraldo.manager.MediaController;
import com.djgeraldo.phonemidea.CheckNetwork;

/**
 * A simple {@link Fragment} subclass.
 */
public class VideoWeb extends Fragment {

    Context context;
    String title,videoLink;
    Fragment frag;
    FragmentTransaction ft;
    FragmentManager fm;
    WebView mWebView;
    WebSettings webSettings;
    String androidId;
    boolean isConnected;
    SharedPreferences preferences;
    String mainTag;
    LinearLayout adView;
    RelativeLayout header;
    ImageView iv_back,iv_player;
    TextView tv_title;

    public VideoWeb() {
        // Required empty public constructor
    }

    @SuppressLint("ValidFragment")
    public VideoWeb(Context context, RelativeLayout header, String title, String videoLink, LinearLayout adView)
    {
        this.context = context;
        this.header = header;
        this.title = title;
        this.videoLink = videoLink;
        this.adView = adView;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        MainActivity.removeAd();
        View v = inflater.inflate(R.layout.fragment_video_web, container, false);
        tv_title = header.findViewById(R.id.titleHeader);
        tv_title.setText(title);
        tv_title.setSelected(true);
        iv_back = header.findViewById(R.id.btnback);
        iv_player = header.findViewById(R.id.btnplayer);
        header.setTag("inner");
        CustomHeader.setInnerFragment(getActivity(),header);
//        iv_back.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                MainActivity.getAd();
//                MainActivity.getMenuFragment();
//                frag = new Videos(context,header);
//                fm = ((MainActivity)context).getSupportFragmentManager();
//                ft = fm.beginTransaction();
//                ft.replace(R.id.fragmentContainer,frag);
//                ft.commit();

//            }
//        });
//        iv_player.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                MainActivity.getAd();
//                MainActivity.getPlayLayout();
//            }
//        });
        preferences = context.getSharedPreferences("MyAndroidId",Context.MODE_PRIVATE);
        androidId = preferences.getString("androidId","");
        isConnected = preferences.getBoolean("isConnected",false);
        Log.d("Videos2","Android id: "+androidId);
        Log.d("Videos2","Net is connected: "+isConnected);
        mWebView = (WebView) v.findViewById(R.id.webView);
        if (CheckNetwork.isInternetAvailable(context)) {
            getwebView(videoLink);
            stopSong();
        }
        else
        {
            Toast.makeText(context,"No Internet Connection",Toast.LENGTH_SHORT).show();
        }
        return v;
    }

    public void stopSong()
    {
        mWebView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (MediaController.getInstance().getPlayingSongDetail() != null)
                {
                    if (!MediaController.getInstance().isAudioPaused())
                    {
                        MediaController.getInstance().pauseAudio(MediaController.getInstance().getPlayingSongDetail());
                    }
                }
                return false;
            }
        });
    }

    public void getwebView(String link)
    {
        mWebView.setWebViewClient(new WebViewClient()
        {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
        mWebView.getSettings().setLoadsImagesAutomatically(true);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.loadUrl(link);
    }


}
