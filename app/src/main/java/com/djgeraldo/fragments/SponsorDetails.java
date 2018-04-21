package com.djgeraldo.fragments;


import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.djgeraldo.R;
import com.djgeraldo.adapter.SponsorImageAdapter;
import com.djgeraldo.custom.CustomHeader;
import com.djgeraldo.data.sponsors;
import com.djgeraldo.phonemidea.CheckNetwork;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

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
public class SponsorDetails extends Fragment {

    Context context;
    RelativeLayout header;
    TextView tv_title;
    String titleStr;
    int id;
    SharedPreferences preferences;
    String androidId;
    boolean isConnected;
    String sponsorName, sponsorAdd,description,logo;
    TextView title,address,descriptionData;
    ArrayList<sponsors> imagesData = new ArrayList<>();
    RecyclerView detailsRecycler;
    RecyclerView.LayoutManager layoutManager;
    ImageView logoImage;SponsorImageAdapter dataAdapter;
    NestedScrollView scrollview;
    ProgressDialog mProgressDialog;
    DisplayImageOptions options;


    public SponsorDetails() {
        // Required empty public constructor
    }

    @SuppressLint("ValidFragment")
    public SponsorDetails(Context context, RelativeLayout header,int id,String titleStr)
    {
        this.context = context;
        this.header = header;
        this.id = id;
        this.titleStr = titleStr;
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.dj_gradlo_app_logo)
                .showImageForEmptyUri(R.drawable.dj_gradlo_app_logo)
                .showImageOnFail(R.drawable.dj_gradlo_app_logo)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_sponsor_details, container, false);
        scrollview = v.findViewById(R.id.scrollview);
        scrollview.setSmoothScrollingEnabled(true);
        title = v.findViewById(R.id.title);
        address = v.findViewById(R.id.address);
        descriptionData = v.findViewById(R.id.descriptionData);
        logoImage = v.findViewById(R.id.logo);
        detailsRecycler = v.findViewById(R.id.detailsRecycler);
        detailsRecycler.setNestedScrollingEnabled(false);
        detailsRecycler.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(context);
        detailsRecycler.setLayoutManager(layoutManager);
        header.setTag("inner");
        CustomHeader.setInnerFragment(getActivity(),header);
        tv_title = header.findViewById(R.id.titleHeader);
        tv_title.setText(titleStr);
        tv_title.setSelected(true);
        preferences = context.getSharedPreferences("MyAndroidId",Context.MODE_PRIVATE);
        androidId = preferences.getString("androidId","");
        isConnected = preferences.getBoolean("isConnected",false);
        Log.d("sponsor details","Android id: "+androidId);
        Log.d("sponsor details","Net is connected: "+isConnected);
        JSONObject object = new JSONObject();
        try {
            object.put("sponsor_id",id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (CheckNetwork.isInternetAvailable(context)){
            new detailsAsync(object).execute();}

            else
        {
            Toast.makeText(context,"No Internet Connection",Toast.LENGTH_SHORT).show();
        }

        return v;
    }

    //http://durisimomobileapps.net/djtico/api/sponsors/sponsors_detail
    private class detailsAsync extends AsyncTask<String,Void,String>
    {
        JSONObject jsonObject;
        public detailsAsync(JSONObject jsonObject)
        {
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
            try
            {
                MediaType JSON = MediaType.parse("application/json; charset=utf-8");
//                String url = "http://durisimomobileapps.net/djtico/api/sponsors/sponsors_detail";
                String url = "http://durisimomobileapps.net/djgeraldo/api/sponsors/sponsors_detail";
                OkHttpClient client = new OkHttpClient();
                RequestBody body = RequestBody.create(JSON, String.valueOf(jsonObject));
                Request request = new Request.Builder()
                        .url(url)
                        .post(body)
                        .build();
                Response response = client.newCall(request).execute();
                result = response.body().string();
                return result;
            } catch(Exception e)
            {
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            mProgressDialog.dismiss();
            Log.d("Sponsors Details","Response is:"+s);
                try {
                    JSONObject object = new JSONObject(s);
                    JSONObject data = object.getJSONObject("data");
                    sponsorName = data.getString("sponsor_name");
                    Log.d("SponsorDetails","name is:"+sponsorName);
                    sponsorAdd = data.getString("sponsor_address");
                    Log.d("SponsorDetails","address is:"+sponsorAdd);
                    description = data.getString("sponsor_description");
                    Log.d("SponsorDetails","description is:"+description);
                    logo = data.getString("sponsor_logo");
                    Log.d("SponsorDetails","logo is:"+logo);
                    title.setText(sponsorName);
                    Spanned htmlAsSpanned = Html.fromHtml(description);
                    descriptionData.setText(htmlAsSpanned);
                    address.setText(sponsorAdd);
//                    Glide.with(context)
//                            .load(logo)
//                            .placeholder(R.drawable.appicone512)
//                            .into(logoImage);
                    ImageLoader.getInstance().displayImage(logo,logoImage,options);
                    JSONArray images = object.getJSONArray("inner_images");
                    for (int i = 0; i < images.length();i++)
                    {
                        String image = images.getString(i);
                        Log.d("SponsorsDetails","Image is:"+image);
                        imagesData.add(new sponsors(image));
                    }
                    dataAdapter = new SponsorImageAdapter(context,imagesData);
                    detailsRecycler.setAdapter(dataAdapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

    }


}
