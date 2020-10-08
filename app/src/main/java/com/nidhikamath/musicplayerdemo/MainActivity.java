package com.nidhikamath.musicplayerdemo;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.nidhikamath.musicplayerdemo.adapter.MusicAdapter;
import com.nidhikamath.musicplayerdemo.model.Music;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private MusicAdapter musicAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<Music> musicList = new ArrayList<>();
    private MediaPlayer mediaPlayer;
    private int REQUEST_CODE = 21;
    private Handler handler = new Handler();
    private int pos = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        musicAdapter = new MusicAdapter(this, musicList);
        recyclerView.setAdapter(musicAdapter);

        checkPermission();
        setMusicOnClick();
    }

    private void addMusic() {
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String selectQuery = MediaStore.Audio.Media.IS_MUSIC + "!=0";
        Cursor c = getContentResolver().query(uri, null, selectQuery, null, null);
        if (c != null) {
            if (c.moveToFirst()) {
                do {
                    int id = c.getColumnIndex(android.provider.MediaStore.Audio.Media._ID);
                    String name = c.getString(c.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
                    String artist = "";
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
                        artist = c.getString(c.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                    }
                    String url = c.getString(c.getColumnIndex(MediaStore.Audio.Media.DATA));

                    musicList.add(new Music(id, name, artist, url));
                } while (c.moveToNext());
            }
            c.close();
            sortMusic();
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
            public void onItemClick(final ImageButton play, View v, final Music music, final int i) {

                try {
                    if (pos != -1) {
                        if(musicList.get(pos).isPlaying()) {
                            stopMusic(play, music, pos);
                        }else{
                            playMusic(music, pos);
                        }
                    }
                    if (pos != i) {
                        if (music.isPlaying()) {
                            stopMusic(play, music, -1);
                        } else {
                            playMusic(music, i);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void stopMusic(ImageButton play, Music music, int i) {
        play.setTag(getResources().getString(R.string.play));
        play.setBackgroundResource(R.drawable.play);
        mediaPlayer.stop();
        mediaPlayer.reset();
        mediaPlayer.release();
        mediaPlayer = null;
        if (i != -1) {
            musicList.get(i).setPlaying(false);
        } else {
            music.setPlaying(false);
        }
        musicAdapter.notifyDataSetChanged();
    }

    private void playMusic(final Music music, final int i) {
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
                        }
                    });
                    music.setPlaying(true);
                    musicAdapter.notifyDataSetChanged();
                    pos = i;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        handler.postDelayed(runnable, 100);
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

    private void sortMusic() {
        Collections.sort(musicList, new Comparator<Music>() {
            @Override
            public int compare(Music music1, Music music2) {
                return music2.getName().compareTo(music1.getName());
            }
        });
    }
}