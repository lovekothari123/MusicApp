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
import com.djgeraldo.data.Genre.GenreData;
import com.djgeraldo.fragments.MusicGenresSongs;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sachin on 11-01-2018.
 */

public class MusicGenreAdapter extends RecyclerView.Adapter<MusicGenreAdapter.MusicGenreHolder> {

    Context context;
    List<GenreData> data = new ArrayList<>();
    Fragment frag;
    FragmentManager fm;
    FragmentTransaction ft;
    String tag;
    RelativeLayout header;

   public MusicGenreAdapter(Context context,List<GenreData> data,RelativeLayout header)
    {
        this.context = context;
        this.data = data;
        this.header = header;
    }

    public class MusicGenreHolder extends RecyclerView.ViewHolder {
       LinearLayout music_genre_list_item;
       ImageView genreImage;
       TextView genreTitle;
       TextView totalSong;
        public MusicGenreHolder(View v) {
            super(v);
            music_genre_list_item = v.findViewById(R.id.music_genre_list_item);
            genreImage = v.findViewById(R.id.gImage);
            genreTitle = v.findViewById(R.id.gText);
            totalSong = v.findViewById(R.id.totalSong);
        }
    }

    @Override
    public MusicGenreHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.music_genre_list_item, parent, false);
        return new MusicGenreHolder(view);
    }

    @Override
    public void onBindViewHolder(MusicGenreHolder holder, int position) {
        GenreData current = data.get(position);
        final int genre_id = current.getGenresId();
        final String name = current.getGenresName();
        holder.genreTitle.setText(current.getGenresName());
        holder.totalSong.setText(String.valueOf(current.getTotalRecords()));
        Glide.with(context)
                .load(current.getImage())
                .placeholder(R.drawable.dj_graldo_music_genris_file)
                .into(holder.genreImage);
        holder.music_genre_list_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                frag = new MusicGenresSongs(context,genre_id,name,header);
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
