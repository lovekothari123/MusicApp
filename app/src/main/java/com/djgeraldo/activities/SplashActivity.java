package com.djgeraldo.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.provider.Settings;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.djgeraldo.R;
import com.djgeraldo.phonemidea.CheckNetwork;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SplashActivity extends Activity {

    private static int SPLASH_TIME_OUT = 2000;
    private String token;
    Context context;
    JSONObject object;
    String androidId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        token = FirebaseInstanceId.getInstance().getToken();
        context= SplashActivity.this;
        androidId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(SplashActivity.this, MainActivity.class);
//                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                SplashScreenActivity.this.finish();
                startActivity(i);
                finish();
            }
        }, SPLASH_TIME_OUT);

        if (CheckNetwork.isInternetAvailable(context)) {
            object = new JSONObject();
            try {
                object.put("u_id", androidId);
                object.put("gcm_id", token);
                object.put("device", "android");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            new userAsyncTask(object).execute();
        } else {
            Toast.makeText(context, "No Internet Connection", Toast.LENGTH_SHORT).show();
        }
    }

    //http://durisimomobileapps.net/djtico/api/user/register
    private class userAsyncTask extends AsyncTask<String, Void, String> {
        JSONObject jsonObject;

        public userAsyncTask(JSONObject jsonObject) {
            this.jsonObject = jsonObject;
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                String url = "http://durisimomobileapps.net/djgeraldo/api/user/register";
                OkHttpClient client = new OkHttpClient();
                RequestBody body = RequestBody.create(JSON, String.valueOf(jsonObject));
                Request request = new Request.Builder()
                        .url(url)
                        .post(body)
                        .build();
                Response response = client.newCall(request).execute();
                String result = response.body().string();
                return result;
            } catch (Exception e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.d("Register User", "Response is:" + s);
            if (s == null) {
                Log.d("Register Response is:", "null");
            } else {
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    int status = jsonObject.getInt("status");
                    Log.d("MainActivity", "Status" + status);
                    String msg = jsonObject.getString("msg");
                    Log.d("MainActivity", "Msg " + msg);
                    JSONObject object = jsonObject.getJSONObject("data");
                    String user_id = object.getString("u_id");
                    Log.d("User Id is:", user_id);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
