package com.example.localflix;

import android.support.annotation.NonNull;

public class EpisodeModel implements Comparable<EpisodeModel>{
    String no;
    String title;
    String path;
    String subtitles;

    public void setPath(String path) {
        this.path = path;
    }

    public void setNo(String no) {
        this.no = no;
    }

    public void setSubtitles(String subtitles) {
        this.subtitles = subtitles;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPath() {
        return path;
    }

    public String getNo() {
        return no;
    }

    public String getSubtitles() {
        return subtitles;
    }

    public String getTitle() {
        return title;
    }

    @Override
    public int compareTo(@NonNull EpisodeModel episodeModel) {
        return Integer.parseInt(this.no)-Integer.parseInt(episodeModel.no);
    }
}


