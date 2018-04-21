package com.djgeraldo.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
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
import com.djgeraldo.activities.MainActivity;
import com.djgeraldo.data.PlayListModel;
import com.djgeraldo.fragments.PlaylistSongs;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by sachin on 17-01-2018.
 */

public class PlayListAdapter extends RecyclerView.Adapter<PlayListAdapter.PlayListHolder> {

    Context context;
    ArrayList<PlayListModel> data;
    int playlistId;
    Fragment frag;
    FragmentTransaction ft;
    FragmentManager fm;
    String tag;
    RelativeLayout header;


    public PlayListAdapter(Context context,RelativeLayout header, ArrayList<PlayListModel> data,int playlistId)
    {
        this.context = context;
        this.data = data;
        this.header = header;
        this.playlistId = playlistId;
    }

    public class PlayListHolder extends RecyclerView.ViewHolder
    {
        public TextView pListNameText,totalCount;
        public ImageView pImage,deleteImage;

        public PlayListHolder(View v) {
            super(v);
            pImage = (ImageView) v.findViewById(R.id.icon);
            deleteImage = (ImageView)v.findViewById(R.id.deleteImage);
            pListNameText = (TextView) v.findViewById(R.id.playlistname);
            totalCount = (TextView)v.findViewById(R.id.songNos);
        }
    }

    @Override
    public PlayListAdapter.PlayListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.playlist_list_item, parent, false);
        return new PlayListAdapter.PlayListHolder(v);
    }

    @Override
    public void onBindViewHolder(PlayListAdapter.PlayListHolder holder, final int position) {
        try {
            PlayListModel currentData = data.get(position);
            final String playName = currentData.getPlaylistname();
            final int playListID = currentData.getPlaylistid();
            int totalCount = currentData.getTotalCount();
            holder.totalCount.setText(String.valueOf(totalCount));
            holder.pListNameText.setText(playName);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    frag = new PlaylistSongs(context,header, playListID, playName);
                    fm = ((MainActivity) context).getSupportFragmentManager();
                    ft = fm.beginTransaction();
                    ft.replace(R.id.fragmentContainer, frag);
                    ft.addToBackStack(null);
                    ft.commit();
                }
            });

            holder.deleteImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    Toast.makeText(context,"Delete option selected",Toast.LENGTH_SHORT).show();
                    final JSONObject object = new JSONObject();
                    try {
                        object.put("playlist_id",playListID);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
                    alertDialog.setTitle("Dj Gelardo");
                    alertDialog.setMessage("Are you sure want to delete?");
                    alertDialog.setPositiveButton("YES",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    new RemoveArrayListAsyncTask(object).execute();
                                    removePosition(position);
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

                    alertDialog.show();

                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void removePosition(int position)
    {

        data.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, data.size());
//        holder.itemView.setVisibility(View.GONE);
    }

    //    http://durisimomobileapps.net/djsisko/api/playlist/removePlaylist
    private class RemoveArrayListAsyncTask extends AsyncTask<String,Void,String>
    {
        JSONObject object;

        private RemoveArrayListAsyncTask(JSONObject object)
        {
            this.object = object;
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                MediaType JSON = MediaType.parse("application/json; charset=utf-8");
//                String url = "http://durisimomobileapps.net/djtico/api/user/playlist/remove";
                String url = "http://durisimomobileapps.net/djgeraldo/api/user/playlist/remove";
                OkHttpClient client = new OkHttpClient();
                RequestBody body = RequestBody.create(JSON,String.valueOf(object));
                Request request = new Request.Builder()
                        .url(url)
                        .post(body)
                        .build();
                Response response = client.newCall(request).execute();
                String result = response.body().string();
                Log.d("Add Fav Song Result",result);
                return result;
            } catch (Exception e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.d("Remove PlayList is",s);
            try {
                JSONObject jsonObject = new JSONObject(s);
                int status = jsonObject.getInt("status");
                Log.d("Remove PlayList status:",""+status);
                String msg = jsonObject.getString("msg");
                Toast.makeText(context,msg,Toast.LENGTH_SHORT).show();
                Log.d("Remove PlayList Message",msg);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }


}
