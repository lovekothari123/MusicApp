package com.djgeraldo.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.djgeraldo.R;
import com.djgeraldo.data.ImageSliderModel;

import java.util.ArrayList;

/**
 * Created by sachin on 12-12-2017.
 */

public class MySliderAdapter extends PagerAdapter {
    private ArrayList<ImageSliderModel> images;
    private LayoutInflater inflater;
    private Context context;

    public MySliderAdapter(Context context, ArrayList<ImageSliderModel> images) {
        this.context = context;
        this.images=images;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return images.size();
    }

    @Override
    public Object instantiateItem(ViewGroup view, int position) {
//        View myImageLayout = inflater.inflate(R.layout.slide, view, false);
        View v = LayoutInflater.from(context).inflate(R.layout.slide,view, false);
        ImageView myImage = (ImageView) v.findViewById(R.id.slideImage);
        ImageSliderModel currentData = images.get(position);
        Glide.with(context)
                .load(currentData.getImage())
                .into(myImage);
        view.addView(v, 0);
        return v;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }
}
