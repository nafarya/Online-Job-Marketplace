package com.highfive.highfive.responseModels;

/**
 * Created by dan on 14.05.17.
 */

public class File {
    private String id;
    private String path;
    private String name;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
