package com.djgeraldo.fragments;


import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.djgeraldo.R;
import com.djgeraldo.activities.MainActivity;
import com.djgeraldo.custom.CustomHeader;

/**
 * A simple {@link Fragment} subclass.
 */
public class MenuFragment extends Fragment implements View.OnClickListener{

    Context context;
    RelativeLayout header;
    ScrollView scrollview;
    TextView tv_titleHeader;
    LinearLayout l_mPlayer,l_musicGenre,l_latin,l_english,l_myplaylist,l_myfav,l_events,l_gallery,l_videos,l_signUp,l_book,l_media,l_notification,l_shareApp,l_Biography,l_sponsored;
    Fragment frag;
    FragmentManager fm;
    FragmentTransaction ft;
    ImageView iv_back,iv_player;
    private boolean Count =true;
    private boolean val;
    private String status;
    private ImageView backBtn;

    public MenuFragment()
    {

    }

    @SuppressLint("ValidFragment")
    public MenuFragment(Context context, RelativeLayout header)
    {
        this.context = context;
        this.header = header;
        this.status = "0";
        val = false;
    }

    @SuppressLint("ValidFragment")
    public MenuFragment(Context context, RelativeLayout header, String status) {
        this.context = context;
        this.header = header;
        this.status = status;
        val = false;
    }

    @SuppressLint("ValidFragment")
    public MenuFragment(Context context, RelativeLayout header, boolean val)
    {
        this.context = context;
        this.header = header;
        this.val=val;
        status = "0";
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_menu, container, false);
        if (status.equals("1"))
        {
            status = "0";
            pushInnerFragment(new MyFav(context,header,true),"MyFav",true);
        }
        if (val)
        {
            val = false;
            pushInnerFragment(new MyPlaylist(context,header,true),"MyPlaylist",true);
        }

        header.setTag("outer");
        CustomHeader.setOuterFragment(getActivity(),header);
        tv_titleHeader = header.findViewById(R.id.titleHeader);
        tv_titleHeader.setText("Main Features");
        backBtn = header.findViewById(R.id.btnback);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.getPlayLayout();
            }
        });

        getInit(v);
        getListener();
        return v;
    }

    public void removeFragment(){
        if (getFragmentManager().getBackStackEntryCount()!=0){
//            fm.popBackStack();
            getFragmentManager().popBackStackImmediate();
        }
    }


    public void getInit(View v)
    {
        scrollview = (ScrollView) v.findViewById(R.id.scrollview);
        scrollview.setSmoothScrollingEnabled(true);

//        iv_back = header.findViewById(R.id.btnback);
//        iv_player = header.findViewById(R.id.btnplayer);
        l_mPlayer = (LinearLayout)v.findViewById(R.id.mPlayer);
        l_musicGenre = (LinearLayout)v.findViewById(R.id.musicGenre);
        l_latin = (LinearLayout)v.findViewById(R.id.latin);
        l_english = (LinearLayout)v.findViewById(R.id.english);
        l_myplaylist = (LinearLayout)v.findViewById(R.id.playList);
        l_myfav = (LinearLayout)v.findViewById(R.id.fav);
        l_events = (LinearLayout)v.findViewById(R.id.events);
        l_gallery = (LinearLayout)v.findViewById(R.id.gallery);
        l_videos = (LinearLayout)v.findViewById(R.id.videos);
        l_signUp = (LinearLayout)v.findViewById(R.id.signUp);
        l_book = (LinearLayout)v.findViewById(R.id.book);
        l_media = (LinearLayout)v.findViewById(R.id.media);
        l_notification = (LinearLayout)v.findViewById(R.id.notification);
        l_shareApp = (LinearLayout)v.findViewById(R.id.shareApp);
        l_Biography = (LinearLayout)v.findViewById(R.id.Biography);
        l_sponsored = (LinearLayout)v.findViewById(R.id.sponsored);
    }

    public void getListener()
    {
        l_mPlayer.setOnClickListener(this);
        l_musicGenre.setOnClickListener(this);
        l_latin.setOnClickListener(this);
        l_english.setOnClickListener(this);
        l_myplaylist.setOnClickListener(this);
        l_myfav.setOnClickListener(this);
        l_events.setOnClickListener(this);
        l_gallery.setOnClickListener(this);
        l_videos.setOnClickListener(this);
        l_signUp.setOnClickListener(this);
        l_book.setOnClickListener(this);
        l_media.setOnClickListener(this);
        l_notification.setOnClickListener(this);
        l_shareApp.setOnClickListener(this);
        l_Biography.setOnClickListener(this);
        l_sponsored.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.sponsored:
                pushInnerFragment(new SponsoredBy(context,header),"Music Geners",true);
                break;
            case R.id.Biography:
                pushInnerFragment(new Biography(context,header),"Music Geners",true);
                break;
            case R.id.mPlayer:
//                Toast.makeText(context,"Music Player",Toast.LENGTH_SHORT).show();
                MainActivity.getPlayLayout();
                break;
            case R.id.musicGenre:
//                frag = new MusicGenres(context,header);
//                fm = ((MainActivity)context).getSupportFragmentManager();
//                ft = fm.beginTransaction();
//                ft.replace(R.id.fragmentContainer,frag);
//                ft.commit();
                pushInnerFragment(new MusicGenres(context,header,false,Count),"Music Geners",true);
                break;
            case R.id.latin:
                pushInnerFragment(new LatinSingles(context,header,false),"Latin Singles",true);
//                frag = new LatinSingles(context,header);
//                fm = ((MainActivity)context).getSupportFragmentManager();
//                ft = fm.beginTransaction();
//                ft.replace(R.id.fragmentContainer,frag);
//                ft.commit();
                break;
            case R.id.english:
                pushInnerFragment(new EnglishSingles(context,header,false),"English Singles",true);
//                frag = new EnglishSingles(context,header);
//                fm = ((MainActivity)context).getSupportFragmentManager();
//                ft = fm.beginTransaction();
//                ft.replace(R.id.fragmentContainer,frag);
//                ft.commit();
                break;
            case R.id.playList:
                pushInnerFragment(new MyPlaylist(context,header,false),"My Playlist",true);
//                frag = new MyPlaylist(context,header);
//                fm = ((MainActivity)context).getSupportFragmentManager();
//                ft = fm.beginTransaction();
//                ft.replace(R.id.fragmentContainer,frag);
//                ft.commit();
                break;
            case R.id.fav:
                pushInnerFragment(new MyFav(context,header,false),"My Favorites",true);
//                frag = new MyFav(context,header);
//                fm = ((MainActivity)context).getSupportFragmentManager();
//                ft = fm.beginTransaction();
//                ft.replace(R.id.fragmentContainer,frag);
//                ft.commit();
                break;
            case R.id.events:
                pushInnerFragment(new Events(context,header),"Events",true);
//                frag = new Events(context,header);
//                fm = ((MainActivity)context).getSupportFragmentManager();
//                ft = fm.beginTransaction();
//                ft.replace(R.id.fragmentContainer,frag);
//                ft.commit();
                break;
            case R.id.gallery:
                pushInnerFragment(new MediaGallery(context,header),"Media Gallery",true);
//                frag = new MediaGallery(context,header);
//                fm = ((MainActivity)context).getSupportFragmentManager();
//                ft = fm.beginTransaction();
//                ft.replace(R.id.fragmentContainer,frag);
//                ft.commit();
                break;
            case R.id.videos:
                pushInnerFragment(new Videos(context,header),"Videos",true);
//                frag = new Videos(context,header);
//                fm = ((MainActivity)context).getSupportFragmentManager();
//                ft = fm.beginTransaction();
//                ft.replace(R.id.fragmentContainer,frag);
//                ft.commit();
                break;
            case R.id.signUp:
                pushInnerFragment(new SignUp(context,header),"Sign Up",true);
//                frag = new SignUp(context,header);
//                fm = ((MainActivity)context).getSupportFragmentManager();
//                ft = fm.beginTransaction();
//                ft.replace(R.id.fragmentContainer,frag);
//                ft.commit();
                break;
            case R.id.book:
                pushInnerFragment(new Bookings(context,header),"Bookings",true);
//                frag = new Bookings(context,header);
//                fm = ((MainActivity)context).getSupportFragmentManager();
//                ft = fm.beginTransaction();
//                ft.replace(R.id.fragmentContainer,frag);
//                ft.commit();
                break;
            case R.id.media:
                pushInnerFragment(new SocialMedia(context,header),"Social Media",true);
//                frag = new SocialMedia(context,header);
//                fm = ((MainActivity)context).getSupportFragmentManager();
//                ft = fm.beginTransaction();
//                ft.replace(R.id.fragmentContainer,frag);
//                ft.commit();
                break;
            case R.id.notification:
                pushInnerFragment(new Notification(context,header),"Notification",true);
//                frag = new Notification(context,header);
//                fm = ((MainActivity)context).getSupportFragmentManager();
//                ft = fm.beginTransaction();
//                ft.replace(R.id.fragmentContainer,frag);
//                ft.commit();
                break;
            case R.id.shareApp:
                pushInnerFragment(new ShareMyApp(context,header),"Share My App",true);
//                frag = new ShareMyApp(context,header);
//                fm = ((MainActivity)context).getSupportFragmentManager();
//                ft = fm.beginTransaction();
//                ft.replace(R.id.fragmentContainer,frag);
//                ft.commit();
                break;
        }
    }

    private void pushInnerFragment(Fragment frag,String tag,Boolean addtobackstack)
    {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.fragmentContainer,frag,tag);
        if(addtobackstack)
        {
            ft.addToBackStack(tag);
        }
        ft.commit();
    }
}
