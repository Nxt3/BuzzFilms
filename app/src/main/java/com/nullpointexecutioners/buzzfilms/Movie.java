package com.nullpointexecutioners.buzzfilms;

import android.text.TextUtils;

import java.util.ArrayList;

/**/
public class Movie {
    public String title;
    private String releaseDate;
    private String synopsis;
    private String posterUrl;
    private float criticsScore;
    private ArrayList<String> castList;

    public Movie(String title, String releaseDate, String synopsis, String posterUrl, float criticsScore) {
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

    public float getCriticsScore() {
        return criticsScore;
    }

    public String getCastList() {
        return TextUtils.join(", ", castList);
    }
}
