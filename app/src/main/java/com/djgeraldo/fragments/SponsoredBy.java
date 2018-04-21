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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.djgeraldo.R;
import com.djgeraldo.adapter.SponsorAdapter;
import com.djgeraldo.custom.CustomHeader;
import com.djgeraldo.data.sponsors;
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
public class SponsoredBy extends Fragment {

    Context context;
    RelativeLayout header;
    TextView tv_title;
    SharedPreferences preferences;
    String androidId;
    boolean isConnected;
    ArrayList<sponsors> data = new ArrayList<>();
    SponsorAdapter dataAdapter;
    RecyclerView sponsorRecycler;
    ProgressDialog mProgressDialog;

    public SponsoredBy() {
        // Required empty public constructor
    }

    @SuppressLint("ValidFragment")
     SponsoredBy(Context context, RelativeLayout header)
    {
        this.context = context;
        this.header = header;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_sponsored_by, container, false);
        sponsorRecycler = v.findViewById(R.id.sponsorRecycler);
        sponsorRecycler.setLayoutManager(new LinearLayoutManager(context));
        header.setTag("inner");
        CustomHeader.setInnerFragment(getActivity(),header);
        tv_title = header.findViewById(R.id.titleHeader);
        tv_title.setText("Sponsored By");
        tv_title.setSelected(true);
        preferences = context.getSharedPreferences("MyAndroidId",Context.MODE_PRIVATE);
        androidId = preferences.getString("androidId","");
        isConnected = preferences.getBoolean("isConnected",false);
        Log.d("Sponsors","Android id: "+androidId);
        Log.d("Sponsors","Net is connected: "+isConnected);
        if (CheckNetwork.isInternetAvailable(context))
        {
            data.clear();
            new sponsorsAsync().execute();
        }
        else
        {
            Toast.makeText(context,"No Internet Connection",Toast.LENGTH_SHORT).show();
        }
        return v;
    }

    //http://durisimomobileapps.net/djtico/api/sponsors
    private class sponsorsAsync extends AsyncTask<String,Void,String>
    {
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
//                String url = "http://durisimomobileapps.net/djtico/api/sponsors";
                String url = "http://durisimomobileapps.net/djgeraldo/api/sponsors";
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
            Log.d("mytag", "Sponsors Response : " + s);
            try {
                JSONObject jsonObject = new JSONObject(s);
                JSONArray jsonArray = jsonObject.getJSONArray("data");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject idJson = jsonArray.getJSONObject(i);
                    int id = idJson.getInt("sponsor_id");
                    Log.d("Sponsors","Id is:"+id);
                    JSONObject nameJson = jsonArray.getJSONObject(i);
                    String name = nameJson.getString("sponsor_name");
                    Log.d("Sponsors","Name is:"+name);
                    JSONObject descrJson = jsonArray.getJSONObject(i);
                    String descr = descrJson.getString("sponsor_description");
                    Log.d("Sponsors","description is:"+descr);
                    JSONObject addrJson = jsonArray.getJSONObject(i);
                    String address = addrJson.getString("sponsor_address");
                    Log.d("Sponsors","Address is:"+address);
                    JSONObject logoJson = jsonArray.getJSONObject(i);
                    String logo = logoJson.getString("sponsor_logo");
                    Log.d("Sponsors","logo is:"+logo);
                    JSONObject bannerJson = jsonArray.getJSONObject(i);
                    String banner = bannerJson.getString("sponsor_banner");
                    Log.d("Sponsors","banner is:"+banner);
                    data.add(new sponsors(id,name,descr,address,logo,banner));
                }
                try
                {
                    dataAdapter = new SponsorAdapter(context,header,data);
                    sponsorRecycler.setAdapter(dataAdapter);
                }catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}
