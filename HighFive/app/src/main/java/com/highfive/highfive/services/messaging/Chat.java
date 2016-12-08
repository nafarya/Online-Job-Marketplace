package com.highfive.highfive.services.messaging;

/**
 * Created by heat_wave on 12/4/16.
 */

public class Chat {
    private String name;
    private String text;
    private String uid;

    public Chat() {
    }

    public Chat(String name, String uid, String message) {
        this.name = name;
        this.text = message;
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public String getUid() {
        return uid;
    }

    public String getText() {
        return text;
    }
}