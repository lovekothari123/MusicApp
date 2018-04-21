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
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.djgeraldo.R;
import com.djgeraldo.custom.CustomHeader;
import com.djgeraldo.phonemidea.CheckNetwork;

/**
 * A simple {@link Fragment} subclass.
 */
public class SocialLink extends Fragment {

    Context context;
    String title,link;
    WebView mWebView;
    Fragment frag;
    FragmentManager fm;
    FragmentTransaction ft;
    RelativeLayout header;
    ImageView iv_back,iv_player;
    TextView tv_title;

    public SocialLink() {
        // Required empty public constructor
    }

    @SuppressLint("ValidFragment")
    public SocialLink(Context context,RelativeLayout header, String title, String link)
    {
        this.context = context;
        this.header = header;
        this.title = title;
        this.link = link;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_social_link, container, false);
        mWebView = (WebView) v.findViewById(R.id.socialLinkWeb);
        if (CheckNetwork.isInternetAvailable(context)) {
            mWebView.loadUrl(link);
        }
        else
        {
            Toast.makeText(context,"No Internet Connection",Toast.LENGTH_SHORT).show();
        }
        // Enable Javascript
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        // Force links and redirects to open in the WebView instead of in a browser
        mWebView.setWebViewClient(new WebViewClient());
        tv_title = header.findViewById(R.id.titleHeader);
        tv_title.setText(title);
        tv_title.setSelected(true);
//        iv_back = header.findViewById(R.id.btnback);
//        iv_player = header.findViewById(R.id.btnplayer);
//        iv_back.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                MainActivity.getMenuFragment();
//                frag = new SocialMedia(context,header);
//                fm = ((MainActivity)context).getSupportFragmentManager();
//                ft = fm.beginTransaction();
//                ft.replace(R.id.fragmentContainer,frag);
//                ft.addToBackStack(null);
//                ft.commit();
//            }
//        });
//        iv_player.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                MainActivity.getPlayLayout();
//            }
//        });
        header.setTag("inner");
        CustomHeader.setInnerFragment(getActivity(),header);
        return v;
    }

}
