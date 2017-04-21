package com.highfive.highfive.responseModels;

import com.google.gson.annotations.SerializedName;

/**
 * Created by dan on 21.04.17.
 */

public class Values<T> {
    @SerializedName("values")
    private T values;

    public T getValues() {
        return values;
    }
}
