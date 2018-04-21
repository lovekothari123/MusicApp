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
import com.djgeraldo.adapter.NotificationAdapter;
import com.djgeraldo.custom.CustomHeader;
import com.djgeraldo.data.notification;
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
public class Notification extends Fragment {

    Context context;
    RelativeLayout header;
    ImageView iv_back,iv_player;
    TextView tv_title;
    ProgressDialog mProgressDialog;
    SharedPreferences preferences;
    String androidId;
    boolean isConnected;
    RecyclerView notificationRecycler;
    RecyclerView.LayoutManager layoutManager;
    ArrayList<notification> data = new ArrayList<>();
    NotificationAdapter notificationAdapter;

    public Notification() {
        // Required empty public constructor
    }

    @SuppressLint("ValidFragment")
    public Notification(Context context, RelativeLayout header)
    {
        this.context = context;
        this.header = header;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_notification, container, false);
        notificationRecycler = v.findViewById(R.id.notificationRecycler);
        layoutManager = new LinearLayoutManager(context);
        notificationRecycler.setLayoutManager(layoutManager);
        tv_title = header.findViewById(R.id.titleHeader);
        tv_title.setText("Notifications");
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

        preferences = context.getSharedPreferences("MyAndroidId",Context.MODE_PRIVATE);
        androidId = preferences.getString("androidId","");
        isConnected = preferences.getBoolean("isConnected",false);
        Log.d("notification","Android id: "+androidId);
        Log.d("notification","Net is connected: "+isConnected);
        if (CheckNetwork.isInternetAvailable(context)){
            data.clear();
            new notificationList().execute();
        }
        else
        {
            Toast.makeText(context,"No Internet Connection",Toast.LENGTH_SHORT).show();
        }
        return v;
    }

    //    http://durisimomobileapps.net/djsisko/api/notifications
    private class notificationList extends AsyncTask<String,Void,String>
    {
        private JSONObject jsonObject;
        public notificationList() {
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
//                String url = "http://durisimomobileapps.net/djtico/api/get_notifications";
                String url = "http://durisimomobileapps.net/djgeraldo/api/get_notifications";
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
            Log.d("Inbox", "Response is:" + s);
            if (s == null) {
                Log.d("mytag", "Inbox Response is null");
            } else {
                try
                {
                    JSONObject jsonObject = new JSONObject(s);
                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject notificationIdJson = jsonArray.getJSONObject(i);
                        int notificationId = notificationIdJson.getInt("notification_id");
                        Log.d("Inbox", "Inbox Id is:" + notificationId);
                        JSONObject msgJson = jsonArray.getJSONObject(i);
                        String msg = msgJson.getString("message");
                        Log.d("Inbox", "message:" + msg);
                        JSONObject createdAtJson = jsonArray.getJSONObject(i);
//                        JSONObject createdAt = createdAtJson.getJSONObject("created_at");
                        String date = createdAtJson.getString("created_at");
                        Log.d("Date is:",date);
                        Log.d("Inbox", "createdAt:" + date);
                        JSONObject updatedAtJson = jsonArray.getJSONObject(i);
                        String updatedAt = updatedAtJson.getString("updated_at");
                        Log.d("Inbox", "updatedAt:" + updatedAt);
                        data.add(new notification(notificationId, msg, date));
                        Log.d("Inbox","Data size is"+data.size());
                        notificationAdapter = new NotificationAdapter(context,header,data);
                        notificationRecycler.setAdapter(notificationAdapter);
                    }
                } catch(JSONException e){
                    e.printStackTrace();
                }
            }
            System.out.println(s);
        }
    }


}
