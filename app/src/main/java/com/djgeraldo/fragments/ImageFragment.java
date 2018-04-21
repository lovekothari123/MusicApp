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
import com.djgeraldo.adapter.ImageDataAdapter;
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
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class ImageFragment extends Fragment {

    ArrayList<GalleryModel> data = new ArrayList<>();
    Context context;
    int id;
    String title;
    RelativeLayout header;
    RecyclerView imageRecycler;
    ImageView iv_back,iv_player;
    TextView tv_title;  Fragment frag;
    FragmentTransaction ft;
    FragmentManager fm;
    ImageDataAdapter imageDataAdapter;
    SharedPreferences preferences;
    String androidId;
    boolean isConnected;
    ProgressDialog mProgressDialog;

    public ImageFragment() {
        // Required empty public constructor
    }

    @SuppressLint("ValidFragment")
    public ImageFragment(Context context,RelativeLayout header, int id, String title)
    {
        this.context = context;
        this.header = header;
        this.id = id;
        this.title = title;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_image, container, false);
        preferences = context.getSharedPreferences("MyAndroidId",Context.MODE_PRIVATE);
        androidId = preferences.getString("androidId","");
        isConnected = preferences.getBoolean("isConnected",false);
        Log.d("ImageFragment","Android id: "+androidId);
        Log.d("ImageFragment","Net is connected: "+isConnected);

        imageRecycler = v.findViewById(R.id.imageRecycler);
        tv_title = header.findViewById(R.id.titleHeader);
        tv_title.setText(title);
        tv_title.setSelected(true);
        header.setTag("inner");
        CustomHeader.setInnerFragment(getActivity(),header);
//        iv_back = header.findViewById(R.id.btnback);
//        iv_player = header.findViewById(R.id.btnplayer);
//        iv_back.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                frag = new MediaGallery(context,header);
//                fm = ((MainActivity)context).getSupportFragmentManager();
//                ft = fm.beginTransaction();
//                ft.replace(R.id.fragmentContainer,frag);
//                ft.commit();
////                MainActivity.getMenuFragment();
//            }
//        });
//        iv_player.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                MainActivity.getPlayLayout();
//            }
//
//        });
        JSONObject object = new JSONObject();
        try {
            object.put("folder_id",id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (CheckNetwork.isInternetAvailable(context))
        {
            data.clear();
            new imageGalleryAsyncTask(object).execute();
        }
        else
        {
            Toast.makeText(context,"No Internet Connection",Toast.LENGTH_SHORT).show();
        }

        return v;
    }


    //    http://durisimomobileapps.net/dreamteam/api/folders/images
    private class imageGalleryAsyncTask extends AsyncTask<String,Void,String> {

    JSONObject object;

    private imageGalleryAsyncTask(JSONObject object)
    {
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
        try
        {
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
//            String url = "http://durisimomobileapps.net/djtico/api/gallery";
            String url = "http://durisimomobileapps.net/djgeraldo/api/gallery";
            OkHttpClient client = new OkHttpClient();
            RequestBody body = RequestBody.create(JSON, String.valueOf(object));
            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .build();
            Response response = client.newCall(request).execute();
            result = response.body().string();
            return result;
        } catch(
                Exception e)

        {
            return null;
        }
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        mProgressDialog.dismiss();
        Log.d("mytag", "Images inside gallery Response : " + s);
        if (s == null) {
            Log.d("mytag", "Bookings Response is null");
        } else {
            try {
                JSONObject jsonObject = new JSONObject(s);
                JSONArray jsonArray = jsonObject.getJSONArray("data");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject galleryIdJson = jsonArray.getJSONObject(i);
                    int galleryId = galleryIdJson.getInt("gallery_id");
                    Log.d("Gallery Id is", "" + galleryId);
                    JSONObject imageJson = jsonArray.getJSONObject(i);
                    String image = imageJson.getString("image");
                    Log.d("Image is:", image);
                    data.add(new GalleryModel(galleryId, image));
//                    dataModules.add(new Song(1,"http:\\/\\/durisimomobileapps.net\\/djsisko\\/core\\/public\\/storage\\/uploads\\/img3_1513072333.png"));
                }
                try {
                    imageDataAdapter = new ImageDataAdapter(context,data);
                    imageRecycler.setLayoutManager(new GridLayoutManager(context, 3));
                    imageRecycler.setAdapter(imageDataAdapter);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            catch(JSONException e){
                e.printStackTrace();
            }
        }
        System.out.println(s);
    }

}

}
