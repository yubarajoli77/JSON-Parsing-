package com.example.yubaraj.movies.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by yubar on 10/2/2017.
 */

public class MovieModel {
    @SerializedName("movie")
    private String name;
    private int year;
    private float rating;
    private String duration;
    @SerializedName("director")
    private String direction;
    private String image;
    private String story;
    @SerializedName("tagline")
    private String tagLine;
    @SerializedName("cast")
    private List<Cast> castList;
    public static class Cast{ private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public String getName() {
        return name;
    }

    public int getYear() {
        return year;
    }

    public float getRating() {
        return rating;
    }

    public String getDuration() {
        return duration;
    }

    public String getDirection() {
        return direction;
    }

    public String getImage() {
        return image;
    }

    public String getStory() {
        return story;
    }

    public String getTagLine() {
        return tagLine;
    }

    public List<Cast> getCastList() {
        return castList;
    }
    //settters

    public void setName(String name) {
        this.name = name;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setStory(String story) {
        this.story = story;
    }

    public void setTagLine(String tagLine) {
        this.tagLine = tagLine;
    }

    public void setCastList(List<Cast> castList) {
        this.castList = castList;
    }
}
