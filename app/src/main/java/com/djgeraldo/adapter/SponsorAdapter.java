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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.djgeraldo.R;
import com.djgeraldo.activities.MainActivity;
import com.djgeraldo.data.sponsors;
import com.djgeraldo.fragments.SponsorDetails;

import java.util.ArrayList;

/**
 * Created by sachin on 28-12-2017.
 */

public class SponsorAdapter extends RecyclerView.Adapter<SponsorAdapter.SponsorHolder> {

    Context context;
    ArrayList<sponsors> data = new ArrayList<>();
    Fragment frag;
    FragmentTransaction ft;
    FragmentManager fm;
    RelativeLayout header;

    public SponsorAdapter(Context context,RelativeLayout header,ArrayList<sponsors> data)
    {
        this.context = context;
        this.header = header;
        this.data = data;
    }

    public class SponsorHolder extends RecyclerView.ViewHolder {

        LinearLayout sponsorList;
        TextView Title;
        ImageView Icon;
        public SponsorHolder(View v) {
            super(v);
            sponsorList = v.findViewById(R.id.sponsorList);
            Title = v.findViewById(R.id.Title);
            Icon = v.findViewById(R.id.Icon);
        }
    }

    @Override
    public SponsorHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.sponsor_list_item, parent, false);
        return new SponsorAdapter.SponsorHolder(view);
    }

    @Override
    public void onBindViewHolder(SponsorHolder holder, final int position) {
        sponsors current = data.get(position);
        final int id = current.getId();
        final String title = current.getName();
        String logo = current.getLogo();
        holder.Title.setText(title);
        Glide.with(context)
                .load(logo)
                .placeholder(R.drawable.dj_gradlo_app_logo)
                .into(holder.Icon);
        holder.sponsorList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                frag = new SponsorDetails(context,header,id,title);
                    fm = ((MainActivity)context).getSupportFragmentManager();
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
