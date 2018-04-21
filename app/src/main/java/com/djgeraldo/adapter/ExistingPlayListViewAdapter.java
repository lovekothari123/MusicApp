package com.djgeraldo.adapter;

import android.content.Context;
import android.os.AsyncTask;
import android.provider.Settings;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import com.djgeraldo.R;
import com.djgeraldo.data.PlayListModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ExistingPlayListViewAdapter extends RecyclerView.Adapter<ExistingPlayListViewAdapter.ViewHolder> {

    ArrayList<PlayListModel> titles;
    Context context;
    ViewHolder viewHolder1;
    int songId;
    String android_id;
    PopupWindow p4;


    public ExistingPlayListViewAdapter(Context context1, ArrayList<PlayListModel> title, int id, PopupWindow p4) {
        titles = title;
        context = context1;
        this.songId = id;
        this.p4 = p4;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textView;

        public ViewHolder(View v) {
            super(v);
            textView = (TextView) v.findViewById(R.id.titlePlayList);
        }
    }

    @Override
    public ExistingPlayListViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(context).inflate(R.layout.existing_playlist_list_item, parent, false);
        viewHolder1 = new ViewHolder(view);
        return viewHolder1;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        PlayListModel current_data = titles.get(position);
        android_id = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        final int id = current_data.getPlaylistid();
        String title = current_data.getPlaylistname();
        holder.textView.setText(title);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JSONObject object = new JSONObject();
                try {
                    object.put("playlist_id",id);
                    object.put("u_id",android_id);
                    object.put("song_id",songId);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                new AddSongAsyncTask(object).execute();

            }
        });
    }

    @Override
    public int getItemCount() {
        return titles.size();
    }
//    http://durisimomobileapps.net/djsisko/api/playlist/add
    //    object -> playlist_id,u_id,song_id.
    private class AddSongAsyncTask extends AsyncTask<String,Void,String>
    {
        JSONObject object;

        private AddSongAsyncTask(JSONObject object)
        {
            this.object = object;
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                MediaType JSON = MediaType.parse("application/json; charset=utf-8");
//                String url = "http://durisimomobileapps.net/djtico/api/user/playlist/add_song";
                String url = "http://durisimomobileapps.net/djgeraldo/api/user/playlist/add_song";
                OkHttpClient client = new OkHttpClient();
                RequestBody body = RequestBody.create(JSON, String.valueOf(object));
                Request request = new Request.Builder()
                        .url(url)
                        .post(body)
                        .build();
                Response response = client.newCall(request).execute();
                String result = response.body().string();
                Log.d("AddSong PlayList Result",result);
                return result;
            } catch (Exception e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.d("Add song to playlist is",s);
            try {
                JSONObject jsonObject = new JSONObject(s);
                int status = jsonObject.getInt("status");
                Log.d("Add song status:",""+status);
                String msg = jsonObject.getString("msg");
                Toast.makeText(context,msg, Toast.LENGTH_SHORT).show();
                p4.dismiss();
                Log.d("Add Song Message",msg);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}