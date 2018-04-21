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
import com.djgeraldo.adapter.DjDataAdapter;
import com.djgeraldo.custom.CustomHeader;
import com.djgeraldo.data.BaseUrlApi;
import com.djgeraldo.data.Dj.Dj;
import com.djgeraldo.data.Dj.DjApi;
import com.djgeraldo.data.Dj.DjData;
import com.djgeraldo.data.PojoSongForPlayer;
import com.djgeraldo.manager.MediaController;
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
public class FeaturedDjFragment extends Fragment {

    Context context;
    RelativeLayout header;
    ImageView iv_back,iv_player;
    TextView tv_title;
    List<DjData> data = new ArrayList<>();
    DjDataAdapter dataAdapter;
    RecyclerView djRecycler;
    ProgressDialog mProgressDialog;

    SharedPreferences preferences;
    String androidId;
    boolean isConnected;

    public FeaturedDjFragment() {
        // Required empty public constructor
    }


    @SuppressLint("ValidFragment")
    public FeaturedDjFragment(Context context,RelativeLayout header)
    {
        this.context = context;
        this.header = header;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_featured_dj, container, false);
        djRecycler = (RecyclerView)v.findViewById(R.id.djRecycler);
        djRecycler.setLayoutManager(new LinearLayoutManager(context));
        tv_title = header.findViewById(R.id.titleHeader);
        tv_title.setText("Featured Dj");
        tv_title.setSelected(true);
        iv_back = header.findViewById(R.id.btnback);
        header.setTag("outer");
        CustomHeader.setOuterFragment(getActivity(),header);
//        iv_player = header.findViewById(R.id.btnplayer);
//        iv_back.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                MainActivity.getPlayLayout();
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

        preferences = context.getSharedPreferences("MyAndroidId",Context.MODE_PRIVATE);
        androidId = preferences.getString("androidId","");
        isConnected = preferences.getBoolean("isConnected",false);
        Log.d("dj","Android id: "+androidId);
        Log.d("dj","Net is connected: "+isConnected);
        if (CheckNetwork.isInternetAvailable(context)) {
            data.clear();
            apiCall();
//            new DjsAsyncTask().execute();
        }
        else
        {
            Toast.makeText(getContext(),"No Internet Connection",Toast.LENGTH_SHORT).show();
        }

        return v;
    }


    public void apiCall()
    {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BaseUrlApi.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        DjApi api = retrofit.create(DjApi.class);

        Call<Dj> call = api.getDjs();
        mProgressDialog = new ProgressDialog(context);
        mProgressDialog.setMessage("Loading");
        mProgressDialog.setCanceledOnTouchOutside(true);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setCancelable(true);
        mProgressDialog.show();

        call.enqueue(new Callback<Dj>() {
            @Override
            public void onResponse(Call<Dj> call, retrofit2.Response<Dj> response) {
                Dj current = response.body();
                data = response.body().getData();
                dataAdapter = new DjDataAdapter(context,header, data);
                djRecycler.setAdapter(dataAdapter);
                mProgressDialog.dismiss();
                PojoSongForPlayer  song = MediaController.getInstance().getPlayingSongDetail();
                if (song != null){
                Log.d("DJ","Current: "+song.getS_name());}
            }

            @Override
            public void onFailure(Call<Dj> call, Throwable t) {
                Toast.makeText(context,t.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });


    }






//    //    http://durisimomobileapps.net/djtico/api/dj
//    private class DjsAsyncTask extends AsyncTask<String,Void,String>
//    {
//        public DjsAsyncTask()
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
//                String url = "http://durisimomobileapps.net/djtico/api/dj";
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
//            Log.d("Featured Dj","Response"+s);
//            if (s == null) {
//                Log.d("mytag","Featured Dj Response is null");
//            }
//            else {
//                try {
//
//                    JSONObject jsonObject = new JSONObject(s);
//                    JSONArray jsonArray = jsonObject.getJSONArray("data");
//                    for (int i = 0; i < jsonArray.length(); i++) {
//                        JSONObject featuredIdJson = jsonArray.getJSONObject(i);
//                        int featuredId = featuredIdJson.getInt("feature_dj_id");
//                        Log.d("Featured Dj", "id is" + featuredId);
//                        JSONObject nameJson = jsonArray.getJSONObject(i);
//                        String name = nameJson.getString("name");
//                        Log.d("Featured Dj", "name is" + name);
//                        JSONObject imageJson = jsonArray.getJSONObject(i);
//                        String image = imageJson.getString("image");
//                        Log.d("Featured Dj", "image is" + image);
//                        JSONObject instaJson = jsonArray.getJSONObject(i);
//                        String instaUrl = instaJson.getString("insta_url");
//                        Log.d("Featured Dj", "insta Url is" + instaUrl);
//                        data.add(new DjList(featuredId,image,name,instaUrl));
//                    }
//                    try {
//                        dataAdapter = new DjDataAdapter(context,header, data);
//                        djRecycler.setAdapter(dataAdapter);
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

}
