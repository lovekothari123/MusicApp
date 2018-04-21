package com.djgeraldo.adapter;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.djgeraldo.R;
import com.djgeraldo.activities.MainActivity;
import com.djgeraldo.data.BufferData;
import com.djgeraldo.data.MyPlayListSongs;
import com.djgeraldo.data.PlayListModel;
import com.djgeraldo.data.PojoSongForPlayer;
import com.djgeraldo.data.commentsModel;
import com.djgeraldo.manager.MediaController;
import com.djgeraldo.phonemidea.CheckNetwork;
import com.djgeraldo.phonemidea.PhoneMediaControl;
import com.djgeraldo.phonemidea.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import pl.droidsonroids.gif.GifImageView;

/**
 * Created by sachin on 17-01-2018.
 */

public class PlayListSongsAdapter extends RecyclerView.Adapter<PlayListSongsAdapter.PlayListHolder> {
    Context context;
    RelativeLayout header;
    ArrayList<MyPlayListSongs> data = new ArrayList<>();
    ArrayList<commentsModel> comments = new ArrayList<>();
    ArrayList<PlayListModel> d = new ArrayList<>();
    ExistingPlayListViewAdapter dataAdapter;
    RecyclerView commentsRecycler;
    RecyclerView.LayoutManager layoutManager2,layoutManager;
    JSONObject listObject;
    int nowPlaying = 0;
    String duration;
    ArrayList<PojoSongForPlayer> sngList;
    SharedPreferences preferences;
    String androidId;
    boolean isConnected;
    CommentsAdapter commentsAdapter;
    PopupWindow pw1,pw2,pw3,pw4;
    RecyclerView list;


    public PlayListSongsAdapter(Context context, RelativeLayout header, ArrayList<MyPlayListSongs> data)
    {
        this.context = context;
        this.header = header;
        this.data = data;
        notifyDataSetChanged();
    }

    public class PlayListHolder extends RecyclerView.ViewHolder {
        LinearLayout ll_song_list_item;
        ImageView iv_songImage, iv_like_Image, iv_share_image, iv_comment_image, iv_options;
        GifImageView song_small_gif;
        TextView tv_songName, tv_artistName, tv_like_number, tv_songDuration;
        public PlayListHolder(View v) {
            super(v);
            ll_song_list_item = v.findViewById(R.id.song_list_item);
            iv_songImage = v.findViewById(R.id.songImage);
            iv_like_Image = v.findViewById(R.id.like_Image);
            iv_share_image = v.findViewById(R.id.share_image);
            iv_comment_image = v.findViewById(R.id.comment_image);
            iv_options = v.findViewById(R.id.options);
            song_small_gif = v.findViewById(R.id.song_small_gif);
            tv_songName = v.findViewById(R.id.songName);
            tv_artistName = v.findViewById(R.id.list_artistName);
            tv_like_number = v.findViewById(R.id.like_number);
            tv_songDuration = v.findViewById(R.id.songDuration);
        }
    }

    @Override
    public PlayListSongsAdapter.PlayListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView= LayoutInflater.from(parent.getContext()).inflate(R.layout.song_list_item, parent, false);

        try {

            sngList = new ArrayList<PojoSongForPlayer>();
            MyPlayListSongs myBean;
            for (int i = 0; i < data.size(); i++) {
                myBean = data.get(i);
                duration = myBean.getS_duration();
                String durInMili = String.valueOf(Utils.getInstance().strToMilli(duration));
                Utils.getInstance().d("mili" + durInMili);
                PojoSongForPlayer mDetail = new PojoSongForPlayer(myBean.getTitle(), durInMili, myBean.getS_url(), myBean.getS_img(), myBean.getArtist(), myBean.getS_id());
                sngList.add(mDetail);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return new PlayListSongsAdapter.PlayListHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final PlayListSongsAdapter.PlayListHolder holder, final int position) {
        preferences = context.getSharedPreferences("MyAndroidId",Context.MODE_PRIVATE);
        androidId = preferences.getString("androidId","");
        isConnected = preferences.getBoolean("isConnected",false);
        final MyPlayListSongs currentData = data.get(position);
        final int sip_id = currentData.getSip_id();
        final int id = currentData.getS_id();
//        final String songUrl = currentData.getSongUrl();
//        final int favId = currentData.getFavId();
//        final int playlistId = currentData.getPlaylistId();
//        favStatus = currentData.getFavStatus();
//        playListStatus = currentData.getPlayListStatus();
        duration = currentData.getS_duration();
//        Picasso.with(context).load(currentData.getSongImage()).error(R.drawable.barkologo).into(holder.iv_songImage);
        Log.d("Playlist image url is:",currentData.getS_img());
        String image = currentData.getS_img();
        final int likeNos = currentData.getLike_status();
        int totalLikes = currentData.getTotal_likes();
        nowPlaying = currentData.getNowPlaying();
        holder.tv_like_number.setText(String.valueOf(totalLikes));
        holder.tv_songName.setText(currentData.getS_name());
        holder.tv_artistName.setText(currentData.getArtist());
        holder.tv_songDuration.setText(currentData.getS_duration());
        holder.tv_songName.setSelected(true);
        holder.tv_artistName.setSelected(true);
        Log.d("Playlist img is: ====>",image);

        Glide.with(context)
                .load(image)
                .placeholder(R.drawable.dj_gradlo_app_logo)
                .into(holder.iv_songImage);
        if (likeNos == 0)
        {
            holder.iv_like_Image.setImageResource(R.drawable.fav_wh);
        }
        else if (likeNos == 1)
        {
            holder.iv_like_Image.setImageResource(R.drawable.fav_yellow);
        }
        if (nowPlaying == 1 )
        {
            holder.song_small_gif.setVisibility(View.VISIBLE);
        }
        else if (nowPlaying == 0)
        {
            holder.song_small_gif.setVisibility(View.GONE);
        }
        holder.iv_comment_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(context,"Comments Clicked",Toast.LENGTH_SHORT).show();
                commentsView(id,position);
            }
        });
        holder.iv_like_Image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject object = new JSONObject();
                try {
                    object.put("u_id", androidId);
                    object.put("song_id", id);
                    if (likeNos == 1)
                    {
                        Log.d("Adapter","0 condition");
                        object.put("likestatus",0);
                    }
                    else if (likeNos == 0)
                    {
                        Log.d("Adapter","1 condition");
                        object.put("likestatus",1);
                    }
//                    object.put("likestatus",likeNos);
                    Log.d("mytag", "Json Object : " + object.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                new songLIkePostAsyncTask(currentData,object,holder.tv_like_number,holder.iv_like_Image).execute();
            }
        });

        holder.iv_share_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent share = new Intent(android.content.Intent.ACTION_SEND);
                share.setType("text/plain");
                share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                String songName = "Song Name: " + currentData.getS_name() + "   ";
                String songUrl = "\nSong Url: " + currentData.getS_url() + "   ";
                String songArtist = "\nSong Artist: " + currentData.getArtist()+ "   ";
                String appName = "\nBy : Dj Gelardo";
                String shareBody = ("Song Name: " + currentData.getS_name() + "\n\n" + "Song Url: " + currentData.getS_url() + "\n\n" + "Artist Name: " + currentData.getArtist() + "\n\n" + "App Name: " + "By Dj Gelardo");
                share.putExtra(Intent.EXTRA_SUBJECT, "Song");
                share.putExtra(Intent.EXTRA_TEXT,shareBody);
                ((MainActivity)context).startActivity(Intent.createChooser(share, "Share song!"));
            }
        });

        holder.iv_options.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    popupWindow(v,id,position,sip_id);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        holder.ll_song_list_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notifyDataSetChanged();
                final  PojoSongForPlayer mDetail = sngList.get(position);
                if (holder.song_small_gif.getVisibility() == View.VISIBLE)
                {
//                    MainActivity.setLatinLayout();
                } else {
                    if (mDetail != null) {
                        final ProgressDialog progressDialog = BufferData.getInstance().getUniversalProgressLoader();
                        if (progressDialog != null) {
                            Utils.getInstance().d("not null");
                            if (!progressDialog.isShowing()) {
                                Utils.getInstance().d("not showing");
                                if (CheckNetwork.isInternetAvailable(context))
                                {
                                    progressDialog.show();
//                                    MainActivity.setLatinLayout();
                                    progressDialog.setCanceledOnTouchOutside(true);
                                }
                                else {
                                    progressDialog.dismiss();
                                    Toast.makeText(context, "No Internet Connection", Toast.LENGTH_SHORT).show();
                                }
                                if (MediaController.getInstance().isPlayingAudio(mDetail) && !MediaController.getInstance().isAudioPaused()) {
                                    MediaController.getInstance().pauseAudio(mDetail);
//                                        holder.img.setSelected(true);

                                } else {
                                    MediaController.getInstance().setPlaylist(sngList, mDetail, PhoneMediaControl.SonLoadFor.All.ordinal(), -1);
//                                    holder.img.setSelected(false);
                                }
                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (progressDialog != null) {
                                            if (progressDialog.isShowing()) {
                                                new Handler().postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        String duration1 = duration;
                                                        String strTime = duration1.substring(duration1.length() - 2);
                                                        int sec1 = mDetail.audioProgressSec % 60;
                                                        int sec = Integer.parseInt(strTime);
                                                        if (sec > 1 && sec1 > 1) {
                                                            progressDialog.dismiss();
//                                                            MainActivity.setLatinLayout();
                                                        }
                                                    }
                                                }, 500);
                                            }
                                        }


                                    }
                                }, 500);

                            }

                        }
                    }
                }
            }
        });
    }


    private void popupWindow(View view, final int id, final int position, final int sip_id) throws JSONException {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.popupwindow1, (ViewGroup) view.findViewById(R.id.popupwindow1));
        TextView playListText = (TextView) layout.findViewById(R.id.playlistPopUp);
        playListText.setText("Remove From Playlist");
//        removePlayList = (TextView)layout.findViewById(R.id.removePlaylistPopUp);
//        removeFavourites = (TextView)layout.findViewById(R.id.removeFavouritesPopUp);
        TextView favText = (TextView) layout.findViewById(R.id.favouritesPopUp);
        pw1 = new PopupWindow(layout, RecyclerView.LayoutParams.MATCH_PARENT, 300, true);
        pw1.showAtLocation(layout, Gravity.CENTER, 0, 0);
//        if (playListStatus == 1)
//        {
//            playListText.setVisibility(View.GONE);
//            removePlayList.setVisibility(View.VISIBLE);
//        }
//        if (favStatus == 1)
//        {
//            favText.setVisibility(View.GONE);
//            removeFavourites.setVisibility(View.VISIBLE);
//        }
//        removePlayList.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
////                Toast.makeText(context,"Remove From Playlist Clicked",Toast.LENGTH_SHORT).show();
//                if (playlistId != 0)
//                {
//                    JSONObject object = new JSONObject();
//                    try {
//                        Log.d("SongDataAdapter","sip_id here:"+playlistId);
//                        object.put("sip_id",playlistId);
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                    new RemoveSongPlayListAsyncTask(object).execute();
//                    removePosition(position);
//                }
//            }
//        });



//        removeFavourites.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
////                Toast.makeText(context,"Remove From Favourites Clicked",Toast.LENGTH_SHORT).show();
//                if (favId != 0)
//                {
//                    JSONObject object = new JSONObject();
//                    try {
//                        Log.d("SongDataAdapter","fav_id here:"+favId);
//                        object.put("fav_id",favId);
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                    new RemoveSongFavAsyncTask(object).execute();
//
//                    removePosition(position);
//                }
//            }
//        });
        playListText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(context, "PlayList Options Clicked", Toast.LENGTH_SHORT).show();
//                pw1.dismiss();
//                playlistPopUp(v,id);
                if (sip_id != 0)
                {
                    JSONObject object = new JSONObject();
                    try {
                        Log.d("Adapter","sip_id here:"+sip_id);
                        object.put("sip_id",sip_id);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    new RemoveSongPlayListAsyncTask(object).execute();
                    removePosition(position);
                }
            }
        });

        favText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject object = new JSONObject();
                try {
                    object.put("u_id",androidId);
                    object.put("song_id",id);
                    Log.d("Song Id in Add song Fav",""+id);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                new AddFavSongAsyncTask(object).execute();
            }
        });


    }

    private void playlistPopUp(View view, final int id) {
//        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = LayoutInflater.from(context).inflate(R.layout.popupwindow2, (ViewGroup) view.findViewById(R.id.popupWindow2));
        TextView existingPlayList = (TextView) layout.findViewById(R.id.existingPlayList);
        TextView newPlayListText = (TextView) layout.findViewById(R.id.newPlaylist);
        pw2 = new PopupWindow(layout, RecyclerView.LayoutParams.MATCH_PARENT, 300, true);
        pw2.showAtLocation(layout, Gravity.CENTER, 0, 0);
        newPlayListText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(context, "Add new PlayList Clicked", Toast.LENGTH_SHORT).show();
                pw2.dismiss();
                newPlayListPopUp(v,id);

            }
        });
        existingPlayList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(context, "Existing PlayList Clicked", Toast.LENGTH_SHORT).show();
                pw2.dismiss();
                d = new ArrayList<>();
                createNewPopUp(v,id);
            }
        });
    }

    private void newPlayListPopUp(View view, final int id) {
        View layout = LayoutInflater.from(context).inflate(R.layout.popupwindow3, (ViewGroup) view.findViewById(R.id.popupwindow3));
        pw3 = new PopupWindow(layout, RecyclerView.LayoutParams.MATCH_PARENT, 350, true);
        pw3.showAtLocation(layout, Gravity.CENTER, 0, 0);
        final EditText addPlayListName = (EditText) layout.findViewById(R.id.newPlayListName);
        Button createBtn = (Button) layout.findViewById(R.id.createBtn);
        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(context, "Create Button Clicked", Toast.LENGTH_SHORT).show();
                String name = addPlayListName.getText().toString();
//                d.add(new DataModule(addPlayListName.getText().toString()));
                JSONObject object = new JSONObject();
                try {
                    object.put("u_id",androidId);
                    object.put("name",name);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                new NewPlayListAsyncTask(object).execute();
                pw3.dismiss();
                createNewPopUp(v,id);
            }
        });
    }

    private void createNewPopUp(View view,int id) {
        View layout = LayoutInflater.from(context).inflate(R.layout.popupwindow4, (ViewGroup) view.findViewById(R.id.popupwindow4));
        pw4 = new PopupWindow(layout, RecyclerView.LayoutParams.WRAP_CONTENT, RecyclerView.LayoutParams.WRAP_CONTENT, true);
        pw4.showAtLocation(layout, Gravity.CENTER, 0, 0);
        list = (RecyclerView) layout.findViewById(R.id.newplaylistRecycler);
        layoutManager = new LinearLayoutManager(context);
        list.setLayoutManager(layoutManager);
        JSONObject object = new JSONObject();
        try {
            object.put("u_id", androidId);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        d.clear();
        new existingPlayListAsyncTask(object,id).execute();
    }

    //http://durisimomobileapps.net/djtico/api/user/playlist/add
    private class NewPlayListAsyncTask extends AsyncTask<String,Void,String>
    {
        JSONObject object;

        private NewPlayListAsyncTask(JSONObject object)
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
                RequestBody body = RequestBody.create(JSON,String.valueOf(object));
                Request request = new Request.Builder()
                        .url(url)
                        .post(body)
                        .build();
                Response response = client.newCall(request).execute();
                String result = response.body().string();
                Log.d("New Playlist Result is:",result);
                return result;
            } catch (Exception e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                Log.d("New Playlist Response",s);
                JSONObject jsonObject = new JSONObject(s);
                int status = jsonObject.getInt("status");
                Log.d("New PlayList status",""+status);
                JSONObject dataJson = jsonObject.getJSONObject("data");
                int playlistId = dataJson.getInt("playlist_id");
                Log.d("New PlayList Id is:",""+playlistId);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    //http://durisimomobileapps.net/djsisko/api/favs/remove -> fav_id
    private class RemoveSongFavAsyncTask extends AsyncTask<String,Void,String>
    {
        JSONObject object;

        private RemoveSongFavAsyncTask(JSONObject object)
        {
            this.object = object;
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                MediaType JSON = MediaType.parse("application/json; charset=utf-8");
//                String url = "http://durisimomobileapps.net/djtico/api/user/favourite/remove";
                String url = "http://durisimomobileapps.net/djgeraldo/api/user/favourite/remove";
                OkHttpClient client = new OkHttpClient();
                RequestBody body = RequestBody.create(JSON,String.valueOf(object));
                Request request = new Request.Builder()
                        .url(url)
                        .post(body)
                        .build();
                Response response = client.newCall(request).execute();
                String result = response.body().string();
                Log.d("Remove Song Favourite",result);
                return result;
            } catch (Exception e) {
                return null;
            }
        }
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.d("Remove song from Fav",s);
            try {
                JSONObject jsonObject = new JSONObject(s);
                int status = jsonObject.getInt("status");
                Log.d("RemoveSongFav status:",""+status);
                String msg = jsonObject.getString("msg");
                Toast.makeText(context,msg,Toast.LENGTH_SHORT).show();
                Log.d("RemoveSongFav msg",msg);
                pw1.dismiss();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    //http://durisimomobileapps.net/djsisko/api/playlist/user
    private class existingPlayListAsyncTask extends AsyncTask<String,Void,String>
    {
        JSONObject object;
        int id;

        private existingPlayListAsyncTask(JSONObject object,int id)
        {
            this.object = object;
            this.id = id;
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                MediaType JSON = MediaType.parse("application/json; charset=utf-8");
//                String url = "http://durisimomobileapps.net/djtico/api/user/user_playlist";
                String url = "http://durisimomobileapps.net/djgeraldo/api/user/user_playlist";
                OkHttpClient client = new OkHttpClient();
                RequestBody body = RequestBody.create(JSON,String.valueOf(object));
                Request request = new Request.Builder()
                        .url(url)
                        .post(body)
                        .build();
                Response response = client.newCall(request).execute();
                String result = response.body().string();
                Log.d("ExistingPLaylist Result",result);
                return result;
            } catch (Exception e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.d("ExistingPlayList s is",s);
            try {
                JSONObject jsonObject = new JSONObject(s);
                int status = jsonObject.getInt("status");
                Log.d("ExistingPlayList","status:"+status);
                JSONArray jsonArray = jsonObject.getJSONArray("data");
                for (int i=0;i < jsonArray.length();i++)
                {
                    JSONObject playListIdJson = jsonArray.getJSONObject(i);
                    int playListId = playListIdJson.getInt("playlist_id");
                    Log.d("ExistingPlayList id:",""+playListId);
                    JSONObject playListNameJson = jsonArray.getJSONObject(i);
                    String playListName = playListNameJson.getString("name");
                    Log.d("ExistingPlayList name:",playListName);
                    if(status == 0) { Toast.makeText(context,"No Playlist",Toast.LENGTH_SHORT).show();}
                    else {
                        d.add(new PlayListModel(playListId, playListName));
                        dataAdapter = new ExistingPlayListViewAdapter(context, d, id, pw4);
                        list.setAdapter(dataAdapter);
                    }
//                    pw3.dismiss();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void removePosition(int position)
    {
        data.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, data.size());
    }




    @Override
    public int getItemCount() {
        return data.size();
    }


    //http://durisimomobileapps.net/djsisko/api/favs/add
    private class AddFavSongAsyncTask extends AsyncTask<String,Void,String>
    {
        JSONObject object;

        private AddFavSongAsyncTask(JSONObject object)
        {
            this.object = object;
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                MediaType JSON = MediaType.parse("application/json; charset=utf-8");
//                String url = "http://durisimomobileapps.net/djtico/api/user/favourite/add";
                String url = "http://durisimomobileapps.net/djgeraldo/api/user/favourite/add";
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
            Log.d("Add Fav Song is",s);
            try {
                JSONObject jsonObject = new JSONObject(s);
                int status = jsonObject.getInt("status");
                Log.d("Add Fav song status:",""+status);
                String msg = jsonObject.getString("msg");
                Toast.makeText(context,msg,Toast.LENGTH_SHORT).show();
                pw1.dismiss();
                Log.d("Add Fav Song Message",msg);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    //   http://durisimomobileapps.net/djtico/api/user/likestatus
    public class songLIkePostAsyncTask extends AsyncTask<String, Void, String> {

        private JSONObject jsonObject;
        TextView likeStatus;
        ImageView likeImage;
        MyPlayListSongs singlesSongsModel;
        public songLIkePostAsyncTask(MyPlayListSongs bean,JSONObject object,TextView likeStatus,ImageView likeImage) {
            this.singlesSongsModel = bean;
            this.likeStatus = likeStatus;
            this.likeImage = likeImage;
            this.jsonObject=object;
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                MediaType JSON = MediaType.parse("application/json; charset=utf-8");
//                String url = "http://durisimomobileapps.net/djtico/api/user/likestatus";
                String url = "http://durisimomobileapps.net/djgeraldo/api/user/likestatus";
                OkHttpClient client = new OkHttpClient();
                RequestBody body = RequestBody.create(JSON, String.valueOf(jsonObject));
                Request request = new Request.Builder()
                        .url(url)
                        .post(body)
                        .build();
                Response response = client.newCall(request).execute();
                String result = response.body().string();
                Log.d("Result is:",result);
                return result;
            } catch (Exception e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                JSONObject jsonObject = new JSONObject(s);
                JSONObject dataObject = jsonObject.getJSONObject("data");
                int likes = dataObject.getInt("like_status");
                Log.d("Likes are:",""+likes);
                int totalLikes = dataObject.getInt("total_like");
                Log.d("Total likes are",""+totalLikes);
                if (likes == 1)
                {
//                    singlesSongsModel.setLike_status(1);
                    Log.d("SongAdapter","Likes 1 updated");
                    likeImage.setImageResource(R.drawable.fav_yellow);
                    likeStatus.setText(String.valueOf(totalLikes));

                }
                else if (likes == 0)
                {
//                    singlesSongsModel.setLike_status(0);
                    Log.d("SongAdapter","Likes 0 updated");
                    likeImage.setImageResource(R.drawable.fav_wh);
                    likeStatus.setText(String.valueOf(totalLikes));
                }
                singlesSongsModel.setLike_status(dataObject.getInt("like_status"));
                likeStatus.setText(jsonObject.getString("total_like"));
                singlesSongsModel.setTotal_likes(dataObject.getInt("total_like"));


//                Toast.makeText(context,"Likes :" +totalLikes,Toast.LENGTH_SHORT).show();

            } catch (Exception e) {
                e.printStackTrace();
            }
            // txtString.setText(s);
            System.out.println(s);
        }
    }
    private  void commentsView(final int id,int position)
    {
//        final Dialog commentsDialog  = new Dialog(this.context, R.style.AlertDialog);
        final AppCompatDialog commentsDialog  = new AppCompatDialog(context,android.R.style.Theme_Black_NoTitleBar);

        View view  =((MainActivity)context).getLayoutInflater().inflate(R.layout.commentsdialog, null);
        commentsDialog.setContentView(view);
        commentsDialog.show();
        ImageView backButtonComments = view.findViewById(R.id.backButtonComments);
        final EditText editComments = view.findViewById(R.id.editTextComments);
        final Button sendButton = view.findViewById(R.id.sendButtonComments);
        backButtonComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                commentsDialog.dismiss();
            }
        });
        commentsRecycler =view.findViewById(R.id.commentsRecycler);
        layoutManager2 = new LinearLayoutManager((MainActivity)context);
        commentsRecycler.setLayoutManager(layoutManager2);
        comments.clear();
//        commentsAdapter = new CommentsAdapter(context,comments);
//        commentsRecycler.setAdapter(commentsAdapter);
        listObject = new JSONObject();
        try {
            listObject.put("song_id",id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        new listCommentsAsyncvTask(listObject).execute();
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Toast.makeText(context,"Send Button Clicked",Toast.LENGTH_SHORT).show();
                if (!editComments.getText().toString().isEmpty() && sendButton.isPressed())
                {
                    String text = editComments.getText().toString();
                    JSONObject object = new JSONObject();
                    try {
                        Log.d("Song id inside object:",""+id);
                        Log.d("Comment inside object:",text);
                        object.put("u_id",androidId);
                        object.put("song_id",id);
                        object.put("comment",text);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    new addCommentAsyncTask(object).execute();
                    editComments.getText().clear();
                    InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(editComments.getWindowToken(), 0);
                    editComments.clearFocus();
                    commentsRecycler.findFocus();
                }
                else
                {
                    editComments.getText().clear();
                }

            }
        });
    }

    private class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.CommentsHolder> {

        Context context;
        ArrayList<commentsModel> dataComments = new ArrayList<>();

        public CommentsAdapter(Context context,ArrayList<commentsModel> dataComments)
        {
            this.context = context;
            this.dataComments = dataComments;
        }

        @Override
        public CommentsAdapter.CommentsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(context).inflate(R.layout.comments_list_item,parent,false);
            return new CommentsAdapter.CommentsHolder(v);
        }

        @Override
        public void onBindViewHolder(CommentsAdapter.CommentsHolder holder, int position) {
            commentsModel currentData = dataComments.get(position);
            String user = currentData.getUserName();
            String comment = currentData.getComment();
            holder.commentUser.setText(user);
            holder.comments.setText(comment);
        }

        @Override
        public int getItemCount() {
            return dataComments.size();
        }

        public class CommentsHolder extends RecyclerView.ViewHolder {
            TextView commentUser,comments;
            public CommentsHolder(View itemView) {
                super(itemView);
                commentUser = itemView.findViewById(R.id.userName);
                comments = itemView.findViewById(R.id.commentsText);
            }
        }
    }

    //    http://durisimomobileapps.net/djtico/api/comments/add
    private class addCommentAsyncTask extends AsyncTask<String,Void,String>
    {
        JSONObject jsonObject;
        private addCommentAsyncTask(JSONObject jsonObject)
        {
            this.jsonObject = jsonObject;
        }

        @Override
        protected String doInBackground(String... strings) {
            try
            {
                MediaType JSON = MediaType.parse("application/json;charset=utf-8");
//                String url = "http://durisimomobileapps.net/djtico/api/comments/add";
                String url = "http://durisimomobileapps.net/djgeraldo/api/comments/add";
                OkHttpClient client = new OkHttpClient();
                RequestBody body = RequestBody.create(JSON,String.valueOf(jsonObject));
                Request request = new Request.Builder()
                        .url(url)
                        .post(body)
                        .build();
                Response response = client.newCall(request).execute();
                String result = response.body().string();
                Log.d("Add Comment json result",result);
                return result;
            }catch (Exception e)
            {
                return null;
            }

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.d("Comment Response is",s);
            try {
                JSONObject jsonObject = new JSONObject(s);
                int status = jsonObject.getInt("status");
                Log.d("Add Comment Status is:",""+status);
                if (status == 1)
                {
                    new listCommentsAsyncvTask(listObject).execute();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }


    //    http://durisimomobileapps.net/djtico/api/comments/get
    private class listCommentsAsyncvTask extends AsyncTask<String,Void,String>
    {
        JSONObject object;
        private listCommentsAsyncvTask(JSONObject object)
        {
            this.object = object;
        }
        @Override
        protected String doInBackground(String... strings) {
            try {
                MediaType JSON = MediaType.parse("application/json; charset=utf-8");
//                String url = "http://durisimomobileapps.net/djtico/api/comments/get";
                String url = "http://durisimomobileapps.net/djgeraldo/api/comments/get";
                OkHttpClient client = new OkHttpClient();
                RequestBody body = RequestBody.create(JSON,String.valueOf(object));
                Request request = new Request.Builder()
                        .url(url)
                        .post(body)
                        .build();
                Response response = client.newCall(request).execute();
                String result = response.body().string();
                Log.d("Comment List Result is:",result);
                return result;
            } catch (Exception e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                Log.d("Comments list Response",s);
                JSONObject jsonObject = new JSONObject(s);
                int status = jsonObject.getInt("status");
                JSONArray jsonArray = jsonObject.getJSONArray("data");
                comments.clear();
                for (int i=0; i < jsonArray.length();i++)
                {
                    JSONObject userJson = jsonArray.getJSONObject(i);
                    String user = userJson.getString("u_id");
                    Log.d("User is:",user);
                    JSONObject songIdJson = jsonArray.getJSONObject(i);
                    int songId = songIdJson.getInt("song_id");
                    Log.d("Comments List","Song Id is:"+songId);
                    JSONObject commentsJson = jsonArray.getJSONObject(i);
                    String comment = commentsJson.getString("comment");
                    Log.d("Comment List","Comment is:"+comment);
                    comments.add(new commentsModel(user,comment));
                    try {
                        commentsAdapter = new CommentsAdapter(context,comments);
                        commentsRecycler.setAdapter(commentsAdapter);
                    }catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println(s);
        }
    }

    //http://durisimomobileapps.net/djtico/api/user/playlist/remove_song -> sip_id
    private class RemoveSongPlayListAsyncTask extends AsyncTask<String,Void,String>
    {
        JSONObject object;


        private RemoveSongPlayListAsyncTask(JSONObject object)
        {
            this.object = object;
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                MediaType JSON = MediaType.parse("application/json; charset=utf-8");
//                String url = "http://durisimomobileapps.net/djtico/api/user/playlist/remove_song";
                String url = "http://durisimomobileapps.net/djgeraldo/api/user/playlist/remove_song";
                OkHttpClient client = new OkHttpClient();
                RequestBody body = RequestBody.create(JSON,String.valueOf(object));
                Request request = new Request.Builder()
                        .url(url)
                        .post(body)
                        .build();
                Response response = client.newCall(request).execute();
                String result = response.body().string();
                Log.d("Remove Song PlayList",result);
                return result;
            } catch (Exception e) {
                return null;
            }
        }
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.d("Remove song from pList",s);
            try {
                JSONObject jsonObject = new JSONObject(s);
                int status = jsonObject.getInt("status");
                Log.d("RemoveSongPList status:",""+status);
                String msg = jsonObject.getString("msg");
                Toast.makeText(context,msg,Toast.LENGTH_SHORT).show();
                Log.d("RemoveSongPList Message",msg);
                if (status == 1)
                {
                    pw1.dismiss();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


}
