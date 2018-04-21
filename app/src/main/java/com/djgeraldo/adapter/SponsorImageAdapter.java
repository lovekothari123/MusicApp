package com.djgeraldo.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.djgeraldo.R;
import com.djgeraldo.data.sponsors;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

/**
 * Created by sachin on 29-12-2017.
 */

public class SponsorImageAdapter extends RecyclerView.Adapter<SponsorImageAdapter.SponsorImageHolder> {

    Context context;
    ArrayList<sponsors> data = new ArrayList<>();
    DisplayImageOptions options;

    public SponsorImageAdapter(Context context,ArrayList<sponsors> data)
    {
        this.context = context;
        this.data = data;
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.dj_gradlo_app_logo)
                .showImageForEmptyUri(R.drawable.dj_gradlo_app_logo)
                .showImageOnFail(R.drawable.dj_gradlo_app_logo)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
    }

    public class SponsorImageHolder extends RecyclerView.ViewHolder {
        ImageView sImage;
        public SponsorImageHolder(View v) {
            super(v);
            sImage = v.findViewById(R.id.sImage);
        }
    }

    @Override
    public SponsorImageAdapter.SponsorImageHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.sponsor_image_list, parent, false);
        return new SponsorImageAdapter.SponsorImageHolder(v);
    }

    @Override
    public void onBindViewHolder(SponsorImageAdapter.SponsorImageHolder holder, int position) {
        sponsors current = data.get(position);
        String image = current.getLogo();
//        Glide.with(context)
//                .load(current.getLogo())
//                .placeholder(R.drawable.appicone1024)
//                .into(holder.sImage);
        ImageLoader.getInstance().displayImage(current.getLogo(),holder.sImage,options);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }


}
