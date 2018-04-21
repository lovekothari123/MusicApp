package com.djgeraldo.fragments;


import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import com.djgeraldo.adapter.FavAdapter;
import com.djgeraldo.custom.CustomHeader;
import com.djgeraldo.data.MyFavSongs;
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
public class MyFav extends Fragment {

    Context context;
    RelativeLayout header;
    ImageView iv_back, iv_player;
    TextView tv_title;
    ProgressDialog mProgressDialog;
    RecyclerView favRecycler;
    FavAdapter dataAdapter;
    ArrayList<MyFavSongs> data = new ArrayList<>();
    SharedPreferences preferences;
    String androidId;
    boolean isConnected;
    private Boolean isOuter = false;

    public MyFav() {
        // Required empty public constructor
    }

    @SuppressLint("ValidFragment")
    public MyFav(Context context, RelativeLayout header, Boolean isOuter) {
        this.context = context;
        this.header = header;
        this.isOuter = isOuter;
    }


    @SuppressLint("ValidFragment")
    public MyFav(Context context, RelativeLayout header) {
        this.context = context;
        this.header = header;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_my_fav, container, false);
        if (isOuter) {
            header.setTag("outer");
            CustomHeader.setOuterFragment(getActivity(), header);
        } else {
            header.setTag("inner");
            CustomHeader.setInnerFragment(getActivity(), header);
        }
        favRecycler = v.findViewById(R.id.favRecycler);
        favRecycler.setLayoutManager(new LinearLayoutManager(context));
//        header.setTag("inner");
//        CustomHeader.setInnerFragment(getActivity(),header);
        tv_title = header.findViewById(R.id.titleHeader);
        tv_title.setText("My Favorites");
        tv_title.setSelected(true);


        preferences = context.getSharedPreferences("MyAndroidId", Context.MODE_PRIVATE);
        androidId = preferences.getString("androidId", "");
        isConnected = preferences.getBoolean("isConnected", false);
        Log.d("MyFav", "Android id: " + androidId);
        Log.d("MyFav", "Net is connected: " + isConnected);
        iv_back = header.findViewById(R.id.btnback);
//        iv_player = header.findViewById(R.id.btnplayer);
//        iv_back.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                MainActivity.getMenuFragment();
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
            object.put("u_id", androidId);
            Log.d("mytag", "Json Object : " + object.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (CheckNetwork.isInternetAvailable(context)) {
            data.clear();
            new MyFavouriteAsyncTask(object).execute();
        } else {
            Toast.makeText(context, "No Internet Connection", Toast.LENGTH_SHORT).show();
        }
        return v;
    }

    public void songLoaded(String url) {
        Log.d("MyPlaylist", "Url is:" + url);
        for (MyFavSongs s1 : data) {
            if (s1.getS_url().equals(url)) {
                s1.setNowPlaying(1);
            } else {
                s1.setNowPlaying(0);
            }
            FavAdapter dataAdapter = new FavAdapter(context, header, data);
            favRecycler.setAdapter(dataAdapter);
        }
    }


    //http://durisimomobileapps.net/djsisko/api/favs/getsongs
    private class MyFavouriteAsyncTask extends AsyncTask<String, Void, String> {
        final PojoSongForPlayer playing = MediaController.getInstance().getPlayingSongDetail();
        JSONObject object;

        private MyFavouriteAsyncTask(JSONObject object) {
            this.object = object;
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
        protected String doInBackground(String... strings) {
            String result = null;
            try {
                MediaType JSON = MediaType.parse("application/json; charset=utf-8");
//                String url = "http://durisimomobileapps.net/djtico/api/user/favourite/songs";
                String url = "http://durisimomobileapps.net/djgeraldo/api/user/favourite/songs";
                OkHttpClient client = new OkHttpClient();
                RequestBody body = RequestBody.create(JSON, String.valueOf(object));
                Request request = new Request.Builder()
                        .url(url)
                        .post(body)
                        .build();
                Response response = client.newCall(request).execute();
                result = response.body().string();
                return result;
            } catch (
                    Exception e)

            {
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            mProgressDialog.dismiss();
            Log.d("mytag", "My Favorite songs Response : " + s);
            if (s == null) {
                Log.d("mytag", "My Favorite Response is null");
            } else {
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    int status = jsonObject.getInt("status");
                    Log.d("Favourites song", " status is:" + status);
                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject favIdJson = jsonArray.getJSONObject(i);
                        int favId = favIdJson.getInt("fav_id");
                        Log.d("Favourites", "Fav Id is" + favId);
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
                        int nowPlaying;
                        if (playing != null) {
                            if (playing.getS_url().equals(songUrl)) {
                                nowPlaying = 1;
                            } else {
                                nowPlaying = 0;
                            }
                        } else {
                            nowPlaying = 0;
                        }
                        Log.d("MyFav", "NowPlaying: " + nowPlaying);
//                        id, image, song, artistApi, songUrl, duration, likes,total, 0, status, 0, favId,nowPlaying
                        data.add(new MyFavSongs(favId, song, duration, songUrl, image, artist, id, likes, total, nowPlaying));
                    }
                    try {
                        dataAdapter = new FavAdapter(context, header, data);
                        favRecycler.setAdapter(dataAdapter);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            System.out.println(s);
        }
    }


}
