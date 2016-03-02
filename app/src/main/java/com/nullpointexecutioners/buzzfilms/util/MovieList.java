package com.nullpointexecutioners.buzzfilms.util;

import com.nullpointexecutioners.buzzfilms.Movie;

import java.util.List;

/**
 * Created by Matthew on 2/29/2016.
 */
public class MovieList {
    private List<Movie> movies;
    public MovieList(Movie movie) {
        movies.add(movie);
    }

    public void clear() {
        movies.clear();
    }

    public void addMovie(Movie movie) {
        movies.add(movie);
    }

    public List<Movie> get() {
        return movies;
    }
}
