package com.djgeraldo.fragments;


import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.djgeraldo.R;
import com.djgeraldo.custom.CustomHeader;
import com.djgeraldo.phonemidea.CheckNetwork;

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
public class ShareMyApp extends Fragment {

    Context context;
    ProgressDialog mProgressDialog;
    String androidId;
    boolean isConnected;
    SharedPreferences preferences;
    RelativeLayout header;
    ImageView iv_back,iv_player;
    TextView tv_title;
    ImageView iv_ShareApp;
    String androidLink;
    ArrayList<String> data = new ArrayList<>();
    String msg,finalLink;

    public ShareMyApp() {
        // Required empty public constructor
    }

    @SuppressLint("ValidFragment")
    public ShareMyApp(Context context, RelativeLayout header)
    {
        this.context = context;
        this.header = header;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_share_my_app, container, false);
        iv_ShareApp = (ImageView)v.findViewById(R.id.shareApp);
        tv_title = header.findViewById(R.id.titleHeader);
        tv_title.setText("Share My App");
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
        preferences = context.getSharedPreferences("MyAndroidId",Context.MODE_PRIVATE);
        androidId = preferences.getString("androidId","");
        isConnected = preferences.getBoolean("isConnected",false);
        Log.d("ShareMyApp","Android id: "+androidId);
        Log.d("ShareMyApp","Net is connected: "+isConnected);
        if (CheckNetwork.isInternetAvailable(context)){
            data.clear();
            new sharemyAsyncTask().execute();
        }
        else
        {
            Toast.makeText(context,"No Internet Connection",Toast.LENGTH_SHORT).show();
        }
        iv_ShareApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shareLink();
            }

        });
        return v;
    }


    public void shareLink()
    {
        Intent share = new Intent(android.content.Intent.ACTION_SEND);
        share.setType("text/plain");
        share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);

        share.putExtra(Intent.EXTRA_TEXT,finalLink);
        startActivity(Intent.createChooser(share, "Share link!"));
    }

    // http://durisimomobileapps.net/djtico/api/share
    private class sharemyAsyncTask extends AsyncTask<String,Void,String>
    {
        private JSONObject jsonObject;
        public sharemyAsyncTask() {}
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
//                String url = "http://durisimomobileapps.net/djtico/api/share";
                String url = "http://durisimomobileapps.net/djgeraldo/api/share";
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
            Log.d("Share My App", "Response is:" + s);
            if (s == null) {
                Log.d("mytag", "Share My App Response is null");
            } else {
                try
                {
                    JSONObject jsonObject = new JSONObject(s);
                    JSONObject jsonObject1 = jsonObject.getJSONObject("data");
                    int shareId = jsonObject1.getInt("share_id");
                    Log.d("Share My App", "Share Id is:" + shareId);
                     String alinks = jsonObject1.getString("android_link");
                    String iosLink = jsonObject1.getString("ios_link");

                    androidLink = jsonObject1.getString("android_link");
                    Log.d("Share My App", "Android Link:" + androidLink);
                    String text = jsonObject1.getString("text");
                    data.add(androidLink);
                    finalLink = text + "\n\n" + alinks + "\n\n" + iosLink ;
                    msg = text + "\n";

                } catch(JSONException e){
                    e.printStackTrace();
                }
            }
            System.out.println(s);
        }

    }


}
