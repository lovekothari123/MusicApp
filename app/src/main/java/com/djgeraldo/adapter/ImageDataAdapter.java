package com.djgeraldo.adapter;

import android.content.Context;
import android.support.v7.app.AppCompatDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.djgeraldo.R;
import com.djgeraldo.data.GalleryModel;

import java.util.ArrayList;

/**
 * Created by sachin on 28-11-2017.
 */

public class ImageDataAdapter extends RecyclerView.Adapter<ImageDataAdapter.ImageDataHolder>
        {
            Context context;
            ArrayList<GalleryModel> dataModules = new ArrayList<>();
//            Gallery gallery;
            RecyclerViewPositionHelper mRecyclerViewHelper;


            public ImageDataAdapter(Context context, ArrayList<GalleryModel> dataModules) {
                this.context = context;
                this.dataModules = dataModules;
            }


            public class ImageDataHolder extends RecyclerView.ViewHolder
            {
                ImageView image;

                public ImageDataHolder(View itemView) {
                    super(itemView);
                    image =(ImageView)itemView.findViewById(R.id.listImage);
                }
            }


            @Override
            public ImageDataHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(context).inflate(R.layout.image_list_item, parent, false);
                return new ImageDataHolder(v);
            }

            @Override
            public void onBindViewHolder(final ImageDataHolder holder, final int position) {
                final GalleryModel currentData = dataModules.get(position);
                int id = currentData.getgId();
                String image = currentData.getgImage();
//                Picasso.with(context).load(image).error(R.drawable.barkologo).into(holder.image);
                Glide.with(context)
                        .load(currentData.getgImage())
                        .placeholder(R.drawable.dj_gradlo_app_logo)
                        .fitCenter()
                        .into(holder.image);
//                ImageLoader.getInstance().displayImage(currentData.getgImage(), holder.iv_gallery_image, options);
                holder.image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                            showPopupImage(position);
                    }
                });
            }

            private void showPopupImage(final int pos) {

                AppCompatDialog imageDialog = new AppCompatDialog(context);
                imageDialog.setContentView(R.layout.custom_dialog_image_gallery);
                imageDialog.show();
                final RecyclerView rv_image_gallery = (RecyclerView) imageDialog.findViewById(R.id.rv_image_gallery);
                rv_image_gallery.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
                final AdapterGalleryNew adapter = new AdapterGalleryNew(context, dataModules);
                rv_image_gallery.setAdapter(adapter);
                mRecyclerViewHelper = RecyclerViewPositionHelper.createHelper(rv_image_gallery);
                ImageView iv_right = (ImageView) imageDialog.findViewById(R.id.iv_right);
                ImageView iv_left = (ImageView) imageDialog.findViewById(R.id.iv_left);
//                rv_image_gallery.scrollToPosition(pos);
                rv_image_gallery.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
                    @Override
                    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                        return e.getAction() == MotionEvent.ACTION_MOVE;
                    }

                    @Override
                    public void onTouchEvent(RecyclerView rv, MotionEvent e) {
                        rv_image_gallery.scrollToPosition(pos);

                    }

                    @Override
                    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

                    }
                });
                iv_left.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int totalCount = adapter.getItemCount();
                        int lastPosition = mRecyclerViewHelper.findLastVisibleItemPosition();
                        if (lastPosition != 0) {
                            rv_image_gallery.scrollToPosition(lastPosition - 1);
                        }


                    }

                });
                iv_right.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int totalCount = adapter.getItemCount();
                        int lastPosition = mRecyclerViewHelper.findLastVisibleItemPosition();
                        if (lastPosition < totalCount) {
                            rv_image_gallery.scrollToPosition(lastPosition + 1);
                        }


                    }

                });
            }

            @Override
            public int getItemCount() {
                return dataModules.size();
            }

}
