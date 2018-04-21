package com.djgeraldo.adapter;

import android.content.Context;
import android.media.MediaPlayer;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.djgeraldo.R;
import com.djgeraldo.activities.MainActivity;
import com.djgeraldo.data.VideoData;
import com.djgeraldo.fragments.VideoWeb;

import java.util.ArrayList;

/**
 * Created by sachin on 11-12-2017.
 */

public class VideoDataAdapter extends RecyclerView.Adapter<VideoDataAdapter.VideoDataHolder> {


    Context context;
    ArrayList<VideoData> data = new ArrayList<>();
    Fragment frag;
    FragmentTransaction ft;
    FragmentManager fm;
    String tag;
    MediaPlayer radioPlayer;
    LinearLayout adView;
    RelativeLayout header;

    public VideoDataAdapter(Context context,RelativeLayout header, ArrayList<VideoData> data, LinearLayout adView)
    {
        this.context = context;
        this.data = data;
        this.header = header;
        this.adView = adView;
    }

    public class VideoDataHolder extends RecyclerView.ViewHolder
    {
        LinearLayout videoList;
        ImageView videoImage;
        TextView videoName,date;
        public VideoDataHolder(View v) {
            super(v);
            videoList = v.findViewById(R.id.videoList);
            videoImage = v.findViewById(R.id.videoImage);
            videoName = v.findViewById(R.id.videoName);
            date = v.findViewById(R.id.date);
            videoName.setSelected(true);
        }
    }

    @Override
    public VideoDataAdapter.VideoDataHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.video_list_item, parent, false);
        return new VideoDataAdapter.VideoDataHolder(v);
    }

    @Override
    public void onBindViewHolder(VideoDataAdapter.VideoDataHolder holder, int position) {
        final VideoData currentData = data.get(position);
        int id = currentData.getId();
        String image = currentData.getVideoImage();
        final String videoLink = currentData.getVideo_link();
        final String title = currentData.getName();
        String date = currentData.getDate();
//        Picasso.with(context).load(image).error(R.drawable.biglogo1).into(holder.videoImage);
        Glide.with(context)
                .load(image)
                .placeholder(R.drawable.dj_gradlo_app_logo)
                .into(holder.videoImage);
        holder.videoName.setText(title);
        holder.date.setText(date);
        holder.videoList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Context context, RelativeLayout header, String title, String videoLink, String mainTag, LinearLayout adView
                frag = new VideoWeb(context,header,title,videoLink,adView);
                fm = ((MainActivity) context).getSupportFragmentManager();
                ft = fm.beginTransaction();
                ft.replace(R.id.fragmentContainer, frag);
                ft.addToBackStack(null);
                ft.commit();
            }
        });

    }

    @Override
    public int getItemCount() {
      return data.size();
    }
}
