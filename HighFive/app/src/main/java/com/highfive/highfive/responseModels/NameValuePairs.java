package com.highfive.highfive.responseModels;

import com.google.gson.annotations.SerializedName;
import com.highfive.highfive.model.Message;

/**
 * Created by dan on 21.04.17.
 */

public class NameValuePairs {
    private String id;
    private String text;
    private String user;
    private String time;
    private String order;

    public String getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public String getUser() {
        return user;
    }

    public String getTime() {
        return time;
    }

    public String getOrder() {
        return order;
    }
}
