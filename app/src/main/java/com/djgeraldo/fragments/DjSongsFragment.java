package com.djgeraldo.fragments;


import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.djgeraldo.R;
import com.djgeraldo.adapter.SinglesSongsAdapter;
import com.djgeraldo.custom.CustomHeader;
import com.djgeraldo.data.SinglesSongsModel;
import com.djgeraldo.data.PojoSongForPlayer;
import com.djgeraldo.manager.MediaController;
import com.djgeraldo.phonemidea.CheckNetwork;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class DjSongsFragment extends Fragment {

    int djId;
    ArrayList<SinglesSongsModel> data = new ArrayList<>();
    ProgressDialog mProgressDialog;
    RecyclerView djsongsRecycler;
    Context context;
    SinglesSongsAdapter dataAdapter;
    RelativeLayout header;
    ImageView iv_back,iv_player;
    TextView tv_title;
    String title;
    Fragment frag;
    FragmentTransaction ft;
    FragmentManager fm;
    SharedPreferences preferences;
    String androidId;
    boolean isConnected;

    public DjSongsFragment() {
        // Required empty public constructor
    }


    @SuppressLint("ValidFragment")
    public DjSongsFragment(Context context, RelativeLayout header, int djId, String title)
    {
        this.context =context;
        this.header = header;
        this.djId = djId;
        this.title = title;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_dj_songs, container, false);
        djsongsRecycler = (RecyclerView)v.findViewById(R.id.djsongsRecycler);
        djsongsRecycler.setLayoutManager(new LinearLayoutManager(context));
        tv_title = header.findViewById(R.id.titleHeader);
        tv_title.setText(title);
        tv_title.setSelected(true);
        header.setTag("inner");
        CustomHeader.setInnerFragment(getActivity(),header);
        preferences = context.getSharedPreferences("MyAndroidId",Context.MODE_PRIVATE);
        androidId = preferences.getString("androidId","");
        isConnected = preferences.getBoolean("isConnected",false);
        Log.d("djsongs","Android id: "+androidId);
        Log.d("djsongs","Net is connected: "+isConnected);
//        iv_back = header.findViewById(R.id.btnback);
//        iv_player = header.findViewById(R.id.btnplayer);
//        iv_back.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                MainActivity.getMenuFragment();
//                frag = new FeaturedDjFragment(context,header);
//                fm =((MainActivity)context).getSupportFragmentManager();
//                ft = fm.beginTransaction();
//                ft.replace(R.id.fragmentContainer, frag);
//                ft.addToBackStack(null);
//                ft.commit();
//                MainActivity.getFragmentLayout();
//            }
//        });
//        iv_player.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                MainActivity.getPlayLayout();
//            }
//        });
        JSONObject object = new JSONObject();
        try {
//            object.put("u_id",androidId);
            object.put("feature_dj_id",djId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (CheckNetwork.isInternetAvailable(context)) {
            data.clear();
            new DjSongAsyncTask(object).execute();
        }
        else
        {
            Toast.makeText(context,"No Internet Connection",Toast.LENGTH_SHORT).show();
        }
        return v;
    }

    public void songLoaded(String url) {
        Log.d("MyPlaylist", "Url is:" + url);
        for (SinglesSongsModel s1 : data) {
            if (s1.getS_url().equals(url)) {
                s1.setNowPlaying(1);
            } else {
                s1.setNowPlaying(0);
            }
            SinglesSongsAdapter dataAdapter = new SinglesSongsAdapter(context,header, data);
            djsongsRecycler.setAdapter(dataAdapter);
        }
    }



    //    http://durisimomobileapps.net/djtico/api/dj/songs
    public class DjSongAsyncTask extends AsyncTask<String, Void, String> {

        final PojoSongForPlayer playingSong = MediaController.getInstance().getPlayingSongDetail();
        private JSONObject jsonObject;
        public DjSongAsyncTask(JSONObject jsonObject) {
            this.jsonObject = jsonObject;
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
//                String url = "http://durisimomobileapps.net/djtico/api/dj/songs";
                String url = "http://durisimomobileapps.net/djgeraldo/api/dj/songs";
                OkHttpClient client = new OkHttpClient();
                RequestBody body = RequestBody.create(JSON, String.valueOf(jsonObject));
                Request request = new Request.Builder()
                        .url(url)
                        .post(body)
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
            Log.d("Dj Songs", "Response is:" + s);
            if (s == null) {
                Log.d("mytag", "Bookings Response is null");
            } else {
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject songIdJson = jsonArray.getJSONObject(i);
                        int songId = songIdJson.getInt("song_id");
                        Log.d("Dj Songs", "Song Id is:" + songId);
                        JSONObject songNameJson = jsonArray.getJSONObject(i);
                        String songName = songNameJson.getString("song_name");
                        Log.d("Dj Songs", "Song Name is:" + songName);
                        JSONObject artistJson = jsonArray.getJSONObject(i);
                        String artist = artistJson.getString("artist_name");
                        Log.d("Dj songs", "Song Artist is: " + artist);
                        JSONObject songUrlJson = jsonArray.getJSONObject(i);
                        String songUrl = songUrlJson.getString("song_url");
                        Log.d("Dj songs", "Song Url is: " + songUrl);
                        JSONObject songImageJson = jsonArray.getJSONObject(i);
                        String songImage = songImageJson.getString("image");
                        Log.d("Dj songs", "Song image is: " + songImage);
                        JSONObject likeStatusJson = jsonArray.getJSONObject(i);
                        int likeStatus = likeStatusJson.getInt("like_status");
                        Log.d("Dj songs", "Song like status is: " + likeStatus);
                        JSONObject totalLikes = jsonArray.getJSONObject(i);
                        int total = totalLikes.getInt("total_likes");
                        Log.d("Total Likes :",""+total);
                        JSONObject songDurationJson = jsonArray.getJSONObject(i);
                        String songDuration = songDurationJson.getString("song_time");
                        Log.d("Dj songs", "Song Duration is: " + songDuration);
//                    dataArtist1.add(new Song(id,image,song,artistApi,songUrl,duration,likes,0,0,0,0));
                        int nowPlaying;
                        if (playingSong != null)
                        {
                            if (playingSong.getS_url().equals(songUrl))
                            {
                                nowPlaying = 1;
                            }
                            else
                            {
                                nowPlaying = 0;
                            }
                        }
                        else
                        {
                            nowPlaying = 0;
                        }
                        Log.d("Dj","NowPlaying: "+nowPlaying);
                        data.add(new SinglesSongsModel(songName,songDuration,songUrl,songImage,artist,songId,likeStatus,total,nowPlaying));
                    }
                    try {
                        dataAdapter = new SinglesSongsAdapter(context, header,data);
                        djsongsRecycler.setAdapter(dataAdapter);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } catch(JSONException e){
                    e.printStackTrace();
                }
            }

            System.out.println(s);
        }
    }
}
