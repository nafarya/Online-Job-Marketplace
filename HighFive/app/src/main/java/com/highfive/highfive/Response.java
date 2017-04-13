package com.highfive.highfive;

import com.google.gson.annotations.SerializedName;

/**
 * Created by dan on 13.04.17.
 */

public class Response<T> {
    @SerializedName("response")
    private T response;

    public T getResponse() {
        return response;
    }
}
