package com.nullpointexecutioners.buzzfilms;

import android.text.TextUtils;

import java.util.ArrayList;

/**/
public class Movie {
    private String title;
    private String releaseDate;
    private String synopsis;
    private String posterUrl;
    private double criticsScore;
    private ArrayList<String> castList;

    /**
     * Constructor for Movie object
     * @param title of movie
     * @param releaseDate of movie
     * @param synopsis of movie
     * @param posterUrl of movie
     * @param criticsScore of movie
     */
    public Movie(String title, String releaseDate, String synopsis, String posterUrl, double criticsScore) {
        this.title = title;
        this.releaseDate = releaseDate;
        this.synopsis = synopsis;
        this.posterUrl = posterUrl;
        this.criticsScore = criticsScore;
    }

    public String getTitle() {
        return title;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public String getSynopsis() {
        return synopsis;
    }

    public String getPosterUrl() {
        return posterUrl;
    }

    public double getCriticsScore() {
        return criticsScore;
    }

    public String getCastList() {
        return TextUtils.join(", ", castList);
    }

    @Override
    public String toString() {
        return title + ", " + synopsis + ", " + criticsScore;
    }
}
