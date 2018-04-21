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
import com.djgeraldo.adapter.PlayListSongsAdapter;
import com.djgeraldo.custom.CustomHeader;
import com.djgeraldo.data.MyPlayListSongs;
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
public class PlaylistSongs extends Fragment {


    Context context;
    String title;
    RelativeLayout header;
    ImageView iv_back,iv_player;
    TextView tv_title;
    int playListID;
    String playName;
    Fragment frag;
    FragmentTransaction ft;
    ProgressDialog mProgressDialog;
    FragmentManager fm;
    RecyclerView MyPlayListSongs;
    RecyclerView.LayoutManager layoutManager;
    PlayListSongsAdapter dataAdapter;
    ArrayList<com.djgeraldo.data.MyPlayListSongs> data = new ArrayList<>();
    String androidId;
    boolean isConnected;
    SharedPreferences preferences;
    String mainTag;
    MediaPlayer radioPlayer;

    public PlaylistSongs() {
        // Required empty public constructor
    }

    @SuppressLint("ValidFragment")
//    context, playListID, playName,mainLayout
    public PlaylistSongs(Context context, RelativeLayout header, int playListID, String playName)
    {
        this.context = context;
        this.header = header;
        this.playListID = playListID;
        this.playName = playName;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_playlist_songs, container, false);

        preferences = context.getSharedPreferences("MyAndroidId",Context.MODE_PRIVATE);
        androidId = preferences.getString("androidId","");
        isConnected = preferences.getBoolean("isConnected",false);
        Log.d("MyPlayListSongs","Android id: "+androidId);
        Log.d("MyPlayListSongs","Net is connected: "+isConnected);
        MyPlayListSongs = (RecyclerView)v.findViewById(R.id.MyPlayListSongs);
        layoutManager = new LinearLayoutManager(context);
        MyPlayListSongs.setLayoutManager(layoutManager);
        tv_title = header.findViewById(R.id.titleHeader);
        tv_title.setText(playName);
        tv_title.setSelected(true);
        header.setTag("inner");
        CustomHeader.setInnerFragment(getActivity(),header);
//        iv_back = header.findViewById(R.id.btnback);
//        iv_player = header.findViewById(R.id.btnplayer);
//        iv_back.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                MainActivity.getMenuFragment()
//            frag = new MyPlaylist(context,header);
//            fm =((MainActivity)context).getSupportFragmentManager();
//            ft = fm.beginTransaction();
//            ft.replace(R.id.fragmentContainer, frag);
//            ft.addToBackStack(null);
//            ft.commit();
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
            object.put("playlist_id",playListID);
            object.put("u_id",androidId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (CheckNetwork.isInternetAvailable(context))
        {
            data.clear();
            new pListDetailsAsyncTask(object).execute();
        }
        else
        {
            Toast.makeText(context,"No Internet Connection",Toast.LENGTH_SHORT).show();
        }
        return v;
    }

    public void songLoaded(String url) {
        Log.d("MyPlaylist", "Url is:" + url);
        for (MyPlayListSongs s1 : data) {
            if (s1.getS_url().equals(url)) {
                s1.setNowPlaying(1);
            } else {
                s1.setNowPlaying(0);
            }
            PlayListSongsAdapter dataAdapter = new PlayListSongsAdapter(context,header, data);
            MyPlayListSongs.setAdapter(dataAdapter);
        }
    }




    //   http://durisimomobileapps.net/djsisko/api/playlist/getsongs
    private class pListDetailsAsyncTask extends AsyncTask<String,Void,String>
    {
        final PojoSongForPlayer playing = MediaController.getInstance().getPlayingSongDetail();
        JSONObject jsonObject;
        private pListDetailsAsyncTask(JSONObject jsonObject){
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
        protected String doInBackground(String... strings) {
            String result = null;
            try {
                MediaType JSON = MediaType.parse("application/json; charset=utf-8");
//                String url = "http://durisimomobileapps.net/djtico/api/user/playlist_songslist";
                String url = "http://durisimomobileapps.net/djgeraldo/api/user/playlist_songslist";
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
        protected void onPostExecute(String s)
        {
            super.onPostExecute(s);
            mProgressDialog.dismiss();
            Log.d("mytag", "PlayList Details Response : " + s);
            if (s == null) {
                Log.d("mytag", "Bookings Response is null");
            } else {
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    int status = jsonObject.getInt("status");
                    Log.d("status is:", "" + status);
                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject sipIdJson = jsonArray.getJSONObject(i);
                        int sipId = sipIdJson.getInt("sip_id");
                        Log.d("PlayList", "sip_Id is:" + sipId);
                        JSONObject idJson = jsonArray.getJSONObject(i);
                        int id = idJson.getInt("song_id");
                        Log.d("Song id is", "" + id);
                        JSONObject songNameJson = jsonArray.getJSONObject(i);
                        String song = songNameJson.getString("song_name");
//                    Log.d("Song name is", song);
                        Log.d("mytag", "Song Name IS " + song);
                        JSONObject artistJson = jsonArray.getJSONObject(i);
                        String artist = artistJson.getString("artist_name");
                        Log.d("mytag", "artistApi IS " + artist);
                        JSONObject imageJson = jsonArray.getJSONObject(i);
                        String image = imageJson.getString("image");
                        Log.d("playlist songs", "Image is: "+image);
                        JSONObject durationJson = jsonArray.getJSONObject(i);
                        String duration = durationJson.getString("song_time");
                        Log.d("Duration is", duration);
                        JSONObject songUrlJson = jsonArray.getJSONObject(i);
                        String songUrl = songUrlJson.getString("song_url");
                        Log.d("Song Url is", songUrl);
                        JSONObject likeNoJson = jsonArray.getJSONObject(i);
                        int likes = likeNoJson.getInt("like_status");
                        Log.d("like status is", "" + likes);
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
                        Log.d("MyPlaylist","NowPlaying: "+nowPlaying);
//                        songName,songDuration,songUrl,songImage,artistApi,songId,likeStatus,total,nowPlaying
                        data.add(new MyPlayListSongs(sipId, song, duration, songUrl, image, artist, id, likes, total, nowPlaying));
//                        data.add(new Song(id, image, song, artistApi, songUrl, duration, likes,total, status, 0, sipId, 0,nowPlaying));
                    }
                    try {
                        dataAdapter = new PlayListSongsAdapter(context,header, data);
                        MyPlayListSongs.setAdapter(dataAdapter);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            System.out.println(s);
        }
    }


}
