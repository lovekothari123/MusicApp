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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.djgeraldo.R;
import com.djgeraldo.adapter.VideoDataAdapter;
import com.djgeraldo.custom.CustomHeader;
import com.djgeraldo.data.VideoData;
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
public class Videos extends Fragment {

    Context context;
    RelativeLayout header;
    ImageView iv_back,iv_player;
    TextView tv_title;
    ProgressDialog mProgressDialog;
    ArrayList<VideoData> videoData = new ArrayList<>();
    VideoDataAdapter dataAdapter;
    RecyclerView videoRecycler;
    LinearLayout adView;
    SharedPreferences preferences;
    String androidId;
    boolean isConnected;

    public Videos() {
        // Required empty public constructor
    }

    @SuppressLint("ValidFragment")
    public Videos(Context context, RelativeLayout header)
    {
        this.context = context;
        this.header = header;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_videos, container, false);
        preferences = context.getSharedPreferences("MyAndroidId",Context.MODE_PRIVATE);
        androidId = preferences.getString("androidId","");
        isConnected = preferences.getBoolean("isConnected",false);
        Log.d("Videos","Android id: "+androidId);
        Log.d("Vidoes","Net is connected: "+isConnected);
        videoRecycler = v.findViewById(R.id.videoRecycler);
        videoRecycler.setLayoutManager(new LinearLayoutManager(context));
        tv_title = header.findViewById(R.id.titleHeader);
        tv_title.setText("Videos");
        tv_title.setSelected(true);
        header.setTag("inner");
        CustomHeader.setInnerFragment(getActivity(),header);
        iv_back = header.findViewById(R.id.btnback);
        iv_player = header.findViewById(R.id.btnplayer);
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
        if (CheckNetwork.isInternetAvailable(context))
        {
            videoData.clear();
            new videosAsyncTask().execute();
        }
        else
        {
            Toast.makeText(context,"No Internet Connection",Toast.LENGTH_SHORT).show();
        }
        return v;
    }

//    http://durisimomobileapps.net/djtico/api/videos
private class videosAsyncTask extends AsyncTask<String,Void,String> {
    JSONObject object;
    private videosAsyncTask()
    {
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
        try
        {
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
//            String url = "http://durisimomobileapps.net/djtico/api/videos";
//            String url = "http://durisimomobileapps.net/djgeraldo/api/videos";
            String url = "http://durisimomobileapps.net/djgeraldo/api/videos";
            OkHttpClient client = new OkHttpClient();
            RequestBody body = RequestBody.create(JSON, String.valueOf(object));
            Request request = new Request.Builder()
                    .url(url)
                    .build();
            Response response = client.newCall(request).execute();
            result = response.body().string();
            return result;
        } catch(Exception e)
        {return null;}
    }


    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        mProgressDialog.dismiss();
        try {
            Log.d("mytag", "Videos Response : " + s);
            JSONObject jsonObject = new JSONObject(s);
            JSONArray jsonArray = jsonObject.getJSONArray("data");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject idJson = jsonArray.getJSONObject(i);
                int id = idJson.getInt("video_id");
                Log.d("Video id is", "" + id);
                JSONObject videoNameJson = jsonArray.getJSONObject(i);
                String vName = videoNameJson.getString("name");
                Log.d("mytag", "Video Name IS " + vName);
                JSONObject imageJson = jsonArray.getJSONObject(i);
                String image = imageJson.getString("image");
                Log.d("Video Image is", image);
                JSONObject dateJson = jsonArray.getJSONObject(i);
                String date = dateJson.getString("date");
                Log.d("Duration is", date);
                JSONObject videoLinkJson = jsonArray.getJSONObject(i);
                String videoLink = videoLinkJson.getString("video_link");
                Log.d("Video Fragment",videoLink);
//                int id,String name,String date,String image,String video_link
                videoData.add(new VideoData(id,vName,date,image,videoLink));
            }
            try {
                dataAdapter = new VideoDataAdapter(context,header, videoData,adView);
                videoRecycler.setAdapter(dataAdapter);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        System.out.println(s);
    }
}

}
