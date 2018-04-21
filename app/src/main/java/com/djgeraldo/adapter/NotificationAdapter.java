package com.djgeraldo.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.djgeraldo.R;
import com.djgeraldo.data.notification;

import java.util.ArrayList;

/**
 * Created by sachin on 19-12-2017.
 */

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationHolder> {
    Context context;
    ArrayList<notification> data = new ArrayList<>();
    RelativeLayout header;

    public NotificationAdapter(Context context,RelativeLayout header, ArrayList<notification> data)
    {
        this.context = context;
        this.header = header;
        this.data = data;
    }

    public class NotificationHolder extends RecyclerView.ViewHolder
    {
        TextView name,date;
        LinearLayout notification_list;
        public NotificationHolder(View v) {
            super(v);
            name = v.findViewById(R.id.notificationMsg);
            name.setSelected(true);
            date = v.findViewById(R.id.date);
            notification_list = v.findViewById(R.id.notification_list);
        }
    }

    @Override
    public NotificationAdapter.NotificationHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.notification_list_item, parent, false);
        return new NotificationAdapter.NotificationHolder(view);
    }

    @Override
    public void onBindViewHolder(NotificationAdapter.NotificationHolder holder, int position) {
        notification currentData = data.get(position);
        int id = currentData.getNotificationId();
        String name = currentData.getMsg();
        String date = currentData.getCreatedAt();
        holder.name.setText(currentData.getMsg());
        holder.date.setText(currentData.getCreatedAt());
    }

    @Override
    public int getItemCount() {
//        return 0;
        return data.size();
    }
}
