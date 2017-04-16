package com.highfive.highfive;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by dan on 16.04.17.
 */

public class Items<T> {

    @SerializedName("count")
    private int count;

    @SerializedName("items")
    private List<T> items;

    public List<T> items() {
        return items;
    }

    public int getCount() {
        return count;
    }
}