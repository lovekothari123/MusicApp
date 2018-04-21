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
public class ArtistSongsFragment extends Fragment {

    int artistId;
    ArrayList<SinglesSongsModel> data = new ArrayList<>();
    ProgressDialog mProgressDialog;
    RecyclerView artistSongRecycler;
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


    public ArtistSongsFragment() {
        // Required empty public constructor
    }

    @SuppressLint("ValidFragment")
    public ArtistSongsFragment(Context context, RelativeLayout header, int artistId, String title)
    {
        this.context =context;
        this.header = header;
        this.artistId = artistId;
        this.title = title;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_artist_songs, container, false);
        artistSongRecycler = (RecyclerView)v.findViewById(R.id.artistSongRecycler);
        artistSongRecycler.setLayoutManager(new LinearLayoutManager(context));
        tv_title = header.findViewById(R.id.titleHeader);
        tv_title.setText(title);
        tv_title.setSelected(true);
        header.setTag("inner");
        CustomHeader.setInnerFragment(getActivity(),header);
//        iv_back = header.findViewById(R.id.btnback);
//        iv_player = header.findViewById(R.id.btnplayer);
        preferences = context.getSharedPreferences("MyAndroidId",Context.MODE_PRIVATE);
        androidId = preferences.getString("androidId","");
        isConnected = preferences.getBoolean("isConnected",false);
        Log.d("Artist","Android id: "+androidId);
        Log.d("Artist","Net is connected: "+isConnected);
//        iv_back.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                MainActivity.getMenuFragment();
//                frag = new ArtistFragment(context,header);
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
            object.put("artists_id",artistId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (CheckNetwork.isInternetAvailable(context))
        {
            data.clear();
            new ArtistSongAsyncTask(object).execute();
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
            artistSongRecycler.setAdapter(dataAdapter);
        }
    }


    //    http://durisimomobileapps.net/djtico/api/dj/songs
    public class ArtistSongAsyncTask extends AsyncTask<String, Void, String> {

        final PojoSongForPlayer playingSong = MediaController.getInstance().getPlayingSongDetail();
        private JSONObject jsonObject;
        public ArtistSongAsyncTask(JSONObject jsonObject) {
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
//                String url = "http://durisimomobileapps.net/djtico/api/artist/songs";
                String url = "http://durisimomobileapps.net/djgeraldo/api/artist/songs";
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
            Log.d("Artist Songs", "Response is:" + s);
            if (s == null) {
                Log.d("mytag", "Artist Response is null");
            } else {
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject songIdJson = jsonArray.getJSONObject(i);
                        int songId = songIdJson.getInt("song_id");
                        Log.d("Artist Songs", "Song Id is:" + songId);
                        JSONObject songNameJson = jsonArray.getJSONObject(i);
                        String songName = songNameJson.getString("song_name");
                        Log.d("Artist Songs", "Song Name is:" + songName);
                        JSONObject artistJson = jsonArray.getJSONObject(i);
                        String artist = artistJson.getString("artist_name");
                        Log.d("Artist songs", "Song Artist is: " + artist);
                        JSONObject songUrlJson = jsonArray.getJSONObject(i);
                        String songUrl = songUrlJson.getString("song_url");
                        Log.d("Artist songs", "Song Url is: " + songUrl);
                        JSONObject songImageJson = jsonArray.getJSONObject(i);
                        String songImage = songImageJson.getString("image");
                        Log.d("Artist songs", "Song image is: " + songImage);
                        JSONObject likeStatusJson = jsonArray.getJSONObject(i);
                        int likeStatus = likeStatusJson.getInt("like_status");
                        Log.d("Artist songs", "Song like status is: " + likeStatus);
                        JSONObject totalLikes = jsonArray.getJSONObject(i);
                        int total = totalLikes.getInt("total_likes");
                        Log.d("Total Likes :",""+total);
                        JSONObject songDurationJson = jsonArray.getJSONObject(i);
                        String songDuration = songDurationJson.getString("song_time");
                        Log.d("Artist songs", "Song Duration is: " + songDuration);
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
                        Log.d("Artist","NowPlaying: "+nowPlaying);
                        data.add(new SinglesSongsModel(songName,songDuration,songUrl,songImage,artist,songId,likeStatus,total,nowPlaying));
//

                    }
                    try {
                        dataAdapter = new SinglesSongsAdapter(context, header,data);
                        artistSongRecycler.setAdapter(dataAdapter);
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
