package com.djgeraldo.fragments;


import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.djgeraldo.R;
import com.djgeraldo.activities.MainActivity;
import com.djgeraldo.data.SocialMediaModel;
import com.djgeraldo.phonemidea.CheckNetwork;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class SocialMedia extends Fragment {

    Context context;
    RelativeLayout header;
    ImageView iv_back,iv_player;
    TextView tv_title;
    ProgressDialog mProgressDialog;
    ArrayList<SocialMediaModel> data = new ArrayList<>();
    ImageView facebook,twitter,youtube,instagram;
    String facebookString,twitterString,youtubeString,instagramString;
    String androidId;
    boolean isConnected;
    SharedPreferences preferences;
    ImageView backButton1,playerButton1;
    Fragment frag;
    FragmentTransaction ft;
    FragmentManager fm;
    String tag = SocialMedia.class.getSimpleName();
    MediaPlayer radioPlayer;
    LinearLayout adView;


    public SocialMedia() {
        // Required empty public constructor
    }

    @SuppressLint("ValidFragment")
    public SocialMedia(Context context, RelativeLayout header)
    {
        this.context = context;
        this.header = header;
    }
    @SuppressLint("ValidFragment")
    public SocialMedia(Context context,LinearLayout adView)
    {
        this.context = context;
        this.adView = adView;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_social_media, container, false);
        facebook = v.findViewById(R.id.facebook);
        twitter = v.findViewById(R.id.twitter);
        youtube = v.findViewById(R.id.youtube);
        instagram = v.findViewById(R.id.instagram);
        tv_title = header.findViewById(R.id.titleHeader);
        tv_title.setText("Social Media");
        tv_title.setSelected(true);
        iv_back = header.findViewById(R.id.btnback);
//        iv_player = header.findViewById(R.id.btnplayer);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.getMenuFragment();
            }
        });
//        iv_player.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                MainActivity.getPlayLayout();
//            }
//        });
//        header.setTag("inner");
//        CustomHeader.setInnerFragment(getActivity(),header);
        preferences = context.getSharedPreferences("MyAndroidId",Context.MODE_PRIVATE);
        androidId = preferences.getString("androidId","");
        isConnected = preferences.getBoolean("isConnected",false);
        Log.d("media","Android id: "+androidId);
        Log.d("media","Net is connected: "+isConnected);
        if (CheckNetwork.isInternetAvailable(context)) {
            data.clear();
            new SocialAsyncTask().execute();
        }
        else
        {
            Toast.makeText(context,"No Internet Connection",Toast.LENGTH_SHORT).show();
        }

        facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                frag = new SocialLink(context,header,"Facebook",facebookString);
                fm = ((MainActivity)context).getSupportFragmentManager();
                ft = fm.beginTransaction();
                ft.replace(R.id.fragmentContainer,frag);
                ft.addToBackStack(null);
                ft.commit();
            }
        });
        twitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                frag = new SocialLink(context,header,"Twitter",twitterString);
                fm = ((MainActivity)context).getSupportFragmentManager();
                ft = fm.beginTransaction();
                ft.replace(R.id.fragmentContainer,frag);
                ft.addToBackStack(null);
                ft.commit();
            }
        });
        youtube.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                frag = new YoutubeFragment(context,header,"Youtube",youtubeString,adView);
                fm = ((MainActivity)context).getSupportFragmentManager();
                ft = fm.beginTransaction();
                ft.replace(R.id.fragmentContainer,frag);
                ft.addToBackStack(null);
                ft.commit();
            }
        });
        instagram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                frag = new SocialLink(context,header,"Instagram",instagramString);
                fm = ((MainActivity)context).getSupportFragmentManager();
                ft = fm.beginTransaction();
                ft.replace(R.id.fragmentContainer,frag);
                ft.addToBackStack(null);
                ft.commit();
            }
        });
        return v; 
    }

    //http://durisimomobileapps.net/djtico/api/sociallinks
    public class SocialAsyncTask extends AsyncTask<String,Void,String>
    {
        private JSONObject jsonObject;
        public SocialAsyncTask() {

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog = new ProgressDialog(context);
            mProgressDialog.setMessage("Loading");
            mProgressDialog.setCanceledOnTouchOutside(true);
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setCancelable(true);
            mProgressDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {

            String result = null;
            try {
                MediaType JSON = MediaType.parse("application/json; charset=utf-8");
//                String url = "http://durisimomobileapps.net/djtico/api/sociallinks";
                String url = "http://durisimomobileapps.net/djgeraldo/api/sociallinks";
                OkHttpClient client = new OkHttpClient();
//               RequestBody body = RequestBody.create(JSON, String.valueOf(jsonObject));
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
            mProgressDialog.dismiss();
            try {
                Log.d("SocialMedia","Response is:"+s);
                JSONObject jsonObject = new JSONObject(s);
                JSONArray jsonArray = jsonObject.getJSONArray("data");
                for (int i = 0; i < jsonArray.length(); i++)
                {
                    JSONObject socialIdJson =jsonArray.getJSONObject(i);
                    int socialId = socialIdJson.getInt("social_id");
                    Log.d("SocialMedia","Social Id is:"+socialId);
                    JSONObject socialLinkJson =jsonArray.getJSONObject(i);
                    String socialLink = socialLinkJson.getString("social_link");
                    Log.d("SocialMedia","Social Link is:"+socialLink);
                    data.add(new SocialMediaModel(socialId,socialLink));
                }

                facebookString = data.get(0).getLink();
                Log.d("Social Media","Facebook: "+facebookString);
                twitterString = data.get(1).getLink();
                Log.d("Social Media","Twitter: "+twitterString);
                youtubeString = data.get(2).getLink();
                Log.d("Social Media","Youtube: "+youtubeString);
                instagramString = data.get(3).getLink();
                Log.d("Social Media","Instagram: "+instagramString);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            System.out.println(s);
        }
    }

}
