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
import com.djgeraldo.adapter.PlayListAdapter;
import com.djgeraldo.custom.CustomHeader;
import com.djgeraldo.data.PlayListModel;
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
public class MyPlaylist extends Fragment {

    Context context;
    RelativeLayout header;
    ImageView iv_back,iv_player;
    TextView tv_title;
    String title;
    RecyclerView myPlayListRecycler;
    RecyclerView.LayoutManager layoutManager;
    PlayListAdapter dataAdapter;
    String androidId;
    boolean isConnected;
    SharedPreferences preferences;
    ArrayList<PlayListModel> data = new ArrayList<>();
    ProgressDialog mProgressDialog;
    Fragment frag;
    FragmentTransaction ft;
    FragmentManager fm;
    String tag = MyPlaylist.class.getSimpleName();
    MediaPlayer radioPlayer;
    private Boolean isOuter = false;

    public MyPlaylist() {
        // Required empty public constructor
    }

    @SuppressLint("ValidFragment")
    public MyPlaylist(Context context, RelativeLayout header,Boolean isOuter)
    {
        this.context = context;
        this.header = header;
        this.isOuter=isOuter;
    }


    @SuppressLint("ValidFragment")
    public MyPlaylist(Context context, RelativeLayout header)
    {
        this.context = context;
        this.header = header;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_my_playlist, container, false);

        tv_title = header.findViewById(R.id.titleHeader);
        tv_title.setText("My Playlist");
        tv_title.setSelected(true);
        if(isOuter)
        {
            header.setTag("outer");
            CustomHeader.setOuterFragment(getActivity(),header);
        }
        else
        {
            header.setTag("inner");
            CustomHeader.setInnerFragment(getActivity(),header);
        }

//        iv_back = header.findViewById(R.id.btnback);
//        iv_player = header.findViewById(R.id.btnplayer);
//        iv_back.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                MainActivity.getMenuFragment();
//
//            }
//        });
//        iv_player.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                MainActivity.getPlayLayout();
//            }
//        });
//        header.setTag("inner");
//        CustomHeader.setInnerFragment(getActivity(),header);
        myPlayListRecycler = (RecyclerView)v.findViewById(R.id.myPlayListRecycler);
        layoutManager = new LinearLayoutManager(context);
        myPlayListRecycler.setLayoutManager(layoutManager);
        preferences = context.getSharedPreferences("MyAndroidId",Context.MODE_PRIVATE);
        androidId = preferences.getString("androidId","");
        isConnected = preferences.getBoolean("isConnected",false);
        Log.d("MyPlayList","Android id: "+androidId);
        Log.d("MyPlayList","Net is connected: "+isConnected);
        JSONObject object = new JSONObject();
        try {
            object.put("u_id",androidId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (CheckNetwork.isInternetAvailable(context))
        {
            data.clear();
            new MyPlayListAsyncTask(object).execute();
        }
        else
        {
            Toast.makeText(context,"No Internet Connection",Toast.LENGTH_SHORT).show();
        }
        return v;
    }

    //    http://durisimomobileapps.net/djsisko/api/playlist/user
    public class MyPlayListAsyncTask extends AsyncTask<String, Void, String> {

        private JSONObject jsonObject;

        public MyPlayListAsyncTask(JSONObject object) {
            this.jsonObject = object;
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
//                String url = "http://durisimomobileapps.net/djtico/api/user/user_playlist";
                String url = "http://durisimomobileapps.net/djgeraldo/api/user/user_playlist";
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
            Log.d("mytag", "MyPlayList Response : " + s);
            if (s == null) {
                Log.d("mytag", "MyPlaylist Response is null");
            } else {
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    int status = jsonObject.getInt("status");
                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject idJson = jsonArray.getJSONObject(i);
                        int playListId = idJson.getInt("playlist_id");
                        Log.d("MyPlayList id is:", "" + playListId);
                        JSONObject userID = jsonArray.getJSONObject(i);
                        String userId = userID.getString("u_id");
                        Log.d("MyPlayList userId is:", userId);
                        JSONObject nameJson = jsonArray.getJSONObject(i);
                        String playListName = nameJson.getString("name");
                        Log.d("MyPlayList Name is:", playListName);
                        JSONObject totalCountJson = jsonArray.getJSONObject(i);
                        int totalCount = totalCountJson.getInt("total_records");
                        Log.d("MyPlayList totalCount", "" + totalCount);
//                int songId,String songTitle,int totalCount
                        data.add(new PlayListModel(playListId, playListName, totalCount));
                        dataAdapter = new PlayListAdapter(context,header, data, playListId);
                        myPlayListRecycler.setAdapter(dataAdapter);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
            System.out.println(s);
        }
    }

}
