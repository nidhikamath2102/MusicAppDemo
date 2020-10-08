package com.nidhikamath.musicplayerdemo.adapter;

import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.nidhikamath.musicplayerdemo.MainActivity;
import com.nidhikamath.musicplayerdemo.R;
import com.nidhikamath.musicplayerdemo.model.Music;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<Music> musicList = new ArrayList<>();
    private OnItemClickListener onItemClickListener;

    public MusicAdapter(Context context, ArrayList<Music> musicList) {
        this.context = context;
        this.musicList = musicList;
    }

    public interface OnItemClickListener{
        void onItemClick(ImageButton play, View v, Music music, int i);
    }

    public void setOnItemClickListener(OnItemClickListener itemClickListener){
        this.onItemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.music_layout, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int i) {
        Music music = musicList.get(i);
        holder.name.setText(music.getName());
        holder.artist.setText(music.getArtist());
        if(!music.isPlaying()){
            holder.play.setBackgroundResource(R.drawable.play);
        }else{
            holder.play.setBackgroundResource(R.drawable.pause);
        }
        holder.play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(onItemClickListener!=null){
                    onItemClickListener.onItemClick(holder.play, view, musicList.get(i), i);
                }
            }
        });

        /*byte[] img = getImage(musicList.get(i).getUrl());
        if(img!=null){
            Glide.with(context).asBitmap().load(img).into(holder.image);
        }*/
    }

    @Override
    public int getItemCount() {
        return musicList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        private TextView name, artist;
        private ImageButton play;
        private ImageView image;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            artist = itemView.findViewById(R.id.artist);
            play = itemView.findViewById(R.id.play);
            image = itemView.findViewById(R.id.image);
        }
    }

    private byte[] getImage(String uri){
        MediaMetadataRetriever ret = new MediaMetadataRetriever();
        ret.setDataSource(uri);
        byte[] art = ret.getEmbeddedPicture();
        ret.release();
        return art;
    }
}
