package com.djgeraldo.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.djgeraldo.R;
import com.djgeraldo.activities.MainActivity;
import com.djgeraldo.data.Dj.DjData;
import com.djgeraldo.fragments.DjSongsFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sachin on 02-12-2017.
 */

public class DjDataAdapter extends RecyclerView.Adapter<DjDataAdapter.DjDataHolder> {

    Context context;
    List<DjData> data = new ArrayList<>();
    Fragment frag;
    FragmentManager fm;
    FragmentTransaction ft;
    String tag;
    RelativeLayout header;


    public DjDataAdapter(Context context,RelativeLayout header, List<DjData> data)
    {
        this.context = context;
        this.header = header;
        this.data = data;
    }

    public class DjDataHolder extends RecyclerView.ViewHolder
    {
        ImageView firstImage;
        TextView middleText;
        RelativeLayout DjList;
        public DjDataHolder(View v) {
            super(v);
            firstImage = v.findViewById(R.id.firstImage);
            middleText = v.findViewById(R.id.middleText);
            DjList = v.findViewById(R.id.DjList);
        }
    }

    @Override
    public DjDataHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.dj_artist_listitem, parent, false);
        return new DjDataHolder(view);
    }

    @Override
    public void onBindViewHolder(DjDataHolder holder, int position) {
        DjData currentData = data.get(position);
        final int id = currentData.getFeatureDjId();
        final String name  = currentData.getName();
        String image = currentData.getImage();
//        final String instaUrl = currentData.getSongUrl();
        holder.middleText.setText(name);
//        Picasso.with(context).load(image).error(R.drawable.barkologo).into(holder.firstImage);
        Glide.with(context)
                .load(currentData.getImage())
                .placeholder(R.drawable.dj_gradlo_app_logo)
                .into(holder.firstImage);
        holder.DjList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    frag = new DjSongsFragment(context,header,id,name);
                    fm = ((MainActivity) context).getSupportFragmentManager();
                    ft = fm.beginTransaction();
                    ft.replace(R.id.fragmentContainer,frag);
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
