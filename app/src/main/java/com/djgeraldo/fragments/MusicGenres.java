package com.djgeraldo.fragments;


import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
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
import com.djgeraldo.adapter.MusicGenreAdapter;
import com.djgeraldo.custom.CustomHeader;
import com.djgeraldo.data.BaseUrlApi;
import com.djgeraldo.data.Genre.GenreApi;
import com.djgeraldo.data.Genre.GenreData;
import com.djgeraldo.data.Genre.GenreList;
import com.djgeraldo.phonemidea.CheckNetwork;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * A simple {@link Fragment} subclass.
 */
public class MusicGenres extends Fragment {

    Context context;
    RelativeLayout header;
    ImageView iv_back,iv_player;
    TextView tv_title;
    ProgressDialog mProgressDialog;
    RecyclerView musicGenreRecycler;
    RecyclerView.LayoutManager layoutManager;
    List<GenreData> data = new ArrayList<>();
    MusicGenreAdapter musicGenreAdapter;
    SharedPreferences preferences;
    String androidId;
    boolean isConnected;
    boolean isOuter=false;
    boolean Count;

    public MusicGenres() {
        // Required empty public constructor
    }

    @SuppressLint("ValidFragment")
    public MusicGenres(Context context, RelativeLayout header,boolean isOuter,boolean Count)
    {
        this.context = context;
        this.header = header;
        this.isOuter=isOuter;
        this.Count=Count;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_music_genres, container, false);
        Log.d("mytag","outer is:"+isOuter);
        if(isOuter==true)
        {
            header.setTag("outer");
            CustomHeader.setOuterFragment(getActivity(),header);
        }
        else
        {
            header.setTag("inner");
            CustomHeader.setInnerFragment(getActivity(),header);
        }

        musicGenreRecycler = (RecyclerView)v.findViewById(R.id.musicGenreRecycler);
        musicGenreRecycler.setLayoutManager(new LinearLayoutManager(context));
//        header.setTag("inner");
//        CustomHeader.setInnerFragment(getActivity(),header);
        tv_title = header.findViewById(R.id.titleHeader);
        tv_title.setText("Music Genres");
        tv_title.setSelected(true);
        iv_back = header.findViewById(R.id.btnback);
        iv_player = header.findViewById(R.id.btnplayer);
        preferences = context.getSharedPreferences("MyAndroidId",Context.MODE_PRIVATE);
        androidId = preferences.getString("androidId","");
        isConnected = preferences.getBoolean("isConnected",false);
        Log.d("genre","Android id: "+androidId);
        Log.d("genre","Net is connected: "+isConnected);

        if(CheckNetwork.isInternetAvailable(context))
        {
            data.clear();
            apiCall();
//            new MusicGenreAsync().execute();
        }
        else
        {
            Toast.makeText(context,"No Internet Connection",Toast.LENGTH_SHORT).show();
        }
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
        return v;
    }



//    //    http://durisimomobileapps.net/djsisko/api/djs
//    private class MusicGenreAsync extends AsyncTask<String,Void,String>
//    {
//        public MusicGenreAsync()
//        {
//
//        }
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            mProgressDialog = new ProgressDialog(context);
//            mProgressDialog.setMessage("Loading");
//            mProgressDialog.setCanceledOnTouchOutside(true);
//            mProgressDialog.setIndeterminate(true);
//            mProgressDialog.setCancelable(true);
//            mProgressDialog.show();
//        }
//
//
//        @Override
//        protected String doInBackground(String... strings) {
//            String result = null;
//            try {
//                MediaType JSON = MediaType.parse("application/json; charset=utf-8");
//                String url = "http://durisimomobileapps.net/djtico/api/genre";
//                OkHttpClient client = new OkHttpClient();
//                Request request = new Request.Builder()
//                        .url(url)
//                        .build();
//                Response response = client.newCall(request).execute();
//                result = response.body().string();
//                return result;
//            } catch (Exception e) {
//                return null;
//            }
//        }
//
//        @Override
//        protected void onPostExecute(String s) {
//            super.onPostExecute(s);
//            mProgressDialog.dismiss();
//            Log.d("Music Genres","Response"+s);
//            if (s == null) {
//                Log.d("mytag","Genres Response is null");
//            }
//            else {
//                try {
//
//                    JSONObject jsonObject = new JSONObject(s);
//                    JSONArray jsonArray = jsonObject.getJSONArray("data");
//                    for (int i = 0; i < jsonArray.length(); i++) {
//                        JSONObject featuredIdJson = jsonArray.getJSONObject(i);
//                        int genres_id = featuredIdJson.getInt("genres_id");
//                        Log.d("Music Genres", "id is" + genres_id);
//                        JSONObject nameJson = jsonArray.getJSONObject(i);
//                        String genres_name = nameJson.getString("genres_name");
//                        Log.d("Music Genres", "name is" + genres_name);
//                        JSONObject imageJson = jsonArray.getJSONObject(i);
//                        String image = imageJson.getString("image");
//                        Log.d("Music Genres", "image is" + image);
//                        JSONObject totalJson = jsonArray.getJSONObject(i);
//                        int total_records = totalJson.getInt("total_records");
//                        Log.d("Music Genres","total genres is:"+total_records);
//                        data.add(new MusicGenreModel(genres_id, genres_name, image,total_records));
//                    }
//                    try {
//                        musicGenreAdapter = new MusicGenreAdapter(context, data,header);
//                        musicGenreRecycler.setAdapter(musicGenreAdapter);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//
//    }


    public void apiCall()
    {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BaseUrlApi.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        GenreApi api = retrofit.create(GenreApi.class);

        Call<GenreList> call = api.getGenres();
        mProgressDialog = new ProgressDialog(context);
        mProgressDialog.setMessage("Loading");
        mProgressDialog.setCanceledOnTouchOutside(true);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setCancelable(true);
        mProgressDialog.show();
        call.enqueue(new Callback<GenreList>() {
            @Override
            public void onResponse(Call<GenreList> call, retrofit2.Response<GenreList> response) {
                GenreList current = response.body();
                data = response.body().getData();
                mProgressDialog.dismiss();
//                String[] genreNames = new String[data.size()];
//                for(int i=0;i < data.size(); i++)
//                {
//                    genreNames[i] = data.get(i).getGenresName();
//                }
                musicGenreAdapter = new MusicGenreAdapter(context,data,header);
                musicGenreRecycler.setAdapter(musicGenreAdapter);
//                genreListView.setAdapter(new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_list_item_1,genreNames));
            }

            @Override
            public void onFailure(Call<GenreList> call, Throwable t) {
//                Toast.makeText(context,t.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });

    }



}
