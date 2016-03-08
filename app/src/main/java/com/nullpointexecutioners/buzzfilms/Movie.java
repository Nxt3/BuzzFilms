package com.nullpointexecutioners.buzzfilms;

/**/
public class Movie {
    private String id;
    private String title;
    private String releaseDate;
    private String synopsis;
    private String posterUrl;
    private double criticsScore;

    /**
     * Constructor for Movie object
     * @param id unique to movie
     * @param title of movie
     * @param releaseDate of movie
     * @param synopsis of movie
     * @param posterUrl of movie
     * @param criticsScore of movie
     */
    public Movie(String id, String title, String releaseDate, String synopsis, String posterUrl, double criticsScore) {
        this.id = id;
        this.title = title;
        this.releaseDate = releaseDate;
        this.synopsis = synopsis;
        this.posterUrl = posterUrl;
        this.criticsScore = criticsScore;
    }

    public String getId() {
        return id;
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

    @Override
    public String toString() {
        return title + ", " + synopsis + ", " + criticsScore;
    }
}
