package com.nidhikamath.musicplayerdemo.model;

public class Music {
    public String name;
    public String artist;
    public String url;

    public Music(String name, String artist, String url) {
        this.name = name;
        this.artist = artist;
        this.url = url;
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
}
