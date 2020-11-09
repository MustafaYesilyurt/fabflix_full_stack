package edu.uci.ics.fabflixmobile;

import java.io.Serializable;

public class Movie implements Serializable {
    private String id;
    private String title;
    private Integer year;
    private String director;
    private Float rating;
    private String genres;
    private String stars;

    public Movie() {};

    public Movie(String id, String title, Integer year, String director, Float rating, String genres, String stars) {
        this.id = id;
        this.title = title;
        this.year = year;
        this.director = director;
        this.rating = rating;
        this.genres = genres;
        this.stars = stars;
    }

    public String getId() { return id; }

    public String getTitle() {
        return title;
    }

    public Integer getYear() {
        return year;
    }

    public String getDirector() { return director; }

    public Float getRating() { return rating; }

    public String getGenres() { return genres; }

    public String getStars() { return stars; }

    public void setId(String id) { this.id = id; }

    public void setTitle(String title) { this.title = title; }

    public void setYear(Integer year) { this.year = year; }

    public void setDirector(String director) { this.director = director; }

    public void setRating(Float rating) { this.rating = rating; }

    public void setGenres(String genres) { this.genres = genres; }

    public void setStars(String stars) { this.stars = stars; }
}
