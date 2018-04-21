package com.djgeraldo.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.djgeraldo.R;
import com.djgeraldo.data.GalleryModel;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

/**
 * Created by Android18 on 20-10-2016.
 */
public class AdapterGalleryNew extends RecyclerView.Adapter<AdapterGalleryNew.ViewHolder> {

    private ArrayList<GalleryModel> arrayList;
    private FragmentManager fragmentManager;
    private Context context;
    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private RecyclerViewPositionHelper mRecyclerViewHelper;
    private DisplayImageOptions options;

    public AdapterGalleryNew(Context context, ArrayList<GalleryModel> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
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

    public class ViewHolder extends RecyclerView.ViewHolder {
        //        public TextView playlistName,playlistCount;
//        LinearLayout ll_playlist_whole_song;
        ImageView iv_gallery_image;

        public ViewHolder(View itemView) {
            super(itemView);
            iv_gallery_image = (ImageView) itemView.findViewById(R.id.sliderGalleryImage);

        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_slider_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        GalleryModel bean = arrayList.get(position);
        final String name = bean.getgImage();
        ImageLoader.getInstance().displayImage(name, holder.iv_gallery_image, options);
//        holder.iv_gallery_image.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                showImagesPopup(position);
//
//            }
//        });

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

//    private void showImagesPopup(final int pos) {
//        AppCompatDialog imageDialog = new AppCompatDialog(context);
//        imageDialog.setContentView(R.layout.custom_dialog_image_gallery);
//        imageDialog.show();
//        final RecyclerView rv_image_gallery = (RecyclerView) imageDialog.findViewById(R.id.rv_image_gallery);
//        rv_image_gallery.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
//        final AdapterImagesGallerySlider adapter = new AdapterImagesGallerySlider(context, arrayList);
//        rv_image_gallery.setAdapter(adapter);
//        mRecyclerViewHelper = RecyclerViewPositionHelper.createHelper(rv_image_gallery);
//        ImageView iv_right = (ImageView) imageDialog.findViewById(R.id.iv_right);
//        ImageView iv_left = (ImageView) imageDialog.findViewById(R.id.iv_left);
//        rv_image_gallery.scrollToPosition(pos);
//        iv_left.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                int totalCount = adapter.getItemCount();
//                int lastPosition = mRecyclerViewHelper.findLastVisibleItemPosition();
//                if (lastPosition != 0) {
//                    rv_image_gallery.scrollToPosition(lastPosition - 1);
//                }
//
//
//            }
//
//        });
//        iv_right.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                int totalCount = adapter.getItemCount();
//                int lastPosition = mRecyclerViewHelper.findLastVisibleItemPosition();
//                if (lastPosition < totalCount) {
//                    rv_image_gallery.scrollToPosition(lastPosition + 1);
//                }
//
//
//            }
//
//        });


//    }


}

