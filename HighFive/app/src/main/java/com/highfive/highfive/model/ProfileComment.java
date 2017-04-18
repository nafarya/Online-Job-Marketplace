package com.highfive.highfive.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by dan on 18.04.17.
 */

public class ProfileComment {

    @SerializedName("author")
    @Expose
    private String author;

    @SerializedName("order")
    @Expose
    private String order;

    @SerializedName("_id")
    @Expose
    private String id;

    @SerializedName("rate")
    @Expose
    private int rate;

    @SerializedName("text")
    @Expose
    private String text;

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getRate() {
        return rate;
    }

    public void setRate(int rate) {
        this.rate = rate;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

}
