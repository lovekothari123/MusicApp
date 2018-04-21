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
import com.djgeraldo.data.GalleryModel;
import com.djgeraldo.fragments.ImageFragment;

import java.util.ArrayList;

public class ImageGalleryDataAdapter extends RecyclerView.Adapter<ImageGalleryDataAdapter.ImageGalleryDataHolder> {

    Context context;
    ArrayList<GalleryModel> dataModules = new ArrayList<>();
    LinearLayout customSlider;
    Fragment frag;
    FragmentTransaction ft;
    FragmentManager fm;
    String tag;
    RelativeLayout header;

    public ImageGalleryDataAdapter(Context context,RelativeLayout header, ArrayList<GalleryModel> dataModules) {
        this.context = context;
        this.dataModules = dataModules;
        this.header = header;
    }

    public class ImageGalleryDataHolder extends RecyclerView.ViewHolder {
        ImageView logo;
        TextView text;
        RelativeLayout ImageListItem;

        public ImageGalleryDataHolder(View v) {
            super(v);
            logo = (ImageView) v.findViewById(R.id.logo);
            text = (TextView) v.findViewById(R.id.name);
            ImageListItem = (RelativeLayout)v.findViewById(R.id.imageListItem);
            text.setSelected(true);
        }
    }

    @Override
    public ImageGalleryDataAdapter.ImageGalleryDataHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.image_gallery_list_item, parent, false);
        return new ImageGalleryDataAdapter.ImageGalleryDataHolder(v);
    }

    @Override
    public void onBindViewHolder(ImageGalleryDataAdapter.ImageGalleryDataHolder holder, int position) {
        try {
            final GalleryModel currentData = dataModules.get(position);
            final int id = currentData.getgId();
            String image = currentData.getgImage();
            final String name = currentData.getgTitle();
            Glide.with(context)
                    .load(currentData.getgImage())
                    .placeholder(R.drawable.dj_gradlo_app_logo)
                    .into(holder.logo);



            holder.text.setText(name);

            holder.ImageListItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    Toast.makeText(context, currentData.getgTitle(), Toast.LENGTH_SHORT).show();
                    frag = new ImageFragment(context,header,id,name);
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