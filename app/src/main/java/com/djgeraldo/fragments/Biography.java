package com.djgeraldo.fragments;


import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.djgeraldo.R;
import com.djgeraldo.custom.CustomHeader;
import com.djgeraldo.phonemidea.CheckNetwork;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class Biography extends Fragment {

    Context context;
    RelativeLayout header;
    TextView tv_title;
    SharedPreferences preferences;
    String androidId;
    boolean isConnected;
    ProgressDialog mProgressDialog;
    ImageView iv_imageBio;
    TextView tv_textBio;
    ScrollView scr;

    public Biography() {
        // Required empty public constructor
    }

    @SuppressLint("ValidFragment")
    public Biography(Context context, RelativeLayout header)
    {
        this.context = context;
        this.header = header;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_biography, container, false);
        header.setTag("inner");
        CustomHeader.setInnerFragment(getActivity(),header);
        scr = (ScrollView)v.findViewById(R.id.scr);
        scr.setSmoothScrollingEnabled(true);
        iv_imageBio = v.findViewById(R.id.imageBio);
        tv_textBio = v.findViewById(R.id.textBio);
        tv_title = header.findViewById(R.id.titleHeader);
        tv_title.setText("Biography");
        tv_title.setSelected(true);

        preferences = context.getSharedPreferences("MyAndroidId",Context.MODE_PRIVATE);
        androidId = preferences.getString("androidId","");
        isConnected = preferences.getBoolean("isConnected",false);
        Log.d("bio","Android id: "+androidId);
        Log.d("bio","Net is connected: "+isConnected);
        if (CheckNetwork.isInternetAvailable(context))
        {
            new BioGraphyAsync().execute();
        }
        else
        {
            Toast.makeText(context,"No Internet Connection",Toast.LENGTH_SHORT).show();
        }
        return v;

    }


    //    http://durisimomobileapps.net/djsisko/api/notifications
    private class BioGraphyAsync extends AsyncTask<String,Void,String>
    {
        private JSONObject jsonObject;
        public BioGraphyAsync() {
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
//                String url = "http://durisimomobileapps.net/djtico/api/biography";
                String url = "http://durisimomobileapps.net/djgeraldo/api/biography";
                OkHttpClient client = new OkHttpClient();
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
            Log.d("bio", "Response is:" + s);
            if (s == null) {
                Log.d("mytag", "bio Response is null");
            } else {
                try
                {
                    JSONObject jsonObject = new JSONObject(s);
                    JSONObject dataJson = jsonObject.getJSONObject("data");
                    String image = dataJson.getString("bio_image");
                    String text = dataJson.getString("text");
                    Spanned result = Html.fromHtml(text);
                    tv_textBio.setText(result);
                    Glide.with(context)
                            .load(image)
                            .placeholder(R.drawable.dj_garelado_man_white)
                            .into(iv_imageBio);
                } catch(JSONException e){
                    e.printStackTrace();
                }
            }
            System.out.println(s);
        }
    }



}
