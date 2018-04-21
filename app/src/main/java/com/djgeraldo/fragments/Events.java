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
import com.djgeraldo.adapter.EventsDataAdapter;
import com.djgeraldo.custom.CustomHeader;
import com.djgeraldo.data.EventsData;
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
public class Events extends Fragment {

    RelativeLayout header;
    ImageView iv_back,iv_player;
    TextView tv_title;
    Context context;
    String name;
    ArrayList<EventsData> eventsData = new ArrayList<>();
    RecyclerView eventsRecycler;
    RecyclerView.LayoutManager layoutManager;
    EventsDataAdapter dataAdapter;
    String androidId;
    boolean isConnected;
    SharedPreferences preferences;
    ProgressDialog mProgressDialog;
    Fragment frag;
    FragmentTransaction ft;
    FragmentManager fm;
    String tag = Events.class.getSimpleName();


    public Events() {
        // Required empty public constructor
    }

    @SuppressLint("ValidFragment")
    public Events(Context context, RelativeLayout header)
    {
        this.context = context;
        this.header = header;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_events, container, false);
        preferences = context.getSharedPreferences("MyAndroidId",Context.MODE_PRIVATE);
        androidId = preferences.getString("androidId","");
        isConnected = preferences.getBoolean("isConnected",false);
        Log.d("Events","Android id: "+androidId);
        Log.d("Events","Net is connected: "+isConnected);
        eventsRecycler = (RecyclerView) v.findViewById(R.id.eventsRecycler);
        layoutManager = new LinearLayoutManager(context);
        eventsRecycler.setLayoutManager(layoutManager);
        tv_title = header.findViewById(R.id.titleHeader);
        tv_title.setText("Events");
        tv_title.setSelected(true);
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
        header.setTag("inner");
        CustomHeader.setInnerFragment(getActivity(),header);
        JSONObject object = new JSONObject();
        try {
            object.put("u_id", androidId);
            Log.d("mytag", "Json Object : " + object.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (CheckNetwork.isInternetAvailable(context))
        {
            eventsData.clear();
            new EventsAsyncTask().execute();
        }
        else
        {
            Toast.makeText(context,"No Internet Connection",Toast.LENGTH_SHORT).show();
        }
        return v;
    }

    //http://durisimomobileapps.net/djtico/api/events
    public class EventsAsyncTask extends AsyncTask<String, Void, String> {
        private JSONObject jsonObject;
        public EventsAsyncTask() {

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
//                String url = "http://durisimomobileapps.net/djtico/api/events";
                String url = "http://durisimomobileapps.net/djgeraldo/api/events";
                OkHttpClient client = new OkHttpClient();
                RequestBody body = RequestBody.create(JSON, String.valueOf(jsonObject));
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
            Log.d("mytag", "REsponse : " + s);
            if (s == null) {
                Log.d("mytag", "Bookings Response is null");
            } else {
                try
                {
                    JSONObject jsonObject = new JSONObject(s);
                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject idJson = jsonArray.getJSONObject(i);
                        int id = idJson.getInt("event_id");
                        Log.d("Event id is", "" + id);
                        JSONObject titleJson = jsonArray.getJSONObject(i);
                        String title = titleJson.getString("title");
                        Log.d("mytag", "Title IS " + title);
                        JSONObject descriptionJson = jsonArray.getJSONObject(i);
                        String description = descriptionJson.getString("description");
                        Log.d("mytag", "description IS " + description);
                        JSONObject imageJson = jsonArray.getJSONObject(i);
                        String image = imageJson.getString("image");
                        Log.d("Events Image is", image);
                        JSONObject timeJson = jsonArray.getJSONObject(i);
                        String time = timeJson.getString("time");
                        Log.d("time is", time);
                        JSONObject startJson = jsonArray.getJSONObject(i);
                        String start = startJson.getString("start");
                        Log.d("start is", start);
                        JSONObject endJson = jsonArray.getJSONObject(i);
                        String end = endJson.getString("end");
                        Log.d("end is", end);
                        JSONObject addressJson = jsonArray.getJSONObject(i);
                        String address = addressJson.getString("address");
                        Log.d("address is", address);
                        JSONObject latitudeJson = jsonArray.getJSONObject(i);
                        String latitude = latitudeJson.getString("latitude");
                        Log.d("latitude is", latitude);
                        JSONObject longitudeJson = jsonArray.getJSONObject(i);
                        String longitude = longitudeJson.getString("longitude");
                        Log.d("longitude is", longitude);
                        //id,image,title,description,start,end,time,address
                        eventsData.add(new EventsData(id, image, title, description, start, end, address, longitude, latitude,time));
                    }
                    try {
                        dataAdapter = new EventsDataAdapter(context,header, eventsData,tag);
                        eventsRecycler.setAdapter(dataAdapter);
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
