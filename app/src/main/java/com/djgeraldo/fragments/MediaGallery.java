package com.djgeraldo.fragments;


import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
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
import com.djgeraldo.adapter.ImageGalleryDataAdapter;
import com.djgeraldo.custom.CustomHeader;
import com.djgeraldo.data.GalleryModel;
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
public class MediaGallery extends Fragment {

    ArrayList<GalleryModel> data = new ArrayList<>();
    Context context;
    RelativeLayout header;
    ImageView iv_back,iv_player;
    TextView tv_title;
    ProgressDialog mProgressDialog;
    ImageGalleryDataAdapter dataAdapter;
    RecyclerView galleryRecycler;
    String androidId;
    boolean isConnected;
    SharedPreferences preferences;

    public MediaGallery() {
        // Required empty public constructor
    }


    @SuppressLint("ValidFragment")
    public MediaGallery(Context context, RelativeLayout header)
    {
        this.context = context;
        this.header = header;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_events_gallery, container, false);
        tv_title = header.findViewById(R.id.titleHeader);
        tv_title.setText("Media Gallery");
        tv_title.setSelected(true);
        header.setTag("inner");
        CustomHeader.setInnerFragment(getActivity(),header);
//        iv_back = header.findViewById(R.id.btnback);
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
        galleryRecycler = v.findViewById(R.id.galleryRecycler);
        preferences = context.getSharedPreferences("MyAndroidId",Context.MODE_PRIVATE);
        androidId = preferences.getString("androidId","");
        isConnected = preferences.getBoolean("isConnected",false);
        Log.d("gallery","Android id: "+androidId);
        Log.d("gallery","Net is connected: "+isConnected);
        if (CheckNetwork.isInternetAvailable(context))
        {
            data.clear();
            new ImageGalleryAsyncTask().execute();
        }
        else
        {
            Toast.makeText(context,"No Internet Connection",Toast.LENGTH_SHORT).show();
        }
        return v;
    }

    //    http://durisimomobileapps.net/djsisko/api/folders
    public class ImageGalleryAsyncTask extends AsyncTask<String, Void, String> {


        public ImageGalleryAsyncTask() {
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
//                String url = "http://durisimomobileapps.net/djtico/api/image_folder";
                String url = "http://durisimomobileapps.net/djgeraldo/api/image_folder";
                OkHttpClient client = new OkHttpClient();
//                RequestBody body = RequestBody.create(JSON, String.valueOf(jsonObject));
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
            Log.d("mytag", "REsponse Gallery : " + s);
            if (s == null) {
                Log.d("mytag", "Gallery Response is null");
            } else {
                try
                {
                    JSONObject jsonObject = new JSONObject(s);
                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject idJson = jsonArray.getJSONObject(i);
                        int id = idJson.getInt("folder_id");
                        Log.d("Folder id is", "" + id);
                        JSONObject folderImageJson = jsonArray.getJSONObject(i);
                        String image = folderImageJson.getString("folder_image");
                        Log.d("Folder image is", "" + image);
                        JSONObject folderTitleJson = jsonArray.getJSONObject(i);
                        String folderTitle = folderTitleJson.getString("folder_name");
                        Log.d("Folder title is", "" + folderTitle);
                        data.add(new GalleryModel(id, image, folderTitle));
                    }
                    try {
                        dataAdapter = new ImageGalleryDataAdapter(context,header,data);
                        galleryRecycler.setLayoutManager(new GridLayoutManager(context, 2));
                        galleryRecycler.setAdapter(dataAdapter);
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
