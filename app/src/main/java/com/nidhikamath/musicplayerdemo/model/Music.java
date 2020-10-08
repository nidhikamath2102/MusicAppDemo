package com.nidhikamath.musicplayerdemo.model;

public class Music {
    private long id = 0;
    private String name;
    private String artist;
    private String url;
    private boolean isPlaying = false;
    public Music(long id, String name, String artist, String url) {
        this.id = id;
        this.name = name;
        this.artist = artist;
        this.url = url;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void setPlaying(boolean playing) {
        isPlaying = playing;
    }
}
