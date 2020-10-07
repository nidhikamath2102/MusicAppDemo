package com.nidhikamath.musicplayerdemo;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;

import com.nidhikamath.musicplayerdemo.adapter.MusicAdapter;
import com.nidhikamath.musicplayerdemo.model.Music;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private SeekBar seekBar;
    private RecyclerView recyclerView;
    private MusicAdapter musicAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<Music> musicList = new ArrayList<>();
    private MediaPlayer mediaPlayer;
    private int REQUEST_CODE = 21;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        seekBar = findViewById(R.id.seekBar);
        recyclerView = findViewById(R.id.recyclerView);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        musicAdapter = new MusicAdapter(this, musicList);
        recyclerView.setAdapter(musicAdapter);

        checkPermission();
        setMusicOnClick();
        Thread t = new MyThread();
        t.start();

    }

    private void addMusic() {
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String selectQuery = MediaStore.Audio.Media.IS_MUSIC + "!=0";
        Cursor c = getContentResolver().query(uri, null, selectQuery, null, null);
        if(c!=null){
            if(c.moveToFirst()){
                do {
                    String name = c.getString(c.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
                    String artist = "";
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
                        artist = c.getString(c.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                    }
                    String url = c.getString(c.getColumnIndex(MediaStore.Audio.Media.DATA));

                    musicList.add(new Music(name, artist, url));
                }while (c.moveToNext());
            }
            c.close();
            musicAdapter.notifyDataSetChanged();
        }

    }


    private void checkPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            addMusic();
        } else {
            if (Build.VERSION.SDK_INT > 23) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE);
            }
        }
    }

    private void setMusicOnClick() {
        musicAdapter.setOnItemClickListener(new MusicAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(final ImageButton play, View v, final Music music, int i) {
                if (play.getTag()!=null && play.getTag().equals(play.getResources().getString(R.string.stop))) {
                    play.setTag(getResources().getString(R.string.play));
                    play.setImageDrawable(getResources().getDrawable(R.drawable.play));
                    mediaPlayer.stop();
                    mediaPlayer.reset();
                    mediaPlayer.release();
                    mediaPlayer = null;
                }else {
                    final Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            try {
                                mediaPlayer = new MediaPlayer();
                                mediaPlayer.setDataSource(music.getUrl());
                                mediaPlayer.prepareAsync();
                                mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                    @Override
                                    public void onPrepared(MediaPlayer mediaPlayer) {
                                        mediaPlayer.start();

                                        seekBar.setProgress(0);
                                        seekBar.setMax(mediaPlayer.getDuration());
                                    }
                                });

                                play.setTag(getResources().getString(R.string.stop));
                                play.setImageDrawable(getResources().getDrawable(R.drawable.pause));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    };

                    handler.postDelayed(runnable, 100);
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 21) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                addMusic();
            } else {
                Toast.makeText(this, getResources().getString(R.string.kindlyacceptpermissions), Toast.LENGTH_SHORT).show();
                checkPermission();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private class MyThread extends Thread{
        @Override
        public void run() {
            try{
                Thread.sleep(1000);
            }catch (Exception e){
                e.printStackTrace();
            }

            if(mediaPlayer!=null) {
                seekBar.post(new Runnable() {
                    @Override
                    public void run() {
                        seekBar.setProgress(mediaPlayer.getCurrentPosition());
                        Log.d("current pos", "run: " + mediaPlayer.getCurrentPosition());
                    }
                });

            }

        }
    }
}