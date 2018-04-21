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
import com.djgeraldo.adapter.ArtistDataAdapter;
import com.djgeraldo.custom.CustomHeader;
import com.djgeraldo.data.BaseUrlApi;
import com.djgeraldo.data.DjList;
import com.djgeraldo.data.artist.artistApi;
import com.djgeraldo.data.artist.artistList;
import com.djgeraldo.phonemidea.CheckNetwork;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * A simple {@link Fragment} subclass.
 */
public class ArtistFragment extends Fragment {

    Context context;
    RelativeLayout header;
    ImageView iv_back,iv_player;
    TextView tv_title;
    ArrayList<DjList> data = new ArrayList<>();
    ArtistDataAdapter dataAdapter;
    RecyclerView artistRecycler;
    ProgressDialog mProgressDialog;
    SharedPreferences preferences;
    String androidId;
    boolean isConnected;


    public ArtistFragment() {
        // Required empty public constructor
    }

    @SuppressLint("ValidFragment")
    public ArtistFragment(Context context,RelativeLayout header)
    {
        this.context = context;
        this.header = header;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_artist, container, false);
        artistRecycler = (RecyclerView)v.findViewById(R.id.artistRecycler);
        artistRecycler.setLayoutManager(new LinearLayoutManager(context));
        tv_title = header.findViewById(R.id.titleHeader);
        tv_title.setText("Artist");
        tv_title.setSelected(true);
        header.setTag("outer");
        CustomHeader.setOuterFragment(getActivity(),header);
        preferences = context.getSharedPreferences("MyAndroidId",Context.MODE_PRIVATE);
        androidId = preferences.getString("androidId","");
        isConnected = preferences.getBoolean("isConnected",false);
        Log.d("artistApi","Android id: "+androidId);
        Log.d("artistApi","Net is connected: "+isConnected);
        iv_back = header.findViewById(R.id.btnback);
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
        if (CheckNetwork.isInternetAvailable(context))
        {
            data.clear();
            new ArtistAsyncTask().execute();
        }
        else
        {
            Toast.makeText(getContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
        }
        return v;
    }

    public void apiCall()
    {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BaseUrlApi.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        artistApi api = retrofit.create(artistApi.class);
        Call<artistList> call = api.getArtist();
        mProgressDialog = new ProgressDialog(context);
        mProgressDialog.setMessage("Loading");
        mProgressDialog.setCanceledOnTouchOutside(true);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setCancelable(true);
        mProgressDialog.show();

        call.enqueue(new Callback<artistList>() {
            @Override
            public void onResponse(Call<artistList> call, Response<artistList> response) {
                mProgressDialog.dismiss();
//                List<>
            }

            @Override
            public void onFailure(Call<artistList> call, Throwable t) {

            }
        });
    }

    //    http://durisimomobileapps.net/djtico/api/artist
    private class ArtistAsyncTask extends AsyncTask<String,Void,String>
    {
    public ArtistAsyncTask()
    {    }

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
//            String url = "http://durisimomobileapps.net/djtico/api/artist";
            String url = "http://durisimomobileapps.net/djgeraldo/api/artist";
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(url)
                    .build();
            okhttp3.Response response = client.newCall(request).execute();
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
        Log.d("Artist","Response"+s);
        if (s == null) {
            Log.d("mytag","Artist Response is null");
        }
        else {
            try {

                JSONObject jsonObject = new JSONObject(s);
                JSONArray jsonArray = jsonObject.getJSONArray("data");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject featuredIdJson = jsonArray.getJSONObject(i);
                    int artist_id = featuredIdJson.getInt("artist_id");
                    Log.d("Artist", "id is" + artist_id);
                    JSONObject nameJson = jsonArray.getJSONObject(i);
                    String name = nameJson.getString("artist_name");
                    Log.d("Artist", "name is" + name);
                    JSONObject imageJson = jsonArray.getJSONObject(i);
                    String image = imageJson.getString("image");
                    Log.d("Artist", "image is" + image);
                    JSONObject instaJson = jsonArray.getJSONObject(i);
                    String instaUrl = instaJson.getString("insta_url");
                    Log.d("Artist", "insta Url is" + instaUrl);
                    data.add(new DjList(artist_id,image,name,instaUrl));
                }
                try {
                    dataAdapter = new ArtistDataAdapter(context,header, data);
                    artistRecycler.setAdapter(dataAdapter);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}
}
