package com.djgeraldo.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.djgeraldo.R;
import com.djgeraldo.activities.MainActivity;
import com.djgeraldo.data.EventsData;
import com.djgeraldo.fragments.EventsDetails;

import java.util.ArrayList;

public class EventsDataAdapter extends RecyclerView.Adapter<EventsDataAdapter.EventsDataHolder> {
    Context context;
    ArrayList<EventsData> dataModules = new ArrayList<>();
    Fragment frag;
    FragmentManager fm;
    FragmentTransaction ft;
    String tag;
    RelativeLayout header;

    public EventsDataAdapter(Context context, RelativeLayout header, ArrayList<EventsData> dataModules, String tag) {
        this.dataModules = dataModules;
        this.context = context;
        this.header = header;
        this.tag = tag;
    }

    public class EventsDataHolder extends RecyclerView.ViewHolder {
        ImageView calImage;
        TextView location,place,dateTimeText;
        RelativeLayout eventsList;
        public EventsDataHolder(View v) {
            super(v);
            eventsList = v.findViewById(R.id.eventsList);
            calImage = v.findViewById(R.id.calImage);
            location = v.findViewById(R.id.location);
            place = v.findViewById(R.id.place);
            dateTimeText = v.findViewById(R.id.dateTimeText);
            location.setSelected(true);
            place.setSelected(true);
        }
    }

    @Override
    public EventsDataAdapter.EventsDataHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.events_list_item, parent, false);
        return new EventsDataAdapter.EventsDataHolder(v);
    }

    @Override
    public void onBindViewHolder(EventsDataAdapter.EventsDataHolder holder, final int position) {
        try {
            final EventsData currentData = dataModules.get(position);
           int id = currentData.getId();
//            Picasso.with(context).load(currentData.getI()).error(R.drawable.cal12).into(holder.calImage);
           holder.location.setText(currentData.geteTitle());
           holder.place.setText(currentData.geteAddress());
           String dateTime = currentData.geteStart() + " At " + currentData.getTime();
           holder.dateTimeText.setText(dateTime);
           Log.d("EventAdapter","Image is:"+currentData.geteImage());
            Glide.with(context)
                    .load(currentData.geteImage())
                    .placeholder(R.drawable.dj_gradlo_app_logo)
                    .into(holder.calImage);
           final String eventLong = currentData.geteLongt();
           final String eventLat = currentData.geteLat();
            Log.d("EventAdapter","Long is "+eventLong);
            Log.d("EventsAdapter","Lat is "+eventLat);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                     frag = new EventsDetails(context,header,currentData.geteTitle(),currentData,eventLong,eventLat,tag);
                    fm = ((MainActivity) context).getSupportFragmentManager();
                    ft = fm.beginTransaction();
                    ft.replace(R.id.fragmentContainer, frag);
                    ft.addToBackStack(null);
                    ft.commit();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        try {
            return dataModules.size();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }


}