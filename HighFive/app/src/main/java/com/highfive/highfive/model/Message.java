package com.highfive.highfive.model;

/**
 * Created by dan on 21.04.17.
 */

public class Message {
    private String id;
    private String text;
    private String user;
    private String time;
    private String order;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getTime() {
        String tmp = time.substring(11, 16) + " " + time.substring(8, 10) + "." + time.substring(5, 7) + "." +
                 time.substring(0, 4);
        return tmp;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }
}